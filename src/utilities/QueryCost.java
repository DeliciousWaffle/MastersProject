package utilities;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;

import java.util.List;

/**
 * This class is used for query cost estimation. Separated into starting calculations, b-tree specific
 * calculations, unsorted file, sorted file, secondary b-tree, clustered b-tree, hash table, clustered file,
 * and join costs. Other misc calculations follow after that. Each calculation has a "to string" method
 * associated with it in order to view how numbers are used and for debugging purposes.
 * Here is a key of all the variables used in this class.
 * Variable:  Denoted As:          Description:
 * -------------------------------------------------------------------------------------------------------
 * r          (numberRecords)      number of records in a file
 * |r|        (recordSize)         size of a record
 * block size (BLOCK_SIZE)         given
 * bf         (blockingFactor)     blocking factor
 * b          (blocks)             blocks
 * d          (distinctValues)     distinct values of an attribute
 * s          (selectivity)        selectivity of an attribute
 * l          (levels)             number of levels in a b-tree
 * m          (degree)             degree of the tree
 * bL         (terminalLevelNodes) number of nodes at the terminal level of a b-tree
 * Each formula has an associated "to string" method that is used for displaying
 * how calculations are performed. Also used for debugging purposes.
 */
public class QueryCost {

    private static final int BLOCK_SIZE = 512;

    // can't instantiate me!
    private QueryCost() {}

    // starting calculations -------------------------------------------------------------------------------------------

    // r - number of records in the file
    public static int getNumberRecords(Table table) {
        return table.getNumRows();
    }

    public static String getNumberRecordsToString(Table table) {
        return "r = " + getNumberRecords(table);
    }

    // |r| - record size
    public static int getRecordSize(List<Column> columns) {
        return columns.stream()
                .map(Column::size)
                .reduce(0, Integer::sum);
    }

    public static String getRecordSizeToString(List<Column> columns) {
        return "|r| = " + getRecordSize(columns);
    }

    // bf - blocking factor
    public static int blockingFactor(int recordSize) {
        return (int) Math.floor((double) BLOCK_SIZE / recordSize);
    }

    public static String blockingFactorToString(int recordSize) {
        return "bf = ⌊BlockSize / |r|⌋\n" +
                "bf = ⌊" + BLOCK_SIZE + " / |" + recordSize + "|⌋\n" +
                "bf = " + blockingFactor(recordSize);
    }

    // b - blocks
    public static int blocks(int numRecords, int blockingFactor) {
        return (int) Math.ceil((double) numRecords / blockingFactor);
    }

    public static String blocksToString(int numRecords, int blockingFactor) {
        return "b = ⌈r / bf⌉\n" +
                "b = ⌈" + numRecords + " / " + blockingFactor + " ⌉\n" +
                "b = " + blocks(numRecords, blockingFactor);
    }

    // s - selectivity
    public static double selectivity(int numRecords, int distinctValues) {
        return (double) numRecords / distinctValues;
    }

    public static String selectivityToString(int numRecords, int distinctValues) {
        return "s = r/d\n" +
                "s = " + numRecords + "/" + distinctValues + "\n" +
                "s = " + selectivity(numRecords, distinctValues);
    }

    // b-tree specific calculations ------------------------------------------------------------------------------------

    // m - b-tree degree
    public static int degree(int keySize) {
        return (int) Math.floor((double) (BLOCK_SIZE + 4 + keySize) / (keySize + 8));
    }

    public static String degreeToString(int keySize) {
        return "m = ⌊(BlockSize + 4 + KeySize) / (KeySize + 8)⌋\n" +
                "m = ⌊(" + BLOCK_SIZE + " + 4 + " + keySize + ") / (" + keySize + " + 8)⌋\n" +
                "m = " + degree(keySize);
    }

