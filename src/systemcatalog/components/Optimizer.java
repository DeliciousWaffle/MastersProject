package systemcatalog.components;

import datastructures.relation.table.Table;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import datastructures.trees.querytree.operator.Operator;
import datastructures.trees.querytree.operator.types.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static datastructures.trees.querytree.QueryTree.Traversal.*;
import static datastructures.trees.querytree.operator.Operator.Type.*;

/**
 * Responsible for determining the execution strategy of a query. This involves converting the user's input
 * into relational algebra and converting the relational algebra into a query tree. The query tree is a tree
 * structure representation of a query with each node containing a relational algebra expression. The idea is
 * to use relational algebra axioms and information about the tables referenced to rearrange the nodes in such
 * a way that it reduces the overall cost of execution. This optimization involves multiple steps.
 * Step 1: Break up any selections with conjunctions with a cascade a of selections
 * Step 2: Move these selections as far down the query tree as possible
 * Step 3: Combine cartesian products and selections with a join condition into joins
 * Step 4: Rearrange these joins such that subtrees with the most restricted references are done first
 * Step 5: Move projections as far down the query tree as possible
 * Step 6: Identify subtrees that can be pipelined
 * This class is also responsible for producing naive and optimized relational algebra expressions which help
 * show the user how their query can be further optimized. Additionally, recommendations for file structures
 * will also be generated based on the query.
 */
public class Optimizer {

    private final RuleGraph queryRuleGraph;
    private boolean toggleRearrangeLeafNodes;

    public Optimizer() {
        this.queryRuleGraph = RuleGraphTypes.getQueryRuleGraph();
        this.toggleRearrangeLeafNodes = true;
    }

    public void toggleRearrangeLeafNodes() {
        this.toggleRearrangeLeafNodes = !toggleRearrangeLeafNodes;
    }

    public List<QueryTree> getQueryTreeStates(String[] input, List<Table> tables) {

        QueryTree queryTree                     = createQueryTree(input, tables, new ArrayList<>());
        QueryTree afterCascadingSelections      = cascadeSelections(new QueryTree(queryTree));
        QueryTree afterPushingDownSelections    = pushDownSelections(new QueryTree(afterCascadingSelections));
        QueryTree afterFormingJoins             = formJoins(new QueryTree(afterPushingDownSelections));
        QueryTree afterRearrangingJoins         = rearrangeJoins(new QueryTree(afterFormingJoins), input, tables);
        /*QueryTree afterPushingDownProjections   = pushDownProjections(new QueryTree(afterRearrangingJoins));
        List<QueryTree> afterPipeliningSubtrees = pipelineSubtrees(new QueryTree(afterPushingDownProjections));

        List<QueryTree> queryTreeStates = new ArrayList<>(Arrays.asList(
                queryTree, afterCascadingSelections, afterPushingDownSelections, afterFormingJoins,
                afterRearrangingJoins, afterPushingDownProjections
        ));

        queryTreeStates.addAll(afterPipeliningSubtrees);*/

        return Arrays.asList();
    }

