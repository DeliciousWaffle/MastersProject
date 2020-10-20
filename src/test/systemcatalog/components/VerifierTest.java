package test.systemcatalog.components;

import datastructures.relation.table.Table;
import datastructures.user.User;
import enums.InputType;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.Parser;
import systemcatalog.components.Verifier;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ensuring that the System Catalog's Verifier is operating as it should be.
 * This will focus on performing integrity checks with respect to what's available on the system.
 */
public class VerifierTest {

    private static Verifier verifier;
    private static List<Table> tables;
    private static List<User> users;

    @BeforeAll
    static void init() {
        verifier = new Verifier();
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES));
        users = Serializer.unSerializeUsers(IO.readOriginalData(FileType.OriginalData.ORIGINAL_USERS));
        /*System.out.println("Tables ----------------------------------------------------------------------------------");
        tables.forEach(System.out::println);
        System.out.println("Users -----------------------------------------------------------------------------------");
        users.forEach(System.out::println);*/
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT CustomerID FROM Customers", // simple
            "SELECT * FROM Customers",
            "SELECT CustomerID, ProductID FROM Customers, Products",
            "SELECT FirstName FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID", // prefixed join
            "SELECT Employees.FirstName FROM Customers INNER JOIN Employees ON CustomerID = EmployeeID",
            "SELECT Customers.CustomerID FROM Customers, CustomerPurchaseDetails", // prefixed columns in an ambiguous situation
            "SELECT CustomerPurchaseDetails.CustomerID FROM Customers, CustomerPurchaseDetails",
    })
    void testValidQuery(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        boolean isValid = verifier.isValid(InputType.QUERY, filtered, tables, users);
        System.out.println("Error Code: " + verifier.getErrorMessage());
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT CustomerID FROM Blah", // Blah table does not exist in system
            "SELECT CustomerID FROM Customers, Blah",
            "SELECT Blah FROM Customers", // column Blah does not exist within table (the table exists though) in select clause
            "SELECT CustomerID, FirstName, LastName, Blah FROM Customers",
            "SELECT CustomerID FROM Customers WHERE Blah = \"Blah\"", // in where clause
            "SELECT CustomerID FROM Customers WHERE CustomerID = 1 AND Blah = \"Blah\"",
            "SELECT CustomerID FROM Customers GROUP BY Blah", // in group by clause
            "SELECT CustomerID FROM Customers GROUP BY CustomerID HAVING COUNT(Blah) > 1", // in having clause

            "SELECT Customers.Blah FROM Customers", // prefixing a table that exists with a column that doesn't exist
            "SELECT CustomerID, Customers.Blah FROM Customers",
            "SELECT Blah.CustomerID FROM Customers", // column that exists but prefixed with a table that doesn't exist
            "SELECT CustomerID FROM Customers, CustomerPurchaseDetails", // ambiguous columns (columns that exist in multiple tables that are not prefixed)
            "SELECT CustomerID FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.Blah", // column does not exist in join criteria (1st and 2nd table)
            "SELECT CustomerID FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.Blah = CustomerPurchaseDetails.CustomerID",
            "SELECT SUM(FirstName) FROM Customers", // only accept numeric values for aggregations, excluding count() in select/having clauses
            "SELECT COUNT(CustomerID), SUM(FirstName) FROM Customers",
            "SELECT COUNT(CustomerID) FROM Customers GROUP BY FirstName HAVING AVG(FirstName) > 1",
            "SELECT Customers.CustomerID FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.FirstName = CustomerPurchaseDetails.CustomerID", // make sure data types of columns match in join criteria
            "SELECT Customers.CustomerID FROM Customers INNER JOIN CustomerPurchaseDetails ON CustomerPurchaseDetails.PaymentMethod = Customers.CustomerID",
            "SELECT Customers.CustomerID FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.FirstName > CustomerPurchaseDetails.PaymentMethod", // if data types match, make sure that if >, <, >=, <= is used, that the values are numeric or dates
            "SELECT CustomerID FROM Customers WHERE CustomerID = \"Blah\"", // make sure data types of columns match in where clause
            "SELECT CustomerID FROM Customers WHERE FirstName = 1",
            "SELECT CustomerID FROM Customers WHERE CustomerID = \"10-10-2020\"",
            "SELECT COUNT(CustomerID) FROM Customers GROUP BY CustomerID HAVING SUM(FirstName) > 1", // make sure data types of columns match in having clause
            // invalid dates for where and having clause TODO
    })
    void testInvalidQuery(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        boolean isValid = verifier.isValid(InputType.QUERY, filtered, tables, users);
        System.out.println("Error Code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        System.out.println(new Parser().isValid(InputType.QUERY, filtered));
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}