package systemcatalog.components;

import datastructures.misc.Pair;
import datastructures.misc.Quadruple;
import datastructures.misc.Triple;
import datastructures.querytree.operator.types.*;
import datastructures.relation.resultset.ResultSet;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.FileStructure;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.querytree.QueryTree;
import datastructures.querytree.operator.Operator;
import utilities.OptimizerUtilities;
import utilities.QueryCost;
import utilities.QueryCostToString;
import utilities.Utilities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static datastructures.querytree.operator.Operator.Type.*;
import static datastructures.querytree.QueryTree.TreeTraversal.*;
import static datastructures.querytree.QueryTree.Traversal.*;
import static utilities.OptimizerUtilities.*;

/**
 * Responsible for determining the execution strategy of a query. This involves converting the user's input
 * into relational algebra and converting the relational algebra into a query tree. The query tree is a tree
 * structure representation of a query with each node containing a relational algebra expression. The idea is
 * to use relational algebra axioms and information about the tables referenced to rearrange the nodes in such
 * a way that it reduces the overall cost of execution. This optimization involves multiple steps.
 * Step 1: Break up any selections with conjunctions with a cascade a of selections
 * Step 2: Move these selections as far down the query tree as possible
 * Step 3: Combine cartesian products and selections with a join condition into joins
 * Step 4: Move projections as far down the query tree as possible
 * Step 5: Rearrange these joins such that subtrees with the most restricted references are done first
 * Step 6: Identify subtrees that can be pipelined
 * This class is also responsible for producing naive and optimized relational algebra expressions which help
 * show the user how their query can be further optimized. Additionally, recommendations for file structures
 * will also be generated based on the query. Note: All methods that don't belong to this class and are not
 * prefixed with a class name will belong to the OptimizerUtilities class.
 */
public class Optimizer {

    private final RuleGraph queryRuleGraph;
    private boolean isJoinOptimizationOn;

    public Optimizer() {
        queryRuleGraph = RuleGraphTypes.getQueryRuleGraph();
        isJoinOptimizationOn = true;
    }

    /**
     * Turns the join rearrangement on.
     */
    public void turnOnJoinOptimization() {
        isJoinOptimizationOn = true;
    }

    /**
     * Turns the join rearrangement off.
     */
    public void turnOffJoinOptimization() {
        isJoinOptimizationOn = false;
    }

    public boolean isJoinOptimizationOn() {
        return isJoinOptimizationOn;
    }

    public List<QueryTree> getQueryTreeStates(String[] filteredInput, List<Table> tables) {

        QueryTree queryTree = createQueryTree(filteredInput, tables, new ArrayList<>());
        QueryTree afterCascadingSelections = cascadeSelections(new QueryTree(queryTree));
        QueryTree afterPushingDownSelections = pushDownSelections(new QueryTree(afterCascadingSelections));
        QueryTree afterFormingJoins = formJoins(new QueryTree(afterPushingDownSelections));
        QueryTree afterPushingDownProjections = pushDownProjections(new QueryTree(afterFormingJoins));
        QueryTree afterRearrangingJoins = rearrangeJoins(new QueryTree(afterFormingJoins), filteredInput, tables);
        List<QueryTree> afterPipeliningSubtrees = pipelineSubtrees(new QueryTree(afterPushingDownProjections));

        List<QueryTree> queryTreeStates = new ArrayList<>(Arrays.asList(
                queryTree, afterCascadingSelections, afterPushingDownSelections, afterFormingJoins,
                afterRearrangingJoins, afterPushingDownProjections
        ));

        queryTreeStates.addAll(afterPipeliningSubtrees);

        return queryTreeStates;
    }

