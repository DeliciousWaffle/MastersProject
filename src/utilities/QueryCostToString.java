package utilities;

import datastructures.relation.table.component.Column;

import java.util.List;

public final class QueryCostToString {

    private QueryCostToString() {}

    // starting calculations -------------------------------------------------------------------------------------------

    public static String numberRecords(List<List<String>> data) {
        return "r = " + QueryCost.numberRecords(data);
    }

    public static String recordSize(List<Column> columns) {
        StringBuilder recordSize = new StringBuilder();
        if (columns.size() > 1) {
            recordSize.append("|r| = ");
            columns.forEach(column -> recordSize.append(column.size()).append(" + "));
            recordSize.delete(recordSize.length() - 3, recordSize.length()); // remove " + "
            recordSize.append("\n");
        }
        recordSize.append("|r| = ").append(QueryCost.recordSize(columns));
        return recordSize.toString();
    }

    public static String blockingFactor(int recordSize) {
        return "bf = ⌊BlockSize / |r|⌋\n" +
                "bf = ⌊" + QueryCost.BLOCK_SIZE + " / " + recordSize + "⌋\n" +
                "bf = " + QueryCost.blockingFactor(recordSize);
    }

    public static String blocks(int numRecords, int blockingFactor) {
        return "b = ⌈r / bf⌉\n" +
                "b = ⌈" + numRecords + " / " + blockingFactor + "⌉\n" +
                "b = " + QueryCost.blocks(numRecords, blockingFactor);
    }

    public static String distinctValues(List<String> columnData) {
        return "d = " + QueryCost.distinctValues(columnData);
    }

    public static String selectivity(int numRecords, int distinctValues) {
        return "s = r / d\n" +
                "s = " + numRecords + " / " + distinctValues + "\n" +
                "s = " + QueryCost.selectivity(numRecords, distinctValues);
    }

    // b-tree specific calculations ------------------------------------------------------------------------------------

    public static String degree(int keySize) {
        return "m = ⌊(BlockSize + 4 + KeySize) / (KeySize + 8)⌋\n" +
                "m = ⌊(" + QueryCost.BLOCK_SIZE + " + 4 + " + keySize + ") / (" + keySize + " + 8)⌋\n" +
                "m = " + QueryCost.degree(keySize);
    }

    public static String levels(int numRecords, int degree) {
        return "l = (log₂(r + (1 / 2)) / log₂(m / 2) + 1)\n" +
                "l = (log₂(" + numRecords + " + (1 / 2)) / log₂(" + degree + " / 2) + 1)\n" +
                "l = " + QueryCost.levels(numRecords, degree);
    }

    public static String terminalLevelNodes(int numRecords, int degree) {
        return "bl = ⌊r / (⌈m / 2⌉ - 1)⌋ \n" +
                "bl = ⌊" + numRecords + " / (⌈" + degree + " / 2⌉ - 1)⌋ \n" +
                "bl = " + QueryCost.terminalLevelNodes(numRecords, degree);
    }

    public static String foreignKeySelectivity(int tableWithPrimaryKeyNumRows, int tableWithForeignKeyNumRows) {
        return "fs = ⌈t2.r / t1.r⌉ \n" +
                "fs = ⌈" + tableWithForeignKeyNumRows + " / " + tableWithPrimaryKeyNumRows + "⌉ \n" +
                "fs = " + QueryCost.foreignKeySelectivity(tableWithPrimaryKeyNumRows, tableWithForeignKeyNumRows);
    }

    // unsorted file costs ---------------------------------------------------------------------------------------------

    public static String unsortedUnique(int blocks) {
        return "Cost Unsorted Unique = b\n" +
                "Cost Unsorted Unique = " + blocks;
    }

    public static String unsortedNonUnique(int blocks) {
        return "Cost Unsorted Non-Unique = b\n" +
                "Cost Unsorted Non-Unique = " + blocks;
    }

