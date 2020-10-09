package datastructures.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Histogram {

    private List<String> rawData;

    // string is the element, integer represents the number of occurrences of that element
    private TreeMap<String, Integer> barGraph;

    // the result of applying varying bucket sizes to the bar graph
    private TreeMap<String, Integer> histogram;

    // bucket size is the number of data items stored within a single bar
    private int numBars, bucketSize;

    public Histogram(ArrayList<String> rawData) {

        this.rawData = rawData;
        this.bucketSize = 1;

        // using a tree map to preserve ordering of the keys
        this.barGraph = new TreeMap<>();

        // adding elements and their frequencies
        for (String data : rawData) {

            // make sure adding a unique element
            boolean foundElement = false;

            for (String element : barGraph.keySet()) {
                if (data.equals(element)) {
                    barGraph.replace(data, barGraph.get(data) + 1);
                    foundElement = true;
                    break;
                }
            }

            if (! foundElement) {
                barGraph.put(data, 1);
            }
        }

        this.numBars = barGraph.size();

        // by default, just make the histogram a bar graph
        histogram = barGraph;
    }

    /**
     * Doubles the total number of bars in the histogram. This has the effect of decreasing
     * the range of data associated with a single bar (bucket size). If this produces an odd
     * number of bars, the rightmost bar will contain 1 less bucket than the others.
     */
    public void incrementNumBars() {

        numBars *= 2;

        if (numBars >= barGraph.size()) {
            numBars = barGraph.size();
        }

        bucketSize /= 2;

        if (bucketSize <= 1) {
            bucketSize = 1;
        }

        resize();
    }

    /**
     * Halves the total number of bars in the histogram. This has the effect of increasing
     * the range of data associated with a single bar (bucket size). If this produces an odd
     * number of bars, the rightmost bar will contain 1 less bucket than the others.
     */
    public void decrementNumBars() {

        numBars /= 2;

        if (numBars <= 1) {
            numBars = 1;
        }

        bucketSize *= 2;

        if (bucketSize >= barGraph.size()) {
            bucketSize = barGraph.size();
        }

        resize();
    }

    /**
     * Resizes the histogram in order to reflect the changes of either incrementing or
     * decrementing the number of bars present.
     */
    private void resize() {

        histogram = new TreeMap<>();

        // resize the histogram based on the bucket size
        StringBuilder rangedDataString = new StringBuilder();
        int cumulativeFrequencies = 0;

        // case 1: bucket size is 1 (basically a bar graph), don't do anything, we're done
        if (bucketSize == 1) {
            histogram = barGraph;
            return;
        }

        // case 2: bucket size is equal to the size of the bar graph (just 1 big bar)
        if (bucketSize == barGraph.size()) {

            for (Map.Entry<String, Integer> entry : barGraph.entrySet()) {
                cumulativeFrequencies += entry.getValue();
            }

            String firstElement = barGraph.firstKey();
            String lastElement  = barGraph.lastKey();
            rangedDataString.append(firstElement).append("-").append(lastElement);

            histogram.put(rangedDataString.toString(), cumulativeFrequencies);

        // case 3: normal case with ranges involved
        } else {

            int elementsToAdd = bucketSize - 1;
            int numLeftovers = 0;

            for (Map.Entry<String, Integer> entry : barGraph.entrySet()) {

                String data = entry.getKey();
                int frequency = entry.getValue();

                boolean firstToAdd = elementsToAdd == (bucketSize - 1);
                boolean lastToAdd = elementsToAdd == 0;

                cumulativeFrequencies += frequency;

                // first element to add to the bucket
                if (firstToAdd) {
                    rangedDataString.append(data).append("-");
                }

                // last element to add to the bucket
                else if (lastToAdd) {

                    rangedDataString.append(data);
                    histogram.put(rangedDataString.toString(), cumulativeFrequencies);

                    rangedDataString = new StringBuilder();
                    cumulativeFrequencies = 0;
                    elementsToAdd = bucketSize - 1;
                    numLeftovers = 0;

                    continue;
                }

                elementsToAdd--;
                numLeftovers++;
            }

            boolean hasLeftOverElements = numLeftovers > 0;

            // store the leftover data in the rightmost bar of the histogram
            if (hasLeftOverElements) {

                boolean onlyOneLeft = numLeftovers == 1;

                // case 1: just a single data entry leftover
                if (onlyOneLeft) {

                    // removing "-", cumulative frequencies and ranged data string contain the last element still
                    rangedDataString.deleteCharAt(rangedDataString.length() - 1);

                // case 2: more than 1 data entry leftover
                } else {

                    String lastData = barGraph.lastKey();
                    rangedDataString.append(lastData);
                }

                histogram.put(rangedDataString.toString(), cumulativeFrequencies);
            }
        }
    }

    // TODO explain this method better
    /**
     * @return 6 points used to estimate the height of all columns
     */
    public int[] getVerticalValues() {

        int[] verticalValues = new int[6];

        // get the largest item in the histogram
        int largestValue = 0;

        for (int value : histogram.values()) {
            if (value >= largestValue) {
                largestValue = value;
            }
        }

        // find the next value that divides evenly by 5
        while (! (largestValue % 5 == 0)) {
            largestValue++;
        }

        // get vertical points
        int blah = largestValue / 5;

        // include 0 in point
        for (int i = 0; i < 6; i++) {
            verticalValues[i] = blah * i;
        }

        return verticalValues;
    }

    public void printRawData() {

        StringBuilder print = new StringBuilder();
        print.append("Raw Data (Unsorted):\n");

        for (String item : rawData) {
            print.append(item).append(", ");
        }

        // remove last ", "
        print.deleteCharAt(print.length() - 1);
        print.deleteCharAt(print.length() - 1);

        System.out.println(print.toString());
    }

    public void printBarGraph() {

        StringBuilder print = new StringBuilder();
        print.append("Bar Graph (Sorted):\n");

        for (Map.Entry<String, Integer> entry : barGraph.entrySet()) {
            print.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        // remove last "\n"
        print.deleteCharAt(print.length() - 1);

        System.out.println(print.toString());
    }

    public void printHistogram() {

        StringBuilder print = new StringBuilder();
        print.append("Histogram (Sorted):\n");
        print.append("Number Bars: ").append(numBars);
        print.append(" Bucket Size: ").append(bucketSize).append("\n");

        for(Map.Entry<String, Integer> entry : histogram.entrySet()) {
            print.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        // remove last "\n"
        print.deleteCharAt(print.length() - 1);

        System.out.println(print.toString());
    }
}