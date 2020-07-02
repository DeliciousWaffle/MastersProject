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

    @Override
    public String toString() {

        StringBuilder tableColumn = new StringBuilder();
        tableColumn.append(name).append(" ").append(dataType).append(" ").append(size);
        return tableColumn.toString();
    }
}