    public static String unsortedRange(int blocks) {
        return "Cost Unsorted Range = b\n" +
                "Cost Unsorted Range = " + blocks;
    }

    public static String unsortedPrintUnsorted(int blocks) {
        return "Cost Unsorted Print Unsorted = b\n" +
                "Cost Unsorted Print Unsorted = " + blocks;
    }

    public static String unsortedPrintSorted(int blocks) {
        return "Cost Unsorted Print Sorted = b * log₂(b) + b\n" +
                "Cost Unsorted Print Sorted  = " + blocks + " log₂(" + blocks + ") + " + blocks + "\n" +
                "Cost Unsorted Print Sorted  = " + QueryCost.unsortedPrintSorted(blocks);
    }

    // sorted file costs -----------------------------------------------------------------------------------------------

    public static String sortedUnique(int blocks) {
        return "Cost Sorted Unique = log₂(b)\n" +
                "Cost Sorted Unique = " + "log₂(" + blocks + ")\n" +
                "Cost Sorted Unique = " + QueryCost.sortedUnique(blocks);
    }

    public static String sortedNonUnique(int blocks, double selectivity, int blockingFactor) {
        return "Cost Sorted Non-Unique = log₂(b) + ⌈s / bf⌉ - 1\n" +
                "Cost Sorted Non-Unique = log₂(" + blocks + ") + ⌈" + selectivity + "/" + blockingFactor + "⌉ - 1\n" +
                "Cost Sorted Non-Unique = " + QueryCost.sortedNonUnique(blocks, selectivity, blockingFactor);
    }

    public static String sortedRange(int blocks) {
        return "Cost Sorted Range = log₂(b) + b/2\n" +
                "Cost Sorted Range = log₂(" + blocks + ") + " + blocks + "/2\n" +
                "Cost Sorted Range = " + QueryCost.sortedRange(blocks);
    }

    public static String sortedPrintUnsorted(int blocks) {
        return "Cost Sorted Print Unsorted = b\n" +
                "Cost Sorted Print Unsorted = " + blocks;
    }

    public static String sortedPrintSorted(int blocks) {
        return "Cost Sorted Print Sorted = b\n" +
                "Cost Sorted Print Sorted = " + blocks;
    }

    // secondary b-tree costs ------------------------------------------------------------------------------------------

    public static String secondaryBTreeUnique(int levels) {
        return "Cost Secondary B-Tree Unique = L + 1\n" +
                "Cost Secondary B-Tree Unique = " + levels + " + 1\n" +
                "Cost Secondary B-Tree Unique = " + QueryCost.secondaryBTreeUnique(levels);
    }

    public static String secondaryBTreeNonUnique(int levels, int degree, double selectivity) {
        return "Cost Secondary B-Tree Non-Unique = L + (⌈s / (⌈m / 2⌉ - 1)⌉ - 1) + s\n" +
                "Cost Secondary B-Tree Non-Unique = " + levels + " + (⌈" + selectivity + " / (⌈" + degree +
                " / 2⌉ - 1)⌉ - 1) + " + selectivity + "\n" +
                "Cost Secondary B-Tree Non-Unique = " + QueryCost.secondaryBTreeNonUnique(levels, degree, selectivity);
    }

    public static String secondaryBTreeRange(int levels, int terminalLevelNodes, int numRecords) {
        return "Cost Secondary B-Tree Range = L + bL/2 + r/2\n" +
                "Cost Secondary B-Tree Range = " + levels + " + " + terminalLevelNodes + "/2 + " + numRecords + "/2\n" +
                "Cost Secondary B-Tree Range = " +
                QueryCost.secondaryBTreeRange(levels, terminalLevelNodes, numRecords);
    }

    public static String secondaryBTreePrintUnsorted(int levels, int terminalLevelNodes, int numRecords) {
        return "Cost Secondary B-Tree Print Unsorted = L + bL + r\n" +
                "Cost Secondary B-Tree Print Unsorted = " + levels + " + " + terminalLevelNodes + " + " + numRecords +
                "\n" +
                "Cost Secondary B-Tree Print Unsorted = " +
                QueryCost.secondaryBTreePrintUnsorted(levels, terminalLevelNodes, numRecords);
    }

