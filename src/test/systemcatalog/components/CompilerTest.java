package test.systemcatalog.components;

import datastructures.querytree.QueryTree;
import datastructures.relation.resultset.ResultSet;
import datastructures.relation.table.Table;
import datastructures.user.User;
import enums.InputType;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Compiler;
import systemcatalog.components.Parser;
import systemcatalog.components.Verifier;
import utilities.Utilities;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for ensuring that the System Catalog's Compiler is operating as it should be.
 * This means that when input is executed, this makes sure that the output is correct.
 */
public class CompilerTest {

    private static Optimizer optimizer;
    private static Compiler compiler;
    private static List<Table> tablesForQuery;
    private static List<User> users;

    @BeforeAll
    public static void init() {
        optimizer = new Optimizer();
        compiler = new Compiler();
        tablesForQuery = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        users = Serializer.unSerializeUsers(IO.readOriginalData(FileType.OriginalData.ORIGINAL_USERS));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers",
            "SELECT FirstName, FirstName FROM Customers",
            "SELECT Customers.FirstName FROM Customers",
            "SELECT FirstName, LastName FROM Customers",
            "SELECT LastName, FirstName FROM Customers",
            "SELECT * FROM Customers",
            "SELECT * FROM Customers WHERE CustomerID = 1",
            "SELECT * FROM Customers WHERE FirstName = \"Genaro\"",
            "SELECT * FROM Customers WHERE FirstName = \"Genaro\" AND CustomerID = -1",
            "SELECT * FROM Customers WHERE CustomerID != 1",
            "SELECT * FROM Customers WHERE CustomerID != 1 AND CustomerID != 20",
            "SELECT * FROM Customers WHERE FirstName != \"Genaro\" AND CustomerID != 20",
            "SELECT CustomerID FROM Customers WHERE CustomerID > 20",
            "SELECT CustomerID FROM Customers WHERE CustomerId < 30",
            "SELECT CustomerID FROM Customers WHERE CustomerId > 20 AND CustomerID < 30",
            "SELECT * FROM CustomerPurchaseDetails WHERE DatePurchased > \"2021-01-01\"",
            "SELECT * FROM CustomerPurchaseDetails WHERE DatePurchased < \"2021-01-01\"",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT COUNT(FirstName) FROM Customers",
            //"SELECT AVG(FirstName) FROM Customers", // would be invalid, but allow anyways
            "SELECT COUNT(CustomerID) FROM Customers",
            "SELECT SUM(CustomerID) FROM Customers",
            "SELECT COUNT(CustomerID), SUM(CustomerID) FROM Customers",
            "SELECT SUM(DatePurchased) FROM CustomerPurchaseDetails",
            "SELECT AVG(DatePurchased) FROM CustomerPurchaseDetails",
            "SELECT SUM(DatePurchased), AVG(DatePurchased), COUNT(CustomerID) FROM CustomerPurchaseDetails",
            "SELECT AVG(Price) FROM Products",
            "SELECT AVG(DiscountAmount) FROM EmployeePurchaseDetails",
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod",
            "SELECT PaymentMethod, Quantity, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod, Quantity",
            "SELECT PaymentMethod, COUNT(PaymentMethod), AVG(Quantity) FROM CustomerPurchaseDetails GROUP BY PaymentMethod",
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails WHERE PaymentMethod = \"American Express\" GROUP By PaymentMethod",
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod HAVING COUNT(PaymentMethod) = 16",
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod HAVING COUNT(PaymentMethod) > 16 AND COUNT(PaymentMethod) < 20",
            "SELECT FirstName, LastName, ProductName FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID",
            "SELECT FirstName, LastName, ProductName FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID WHERE FirstName = \"Genaro\" AND LastName = \"Curnutt\"",
            "SELECT ProductName, COUNT(CustomerPurchaseDetails.ProductID) FROM Products INNER JOIN CustomerPurchaseDetails ON Products.ProductID = CustomerPurchaseDetails.ProductID GROUP BY ProductName",
            "SELECT ProductName, COUNT(Products.ProductID) FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID INNER JOIN Products ON CustomerPurchaseDetails.ProductID = Products.ProductID GROUP BY ProductName",
            "SELECT * FROM Products INNER JOIN CustomerPurchaseDetails ON Products.ProductID = CustomerPurchaseDetails.ProductID",
            "SELECT * FROM Customers, Products WHERE CustomerID = 1",
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails WHERE PaymentMethod = \"Check\" GROUP BY PaymentMethod HAVING COUNT(PaymentMethod) > 16 AND COUNT(PaymentMethod) < 20",
            // not supported
            //"SELECT * FROM Customers, CustomerPurchaseDetails WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID AND Customers.CustomerID = CustomerPurchaseDetails.Quantity"
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID > CustomerPurchaseDetails.CustomerID"
    })
    public void testQuery(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tablesForQuery);
        ResultSet resultSet = compiler.executeQuery(queryTrees, tablesForQuery);
        System.out.println(resultSet);

        Parser p = new Parser();
        if (! p.isValid(InputType.QUERY, filtered)) {
            System.out.println(p.getErrorMessage());
            fail();
        }

        Verifier v = new Verifier();
        if (! v.isValid(InputType.QUERY, filtered, tablesForQuery, users)) {
            System.out.println(v.getErrorMessage());
            fail();
        }
    }

    @Test
    public void testCreateTable() {
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        String input = "CREATE TABLE Blah1(Col1 NUMBER(2, 1), Col2 CHAR(3), Col3 DATE, Col4 CHAR(10))";
        System.out.println(input);
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.CREATE_TABLE, tokens, tables, users);
        input = "CREATE TABLE Blah2(Col1 NUMBER(2))";
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.CREATE_TABLE, tokens, tables, users);
        input = "CREATE TABLE Blah3(Col1 NUMBER(2, 5), Col2 NUMBER(3, 5), Col3 DATE, Col4 DATE, Col5 CHAR(10), Col6 NUMBER(3), Col7 DATE)";
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.CREATE_TABLE, tokens, tables, users);
        tables.forEach(System.out::println);
    }

    @Test
    public void testDropTable() {
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        String input = "DROP TABLE Customers";
        System.out.println(input);
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.DROP_TABLE, tokens, tables, users);
        tables.forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ALTER TABLE Customers MODIFY CustomerID NUMBER(100)",
            "ALTER TABLE Customers MODIFY CustomerID CHAR(5)",
            "ALTER TABLE Customers MODIFY FirstName CHAR(100)",
            "ALTER TABLE Customers MODIFY FirstName CHAR(1)",
            "ALTER TABLE Customers ADD Blah NUMBER(5)",
            "ALTER TABLE Customers ADD FOREIGN KEY EmployeePurchaseDetails.EmployeeID",
            "ALTER TABLE Customers ADD PRIMARY KEY FirstName",
            "ALTER TABLE Customers DROP LastName"
    })
    public void testAlterTable(String input) {
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.ALTER_TABLE, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("Customers"))
                .forEach(System.out::println);
    }

    @Test
    public void testInsert() {

        String input = "INSERT INTO Customers VALUES(-1, \"Blah\", \"Blah\")";
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.INSERT, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("Customers"))
                .forEach(System.out::println);

        input = "INSERT INTO Customers VALUES(-2)";
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.INSERT, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("Customers"))
                .forEach(System.out::println);

        input = "INSERT INTO Customers VALUES(-3, \"A Very Long Name That Should Be Cut Off\")";
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.INSERT, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("Customers"))
                .forEach(System.out::println);
    }

    @Test
    public void testDelete() {

        String input = "DELETE FROM Customers WHERE CustomerID = 1";
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.DELETE, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("Customers"))
                .forEach(System.out::println);

        input = "DELETE FROM CustomerPurchaseDetails WHERE PaymentMethod = \"Check\"";
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.DELETE, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("CustomerPurchaseDetails"))
                .forEach(System.out::println);
    }

    @Test
    public void testUpdate() {

        String input = "UPDATE Customers SET FirstName = \"Blah\" WHERE CustomerID = 1";
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.UPDATE, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("Customers"))
                .forEach(System.out::println);

        input = "UPDATE CustomerPurchaseDetails SET PaymentMethod = \"Blah\" WHERE PaymentMethod = \"Check\"";
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.UPDATE, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("CustomerPurchaseDetails"))
                .forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "BUILD HASH TABLE ON CustomerID IN Customers",
            "BUILD SECONDARY BTREE ON CustomerID IN Customers",
            "BUILD CLUSTERED BTREE ON CustomerID IN Customers",
            "BUILD CLUSTERED FILE ON Customers AND CustomerPurchaseDetails"
    })
    public void testBuildFileStructure(String input) {
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(input);
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.BUILD_FILE_STRUCTURE, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("Customers"))
                .forEach(table -> {
                    System.out.println(table.getTableName() + " clustered with " + table.getClusteredWithTableName());
                    table.getColumns()
                        .forEach(col -> System.out.println(col + " " + col.getFileStructure()));
                });
    }

    @ParameterizedTest
    @CsvSource(value = {
            "BUILD HASH TABLE ON CustomerID IN Customers,REMOVE FILE STRUCTURE ON CustomerID IN Customers",
            "BUILD SECONDARY BTREE ON CustomerID IN Customers,REMOVE FILE STRUCTURE ON CustomerID IN Customers",
            "BUILD CLUSTERED BTREE ON CustomerID IN Customers,REMOVE FILE STRUCTURE ON CustomerID IN Customers",
            "BUILD CLUSTERED FILE ON Customers AND CustomerPurchaseDetails,REMOVE CLUSTERED FILE ON Customers AND CustomerPurchaseDetails"
    })
    public void testRemoveFileStructure(String buildFileStructure, String removeFileStructure) {
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        System.out.println(removeFileStructure);
        String[] tokens = Utilities.filterInput(buildFileStructure);
        compiler.executeDML(InputType.BUILD_FILE_STRUCTURE, tokens, tables, users);
        tokens = Utilities.filterInput(removeFileStructure);
        compiler.executeDML(InputType.REMOVE_FILE_STRUCTURE, tokens, tables, users);
        tables.stream()
                .filter(table -> table.getTableName().equalsIgnoreCase("Customers"))
                .forEach(table -> {
                    System.out.println(table.getTableName() + " clustered with " + table.getClusteredWithTableName());
                    table.getColumns()
                            .forEach(col -> System.out.println(col + " " + col.getFileStructure()));
                });
    }

    @Test
    public void testGrant() {
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        String input = "GRANT ALTER, DELETE, INDEX ON Customers TO Jango";
        System.out.println(input);
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.GRANT, tokens, tables, users);
        users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase("Jango"))
                .forEach(System.out::println);

        input = "GRANT ALTER, UPDATE(CustomerID, FirstName, LastName) ON Customers TO Jango";
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.GRANT, tokens, tables, users);
        users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase("Jango"))
                .forEach(System.out::println);

        input = "GRANT SELECT, UPDATE(CustomerID, FirstName, LastName) ON Customers TO Jango WITH GRANT OPTION";
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.GRANT, tokens, tables, users);
        users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase("Jango"))
                .forEach(System.out::println);

        input = "GRANT ALL PRIVILEGES ON Customers TO Jango";
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.GRANT, tokens, tables, users);
        users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase("Jango"))
                .forEach(System.out::println);
    }

    @Test
    public void testRevoke() {
        List<Table> tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES), false);
        String input = "GRANT ALL PRIVILEGES ON Customers TO Jango WITH GRANT OPTION";
        String[] tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.GRANT, tokens, tables, users);
        users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase("Jango"))
                .forEach(System.out::println);

        input = "REVOKE ALTER, DELETE, INDEX ON Customers FROM Jango";
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.REVOKE, tokens, tables, users);
        users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase("Jango"))
                .forEach(System.out::println);

        input = "REVOKE SELECT, REFERENCES(CustomerID, FirstName) ON Customers FROM Jango";
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.REVOKE, tokens, tables, users);
        users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase("Jango"))
                .forEach(System.out::println);

        input = "REVOKE ALL PRIVILEGES ON Customers FROM Jango";
        System.out.println(input);
        tokens = Utilities.filterInput(input);
        compiler.executeDML(InputType.REVOKE, tokens, tables, users);
        users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase("Jango"))
                .forEach(System.out::println);
    }
}