    // l - b-tree levels
    public static int levels(int numRecords, int degree) {
        double temp1 = numRecords + 1 / 2.0;
        double temp2 = Math.ceil(degree / 2.0);
        return (int) (log(temp1) / log(temp2) + 1);
    }

    public static String levelsToString(int numRecords, int degree) {
        return "l = (log(r + (1 / 2)) / log(m / 2) + 1)\n" +
                "l = (log(" + numRecords + " + (1 / 2)) / log(" + degree + " / 2) + 1)\n" +
                "l = " + levels(numRecords, degree);
    }

    // bL - number nodes at b-tree terminal level
    public static int terminalLevelNodes(int numRecords, int degree) {
        return (int) Math.floor(numRecords / (Math.ceil(degree / 2.0) - 1));
    }

    public static String terminalLevelNodesToString(int numRecords, int degree) {
        return "bL = ⌊r / (⌈m / 2⌉ - 1)⌋ \n" +
                "bL = ⌊" + numRecords + " / (⌈" + degree + " / 2⌉ - 1)⌋ \n" +
                "bL = " + terminalLevelNodes(numRecords, degree);

    }

    // unsorted file costs ---------------------------------------------------------------------------------------------

    public static int unsortedUnique(int blocks) {
        return blocks;
    }

    public static String unsortedUniqueToString(int blocks) {
        return "CUu = b\n" +
                "CUu = " + blocks;
    }

    public static int unsortedNonUnique(int blocks) {
        return blocks;
    }

    public static String unsortedNonUniqueToString(int blocks) {
        return "CUn = b\n" +
                "CUn = " + blocks;
    }

    public static int unsortedRange(int blocks) {
        return blocks;
    }

    public static String unsortedRangeToString(int blocks) {
        return "CUr = b\n" +
                "CUr = " + blocks;
    }

    public static int unsortedPrintUnsorted(int blocks) {
        return blocks;
    }

    public static String unsortedPrintUnsortedToString(int blocks) {
        return "CUpu = b\n" +
                "CUpu = " + blocks;
    }


    public static int unsortedPrintSorted(int blocks) {
        return ((int) (blocks * log(blocks))) + blocks;
    }

    public static String unsortedPrintSortedToString(int blocks) {
        return "CUps = b * log(b) + b\n" +
                "CUps = " + blocks + " log(" + blocks + ") + " + blocks + "\n" +
                "CUps = " + unsortedPrintSorted(blocks);
    }

    // sorted file costs -----------------------------------------------------------------------------------------------

    public static int sortedUnique(int blocks) {
        return (int) log(blocks);
    }

    public static String sortedUniqueToString(int blocks) {
        return "CSu = log(b)\n" +
                "CSu = " + "log(" + blocks + ")\n" +
                "CSu = " + sortedUnique(blocks);
    }

    public static int sortedNonUnique(int blocks, double selectivity, int blockingFactor) {
        return (int) (log(blocks) + Math.ceil(selectivity / blockingFactor) - 1);
    }

    public static String sortedNonUniqueToString(int blocks, double selectivity, int blockingFactor) {
        return "CSn = log(b) + ⌈s / bf⌉ - 1\n" +
                "CSn = log(" + blocks + ") + ⌈" + selectivity + "/" + blockingFactor + "⌉ - 1\n" +
                "CSn = " + sortedNonUnique(blocks, selectivity, blockingFactor);
    }

    public static int sortedRange(int blocks) {
        return (int) log(blocks) + (blocks / 2);
    }

    public static String sortedRangeToString(int blocks) {
        return "CSr = log(b) + b/2\n" +
                "CSr = log(" + blocks + ") + " + blocks + "/2\n" +
                "CSr = " + sortedRange(blocks);
    }

    public static int sortedPrintUnsorted(int blocks) {
        return blocks;
    }

    public static String sortedPrintUnsortedToString(int blocks) {
        return "CSpu = b\n" +
                "CSpu = " + blocks;
    }

    public static int sortedPrintSorted(int blocks) {
        return blocks;
    }

