package ztest.systemcatalog.component;

import datastructure.relation.table.Table;
import datastructure.relation.table.component.Column;
import datastructure.relation.table.component.DataType;
import datastructure.rulegraph.RuleGraph;
import datastructure.rulegraph.type.RuleGraphTypes;
import datastructure.tree.querytree.QueryTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptimizerTest {

    private static Optimizer optimizer;
    private static RuleGraph queryRuleGraph;
    private static List<Table> tables;

    @BeforeAll
    public static void create() {

        optimizer = new Optimizer();
        queryRuleGraph = new RuleGraphTypes().getQueryRuleGraph();

        tables = new ArrayList<>();
        Table tab1 = new Table("Tab1");
        Table tab2 = new Table("Tab2");
        Table tab3 = new Table("Tab3");
        Table tab4 = new Table("Tab4");

        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("Col1", DataType.NUMBER, 20));
        columns.add(new Column("Col2", DataType.NUMBER, 20));
        columns.add(new Column("jCol1", DataType.NUMBER, 20));
        tab1.setColumns(columns);

        columns = new ArrayList<>();
        columns.add(new Column("Col3", DataType.NUMBER, 20));
        columns.add(new Column("Col4", DataType.NUMBER, 20));
        columns.add(new Column("jCol1", DataType.NUMBER, 20));
        columns.add(new Column("jCol2", DataType.NUMBER, 20));
        tab2.setColumns(columns);

        columns = new ArrayList<>();
        columns.add(new Column("Col5", DataType.NUMBER, 20));
        columns.add(new Column("Col6", DataType.NUMBER, 20));
        columns.add(new Column("jCol1", DataType.NUMBER, 20));
        columns.add(new Column("jCol2", DataType.NUMBER, 20));
        tab3.setColumns(columns);

        columns = new ArrayList<>();
        columns.add(new Column("Col7", DataType.NUMBER, 20));
        columns.add(new Column("Col8", DataType.NUMBER, 20));
        columns.add(new Column("jCol2", DataType.NUMBER, 20));
        tab4.setColumns(columns);

        tables.add(tab1);
        tables.add(tab2);
        tables.add(tab3);
        tables.add(tab4);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT Tab1.COL1 FROM TAB1",
            "SELECT COL1 FROM TAB1",
            "SELECT COL1, COL2, JCOL1 FROM TAB1",
            "SELECT COL1 FROM TAB1 WHERE COL1 = A",
            "SELECT COL1 FROM TAB1 WHERE COL1 = A AND COL2 = B",
            "SELECT COL1 FROM TAB1 JOIN TAB2 USING(JCOL1)",
            "SELECT COL1, COL3, JCOL2 FROM TAB1 JOIN TAB2 USING(JCOL1), TAB3",
            "SELECT COL1 FROM TAB1, TAB2 JOIN TAB3 USING(JCOL2)",
            "SELECT COL1 FROM TAB1 JOIN TAB2 USING(JCOL1), TAB3 JOIN TAB4 USING(JCOL2)",
            "SELECT COL1 FROM TAB1 JOIN TAB2 USING(JCOL1), TAB3 WHERE COL1 = A",
            "SELECT COL1 FROM TAB1 JOIN TAB2 USING(JCOL1), TAB3 WHERE COL1 = A AND COL2 = 7 AND COL3 = 3",
            "SELECT COL1, MIN(COL2) FROM TAB1 GROUP BY COL1",
            "SELECT MIN(COL1) FROM TAB1;",
            "SELECT COL1, COL2, MIN(jCOL1), MAX(jCOL1) FROM TAB1 GROUP BY COL1, COL2",
            "SELECT COL1, MIN(COL2) FROM TAB1 GROUP BY COL1 HAVING MAX(jCOL1) > 6",
            "SELECT COL1, MIN(COL2) FROM TAB1, TAB2 GROUP BY COL1 HAVING MAX(jCOL2) = 1",
            "SELECT MIN(COL1) FROM TAB1 GROUP BY COL2",
            "SELECT COL1, COL2, MIN(COL2), MAX(COL2) FROM TAB1 GROUP BY COL1, COL2, JCOL1 HAVING COUNT(COL1) > 4",
            "SELECT COL1, COL2, MIN(COL3), MAX(COL4) FROM TAB1, TAB2, TAB3 JOIN TAB4 USING(JCOL2) WHERE COL1 = A GROUP BY COL1, COL2, COL5 HAVING COUNT(COL1) > 5"
    })
    public void testCreation(String input) {
        String[] inputTokens = new Parser().formatAndTokenizeInput(input);
        System.out.println("Input:");
        System.out.println(input);
        System.out.println();
        QueryTree queryTree = optimizer.createQueryTree(queryRuleGraph, inputTokens, tables);
        queryTree = optimizer.cascadeSelections(queryTree);
        //System.out.println("After Cascading Selections:");
        //System.out.println(queryTree.getStructure());
        queryTree = optimizer.pushDownSelections(queryTree);
        //System.out.println("After Pushing Down Selections:");
        //System.out.println(queryTree.getStructure());
        queryTree = optimizer.cascadeAndPushDownProjections(queryTree);
        //System.out.println("After Cascading and Pushing Down Projections");
        //System.out.println(queryTree.getStructure());
        queryTree = optimizer.formJoins(queryTree);
        System.out.println(queryTree.getStructure());
        queryTree = optimizer.findSubtreesToPipeline(queryTree);

        System.out.println("----------------------------------------");
        assertTrue(true);
    }
}