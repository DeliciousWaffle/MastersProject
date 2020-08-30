package ztest.utilities;

import org.junit.jupiter.api.Test;
import utilities.QueryCost;
import static org.junit.jupiter.api.Assertions.*;

class CostTest {

    @Test
    public void unsortedPrintSorted() {
        int val = QueryCost.unsortedPrintSorted(300);
        assertEquals(2768, val);
    }

    @Test
    public void sortedNonUnique() {
        int blockingFactor = QueryCost.blockingFactor(100);
        double selectivity = QueryCost.selectivity(45, 7);
        int val = QueryCost.sortedNonUnique(300, selectivity, blockingFactor);
        assertEquals(9, val);
    }

    @Test
    public void degree() {
        int val = QueryCost.degree(11);
        assertEquals(27, val);
    }

    @Test
    public void levels() {
        int val = QueryCost.levels(200000, 27);
        assertEquals(5, val);
    }

    @Test
    public void terminalLevelNodes() {
        int val = QueryCost.terminalLevelNodes(200000, 27);
        assertEquals(15384, val);
    }
}