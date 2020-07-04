package datastructures.table.component;

import java.util.ArrayList;

/**
 * Represents all the rows and columns stored within a particular table.
 * Rows and columns can grow, shrink, have their contents changed, and
 * and accept null values. These will be the direct result of a DMl statement.
 */
public class TableData {

    private ArrayList<Integer> paddingAmountList;    // for formatting
    private ArrayList<ArrayList<String>> tableData;

    public TableData(ArrayList<Integer> paddingAmountList, ArrayList<ArrayList<String>> tableData) {
        this.paddingAmountList = paddingAmountList;
        this.tableData = tableData;
    }

    /**
     * @return columnSizes a list of all the column sizes
     */
    public ArrayList<Integer> getPaddingAmountList() { return paddingAmountList; }

    /**
     * @return the number of rows in this table
     */
    public int getNumRows() {
        return tableData.size();
    }

    /**
     * @return the number of columns in this table
     */
    public int getNumCols() {
        return ! tableData.isEmpty() ? tableData.get(0).size() : 0;
    }

    /**
     * Deletes the row at the supplied index.
     * @param index the location of the row to delete
     */
    public void deleteRowAt(int index) {
        tableData.remove(index);
    }

    /**
     * Deletes the column at the supplied index.
     * @param index the location of the column to delete
     */
    public void deleteColumnAt(int index) {

        for(int rows = 0; rows < tableData.size(); rows++) {
            tableData.get(rows).remove(index);
        }

        paddingAmountList.remove(index);
    }

    /**
     * Alters the data of a cell to something else.
     */
    public void alterCellAt(int row, int col, String cell) {
        tableData.get(row).set(col, cell);
    }

    /**
     * Adds a row to the bottom of the table.
     */
    public void addRow(ArrayList<String> row) {
        tableData.add(row);
    }

    /**
     * Adds a row to the table at the supplied index.
     * Use for maintaining an order with respect to the primary key.
     */
    public void addRowAt(int index, ArrayList<String> row) {
        tableData.add(index, row);
    }

    /**
     * Adds a column to the table. New columns are appended to right of the table.
     * The rows within this new column will be null.
     * @param columnSize is the size of the new column to add
     */
    public void addColumn(int columnSize) {

        for(int rows = 0; rows < tableData.size(); rows++) {
            tableData.get(rows).add("null");
        }

        paddingAmountList.add(columnSize);
    }

    /**
     * Returns whether the data stored within this table is deeply equal to
     * the object provided. Pretty much just used for testing purposes.
     * @param other object for comparison
     * @return whether the table in this table is deeply equal to the object supplied
     */
    @Override
    public boolean equals(Object other) {

        if(other == this) {
            return true;
        }

        if(! (other instanceof TableData)) {
            return false;
        }

        TableData otherTableData = (TableData) other;

        if(otherTableData.getPaddingAmountList() != this.getPaddingAmountList()) {
            System.out.println("Table Data not equal");
            System.out.println("Other Column Sizes: " + otherTableData.getPaddingAmountList() +
                    "This Column Sizes: " + this.getPaddingAmountList());
            return false;
        }

        // check each column size for equality
        for(int i = 0; i < paddingAmountList.size(); i++) {

            int otherColumnSize = otherTableData.getPaddingAmountList().get(i);
            int thisColumnSize  = this.getPaddingAmountList().get(i);

            if(otherColumnSize != thisColumnSize) {
                System.out.println("Table Data not equal");
                System.out.println("Other Column Size: " + otherColumnSize +
                        "This Column Size: " + thisColumnSize);
                return false;
            }
        }

        if(otherTableData.getNumRows() != this.getNumRows()) {
            System.out.println("Table Data not equal");
            System.out.println("Other Num Rows: " + otherTableData.getNumRows() +
                    " This Num Rows: " + this.getNumRows());
            return false;
        }

        if(otherTableData.getNumCols() != this.getNumCols()) {
            System.out.println("Table Data not equal");
            System.out.println("Other Num Cols: " + otherTableData.getNumCols() +
                    " This Num Cols: " + this.getNumCols());
            return false;
        }

        // check each cell within the table for equality
        for(int rows = 0; rows < tableData.size(); rows++) {
            for(int cols = 0; cols < tableData.get(rows).size(); cols++) {

                String otherCell = otherTableData.tableData.get(rows).get(cols);
                String thisCell  = this.tableData.get(rows).get(cols);

                if(! otherCell.equals(thisCell)) {
                    System.out.println("Table Data not equal");
                    System.out.println("Other Cell:" + otherCell + " This Cell: " + thisCell);
                    return false;
                }
            }
        }

        System.out.println("Table Data equal");
        return true;
    }

    /**
     * @return a string representation of the data stored within the table
     */
    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        for(int rows = 0; rows < tableData.size(); rows++) {

            for(int cols = 0; cols < tableData.get(rows).size(); cols++) {

                // space formatting
                int colSize = tableData.get(rows).get(cols).length();
                int maxColSize = paddingAmountList.get(cols);
                int spaceOffset = Math.abs(colSize - maxColSize);

                StringBuilder spaces = new StringBuilder();

                for(int i = 0; i < spaceOffset; i++) {
                    spaces.append(" ");
                }

                print.append(tableData.get(rows).get(cols)).append(spaces).append(" ");
            }

            print.append("\n");
        }

        // remove "\n"
        print.deleteCharAt(print.length() - 1);

        return print.toString();
    }
}
