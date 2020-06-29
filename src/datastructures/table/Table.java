package datastructures.table;

import datastructures.table.component.Column;
import datastructures.table.component.FileStructure;

import java.util.ArrayList;

public class Table {

    private String name;
    private ArrayList<Column> columns;

    public Table(String name, ArrayList<Column> columns) {

        this.name    = name;
        this.columns = columns;
    }

    /**
     * @return the name of this table
     */
    public String getName() { return name; }

    public ArrayList<Column> getColumns() { return columns; }

    public int getNumCols() { return columns.size(); }

    public Column getColumn(String columnName) {

        for(int i = 0; i < columns.size(); i++) {
            if(columns.get(i).getName().equalsIgnoreCase(columnName)) {
                return columns.get(i);
            }
        }

        return null;
    }

    /**
     * Returns whether the candidate column is a member of this table.
     * @param candidate the column to check
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

    /**
     * Removes all file structures on this table. Typically called before clustering this table
     * with another table since clustered files don't allow intermingling of other file structures.
     */
    /*public void removeAllFileStructures() {
        for(Column column : columns) {
            column.removeAllFileStructures();
        }
    }

    @Override
    public String toString() {

        StringBuilder table = new StringBuilder();
        table.append("Table: ").append(name).append(" (");

        for(Column column : columns) {
            table.append("Column: ").append(column).append("\n");
            for(FileStructure fileStructure : column.getFileStructures()) {
                table.append("File Structure(s): ").append(fileStructure).append(" ");
            }
            if(! column.getFileStructures().isEmpty()) {
                table.deleteCharAt(table.length() - 1);
                table.append("\n");
            } else {
                table.deleteCharAt(table.length() - 1);
            }
        }

        return table.toString();
    }*/
}
