package utilities;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;

import java.util.List;

import static utilities.QueryCost.*;

public final class QueryCostToString {

    private QueryCostToString() {}

    // starting calculations -------------------------------------------------------------------------------------------

    public static String numberRecordsToString(Table table) {
        return "r = " + numberRecords(table);
    }

    public static String recordSizeToString(List<Column> columns) {
        return "|r| = " + recordSize(columns);
    }

    public static String blockingFactorToString(int recordSize) {
        return "bf = ⌊BlockSize / |r|⌋\n" +
                "bf = ⌊" + BLOCK_SIZE + " / |" + recordSize + "|⌋\n" +
                "bf = " + blockingFactor(recordSize);
    }

    public static String blocksToString(int numRecords, int blockingFactor) {
        return "b = ⌈r / bf⌉\n" +
                "b = ⌈" + numRecords + " / " + blockingFactor + " ⌉\n" +
                "b = " + blocks(numRecords, blockingFactor);
    }

    public static String selectivityToString(int numRecords, int distinctValues) {
        return "s = r/d\n" +
                "s = " + numRecords + "/" + distinctValues + "\n" +
                "s = " + selectivity(numRecords, distinctValues);
    }

    // b-tree specific calculations ------------------------------------------------------------------------------------

    public static String degreeToString(int keySize) {
        return "m = ⌊(BlockSize + 4 + KeySize) / (KeySize + 8)⌋\n" +
                "m = ⌊(" + BLOCK_SIZE + " + 4 + " + keySize + ") / (" + keySize + " + 8)⌋\n" +
                "m = " + degree(keySize);
    }

    public static String levelsToString(int numRecords, int degree) {
        return "l = (log(r + (1 / 2)) / log(m / 2) + 1)\n" +
                "l = (log(" + numRecords + " + (1 / 2)) / log(" + degree + " / 2) + 1)\n" +
                "l = " + levels(numRecords, degree);
    }

    public static String terminalLevelNodesToString(int numRecords, int degree) {
        return "bl = ⌊r / (⌈m / 2⌉ - 1)⌋ \n" +
                "bl = ⌊" + numRecords + " / (⌈" + degree + " / 2⌉ - 1)⌋ \n" +
                "bl = " + terminalLevelNodes(numRecords, degree);
    }

    public static String foreignKeySelectivityToString(int tableWithPrimaryKeyNumRows, int tableWithForeignKeyNumRows) {
        return "fs = ⌈t2.r / t1.r⌉ \n" +
                "fs = ⌈" + tableWithForeignKeyNumRows + " / " + tableWithPrimaryKeyNumRows + "⌉ \n" +
                "fs = " + foreignKeySelectivity(tableWithPrimaryKeyNumRows, tableWithForeignKeyNumRows);
    }

    // unsorted file costs ---------------------------------------------------------------------------------------------

    public static String unsortedUniqueToString(int blocks) {
        return "CUu = b\n" +
                "CUu = " + blocks;
    }

    public static String unsortedNonUniqueToString(int blocks) {
        return "CUn = b\n" +
                "CUn = " + blocks;
    }

    public static String unsortedRangeToString(int blocks) {
        return "CUr = b\n" +
                "CUr = " + blocks;
    }

    public static String unsortedPrintUnsortedToString(int blocks) {
        return "CUpu = b\n" +
                "CUpu = " + blocks;
    }

    public static String unsortedPrintSortedToString(int blocks) {
        return "CUps = b * log(b) + b\n" +
                "CUps = " + blocks + " log(" + blocks + ") + " + blocks + "\n" +
                "CUps = " + unsortedPrintSorted(blocks);
    }

    // sorted file costs -----------------------------------------------------------------------------------------------

    public static String sortedUniqueToString(int blocks) {
        return "CSu = log(b)\n" +
                "CSu = " + "log(" + blocks + ")\n" +
                "CSu = " + sortedUnique(blocks);
    }

    public static String sortedNonUniqueToString(int blocks, double selectivity, int blockingFactor) {
        return "CSn = log(b) + ⌈s / bf⌉ - 1\n" +
                "CSn = log(" + blocks + ") + ⌈" + selectivity + "/" + blockingFactor + "⌉ - 1\n" +
                "CSn = " + sortedNonUnique(blocks, selectivity, blockingFactor);
    }

