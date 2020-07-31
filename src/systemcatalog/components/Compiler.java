package systemcatalog.components;

import datastructure.relation.table.component.Column;
import datastructure.relation.table.component.DataType;
import datastructure.relation.table.component.FileStructure;
import datastructure.relation.table.component.TableData;
import datastructure.user.component.Privilege;
import datastructure.user.component.TablePrivileges;
import utilities.enums.InputType;
import datastructure.rulegraph.RuleGraph;
import datastructure.relation.table.Table;
import datastructure.user.User;
import utilities.enums.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for executing a given input. If the input is a query, an associated result set
 * will be produced. If the input is a DML statement, then the change will be carried out here.
 */
public class Compiler {

    public Compiler() {
    }

    public void executeInput(InputType inputType, String[] input, RuleGraph ruleGraph,
                             ArrayList<User> users, ArrayList<Table> tables) {
        switch (inputType) {
            case QUERY:
                executeQuery(input, ruleGraph, tables);
                break;
            case CREATE_TABLE:
                createTable(input, ruleGraph, tables);
                break;
            case DROP_TABLE:
                dropTable(input, ruleGraph, tables);
                break;
            case ALTER_TABLE:
                alterTable(input, ruleGraph, tables);
                break;
            case INSERT:
                insert(input, ruleGraph, tables);
                break;
            case DELETE:
                delete(input, ruleGraph, tables);
                break;
            case UPDATE:
                update(input, ruleGraph, tables);
                break;
            case BUILD_FILE_STRUCTURE:
                buildFileStructure(input, ruleGraph, tables);
                break;
            case REMOVE_FILE_STRUCTURE:
                removeFileStructure(input, ruleGraph, tables);
                break;
            case GRANT:
                grant(input, ruleGraph, users, tables);
                break;
            case REVOKE:
                revoke(input, ruleGraph, users);
                break;
            case UNKNOWN:
            default:
                break;
        }
    }

    public void executeQuery(String[] query, RuleGraph ruleGraph, List<Table> tables) {

    }

