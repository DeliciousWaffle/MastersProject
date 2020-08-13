package systemcatalog.components;

import datastructures.relation.resultset.ResultSet;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.FileStructure;
import datastructures.relation.table.component.TableData;
import datastructures.trees.querytree.QueryTree;
import datastructures.trees.querytree.operator.types.Relation;
import datastructures.user.component.Privilege;
import datastructures.user.component.TablePrivileges;
import utilities.enums.InputType;
import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.user.User;
import utilities.enums.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for executing a given input. If the input is a query, an associated result set
 * will be produced. If the input is a DML statement, then the change will be carried out here.
 */
public class Compiler {

    private RuleGraph.Type ruleGraphType;
    private RuleGraph ruleGraphToUse;
    private String[] tokenizedInput;
    private List<Table> tables;
    private List<User> users;
    private List<QueryTree> queryTreeStates;

    public Compiler() {
        queryTreeStates = new ArrayList<>();
    }

    // setters ---------------------------------------------------------------------------------------------------------

    public void setRuleGraphType(RuleGraph.Type ruleGraphType) {
        this.ruleGraphType = ruleGraphType;
    }

    public void setRuleGraphToUse(RuleGraph ruleGraphToUse) {
        this.ruleGraphToUse = ruleGraphToUse;
    }

