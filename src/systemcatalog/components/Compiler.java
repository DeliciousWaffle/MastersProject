package systemcatalog.components;

import datastructures.querytree.operator.Operator;
import datastructures.querytree.operator.types.*;
import datastructures.relation.resultset.ResultSet;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.FileStructure;
import datastructures.relation.table.component.TableData;
import datastructures.querytree.QueryTree;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.user.component.Privilege;
import datastructures.user.component.TablePrivileges;
import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.user.User;
import enums.InputType;
import enums.Keyword;
import enums.Symbol;
import utilities.OptimizerUtilities;
import utilities.Utilities;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static datastructures.querytree.QueryTree.TreeTraversal.PREORDER;

/**
 * Responsible for executing a given input. If the input is a query, an associated result set
 * will be produced. If the input is a DML statement, then the change will be carried out here.
 */
public class Compiler {

    /**
     * Executes the given query which produces a result set. Requires the query tree produced right before
     * pipelining in order to create the result set. This query tree state contains all the necessary information
     * in order to produce a result set. Each node of the query contains an operator that applies
     * a transformation to previous nodes.
     * @param queryTreeStates are the states of the query tree after being run through the Optimizer
     * @return the result set of executing the query
     */
    public ResultSet executeQuery(List<QueryTree> queryTreeStates, List<Table> tables) {

        // get query tree before pipelining and convert it to a stack
        QueryTree queryTreeBeforePipelining = queryTreeStates.get(5);
        Deque<Operator> startingStack =
                OptimizerUtilities.setToDeque(queryTreeBeforePipelining.getOperatorsAndLocations(PREORDER).keySet());
        Deque<ResultSet> workingStack = new ArrayDeque<>();
System.out.println(queryTreeBeforePipelining);
        while (! startingStack.isEmpty()) {
            Operator operator = startingStack.pop();
            switch (operator.getType()) {
                case RELATION: {
                    Relation relation = (Relation) operator;
                    String tableName = relation.getTableName();
                    ResultSet resultSet = new ResultSet(Utilities.getReferencedTable(tableName, tables));
                    workingStack.push(resultSet);
                    break;
                }
                case SIMPLE_SELECTION: {
                    SimpleSelection simpleSelection = (SimpleSelection) operator;
                    String columnName = simpleSelection.getColumnName();
                    String symbol = simpleSelection.getSymbol();
                    String value = simpleSelection.getValue();
                    ResultSet resultSet = workingStack.pop().selection(columnName, symbol, value);
                    workingStack.push(resultSet);
                    break;
                }
                case PROJECTION:
                    Projection projection = (Projection) operator;
                    List<String> columnNames = projection.getColumnNames();
                    ResultSet resultSet = workingStack.pop().projection(columnNames);
                    workingStack.push(resultSet);
                    break;
                case INNER_JOIN: {
                    InnerJoin innerJoin = (InnerJoin) operator;
                    String firstJoinColumnName = innerJoin.getFirstJoinColumnName();
                    String joinSymbolName = innerJoin.getSymbol();
                    String secondJoinColumnName = innerJoin.getSecondJoinColumnName();
                    ResultSet firstResultSet = workingStack.pop();
                    ResultSet secondResultSet = workingStack.pop();
                    workingStack.push(firstResultSet.innerJoin(
                            secondResultSet, firstJoinColumnName, joinSymbolName, secondJoinColumnName));
                    break;
                }
                case CARTESIAN_PRODUCT: {
                    ResultSet firstResultSet = workingStack.pop();
                    ResultSet secondResultSet = workingStack.pop();
                    workingStack.push(firstResultSet.cartesianProduct(secondResultSet));
                    break;
                }
                case AGGREGATION: {
                    Aggregation aggregation = (Aggregation) operator;
                    List<String> groupByColumnNames = aggregation.getGroupByColumnNames();
                    List<String> aggregationTypes = aggregation.getAggregationTypes();
                    List<String> aggregatedColumnNames = aggregation.getAggregatedColumnNames();
                    workingStack.push(workingStack.pop().aggregate(
                            groupByColumnNames, aggregationTypes, aggregatedColumnNames));
                    break;
                }
                case AGGREGATE_SELECTION: {
                    AggregateSelection aggregateSelection = (AggregateSelection) operator;
                    List<String> aggregationTypes = aggregateSelection.getAggregateTypes();
                    List<String> aggregatedColumnNames = aggregateSelection.getColumnNames();
                    List<String> symbols = aggregateSelection.getSymbols();
                    List<String> values = aggregateSelection.getValues();
                    workingStack.push(workingStack.pop().having(
                            aggregationTypes, aggregatedColumnNames, symbols, values));
                    break;
                }
            }
        }

        return workingStack.pop();
    }

