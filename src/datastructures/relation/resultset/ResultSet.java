package datastructures.relation.resultset;

import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.TableData;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.DataType;
import enums.Keyword;
import enums.Symbol;
import utilities.Utilities;

import java.time.LocalDate;
import java.util.*;

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
    public ResultSet innerJoin(ResultSet otherResultSet, String firstColumnToJoin, String secondColumnToJoin) {

        // perform a cartesian product
        ResultSet cartesianProductResultSet = this.cartesianProduct(otherResultSet);

        // copy the columns to keep
        List<Column> joinColumns = new ArrayList<>();
        cartesianProductResultSet.columns.forEach(column -> joinColumns.add(new Column(column)));

        // get the index locations of the columns to perform the join on
        int firstColumnIndex = getColumnLocation(getColumnFromColumnName(firstColumnToJoin));
        int secondColumnIndex = getColumnLocation(getColumnFromColumnName(secondColumnToJoin));

        // keep only rows to join on
        List<List<String>> joinRows = new ArrayList<>();

        for(List<String> rows : cartesianProductResultSet.data) {
            String firstRowData = rows.get(firstColumnIndex);
            String secondRowData = rows.get(secondColumnIndex);
            if (firstRowData.equalsIgnoreCase(secondRowData)) {
                joinRows.add(rows);
            }
        }

        return new ResultSet(joinColumns, joinRows);
    }

    /**
     * Performs an aggregate function on the columns supplied for this result set and groups the result
     * according to the supplied column names. Aggregate functions include min, max, avg, count, and sum.
     * Similar to using the SELECT clause with an aggregate function along with a group by clause.
     * @param groupByColumnNames are the column names to group by, column names are assumed to be prefixed
     * @param aggregationTypes are the types of aggregations to apply to the aggregated columns
     * @param aggregatedColumnNames are the names of the columns to be aggregated, assumed to be prefixed
     * @return a new result set after being aggregated
     */
    public ResultSet aggregate(List<String> groupByColumnNames, List<String> aggregationTypes,
                               List<String> aggregatedColumnNames) {

        return null;
        // first project each of the columns that we wish to aggregate on this result set
        /*List<Column> columnsToProject = new ArrayList<>();

        for(Column columnToProject : columnsToAggregate.values()) {
            columnsToProject.add(new Column(columnToProject));
        }

        // will be using this result set to apply our aggregate functions to
        ResultSet projectedResultSet = this.projection(columnsToProject);

        // adding the columns to group by, transforms "t1.col1" into "<SomeAggregateFunction>(t1.col1)"
        List<Column> columnsToReturn = new ArrayList<>();

        for(Map.Entry<Keyword, Column> entry : columnsToAggregate.entrySet()) {

            Keyword keyword = entry.getKey();
            Column column = entry.getValue();
            String columnName = column.getColumnName();

            String groupByColumnName = keyword + "(" + columnName + ")";

            // the size will be set later based on the length of the value returned from the aggregate function
            Column groupByColumn = new Column(groupByColumnName, DataType.NUMBER, 0, 0);
            columnsToReturn.add(groupByColumn);
        }

        // data to be returned after applying the aggregate function, will only contain a single row
        List<List<String>> groupByData = new ArrayList<>();
        List<String> groupByRow = new ArrayList<>();

        for(Map.Entry<Keyword, Column> entry : columnsToAggregate.entrySet()) {

            Keyword aggregateFunction = entry.getKey();
            Column columnToAggregate = entry.getValue();

            List<String> columnData = projectedResultSet.getColumnDataAt(columnToAggregate);

            switch(aggregateFunction) {
                case MIN:
                    groupByRow.add(Double.toString(minColumnValue(columnData)));
                    break;
                case MAX:
                    groupByRow.add(Double.toString(maxColumnValue(columnData)));
                    break;
                case AVG:
                    groupByRow.add(Double.toString(avgColumnValue(columnData)));
                    break;
                case COUNT:
                    groupByRow.add(Double.toString(countColumnValue(columnData)));
                    break;
                case SUM:
                    groupByRow.add(Double.toString(sumColumnValue(columnData)));
                    break;
            }
        }

        groupByData.add(groupByRow);

        // set the size of each column to the length of the data stored there
        List<String> row = groupByData.get(0);

        for(int i = 0; i < columnsToReturn.size(); i++) {
            Column column = columnsToReturn.get(i);
            int size = row.get(i).length();
            column.setSize(size);
        }

        return new ResultSet(columnsToReturn, groupByData);*/
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

        return new ResultSet();
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

    /**
     * Orders the rows of this result set in ascending order based on the column supplied.
     * @param columnToOrderBy is the column to order by
     * @return a result set that is in ascending order based on the column supplied
     */
    public ResultSet orderByAsc(Column columnToOrderBy) {

        // copy columns
        List<Column> orderedByAscColumns = new ArrayList<>();

        for(Column column : columns) {
            orderedByAscColumns.add(new Column(column));
        }

        // get column index for this result set
        int columnIndex = 0;
        String columnNameToOrderBy = columnToOrderBy.getColumnName();

        for(int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getColumnName();
            if(columnName.equalsIgnoreCase(columnNameToOrderBy)) {
                columnIndex = i;
            }
        }

        // copy data but sorted on column name, this is not done efficiently
        List<List<String>> orderedByData = new ArrayList<>();

        for(List<String> rows : this.data) {
            orderedByData.add(new ArrayList<>(rows));
        }

        // if cell is a string, sort lexicographically, otherwise just do things normally
        boolean isNumeric = Utilities.isNumeric(orderedByData.get(0).get(columnIndex));

        // just doing a ghetto bubble sort, not efficient but am lazy
        for(int i = 0; i < orderedByData.size() - 1; i++) {
            for(int j = 0; j < orderedByData.size() - i - 1; j++) {

                if (isNumeric) {

                    int cell1 = Integer.parseInt(orderedByData.get(j).get(columnIndex));
                    int cell2 = Integer.parseInt(orderedByData.get(j + 1).get(columnIndex));

                    if(cell1 > cell2) {
                        List<String> temp = orderedByData.get(j);
                        orderedByData.set(j, orderedByData.get(j + 1));
                        orderedByData.set(j + 1, temp);
                    }

                } else {

                    String cell1 = orderedByData.get(j).get(columnIndex);
                    String cell2 = orderedByData.get(j + 1).get(columnIndex);

                    if(cell1.compareTo(cell2) > 0) {
                        List<String> temp = orderedByData.get(j);
                        orderedByData.set(j, orderedByData.get(j + 1));
                        orderedByData.set(j + 1, temp);
                    }
                }
            }
        }

        return new ResultSet(orderedByAscColumns, orderedByData);
    }

    /**
     * Orders the rows of this result set in descending order based on the column supplied.
     * @param columnToOrderBy is the column to order by
     * @return a result set that is in descending order based on the column supplied
     */
    public ResultSet orderByDesc(Column columnToOrderBy) {

        ResultSet orderedAscResultSet = orderByAsc(columnToOrderBy);

        // copy columns
        List<Column> orderedByDescColumns = new ArrayList<>();

        for(Column column : columns) {
            orderedByDescColumns.add(new Column(column));
        }

        // copy data, but reversed
        List<List<String>> orderByDescData = new ArrayList<>();

        for (int rows = orderedAscResultSet.data.size() - 1; rows >= 0; rows--) {
            orderByDescData.add(orderedAscResultSet.data.get(rows));
        }

        return new ResultSet(orderedByDescColumns, orderByDescData);
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

        for(List<String> rows : data) {
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

        for(int cols = 0; cols < columns.size(); cols++) {
            String thisColumnName = columns.get(cols).getColumnName();
            if(columnName.equalsIgnoreCase(thisColumnName)) {
                return getColumnDataAt(cols);
            }
        }

        return new ArrayList<>();
    }

    /**
     * @param columnData is the column data to search
     * @return the minimum value from the column data supplied
     */
    public double minColumnValue(List<String> columnData) {

        double min = Integer.parseInt(columnData.get(0));

        for(String data : columnData) {
            double dataValue = Double.parseDouble(data);
            if(dataValue < min) {
                min = dataValue;
            }
        }

        return min;
    }

    /**
     * @param columnData is the column data to search
     * @return the maximum value from the column data supplied
     */
    public double maxColumnValue(List<String> columnData) {

        double max = Integer.parseInt(columnData.get(0));

        for(String data : columnData) {
            double dataValue = Double.parseDouble(data);
            if(dataValue > max) {
                max = dataValue;
            }
        }

        return max;
    }

    /**
     * @param columnData is the column data to search
     * @return the average value of data within a column
     */
    public double avgColumnValue(List<String> columnData) {

        double totalValue = sumColumnValue(columnData);
        int numRows = countColumnValue(columnData);

        return totalValue / numRows;
    }

    /**
     * @param columnData is the column data to search
     * @return the number of rows in a column of data
     */
    public int countColumnValue(List<String> columnData) {
        return columnData.size();
    }

    /**
     * @param columnData is the column data to search
     * @return the summation of all data for a column
     */
    public double sumColumnValue(List<String> columnData) {

        double sum = 0;

        for(String data : columnData) {
            double dataValue = Double.parseDouble(data);
            sum += dataValue;
        }

        return sum;
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