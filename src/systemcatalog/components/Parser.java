package systemcatalog.components;

import datastructures.misc.Pair;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import enums.InputType;
import utilities.Utilities;

/**
 * Responsible for checking the syntax of the input to ensure it is syntactically correct along with some other
 * basic error checking. Note that methods of the RuleGraph class do most of the heavy lifting when it comes to
 * error checking. Refer to the diagrams in "files/assets/helpscreen/diagrams" for information about how the
 * input is processed. Data is assumed to have already been passed through the Utilities.filterInput() method.
 */
public class Parser {

    private String errorMessage;

    public Parser() {
        errorMessage = "";
    }

    /**
     * @return the error message if an error occurred while parsing the input
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Clears information present in the Parser, in this case, just the error message.
     */
    public void resetErrorMessage() {
        errorMessage = "";
    }

    /**
     * Returns whether the syntax of the input is syntactically correct along with some other basic error checking.
     * @param inputType is the type of input
     * @param filteredInput is input that has already been filtered for use
     * @return whether the input is syntactically correct
     */
    public boolean isValid(InputType inputType, String[] filteredInput) {
        switch (inputType) {
            case QUERY:
                return isValidQuery(filteredInput);
            case CREATE_TABLE:
                return isValidCreateTable(filteredInput);
            case DROP_TABLE:
                return isValidDropTable(filteredInput);
            case ALTER_TABLE:
                return isValidAlterTable(filteredInput);
            case INSERT:
                return isValidInsert(filteredInput);
            case DELETE:
                return isValidDelete(filteredInput);
            case UPDATE:
                return isValidUpdate(filteredInput);
            case GRANT:
                return isValidGrant(filteredInput);
            case REVOKE:
                return isValidRevoke(filteredInput);
            case BUILD_FILE_STRUCTURE:
                return isValidBuildFileStructure(filteredInput);
            case REMOVE_FILE_STRUCTURE:
                return isValidRemoveFileStructure(filteredInput);
            case UNKNOWN:
            default:
                return false;
        }
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid QUERY
     */
    private boolean isValidQuery(String[] filteredInput) {

        RuleGraph queryRuleGraph = RuleGraphTypes.getQueryRuleGraph();

        boolean isValid = queryRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! queryRuleGraph.hasIllegalReservedWord(filteredInput, 2, 9, 13, 15, 18, 20, 27, 29, 36, 38, 43, 52, 60, 62) &&
                // ensure that column names, table names, and other values are non numeric
                queryRuleGraph.hasNonNumericAt(filteredInput, 2, 9, 13, 15, 18, 20, 27, 29, 38, 43, 52, 62) &&
                // ensure that certain values are numeric
                queryRuleGraph.hasNumericAt(filteredInput, 36, 60) &&
                // column names referenced in SELECT clause must be unique
                ! queryRuleGraph.hasDuplicatesAt(filteredInput, 2, 9) &&
                // table names referenced in FROM clause must be unique
                ! queryRuleGraph.hasDuplicatesAt(filteredInput, 13, 15, 18) &&
                // column names referenced in group by clause must be unique
                ! queryRuleGraph.hasDuplicatesAt(filteredInput, 43) &&
                // >, <, >=, and <= can only be used with a date value for WHERE and HAVING clauses
                ! queryRuleGraph.hasIllegalDateAt(filteredInput, new int[] {30, 31, 32, 33, 34, 35}, new int[] {36, 38}) &&
                ! queryRuleGraph.hasIllegalDateAt(filteredInput, new int[] {54, 55, 56, 57, 58, 59}, new int[] {60, 62});

        // make sure the group by clause is valid
        Pair<Boolean, String> groupByClauseInfo = Utilities.hasInvalidGroupByClause(filteredInput);
        boolean hasInvalidGroupByClause = groupByClauseInfo.getFirst();

        if (hasInvalidGroupByClause) {
            String groupByErrorMessage = groupByClauseInfo.getSecond();
            errorMessage = "Parser error when validating Query:\n" + groupByErrorMessage;
            isValid = false;
        }

        errorMessage = isValid ? "" : "Parser error when validating Query:\n" + queryRuleGraph.getErrorMessage();
        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid CREATE TABLE command
     */
    private boolean isValidCreateTable(String[] filteredInput) {

        RuleGraph createTableRuleGraph = RuleGraphTypes.getCreateTableRuleGraph();

        boolean isValid = createTableRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! createTableRuleGraph.hasIllegalReservedWord(filteredInput, 2, 4, 9, 11) &&
                // want non numeric values for table name and column names
                createTableRuleGraph.hasNonNumericAt(filteredInput, 2, 4) &&
                // want numeric values for the sizes
                createTableRuleGraph.hasNumericAt(filteredInput, 9, 11) &&
                // can't have decimal values
                createTableRuleGraph.hasIntegerAt(filteredInput, 9, 11) &&
                // can't have negative values
                createTableRuleGraph.hasPositiveNumberAt(filteredInput, 9, 11) &&
                // can't have duplicate column names
                ! createTableRuleGraph.hasDuplicatesAt(filteredInput, 4);

        errorMessage = isValid ? "" : "Parser error when validating Create Table command:\n" +
                createTableRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid DROP TABLE command
     */
    private boolean isValidDropTable(String[] filteredInput) {

        RuleGraph dropTableRuleGraph = RuleGraphTypes.getDropTableRuleGraph();

        boolean isValid = dropTableRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! dropTableRuleGraph.hasIllegalReservedWord(filteredInput, 2) &&
                dropTableRuleGraph.hasNonNumericAt(filteredInput, 2);

        errorMessage = isValid ? "" : "Parser error when validating Drop Table command:\n" +
                dropTableRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid ALTER TABLE command
     */
    private boolean isValidAlterTable(String[] filteredInput) {

        RuleGraph alterTableRuleGraph = RuleGraphTypes.getAlterTableRuleGraph();

        boolean isValid = alterTableRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! alterTableRuleGraph.hasIllegalReservedWord(filteredInput, 2, 6, 11, 16) &&
                alterTableRuleGraph.hasNonNumericAt(filteredInput, 2, 6, 16) &&
                alterTableRuleGraph.hasNumericAt(filteredInput, 11) &&
                alterTableRuleGraph.hasPositiveNumberAt(filteredInput, 11) &&
                alterTableRuleGraph.hasIntegerAt(filteredInput, 11);

        errorMessage = isValid ? "" : "Parser error when validating Alter Table command:\n" +
                alterTableRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid INSERT command
     */
    private boolean isValidInsert(String[] filteredInput) {

        RuleGraph insertRuleGraph = RuleGraphTypes.getInsertRuleGraph();

        boolean isValid = insertRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! insertRuleGraph.hasIllegalReservedWord(filteredInput, 2, 5, 7) &&
                insertRuleGraph.hasNonNumericAt(filteredInput, 2, 7) &&
                insertRuleGraph.hasNumericAt(filteredInput, 5);

        errorMessage = isValid ? "" : "Parser error when validating Insert command:\n" +
                insertRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid DELETE command
     */
    private boolean isValidDelete(String[] filteredInput) {

        RuleGraph deleteRuleGraph = RuleGraphTypes.getDeleteRuleGraph();

        boolean isValid = deleteRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! deleteRuleGraph.hasIllegalReservedWord(filteredInput, 2, 4, 11, 13) &&
                deleteRuleGraph.hasNonNumericAt(filteredInput, 2, 4, 13) &&
                deleteRuleGraph.hasNumericAt(filteredInput, 11) &&
                ! deleteRuleGraph.hasIllegalDateAt(filteredInput, new int[] {5, 6, 7, 8, 9, 10}, new int[] {11, 13});

        errorMessage = isValid ? "" : "Parser error when validating Delete command:\n" +
                deleteRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid UPDATE command
     */
    private boolean isValidUpdate(String[] filteredInput) {

        RuleGraph updateRuleGraph = RuleGraphTypes.getUpdateRuleGraph();

        boolean isValid = updateRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! updateRuleGraph.hasIllegalReservedWord(filteredInput, 1, 3, 5, 7, 10, 12, 14) &&
                updateRuleGraph.hasNonNumericAt(filteredInput, 1, 3, 7, 10, 14) &&
                updateRuleGraph.hasNumericAt(filteredInput, 5, 12);

        errorMessage = isValid ? "" : "Parser error when validating Update command:\n" +
                updateRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid GRANT command
     */
    private boolean isValidGrant(String[] filteredInput) {

        RuleGraph grantRuleGraph = RuleGraphTypes.getGrantRuleGraph();

        boolean isValid = grantRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! grantRuleGraph.hasIllegalReservedWord(filteredInput, 12, 16, 20, 22) &&
                grantRuleGraph.hasNonNumericAt(filteredInput, 12, 16, 20, 22) &&
                // can't grant duplicate privileges
                ! grantRuleGraph.hasDuplicatesAt(filteredInput, 1, 2, 3, 4, 5, 6, 7) &&
                // can't have duplicate update columns
                ! grantRuleGraph.hasDuplicatesAt(filteredInput, 12) &&
                // can't have duplicate reference columns
                ! grantRuleGraph.hasDuplicatesAt(filteredInput, 16) &&
                // can't have duplicate users
                ! grantRuleGraph.hasDuplicatesAt(filteredInput, 22);

        errorMessage = isValid ? "" : "Parser error when validating Grant command:\n" +
                grantRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid REVOKE command
     */
    private boolean isValidRevoke(String[] filteredInput) {

        RuleGraph revokeRuleGraph = RuleGraphTypes.getRevokeRuleGraph();

        boolean isValid = revokeRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! revokeRuleGraph.hasIllegalReservedWord(filteredInput, 12, 16, 20, 22) &&
                revokeRuleGraph.hasNonNumericAt(filteredInput, 12, 16, 20, 22) &&
                // can't grant duplicate privileges
                ! revokeRuleGraph.hasDuplicatesAt(filteredInput, 1, 2, 3, 4, 5, 6, 7) &&
                // can't have duplicate update columns
                ! revokeRuleGraph.hasDuplicatesAt(filteredInput, 12) &&
                // can't have duplicate reference columns
                ! revokeRuleGraph.hasDuplicatesAt(filteredInput, 16) &&
                // can't have duplicate users
                ! revokeRuleGraph.hasDuplicatesAt(filteredInput, 22);

        errorMessage = isValid ? "" : "Parser error when validating Revoke command:\n" +
                revokeRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid BUILD FILE STRUCTURE command
     */
    private boolean isValidBuildFileStructure(String[] filteredInput) {

        RuleGraph buildFileStructureRuleGraph = RuleGraphTypes.getBuildFileStructureRuleGraph();

        boolean isValid = buildFileStructureRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! buildFileStructureRuleGraph.hasIllegalReservedWord(filteredInput, 7, 9, 12, 14) &&
                buildFileStructureRuleGraph.hasNonNumericAt(filteredInput, 7, 9, 12, 14) &&
                // can't cluster a table with itself
                ! buildFileStructureRuleGraph.hasDuplicatesAt(filteredInput,12, 14);

        errorMessage = isValid ? "" : "Parser error when validating Build File Structure command:\n" +
                buildFileStructureRuleGraph.getErrorMessage();

        return isValid;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid REMOVE FILE STRUCTURE command
     */
    private boolean isValidRemoveFileStructure(String[] filteredInput) {

        RuleGraph removeFileStructureRuleGraph = RuleGraphTypes.getRemoveFileStructureRuleGraph();

        boolean isValid = removeFileStructureRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! removeFileStructureRuleGraph.hasIllegalReservedWord(filteredInput, 4, 6, 10) &&
                removeFileStructureRuleGraph.hasNonNumericAt(filteredInput, 4, 6, 10) &&
                ! removeFileStructureRuleGraph.hasDuplicatesAt(filteredInput, 6, 10);

        errorMessage = isValid ? "" : "Parser error when validating Remove File Structure command:\n" +
                removeFileStructureRuleGraph.getErrorMessage();

        return isValid;
    }
}