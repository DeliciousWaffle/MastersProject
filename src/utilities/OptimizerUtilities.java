package utilities;

import datastructures.misc.Pair;
import datastructures.misc.Triple;
import datastructures.querytree.operator.types.*;
import datastructures.relation.table.Table;
import datastructures.querytree.QueryTree;
import datastructures.querytree.operator.Operator;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.FileStructure;

import java.util.*;
import java.util.stream.Collectors;

import static datastructures.querytree.QueryTree.TreeTraversal.PREORDER;
import static utilities.Utilities.isNumeric;
import static utilities.Utilities.isPresent;

/**
 * Class that offers additional functionality to the Optimizer class because that class
 * got polluted with too many internal helper methods and this keeps things somewhat clean.
 */
public final class OptimizerUtilities {

    /**
     * If a "*" is used in the SELECT clause, this returns all the columns of any tables that are
     * referenced in the FROM clause. This also prefixes each column name with its respected table name.
     * If a "*" is not found in the provided list of column names, makes no changes and returns.
     * @param columnNames is a list of column names referenced in the SELECT clause, may contain a "*"
     * @param tables are tables referenced in the FROM clause
     */
    public static void getColumnNamesFromStar(List<String> columnNames, List<Table> tables) {

        // return if there are not any column names to begin with
        if (columnNames.isEmpty()) {
            return;
        }

        // also return if "*" doesn't exist
        boolean hasStar = columnNames.get(0).equalsIgnoreCase("*");

        if (! hasStar) {
            return;
        }

        // otherwise remove the "*" and for each table, pull out it's column names and add them to the list to return
        columnNames.remove(0);

        for (Table table : tables) {
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

        for (int i = 0; i < columnNames.size(); i++) {
            columnNames.set(i, prefixColumnNameWithTableName(columnNames.get(i), tables));
        }
    }

    /**
     * Prefixes a column name to a table name. Involves looking through all the tables available
     * and determining which one that column belongs to.
     * @param columnName is the column name to prefix
     * @param tables are all the tables in the system
     * @return column name prefixed with the table name
     */
    public static String prefixColumnNameWithTableName(String columnName, List<Table> tables) {

        // don't need to prefix the table name if it's prefixed
        if (isPrefixed(columnName)) {
            return columnName;
        }

        // also don't need to prefix "." with anything
        if (columnName.equals(".")) {
            return columnName;
        }

        // otherwise prefix
        for (Table table : tables) {
            if (table.hasColumn(columnName)) {
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
    public static boolean isPrefixed(String columnName) {
        return columnName.contains(".");
    }

    /**
     * Adds unique column names to the provided list of column names
     * @param columnNames is the list of column names to add to
     * @param columnNamesToAdd are the column names to add
     * @return list of unique column names
     */
    public static List<String> addUniqueColumnNames(List<String> columnNames, List<String> columnNamesToAdd) {

        List<String> uniqueColumnNames = new ArrayList<>(columnNames);

        for (String columnNameToAdd : columnNamesToAdd) {
            if (! isPresent(columnNameToAdd, uniqueColumnNames)) {
                uniqueColumnNames.add(columnNameToAdd);
            }
        }

        return uniqueColumnNames;
    }

    /**
     * Wraps quotation marks around present string values in the given list.
     * @param values is the list of values to perform this operation on
     */
    public static void addQuotationsToStringValues(List<String> values) {

        for (int i = 0; i < values.size(); i++) {

            String value = values.get(i);
            boolean isNumeric = isNumeric(value);
            boolean hasPrefixedTableName = isPrefixed(value); // don't wrap column names in quotes

            if (! isNumeric && ! hasPrefixedTableName) {
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

        int indexOfSmallestList = 0, smallestListSize = listOfLists.get(0).size();

        for (int i = 0; i < listOfLists.size(); i++) {

            int listSize = listOfLists.get(i).size();

            if (listSize < smallestListSize) {
                smallestListSize = listSize;
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

        for (int i = 0; i < listOfLists.size(); i++) {

            int listSize = listOfLists.get(i).size();

            if (listSize > largestSize) {
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
     * which contains every permutation of the original list. When first calling, set "i" to
     * the size of "list" and "permutedList" should initially be empty.
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
     * Basically converts the given set to a "stack" which takes the form of a deque.
     * @param set is the set to convert
     * @param <T> is a generic type
     * @return a deque
     */
    public static <T> Deque<T> setToDeque(Set<T> set) {

        List<T> operators = new ArrayList<>(set);
        Deque<T> stack = new ArrayDeque<>();

        while (! operators.isEmpty()) {
            stack.addFirst(operators.remove(0));
        }

        return stack;
    }

    /**
     * Returns a list of column names that belong to the associated table name. Each column name
     * is assumed to already by prefixed with it's associated table name. I forgot why I needed this.
     * @param columnNames is a list of column names that are prefixed with the table names that they belong to
     * @param tableName is the table name to whose column names we want
     * @return a list of column names that belong to the given table name
     */
    public static List<String> getColumnNamesWithRelationName(List<String> columnNames, String tableName) {
        return columnNames.stream()
                .filter(columnName -> columnName.split("\\.")[0].equalsIgnoreCase(tableName))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Basically performs the set operation minus which means that the this method will return a list
     * of elements that are in the first list, but not the second. Ignores the casing of strings.
     * @param list is the initial list
     * @param toSubtract is the list whose elements will not appear in the final product
     * @return returns a new list containing elements that are in the first list, but not in the second
     */
    public static List<String> minus(List<String> list, List<String> toSubtract) {

        List<String> toKeep = new ArrayList<>();

        for (String element : list) {

            boolean foundElementToSubtract = false;

            for (String elementToSubtract : toSubtract) {
                if (element.equalsIgnoreCase(elementToSubtract)) {
                    foundElementToSubtract = true;
                    break;
                }
            }

            if (! foundElementToSubtract) {
                toKeep.add(element);
            }
        }

        return toKeep;
    }

    /**
     * Swaps the first and last elements of a given linked hash map.
     * @param original is the linked hash map whose first and last elements will be swapped
     * @param <K> a generic key of this map
     * @param <V> a generic value of this map
     */
    public static <K, V> Map<K, V> swapFirstAndLastElementsOfLinkedHashMap(Map<K, V> original) {

        Map<K, V> swapped = new LinkedHashMap<>();

        Iterator<Map.Entry<K, V>> iterator = original.entrySet().iterator();
        Map.Entry<K, V> firstEntry = iterator.next();
        Map.Entry<K, V> lastEntry = firstEntry; // handles case in which the original contains a single entry

        while (iterator.hasNext()) {
            lastEntry = iterator.next();
        }

        // remove the first and last entries from the original
        original.remove(firstEntry.getKey());
        original.remove(lastEntry.getKey());

        // put the last entry, put all remaining entries, and put first entry into swapped from original
        swapped.put(lastEntry.getKey(), lastEntry.getValue());
        swapped.putAll(original);
        swapped.put(firstEntry.getKey(), firstEntry.getValue());

        return swapped;
    }

    /**
     * @param original is the linked hash map whose first element will be placed last in the sequence
     * @param <K> a generic key of this map
     * @param <V> a generic value of this map
     */
    public static <K, V> void putFirstElementLastOfLinkedHashMap(Map<K, V> original) {

        // don't bother with any of this if there's only one element/no elements
        if (original.size() <= 1) {
            return;
        }

        Iterator<Map.Entry<K, V>> iterator = original.entrySet().iterator();
        Map.Entry<K, V> firstEntry = iterator.next();
        original.remove(firstEntry.getKey());
        original.put(firstEntry.getKey(), firstEntry.getValue());
    }

    /**
     * Returns the location of the first instance of a file structure conflict. These conflicts occur when
     * either a clustered b-tree or hash table is built on the same column of a table. In order to resolve,
     * build a secondary b-tree instead.
     * @param candidate is a triplet containing the table name, column name, and file structure in that order
     * @param recommendedFileStructures is a list of triplets containing table name, column name, and file
     * structure in that order
     */
    public static int getFileStructureConflictLocation(Triple<String, String, String> candidate,
                                                       List<Triple<String, String, String>> recommendedFileStructures) {

        String candidateTableName = candidate.getFirst();
        String candidateColumnName = candidate.getSecond();

        for (int i = 0; i < recommendedFileStructures.size(); i++) {

            Triple<String, String, String> recommendation = recommendedFileStructures.get(i);
            String tableName = recommendation.getFirst();
            String columnName = recommendation.getSecond();

            if (tableName.equalsIgnoreCase(candidateTableName) && columnName.equalsIgnoreCase(candidateColumnName)) {
                return i;
            }
        }

        return -1;
    }

    public static List<Triple<String, String,String>> distinctFileStructures(
            List<Triple<String, String, String>> recommendedFileStructures) {

        List<Triple<String, String, String>> distinctFileStructures = new ArrayList<>();

        for (Triple<String, String, String> recommendedFileStructure : recommendedFileStructures) {
            boolean containsRecommendedFileStructure = false;
            for (Triple<String, String, String> distinctFileStructure : distinctFileStructures) {
                boolean isEqual =
                        recommendedFileStructure.getFirst().equalsIgnoreCase(distinctFileStructure.getFirst()) &&
                        recommendedFileStructure.getSecond().equalsIgnoreCase(distinctFileStructure.getSecond()) &&
                        recommendedFileStructure.getThird().equalsIgnoreCase(distinctFileStructure.getThird());
                if (isEqual) {
                    containsRecommendedFileStructure = true;
                    break;
                }
            }
            if (! containsRecommendedFileStructure) {
                distinctFileStructures.add(recommendedFileStructure);
            }
        }

        return distinctFileStructures;
    }

    public static void removeAllFileStructures(List<Table> tables) {
        for (Table table : tables) {
            for (Column column : table.getColumns()) {
                column.setFileStructure(FileStructure.NONE);
            }
        }
    }

    /**
     * Doesn't keep inverted table names eg. <T1, T2> and <T2, T1>
     * @param tableNames
     * @return
     */
    public static List<Pair<String, String>> getAllPossibleTablePairs(List<String> tableNames) {

        List<Pair<String, String>> allPossibleTablePairs = new ArrayList<>();

        for (String currentTableName : tableNames) {
            for (String tableName : tableNames) {

                if (currentTableName.equalsIgnoreCase(tableName)) {
                    continue;
                }

                Pair<String, String> toAdd = new Pair<>(currentTableName, tableName);
                boolean isSwappedDuplicate = false;

                for (Pair<String, String> tablePair : allPossibleTablePairs) {
                    if (tablePair.getFirst().equalsIgnoreCase(toAdd.getSecond()) &&
                            tablePair.getSecond().equalsIgnoreCase(toAdd.getFirst())) {
                        isSwappedDuplicate = true;
                        break;
                    }
                }

                if (! isSwappedDuplicate) {
                    allPossibleTablePairs.add(toAdd);
                }
            }
        }

        return allPossibleTablePairs;
    }

    /**
     * Removes column names that are prefixed with a table name only if the removal doesn't produce
     * an unambiguous result. For instance, an operator containing tab1.col1, tab2.col1, tab3.col2 will
     * yield tab1.col1, tab2.col1, col2.
     * @param prefixedColumnNames is a list of prefixed column names whose prefixes will attempted to be removed
     */
    public static List<String> removePrefixedColumnNames(List<String> prefixedColumnNames) {
        return prefixedColumnNames.stream()
                .map(prefixedColumnName -> {
                    // is ambiguous, don't remove prefixing
                    if (isAmbiguousColumnName(prefixedColumnName, prefixedColumnNames)) {
                        return prefixedColumnName;
                    // remove prefixing
                    } else {
                        // is an aggregated column name
                        if (prefixedColumnName.contains("(") && prefixedColumnName.contains(")")) {
                            String aggregateFunction = prefixedColumnName.split("\\(")[0];
                            String columnName = prefixedColumnName.split("\\(")[1];
                            columnName = columnName.substring(0, columnName.length() - 1); // remove trailing ")"
                            return aggregateFunction + "(" + columnName.split("\\.")[1] + ")";
                        // regular column name
                        } else {
                            return prefixedColumnName.split("\\.")[1];
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * If the given column name is prefixed with a table name, removes the prefixed table name.
     * @param columnName is the column name whose prefixed table name shall be removed
     * @return column name without its prefixed table name
     */
    public static String removePrefixedColumnName(String columnName) {
        if (isPrefixed(columnName)) {
            return columnName.split("\\.")[1];
        }
        return columnName;
    }

    /**
     * NOTE: This is not a perfect science, would need a reference to the tables being referenced
     * in the query too. I will change this at some point.
     * @param candidate is the prefixed column name to check
     * @param columnNames is the list of prefixed columns to check the prefixed column name against
     * @return returns whether the prefixed column name would be ambiguous if it wasn't prefixed with the
     * table name it belonged to
     */
    public static boolean isAmbiguousColumnName(String candidate, List<String> columnNames) {
        return columnNames.stream()
                .map(prefixedColumnName -> isPrefixed(prefixedColumnName)
                        ? prefixedColumnName.split("\\.")[1]
                        : prefixedColumnName)
                .filter(columnName -> columnName.equalsIgnoreCase(isPrefixed(candidate)
                        ? candidate.split("\\.")[1]
                        : candidate))
                .count() >= 2;
    }

    public static boolean isJoinPredicate(String firstColumnName, String secondColumnName) {
        return firstColumnName.contains(".") && (! isNumeric(secondColumnName) && secondColumnName.contains("."));
    }

    public static <T> void reverseList(List<T> list) {
        for (int i = 0; i < list.size() / 2; i++) {
            swap(i, list.size() - i - 1, list);
        }
    }

    public static void removePrefixedColumnNamesFromQueryTrees(QueryTree queryTree) {

        List<Operator> operators = new ArrayList<>(queryTree.getOperatorsAndLocations(PREORDER).keySet());
        List<String> allReferencedColumns = operators
                .stream()
                .map(Operator::getReferencedColumnNames)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        for (Operator operator : operators) {
            switch (operator.getType()) {
                case AGGREGATE_SELECTION: {
                    AggregateSelection aggregateSelection = (AggregateSelection) operator;
                    List<String> columnNames = aggregateSelection.getColumnNames()
                            .stream()
                            .map(columnName -> {
                                if (! isAmbiguousColumnName(columnName, allReferencedColumns)) {
                                    return removePrefixedColumnName(columnName);
                                }
                                return columnName;
                            })
                            .collect(Collectors.toList());
                    aggregateSelection.setColumnNames(columnNames);
                    break;
                }
                case AGGREGATION: {
                    Aggregation aggregation = (Aggregation) operator;
                    List<String> groupByColumnNames = aggregation.getGroupByColumnNames()
                            .stream()
                            .map(groupByColumnName -> {
                                if (!isAmbiguousColumnName(groupByColumnName, allReferencedColumns)) {
                                    return removePrefixedColumnName(groupByColumnName);
                                }
                                return groupByColumnName;
                            })
                            .collect(Collectors.toList());
                    aggregation.setGroupByColumnNames(groupByColumnNames);
                    List<String> aggregatedColumnNames = aggregation.getAggregatedColumnNames()
                            .stream()
                            .map(aggregatedColumnName -> {
                                if (!isAmbiguousColumnName(aggregatedColumnName, allReferencedColumns)) {
                                    return removePrefixedColumnName(aggregatedColumnName);
                                }
                                return aggregatedColumnName;
                            })
                            .collect(Collectors.toList());
                    aggregation.setAggregatedColumnNames(aggregatedColumnNames);
                    break;
                }
                case COMPOUND_SELECTION: {
                    CompoundSelection compoundSelection = (CompoundSelection) operator;
                    List<String> columnNames = compoundSelection.getColumnNames()
                            .stream()
                            .map(columnName -> {
                                if (!isAmbiguousColumnName(columnName, allReferencedColumns)) {
                                    return removePrefixedColumnName(columnName);
                                }
                                return columnName;
                            })
                            .collect(Collectors.toList());
                    compoundSelection.setColumnNames(columnNames);
                    // there may be join columns present
                    List<String> joinColumnNames = compoundSelection.getValues();
                    for (int i = 0; i < joinColumnNames.size(); i++) {
                        String joinColumnName = joinColumnNames.get(i);
                        if ((isPrefixed(joinColumnName) && ! Utilities.isNumeric(joinColumnName)) &&
                                ! isAmbiguousColumnName(joinColumnName, allReferencedColumns)) {
                            joinColumnNames.set(i, OptimizerUtilities.removePrefixedColumnName(joinColumnName));
                        }
                    }
                    compoundSelection.setValues(joinColumnNames);
                    break;
                }
                case INNER_JOIN: {
                    InnerJoin innerJoin = (InnerJoin) operator;
                    String firstJoinColumnName = innerJoin.getFirstJoinColumnName();
                    if (! OptimizerUtilities.isAmbiguousColumnName(firstJoinColumnName, allReferencedColumns)) {
                        firstJoinColumnName = OptimizerUtilities.removePrefixedColumnName(firstJoinColumnName);
                    }
                    innerJoin.setFirstJoinColumnName(firstJoinColumnName);
                    String secondJoinColumnName = innerJoin.getSecondJoinColumnName();
                    if (! OptimizerUtilities.isAmbiguousColumnName(secondJoinColumnName, allReferencedColumns)) {
                        secondJoinColumnName = OptimizerUtilities.removePrefixedColumnName(secondJoinColumnName);
                    }
                    innerJoin.setSecondJoinColumnName(secondJoinColumnName);
                    break;
                }
                case PROJECTION: {
                    Projection projection = (Projection) operator;
                    List<String> columnNames = projection.getColumnNames()
                            .stream()
                            .map(columnName -> {
                                if (! OptimizerUtilities.isAmbiguousColumnName(columnName, allReferencedColumns)) {
                                    return OptimizerUtilities.removePrefixedColumnName(columnName);
                                }
                                return columnName;
                            })
                            .collect(Collectors.toList());
                    projection.setColumnNames(columnNames);
                    break;
                }
                case SIMPLE_SELECTION: {
                    SimpleSelection simpleSelection = (SimpleSelection) operator;
                    String columnName = simpleSelection.getColumnName();
                    if (! OptimizerUtilities.isAmbiguousColumnName(columnName, allReferencedColumns)) {
                        columnName = OptimizerUtilities.removePrefixedColumnName(columnName);
                    }
                    simpleSelection.setColumnName(columnName);
                    // there may be a join column present
                    String joinColumnName = simpleSelection.getValue();
                    if ((! Utilities.isNumeric(joinColumnName) && OptimizerUtilities.isPrefixed(joinColumnName)) &&
                            ! OptimizerUtilities.isAmbiguousColumnName(joinColumnName, allReferencedColumns)) {
                            joinColumnName = OptimizerUtilities.removePrefixedColumnName(joinColumnName);
                    }
                    simpleSelection.setValue(joinColumnName);
                    break;
                }
            }
        }
    }
}