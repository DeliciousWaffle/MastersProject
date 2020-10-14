package datastructures.rulegraph;

import datastructures.rulegraph.component.RuleNode;
import enums.Symbol;
import utilities.Utilities;

import java.util.ArrayList;
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
     * @param filteredInput is the input after being filtered
     * @return whether illegal keywords are contained in this input
     */
    public boolean hasIllegalKeyword(String[] filteredInput) {

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for (String token : filteredInput) {

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;
            RuleNode mutableChild = null;

            for (RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                if (isMutable) {
                    mutableChild = child;
                }

                if (rule.equalsIgnoreCase(token) && ! isMutable) {
                    pointer = child;
                    foundChild = true;
                    break;
                }
            }

            if (! foundChild && mutableChild != null) {
                pointer = mutableChild;
            }

            // determine if the input contains a keyword, no want
            boolean isReservedWord = Utilities.isReservedWord(token);
            boolean isMutable = pointer.isMutable();

            if (isReservedWord && isMutable) {
                errorMessage = "Found unexpected Keyword or Symbol: \"" + token + "\"";
                return true;
            }
        }

        return false;
    }
    
    /**
     * Given one or more rule IDs, finds whether duplicate values occur within that list.
     * The IDs must be mutable or this method makes no sense.
     * Eg. SELECT A, A FROM TableName is a taste for what we're searching for
     * @param filteredInput is a query filtered from previous methods that has no major errors
     * @param ids are rules to check
     * @return whether there are duplicate values for one or more rules
     */
    public boolean hasDuplicatesAt(String[] filteredInput, int... ids) {

        List<String> occurrences = new ArrayList<>();

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for (String token : filteredInput) {

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;
            RuleNode mutableChild = null;

            for (RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                if (isMutable) {
                    mutableChild = child;
                }

                if (rule.equalsIgnoreCase(token) && ! isMutable) {
                    pointer = child;
                    foundChild = true;
                    break;
                }
            }

            if (! foundChild && mutableChild != null) {
                pointer = mutableChild;
            }

            // does the current token have any one of the IDs we're searching for?
            for (int id : ids) {
                if (pointer.getId() == id) {
                     occurrences.add(token);
                }
            }
        }

        // checking for duplicates at the particular node
        for (int i = 0; i < occurrences.size() - 1; i++) {

            String current = occurrences.get(i);

            for (int j = i + 1; j < occurrences.size(); j++) {

                if (current.equals(occurrences.get(j))) {
                    StringBuilder debugOccurrences = new StringBuilder();
                    debugOccurrences.append("Duplicate Value: ").append(current).append("\n");
                    debugOccurrences.append("Stored Content:  ");

                    for (String occurrence : occurrences) {
                        debugOccurrences.append("\"").append(occurrence).append("\", ");
                    }

                    debugOccurrences.delete(debugOccurrences.length() - 2, debugOccurrences.length() - 1);
                    errorMessage = debugOccurrences.toString();

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Makes sure that an illegal comparison is not performed on a string value. This means that if
     * >, <, >=, <= is encountered, this method makes sure that the next string value present is a date.
     * Eg. SELECT col1 FROM tab1 WHERE col1 > "Steve" will return false because "Steve" is not
     * a date value while SELECT col1 FROM tab1 WHERE col1 > "10-12-2020" will return true.
     * @param filteredInput is the input after being filtered
     * @param comparisonIds are ids containing >, <, >=, <=
     * @param valueIds are ids containing values
     */
    public boolean hasIllegalDate(String[] filteredInput, int[] comparisonIds, int[] valueIds) {

        // the comparisons and values are mapped
        List<String> comparisons = getTokensAt(filteredInput, comparisonIds); // =, !=, >, <, >=, <=
        List<String> values = getTokensAt(filteredInput, valueIds);

        for (int i = 0; i < comparisons.size(); i++) {

            boolean isRangeSymbol = Symbol.isRangeSymbol(comparisons.get(i));
            boolean hasDateFormat = Utilities.hasDateFormat(values.get(i));
            boolean isNumeric = Utilities.isNumeric(values.get(i));

            if (! isNumeric) { // ignore values that are numeric (these are fine)
                if (isRangeSymbol && ! hasDateFormat) {
                    errorMessage = "Expected a Date value (MM-DD-YYYY), but found: \"" + values.get(i) + "\"";
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks whether or not the token encountered at the supplied id is numeric. Also has
     * an option for checking that the value is an integer as well.
     * @param filteredInput is the input after being filtered
     * @param checkingForIntegerToo true if checking to make sure the value is an integer as well, false otherwise
     * @param targetIds are the ids to check
     * @return whether the input contains numeric values at the supplied target ids
     */
    public boolean hasNumericAt(String[] filteredInput, boolean checkingForIntegerToo, int... targetIds) {

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for (String token : filteredInput) {

            boolean encounteredTarget = false;

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;
            RuleNode mutableChild = null;

            for (RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                if (isMutable) {
                    mutableChild = child;
                }

                if (rule.equalsIgnoreCase(token) && ! isMutable) {
                    pointer = child;
                    foundChild = true;
                    break;
                }
            }

            if (! foundChild && mutableChild != null) {
                pointer = mutableChild;
            }

            for (int id : targetIds) {
                if (id == pointer.getId()) {
                    encounteredTarget = true;
                    break;
                }
            }

            boolean isNumeric = Utilities.isNumeric(token);

            // found a numeric value, don't care if it's an integer or not
            if (encounteredTarget && isNumeric) {

                boolean isInteger = ! token.contains("\\.");

                if (! isInteger && checkingForIntegerToo) {
                    errorMessage = "Expected a whole number, but found: \"" + token + "\"";
                } else {
                    errorMessage = "Found unexpected numeric value at: " + token + "\"";
                }

                return true;
            }
        }

        return false;
    }

    /**
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
     * @return a String representation of the RuleGraph object, basically an adjacency list
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