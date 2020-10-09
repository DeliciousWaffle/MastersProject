package systemcatalog.components;

import datastructures.relation.resultset.ResultSet;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.FileStructure;
import datastructures.relation.table.component.TableData;
import datastructures.querytree.QueryTree;
import datastructures.querytree.operator.types.Relation;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.user.component.Privilege;
import datastructures.user.component.TablePrivileges;
import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.user.User;
import enums.InputType;
import enums.Symbol;

import java.util.ArrayList;
import java.util.List;

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
    public ResultSet executeQuery(List<QueryTree> queryTreeStates) {
        QueryTree queryTreeBeforePipelining = queryTreeStates.get(5);
        return null;
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

        RuleGraph createTableRuleGraph = RuleGraphTypes.getCreateTableRuleGraph();

        // getting the table data
        String tableName = createTableRuleGraph.getTokensAt(filteredInput, 2).get(0);
        List<String> columnNames = createTableRuleGraph.getTokensAt(filteredInput, 4);
        List<String> dataTypeNames = createTableRuleGraph.getTokensAt(filteredInput, 5, 6);
        List<String> sizeNames = createTableRuleGraph.getTokensAt(filteredInput, 8);

        // convert the input into a table along with its column data
        Table table = new Table(tableName);
        List<Column> columns = new ArrayList<>();

        for (int i = 0; i < columnNames.size(); i++) {

            String columnName = columnNames.get(i);
            String dataTypeName = dataTypeNames.get(i);
            String sizeName = sizeNames.get(i);

            DataType dataType = DataType.convertToDataType(dataTypeName);
            int size = Integer.parseInt(sizeName);

            Column column = new Column(columnName, dataType, size);
            columns.add(column);
        }

        table.setColumns(columns);
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
            String dataTypeName = alterTableRuleGraph.getTokensAt(filteredInput, 7, 8).get(0);
            String sizeName = alterTableRuleGraph.getTokensAt(filteredInput, 10).get(0);

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

            boolean isAddingKey = ! alterTableRuleGraph.getTokensAt(filteredInput, 14).isEmpty();

            // we're adding a key to the table
            if (isAddingKey) {

                String keyTypeName = alterTableRuleGraph.getTokensAt(filteredInput, 12, 13).get(0);
                String columnName = alterTableRuleGraph.getTokensAt(filteredInput, 15).get(0);

                /*if (keyTypeName.equalsIgnoreCase("foreign")) {
                    for (Table table : tables) {
                        if (table.getTableName().equalsIgnoreCase(tableName)) {
                            table.addForeignKey(columnName);
                            break;
                        }
                    }

                } else if (keyTypeName.equalsIgnoreCase("primary")) {
                    for (Table table : tables) {
                        if (table.getTableName().equalsIgnoreCase(tableName)) {
                            table.setPrimaryKey(columnName);
                            break;
                        }
                    }
                }*/

                // just adding a new column to the table
            } else {

                String columnName = alterTableRuleGraph.getTokensAt(filteredInput, 6).get(0);
                String dataTypeName = alterTableRuleGraph.getTokensAt(filteredInput, 7, 8).get(0);
                String sizeName = alterTableRuleGraph.getTokensAt(filteredInput, 10).get(0);

                DataType dataType = DataType.convertToDataType(dataTypeName);
                int size = Integer.parseInt(sizeName);

                Column column = new Column(columnName, dataType, size);

                for (Table table : tables) {
                    if (table.getTableName().equalsIgnoreCase(tableName)) {
                        table.addColumn(column);
                        break;
                    }
                }
            }

        } else if (alterType.equalsIgnoreCase("drop")) {

            String columnName = alterTableRuleGraph.getTokensAt(filteredInput, 15).get(0);

            for (Table table : tables) {
                if (table.getTableName().equalsIgnoreCase(tableName)) {
                    table.removeColumn(columnName);
                    break;
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
        List<String> valueNames = insertRuleGraph.getTokensAt(filteredInput, 5);

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
        String constantName = deleteRuleGraph.getTokensAt(filteredInput, 11).get(0);

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

                        String value = row.get(mappedColInd);

                        // if the condition is met, don't add the data to the rows to keep, just continue past
                        switch (symbol) {
                            case EQUAL:
                                if (value.equalsIgnoreCase(constantName)) {
                                    continue;
                                }
                                break;
                            case NOT_EQUAL:
                                if (!value.equalsIgnoreCase(constantName)) {
                                    continue;
                                }
                                break;
                        }

                    } else if (dataType == DataType.NUMBER) {

                        int value = Integer.parseInt(row.get(mappedColInd));
                        int constant = Integer.parseInt(constantName);

                        switch (symbol) {
                            case EQUAL:
                                if (value == constant) {
                                    continue;
                                }
                                break;
                            case NOT_EQUAL:
                                if (value != constant) {
                                    continue;
                                }
                                break;
                            case GREATER_THAN:
                                if (value > constant) {
                                    continue;
                                }
                                break;
                            case LESS_THAN:
                                if (value < constant) {
                                    continue;
                                }
                                break;
                            case GREATER_THAN_OR_EQUAL:
                                if (value >= constant) {
                                    continue;
                                }
                                break;
                            case LESS_THAN_OR_EQUAL:
                                if (value <= constant) {
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
        String tableInput = updateRuleGraph.getTokensAt(filteredInput, 1).get(0);
        String setColumnInput = updateRuleGraph.getTokensAt(filteredInput, 3).get(0);
        String updateToValueInput = updateRuleGraph.getTokensAt(filteredInput, 5).get(0);

        boolean updatingWholeColumn = updateRuleGraph.getTokensAt(filteredInput, 6).isEmpty();

        // search for the table
        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableInput)) {

                // get the column to update
                TableData tableData = table.getTableData();
                int mappedColInd = -1;

                for (int i = 0; i < table.getNumCols(); i++) {
                    if (table.getColumns().get(i).getColumnName().equalsIgnoreCase(setColumnInput)) {
                        mappedColInd = i;
                        break;
                    }
                }

                // updating a whole column to a new value
                if (updatingWholeColumn) {
                    tableData.updateCellAt(mappedColInd, updateToValueInput);

                    // updating values to the new value
                } else {

                    String whereColumnInput = updateRuleGraph.getTokensAt(filteredInput, 7).get(0);
                    String equalsValueInput = updateRuleGraph.getTokensAt(filteredInput, 9).get(0);

                    // find what column the where column is mapped to
                    int whereMappedColInd = -1;

                    for (int i = 0; i < table.getNumCols(); i++) {
                        if (table.getColumns().get(i).getColumnName().equalsIgnoreCase(whereColumnInput)) {
                            whereMappedColInd = i;
                            break;
                        }
                    }

                    for (int row = 0; row < tableData.getNumRows(); row++) {
                        if (tableData.getData().get(row).get(whereMappedColInd).equalsIgnoreCase(equalsValueInput)) {
                            tableData.updateCellAt(row, mappedColInd, updateToValueInput);
                        }
                    }
                }

                break;
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
        List<String> updateColumnsInput = grantRuleGraph.getTokensAt(filteredInput, 6);
        List<String> referenceColumnsInput = grantRuleGraph.getTokensAt(filteredInput, 7);
        String tableInput = grantRuleGraph.getTokensAt(filteredInput, 20).get(0);
        List<String> userNamesInput = grantRuleGraph.getTokensAt(filteredInput, 22);

        // check if using grant option
        boolean hasWithGrantOption = ! grantRuleGraph.getTokensAt(filteredInput, 25).isEmpty();

        // create the privileges from privilege input
        List<Privilege> privileges = new ArrayList<>();

        // if granting all privileges, well, do that
        if ((privilegesInput.size() == 1) && privilegesInput.get(0).equalsIgnoreCase("all")) {
            privileges = Privilege.getAllPrivileges();
            // figure out what column names are within the given table in order to set the update and reference columns
            for (Table table : tables) {
                String tableName = table.getTableName();
                if (tableName.equalsIgnoreCase(tableInput)) {
                    for (Column column : table.getColumns()) {
                        String columnName = column.getColumnName();
                        updateColumnsInput.add(columnName);
                        referenceColumnsInput.add(columnName);
                    }
                    break;
                }
            }
        } else {
            for (String privilegeInput : privilegesInput) {
                privileges.add(Privilege.convertToPrivilege(privilegeInput));
            }
        }

        // create the table privileges to be granted
        TablePrivileges tablePrivileges = new TablePrivileges(tableInput, privileges,
                updateColumnsInput, referenceColumnsInput);

        // the user(s) will only have this if the grant option was used
        TablePrivileges passableTablePrivileges = new TablePrivileges(tablePrivileges);

        // for each user to be granted table privileges, grant the table privileges
        for (User user : users) {
            String userName = user.getUsername();
            for (String userNameInput : userNamesInput) {
                if (userName.equalsIgnoreCase(userNameInput)) {
                    user.addTablePrivileges(tablePrivileges);
                    if (hasWithGrantOption) {
                        user.addPassableTablePrivileges(passableTablePrivileges);
                    }
                    break;
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
        List<String> updateColumnsInput = revokeRuleGraph.getTokensAt(filteredInput, 6);
        List<String> referenceColumnsInput = revokeRuleGraph.getTokensAt(filteredInput, 7);
        String tableInput = revokeRuleGraph.getTokensAt(filteredInput, 20).get(0);
        List<String> usernamesInput = revokeRuleGraph.getTokensAt(filteredInput, 22);

        // check if revoking all privileges
        if ((privilegesInput.size() == 1) && privilegesInput.get(0).equalsIgnoreCase("all")) {
            for (User user : users) {
                String username = user.getUsername();
                for (String usernameInput : usernamesInput) {
                    if (usernameInput.equalsIgnoreCase(username)) {
                        user.revokeAllTablePrivileges(tableInput);
                        break;
                    }
                }
            }

        } else {

            // create the privileges from privilege input
            List<Privilege> privileges = new ArrayList<>();

            for (String privilegeInput : privilegesInput) {
                privileges.add(Privilege.convertToPrivilege(privilegeInput));
            }

            // create the table privileges to revoke
            TablePrivileges tablePrivilegesToRevoke = new TablePrivileges(tableInput, privileges,
                    updateColumnsInput, referenceColumnsInput);

            // for each user to be revoked table privileges, revoke the table privileges
            for (User user : users) {
                String userName = user.getUsername();
                for (String userNameInput : usernamesInput) {
                    if (userName.equalsIgnoreCase(userNameInput)) {
                        // revokes passable table privileges too
                        user.revokeTablePrivileges(tablePrivilegesToRevoke);
                        break;
                    }
                }
            }
        }
    }
}