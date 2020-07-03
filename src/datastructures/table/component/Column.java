package datastructures.table.component;

public class Column {

    private String name;
    private String dataType; // make into an enum or just remove entirely
    private boolean isNumeric;
    private boolean canAlterCharToInt;
    private int size;
    private FileStructure fileStructure;

    public Column(String name, String dataType, boolean isNumeric, int size) {

        this.name      = name;
        this.dataType  = dataType;
        this.size      = size;
        this.isNumeric = isNumeric;
        fileStructure  = FileStructure.NONE;
    }

    public String getName() { return name; }

    public void setDataType(String dataType) { this.dataType = dataType; }

    public String getDataType() { return dataType; }

    public void setIsNumeric(boolean isNumeric) { this.isNumeric = isNumeric; }

    public boolean isNumeric() { return isNumeric; }

    public void setCanAlterCharToInt(boolean canAlterCharToInt) { this.canAlterCharToInt = canAlterCharToInt; }

    public boolean canAlterCharToInt() { return canAlterCharToInt; }

    public void setSize(int size) { this.size = size; }

    public int size() { return size; }

    public FileStructure getFileStructure() { return fileStructure; }

    public void setFileStructure(FileStructure fileStructure) { this.fileStructure = fileStructure; }

    public void removeFileStructure() { fileStructure = fileStructure.NONE; }

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

        if(otherColumn.isNumeric() != this.isNumeric()) {
            System.out.println("Columns not equal");
            System.out.println("Other Column is numeric: " + otherColumn.isNumeric() +
                    " This Column is numeric: " + this.isNumeric());
            return false;
        }

        if(otherColumn.canAlterCharToInt() != this.canAlterCharToInt()) {
            System.out.println("Columns not equal");
            System.out.println("Other Column can alter char to int: " +
                    otherColumn.canAlterCharToInt() + " This Column can alter char to int: " +
                    this.canAlterCharToInt());
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