    public static String sortedPrintSortedToString(int blocks) {
        return "CSps = b\n" +
                "CSps = " + blocks;
    }

    // secondary b-tree costs ------------------------------------------------------------------------------------------

    public static int secondaryBTreeUnique(int levels) {
        return levels + 1;
    }

    public static String secondaryBTreeUniqueToString(int levels) {
        return "CBu = L + 1\n" +
                "CBu = " + levels + " + 1\n" +
                "CBu = " + secondaryBTreeUnique(levels);
    }

    // degree = m
    public static int secondaryBTreeNonUnique(int levels, int degree, double selectivity) {
        return (int) ((levels + Math.ceil(selectivity / (Math.ceil(degree / 2.0) - 1)) - 1) + selectivity);
    }

    public static String secondaryBTreeNonUniqueToString(int levels, int degree, double selectivity) {
        return "CBn = L + (⌈s / (⌈m / 2⌉ - 1)⌉ - 1) + s\n" +
                "CBn = " + levels + " + (⌈" + selectivity + " / (⌈" + degree + " / 2⌉ - 1)⌉ - 1) + " + selectivity +"\n" +
                "CBn = " + secondaryBTreeNonUnique(levels, degree, selectivity);
    }

    // terminal level nodes = bL
    public static int secondaryBTreeRange(int levels, int terminalLevelNodes, int numRecords) {
        return (int) (levels + (terminalLevelNodes / 2.0) + (numRecords / 2.0));
    }

    public static String secondaryBTreeRangeToString(int levels, int terminalLevelNodes, int numRecords) {
        return "CBr = L + bL/2 + r/2\n" +
                "CBr = " + levels + " + " + terminalLevelNodes + "/2 + " + numRecords + "/2\n" +
                "CBr = " + secondaryBTreeRange(levels, terminalLevelNodes, numRecords);
    }

    public static int secondaryBTreePrintUnsorted(int levels, int terminalLevelNodes, int numRecords) {
        return levels + terminalLevelNodes + numRecords;
    }

    public static String secondaryBTreePrintUnsortedToString(int levels, int terminalLevelNodes, int numRecords) {
        return "CBpu = L + bL + r\n" +
                "CBpu = " + levels + " + " + terminalLevelNodes + " + " + numRecords + "\n" +
                "CBpu = " + secondaryBTreePrintUnsorted(levels, terminalLevelNodes, numRecords);
    }

    public static int secondaryBTreePrintSorted(int levels, int terminalLevelNodes, int numRecords) {
        return levels + terminalLevelNodes + numRecords;
    }

    public static String secondaryBTreePrintSortedToString(int levels, int terminalLevelNodes, int numRecords) {
        return "CBps = L + bL + r\n" +
                "CBps = " + levels + " + " + terminalLevelNodes + " + " + numRecords + "\n" +
                "CBps = " + secondaryBTreePrintUnsorted(levels, terminalLevelNodes, numRecords);
    }

    // clustered b-tree costs ------------------------------------------------------------------------------------------

    public static int clusteredBTreeUnique(int levels) {
        return levels;
    }

    public static String clusteredBTreeUniqueToString(int levels) {
        return "CBPu = L\n" +
                "CBPu = " + levels;
    }

    public static int clusteredBTreeNonUnique(int levels, double selectivity, int degree) {
        return (int) (levels + Math.ceil(selectivity / (degree - 1)) - 1);
    }

    public static String clusteredBTReeNonUniqueToString(int levels, double selectivity, int degree) {
        return "CBPn = L + (⌈s / (⌈m / 2⌉ - 1⌉ - 1)\n" +
                "CBPn = " + levels + " + (⌈" + selectivity + " / (⌈" + degree + " / 2⌉ - 1⌉ - 1)\n" +
                "CBPn = " + clusteredBTreeNonUnique(levels, selectivity, degree);
    }

