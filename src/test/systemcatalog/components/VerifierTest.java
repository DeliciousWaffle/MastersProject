package test.systemcatalog.components;

import datastructures.relation.table.Table;
import datastructures.rulegraph.types.RuleGraphTypes;
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
            "SELECT Customers.CustomerID FROM Customers, CustomerPurchaseDetails WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID", // join in where clause
            "SELECT Customers.CustomerID FROM Customers, CustomerPurchaseDetails WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID and FirstName = \"Billy\"",
            "SELECT FirstName FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID", // prefixed join
            "SELECT Employees.FirstName FROM Customers INNER JOIN Employees ON CustomerID = EmployeeID",
            "SELECT Customers.CustomerID FROM Customers, CustomerPurchaseDetails", // prefixed columns in an ambiguous situation
            "SELECT CustomerPurchaseDetails.CustomerID FROM Customers, CustomerPurchaseDetails",
            "SELECT CustomerID FROM Customers WHERE FirstName = \"Blah\"", // where clause stuff
            "SELECT ProductName FROM Products WHERE Price > 1.20",
            "SELECT * FROM CustomerPurchaseDetails WHERE DatePurchased < \"2019-10-20\"",
            "SELECT State, COUNT(State) FROM Stores GROUP BY State", // group by stuff
            "SELECT State, COUNT(State) FROM Stores GROUP BY State HAVING COUNT(State) > 1", // having clause stuff
            "SELECT PaymentMethod, COUNT(PaymentMethod), AVG(Quantity) FROM CustomerPurchaseDetails GROUP BY PaymentMethod HAVING AVG(Quantity) > 1",
    })
    void testValidQuery(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        boolean isValid = verifier.isValid(InputType.QUERY, filtered, tables, users);
        System.out.println("Error Code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
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
            "SELECT CustomerID FROM Customers WHERE CustomerID = \"2020-10-20\"",
            "SELECT COUNT(CustomerID) FROM Customers GROUP BY CustomerID HAVING SUM(FirstName) > 1", // make sure data types of columns match in having clause
            "SELECT CustomerID FROM CustomerPurchaseDetails WHERE DatePurchased = \"2020-99-20\"", // invalid dates for where and having clause
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod HAVING AVG(DatePurchased) > \"Blah\"",
            "SELECT * FROM Customers GROUP BY CustomerID", // not allowed as there are no aggregate functions being used
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod HAVING AVG(DatePurchased) > \"2020-10-17\"" // missing AVG(DatePurchased) in select clause
    })
    void testInvalidQuery(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        boolean isValid = verifier.isValid(InputType.QUERY, filtered, tables, users);
        System.out.println("Error Code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CREATE TABLE Blah1(Col1 NUMBER(2, 1), Col2 CHAR(3), Col3 DATE, Col4 CHAR(10))"
    })
    void validCreateTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.CREATE_TABLE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CREATE TABLE 1(Col1 DATE)", // numeric table name
            "CREATE TABLE Blah(Col1 DATE, 2 CHAR(1), Col3 NUMBER(5))", // numeric column name
            "CREATE TABLE Customers(Col1 DATE)", // table already exists in system
            "CREATE TABLE Blah(Col1 CHAR(10.2))", // decimal size for size
            "CREATE TABLE Blah(Col1 NUMBER(10, 1.2))", // decimal size for decimal size
            "CREATE TABLE Blah(Col1 CHAR(10, 2))", // having a decimal size for char data type
            "CREATE TABLE Blah(Col1 CHAR(5), Col2 CHAR(10, 2))",
            "CREATE TABLE Blah(Col1 CHAR(-1))", // negative size
            "CREATE TABLE Blah(Col1 NUMBER(10, -1))", // negative decimal size
            "CREATE TABLE Blah(Col1 DATE, Col1 CHAR(3))" // duplicate columns
    })
    void invalidCreateTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.CREATE_TABLE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE Customers" // exists
    })
    void validDropTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.DROP_TABLE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE Blah" // table does not exist
    })
    void invalidDropTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.DROP_TABLE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ALTER TABLE Customers MODIFY CustomerID NUMBER(100)", // valid number change
            "ALTER TABLE Customers MODIFY CustomerID CHAR(5)", // valid number change
            "ALTER TABLE Customers ADD Blah NUMBER(5)",
            "ALTER TABLE Customers ADD FOREIGN KEY EmployeePurchaseDetails.EmployeeID", // TODO prefixed column name?
            "ALTER TABLE Customers ADD PRIMARY KEY FirstName",
    })
    void validAlterTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.ALTER_TABLE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ALTER TABLE Blah ADD Col1 DATE", // table Blah does not exist
            "ALTER TABLE Customers MODIFY Blah NUMBER(2)", // column blah does not exist
            "ALTER TABLE Customers MODIFY Blah CHAR(2)",
            "ALTER TABLE Customers MODIFY Blah DATE",
            "ALTER TABLE Customers DROP FOREIGN KEY Blah.BlahID", // foreign key does not exist
            "ALTER TABLE Customers ADD FOREIGN KEY Employees.BlahID",
            "ALTER TABLE Customers DROP PRIMARY KEY Blah", // primary key does not exist
            "ALTER TABLE Customers ADD PRIMARY KEY Blah",
            "ALTER TABLE Customers ADD Col1 NUMBER(-1)", // negative size
            "ALTER TABLE Customers ADD Col1 NUMBER(1.1)", // decimal size
            "ALTER TABLE Customers ADD Col1 NUMBER(0)", // 0 size
            "ALTER TABLE Customers MODIFY CustomerID DATE", // invalid conversion
            "ALTER TABLE Customers MODIFY FirstName DATE",
            "ALTER TABLE Customers MODIFY FirstName NUMBER(20)"
    })
    void invalidAlterTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.ALTER_TABLE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INSERT INTO CustomerPurchaseDetails VALUES(1, 2, 3, \"Blah\", \"2020-10-10\")",
            "INSERT INTO CustomerPurchaseDetails VALUES(1)" // rest of the values will be null
    })
    void validInsertCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.INSERT, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INSERT INTO CustomerPurchaseDetails VALUES(\"Blah\", 2, 3, \"Blah\", \"2020-10-10\")", // mismatch data types
            "INSERT INTO CustomerPurchaseDetails VALUES(\"2020-10-10\", 2, 3, \"Blah\", \"2020-10-10\")",
            "INSERT INTO CustomerPurchaseDetails VALUES(1, 2, 3, \"Blah\", 1)",
            "INSERT INTO CustomerPurchaseDetails VALUES(1, 2, 3, 4, \"2020-10-10\")",
            "INSERT INTO CustomerPurchaseDetails VALUES(1, 2, 3, \"Blah\", \"2020-10-10\", 4)", // too many values
    })
    void invalidInsertCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.INSERT, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE FROM Customers WHERE CustomerID = 1"
    })
    void validDeleteCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.DELETE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE FROM Blah WHERE CustomerID = 1", // table does not exist
            "DELETE FROM Customers WHERE Blah = 1", // column does not exist
            "DELETE FROM Customers WHERE CustomerID = \"Blah\"", // mismatch data type
            "DELETE FROM Customers WHERE FirstName = 1",
            "DELETE FROM Customers WHERE FirstName = \"2020-10-10\""
    })
    void invalidDeleteCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.DELETE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UPDATE Customers SET CustomerID = -1 WHERE CustomerID = 1",
            "UPDATE Customers SET FirstName = \"Blah\" WHERE CustomerID = 1"
    })
    void validUpdateCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.UPDATE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        System.out.println(RuleGraphTypes.getUpdateRuleGraph());
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UPDATE Blah SET Col1 = 1 WHERE Col1 = 1", // table does not exist
            "UPDATE Customers SET Blah = 1 WHERE Col1 = 1", // column does not exist
            "UPDATE Customers SET CustomerID = -1 WHERE Blah = \"Blah\"",
            "UPDATE Customers SET CustomerID = \"Blah\" WHERE FirstName = \"Blah\"", // mismatch data type
            "UPDATE Customers SET CustomerID = 1 WHERE FirstName = 1", // mismatch data type
    })
    void invalidUpdateCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.UPDATE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GRANT ALTER ON Customers TO Bob", // normal
            "GRANT ALTER, DELETE, INDEX ON Customers TO Bob", // multiple columns
            "GRANT UPDATE(CustomerID, FirstName, LastName) ON Customers TO Bob", // update, references
            "GRANT ALL PRIVILEGES ON Customers TO Bob", // all
            "GRANT ALTER ON Customers TO Bob, Sally, John", // multiple users
            "GRANT ALTER ON Customers TO Bob WITH GRANT OPTION", // grant option
            "GRANT ALTER, DELETE, INDEX, UPDATE(CustomerID, FirstName, LastName) ON Customers TO Bob, Sally, John" // mix of stuff
    })
    void validGrantCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.GRANT, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GRANT ALTER ON Blah TO Bob", // table doesn't exist
            "GRANT ALTER ON Customers TO Blah", // user doesn't exist
            "GRANT ALTER ON Customers TO Bob, Sally, Blah", // multiple
            "GRANT UPDATE(Blah) ON Customers TO Bob", // update, referenced columns don't exist in table
            "GRANT UPDATE(CustomerID, FirstName, LastName, Blah) ON Customers TO Bob",
            "GRANT REFERENCES(Blah) ON Customers TO Bob",
            "GRANT REFERENCES(CustomerID, FirstName, LastName, Blah) ON Customers TO Bob",
    })
    void invalidGrantCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.GRANT, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "REVOKE ALTER ON Customers FROM Bob", // normal
            "REVOKE ALTER, DELETE, INDEX ON Customers FROM Bob", // multiple columns
            "REVOKE UPDATE(CustomerID, FirstName, LastName) ON Customers FROM Bob", // update, references
            "REVOKE ALL PRIVILEGES ON Customers FROM Bob", // all
            "REVOKE ALTER ON Customers FROM Bob, Sally, John", // multiple users
            "REVOKE ALTER, DELETE, INDEX, UPDATE(CustomerID, FirstName, LastName) ON Customers FROM Bob, Sally, John" // mix of stuff
    })
    void validRevokeCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.REVOKE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "REVOKE ALTER ON Blah FROM Bob", // table doesn't exist
            "REVOKE ALTER ON Customers FROM Blah", // user doesn't exist
            "REVOKE ALTER ON Customers FROM Bob, Sally, Blah", // multiple
            "REVOKE UPDATE(Blah) ON Customers FROM Bob", // update, referenced columns don't exist in table
            "REVOKE UPDATE(CustomerID, FirstName, LastName, Blah) ON Customers FROM Bob",
            "REVOKE REFERENCES(Blah) ON Customers FROM Bob",
            "REVOKE REFERENCES(CustomerID, FirstName, LastName, Blah) ON Customers FROM Bob",
    })
    void invalidRevokeCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.REVOKE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "BUILD HASH TABLE ON CustomerID IN Customers", // normal
            "BUILD CLUSTERED FILE ON Customers AND CustomerPurchaseDetails" // clustered files
    })
    void validBuildFileStructure(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.BUILD_FILE_STRUCTURE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "BUILD HASH TABLE ON CustomerID IN Blah", // table does not exist
            "BUILD CLUSTERED FILE ON Customers AND Blah",
            "BUILD CLUSTERED FILE ON Blah AND Customers",
            "BUILD HASH TABLE ON Blah IN Customers", // column does not exist
    })
    void invalidBuildFileStructure(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.BUILD_FILE_STRUCTURE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "REMOVE FILE STRUCTURE ON CustomerID IN Customers",
            "REMOVE CLUSTERED FILE ON Customers AND CustomerPurchaseDetails"
    })
    void validRemoveFileStructure(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.REMOVE_FILE_STRUCTURE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "REMOVE FILE STRUCTURE ON CustomerID IN Blah", // table does not exist
            "REMOVE CLUSTERED FILE ON Customers AND Blah",
            "REMOVE CLUSTERED FILE ON Blah AND Customers",
            "REMOVE FILE STRUCTURE ON Blah IN Customers" // column does not exist
    })
    void invalidRemoveFileStructure(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = verifier.isValid(InputType.REMOVE_FILE_STRUCTURE, filtered, tables, users);
        System.out.println("Error code: " + verifier.getErrorMessage());
        verifier.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}