    /**
     * Responsible for query tree creation. This involves extracting the user's input and making references
     * to the system tables in order to create relational algebra operators and add them to the tree.
     * For initial query tree creation, tableNames should be an empty list. Otherwise, it is used for creating
     * permutations of possible query trees when rearrangingJoins() is eventually reached.
     * @param input is the user's tokenized input
     * @param tables is a list of system tables
     * @param tableNames should be an empty list if calling for the first time, otherwise used for creating
     * permutations of query trees
     * @return the query tree after creation
     */
    public QueryTree createQueryTree(String[] input, List<Table> tables, List<String> tableNames) {

        // extracting all the data from the input

        // select clause data
        List<String> selectClauseColumnNames = queryRuleGraph.getTokensAt(input, 1, 2);
        List<String> selectClauseAggregationTypes = queryRuleGraph.getTokensAt(input, 3, 4, 5, 6, 7);
        List<String> selectClauseAggregatedColumnNames = queryRuleGraph.getTokensAt(input, 9);

        // from clause data
        if (tableNames.isEmpty()) { // initial query tree creation, extract table data from input
            tableNames = queryRuleGraph.getTokensAt(input, 13, 15, 18);
        }

        List<String> firstJoinOnColumnNames = queryRuleGraph.getTokensAt(input, 20);
        List<String> innerJoinSymbols = queryRuleGraph.getTokensAt(input, 21, 22, 23, 24, 25, 26);
        List<String> secondJoinOnColumnNames = queryRuleGraph.getTokensAt(input, 27);
        int numCartesianProducts = queryRuleGraph.getTokensAt(input, 14, 16).size();

        // where clause data
        List<String> whereClauseColumnNames = queryRuleGraph.getTokensAt(input, 29);
        List<String> whereClauseSymbols = queryRuleGraph.getTokensAt(input, 30, 31, 32, 33, 34, 35);
        List<String> whereClauseValues = queryRuleGraph.getTokensAt(input, 36, 38);

        // group by clause data
        List<String> groupByColumnNames = queryRuleGraph.getTokensAt(input, 43);

        // having clause data
        List<String> havingClauseAggregationTypes = queryRuleGraph.getTokensAt(input, 46, 47, 48, 49, 50);
        List<String> havingClauseColumnNames = queryRuleGraph.getTokensAt(input, 52);
        List<String> havingClauseSymbols = queryRuleGraph.getTokensAt(input, 54, 55, 56, 57, 58, 59);
        List<String> havingClauseValues = queryRuleGraph.getTokensAt(input, 60, 62);

        // the following is just some data cleaning

        List<Table> referencedTables = Utilities.getReferencedTables(tableNames, tables);

        // extract all column names if a "*" is used
        OptimizerUtilities.getColumnNamesFromStar(selectClauseColumnNames, referencedTables);

        // append each column name referenced with it's associated table name
        OptimizerUtilities.prefixColumnNamesWithTableNames(selectClauseColumnNames, referencedTables);
        OptimizerUtilities.prefixColumnNamesWithTableNames(selectClauseAggregatedColumnNames, referencedTables);
        OptimizerUtilities.prefixColumnNamesWithTableNames(firstJoinOnColumnNames, referencedTables);
        OptimizerUtilities.prefixColumnNamesWithTableNames(secondJoinOnColumnNames, referencedTables);
        OptimizerUtilities.prefixColumnNamesWithTableNames(whereClauseColumnNames, referencedTables);
        OptimizerUtilities.prefixColumnNamesWithTableNames(groupByColumnNames, referencedTables);
        OptimizerUtilities.prefixColumnNamesWithTableNames(havingClauseColumnNames, referencedTables);

        // add each join column to where clause column names
        whereClauseColumnNames.addAll(firstJoinOnColumnNames);
        whereClauseSymbols.addAll(innerJoinSymbols);
        whereClauseValues.addAll(secondJoinOnColumnNames);

        // checking to see what will be eventually added to the tree

        boolean hasAggregateSelection = ! havingClauseAggregationTypes.isEmpty();
        boolean hasAggregation = ! selectClauseAggregatedColumnNames.isEmpty();
        boolean hasProjection = ! selectClauseColumnNames.isEmpty() && ! hasAggregation;
        boolean hasCompoundSelection = whereClauseColumnNames.size() >= 2;
        boolean hasSimpleSelection = whereClauseColumnNames.size() == 1;
        boolean hasOneRelation = tableNames.size() == 1;
        boolean hasOneCartesianProduct = numCartesianProducts == 1;
        boolean hasMultipleCartesianProducts = numCartesianProducts >= 2;

        // add each present relational algebra operator to the tree

        QueryTree queryTree = new QueryTree((Operator) null);
        List<QueryTree.Traversal> traversals = new ArrayList<>();

        if (hasAggregateSelection) {
            queryTree.add(traversals, NONE, new AggregateSelection(
                    havingClauseAggregationTypes, havingClauseColumnNames, havingClauseSymbols, havingClauseValues
            ));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        if (hasAggregation) {
            queryTree.add(traversals, NONE, new Aggregation(
                    selectClauseColumnNames, selectClauseAggregationTypes, selectClauseAggregatedColumnNames
            ));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        if (hasProjection) {
            queryTree.add(traversals, NONE, new Projection(selectClauseColumnNames));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        if (hasCompoundSelection) {
            queryTree.add(traversals, NONE, new CompoundSelection(
                    whereClauseColumnNames, whereClauseSymbols, whereClauseValues
            ));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        if (hasSimpleSelection) {
            queryTree.add(traversals, NONE, new SimpleSelection(
                    whereClauseColumnNames.get(0), whereClauseSymbols.get(0), whereClauseValues.get(0)
            ));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        List<Operator> cartesianProducts = Stream.generate(CartesianProduct::new)
                .limit(numCartesianProducts)
                .collect(Collectors.toList());

        List<Operator> relations = tableNames.stream()
                .map(Relation::new)
                .collect(Collectors.toList());

        if (hasOneRelation) {
            queryTree.add(traversals, NONE, relations.get(0));
        }

        if (hasOneCartesianProduct) {
            queryTree.add(traversals, NONE, cartesianProducts.get(0));
            queryTree.add(traversals, LEFT, relations.get(0));
            queryTree.add(traversals, RIGHT, relations.get(1));
        }

        if (hasMultipleCartesianProducts) {
            while (! cartesianProducts.isEmpty()) {
                queryTree.add(traversals, NONE, cartesianProducts.remove(0));
                int lastIndex = relations.size() - 1;
                queryTree.add(traversals, RIGHT, relations.remove(lastIndex));
                traversals.add(LEFT);
            }
            queryTree.add(traversals, NONE, relations.remove(0));
        }

        return queryTree;
    }

    /**
     * Performs the first step in the optimization heuristic which is to find any selections with conjunctions
     * and cascade them into multiple selections. If none are found, just returns the unaltered query tree.
     * @param queryTree is the query tree whose selections with conjunctions will be cascaded
     * @return the query tree after selections have been cascaded
     */
    public QueryTree cascadeSelections(QueryTree queryTree) {

        // if there are no selections with conjunctions, don't bother with this
        boolean hasCompoundSelections = queryTree.getTypeOccurrence(COMPOUND_SELECTION) != 0;

        if (! hasCompoundSelections) {
            return queryTree;
        }

        // get the compound selection, its location, then remove it from the tree
        Map.Entry<Operator, List<QueryTree.Traversal>> compoundSelectionAndLocation =
                queryTree.getFirstOccurrenceOfType(COMPOUND_SELECTION);
        CompoundSelection compoundSelection = (CompoundSelection) compoundSelectionAndLocation.getKey();
        List<QueryTree.Traversal> compoundSelectionsLocation = compoundSelectionAndLocation.getValue();
        queryTree.remove(compoundSelectionsLocation, NONE);

        // get the compound selection's data
        List<String> columnNames = compoundSelection.getColumnNames();
        List<String> symbols = compoundSelection.getSymbols();
        List<String> values = compoundSelection.getValues();

        // create and insert simple selections to the tree (doing in reverse to preserve conjunction ordering)
        int numSimpleSelections = columnNames.size();

        for(int i = numSimpleSelections - 1; i >= 0; i--) {
            Operator simpleSelection = new SimpleSelection(columnNames.get(i), symbols.get(i), values.get(i));
            queryTree.add(compoundSelectionsLocation, NONE, simpleSelection);
        }

        return queryTree;
    }

    /**
     * Performs the second step in the optimization heuristic which is to push down any selections as
     * far down the query tree as possible. If no selections are found, an unaltered query tree is returned.
     * @param queryTree is the query tree whose selections will be pushed down
     * @return query tree after selections have been pushed down
     */
    public QueryTree pushDownSelections(QueryTree queryTree) {

        // don't bother if there are no selections to push down
        boolean hasSimpleSelections = queryTree.getTypeOccurrence(SIMPLE_SELECTION) != 0;

        if (! hasSimpleSelections) {
            return queryTree;
        }

        // also don't bother if there is only 1 relation
        boolean hasOneRelation = queryTree.getTypeOccurrence(RELATION) == 1;

        if (hasOneRelation) {
            return queryTree;
        }

        // getting all simple selections from the query tree
        Map<Operator, List<QueryTree.Traversal>> simpleSelectionsAndLocations =
                queryTree.getOperatorsAndLocationsOfType(SIMPLE_SELECTION, PREORDER);
        List<Operator> simpleSelections = new ArrayList<>(simpleSelectionsAndLocations.keySet());

        // removing each simple selection from the query tree, this will be used as a starting point
        List<List<QueryTree.Traversal>> simpleSelectionLocations =
                new ArrayList<>(simpleSelectionsAndLocations.values());
        List<QueryTree.Traversal> smallestTraversal =
                OptimizerUtilities.getListOfFewestElements(simpleSelectionLocations);

        // removing each simple selection from the query tree
        while (hasSimpleSelections) {
            queryTree.remove(smallestTraversal, NONE);
            hasSimpleSelections = queryTree.getTypeOccurrence(SIMPLE_SELECTION) != 0;
        }

        // get a list of selections that contain join conditions, these will be dealt with later
        List<Operator> selectionsWithJoinConditions =
                OptimizerUtilities.getSelectionsWithJoinConditions(simpleSelections);

        // place the remaining selections right above their respective relations
        Map<Operator, List<QueryTree.Traversal>> relationsAndLocations =
                queryTree.getOperatorsAndLocationsOfType(RELATION, PREORDER);

        for (Operator simpleSelection : simpleSelections) {
            String prefixedTableName = ((SimpleSelection) simpleSelection).getColumnName().split("\\.")[0];
            for (Map.Entry<Operator, List<QueryTree.Traversal>> entry : relationsAndLocations.entrySet()) {
                String tableName = ((Relation) entry.getKey()).getTableName();
                List<QueryTree.Traversal> traversals = entry.getValue();
                if (tableName.equalsIgnoreCase(prefixedTableName)) {
                    queryTree.add(traversals, UP, simpleSelection);
                    break;
                }
            }
        }

        // place the selections with join conditions above cartesian products
        for (Operator simpleSelection : selectionsWithJoinConditions) {
            // changing the traversal locations for each relation, will need to get the updated versions
            relationsAndLocations = queryTree.getOperatorsAndLocationsOfType(RELATION, PREORDER);
            String firstPrefixedTableName = ((SimpleSelection) simpleSelection).getColumnName().split("\\.")[0];
            String secondPrefixedTableName = ((SimpleSelection) simpleSelection).getValue().split("\\.")[0];
            // find each corresponding table name and get their traversals
            List<QueryTree.Traversal> firstTablesLocation = new ArrayList<>();
            List<QueryTree.Traversal> secondTablesLocation = new ArrayList<>();
            for (Map.Entry<Operator, List<QueryTree.Traversal>> entry : relationsAndLocations.entrySet()) {
                String tableName = ((Relation) entry.getKey()).getTableName();
                List<QueryTree.Traversal> traversals = entry.getValue();
                if (tableName.equalsIgnoreCase(firstPrefixedTableName)) {
                    firstTablesLocation = traversals;
                }
                if (tableName.equalsIgnoreCase(secondPrefixedTableName)) {
                    secondTablesLocation = traversals;
                }
            }
            // the relation with the fewest number of traversals is furthest up the tree, which is where
            // we want the join condition placed
            boolean atCartesianProduct = false;
            if (firstTablesLocation.size() < secondTablesLocation.size()) {
                while (! atCartesianProduct) {
                    int lastIndex = firstTablesLocation.size() - 1;
                    firstTablesLocation.remove(lastIndex);
                    atCartesianProduct = queryTree.get(firstTablesLocation, NONE).getType() == CARTESIAN_PRODUCT;
                }
                queryTree.add(firstTablesLocation, UP, simpleSelection);
            } else {
                while (! atCartesianProduct) {
                    int lastIndex = secondTablesLocation.size() - 1;
                    secondTablesLocation.remove(lastIndex);
                    atCartesianProduct = queryTree.get(secondTablesLocation, NONE).getType() == CARTESIAN_PRODUCT;
                }
                queryTree.add(secondTablesLocation, UP, simpleSelection);
            }
        }

        return queryTree;
    }

    /**
     * Performs the third step in the optimization heuristic which is to combine selections with join conditions
     * and cartesian products into joins. If no cartesian products or selections are found,
     * an unaltered query tree is returned.
     * @param queryTree is whose cartesian products and selections containing join conditions
     * will be combined into joins
     * @return the query tree with cartesian products and selections containing join conditions
     * combined into joins
     */
    public QueryTree formJoins(QueryTree queryTree) {

        // don't bother if no cartesian products exist or there are no selections
        boolean hasCartesianProducts = queryTree.getTypeOccurrence(CARTESIAN_PRODUCT) >= 1;
        boolean hasSimpleSelections = queryTree.getTypeOccurrence(SIMPLE_SELECTION) >= 1;

        if (! hasSimpleSelections || ! hasCartesianProducts) {
            return queryTree;
        }

        // for each cartesian product, check above it to see if a selection with a join condition exists
        boolean finishedJoining = false;

        while (! finishedJoining) {

            // cartesian products and their locations will change upon removal of selections
            Map<Operator, List<QueryTree.Traversal>> cartesianProductsAndLocations =
                    queryTree.getOperatorsAndLocationsOfType(CARTESIAN_PRODUCT, PREORDER);

            boolean madeChanges = false;

            for (Map.Entry<Operator, List<QueryTree.Traversal>> entry : cartesianProductsAndLocations.entrySet()) {

                List<QueryTree.Traversal> traversals = entry.getValue();
                boolean isJoin = queryTree.get(traversals, UP).getType() == SIMPLE_SELECTION;

                if (isJoin) {

                    Operator operator = queryTree.get(traversals, UP);
                    SimpleSelection simpleSelection = (SimpleSelection) operator;
                    String firstColumnName = simpleSelection.getColumnName();
                    String symbol = simpleSelection.getSymbol();
                    String secondColumnName = simpleSelection.getValue();

                    InnerJoin innerJoin = new InnerJoin(firstColumnName, symbol, secondColumnName);
                    queryTree.set(traversals, NONE, innerJoin);
                    queryTree.remove(traversals, UP);

                    madeChanges = true;
                    break;
                }
            }

            if (! madeChanges) {
                finishedJoining = true;
            }
        }

        return  queryTree;
    }

    /**
     * Performs the 4th step in the optimization process which is to create projections and push
     * them down the tree. These will be placed right below cartesian products and joins. These
     * projections will only contain the columns that are needed for the end result which means that
     * columns that are not referenced further up the query tree will be omitted. If there exists only
     * one relation, this is completely unnecessary and an unmodified query tree will be returned.
     * @param queryTree is the query tree whose projections will be pushed down
     * @return the query tree after pushing down projections
     */
    public QueryTree pushDownProjections(QueryTree queryTree) {

        // will not need to push down projections if there is only one relation
        boolean hasOneRelation = queryTree.getTypeOccurrence(RELATION) == 1;

        if (hasOneRelation) {
            return queryTree;
        }

        // for each relation, starting from the root, move down the query tree until that relation is reached
        // while adding all referenced column names that appear in the operator nodes along the way
        // the locations of each relation changes while adding new projections, this helps with that problem
        List<Operator> relations =
                new ArrayList<>(queryTree.getOperatorsAndLocationsOfType(RELATION, PREORDER).keySet());

        for (Operator relation : relations) {

            String tableName = ((Relation) relation).getTableName();
            List<QueryTree.Traversal> relationLocation =
                    queryTree.getOperatorsAndLocationsOfType(RELATION, PREORDER).get(relation);
            relationLocation.add(0, NONE); // adding this traversal to include the root node

            List<String> columnNamesForProjection = new ArrayList<>();

            List<QueryTree.Traversal> traversals = new ArrayList<>(); // used for getting to the relation's location
            // start from the root of the query tree and work towards the location of the current relation
            for (QueryTree.Traversal traversal : relationLocation) {
                traversals.add(traversal);
                Operator operator = queryTree.get(traversals, NONE);
                // add the current operators data to the data that will be used for the projection node
                columnNamesForProjection = OptimizerUtilities.addUniqueColumnNames(
                        columnNamesForProjection,
                        OptimizerUtilities.getColumnNamesWithRelationName(
                                operator.getReferencedColumnNames(),
                                tableName
                        )
                );
            }

            // add this new projection right below the first instance of a cartesian product or join
            Projection projection = new Projection(columnNamesForProjection);
            boolean foundCartesianOrJoinAbove = queryTree.get(relationLocation, UP).getType() == CARTESIAN_PRODUCT ||
                    queryTree.get(relationLocation, UP).getType() == INNER_JOIN;
            while (! foundCartesianOrJoinAbove) {
                int lastIndex = relationLocation.size() - 1;
                relationLocation.remove(lastIndex);
                foundCartesianOrJoinAbove = queryTree.get(relationLocation, UP).getType() == CARTESIAN_PRODUCT ||
                        queryTree.get(relationLocation, UP).getType() == INNER_JOIN;
            }
            queryTree.add(relationLocation, UP, projection);
        }

        // after that, will need to add projections between sequential cartesian products and sequential joins, will get
        // the column names referenced in the projections added from earlier which are located to the left and right of
        // cartesian products/joins, removing the join criteria (if join) and add above the cartesian product/join
        // also adds projections right before aggregations too!
        boolean needsBetweenTreatment = queryTree.getTypeOccurrence(CARTESIAN_PRODUCT) +
                queryTree.getTypeOccurrence(INNER_JOIN) >= 2;
        boolean needsProjectionBeforeAggregation =
                (queryTree.getTypeOccurrence(INNER_JOIN) >= 1 && queryTree.getTypeOccurrence(AGGREGATION) == 1) ||
                (queryTree.getTypeOccurrence(CARTESIAN_PRODUCT) >= 1 && queryTree.getTypeOccurrence(AGGREGATION) == 1);

        if (needsBetweenTreatment || needsProjectionBeforeAggregation) {

            List<Operator> cartesianProductsAndJoins = Stream.concat(
                    queryTree.getOperatorsAndLocationsOfType(CARTESIAN_PRODUCT, PREORDER).keySet().stream(),
                    queryTree.getOperatorsAndLocationsOfType(INNER_JOIN, PREORDER).keySet().stream()
            ).collect(Collectors.toList());

            // reversing the order, following algorithm want joins located near the bottom first when processing
            reverseList(cartesianProductsAndJoins);

            for (Operator operator : cartesianProductsAndJoins) {

                // all this mess does is get the current operator's location
                List<QueryTree.Traversal> operatorsLocation = Stream.concat(
                        queryTree.getOperatorsAndLocationsOfType(CARTESIAN_PRODUCT, PREORDER).entrySet().stream(),
                        queryTree.getOperatorsAndLocationsOfType(INNER_JOIN, PREORDER).entrySet().stream()
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).get(operator);

                // avoid producing a redundant projection (occurs if root node is a projection and we're at a join)
                if (queryTree.get(operatorsLocation, UP).getType() == PROJECTION) {
                    continue;
                }

                // get and add all column names from the left and right projections
                List<String> columnNamesToProject = new ArrayList<>();

                columnNamesToProject.addAll(queryTree.get(operatorsLocation, LEFT).getReferencedColumnNames());
                columnNamesToProject.addAll(queryTree.get(operatorsLocation, RIGHT).getReferencedColumnNames());

                // move up the query tree from this position, adding all referenced columns
                List<QueryTree.Traversal> traversingUp = new ArrayList<>(operatorsLocation);
                traversingUp.remove(traversingUp.size() - 1);
                List<String> allReferencedColumnNames = new ArrayList<>();

                while (! traversingUp.isEmpty()) {
                    allReferencedColumnNames = OptimizerUtilities.addUniqueColumnNames(allReferencedColumnNames,
                            queryTree.get(traversingUp, NONE).getReferencedColumnNames());
                    traversingUp.remove(traversingUp.size() - 1);
                }

                // do one more addition at the root to include its columns
                allReferencedColumnNames = OptimizerUtilities.addUniqueColumnNames(allReferencedColumnNames,
                        queryTree.get(new ArrayList<>(), NONE).getReferencedColumnNames());

                // remove from the columns to project columns that do no appear farther up the tree
                boolean doneRemoving = false;
                while (! doneRemoving) {
                    boolean madeChanges = false;
                    for (int i = 0; i < columnNamesToProject.size(); i++) {
                        String columnNameToProject = columnNamesToProject.get(i);
                        boolean foundColumn = false;
                        for (String referencedColumnName : allReferencedColumnNames) {
                            if (columnNameToProject.equalsIgnoreCase(referencedColumnName)) {
                                foundColumn = true;
                                break;
                            }
                        }
                        if (! foundColumn) {
                            columnNamesToProject.remove(i);
                            madeChanges = true;
                            break;
                        }
                    }
                    if (! madeChanges) {
                        doneRemoving = true;
                    }
                }

                Operator projection = new Projection(columnNamesToProject);
                queryTree.add(operatorsLocation, UP, projection);
            }
        }

        return queryTree;
    }

    /**
     * Performs the 5th step in query tree optimization which is to rearrange the ordering of joins
     * such that ones with the most restricted references are executed first in the query tree. This
     * primarily involves finding the query tree that executes the smaller relations first.
     * Performing this action will reduce the write to disk cost
     * when pipelining intermediary subtrees of the query tree, thus, increasing performance.
     * @param queryTree is the query tree to perform the reordering of joins on
     * @param tables are a list of tables in the system that will be referenced to determine the cost
     * of executing a particular subtree and whether a leaf node will need to be rearranged
     * @return the query tree after the leaf nodes have been rearranged
     */
    public QueryTree rearrangeJoins(QueryTree queryTree, String[] input, List<Table> tables) {

        if (!isJoinOptimizationOn) {
            return new QueryTree(queryTree);
        }

        // don't bother if we only have a single table or have 1 or 0 joins
        boolean hasOneRelation = queryTree.getTypeOccurrence(RELATION) == 1;
        boolean hasOneOrLessInnerJoins = queryTree.getTypeOccurrence(INNER_JOIN) <= 1;

        if (hasOneRelation || hasOneOrLessInnerJoins) {
            return queryTree;
        }

        // permuting a list of all possible relation locations which will produce a list of all
        // possible query tree orientations, making calls to previous methods to recreate these new query trees
        List<String> tableNames = queryRuleGraph.getTokensAt(input, 13, 15, 18);
        List<List<String>> permutedTableNames = new ArrayList<>();
        OptimizerUtilities.permuteList(tableNames.size(), tableNames, permutedTableNames);

        List<QueryTree> permutedQueryTrees = new ArrayList<>();

        for (int i = 0; i < permutedTableNames.size(); i++) {
            QueryTree permutedQueryTree = createQueryTree(input, tables, permutedTableNames.get(i));
            permutedQueryTree = cascadeSelections(permutedQueryTree);
            permutedQueryTree = pushDownSelections(permutedQueryTree);
            permutedQueryTree = formJoins(permutedQueryTree);
            permutedQueryTrees.add(permutedQueryTree);
        }

        // filter out query tree permutations that contain cartesian products, these will be inherently worse
        permutedQueryTrees = removeQueryTreesWithOperatorsOfType(permutedQueryTrees, CARTESIAN_PRODUCT);

        // finding the query tree that executes its smallest relations first
        QueryTree smallestPermutedQueryTree = permutedQueryTrees.get(0);
        int smallestBlockingSize = Integer.MAX_VALUE;

        for (QueryTree permutedQueryTree : permutedQueryTrees) {

            // mapping each relation to the number of blocks it contains
            List<Integer> blockSizeOfEachTableInPermutedQueryTree =
                    permutedQueryTree.getOperatorsAndLocationsOfType(RELATION, PREORDER)
                    .keySet()
                    .stream()
                    .map(relation -> ((Relation) relation).getTableName())
                    .map(tableName -> Utilities.getReferencedTable(tableName, tables))
                    .map(table -> {
                        assert table != null;
                        int recordSize = QueryCost.recordSize(table.getColumns());
                        int numRecords = QueryCost.numberRecords(table.getTableData().getData());
                        int blockingFactor = QueryCost.blockingFactor(recordSize);
                        return QueryCost.blocks(numRecords, blockingFactor);
                    })
                    .collect(Collectors.toList());

            // this is not a perfect science, just adding the deepest tables blocks together first
            // and if there are matches, choose the query with the largest table that's executed last
            int leftDeepNodeBlocks = blockSizeOfEachTableInPermutedQueryTree.get(0);
            int rightDeepNodeBlocks = blockSizeOfEachTableInPermutedQueryTree.get(1);

            if (leftDeepNodeBlocks + rightDeepNodeBlocks < smallestBlockingSize) {
                smallestBlockingSize = leftDeepNodeBlocks + rightDeepNodeBlocks;
                smallestPermutedQueryTree = permutedQueryTree;
            }
        }

        return smallestPermutedQueryTree;
    }

    /**
     * Performs the last step in the optimization process which is to pipeline subtrees. These are operations
     * that can be executed all in one step without writing anything out to disk. This may be executed multiple
     * times, thus, multiple query trees can be returned.
     * @param queryTree is the query tree to pipeline
     * @return a list of pipelined query trees
     */
    public List<QueryTree> pipelineSubtrees(QueryTree queryTree) {

        List<QueryTree> pipelinedSubtrees = new ArrayList<>();

        // when a projection is encountered, remove that subtree and set a pipelined expression operator in its place
        // then, add this newly created query tree to the list of pipelined query trees
        // although the size of the query tree will change, the locations of the projections will not
        Map<Operator, List<QueryTree.Traversal>> projectionsAndLocations =
                new LinkedHashMap<>(queryTree.getOperatorsAndLocationsOfType(PROJECTION, POSTORDER));

        int numSubscripts = 0; // used to keep track of the pipelined expressions

        for (Map.Entry<Operator, List<QueryTree.Traversal>> entry : projectionsAndLocations.entrySet()) {

            List<QueryTree.Traversal> projectionLocation = entry.getValue();
            List<Operator> pipelinedOperators = queryTree.removeSubtree(projectionLocation);

            Operator pipelinedExpression = new PipelinedExpression(pipelinedOperators, numSubscripts);
            numSubscripts++;

            queryTree.add(projectionLocation, NONE, pipelinedExpression);
            pipelinedSubtrees.add(new QueryTree(queryTree));
        }

        // take care of aggregation/aggregate selection
        boolean hasAggregation = queryTree.getTypeOccurrence(AGGREGATION) == 1;

        if (hasAggregation) {
            List<Operator> pipelinedExpressions = queryTree.removeSubtree(NO_TRAVERSALS);
            Operator pipelinedExpression = new PipelinedExpression(pipelinedExpressions, numSubscripts);
            queryTree.add(NO_TRAVERSALS, NONE, pipelinedExpression);
            pipelinedSubtrees.add(new QueryTree(queryTree));
        }

        return pipelinedSubtrees;
    }

    /**
     * After the user enters some form of input, this returns the equivalent relational algebra
     * without making any kinds of optimization changes. Extracts this data from the query tree
     * that is originally produced from the input.
     * @param queryTreeStates is a list of query tree states produced after the optimization process
     * @return the unoptimized relational algebra extracted from this query tree
     */
    public String getNaiveRelationAlgebra(List<QueryTree> queryTreeStates) {

        QueryTree initialQueryTree = new QueryTree(queryTreeStates.get(0));
        removePrefixedColumnNamesFromQueryTrees(initialQueryTree);

        StringBuilder naiveRelationalAlgebra = new StringBuilder();
        StringBuilder closingBrackets = new StringBuilder();
        Deque<String> cartesianProducts = new ArrayDeque<>();

        List<Operator> operators = new ArrayList<>(initialQueryTree.getOperatorsAndLocations(PREORDER).keySet());

        for (Operator operator : operators) {
            if (operator.getType() == CARTESIAN_PRODUCT) {
                cartesianProducts.push(operator.toString());
            } else {
                if (operator.getType() == RELATION) {
                    naiveRelationalAlgebra.append(operator).append(" ");
                    if (! cartesianProducts.isEmpty()) {
                        naiveRelationalAlgebra.append(cartesianProducts.pop()).append(" ");
                    } else {
                        naiveRelationalAlgebra.deleteCharAt(naiveRelationalAlgebra.length() - 1);
                    }
                } else {
                    naiveRelationalAlgebra.append(operator.toString()).append(" [");
                    closingBrackets.append("]");
                }
            }
        }

        naiveRelationalAlgebra.append(closingBrackets);

        String toReturn = naiveRelationalAlgebra.toString();

        // if the size is very long, break up into multiple lines
        if (toReturn.length() > 75) {
            toReturn = toReturn.replaceAll("\\[", "\n\t[");
        }

        return toReturn;
    }

    /**
     * When the user enters input, this returns the equivalent optimized relational algebra.
     * @param queryTreeStates is a list of query tree states produced after the optimization process
     * @return optimized relational algebra
     */
    public String getOptimizedRelationalAlgebra(List<QueryTree> queryTreeStates) {

        // get the query tree before pipelining
        QueryTree temp = new QueryTree(queryTreeStates.get(5));
        // remove any unnecessary prefixing
        removePrefixedColumnNamesFromQueryTrees(temp);
        // getting the pipelined query trees
        List<QueryTree> pipelinedQueryTrees = pipelineSubtrees(temp);

        StringBuilder optimizedRelationalAlgebra = new StringBuilder();


        // for each pipelined query tree, add all pipelined expressions that appear to this list
        List<PipelinedExpression> pipelinedExpressions = new ArrayList<>();

        for (QueryTree pipelinedQueryTree : pipelinedQueryTrees) {
            pipelinedExpressions.addAll(
                    pipelinedQueryTree.getOperatorsAndLocationsOfType(PIPELINED_EXPRESSION, PREORDER)
                            .keySet()
                            .stream()
                            .map(operator -> (PipelinedExpression) operator)
                            .collect(Collectors.toList())
            );
        }

        // remove duplicate pipelined expressions
        pipelinedExpressions = pipelinedExpressions.stream()
                .distinct()
                .collect(Collectors.toList());

        // appending to string builder
        pipelinedExpressions.forEach(pipelinedExpression -> {
            String toAppend = pipelinedExpression.getRelationalAlgebra();
            if (toAppend.length() > 75) {
                toAppend = toAppend.replaceAll("\\[", "\n\t[");
            }
            optimizedRelationalAlgebra.append(toAppend).append("\n");
        });

        // remove "\n"
        if (optimizedRelationalAlgebra.length() != 0) {
            optimizedRelationalAlgebra.deleteCharAt(optimizedRelationalAlgebra.length() - 1);
        }

        // remove "P = " junk
        if (pipelinedQueryTrees.size() == 1) {
            optimizedRelationalAlgebra.delete(0, 6);
        }

        return optimizedRelationalAlgebra.toString();
    }

    /**
     * After execution of a query, recommends possible file structures that can be built on columns in
     * tables in order to reduce query costs. These file structures include secondary b-trees,
     * clustered b-trees, hash tables, and clustered files. Secondary b-trees are a jack of all trades and
     * are used if there is a conflict between a clustered b-tree and hash table. Clustered b-trees
     * perform best for range queries while hash tables are best for simple selections. Clustered files
     * are typically used to speed up joins, but incur a high storage cost and can only be built on tables.
     * They also prevent the other file structures from being built within these tables.
     * @param queryTreeStates is a list of query tree states produced after the optimization process
     * @param tables are the system tables (used if the verifier is on)
     * @param isVerifierOn is whether the verifier is on, if off, will never recommend to build clustered files
     * because it is uncertain if the tables exist
     * @return a recommendation of file structures to build for a particular query (a list of items consisting of the
     * a table name, column name, and file structure to build; a list of two tables that are clustered together)
     */
    public Pair<List<Triple<String, String, String>>, List<Pair<String, String>>> getRecommendedFileStructures(
            List<QueryTree> queryTreeStates, List<Table> tables, boolean isVerifierOn) {

        QueryTree queryTreeBeforePipelining = queryTreeStates.get(5);

        Pair<List<Triple<String, String, String>>, List<Pair<String, String>>> recommendedFileStructures =
                new Pair<>(getRecommendedFileStructuresWithoutVerifier(queryTreeBeforePipelining), new ArrayList<>());

        if (isVerifierOn) {
            return getRecommendedFileStructuresWithVerifier(recommendedFileStructures.getFirst(), queryTreeStates,
                    tables);
        }

        return recommendedFileStructures;
    }

    /**
     * Helper method that returns the recommended file structures without the verifier. Simply looks at the symbols
     * used on a selection/inner join and suggests a file structure, resolving conflicts with b-trees. Will never
     * recommend clustered files because the verifier is off, meaning we can't calculate how well a clustered file
     * will perform. Also won't recommend "no file structure" as an option because of the same reason.
     * @param queryTreeBeforePipelining is self explanatory
     * @return recommended file structures when verifier is off
     */
    private List<Triple<String, String, String>> getRecommendedFileStructuresWithoutVerifier(
            QueryTree queryTreeBeforePipelining) {

        // a list that contains triplets which are composed of table name, column name, and the file structure to use
        List<Triple<String, String, String>> recommendedFileStructures = new ArrayList<>();

        // will build file structures on columns referenced in where clause first
        List<SimpleSelection> simpleSelections =
                queryTreeBeforePipelining.getOperatorsAndLocationsOfType(SIMPLE_SELECTION, PREORDER)
                        .keySet()
                        .stream()
                        .map(operator -> (SimpleSelection) operator)
                        .collect(Collectors.toList());

        for (SimpleSelection simpleSelection : simpleSelections) {

            String[] tokens = simpleSelection.getColumnName().split("\\."); // will be prefixed with a table name
            String tableName = tokens[0];
            String columnName = tokens[1];
            String fileStructure = "";
            String symbol = simpleSelection.getSymbol();

            if (symbol.equalsIgnoreCase("=") || symbol.equalsIgnoreCase("!=")) {
                fileStructure = "Hash Table";
            } else { // >, <, >=, <=
                fileStructure = "Clustered B-Tree";
            }

            Triple<String, String, String> recommendedFileStructure =
                    new Triple<>(tableName, columnName, fileStructure);

            // if there's a conflict, don't add, but instead replace the existing recommendation with a secondary b-tree
            int replaceIndex = getFileStructureConflictLocation(recommendedFileStructure, recommendedFileStructures);
            boolean hasConflict = replaceIndex != -1;

            if (hasConflict) {
                recommendedFileStructure = new Triple<>(tableName, columnName, "Secondary B-Tree");
                recommendedFileStructures.set(replaceIndex, recommendedFileStructure);
            } else {
                recommendedFileStructures.add(recommendedFileStructure);
            }
        }

        // suggest file structures to build on for joins (either secondary/clustered b-tree, no support for hash tables
        // and clustered files are computed later if verifier is on)
        List<InnerJoin> innerJoins =
                queryTreeBeforePipelining.getOperatorsAndLocationsOfType(INNER_JOIN, PREORDER)
                        .keySet()
                        .stream()
                        .map(operator -> (InnerJoin) operator)
                        .collect(Collectors.toList());

        for (InnerJoin innerJoin : innerJoins) {

            String firstJoinColumn = innerJoin.getFirstJoinColumnName();
            String secondJoinColumn = innerJoin.getSecondJoinColumnName();
            String firstTableName = firstJoinColumn.split("\\.")[0];
            String secondTableName = secondJoinColumn.split("\\.")[0];
            String firstColumnName = firstJoinColumn.split("\\.")[1];
            String secondColumnName = firstJoinColumn.split("\\.")[1];
            String firstFileStructure = "";
            String secondFileStructure = "";
            String symbol = innerJoin.getSymbol();

            if (symbol.equalsIgnoreCase("=") || symbol.equalsIgnoreCase("!=")) {
                firstFileStructure = "Secondary B-Tree";
                secondFileStructure = "Secondary B-Tree";
            } else { // >, <, >=, <=
                firstFileStructure = "Clustered B-Tree";
                secondFileStructure = "Clustered B-Tree";
            }

            Triple<String, String, String> firstRecommendedFileStructure =
                    new Triple<>(firstTableName, firstColumnName, firstFileStructure);

            int replaceIndex =
                    getFileStructureConflictLocation(firstRecommendedFileStructure, recommendedFileStructures);
            boolean hasConflict = replaceIndex != -1;

            if (hasConflict) {
                firstRecommendedFileStructure = new Triple<>(firstTableName, firstColumnName, "Secondary B-Tree");
                recommendedFileStructures.set(replaceIndex, firstRecommendedFileStructure);
            } else {
                recommendedFileStructures.add(firstRecommendedFileStructure);
            }

            Triple<String, String, String> secondRecommendedFileStructure =
                    new Triple<>(secondTableName, secondColumnName, secondFileStructure);

            replaceIndex =
                    getFileStructureConflictLocation(secondRecommendedFileStructure, recommendedFileStructures);
            hasConflict = replaceIndex != -1;

            if (hasConflict) {
                secondRecommendedFileStructure =
                        new Triple<>(secondTableName, secondColumnName, "Secondary B-Tree");
                recommendedFileStructures.set(replaceIndex, secondRecommendedFileStructure);
            } else {
                recommendedFileStructures.add(secondRecommendedFileStructure);
            }
        }

        return distinctFileStructures(recommendedFileStructures);
    }

    /**
     * Helper method that returns the recommended file structures with the verifier. Will use the above method as
     * a starting place. Main difference is that clustered files could be recommended as well as the option for
     * "no file structure" as a recommendation.
     * @param recommendedFileStructures are the file structures recommended so far based on the verifier being off
     * @param queryTreeStates are the states of the query tree
     * @param tables are the tables of the system
     * @return recommended file structures when verifier is on
     */
    private Pair<List<Triple<String, String, String>>, List<Pair<String, String>>>
        getRecommendedFileStructuresWithVerifier(List<Triple<String, String, String>> recommendedFileStructures,
                                                 List<QueryTree> queryTreeStates, List<Table> tables) {

        // check each file structure recommended to see if not building a file structure will yield a better result
        for (int i = 0; i < recommendedFileStructures.size(); i++) {

            Triple<String, String, String> recommendedFileStructure = recommendedFileStructures.get(i);

            // create a deep copy of the system tables, apply the file structure for the given table, and see if
            // the cost of producing the query tree is lower than with the file structure
            List<Table> tablesCopy = tables
                    .stream()
                    .map(Table::new)
                    .collect(Collectors.toList());

            removeAllFileStructures(tablesCopy);

            String tableName = recommendedFileStructure.getFirst();
            String columnName = recommendedFileStructure.getSecond();
            String fileStructureString = recommendedFileStructure.getThird();

            FileStructure fileStructure = FileStructure.NONE;

            switch (fileStructureString) {
                case "Secondary B-Tree":
                    fileStructure = FileStructure.SECONDARY_B_TREE;
                    break;
                case "Clustered B-Tree":
                    fileStructure = FileStructure.CLUSTERED_B_TREE;
                    break;
                case "Hash Table":
                    fileStructure = FileStructure.HASH_TABLE;
                    break;
            }

            Table referencedTable = Utilities.getReferencedTable(tableName, tablesCopy);
            assert referencedTable != null;
            Column referencedColumn = referencedTable.getColumn(columnName);
            assert referencedColumn != null;
            referencedColumn.setFileStructure(fileStructure);

            // with file structure built
            int productionCostWithFileStructure =
                    getCostAnalysis(queryTreeStates, tablesCopy, true).getFirst();

            // without file structure built
            referencedColumn.setFileStructure(FileStructure.NONE);
            int productionCostWithoutFileStructure =
                    getCostAnalysis(queryTreeStates, tablesCopy, true).getFirst();

            if (productionCostWithFileStructure > productionCostWithoutFileStructure) {
                System.out.println("Changing " + recommendedFileStructure + " to no file structure"); // TODO remove me
                System.out.println("Production Cost With: " + productionCostWithFileStructure + " without: " + productionCostWithoutFileStructure);
                recommendedFileStructures.set(i, new Triple<>(tableName, columnName, "None"));
            }
        }

        // check to see if clustering the two tables would perform better than the previous file structure
        // recommendations, in order to do this, will need to calculate the total cost of the query tree with the
        // file structures already built and compare them to each possible clustered file query tree cost

        // copying the tables and building all recommended file structures
        List<Table> tablesCopy = tables
                .stream()
                .map(Table::new)
                .collect(Collectors.toList());

        removeAllFileStructures(tablesCopy);

        for (Triple<String, String, String> recommendedFileStructure : recommendedFileStructures) {

            String tableName = recommendedFileStructure.getFirst();
            String columnName = recommendedFileStructure.getSecond();
            String fileStructureString = recommendedFileStructure.getThird();

            FileStructure fileStructure = FileStructure.NONE;

            switch (fileStructureString) {
                case "Secondary B-Tree":
                    fileStructure = FileStructure.SECONDARY_B_TREE;
                    break;
                case "Clustered B-Tree":
                    fileStructure = FileStructure.CLUSTERED_B_TREE;
                    break;
                case "Hash Table":
                    fileStructure = FileStructure.HASH_TABLE;
                    break;
                case "None":
                    fileStructure = FileStructure.NONE;
            }

            Table referencedTable = Utilities.getReferencedTable(tableName, tablesCopy);
            assert referencedTable != null;
            Column referencedColumn = referencedTable.getColumn(columnName);
            assert referencedColumn != null;
            referencedColumn.setFileStructure(fileStructure);
        }

        int productionCostWithoutClustering = getCostAnalysis(queryTreeStates, tablesCopy, true).getFirst();

        // getting all possible table pairs (does not include duplicates like <T1, T2> and <T2, T1>)
        List<Pair<String, String>> allPossibleTablePairs = getAllPossibleTablePairs(tables
                .stream()
                .map(Table::getTableName)
                .collect(Collectors.toList())
        );

        // what table pairs to keep, if any
        List<Pair<String, String>> clusteredTables = new ArrayList<>();

        // the mapped cost of each clustered table (if there is any), will be used to resolve conflicts like
        // <T1, T2> and <T2, T3> (T2 is shared which is big no no, choose smallest)
        List<Integer> clusteredTablesCost = new ArrayList<>();

        for (Pair<String, String> tablePair : allPossibleTablePairs) {

            removeAllFileStructures(tablesCopy);

            String firstTableName = tablePair.getFirst();
            String secondTableName = tablePair.getSecond();
            Table firstTable = Utilities.getReferencedTable(firstTableName, tablesCopy);
            Table secondTable = Utilities.getReferencedTable(secondTableName, tablesCopy);

            assert firstTable != null && secondTable != null;

            firstTable.setClusteredWith(secondTableName);
            secondTable.setClusteredWith(firstTableName);

            int productionCostWithClustering = getCostAnalysis(queryTreeStates, tablesCopy, true).getFirst();

            if (productionCostWithClustering < productionCostWithoutClustering) {
                clusteredTables.add(tablePair);
                clusteredTablesCost.add(productionCostWithClustering);
            }
        }

        // if there are any conflicts (the <T1, T2> and <T2, T3> thing) choose the one with the lowest cost
        List<Pair<String, String>> clusteredTablesToKeep = new ArrayList<>();

        for (int i = 0; i < clusteredTables.size(); i++) {

            Pair<String, String> tablePair = clusteredTables.get(i);
            int costOfTablePair = clusteredTablesCost.get(i);

            for (int j = 0; j < clusteredTables.size(); j++) {

                if (i == j) {
                    continue;
                }

                Pair<String, String> tablePairToCheck = clusteredTables.get(j);
                int costOfPairToCheck = clusteredTablesCost.get(j);

                boolean hasConflict = tablePair.getFirst().equalsIgnoreCase(tablePairToCheck.getFirst()) ||
                        tablePair.getFirst().equalsIgnoreCase(tablePairToCheck.getSecond()) ||
                        tablePair.getSecond().equalsIgnoreCase(tablePairToCheck.getFirst()) ||
                        tablePair.getSecond().equalsIgnoreCase(tablePairToCheck.getSecond());

                if (hasConflict) {
                    if (costOfTablePair < costOfPairToCheck) {
                        clusteredTablesToKeep.add(tablePair);
                    } else {
                        clusteredTablesToKeep.add(tablePairToCheck);
                    }
                }
            }

        }

        // may have duplicates?
        clusteredTables = clusteredTablesToKeep.stream().distinct().collect(Collectors.toList());

        return new Pair<>(recommendedFileStructures, clusteredTables);
    }

    /**
     * Returns a lot of information with respect to how the cost of the query tree is calculated. This includes
     * the total execution cost, the total write to disk cost, and a string containing info about how these
     * costs were generated. Essentially, shows the work that was done to get these costs.
     * @param queryTreeStates is a list of query tree states produced after the optimization process
     * @param tables are the tables of the system
     * @param isVerifierOn is whether the verifier is on, if off, will simply return because we can't retrieve data
     * from tables that we don't know exist
     * @return the total production cost, the total write to disk cost, and a string showing the work to get to
     * the total production cost, and a string showing the work to get to the write to disk cost
     */
    public Quadruple<Integer, Integer, String, String> getCostAnalysis(List<QueryTree> queryTreeStates,
                                                                       List<Table> tables, boolean isVerifierOn) {

        if (! isVerifierOn) {
            return new Quadruple<>(0, 0, "", "");
        }

        QueryTree queryTreeBeforePipelining = queryTreeStates.get(5);

        // the stuff to return
        int totalProductionCost = 0;
        int totalWriteToDiskCost = 0;
        StringBuilder productionCostWork = new StringBuilder();
        StringBuilder writeToDiskCostWork = new StringBuilder();

        // used for producing result sets
        Deque<Operator> operatorStack =
                setToDeque(queryTreeBeforePipelining.getOperatorsAndLocations(PREORDER).keySet());
        Deque<ResultSet> workingOperatorStack = new ArrayDeque<>();

        // makes sure that the pipelined nodes are not getting mixed up
        Deque<String> pipelinedProductionCostWorkStack = new ArrayDeque<>();
        Deque<String> pipelinedWriteToDiskCostWorkStack = new ArrayDeque<>();
        int subscript = 0;

        // production costs may change depending on the node encountered while going through a pipelined subtree
        // write to disk cost doesn't need this as it is recorded as soon as a projection (not the last one) is found
        StringBuilder pipelinedProductionCostWork = new StringBuilder();
        int pipelinedProductionCost = 0;

        while (! operatorStack.isEmpty()) {

            Operator operator = operatorStack.pop();

            switch (operator.getType()) {
                case RELATION: {

                    Relation relation = (Relation) operator;
                    String tableName = relation.getTableName();
                    Table table = Utilities.getReferencedTable(tableName, tables);
                    assert table != null;
                    ResultSet resultSet = new ResultSet(table);
                    workingOperatorStack.push(resultSet);

                    // get the table's information (will likely get overwritten if a selection, inner join,
                    // or cartesian product is encountered
                    int recordSize = QueryCost.recordSize(resultSet.getColumns()); // |r|
                    int numRecords = QueryCost.numberRecords(resultSet.getData()); // r
                    int blockingFactor = QueryCost.blockingFactor(recordSize); // bf
                    int blocks = QueryCost.blocks(numRecords, blockingFactor); // b

                    pipelinedProductionCost = blocks;

                    pipelinedProductionCostWork.append(QueryCostToString.recordSize(resultSet.getColumns()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.numberRecords(resultSet.getData()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blockingFactor(recordSize)).append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blocks(numRecords, blockingFactor))
                            .append("\n");

                    break;
                }
                case SIMPLE_SELECTION: {

                    SimpleSelection simpleSelection = (SimpleSelection) operator;
                    String columnName = simpleSelection.getColumnName();
                    String symbol = simpleSelection.getSymbol();
                    String value = simpleSelection.getValue();
                    ResultSet resultSet = workingOperatorStack.pop();
                    workingOperatorStack.push(resultSet.selection(columnName, symbol, value));

                    // get the data from the result set before applying the projection, this will overwrite any data
                    // that is found in working production val and working production cost
                    Column column = resultSet.getColumnFromColumnName(columnName);
                    FileStructure fileStructure = column.getFileStructure();

                    boolean isUniqueValue = resultSet.getColumnDataAt(resultSet.getColumnFromColumnName(columnName))
                            .stream()
                            .filter(v -> v.equalsIgnoreCase(value))
                            .count() <= 1;

                    // general calculations
                    int recordSize = QueryCost.recordSize(resultSet.getColumns()); // |r|
                    int numRecords = resultSet.getNumRows(); // r
                    int blockingFactor = QueryCost.blockingFactor(recordSize); // bf
                    int blocks = QueryCost.blocks(numRecords, blockingFactor); // b

                    // b-tree specific calculations
                    int keySize = resultSet.getColumnFromColumnName(columnName).size(); // |r| of column
                    int degree = QueryCost.degree(keySize); // m
                    int levels = QueryCost.levels(numRecords, degree); // l
                    int terminalLevelNodes = QueryCost.terminalLevelNodes(numRecords, degree); // bl
                    int numDistinctValues = // d
                            (int) resultSet.getColumnDataAt(resultSet.getColumnFromColumnName(columnName))
                            .stream()
                            .distinct()
                            .count();
                    int selectivity = QueryCost.selectivity(numRecords, numDistinctValues); // s

                    pipelinedProductionCostWork = new StringBuilder();
                    pipelinedProductionCostWork.append(QueryCostToString.recordSize(resultSet.getColumns()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.numberRecords(resultSet.getData()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blockingFactor(recordSize)).append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blocks(numRecords, blockingFactor))
                            .append("\n");
                    pipelinedProductionCostWork.append("d = ").append(numDistinctValues).append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.selectivity(numRecords, numDistinctValues))
                            .append("\n");

                    // production cost will be dependent on the type of file structure currently built on the column
                    switch (fileStructure) {
                        case NONE: {
                            if (symbol.equalsIgnoreCase("=") || symbol.equalsIgnoreCase("!=")) {
                                if (isUniqueValue) {
                                    pipelinedProductionCost = QueryCost.unsortedUnique(blocks);
                                    pipelinedProductionCostWork.append(QueryCostToString.unsortedUnique(blocks))
                                            .append("\n");
                                } else {
                                    pipelinedProductionCost = QueryCost.unsortedNonUnique(blocks);
                                    pipelinedProductionCostWork
                                            .append(QueryCostToString.unsortedNonUnique(blocks)).append("\n");
                                }
                            } else { // >, <, >=, <=
                                pipelinedProductionCost = QueryCost.unsortedRange(blocks);
                                pipelinedProductionCostWork.append(QueryCostToString.unsortedRange(blocks))
                                        .append("\n");
                            }
                            break;
                        }
                        case SECONDARY_B_TREE: {

                            pipelinedProductionCostWork.append("\n");
                            pipelinedProductionCostWork.append("Key Size = ").append(keySize).append("\n");
                            pipelinedProductionCostWork.append(QueryCostToString.degree(keySize)).append("\n");
                            pipelinedProductionCostWork.append(QueryCostToString.levels(numRecords, degree))
                                    .append("\n");
                            pipelinedProductionCostWork.append(QueryCostToString.terminalLevelNodes(numRecords, degree))
                                    .append("\n");
                            pipelinedProductionCostWork.append("d = ").append(numDistinctValues).append("\n");
                            pipelinedProductionCostWork.append(QueryCostToString.selectivity(numRecords,
                                    numDistinctValues))
                                    .append("\n\n");

                            if (symbol.equalsIgnoreCase("=") || symbol.equalsIgnoreCase("!=")) {
                                if (isUniqueValue) {
                                    pipelinedProductionCost = QueryCost.secondaryBTreeUnique(levels);
                                    pipelinedProductionCostWork.append(QueryCostToString.secondaryBTreeUnique(levels))
                                            .append("\n");
                                } else {
                                    pipelinedProductionCost = QueryCost.secondaryBTreeNonUnique(levels, degree,
                                            selectivity);
                                    pipelinedProductionCostWork.append(QueryCostToString.secondaryBTreeNonUnique(
                                            levels, degree, selectivity)).append("\n");
                                }
                            } else { // >, <, >=, <=
                                pipelinedProductionCost = QueryCost.secondaryBTreeRange(levels, terminalLevelNodes,
                                        numRecords);
                                pipelinedProductionCostWork.append(QueryCostToString.secondaryBTreeRange(
                                        levels, terminalLevelNodes, numRecords)).append("\n");
                            }
                            break;
                        }
                        case CLUSTERED_B_TREE: {

                            pipelinedProductionCostWork.append("\n");
                            pipelinedProductionCostWork.append("Key Size = ").append(keySize).append("\n");
                            pipelinedProductionCostWork.append(QueryCostToString.degree(keySize)).append("\n");
                            pipelinedProductionCostWork.append(QueryCostToString.levels(numRecords, degree))
                                    .append("\n");
                            pipelinedProductionCostWork.append(QueryCostToString.terminalLevelNodes(numRecords, degree))
                                    .append("\n");
                            pipelinedProductionCostWork.append("d = ").append(numDistinctValues).append("\n");
                            pipelinedProductionCostWork.append(QueryCostToString.selectivity(numRecords,
                                    numDistinctValues))
                                    .append("\n\n");

                            if (symbol.equalsIgnoreCase("=") || symbol.equalsIgnoreCase("!=")) {
                                if (isUniqueValue) {
                                    pipelinedProductionCost = QueryCost.clusteredBTreeUnique(levels);
                                    pipelinedProductionCostWork.append(QueryCostToString.clusteredBTreeUnique(levels))
                                            .append("\n");
                                } else {
                                    pipelinedProductionCost = QueryCost.clusteredBTreeNonUnique(levels, selectivity,
                                            degree);
                                    pipelinedProductionCostWork.append(QueryCostToString.clusteredBTreeNonUnique(
                                            levels, selectivity, degree)).append("\n");
                                }
                            } else { // >, <, >=, <=
                                pipelinedProductionCost = QueryCost.clusteredBTreeRange(levels, terminalLevelNodes);
                                pipelinedProductionCostWork.append(QueryCostToString.clusteredBTreeRange(
                                        levels, terminalLevelNodes)).append("\n");
                            }
                            break;
                        }
                        case HASH_TABLE: {
                            if (symbol.equalsIgnoreCase("=") || symbol.equalsIgnoreCase("!=")) {
                                if (isUniqueValue) {
                                    pipelinedProductionCost = QueryCost.hashTableUnique();
                                    pipelinedProductionCostWork.append(QueryCostToString.hashTableUnique())
                                            .append("\n");
                                } else {
                                    pipelinedProductionCost = QueryCost.hashTableNonUnique(selectivity);
                                    pipelinedProductionCostWork
                                            .append(QueryCostToString.hashTableNonUnique(selectivity)).append("\n");
                                }
                            } else { // >, <, >=, <=
                                pipelinedProductionCost = QueryCost.hashTableRange();
                                pipelinedProductionCostWork.append(QueryCostToString.hashTableRange()).append("\n");
                            }
                            break;
                        }
                    }

                    break;
                }
                case PROJECTION: {

                    Projection projection = (Projection) operator;
                    List<String> columnNames = projection.getColumnNames();
                    ResultSet resultSet = workingOperatorStack.pop().projection(columnNames);
                    workingOperatorStack.push(resultSet);

                    // get result set data (will only be used if and only if the working production cost is empty
                    // which means that it wasn't written to from previous operator nodes encountered)
                    int recordSize = QueryCost.recordSize(resultSet.getColumns());
                    int numRecords = QueryCost.numberRecords(resultSet.getData());
                    int blockingFactor = QueryCost.blockingFactor(recordSize);
                    int blocks = QueryCost.blocks(numRecords, blockingFactor);

                    if (pipelinedProductionCostWork.length() == 0) {
                        pipelinedProductionCostWork.append(QueryCostToString.recordSize(resultSet.getColumns()))
                                .append("\n");
                        pipelinedProductionCostWork.append(QueryCostToString.numberRecords(resultSet.getData()))
                                .append("\n");
                        pipelinedProductionCostWork.append(QueryCostToString.blockingFactor(recordSize)).append("\n");
                        pipelinedProductionCostWork.append(QueryCostToString.blocks(numRecords, blockingFactor))
                                .append("\n");
                    }

                    // add the working production cost to the stack to preserve correct pipelining ordering
                    pipelinedProductionCostWorkStack.push(pipelinedProductionCostWork.toString());
                    totalProductionCost += pipelinedProductionCost;

                    // reset these
                    pipelinedProductionCost = 0;
                    pipelinedProductionCostWork = new StringBuilder();

                    // handling write to disk cost which is more straight forward than production cost
                    boolean lastProjection = operatorStack.isEmpty();

                    if (! lastProjection) {

                        totalWriteToDiskCost += blocks;
                        StringBuilder temp = new StringBuilder();
                        temp.append(QueryCostToString.recordSize(resultSet.getColumns())).append("\n");
                        temp.append(QueryCostToString.numberRecords(resultSet.getData())).append("\n");
                        temp.append(QueryCostToString.blockingFactor(recordSize)).append("\n");
                        temp.append(QueryCostToString.blocks(numRecords, blockingFactor)).append("\n");
                        pipelinedWriteToDiskCostWorkStack.push(temp.toString());
                    }

                    break;
                }
                case INNER_JOIN: {

                    InnerJoin innerJoin = (InnerJoin) operator;
                    String firstJoinColumnName = innerJoin.getFirstJoinColumnName();
                    String joinSymbolName = innerJoin.getSymbol();
                    String secondJoinColumnName = innerJoin.getSecondJoinColumnName();
                    ResultSet firstResultSet = workingOperatorStack.pop();
                    ResultSet secondResultSet = workingOperatorStack.pop();
                    workingOperatorStack.push(firstResultSet.innerJoin(
                            secondResultSet, firstJoinColumnName, joinSymbolName, secondJoinColumnName));

                    // pop off the strings for the production and write to disk costs and add append these to the work
                    String productionCostWorkTemp1 = pipelinedProductionCostWorkStack.pop();
                    productionCostWorkTemp1 = "Produce " + PipelinedExpression.SYMBOL +
                            PipelinedExpression.toSubscript(subscript) + ":\n\n" + productionCostWorkTemp1 +
                            "\n----------------------------------------------------------------\n\n";
                    productionCostWork.append(productionCostWorkTemp1);
                    String writeToDiskCostWorkTemp1 = pipelinedWriteToDiskCostWorkStack.pop();
                    writeToDiskCostWorkTemp1 = "Write To Disk " + PipelinedExpression.SYMBOL +
                            PipelinedExpression.toSubscript(subscript) + ":\n\n" + writeToDiskCostWorkTemp1 +
                            "\n----------------------------------------------------------------\n\n";
                    writeToDiskCostWork.append(writeToDiskCostWorkTemp1);
                    subscript++;

                    String productionCostWorkTemp2 = pipelinedProductionCostWorkStack.pop();
                    productionCostWorkTemp2 = "Produce " + PipelinedExpression.SYMBOL +
                            PipelinedExpression.toSubscript(subscript) + ":\n\n" + productionCostWorkTemp2 +
                            "\n----------------------------------------------------------------\n\n";
                    productionCostWork.append(productionCostWorkTemp2);
                    String writeToDiskCostWorkTemp2 = pipelinedWriteToDiskCostWorkStack.pop();
                    writeToDiskCostWorkTemp2 = "Write To Disk " + PipelinedExpression.SYMBOL +
                            PipelinedExpression.toSubscript(subscript) + ":\n\n" + writeToDiskCostWorkTemp2 +
                            "\n----------------------------------------------------------------\n\n";
                    writeToDiskCostWork.append(writeToDiskCostWorkTemp2);
                    subscript++;

                    Column firstJoinColumn = firstResultSet.getColumnFromColumnName(firstJoinColumnName);
                    Column secondJoinColumn = secondResultSet.getColumnFromColumnName(secondJoinColumnName);

                    int firstTableRecordSize = QueryCost.recordSize(firstResultSet.getColumns());
                    int firstTableNumRecords = QueryCost.numberRecords(firstResultSet.getData());
                    int firstTableBlockingFactor = QueryCost.blockingFactor(firstTableRecordSize);
                    int firstTableBlocks = QueryCost.blocks(firstTableNumRecords, firstTableBlockingFactor);

                    int secondTableRecordSize = QueryCost.recordSize(secondResultSet.getColumns());
                    int secondTableNumRecords = QueryCost.numberRecords(secondResultSet.getData());
                    int secondTableBlockingFactor = QueryCost.blockingFactor(secondTableRecordSize);
                    int secondTableBlocks = QueryCost.blocks(secondTableNumRecords, secondTableBlockingFactor);

                    int firstColumnDegree = QueryCost.degree(firstJoinColumn.size());
                    int firstColumnNumLevels = QueryCost.levels(firstTableNumRecords, firstColumnDegree);

                    int secondColumnDegree = QueryCost.degree(secondJoinColumn.size());
                    int secondColumnNumLevels = QueryCost.levels(secondTableNumRecords, secondColumnDegree);

                    int firstColumnForeignKeySelectivity =
                            QueryCost.foreignKeySelectivity(firstTableNumRecords, secondTableNumRecords);
                    int secondColumnForeignKeySelectivity =
                            QueryCost.foreignKeySelectivity(secondTableNumRecords, firstTableNumRecords);

                    pipelinedProductionCostWork = new StringBuilder();
                    pipelinedProductionCostWork.append("First Relation (Left Subtree):\n");
                    pipelinedProductionCostWork.append(QueryCostToString.recordSize(firstResultSet.getColumns()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.numberRecords(firstResultSet.getData()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blockingFactor(firstTableRecordSize))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blocks(firstTableNumRecords,
                            firstTableBlockingFactor));
                    pipelinedProductionCostWork.append("\n\n");

                    pipelinedProductionCostWork.append("Second Relation (Right Subtree):\n");
                    pipelinedProductionCostWork.append(QueryCostToString.recordSize(secondResultSet.getColumns()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.numberRecords(secondResultSet.getData()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blockingFactor(secondTableRecordSize))
                            .append("\n");
                    pipelinedProductionCostWork
                            .append(QueryCostToString.blocks(secondTableNumRecords, secondTableBlockingFactor))
                            .append("\n\n");

                    Table firstTable = Utilities.getReferencedTable(
                            firstJoinColumn.getColumnName().split("\\.")[0], tables);
                    Table secondTable = Utilities.getReferencedTable(
                            secondJoinColumn.getColumnName().split("\\.")[0], tables);

                    assert firstTable != null && secondTable != null;

                    boolean isClustered = firstTable.getClusteredWithTableName()
                            .equalsIgnoreCase(secondTable.getTableName()) && secondTable.getClusteredWithTableName()
                            .equalsIgnoreCase(firstTable.getTableName());

                    if (isClustered) {

                        int clusteredJoinCost = QueryCost.clusteredJoin(firstTableBlocks, secondTableBlocks);
                        pipelinedProductionCost = clusteredJoinCost;

                        pipelinedProductionCostWork.append(QueryCostToString.clusteredFileJoin(firstTableBlocks,
                                secondTableBlocks)).append("\n");

                    // not clustered
                    } else {

                        boolean noFileStructuresBuilt = firstJoinColumn.getFileStructure() == FileStructure.NONE &&
                                secondJoinColumn.getFileStructure() == FileStructure.NONE;

                        if (noFileStructuresBuilt) {

                            int nestedLoopJoinCost = QueryCost.nestedLoopJoin(firstTableBlocks, secondTableBlocks);
                            pipelinedProductionCost = nestedLoopJoinCost;

                            pipelinedProductionCostWork.append(QueryCostToString.nestedLoopJoin(firstTableBlocks,
                                    secondTableBlocks)).append("\n");

                        // has a file structure built on one of the columns, if both, choose lowest produced cost
                        } else {

                            int firstBTreeJoinCost = Integer.MAX_VALUE;
                            int secondBTreeJoinCost = Integer.MAX_VALUE;

                            if (firstJoinColumn.getFileStructure() != FileStructure.NONE) {
                                firstBTreeJoinCost = QueryCost.bTreeJoin(secondTableBlocks, secondTableNumRecords,
                                        firstColumnNumLevels, firstColumnForeignKeySelectivity);
                            }

                            if (secondJoinColumn.getFileStructure() != FileStructure.NONE) {
                                secondBTreeJoinCost = QueryCost.bTreeJoin(firstTableBlocks, firstTableNumRecords,
                                        secondColumnNumLevels, secondColumnForeignKeySelectivity);
                            }

                            if (firstBTreeJoinCost < secondBTreeJoinCost) {
                                pipelinedProductionCost = firstBTreeJoinCost;
                                pipelinedProductionCostWork.append(QueryCostToString.bTreeJoin(secondTableBlocks,
                                        secondTableNumRecords, firstColumnNumLevels, firstColumnForeignKeySelectivity))
                                .append("\n");

                            } else {
                                pipelinedProductionCost = secondBTreeJoinCost;
                                pipelinedProductionCostWork.append(QueryCostToString.bTreeJoin(firstTableBlocks,
                                        firstTableNumRecords, secondColumnNumLevels,
                                        secondColumnForeignKeySelectivity)).append("\n");
                            }
                        }
                    }

                    break;
                }
                case CARTESIAN_PRODUCT: {

                    ResultSet firstResultSet = workingOperatorStack.pop();
                    ResultSet secondResultSet = workingOperatorStack.pop();
                    ResultSet cartesianProduct = firstResultSet.cartesianProduct(secondResultSet);
                    workingOperatorStack.push(cartesianProduct);

                    int recordSize = QueryCost.recordSize(cartesianProduct.getColumns());
                    int numRecords = QueryCost.numberRecords(cartesianProduct.getData());
                    int blockingFactor = QueryCost.blockingFactor(recordSize);
                    int blocks = QueryCost.blocks(numRecords, blockingFactor);

                    pipelinedProductionCost = blocks;

                    pipelinedProductionCostWork.append(QueryCostToString.recordSize(cartesianProduct.getColumns()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.numberRecords(cartesianProduct.getData()))
                            .append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blockingFactor(recordSize)).append("\n");
                    pipelinedProductionCostWork.append(QueryCostToString.blocks(numRecords, blockingFactor))
                            .append("\n");

                    break;
                }
                case AGGREGATION: {

                    Aggregation aggregation = (Aggregation) operator;
                    List<String> groupByColumnNames = aggregation.getGroupByColumnNames();
                    List<String> aggregationTypes = aggregation.getAggregationTypes();
                    List<String> aggregatedColumnNames = aggregation.getAggregatedColumnNames();
                    workingOperatorStack.push(workingOperatorStack.pop().aggregate(
                            groupByColumnNames, aggregationTypes, aggregatedColumnNames));

                    break;
                }
                case AGGREGATE_SELECTION: {

                    AggregateSelection aggregateSelection = (AggregateSelection) operator;
                    List<String> aggregationTypes = aggregateSelection.getAggregateTypes();
                    List<String> aggregatedColumnNames = aggregateSelection.getColumnNames();
                    List<String> symbols = aggregateSelection.getSymbols();
                    List<String> values = aggregateSelection.getValues();
                    workingOperatorStack.push(workingOperatorStack.pop().having(
                            aggregationTypes, aggregatedColumnNames, symbols, values));

                    break;
                }
            }
        }

        String finalRelationWork = pipelinedProductionCostWorkStack.pop();
        finalRelationWork = "Produce " + PipelinedExpression.SYMBOL + PipelinedExpression.toSubscript(subscript) +
                ":\n\n" + finalRelationWork + "\n----------------------------------------------------------------\n\n";
        productionCostWork.append(finalRelationWork);

        return new Quadruple<>(totalProductionCost, totalWriteToDiskCost,
                productionCostWork.toString(), writeToDiskCostWork.toString());
    }
}