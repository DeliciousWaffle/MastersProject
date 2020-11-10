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
                "bf = ⌊" + QueryCost.BLOCK_SIZE + " / |" + recordSize + "|⌋\n" +
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
        return "s = r/d\n" +
                "s = " + numRecords + "/" + distinctValues + "\n" +
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
        return "CUu = b\n" +
                "CUu = " + blocks;
    }

    public static String unsortedNonUnique(int blocks) {
        return "CUn = b\n" +
                "CUn = " + blocks;
    }

    public static String unsortedRange(int blocks) {
        return "CUr = b\n" +
                "CUr = " + blocks;
    }

    public static String unsortedPrintUnsorted(int blocks) {
        return "CUpu = b\n" +
                "CUpu = " + blocks;
    }

    public static String unsortedPrintSorted(int blocks) {
        return "CUps = b * log₂(b) + b\n" +
                "CUps = " + blocks + " log₂(" + blocks + ") + " + blocks + "\n" +
                "CUps = " + QueryCost.unsortedPrintSorted(blocks);
    }

    // sorted file costs -----------------------------------------------------------------------------------------------

    public static String sortedUnique(int blocks) {
        return "CSu = log₂(b)\n" +
                "CSu = " + "log₂(" + blocks + ")\n" +
                "CSu = " + QueryCost.sortedUnique(blocks);
    }

    public static String sortedNonUnique(int blocks, double selectivity, int blockingFactor) {
        return "CSn = log₂(b) + ⌈s / bf⌉ - 1\n" +
                "CSn = log₂(" + blocks + ") + ⌈" + selectivity + "/" + blockingFactor + "⌉ - 1\n" +
                "CSn = " + QueryCost.sortedNonUnique(blocks, selectivity, blockingFactor);
    }

    public static String sortedRange(int blocks) {
        return "CSr = log₂(b) + b/2\n" +
                "CSr = log₂(" + blocks + ") + " + blocks + "/2\n" +
                "CSr = " + QueryCost.sortedRange(blocks);
    }

    public static String sortedPrintUnsorted(int blocks) {
        return "CSpu = b\n" +
                "CSpu = " + blocks;
    }

    public static String sortedPrintSorted(int blocks) {
        return "CSps = b\n" +
                "CSps = " + blocks;
    }

    // secondary b-tree costs ------------------------------------------------------------------------------------------

    public static String secondaryBTreeUnique(int levels) {
        return "CBu = L + 1\n" +
                "CBu = " + levels + " + 1\n" +
                "CBu = " + QueryCost.secondaryBTreeUnique(levels);
    }

    public static String secondaryBTreeNonUnique(int levels, int degree, double selectivity) {
        return "CBn = L + (⌈s / (⌈m / 2⌉ - 1)⌉ - 1) + s\n" +
                "CBn = " + levels + " + (⌈" + selectivity + " / (⌈" + degree + " / 2⌉ - 1)⌉ - 1) + " + selectivity
                +"\n" +
                "CBn = " + QueryCost.secondaryBTreeNonUnique(levels, degree, selectivity);
    }

    public static String secondaryBTreeRange(int levels, int terminalLevelNodes, int numRecords) {
        return "CBr = L + bL/2 + r/2\n" +
                "CBr = " + levels + " + " + terminalLevelNodes + "/2 + " + numRecords + "/2\n" +
                "CBr = " + QueryCost.secondaryBTreeRange(levels, terminalLevelNodes, numRecords);
    }

    public static String secondaryBTreePrintUnsorted(int levels, int terminalLevelNodes, int numRecords) {
        return "CBpu = L + bL + r\n" +
                "CBpu = " + levels + " + " + terminalLevelNodes + " + " + numRecords + "\n" +
                "CBpu = " + QueryCost.secondaryBTreePrintUnsorted(levels, terminalLevelNodes, numRecords);
    }

    public static String secondaryBTreePrintSorted(int levels, int terminalLevelNodes, int numRecords) {
        return "CBps = L + bL + r\n" +
                "CBps = " + levels + " + " + terminalLevelNodes + " + " + numRecords + "\n" +
                "CBps = " + secondaryBTreePrintUnsorted(levels, terminalLevelNodes, numRecords);
    }

    // clustered b-tree costs ------------------------------------------------------------------------------------------

    public static String clusteredBTreeUnique(int levels) {
        return "CBPu = L\n" +
                "CBPu = " + levels;
    }

    public static String clusteredBTReeNonUnique(int levels, double selectivity, int degree) {
        return "CBPn = L + (⌈s / (⌈m / 2⌉ - 1⌉ - 1)\n" +
                "CBPn = " + levels + " + (⌈" + selectivity + " / (⌈" + degree + " / 2⌉ - 1⌉ - 1)\n" +
                "CBPn = " + QueryCost.clusteredBTreeNonUnique(levels, selectivity, degree);
    }

    public static String clusteredBTreeRange(int levels, int terminalLevelNodes) {
        return "CBPr = L + bL/2" +
                "CBPr = " + levels + " + " + terminalLevelNodes + "/2\n" +
                "CBPr = " + QueryCost.clusteredBTreeRange(levels, terminalLevelNodes);
    }

    public static String clusteredBTreePrintUnsorted(int levels, int terminalLevelNodes) {
        return "CBPpu = L + bL\n" +
                "CBPu = " + levels + " + " + terminalLevelNodes + "\n" +
                "CBPu = " + QueryCost.clusteredBTreePrintUnsorted(levels, terminalLevelNodes);
    }

    public static String clusteredBTreePrintSorted(int levels, int terminalLevelNodes) {
        return "CBPps = L + bL\n" +
                "CBPps = " + levels + " + " + terminalLevelNodes + "\n" +
                "CBPps = " + QueryCost.clusteredBTreePrintSorted(levels, terminalLevelNodes);
    }

    // hash table costs ------------------------------------------------------------------------------------------------

    public static String hashTableUnique() {
        return "CHu = 1 + 1";
    }

    public static String hashTableNonUnique(int selectivity) {
        return "CHn = 1 + " + selectivity + "\n" +
                "CHn = 1 + " + selectivity + "\n" +
                "CHn = " + QueryCost.hashTableNonUnique(selectivity);
    }

    public static String hashTableRange() {
        return "CHr = N/A";
    }

    public static String hashTablePrintUnsorted(int numBins, int numRecords) {
        return "CHpu = #bins + r\n" +
                "CHpu = " + numBins + " + " + numRecords + "\n" +
                "CHpu = " + QueryCost.hashTablePrintUnsorted(numBins, numRecords);
    }

    public static String hashTablePrintSorted() {
        return "CHps = N/A";
    }

    // clustered file costs --------------------------------------------------------------------------------------------

    public static String clusteredFileUnique(int table1Blocks, int table2Blocks) {
        return "CCu = log₂(bR + bS)\n" +
                "CCu = log₂(" + table1Blocks + " + " + table2Blocks + ")\n" +
                "CCu = " + QueryCost.clusteredFileUnique(table1Blocks, table2Blocks);
    }

    public static String clusteredFileNonUnique(int table1Blocks, int table2Blocks, double selectivity,
                                                int blockingFactor) {
        return "CCn = log₂(bR + bS) + ⌈s / bf⌉ - 1\n" +
                "CCn = log₂(" + table1Blocks + " + " + table2Blocks + ") + ⌈" + selectivity + " / " + blockingFactor +
                "⌉ - 1\n" +
                "CCn = " + QueryCost.clusteredFileNonUnique(table1Blocks, table2Blocks, selectivity, blockingFactor);
    }

    public static String clusteredFileRange(int table1Blocks, int table2Blocks) {
        return "CCr = log₂(bR + bS) + (bR + bS) / 2\n" +
                "CCr = log₂(" + table1Blocks + " + " + table2Blocks + ") + (" + table1Blocks + " + " + table2Blocks +
                ") / 2\n" +
                "CCr = " + QueryCost.clusteredFileRange(table1Blocks, table2Blocks);
    }

    public static String clusteredFilePrintUnsorted(int table1Blocks, int table2Blocks) {
        return "CCpu = bR + bS\n" +
                "CCpu = " + table1Blocks + " + " + table2Blocks + "\n" +
                "CCpu = " + QueryCost.clusteredFilePrintUnsorted(table1Blocks, table2Blocks);
    }

    public static String clusteredFilePrint(int table1Blocks, int table2Blocks) {
        return "CCps = bR + bS\n" +
                "CCps = " + table1Blocks + " + " + table2Blocks + "\n" +
                "CCps = " + QueryCost.clusteredFilePrintUnsorted(table1Blocks, table2Blocks);
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

    public static String bTreeJoin(int table2Blocks, int table2NumRecs, int table1IndColLevels,
                                   int table2IndColSelectivity) {
        return "B-Tree Join = T2.b + T2.r * (T1.IndCol.l + T1.Ind.s)\n" +
                "B-Tree Join = " + table2Blocks + " + " + table2NumRecs + " * (" + table1IndColLevels + " + " +
                table2IndColSelectivity + ")\n" +
                "B-Tree Join = " + QueryCost.bTreeJoin(table2Blocks, table2NumRecs, table1IndColLevels,
                table2IndColSelectivity);
    }

    public static String clusteredJoin(int table1Blocks, int table2Blocks) {
        return "Clustered File Join = bR + bS\n" +
                "Clustered File Join = " + table1Blocks + " + " + table2Blocks +
                "Clustered File Join = " + QueryCost.clusteredJoin(table1Blocks, table2Blocks);
    }
}