package datastructures.relation.table.component;

/**
 * Class representing a column belonging to a table. Each column has a name,
 * datatype (CHAR, NUMBER, or DATE), a size/decimal size (Eg. 123.45 has a size of 3 and a decimal size of 2),
 * and a file structure associated with it.
 */
public class Column {

    private String name;
    private DataType dataType;
    private int size, decimalSize;
    private FileStructure fileStructure;

    /**
     * Default constructor used for un-serializing serialized data.
     * Should not be used for anything else.
     */
    public Column() {
        this.name = "";
        this.dataType = DataType.CHAR;
        this.size  = 0;
        this.decimalSize = 0;
        fileStructure = FileStructure.NONE;
    }

    /**
     * Column that does not have a file structure associated with it.
     * @param name is the name of the column
     * @param dataType is the datatype of the column
     * @param size is the size of the column
     * @param decimalSize is the decimal size of the column
     */
    public Column(String name, DataType dataType, int size, int decimalSize) {
        this.name = name;
        this.dataType = dataType;
        this.size = size;
        this.decimalSize = decimalSize;
        fileStructure = FileStructure.NONE;
    }

    /**
     * Column that does have a file structure associated with it.
     * @param name is the name of the column
     * @param dataType is the datatype of the column
     * @param size is the size of the column
     * @param decimalSize is the decimal size of the column
     * @param fileStructure is the file structure built on this column (can't be clustered file)
     */
    public Column(String name, DataType dataType, int size, int decimalSize, FileStructure fileStructure) {
        this(name, dataType, size, decimalSize);
        assert fileStructure != FileStructure.CLUSTERED_FILE;
        this.fileStructure = fileStructure;
    }

    /**
     * Copy constructor to make a deep copy of a column.
     * @param toCopy is the column to make a deep copy of
     */
    public Column(Column toCopy) {
        this.name = toCopy.name;
        this.dataType = toCopy.dataType;
        this.size = toCopy.size;
        this.decimalSize = toCopy.decimalSize;
        this.fileStructure = toCopy.fileStructure;
    }

    public String getColumnName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public int size() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDecimalSize() {
        return decimalSize;
    }

    public void setDecimalSize(int decimalSize) {
        this.decimalSize = decimalSize;
    }

    public FileStructure getFileStructure() {
        return fileStructure;
    }

    public void setFileStructure(FileStructure fileStructure) {
        assert fileStructure != FileStructure.CLUSTERED_FILE;
        this.fileStructure = fileStructure;
    }

    public void removeFileStructure() {
        this.fileStructure = FileStructure.NONE;
    }

    @Override
    public String toString() {
        StringBuilder print = new StringBuilder();
        print.append(name).append(" ").append(dataType);
        if (dataType == DataType.DATE) {
            return print.toString();
        }
        print.append("(").append(size);
        if (decimalSize > 0) {
            print.append(",").append(decimalSize);
        }
        print.append(")");
        return print.toString();
    }
}