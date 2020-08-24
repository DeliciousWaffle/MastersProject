package systemcatalog.components;

import datastructures.relation.table.Table;
import datastructures.rulegraph.RuleGraph;
import datastructures.trees.querytree.QueryTree;
import datastructures.trees.querytree.operator.*;
import datastructures.trees.querytree.operator.types.*;

import java.util.*;

/**
 * Responsible for determining the execution strategy of a query. This involves creating a query tree
 * and applying rules to it to lessen the cost of overall execution.
 */
public class Optimizer {

    // represents each state of the query tree when applying the optimization heuristic
    private RuleGraph queryRuleGraph;
    private String[] tokenizedInput;
    private List<Table> tables;
    private List<QueryTree> queryTreeStates;
    private List<String> recommendedFileStructures;
    private boolean toggleJoinOptimization;

    public Optimizer() {
        this.queryTreeStates = new ArrayList<>();
        this.recommendedFileStructures = new ArrayList<>();
        this.toggleJoinOptimization = true;
    }

    // setters ---------------------------------------------------------------------------------------------------------

    public void setRuleGraphToUse(RuleGraph queryRuleGraph) {
        this.queryRuleGraph = queryRuleGraph;
    }

    public void setTokenizedInput(String[] tokenizedInput) {
        this.tokenizedInput = tokenizedInput;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public void toggleJoinOptimization() {
        this.toggleJoinOptimization = ! toggleJoinOptimization;
    }

    // getters ---------------------------------------------------------------------------------------------------------

    public List<QueryTree> getQueryTreeStates() {
        return queryTreeStates;
    }

    public List<String> getRecommendedFileStructures() {
        return recommendedFileStructures;
    }

    // query tree creation and optimization ----------------------------------------------------------------------------

    public void optimize() {

        QueryTree workingTree = createQueryTree();
        queryTreeStates.add(workingTree);

        workingTree = cascadeSelections(workingTree);
        queryTreeStates.add(workingTree);

        workingTree = pushDownSelections(workingTree);
        queryTreeStates.add(workingTree);

        workingTree = cascadeAndPushDownProjections(workingTree);
        queryTreeStates.add(workingTree);

        workingTree = formJoins(workingTree);
        queryTreeStates.add(workingTree);

        workingTree = rearrangeLeafNodes(workingTree, tables);
        queryTreeStates.add(new QueryTree(workingTree));

        workingTree = findSubtreesToPipeline(workingTree);
        queryTreeStates.add(workingTree);
    }

    public QueryTree createQueryTree() {

        QueryTree queryTree = new QueryTree((Operator) null);

        /* 1. get the contents of the select clause and form into either a projection
              or aggregation and set as root for query tree */
        // -------------------------------------------------------------------------------------------------------------

        // getting the input
        List<String> columnNames = queryRuleGraph.getTokensAt(tokenizedInput, 1, 2);
        List<String> aggregationTypes = queryRuleGraph.getTokensAt(tokenizedInput, 3, 4, 5, 6, 7);
        List<String> aggregatedColumnNames = queryRuleGraph.getTokensAt(tokenizedInput, 9, 10);

        // prefix column names
        prefixColumnNames(columnNames, tables);
        prefixColumnNames(aggregatedColumnNames, tables);

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
        aggregationTypes = queryRuleGraph.getTokensAt(tokenizedInput, 38, 39, 40, 41, 42);
        aggregatedColumnNames = new ArrayList<>();
        aggregatedColumnNames = queryRuleGraph.getTokensAt(tokenizedInput, 44, 45);
        List<String> symbols = queryRuleGraph.getTokensAt(tokenizedInput, 47, 48, 49, 50, 51, 52);
        List<String> values = queryRuleGraph.getTokensAt(tokenizedInput , 53);

        // prefix column names
        prefixColumnNames(aggregatedColumnNames, tables);

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
        columnNames = queryRuleGraph.getTokensAt(tokenizedInput, 23);
        symbols = new ArrayList<>();
        symbols = queryRuleGraph.getTokensAt(tokenizedInput, 24, 25, 26, 27, 28, 29);
        values = new ArrayList<>();
        values = queryRuleGraph.getTokensAt(tokenizedInput, 30);

        // prefix column names
        prefixColumnNames(columnNames, tables);

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
        List<String> tableNames = queryRuleGraph.getTokensAt(tokenizedInput, 14, 17);
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

        /* 5. if a join using was used, need to set/add the selection node to account for the join criteria */
        // -------------------------------------------------------------------------------------------------------------

        // getting the table names, number of joins used, and the column names to join on
        columnNames = new ArrayList<>();
        symbols = new ArrayList<>();
        values = new ArrayList<>();

        // will need to make sure the mapping is correct which makes things complicated
        boolean canStartMapping = false;

        for(int i = 0; i < tokenizedInput.length; i++) {

            String queryToken = tokenizedInput[i];

            if(canStartMapping) {

                // if an exception is thrown, then we're done mapping
                try {

                    String firstJoinTableName = tokenizedInput[i - 1];
                    String secondJoinTableName = tokenizedInput[i + 1];
                    String joinOnColumnName = tokenizedInput[i + 4];

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

        // getting the input
        List<String> havingClauseColumnNames = queryRuleGraph.getTokensAt(tokenizedInput, 35);

        // prefixing with table names
        prefixColumnNames(havingClauseColumnNames, tables);

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

        return queryTree;
    }

    public void prefixColumnNames(List<String> columnNames, List<Table> tables) {
        for(int i = 0; i < columnNames.size(); i++) {
            columnNames.set(i, prefixColumnName(columnNames.get(i), tables));
        }
    }

    /**
     * Prefixes a column name to a table name. Involves looking through all the
     * tables available and determining which one that column belongs to.
     * @param columnName is the column name to prefix
     * @param tables are all the tables in the database
     * @return column name prefixed with the table name
     */
    public String prefixColumnName(String columnName, List<Table> tables) {

        // don't need to prefix the table name if it's already there
        if(hasPrefixedTableName(columnName)) {
            return columnName;
        }

        // also don't need to prefix "." with anything
        if(columnName.equals(".")) {
            return columnName;
        }

        // otherwise prefix
        for(Table table : tables) {
            if(table.hasColumn(columnName)) {
                columnName = table.getTableName() + "." + columnName;
                break;
            }
        }

        return columnName;
    }

    /**
     * @param columnName is the string to check
     * @return whether the candidate string is prefixed with a table name
     */
    public boolean hasPrefixedTableName(String columnName) {
        return columnName.contains(".");
    }

    public QueryTree cascadeSelections(QueryTree queryTree) {

        // check if we even need to cascade
        boolean hasCompoundSelection = false;

        for(Operator operator : queryTree) {
            if(operator.getType() == Operator.Type.COMPOUND_SELECTION) {
                hasCompoundSelection = true;
            }
        }

        if(hasCompoundSelection) {

            // get the location of the compound selection
            List<QueryTree.Traversal> traversals = new ArrayList<>();
            boolean foundCompoundSelection = false;

            while(! foundCompoundSelection) {
                foundCompoundSelection = queryTree.get(traversals, QueryTree.Traversal.DOWN)
                        .getType() == Operator.Type.COMPOUND_SELECTION;
                traversals.add(QueryTree.Traversal.DOWN);
            }

            CompoundSelection compoundSelection = (CompoundSelection)
                    queryTree.get(traversals, QueryTree.Traversal.NONE);

            queryTree.remove(traversals, QueryTree.Traversal.NONE);

            // split the compound selection into simple selections
            List<String> columnNames = compoundSelection.getColumnNames();
            List<String> symbols = compoundSelection.getSymbols();
            List<String> values = compoundSelection.getValues();

            List<SimpleSelection> simpleSelections = new ArrayList<>();

            for(int i = 0; i < columnNames.size(); i++) {
                simpleSelections.add(
                        new SimpleSelection(columnNames.get(i), symbols.get(i), values.get(i)));
            }

            // move the series of projections right above the first cartesian product or relation
            if(! traversals.isEmpty()) {
                traversals.remove(traversals.size() - 1);
            }

            for(int i = 0; i < simpleSelections.size(); i++) {
                queryTree.add(traversals, QueryTree.Traversal.DOWN, simpleSelections.get(i));
                traversals.add(QueryTree.Traversal.DOWN);
            }
        }

        return new QueryTree(queryTree);
    }

    public QueryTree pushDownSelections(QueryTree queryTree) {

        // check if we even have any simple selections
        boolean hasSimpleSelections = false;

        for(Operator operator : queryTree) {
            if(operator.getType() == Operator.Type.SIMPLE_SELECTION) {
                hasSimpleSelections = true;
            }
        }

        if(hasSimpleSelections) {

            List<SimpleSelection> selections = new ArrayList<>();
            List<QueryTree.Traversal> traversals = new ArrayList<>();

            // get the location of the first simple selection node
            boolean foundFirstSelection = false;

            while (!foundFirstSelection) {
                traversals.add(QueryTree.Traversal.DOWN);
                foundFirstSelection = queryTree.get(traversals, QueryTree.Traversal.NONE)
                        .getType() == Operator.Type.SIMPLE_SELECTION;
            }

            boolean doneGettingSelections = false;

            while(! doneGettingSelections) {

                Operator operator = queryTree.get(traversals, QueryTree.Traversal.NONE);

                if (operator.getType() == Operator.Type.SIMPLE_SELECTION) {
                    selections.add((SimpleSelection) operator);
                    queryTree.remove(traversals, QueryTree.Traversal.NONE);
                } else {
                    doneGettingSelections = true;
                }
            }

            // these will be dealt with later, as these selections will be placed above cartesian products
            List<SimpleSelection> joinSelections = new ArrayList<>();
            boolean doneFindingAllJoinSelections = false;

            while(! doneFindingAllJoinSelections) {
                boolean foundJoinSelection = false;

                for(int i = 0; i < selections.size(); i++) {
                    SimpleSelection selection = selections.get(i);

                    if(isJoinCondition(selection)) {
                        joinSelections.add(selections.remove(i));
                        foundJoinSelection = true;
                        break;
                    }
                }

                if(! foundJoinSelection) {
                   doneFindingAllJoinSelections = true;
                }
            }

            // deal with the remaining selections and place them above of their respective relations
            for(SimpleSelection selection : selections) {
                String relationName = selection.getColumnName().split("\\.")[0];
                traversals = queryTree.getRelationLocation(relationName);
                queryTree.add(traversals, QueryTree.Traversal.UP, selection);
            }

            // deal with the remaining join selections and place them above the right cartesian products
            for(SimpleSelection joinSelection : joinSelections) {

                String firstRelationName = joinSelection.getColumnName().split("\\.")[0];
                String secondRelationName = joinSelection.getValue().split("\\.")[0];

                List<QueryTree.Traversal> firstRelationLocation =
                        queryTree.getRelationLocation(firstRelationName);
                List<QueryTree.Traversal> secondRelationLocation =
                        queryTree.getRelationLocation(secondRelationName);

                // the relation with the fewest number of traversals is further up the tree,
                // which is where we want the join criteria, should be fine if traversal sizes are equal
                if(firstRelationLocation.size() < secondRelationLocation.size()) {

                    // traverse upwards until we reach a cartesian product and insert above that
                    boolean isCartesianProduct = false;

                    while(! isCartesianProduct) {

                        firstRelationLocation.remove(firstRelationLocation.size() - 1);
                        isCartesianProduct = queryTree.get(firstRelationLocation, QueryTree.Traversal.NONE)
                                .getType() == Operator.Type.CARTESIAN_PRODUCT;
                    }

                    queryTree.add(firstRelationLocation, QueryTree.Traversal.UP, joinSelection);

                } else {

                    boolean isCartesianProduct = false;

                    while(! isCartesianProduct) {
                        secondRelationLocation.remove(secondRelationLocation.size() - 1);
                        isCartesianProduct = queryTree.get(secondRelationLocation, QueryTree.Traversal.NONE)
                                .getType() == Operator.Type.CARTESIAN_PRODUCT;
                    }

                    queryTree.add(secondRelationLocation, QueryTree.Traversal.UP, joinSelection);
                }
            }
        }

        return new QueryTree(queryTree);
    }

    /**
     * @return whether the provided simple selection is a join condition.
     */
    private boolean isJoinCondition(SimpleSelection simpleSelection) {
        return simpleSelection.getValue().contains(".");
    }

    public QueryTree cascadeAndPushDownProjections(QueryTree queryTree) {

        // don't bother with any of this if you can't cascade any of the projections
        int numCartesianProducts = 0;

        for(Operator operator : queryTree) {
            if(operator.getType() == Operator.Type.CARTESIAN_PRODUCT) {
                numCartesianProducts++;
            }
        }

        boolean canCascadeProjections = numCartesianProducts > 1;

        if(! canCascadeProjections) {
            return new QueryTree(queryTree);
        }

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
                }

                else if(operator.getType() == Operator.Type.AGGREGATION) {

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
                }

                else if(operator.getType() == Operator.Type.PROJECTION) {

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
                }

                traversalStack.pop();
            }

            // finally add the projection node right below the cartesian product closest to the relation
            boolean foundCartesian = queryTree.get(relationTraversalLocation, QueryTree.Traversal.UP)
                    .getType() == Operator.Type.CARTESIAN_PRODUCT;

            while(! foundCartesian) {
                relationTraversalLocation.remove(relationTraversalLocation.size() - 1);
                foundCartesian = queryTree.get(relationTraversalLocation, QueryTree.Traversal.UP)
                        .getType() == Operator.Type.CARTESIAN_PRODUCT;
            }

            // make sure we're not adding an empty projection node!
            if(! projectedColumnNames.isEmpty()) {
                queryTree.add(relationTraversalLocation, QueryTree.Traversal.UP, new Projection(projectedColumnNames));
            }
        }

        // if there are multiple cartesian products, will need to add additional projections below those too
        // this relation is the deepest part of the tree, so we will start there
        List<QueryTree.Traversal> traversals = queryTree.getRelationLocation(relationNames.get(0));

        // move up until reached the second cartesian product, this will be our starting point
        int numCartesianProductsFound = 0;

        while(numCartesianProductsFound < 2) {
            traversals.remove(traversals.size() - 1);
            if(queryTree.get(traversals, QueryTree.Traversal.NONE)
                    .getType() == Operator.Type.CARTESIAN_PRODUCT) {
                numCartesianProductsFound++;
            }
        }

        // keep adding projections below cartesian products until done
        do {

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

            numCartesianProductsFound++;

            // move up until the next cartesian product is reached
            try {

                if (queryTree.get(traversals, QueryTree.Traversal.UP).getType() != Operator.Type.CARTESIAN_PRODUCT) {
                    while (queryTree.get(traversals, QueryTree.Traversal.UP).getType() != Operator.Type.CARTESIAN_PRODUCT) {
                        traversals.remove(traversals.size() - 1);
                    }
                }

                traversals.remove(traversals.size() - 1);

            } catch(NullPointerException e) {
                break;
            }

        } while(numCartesianProductsFound <= numCartesianProducts);

        return new QueryTree(queryTree);
    }

    private boolean containsDuplicateColumnNames(String candidate, List<String> columnNames) {
        for(String columnName : columnNames) {
            if(columnName.equalsIgnoreCase(candidate)) {
                return true;
            }
        }
        return false;
    }

    public QueryTree formJoins(QueryTree queryTree) {

        // don't bother if there is nothing to join
        boolean canFormJoins = false;

        for(Operator operator : queryTree) {
            if(operator.getType() == Operator.Type.SIMPLE_SELECTION) {
                if(isJoinCondition((SimpleSelection) operator)) {
                    canFormJoins = true;
                }
            }
        }

        if(! canFormJoins) {
            return new QueryTree(queryTree);
        }

        // locate each selection that contains a join criteria, using the first relation node
        // as a starting point since it will be the deepest node
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

        boolean atRoot = false;

        while(! atRoot) {

            Operator operator = queryTree.get(traversals, QueryTree.Traversal.NONE);

            if(operator.getType() == Operator.Type.SIMPLE_SELECTION) {
                SimpleSelection selection = (SimpleSelection) operator;

                // get the join criteria and set the cartesian product to a join node, also remove the selection
                if(isJoinCondition(selection)) {

                    String firstJoinCol = selection.getColumnName();
                    String secondJoinCol = selection.getValue();

                    queryTree.set(traversals, QueryTree.Traversal.DOWN, new InnerJoin(firstJoinCol, secondJoinCol));
                    queryTree.remove(traversals, QueryTree.Traversal.NONE);
                }
            }

            traversals.remove(traversals.size() - 1);
            atRoot = traversals.isEmpty();
        }

        return new QueryTree(queryTree);
    }

    // TODO: not sure how to handle yet, rearrangement will put all other nodes out of whack
    public QueryTree rearrangeLeafNodes(QueryTree queryTree, List<Table> tables) {
        return new QueryTree(queryTree);
    }

    public QueryTree findSubtreesToPipeline(QueryTree queryTree) {

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
        return new QueryTree(queryTree);
    }

    // utility methods for tree creation/optimization ------------------------------------------------------------------

    // file structure recommendation -----------------------------------------------------------------------------------

}