package datastructures.rulegraph;

import datastructures.rulegraph.component.RuleNode;
import enums.Symbol;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A graph containing a set of rules that the input must abide by. If the input is syntactically correct, then this
 * class offers methods for extracting data so that it can be used elsewhere in the application. For reference,
 * go to src/files/assets/helpscreen for a collection of diagrams that showcase the syntax of each command possible.
 */
public class RuleGraph {

    private final List<RuleNode> rules;
    private String errorMessage;

    public RuleGraph() {
        rules = new ArrayList<>();
        errorMessage = "";
    }

    public void addRule(String rule, boolean mutable, int id) {
        rules.add(new RuleNode(rule, mutable, id));
    }

    /**
     * Sets the current node's children. The children will represent the valid state transitions that
     * the current id can take when traversing. Assumes that all children have already been added.
     * @param id is the current id
     * @param childrenIDs will be the children nodes of the id supplied
     */
    public void setChildren(int id, int... childrenIDs) {

        RuleNode current = rules.get(id);
        RuleNode[] children = new RuleNode[childrenIDs.length];

        for (int i = 0; i < childrenIDs.length; i++) {
            int childID = childrenIDs[i];
            children[i] = rules.get(childID);
        }

        current.setChildren(children);
    }

    public RuleNode[] getChildren(RuleNode current) {
        return current.getChildren();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Given some form of tokenized input and a graph to abide by, returns whether it is syntactically correct.
     * Should be called before calling any other method.
     * @param filteredInput is the input after being filtered
     * @return whether the filtered input is syntactically correct
     */
    public boolean isSyntacticallyCorrect(String[] filteredInput) {

        // used for debugging purposes
        StringBuilder debugTokens = new StringBuilder();
        StringBuilder debugGraph = new StringBuilder();

        // temp node to help with traversal, connect it to the first rule
        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));

        // used for traversal through the graph
        RuleNode pointer = root;

        for (String token : filteredInput) {

            debugTokens.append(token);

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;

            // search for immutable children first, give precedence to keywords
            RuleNode mutableChild = null;

            for (RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                // save this thing for later
                if (isMutable) {
                    mutableChild = child;
                }

                // found a keyword, we're good to go!
                if (rule.equalsIgnoreCase(token) && ! isMutable) {
                    pointer = child;
                    foundChild = true;
                    debugGraph.append(pointer.getData());
                    break;
                }
            }

            // didn't find an immutable child, see if we found a mutable one
            if (! foundChild && mutableChild != null) {
                pointer = mutableChild;
                foundChild = true;
                debugGraph.append(pointer.getData());
            }

            // didn't find anything, what happened?
            if (! foundChild) {
                setSyntaxErrorMessage(pointersChildren, token, debugTokens, debugGraph);
                return false;
            }

            debugTokens.append(" ");
            debugGraph.append(" ");
        }

