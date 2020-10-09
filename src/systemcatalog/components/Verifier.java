package systemcatalog.components;

import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.user.User;
import utilities.OptimizerUtilities;
import utilities.Utilities;
import enums.InputType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Responsible for making sure any parts of the input that make references to something within the system
 * are correct. This can take several forms such as checking whether tables and their associated columns
 * exists or that data types match. Also has an option for turning on or off. When off, the user can input
 * any data without the worry about whether their input makes sense with respect to the system data. Also,
 * input is assumed to have already been passed through the Parser.
 */
public class Verifier {

    private String errorMessage;
    private boolean isOn;

    public Verifier() {
        errorMessage = "";
        isOn = true;
    }

    /**
     * @return the error message if an error occurred
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Clears the data within the Verifier. Error message gets reset and toggle is set to true.
     */
    public void resetErrorMessage() {
        errorMessage = "";
    }

    /**
     * Turns the Verifier on.
     */
    public void turnOn() {
        isOn = true;
    }

    /**
     * Turns the Verifier off.
     */
    public void turnOff() {
        isOn = false;
    }

    /**
     * Determines whether the input is valid with respect to the data on the system. This involves checking
     * that tables/users exist in the system if they are referenced. Making sure that the columns of tables exist
     * and that their data type is correct. Handles other details as well.
     * @param filteredInput is the filtered input
     * @param inputType is the type of input
     * @param tables are the tables in the system
     * @param users are the users in the system
     * @return whether the input is valid with respect to the data on the system
     */
    public boolean isValid(InputType inputType, String[] filteredInput, List<Table> tables, List<User> users) {

        if (! isOn) {
            return true;
        }

        switch (inputType) {
            case QUERY:
                return isValidQuery(filteredInput, tables);
            case CREATE_TABLE:
                return isValidCreateTable(filteredInput, tables);
            case DROP_TABLE:
                return isValidDropTable(filteredInput, tables);
            case ALTER_TABLE:
                return isValidAlterTable(filteredInput, tables);
            case INSERT:
                return isValidInsert(filteredInput, tables);
            case DELETE:
                return isValidDelete(filteredInput, tables);
            case UPDATE:
                return isValidUpdate(filteredInput, tables);
            case GRANT:
                return isValidGrant(filteredInput, tables, users);
            case REVOKE:
                return isValidRevoke(filteredInput, tables, users);
            case BUILD_FILE_STRUCTURE:
                return isValidBuildFileStructure(filteredInput, tables);
            case REMOVE_FILE_STRUCTURE:
                return isValidRemoveFileStructure(filteredInput, tables);
            case UNKNOWN:
            default:
                return false;
        }
    }

