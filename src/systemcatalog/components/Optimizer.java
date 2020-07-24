package systemcatalog.components;

import datastructure.relation.table.component.Column;
import datastructure.rulegraph.RuleGraph;
import datastructure.tree.querytree.QueryTree;
import datastructure.tree.querytree.operator.*;

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

    // notation: if has args at the end of something -> coming from the input
    public QueryTree createQueryTree(RuleGraph queryRuleGraph, String[] queryTokens) {

        QueryTree queryTree = new QueryTree((Operator) null);

        /* 1. get the contents of the select clause and form into either a projection
              or aggregation and set as root for query tree */

        // getting the input
        List<String> columnNames = queryRuleGraph.getTokensAt(queryTokens, 1, 2);
        List<String> aggregationTypes = queryRuleGraph.getTokensAt(queryTokens, 3, 4, 5, 6, 7);
        List<String> aggregatedColumnNames = queryRuleGraph.getTokensAt(queryTokens, 9, 10);

        // creating either a projection of aggregation based on the columns
        boolean hasAggregation = ! aggregationTypes.isEmpty();

        // setting the projection or aggregation as the root node
        if (hasAggregation) {
            queryTree.setRoot(new Aggregation(columnNames, aggregationTypes, aggregatedColumnNames));
        } else {
            queryTree.setRoot(new Projection(columnNames));
        }

        // TODO doesn't handle OR's at the moment
        /* 2. if there is a HAVING clause, create an aggregate selection and add it above the root */

        // getting the input
        aggregationTypes.clear();
        aggregationTypes = queryRuleGraph.getTokensAt(queryTokens, 38, 39, 40, 41, 42);
        aggregatedColumnNames.clear();
        aggregatedColumnNames = queryRuleGraph.getTokensAt(queryTokens, 44, 45);
        List<String> symbols = queryRuleGraph.getTokensAt(queryTokens, 47, 48, 49, 50, 51, 52);
        List<String> values = queryRuleGraph.getTokensAt(queryTokens , 53);

        boolean hasAggregateSelection = ! aggregationTypes.isEmpty();

        if(hasAggregateSelection) {
            // creating the aggregate selection and adding it above the root node
            queryTree.add(new ArrayList<>(), QueryTree.Traversal.UP,
                    new AggregateSelection(aggregationTypes, aggregatedColumnNames, symbols, values));
        }

        // TODO doesn't handle OR's at the moment
        /* 3. if there is a WHERE clause, create a selection and place it at the very bottom of the tree so far */

        // getting the input
        columnNames.clear();
        columnNames = queryRuleGraph.getTokensAt(queryTokens, 23);
        symbols.clear();
        symbols = queryRuleGraph.getTokensAt(queryTokens, 24, 25, 26, 27, 28, 29);
        values.clear();
        values = queryRuleGraph.getTokensAt(queryTokens, 30);

        boolean hasSelection = ! columnNames.isEmpty();

        if(hasSelection) {

            // determine if the selection is simple or compound and create it
            boolean isSimpleSelection = columnNames.size() == 1;
            Operator simpleSelection = null;
            Operator compoundSelection = null;

            if (isSimpleSelection) {
                simpleSelection = new SimpleSelection(columnNames.get(0), symbols.get(0), values.get(0));
            } else {
                compoundSelection = new CompoundSelection(columnNames, symbols, values);
            }

            // TODO: where left off
            // place at the very bottom of the tree as the only child
            List<QueryTree.Traversal> traversals = new ArrayList<>();

            // could have 2 children already, if that's the case, travers down once
            boolean hasTwoChildren = queryTree.getSize() == 2;

            if (hasTwoChildren) {
                traversals.add(QueryTree.Traversal.DOWN);
            }

            queryTree.add(traversals, QueryTree.Traversal.DOWN, (CompoundSelection) null);
        }
        /* 4. determine if any cartesian products need to be added */

        // getting the input and the number of cartesian products needed
        List<String> tableInputList = queryRuleGraph.getTokensAt(queryTokens, 14, 17);
        int numCartesianProducts = tableInputList.size() - 1;

        // get corresponding tables from input
        for(Table table : table)

        // create both the cartesian products and relations

        // place these near the bottom of the tree

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