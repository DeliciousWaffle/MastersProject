package systemcatalog.components;

import datastructure.rulegraph.RuleGraph;
import datastructure.tree.querytree.QueryTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for determining the execution strategy of a query. This involves creating a query tree
 * and applying rules to it to lessen the cost of overall execution.
 */
public class Optimizer {

    // represents each state of the query tree when applying the optimization heuristic
    private List<QueryTree> queryTreeStates;

    public Optimizer() {
        queryTreeStates = new ArrayList<>();
    }

    public List<QueryTree> getQueryTreeStates() {
        return queryTreeStates;
    }

    public void optimize(RuleGraph queryRuleGraph, String[] queryTokens) {

        QueryTree workingTree = createQueryTree(queryRuleGraph, queryTokens);
        queryTreeStates.add(new QueryTree(workingTree));

        workingTree = pushDownSelections(workingTree);
        queryTreeStates.add(new QueryTree(workingTree));

        workingTree = pushDownProjections(workingTree);
        queryTreeStates.add(new QueryTree(workingTree));

        workingTree = formJoins(workingTree);
        queryTreeStates.add(new QueryTree(workingTree));

        workingTree = rearrangeLeafNodes(workingTree);
        queryTreeStates.add(new QueryTree(workingTree));

        workingTree = findSubtreesToPipeline(workingTree);
        queryTreeStates.add(new QueryTree(workingTree));
    }

    private QueryTree createQueryTree(RuleGraph queryRuleGraph, String[] queryTokens) {

        // 1. if there is a having clause, set this selection as the root
        // 2. set columns in select clause as projection OR set aggregation as the root
        // 3. if there is a where clause, add it right below the root
        // 4. iterate through the tables in the from clause until everything is added
        //     4a. if no joins, cartesian products
        return null;
    }

    private QueryTree pushDownSelections(QueryTree queryTree) {
        return null;
    }

    private QueryTree pushDownProjections(QueryTree queryTree) {
        return  null;
    }

    private QueryTree formJoins(QueryTree queryTree) {
        return null;
    }

    private QueryTree rearrangeLeafNodes(QueryTree queryTree) {
        return null;
    }

    private QueryTree copyQueryTree(QueryTree queryTree) {
        return null;
    }

    private QueryTree findSubtreesToPipeline(QueryTree queryTree) {
        return null;
    }
}