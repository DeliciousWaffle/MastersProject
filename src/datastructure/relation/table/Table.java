package datastructure.relation.table;

import datastructure.relation.table.component.Column;
import datastructure.relation.table.component.TableData;
import datastructure.relation.table.component.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of the table stored within the database. This stores stuff
 * like the table name, columns, whether there's a primary key, and the data itself.
 */
public class Table {

    private String tableName;
    private List<Column> columns;
    private String primaryKey;
    private List<String> foreignKeys;
    private String clusteredWith;
    private TableData tableData;

    /**
     * Default constructor used for un-serializing serialized data.
     * Should not be used for any other purpose
     */
    public Table() {

        this.tableName = "";
        this.columns = new ArrayList<>();
        this.primaryKey = "";
        this.foreignKeys = new ArrayList<>();
        this.clusteredWith = "";
        this.tableData = new TableData(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Mainly used for testing purposes, I hope. Everything except
     * the table name must be manually set so nothing breaks.
     * @param tableName is the name of the table
     */
    public Table(String tableName) {

        this.tableName = tableName;
        this.columns = new ArrayList<>();
        this.primaryKey = "none";
        this.foreignKeys = new ArrayList<>();
        this.clusteredWith = "none";
        this.tableData = new TableData(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Used when the user uses the CREATE TABLE command. Table data is set later.
     * @param tableName is the name of the table
     * @param columns are the columns that the table contains
     * @param primaryKey is the primary key of this table
     * @param foreignKeys are foreign keys of this table
     */
    public Table(String tableName, List<Column> columns, String primaryKey, List<String> foreignKeys) {

        this(tableName);
        this.columns = columns;
        this.primaryKey = primaryKey;
        this.foreignKeys = foreignKeys;
        this.clusteredWith = "none";
        this.tableData = new TableData(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Returns a deep copy of this table. Need came from result set class tampering with
     * original table data which is not good.
     * @param toCopy is the table to copy
     */
    public Table(Table toCopy) {
        this.tableName = toCopy.tableName;
        this.columns = new ArrayList<>();
        for(Column column : toCopy.getColumns()) {
            this.columns.add(new Column(column));
        }
        this.primaryKey = toCopy.primaryKey;
        this.foreignKeys = new ArrayList<>();
        for(String foreignKey : toCopy.foreignKeys) {
            this.foreignKeys.add(foreignKey);
        }
        this.clusteredWith = toCopy.clusteredWith;
        this.tableData = new TableData(toCopy.getTableData());
    }
    // getters, setters ------------------------------------------------------------------------------------------------

    /**
     * @return the name of this table
     */
    public String getTableName() { return tableName; }

    /**
     * @param tableName is the table name to be set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the columns stored within this table
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * @param columns are the columns to be set for this table
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * Adds a column to the table. Columns are appended to the rightmost side of the table.
     * Rows of that column are populated with "null" values.
     * @param column is the column to add
     */
    public void addColumn(Column column) {
        columns.add(column);
        tableData.addColumn(column.size());
    }

    /**
     * Removes the column supplied from the table.
     * @param columnToRemove is the column to remove
     */
    public void removeColumn(Column columnToRemove) {
        removeColumn(columnToRemove.getName());
    }

    /**
     * Removes the column supplied from the table.
     * @param columnToRemove is the name of the column to remove
     */
    public void removeColumn(String columnToRemove) {
        for(int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getName();
            if(columnName.equalsIgnoreCase(columnToRemove)) {
                columns.remove(i);
                return;
            }
        }
    }

    /**
     * @return the primary key of this table
     */
    public String getPrimaryKey() { return primaryKey; }

    /**
     * @param primaryKey is what to set the new primary key to
     */
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return whether this table has a primary key
     */
    public boolean hasPrimaryKey() {
        return ! primaryKey.equals("none");
    }

    /**
     * @return the foreign keys of this table
     */
    public List<String> getForeignKeys() { return foreignKeys; }

    /**
     * @param foreignKeys are the foreign keys to set
     */
    public void setForeignKeys(List<String> foreignKeys) { this.foreignKeys = foreignKeys; }

    /**
     * @param foreignKey is the foreign key to add
     */
    public void addForeignKey(String foreignKey) {
        if(! hasColumn(foreignKey)) {
            foreignKeys.add(foreignKey);
        }
    }

    /**
     * @return the table name that this table is clustered with
     */
    public String getClusteredWith() { return clusteredWith; }

    /**
     * @param clusteredWith is the table that this table will be clustered with
     */
    public void setClusteredWith(String clusteredWith) {
        this.clusteredWith = clusteredWith;
    }

    /**
     * @return whether this table is clustered with another table
     */
    public boolean isClustered() { return ! clusteredWith.equals("none"); }

    /**
     * @return all the data held within a table
     */
    public TableData getTableData() {
        return tableData;
    }

    /**
     * @param tableData is the table data to set
     */
    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

    // utility methods -------------------------------------------------------------------------------------------------

    /**
     * @return the number of columns within the table
     */
    public int getNumCols() { return columns.size(); }

    /**
     * @return the number of rows within the table
     */
    public int getNumRows() { return tableData.getNumRows(); }

    /**
     * Given a column name, returns a reference to that column.
     * @param columnName is the column to get
     * @return reference to the column
     */
    public Column getColumn(String columnName) {

        for(int i = 0; i < columns.size(); i++) {
            if(columns.get(i).getName().equalsIgnoreCase(columnName)) {
                return columns.get(i);
            }
        }

        // didn't find what was asked, just return empty column
        return new Column("null", DataType.CHAR, 0);
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

    /**
     * Returns whether this table is deeply equal to the object provided.
     * Pretty much just used for testing purposes.
     * @param other object for comparison
     * @return whether the table is deeply equal to the object supplied
     */
    @Override
    public boolean equals(Object other) {

        if(other == this) {
            return true;
        }

        if(! (other instanceof Table)) {
            return false;
        }

        Table otherTable = (Table) other;

        if(! otherTable.getTableName().equals(this.getTableName())) {
            System.out.println("Tables not equal");
            System.out.println("Other Table name: " + otherTable.getTableName() +
                    " This Table name: " + this.getTableName());
            return false;
        }

        if(otherTable.getNumCols() != this.getNumCols()) {
            System.out.println("Tables not equal");
            System.out.println("Other Table number columns: " + otherTable.getNumCols() +
                    " This Table number columns: " + this.getNumCols());
            return false;
        }

        if(otherTable.getNumRows() != this.getNumRows()) {
            System.out.println("Tables not equal");
            System.out.println("Other Table number rows: " + otherTable.getNumRows() +
                    " This Table number rows: " + this.getNumRows());
            return false;
        }

        for(int i = 0; i < this.getNumCols(); i++) {

            Column otherColumn = otherTable.getColumns().get(i);
            Column thisColumn  = this.getColumns().get(i);

            if(! thisColumn.equals(otherColumn)) {
                return false;
            }
        }

        if(! otherTable.getPrimaryKey().equals(this.getPrimaryKey())) {
            return false;
        }

        if(! otherTable.getClusteredWith().equals(this.getClusteredWith())) {
            return false;
        }

        return otherTable.getTableData().equals(this.getTableData());
    }

    /**
     * @return a string representation of this table
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Table Name: ").append(tableName).append("\n");
        stringBuilder.append("Columns: ");

        for(Column column : columns) {
            stringBuilder.append(column.toString()).append(", ");
        }

        // remove ", "
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        stringBuilder.append("\n").append("Primary Key: ").append(primaryKey).append("\n\n");

        // adding table data stuff
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
        stringBuilder.append(tableData);

        return stringBuilder.toString();
    }
}