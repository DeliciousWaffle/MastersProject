package datastructures.relation.resultset;

import datastructures.trees.conditiontree.component.Condition;
import datastructures.trees.conditiontree.ConditionExpression;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.TableData;
import datastructures.relation.table.Table;
import systemcatalog.components.Parser;
import datastructures.relation.table.component.DataType;
import utilities.enums.Keyword;
import utilities.enums.Symbol;

import java.util.*;

/**
 * Represents the data returned after execution of a query. Table data is set initially, then operations
 * are performed on the result set to reflect what the query is asking. This class is immutable which may
 * pose some performance issues from all the instantiation going on. This is a problem for future me though.
 * Usage: ResultSet resultSet = new ResultSet(Table table);
 *        resultSet = resultSet.<some operation>(<some argument(s)>);
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
     * Initializes this result set with the data from the table provided.
     * @param table is the table to initialize from
     */
    public ResultSet(Table table) {

        // need to create a deep copy of the table to prevent unwanted changes to table data
        Table copyTable = new Table(table);

        // used for identifying which column belongs to which table
        for(Column column: copyTable.getColumns()) {
            column.setName(copyTable.getTableName() + "." + column.getColumnName());
        }

        this.columns = copyTable.getColumns();
        this.data = copyTable.getTableData().getData();
    }

    /**
     * Result set that is the product of applying some form of transformation to one
     * or more previous result sets.
     * @param columns are the columns of this result set
     * @param data is the data of this result set
     */
    public ResultSet(List<Column> columns, List<List<String>> data) {
        this.columns = columns;
        this.data = data;
    }

    /**
     * Creates a deep copy of the result set provided.
     * @param toCopy is the result set to make a deep copy of
     */
    public ResultSet(ResultSet toCopy) {
        this(); // initializing member variables
        for(Column toCopyColumn : toCopy.columns) {
            this.columns.add(new Column(toCopyColumn));
        }
        for(List<String> toCopyRows : toCopy.data) {
            List<String> rows = new ArrayList<>(toCopyRows);
            this.data.add(rows);
        }
    }

    // methods ---------------------------------------------------------------------------------------------------------

    /**
     * @return the columns of this result set
     */
    public List<Column> getColumns() { return columns; }

    /**
     * @return the data of this result set
     */
    public List<List<String>> getData() {
        return data;
    }

    /**
     * @return the number of rows of this result set
     */
    public int getNumRows() {
        return data.size();
    }

    /**
     * @return the number of columns of this result set
     */
    public int getNumCols() {
        return columns.size();
    }

    /**
     * @return whether this result set is empty
     */
    public boolean isEmpty() { return columns.isEmpty(); }

    // applying transformations on the data to fit the query request using relational algebra --------------------------

    /**
     * Applies a projection of this result set with the columns supplied.
     * This is equivalent to performing an SQL SELECT clause on a relation.
     * Careful not to confuse this method with the selection method!
     * @param columnsToProject are the columns to perform the projection on
     */
    public ResultSet projection(List<Column> columnsToProject) {

        List<Column> projectedColumns = new ArrayList<>();

        for(Column columnToProject : columnsToProject) {
            projectedColumns.add(new Column(columnToProject));
        }

        // find the locations of each column to project
        List<Integer> columnsToProjectIndexes = new ArrayList<>();

        for(int cols = 0; cols < columns.size(); cols++) {
            String columnName = columns.get(cols).getColumnName();

            for(Column columnToProject : columnsToProject) {
                String columnToProjectName = columnToProject.getColumnName();

                if(columnName.equalsIgnoreCase(columnToProjectName)) {
                    columnsToProjectIndexes.add(cols);
                    break;
                }
            }
        }

        List<List<String>> projectedData = new ArrayList<>();

        // ignore columns that are not being projected
        for(int rows = 0; rows < data.size(); rows++) {
            List<String> columnsToAdd = new ArrayList<>();

            for(int cols = 0; cols < data.get(rows).size(); cols++) {
                boolean projectThisColumn = false;

                for(int columnToProjectIndex : columnsToProjectIndexes) {
                    if(cols == columnToProjectIndex) {
                        projectThisColumn = true;
                        break;
                    }
                }

                if(projectThisColumn) {
                    columnsToAdd.add(data.get(rows).get(cols));
                }
            }

            projectedData.add(columnsToAdd);
        }

        return new ResultSet(projectedColumns, projectedData);
    }

    /**
     * TODO: this method is kind of a hot mess right now, will need to refactor at some point
     * TODO: also does not 100% work with more complicated condition expressions
     * Performs a selection on the given condition expression. Will evaluate AND expressions first
     * before moving on to OR conditions. Equivalent to SQL WHERE clause that has many conditions that
     * need to be satisfied.
     * @param conditionExpression is an expression of conditions to evaluate
     * @return the result set from applying this transformation
     */
    public ResultSet selection(ConditionExpression conditionExpression) {

        // will need to resolve the ANDs first before resolving the ORs, using a queue for no real reason
        Queue<Queue<ResultSet>> resolvedAndResultSets = new LinkedList<>();
        Queue<ResultSet> andResultSets = new LinkedList<>();

        List<Condition> conditions = conditionExpression.getConditions();
        List<ConditionExpression.Type> operators = conditionExpression.getOperators();

        for(int i = 0; i < conditions.size(); i++) {

            Condition currentCondition = conditions.get(i);
            ConditionExpression.Type operator = operators.get(i);

            ResultSet workingResultSet = selection(currentCondition);

            switch(operator) {
                case NONE:
                case AND:
                    andResultSets.offer(workingResultSet);
                    break;
                case OR:
                    resolvedAndResultSets.offer(andResultSets);
                    andResultSets = new LinkedList<>();
                    andResultSets.offer(workingResultSet);
                    break;

            }
        }

        if(! andResultSets.isEmpty()) {
            resolvedAndResultSets.offer(andResultSets);
        }

        ResultSet toReturn = new ResultSet();

        // outer loop = resolve each or condition
        while(! resolvedAndResultSets.isEmpty()) {

            Queue<ResultSet> temp = resolvedAndResultSets.poll();
            ResultSet currentAnds = temp.poll();

            // inner loop = resolve each and condition
            while(! temp.isEmpty()) {
                if(currentAnds != null) {
                    currentAnds = currentAnds.intersection(temp.poll());
                }
            }

            toReturn = toReturn.union(currentAnds);
        }

        return toReturn;
    }

    /**
     * Applies a selection of this result set with the column to select, the symbol to
     * operate on, and a target constant. The previous can be repeated more than once with
     * with either an AND or an OR, resulting in a condition list. This is equivalent to
     * performing an SQL WHERE clause on a relation. Careful not to confuse with the projection method!
     * @param condition is the condition that must be met
     */
    public ResultSet selection(Condition condition) {

        List<Column> selectionColumns = new ArrayList<>();

        for(Column column : columns) {
            selectionColumns.add(new Column(column));
        }

        // get the location of the column to perform a selection on
        int selectionColumnIndex = 0;
        String selectionColumnName = condition.getColumn().getColumnName();

        for(int cols = 0; cols < selectionColumns.size(); cols++) {
            String columnName = selectionColumns.get(cols).getColumnName();
            if(selectionColumnName.equals(columnName)) {
                selectionColumnIndex = cols;
                break;
            }
        }

        List<List<String>> selectionData = new ArrayList<>();

        for(int rows = 0; rows < data.size(); rows++) {

            Symbol symbol = condition.getSymbol();
            String target = condition.getTarget();

            String possibleTarget = data.get(rows).get(selectionColumnIndex);

            // determines the type of operation to use
            boolean isNumeric = condition.getColumn().getDataType() == DataType.NUMBER;

            if(isNumeric) {

                double targetNumber = Double.parseDouble(target);
                double possibleTargetNumber = Double.parseDouble(possibleTarget);

                switch (symbol) {
                    case EQUAL:
                        if (targetNumber == possibleTargetNumber) {
                            selectionData.add(data.get(rows));
                        }
                        break;
                    case NOT_EQUAL:
                        if (targetNumber != possibleTargetNumber) {
                            selectionData.add(data.get(rows));
                        }
                        break;
                    case GREATER_THAN:
                        if (possibleTargetNumber > targetNumber) {
                            selectionData.add(data.get(rows));
                        }
                        break;
                    case LESS_THAN:
                        if (possibleTargetNumber < targetNumber) {
                            selectionData.add(data.get(rows));
                        }
                        break;
                    case GREATER_THAN_OR_EQUAL:
                        if (possibleTargetNumber >= targetNumber) {
                            selectionData.add(data.get(rows));
                        }
                        break;
                    case LESS_THAN_OR_EQUAL:
                        if (possibleTargetNumber <= targetNumber) {
                            selectionData.add(data.get(rows));
                        }
                        break;
                    default: {
                        System.out.println("In ResultSet.projection()");
                        System.out.println("Symbol Used: " + symbol.toString());
                        return new ResultSet();
                    }
                }

                // not a numeric value
            } else {

                switch (symbol) {
                    case EQUAL:
                        if (target.equalsIgnoreCase(possibleTarget)) {
                            selectionData.add(data.get(rows));
                        }
                        break;
                    case NOT_EQUAL:
                        if (!target.equalsIgnoreCase(possibleTarget)) {
                            selectionData.add(data.get(rows));
                        }
                        break;
                    default: {
                        System.out.println("In ResultSet.projection()");
                        System.out.println("Symbol Used: " + symbol.toString());
                        return new ResultSet();
                    }
                }
            }
        }

        return new ResultSet(selectionColumns, selectionData);
    }

    /**
     * Intersects this result set with the one provided. This means that only rows in
     * this result set that match with the one provided are returned.
     * @param otherResultSet is the other result set to perform the intersection on
     */
    public ResultSet intersection(ResultSet otherResultSet) {

        List<Column> intersectionColumns = new ArrayList<>();

        for(Column column : columns) {
            intersectionColumns.add(new Column(column));
        }

        List<List<String>> intersectedData = new ArrayList<>();

        for(List<String> theseRows : data) {
            for(List<String> otherRows : otherResultSet.data) {
                if(hasEqualRows(theseRows, otherRows)) {
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

        List<Column> unionColumns = new ArrayList<>();

        // add each column to be unionized, don't add duplicates!
        for(Column column : columns) {
            unionColumns.add(new Column(column));
        }

        for(Column otherColumn : otherResultSet.columns) {
            boolean equalColumn = false;
            for(Column column : unionColumns) {
                if(otherColumn.equals(column)) {
                    equalColumn = true;
                    break;
                }
            }
            if(! equalColumn) {
                unionColumns.add(otherColumn);
            }
        }

        // add all rows from this result set
        List<List<String>> unionData = new ArrayList<>(data);

        // add all rows from other result set, don't add duplicates!
        for(List<String> otherRows : otherResultSet.data) {
            boolean foundDuplicate = false;
            for(List<String> theseRows : data) {
                if(hasEqualRows(otherRows, theseRows)) {
                    foundDuplicate = true;
                    break;
                }
            }
            if(! foundDuplicate) {
                unionData.add(otherRows);
            }
        }

        return new ResultSet(unionColumns, unionData);
    }

    /**
     * Performs a cartesian product on this result set and another result set,
     * combining the two. This is equivalent to an SQL FROM clause that lists more than 1 table.
     * @param otherResultSet is the other result set to perform a cartesian product on
     */
    public ResultSet cartesianProduct(ResultSet otherResultSet) {

        // add the other result set's columns to this result set's columns
        List<Column> cartesianProductColumns = new ArrayList<>();

        for(Column column : columns) {
            cartesianProductColumns.add(new Column(column));
        }

        for(Column column : otherResultSet.columns) {
            cartesianProductColumns.add(new Column(column));
        }

        List<List<String>> cartesianProductData = new ArrayList<>();

        for(int theseRows = 0; theseRows < data.size(); theseRows++) {
            for(int otherRows = 0; otherRows < otherResultSet.data.size(); otherRows++) {

                List<String> rowToAdd = new ArrayList<>();

                rowToAdd.addAll(data.get(theseRows));
                rowToAdd.addAll(otherResultSet.data.get(otherRows));

                cartesianProductData.add(rowToAdd);
            }
        }

        // overwrite the data with this cartesian product
        return new ResultSet(cartesianProductColumns, cartesianProductData);
    }

    /**
     * Performs an equi-join on this result set and another result set,
     * combining the two joined on the column supplied. This means that both result sets
     * must have a matching column name. This is equivalent to preforming a join using
     * in an SQL FROM clause.
     * @param otherResultSet is the other result set to perform a natural join on
     * @param joinOn is the column to join on, can come from either table
     */
    public ResultSet joinUsing(ResultSet otherResultSet, Column joinOn) {

        // if there is a table name associated with the join on column, remove that
        String columnNameToJoinOn = joinOn.getColumnName();

        if(columnNameToJoinOn.contains(".")) {
            int removeUpUntilIndex = columnNameToJoinOn.indexOf(".") + 1; // + 1 removes the "."
            columnNameToJoinOn = columnNameToJoinOn.substring(removeUpUntilIndex);
        }

        // find the column from each table and use the innerJoin method to perform the operation
        Column thisColumnToJoinOn = new Column();
        Column otherColumnToJoinOn = new Column();

        // first table
        for(Column column : this.columns) {
            String columnName = column.getColumnName();
            if(columnName.equalsIgnoreCase(columnNameToJoinOn)) {
                thisColumnToJoinOn = column;
                break;
            }
        }

        // second table
        for(Column column : otherResultSet.columns) {
            String columnName = column.getColumnName();
            if(columnName.equalsIgnoreCase(columnNameToJoinOn)) {
                otherColumnToJoinOn = column;
                break;
            }
        }

        return this.innerJoin(otherResultSet, otherColumnToJoinOn, thisColumnToJoinOn);
    }

    /**
     * Performs an inner join on this result set and another result set, combining the two
     * joined on the columns supplied. For now, only supporting the equi-join.
     * @param otherResultSet is the result set to perform an inner join on
     * @param thisColumnToJoinOn is the column to join on from this result set
     * @param otherColumnToJoinOn is the column to join on from the other result set
     */
    public ResultSet innerJoin(ResultSet otherResultSet, Column otherColumnToJoinOn, Column thisColumnToJoinOn) {

        // perform a cartesian product
        ResultSet cartesianProductResultSet = this.cartesianProduct(otherResultSet);

        List<Column> joinColumns = new ArrayList<>();

        for(Column column : cartesianProductResultSet.columns) {
            joinColumns.add(new Column(column));
        }

        // get the index locations of the columns to perform the join on
        int firstColumnIndex = -1;
        int secondColumnIndex = -1;

        String thisColumnName = thisColumnToJoinOn.getColumnName();
        String otherColumnName = otherColumnToJoinOn.getColumnName();

        for(int i = 0; i < joinColumns.size(); i++) {
            String columnName = joinColumns.get(i).getColumnName();
            if(columnName.equalsIgnoreCase(thisColumnName)) {
                firstColumnIndex = i;
                break;
            }
        }

        // skip past the index previously found, should not throw an exception if there exists another column
        try {
            for(int i = firstColumnIndex + 1; i < joinColumns.size(); i++) {
                String columnName = joinColumns.get(i).getColumnName();
                if(columnName.equalsIgnoreCase(otherColumnName)) {
                    secondColumnIndex = i;
                    break;
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("In ResultSet.naturalJoin()");
            System.out.println("Could not find the second index to join on");
            return new ResultSet();
        }

        if(firstColumnIndex == -1 || secondColumnIndex == -1) {
            System.out.println("In ResultSet.naturalJoin()");
            System.out.println("Could not find one or both indices to join on");
            return new ResultSet();
        }

        // keep only rows to join on
        List<List<String>> joinRows = new ArrayList<>();

        for(List<String> rows : cartesianProductResultSet.data) {

            String firstRowData = rows.get(firstColumnIndex);
            String secondRowData = rows.get(secondColumnIndex);

            if(firstRowData.equals(secondRowData)) {
                joinRows.add(rows);
            }
        }

        return new ResultSet(joinColumns, joinRows);
    }

    /**
     * TODO: currently unimplemented, will return an empty result set if called
     * Performs a natural join on this result set and the on provided. Common columns also
     * appear only once here.
     * @param otherResultSet is the other result set to perform the natural join on
     * @return a result set after performing a natural join
     */
    public ResultSet naturalJoin(ResultSet otherResultSet) {
        return new ResultSet();
    }

    /**
     * Performs an aggregate function on the columns supplied for this result set. Aggregate
     * functions include min, max, avg, count, and sum. This method in particular assumes that
     * are no columns to group by.
     * @param columnsToAggregate are the columns to perform aggregate functions on
     */
    public ResultSet groupBy(Map<Keyword, Column> columnsToAggregate) {

        // first project each of the columns that we wish to aggregate on this result set
        List<Column> columnsToProject = new ArrayList<>();

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
            Column groupByColumn = new Column(groupByColumnName, DataType.NUMBER, 0);
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

        return new ResultSet(columnsToReturn, groupByData);
    }

    /**
     * TODO: currently not implemented
     * Applies the having clause to this result set. This returns a new result set that
     * meets the condition of the having clause. As of right now, only allows for a single condition.
     * A result set must have been grouped by prior to using this method or it won't work.
     * @param condition is the condition to satisfy, the column must be aggregated
     * @return a result set that met the having clause's condition
     */
    public ResultSet having(Condition condition) {
        return new ResultSet();
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
        boolean isNumeric = Parser.isNumeric(orderedByData.get(0).get(columnIndex));

        // just doing a simple bubble sort, not efficient but am lazy
        for(int i = 0; i < orderedByData.size() - 1; i++) {
            for(int j = 0; j < orderedByData.size() - i - 1; j++) {

                if(isNumeric) {

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

        for(int rows = orderedAscResultSet.data.size() - 1; rows >= 0; rows--) {
            orderByDescData.add(orderedAscResultSet.data.get(rows));
        }

        return new ResultSet(orderedByDescColumns, orderByDescData);
    }

    // helper methods for above transformation methods -----------------------------------------------------------------

    /**
     * Compares the cells of the first row with the second to determine whether
     * all elements are equal.
     * @param row1 is the first row to compare
     * @param row2 is the second row to compare
     * @return whether these two rows are equal
     */
    public boolean hasEqualRows(List<String> row1, List<String> row2) {

        for(int cols = 0; cols < row1.size(); cols++) {

            String col1 = row1.get(cols);
            String col2 = row2.get(cols);

            if(! col1.equals(col2)) {
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
        for(Column column : columns) {

            int columnNameLength = column.getColumnName().length();
            int maxNumSpaces = column.size();

            if(columnNameLength > maxNumSpaces) {
                paddingAmountList.add(columnNameLength);
            } else {
                paddingAmountList.add(column.size());
            }
        }

        for(Column column : columns) {

            String columnName = column.getColumnName();

            StringBuilder spaces = new StringBuilder();
            int columnNameLength = columnName.length();
            int maxNumSpaces = column.size();
            int numSpacesToPad = maxNumSpaces - columnNameLength;

            boolean needsPadding = numSpacesToPad > 0;

            if(needsPadding) {
                for(int i = 0; i < numSpacesToPad; i++) {
                    spaces.append(" ");
                }
            }

            stringBuilder.append(columnName).append(spaces).append(" ");
        }

        stringBuilder.append("\n");

        // adding a dashed line
        for(Column column : columns) {

            int maxColumnNameLength = column.size();
            int columnNameLength = column.getColumnName().length();
            int numDashes = Math.max(maxColumnNameLength, columnNameLength);

            StringBuilder dashes = new StringBuilder();

            for(int i = 0; i < numDashes; i++) {
                dashes.append("-");
            }

            stringBuilder.append(dashes).append(" ");
        }

        stringBuilder.append("\n");

        // a very bad hack that gets the job done
        stringBuilder.append(new TableData(paddingAmountList, data).toString());

        if(! isEmpty()) {
            stringBuilder.append("\n");

        }

        stringBuilder.append("Number of Rows: ").append(getNumRows());

        return stringBuilder.toString();
    }
}