    public static String secondaryBTreePrintSorted(int levels, int terminalLevelNodes, int numRecords) {
        return "Cost Secondary B-Tree Print Sorted = L + bL + r\n" +
                "Cost Secondary B-Tree Print Sorted = " + levels + " + " + terminalLevelNodes + " + " + numRecords +
                "\n" +
                "Cost Secondary B-Tree Print Sorted = " +
                secondaryBTreePrintUnsorted(levels, terminalLevelNodes, numRecords);
    }

    // clustered b-tree costs ------------------------------------------------------------------------------------------

    public static String clusteredBTreeUnique(int levels) {
        return "Cost Clustered B-Tree Unique = L\n" +
                "Cost Clustered B-Tree Unique = " + levels;
    }

    public static String clusteredBTreeNonUnique(int levels, double selectivity, int degree) {
        return "Cost Clustered B-Tree Non-Unique = L + (⌈s / (⌈m / 2⌉ - 1⌉ - 1)\n" +
                "Cost Clustered B-Tree Non-Unique = " + levels + " + (⌈" + selectivity + " / (⌈" + degree +
                " / 2⌉ - 1⌉ - 1)\n" +
                "Cost Clustered B-Tree Non-Unique = " + QueryCost.clusteredBTreeNonUnique(levels, selectivity, degree);
    }

    public static String clusteredBTreeRange(int levels, int terminalLevelNodes) {
        return "Cost Clustered B-Tree Range = L + bL/2" +
                "Cost Clustered B-Tree Range = " + levels + " + " + terminalLevelNodes + "/2\n" +
                "Cost Clustered B-Tree Range = " + QueryCost.clusteredBTreeRange(levels, terminalLevelNodes);
    }

    public static String clusteredBTreePrintUnsorted(int levels, int terminalLevelNodes) {
        return "Cost Clustered B-Tree Print Unsorted = L + bL\n" +
                "Cost Clustered B-Tree Print Unsorted = " + levels + " + " + terminalLevelNodes + "\n" +
                "Cost Clustered B-Tree Print Unsorted = " +
                QueryCost.clusteredBTreePrintUnsorted(levels, terminalLevelNodes);
    }

    public static String clusteredBTreePrintSorted(int levels, int terminalLevelNodes) {
        return "Cost Clustered B-Tree Print Sorted = L + bL\n" +
                "Cost Clustered B-Tree Print Sorted = " + levels + " + " + terminalLevelNodes + "\n" +
                "Cost Clustered B-Tree Print Sorted = " +
                QueryCost.clusteredBTreePrintSorted(levels, terminalLevelNodes);
    }

    // hash table costs ------------------------------------------------------------------------------------------------

    public static String hashTableUnique() {
        return "Cost Hash Table Unique = 1 + 1";
    }

    public static String hashTableNonUnique(int selectivity) {
        return "Cost Hash Table Non-Unique = 1 + s\n" +
                "Cost Hash Table Non-Unique = 1 + " + selectivity + "\n" +
                "Cost Hash Table Non-Unique = " + QueryCost.hashTableNonUnique(selectivity);
    }

    public static String hashTableRange() {
        return "Cost Hash Table Range = N/A";
    }

    public static String hashTablePrintUnsorted(int numBins, int numRecords) {
        return "Cost Hash Table Print Unsorted = #bins + r\n" +
                "Cost Hash Table Print Unsorted = " + numBins + " + " + numRecords + "\n" +
                "Cost Hash Table Print Unsorted = " + QueryCost.hashTablePrintUnsorted(numBins, numRecords);
    }

    public static String hashTablePrintSorted() {
        return "Cost Hash Table Print Sorted  = N/A";
    }

    // clustered file costs --------------------------------------------------------------------------------------------

