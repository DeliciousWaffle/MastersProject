package datastructures.relation.table;

import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.TableData;
import datastructures.relation.table.component.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation of the table stored within the database. This stores stuff
 * like the table name, columns, whether there's a primary key, and the data itself.
 */
public class Table {

    private String tableName;
    private List<Column> columns;
    private List<String> primaryKeys;
    private Map<String, String> foreignKeys;
    private String clusteredWith;
    private TableData tableData;

    /**
     * Default constructor used for un-serializing serialized data.
     * Should not be used for any other purpose
     */
    public Table() {
        this.tableName = "none";
        this.columns = new ArrayList<>();
        this.primaryKeys = new ArrayList<>();
        this.foreignKeys = new HashMap<>();
        this.clusteredWith = "none";
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
        this.primaryKeys = new ArrayList<>();
        this.foreignKeys = new HashMap<>();
        this.clusteredWith = "none";
        this.tableData = new TableData(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Used when the user uses the CREATE TABLE command. Table data is set later.
     * @param tableName is the name of the table
     * @param columns are the columns that the table contains
     * @param primaryKeys is the primary key of this table
     * @param foreignKeys are foreign keys of this table
     */
    public Table(String tableName, List<Column> columns, List<String> primaryKeys, Map<String, String> foreignKeys) {
        this(tableName);
        this.columns = columns;
        this.primaryKeys = primaryKeys;
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
        this.primaryKeys = new ArrayList<>();
        this.primaryKeys.addAll(toCopy.primaryKeys);
        this.foreignKeys = new HashMap<>();
        this.foreignKeys.putAll(toCopy.foreignKeys);
        this.clusteredWith = toCopy.clusteredWith;
        this.tableData = new TableData(toCopy.getTableData());
    }

    // getters, setters ------------------------------------------------------------------------------------------------

    /**
     * @return the name of this table
     */
    public String getTableName() {
        return tableName;
    }

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
     * Removes the column supplied from the table and all the data there.
     * @param columnToRemove is the column to remove
     */
    public void removeColumn(Column columnToRemove) {
        removeColumn(columnToRemove.getColumnName());
    }

    /**
     * Removes the column supplied from the table and all the data there.
     * @param columnToRemove is the name of the column to remove
     */
    public void removeColumn(String columnToRemove) {
        for(int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getColumnName();
            if(columnName.equalsIgnoreCase(columnToRemove)) {
                columns.remove(i);
                tableData.deleteColumnAt(i);
                return;
            }
        }
    }

    /**
     * @return the primary keys of this table
     */
    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * @param primaryKeys is what to set the new primary key to
     */
    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public void addPrimaryKey(String primaryKey) {
        primaryKeys.add(primaryKey);
    }

    /**
     * @return the foreign keys of this table
     */
    public Map<String, String> getForeignKeys() {
        return foreignKeys;
    }

    /**
     * @param foreignKeys are the foreign keys to set
     */
    public void setForeignKeys(Map<String, String> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    /**
     * @param columnName is the foreign key to add
     */
    public void addForeignKey(String tableName, String columnName) {
        foreignKeys.put(tableName, columnName);
    }

    /**
     * @return the table name that this table is clustered with
     */
    public String getClusteredWith() {
        return clusteredWith;
    }

    /**
     * @param clusteredWith is the table that this table will be clustered with
     */
    public void setClusteredWith(String clusteredWith) {
        this.clusteredWith = clusteredWith;
    }

    /**
     * @return whether this table is clustered with another table
     */
    public boolean isClustered() {
        return ! clusteredWith.equals("none");
    }

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
    public int getNumCols() {
        return columns.size();
    }

    /**
     * @return the number of rows within the table
     */
    public int getNumRecords() {
        return tableData.getNumRows();
    }

    /**
     * Given a column name, returns a reference to that column.
     * @param columnName is the column to get
     * @return reference to the column
     */
    public Column getColumn(String columnName) {

        for (Column column : columns) {
            if (column.getColumnName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }

        // didn't find what was asked, just return empty column
        return new Column("null", DataType.CHAR, 0, 0);
    }

    /**
     * @param candidate the column to check
     * @return whether the candidate column is a member of this table
     */
    public boolean hasColumn(Column candidate) {
        return hasColumn(candidate.getColumnName());
    }

    /**
     * @param candidate the column name to check
     * @return whether the candidate column is a member of this table
     */
    public boolean hasColumn(String candidate) {

        for(Column column : columns) {
            if(column.getColumnName().equalsIgnoreCase(candidate)) {
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
    public int getRecordSize() {

        int totalColSize = 0;

        for(Column column : columns) {
            totalColSize += column.size();
        }

        return totalColSize;
    }

    public void addRow(List<String> row) {
        tableData.addRow(row);
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

        if(otherTable.getNumRecords() != this.getNumRecords()) {
            System.out.println("Tables not equal");
            System.out.println("Other Table number rows: " + otherTable.getNumRecords() +
                    " This Table number rows: " + this.getNumRecords());
            return false;
        }

        for(int i = 0; i < this.getNumCols(); i++) {

            Column otherColumn = otherTable.getColumns().get(i);
            Column thisColumn  = this.getColumns().get(i);

            if(! thisColumn.equals(otherColumn)) {
                return false;
            }
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

        stringBuilder.append("\n").append("Primary Key(s): ");
        if(primaryKeys.isEmpty()) {
            stringBuilder.append("none");
        } else {
            for(String primaryKey : primaryKeys) {
                stringBuilder.append(primaryKey).append(", ");
            }
            // remove ", "
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        stringBuilder.append("\nForeign Key(s): ");
        if(foreignKeys.isEmpty()) {
            stringBuilder.append("none");
        } else {
            for(Map.Entry<String, String> entry : foreignKeys.entrySet()) {
                stringBuilder.append(entry.getKey()).append(".").append(entry.getValue()).append(", ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        stringBuilder.append("\nClustered With Table: ").append(clusteredWith).append("\n\n");

        // adding table data stuff
        for(Column column : columns) {

            String columnName = column.getColumnName();

            StringBuilder spaces = new StringBuilder();
            int columnNameLength = columnName.length();
            int maxNumSpaces = column.size();
            if (column.getDecimalSize() > 0) {
                maxNumSpaces += (column.getDecimalSize() + 1); // + 1 to account for the "."
            }
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
            if (column.getDecimalSize() > 0) {
                maxColumnNameLength += (column.getDecimalSize() + 1); // + 1 to account for the ".";
            }
            int columnNameLength = column.getColumnName().length();
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