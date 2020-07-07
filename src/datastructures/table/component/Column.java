package datastructures.table.component;

import utilities.enums.DataType;

public class Column {

    private String name;
    private DataType dataType;
    private int size;
    private FileStructure fileStructure;

    /**
     * Default constructor used for un-serializing serialized data.
     * Should not be used for anything else.
     */
    public Column() {

        this.name      = "";
        this.dataType  = DataType.CHAR;
        this.size      = 0;
        fileStructure  = FileStructure.NONE;
    }

    public Column(String name, DataType dataType, int size) {

        this.name      = name;
        this.dataType  = dataType;
        this.size      = size;
        fileStructure  = FileStructure.NONE;
    }

    public Column(String name, DataType dataType, int size, FileStructure fileStructure) {
        this(name, dataType, size);
        this.fileStructure = fileStructure;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public void setDataType(DataType dataType) { this.dataType = dataType; }

    public DataType getDataType() { return dataType; }

    public void setSize(int size) { this.size = size; }

    public int size() { return size; }

    public FileStructure getFileStructure() { return fileStructure; }

    public void setFileStructure(FileStructure fileStructure) { this.fileStructure = fileStructure; }

    public void removeFileStructure() { this.fileStructure = FileStructure.NONE; }

    /**
     * Returns whether this column is deeply equal to the object provided.
     * Pretty much just used for testing purposes.
     * @param other object for comparison
     * @return whether the column is deeply equal to the object supplied
     */
    @Override
    public boolean equals(Object other) {

        if(other == this) {
            return true;
        }

        if(! (other instanceof Column)) {
            return false;
        }

        Column otherColumn = (Column) other;

        if(! otherColumn.getName().equals(this.getName())) {
            System.out.println("Columns not equal");
            System.out.println("Other Column name: " + otherColumn.getName() +
                    " This Column name: " + this.getName());
            return false;
        }

        if(! otherColumn.getDataType().equals(this.getDataType())) {
            System.out.println("Columns not equal");
            System.out.println("Other Column data type: " + otherColumn.getDataType() +
                    " This Column data type: " + this.getDataType());
            return false;
        }

        if(otherColumn.size() != this.size()) {
            System.out.println("Columns not equal");
            System.out.println("Other Column size: " + otherColumn.size() +
                    " This Column size: " + this.size());
            return false;
        }

        if(otherColumn.getFileStructure() != this.getFileStructure()) {
            System.out.println("Columns not equal");
            System.out.println("Other Column file structure: " + otherColumn.getFileStructure() +
                    " This Column file structure: " + this.getFileStructure());
            return false;
        }

        return true;
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();
        print.append(name).append(" ").append(dataType).append("(").append(size).append(")");
        return print.toString();
    }
}
