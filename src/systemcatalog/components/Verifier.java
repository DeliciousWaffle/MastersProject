package systemcatalog.components;

import datastructures.relation.table.component.DataType;
import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.user.User;
import enums.Keyword;
import enums.Symbol;
import utilities.OptimizerUtilities;
import utilities.Utilities;
import enums.InputType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Responsible for making sure any parts of the input that make references to something within the system
 * are correct. This can take several forms such as checking whether tables and their associated columns
 * exist or that data types match. Also has an option for turning it on or off. When off, the user can input
 * any data without the worry about whether their input makes sense with respect to the system data. When on,
 * the Verifier will check the system tables and users to make sure the input makes sense. Also, input is
 * assumed to have passed through the Parser and is syntactically correct.
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
     * @param systemTables are the systemTables of the system
     * @return whether the QUERY is valid
     */
    public boolean isValidQuery(String[] filteredInput, List<Table> systemTables) {

        RuleGraph queryRuleGraph = RuleGraphTypes.getQueryRuleGraph();
        String queryError = "Verifier error when validating Query:\n";

        // check that systemTables referenced exist in the system
        List<String> tableNames = queryRuleGraph.getTokensAt(filteredInput, 13, 15, 18);

        for (String tableName : tableNames) {

            boolean tableExists = Utilities.getReferencedTable(tableName, systemTables) != null;

            if (! tableExists) {
                errorMessage = queryError + "Table \"" + tableName + "\" does not exist";
                return false;
            }
        }


        // check that all columns exist within the systemTables referenced
        List<Table> referencedTables = Utilities.getReferencedTables(tableNames, systemTables);
        List<String> columnNames = queryRuleGraph.getTokensAt(filteredInput, 2, 9, 20, 27, 29, 43, 52);
        OptimizerUtilities.getColumnNamesFromStar(columnNames, referencedTables);

        for (String columnName : columnNames) {

            columnName = OptimizerUtilities.removePrefixedColumnName(columnName); // remove any prefixing
            boolean foundTable = false;

            for (Table table : referencedTables) {
                if (table.hasColumn(columnName)) {
                    foundTable = true;
                    break;
                }
            }

            if (! foundTable) {
                errorMessage = queryError + "The column \"" + columnName + "\" does not exist";
                return false;
            }
        }


        // check that prefixed columns exist
        for (String columnName : columnNames) {

            boolean isPrefixed = OptimizerUtilities.hasPrefixedTableName(columnName);

            if (isPrefixed) {

                String tableName = columnName.split("\\.")[0];

                // check if the table exists
                Table table = Utilities.getReferencedTable(tableName, systemTables);
                boolean tableExists = table != null;

                if (! tableExists) {
                    errorMessage = queryError + "The column \"" + columnName + "\" does not exist";
                    return false;
                }
            }
        }


        // check for ambiguous column names which are those that appear in one or more tables
        for (String columnName : columnNames) {

            boolean isAmbiguousColumn = Utilities.isAmbiguousColumn(columnName, referencedTables);

            if (isAmbiguousColumn) {
                errorMessage = queryError + "The column \"" + columnName + "\" is ambiguous\n" +
                "The column should be prefixed with the table name like this \"<TableName>.<ColumnName>\"";
                return false;
            }
        }


        // if using an aggregate function (except COUNT) make sure the column is numeric or a date value
        List<String> aggregatedColumnNames = queryRuleGraph.getTokensAt(filteredInput, 9, 52);
        List<String> aggregationTypes =
                queryRuleGraph.getTokensAt(filteredInput, 3, 4, 5, 6, 7, 46, 47, 48, 49, 50);

        // removing columns that are mapped with COUNT
        boolean doneRemovingColumns = false;

        while (! doneRemovingColumns) {

            boolean madeChanges = false;

            for (int i = 0; i < aggregatedColumnNames.size(); i++) {

                Keyword aggregationType = Keyword.toKeyword(aggregationTypes.get(i));

                if (aggregationType == Keyword.COUNT) { // remove the associate column and type
                    aggregatedColumnNames.remove(i);
                    aggregationTypes.remove(i);
                    madeChanges = true;
                    break;
                }
            }

            if (! madeChanges) {
                doneRemovingColumns = true;
            }
        }

        List<Column> referencedColumns = Utilities.getReferencedColumns(aggregatedColumnNames, referencedTables);

        for (int i = 0; i < referencedColumns.size(); i++) {
            if (referencedColumns.get(i).getDataType() == DataType.CHAR) {
                errorMessage = queryError + "The function \"" + aggregationTypes.get(i) + "\" can only be used with " +
                        "numeric values\nor dates, but the column \"" + referencedColumns.get(i).getColumnName() +
                        "\" is of type \"CHAR\"";
                return false;
            }
        }


        // make sure the columns being joined are of the correct datatype
        List<String> joinColumns = queryRuleGraph.getTokensAt(filteredInput, 20, 27);
        referencedColumns = Utilities.getReferencedColumns(joinColumns, referencedTables);
        List<String> joinSymbols = queryRuleGraph.getTokensAt(filteredInput, 21, 22, 23, 24, 25, 26);

        for (int i = 0, j = 0, k = 1; i < referencedColumns.size(); i += 2, j++, k += 2) {

            Column firstColumn = referencedColumns.get(i);
            String joinSymbol = joinSymbols.get(j);
            Column secondColumn = referencedColumns.get(k);

            boolean hasMatchingDataTypes = firstColumn.getDataType() == secondColumn.getDataType();

            if (! hasMatchingDataTypes) {
                errorMessage = queryError + "The column \"" + firstColumn.getColumnName() + "\" has a datatype of \"" +
                        firstColumn.getDataType() + "\" which does not\n" + "match the column \"" +
                        secondColumn.getColumnName() + "\" which has a datatype of \"" +
                        secondColumn.getDataType() + "\"";
                return false;

            } else { // data types match

                // if >, <, >=, <= are used, make sure the data types are numeric or dates
                boolean isRangeSymbol = Symbol.isRangeSymbol(joinSymbol);
                boolean isChar = firstColumn.getDataType() == DataType.CHAR;

                if (isRangeSymbol && isChar) {
                    errorMessage = queryError + "Both \"" + firstColumn.getColumnName() + "\" and \"" +
                            secondColumn.getColumnName() + "\" have a datatype of type CHAR which can't be used with \"" +
                            joinSymbol + "\"";
                    return false;
                }
            }
        }

        // TODO figure out having clause fiasco

        // make sure data types are valid in where and having clauses
        columnNames = queryRuleGraph.getTokensAt(filteredInput, 29, 52);
        referencedColumns = Utilities.getReferencedColumns(columnNames, referencedTables);
        List<String> symbols = queryRuleGraph.getTokensAt(filteredInput, 30, 31, 32, 33, 34, 35, 54, 55, 56, 57, 58, 59);
        List<String> values = queryRuleGraph.getTokensAt(filteredInput, 36, 38, 60, 62);

        for (int i = 0; i < referencedColumns.size(); i++) {

            // make sure that the format of a date is correct first
            if (Utilities.hasDateFormat(values.get(i)) && ! Utilities.isValidDate(values.get(i))) {
                errorMessage = queryError + "\"" + values.get(i) + "\" is an invalid date";
                return false;
            }

            DataType columnDataType = referencedColumns.get(i).getDataType();
            DataType valueDataType = Utilities.getDataType(values.get(i));

            if (columnDataType != valueDataType) {
                errorMessage = queryError + "Column \"" + referencedColumns.get(i).getColumnName() +
                        "\" has a datatype of \"" + columnDataType + "\" which\ndoesn't match \"" + values.get(i) +
                        "\" which has a data type of \"" + valueDataType + "\"";
                return false;
            }

            // if using >, <, >=, <= make sure value is not of type CHAR
            boolean isRangeSymbol = Symbol.isRangeSymbol(symbols.get(i));
            boolean isChar = valueDataType == DataType.CHAR;

            if (isRangeSymbol && isChar) {
                errorMessage = queryError + "Can't use \"" + symbols.get(i) + "\" with CHAR values";
                return false;
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

        String createTableError = "Verifier error when validating Create Table statement:\n";
        RuleGraph createTableRuleGraph = RuleGraphTypes.getCreateTableRuleGraph();

        // make sure that the table name is not numeric
        String tableName = filteredInput[2];
        boolean isNumeric = Utilities.isNumeric(tableName);

        if (isNumeric) {
            errorMessage = createTableError + "Table name can't be completely numeric";
            return false;
        }


        // make sure that none of the column names are numeric
        List<String> columns = createTableRuleGraph.getTokensAt(filteredInput, 4);

        for (String column : columns) {
            isNumeric = Utilities.isNumeric(column);
            if (isNumeric) {
                errorMessage = createTableError + "Column names can't be completely numeric";
                return false;
            }
        }


        // make sure that the table doesn't already exist
        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {
                errorMessage = createTableError + "Table \"" + table + "\" already exists";
                return false;
            }
        }


        // make sure size is not a decimal, a negative value, and zero
        List<String> sizes = createTableRuleGraph.getTokensAt(filteredInput, 9);

        for (String size : sizes) {
            boolean isDecimal = size.contains(".");
            if (isDecimal) {
                errorMessage = createTableError + "Size can't be a decimal";
                return false;
            }
            boolean isNegative = Integer.parseInt(size) < 0;
            if (isNegative) {
                errorMessage = createTableError + "Size cannot be negative";
                return false;
            }
            boolean isZero = size.equalsIgnoreCase("0");
            if (isZero) {
                errorMessage = createTableError + "Size cannot be 0";
                return false;
            }
        }


        // make sure decimal size is not a decimal and a negative value
        List<String> decimalSizes = createTableRuleGraph.getTokensAt(filteredInput, 11);

        for (String decimalSize : decimalSizes) {
            boolean isDecimal = decimalSize.contains(".");
            if (isDecimal) {
                errorMessage = createTableError + "Decimal size can't be a decimal";
                return false;
            }
            boolean isNegative = Integer.parseInt(decimalSize) < 0;
            if (isNegative) {
                errorMessage = createTableError + "Decimal size can't be negative";
                return false;
            }
        }


        // char can't have decimal size > 0
        // also can't use the rule graph to get the correct mapping of values, so things get messy
        for (int i = 0; i < filteredInput.length - 4; i++) {

            boolean foundChar = filteredInput[i].equalsIgnoreCase("CHAR");
            boolean foundParenthesis = filteredInput[i + 1].equalsIgnoreCase("(");
            boolean foundSize = Utilities.isNumeric(filteredInput[i + 2]);
            boolean foundDecimalSize = Utilities.isNumeric(filteredInput[i + 4]);

            boolean charHasDecimalSize = foundChar && foundParenthesis && foundSize && foundDecimalSize;

            if (charHasDecimalSize) {
                boolean hasInvalidChar = Integer.parseInt(filteredInput[i + 4]) > 0;
                if (hasInvalidChar) {
                    errorMessage = createTableError + "Can't have a datatype of \"CHAR\" and a decimal size > 0";
                    return false;
                }
            }
        }


        // can't have duplicate columns
        for (int i = 0; i < columns.size(); i++) {
            for (int j = i + 1; j < columns.size(); j++) {
                if (columns.get(i).equalsIgnoreCase(columns.get(j))) {
                    errorMessage = createTableError + "Table contains duplicate column \"" + columns.get(i) + "\"";
                    return false;
                }
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

        // make sure table exists
        String tableName = filteredInput[2];

        for (Table table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {
                return true;
            }
        }

        errorMessage = "Verifier error when validating Invalid Drop Table statement:\n" +
                "Table \"" + tableName + "\" does not exist";

        return false;
    }

    /**
     * Returns whether the data referenced in the ALTER TABLE command makes sense with respect
     * to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the ALTER TABLE command is valid
     */
    public boolean isValidAlterTable(String[] filteredInput, List<Table> tables) {

        String alterTableError = "Verifier error when validating Alter Table statement:\n";
        RuleGraph alterTableRuleGraph = RuleGraphTypes.getAlterTableRuleGraph();

        // make sure the table exists
        String tableName = alterTableRuleGraph.getTokensAt(filteredInput, 2).get(0);
        Table referencedTable = Utilities.getReferencedTable(tableName, tables);
        boolean hasTable = referencedTable != null;

        if (! hasTable) {
            errorMessage = alterTableError + "Table \"" + tableName + "\" does not exist";
            return false;
        }


        // make sure column exists
        String columnName = alterTableRuleGraph.getTokensAt(filteredInput, 6, 16).get(0);

        boolean hasModify = ! alterTableRuleGraph.getTokensAt(filteredInput, 3).isEmpty();
        boolean hasAdd = ! alterTableRuleGraph.getTokensAt(filteredInput, 4).isEmpty();
        boolean hasDrop = ! alterTableRuleGraph.getTokensAt(filteredInput, 5).isEmpty();
        boolean hasForeignKey = ! alterTableRuleGraph.getTokensAt(filteredInput, 13).isEmpty();
        boolean hasPrimaryKey = ! alterTableRuleGraph.getTokensAt(filteredInput, 14).isEmpty();

        if (! ((hasAdd && ! hasForeignKey && ! hasPrimaryKey) || (hasAdd && hasForeignKey) ||
                (hasDrop && hasForeignKey))) {
            boolean hasColumn = referencedTable.hasColumn(columnName);
            if (! hasColumn) {
                errorMessage = alterTableError + "Column \"" + columnName + "\" does not exist";
                return false;
            }
        }


        // make sure foreign key exists
        if (hasForeignKey) {

            boolean isPrefixed = OptimizerUtilities.hasPrefixedTableName(columnName);

            if (! isPrefixed) {
                errorMessage = "Column needs to be prefixed with table name like this <table name>.<column name>";
                return false;

            } else {

                String prefixedTableName = columnName.split("\\.")[0];
                String prefixedColumnName = columnName.split("\\.")[1];
                Table prefixedReferenceTable = Utilities.getReferencedTable(prefixedTableName, tables);
                hasTable = prefixedReferenceTable != null;

                if (! hasTable) {
                    errorMessage = alterTableError + "Table \"" + prefixedTableName + "\" does not exist";
                    return false;
                }

                boolean hasColumn = prefixedReferenceTable.hasColumn(prefixedColumnName);
                if (! hasColumn) {
                    errorMessage = alterTableError + "Prefixed column \"" + prefixedColumnName + "\" does not exist";
                    return false;
                }
            }
        }


        // check for negative size, decimal size, and size of 0
        boolean hasSize = ! alterTableRuleGraph.getTokensAt(filteredInput, 11).isEmpty();

        if (hasSize) {

            String size = alterTableRuleGraph.getTokensAt(filteredInput, 11).get(0);

            boolean isDecimal = size.contains(".");
            if (isDecimal) {
                errorMessage = alterTableError + "Size can't be a decimal";
                return false;
            }

            boolean isNegative = Integer.parseInt(size) < 0;
            if (isNegative) {
                errorMessage = alterTableError + "Size cannot be negative";
                return false;
            }

            boolean isZero = size.equalsIgnoreCase("0");
            if (isZero) {
                errorMessage = alterTableError + "Size cannot be 0";
                return false;
            }
        }


        // make sure the conversion will be valid
        if (hasModify) {

            Column referencedColumn = referencedTable.getColumn(columnName);
            DataType columnDataType = referencedColumn.getDataType();
            DataType toConvertDataType = DataType.convertToDataType(
                    alterTableRuleGraph.getTokensAt(filteredInput, 7, 8, 9).get(0));

            // don't worry about size changes if the data types are the same
            if (columnDataType != toConvertDataType) {

                if (columnDataType == DataType.NUMBER) {
                    // number to date (can't do)
                    if (toConvertDataType == DataType.DATE) {
                        errorMessage = alterTableError + "Can't convert a column of type \"NUMBER\" to type \"DATE\"";
                        return false;
                    }
                    // number to char (auto succeed - don't check)

                } else if (columnDataType == DataType.CHAR) {
                    // char to number (all chars must be successfully converted to numbers)
                    if (toConvertDataType == DataType.NUMBER) {
                        List<String> rowsOfColumn = Utilities.getRowsAtColumn(referencedColumn, referencedTable);
                        for (String row : rowsOfColumn) {
                            boolean canBeConvertedToNumeric = Utilities.isNumeric(row);
                            if (! canBeConvertedToNumeric) {
                                errorMessage = alterTableError + "Unsuccessful conversion of type \"CHAR\" to " +
                                        "\"NUMBER\", row \"" + row + "\" couldn't be converted successfully";
                                return false;
                            }
                        }
                    }
                    // char to date (must have a date format and be a valid date)
                    if (toConvertDataType == DataType.DATE) {
                        List<String> rowsOfColumn = Utilities.getRowsAtColumn(referencedColumn, referencedTable);
                        for (String row : rowsOfColumn) {
                            boolean isValidDate = Utilities.isValidDate(row);
                            if (! isValidDate) {
                                errorMessage = alterTableError + "Unsuccessful conversion of type \"CHAR\" to " +
                                        "\"DATE\", row \"" + row + "\" couldn't be converted successfully";
                                return false;
                            }
                        }
                    }

                // convert to date
                } else if (columnDataType == DataType.DATE) {
                    // date to number (can't do)
                    if (toConvertDataType == DataType.NUMBER) {
                        errorMessage = alterTableError + "Can't convert a column of type \"DATE\" to type \"NUMBER\"";
                        return false;
                    }
                    // date to char (auto succeed - don't check)
                }
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

        String insertError = "Verifier error when validating Insert statement:\n";
        RuleGraph insertRuleGraph = RuleGraphTypes.getInsertRuleGraph();

        // make sure table name exists
        String tableName = filteredInput[2];
        Table referencedTable = Utilities.getReferencedTable(tableName, tables);
        boolean tableExists = referencedTable != null;

        if (! tableExists) {
            errorMessage = insertError + "Table \"" + tableName + "\" does not exist";
            return false;
        }


        // make sure that the number of values being inserted is not greater than the number of columns in the table
        int numColsInTable = referencedTable.getNumCols();
        int numColsToInsert = insertRuleGraph.getTokensAt(filteredInput, 5, 7).size();

        if (numColsToInsert > numColsInTable) {
            errorMessage = insertError + "Number of columns inserted \"" + numColsToInsert + "\" is greater than\n" +
                    "the number of columns in the table \"" + numColsInTable;
            return false;
        }


        // make sure the data types of the values being inserted match those of the table
        List<String> values = insertRuleGraph.getTokensAt(filteredInput, 5, 7);
        List<Column> referencedColumns = referencedTable.getColumns();

        for (int i = 0; i < values.size(); i++) {
            DataType valueDataType = Utilities.getDataType(values.get(i));
            DataType columnDataType = referencedColumns.get(i).getDataType();
            if (valueDataType != columnDataType) {
                errorMessage = insertError + "The value being inserted has a datatype of \"" + valueDataType +
                        "\" which does not\nmatch column \"" + referencedColumns.get(i).getColumnName() +
                        "\" which has a datatype of \"" + columnDataType + "\"";
                return false;
            }
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

        String insertError = "Verifier error when validating Delete statement:\n";
        RuleGraph deleteRuleGraph = RuleGraphTypes.getDeleteRuleGraph();

        // make sure table name exists
        String tableName = filteredInput[2];
        Table referencedTable = Utilities.getReferencedTable(tableName, tables);
        boolean foundTable = referencedTable != null;

        if (! foundTable) {
            errorMessage = insertError + "Table \"" + tableName + "\" does not exist";
            return false;
        }


        // make sure column exists for given table name
        String columnName = filteredInput[4];
        boolean foundColumn = referencedTable.hasColumn(columnName);

        if (! foundColumn) {
            errorMessage = insertError + "Column \"" + columnName + "\" does not exist";
            return false;
        }

        // make sure the data type of the value matches that of the column
        String value = deleteRuleGraph.getTokensAt(filteredInput, 11, 13).get(0);
        DataType valueDataType = Utilities.getDataType(value);
        DataType columnDataType = referencedTable.getColumn(columnName).getDataType();
        if (valueDataType != columnDataType) {
            errorMessage = insertError + "The value being inserted has a datatype of \"" + valueDataType +
                    "\" which does not\nmatch column \"" + columnName +
                    "\" which has a datatype of \"" + columnDataType + "\"";
            return false;
        }

        return true;
    }

    /**
     * Returns whether the data referenced in the UPDATE command makes sense with respect to the data on the system.
     * @param filteredInput is the filtered input
     * @param tables are the tables of the system
     * @return whether the UPDATE command is valid
     */
    public boolean isValidUpdate(String[] filteredInput, List<Table> tables) {

        String insertError = "Verifier error when validating Update statement:\n";
        RuleGraph updateRuleGraph = RuleGraphTypes.getUpdateRuleGraph();

        // make sure table name exists
        String tableName = filteredInput[1];
        Table referencedTable = Utilities.getReferencedTable(tableName, tables);
        boolean foundTable = referencedTable != null;

        if (! foundTable) {
            errorMessage = insertError + "Table \"" + tableName + "\" does not exist";
            return false;
        }


        // make sure the columns exist
        String firstColumnName = filteredInput[3];
        String secondColumnName = filteredInput[10];

        if (! referencedTable.hasColumn(firstColumnName)) {
            errorMessage = insertError + "Column \"" + firstColumnName + "\" does not exist";
            return false;
        }

        if (! referencedTable.hasColumn(secondColumnName)) {
            errorMessage = insertError + "Column \"" + secondColumnName + "\" does not exist";
            return false;
        }

        // make sure the data types of the values match those of the columns
        DataType firstColumnDataType = referencedTable.getColumn(firstColumnName).getDataType();
        DataType secondColumnDataType = referencedTable.getColumn(secondColumnName).getDataType();

        DataType firstValueDataType = Utilities.getDataType(
                updateRuleGraph.getTokensAt(filteredInput, 5, 7).get(0));
        DataType secondValueDataType = Utilities.getDataType(
                updateRuleGraph.getTokensAt(filteredInput, 12, 14).get(0));

        if (firstColumnDataType != firstValueDataType) {
            errorMessage = insertError + "The value being updated has a datatype of \"" + firstValueDataType +
                    "\" which does not\nmatch column \"" + firstColumnName +
                    "\" which has a datatype of \"" + firstColumnDataType + "\"";
            return false;
        }

        if (secondColumnDataType != secondValueDataType) {
            errorMessage = insertError + "The value being updated has a datatype of \"" + secondValueDataType +
                    "\" which does not\nmatch column \"" + secondColumnName +
                    "\" which has a datatype of \"" + secondColumnDataType + "\"";
            return false;
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