    /**
     * Returns whether the data referenced in the QUERY makes sense with respect to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the QUERY is valid
     */
    public boolean isValidQuery(String[] filteredInput, List<Table> tables) {

        RuleGraph queryRuleGraph = RuleGraphTypes.getQueryRuleGraph();
        String queryError = "Verifier error when validating Query:\n";

        // check that tables referenced exist in the system
        List<String> tableNames = queryRuleGraph.getTokensAt(filteredInput, 13, 15, 18);

        for (String tableName : tableNames) {
            boolean tableExists = Utilities.getReferencedTable(tableName, tables) != null;
            if (! tableExists) {
                errorMessage = queryError + "The table: " + tableName + " does not exist";
                return false;
            }
        }

        // check that all columns exist within the tables referenced (all column name nodes except for inner join ones)
        List<String> columnNames = queryRuleGraph.getTokensAt(filteredInput, 2, 10, 20, 23, 35, 45);

        for (Table table : tables) {
            for (String columnName : columnNames) {
                // remove any column names that are prefixed with a table name
                columnName = OptimizerUtilities.removePrefixedColumnName(columnName);
                if (! table.hasColumn(columnName)) {
                    errorMessage = queryError + "\"" + columnName + "\" does not exist in " + table;
                    return false;
                }
            }
        }

        // if joining tables, ensure that the column names exist in the corresponding tables
        boolean isJoiningTables = ! queryRuleGraph.getTokensAt(filteredInput, 16).isEmpty();

        if (isJoiningTables) {
            List<String> joinOnTableNames = queryRuleGraph.getTokensAt(filteredInput, 13, 18);
            List<String> joinOnColumnNames = queryRuleGraph.getTokensAt(filteredInput, 20, 27);
            for (Table table : tables) {

            }
        }

        // check correct data types

        // ensure that the columns used to join on in the using clause appear in the corresponding tables
        // Eg. if have t1 JOIN t2 USING(col1) JOIN t3 USING(col2)
        // t1 and t2 must have "col1" and t2 and t3 must have "col2" in their tables
        List<String> tablesJoined  = queryRuleGraph.getTokensAt(filteredInput, 13, 17);
        List<String> columnsJoined = queryRuleGraph.getTokensAt(filteredInput, 20);
        HashMap<String, ArrayList<String>> columnTablePairs = new HashMap<>();

        for(int i = 0; i < columnsJoined.size(); i++) {

            ArrayList<String> marriedTables = new ArrayList<>();
            marriedTables.add(tablesJoined.get(i));
            marriedTables.add(tablesJoined.get(i + 1));

            columnTablePairs.put(columnsJoined.get(i), marriedTables);
        }

        for(String joinColumn : columnTablePairs.keySet()) {

            boolean foundFirstColumn  = false;
            boolean foundSecondColumn = false;

            for(String joinTable : columnTablePairs.get(joinColumn)) {

                for(Table table : tables) {

                    if(joinTable.equalsIgnoreCase(table.getTableName()) && table.hasColumn(joinColumn)) {

                        if(foundFirstColumn) {
                            foundSecondColumn = true;
                        } else {
                            foundFirstColumn  = true;
                        }
                    }
                }

                if(! (foundFirstColumn || foundSecondColumn)) {
                    return false;
                }
            }
        }

        // only accept numeric columns if using MIN, MAX, AVG, COUNT, SUM
        columnNames = queryRuleGraph.getTokensAt(filteredInput, 10);
        String candidateColumn = columnNames.isEmpty() ? "null" : columnNames.get(0);

        /*for(Table currentTable : tables) {
            for(Column currentColumn : currentTable.getColumns()) {
                String columnName = currentColumn.getName();
                if(candidateColumn.equalsIgnoreCase(columnName) && ! currentColumn.isNumeric()) {
                    return false;
                }
            }
        }*/



        // make sure that column name matches the data type of the constant in where clause
        List<String> whereColumns = queryRuleGraph.getTokensAt(filteredInput, 24);
        List<String> constants = queryRuleGraph.getTokensAt(filteredInput, 31);
        int pairSize = whereColumns.size();

        for(int i = 0; i < pairSize; i++) {

            String column = whereColumns.get(i);
            String constant = constants.get(i);

            for(Table table : tables) {

                if(! table.hasColumn(column)) {
                    continue;
                }

                for(Column column1 : table.getColumns()) {

                    if(column1.getColumnName().equalsIgnoreCase(column)) {

                        boolean isNumericConstant = Utilities.isNumeric(constant);

                        // TODO
                        /*if(column1.isNumeric() && ! isNumericConstant) {
                            return false;
                        }*/
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns whether the data referenced in the CREATE TABLE command makes sense with respect to
     * the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the CREATE TABLE command is valid
     */
    public boolean isValidCreateTable(String[] filteredInput, List<Table> tables) {

        RuleGraph createTableRuleGraph = RuleGraphTypes.getCreateTableRuleGraph();

        // make sure that the table name is not numeric
        String tableName  = filteredInput[2];
        boolean isNumeric = Utilities.isNumeric(tableName);

        if (isNumeric) {
            return false;
        }

        // make sure that none of the column names are numeric
        List<String> columns = createTableRuleGraph.getTokensAt(filteredInput, 4);

        for(String column : columns) {
            isNumeric = Utilities.isNumeric(column);
            if(isNumeric) {
                return false;
            }
        }

        // make sure that the table doesn't already exist
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                foundTable = true;
                break;
            }
        }

        if(foundTable) {
            return false;
        }

        // make sure none of the columns' sizes are greater than 99
        List<String> columnSizes = createTableRuleGraph.getTokensAt(filteredInput, 8);

        for(String columnSize : columnSizes) {
            int size = Integer.parseInt(columnSize);
            if(size > 99) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the data referenced in the DROP TABLE command makes sense with respect
     * to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the DROP TABLE command is valid
     */
    public boolean isValidDropTable(String[] filteredInput, List<Table> tables) {

        // make sure table name exists
        String tableName = filteredInput[2];
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                foundTable = true;
                break;
            }
        }

        return foundTable;
    }

    /**
     * Returns whether the data referenced in the ALTER TABLE command makes sense with respect
     * to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the ALTER TABLE command is valid
     */
    public boolean isValidAlterTable(String[] filteredInput, List<Table> tables) {

        // make sure table name exists
        String tableName = filteredInput[2];
        Table referencedTable = null;
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                foundTable = true;
                break;
            }
        }

        if(! foundTable) {
            return false;
        }

        String type = filteredInput[3];
        String columnName = filteredInput[6];

        // if using drop or modify, ensure columns exist with associated table
        if(type.equalsIgnoreCase("DROP") || type.equalsIgnoreCase("MODIFY")) {
            if(! referencedTable.hasColumn(columnName)) {
                return false;
            }
        }

        // make sure all rows associated with the column to change can actually transition from char to int
        if(type.equalsIgnoreCase("MODIFY")) {
            Column referencedColumn = referencedTable.getColumn(columnName);
            // TODO
            /*if(! referencedColumn.canAlterCharToInt()) {
                return false;
            }*/
        }

        // if using add or modify, ensure size is not greater than 99
        if(type.equalsIgnoreCase("ADD") || type.equalsIgnoreCase("MODIFY")) {
            int size = Integer.parseInt(filteredInput[7]);
            if(size > 99) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the data referenced in the INSERT command makes sense with respect to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the INSERT command is valid
     */
    public boolean isValidInsert(String[] filteredInput, List<Table> tables) {

        RuleGraph insertRuleGraph = RuleGraphTypes.getInsertRuleGraph();

        // make sure table name exists
        String tableName = filteredInput[2];
        Table referencedTable = null;
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                referencedTable = table;
                foundTable = true;
                break;
            }
        }

        if(! foundTable) {
            return false;
        }

        // make sure that the number of values being inserted is not greater than the number of columns in the table
        int numColsInTable = referencedTable.getNumCols();
        int numColsToInsert = insertRuleGraph.getTokensAt(filteredInput, 5).size();

        if(numColsToInsert > numColsInTable) {
            return false;
        }

        // make sure that each new column's value matches the datatype in the corresponding table
        List<String> valuesToInsert = insertRuleGraph.getTokensAt(filteredInput, 5);
        List<Column> columns = referencedTable.getColumns();

        for(int i = 0; i < columns.size(); i++) {

            // TODO
            /*boolean isNumericColumn = columns.get(i).isNumeric();
            boolean isNumericValue  = Parser.isNumeric(valuesToInsert.get(i));

            if(isNumericColumn && ! isNumericValue) {
                return false;
            }*/
        }

        return true;
    }

    /**
     * Returns whether the data referenced in the DELETE command makes sense with respect to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the DELETE is valid
     */
    public boolean isValidDelete(String[] filteredInput, List<Table> tables) {

        RuleGraph deleteRuleGraph = RuleGraphTypes.getDeleteRuleGraph();

        // make sure table name exists
        String tableName = filteredInput[2];
        Table referencedTable = null;
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                foundTable = true;
                referencedTable = table;
                break;
            }
        }

