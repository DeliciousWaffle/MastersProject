package ztest.systemcatalog.component;

import datastructure.rulegraph.RuleGraph;
import datastructure.rulegraph.type.RuleGraphTypes;
import datastructure.tree.querytree.QueryTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptimizerTest {

    private static Optimizer optimizer;
    private static RuleGraph queryRuleGraph;


    @BeforeAll
    public static void create() {
        optimizer = new Optimizer();
        queryRuleGraph = new RuleGraphTypes().getQueryRuleGraph();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT COL1 FROM TAB1",
            "SELECT COL1, COL2, COL3 FROM TAB1",
            "SELECT COL1 FROM TAB1 WHERE COL1 = A",
            "SELECT COL1 FROM TAB1 WHERE COL1 = A AND COL2 = B",
            "SELECT COL1 FROM TAB1 JOIN TAB2 USING(COL1)",
            "SELECT COL1 FROM TAB1 JOIN TAB2 USING(COL1), TAB3",
            "SELECT COL1 FROM TAB1, TAB2 JOIN TAB3 USING(COL1)",
            "SELECT COL1 FROM TAB1 JOIN TAB2 USING(COL1), TAB3 JOIN TAB4 USING(COL2)",
            "SELECT COL1 FROM TAB1 JOIN TAB2 USING(COL1), TAB3 WHERE COL1 = A",
            "SELECT COL1, MIN(COL2) FROM TAB1 GROUP BY COL1",
            "SELECT MIN(COL1) FROM TAB1;",
            "SELECT COL1, COL2, MIN(COL3), MAX(COL4) FROM TAB1 GROUP BY COL1, COL2",
            "SELECT COL1, COL2, MIN(COL3), MAX(COL4) FROM TAB1 GROUP BY COL1, COL2 HAVING COUNT(COL1) > 4",
            /*"SELECT COL1, COL2, MIN(COL3), MAX(COL4) FROM TAB1, TAB2, TAB3 JOIN TAB4 USING(COL1) WHERE COL1 = A GROUP BY COL1, COL2 HAVING COUNT(COL1) > 5"
    */})
    public void testCreation(String input) {
        String[] inputTokens = new Parser().formatAndTokenizeInput(input);
        System.out.println(input);
        QueryTree queryTree = optimizer.createQueryTree(queryRuleGraph, inputTokens);
        //System.out.println(queryTree.getStructure());
        System.out.println("----------------------------------------");
        assertTrue(true);
    }
}