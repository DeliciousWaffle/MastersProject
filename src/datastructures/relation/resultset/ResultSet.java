package datastructures.relation.resultset;

import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.TableData;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.DataType;
import enums.Keyword;
import enums.Symbol;
import utilities.OptimizerUtilities;
import utilities.Utilities;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the data returned after execution of a query. Table data is set initially, then
 * relational algebra operations are performed to produce new result sets. Columns are prefixed
 * with the table name that they originally came from.
 */
public class ResultSet {

    private final List<Column> columns;
    private final List<List<String>> data;

    /**
     * Default constructor that initializes this result set with no data.
     */
    public ResultSet() {
        this.columns = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    /**
     * Initializes this result set with the data from the table provided. Each column name
     * will be prefixed with the table name that it belongs to.
     * @param table is the table to initialize from
     */
    public ResultSet(Table table) {

        // need to create a deep copy of the table to prevent unwanted changes to table data
        Table copyTable = new Table(table);

        // used for identifying which column belongs to which table
        for (Column column: copyTable.getColumns()) {
            column.setName(copyTable.getTableName() + "." + column.getColumnName());
        }

        this.columns = copyTable.getColumns();
        this.data = copyTable.getTableData().getData();
    }

    /**
     * Result set that is the product of applying some form of transformation to one or more previous
     * result sets. This is created internally when a relational algebra method is called.
     * @param columns are the columns of this result set
     * @param data is the data of this result set
     */
    private ResultSet(List<Column> columns, List<List<String>> data) {
        this.columns = columns;
        this.data = data;
    }

    /**
     * Creates a deep copy of the result set provided.
     * @param toCopy is the result set to make a deep copy of
     */
    public ResultSet(ResultSet toCopy) {
        this(); // initializing member variables
        for (Column toCopyColumn : toCopy.columns) {
            this.columns.add(new Column(toCopyColumn));
        }
        for (List<String> toCopyRows : toCopy.data) {
            List<String> rows = new ArrayList<>(toCopyRows);
            this.data.add(rows);
        }
    }

    // utility methods

    public List<Column> getColumns() {
        return columns;
    }

    public List<List<String>> getData() {
        return data;
    }

    public int getNumRows() {
        return data.size();
    }

    public int getNumColumns() {
        return columns.size();
    }

    public boolean isEmpty() {
        return columns.isEmpty();
    }

    public Column getColumnFromColumnName(String columnName) {

        for (Column column : columns) {
            if (column.getColumnName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }

        return null;
    }

    public List<Column> getColumnsFromColumnNames(List<String> columnNames) {

        List<Column> columns = new ArrayList<>();

        for (String columnNameToGet : columnNames) {
            columns.add(getColumnFromColumnName(columnNameToGet));
        }

        return columns;
    }

    public int getColumnLocation(Column column) {

        String columnToFindName = column.getColumnName();

        for (int i = 0; i < columns.size(); i++) {
            String resultSetColumnName = columns.get(i).getColumnName();
            if (resultSetColumnName.equalsIgnoreCase(columnToFindName)) {
                return i;
            }
        }

        System.out.println("getColumnLocationWithRespectToResultSet()");
        System.out.println("Did not find column location in result set");

        return -1;
    }

    public List<Integer> getColumnLocations(List<Column> columns) {

        List<Integer> columnLocations = new ArrayList<>();

        for (Column column : columns) {
            columnLocations.add(getColumnLocation(column));
        }

        return columnLocations;
    }

    // relational algebra transformations

    /**
     * Applies a projection of this result set with the columns supplied. This creates a new result
     * set with only the columns projected present. This is equivalent to performing an SQL SELECT
     * clause on a relation. Should not be confused with the selection method.
     * @param columnNames are the names of the columns to perform the projection on, assumed to be prefixed
     */
    public ResultSet projection(List<String> columnNames) {

        // copying the columns to project
        List<Column> columns = getColumnsFromColumnNames(columnNames);
        List<Column> projectedColumns = new ArrayList<>();
        columns.forEach(column -> projectedColumns.add(new Column(column)));

        // find the locations of each column to project
        List<Integer> locationsOfColumnsToProject = getColumnLocations(columns);

        List<List<String>> projectedData = new ArrayList<>();

        // ignore columns that are not being projected
        for (List<String> row : data) {

            List<String> columnsToAdd = new ArrayList<>();

            for (int columnToProjectLocation : locationsOfColumnsToProject) {
                columnsToAdd.add(row.get(columnToProjectLocation));
            }

            projectedData.add(columnsToAdd);
        }

        return new ResultSet(projectedColumns, projectedData);
    }

    /**
     * Applies a selection of this result set with the column to select, the symbol to
     * operate on, and a target value. This will produce a new result set containing rows
     * that satisfy the given predicate. This is equivalent to the condition found in an SQL WHERE clause.
     * @param columnName is the name of the column to operate on, assumed to be prefixed
     * @param symbolName is the symbol to use
     * @param value is the value to check
     * @return a new result set contain rows that satisfied the given predicate
     */
    public ResultSet selection(String columnName, String symbolName, String value) {

        // creating a copy of the column data
        List<Column> selectionColumns = new ArrayList<>();
        columns.forEach(col -> selectionColumns.add(new Column(col)));

        Column column = getColumnFromColumnName(columnName);
        DataType dataType = column.getDataType();
        Symbol symbol = Symbol.convertToSymbol(symbolName);
        int selectionColumnLocation = getColumnLocation(column);

        List<List<String>> selectionData = new ArrayList<>();

        for (List<String> row : data) {

            String resultSetValue = row.get(selectionColumnLocation);

            switch (dataType) {

                case NUMBER:
                    double numericValue = Double.parseDouble(value);
                    double numericResultSetValue = Double.parseDouble(resultSetValue);
                    switch (symbol) {
                        case EQUAL:
                            if (numericValue == numericResultSetValue) {
                                selectionData.add(row);
                            }
                            break;
                        case NOT_EQUAL:
                            if (numericValue != numericResultSetValue) {
                                selectionData.add(row);
                            }
                            break;
                        case GREATER_THAN:
                            if (numericResultSetValue > numericValue) {
                                selectionData.add(row);
                            }
                            break;
                        case LESS_THAN:
                            if (numericResultSetValue < numericValue) {
                                selectionData.add(row);
                            }
                            break;
                        case GREATER_THAN_OR_EQUAL:
                            if (numericResultSetValue >= numericValue) {
                                selectionData.add(row);
                            }
                            break;
                        case LESS_THAN_OR_EQUAL:
                            if (numericResultSetValue <= numericValue) {
                                selectionData.add(row);
                            }
                            break;
                    } // end switch symbol
                    break; // case NUMBER break

                case CHAR:
                    switch (symbol) {
                        case EQUAL:
                            if (value.equalsIgnoreCase(resultSetValue)) {
                                selectionData.add(row);
                            }
                            break;
                        case NOT_EQUAL:
                            if (! value.equalsIgnoreCase(resultSetValue)) {
                                selectionData.add(row);
                            }
                            break;
                    } // end switch symbol
                    break; // case CHAR break

                case DATE:
                    LocalDate dateValue = LocalDate.parse(value);
                    LocalDate resultSetDateValue = LocalDate.parse(resultSetValue);
                    switch (symbol) {
                        case EQUAL:
                            if (dateValue.equals(resultSetDateValue)) {
                                selectionData.add(row);
                            }
                            break;
                        case NOT_EQUAL:
                            if (! dateValue.equals(resultSetDateValue)) {
                                selectionData.add(row);
                            }
                            break;
                        case GREATER_THAN:
                            if (resultSetDateValue.compareTo(dateValue) > 0) {
                                selectionData.add(row);
                            }
                            break;
                        case LESS_THAN:
                            if (resultSetDateValue.compareTo(dateValue) < 0) {
                                selectionData.add(row);
                            }
                            break;
                        case GREATER_THAN_OR_EQUAL:
                            if (resultSetDateValue.compareTo(dateValue) >= 0) {
                                selectionData.add(row);
                            }
                            break;
                        case LESS_THAN_OR_EQUAL:
                            if (resultSetDateValue.compareTo(dateValue) <= 0) {
                                selectionData.add(row);
                            }
                            break;
                    }
                    break;
            }
        }

        return new ResultSet(selectionColumns, selectionData);
    }

    /**
     * Performs a cartesian product on this result set and another result set,
     * combining the two. This is equivalent to an SQL FROM clause that lists more than 1 table.
     * @param otherResultSet is the other result set to perform a cartesian product on
     */
    public ResultSet cartesianProduct(ResultSet otherResultSet) {

        // add the other result set's columns to this result set's columns
        List<Column> cartesianProductColumns = new ArrayList<>();
        columns.forEach(column -> cartesianProductColumns.add(new Column(column)));
        otherResultSet.columns.forEach(column -> cartesianProductColumns.add(new Column(column)));

        List<List<String>> cartesianProductData = new ArrayList<>();

        for(int theseRows = 0; theseRows < data.size(); theseRows++) {
            for(int otherRows = 0; otherRows < otherResultSet.data.size(); otherRows++) {

                List<String> rowToAdd = new ArrayList<>();

                rowToAdd.addAll(data.get(theseRows));
                rowToAdd.addAll(otherResultSet.data.get(otherRows));

                cartesianProductData.add(rowToAdd);
            }
        }

        return new ResultSet(cartesianProductColumns, cartesianProductData);
    }

    /**
     * Performs an inner join on this result set and another result set, combining the two
     * joined on the columns supplied. For now, only supporting the equi-join.
     * @param otherResultSet is the result set to perform an inner join on
     * @param firstColumnToJoin is the column to join on from this result set
     * @param secondColumnToJoin is the column to join on from the other result set
     */
    public ResultSet innerJoin(ResultSet otherResultSet, String firstColumnToJoin, String joinSymbolName,
                               String secondColumnToJoin) {

        // perform a cartesian product
        ResultSet cartesianProductResultSet = this.cartesianProduct(otherResultSet);

        // copy the columns to keep
        List<Column> joinColumns = new ArrayList<>();
        cartesianProductResultSet.columns.forEach(column -> joinColumns.add(new Column(column)));

        // get the index locations of the columns to perform the join on
        int firstColumnIndex = cartesianProductResultSet.getColumnLocation(
                cartesianProductResultSet.getColumnFromColumnName(firstColumnToJoin));
        int secondColumnIndex = cartesianProductResultSet.getColumnLocation(
                        cartesianProductResultSet.getColumnFromColumnName(secondColumnToJoin));
        Symbol joinSymbol = Symbol.convertToSymbol(joinSymbolName);
        DataType bothColumnsDataType =
                cartesianProductResultSet.getColumnFromColumnName(firstColumnToJoin).getDataType();

        // keep only rows to join on
        List<List<String>> joinRows = new ArrayList<>();

        for(List<String> rows : cartesianProductResultSet.data) {
            String firstRowData = rows.get(firstColumnIndex);
            String secondRowData = rows.get(secondColumnIndex);
            switch (joinSymbol) {
                case EQUAL: {
                    if (firstRowData.equalsIgnoreCase(secondRowData)) {
                        joinRows.add(rows);
                    }
                    break;
                }
                case NOT_EQUAL: {
                    if (! firstRowData.equalsIgnoreCase(secondRowData)) {
                        joinRows.add(rows);
                    }
                    break;
                }
                case LESS_THAN: {
                    if (bothColumnsDataType == DataType.NUMBER) {
                        if (Double.parseDouble(firstRowData) < Double.parseDouble(secondRowData)) {
                            joinRows.add(rows);
                        }
                    } else {
                        if (LocalDate.parse(firstRowData).compareTo(LocalDate.parse(secondRowData)) < 0) {
                            joinRows.add(rows);
                        }
                    }
                    break;
                }
                case GREATER_THAN: {
                    if (bothColumnsDataType == DataType.NUMBER) {
                        if (Double.parseDouble(firstRowData) > Double.parseDouble(secondRowData)) {
                            joinRows.add(rows);
                        }
                    } else {
                        if (LocalDate.parse(firstRowData).compareTo(LocalDate.parse(secondRowData)) > 0) {
                            joinRows.add(rows);
                        }
                    }
                    break;
                }
                case LESS_THAN_OR_EQUAL: {
                    if (bothColumnsDataType == DataType.NUMBER) {
                        if (Double.parseDouble(firstRowData) <= Double.parseDouble(secondRowData)) {
                            joinRows.add(rows);
                        }
                    } else {
                        if (LocalDate.parse(firstRowData).compareTo(LocalDate.parse(secondRowData)) <= 0) {
                            joinRows.add(rows);
                        }
                    }
                    break;
                }
                case GREATER_THAN_OR_EQUAL: {
                    if (bothColumnsDataType == DataType.NUMBER) {
                        if (Double.parseDouble(firstRowData) >= Double.parseDouble(secondRowData)) {
                            joinRows.add(rows);
                        }
                    } else {
                        if (LocalDate.parse(firstRowData).compareTo(LocalDate.parse(secondRowData)) >= 0) {
                            joinRows.add(rows);
                        }
                    }
                    break;
                }
            }
        }

        return new ResultSet(joinColumns, joinRows);
    }

    /**
     * Performs an aggregate function on the columns supplied for this result set and groups the result
     * according to the supplied column names. Aggregate functions include min, max, avg, count, and sum.
     * Similar to using the SELECT clause with an aggregate function along with a group by clause.
     * @param columnNamesToGroupBy are the column names to group by, column names are assumed to be prefixed
     * @param aggregationTypes are the types of aggregations to apply to the aggregated columns
     * @param columnNamesToAggregate are the names of the columns to be aggregated, assumed to be prefixed
     * @return a new result set after being aggregated
     */
    public ResultSet aggregate(List<String> columnNamesToGroupBy, List<String> aggregationTypes,
                               List<String> columnNamesToAggregate) {

        // copying each column
        List<Column> columnsToGroupByCopy = columnNamesToGroupBy.stream()
                .map(this::getColumnFromColumnName)
                .map(Column::new)
                .collect(Collectors.toList());

        List<Column> columnsToAggregateCopy = new ArrayList<>();

        for (int i = 0; i < aggregationTypes.size(); i++) {

            // append the aggregation type to the aggregated column name and change its type
            String aggregationType = aggregationTypes.get(i);
            String aggregatedColumnName = columnNamesToAggregate.get(i);
            String newColumnName = aggregationType + "(" + aggregatedColumnName + ")";

            Column columnToAdd = new Column(getColumnFromColumnName(aggregatedColumnName));
            columnToAdd.setName(newColumnName);
            // performing COUNT() on a char data type produces a number, not a char
            if (columnToAdd.getDataType() == DataType.CHAR) {
                columnToAdd.setDataType(DataType.NUMBER);
            }

            columnsToAggregateCopy.add(columnToAdd);
        }

        List<Column> columnsToAggregate = columnNamesToAggregate.stream()
                .map(this::getColumnFromColumnName)
                .collect(Collectors.toList());

        boolean hasGroupByColumns = ! columnNamesToGroupBy.isEmpty();

        // no group by columns, just perform each aggregated function and return the row created
        if (! hasGroupByColumns) {

            List<String> rowToReturn = new ArrayList<>();

            for (int i = 0; i < columnsToAggregate.size(); i++) {

                // extract the data needed from the column in order to produce the aggregation
                Column columnToAggregate = columnsToAggregate.get(i);
                List<String> columnsData = getColumnDataAt(columnToAggregate);
                int decimalSize = columnToAggregate.getDecimalSize();
                DataType dataType = columnToAggregate.getDataType();
                Keyword aggregationType = Keyword.toKeyword(aggregationTypes.get(i));

                // get the result of the aggregation and add it to the row to return
                rowToReturn.add(performAggregation(columnsData, decimalSize, dataType, aggregationType));
            }

            // plop the row
            List<List<String>> toReturnData = new ArrayList<>();
            toReturnData.add(rowToReturn);

            return new ResultSet(columnsToAggregateCopy, toReturnData);
        }

        // end if, there are group by columns

        // find each group by column's location and aggregate column location
        // with respect to the columns of the result set being processed
        List<Integer> groupByColumnLocations = getColumnLocations(getColumnsFromColumnNames(columnNamesToGroupBy));
        List<Integer> aggregateColumnLocations = getColumnLocations(getColumnsFromColumnNames(columnNamesToAggregate));

        // project the columns to keep (this only includes group by columns, aggregated columns get added later)
        ResultSet projection = projection(columnNamesToGroupBy);

        // get the unique rows according to the projected data
        List<List<String>> uniqueProjectedRows = uniqueRows(projection.data);

        List<List<String>> rowsToReturn = new ArrayList<>();

        // for each unique projected row, go through all of the original data,
        // finding matches for that unique projected row
        for (List<String> uniqueProjectedRow : uniqueProjectedRows) {

            // when a match is found, add it to the list of column data to aggregate
            List<List<String>> columnDataToAggregate = new ArrayList<>();

            for (List<String> row : data) {
                if (containsMatchingElementsAt(uniqueProjectedRow, row, groupByColumnLocations)) {
                    columnDataToAggregate.add(row);
                }
            }

            // once we got all rows corresponding with that unique projected row, for each column to aggregate,
            // perform that aggregation, adding the result to the unique projected row to return
            // each aggregated column will be added to rowToAdd which will then be added to rowsToReturn
            List<String> rowToAdd = new ArrayList<>(uniqueProjectedRow);

            for (int i = 0; i < aggregateColumnLocations.size(); i++) {

                // get the data needed to perform the aggregation for this column of data
                int aggregateColumnLocation = aggregateColumnLocations.get(i);
                List<String> columnData = getColumnDataAt(columnDataToAggregate, aggregateColumnLocation);
                int decimalSize = columns.get(aggregateColumnLocation).getDecimalSize();
                DataType dataType = columns.get(aggregateColumnLocation).getDataType();
                Keyword aggregationToPerform = Keyword.toKeyword(aggregationTypes.get(i));

                // perform the aggregation and add it to the row
                rowToAdd.add(performAggregation(columnData, decimalSize, dataType, aggregationToPerform));
            }

            rowsToReturn.add(rowToAdd);
        }

        columnsToGroupByCopy.addAll(columnsToAggregateCopy);

        return new ResultSet(columnsToGroupByCopy, rowsToReturn);
    }

    private List<List<String>> uniqueRows(List<List<String>> allRows) {

        List<List<String>> uniqueRows = new ArrayList<>();

        for (List<String> row : allRows) {
            boolean foundRow = false;
            for (List<String> uniqueRow : uniqueRows) {
                if (hasEqualRows(row, uniqueRow)) {
                    foundRow = true;
                    break;
                }
            }
            if (! foundRow) {
                uniqueRows.add(row);
            }
        }

        return uniqueRows;
    }

    public boolean containsMatchingElementsAt(List<String> uniqueProjectedRow, List<String> row,
                                              List<Integer> locations) {

        for (int i = 0, j = 0; i < locations.size(); i++, j++) {
            if (! uniqueProjectedRow.get(i).equalsIgnoreCase(row.get(locations.get(j)))) {
                return false;
            }
        }

        return true;
    }

    public String performAggregation(List<String> columnData, int decimalSize, DataType dataType,
                                     Keyword aggregationType) {

        // for decimal formatting
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < decimalSize; j++) {
            sb.append("0");
        }

        DecimalFormat decimalFormat = new DecimalFormat("#." + sb.toString());
        decimalFormat.setDecimalSeparatorAlwaysShown(false);

        String toReturn = "";

        // process the data and add it to the data to be returned
        switch (dataType) {
            case NUMBER: {
                List<Double> columnsDataAsNumber = columnData.stream()
                        .map(Double::parseDouble)
                        .collect(Collectors.toList());
                switch (aggregationType) {
                    case MIN:
                        double min = minDoubleInColumnData(columnsDataAsNumber);
                        toReturn = decimalFormat.format(min);
                        break;
                    case MAX:
                        double max = maxDoubleInColumnData(columnsDataAsNumber);
                        toReturn = decimalFormat.format(max);
                        break;
                    case AVG:
                        double avg = avgDoubleInColumnData(columnsDataAsNumber);
                        toReturn = decimalFormat.format(avg);
                        break;
                    case COUNT:
                        toReturn = Integer.toString(columnsDataAsNumber.size());
                        break;
                    case SUM:
                        double sum = sumDoubleInColumnData(columnsDataAsNumber);
                        toReturn = decimalFormat.format(sum);
                        break;
                }
                break;
            }
            case DATE: {
                List<LocalDate> columnsDataAsDate = columnData.stream()
                        .map(LocalDate::parse)
                        .collect(Collectors.toList());
                switch (aggregationType) {
                    case MIN:
                        toReturn = minDateInColumnData(columnsDataAsDate).toString();
                        break;
                    case MAX:
                        toReturn = maxDataInColumnData(columnsDataAsDate).toString();
                        break;
                    case AVG:
                        toReturn = avgDateInColumnData(columnsDataAsDate).toString();
                        break;
                    case COUNT:
                        toReturn = Integer.toString(columnsDataAsDate.size());
                        break;
                    case SUM:
                        toReturn = sumDateInColumnData(columnsDataAsDate).toString();
                        break;
                }
                break;
            }
            case CHAR: {
                toReturn = Integer.toString(columnData.size());
                break;
            }
        }

        return toReturn;
    }

    /**
     * Applies the having clause to this result set. This returns a new result set that
     * meets the condition of the having clause. As of right now, only allows for a single condition.
     * A result set must have been grouped by prior to using this method or it won't work.
     * @param aggregationTypes are the aggregation types to be applied to the column
     * @param columnNames is a list of column names prefixed with the table name that they belong to
     * @param symbols is a list of symbols
     * @param values is a list of values to check
     * @return a result set that met the having clause's condition
     */
    public ResultSet having(List<String> aggregationTypes, List<String> columnNames, List<String> symbols,
                            List<String> values) {

        // convert into the correct column name
        String aggregatedColumnName = aggregationTypes.get(0) + "(" + columnNames.get(0) + ")";

        // each predicate is essentially a selection
        ResultSet resultSet = selection(aggregatedColumnName, symbols.get(0), values.get(0));

        // if there is more than 1 predicate, process those too
        for (int i = 1; i < aggregationTypes.size(); i++) {
            aggregatedColumnName = aggregationTypes.get(i) + "(" + columnNames.get(i) + ")";
            resultSet = resultSet.selection(aggregatedColumnName, symbols.get(i), values.get(i));
        }

        return resultSet;
    }

    /**
     * Intersects this result set with the one provided. This means that only rows in
     * this result set that match with the one provided are returned.
     * @param otherResultSet is the other result set to perform the intersection on
     */
    public ResultSet intersection(ResultSet otherResultSet) {

        // copying the intersected columns
        List<Column> intersectionColumns = new ArrayList<>();
        columns.forEach(column -> intersectionColumns.add(new Column(column)));

        List<List<String>> intersectedData = new ArrayList<>();

        for (List<String> theseRows : data) {
            for (List<String> otherRows : otherResultSet.data) {
                if (hasEqualRows(theseRows, otherRows)) {
                    intersectedData.add(theseRows);
                    break;
                }
            }
        }

        return new ResultSet(intersectionColumns, intersectedData);
    }

    /**
     * Unions this result set with the one provided. This means that rows from this result
     * set and the one provided will be added to this result set. Duplicates will not appear.
     * @param otherResultSet is other result set to perform the union on
     */
    public ResultSet union(ResultSet otherResultSet) {

        // copying the columns
        List<Column> unionColumns = new ArrayList<>();
        columns.forEach(column -> unionColumns.add(new Column(column)));

        for (Column otherColumn : otherResultSet.columns) {
            boolean equalColumn = false;
            for (Column column : unionColumns) {
                if (otherColumn.equals(column)) {
                    equalColumn = true;
                    break;
                }
            }
            if (! equalColumn) {
                unionColumns.add(otherColumn);
            }
        }

        // add all rows from this result set
        List<List<String>> unionData = new ArrayList<>(data);

        // add all rows from other result set, don't add duplicates!
        for (List<String> otherRows : otherResultSet.data) {
            boolean foundDuplicate = false;
            for (List<String> theseRows : data) {
                if (hasEqualRows(otherRows, theseRows)) {
                    foundDuplicate = true;
                    break;
                }
            }
            if (! foundDuplicate) {
                unionData.add(otherRows);
            }
        }

        return new ResultSet(unionColumns, unionData);
    }

    public void orderByAsc(List<String> columnNamesToOrderBy) {

        List<Integer> columnLocations = getColumnLocations(getColumnsFromColumnNames(columnNamesToOrderBy));
        columnLocations.add(0, -1); // used for the helper method

        for (int i = 1; i < columnLocations.size(); i++) {
            int prevSortInd = columnLocations.get(i - 1);
            int sortInd = columnLocations.get(i);
            orderGroupedRow(data, sortInd, prevSortInd);
        }
    }

    public void orderGroupedRow(List<List<String>> groupedRows, int sortInd, int lastSortInd) {
        for (int i = 0; i < groupedRows.size() - 1; i++) {
            for (int j = 0; j < groupedRows.size() - i - 1; j++) {
                if (Utilities.isNumeric(groupedRows.get(j).get(sortInd))) {
                    // equal rows
                    if (lastSortInd == -1 || groupedRows.get(j).get(lastSortInd).equalsIgnoreCase(groupedRows.get(j + 1).get(lastSortInd))) {
                        if (Double.parseDouble(groupedRows.get(j).get(sortInd)) > Double.parseDouble(groupedRows.get(j + 1).get(sortInd))) {
                            swap(data, j, j + 1);
                        }
                    }
                } else {
                    if (lastSortInd == -1 || groupedRows.get(j).get(lastSortInd).equalsIgnoreCase(groupedRows.get(j + 1).get(lastSortInd))) {
                        if (groupedRows.get(j).get(sortInd).compareTo(groupedRows.get(j + 1).get(sortInd)) > 0) {
                            swap(data, j, j + 1);
                        }
                    }
                }
            }
        }
    }

    private void swap(List<List<String>> data, int i, int j) {
        List<String> temp = data.get(i);
        data.set(i, data.get(j));
        data.set(j, temp);
    }

    // helper methods for above transformation methods -----------------------------------------------------------------

    /**
     * Compares the values contained in the first row and compares them to those of
     * the second row to check for equality. Two rows are equal if the values and order
     * of both are equal.
     * @param row1 is the first row to compare
     * @param row2 is the second row to compare
     * @return whether these two rows are equal
     */
    public boolean hasEqualRows(List<String> row1, List<String> row2) {

        for (int cols = 0; cols < row1.size(); cols++) {
            String col1 = row1.get(cols);
            String col2 = row2.get(cols);
            if (! col1.equalsIgnoreCase(col2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a list of data associated with a column.
     * @param columnIndex is the index of the column to get the data from
     * @return the rows of this column
     */
    public List<String> getColumnDataAt(int columnIndex) {

        List<String> columnData = new ArrayList<>();

        for (List<String> rows : data) {
            String data = rows.get(columnIndex);
            columnData.add(data);
        }

        return columnData;
    }

    /**
     * @param column is the column to get the data from
     * @return the rows of this column
     */
    public List<String> getColumnDataAt(Column column) {

        String columnName = column.getColumnName();

        for( int cols = 0; cols < columns.size(); cols++) {
            String thisColumnName = columns.get(cols).getColumnName();
            if (columnName.equalsIgnoreCase(thisColumnName)) {
                return getColumnDataAt(cols);
            }
        }

        return new ArrayList<>();
    }

    public List<String> getColumnDataAt(List<List<String>> rows, int colInd) {
        List<String> columnData = new ArrayList<>();
        for (List<String> row : rows) {
            columnData.add(row.get(colInd));
        }
        return columnData;
    }

    public double minDoubleInColumnData(List<Double> columnData) {

        double min = columnData.get(0);

        for (Double data : columnData) {
            if (data < min) {
                min = data;
            }
        }

        return min;
    }

    public LocalDate minDateInColumnData(List<LocalDate> columnData) {

        LocalDate min = columnData.get(0);

        for (LocalDate localDate : columnData) {
            if (localDate.isBefore(min)) {
                min = localDate;
            }
        }

        return min;
    }

    public double maxDoubleInColumnData(List<Double> columnData) {

        double max = columnData.get(0);

        for (Double data : columnData) {
            if(data > max) {
                max = data;
            }
        }

        return max;
    }

    public LocalDate maxDataInColumnData(List<LocalDate> columnData) {

        LocalDate max = columnData.get(0);

        for (LocalDate localDate : columnData) {
            if (localDate.isAfter(max)) {
                max = localDate;
            }
        }

        return max;
    }

    public double avgDoubleInColumnData(List<Double> columnData) {

        double totalValue = sumDoubleInColumnData(columnData);
        int numRows = columnData.size();

        return totalValue / numRows;
    }

    public LocalDate avgDateInColumnData(List<LocalDate> columnData) {

        BigInteger totalMillis = BigInteger.ZERO;

        for (LocalDate localDate : columnData) {
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            totalMillis = totalMillis.add(BigInteger.valueOf(date.getTime()));
        }

        BigInteger averageMillis = totalMillis.divide(BigInteger.valueOf(columnData.size()));
        Date averageDate = new Date(averageMillis.longValue());

        return averageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public double sumDoubleInColumnData(List<Double> columnData) {

        double sum = 0;

        for(Double data : columnData) {
            double dataValue = data;
            sum += dataValue;
        }

        return sum;
    }

    public LocalDate sumDateInColumnData(List<LocalDate> columnData) {

        BigInteger totalMillis = BigInteger.ZERO;

        for (LocalDate localDate : columnData) {
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            totalMillis = totalMillis.add(BigInteger.valueOf(date.getTime()));
        }

        Date date = new Date(totalMillis.longValue());
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * @return a string representation of this result set
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        // used for formatting purposes
        ArrayList<Integer> paddingAmountList = new ArrayList<>();

        // figuring out padding amounts and adding to the list of column sizes
        for (Column column : columns) {

            int columnNameLength = column.getColumnName().length();
            int maxNumSpaces = column.size();

            if (columnNameLength > maxNumSpaces) {
                paddingAmountList.add(columnNameLength);
            } else {
                paddingAmountList.add(column.size());
            }
        }

        for (Column column : columns) {

            String columnName = column.getColumnName();

            StringBuilder spaces = new StringBuilder();
            int columnNameLength = columnName.length();
            int maxNumSpaces = column.size();
            int numSpacesToPad = maxNumSpaces - columnNameLength;

            boolean needsPadding = numSpacesToPad > 0;

            if (needsPadding) {
                for (int i = 0; i < numSpacesToPad; i++) {
                    spaces.append(" ");
                }
            }

            stringBuilder.append(columnName).append(spaces).append(" ");
        }

        stringBuilder.append("\n");

        // adding a dashed line
        for (Column column : columns) {

            int maxColumnNameLength = column.size();
            int columnNameLength = column.getColumnName().length();
            int numDashes = Math.max(maxColumnNameLength, columnNameLength);

            StringBuilder dashes = new StringBuilder();

            for (int i = 0; i < numDashes; i++) {
                dashes.append("-");
            }

            stringBuilder.append(dashes).append(" ");
        }

        stringBuilder.append("\n");

        // a very bad hack that gets the job done
        stringBuilder.append(new TableData(paddingAmountList, data).toString());

        if (! isEmpty()) {
            stringBuilder.append("\n");

        }

        stringBuilder.append("Number of Rows: ").append(getNumRows());

        return stringBuilder.toString();
    }
}