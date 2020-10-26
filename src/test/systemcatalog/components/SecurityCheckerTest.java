package test.systemcatalog.components;

import datastructures.relation.table.Table;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.user.User;
import enums.InputType;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.SecurityChecker;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ensuring that the System Catalog's Security Checker is operating as it should be.
 * This will focus on a user with a set of privileges and passable privileges executing commands
 * to see whether those commands are successfully executed.
 */
public class SecurityCheckerTest {

    private static SecurityChecker securityChecker;
    private static List<Table> tables;
    private static List<User> users;
    private static User dba, noPrivilegeGuy;

    @BeforeAll
    static void init() {
        securityChecker = new SecurityChecker();
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES));
        users = Serializer.unSerializeUsers(IO.readOriginalData(FileType.OriginalData.ORIGINAL_USERS));
        dba = User.DatabaseAdministrator(tables);
        noPrivilegeGuy = new User("No Privilege Guy", new ArrayList<>(), new ArrayList<>());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT CustomerID FROM Customers", // 1 table
            "SELECT * FROM Customers, CustomerPurchaseDetails, Employees" // multiple tables
    })
    void testValidQuery(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        boolean isValid = securityChecker.isValid(InputType.QUERY, filtered, dba, tables);
        System.out.println("Error Code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT CustomerID FROM Customers", // 1 table
            "SELECT * FROM Customers, CustomerPurchaseDetails, Employees" // multiple tables
    })
    void testInvalidQuery(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        boolean isValid = securityChecker.isValid(InputType.QUERY, filtered, noPrivilegeGuy, tables);
        System.out.println("Error Code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CREATE TABLE Blah(Col1 DATE, Col2 CHAR(3), NUMBER(4, 2))"
    })
    void validCreateTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.CREATE_TABLE, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // create table will always return true

    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE Customers"
    })
    void validDropTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.DROP_TABLE, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE Customers"
    })
    void invalidDropTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.DROP_TABLE, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ALTER TABLE Customers ADD Col1 DATE",
            "ALTER TABLE Customers ADD FOREIGN KEY CustomerPurchaseDetails.CustomerID" // checking for references privilege
    })
    void validAlterTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.ALTER_TABLE, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ALTER TABLE Customers ADD Col1 DATE",
            "ALTER TABLE Customers ADD FOREIGN KEY CustomerPurchaseDetails.CustomerID" // checking for references privilege
    })
    void invalidAlterTable(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.ALTER_TABLE, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INSERT INTO Customers VALUES(1)",
            "INSERT INTO Customers VALUES(1, \"Blah\", \"Blah\")"
    })
    void validInsertCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.INSERT, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INSERT INTO Customers VALUES(1)",
            "INSERT INTO Customers VALUES(1, \"Blah\", \"Blah\")"
    })
    void invalidInsertCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.INSERT, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
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
        boolean isValid = securityChecker.isValid(InputType.DELETE, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE FROM Customers WHERE CustomerID = 1"
    })
    void invalidDeleteCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.DELETE, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UPDATE Customers SET FirstName = \"Blah\" WHERE CustomerID = 1"
    })
    void validUpdateCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.UPDATE, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        System.out.println(RuleGraphTypes.getUpdateRuleGraph());
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UPDATE Customers SET FirstName = \"Blah\" WHERE CustomerID = 1"
    })
    void invalidUpdateCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.UPDATE, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
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
        boolean isValid = securityChecker.isValid(InputType.GRANT, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
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
    void invalidGrantCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.GRANT, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
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
        boolean isValid = securityChecker.isValid(InputType.REVOKE, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
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
    void invalidRevokeCommand(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.REVOKE, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
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
        boolean isValid = securityChecker.isValid(InputType.BUILD_FILE_STRUCTURE, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "BUILD HASH TABLE ON CustomerID IN Customers", // normal
            "BUILD CLUSTERED FILE ON Customers AND CustomerPurchaseDetails" // clustered files
    })
    void invalidBuildFileStructure(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.BUILD_FILE_STRUCTURE, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
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
        boolean isValid = securityChecker.isValid(InputType.REMOVE_FILE_STRUCTURE, filtered, dba, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "REMOVE FILE STRUCTURE ON CustomerID IN Customers",
            "REMOVE CLUSTERED FILE ON Customers AND CustomerPurchaseDetails"
    })
    void invalidRemoveFileStructure(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = securityChecker.isValid(InputType.REMOVE_FILE_STRUCTURE, filtered, noPrivilegeGuy, tables);
        System.out.println("Error code: " + securityChecker.getErrorMessage());
        securityChecker.resetErrorMessage();
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}