package systemcatalog.components;

import datastructures.misc.Logger;
import utilities.enums.Keyword;
import datastructures.rulegraph.RuleGraph;

/**
 * Responsible for checking the syntax of the input to ensure it is syntactically correct.
 * Also handles miscellaneous functions that involve parsing input.
 * Please refer to the diagrams in "files.assets.helpscene.diagrams" for information about
 * the syntax accepted.
 */
public class Parser {

    private RuleGraph.Type ruleGraphType;
    private RuleGraph ruleGraphToUse;
    private String[] tokenizedInput;

    public Parser() {}

    // static utility methods ------------------------------------------------------------------------------------------

    /**
     * Cleans raw input so that it can be used by other methods without hassle.
     * @param input is unformatted data
     * @return formatted data
     */
    public static String[] formatAndTokenizeInput(String input) {

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
     * Given a tokenized command, returns the type that it is or UNKNOWN.
     * @param tokenizedInput tokenized command
     * @return the input type
     */
    public static RuleGraph.Type determineRuleGraphType(String[] tokenizedInput) {

        String firstToken = tokenizedInput[0];

        switch(firstToken) {
            case "SELECT":
                return RuleGraph.Type.QUERY;
            case "CREATE":
                return RuleGraph.Type.CREATE_TABLE;
            case "DROP":
                return RuleGraph.Type.DROP_TABLE;
            case "ALTER":
                return RuleGraph.Type.ALTER_TABLE;
            case "INSERT":
                return RuleGraph.Type.INSERT;
            case "DELETE":
                return RuleGraph.Type.DELETE;
            case "UPDATE":
                return RuleGraph.Type.UPDATE;
            case "GRANT":
                return RuleGraph.Type.GRANT;
            case "REVOKE":
                return RuleGraph.Type.REVOKE;
            case "BUILD":
                return RuleGraph.Type.BUILD_FILE_STRUCTURE;
            case "REMOVE":
                return RuleGraph.Type.REMOVE_FILE_STRUCTURE;
            default:
                return RuleGraph.Type.UNKNOWN;
        }
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

    // validation ------------------------------------------------------------------------------------------------------

    /**
     * @return whether the given input is syntactically correct with respect to
     * the tokenized input and rule graph that have been set
     */
    public boolean isValid() {

        // check if the input is syntactically correct and contains no illegal keywords
        boolean isValid = (ruleGraphToUse.isSyntacticallyCorrect(tokenizedInput)) &&
                ! ruleGraphToUse.hasIllegalKeyword(tokenizedInput);

        if(! isValid) {
            return false;
        }

        switch(ruleGraphType) {
            case QUERY:
                return isValidQuery();
            case CREATE_TABLE:
                return isValidCreateTable();
            case DROP_TABLE:
                return isValidDropTable();
            case ALTER_TABLE:
                return isValidAlterTable();
            case INSERT:
                return isValidInsert();
            case DELETE:
                return isValidDelete();
            case UPDATE:
                return isValidUpdate();
            case GRANT:
                return isValidGrant();
            case REVOKE:
                return isValidRevoke();
            case BUILD_FILE_STRUCTURE:
                return isValidBuildFileStructure();
            case REMOVE_FILE_STRUCTURE:
                return isValidRemoveFileStructure();
            case UNKNOWN:
            default:
                return false;
        }
    }

    public boolean isValidQuery() {
        return  ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 2, 10, 14, 17, 20, 23, 35, 45) &&
                // > (26), < (27), >= (28), <= (29) can only be used with a numeric value (30) in WHERE clause
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput,30, 26, 27, 28, 29) &&
                // same thing in having clause
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 53, 49, 50, 51, 52);
    }

    public boolean isValidCreateTable() {
        return  ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 2, 4) &&
                // can't have duplicate column names
                ! ruleGraphToUse.hasDuplicatesAt(tokenizedInput, 4) &&
                // size of the column can only be numeric
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 8);
    }

    public boolean isValidDropTable() {
        return ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 2);
    }

    public boolean isValidAlterTable() {
        return  ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 2, 6, 15) &&
                // size can only be numeric
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 10);
    }

    public boolean isValidInsert() {
        return ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 2);
    }

    public boolean isValidDelete() {
        return  ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 2, 4) &&
                // > (7), < (8), >= (9), <= (10) can only be used with a numeric value (11)
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 11, 7, 8, 9, 10);
    }

    public boolean isValidUpdate() {
        return ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 1, 3, 7);
    }

    public boolean isValidGrant() {
        return  ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 12, 16, 20, 22) &&
                // can't grant the same privilege more than once, update and reference
                // columns must be unique, and usernames must be unique
                ! ruleGraphToUse.hasDuplicatesAt(tokenizedInput, 1, 2, 3, 4, 5, 6, 12, 16, 22);
    }

    public boolean isValidRevoke() {
        // grant command is similar enough in structure to do this
        return isValidGrant();
    }

    public boolean isValidBuildFileStructure() {
        return  ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 7, 9, 12, 14) &&
                // can't cluster a table with itself
                ! ruleGraphToUse.hasDuplicatesAt(tokenizedInput,12, 14);
    }

    public boolean isValidRemoveFileStructure() {
        return ! ruleGraphToUse.hasIllegalNumericAt(tokenizedInput, 4, 6);
    }
}