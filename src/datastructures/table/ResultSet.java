package datastructures.table;

import datastructures.Condition;
import datastructures.ConditionSet;
import datastructures.table.component.Column;
import datastructures.table.component.TableData;
import utilities.enums.DataType;
import utilities.enums.Keyword;
import utilities.enums.Symbol;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Represents the data returned after execution of a query. Data is set initially, operations are then
 * then performed on rows and columns to reflect what the query is asking. Result sets can be operated on
 * as well and produce new result sets. The cost of these operations is also stored for query cost analysis.
 */
public class ResultSet {

    private ArrayList<Column> columns;
    private ArrayList<ArrayList<String>> data;

    /**
     * Default constructor, should only be used for initially setting up the query tree.
     * @param table is the table to use
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

    // applying transformations on the raw data to fit the query request using relational algebra ----------------------

    /**
     * Applies a projection of this result set with the columns supplied.
     * This is equivalent to performing an SQL SELECT clause on a relation.
     * Careful not to confuse this method with the selection method!
     * @param columnsToProject are the columns to perform the projection on
     */
    public void projection(ArrayList<Column> columnsToProject) {

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

        columns = columnsToProject;
        data = dataToProject;
    }

    public void selection(ConditionSet conditionSet) {

        Stack<Stack<Condition>> conditions = conditionSet.getConditions();
        ArrayList<ResultSet> resultSets = new ArrayList<>();

        while(! conditions.isEmpty()) {
            Stack<Condition> andStack = conditions.pop();
            if(andStack.size() == 1) {

            } else {
                while(! andStack.isEmpty()) {
                    Condition condition = andStack.pop();
                    selection(condition);
                }
            }
        }
    }

