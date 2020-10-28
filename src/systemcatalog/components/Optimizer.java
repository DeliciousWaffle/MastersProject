package systemcatalog.components;

import datastructures.misc.Triple;
import datastructures.querytree.operator.types.*;
import datastructures.relation.table.Table;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.querytree.QueryTree;
import datastructures.querytree.operator.Operator;
import utilities.OptimizerUtilities;
import utilities.QueryCost;
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
    private boolean joinOptimizationIsOn;

    public Optimizer() {
        queryRuleGraph = RuleGraphTypes.getQueryRuleGraph();
        joinOptimizationIsOn = true;
    }

    /**
     * Turns the join rearrangement on.
     */
    public void turnOnJoinOptimization() {
        joinOptimizationIsOn = true;
    }

    /**
     * Turns the join rearrangement off.
     */
    public void turnOffJoinOptimization() {
        joinOptimizationIsOn = false;
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

        // remove prefixed column names from each query tree state
        // TODO figure out why not working

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

        // wrap each value that appears in the WHERE and HAVING clauses in quotation marks
        // TODO don't do this
        //OptimizerUtilities.addQuotationsToStringValues(whereClauseValues);
        //OptimizerUtilities.addQuotationsToStringValues(havingClauseValues);

        // add unique column names that don't appear in select clause from group by when using aggregation
        selectClauseColumnNames = OptimizerUtilities.addUniqueColumnNames(selectClauseColumnNames, groupByColumnNames);

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
        List<Operator> relations = new ArrayList<>(queryTree.getOperatorsAndLocationsOfType(RELATION, PREORDER).keySet());

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

        // after that, will need to add projections between sequential cartesian products and sequential joins will get
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

            for (Operator operator : cartesianProductsAndJoins) {

                // all this mess does is get the current operator's location
                List<QueryTree.Traversal> operatorsLocation = Stream.concat(
                        queryTree.getOperatorsAndLocationsOfType(CARTESIAN_PRODUCT, PREORDER).entrySet().stream(),
                        queryTree.getOperatorsAndLocationsOfType(INNER_JOIN, PREORDER).entrySet().stream()
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).get(operator);

                // avoid producing a redundant projection (only occurs if root node is a projection)
                if (queryTree.get(operatorsLocation, UP).getType() == PROJECTION) {
                    continue;
                }

                // get and add all column names from the left and right projections
                List<String> columnNamesToProject = new ArrayList<>();
                columnNamesToProject.addAll(queryTree.get(operatorsLocation, LEFT).getReferencedColumnNames());
                columnNamesToProject.addAll(queryTree.get(operatorsLocation, RIGHT).getReferencedColumnNames());

                // if current operator is a join, subtract the join criteria from the list of column names to project
                if (operator.getType() == INNER_JOIN) {
                    String firstJoinColumnName = ((InnerJoin) operator).getFirstJoinColumnName();
                    String secondJoinColumnName = ((InnerJoin) operator).getSecondJoinColumnName();
                    columnNamesToProject = OptimizerUtilities
                            .minus(columnNamesToProject, Arrays.asList(firstJoinColumnName, secondJoinColumnName));
                    // may get an empty list, in this case, add all referenced column names from the aggregation above
                    if (columnNamesToProject.isEmpty()) {
                        columnNamesToProject.addAll(queryTree.get(operatorsLocation, UP).getReferencedColumnNames());
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
     * takes into account selections that are performed in the subtree of where that table is located
     * as well as available file structures. Performing this action will reduce the write to disk cost
     * when pipelining intermediary subtrees of the query tree, thus, increasing performance.
     * @param queryTree is the query tree to perform the reordering of joins on
     * @param tables are a list of tables in the system that will be referenced to determine the cost
     * of executing a particular subtree and whether a leaf node will need to be rearranged
     * @return the query tree after the leaf nodes have been rearranged
     */
    public QueryTree rearrangeJoins(QueryTree queryTree, String[] input, List<Table> tables) {

        if (joinOptimizationIsOn) {
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

        // for each query tree permutation, calculate the cost it will take to execute each one with respect
        // to selections encountered and file structures already built
        int lowestCost = Integer.MAX_VALUE, indexOfLowestCost = 0;

        for (QueryTree permutedQueryTree : permutedQueryTrees) {
            // convert the operators from this permuted query tree to a stack
            Deque<Operator> operatorStack =
                    OptimizerUtilities.setToDeque(permutedQueryTree.getOperatorsAndLocations(PREORDER).keySet());
            Deque<Integer> costStack = new ArrayDeque<>();

            // perform the calculation
            while(! operatorStack.isEmpty()) {
                Operator operator = operatorStack.pop();

                switch(operator.getType()) {
                    case RELATION: {
                        Relation relation = (Relation) operator;
                        Table table = Utilities.getReferencedTable(relation.getTableName(), tables);
                        assert table != null;
                        int recordSize = table.getRecordSize();
                        int numRecords = table.getNumRecords();
                        int blockingFactor = QueryCost.blockingFactor(recordSize);
                        int blocks = QueryCost.blocks(numRecords, blockingFactor);
                        costStack.push(blocks);
                        break;
                    }
                    case SIMPLE_SELECTION: {
                        SimpleSelection simpleSelection = (SimpleSelection) operator;
                        
                        break;
                    }
                    case INNER_JOIN: {
                        InnerJoin innerJoin = (InnerJoin) operator;
                        break;
                    }
                }
            }
            // for each relation, traverse up the tree, calculating cost along the way


            // get each node from the tree
            // determine type of node
            // relation
            // map to system data
            // get size and add it to running cost
            // selection
            // map to system data
            // calculate the cost and add it to running cost
            // join
            // map to system data
            // calculate the cost and add it to running cost
        }

        return queryTree;
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

        QueryTree initialQueryTree = queryTreeStates.get(0);

        StringBuilder naiveRelationalAlgebra = new StringBuilder();
        StringBuilder closingBrackets = new StringBuilder();

        List<Operator> operators = new ArrayList<>(initialQueryTree.getOperatorsAndLocations(PREORDER).keySet());
        List<Operator> relations =
                new ArrayList<>(initialQueryTree.getOperatorsAndLocationsOfType(RELATION, PREORDER).keySet());

        for (Operator operator : operators) {
            if (operator.getType() == CARTESIAN_PRODUCT) {
                break;
            }
            naiveRelationalAlgebra.append(operator).append(" [");
            closingBrackets.append("]");
        }

        // remove last " [" and "]" if they are not needed
        if (relations.size() == 1) {
            naiveRelationalAlgebra.delete(naiveRelationalAlgebra.length() - 2, naiveRelationalAlgebra.length());
            closingBrackets.deleteCharAt(closingBrackets.length() - 1);
        }

        // add the relations and cartesian products manually
        relations.forEach(relation -> naiveRelationalAlgebra.append(relation).append(" ✕ "));

        // remove last " ✕ "
        naiveRelationalAlgebra.delete(naiveRelationalAlgebra.length() - 3, naiveRelationalAlgebra.length());

        // add closing brackets
        naiveRelationalAlgebra.append(closingBrackets);

        return naiveRelationalAlgebra.toString();
    }

    /**
     * When the user enters input, this returns the equivalent optimized relational algebra.
     * @param queryTreeStates is a list of query tree states produced after the optimization process
     * @return optimized relational algebra
     */
    public String getOptimizedRelationalAlgebra(List<QueryTree> queryTreeStates) {

        List<QueryTree> pipelinedQueryTrees = queryTreeStates.subList(6, queryTreeStates.size());

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
        pipelinedExpressions.forEach(pipelinedExpression ->
                optimizedRelationalAlgebra.append(pipelinedExpression.getRelationalAlgebra()).append("\n"));

        // remove "\n"
        optimizedRelationalAlgebra.deleteCharAt(optimizedRelationalAlgebra.length() - 1);

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
     * @return a recommendation of file structures to build for a particular query
     */
    public String getRecommendedFileStructures(List<QueryTree> queryTreeStates) {

        QueryTree queryTreeBeforePipelining = queryTreeStates.get(5);

        // the triplet is composed of column name, table name, and the file structure to use
        List<Triple<String, String, String>> recommendedFileStructures = new ArrayList<>();

        // will build file structures on columns in tables first
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
            int replaceIndex = hasFileStructureConflict(recommendedFileStructure, recommendedFileStructures);

            boolean hasConflict = replaceIndex != -1;

            if (hasConflict) {
                recommendedFileStructure = new Triple<>(tableName, columnName, "Secondary B-Tree");
                recommendedFileStructures.set(replaceIndex, recommendedFileStructure);
            } else {
                recommendedFileStructures.add(recommendedFileStructure);
            }
        }

        // suggest file structures to build on for joins

        // if a clustered b-tree and hash table are recommended for the same column, change to secondary b-tree

        // if a clustered file is recommended for a join, remove any file structures that were built on columns
        // belonging to the tables being joined together


        return "";
    }

    /**
     * @param queryTreeStates is a list of query tree states produced after the optimization process
     * @return returns a mini spreadsheet outlining how query cost was produced
     */
    public String getCostAnalysis(List<QueryTree> queryTreeStates) {

        QueryTree queryTreeBeforePipelining = queryTreeStates.get(5);

        StringBuilder costAnalysis = new StringBuilder();

        return costAnalysis.toString();
    }
}