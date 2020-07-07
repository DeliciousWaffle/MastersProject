package datastructures.table;

import datastructures.Selection;
import datastructures.table.component.Column;
import datastructures.table.component.TableData;
import utilities.enums.DataType;
import utilities.enums.Keyword;
import utilities.enums.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param table
     */
    public ResultSet(Table table) {

        // used for identifying which column belongs to which table
        for(Column column: table.getColumns()) {
            column.setName(table.getTableName() + "." + column.getName());
        }
System.out.println(table.getColumns());
        this.columns = table.getColumns();
        this.data = table.getTableData().getData();
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

    /**
     * Applies a selection of this result set with the column to select, the symbol to
     * operate on, and a target constant. This is equivalent to performing an SQL
     * WHERE clause on a relation. Careful not to confuse with the projection method!
     * @param selections are a list of selections on a particular relation
     */
    public void selection(ArrayList<Selection> selections) {

        List<Integer> selectionColumnIndices = new ArrayList<>();

        for(Selection selection : selections) {
            String selectionColumnName = selection.getColumn().getName();
            for(int cols = 0; cols < columns.size(); cols++) {
                String columnName = columns.get(cols).getName();
                if(selectionColumnName.equalsIgnoreCase(columnName)) {
                    selectionColumnIndices.add(cols);
                }
            }
        }

        //for(int x : selectionColumnIndices) System.out.println(x);

        /*String selectionColumnName = selectionColumn.getName();

        for(int cols = 0; cols < columns.size(); cols++) {
            String columnName = columns.get(cols).getName();
            if(selectionColumnName.equals(columnName)) {
                colIndex = cols;
                break;
            }
        }

        ArrayList<ArrayList<String>> rowsToKeep = new ArrayList<>();

        // used for determining whether the current row meets all the criteria to be added
        boolean meetsAllCriteria = false;

        for(int rows = 0; rows < data.size(); rows++) {

            String possibleTarget = data.get(rows).get(colIndex);

            for (Selection selection : selections) {
                // determines the type of operation to use
                boolean isNumeric = selectionColumn.getDataType() == DataType.NUMBER;

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
        }

        // overwrite with the rows to keep
        data = rowsToKeep;*/
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

        for(ArrayList<String> rows : data) {
            ArrayList<String> cartesianRow = new ArrayList<>();
            for(String col : rows) {
                cartesianRow.add(col);
                for(ArrayList<String> otherRows : otherResultSet.getData()) {
                    for(String otherCol : otherRows) {
                        cartesianRow.add(otherCol);
                    }
                }
            }
            cartesianProduct.add(cartesianRow);
        }

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
        for(Column column : getColumns()) {

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

        return stringBuilder.toString();
    }
}