        if(! foundTable) {
            return false;
        }

        // make sure column exists for given table name
        String columnName = filteredInput[4];
        boolean foundColumn = referencedTable.hasColumn(columnName);

        if(! foundColumn) {
            return false;
        }

        // make sure the column referenced matches the datatype of the value supplied
        String value = filteredInput[11];
        // TODO
        /*boolean isNumericColumn = referencedTable.getColumn(columnName).isNumeric();
        boolean isNumericValue  = Parser.isNumeric(value);

        if(isNumericColumn && ! isNumericValue) {
            return false;
        }
*/
        return true;
    }

    /**
     * Returns whether the data referenced in the UPDATE command makes sense with respect to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the UPDATE command is valid
     */
    public boolean isValidUpdate(String[] filteredInput, List<Table> tables) {

        RuleGraph updateRuleGraph = RuleGraphTypes.getUpdateRuleGraph();

        // make sure table name exists
        String tableName = filteredInput[1];
        Table referencedTable = null;
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                referencedTable = table;
                foundTable = true;
                break;
            }
        }

        if(! foundTable) {
            return false;
        }

        // make sure columns referenced actually belong to the table supplied
        String setColumn   = filteredInput[3];
        if(! referencedTable.hasColumn(setColumn)) {
            return false;
        }

        // an UPDATE using the WHERE clause
        if(filteredInput.length > 6) {

            String whereColumn = filteredInput[7];

            if(! referencedTable.hasColumn(whereColumn)) {
                return false;
            }
// TODO
            // make sure the column referenced in WHERE clause has the correct data type
            /*boolean isNumericColumn = referencedTable.getColumn(whereColumn).isNumeric();
            boolean isNumericValue  = Parser.isNumeric(update[9]);

            if(isNumericColumn && !isNumericValue) {
                return false;
            }*/
        }

        return true;
    }

    /**
     * Returns whether the data referenced in the GRANT command makes sense with respect to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the GRANT command is valid
     */
    public boolean isValidGrant(String[] filteredInput, List<Table> tables, List<User> users) {

        RuleGraph grantRuleGraph = RuleGraphTypes.getGrantRuleGraph();

        // make sure table exists
        String tableName = grantRuleGraph.getTokensAt(filteredInput, 20).get(0);
        Table referencedTable = null;
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                referencedTable = table;
                foundTable = true;
            }
        }

        if(! foundTable) {
            return false;
        }

        // make sure that columns exist for corresponding table if using UPDATE or REFERENCES
        List<String> updateAndReferencesCols = grantRuleGraph.getTokensAt(filteredInput, 12, 16);

        for(String candidate : updateAndReferencesCols) {
            boolean hasCandidate = referencedTable.hasColumn(candidate);
            if(! hasCandidate) {
                return false;
            }
        }

        // make sure user(s) exists
        List<String> usersReferenced = grantRuleGraph.getTokensAt(filteredInput, 22);

        for(String candidate : usersReferenced) {

            boolean foundUser = false;

            for(User user : users) {
                String username = user.getUsername();
                if(username.equals(candidate)) {
                    foundUser = true;
                }
            }

            if(! foundUser) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the data referenced in the REVOKE command makes sense with respect to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the REVOKE command is valid
     */
    public boolean isValidRevoke(String[] filteredInput, List<Table> tables, List<User> users) {
        return false;
    }

    /**
     * Returns whether the data referenced in the BUILD FILE STRUCTURE command makes sense with respect
     * to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the BUILD FILE STRUCTURE command is valid
     */
    public boolean isValidBuildFileStructure(String[] filteredInput, List<Table> tables) {

        // make sure table exists
        String tableName = filteredInput[6];
        Table referencedTable = null;
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                referencedTable = table;
                foundTable = true;
            }
        }

        if(! foundTable) {
            return false;
        }

        String column = filteredInput[4];

        if(! referencedTable.hasColumn(column)) {
            return false;
        }

        return true;
    }

    /**
     * Returns whether the data referenced in the REMOVE FILE STRUCTURE command makes sense with respect
     * to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the REMOVE FILE STRUCTURE command is valid
     */
    public boolean isValidRemoveFileStructure(String[] filteredInput, List<Table> tables) {

        return false;
    }
}