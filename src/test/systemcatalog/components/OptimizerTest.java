package test.systemcatalog.components;

import datastructures.misc.Pair;
import datastructures.misc.Quadruple;
import datastructures.misc.Triple;
import datastructures.querytree.QueryTree;
import datastructures.relation.table.Table;
import datastructures.user.User;
import enums.InputType;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;
import systemcatalog.components.Verifier;
import utilities.Utilities;

import javax.rmi.CORBA.Util;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for ensuring that the System Catalog's Optimizer is operating as it should be.
 * This will focus on looking at whether the query trees are created correctly, the input's
 * corresponding relational algebra is accurate, and the correct file structures are recommended.
 */
public class OptimizerTest {

    private static List<Table> tables;
    private static List<User> users;

    @BeforeAll
    public static void create() {
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES),
                false);
        users = Serializer.unSerializeUsers(IO.readOriginalData(FileType.OriginalData.ORIGINAL_USERS));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers", // simple query
            "SELECT Customers.FirstName FROM Customers", // simple query with column name prefixed with table name
            "   SELECT CustomerID,   FirstName, LastName  FROM   Customers   ", // many columns in projection
            "SELECT * FROM Customers", // using *
            "SELECT * FROM Customers, CustomerPurchaseDetails",
            "SELECT FirstName FROM Customers WHERE CustomerID = 1", // using a where clause
            "SELECT FirstName, LastName FROM Customers WHERE FirstName = \" Genaro   Blah\" AND LastName = \"  Curnutt \" AND CustomerID = 5 AND CustomerID = 7 AND LastName = \"Blaj\"", // where clause with 2 predicates
            "SELECT FirstName, LastName FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID", // basic join
            "SELECT FirstName, LastName FROM Customers, CustomerPurchaseDetails WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID", // basic join with where clause containing join condition
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID", // basic join with *
            "SELECT FirstName, LastName, StoreName FROM Employees INNER JOIN Stores ON EmployeeID = ManagerID", // join that's not prefixed with table name
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID", // basic join with 3 tables ===
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers, CustomerPurchaseDetails, Products WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID AND CustomerPurchaseDetails.ProductID = Products.ProductID", // basic join with 3 tables where join condition is in where clause ===
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID WHERE Customers.CustomerID = 1 AND Products.ProductID > 1 AND CustomerPurchaseDetails.PaymentMethod = \"blah\"", // basic join with 3 tables along with three predicates in where clause ===
            "SELECT FirstName, LastName, ProductName FROM Customers, Products", // basic cartesian product
            "SELECT MIN(CustomerID) FROM Customers", // simple aggregate function
            "SELECT COUNT(State) FROM Employees GROUP BY State", // aggregate function with group by clause
            "SELECT State, COUNT(State) FROM Employees GROUP BY State", // aggregate function with group by clause and a column to group by
            "SELECT State, COUNT(State) FROM Employees GROUP BY State HAVING COUNT(STATE) > 2", // aggregate function with group by clause and having clause
            "SELECT FirstName, LastName, MIN(Salary), COUNT(State) FROM Employees GROUP BY FirstName, LastName", // aggregate function with more advanced group by clause
            "SELECT State, COUNT(State) FROM Employees WHERE EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1", // complex aggregate function with where clause
            "SELECT MIN(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID", // simple aggregate function with join
            "SELECT Employees.EmployeeID, COUNT(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID GROUP BY Employees.EmployeeID", // aggregate function with join and group by clause
            "SELECT Employees.EmployeeID, COUNT(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID GROUP BY Employees.EmployeeID HAVING COUNT(Employees.EmployeeID) = 1", // aggregate function with join, group by, and having clauses
            "SELECT State, COUNT(State) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1", // aggregate function with join, where, group by, and having clauses
            "SELECT State, COUNT(State) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID INNER JOIN Products ON EmployeePurchaseDetails.ProductID = Products.ProductID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1", // aggregate function with 3 joins, where, group by, and having clause ===
    })
    public void testQueryTreeStateCreation(String input) {

        System.out.println(input + "\n");
        String[] filtered = Utilities.filterInput(input);
        Optimizer optimizer = new Optimizer();
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);

        Parser p = new Parser();
        if (! p.isValid(InputType.QUERY, filtered, true)) {
            System.out.println(p.getErrorMessage());
            fail();
        }

        Verifier verifier = new Verifier();
        if (! verifier.isValid(InputType.QUERY, filtered, tables, users)) {
            System.out.println(verifier.getErrorMessage());
            fail();
        }

        System.out.println("Query Tree Creation: --------------------------------------------------------------------");
        queryTrees.get(0).getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER).forEach((k, v) ->
                System.out.println(k + " " + v));

        System.out.println("\nCascade Selections: -------------------------------------------------------------------");
        queryTrees.get(1).getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER).forEach((k, v) ->
                System.out.println(k + " " + v));

        System.out.println("\nPush Down Selections: -----------------------------------------------------------------");
        queryTrees.get(2).getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER).forEach((k, v) ->
                System.out.println(k + " " + v));

        System.out.println("\nForm Joins: ---------------------------------------------------------------------------");
        queryTrees.get(3).getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER).forEach((k, v) ->
                System.out.println(k + " " + v));

        System.out.println("\nRearrange Leaf Nodes: -----------------------------------------------------------------");
        queryTrees.get(4).getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER).forEach((k, v) ->
                System.out.println(k + " " + v));

        System.out.println("\nPush Down Projections: ----------------------------------------------------------------");
        queryTrees.get(5).getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER).forEach((k, v) ->
                System.out.println(k + " " + v));

        System.out.println("\nPipeline Subtrees: --------------------------------------------------------------------");
        StringBuilder sb = new StringBuilder();
        for (int i = 6; i < queryTrees.size(); i++) { // 6 is the index of where pipelining begins
            queryTrees.get(i).getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER).forEach((k, v) ->
                    sb.append(k).append(" ").append(v).append("\n"));
            sb.append("\nPipeline Subtrees Again:\n");
        }
        sb.delete(sb.length() - 24, sb.length()); // removing "Pipeline Subtrees Again"
        System.out.println(sb.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers",
            "SELECT Customers.FirstName FROM Customers",
            "   SELECT CustomerID,   FirstName, LastName  FROM   Customers   ",
            "SELECT * FROM Customers",
            "SELECT FirstName FROM Customers WHERE CustomerID = 1",
            "SELECT FirstName FROM Customers WHERE FirstName = \"Genaro\"",
            "SELECT DatePurchased FROM CustomerPurchaseDetails WHERE DatePurchased = \"2020-01-01\"",
            "SELECT FirstName, LastName FROM Customers WHERE FirstName = \" Genaro   Blah\" AND LastName = \"  Curnutt \" AND CustomerID = 5 AND CustomerID = 7 AND LastName = \"Blaj\"",
            "SELECT FirstName, LastName FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT FirstName, LastName FROM Customers, CustomerPurchaseDetails WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT FirstName, LastName, StoreName FROM Employees INNER JOIN Stores ON EmployeeID = ManagerID",
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID",
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers, CustomerPurchaseDetails, Products WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID AND CustomerPurchaseDetails.ProductID = Products.ProductID",
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID WHERE Customers.CustomerID = 1 AND Products.ProductID > 1 AND CustomerPurchaseDetails.PaymentMethod = \"blah\"",
            "SELECT FirstName, LastName, ProductName FROM Customers, Products",
            "SELECT MIN(CustomerID) FROM Customers",
            "SELECT COUNT(State) FROM Employees GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees GROUP BY State HAVING COUNT(STATE) > 2",
            "SELECT FirstName, LastName, MIN(Salary), COUNT(State) FROM Employees GROUP BY FirstName, LastName",
            "SELECT State, COUNT(State) FROM Employees WHERE EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1",
            "SELECT MIN(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID",
            "SELECT Employees.EmployeeID, COUNT(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID GROUP BY Employees.EmployeeID",
            "SELECT Employees.EmployeeID, COUNT(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID GROUP BY Employees.EmployeeID HAVING COUNT(Employees.EmployeeID) = 1",
            "SELECT State, COUNT(State) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1",
            "SELECT State, COUNT(State), COUNT(Employees.EmployeeID), COUNT(EmployeePurchaseDetails.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID INNER JOIN Products ON EmployeePurchaseDetails.ProductID = Products.ProductID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1 AND COUNT(Employees.EmployeeID) > 1 AND COUNT(EmployeePurchaseDetails.EmployeeID) > 1",
    })
    void testNaiveRelationalAlgebra(String input) {

        System.out.println(input + "\n");
        String[] filtered = Utilities.filterInput(input);
        Optimizer optimizer = new Optimizer();
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);

        System.out.println(optimizer.getNaiveRelationAlgebra(queryTrees));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers",
            "SELECT Customers.FirstName FROM Customers",
            "   SELECT CustomerID,   FirstName, LastName  FROM   Customers   ",
            "SELECT * FROM Customers",
            "SELECT FirstName FROM Customers WHERE CustomerID = 1",
            "SELECT FirstName FROM Customers WHERE FirstName = \"Genaro\"",
            "SELECT DatePurchased FROM CustomerPurchaseDetails WHERE DatePurchased = \"2020-01-01\"",
            "SELECT FirstName, LastName FROM Customers WHERE FirstName = \" Genaro   Blah\" AND LastName = \"  Curnutt \" AND CustomerID = 5 AND CustomerID = 7 AND LastName = \"Blaj\"",
            "SELECT FirstName, LastName FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT FirstName, LastName FROM Customers, CustomerPurchaseDetails WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT FirstName, LastName, StoreName FROM Employees INNER JOIN Stores ON EmployeeID = ManagerID",
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID",
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers, CustomerPurchaseDetails, Products WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID AND CustomerPurchaseDetails.ProductID = Products.ProductID",
            "SELECT FirstName, LastName, ProductName, Price, Quantity, PaymentMethod FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID WHERE Customers.CustomerID = 1 AND Products.ProductID > 1 AND CustomerPurchaseDetails.PaymentMethod = \"blah\"",
            "SELECT FirstName, LastName, ProductName FROM Customers, Products",
            "SELECT MIN(CustomerID) FROM Customers",
            "SELECT COUNT(State) FROM Employees GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees GROUP BY State HAVING COUNT(STATE) > 2",
            "SELECT FirstName, LastName, MIN(Salary), COUNT(State) FROM Employees GROUP BY FirstName, LastName",
            "SELECT State, COUNT(State) FROM Employees WHERE EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1",
            "SELECT MIN(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID",
            "SELECT Employees.EmployeeID, COUNT(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID GROUP BY Employees.EmployeeID",
            "SELECT Employees.EmployeeID, COUNT(Employees.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID GROUP BY Employees.EmployeeID HAVING COUNT(Employees.EmployeeID) = 1",
            "SELECT State, COUNT(State) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1",
            "SELECT State, COUNT(State), COUNT(Employees.EmployeeID), COUNT(EmployeePurchaseDetails.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID INNER JOIN Products ON EmployeePurchaseDetails.ProductID = Products.ProductID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1 AND COUNT(Employees.EmployeeID) > 1 AND COUNT(EmployeePurchaseDetails.EmployeeID) > 1",
    })
    void testOptimizedRelationalAlgebra(String input) {

        System.out.println(input + "\n");
        String[] filtered = Utilities.filterInput(input);
        Optimizer optimizer = new Optimizer();
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);

        System.out.println(optimizer.getOptimizedRelationalAlgebra(queryTrees));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers", // N/A
            "SELECT CustomerID, FirstName, LastName FROM Customers", // N/A
            "SELECT * FROM Customers", // N/A
            "SELECT FirstName FROM Customers WHERE CustomerID = 1", // hash on CustomerID
            "SELECT FirstName FROM Customers WHERE FirstName = \"Genaro\"", // hash on FirstName
            "SELECT * FROM Customers WHERE CustomerID > 1", // clustered b tree on CustomerID
            "SELECT DatePurchased FROM CustomerPurchaseDetails WHERE DatePurchased > \"2020-01-01\"", // clustered b tree on DatePurchased
            "SELECT * FROM Customers WHERE CustomerID = 1 AND CustomerID > 1", // secondary b tree on CustomerID
            "SELECT * FROM Customers WHERE CustomerID = 1 AND CustomerID > 1 AND FirstName = \"Blah\" AND LastName = \"Blah\"", // secondary b tree on CustomerID (none), hash on FirstName, hash on LastName
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID", // clustered file
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID = 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1 AND PaymentMethod = \"Discover\"",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID = 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID WHERE Customers.CustomerID > 1 AND PaymentMethod = \"Discover\" AND Price > 10.2",
            "SELECT MIN(CustomerID) FROM Customers",
            "SELECT MIN(CustomerID) FROM Customers WHERE CustomerID > 30",
            "SELECT COUNT(State) FROM Employees WHERE State = \"AZ\" GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees WHERE State = \"AZ\" GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees WHERE State = \"AZ\" AND EmployeeID > 1 GROUP BY State HAVING COUNT(STATE) > 2",
            "SELECT State, COUNT(State), COUNT(Employees.EmployeeID), COUNT(EmployeePurchaseDetails.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID INNER JOIN Products ON EmployeePurchaseDetails.ProductID = Products.ProductID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1 AND COUNT(Employees.EmployeeID) > 1 AND COUNT(EmployeePurchaseDetails.EmployeeID) > 1",
    })
    void testRecommendedFileStructuresWithVerifier(String query) {

        System.out.println(query + "\n");
        String[] filtered = Utilities.filterInput(query);
        Optimizer optimizer = new Optimizer();
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);

        List<Triple<String, String, String>> recommendedFileStructures =
                optimizer.getRecommendedFileStructures(queryTrees, tables, true).getFirst();
        System.out.println("Recommended File Structures:");
        recommendedFileStructures.forEach(System.out::println);

        System.out.println();

        List<Pair<String, String>> clusteredTables =
                optimizer.getRecommendedFileStructures(queryTrees, tables, true).getSecond();
        System.out.println("Tables To Cluster:");
        clusteredTables.forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers",
            "SELECT CustomerID, FirstName, LastName FROM Customers",
            "SELECT * FROM Customers",
            "SELECT FirstName FROM Customers WHERE CustomerID = 1",
            "SELECT FirstName FROM Customers WHERE FirstName = \"Genaro\"",
            "SELECT * FROM Customers WHERE CustomerID > 1",
            "SELECT DatePurchased FROM CustomerPurchaseDetails WHERE DatePurchased > \"2020-01-01\"",
            "SELECT * FROM Customers WHERE CustomerID = 1 AND CustomerID > 1",
            "SELECT * FROM Customers WHERE CustomerID = 1 AND CustomerID > 1 AND FirstName = \"Blah\" AND LastName = \"Blah\"",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID = 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1 AND PaymentMethod = \"Discover\"",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID = 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID WHERE Customers.CustomerID > 1 AND PaymentMethod = \"Discover\" AND Price > 10.2",
            "SELECT MIN(CustomerID) FROM Customers",
            "SELECT MIN(CustomerID) FROM Customers WHERE CustomerID > 30",
            "SELECT COUNT(State) FROM Employees WHERE State = \"AZ\" GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees WHERE State = \"AZ\" GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees WHERE State = \"AZ\" AND EmployeeID > 1 GROUP BY State HAVING COUNT(STATE) > 2",
            "SELECT State, COUNT(State), COUNT(Employees.EmployeeID), COUNT(EmployeePurchaseDetails.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID INNER JOIN Products ON EmployeePurchaseDetails.ProductID = Products.ProductID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1 AND COUNT(Employees.EmployeeID) > 1 AND COUNT(EmployeePurchaseDetails.EmployeeID) > 1",
    })
    void testRecommendedFileStructuresWithoutVerifier(String query) {

        System.out.println(query + "\n");
        String[] filtered = Utilities.filterInput(query);
        Optimizer optimizer = new Optimizer();
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);

        List<Triple<String, String, String>> recommendedFileStructures =
                optimizer.getRecommendedFileStructures(queryTrees, tables, false).getFirst();
        System.out.println("Recommended File Structures:");
        recommendedFileStructures.forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers",
            "SELECT CustomerID, FirstName, LastName FROM Customers",
            "SELECT * FROM Customers",
            "SELECT FirstName FROM Customers WHERE CustomerID = 1",
            "SELECT FirstName FROM Customers WHERE FirstName = \"Genaro\"",
            "SELECT * FROM Customers WHERE CustomerID > 1",
            "SELECT DatePurchased FROM CustomerPurchaseDetails WHERE DatePurchased > \"2020-01-01\"",
            "SELECT * FROM Customers WHERE CustomerID = 1 AND CustomerID > 1",
            "SELECT * FROM Customers WHERE CustomerID = 1 AND CustomerID > 1 AND FirstName = \"Blah\" AND LastName = \"Blah\"",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID = 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1 AND PaymentMethod = \"Discover\"",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID = 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID WHERE Customers.CustomerID > 1",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID WHERE Customers.CustomerID > 1 AND PaymentMethod = \"Discover\" AND Price > 10.2",
            "SELECT MIN(CustomerID) FROM Customers",
            "SELECT MIN(CustomerID) FROM Customers WHERE CustomerID > 30",
            "SELECT COUNT(State) FROM Employees WHERE State = \"AZ\" GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees WHERE State = \"AZ\" GROUP BY State",
            "SELECT State, COUNT(State) FROM Employees WHERE State = \"AZ\" AND EmployeeID > 1 GROUP BY State HAVING COUNT(STATE) > 2",
            "SELECT State, COUNT(State), COUNT(Employees.EmployeeID), COUNT(EmployeePurchaseDetails.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID INNER JOIN Products ON EmployeePurchaseDetails.ProductID = Products.ProductID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1 AND COUNT(Employees.EmployeeID) > 1 AND COUNT(EmployeePurchaseDetails.EmployeeID) > 1",
    })
    void testCostAnalysis(String query) {

        System.out.println(query + "\n");
        String[] filtered = Utilities.filterInput(query);
        Optimizer optimizer = new Optimizer();
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);

        Quadruple<Integer, Integer, String, String> costAnalysis =
                optimizer.getCostAnalysis(queryTrees, tables, true);
        System.out.println("Production Cost: " + costAnalysis.getFirst() + "\n");
        System.out.println("Write To Disk Cost: " + costAnalysis.getSecond() + "\n");
        System.out.println(costAnalysis.getThird() + "\n");
        System.out.println(costAnalysis.getFourth() + "\n");
    }
}