    public static int clusteredBTreeRange(int levels, int terminalLevelNodes) {
        return (int) (levels + (terminalLevelNodes / 2.0));
    }

    public static String clusteredBTreeRangeToString(int levels, int terminalLevelNodes) {
        return "CBPr = L + bL/2" +
                "CBPr = " + levels + " + " + terminalLevelNodes + "/2\n" +
                "CBPr = " + clusteredBTreeRange(levels, terminalLevelNodes);
    }

    public static int clusteredBTreePrintUnsorted(int levels, int terminalLevelNodes) {
        return levels + terminalLevelNodes;
    }

    public static String clusteredBTreePrintUnsortedToString(int levels, int terminalLevelNodes) {
        return "CBPpu = L + bL\n" +
                "CBPu = " + levels + " + " + terminalLevelNodes + "\n" +
                "CBPu = " + clusteredBTreePrintUnsorted(levels, terminalLevelNodes);
    }

    public static int clusteredBTreePrintSorted(int levels, int terminalLevelNodes) {
        return levels + terminalLevelNodes;
    }

    public static String clusteredBTreePrintSortedToString(int levels, int terminalLevelNodes) {
        return "CBPps = L + bL\n" +
                "CBPps = " + levels + " + " + terminalLevelNodes + "\n" +
                "CBPps = " + clusteredBTreePrintSorted(levels, terminalLevelNodes);
    }

    // hash table costs ------------------------------------------------------------------------------------------------

    public static int hashTableUnique() {
        return 1 + 1;
    }

    public static String hashTableUniqueToString() {
        return "CHu = 1 + 1";
    }

    public static int hashTableNonUnique(int selectivity) {
        return 1 + selectivity;
    }

    public static String hashTableNonUniqueToString(int selectivity) {
        return "CHn = 1 + " + selectivity + "\n" +
                "CHn = 1 + " + selectivity + "\n" +
                "CHn = " + hashTableNonUnique(selectivity);
    }

    // can't do ranged queries with hash tables!
    public static int hashTableRange() {
        return Integer.MIN_VALUE;
    }

    public static String hashTableRangeToString() {
        return "CHr = N/A";
    }

    public static int hashTablePrintUnsorted(int numBins, int numRecords) {
        return numBins + numRecords;
    }

    public static String hashTablePrintUnsortedToString(int numBins, int numRecords) {
        return "CHpu = #bins + r\n" +
                "CHpu = " + numBins + " + " + numRecords + "\n" +
                "CHpu = " + hashTablePrintUnsorted(numBins, numRecords);
    }

    // can't print sorted with hash tables!
    public static int hashTablePrintSorted() {
        return Integer.MIN_VALUE;
    }

    public static String hashTablePrintSortedToString() {
        return "CHps = N/A";
    }

    // clustered file costs --------------------------------------------------------------------------------------------

    public static int clusteredFileUnique(int table1Blocks, int table2Blocks) {
        return (int) log(table1Blocks + table2Blocks);
    }

    public static String clusteredFileUniqueToString(int table1Blocks, int table2Blocks) {
        return "CCu = log(bR + bS)\n" +
                "CCu = log(" + table1Blocks + " + " + table2Blocks + ")\n" +
                "CCu = " + clusteredFileUnique(table1Blocks, table2Blocks);
    }

    public static int clusteredFileNonUnique(int table1Blocks, int table2Blocks, double selectivity, int blockingFactor) {
        return (int) (log(table1Blocks + table2Blocks) + Math.ceil(selectivity / blockingFactor) - 1);
    }

    public static String clusteredFileNonUniqueToString(int table1Blocks, int table2Blocks, double selectivity, int blockingFactor) {
        return "CCn = log(bR + bS) + ⌈s / bf⌉ - 1\n" +
                "CCn = log(" + table1Blocks + " + " + table2Blocks + ") + ⌈" + selectivity + " / " + blockingFactor + "⌉ - 1\n" +
                "CCn = " + clusteredFileNonUnique(table1Blocks, table2Blocks, selectivity, blockingFactor);
    }

