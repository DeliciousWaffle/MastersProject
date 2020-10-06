package utilities;

import datastructures.rulegraph.RuleGraph;
import exceptions.UnknownInputTypeException;
import utilities.enums.InputType;
import utilities.enums.Keyword;
import utilities.enums.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing random stuff that doesn't necessarily belong to any other class.
 */
public final class Utilities {

    private Utilities() {}

    /**
     * Returns whether the given string is a numeric value.
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

    /**
     * "Cleans" raw user input by formatting and breaking it up into tokens so that it can be easily
     * used by other methods without hassle.
     * @param input is raw, unformatted data from the user
     * @return formatted, tokenized input to be used by other methods
     */
    public static String[] filterInput(String input) {

        // remove spaces at the beginning and end of the input
        input = input.trim();

        // tokenize the input via spaces, removes redundant spaces between characters too
        String[] inputAsTokens = input.split("\\s+");
        StringBuilder rebuildInput = new StringBuilder();

        // capitalizing keywords (not necessary, but makes things pretty)
        for (int i = 0; i < inputAsTokens.length; i++) {
            inputAsTokens[i] = Keyword.toUppercase(inputAsTokens[i]);
        }

        // re-create the input, but with fixed spaces
        for (String token : inputAsTokens) {
            rebuildInput.append(token).append(" ");
        }

        // re-add ";" which is used for determining where the input will end later on
        rebuildInput.append(";");

        // adding spaces between ',', ')', '(', '"' too
        for (int i = 1; i < rebuildInput.length() - 1; i++) {

            char token = rebuildInput.charAt(i);

            if (token == ',' || token == ')' || token == '(' || token == '"') {

                // is a space needed before/after?
                char beforeToken = rebuildInput.charAt(i - 1);
                char afterToken = rebuildInput.charAt(i + 1);
                int offset = 0; // insertion will change the length of the string, need to account for this

                if (beforeToken != ' ') {
                    rebuildInput.insert(i, " ");
                    offset = 1;
                }

                if (afterToken != ' ') {
                    rebuildInput.insert(i + 1 + offset, " ");
                }
            }
        }

        // need to handle the strings that appear in double quotes which makes things difficult
        inputAsTokens = rebuildInput.toString().split("\\s+");

        // collecting strings that appear in double quotes
        List<String> stringInDoubleQuotesList = new ArrayList<>();
        StringBuilder stringInDoubleQuotes = new StringBuilder();

        char[] inputAsChars = input.toCharArray();

        for (int i = 0; i < inputAsChars.length; i++) {

            char currentChar = inputAsChars[i];

            // keep moving forward in the array until the second '"' is found
            if (currentChar == '"') {
                for (int j = i + 1; j < inputAsChars.length; j++) { // j = i + 1 skips over current '"'
                    currentChar = inputAsChars[j];
                    if (currentChar == '"') {
                        i = j; // set to the closing '"' for now then i will increment to next token in outer loop
                        break; // found second '"', we're done
                    }

                    stringInDoubleQuotes.append(currentChar);
                }

                stringInDoubleQuotesList.add(stringInDoubleQuotes.toString()); // add the contents of what's in the ""
                stringInDoubleQuotes = new StringBuilder();
            }
        }

        // combining what we currently have with the stuff in double quotes
        List<String> handleDoubleQuotes = new ArrayList<>();

        for (int i = 0; i < inputAsTokens.length; i++) {

            String currentToken = inputAsTokens[i];

            // similar process to what we did with the characters, skip everything in the middle of ""
            if (currentToken.equalsIgnoreCase("\"")) {
                handleDoubleQuotes.add(currentToken); // add the first "
                for (int j = i + 1; j < inputAsTokens.length; j++) {
                    currentToken = inputAsTokens[j];
                    if (currentToken.equalsIgnoreCase("\"")) {
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

        for (int i = 0; i < handleDoubleQuotes.size(); i++) {
            formattedInput[i] = handleDoubleQuotes.get(i);
        }

        return formattedInput;
    }

    /**
     * Given the filtered and tokenized input, determines the type of input that this is.
     * @param filteredInput is the input after being filtered
     * @return the type of input that this is
     */
    public static InputType determineRuleGraphType(String[] filteredInput) {

        String firstToken = filteredInput[0];

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
            default:
                return InputType.UNKNOWN;
        }
    }

    /**
     * @param candidate is the String to test
     * @return whether candidate is a keyword or symbol
     */
    public static boolean isReservedWord(String candidate) {

        for (Keyword keyword : Keyword.values()) {
            if (candidate.equalsIgnoreCase(keyword.toString())) {
                return true;
            }
        }

        for (Symbol symbol : Symbol.values()) {
            if (candidate.equalsIgnoreCase(symbol.getSymbol())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param candidate is the String to check
     * @return whether or not the candidate String is in the format of a date (MM-DD-YYYY)
     */
    public static boolean hasDateFormat(String candidate) {

        // make sure length is correct
        int length = candidate.length();

        if (length != 10) {
            return false;
        }

        // check that "-" is present
        boolean hasCorrectDashes = candidate.charAt(2) == '-' && candidate.charAt(5) == '-';

        if (! hasCorrectDashes) {
            return false;
        }

        // check that MM, DD, and YYYY are numeric
        String[] monthDayYear = candidate.split("-");

        boolean hasNumericMonth = isNumeric(monthDayYear[0]);
        boolean hasNumericDay = isNumeric(monthDayYear[1]);
        boolean hasNumericYear = isNumeric(monthDayYear[2]);

        return hasNumericMonth && hasNumericDay && hasNumericYear;
    }
}