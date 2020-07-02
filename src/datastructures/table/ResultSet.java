package datastructures.table;

/**
 * The rows and columns returned after executing a query.
 */
public class ResultSet {

    private final String[] columnNames;
    private final String[][] data;

    public ResultSet(String[] columnNames, String[][] data) {
        this.columnNames = columnNames;
        this.data = data;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String[][] getData() {
        return data;
    }

    public int getNumRows() {
        return data.length;
    }

    public int getNumCols() {
        return columnNames.length;
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        for(String[] rows : data) {
            for(String col : rows) {
                print.append(col).append("\t");
            }
            print.append("\n");
        }

        return print.toString();
    }
}