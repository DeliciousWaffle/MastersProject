package systemcatalog.components;

import datastructures.rulegraph.RuleGraph;
import datastructures.table.Table;
import datastructures.table.component.Column;
import datastructures.user.User;
import utilities.enums.InputType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Responsible for making sure any parts of the input that make references to the overall
 * schema are logically correct. This can take several forms such as checking whether tables
 * and their associated columns exists or that data types match.
 */
public class Verifier {

    private RuleGraph ruleGraph;

    public Verifier() {}

    public void setRuleGraph(RuleGraph ruleGraph) { this.ruleGraph = ruleGraph; }

    public boolean isValid(InputType inputType, String[] input, ArrayList<User> users, ArrayList<Table> tables) {
        switch(inputType) {
            case QUERY:
                return isValidQuery(input, tables);
            case CREATE_TABLE:
                return isValidCreateTable(input, tables);
            case DROP_TABLE:
                return isValidDropTable(input, tables);
            case ALTER_TABLE:
                return isValidAlterTable(input, tables);
            case INSERT:
                return isValidInsert(input, tables);
            case DELETE:
                return isValidDelete(input, tables);
            case UPDATE:
                return isValidUpdate(input, tables);
            // note the fall through, GRANT and REVOKE are very similar commands
            case GRANT:
            case REVOKE:
                return isValidPrivilegeCommand(input, users, tables);
            // same with a BUILD with secondary b-trees, clustered b-trees, and hash tables
            case BUILD_SECONDARY_B_TREE:
            case BUILD_CLUSTERED_B_TREE:
            case BUILD_HASH_TABLE:
                return isValidBuildFileStructure(input, tables);
            case BUILD_CLUSTERED_FILE:
                return isValidClusteredFile(input, tables);
            case UNKNOWN:
            default:
                return false;
        }
    }