    public static int clusteredFileRange(int table1Blocks, int table2Blocks) {
        return (int) (log(table1Blocks + table2Blocks) + (table1Blocks + table2Blocks) / 2.0);
    }

    public static String clusteredFileRangeToString(int table1Blocks, int table2Blocks) {
        return "CCr = log(bR + bS) + (bR + bS) / 2\n" +
                "CCr = log(" + table1Blocks + " + " + table2Blocks + ") + (" + table1Blocks + " + " + table2Blocks + ") / 2\n" +
                "CCr = " + clusteredFileRange(table1Blocks, table2Blocks);
    }

    public static int clusteredFilePrintUnsorted(int table1Blocks, int table2Blocks) {
        return table1Blocks + table2Blocks;
    }

    public static String clusteredFilePrintUnsortedToString(int table1Blocks, int table2Blocks) {
        return "CCpu = bR + bS\n" +
                "CCpu = " + table1Blocks + " + " + table2Blocks + "\n" +
                "CCpu = " + clusteredFilePrintUnsorted(table1Blocks, table2Blocks);
    }

    public static int clusteredFilePrintSorted(int table1Blocks, int table2Blocks) {
        return table1Blocks + table2Blocks;
    }

    public static String clusteredFilePrintToString(int table1Blocks, int table2Blocks) {
        return "CCps = bR + bS\n" +
                "CCps = " + table1Blocks + " + " + table2Blocks + "\n" +
                "CCps = " + clusteredFilePrintUnsorted(table1Blocks, table2Blocks);
    }

    // join costs ------------------------------------------------------------------------------------------------------

    public static int nestedLoopJoin(int table1Blocks, int table2Blocks) {
        return table1Blocks < table2Blocks ? table1Blocks + (table1Blocks * table2Blocks) :
                table2Blocks + (table2Blocks * table1Blocks);
    }

    public static String nestedLoopJoinToString(int table1Blocks, int table2Blocks) {

        String tableOrdering = table1Blocks < table2Blocks ?
                table1Blocks + " + (" + table1Blocks + " * " + table2Blocks + ")" :
                table2Blocks + " + (" + table2Blocks + " * " + table1Blocks + ")";

        return "CJ1 = bR + bR * bS\n" +
                "CJ1 = " + tableOrdering + "\n" +
                "CJ1 = " + nestedLoopJoin(table1Blocks, table2Blocks);

    }

    public static int bTreeJoin(int table1Blocks, int table1NumRecords, int table2Levels, int table2Selectivity) {
        return table1Blocks + table1NumRecords * (table2Levels + table2Selectivity);
    }

    public static String bTreeJoinToString(int table1Blocks, int table1NumRecords, int table2Levels, int table2Selectivity) {
        return "CJ2 = R1.blocks + R1.numrecs * (R2.IndexedCol.levels + R2.IndexedCol.selectivity)\n" +
                "CJ2 = " + table1Blocks + " + " + table1NumRecords + " * (" + table2Levels + " + " + table2Selectivity + ")\n" +
                "CJ2 = " + bTreeJoin(table1Blocks, table1NumRecords, table2Levels, table2Selectivity);
    }

    public static int clusteredJoin(int table1Blocks, int table2Blocks) {
        return table1Blocks + table2Blocks;
    }

    public static String clusteredJoinToString(int table1Blocks, int table2Blocks) {
        return "CJ3 = bR + bS\n" +
                "CJ3 = " + table1Blocks + " + " + table2Blocks +
                "CJ3 = " + clusteredJoin(table1Blocks, table2Blocks);
    }

    // other calculations ----------------------------------------------------------------------------------------------

    public static double log(double value) {
        return Math.log(value) / Math.log(2);
    }
}