package systemcatalog.components;

import utilities.InputType;
import utilities.Keyword;
import datastructures.rulegraph.RuleGraph;

/**
 * Responsible for checking the syntax of the input to ensure it abides.
 */
public class Parser {

    private RuleGraph ruleGraph;

    public Parser() {}

    public static boolean isNumeric(String candidate) {

        try {
            Double.parseDouble(candidate);
        } catch(NumberFormatException e) {
            return false;
        }

        return true;
    }

    public void setRuleGraph(RuleGraph ruleGraph) { this.ruleGraph = ruleGraph; }

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
            default: {
                try {
                    String fileStructure = command[1] + " " + command[2];
                    switch(fileStructure) {
                        case "SECONDARY B-TREE":
                            return InputType.BUILD_SECONDARY_B_TREE;
                        case "CLUSTERED B-TREE":
                            return InputType.BUILD_CLUSTERED_B_TREE;
                        case "HASH TABLE":
                            return InputType.BUILD_HASH_TABLE;
                        case "CLUSTERED FILE":
                            return InputType.BUILD_CLUSTERED_FILE;
                        default:
                            return InputType.UNKNOWN;
                    }
                } catch(ArrayIndexOutOfBoundsException e) {
                    return InputType.UNKNOWN;
                }
            }
        }
    }

    public boolean isValid(InputType inputType, String[] input) {
        switch(inputType) {
            case QUERY:
                return isValidQuery(input);
            case CREATE_TABLE:
                return isValidCreateTable(input);
            case DROP_TABLE:
                return isValidDropTable(input);
            case ALTER_TABLE:
                return isValidAlterTable(input);
            case INSERT:
                return isValidInsert(input);
            case DELETE:
                return isValidDelete(input);
            case UPDATE:
                return isValidUpdate(input);
            case GRANT:
                return isValidGrant(input);
            case REVOKE:
                return isValidRevoke(input);
            case BUILD_SECONDARY_B_TREE:
                return isValidBuildSecondaryBTree(input);
            case BUILD_CLUSTERED_B_TREE:
                return isValidBuildClusteredBTree(input);
            case BUILD_HASH_TABLE:
                return isValidBuildHashTable(input);
            case BUILD_CLUSTERED_FILE:
                return isValidBuildClusteredFile(input);
            case UNKNOWN:
            default:
                return false;
        }
    }

    public boolean isValidQuery(String[] query) {
        return ruleGraph.isSyntacticallyCorrect(query) &&
                ! ruleGraph.hasIllegalKeyword(query) &&
                // at SELECT clause's columns
                ! ruleGraph.hasDuplicatesAt(query, 2) &&
                // at WHERE clause's table names (cartesian product)
                ! ruleGraph.hasDuplicatesAt(query, 13, 15) &&
                // at WHERE clause's table names (join)
                ! ruleGraph.hasDuplicatesAt(query, 13, 17) &&
                // (>, <, >=, <= must be associated with a number data type)
                ! ruleGraph.hasIllegalNumericAt(query,31, 27, 28, 29, 30);
    }

    public boolean isValidCreateTable(String[] createTable) {
        return ruleGraph.isSyntacticallyCorrect(createTable) &&
                ! ruleGraph.hasIllegalKeyword(createTable) &&
                // can't have duplicate column names
                ! ruleGraph.hasDuplicatesAt(createTable, 4) &&
                // size of the column can only be numeric
                ! ruleGraph.hasIllegalNumericAt(createTable, 8);
    }

    public boolean isValidDropTable(String[] dropTable) {
        return ruleGraph.isSyntacticallyCorrect(dropTable) &&
                ! ruleGraph.hasIllegalKeyword(dropTable);
    }

    public boolean isValidAlterTable(String[] alterTable) {
        return ruleGraph.isSyntacticallyCorrect(alterTable) &&
                ! ruleGraph.hasIllegalKeyword(alterTable);
    }

    public boolean isValidInsert(String[] insert) {
        return ruleGraph.isSyntacticallyCorrect(insert) &&
                ! ruleGraph.hasIllegalKeyword(insert);
    }

    public boolean isValidDelete(String[] delete) {
        return ruleGraph.isSyntacticallyCorrect(delete) &&
                ! ruleGraph.hasIllegalKeyword(delete) &&
                // (>, <, >=, <= must be associated with a number data type)
                ! ruleGraph.hasIllegalNumericAt(delete, 11, 7, 8, 9, 10);
    }

    public boolean isValidUpdate(String[] update) {
        return ruleGraph.isSyntacticallyCorrect(update) &&
                ! ruleGraph.hasIllegalKeyword(update);
    }

    // TODO add checking for duplicates
    public boolean isValidGrant(String[] grant) {
        return ruleGraph.isSyntacticallyCorrect(grant) &&
                ! ruleGraph.hasIllegalKeyword(grant);
    }

    // TODO add checking for duplicates
    public boolean isValidRevoke(String[] revoke) {
        return ruleGraph.isSyntacticallyCorrect(revoke) &&
                ! ruleGraph.hasIllegalKeyword(revoke);
    }

    public boolean isValidBuildSecondaryBTree(String[] secondaryBTree) {
        return ruleGraph.isSyntacticallyCorrect(secondaryBTree) &&
                ! ruleGraph.hasIllegalKeyword(secondaryBTree);
    }

    public boolean isValidBuildClusteredBTree(String[] clusteredBTree) {
        return ruleGraph.isSyntacticallyCorrect(clusteredBTree) &&
                ! ruleGraph.hasIllegalKeyword(clusteredBTree);
    }

    public boolean isValidBuildHashTable(String[] hashTable) {
        return ruleGraph.isSyntacticallyCorrect(hashTable) &&
                ! ruleGraph.hasIllegalKeyword(hashTable);
    }

    public boolean isValidBuildClusteredFile(String[] clusteredFile) {
        return ruleGraph.isSyntacticallyCorrect(clusteredFile) &&
                ! ruleGraph.hasIllegalKeyword(clusteredFile) &&
                ! ruleGraph.hasDuplicatesAt(clusteredFile, 4, 6);
    }
}