    public static String sortedRangeToString(int blocks) {
        return "CSr = log(b) + b/2\n" +
                "CSr = log(" + blocks + ") + " + blocks + "/2\n" +
                "CSr = " + sortedRange(blocks);
    }

    public static String sortedPrintUnsortedToString(int blocks) {
        return "CSpu = b\n" +
                "CSpu = " + blocks;
    }

    public static String sortedPrintSortedToString(int blocks) {
        return "CSps = b\n" +
                "CSps = " + blocks;
    }

    // secondary b-tree costs ------------------------------------------------------------------------------------------

    public static String secondaryBTreeUniqueToString(int levels) {
        return "CBu = L + 1\n" +
                "CBu = " + levels + " + 1\n" +
                "CBu = " + secondaryBTreeUnique(levels);
    }

    public static String secondaryBTreeNonUniqueToString(int levels, int degree, double selectivity) {
        return "CBn = L + (⌈s / (⌈m / 2⌉ - 1)⌉ - 1) + s\n" +
                "CBn = " + levels + " + (⌈" + selectivity + " / (⌈" + degree + " / 2⌉ - 1)⌉ - 1) + " + selectivity +"\n" +
                "CBn = " + secondaryBTreeNonUnique(levels, degree, selectivity);
    }

    public static String secondaryBTreeRangeToString(int levels, int terminalLevelNodes, int numRecords) {
        return "CBr = L + bL/2 + r/2\n" +
                "CBr = " + levels + " + " + terminalLevelNodes + "/2 + " + numRecords + "/2\n" +
                "CBr = " + secondaryBTreeRange(levels, terminalLevelNodes, numRecords);
    }

    public static String secondaryBTreePrintUnsortedToString(int levels, int terminalLevelNodes, int numRecords) {
        return "CBpu = L + bL + r\n" +
                "CBpu = " + levels + " + " + terminalLevelNodes + " + " + numRecords + "\n" +
                "CBpu = " + secondaryBTreePrintUnsorted(levels, terminalLevelNodes, numRecords);
    }

    public static String secondaryBTreePrintSortedToString(int levels, int terminalLevelNodes, int numRecords) {
        return "CBps = L + bL + r\n" +
                "CBps = " + levels + " + " + terminalLevelNodes + " + " + numRecords + "\n" +
                "CBps = " + secondaryBTreePrintUnsorted(levels, terminalLevelNodes, numRecords);
    }

    // clustered b-tree costs ------------------------------------------------------------------------------------------

    public static String clusteredBTreeUniqueToString(int levels) {
        return "CBPu = L\n" +
                "CBPu = " + levels;
    }

    public static String clusteredBTReeNonUniqueToString(int levels, double selectivity, int degree) {
        return "CBPn = L + (⌈s / (⌈m / 2⌉ - 1⌉ - 1)\n" +
                "CBPn = " + levels + " + (⌈" + selectivity + " / (⌈" + degree + " / 2⌉ - 1⌉ - 1)\n" +
                "CBPn = " + clusteredBTreeNonUnique(levels, selectivity, degree);
    }

    public static String clusteredBTreeRangeToString(int levels, int terminalLevelNodes) {
        return "CBPr = L + bL/2" +
                "CBPr = " + levels + " + " + terminalLevelNodes + "/2\n" +
                "CBPr = " + clusteredBTreeRange(levels, terminalLevelNodes);
    }

    public static String clusteredBTreePrintUnsortedToString(int levels, int terminalLevelNodes) {
        return "CBPpu = L + bL\n" +
                "CBPu = " + levels + " + " + terminalLevelNodes + "\n" +
                "CBPu = " + clusteredBTreePrintUnsorted(levels, terminalLevelNodes);
    }

    public static String clusteredBTreePrintSortedToString(int levels, int terminalLevelNodes) {
        return "CBPps = L + bL\n" +
                "CBPps = " + levels + " + " + terminalLevelNodes + "\n" +
                "CBPps = " + clusteredBTreePrintSorted(levels, terminalLevelNodes);
    }

    // hash table costs ------------------------------------------------------------------------------------------------