        return true;
    }

    /**
     * After ensuring that the user's input is correct from calling isSyntacticallyCorrect() , this returns a list
     * of tokens encountered at the supplied ids.
     * @param filteredInput is the input after being filtered
     * @param ids are the ids to extract input from
     * @return returns the tokens encountered at the supplied ids
     */
    public List<String> getTokensAt(String[] filteredInput, int... ids) {

        List<String> tokensToGet = new ArrayList<>();

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for(String token : filteredInput) {

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;
            RuleNode mutableChild = null;

            for (RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                if (isMutable) {
                    mutableChild = child;
                }

                if (rule.equalsIgnoreCase(token) && !isMutable) {
                    pointer = child;
                    foundChild = true;
                    break;
                }
            }

            if (!foundChild && mutableChild != null) {
                pointer = mutableChild;
            }

            // see if this current token matches any of the IDs supplied
            int currentId = pointer.getId();

            for(int id : ids) {
                if(currentId == id) {
                    tokensToGet.add(token);
                    break;
                }
            }
        }

        return tokensToGet;
    }

    /**
     * Returns whether an illegal reserved word is encountered where one shouldn't be. This would
     * check for mutable nodes that contain reserved words like FROM, WHERE, etc.
     * @param filteredInput is the input after being filtered
     * @return whether illegal keywords are contained in this input
     */
    public boolean hasIllegalReservedWord(String[] filteredInput, int... mutableNodeIds) {

        List<String> tokens = getTokensAt(filteredInput, mutableNodeIds);

        for (String token : tokens) {
            boolean isReservedWord = Utilities.isReservedWord(token);
            if (isReservedWord) {
                errorMessage = "Found unexpected Keyword or Symbol: \"" + token + "\"";
                return true;
            }
        }

        return false;
    }
    
    /**
     * Given one or more rule IDs, finds whether duplicate values occur within that list.
     * Will completely ignore the case of the tokens found at the ids supplied.
     * Eg. SELECT A, a FROM TableName is a taste for what we're searching for.
     * @param filteredInput is a query filtered from previous methods that has no major errors
     * @param ids are rules to check
     * @return whether there are duplicate values for one or more rules
     */
    public boolean hasDuplicatesAt(String[] filteredInput, int... ids) {

        List<String> tokens = getTokensAt(filteredInput, ids);
        List<String> occurrences = new ArrayList<>();

        for (String token : tokens) {
            for (String occurrence : occurrences) {
                if (token.equalsIgnoreCase(occurrence)) {
                    errorMessage = "Duplicate values are not allowed, found: \"" + token + "\"";
                    return true;
                }
            }
            occurrences.add(token); // didn't find a duplicate value, add the token
        }

        return false;
    }

    /**
     * Returns whether the numbers encountered at the supplied ids are numeric values. Assumed to
     * have called isNumeric() method first. All ids supplied must be numeric for true to be returned.
     * @param filteredInput is the input after being filtered
     * @param ids are the ids to check
     * @return whether the values encountered are all numeric
     */
    public boolean hasNumericAt(String[] filteredInput, int... ids) {

        List<String> tokens = getTokensAt(filteredInput, ids);

        for (String token : tokens) {
            boolean isNumeric = Utilities.isNumeric(token);
            if (! isNumeric) {
                errorMessage = "Expected a numeric value, but found: " + token + "\"";
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the numbers encountered at the supplied ids are integer values. Assumed to
     * have called isNumeric() method first. All ids supplied must be numeric for true to be returned.
     * @param filteredInput is the input after being filtered
     * @param ids are the ids to check
     * @return whether all numbers accounted are integers
     */
    public boolean hasIntegerAt(String[] filteredInput, int... ids) {

        List<String> tokens = getTokensAt(filteredInput, ids);

        for (String token : tokens) {
            boolean isInteger = ! token.contains(".");
            System.out.println(token + " " + isInteger);
            if (! isInteger) {
                errorMessage = "Expected an integer value, but found: \"" + token + "\"";
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the numbers encountered at the supplied ids are positive numbers. Assumed to
     * have called isNumeric() method first. All ids supplied must be numeric for true to be returned.
     * @param filteredInput is the input after being filtered
     * @param ids are the ids to check
     * @return whether all numbers encountered are positive values
     */
    public boolean hasPositiveNumberAt(String[] filteredInput, int... ids) {

        List<String> tokens = getTokensAt(filteredInput, ids);

        for (String token : tokens) {
            double value = Double.parseDouble(token);
            boolean isNegative = value < 0;
            if (isNegative) {
                errorMessage = "Expected a non-negative numeric value, but found: \"" + token + "\"";
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the values encountered at the supplied ids are string values.
     * All ids supplied must be strings for true to be returned.
     * @param filteredInput is the input after being filtered
     * @param ids are the ids to check
     * @return whether the values encountered are all string values
     */
    public boolean hasNonNumericAt(String[] filteredInput, int... ids) {

        List<String> tokens = getTokensAt(filteredInput, ids);

        for (String token : tokens) {
            boolean isNumeric = Utilities.isNumeric(token);
            if (isNumeric) {
                errorMessage = "Expected a string value, but found: \"" + token + "\"";
                return false;
            }
        }

        return true;
    }

    /**
     * Makes sure that an illegal comparison is not performed on a string value. This means that if
     * >, <, >=, <= is encountered, this method makes sure that the next string value present is a date.
     * Eg. SELECT col1 FROM tab1 WHERE col1 > "Steve" will return false because "Steve" is not
     * a date value while SELECT col1 FROM tab1 WHERE col1 > "2020-10-12" will return true.
     * @param filteredInput is the input after being filtered
     * @param comparisonIds are ids containing >, <, >=, <=
     * @param valueIds are ids containing values
     */
    public boolean hasIllegalDateAt(String[] filteredInput, int[] comparisonIds, int[] valueIds) {

        // the comparisons and values are mapped
        List<String> comparisons = getTokensAt(filteredInput, comparisonIds); // =, !=, >, <, >=, <=
        List<String> values = getTokensAt(filteredInput, valueIds);

        for (int i = 0; i < comparisons.size(); i++) {

            boolean isRangeSymbol = Symbol.isRangeSymbol(comparisons.get(i));
            boolean hasDateFormat = Utilities.hasDateFormat(values.get(i));
            boolean isNumeric = Utilities.isNumeric(values.get(i));

            if (! isNumeric) { // ignore values that are numeric (these are fine)
                if (isRangeSymbol && ! hasDateFormat) {
                    errorMessage = "Expected a Date value (YYYY-MM-DD), but found: \"" + values.get(i) + "\"";
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Helper method for isSyntacticallyCorrect() that helps print out a sequence of values that were encountered
     * along the graph traversal as well what the next expected token was supposed to be when the error occurred.
     * @param pointersChildren is the current node that we're on when the error occurred
     * @param token is the erroneous value that was encountered
     * @param debugTokens is a sequence of tokens from the user that have been encountered so far
     * @param debugGraph is the traversal of the graph up until the error occurred
     */
    private void setSyntaxErrorMessage(RuleNode[] pointersChildren, String token, StringBuilder debugTokens, StringBuilder debugGraph) {

        StringBuilder printChildren = new StringBuilder();

        for (RuleNode child : pointersChildren) {
            printChildren.append("\"").append(child.getData()).append("\", ");
        }

        if (printChildren.length() != 0) {
            printChildren.delete(printChildren.length() - 2, printChildren.length() - 1);
        }

        debugGraph.append("ERROR!");

        String[] debugTokensTokens = debugTokens.toString().split("\\s+");
        String[] debugGraphTokens  = debugGraph.toString().split("\\s+");

        debugTokens = new StringBuilder("Tokens: ");
        debugGraph  = new StringBuilder("Graph:  ");

        for (int i = 0; i < debugTokensTokens.length; i++) {

            String debugToken = debugTokensTokens[i];
            String debugGraphToken = debugGraphTokens[i];

            debugTokens.append(debugToken);
            debugGraph.append(debugGraphToken);

            int paddingAmount = Math.abs(debugToken.length() - debugGraphToken.length());

            for (int j = 0; j < paddingAmount; j++) {

                if (debugToken.length() > debugGraphToken.length()) {
                    debugGraph.append(" ");
                } else {
                    debugTokens.append(" ");
                }
            }

            debugTokens.append("->");
            debugGraph.append("->");
        }

        // remove "->"
        debugTokens.delete(debugTokens.length() - 2, debugTokens.length());
        debugGraph.delete(debugGraph.length() - 2, debugGraph.length());

        errorMessage = "Current Token: " + token + "\n" +
                "Expected Token(s): " + printChildren + "\n" +
                "Traversal: " + "\n" +
                debugTokens + "\n" +
                debugGraph;
    }

    /**
     * @return a String representation of the RuleGraph object, basically just an adjacency list
     */
    @Override
    public String toString() {

        StringBuilder adjacencyList = new StringBuilder();

        for (RuleNode rule : rules) {

            adjacencyList.append("\"").append(rule.getData()).append("\": ");
            RuleNode[] children = rule.getChildren();

            for (RuleNode currentChild : children) {
                adjacencyList.append("\"").append(currentChild.getData()).append("\", ");
            }

            // remove last ", "
            adjacencyList.delete(adjacencyList.length() - 2, adjacencyList.length() - 1);
            adjacencyList.append("\n");
        }

        return adjacencyList.toString();
    }
}