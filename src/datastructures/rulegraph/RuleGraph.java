package datastructures.rulegraph;

import datastructures.rulegraph.component.RuleNode;
import utilities.enums.Keyword;
import utilities.enums.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A graph containing a set of rules that the input must abide by. If the input is syntactically correct, then this
 * class offers methods for extracting data so that it can be used elsewhere in the application. For reference,
 * go to src/files/assets/helpscreen for a collection of images that showcase the syntax of each command possible.
 */
public class RuleGraph {

    public enum Type {

        QUERY(0), CREATE_TABLE(1), ALTER_TABLE(2), DROP_TABLE(3), INSERT(4), DELETE(5),
        UPDATE(6), GRANT(7), REVOKE(8), BUILD_FILE_STRUCTURE(9), REMOVE_FILE_STRUCTURE(10),
        UNKNOWN(-1);

        private final int code;

        Type(int code) {
            this.code = code;
        }

        public int index() {
            return code;
        }
    }

    private ArrayList<RuleNode> rules;

    public RuleGraph() {
        rules = new ArrayList<>();
    }

    public void addRule(String rule, boolean mutable, int id) {
        rules.add(new RuleNode(rule, mutable, id));
    }

    public String getRule(int id) {
        return rules.get(id).getData();
    }

    public boolean isMutable(int id) {
        return rules.get(id).isMutable();
    }

    public void setChildren(int id, int... childrenIDs) {

        RuleNode current = rules.get(id);
        RuleNode[] children = new RuleNode[childrenIDs.length];

        for(int i = 0; i < childrenIDs.length; i++) {

            int childID = childrenIDs[i];
            children[i] = rules.get(childID);
        }

        current.setChildren(children);
    }

    public RuleNode[] getChildren(RuleNode current) {
        return current.getChildren();
    }

    public RuleNode getChild(RuleNode current, int index) {
        return current.getChild(index);
    }

    public boolean hasChildren(RuleNode current) {
        return current.hasChildren();
    }

