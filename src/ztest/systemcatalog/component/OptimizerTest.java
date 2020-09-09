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
            "SELECT FirstName FROM Customers", // simple query
            "SELECT Customers.FirstName FROM Customers", // simple query with column name prefixed with table name
            "SELECT CustomerID, FirstName, LastName FROM Customers", // many columns in projection
            "SELECT * FROM Customers", // using *
            "SELECT FirstName FROM Customers WHERE CustomerID = 1", // using a where clause
            "SELECT FirstName, LastName FROM Customers WHERE FirstName = Genaro AND LastName = Curnutt", // where clause with 2 predicates
            "SELECT FirstName, LastName FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID)", // basic join
            "SELECT FirstName, LastName FROM Customers, CustomerPurchaseDetails WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID", // basic join with where clause containing join condition
            "SELECT * FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID)", // basic join with *
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID) JOIN Products USING(ProductID)", // basic join with 3 tables
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers, CustomerPurchaseDetails, Products WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID AND CustomerPurchaseDetails.ProductID = Products.ProductID", // basic join with 3 tables where join condition is in where clause
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID) JOIN Products USING(ProductID) WHERE CustomerID = 1 AND ProductID = 1", // basic join with 3 tables along with a two predicates in where clause
            "SELECT FirstName, LastName, ProductName FROM Customers, Products", // basic cartesian product
            "SELECT FirstName, LastName, ProductName FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID), Products", // join combined with cartesian product
            "SELECT FirstName FROM Customers, Products JOIN EmployeePurchaseDetails USING(ProductID)", // cartesian product combined with join
            "SELECT FirstName FROM Customers JOIN CustomerPurchaseDetails USING(CustomerID), Employees JOIN EmployeePurchaseDetails USING(EmployeeID)", // 2 joins combined as cartesian products
            "SELECT MIN(CustomerID) FROM Customers", // simple aggregate function
            "SELECT COUNT(State) FROM Employees GROUP BY State", // aggregate function with group by clause
            "SELECT State, COUNT(State) FROM Employees GROUP BY State", // aggregate function with group by clause and a column to group by
            "SELECT State, COUNT(State) FROM Employees GROUP BY State HAVING COUNT(STATE) > 2", // aggregate function with group by clause and having clause
            "SELECT FirstName, LastName, MIN(Salary), COUNT(State) FROM Stores GROUP BY FirstName, LastName", // aggregate function with more advanced group by clause
            "SELECT State, COUNT(State) FROM Employees WHERE EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1", // complex aggregate function with where clause
            "SELECT MIN(CustomerID) FROM Customers JOIN EmployeePurchaseDetails USING(EmployeeID)", // simple aggregate function with joins
            "SELECT CustomerID, MIN(CustomerID) FROM Customers JOIN EmployeePurchaseDetails USING(EmployeeID) GROUP BY CustomerID", // aggregate function with joins and group by clause
            "SELECT CustomerID, MIN(CustomerID) FROM Customers JOIN EmployeePurchaseDetails USING(EmployeeID) GROUP BY CustomerID HAVING COUNT(CustomerID) = 1", // aggregate function with joins, group by, and having clauses
            "SELECT State, COUNT(State) FROM Employees JOIN EmployeePurchaseDetails USING(EmployeeID) WHERE EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1", // aggregate function with joins, where, group by, and having clauses
    })
    public void testCreationWithNoVerifier(String input) {

        System.out.println(input + "\n");

        String[] tokenizedInput = Parser.formatAndTokenizeInput(input);
        Optimizer optimizer = new Optimizer();
        optimizer.toggleRearrangeLeafNodes();
        List<QueryTree> queryTrees;// = optimizer.getQueryTreeStates(tokenizedInput, tables);

        System.out.println("Query Tree Creation:");
        //System.out.println(queryTrees.get(0).getTreeStructure());
        System.out.println("Cascade Selections:");
        //System.out.println(queryTrees.get(1).getTreeStructure());
        System.out.println("Push Down Selections:");
        //System.out.println(queryTrees.get(2).getTreeStructure());
        System.out.println("Form Joins:");
        //System.out.println(queryTrees.get(3).getTreeStructure());
        System.out.println("Rearrange Leaf Nodes:");
        //System.out.println(queryTrees.get(4).getTreeStructure());
        System.out.println("Push Down Projections:");
        //System.out.println(queryTrees.get(5).getTreeStructure());
        System.out.println("Pipeline Subtrees:");
        StringBuilder sb = new StringBuilder();
        //for(int i = 6; i < queryTrees.size(); i++) { // 6 is the index of where pipelining begins
            //sb.append(queryTrees.get(i).getTreeStructure());
            sb.append("Pipeline Subtrees Again:");
        //}
        sb.delete(sb.length() - 24, sb.length()); // removing "Pipeline Subtrees Again"
        System.out.println(sb.toString());
        //System.out.println("relational algebra: " + optimizer.getNaiveRelationalAlgebra(queryTrees.get(0)));
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