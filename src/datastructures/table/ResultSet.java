package datastructures.table;

/**
 * Represents the data returned after execution of a query.
 */
public class ResultSet {

    private final String[][] data;

    public ResultSet(String[][] data) {
        this.data = data;
    }

    public String[][] getData() {
        return data;
    }

    public int getNumRows() {
        return data.length;
    }

    public int getNumCols() {
        return data[0].length;
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        // the following will be used for formatting the table data to look pretty
        int[] largestColSizes = new int[getNumCols()];

        // going down column by column
        for(int cols = 0; cols < data[0].length; cols++) {

            int largestRowInColSize = 0;

            for(int rows = 0; rows < data.length; rows++) {


                System.out.println(data[rows][cols]);
                int rowInColSize = data[rows][cols].length();

                if(rowInColSize > largestRowInColSize) {
                    largestRowInColSize = rowInColSize;
                }
            }

            largestColSizes[cols] = largestRowInColSize;
        }

        for(int rows = 0; rows < data.length; rows++) {

            for(int cols = 0; cols < data[rows].length; cols++) {

                // space formatting
                int colSize = data[rows][cols].length();
                int largestColSize = largestColSizes[cols];
                int spaceOffset = Math.abs(colSize - largestColSize);

                StringBuilder spaces = new StringBuilder();

                for(int i = 0; i < spaceOffset; i++) {
                    spaces.append(" ");
                }

                print.append(data[rows][cols]).append(spaces).append("\n");
            }
        }

        return print.toString();
    }
}