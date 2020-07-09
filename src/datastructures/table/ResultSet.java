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

        // need to create a deep copy of the table to prevent weirdness
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
        this.columns.addAll(toCopy.columns);
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

    // applying transformations on the data to fit the query request using relational algebra --------------------------

    /**
     * Applies a projection of this result set with the columns supplied.
     * This is equivalent to performing an SQL SELECT clause on a relation.
     * Careful not to confuse this method with the selection method!
     * @param columnsToProject are the columns to perform the projection on
     */
    public ResultSet projection(ArrayList<Column> columnsToProject) {

        ArrayList<Column> projectedColumns = new ArrayList<>(columnsToProject);

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

        ArrayList<ArrayList<String>> dataToProject = new ArrayList<>();

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

            dataToProject.add(columnsToAdd);
        }

        return new ResultSet(projectedColumns, dataToProject);
    }

    /**
     * TODO: this method is kind of a hot mess right now, will need to refactor at some point
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
                currentAnds = currentAnds.intersection(temp.poll());
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

        // get the location of the column to perform a selection on
        int selectionColumnIndex = 0;
        String selectionColumnName = condition.getColumn().getName();

        for(int cols = 0; cols < columns.size(); cols++) {
            String columnName = columns.get(cols).getName();
            if(selectionColumnName.equals(columnName)) {
                selectionColumnIndex = cols;
                break;
            }
        }

        ArrayList<ArrayList<String>> rowsToKeep = new ArrayList<>();

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
                            rowsToKeep.add(data.get(rows));
                        }
                        break;
                    case NOT_EQUAL:
                        if (targetNumber != possibleTargetNumber) {
                            rowsToKeep.add(data.get(rows));
                        }
                        break;
                    case GREATER_THAN:
                        if (possibleTargetNumber > targetNumber) {
                            rowsToKeep.add(data.get(rows));
                        }
                        break;
                    case LESS_THAN:
                        if (possibleTargetNumber < targetNumber) {
                            rowsToKeep.add(data.get(rows));
                        }
                        break;
                    case GREATER_THAN_OR_EQUAL:
                        if (possibleTargetNumber >= targetNumber) {
                            rowsToKeep.add(data.get(rows));
                        }
                        break;
                    case LESS_THAN_OR_EQUAL:
                        if (possibleTargetNumber <= targetNumber) {
                            rowsToKeep.add(data.get(rows));
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
                            rowsToKeep.add(data.get(rows));
                        }
                        break;
                    case NOT_EQUAL:
                        if (!target.equalsIgnoreCase(possibleTarget)) {
                            rowsToKeep.add(data.get(rows));
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

        return new ResultSet(columns, rowsToKeep);
    }

    /**
     * Intersects this result set with the one provided. This means that only rows in
     * this result set that match with the one provided are returned.
     * @param otherResultSet is the other result set to perform the intersection on
     */
    public ResultSet intersection(ResultSet otherResultSet) {

        ArrayList<ArrayList<String>> intersectedRows = new ArrayList<>();

        for(ArrayList<String> theseRows : data) {
            for(ArrayList<String> otherRows : otherResultSet.data) {
                if(hasEqualRows(theseRows, otherRows)) {
                    intersectedRows.add(theseRows);
                    break;
                }
            }
        }

        return new ResultSet(columns, intersectedRows);
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
        ArrayList<ArrayList<String>> unionRows = new ArrayList<>(data);

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
                unionRows.add(otherRows);
            }
        }

        return new ResultSet(unionColumns, unionRows);
    }

    /**
     * Performs a cartesian product on this result set and another result set,
     * combining the two. This is equivalent to an SQL FROM clause that lists more than 1 table.
     * @param otherResultSet is the other result set to perform a cartesian product on
     */
    public ResultSet cartesianProduct(ResultSet otherResultSet) {

        // add the other result set's columns to this result set's columns
        ArrayList<Column> cartesianColumns = new ArrayList<>();
        cartesianColumns.addAll(columns);
        cartesianColumns.addAll(otherResultSet.getColumns());

        ArrayList<ArrayList<String>> cartesianProduct = new ArrayList<>();

        for(int theseRows = 0; theseRows < data.size(); theseRows++) {
            for(int otherRows = 0; otherRows < otherResultSet.data.size(); otherRows++) {

                ArrayList<String> rowToAdd = new ArrayList<>();

                rowToAdd.addAll(data.get(theseRows));
                rowToAdd.addAll(otherResultSet.data.get(otherRows));

                cartesianProduct.add(rowToAdd);
            }
        }

        // overwrite the data with this cartesian product
        return new ResultSet(cartesianColumns, cartesianProduct);
    }

    /**
     * Performs a natural join on this result set and another result set,
     * combining the two joined on the column supplied. This means that both result sets
     * must have a matching column name. This is equivalent to preforming a natural join
     * in an SQL FROM clause.
     * @param otherResultSet is the other result set to perform a natural join on
     * @param joinOn is the column to join on
     */
    public ResultSet naturalJoin(ResultSet otherResultSet, Column joinOn) {

        return null;
    }

    /**
     * TODO: Currently is not in use. May be implemented later.
     * Performs an inner join on this result set and another result set, combining the two
     * joined on the columns supplied.
     * @param otherResultSet
     * @param otherColumnToJoinOn
     * @param thisColumnToJoinOn
     */
    public ResultSet innerJoin(ResultSet otherResultSet, Column otherColumnToJoinOn, Column thisColumnToJoinOn) {

        return null;
    }

    /**
     * Performs an aggregate function on the column supplied for this result set. Aggregate
     * functions include min, max, avg, count, and sum.
     * @param aggregateFunction is the aggregate function to perform
     * @param columnToAggregate is the column to aggregate
     */
    public ResultSet aggregate(Keyword aggregateFunction, Column columnToAggregate) {

        return null;
    }

    /**
     * TODO: currently not in use. May implement later.
     */
    public ResultSet groupBy(Column columnToGroupBy) {

        return null;
    }

    /**
     * TODO: currently not in use. May implement later.
     */
    public ResultSet having() {

        return null;
    }

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