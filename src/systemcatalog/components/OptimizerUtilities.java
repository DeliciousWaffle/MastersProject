package systemcatalog.components;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.trees.querytree.QueryTree;
import datastructures.trees.querytree.operator.Operator;
import datastructures.trees.querytree.operator.types.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that offers additional functionality to the Optimizer class because that class
 * got very hairy and this keeps things somewhat clean.
 */
public final class OptimizerUtilities {

    /**
     * Given a list of table names and a list of tables from the system, returns the tables referenced.
     * @param tableNames is a list of table names referenced
     * @param tables is a list of tables of the system
     * @return a list of tables referenced from the given table names
     */
    public static List<Table> getReferencedTables(List<String> tableNames, List<Table> tables) {
        return tableNames.stream()
                .map(tableName -> getReferencedTable(tableName, tables))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Given a table name and a list of tables from the system, returns the table referenced.
     * @param tableName is the table name of the table referenced
     * @param tables is a list of tables in the system
     * @return the table referenced from the table name or null if not found
     */
    public static Table getReferencedTable(String tableName, List<Table> tables) {
        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                return table;
            }
        }
        return null;
    }

    /**
     * If a "*" is used in the SELECT clause, this returns all the columns of any tables that are
     * referenced in the FROM clause. This also prefixes each column name with its respected table name.
     * If a "*" is not found in the provided list of column names, makes no changes and returns.
     * @param columnNames is a list of column names referenced in the SELECT clause, may contain a "*"
     * @param referencedTables are tables referenced in the FROM clause
     * @return a list of all column names that belong to any tables referenced in the FROM clause if
     * a "*" is present in columnNames
     */
    public static void getColumnNamesFromStar(List<String> columnNames, List<Table> referencedTables) {

        // return if there are not any column names to begin with
        if(columnNames.isEmpty()) {
            return;
        }

        // also return if "*" doesn't exist
        boolean hasStar = columnNames.get(0).equalsIgnoreCase("*");

        if(! hasStar) {
            return;
        }

        // otherwise remove the "*" and for each table, pull out it's column names and add them to the list to return
        columnNames.remove(0);

        for(Table table : referencedTables) {
            List<String> referencedTablesColumnNames = table.getColumns().stream()
                    .map(e -> table.getTableName() + "." + e.getColumnName())
                    .collect(Collectors.toList());
            columnNames.addAll(referencedTablesColumnNames);
        }
    }

    /**
     * Prefixes all column names present in the system with their respective table names.
     * @param columnNames is a list of column names to prefix
     * @param tables are the tables in the system
     */
    public static void prefixColumnNamesWithTableNames(List<String> columnNames, List<Table> tables) {
        for(int i = 0; i < columnNames.size(); i++) {
            columnNames.set(i, prefixColumnNameWithTableName(columnNames.get(i), tables));
        }
    }

    /**
     * Prefixes a column name to a table name. Involves looking through all the tables available
     * and determining which one that column belongs to.
     * @param columnName is the column name to prefix
     * @param tables are all the tables in the database
     * @return column name prefixed with the table name
     */
    public static String prefixColumnNameWithTableName(String columnName, List<Table> tables) {

        // don't need to prefix the table name if it's prefixed
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
    public static boolean hasPrefixedTableName(String columnName) {
        return columnName.contains(".");
    }

    /**
     * Adds unique column names to the provided list of column names
     * @param columnNames is the list of column names to add to
     * @param toAdd are the column names to add
     * @return list of unique column names
     */
    public static List<String> addUniqueColumnNames(List<String> columnNames, List<String> toAdd) {
        columnNames.addAll(toAdd);
        return columnNames.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Wraps quotation marks around present string values in the given list.
     * @param values is the list of values to perform this operation on
     */
    public static void addQuotationsToStringValues(List<String> values) {
        for(int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            boolean isNumeric = Parser.isNumeric(value);
            boolean hasPrefixedTableName = hasPrefixedTableName(value); // don't wrap column names in quotes
            if(! isNumeric && ! hasPrefixedTableName) {
                values.set(i, "\"" + value + "\"");
            }
        }
    }

    /**
     * @param listOfLists is a list of lists containing elements
     * @param <T> is a generic type
     * @return given a list of lists, returns the list with the fewest elements
     */
    public static <T> List<T> getListOfFewestElements(List<List<T>> listOfLists) {
        int indexOfSmallestList = 0, smallestSize = listOfLists.get(0).size();
        for(int i = 0; i < listOfLists.size(); i++) {
            int listSize = listOfLists.get(i).size();
            if(listSize < smallestSize) {
                smallestSize = listSize;
                indexOfSmallestList = i;
            }
        }
        return listOfLists.get(indexOfSmallestList);
    }

    /**
     * @param listOfLists is a list of lists containing elements
     * @param <T> is a generic type
     * @return given a list of lists, returns the list with the most elements
     */
    public static <T> List<T> getListOfMostElements(List<List<T>> listOfLists) {
        int indexOfLargestList = 0, largestSize = listOfLists.get(0).size();
        for(int i = 0; i < listOfLists.size(); i++) {
            int listSize = listOfLists.get(i).size();
            if(listSize > largestSize) {
                largestSize = listSize;
                indexOfLargestList = i;
            }
        }
        return listOfLists.get(indexOfLargestList);
    }

    /**
     * @param selections is a list of selections to check, note: if a selection with a join condition
     * is found, will remove it from this list
     * @return a list of simple selections that have a join condition
     */
    public static List<Operator> getSelectionsWithJoinConditions(List<Operator> selections) {
        List<Operator> selectionsWithJoinConditions = new ArrayList<>();
        boolean finished = false;
        while (! finished) {
            boolean madeModification = false;
            for (int i = 0; i < selections.size(); i++) {
                SimpleSelection currentSimpleSelection = (SimpleSelection) selections.get(i);
                String value = currentSimpleSelection.getValue();
                if (value.contains(".")) {
                    selectionsWithJoinConditions.add(selections.remove(i));
                    madeModification = true;
                    break;
                }
            }
            if (! madeModification) {
                finished = true;
            }
        }
        return selectionsWithJoinConditions;
    }

    /**
     * Given a list of stuff, performs Heap's algorithm in order to produce a list of lists
     * which contains every permutation of the original list. When first calling, set i to
     * the size of the list to permute and the permuted list should initially be empty.
     * Source: https://en.wikipedia.org/wiki/Heap%27s_algorithm
     * @param i is the initial size of the list before recursively calling itself
     * @param list is the list to permute
     * @param permutedList is a list containing all permutations of list, originally empty
     * @param <T> is a generic type
     */
    public static <T> void permuteList(int i, List<T> list, List<List<T>> permutedList) {
        if (i == 1) {
            permutedList.add(new ArrayList<>(list));
        } else {
            permuteList(i - 1, list, permutedList);
            for (int j = 0; j < i - 1; j++) {
                if (j % 2 == 0) {
                    swap(j, i - 1, list);
                } else {
                    swap(0, i - 1, list);
                }
                permuteList(i - 1, list, permutedList);
            }
        }
    }

    /**
     * Swaps two elements in a list. Helper method for permuteList().
     * @param i first index to swap
     * @param j second index to swap
     * @param list a referenced to the list
     * @param <T> is a generic type
     */
    public static <T> void swap(int i, int j, List<T> list) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * @param queryTrees is a list of query trees to operator on
     * @return a list of query trees that don't contain the operator specified
     */
    public static List<QueryTree> removeQueryTreesWithOperatorsOfType(List<QueryTree> queryTrees, Operator.Type type) {
        return queryTrees.stream()
                .filter(queryTree -> queryTree.getTypeOccurrence(type) == 0)
                .collect(Collectors.toList());
    }

    /**
     * @param set is the set to convert
     * @param <T> is a generic type
     * @return a deque
     */
    public static <T> Deque<T> setToDeque(Set<T> set) {
        List<T> operators = new ArrayList<>(set);
        Deque<T> stack = new ArrayDeque<>();
        while(! operators.isEmpty()) {
            stack.addFirst(operators.remove(0));
        }
        return stack;
    }

    /**
     * Given the current operator and a relation name,
     * @param operator
     * @return
     */
    public static List<String> getColumnNamesFromOperator(Operator operator) {
        List<String> columnNames = new ArrayList<>();
        switch(operator.getType()) {
            case AGGREGATE_SELECTION:
                List<String> aggregateSelectionColumnNames = ((AggregateSelection) operator).getColumnNames();
                // remove
                break;
            case AGGREGATION:
                columnNames.addAll(((Aggregation) operator));
                break;
            case PROJECTION:
                columnNames.addAll(((Projection) operator));
                break;
            case INNER_JOIN:
                columnNames.addAll(((InnerJoin) operator));
                break;
            case SIMPLE_SELECTION:
                columnNames.addAll(((SimpleSelection) operator));
                break;
            default:
                break;
        }

        return columnNames;
    }

    public static List<String> getColumnNamesWithRelationName(List<String> columnNames) {
        return columnNames
    }

    public static <T> List<T> removeDuplicatesFromList(List<T> list) {
        return list.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
        public QueryTree pushDownProjections(QueryTree queryTree) {

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
    }
}