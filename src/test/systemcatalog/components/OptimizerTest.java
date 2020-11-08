package test.systemcatalog.components;

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
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
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
    public void testCreation(String input) {

        System.out.println(input + "\n");
        String[] filtered = Utilities.filterInput(input);
        Optimizer optimizer = new Optimizer();
        optimizer.getQueryTreeStates(filtered, tables);
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
/*
    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers", // simple query
            "SELECT Customers.FirstName FROM Customers", // simple query with column name prefixed with table name
            "   SELECT CustomerID,   FirstName, LastName  FROM   Customers   ", // many columns in projection
            "SELECT * FROM Customers", // using *
            "SELECT FirstName FROM Customers WHERE CustomerID = 1", // using a where clause
            "SELECT FirstName FROM Customers WHERE FirstName = \"Genaro\"",
            "SELECT DatePurchased FROM CustomerPurchaseDetails WHERE DatePurchased = \"2020-01-01\"",
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
            "SELECT State, COUNT(State), COUNT(Employees.EmployeeID), COUNT(EmployeePurchaseDetails.EmployeeID) FROM Employees INNER JOIN EmployeePurchaseDetails ON Employees.EmployeeID = EmployeePurchaseDetails.EmployeeID INNER JOIN Products ON EmployeePurchaseDetails.ProductID = Products.ProductID WHERE Employees.EmployeeID = 1 GROUP BY State HAVING COUNT(State) > 1 AND COUNT(Employees.EmployeeID) > 1 AND COUNT(EmployeePurchaseDetails.EmployeeID) > 1", // aggregate function with 3 joins, where, group by, and having clause ===
            "SELECT DatePurchased, COUNT(DatePurchased) FROM CustomerPurchaseDetails GROUP BY DatePurchased HAVING COUNT(DatePurchased) = \"2020-10-10\"",
    })
    void testNaiveRelationalAlgebra(String input) {

        System.out.println(input + "\n");
        String[] filtered = Utilities.filterInput(input);
        Optimizer optimizer = new Optimizer();
        optimizer.getQueryTreeStates(filtered, tables);
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);

        System.out.println(optimizer.getNaiveRelationAlgebra(queryTrees));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers", // simple query
            "SELECT Customers.FirstName FROM Customers", // simple query with column name prefixed with table name
            "   SELECT CustomerID,   FirstName, LastName  FROM   Customers   ", // many columns in projection
            "SELECT * FROM Customers", // using *
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
    void testOptimizedRelationalAlgebra(String input) {

        System.out.println(input + "\n");
        String[] filtered = Utilities.filterInput(input);
        Optimizer optimizer = new Optimizer();
        optimizer.getQueryTreeStates(filtered, tables);
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);

        System.out.println(optimizer.getOptimizedRelationalAlgebra(queryTrees));
    }
*//*
    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers", // simple query
            "SELECT Customers.FirstName FROM Customers", // simple query with column name prefixed with table name
            "   SELECT CustomerID,   FirstName, LastName  FROM   Customers   ", // many columns in projection
            "SELECT * FROM Customers", // using *
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
    void testRecommendedFileStructures(String query) {

    }
*/
    /*
    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers", // simple query
            "SELECT Customers.FirstName FROM Customers", // simple query with column name prefixed with table name
            "   SELECT CustomerID,   FirstName, LastName  FROM   Customers   ", // many columns in projection
            "SELECT * FROM Customers", // using *
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
    void testCostAnalysis(String query) {

    }*/
}