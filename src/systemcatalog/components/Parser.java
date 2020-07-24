package systemcatalog.components;

import utilities.enums.InputType;
import utilities.enums.Keyword;
import datastructure.rulegraph.RuleGraph;

/**
 * Responsible for checking the syntax of the input to ensure it is syntactically correct.
 * Also handles miscellaneous functions that involve parsing input.
 * Please refer to the diagrams in "datastructures.rulegraph.diagrams" for information about
 * the syntax accepted.
 */
public class Parser {

    private RuleGraph ruleGraph;

    public Parser() {}

    // static utility methods ------------------------------------------------------------------------------------------

    /**
     * Cleans raw input so that it can be used by other methods without hassle.
     * @param input is unformatted data
     * @return formatted data
     */
    public String[] formatAndTokenizeInput(String input) {

        input = input.toLowerCase();

        // capitalize keywords within input
        for(Keyword keyword : Keyword.values()) {
            input = input.replaceAll(keyword.toString().toLowerCase(), keyword.toString());
        }

        // remove spaces at the beginning of the input
        input = input.replaceAll("^\\s+", "");

        // tokenize via spaces (removes redundant spaces too)
        String[] tokens = input.split("\\s+");
        StringBuilder formatted = new StringBuilder();

        // re-add the spaces
        for(String token : tokens) {
            formatted.append(token).append(" ");
        }

        // re-add ";"
        formatted.append(";");

        // TODO might be error-prone, length changes in loop
        // adding spaces between ",", ")", "(" too
        for(int i = 1; i < formatted.length() - 1; i++) {

            char token = formatted.charAt(i);

            if(token == ',' || token == ')' || token == '(') {

                // is a space needed before/after?
                char beforeToken = formatted.charAt(i - 1);
                char afterToken = formatted.charAt(i + 1);
                int offset = 0;

                if(beforeToken != ' ') {
                    formatted.insert(i, " ");
                    offset = 1;
                }

                if(afterToken != ' ') {
                    formatted.insert(i + 1 + offset, " ");
                }
            }
        }

        return formatted.toString().split("\\s+");
    }

