package utilities;

/**
 * This class is used for query cost estimation. Separated into unsorted file, sorted file
 * secondary b-tree, clustered b-tree, hash table, clustered file, and join costs. Other
 * misc calculations follow after that. Here is a key of all the variables here.
 * r          (numRecords)         number of records in a file
 * |r|        (recordSize)         size of a record
 * block size (BLOCK_SIZE)         given
 * bf         (blockingFactor)     blocking factor
 * b          (blocks)             blocks
 * d                               distinct values of an attribute
 * s          (selectivity)        selectivity of an attribute
 * l          (levels)             number of levels in a b-tree
 * m          (degree)             degree of the tree
 * bl         (terminalLevelNodes) number of nodes at the terminal level of a b-tree
 */
public class Cost {

    private static final int BLOCK_SIZE = 512;

    // can't instantiate me!
    private Cost() {}

    // unsorted file costs ---------------------------------------------------------------------------------------------

    public static int unsortedUnique(int blocks) {
        return blocks;
    }

    public static int unsortedNonUnique(int blocks) {
        return blocks;
    }

    public static int unsortedRange(int blocks) {
        return blocks;
    }

    public static int unsortedPrintUnsorted(int blocks) {
        return blocks;
    }

    public static int unsortedPrintSorted(int blocks) {
        return ((int) (blocks * log(blocks))) + blocks;
    }

    // sorted file costs -----------------------------------------------------------------------------------------------

    public static int sortedUnique(int blocks) {
        return (int) log(blocks);
    }

    public static int sortedNonUnique(int blocks, double selectivity, int blockingFactor) {
        return (int) (log(blocks) + Math.ceil(selectivity / blockingFactor) - 1);
    }

    public static int sortedRange(int blocks) {
        return (int) log(blocks) + (blocks / 2);
    }

    public static int sortedPrintUnsorted(int blocks) {
        return blocks;
    }

    public static int sortedPrintSorted(int blocks) {
        return blocks;
    }

    // secondary b-tree costs ------------------------------------------------------------------------------------------

    public static int secondaryBTreeUnique(int levels) {
        return levels + 1;
    }

    // degree = m
    public static int secondaryBTreeNonUnique(int levels, int degree, double selectivity) {
        return (int) ((levels + Math.ceil(selectivity / (Math.ceil(degree / 2.0) - 1)) - 1) + selectivity);
    }

    // terminal level nodes = bL
    public static int secondaryBTreeRange(int levels, int terminalLevelNodes, int numRecords) {
        return (int) (levels + (terminalLevelNodes / 2.0) + (numRecords / 2.0));
    }

    public static int secondaryBTreePrintUnsorted(int levels, int terminalLevelNodes, int numRecords) {
        return levels + terminalLevelNodes + numRecords;
    }

    public static int secondaryBTreePrintSorted(int levels, int terminalLevelNodes, int numRecords) {
        return levels + terminalLevelNodes + numRecords;
    }

    // clustered b-tree costs ------------------------------------------------------------------------------------------

    public static int clusteredBTreeUnique(int levels) {
        return levels;
    }

    public static int clusteredBTreeNonUnique(int levels, double selectivity, int degree) {
        return (int) (levels + Math.ceil(selectivity / (degree - 1)) - 1);
    }

    public static int clusteredBTreeRange(int levels, int terminalLevelNodes) {
        return (int) (levels + (terminalLevelNodes / 2.0));
    }

    public static int clusteredBTreePrintUnsorted(int levels, int terminalLevelNodes) {
        return levels + terminalLevelNodes;
    }

    public static int clusteredBTreePrintSorted(int levels, int terminalLevelNodes) {
        return levels + terminalLevelNodes;
    }

    // hash table costs ------------------------------------------------------------------------------------------------

    public static int hashTableUnique() {
        return 1 + 1;
    }

    public static int hashTableNonUnique(int selectivity) {
        return 1 + selectivity;
    }

    // can't do ranged queries with hash tables!
    public static int hashTableRange() {
        return -1;
    }

    public static int hashTablePrintUnsorted(int numBins, int numRecords) {
        return numBins + numRecords;
    }

    // can't print sorted with hash tables!
    public static int hashTablePrintSorted() {
        return -1;
    }

    // clustered file costs --------------------------------------------------------------------------------------------

    public static int clusteredFileUnique(int table1Blocks, int table2Blocks) {
        return (int) log(table1Blocks + table2Blocks);
    }

    public static int clusteredFileNonUnique(int table1Blocks, int table2Blocks, double selectivity,
                                             int blockingFactor) {
        return (int) (log(table1Blocks + table2Blocks) + Math.ceil(selectivity / blockingFactor) - 1);
    }

    public static int clusteredFileRange(int table1Blocks, int table2Blocks) {
        return (int) (log(table1Blocks + table2Blocks) + (table1Blocks + table2Blocks) / 2.0);
    }

    public static int clusteredFilePrintUnsorted(int table1Blocks, int table2Blocks) {
        return table1Blocks + table2Blocks;
    }

    public static int clusteredFilePrintSorted(int table1Blocks, int table2Blocks) {
        return table1Blocks + table2Blocks;
    }

    // join costs ------------------------------------------------------------------------------------------------------

    public static int nestedLoopJoin(int table1Blocks, int table2Blocks) {
        if(table1Blocks < table2Blocks) {
            return table1Blocks + (table1Blocks * table2Blocks);
        } else {
            return table2Blocks + (table2Blocks * table1Blocks);
        }
    }

    public static int bTreeJoin(int table1Blocks, int table1NumRecords, int table2Levels, int table2Selectivity) {
        return table1Blocks + table1NumRecords * (table2Levels + table2Selectivity);
    }

    public static int clusteredJoin(int table1Blocks, int table2Blocks) {
        return table1Blocks + table2Blocks;
    }

    // other -----------------------------------------------------------------------------------------------------------

    public static int blockingFactor(int recordSize) {
        return (int) Math.floor((double) BLOCK_SIZE / recordSize);
    }

    public static int blocks(int numRecords, int blockingFactor) {
        return (int) Math.ceil((double) numRecords / blockingFactor);
    }

    public static double selectivity(int numRecords, int distinctValues) {
        return (double) numRecords / distinctValues;
    }

    // m
    public static int degree(int keySize) {
        return (int) Math.floor((double) (BLOCK_SIZE + 4 + keySize) / (keySize + 8));
    }

    // l
    public static int levels(int numRecords, int degree) {
        double temp1 = numRecords + 1 / 2.0;
        double temp2 = Math.ceil(degree / 2.0);
        return (int) (log(temp1) / log(temp2) + 1);
    }

    // bl
    public static int terminalLevelNodes(int numRecords, int degree) {
        return (int) Math.floor(numRecords / (Math.ceil(degree / 2.0) - 1));
    }

    public static double log(double value) {
        return Math.log(value) / Math.log(2);
    }
}