    /**
     * Executes a data manipulation language (DML) statement. Essentially, a command that makes changes to the
     * system's data in some shape or form. Input is assumed to have already passed through the Parser, Verifier,
     * and Security Checker to ensure that the it's syntactically and logically correct.
     * @param dmlInputType is the type dml statement
     * @param filteredInput is the filtered input
     * @param tables is a list of the system tables
     * @param users is a list of users of the system
     */
    public void executeDML(InputType dmlInputType, String[] filteredInput, List<Table> tables, List<User> users) {
        switch(dmlInputType) {
            case CREATE_TABLE:
                createTable(filteredInput, tables);
                break;
            case DROP_TABLE:
                dropTable(filteredInput, tables);
                break;
            case ALTER_TABLE:
                alterTable(filteredInput, tables);
                break;
            case INSERT:
                insert(filteredInput, tables);
                break;
            case DELETE:
                delete(filteredInput, tables);
                break;
            case UPDATE:
                update(filteredInput, tables);
                break;
            case BUILD_FILE_STRUCTURE:
                buildFileStructure(filteredInput, tables);
                break;
            case REMOVE_FILE_STRUCTURE:
                removeFileStructure(filteredInput, tables);
                break;
            case GRANT:
                grant(filteredInput, users, tables);
                break;
            case REVOKE:
                revoke(filteredInput, users);
                break;
        }
    }

    /**
     * Executes the CREATE TABLE command, adding a new table to the system tables.
     * @param filteredInput is the filtered input
     * @param tables is a list of system tables
     */
    public void createTable(String[] filteredInput, List<Table> tables) {

        // unfortunately, can't use the create table rule graph because the mapping gets wonky, so we got this
        String tableName = filteredInput[2];
        List<Column> columns = new ArrayList<>();

        for (int i = 4; i < filteredInput.length; i++) {

            String columnName = filteredInput[i];
            DataType dataType = DataType.convertToDataType(filteredInput[i + 1]);
            boolean isNumeric = dataType == DataType.NUMBER;
            boolean isChar = dataType == DataType.CHAR;
            boolean isDate = dataType == DataType.DATE;

            if (isNumeric) {
                int size = Integer.parseInt(filteredInput[i + 2]);
                int decimalSize = 0;
                boolean hasDecimalSize = filteredInput[i + 4].equalsIgnoreCase(",") && Utilities.isNumeric(filteredInput[i + 5]);
                if (hasDecimalSize) {
                    decimalSize = Integer.parseInt(filteredInput[i + 5]);
                }
                Column column = new Column(columnName, DataType.NUMBER, size, decimalSize);
                columns.add(column);
            }

            if (isChar) {
                int size = Integer.parseInt(filteredInput[i + 2]);
                Column column = new Column(columnName, DataType.CHAR, size, 0);
                columns.add(column);
            }

            if (isDate) {
                Column column = new Column(columnName, DataType.DATE, 0, 0);
                columns.add(column);
            }
        }

        Table table = new Table(tableName, columns, new ArrayList<>(), new HashMap<>());
        tables.add(table);
    }

    /**
     * Executes the DROP TABLE command, removing a table from the system tables.
     * @param filteredInput is the filtered input
     * @param tables is a list of system tables
     */
    public void dropTable(String[] filteredInput, List<Table> tables) {

        RuleGraph dropTableRuleGraph = RuleGraphTypes.getDropTableRuleGraph();

        // getting the table name to remove
        String tableNameToRemove = dropTableRuleGraph.getTokensAt(filteredInput, 2).get(0);

        // remove the table
        for (int i = 0; i < tables.size(); i++) {
            String tableName = tables.get(i).getTableName();
            if (tableName.equalsIgnoreCase(tableNameToRemove)) {
                tables.remove(i);
                break;
            }
        }

        // remove any foreign keys from other tables as well
        for (Table table : tables) {
            Map<String, String> foreignKeys = table.getForeignKeys();
            foreignKeys.remove(table.getTableName());
        }
    }