    public static String clusteredFileUnique(int table1Blocks, int table2Blocks) {
        return "Cost Clustered File Unique = log₂(bR + bS)\n" +
                "Cost Clustered File Unique = log₂(" + table1Blocks + " + " + table2Blocks + ")\n" +
                "Cost Clustered File Unique = " + QueryCost.clusteredFileUnique(table1Blocks, table2Blocks);
    }

    public static String clusteredFileNonUnique(int table1Blocks, int table2Blocks, double selectivity,
                                                int blockingFactor) {
        return "Cost Clustered File Non-Unique = log₂(bR + bS) + ⌈s / bf⌉ - 1\n" +
                "Cost Clustered File Non-Unique = log₂(" + table1Blocks + " + " + table2Blocks + ") + ⌈" + selectivity +
                " / " + blockingFactor + "⌉ - 1\n" +
                "Cost Clustered File Non-Unique = " +
                QueryCost.clusteredFileNonUnique(table1Blocks, table2Blocks, selectivity, blockingFactor);
    }

    public static String clusteredFileRange(int table1Blocks, int table2Blocks) {
        return "Cost Clustered File Range = log₂(bR + bS) + (bR + bS) / 2\n" +
                "Cost Clustered File Range = log₂(" + table1Blocks + " + " + table2Blocks + ") + (" + table1Blocks +
                " + " + table2Blocks + ") / 2\n" +
                "Cost Clustered File Range = " + QueryCost.clusteredFileRange(table1Blocks, table2Blocks);
    }

    public static String clusteredFilePrintUnsorted(int table1Blocks, int table2Blocks) {
        return "Cost Clustered File Print Unsorted = bR + bS\n" +
                "Cost Clustered File Print Unsorted = " + table1Blocks + " + " + table2Blocks + "\n" +
                "Cost Clustered File Print Unsorted = " +
                QueryCost.clusteredFilePrintUnsorted(table1Blocks, table2Blocks);
    }

    public static String clusteredFilePrintSorted(int table1Blocks, int table2Blocks) {
        return "Cost Clustered File Print Sorted = bR + bS\n" +
                "Cost Clustered File Print Sorted = " + table1Blocks + " + " + table2Blocks + "\n" +
                "Cost Clustered File Print Sorted = " +
                QueryCost.clusteredFilePrintUnsorted(table1Blocks, table2Blocks);
    }

    // join costs ------------------------------------------------------------------------------------------------------

    public static String nestedLoopJoin(int table1Blocks, int table2Blocks) {
        String tableOrdering = table1Blocks < table2Blocks ?
                table1Blocks + " + (" + table1Blocks + " * " + table2Blocks + ")" :
                table2Blocks + " + (" + table2Blocks + " * " + table1Blocks + ")";
        return "Nested Loop Join = bR + bR * bS\n" +
                "Nested Loop Join = " + tableOrdering + "\n" +
                "Nested Loop Join = " + QueryCost.nestedLoopJoin(table1Blocks, table2Blocks);
    }

    public static String bTreeJoin(int secondTableBlocks, int secondTableNumRecs, int firstTableColLevels,
                                   int firstTableColSelectivity) {
        return "B-Tree Join = T2.b + T2.r * (T1.B-TreeColumn.l + T1.B-TreeColumn.s)\n" +
                "B-Tree Join = " + secondTableBlocks + " + " + secondTableNumRecs + " * (" + firstTableColLevels +
                " + " + firstTableColSelectivity + ")\n" +
                "B-Tree Join = " + QueryCost.bTreeJoin(secondTableBlocks, secondTableNumRecs, firstTableColLevels,
                firstTableColSelectivity);
    }

    public static String clusteredFileJoin(int table1Blocks, int table2Blocks) {
        return "Clustered File Join = bR + bS\n" +
                "Clustered File Join = " + table1Blocks + " + " + table2Blocks + "\n" +
                "Clustered File Join = " + QueryCost.clusteredJoin(table1Blocks, table2Blocks);
    }
}