    /**
     * Applies a selection of this result set with the column to select, the symbol to
     * operate on, and a target constant. The previous can be repeated more than once with
     * with either an AND or an OR, resulting in a condition list. This is equivalent to
     * performing an SQL WHERE clause on a relation. Careful not to confuse with the projection method!
     * @param condition is the condition that must be met
     */
    public void selection(Condition condition) {

        /*ArrayList<ArrayList<String>> rowsToKeep = new ArrayList<>();
        HashMap<String, String> columnRowPairs = new HashMap<>();

        for(ArrayList<String> rows : data) {

            for(int cols = 0; cols < rows.size(); cols++) {
                columnRowPairs.put(columns.get(cols).getName(), rows.get(cols));
            }

            boolean isRowToKeep = conditionList.resolve(columnRowPairs);

            if(isRowToKeep) {
                rowsToKeep.add(rows);
            }
        }

        data = rowsToKeep;*/

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
                            if (targetNumber > possibleTargetNumber) {
                                rowsToKeep.add(data.get(rows));
                            }
                            break;
                        case LESS_THAN:
                            if (targetNumber < possibleTargetNumber) {
                                rowsToKeep.add(data.get(rows));
                            }
                            break;
                        case GREATER_THAN_OR_EQUAL:
                            if (targetNumber >= possibleTargetNumber) {
                                rowsToKeep.add(data.get(rows));
                            }
                            break;
                        case LESS_THAN_OR_EQUAL:
                            if (targetNumber <= possibleTargetNumber) {
                                rowsToKeep.add(data.get(rows));
                            }
                            break;
                        default:
                            System.out.println("In ResultSet.projection()");
                            System.out.println("Symbol Used: " + symbol.toString());
                            return;
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
                        default:
                            System.out.println("In ResultSet.projection()");
                            System.out.println("Symbol Used: " + symbol.toString());
                            return;
                    }

            }
        }

        // overwrite with the rows to keep
        data = rowsToKeep;
    }

    /**
     * Intersects this result set with the one provided. This means that rows in this
     * result set that are equal to the ones in the provided result set
     * will appear in the final result set.
     * @param otherResultSet is the other result set to perform the intersection on
     */
    public void intersection(ResultSet otherResultSet) {

        ArrayList<ArrayList<String>> intersectedRows = new ArrayList<>();

        for(int theseRows = 0; theseRows < data.size(); theseRows++) {
            boolean equalRows = true;
            for(int otherRows = 0; otherRows < otherResultSet.data.size(); otherRows++) {
                for(int cols = 0 ; cols < data.get(theseRows).size(); cols++) {
                    if(! data.get(theseRows).get(cols).equals(otherResultSet.data.get(otherRows).get(cols))) {
                        equalRows = false;
                        break;
                    }
                }
                if(equalRows) {
                    intersectedRows.add(otherResultSet.data.get(otherRows));
                    break;
                }
            }
        }

        data = intersectedRows;
    }

    /**
     * Unions this result set with the one provided. This means that rows from this result
     * set and the one provided will be added to this result set. Duplicates will not appear.
     * @param otherResultSet is other result set to perform the union on
     */
    public void union(ResultSet otherResultSet) {

        ArrayList<ArrayList<String>> unionAllRows = new ArrayList<>();

        for(ArrayList<String> theseRows : data) {
            unionAllRows.add(theseRows);
        }

        for(ArrayList<String> otherRows: otherResultSet.data) {
            unionAllRows.add(otherRows);
        }

        // remove duplicates
        ArrayList<ArrayList<String>> unionRows = new ArrayList<>();

        for(int rows = 0; rows < unionAllRows.size(); rows++) {
            boolean equalRows = true;
            for (int dupRows = 0; dupRows < unionAllRows.size(); dupRows++) {
                for(int cols = 0; cols < unionAllRows.get(rows).size(); cols++) {
                    if(! unionAllRows.get(rows).get(cols).equals(unionAllRows.get(dupRows).get(cols))) {
                        equalRows = false;
                        break;
                    }
                }
                if(! equalRows) {
                    break;
                }
            }
            if(! equalRows) {
                unionRows.add(unionAllRows.get(rows));
            }
        }

        data = unionAllRows;
    }

    /**
     * Performs a cartesian product on this result set and another result set,
     * combining the two. This is equivalent to an SQL FROM clause that lists more than 1 table.
     * @param otherResultSet is the other result set to perform a cartesian product on
     */
    public void cartesianProduct(ResultSet otherResultSet) {

        // add the other result set's columns to this result set's columns
        for(Column otherColumn : otherResultSet.getColumns()) {
            columns.add(otherColumn);
        }

        ArrayList<ArrayList<String>> cartesianProduct = new ArrayList<>();

        for(int theseRows = 0; theseRows < data.size(); theseRows++) {
            ArrayList<String> rowToAdd = new ArrayList<>();
            for(int theseCols = 0; theseCols < data.get(theseRows).size(); theseCols++) {
                rowToAdd.add(data.get(theseRows).get(theseCols));
            }
            for(int otherCols = 0; otherCols < otherResultSet.data.get())
        }

        // TODO cartesian product and stupid selection
        // overwrite the data with this cartesian product
        data = cartesianProduct;
    }

    /**
     * Performs a natural join on this result set and another result set,
     * combining the two joined on the column supplied. This means that both result sets
     * must have a matching column name. This is equivalent to preforming a natural join
     * in an SQL FROM clause.
     * @param otherResultSet is the other result set to perform a natural join on
     * @param joinOn is the column to join on
     */
    public void naturalJoin(ResultSet otherResultSet, Column joinOn) {

    }

    /**
     * TODO: Currently is not in use. May be implemented later.
     * Performs an inner join on this result set and another result set, combining the two
     * joined on the columns supplied.
     * @param otherResultSet
     * @param otherColumnToJoinOn
     * @param thisColumnToJoinOn
     */
    public void innerJoin(ResultSet otherResultSet, Column otherColumnToJoinOn, Column thisColumnToJoinOn) {

    }

    /**
     * Performs an aggregate function on the column supplied for this result set. Aggregate
     * functions include min, max, avg, count, and sum.
     * @param aggregateFunction is the aggregate function to perform
     * @param columnToAggregate is the column to aggregate
     */
    public void aggregate(Keyword aggregateFunction, Column columnToAggregate) {

        // validation

    }

    /**
     * TODO: currently not in use. May implement later.
     */
    public void groupBy(Column columnToGroupBy) {

    }

    /**
     * TODO: currently not in use. May implement later.
     */
    public void having() {

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
System.out.println(paddingAmountList.size());
System.out.println(data.get(0).size());
        // a very bad hack that gets the job done
        stringBuilder.append(new TableData(paddingAmountList, data).toString());

        return stringBuilder.toString();
    }
}