    /**
     * Responsible for query tree creation. This involves extracting the user's input and making references
     * to the system tables in order to create relational algebra operators and add them to the tree.
     * @param input is the user's tokenized input
     * @param tables is a list of system tables
     * @return the query tree after creation
     */
    public QueryTree createQueryTree(String[] input, List<Table> tables, List<String> tableNames) {

        // extracting all the data from the input

        // select clause data
        List<String> selectClauseColumnNames = queryRuleGraph.getTokensAt(input, 1, 2);
        List<String> selectClauseAggregationTypes = queryRuleGraph.getTokensAt(input, 3, 4, 5, 6, 7);
        List<String> selectClauseAggregatedColumnNames = queryRuleGraph.getTokensAt(input, 9);

        // from clause data
        if(tableNames.isEmpty()) {
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
        int numWhereClauseAnds = queryRuleGraph.getTokensAt(input, 40).size();

        // group by clause data
        List<String> groupByColumnNames = queryRuleGraph.getTokensAt(input, 43);

        // having clause data
        List<String> havingClauseAggregationTypes = queryRuleGraph.getTokensAt(input, 46, 47, 48, 49, 50);
        List<String> havingClauseColumnNames = queryRuleGraph.getTokensAt(input, 52);
        List<String> havingClauseSymbols = queryRuleGraph.getTokensAt(input, 54, 55, 56, 57, 58, 59);
        List<String> havingClauseValues = queryRuleGraph.getTokensAt(input, 60, 62);

        // the following is just some data cleaning

        List<Table> referencedTables = OptimizerUtilities.getReferencedTables(tableNames, tables);

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
        OptimizerUtilities.addQuotationsToStringValues(whereClauseValues);
        OptimizerUtilities.addQuotationsToStringValues(havingClauseValues);

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

        if(hasAggregateSelection) {
            queryTree.add(traversals, NONE, new AggregateSelection(
                    havingClauseAggregationTypes, havingClauseColumnNames, havingClauseSymbols, havingClauseValues
            ));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        if(hasAggregation) {
            queryTree.add(traversals, NONE, new Aggregation(
                    selectClauseColumnNames, selectClauseAggregationTypes, selectClauseAggregatedColumnNames
            ));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        if(hasProjection) {
            queryTree.add(traversals, NONE, new Projection(selectClauseColumnNames));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        if(hasCompoundSelection) {
            queryTree.add(traversals, NONE, new CompoundSelection(
                    whereClauseColumnNames, whereClauseSymbols, whereClauseValues
            ));
            traversals.add(QueryTree.Traversal.DOWN);
        }

        if(hasSimpleSelection) {
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

        if(hasOneRelation) {
            queryTree.add(traversals, NONE, relations.get(0));
        }

        if(hasOneCartesianProduct) {
            queryTree.add(traversals, NONE, cartesianProducts.get(0));
            queryTree.add(traversals, LEFT, relations.get(0));
            queryTree.add(traversals, RIGHT, relations.get(1));
        }

        if(hasMultipleCartesianProducts) {
            while(! cartesianProducts.isEmpty()) {
                queryTree.add(traversals, NONE, cartesianProducts.remove(0));
                int lastIndex = relations.size() - 1;
                queryTree.add(traversals, RIGHT, relations.remove(lastIndex));
                traversals.add(LEFT);
            }
            queryTree.add(traversals, NONE, relations.remove(0));
        }
        //queryTree.getOperatorsAndLocations().forEach((k, v) -> System.out.println(k + " " + v));
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

        if(! hasCompoundSelections) {
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

        //System.out.println("\nCascade Selections:");
        //queryTree.getOperatorsAndLocations().forEach((k, v) -> System.out.println(k + " " + v));

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

        if(! hasSimpleSelections) {
            return queryTree;
        }

        // also don't bother if there is only 1 relation
        boolean hasOneRelation = queryTree.getTypeOccurrence(RELATION) == 1;

        if(hasOneRelation) {
            return queryTree;
        }

        // getting all simple selections from the query tree
        Map<Operator, List<QueryTree.Traversal>> simpleSelectionsAndLocations =
                queryTree.getOperatorsAndLocationsOfType(SIMPLE_SELECTION);
        List<Operator> simpleSelections = new ArrayList<>(simpleSelectionsAndLocations.keySet());

        // removing each simple selection from the query tree, this will be used as a starting point
        List<List<QueryTree.Traversal>> simpleSelectionLocations =
                new ArrayList<>(simpleSelectionsAndLocations.values());
        List<QueryTree.Traversal> smallestTraversal =
                OptimizerUtilities.getListOfFewestElements(simpleSelectionLocations);

        // removing each simple selection from the query tree
        while(hasSimpleSelections) {
            queryTree.remove(smallestTraversal, NONE);
            hasSimpleSelections = queryTree.getTypeOccurrence(SIMPLE_SELECTION) != 0;
        }

        // get a list of selections that contain join conditions, these will be dealt with later
        List<Operator> selectionsWithJoinConditions =
                OptimizerUtilities.getSelectionsWithJoinConditions(simpleSelections);


        // place the remaining selections right above their respective relations
        Map<Operator, List<QueryTree.Traversal>> relationsAndLocations =
                queryTree.getOperatorsAndLocationsOfType(RELATION);

        for(Operator simpleSelection : simpleSelections) {
            String prefixedTableName = ((SimpleSelection) simpleSelection).getColumnName().split("\\.")[0];
            for(Map.Entry<Operator, List<QueryTree.Traversal>> entry : relationsAndLocations.entrySet()) {
                String tableName = ((Relation) entry.getKey()).getTableName();
                List<QueryTree.Traversal> traversals = entry.getValue();
                if(tableName.equalsIgnoreCase(prefixedTableName)) {
                    queryTree.add(traversals, UP, simpleSelection);
                    break;
                }
            }
        }

        // place the selections with join conditions above cartesian products
        for(Operator simpleSelection : selectionsWithJoinConditions) {
            // changing the traversal locations for each relation, will need to get the updated versions
            relationsAndLocations = queryTree.getOperatorsAndLocationsOfType(RELATION);
            String firstPrefixedTableName = ((SimpleSelection) simpleSelection).getColumnName().split("\\.")[0];
            String secondPrefixedTableName = ((SimpleSelection) simpleSelection).getValue().split("\\.")[0];
            // find each corresponding table name and get their traversals
            List<QueryTree.Traversal> firstTablesLocation = new ArrayList<>();
            List<QueryTree.Traversal> secondTablesLocation = new ArrayList<>();
            for(Map.Entry<Operator, List<QueryTree.Traversal>> entry : relationsAndLocations.entrySet()) {
                String tableName = ((Relation) entry.getKey()).getTableName();
                List<QueryTree.Traversal> traversals = entry.getValue();
                if(tableName.equalsIgnoreCase(firstPrefixedTableName)) {
                    firstTablesLocation = traversals;
                }
                if(tableName.equalsIgnoreCase(secondPrefixedTableName)) {
                    secondTablesLocation = traversals;
                }
            }
            // the relation with the fewest number of traversals is furthest up the tree, which is where
            // we want the join condition placed
            boolean atCartesianProduct = false;
            if(firstTablesLocation.size() < secondTablesLocation.size()) {
                while(! atCartesianProduct) {
                    int lastIndex = firstTablesLocation.size() - 1;
                    firstTablesLocation.remove(lastIndex);
                    atCartesianProduct = queryTree.get(firstTablesLocation, NONE).getType() == CARTESIAN_PRODUCT;
                }
                queryTree.add(firstTablesLocation, UP, simpleSelection);
            } else {
                while(! atCartesianProduct) {
                    int lastIndex = secondTablesLocation.size() - 1;
                    secondTablesLocation.remove(lastIndex);
                    atCartesianProduct = queryTree.get(secondTablesLocation, NONE).getType() == CARTESIAN_PRODUCT;
                }
                queryTree.add(secondTablesLocation, UP, simpleSelection);
            }
        }

        //System.out.println("\nPush Down Selections:");
        //queryTree.getOperatorsAndLocations().forEach((k, v) -> System.out.println(k + " " + v));

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

        if(! hasSimpleSelections || ! hasCartesianProducts) {
            return queryTree;
        }

        // for each cartesian product, check above it to see if a selection with a join condition exists
        boolean finishedJoining = false;

        while (! finishedJoining) {

            // cartesian products and their locations will change upon removal of selections
            Map<Operator, List<QueryTree.Traversal>> cartesianProductsAndLocations =
                    queryTree.getOperatorsAndLocationsOfType(CARTESIAN_PRODUCT);

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

        //System.out.println("Form Joins");
        //queryTree.getOperatorsAndLocations().forEach((k, v) -> System.out.println(k + " " + v));

        return  queryTree;
    }

    /**
     * Rearranges the ordering of joins such that ones with the most restricted references
     * are executed first in the query tree. This takes into account selections that are performed
     * in the subtree of where that table is located as well as available file structures.
     * Performing this action will reduce the write to disk cost when pipelining intermediary
     * subtrees of the query tree, thus, increasing performance.
     * @param queryTree is the query tree to perform the reordering of leaf nodes on
     * @param tables are a list of tables in the system that will be referenced to determine the cost
     * of executing a particular subtree and whether a leaf node will need to be rearranged
     * @return the query tree after the leaf nodes have been rearranged in order to reduce write to disk costs
     */
    public QueryTree rearrangeJoins(QueryTree queryTree, String[] input, List<Table> tables) {

        // don't bother if we only have a single table or have 1 or 0 joins
        boolean hasOneRelation = queryTree.getTypeOccurrence(RELATION) == 1;
        boolean hasOneOrLessInnerJoins = queryTree.getTypeOccurrence(INNER_JOIN) <= 1;

        if(hasOneRelation || hasOneOrLessInnerJoins) {
            return queryTree;
        }

        // permuting a list of all possible query tree orientations while preserving join integrity
        List<String> tableNames = queryRuleGraph.getTokensAt(input, 13, 15, 18);
        System.out.println("REARRANGE JOINS");
        List<List<String>> permutedTableNames = new ArrayList<>();
        OptimizerUtilities.permuteList(tableNames.size(), tableNames, permutedTableNames);
        permutedTableNames.forEach(System.out::println);

        List<QueryTree> permutedQueryTrees = new ArrayList<>();
        for(int i = 0; i < permutedTableNames.size(); i++) {
            QueryTree permutedQueryTree = createQueryTree(input, tables, permutedTableNames.get(i));
            permutedQueryTree = cascadeSelections(permutedQueryTree);
            permutedQueryTree = pushDownSelections(permutedQueryTree);
            permutedQueryTree = formJoins(permutedQueryTree);
            permutedQueryTrees.add(permutedQueryTree);
        }
        System.out.println("START PERMUTING==================================================================================");
        // filter out permutations that contain cartesian products, these will be inherently worse
        permutedQueryTrees.forEach(e -> e.getOperatorsAndLocations().forEach((k, v) -> System.out.println(k + " " + v)));
System.out.println("DONE PERMUTING==================================================================================\n");
        return queryTree;
    }
}

    /*

    // 4. Rearrangement of Leaf Nodes ==================================================================================

    /**
     * Rearranges the leaf nodes (the tables) such that ones with the most restricted references
     * are executed first in the query tree. This takes into account selections that are performed
     * in the subtree of where that table is located. Performing this action will reduce the write to
     * disk cost when pipelining intermediary subtrees of the query tree, thus, increasing performance.
     * @param queryTree is the query tree to perform the reordering of leaf nodes on
     //* @param tables are a list of tables in the system that will be referenced to determine the cost
     * of executing a particular subtree and whether a leaf node will need to be rearranged
     * @return the query tree after the leaf nodes have been rearranged in order to reduce write to disk costs
     */
    /*public QueryTree rearrangeLeafNodes(QueryTree queryTree, List<Table> tables) {

        // don't bother if the user doesn't wish to rearrange the leaf nodes
        if (! toggleRearrangeLeafNodes) {
            return queryTree;
        }

        // also don't bother if there are only 1 or 2 tables being referenced, the ordering of leaf nodes will not make a difference
        if (queryTree.getNumRelations() <= 2) {
            return queryTree;
        }

        // get the locations of each table in the query tree
        List<List<QueryTree.Traversal>> tableLocations = getTableLocationsInQueryTree(queryTree);

        // get the names of each table that appears in the query tree
        List<String> tableNames = getTableNamesFromRelationLocations(tableLocations, queryTree);

        // get each table referenced in the query tree from the tables in the system
        List<Table> referencedTables = getReferencedTables(tableNames, tables);

        // order the referenced tables such that they match the ordering of tables in the query tree
        orderReferencedTables(referencedTables, tableNames);

        // get the cost of executing each table
        List<Integer> costs = getTableCosts(referencedTables);

        // for each leaf node location, traverse up until a join, calculating cost along the way

        // sort each referenced table

        // now order each subtree such that ones that produce the least cost are executed first

        // TODO implement a swap method in query tree


        return queryTree;
    }

    private List<List<QueryTree.Traversal>> getTableLocationsInQueryTree(QueryTree queryTree) {

        List<List<QueryTree.Traversal>> relationLocations = new ArrayList<>();
        List<List<QueryTree.Traversal>> allOperatorLocations = queryTree.getEveryOperatorsLocation();

        for(List<QueryTree.Traversal> operatorLocation : allOperatorLocations) {
            Operator operator = queryTree.get(operatorLocation, QueryTree.Traversal.NONE);
            if(operator.getType() == Operator.Type.RELATION) {
                relationLocations.add(operatorLocation);
            }
        }

        return relationLocations;
    }*/

    /*private List<String> getTableNamesFromRelationLocations(List<List<QueryTree.Traversal>> relationLocations, QueryTree queryTree) {

        List<String> tableNames = new ArrayList<>();

        for(List<QueryTree.Traversal> relationLocation : relationLocations) {
            Relation relation = (Relation) queryTree.get(relationLocation, QueryTree.Traversal.NONE);
            String tableName = relation.getTableName();
            tableNames.add(tableName);
        }

        return tableNames;
    }

    //TODO not sure if work
    private List<Table> getReferencedTables(List<String> tableNames, List<Table> tables) {
        return tables.stream()
                .filter(table -> tableNames.stream()
                        .anyMatch(tableName -> table.getTableName().equalsIgnoreCase(tableName)))
                .collect(Collectors.toList());
    }

    private void orderReferencedTables(List<Table> tables, List<String> tableNames) {

    }

    private List<Integer> getTableCosts(List<Table> tables) {

        List<Integer> tableCosts = new ArrayList<>();

        for(Table table : tables) {
            int numberRecords = QueryCost.numberRecords(table);
            int recordSize = QueryCost.numberRecords(table);
            int blockingFactor = QueryCost.blockingFactor(recordSize);
            int blocks = QueryCost.blocks(numberRecords, blockingFactor);

            tableCosts.add(blocks);
        }

        return tableCosts;
    }

    // 5. Pushing Down Projections =====================================================================================

    /*public QueryTree pushDownProjections(QueryTree queryTree) {

        // TODO remove
        if(queryTree != null) {
            return queryTree;
        }

        // if we only have 1 table in the query tree, there is no need to push down projections
        boolean hasOneTable = getTypeOccurrence(queryTree, Operator.Type.RELATION) == 1;

        if(hasOneTable) {
            return queryTree;
        }

        int numCartesianProductsAndJoins = getTypeOccurrence(queryTree, Operator.Type.CARTESIAN_PRODUCT) +
                getTypeOccurrence(queryTree, Operator.Type.INNER_JOIN);

System.out.println(queryTree.getTreeStructure());

        // figure out what relations we have
        List<String> relationNames = new ArrayList<>();

        for(Operator operator : queryTree) {
            if(operator.getType() == Operator.Type.RELATION) {
                relationNames.add(((Relation) operator).getTableName());
            }
        }

        // for each relation, explore each node along the traversal to reach that relation
        // adding each column needed that appears with that relation's name to the working projection
        for(String relationName : relationNames) {

            List<String> projectedColumnNames = new ArrayList<>();

            List<QueryTree.Traversal> relationTraversalLocation = queryTree.getRelationLocation(relationName);
            Stack<QueryTree.Traversal> traversalStack = new Stack<>();
            traversalStack.addAll(relationTraversalLocation);

            while(! traversalStack.isEmpty()) {

                Operator operator = queryTree.get(traversalStack, QueryTree.Traversal.NONE);

                if(operator.getType() == Operator.Type.AGGREGATE_SELECTION) {

                    AggregateSelection aggregateSelection = (AggregateSelection) operator;

                    // check aggregate column names, if there is any, remove the aggregation type
                    for(String aggregateColumnName : aggregateSelection.getColumnNames()) {
                        String candidateRelation = aggregateColumnName.split("\\.")[0];

                        if(candidateRelation.equalsIgnoreCase(relationName)) {
                            if(! containsDuplicateColumnNames(aggregateColumnName, projectedColumnNames)) {
                                projectedColumnNames.add(aggregateColumnName);
                            }
                        }
                    }

                } else if(operator.getType() == Operator.Type.AGGREGATION) {

                    Aggregation aggregation = (Aggregation) operator;

                    // check group by column names
                    for(String groupByColumnName : aggregation.getGroupByColumnNames()) {
                        String candidateRelation = groupByColumnName.split("\\.")[0];
                        if(candidateRelation.equalsIgnoreCase(relationName)) {
                            if(! containsDuplicateColumnNames(groupByColumnName, projectedColumnNames)) {
                                projectedColumnNames.add(groupByColumnName);
                            }
                        }
                    }

                    // check aggregate column names, if there is any, remove the aggregation type
                    for(String aggregateColumnName : aggregation.getColumnNames()) {
                        String candidateRelation = aggregateColumnName.split("\\.")[0];
                        if(candidateRelation.equalsIgnoreCase(relationName)) {
                            if(! containsDuplicateColumnNames(aggregateColumnName, projectedColumnNames)) {
                                projectedColumnNames.add(aggregateColumnName);
                            }
                        }
                    }

                } else if(operator.getType() == Operator.Type.PROJECTION) {

                    Projection projection = (Projection) operator;

                    for(String projectedColumnName : projection.getColumnNames()) {
                        String candidateRelation = projectedColumnName.split("\\.")[0];
                        if(candidateRelation.equalsIgnoreCase(relationName)) {
                            if(! containsDuplicateColumnNames(projectedColumnName, projectedColumnNames)) {
                                projectedColumnNames.add(projectedColumnName);
                            }
                        }
                    }
                }

                else if(operator.getType() == Operator.Type.SIMPLE_SELECTION) {

                    SimpleSelection simpleSelection = (SimpleSelection) operator;

                    if(isJoinCondition(simpleSelection)) {

                        String candidateFirstRelation = simpleSelection.getColumnName().split("\\.")[0];
                        String candidateSecondRelation = simpleSelection.getValue().split("\\.")[0];

                        if(candidateFirstRelation.equalsIgnoreCase(relationName)) {
                            if(! containsDuplicateColumnNames(simpleSelection.getColumnName(), projectedColumnNames)) {
                                projectedColumnNames.add(simpleSelection.getColumnName());
                            }
                        } else if(candidateSecondRelation.equalsIgnoreCase(relationName)) {
                            if(! containsDuplicateColumnNames(simpleSelection.getValue(), projectedColumnNames)) {
                                projectedColumnNames.add(simpleSelection.getValue());
                            }
                        }

                    } else {

                        String candidateRelation = simpleSelection.getColumnName().split("\\.")[0];

                        if(candidateRelation.equalsIgnoreCase(relationName)) {
                            if(! containsDuplicateColumnNames(simpleSelection.getColumnName(), projectedColumnNames)) {
                                projectedColumnNames.add(simpleSelection.getColumnName());
                            }
                        }
                    }

                    // TODO
                } else if(operator.getType() == Operator.Type.INNER_JOIN) {

                    InnerJoin innerJoin = (InnerJoin) operator;

                    //split check dups
                    System.out.println("remove"+innerJoin.getJoinOnColumn1());
                    System.out.println(innerJoin.getJoinOnColumn2());
                }

                traversalStack.pop();
            }

            // finally add the projection node right below the cartesian product closest to the relation
            boolean foundCartesian = queryTree.get(relationTraversalLocation, QueryTree.Traversal.UP).getType() == Operator.Type.CARTESIAN_PRODUCT || queryTree.get(relationTraversalLocation, QueryTree.Traversal.UP).getType() == Operator.Type.INNER_JOIN;

            while(! foundCartesian) {
                relationTraversalLocation.remove(relationTraversalLocation.size() - 1);
                foundCartesian = queryTree.get(relationTraversalLocation, QueryTree.Traversal.UP).getType() == Operator.Type.CARTESIAN_PRODUCT || queryTree.get(relationTraversalLocation, QueryTree.Traversal.UP).getType() == Operator.Type.INNER_JOIN;
            }

            // make sure we're not adding an empty projection node!
            if(! projectedColumnNames.isEmpty()) {
                queryTree.add(relationTraversalLocation, QueryTree.Traversal.UP, new Projection(projectedColumnNames));
            }

            System.out.println(queryTree.getTreeStructure());
        }

        // if there are multiple cartesian products, will need to add additional projections below those too
        // this relation is the deepest part of the tree, so we will start there
        List<QueryTree.Traversal> traversals = queryTree.getRelationLocation(relationNames.get(0));

        // move up until reached the second cartesian product, this will be our starting point
        int numCartesianProductsAndJoinsEncountered = 0;*/
//TODO
        /*while(numCartesianProductsAndJoinsEncountered < 2) {
            traversals.remove(traversals.size() - 1);
            if(queryTree.get(traversals, QueryTree.Traversal.NONE).getType() == Operator.Type.CARTESIAN_PRODUCT ||
                    queryTree.get(traversals, QueryTree.Traversal.NONE).getType() == Operator.Type.INNER_JOIN) {
                numCartesianProductsAndJoinsEncountered++;
            }
        }*/

        // keep adding projections below cartesian products until done
        /*do {

            // will eventually insert below the current cartesian product, need to move up the tree,
            // adding columns along the way
            List<String> projectedColumnNames = new ArrayList<>();
            List<QueryTree.Traversal> tempTraversal = new ArrayList<>(traversals);

            // use temp traversal to move up until root is reached
            boolean reachedRoot = false;

            // TODO refactor at some point because code duplication big bad
            while (!reachedRoot) {
                // depending on the type of node encountered, add it what's needed to project
                Operator operator = queryTree.get(tempTraversal, QueryTree.Traversal.NONE);

                if (operator.getType() == Operator.Type.AGGREGATE_SELECTION) {
                    AggregateSelection aggregateSelection = (AggregateSelection) operator;
                    for (String aggregateColumnName : aggregateSelection.getColumnNames()) {
                        if (!containsDuplicateColumnNames(aggregateColumnName, projectedColumnNames)) {
                            projectedColumnNames.add(aggregateColumnName);
                        }
                    }

                } else if (operator.getType() == Operator.Type.AGGREGATION) {
                    Aggregation aggregation = (Aggregation) operator;
                    // check group by columns
                    for (String groupByColumnName : aggregation.getGroupByColumnNames()) {
                        if (!containsDuplicateColumnNames(groupByColumnName, projectedColumnNames)) {
                            projectedColumnNames.add(groupByColumnName);
                        }
                    }
                    // check aggregate column names
                    for (String aggregateColumnName : aggregation.getColumnNames()) {
                        if (!containsDuplicateColumnNames(aggregateColumnName, projectedColumnNames)) {
                            projectedColumnNames.add(aggregateColumnName);
                        }
                    }

                } else if (operator.getType() == Operator.Type.PROJECTION) {
                    Projection projection = (Projection) operator;
                    for (String projectedColumnName : projection.getColumnNames()) {
                        if (!containsDuplicateColumnNames(projectedColumnName, projectedColumnNames)) {
                            projectedColumnNames.add(projectedColumnName);
                        }
                    }

                } else if (operator.getType() == Operator.Type.SIMPLE_SELECTION) {
                    SimpleSelection simpleSelection = (SimpleSelection) operator;
                    if (isJoinCondition(simpleSelection)) {
                        if (!containsDuplicateColumnNames(simpleSelection.getColumnName(), projectedColumnNames)) {
                            projectedColumnNames.add(simpleSelection.getColumnName());
                        }
                        if (!containsDuplicateColumnNames(simpleSelection.getValue(), projectedColumnNames)) {
                            projectedColumnNames.add(simpleSelection.getValue());
                        }
                    } else {
                        if (!containsDuplicateColumnNames(simpleSelection.getColumnName(), projectedColumnNames)) {
                            projectedColumnNames.add(simpleSelection.getColumnName());
                        }
                    }
                }

                // check to see if we are at the root
                reachedRoot = tempTraversal.isEmpty();

                if (!reachedRoot) {
                    tempTraversal.remove(tempTraversal.size() - 1);
                }
            }

            // finally add the new projection node right below the cartesian product
            queryTree.add(traversals, QueryTree.Traversal.LEFT, new Projection(projectedColumnNames));

            numCartesianProductsAndJoinsEncountered++;

            // move up until the next cartesian product is reached
            try {

                if (queryTree.get(traversals, QueryTree.Traversal.UP).getType() != Operator.Type.CARTESIAN_PRODUCT || queryTree.get(traversals, QueryTree.Traversal.UP).getType() != Operator.Type.INNER_JOIN) {
                    while (queryTree.get(traversals, QueryTree.Traversal.UP).getType() != Operator.Type.CARTESIAN_PRODUCT || queryTree.get(traversals, QueryTree.Traversal.UP).getType() != Operator.Type.INNER_JOIN) {
                        traversals.remove(traversals.size() - 1);
                    }
                }

                traversals.remove(traversals.size() - 1);

            } catch(NullPointerException e) {
                break;
            }

        } while(numCartesianProductsAndJoinsEncountered <= numCartesianProductsAndJoins);

        return queryTree;
    }

    private int getTypeOccurrence(QueryTree queryTree, Operator.Type type) {

        int typeOccurrence = 0;

        for (Operator operator : queryTree) {
            if (operator.getType() == type) {
                typeOccurrence++;
            }
        }

        return typeOccurrence;
    }

    private boolean containsDuplicateColumnNames(String candidate, List<String> columnNames) {
        for(String columnName : columnNames) {
            if(columnName.equalsIgnoreCase(candidate)) {
                return true;
            }
        }
        return false;
    }

    // 6. Finding Subtrees to Pipeline =================================================================================

    public List<QueryTree> pipelineSubtrees(QueryTree queryTree) {

        // TODO remove
        if(queryTree != null) {
            return new ArrayList<>(Arrays.asList(new QueryTree(queryTree)));
        }

        List<QueryTree> pipelinedSubtrees = new ArrayList<>();

        // starting at the deepest node of the tree
        String relationName = null;
        boolean foundFirstRelation = false;

        for(Operator operator : queryTree) {
            if(! foundFirstRelation) {
                if (operator.getType() == Operator.Type.RELATION) {
                    relationName = ((Relation) operator).getTableName();
                    foundFirstRelation = true;
                }
            }
        }

        List<QueryTree.Traversal> traversals = queryTree.getRelationLocation(relationName);

        // identifying trees that can be pipeline (those that appear right under a join or cartesian product)
        boolean atRoot = false;

        while(! atRoot) {

            Operator operator = queryTree.get(traversals, QueryTree.Traversal.NONE);

            if(operator.getType() == Operator.Type.CARTESIAN_PRODUCT ||
                    operator.getType() == Operator.Type.INNER_JOIN) {
                // need to make sure that the candidate nodes have children, if not, can pipeline whole thing
                queryTree.tryToPipelineSubtree(traversals, QueryTree.Traversal.LEFT);
                if(queryTree.canPipelineSubtree(traversals, QueryTree.Traversal.LEFT)) {
                    //System.out.println("Pipelining: " + queryTree.get(traversals, QueryTree.Traversal.LEFT));
                }
                queryTree.tryToPipelineSubtree(traversals, QueryTree.Traversal.RIGHT);
                if(queryTree.canPipelineSubtree(traversals, QueryTree.Traversal.RIGHT)) {
                    //System.out.println("Pipelining: " + queryTree.get(traversals, QueryTree.Traversal.RIGHT));
                }
            }

            traversals.remove(traversals.size() - 1);
            atRoot = traversals.isEmpty();
        }

        // finally pipeline the root
        queryTree.tryToPipelineSubtree(traversals, QueryTree.Traversal.NONE);
        //System.out.println("Pipelining: " + queryTree.get(traversals, QueryTree.Traversal.NONE));

        pipelinedSubtrees.add(new QueryTree(queryTree));

        return pipelinedSubtrees;
    }

    // utility methods =================================================================================================

    // naive relational algebra ========================================================================================

    public String getNaiveRelationalAlgebra(QueryTree initialState) {

        StringBuilder naiveRelationalAlgebra = new StringBuilder();

        /*boolean hasOneRelation = this.getTypeOccurrence(initialState, Operator.Type.RELATION) == 1;

        if(hasOneRelation) {

            for(Operator operator : initialState) {
                naiveRelationalAlgebra.append("[").append(operator).append(" ");
            }

            // remove " "
            naiveRelationalAlgebra.deleteCharAt(naiveRelationalAlgebra.length() - 1);

        } else {


        }







        int numBrackets = initialState.getSize();

        for(int i = 0; i < numBrackets; i++) {
            naiveRelationalAlgebra.append("]");
        }

        return naiveRelationalAlgebra.toString();
    }

    // optimized relational algebra ====================================================================================

    public String getOptimizedRelationalAlgebra() {
        return "";
    }

    // file structure recommendation ===================================================================================

    public String getRecommendedFileStructures() {
        return "";
    }
}*/