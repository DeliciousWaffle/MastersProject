package test;

import org.junit.jupiter.api.Test;
import utilities.Cost;
import static org.junit.jupiter.api.Assertions.*;

class CostTest {

    @Test
    public void unsortedPrintSorted() {
        int val = Cost.unsortedPrintSorted(300);
        assertEquals(2768, val);
    }

    @Test
    public void sortedNonUnique() {
        int blockingFactor = Cost.blockingFactor(100);
        double selectivity = Cost.selectivity(45, 7);
        int val = Cost.sortedNonUnique(300, selectivity, blockingFactor);
        assertEquals(9, val);
    }

    @Test
    public void degree() {
        int val = Cost.degree(11);
        assertEquals(27, val);
    }

    @Test
    public void levels() {
        int val = Cost.levels(200000, 27);
        assertEquals(5, val);
    }

    @Test
    public void terminalLevelNodes() {
        int val = Cost.terminalLevelNodes(200000, 27);
        assertEquals(15384, val);
    }
}