package ztest.utilities;

import datastructures.trees.querytree.QueryTree;
import org.junit.jupiter.api.Test;
import utilities.QueryCost;
import static org.junit.jupiter.api.Assertions.*;

class CostTest {

    @Test
    public void question1() {

        // produce first temp
        int recordSize = 63;
        int numRecords = 3954;
        int blockingFactor = QueryCost.blockingFactor(recordSize); // 16
        int blocks = QueryCost.blocks(numRecords, blockingFactor); // 248
        int CUr = QueryCost.unsortedRange(blocks);
        assertEquals(248, CUr);

        // product temp1 with btree on RentAmount
        numRecords = 3954;
        int keySize = 4;
        int degree = QueryCost.degree(keySize); // 86
        int levels = QueryCost.levels(numRecords, degree); // 3
        int terminalLevelNodes = QueryCost.terminalLevelNodes(numRecords, degree); // 94
        int CBr = QueryCost.secondaryBTreeRange(levels, terminalLevelNodes, numRecords); // 2027
        assertEquals(2027, CBr);
    }

    @Test
    public void question2() {

        // produce temp1 with btree on Owner.OwnerID
        int numRecs = 3954;
        int keySize = 4;
        int degree = QueryCost.degree(keySize);
        int levels = QueryCost.levels(numRecs, degree);
        int terminalLevelNodes = QueryCost.terminalLevelNodes(numRecs, degree);



        // product temp1 with btree on Site.OwnerID
    }
/*
    @Test
    public void question3() {

    }

    @Test
    public void question4() {

    }

    @Test
    public void question5() {

    }*/
}