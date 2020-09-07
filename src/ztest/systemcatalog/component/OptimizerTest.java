package ztest.systemcatalog.component;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import files.io.FileType;
import files.io.IO;
import files.io.Serialize;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptimizerTest {

    private static List<Table> tables;

    @BeforeAll
    public static void create() {
        tables = Serialize.unSerializeTables(IO.readCurrentData(FileType.CurrentData.CURRENT_TABLES));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers",
            "SELECT Customers.FirstName FROM Customers",
            "SELECT CustomerID, FirstName, LastName FROM Customers",
            "SELECT FirstName, LastName FROM Customers WHERE CustomerID = 1",
            "SELECT * FROM Customers WHERE FirstName = Genaro AND LastName = Curnutt",
            "SELECT * FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID)",
            "SELECT * FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID), Products",
            "SELECT FirstName FROM Customers, Products JOIN EmployeePurchaseDetails USING(ProductID)",
            "SELECT FirstName FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID), Employees JOIN EmployeePurchaseDetails USING(EmployeeID)",
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
    public void testCreationWithNoVerifier(String input) {

        System.out.println(input + "\n");

        String[] tokenizedInput = Parser.formatAndTokenizeInput(input);
        Optimizer optimizer = new Optimizer();
        optimizer.toggleRearrangeLeafNodes();
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(tokenizedInput, tables);

        System.out.println("Query Tree Creation:");
        System.out.println(queryTrees.get(0).getTreeStructure());
        System.out.println("Cascade Selections:");
        System.out.println(queryTrees.get(1).getTreeStructure());
        System.out.println(queryTrees.get(1).getOperatorsAndLocations());
        System.out.println("Push Down Selections:");
        System.out.println(queryTrees.get(2).getTreeStructure());
        System.out.println("Form Joins:");
        System.out.println(queryTrees.get(3).getTreeStructure());
        System.out.println("Rearrange Leaf Nodes:");
        System.out.println(queryTrees.get(4).getTreeStructure());
        System.out.println("Push Down Projections:");
        System.out.println(queryTrees.get(5).getTreeStructure());
        System.out.println("Pipeline Subtrees:");
        StringBuilder sb = new StringBuilder();
        for(int i = 6; i < queryTrees.size(); i++) { // 6 is the index of where pipelining begins
            sb.append(queryTrees.get(i).getTreeStructure());
            sb.append("Pipeline Subtrees Again:");
        }
        sb.delete(sb.length() - 24, sb.length()); // removing "Pipeline Subtrees Again"
        System.out.println(sb.toString());
        System.out.println("relational algebra: " + optimizer.getNaiveRelationalAlgebra(queryTrees.get(0)));
        System.out.println("=========================================================================================");

        assertTrue(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ""
    })
    public void testCreationWithVerifier(String input) {

        assertTrue(true);
    }
}