    public void createTable(String[] createTable, RuleGraph ruleGraph, List<Table> tables) {

        // getting the table data
        String tableName = ruleGraph.getTokensAt(createTable, 2).get(0);
        List<String> columnNames = ruleGraph.getTokensAt(createTable, 4);
        List<String> dataTypeNames = ruleGraph.getTokensAt(createTable, 5, 6);
        List<String> sizeNames = ruleGraph.getTokensAt(createTable, 8);

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

    public void dropTable(String[] dropTable, RuleGraph ruleGraph, List<Table> tables) {

        // getting the table name to remove
        String tableNameToRemove = ruleGraph.getTokensAt(dropTable, 2).get(0);

        // remove the table
        for (int i = 0; i < tables.size(); i++) {
            String tableName = tables.get(i).getTableName();
            if (tableName.equalsIgnoreCase(tableNameToRemove)) {
                tables.remove(i);
                break;
            }
        }
    }

    public void alterTable(String[] alterTable, RuleGraph ruleGraph, List<Table> tables) {

        // getting the table, column data, and what to do
        String tableName = ruleGraph.getTokensAt(alterTable, 2).get(0);
        String alterType = ruleGraph.getTokensAt(alterTable, 3, 4, 5).get(0);

        if (alterType.equalsIgnoreCase("modify")) {

            String columnName = ruleGraph.getTokensAt(alterTable, 6).get(0);
            String dataTypeName = ruleGraph.getTokensAt(alterTable, 7, 8).get(0);
            String sizeName = ruleGraph.getTokensAt(alterTable, 10).get(0);

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

            boolean isAddingKey = !ruleGraph.getTokensAt(alterTable, 14).isEmpty();

            // we're adding a key to the table
            if (isAddingKey) {

                String keyTypeName = ruleGraph.getTokensAt(alterTable, 12, 13).get(0);
                String columnName = ruleGraph.getTokensAt(alterTable, 15).get(0);

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

                String columnName = ruleGraph.getTokensAt(alterTable, 6).get(0);
                String dataTypeName = ruleGraph.getTokensAt(alterTable, 7, 8).get(0);
                String sizeName = ruleGraph.getTokensAt(alterTable, 10).get(0);

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

            String columnName = ruleGraph.getTokensAt(alterTable, 15).get(0);

            for (Table table : tables) {
                if (table.getTableName().equalsIgnoreCase(tableName)) {
                    table.removeColumn(columnName);
                    break;
                }
            }
        }
    }

    public void insert(String[] insert, RuleGraph ruleGraph, List<Table> tables) {

        String tableName = ruleGraph.getTokensAt(insert, 2).get(0);
        List<String> valueNames = ruleGraph.getTokensAt(insert, 5);

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

    public void delete(String[] delete, RuleGraph ruleGraph, List<Table> tables) {

        String tableName = ruleGraph.getTokensAt(delete, 2).get(0);
        String columnName = ruleGraph.getTokensAt(delete, 4).get(0);
        String symbolName = ruleGraph.getTokensAt(delete, 5, 6, 7, 8, 9, 10).get(0);
        Symbol symbol = Symbol.convertToSymbol(symbolName);
        String constantName = ruleGraph.getTokensAt(delete, 11).get(0);

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

    public void update(String[] update, RuleGraph ruleGraph, List<Table> tables) {

        // get input
        String tableInput = ruleGraph.getTokensAt(update, 1).get(0);
        String setColumnInput = ruleGraph.getTokensAt(update, 3).get(0);
        String updateToValueInput = ruleGraph.getTokensAt(update, 5).get(0);

        boolean updatingWholeColumn = ruleGraph.getTokensAt(update, 6).isEmpty();

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

                    String whereColumnInput = ruleGraph.getTokensAt(update, 7).get(0);
                    String equalsValueInput = ruleGraph.getTokensAt(update, 9).get(0);

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

    public void buildFileStructure(String[] buildFileStructure, RuleGraph ruleGraph, List<Table> tables) {

        // getting input
        List<String> fileStructureInput = ruleGraph.getTokensAt(buildFileStructure, 1, 2, 3, 4, 5, 10);
        String formatted = fileStructureInput.get(0) + " " + fileStructureInput.get(1);
        FileStructure fileStructure = FileStructure.convertToFileStructure(formatted);

        // hash tables, secondary b-trees, and clustered b-trees have similar input locations
        if (fileStructure != FileStructure.CLUSTERED_FILE) {

            String columnInput = ruleGraph.getTokensAt(buildFileStructure, 7).get(0);
            String tableInput = ruleGraph.getTokensAt(buildFileStructure, 9).get(0);

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

            String firstTableInput = ruleGraph.getTokensAt(buildFileStructure, 12).get(0);
            String secondTableInput = ruleGraph.getTokensAt(buildFileStructure, 14).get(0);

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

    public void removeFileStructure(String[] removeFileStructure, RuleGraph ruleGraph, List<Table> tables) {

        boolean removingClusteredFile = ! ruleGraph.getTokensAt(removeFileStructure, 7).isEmpty();

        if (removingClusteredFile) {

            String firstClusteredTableInput = ruleGraph.getTokensAt(removeFileStructure, 10).get(0);
            String secondClusteredTableInput = ruleGraph.getTokensAt(removeFileStructure, 6).get(0);

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

            String tableInput = ruleGraph.getTokensAt(removeFileStructure, 6).get(0);
            String columnInput = ruleGraph.getTokensAt(removeFileStructure, 4).get(0);

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

    public void grant(String[] grant, RuleGraph ruleGraph, List<User> users, List<Table> tables) {

        // getting what to grant
        List<String> privilegesInput = ruleGraph.getTokensAt(grant, 1, 2, 3, 4, 5, 6, 7, 8);
        List<String> updateColumnsInput = ruleGraph.getTokensAt(grant, 6);
        List<String> referenceColumnsInput = ruleGraph.getTokensAt(grant, 7);
        String tableInput = ruleGraph.getTokensAt(grant, 20).get(0);
        List<String> userNamesInput = ruleGraph.getTokensAt(grant, 22);

        // check if using grant option
        boolean hasWithGrantOption = ! ruleGraph.getTokensAt(grant, 25).isEmpty();

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

    public void revoke(String[] revoke, RuleGraph ruleGraph, List<User> users) {

        // getting what to revoke
        List<String> privilegesInput = ruleGraph.getTokensAt(revoke, 1, 2, 3, 4, 5, 6, 7, 8);
        List<String> updateColumnsInput = ruleGraph.getTokensAt(revoke, 6);
        List<String> referenceColumnsInput = ruleGraph.getTokensAt(revoke, 7);
        String tableInput = ruleGraph.getTokensAt(revoke, 20).get(0);
        List<String> usernamesInput = ruleGraph.getTokensAt(revoke, 22);

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