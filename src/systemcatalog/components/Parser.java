package systemcatalog.components;

import utilities.enums.Keyword;
import datastructures.rulegraph.RuleGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        // remove spaces at the beginning and end of the input
        input = input.trim();

        // tokenize the input via spaces, removes redundant input too
        String[] inputAsTokens = input.split("\\s+");
        StringBuilder rebuildInput = new StringBuilder();

        // capitalizing keywords
        for(int i = 0; i < inputAsTokens.length; i++) {
            inputAsTokens[i] = Keyword.toUppercase(inputAsTokens[i]);
        }

        // re-create the input, but with fixed spaces
        for(String token : inputAsTokens) {
            rebuildInput.append(token).append(" ");
        }

        // re-add ";" which is used for determining where the input will end later on
        rebuildInput.append(";");

        // adding spaces between ',', ')', '(', '"' too
        for(int i = 1; i < rebuildInput.length() - 1; i++) {

            char token = rebuildInput.charAt(i);

            if(token == ',' || token == ')' || token == '(' || token == '"') {

                // is a space needed before/after?
                char beforeToken = rebuildInput.charAt(i - 1);
                char afterToken = rebuildInput.charAt(i + 1);
                int offset = 0;

                if(beforeToken != ' ') {
                    rebuildInput.insert(i, " ");
                    offset = 1;
                }

                if(afterToken != ' ') {
                    rebuildInput.insert(i + 1 + offset, " ");
                }
            }
        }

        // need to handle stuff that appears in double quotes which makes things difficult
        inputAsTokens = rebuildInput.toString().split("\\s+");

        // collecting strings that appear in double quotes
        List<String> stringInDoubleQuotesList = new ArrayList<>();
        StringBuilder stringInDoubleQuotes = new StringBuilder();

        char[] inputAsChars = input.toCharArray();
        int count = 0;

        for(int i = 0; i < inputAsChars.length; i++) {

            char currentChar = inputAsChars[i];

            // keep moving forward in the array until the second '"' is found
            if(currentChar == '"') {
                for(int j = i + 1; j < inputAsChars.length; j++) { // j = i + 1 skips over current '"'
                    currentChar = inputAsChars[j];

                    if(currentChar == '"') {
                        i = j; // set to the closing '"' for now then i will increment to next token in outer loop
                        break; // found second '"', we're done
                    }

                    stringInDoubleQuotes.append(currentChar);
                }

                stringInDoubleQuotesList.add(stringInDoubleQuotes.toString()); // add the contents of what's in ""
                stringInDoubleQuotes = new StringBuilder();
            }

        }

        // combining what we currently have with the stuff in double quotes
        List<String> handleDoubleQuotes = new ArrayList<>();

        for(int i = 0; i < inputAsTokens.length; i++) {

            String currentToken = inputAsTokens[i];

            // similar process to what we did with the characters, skip everything in the middle of ""
            if(currentToken.equalsIgnoreCase("\"")) {
                handleDoubleQuotes.add(currentToken); // add the first "

                for(int j = i + 1; j < inputAsTokens.length; j++) {
                    currentToken = inputAsTokens[j];

                    if(currentToken.equalsIgnoreCase("\"")) {
                        i = j;
                        break;
                    }
                }

                handleDoubleQuotes.add(stringInDoubleQuotesList.remove(0)); // add the item in between ""
                handleDoubleQuotes.add(currentToken); // add the last "

            // just normal formatted input, just add it
            } else {
                handleDoubleQuotes.add(currentToken);
            }
        }

        // populate the stuff to return with the correct input
        String[] formattedInput = new String[handleDoubleQuotes.size()];

        for(int i = 0; i < handleDoubleQuotes.size(); i++) {
            formattedInput[i] = handleDoubleQuotes.get(i);
        }

        return formattedInput;
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
        return ! ruleGraphToUse.hasNumericAt(tokenizedInput,2, 9, 13, 15, 18, 20, 27, 29, 38, 43, 52, 62) &&
                // table names referenced in from clause must be unique
                ! ruleGraphToUse.hasDuplicatesAt(tokenizedInput, 13, 15, 18) &&
                // column names referenced in group by clause must be unique
                ! ruleGraphToUse.hasDuplicatesAt(tokenizedInput, 43) &&
                // >, <, >=, and <= can only be used with a numeric value
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 37, 32, 33, 34, 35) &&
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 61, 56, 57, 58, 59);
    }

    public boolean isValidCreateTable() {
        return  ! ruleGraphToUse.hasNumericAt(tokenizedInput, 2, 4) &&
                // can't have duplicate column names
                ! ruleGraphToUse.hasDuplicatesAt(tokenizedInput, 4) &&
                // size of the column can only be numeric
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 8);
    }

    public boolean isValidDropTable() {
        return ! ruleGraphToUse.hasNumericAt(tokenizedInput, 2);
    }

    public boolean isValidAlterTable() {
        return  ! ruleGraphToUse.hasNumericAt(tokenizedInput, 2, 6, 15) &&
                // size can only be numeric
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 10);
    }

    public boolean isValidInsert() {
        return ! ruleGraphToUse.hasNumericAt(tokenizedInput, 2, 7);
    }

    public boolean isValidDelete() {
        return  ! ruleGraphToUse.hasNumericAt(tokenizedInput, 2, 4, 13) &&
                // >, <, >=, and <= can only be used with a numeric value
                ! ruleGraphToUse.hasIllegalValue(tokenizedInput, 12, 7, 8, 9, 10);
    }

    public boolean isValidUpdate() {
        return ! ruleGraphToUse.hasNumericAt(tokenizedInput, 1, 3, 7, 10, 14);
    }

    public boolean isValidGrant() {
        return  ! ruleGraphToUse.hasNumericAt(tokenizedInput, 12, 16, 20, 22) &&
                // can't grant the same privilege more than once, update and reference
                // columns must be unique, and usernames must be unique
                ! ruleGraphToUse.hasDuplicatesAt(tokenizedInput, 1, 2, 3, 4, 5, 6, 12, 16, 22);
    }

    public boolean isValidRevoke() {
        // grant command is similar enough in structure to do this
        return isValidGrant();
    }

    public boolean isValidBuildFileStructure() {
        return  ! ruleGraphToUse.hasNumericAt(tokenizedInput, 7, 9, 12, 14) &&
                // can't cluster a table with itself
                ! ruleGraphToUse.hasDuplicatesAt(tokenizedInput,12, 14);
    }

    public boolean isValidRemoveFileStructure() {
        return ! ruleGraphToUse.hasNumericAt(tokenizedInput, 4, 6);
    }
}