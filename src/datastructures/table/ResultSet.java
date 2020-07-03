package datastructures.table;

import java.util.ArrayList;

/**
 * Represents the data returned after execution of a query. Raw data is set initially, afterwards
 * rows and columns are changed to reflect what the query is asking.
 */
public class ResultSet {

    private ArrayList<String> rawColumnNames, filteredColumnNames;
    private ArrayList<ArrayList<String>> rawData, filteredData;

    public ResultSet(ArrayList<String> rawColumnNames, ArrayList<ArrayList<String>> rawData) {
        this.rawColumnNames = rawColumnNames;
        this.rawData = rawData;
    }

    public ArrayList<String> getRawColumnNames() { return rawColumnNames; }

    public ArrayList<String> getFilteredColumnNames() { return filteredColumnNames; }

    public ArrayList<ArrayList<String>> getRawData() {
        return rawData;
    }

    public ArrayList<ArrayList<String>> getFilteredData() { return filteredData; }

    // utility methods -------------------------------------------------------------------------------------------------

    public int getRawNumRows() {
        return rawData.size();
    }

    public int getRawNumCols() {
        return ! rawData.isEmpty() ? rawData.get(0).size() : 0;
    }

    public int getFilteredNumRows() { return filteredData.size(); }

    public int getFilteredNumCols() { return ! filteredData.isEmpty() ? filteredData.get(0).size() : 0; }

    // applying transformations on the raw data to fit the query request -----------------------------------------------

    /**
     * @return a string representation of the FILTERED data
     */
    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        // the following will be used for formatting the result set data to look pretty
        int[] largestColSizes = new int[getFilteredNumCols()];

        // going down column by column
        for(int cols = 0; cols < filteredData.get(0).size(); cols++) {

            int largestRowInColSize = 0;

            for(int rows = 0; rows < filteredData.size(); rows++) {

                int rowInColSize = filteredData.get(rows).get(cols).length();

                if(rowInColSize > largestRowInColSize) {
                    largestRowInColSize = rowInColSize;
                }
            }

            largestColSizes[cols] = largestRowInColSize;
        }

        for(int rows = 0; rows < filteredData.size(); rows++) {

            for(int cols = 0; cols < filteredData.get(rows).size(); cols++) {

                // space formatting
                int colSize = filteredData.get(rows).get(cols).length();
                int largestColSize = largestColSizes[cols];
                int spaceOffset = Math.abs(colSize - largestColSize);

                StringBuilder spaces = new StringBuilder();

                for(int i = 0; i < spaceOffset; i++) {
                    spaces.append(" ");
                }

                print.append(filteredData.get(rows).get(cols)).append(spaces).append("\n");
            }
        }

        return print.toString();
    }
}