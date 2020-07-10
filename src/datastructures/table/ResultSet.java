package datastructures.table;

import datastructures.Condition;
import datastructures.ConditionExpression;
import datastructures.table.component.Column;
import datastructures.table.component.TableData;
import utilities.enums.DataType;
import utilities.enums.Keyword;
import utilities.enums.Symbol;

import java.util.*;

/**
 * Represents the data returned after execution of a query. Table data is set initially, then, operations
 * are performed on the result set to reflect what the query is asking. This class is immutable which means
 * once the data is set, it's set. This may come back to haunt me because of how costly this is.
 * Usage: ResultSet resultSet = new ResultSet(TableData tableData);
 *        resultSet = resultSet.<some operation>(<some argument(s)>);
 */
public class ResultSet {

    private final ArrayList<Column> columns;
    private final ArrayList<ArrayList<String>> data;

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
            column.setName(copyTable.getTableName() + "." + column.getName());
        }

        this.columns = copyTable.getColumns();
        this.data = copyTable.getTableData().getData();
    }

    /**
     * Result set typically used after applying some kind of transformation to a
     * previous result set.
     * @param columns are the columns of this result set
     * @param data is the data of this result set
     */
    public ResultSet(ArrayList<Column> columns, ArrayList<ArrayList<String>> data) {
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
        for(ArrayList<String> toCopyRows : toCopy.data) {
            ArrayList<String> rows = new ArrayList<>(toCopyRows);
            this.data.add(rows);
        }
    }

    public ArrayList<Column> getColumns() { return columns; }

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }

    // utility methods -------------------------------------------------------------------------------------------------

    public int getNumRows() {
        return data.size();
    }

    public int getNumCols() {
        return ! data.isEmpty() ? data.get(0).size() : 0;
    }

    public boolean isEmpty() { return columns.isEmpty(); }

    public ArrayList<String> getColumnsAt(int columnIndex) {
        ArrayList<String> columnData = new ArrayList<>();
        for(ArrayList<String> rows : data) {
            String data = rows.get(columnIndex);
            columnData.add(data);
        }
        return columnData;
    }

    // applying transformations on the data to fit the query request using relational algebra --------------------------

    /**
     * Applies a projection of this result set with the columns supplied.
     * This is equivalent to performing an SQL SELECT clause on a relation.
     * Careful not to confuse this method with the selection method!
     * @param columnsToProject are the columns to perform the projection on
     */
    public ResultSet projection(ArrayList<Column> columnsToProject) {

        ArrayList<Column> projectedColumns = new ArrayList<>();

        for(Column columnToProject : columnsToProject) {
            projectedColumns.add(new Column(columnToProject));
        }

        // find the locations of each column to project
        ArrayList<Integer> columnsToProjectIndexes = new ArrayList<>();

        for(int cols = 0; cols < columns.size(); cols++) {
            String columnName = columns.get(cols).getName();

            for(Column columnToProject : columnsToProject) {
                String columnToProjectName = columnToProject.getName();

                if(columnName.equalsIgnoreCase(columnToProjectName)) {
                    columnsToProjectIndexes.add(cols);
                    break;
                }
            }
        }

        ArrayList<ArrayList<String>> projectedData = new ArrayList<>();

        // ignore columns that are not being projected
        for(int rows = 0; rows < data.size(); rows++) {
            ArrayList<String> columnsToAdd = new ArrayList<>();

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

        ArrayList<Column> selectionColumns = new ArrayList<>();

        for(Column column : columns) {
            selectionColumns.add(new Column(column));
        }

        // get the location of the column to perform a selection on
        int selectionColumnIndex = 0;
        String selectionColumnName = condition.getColumn().getName();

        for(int cols = 0; cols < selectionColumns.size(); cols++) {
            String columnName = selectionColumns.get(cols).getName();
            if(selectionColumnName.equals(columnName)) {
                selectionColumnIndex = cols;
                break;
            }
        }

        ArrayList<ArrayList<String>> selectionData = new ArrayList<>();

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

        ArrayList<Column> intersectionColumns = new ArrayList<>();

        for(Column column : columns) {
            intersectionColumns.add(new Column(column));
        }

        ArrayList<ArrayList<String>> intersectedData = new ArrayList<>();

        for(ArrayList<String> theseRows : data) {
            for(ArrayList<String> otherRows : otherResultSet.data) {
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

        ArrayList<Column> unionColumns = new ArrayList<>();

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
        ArrayList<ArrayList<String>> unionData = new ArrayList<>(data);

        // add all rows from other result set, don't add duplicates!
        for(ArrayList<String> otherRows : otherResultSet.data) {
            boolean foundDuplicate = false;
            for(ArrayList<String> theseRows : data) {
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
        ArrayList<Column> cartesianProductColumns = new ArrayList<>();

        for(Column column : columns) {
            cartesianProductColumns.add(new Column(column));
        }

        for(Column column : otherResultSet.columns) {
            cartesianProductColumns.add(new Column(column));
        }

        ArrayList<ArrayList<String>> cartesianProductData = new ArrayList<>();

        for(int theseRows = 0; theseRows < data.size(); theseRows++) {
            for(int otherRows = 0; otherRows < otherResultSet.data.size(); otherRows++) {

                ArrayList<String> rowToAdd = new ArrayList<>();

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
        String columnNameToJoinOn = joinOn.getName();

        if(columnNameToJoinOn.contains(".")) {
            int removeUpUntilIndex = columnNameToJoinOn.indexOf(".") + 1; // + 1 removes the "."
            columnNameToJoinOn = columnNameToJoinOn.substring(removeUpUntilIndex);
        }

        // find the column from each table and use the innerJoin method to perform the operation
        Column thisColumnToJoinOn = new Column();
        Column otherColumnToJoinOn = new Column();

        // first table
        for(Column column : this.columns) {
            String columnName = column.getName();
            if(columnName.equalsIgnoreCase(columnNameToJoinOn)) {
                thisColumnToJoinOn = column;
                break;
            }
        }

        // second table
        for(Column column : otherResultSet.columns) {
            String columnName = column.getName();
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

        ArrayList<Column> joinColumns = new ArrayList<>();

        for(Column column : cartesianProductResultSet.columns) {
            joinColumns.add(new Column(column));
        }

        // get the index locations of the columns to perform the join on
        int firstColumnIndex = -1;
        int secondColumnIndex = -1;

        String thisColumnName = thisColumnToJoinOn.getName();
        String otherColumnName = otherColumnToJoinOn.getName();

        for(int i = 0; i < joinColumns.size(); i++) {
            String columnName = joinColumns.get(i).getName();
            if(columnName.equalsIgnoreCase(thisColumnName)) {
                firstColumnIndex = i;
                break;
            }
        }

        // skip past the index previously found, should not throw an exception if there exists another column
        try {
            for(int i = firstColumnIndex + 1; i < joinColumns.size(); i++) {
                String columnName = joinColumns.get(i).getName();
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
        ArrayList<ArrayList<String>> joinRows = new ArrayList<>();

        for(ArrayList<String> rows : cartesianProductResultSet.data) {

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
     * Performs an aggregate function on the column supplied for this result set. Aggregate
     * functions include min, max, avg, count, and sum.
     * @param columnsToGroupBy are columns to group by
     * @param columnsToAggregate are the columns to perform aggregate functions on
     */
    public ResultSet groupBy(ArrayList<Column> columnsToGroupBy, Map<Keyword, Column> columnsToAggregate) {

        // adding the columns to group by
        ArrayList<Column> columnsToReturn = new ArrayList<>();

        for(Column column : columnsToGroupBy) {
            columnsToReturn.add(new Column(column));
        }

        // adding the columns to aggregate
        for(Map.Entry<Keyword, Column> entry : columnsToAggregate.entrySet()) {

            Keyword keyword = entry.getKey();
            Column column = entry.getValue();
            String columnName = column.getName();

            String groupByColumnName = keyword + "(" + columnName + ")";

            // the size will be set later, 0 is arbitrary
            Column groupByColumn = new Column(groupByColumnName, DataType.NUMBER, 0);
            columnsToReturn.add(groupByColumn);
        }

        // get the index locations of the columns to group by from this result set
        /*ArrayList<Integer> columnsToGroupByIndices = new ArrayList<>();

        for(int i = 0; i < this.columns.size(); i++) {
            String columnName = this.columns.get(i).getName();
            for(Column columnToGroupBy : columnsToGroupBy) {
                String columnNameToGroupBy = columnToGroupBy.getName();
                if(columnName.equalsIgnoreCase(columnNameToGroupBy)) {
                    columnsToGroupByIndices.add(i);
                    break;
                }
            }
        }*/

        // get the index locations of the columns to aggregate for this result set
        Map<Keyword, Integer> columnsToAggregateIndices = new HashMap<>();

        // used for eventually updating column sizes, <this.columns.index, groupByColumns.index>
        Map<Integer, Integer> columnsAndGroupByColumnsIndexMappings = new HashMap<>();

        for(int i = 0; i < this.columns.size(); i++) {
            String columnName = this.columns.get(i).getName();
            int j = 0;
            for(Map.Entry<Keyword, Column> entry : columnsToAggregate.entrySet()) {
                String columnNameToAggregate = entry.getValue().getName();
                if(columnName.equalsIgnoreCase(columnNameToAggregate)) {
                    columnsToAggregateIndices.put(entry.getKey(), i);
                    columnsAndGroupByColumnsIndexMappings.put(i, j);
                    break;
                }
                j++;
            }
        }System.out.println(columnsAndGroupByColumnsIndexMappings);
        System.out.println(groupByColumns);

        ArrayList<ArrayList<String>> groupByData = new ArrayList<>();

        // if there are no columns to group by, our lives are made easier
        boolean hasColumnsToGroupBy = ! columnsToGroupBy.isEmpty();

        if(! hasColumnsToGroupBy) {

            // will only need to add a single row
            ArrayList<String> groupByRow = new ArrayList<>();

            for(Map.Entry<Keyword, Integer> entry : columnsToAggregateIndices.entrySet()) {

                Keyword aggregateFunction = entry.getKey();
                int columnToAggregateIndex = entry.getValue();

                ArrayList<String> columnData = this.getColumnsAt(columnToAggregateIndex);
                String valueToAdd = "";
                int sizeOfValue = 0;

                switch(aggregateFunction) {
                    case MIN:
                        valueToAdd = Double.toString(minColumnValue(columnData));
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

                // set the size of the column to the length of the value obtained
                sizeOfValue = valueToAdd.length();

                int blah = columnsAndGroupByColumnsIndexMappings.get(columnToAggregateIndex);
                Column columnToGroupBy = groupByColumns.get(blah);
                columnToGroupBy.setSize(sizeOfValue);

                groupByRow.add(valueToAdd);
            }

            groupByData.add(groupByRow);

        // lives are made slightly harder if grouping by a column
        } else {

        }

        return new ResultSet(columnsToReturn, groupByData);
    }

    // may not implement
    public ResultSet having(Keyword aggregateFunction, Column havingColumn) {

        // making a copy of the columns of this result set
        ArrayList<Column> havingColumns = new ArrayList<>();

        for(Column column : columns) {
            havingColumns.add(new Column(column));
        }

        // get corresponding index of this result set
        int havingColumnIndex = 0;
        String havingColumnName = havingColumn.getName();

        for(int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getName();
            if(columnName.equalsIgnoreCase(havingColumnName)) {
                havingColumnIndex = i;
                break;
            }
        }

        // add each row that satisfies the aggregate function
        ArrayList<ArrayList<String>> havingData = new ArrayList<>();

        for(ArrayList<String> rows : data) {
            String currentCell = rows.get(havingColumnIndex);
        }

        return new ResultSet();
    }

    public ResultSet orderByAsc(Column columnToOrderBy) {

        // copy columns
        ArrayList<Column> orderedByAscColumns = new ArrayList<>();

        for(Column column : columns) {
            orderedByAscColumns.add(new Column(column));
        }

        // get column index for this result set
        int columnIndex = 0;
        String columnNameToOrderBy = columnToOrderBy.getName();

        for(int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getName();
            if(columnName.equalsIgnoreCase(columnNameToOrderBy)) {
                columnIndex = i;
            }
        }

        // copy data but sorted on column name, this is not done efficiently
        ArrayList<ArrayList<String>> orderedByData = new ArrayList<>();

        for(int i = 0; i < data.size(); i++) {

            ArrayList<String> rowToAdd = new ArrayList<>();

            int minRowIndex = 0;
            String minCell = data.get(0).get(columnIndex);

            for(int j = 0; j < data.size(); j++) {
                String cell = data.get(j).get(columnIndex);
                if(cell.compareTo(minCell) < 0) {
                    minCell = cell;
                    minRowIndex = j;
                }
            }

            rowToAdd.addAll(data.get(minRowIndex));
        }

        return new ResultSet(orderedByAscColumns, orderedByData);
    }

    public ResultSet orderByDesc(Column columnToOrderBy) {

        ResultSet orderedAscResultSet = orderByAsc(columnToOrderBy);

        // copy columns
        ArrayList<Column> orderedByDescColumns = new ArrayList<>();

        for(Column column : columns) {
            orderedByDescColumns.add(new Column(column));
        }

        // copy data, but reversed
        ArrayList<ArrayList<String>> orderByDescData = new ArrayList<>();

        for(int rows = orderedAscResultSet.data.size() - 1; rows >= 0; rows++) {
            orderByDescData.add(orderedAscResultSet.data.get(rows));
        }

        return new ResultSet(orderedByDescColumns, orderByDescData);
    }

    // helper methods for above transformation methods -----------------------------------------------------------------

    public boolean hasEqualRows(ArrayList<String> row1, ArrayList<String> row2) {
        for(int cols = 0; cols < row1.size(); cols++) {
            String col1 = row1.get(cols);
            String col2 = row2.get(cols);
            if(! col1.equals(col2)) {
                return false;
            }
        }
        return true;
    }

    public double minColumnValue(ArrayList<String> columnData) {

        double min = Integer.parseInt(columnData.get(0));

        for(String data : columnData) {
            double dataValue = Double.parseDouble(data);
            if(dataValue < min) {
                min = dataValue;
            }
        }

        return min;
    }

    public double maxColumnValue(ArrayList<String> columnData) {

        double max = Integer.parseInt(columnData.get(0));

        for(String data : columnData) {
            double dataValue = Double.parseDouble(data);
            if(dataValue > max) {
                max = dataValue;
            }
        }

        return max;
    }

    public double avgColumnValue(ArrayList<String> columnData) {

        double totalValue = sumColumnValue(columnData);
        int numRows = countColumnValue(columnData);

        return totalValue / numRows;
    }

    public int countColumnValue(ArrayList<String> columnData) {
        return columnData.size();
    }

    public double sumColumnValue(ArrayList<String> columnData) {

        double sum = 0;

        for(String data : columnData) {
            double dataValue = Double.parseDouble(data);
            sum += dataValue;
        }

        return sum;
    }

    /**
     * @return a string representation of the FILTERED data
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        // used for formatting purposes
        ArrayList<Integer> paddingAmountList = new ArrayList<>();

        // figuring out padding amounts and adding to the list of column sizes
        for(Column column : columns) {

            int columnNameLength = column.getName().length();
            int maxNumSpaces = column.size();

            if(columnNameLength > maxNumSpaces) {
                paddingAmountList.add(columnNameLength);
            } else {
                paddingAmountList.add(column.size());
            }
        }

        for(Column column : columns) {

            String columnName = column.getName();

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
            int columnNameLength = column.getName().length();
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