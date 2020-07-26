package systemcatalog.components;

import datastructure.relation.table.component.Column;
import datastructure.rulegraph.RuleGraph;
import datastructure.tree.querytree.QueryTree;
import datastructure.tree.querytree.operator.*;

import java.util.ArrayList;
import java.util.Arrays;
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

    public QueryTree createQueryTree(RuleGraph queryRuleGraph, String[] queryTokens) {

        QueryTree queryTree = new QueryTree((Operator) null);

        /* 1. get the contents of the select clause and form into either a projection
              or aggregation and set as root for query tree */
        // -------------------------------------------------------------------------------------------------------------

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
        // -------------------------------------------------------------------------------------------------------------

        // getting the input
        aggregationTypes = new ArrayList<>(); // can't use .clear() because it will null out the values in the tree
        aggregationTypes = queryRuleGraph.getTokensAt(queryTokens, 38, 39, 40, 41, 42);
        aggregatedColumnNames = new ArrayList<>();
        aggregatedColumnNames = queryRuleGraph.getTokensAt(queryTokens, 44, 45);
        List<String> symbols = queryRuleGraph.getTokensAt(queryTokens, 47, 48, 49, 50, 51, 52);
        List<String> values = queryRuleGraph.getTokensAt(queryTokens , 53);

        boolean hasAggregateSelection = ! aggregatedColumnNames.isEmpty();

        if(hasAggregateSelection) {
            // creating the aggregate selection and adding it above the root node
            queryTree.add(new ArrayList<>(), QueryTree.Traversal.UP,
                    new AggregateSelection(aggregationTypes, aggregatedColumnNames, symbols, values));
        }

        // TODO doesn't handle OR's at the moment
        /* 3. if there is a WHERE clause, create a selection and place it at the very bottom of the tree so far */
        // -------------------------------------------------------------------------------------------------------------

        // getting the input
        columnNames = new ArrayList<>();
        columnNames = queryRuleGraph.getTokensAt(queryTokens, 23);
        symbols = new ArrayList<>();
        symbols = queryRuleGraph.getTokensAt(queryTokens, 24, 25, 26, 27, 28, 29);
        values = new ArrayList<>();
        values = queryRuleGraph.getTokensAt(queryTokens, 30);

        boolean hasSelection = ! columnNames.isEmpty();

        if(hasSelection) {

            // will place the selection at the very bottom of the tree as the only child
            List<QueryTree.Traversal> traversals = new ArrayList<>();

            // could have 2 children already, if that's the case, traverse down once
            boolean hasTwoChildren = queryTree.getSize() == 2;

            if(hasTwoChildren) {
                traversals.add(QueryTree.Traversal.DOWN);
            }

            // determine if the selection is simple or compound
            boolean isSimpleSelection = columnNames.size() == 1;
            Operator simpleSelection = null;
            Operator compoundSelection = null;

            // add it to the bottom
            if(isSimpleSelection) {
                simpleSelection = new SimpleSelection(columnNames.get(0), symbols.get(0), values.get(0));
                queryTree.add(traversals, QueryTree.Traversal.DOWN, simpleSelection);
            } else {
                compoundSelection = new CompoundSelection(columnNames, symbols, values);
                queryTree.add(traversals, QueryTree.Traversal.DOWN, compoundSelection);
            }
        }

        /* 4. determine if any cartesian products need to be added */
        // -------------------------------------------------------------------------------------------------------------

        // getting the tables and the number of cartesian products needed
        List<String> tableNames = queryRuleGraph.getTokensAt(queryTokens, 14, 17);
        int numCartesianProducts = tableNames.size() - 1;

        // get to the location that we want
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        int numTraversals = queryTree.getSize() - 1;

        for(int i = 0; i < numTraversals; i++) {
            traversals.add(QueryTree.Traversal.DOWN);
        }

        // no cartesian products, just the table
        if(numCartesianProducts == 0) {

            queryTree.add(traversals, QueryTree.Traversal.DOWN, new Relation(tableNames.get(0)));

        // only 1 between 2 tables
        } else if(numCartesianProducts == 1) {

            queryTree.add(traversals, QueryTree.Traversal.DOWN, new CartesianProduct());
            traversals.add(QueryTree.Traversal.DOWN);
            queryTree.add(traversals, QueryTree.Traversal.LEFT, new Relation(tableNames.get(0)));
            queryTree.add(traversals, QueryTree.Traversal.RIGHT, new Relation(tableNames.get(1)));

        // more than 1 cartesian product,
        } else {

            queryTree.add(traversals, QueryTree.Traversal.DOWN, new CartesianProduct());
            traversals.add(QueryTree.Traversal.DOWN);

            for(int i = 0; i < numCartesianProducts; i++) {

                queryTree.add(traversals, QueryTree.Traversal.LEFT, new CartesianProduct());

                // doesn't need this because multiple cartesian products are associative,
                // but the tree output makes more sense if adding relations in reverse
                int indexToGet = tableNames.size() - i - 1;
                queryTree.add(traversals, QueryTree.Traversal.RIGHT, new Relation(tableNames.get(indexToGet)));
                traversals.add(QueryTree.Traversal.LEFT);
            }

            // add final relation to left side, overwrite the cartesian product that's there
            traversals.remove(traversals.size() - 1);
            queryTree.set(traversals, QueryTree.Traversal.LEFT, new Relation(tableNames.get(0)));
        }
System.out.println("Before");
System.out.println(queryTree.getStructure());
        /* 5. if a join using was used, need to set/add the selection node to account for the join criteria */
        // -------------------------------------------------------------------------------------------------------------

        // getting the table names, number of joins used, and the column names to join on
        columnNames = new ArrayList<>();
        symbols = new ArrayList<>();
        values = new ArrayList<>();

        // will need to make sure the mapping is correct which makes things complicated
        boolean canStartMapping = false;

        for(int i = 0; i < queryTokens.length; i++) {

            String queryToken = queryTokens[i];

            if(canStartMapping) {

                // if an exception is thrown, then we're done mapping
                try {

                    String firstJoinTableName = queryTokens[i - 1];
                    String secondJoinTableName = queryTokens[i + 1];
                    String joinOnColumnName = queryTokens[i + 4];

                    if(queryToken.equalsIgnoreCase("JOIN")) {

                        firstJoinTableName = firstJoinTableName + "." + joinOnColumnName;
                        String symbol = "=";
                        secondJoinTableName = secondJoinTableName + "." + joinOnColumnName;

                        columnNames.add(firstJoinTableName);
                        symbols.add(symbol);
                        values.add(secondJoinTableName);
                    }

                } catch(ArrayIndexOutOfBoundsException e) {
                    break;
                }
            }

            // start mapping
            if(queryToken.equalsIgnoreCase("FROM")) {
                canStartMapping = true;
            }

            // finished mapping
            if(queryToken.equalsIgnoreCase("WHERE") || queryToken.equalsIgnoreCase(";")) {
                break;
            }
        }
//System.out.println("Natural Join");
//for(int i = 0; i < columnNames.size(); i++) {
//            System.out.println(columnNames.get(i) + symbols.get(i) + values.get(i));
//        }

        // don't bother with any of this if there is nothing to add
        boolean haveSelectionsToAdd = ! columnNames.isEmpty();

        if(haveSelectionsToAdd) {

            // check if we have a selection already
            boolean foundSelection = false;

            // TODO: change back to break statements once query tree is fixed
            boolean skipOverStuff = false;

            traversals = new ArrayList<>();

            for (Operator operator : queryTree) {

                if (! skipOverStuff) {

                    // found something!
                    if (operator.getType() == Operator.Type.COMPOUND_SELECTION ||
                            operator.getType() == Operator.Type.SIMPLE_SELECTION) {
                        foundSelection = true;
                        skipOverStuff = true;
                    }

                    // went too far!
                    if (operator.getType() == Operator.Type.CARTESIAN_PRODUCT ||
                            operator.getType() == Operator.Type.RELATION) {
                        skipOverStuff = true;
                    }

                    if(! skipOverStuff) {
                        traversals.add(QueryTree.Traversal.DOWN);
                    }
                }
            }

            // if we found a selection and have something to add, will set the node as a compound selection
            if (foundSelection) {

                boolean foundSimpleSelection = queryTree.get(traversals, QueryTree.Traversal.NONE)
                        .getType() == Operator.Type.SIMPLE_SELECTION;

                boolean foundCompoundSelection = queryTree.get(traversals, QueryTree.Traversal.NONE)
                        .getType() == Operator.Type.COMPOUND_SELECTION;

                if (foundSimpleSelection) {

                    columnNames.add(((SimpleSelection)
                            queryTree.get(traversals, QueryTree.Traversal.NONE)).getColumnName());
                    symbols.add(((SimpleSelection)
                            queryTree.get(traversals, QueryTree.Traversal.NONE)).getSymbol());
                    values.add(((SimpleSelection)
                            queryTree.get(traversals, QueryTree.Traversal.NONE)).getValue());

                } else if(foundCompoundSelection) {

                    columnNames.addAll(((CompoundSelection)
                            queryTree.get(traversals, QueryTree.Traversal.NONE)).getColumnNames());
                    symbols.addAll(((CompoundSelection)
                            queryTree.get(traversals, QueryTree.Traversal.NONE)).getSymbols());
                    values.addAll(((CompoundSelection)
                            queryTree.get(traversals, QueryTree.Traversal.NONE)).getValues());

                } else {

                    System.out.println("In Optimizer.createTree()");
                    System.out.println("Didn't find a simple or compound selection");
                    return queryTree;
                }

                // set the selection
                queryTree.set(traversals, QueryTree.Traversal.NONE,
                        new CompoundSelection(columnNames, symbols, values));

            // didn't find a selection, will set the node either as a simple or compound selection
            } else {

                System.out.println(queryTree.get(traversals, QueryTree.Traversal.NONE).getType());

                // traversal should be set at the first cartesian product node
                // determine if the selection to add is simple or compound
                boolean isSimpleSelection = columnNames.size() == 1;

                if (isSimpleSelection) {
                    queryTree.add(traversals, QueryTree.Traversal.UP,
                            new SimpleSelection(columnNames.get(0), symbols.get(0), values.get(0)));
                } else {
                    queryTree.add(traversals, QueryTree.Traversal.UP,
                            new CompoundSelection(columnNames, symbols, values));
                }
            }
        }

        /* 7. if there are any column names in the having clause that are not already in the
              aggregation node, then add them as group by columns there */

        List<String> havingClauseColumnNames = queryRuleGraph.getTokensAt(queryTokens, 35);
        boolean hasHavingColumnNames = ! havingClauseColumnNames.isEmpty();

        if(hasHavingColumnNames) {

            traversals = new ArrayList<>();
            hasAggregateSelection = queryTree.get(traversals, QueryTree.Traversal.NONE)
                    .getType() == Operator.Type.AGGREGATE_SELECTION;

            Aggregation aggregation = null;

            if(hasAggregateSelection) {
                aggregation = ((Aggregation) queryTree.get(traversals, QueryTree.Traversal.DOWN));
            } else {
                aggregation = ((Aggregation) queryTree.get(traversals, QueryTree.Traversal.NONE));
            }

            List<String> groupByColumnNames = aggregation.getGroupByColumnNames();
            boolean doneFindingDuplicates = false;

            while(! doneFindingDuplicates) {

                boolean hasDuplicates = false;

                outerLoop:
                for(int i = 0; i < havingClauseColumnNames.size(); i++) {
                    String havingClauseColumnName = havingClauseColumnNames.get(i);

                    for(int j = 0; j < groupByColumnNames.size(); j++) {
                        String groupByColumnName = groupByColumnNames.get(j);

                        if(havingClauseColumnName.equalsIgnoreCase(groupByColumnName)) {
                            hasDuplicates = true;
                            havingClauseColumnNames.remove(i);
                            break outerLoop;
                        }
                    }
                }

                if(! hasDuplicates) {
                    doneFindingDuplicates = true;
                }
            }

            groupByColumnNames.addAll(havingClauseColumnNames);

            // shouldn't need to set anything because dealing with the reference to groupByColumnNames
        }
//System.out.println("After");
//System.out.println(queryTree.getStructure());
        return queryTree;
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