    /**
     * @param candidate the string to test
     * @return whether the given candidate is numeric
     */
    public static boolean isNumeric(String candidate) {
        try {
            Double.parseDouble(candidate);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    // methods ---------------------------------------------------------------------------------------------------------

    public void setRuleGraph(RuleGraph ruleGraph) {
        this.ruleGraph = ruleGraph;
    }

    /**
     * Given a tokenized command, returns the type that it is or UNKNOWN.
     * @param command tokenized command
     * @return the input type
     */
    public InputType determineInputType(String[] command) {

        String firstToken = command[0];

        switch(firstToken) {
            case "SELECT":
                return InputType.QUERY;
            case "CREATE":
                return InputType.CREATE_TABLE;
            case "DROP":
                return InputType.DROP_TABLE;
            case "ALTER":
                return InputType.ALTER_TABLE;
            case "INSERT":
                return InputType.INSERT;
            case "DELETE":
                return InputType.DELETE;
            case "UPDATE":
                return InputType.UPDATE;
            case "GRANT":
                return InputType.GRANT;
            case "REVOKE":
                return InputType.REVOKE;
            case "BUILD":
                return InputType.BUILD_FILE_STRUCTURE;
            case "REMOVE":
                return InputType.REMOVE_FILE_STRUCTURE;
            default: {
                return InputType.UNKNOWN;
            }
        }
    }

    /**
     * This returns whether the input is syntactically correct depending on the rule graph set
     * and the input type.
     * @param inputType the type of input
     * @param input - input from the user
     * @return whether the input is syntactically correct
     */
    public boolean isValid(InputType inputType, String[] input) {
        // all input types will check for valid syntax and illegal keyword usage
        boolean isValid = (ruleGraph.isSyntacticallyCorrect(input)) && ! ruleGraph.hasIllegalKeyword(input);
        switch(inputType) {
            case QUERY:
                return isValid && isValidQuery(input);
            case CREATE_TABLE:
                return isValid && isValidCreateTable(input);
            case DROP_TABLE:
                return isValid && isValidDropTable(input);
            case ALTER_TABLE:
                return isValid && isValidAlterTable(input);
            case INSERT:
                return isValid && isValidInsert(input);
            case DELETE:
                return isValid && isValidDelete(input);
            case UPDATE:
                return isValid && isValidUpdate(input);
            case GRANT:
                return isValid && isValidGrant(input);
            case REVOKE:
                return isValid && isValidRevoke(input);
            case BUILD_FILE_STRUCTURE:
                return isValid && isValidBuildFileStructure(input);
            case REMOVE_FILE_STRUCTURE:
                return isValid && isValidRemoveFileStructure(input);
            case UNKNOWN:
            default:
                return false;
        }
    }

    public boolean isValidQuery(String[] query) {
        return  ! ruleGraph.hasIllegalNumericAt(query, 2, 10, 14, 17, 20, 23, 35, 45) &&
                // > (26), < (27), >= (28), <= (29) can only be used with a numeric value (30) in WHERE clause
                ! ruleGraph.hasIllegalValue(query,30, 26, 27, 28, 29) &&
                // same thing in having clause
                ! ruleGraph.hasIllegalValue(query, 53, 49, 50, 51, 52);
    }

    public boolean isValidCreateTable(String[] createTable) {
        return  ! ruleGraph.hasIllegalNumericAt(createTable, 2, 4) &&
                // can't have duplicate column names
                ! ruleGraph.hasDuplicatesAt(createTable, 4) &&
                // size of the column can only be numeric
                ! ruleGraph.hasIllegalValue(createTable, 8);
    }

    public boolean isValidDropTable(String[] dropTable) {
        return ! ruleGraph.hasIllegalNumericAt(dropTable, 2);
    }

    public boolean isValidAlterTable(String[] alterTable) {
        return  ! ruleGraph.hasIllegalNumericAt(alterTable, 2, 6, 15) &&
                // size can only be numeric
                ! ruleGraph.hasIllegalValue(alterTable, 10);
    }

    public boolean isValidInsert(String[] insert) {
        return ! ruleGraph.hasIllegalNumericAt(insert, 2);
    }

    public boolean isValidDelete(String[] delete) {
        return  ! ruleGraph.hasIllegalNumericAt(delete, 2, 4) &&
                // > (7), < (8), >= (9), <= (10) can only be used with a numeric value (11)
                ! ruleGraph.hasIllegalValue(delete, 11, 7, 8, 9, 10);
    }

    public boolean isValidUpdate(String[] update) {
        return ! ruleGraph.hasIllegalNumericAt(update, 1, 3, 7);
    }

    public boolean isValidGrant(String[] grant) {
        return  ! ruleGraph.hasIllegalNumericAt(grant, 12, 16, 20, 22) &&
                // can't grant the same privilege more than once, update and reference
                // columns must be unique, and usernames must be unique
                ! ruleGraph.hasDuplicatesAt(grant, 1, 2, 3, 4, 5, 6, 12, 16, 22);
    }

    public boolean isValidRevoke(String[] revoke) {
        // grant command is similar enough in structure to do this
        return isValidGrant(revoke);
    }

    public boolean isValidBuildFileStructure(String[] buildFileStructure) {
        return  ! ruleGraph.hasIllegalNumericAt(buildFileStructure, 7, 9, 12, 14) &&
                // can't cluster a table with itself
                ! ruleGraph.hasDuplicatesAt(buildFileStructure,12, 14);
    }

    public boolean isValidRemoveFileStructure(String[] removeFileStructure) {
        return ! ruleGraph.hasIllegalNumericAt(removeFileStructure, 4, 6);
    }
}