    public static String hashTableUniqueToString() {
        return "CHu = 1 + 1";
    }

    public static String hashTableNonUniqueToString(int selectivity) {
        return "CHn = 1 + " + selectivity + "\n" +
                "CHn = 1 + " + selectivity + "\n" +
                "CHn = " + hashTableNonUnique(selectivity);
    }

    public static String hashTableRangeToString() {
        return "CHr = N/A";
    }

    public static String hashTablePrintUnsortedToString(int numBins, int numRecords) {
        return "CHpu = #bins + r\n" +
                "CHpu = " + numBins + " + " + numRecords + "\n" +
                "CHpu = " + hashTablePrintUnsorted(numBins, numRecords);
    }

    public static String hashTablePrintSortedToString() {
        return "CHps = N/A";
    }

    // clustered file costs --------------------------------------------------------------------------------------------

    public static String clusteredFileUniqueToString(int table1Blocks, int table2Blocks) {
        return "CCu = log(bR + bS)\n" +
                "CCu = log(" + table1Blocks + " + " + table2Blocks + ")\n" +
                "CCu = " + clusteredFileUnique(table1Blocks, table2Blocks);
    }

    public static String clusteredFileNonUniqueToString(int table1Blocks, int table2Blocks, double selectivity, int blockingFactor) {
        return "CCn = log(bR + bS) + ⌈s / bf⌉ - 1\n" +
                "CCn = log(" + table1Blocks + " + " + table2Blocks + ") + ⌈" + selectivity + " / " + blockingFactor + "⌉ - 1\n" +
                "CCn = " + clusteredFileNonUnique(table1Blocks, table2Blocks, selectivity, blockingFactor);
    }

    public static String clusteredFileRangeToString(int table1Blocks, int table2Blocks) {
        return "CCr = log(bR + bS) + (bR + bS) / 2\n" +
                "CCr = log(" + table1Blocks + " + " + table2Blocks + ") + (" + table1Blocks + " + " + table2Blocks + ") / 2\n" +
                "CCr = " + clusteredFileRange(table1Blocks, table2Blocks);
    }

    public static String clusteredFilePrintUnsortedToString(int table1Blocks, int table2Blocks) {
        return "CCpu = bR + bS\n" +
                "CCpu = " + table1Blocks + " + " + table2Blocks + "\n" +
                "CCpu = " + clusteredFilePrintUnsorted(table1Blocks, table2Blocks);
    }

    public static String clusteredFilePrintToString(int table1Blocks, int table2Blocks) {
        return "CCps = bR + bS\n" +
                "CCps = " + table1Blocks + " + " + table2Blocks + "\n" +
                "CCps = " + clusteredFilePrintUnsorted(table1Blocks, table2Blocks);
    }

    // join costs ------------------------------------------------------------------------------------------------------

    public static String nestedLoopJoinToString(int table1Blocks, int table2Blocks) {
        String tableOrdering = table1Blocks < table2Blocks ?
                table1Blocks + " + (" + table1Blocks + " * " + table2Blocks + ")" :
                table2Blocks + " + (" + table2Blocks + " * " + table1Blocks + ")";
        return "CJ1 = bR + bR * bS\n" +
                "CJ1 = " + tableOrdering + "\n" +
                "CJ1 = " + nestedLoopJoin(table1Blocks, table2Blocks);
    }

    public static String bTreeJoinToString(int table2Blocks, int table2NumRecs, int table1IndColLevels, int table2IndColSelectivity) {
        return "CJ2 = T2.b + T2.r * (T1.IndCol.l + T1.Ind.s)\n" +
                "CJ2 = " + table2Blocks + " + " + table2NumRecs + " * (" + table1IndColLevels + " + " + table2IndColSelectivity + ")\n" +
                "CJ2 = " + bTreeJoin(table2Blocks, table2NumRecs, table1IndColLevels, table2IndColSelectivity);
    }

    public static String clusteredJoinToString(int table1Blocks, int table2Blocks) {
        return "CJ3 = bR + bS\n" +
                "CJ3 = " + table1Blocks + " + " + table2Blocks +
                "CJ3 = " + clusteredJoin(table1Blocks, table2Blocks);
    }
}