    /**
     * Executes the ALTER TABLE command, making changes to the specified table.
     * @param filteredInput is the filtered input
     * @param tables is a list of system tables
     */
    public void alterTable(String[] filteredInput, List<Table> tables) {

        RuleGraph alterTableRuleGraph = RuleGraphTypes.getAlterTableRuleGraph();

        // getting the table, column data, and what to do
        String tableName = alterTableRuleGraph.getTokensAt(filteredInput, 2).get(0);
        String alterType = alterTableRuleGraph.getTokensAt(filteredInput, 3, 4, 5).get(0);

        if (alterType.equalsIgnoreCase("modify")) {

            String columnName = alterTableRuleGraph.getTokensAt(filteredInput, 6).get(0);
            String dataTypeName = alterTableRuleGraph.getTokensAt(filteredInput, 7, 8, 9).get(0);
            String sizeName = alterTableRuleGraph.getTokensAt(filteredInput, 11).get(0);

            DataType dataType = DataType.convertToDataType(dataTypeName);
            int size = Integer.parseInt(sizeName);

            outerLoop:
            for (Table table : tables) {
                if (table.getTableName().equalsIgnoreCase(tableName)) {
                    for (Column column : table.getColumns()) {
                        if (column.getColumnName().equalsIgnoreCase(columnName)) {
                            column.setSize(size);
                            column.setDataType(dataType);
                            break outerLoop;
                        }
                    }
                }
            }

        } else if (alterType.equalsIgnoreCase("add")) {

            boolean isAddingKey = ! alterTableRuleGraph.getTokensAt(filteredInput, 15).isEmpty();

            // we're adding a key to the table
            if (isAddingKey) {

                String keyTypeName = alterTableRuleGraph.getTokensAt(filteredInput, 13, 14).get(0);
                String columnName = alterTableRuleGraph.getTokensAt(filteredInput, 16).get(0);

                if (keyTypeName.equalsIgnoreCase("foreign")) {
                    for (Table table : tables) {
                        if (table.getTableName().equalsIgnoreCase(tableName)) {
                            table.addForeignKey(columnName.split("\\.")[0], columnName.split("\\.")[1]);
                            break;
                        }
                    }

                } else if (keyTypeName.equalsIgnoreCase("primary")) {
                    for (Table table : tables) {
                        if (table.getTableName().equalsIgnoreCase(tableName)) {
                            table.addPrimaryKey(columnName);
                            break;
                        }
                    }
                }

            // just adding a new column to the table
            } else {

                String columnName = alterTableRuleGraph.getTokensAt(filteredInput, 6).get(0);
                String dataTypeName = alterTableRuleGraph.getTokensAt(filteredInput, 7, 8, 9).get(0);
                String sizeName = alterTableRuleGraph.getTokensAt(filteredInput, 11).get(0);

                DataType dataType = DataType.convertToDataType(dataTypeName);
                int size = Integer.parseInt(sizeName);

                Column column = new Column(columnName, dataType, size, 0);

                for (Table table : tables) {
                    if (table.getTableName().equalsIgnoreCase(tableName)) {
                        table.addColumn(column);
                        break;
                    }
                }
            }

        } else if (alterType.equalsIgnoreCase("drop")) {

            String columnName = alterTableRuleGraph.getTokensAt(filteredInput, 16).get(0);
            boolean isDroppingKey = ! alterTableRuleGraph.getTokensAt(filteredInput, 15).isEmpty();

            if (isDroppingKey) {

                String keyName = alterTableRuleGraph.getTokensAt(filteredInput, 13, 14).get(0);
                Table referencedTable = Utilities.getReferencedTable(tableName, tables);
                assert referencedTable != null;

                if (keyName.equalsIgnoreCase("primary")) {
                    List<String> primaryKeys = referencedTable.getPrimaryKeys();
                    for (int i = 0; i < primaryKeys.size(); i++) {
                        String primaryKey = primaryKeys.get(i);
                        if (primaryKey.equalsIgnoreCase(columnName)) {
                            primaryKeys.remove(i);
                            break;
                        }
                    }

                } else if (keyName.equalsIgnoreCase("foreign")) {
                    Map<String, String> foreignKeys = referencedTable.getForeignKeys();
                    foreignKeys.remove(columnName.split("\\.")[0]);
                }

            // just dropping a column
            } else {
                for (Table table : tables) {
                    if (table.getTableName().equalsIgnoreCase(tableName)) {
                        table.removeColumn(columnName);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Executes the ALTER TABLE command, making changes to the specified table.
     * @param filteredInput is the filtered input
     * @param tables is a list of system tables
     */
    public void insert(String[] filteredInput, List<Table> tables) {

        RuleGraph insertRuleGraph = RuleGraphTypes.getInsertRuleGraph();

        String tableName = insertRuleGraph.getTokensAt(filteredInput, 2).get(0);
        List<String> valueNames = insertRuleGraph.getTokensAt(filteredInput, 5, 7);

        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {
                // if there are less values than what's stored in the table, add nulls
                while (valueNames.size() < table.getNumCols()) {
                    valueNames.add("null");
                }
                // add the new row
                table.addRow(valueNames);
                break;
            }
        }
    }

    /**
     * Executes the ALTER TABLE command, deleting rows from the supplied table that meets the predicate.
     * @param filteredInput is the filtered input
     * @param tables is a list of system tables
     */
    public void delete(String[] filteredInput, List<Table> tables) {

        RuleGraph deleteRuleGraph = RuleGraphTypes.getDeleteRuleGraph();

        String tableName = deleteRuleGraph.getTokensAt(filteredInput, 2).get(0);
        String columnName = deleteRuleGraph.getTokensAt(filteredInput, 4).get(0);
        String symbolName = deleteRuleGraph.getTokensAt(filteredInput, 5, 6, 7, 8, 9, 10).get(0);
        Symbol symbol = Symbol.convertToSymbol(symbolName);
        String valueName = deleteRuleGraph.getTokensAt(filteredInput, 11, 13).get(0);

        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {
                // get the table data
                TableData tableData = table.getTableData();

                // get the column index that's mapped to the tableData and determine what type of data it holds
                int mappedColInd = -1;
                DataType dataType = null;

                for (int i = 0; i < table.getNumCols(); i++) {
                    if (table.getColumns().get(i).getColumnName().equalsIgnoreCase(columnName)) {
                        mappedColInd = i;
                        dataType = table.getColumns().get(i).getDataType();
                        break;
                    }
                }

                List<List<String>> rowsToKeep = new ArrayList<>();
                List<List<String>> data = tableData.getData();

                for (List<String> row : data) {

                    // determine data type
                    if (dataType == DataType.CHAR) {

                        String tableValue = row.get(mappedColInd);

                        // if the condition is met, don't add the data to the rows to keep, just continue past
                        switch (symbol) {
                            case EQUAL:
                                if (tableValue.equalsIgnoreCase(valueName)) {
                                    continue;
                                }
                                break;
                            case NOT_EQUAL:
                                if (! tableValue.equalsIgnoreCase(valueName)) {
                                    continue;
                                }
                                break;
                        }

                    } else if (dataType == DataType.NUMBER) {

                        double value = Double.parseDouble(valueName);
                        double tableValue = Double.parseDouble(row.get(mappedColInd));

                        switch (symbol) {
                            case EQUAL:
                                if (tableValue == value) {
                                    continue;
                                }
                                break;
                            case NOT_EQUAL:
                                if (tableValue != value) {
                                    continue;
                                }
                                break;
                            case GREATER_THAN:
                                if (tableValue > value) {
                                    continue;
                                }
                                break;
                            case LESS_THAN:
                                if (tableValue < value) {
                                    continue;
                                }
                                break;
                            case GREATER_THAN_OR_EQUAL:
                                if (tableValue >= value) {
                                    continue;
                                }
                                break;
                            case LESS_THAN_OR_EQUAL:
                                if (tableValue <= value) {
                                    continue;
                                }
                                break;
                        }

                    } else if (dataType == DataType.DATE) {

                        LocalDate value = LocalDate.parse(valueName);
                        LocalDate tableDate = LocalDate.parse(row.get(mappedColInd));

                        switch (symbol) {
                            case EQUAL:
                                if (tableDate.equals(value)) {
                                    continue;
                                }
                                break;
                            case NOT_EQUAL:
                                if (! tableDate.equals(value)) {
                                    continue;
                                }
                                break;
                            case GREATER_THAN:
                                if (tableDate.compareTo(value) > 0) {
                                    continue;
                                }
                                break;
                            case LESS_THAN:
                                if (tableDate.compareTo(value) < 0) {
                                    continue;
                                }
                                break;
                            case GREATER_THAN_OR_EQUAL:
                                if (tableDate.compareTo(value) >= 0) {
                                    continue;
                                }
                                break;
                            case LESS_THAN_OR_EQUAL:
                                if (tableDate.compareTo(value) <= 0) {
                                    continue;
                                }
                                break;
                        }
                    }

                    // didn't meet any of the conditions for removal, good to go
                    rowsToKeep.add(row);
                }

                // set the table data to the rows to keep
                tableData.setData(rowsToKeep);
                break;
            } // end if statement checking for table name equality
        } // end loop through tables
    }

    /**
     * Executes the UPDATE command, making an update to the specified table.
     * @param filteredInput is the filtered input
     * @param tables is a list of system tables
     */
    public void update(String[] filteredInput, List<Table> tables) {

        RuleGraph updateRuleGraph = RuleGraphTypes.getUpdateRuleGraph();

        // get input
        String tableName = updateRuleGraph.getTokensAt(filteredInput, 1).get(0);
        String setColumnName = updateRuleGraph.getTokensAt(filteredInput, 3).get(0);
        String newValue = updateRuleGraph.getTokensAt(filteredInput, 5, 7).get(0);
        String whereColumnName = updateRuleGraph.getTokensAt(filteredInput, 10).get(0);
        String equalsValue = updateRuleGraph.getTokensAt(filteredInput, 12, 14).get(0);

        Table tableReference = Utilities.getReferencedTable(tableName, tables);
        assert tableReference != null;
        List<Column> columns = tableReference.getColumns();

        // get locations needed for table data
        int setTableDataLocation = -1, whereTableDataLocation = -1;

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (column.getColumnName().equalsIgnoreCase(setColumnName)) {
                setTableDataLocation = i;
            }
            if (column.getColumnName().equalsIgnoreCase(whereColumnName)) {
                whereTableDataLocation = i;
            }
        }

        assert setTableDataLocation != -1 && whereTableDataLocation != -1;

        // search for the rows that satisfy the condition
        for (List<String> row : tableReference.getTableData().getData()) {
            String tableValue = row.get(whereTableDataLocation);
            if (tableValue.equalsIgnoreCase(equalsValue)) {
                row.set(setTableDataLocation, newValue);
            }
        }
    }

    /**
     * Executes the BUILD FILE STRUCTURE command, making changes to the file structure built on the supplied column.
     * @param filteredInput is the filtered input
     * @param tables is a list of system tables
     */
    public void buildFileStructure(String[] filteredInput, List<Table> tables) {

        RuleGraph buildFileStructureRuleGraph = RuleGraphTypes.getBuildFileStructureRuleGraph();

        // getting input
        List<String> fileStructureInput = buildFileStructureRuleGraph.getTokensAt(filteredInput, 1, 2, 3, 4, 5, 10);
        String formatted = fileStructureInput.get(0) + " " + fileStructureInput.get(1);
        FileStructure fileStructure = FileStructure.convertToFileStructure(formatted);

        // hash tables, secondary b-trees, and clustered b-trees have similar input locations
        if (fileStructure != FileStructure.CLUSTERED_FILE) {

            String columnInput = buildFileStructureRuleGraph.getTokensAt(filteredInput, 7).get(0);
            String tableInput = buildFileStructureRuleGraph.getTokensAt(filteredInput, 9).get(0);

            for (Table table : tables) {
                String tableName = table.getTableName();
                if (tableName.equalsIgnoreCase(tableInput)) {
                    Column column = table.getColumn(columnInput);
                    // determine type of file structure to build
                    switch (fileStructure) {
                        case HASH_TABLE:
                            column.setFileStructure(FileStructure.HASH_TABLE);
                            break;
                        case SECONDARY_B_TREE:
                            column.setFileStructure(FileStructure.SECONDARY_B_TREE);
                            break;
                        case CLUSTERED_B_TREE:
                            column.setFileStructure(FileStructure.CLUSTERED_B_TREE);
                            break;
                    }
                    break;
                }
            }

        // clustering files
        } else {

            String firstTableInput = buildFileStructureRuleGraph.getTokensAt(filteredInput, 12).get(0);
            String secondTableInput = buildFileStructureRuleGraph.getTokensAt(filteredInput, 14).get(0);

            for (Table table : tables) {
                String tableName = table.getTableName();
                if (tableName.equalsIgnoreCase(firstTableInput)) {
                    // will need to remove any preexisting file structures on columns from both tables
                    for (Column column : table.getColumns()) {
                        column.removeFileStructure();
                    }
                    // set this table clustered with the other table
                    table.setClusteredWith(secondTableInput);
                }

                if (tableName.equalsIgnoreCase(secondTableInput)) {
                    for (Column column : table.getColumns()) {
                        column.removeFileStructure();
                    }
                    table.setClusteredWith(firstTableInput);
                }
            }
        }
    }

    /**
     * Executes the REMOVE FILE STRUCTURE command, removing the file structure on the supplied column
     * or removing the clustering on two tables if one exists.
     * @param filteredInput is the filtered input
     * @param tables is a list of system tables
     */
    public void removeFileStructure(String[] filteredInput, List<Table> tables) {

        RuleGraph removeFileStructure = RuleGraphTypes.getRemoveFileStructureRuleGraph();
        boolean removingClusteredFile = ! removeFileStructure.getTokensAt(filteredInput, 7).isEmpty();

        if (removingClusteredFile) {

            String firstClusteredTableInput = removeFileStructure.getTokensAt(filteredInput, 10).get(0);
            String secondClusteredTableInput = removeFileStructure.getTokensAt(filteredInput, 6).get(0);

            // search though the list of tables, checking if it's what we're looking for
            for (Table table : tables) {
                String tableName = table.getTableName();
                if (tableName.equalsIgnoreCase(firstClusteredTableInput) ||
                        tableName.equalsIgnoreCase(secondClusteredTableInput)) {
                    table.setClusteredWith("none");
                }
            }

        // just remove the file structure
        } else {

            String tableInput = removeFileStructure.getTokensAt(filteredInput, 6).get(0);
            String columnInput = removeFileStructure.getTokensAt(filteredInput, 4).get(0);

            for (Table table : tables) {
                String tableName = table.getTableName();
                if (tableName.equalsIgnoreCase(tableInput)) {
                    Column column = table.getColumn(columnInput);
                    column.removeFileStructure();
                    break;
                }
            }
        }
    }

    /**
     * Executes the GRANT command, granting privileges to the specified users on the given table.
     * @param filteredInput is the filtered input
     * @param users is a list of system users
     * @param tables is a list of system tables
     */
    public void grant(String[] filteredInput, List<User> users, List<Table> tables) {

        RuleGraph grantRuleGraph = RuleGraphTypes.getGrantRuleGraph();

        // getting what to grant
        List<String> privilegesInput = grantRuleGraph.getTokensAt(filteredInput, 1, 2, 3, 4, 5, 6, 7, 8);
        List<String> updateColumnsInput = grantRuleGraph.getTokensAt(filteredInput, 12);
        List<String> referenceColumnsInput = grantRuleGraph.getTokensAt(filteredInput, 16);
        String tableInput = grantRuleGraph.getTokensAt(filteredInput, 20).get(0);
        List<String> userNamesInput = grantRuleGraph.getTokensAt(filteredInput, 22);

        // rule graph would only allow for one element if granting all privileges
        boolean isGrantingAllPrivileges =
                Privilege.convertToPrivilege(privilegesInput.get(0)) == Privilege.ALL_PRIVILEGES;
        // check if using grant option
        boolean hasWithGrantOption = ! grantRuleGraph.getTokensAt(filteredInput, 25).isEmpty();

        // map the data
        Table tableReference = Utilities.getReferencedTable(tableInput, tables);
        assert tableReference != null;
        List<User> referencedUsers = Utilities.getReferencedUsers(userNamesInput, users);

        // granting all privileges
        if (isGrantingAllPrivileges) {

            for (User referencedUser : referencedUsers) {

                TablePrivileges tablePrivileges = new TablePrivileges();
                tablePrivileges.grantAllPrivileges(tableReference);
                referencedUser.addTablePrivileges(tablePrivileges);

                if (hasWithGrantOption) {
                    TablePrivileges grantedTablePrivileges = new TablePrivileges();
                    grantedTablePrivileges.grantAllPrivileges(tableReference);
                    referencedUser.addGrantedTablePrivileges(grantedTablePrivileges);
                }
            }

        // just granting a select amount of privileges
        } else {

            for (User referencedUser : referencedUsers) {

                List<Privilege> privileges = privilegesInput.stream()
                        .map(Privilege::convertToPrivilege)
                        .collect(Collectors.toList());

                TablePrivileges tablePrivileges = new TablePrivileges();
                tablePrivileges.grantPrivileges(privileges);
                tablePrivileges.addUpdateColumns(updateColumnsInput);
                tablePrivileges.addReferencesColumns(referenceColumnsInput);

                referencedUser.addTablePrivileges(tablePrivileges);

                if (hasWithGrantOption) {

                    TablePrivileges grantedTablePrivileges = new TablePrivileges();
                    grantedTablePrivileges.grantPrivileges(privileges);
                    grantedTablePrivileges.addUpdateColumns(updateColumnsInput);
                    grantedTablePrivileges.addReferencesColumns(referenceColumnsInput);

                    referencedUser.addGrantedTablePrivileges(grantedTablePrivileges);
                }
            }
        }
    }

    /**
     * Executes the REVOKE command, revoking privileges from the supplied users on a given table.
     * @param filteredInput is the filtered input
     * @param users is a list of the system users
     */
    public void revoke(String[] filteredInput, List<User> users) {

        RuleGraph revokeRuleGraph = RuleGraphTypes.getRevokeRuleGraph();

        // getting what to revoke
        List<String> privilegesInput = revokeRuleGraph.getTokensAt(filteredInput, 1, 2, 3, 4, 5, 6, 7, 8);
        List<String> updateColumnsInput = revokeRuleGraph.getTokensAt(filteredInput, 12);
        List<String> referenceColumnsInput = revokeRuleGraph.getTokensAt(filteredInput, 16);
        String tableInput = revokeRuleGraph.getTokensAt(filteredInput, 20).get(0);
        List<String> usernamesInput = revokeRuleGraph.getTokensAt(filteredInput, 22);

        boolean isRevokingAllPrivileges =
                Privilege.convertToPrivilege(privilegesInput.get(0)) == Privilege.ALL_PRIVILEGES;

        // map the data
        List<User> referencedUsers = Utilities.getReferencedUsers(usernamesInput, users);

        if (isRevokingAllPrivileges) {

            for (User referencedUser : referencedUsers) {
                referencedUser.revokeAllTablePrivilegesAndGrantedTablePrivileges(tableInput);
            }

        } else {

            for (User referencedUser : referencedUsers) {

                List<Privilege> privileges = privilegesInput.stream()
                        .map(Privilege::convertToPrivilege)
                        .collect(Collectors.toList());

                TablePrivileges tablePrivileges = new TablePrivileges();
                tablePrivileges.grantPrivileges(privileges);
                tablePrivileges.addUpdateColumns(updateColumnsInput);
                tablePrivileges.addReferencesColumns(referenceColumnsInput);

                referencedUser.revokeTablePrivilegesAndGrantedTablePrivileges(tablePrivileges);
            }
        }
    }
}