    public void setTokenizedInput(String[] tokenizedInput) {
        this.tokenizedInput = tokenizedInput;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setQueryTreeStates(List<QueryTree> queryTreeStates) {
        this.queryTreeStates = queryTreeStates;
    }

    // private utility methods -----------------------------------------------------------------------------------------

    private Table relationToTable(Relation relation, List<Table> tables) {
        for(Table table : tables) {
            if(relation.getTableName().equalsIgnoreCase(table.getTableName())) {
                return table;
            }
        }
        return null;
    }

    // query execution -------------------------------------------------------------------------------------------------

    public ResultSet executeQuery() {
        return null;
    }

    // DML execution ---------------------------------------------------------------------------------------------------

    public void executeDML() {
        switch(ruleGraphType) {
            case CREATE_TABLE:
                createTable();
                break;
            case DROP_TABLE:
                dropTable();
                break;
            case ALTER_TABLE:
                alterTable();
                break;
            case INSERT:
                insert();
                break;
            case DELETE:
                delete();
                break;
            case UPDATE:
                update();
                break;
            case BUILD_FILE_STRUCTURE:
                buildFileStructure();
                break;
            case REMOVE_FILE_STRUCTURE:
                removeFileStructure();
                break;
            case GRANT:
                grant();
                break;
            case REVOKE:
                revoke();
                break;
            case UNKNOWN:
            default:
                break;
        }
    }

    public void createTable() {

        // getting the table data
        String tableName = ruleGraphToUse.getTokensAt(tokenizedInput, 2).get(0);
        List<String> columnNames = ruleGraphToUse.getTokensAt(tokenizedInput, 4);
        List<String> dataTypeNames = ruleGraphToUse.getTokensAt(tokenizedInput, 5, 6);
        List<String> sizeNames = ruleGraphToUse.getTokensAt(tokenizedInput, 8);

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

    public void dropTable() {

        // getting the table name to remove
        String tableNameToRemove = ruleGraphToUse.getTokensAt(tokenizedInput, 2).get(0);

        // remove the table
        for (int i = 0; i < tables.size(); i++) {
            String tableName = tables.get(i).getTableName();
            if (tableName.equalsIgnoreCase(tableNameToRemove)) {
                tables.remove(i);
                break;
            }
        }
    }

    public void alterTable() {

        // getting the table, column data, and what to do
        String tableName = ruleGraphToUse.getTokensAt(tokenizedInput, 2).get(0);
        String alterType = ruleGraphToUse.getTokensAt(tokenizedInput, 3, 4, 5).get(0);

        if (alterType.equalsIgnoreCase("modify")) {

            String columnName = ruleGraphToUse.getTokensAt(tokenizedInput, 6).get(0);
            String dataTypeName = ruleGraphToUse.getTokensAt(tokenizedInput, 7, 8).get(0);
            String sizeName = ruleGraphToUse.getTokensAt(tokenizedInput, 10).get(0);

            DataType dataType = DataType.convertToDataType(dataTypeName);
            int size = Integer.parseInt(sizeName);

            outerLoop:
            for (Table table : tables) {
                if (table.getTableName().equalsIgnoreCase(tableName)) {
                    for (Column column : table.getColumns()) {
                        if (column.getName().equalsIgnoreCase(columnName)) {
                            column.setSize(size);
                            column.setDataType(dataType);
                            break outerLoop;
                        }
                    }
                }
            }

        } else if (alterType.equalsIgnoreCase("add")) {

            boolean isAddingKey = !ruleGraphToUse.getTokensAt(tokenizedInput, 14).isEmpty();

            // we're adding a key to the table
            if (isAddingKey) {

                String keyTypeName = ruleGraphToUse.getTokensAt(tokenizedInput, 12, 13).get(0);
                String columnName = ruleGraphToUse.getTokensAt(tokenizedInput, 15).get(0);

                if (keyTypeName.equalsIgnoreCase("foreign")) {
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
                }

                // just adding a new column to the table
            } else {

                String columnName = ruleGraphToUse.getTokensAt(tokenizedInput, 6).get(0);
                String dataTypeName = ruleGraphToUse.getTokensAt(tokenizedInput, 7, 8).get(0);
                String sizeName = ruleGraphToUse.getTokensAt(tokenizedInput, 10).get(0);

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

            String columnName = ruleGraphToUse.getTokensAt(tokenizedInput, 15).get(0);

            for (Table table : tables) {
                if (table.getTableName().equalsIgnoreCase(tableName)) {
                    table.removeColumn(columnName);
                    break;
                }
            }
        }
    }

    public void insert() {

        String tableName = ruleGraphToUse.getTokensAt(tokenizedInput, 2).get(0);
        List<String> valueNames = ruleGraphToUse.getTokensAt(tokenizedInput, 5);

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

    public void delete() {

        String tableName = ruleGraphToUse.getTokensAt(tokenizedInput, 2).get(0);
        String columnName = ruleGraphToUse.getTokensAt(tokenizedInput, 4).get(0);
        String symbolName = ruleGraphToUse.getTokensAt(tokenizedInput, 5, 6, 7, 8, 9, 10).get(0);
        Symbol symbol = Symbol.convertToSymbol(symbolName);
        String constantName = ruleGraphToUse.getTokensAt(tokenizedInput, 11).get(0);

        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {

                // get the table data
                TableData tableData = table.getTableData();

                // get the column index that's mapped to the tableData and determine what type of data it holds
                int mappedColInd = -1;
                DataType dataType = null;

                for (int i = 0; i < table.getNumCols(); i++) {
                    if (table.getColumns().get(i).getName().equalsIgnoreCase(columnName)) {
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

    public void update() {

        // get input
        String tableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 1).get(0);
        String setColumnInput = ruleGraphToUse.getTokensAt(tokenizedInput, 3).get(0);
        String updateToValueInput = ruleGraphToUse.getTokensAt(tokenizedInput, 5).get(0);

        boolean updatingWholeColumn = ruleGraphToUse.getTokensAt(tokenizedInput, 6).isEmpty();

        // search for the table
        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableInput)) {

                // get the column to update
                TableData tableData = table.getTableData();
                int mappedColInd = -1;

                for (int i = 0; i < table.getNumCols(); i++) {
                    if (table.getColumns().get(i).getName().equalsIgnoreCase(setColumnInput)) {
                        mappedColInd = i;
                        break;
                    }
                }

                // updating a whole column to a new value
                if (updatingWholeColumn) {
                    tableData.updateCellAt(mappedColInd, updateToValueInput);

                    // updating values to the new value
                } else {

                    String whereColumnInput = ruleGraphToUse.getTokensAt(tokenizedInput, 7).get(0);
                    String equalsValueInput = ruleGraphToUse.getTokensAt(tokenizedInput, 9).get(0);

                    // find what column the where column is mapped to
                    int whereMappedColInd = -1;

                    for (int i = 0; i < table.getNumCols(); i++) {
                        if (table.getColumns().get(i).getName().equalsIgnoreCase(whereColumnInput)) {
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

    public void buildFileStructure() {

        // getting input
        List<String> fileStructureInput = ruleGraphToUse.getTokensAt(tokenizedInput, 1, 2, 3, 4, 5, 10);
        String formatted = fileStructureInput.get(0) + " " + fileStructureInput.get(1);
        FileStructure fileStructure = FileStructure.convertToFileStructure(formatted);

        // hash tables, secondary b-trees, and clustered b-trees have similar input locations
        if (fileStructure != FileStructure.CLUSTERED_FILE) {

            String columnInput = ruleGraphToUse.getTokensAt(tokenizedInput, 7).get(0);
            String tableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 9).get(0);

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

            String firstTableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 12).get(0);
            String secondTableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 14).get(0);

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

    public void removeFileStructure() {

        boolean removingClusteredFile = ! ruleGraphToUse.getTokensAt(tokenizedInput, 7).isEmpty();

        if (removingClusteredFile) {

            String firstClusteredTableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 10).get(0);
            String secondClusteredTableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 6).get(0);

            // search though the list of tables, checking if it's what we're looking for
            for(Table table : tables) {
                String tableName = table.getTableName();
                if(tableName.equalsIgnoreCase(firstClusteredTableInput) ||
                        tableName.equalsIgnoreCase(secondClusteredTableInput)) {
                    table.setClusteredWith("none");
                }
            }

        // just remove the file structure
        } else {

            String tableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 6).get(0);
            String columnInput = ruleGraphToUse.getTokensAt(tokenizedInput, 4).get(0);

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

    public void grant() {

        // getting what to grant
        List<String> privilegesInput = ruleGraphToUse.getTokensAt(tokenizedInput, 1, 2, 3, 4, 5, 6, 7, 8);
        List<String> updateColumnsInput = ruleGraphToUse.getTokensAt(tokenizedInput, 6);
        List<String> referenceColumnsInput = ruleGraphToUse.getTokensAt(tokenizedInput, 7);
        String tableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 20).get(0);
        List<String> userNamesInput = ruleGraphToUse.getTokensAt(tokenizedInput, 22);

        // check if using grant option
        boolean hasWithGrantOption = ! ruleGraphToUse.getTokensAt(tokenizedInput, 25).isEmpty();

        // create the privileges from privilege input
        List<Privilege> privileges = new ArrayList<>();

        // if granting all privileges, well, do that
        if((privilegesInput.size() == 1) && privilegesInput.get(0).equalsIgnoreCase("all")) {
            privileges = Privilege.getAllPrivileges();
            // figure out what column names are within the given table in order to set the update and reference columns
            for(Table table : tables) {
                String tableName = table.getTableName();
                if(tableName.equalsIgnoreCase(tableInput)) {
                    for(Column column : table.getColumns()) {
                        String columnName = column.getName();
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
        for(User user : users) {
            String userName = user.getUsername();
            for(String userNameInput : userNamesInput) {
                if(userName.equalsIgnoreCase(userNameInput)) {
                    user.addTablePrivileges(tablePrivileges);
                    if(hasWithGrantOption) {
                        user.addPassableTablePrivileges(passableTablePrivileges);
                    }
                    break;
                }
            }
        }
    }

    public void revoke() {

        // getting what to revoke
        List<String> privilegesInput = ruleGraphToUse.getTokensAt(tokenizedInput, 1, 2, 3, 4, 5, 6, 7, 8);
        List<String> updateColumnsInput = ruleGraphToUse.getTokensAt(tokenizedInput, 6);
        List<String> referenceColumnsInput = ruleGraphToUse.getTokensAt(tokenizedInput, 7);
        String tableInput = ruleGraphToUse.getTokensAt(tokenizedInput, 20).get(0);
        List<String> usernamesInput = ruleGraphToUse.getTokensAt(tokenizedInput, 22);

        // check if revoking all privileges
        if((privilegesInput.size() == 1) && privilegesInput.get(0).equalsIgnoreCase("all")) {
            for(User user : users) {
                String username = user.getUsername();
                for(String usernameInput : usernamesInput) {
                    if(usernameInput.equalsIgnoreCase(username)) {
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