    public boolean isValidQuery(String[] query, ArrayList<Table> tables) {

        // make sure all tables exist
        ArrayList<String> candidateTables = ruleGraph.getTokensAt(query, 13, 15, 17);

        for(Table currentTable : tables) {

            String tableName = currentTable.getTableName();
            boolean foundTable = false;

            for(String candidateTable : candidateTables) {
                if(candidateTable.equalsIgnoreCase(tableName)) {
                    foundTable = true;
                }
            }

            if(! foundTable) {
                return false;
            }
        }

        // make sure all columns exist in the tables supplied
        ArrayList<String> candidateColumns = ruleGraph.getTokensAt(query, 2, 10, 20, 24);

        for(Table currentTable : tables) {
            for(String candidateColumn : candidateColumns) {
                if(! currentTable.hasColumn(candidateColumn)) {
                    return false;
                }
            }
        }

        // only accept numeric columns if using MIN, MAX, AVG, COUNT, SUM
        candidateColumns = ruleGraph.getTokensAt(query, 10);
        String candidateColumn = candidateColumns.isEmpty() ? "null" : candidateColumns.get(0);

        for(Table currentTable : tables) {
            for(Column currentColumn : currentTable.getColumns()) {
                String columnName = currentColumn.getName();
                if(candidateColumn.equalsIgnoreCase(columnName) && ! currentColumn.isNumeric()) {
                    return false;
                }
            }
        }

        // TODO not sure if this 100% correct
        // ensure that the columns used to join on in the using clause appear in the corresponding tables
        // Eg. if have t1 JOIN t2 USING(col1) JOIN t3 USING(col2)
        // t1 and t2 must have "col1" and t2 and t3 must have "col2" in their tables
        ArrayList<String> tablesJoined  = ruleGraph.getTokensAt(query, 13, 17);
        ArrayList<String> columnsJoined = ruleGraph.getTokensAt(query, 20);
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

        // make sure that column name matches the data type of the constant in where clause
        ArrayList<String> whereColumns = ruleGraph.getTokensAt(query, 24);
        ArrayList<String> constants = ruleGraph.getTokensAt(query, 31);
        int pairSize = whereColumns.size();

        for(int i = 0; i < pairSize; i++) {

            String column = whereColumns.get(i);
            String constant = constants.get(i);

            for(Table table : tables) {

                if(! table.hasColumn(column)) {
                    continue;
                }

                for(Column column1 : table.getColumns()) {

                    if(column1.getName().equalsIgnoreCase(column)) {

                        boolean isNumericConstant = Parser.isNumeric(constant);

                        if(column1.isNumeric() && ! isNumericConstant) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean isValidCreateTable(String[] createTable, ArrayList<Table> tables) {

        // make sure that the table name is not numeric
        String tableName  = createTable[2];
        boolean isNumeric = Parser.isNumeric(tableName);

        if(isNumeric) {
            return false;
        }

        // make sure that none of the column names are numeric
        ArrayList<String> columns = ruleGraph.getTokensAt(createTable, 4);

        for(String column : columns) {
            isNumeric = Parser.isNumeric(column);
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
        ArrayList<String> columnSizes = ruleGraph.getTokensAt(createTable, 8);

        for(String columnSize : columnSizes) {
            int size = Integer.parseInt(columnSize);
            if(size > 99) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidDropTable(String[] dropTable, ArrayList<Table> tables) {

        // make sure table name exists
        String tableName = dropTable[2];
        boolean foundTable = false;

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                foundTable = true;
                break;
            }
        }

        return foundTable;
    }

    public boolean isValidAlterTable(String[] alterTable, ArrayList<Table> tables) {

        // make sure table name exists
        String tableName = alterTable[2];
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

        String type = alterTable[3];
        String columnName = alterTable[6];

        // if using drop or modify, ensure columns exist with associated table
        if(type.equalsIgnoreCase("DROP") || type.equalsIgnoreCase("MODIFY")) {
            if(! referencedTable.hasColumn(columnName)) {
                return false;
            }
        }

        // make sure all rows associated with the column to change can actually transition from char to int
        if(type.equalsIgnoreCase("MODIFY")) {
            Column referencedColumn = referencedTable.getColumn(columnName);
            if(! referencedColumn.canAlterCharToInt()) {
                return false;
            }
        }

        // if using add or modify, ensure size is not greater than 99
        if(type.equalsIgnoreCase("ADD") || type.equalsIgnoreCase("MODIFY")) {
            int size = Integer.parseInt(alterTable[7]);
            if(size > 99) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidInsert(String[] insert, ArrayList<Table> tables) {

        // make sure table name exists
        String tableName = insert[2];
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
        int numColsInTable  = referencedTable.getNumCols();
        int numColsToInsert = ruleGraph.getTokensAt(insert, 5).size();

        if(numColsToInsert > numColsInTable) {
            return false;
        }

        // make sure that each new column's value matches the datatype in the corresponding table
        ArrayList<String> valuesToInsert = ruleGraph.getTokensAt(insert, 5);
        ArrayList<Column> columns = referencedTable.getColumns();

        for(int i = 0; i < columns.size(); i++) {

            boolean isNumericColumn = columns.get(i).isNumeric();
            boolean isNumericValue  = Parser.isNumeric(valuesToInsert.get(i));

            if(isNumericColumn && ! isNumericValue) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidDelete(String[] delete, ArrayList<Table> tables) {

        // make sure table name exists
        String tableName = delete[2];
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
        String columnName   = delete[4];
        boolean foundColumn = referencedTable.hasColumn(columnName);

        if(! foundColumn) {
            return false;
        }

        // make sure the column referenced matches the datatype of the value supplied
        String value = delete[11];
        boolean isNumericColumn = referencedTable.getColumn(columnName).isNumeric();
        boolean isNumericValue  = Parser.isNumeric(value);

        if(isNumericColumn && ! isNumericValue) {
            return false;
        }

        return true;
    }

    public boolean isValidUpdate(String[] update, ArrayList<Table> tables) {

        // make sure table name exists
        String tableName = update[1];
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
        String setColumn   = update[3];
        if(! referencedTable.hasColumn(setColumn)) {
            return false;
        }

        // an UPDATE using the WHERE clause
        if(update.length > 6) {

            String whereColumn = update[7];

            if(! referencedTable.hasColumn(whereColumn)) {
                return false;
            }

            // make sure the column referenced in WHERE clause has the correct data type
            boolean isNumericColumn = referencedTable.getColumn(whereColumn).isNumeric();
            boolean isNumericValue  = Parser.isNumeric(update[9]);

            if(isNumericColumn && !isNumericValue) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param privilege - a tokenized privilege statement
     * @param users - all users of the system
     * @param tables - all tables within the system
     * @return whether a GRANT or REVOKE command is valid
     */
    public boolean isValidPrivilegeCommand(String[] privilege, ArrayList<User> users, ArrayList<Table> tables) {

        // make sure table exists
        String tableName = ruleGraph.getTokensAt(privilege, 20).get(0);
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
        ArrayList<String> updateAndReferencesCols = ruleGraph.getTokensAt(privilege, 12, 16);

        for(String candidate : updateAndReferencesCols) {
            boolean hasCandidate = referencedTable.hasColumn(candidate);
            if(! hasCandidate) {
                return false;
            }
        }

        // make sure user(s) exists
        ArrayList<String> usersReferenced = ruleGraph.getTokensAt(privilege, 22);

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

    public boolean isValidBuildFileStructure(String[] fileStructure, ArrayList<Table> tables) {

        // make sure table exists
        String tableName = fileStructure[6];
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

        String column = fileStructure[4];

        if(! referencedTable.hasColumn(column)) {
            return false;
        }

        return true;
    }

    public boolean isValidClusteredFile(String[] clusteredFile, ArrayList<Table> tables) {

        String table1 = clusteredFile[4];
        String table2 = clusteredFile[6];
        boolean foundTable1 = false;
        boolean foundTable2 = false;

        for(Table table : tables) {
            String tableName = table.getTableName();
            if(tableName.equalsIgnoreCase(table1)) {
                foundTable1 = true;
            }
            if(tableName.equalsIgnoreCase(table2)) {
                foundTable2 = true;
            }
        }

        return foundTable1 && foundTable2;
    }
}
