package datastructures.table;

import datastructures.table.component.Column;
import datastructures.table.component.TableData;

import java.util.ArrayList;

/**
 * A representation of the table stored within the database. This stores stuff
 * like the table name, columns, whether there's a primary key, and the data itself.
 */
public class Table {

    private String name;
    private ArrayList<Column> columns;
    private Column primaryKey;
    private TableData tableData;

    public Table(String name, ArrayList<Column> columns, Column primaryKey, TableData tableData) {

        this.name       = name;
        this.columns    = columns;
        this.primaryKey = primaryKey;
        this.tableData  = tableData;
    }

    // getters, setters ------------------------------------------------------------------------------------------------

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ArrayList<Column> getColumns() { return columns; }

    public void setColumns(ArrayList<Column> columns) {
        this.columns = columns;
    }

    public void addColumn(Column column) {
        columns.add(column);
        tableData.addColumn();
    }

    public boolean removeColumn(Column columnToRemove) {

        if(! hasColumn(columnToRemove)) {
            return false;

        } else {
            for(int i = 0; i < columns.size(); i++) {
                String columnName = columns.get(i).getName();

                if(columnName.equals(columnToRemove)) {
                    columns.remove(i);
                    break;
                }
            }
        }

        return true;
    }

    public Column getPrimaryKey() { return primaryKey; }

    public void setPrimaryKey(Column primaryKey) {
        this.primaryKey = primaryKey;
    }

    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

    // utility methods -------------------------------------------------------------------------------------------------

    public int getNumCols() { return columns.size(); }

    public int getNumRows() { return tableData.getNumRows(); }

    public Column getColumn(String columnName) {

        for(int i = 0; i < columns.size(); i++) {
            if(columns.get(i).getName().equalsIgnoreCase(columnName)) {
                return columns.get(i);
            }
        }

        return new Column("null", "null", false, 0);
    }

    /**
     * @param candidate the column to check
     * @return whether the candidate column is a member of this table
     */
    public boolean hasColumn(Column candidate) {
        return hasColumn(candidate.getName());
    }

    /**
     * @param candidate the column name to check
     * @return whether the candidate column is a member of this table
     */
    public boolean hasColumn(String candidate) {

        for(Column column : columns) {
            if(column.getName().equalsIgnoreCase(candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the total size of all columns within this table.
     * Eg. if the table had columns = {col1 Number(4), col2 Char(3), col3 Char(5)},
     * this method will return 12
     * @return the total size of all columns within the table
     */
    public int getTotalColumnSize() {

        int totalColSize = 0;

        for(Column column : columns) {
            totalColSize += column.size();
        }

        return totalColSize;
    }

    @Override
    public boolean equals() {
        
    }

    @Override
    public String toString() {
        return null;
    }
}
