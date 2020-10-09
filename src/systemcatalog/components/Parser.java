package systemcatalog.components;

import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import enums.InputType;

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
                ! queryRuleGraph.hasIllegalKeyword(filteredInput) &&
                // column names, table names, completely numeric values encased in quotes like "123"
                // ("blah123" is fine) should not be numeric at all
                ! queryRuleGraph.hasNumericAt(filteredInput, false, 2, 9, 13, 15, 18, 20, 27, 29, 38, 43, 52, 62) &&
                // column names referenced in SELECT clause must be unique
                ! queryRuleGraph.hasDuplicatesAt(filteredInput, 2, 9) &&
                // table names referenced in FROM clause must be unique
                ! queryRuleGraph.hasDuplicatesAt(filteredInput, 13, 15, 18) &&
                // column names referenced in group by clause must be unique
                ! queryRuleGraph.hasDuplicatesAt(filteredInput, 43) &&
                // >, <, >=, and <= can only be used with a numeric value/date for WHERE and HAVING clauses
                ! queryRuleGraph.hasIllegalComparison(filteredInput, 38, 32, 33, 34, 35) &&
                ! queryRuleGraph.hasIllegalComparison(filteredInput, 62, 56, 57, 58, 59);

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
                ! createTableRuleGraph.hasIllegalKeyword(filteredInput) &&
                // don't want numeric values for table name and column names, want numeric values for the sizes though
                ! createTableRuleGraph.hasNumericAt(filteredInput, false, 2, 4) &&
                createTableRuleGraph.hasNumericAt(filteredInput, true, 9, 11) &&
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
                ! dropTableRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! dropTableRuleGraph.hasNumericAt(filteredInput, false, 2);

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
                ! alterTableRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! alterTableRuleGraph.hasNumericAt(filteredInput, false, 2, 6, 16) &&
                alterTableRuleGraph.hasNumericAt(filteredInput, true, 11);

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
                ! insertRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! insertRuleGraph.hasNumericAt(filteredInput, false, 2, 5);

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
                ! deleteRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! deleteRuleGraph.hasNumericAt(filteredInput, false, 2, 4, 13) &&
                deleteRuleGraph.hasNumericAt(filteredInput, false, 11) &&
                ! deleteRuleGraph.hasIllegalComparison(filteredInput, 13, 7, 8, 9, 10);

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
                ! updateRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! updateRuleGraph.hasNumericAt(filteredInput, false, 1, 3, 7, 10, 14) &&
                updateRuleGraph.hasNumericAt(filteredInput, false, 5, 12);

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
                ! grantRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! grantRuleGraph.hasNumericAt(filteredInput, false, 12, 16, 20, 22) &&
                // can't grant the same privilege more than once, update and reference columns must be unique,
                // and usernames must be unique
                ! grantRuleGraph.hasDuplicatesAt(filteredInput, 1, 2, 3, 4, 5, 6, 7, 12, 16, 22);

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
                ! revokeRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! revokeRuleGraph.hasNumericAt(filteredInput, false, 12, 16, 20, 22) &&
                // can't grant the same privilege more than once, update and reference columns must be unique,
                // and usernames must be unique
                ! revokeRuleGraph.hasDuplicatesAt(filteredInput, 1, 2, 3, 4, 5, 6, 7, 12, 16, 22);

        errorMessage = isValid ? "" : "Parser error when validating Revoke command:\n" +
                revokeRuleGraph.getErrorMessage();

        return false;
    }

    /**
     * @param filteredInput is the input after being filtered
     * @return whether this is a valid BUILD FILE STRUCTURE command
     */
    private boolean isValidBuildFileStructure(String[] filteredInput) {

        RuleGraph buildFileStructureRuleGraph = RuleGraphTypes.getBuildFileStructureRuleGraph();

        boolean isValid = buildFileStructureRuleGraph.isSyntacticallyCorrect(filteredInput) &&
                ! buildFileStructureRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! buildFileStructureRuleGraph.hasNumericAt(filteredInput, false, 7, 9, 12, 14) &&
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
                ! removeFileStructureRuleGraph.hasIllegalKeyword(filteredInput) &&
                ! removeFileStructureRuleGraph.hasNumericAt(filteredInput, false, 4, 6, 10);

        errorMessage = isValid ? "" : "Parser error when validating Remove File Structure command:\n" +
                removeFileStructureRuleGraph.getErrorMessage();

        return isValid;
    }
}