    /**
     * Given some form of tokenized input and a graph to abide by, returns whether it is syntactically correct.
     * @param tokens a string of text that has been filtered enough to be properly tokenized
     * @return whether the query adheres to the rules applied
     */
    public boolean isSyntacticallyCorrect(String[] tokens) {

        // used for debugging purposes
        StringBuilder debugTokens = new StringBuilder();
        StringBuilder debugGraph  = new StringBuilder();

        // temp node to help with traversal, connect it to the first rule
        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));

        // used for traversal through the graph
        RuleNode pointer = root;

        for(String token : tokens) {

            debugTokens.append(token);

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;

            // search for immutable children first, give precedence to keywords
            RuleNode mutableChild = null;

            for(RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                // save this thing for later
                if(isMutable) {
                    mutableChild = child;
                }

                // found a keyword, we're good to go!
                if(rule.equalsIgnoreCase(token) && ! isMutable) {

                    pointer = child;
                    foundChild = true;
                    debugGraph.append(pointer.getData());
                    break;
                }
            }

            // didn't find an immutable child, see if we found a mutable one
            if(! foundChild && mutableChild != null) {

                pointer = mutableChild;
                foundChild = true;
                debugGraph.append(pointer.getData());
            }

            // didn't find anything, what the heck happened?
            if(! foundChild) {

                printDebugger(pointersChildren, token, debugTokens, debugGraph);
                return false;
            }

            debugTokens.append(" ");
            debugGraph.append(" ");
        }

        //printDebugger(new RuleNode[0], "null", debugTokens, debugGraph);
        return true;
    }

    /**
     * Given one or more rule IDs, finds whether duplicate values occur within that rule set.
     * The IDs must be mutable or this method makes no sense.
     * Eg. SELECT A, A FROM TableName is a taste for what we're searching for
     * @param tokens is a query filtered from previous methods that has no major errors
     * @param ids are rules to check
     * @return whether there are duplicate values for one or more rules
     */
    public boolean hasDuplicatesAt(String[] tokens, int... ids) {

        ArrayList<String> occurrences = new ArrayList<>();

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for(String token : tokens) {

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;
            RuleNode mutableChild = null;

            for(RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                if(isMutable) {
                    mutableChild = child;
                }

                if(rule.equalsIgnoreCase(token) && ! isMutable) {

                    pointer = child;
                    foundChild = true;
                    break;
                }
            }

            if(! foundChild && mutableChild != null) {
                pointer = mutableChild;
            }

            // does the current token have any one of the IDs we're searching for?
            for(int id : ids) {
                if(pointer.getId() == id) {
                    occurrences.add(token);
                }
            }
        }

        // checking for duplicates at the particular node
        for(int i = 0; i < occurrences.size() - 1; i++) {

            String current = occurrences.get(i);

            for(int j = i + 1; j < occurrences.size(); j++) {

                if(current.equals(occurrences.get(j))) {

                    StringBuilder debugOccurrences = new StringBuilder();
                    debugOccurrences.append("Duplicate Value: ").append(current).append("\n");
                    debugOccurrences.append("Stored Content:  ");

                    for(String occurrence : occurrences) {
                        debugOccurrences.append("\"").append(occurrence).append("\", ");
                    }

                    debugOccurrences.delete(debugOccurrences.length() - 2, debugOccurrences.length() - 1);

                    System.out.println("In GraphParser.hasDuplicatesAt()");
                    System.out.println(debugOccurrences.toString());

                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasIllegalKeyword(String[] tokens) {

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for(String token : tokens) {

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;
            RuleNode mutableChild = null;

            for(RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                if(isMutable) {
                    mutableChild = child;
                }

                if(rule.equalsIgnoreCase(token) && ! isMutable) {

                    pointer = child;
                    foundChild = true;
                    break;
                }
            }

            if(! foundChild && mutableChild != null) {
                pointer = mutableChild;
            }

            // determine if the input contains a keyword, no want
            boolean isKeyword = isKeyword(token);
            boolean isMutable = pointer.isMutable();

            if(isKeyword && isMutable) {

                System.out.println("In RuleGraph.hasKeywordInWrongSpot()");
                System.out.println("Keyword: " + token + " does not belong here!");
                return true;
            }
        }

        return false;
    }

    /**
     * Makes sure that the correct data type is being used for an associated keyword. This means that if
     * ">", "<", ">=", "<=" is encountered, make sure the next value present is numeric.
     * Eg. SELECT col1 FROM tab1 WHERE col1 > "Steve" will return false while because "Steve" is not
     * a numeric value while SELECT col1 FROM tab1 WHERE col1 > 17 will return true.
     * @param tokens
     * @param dataTypeId of the rule with the data (this comes after ids)
     * @param operatorIds of the rules that enforce a particular data type (they come before id)
     * @return whether the data type of id matches that of ids
     */
    public boolean hasIllegalValue(String[] tokens, int dataTypeId, int... operatorIds) {

        boolean encounteredComparator = false;

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for(String token : tokens) {

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;
            RuleNode mutableChild = null;

            for(RuleNode child : pointersChildren) {

                String rule = child.getData();
                boolean isMutable = child.isMutable();

                if(isMutable) {
                    mutableChild = child;
                }

                if(rule.equalsIgnoreCase(token) && ! isMutable) {

                    pointer = child;
                    foundChild = true;
                    break;
                }
            }

            if(! foundChild && mutableChild != null) {
                pointer = mutableChild;
            }

            // will be used later
            for(int id : operatorIds) {
                if(id == pointer.getId()) {
                    encounteredComparator = true;
                    break;
                }
            }

            // make sure data type is being enforced
            if((dataTypeId == pointer.getId() && encounteredComparator) ||
                    (dataTypeId == pointer.getId() && operatorIds.length == 0)) {
                try {

                    // will throw an exception if the token can't be converted to a number
                    Double.parseDouble(token);
                    encounteredComparator = false;

                } catch(NumberFormatException e) {

                    System.out.println("In RuleGraph.hasIncorrectDataType()");
                    System.out.println("Expected a number, but found a string");
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param tokens is the tokenized input, assumed to be syntactically correct
     * @param targetIds are the ids to check
     * @return whether the input contains numeric values at the supplied target ids
     */
    public boolean hasNumericAt(String[] tokens, int... targetIds) {

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for(String token : tokens) {

            boolean encounteredTarget = false;

            RuleNode[] pointersChildren = pointer.getChildren();
            boolean foundChild = false;
            RuleNode mutableChild = null;

            for(RuleNode child : pointersChildren) {

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

            if(!foundChild && mutableChild != null) {
                pointer = mutableChild;
            }

            // did we find one of the targets?
            int idEncountered = -1;
            for(int id : targetIds) {
                if(id == pointer.getId()) {
                    encounteredTarget = true;
                    idEncountered = id;
                    break;
                }
            }

            boolean isNumeric = false;

            if(encounteredTarget) {
                try {
                    Double.parseDouble(token);
                    isNumeric = true;
                    System.out.println("In RuleGraph.hasNumericAt()");
                    System.out.println("Found: " + token + " at id " + idEncountered);
                } catch (NumberFormatException e) {
                    isNumeric = false;
                }
            }

            if(isNumeric) {
                return true;
            }
        }

        return false;
    }

    public List<String> getTokensAt(String[] tokens, int... ids) {

        List<String> tokensToGet = new ArrayList<>();

        RuleNode root = new RuleNode("ROOT", false, -1);
        root.setChildren(rules.get(0));
        RuleNode pointer = root;

        for(String token : tokens) {

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
     * Prints an adjacency matrix for this graph. Each adjacent node of a
     * single node represents the possible states that it can transition to.
     */
    public void printRuleSet() {

        StringBuilder ruleSet = new StringBuilder();

        for(RuleNode rule : rules) {

            ruleSet.append("\"").append(rule.getData()).append("\": ");
            RuleNode[] children = rule.getChildren();

            for(RuleNode currentChild : children) {
                ruleSet.append("\"").append(currentChild.getData()).append("\", ");
            }

            // remove last ", "
            ruleSet.delete(ruleSet.length() - 2, ruleSet.length() - 1);
            ruleSet.append("\n");
        }

        System.out.println(ruleSet.toString());
    }

    private boolean isKeyword(String candidate) {

        for(Keyword keyword : Keyword.values()) {
            if(candidate.equalsIgnoreCase(keyword.toString())) {
                return true;
            }
        }

        for(Symbol symbol : Symbol.values()) {
            if(candidate.equalsIgnoreCase(symbol.getSymbol())) {
                return true;
            }
        }

        return false;
    }

    private void printDebugger(RuleNode[] pointersChildren, String token, StringBuilder debugTokens, StringBuilder debugGraph) {

        StringBuilder printChildren = new StringBuilder();

        for(RuleNode child : pointersChildren) {
            printChildren.append("\"").append(child.getData()).append("\", ");
        }

        if(printChildren.length() != 0) {
            printChildren.delete(printChildren.length() - 2, printChildren.length() - 1);
        }

        debugGraph.append("ERROR!");

        String[] debugTokensTokens = debugTokens.toString().split("\\s+");
        String[] debugGraphTokens  = debugGraph.toString().split("\\s+");

        debugTokens = new StringBuilder("Tokens: ");
        debugGraph  = new StringBuilder("Graph:  ");

        for(int i = 0; i < debugTokensTokens.length; i++) {

            String debugToken = debugTokensTokens[i];
            String debugGraphToken = debugGraphTokens[i];

            debugTokens.append(debugToken);
            debugGraph.append(debugGraphToken);

            int paddingAmount = Math.abs(debugToken.length() - debugGraphToken.length());

            for(int j = 0; j < paddingAmount; j++) {

                if(debugToken.length() > debugGraphToken.length()) {
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

        System.out.println("In RuleGraph.isSyntacticallyCorrect()");
        System.out.println("Current Token:      " + token);
        System.out.println("Expected token(s):  " + printChildren.toString());
        System.out.println("Traversal:");
        System.out.println(debugTokens.toString());
        System.out.println(debugGraph.toString());
    }
}