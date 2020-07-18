package systemcatalog.components;

import datastructure.rulegraph.RuleGraph;
import datastructure.tree.binarytree.BinaryTree;
import datastructure.tree.querytree.operator.Operator;

import java.util.List;

/**
 * Responsible for determining the execution strategy of a query.
 */
public class Optimizer {

    // represents each state of the query tree when applying the optimization heuristic
    private List<BinaryTree<Operator>> queryTreeStates;
    private BinaryTree<Operator> workingTree;

    private RuleGraph queryRuleGraph;
    private String[] query;

    public Optimizer() {
        /*queryTreeStates = new ArrayList<>();
        queryTreeStates.add(initialQueryTree());
        queryTreeStates.add(pushDownSelections());
        queryTreeStates.add(pushDownProjections());
        queryTreeStates.add(formJoins());
        queryTreeStates.add(rearrangeLeafNodes());*/
        // identify pipelines?
        initialQueryTree();
        pushDownSelections();
        pushDownProjections();
        formJoins();
        rearrangeLeafNodes();

    }

    public List<BinaryTree<Operator>> getQueryTreeStates() {
        return queryTreeStates;
    }

    public BinaryTree<Operator> getFinalQueryTreeState() {
        return queryTreeStates.get(3);
    }

    public void setQuery(RuleGraph queryRuleGraph, String[] query) {

    }

    private void initialQueryTree() {

    }

    private void pushDownSelections() {

    }

    private void pushDownProjections() {

    }

    private void formJoins() {

    }

    private void rearrangeLeafNodes() {

    }

    private void copyQueryTree() {

    }
}