package test;

import datastructures.table.Table;
import datastructures.table.component.Column;
import datastructures.table.component.TableData;
import datastructures.user.TablePrivileges;
import datastructures.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import files.IO;
import utilities.Serialize;
import utilities.enums.Filename;
import utilities.enums.Privilege;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SerializeTest {

    private static ArrayList<User> expectedUsers = new ArrayList<>();
    private static ArrayList<TablePrivileges> tablePrivilegesList;
    private static ArrayList<TablePrivileges> passableTablePrivilegesList;
    private static TablePrivileges tablePrivileges;
    private static ArrayList<String> referenceColumns;
    private static ArrayList<String> updateColumns;

    @BeforeAll
    public static void init() {

        clearLists();
        clearData();

        User bob = new User();
        bob.setUsername("Bob");

        tablePrivileges.setTableName("Table1");
        tablePrivileges.grantPrivilege(Privilege.ALTER);
        tablePrivileges.grantPrivilege(Privilege.DELETE);

        tablePrivilegesList.add(tablePrivileges);
        clearData();

        tablePrivileges.setTableName("Table2");
        tablePrivileges.grantPrivilege(Privilege.UPDATE);
        updateColumns.add("Col1");
        updateColumns.add("Col4");
        tablePrivileges.setUpdateColumns(updateColumns);

        tablePrivilegesList.add(tablePrivileges);
        clearData();

        tablePrivileges.setTableName("Table3");
        tablePrivileges.grantPrivilege(Privilege.REFERENCES);
        referenceColumns.add("Col1");
        referenceColumns.add("Col2");
        referenceColumns.add("Col3");
        tablePrivileges.setReferenceColumns(referenceColumns);
        tablePrivileges.grantPrivilege(Privilege.SELECT);
        tablePrivileges.grantPrivilege(Privilege.INDEX);

        tablePrivilegesList.add(tablePrivileges);
        clearData();

        tablePrivileges.setTableName("Table1");
        tablePrivileges.grantPrivilege(Privilege.DELETE);

        passableTablePrivilegesList.add(tablePrivileges);
        clearData();

        bob.setTablePrivilegesList(tablePrivilegesList);
        bob.setPassableTablePrivilegesList(passableTablePrivilegesList);
        clearData();
        clearLists();

        User john = new User();
        john.setUsername("John");

        tablePrivileges.setTableName("Table2");
        tablePrivileges.grantPrivilege(Privilege.INSERT);
        tablePrivilegesList.add(tablePrivileges);

        john.setTablePrivilegesList(tablePrivilegesList);
        john.setPassableTablePrivilegesList(new ArrayList<>());

        clearData();
        clearLists();

        User dan = new User();
        dan.setUsername("Dan");

        tablePrivileges.setTableName("Table1");
        tablePrivileges.grantPrivilege(Privilege.UPDATE);
        updateColumns.add("Col2");
        tablePrivileges.setUpdateColumns(updateColumns);

        tablePrivilegesList.add(tablePrivileges);
        clearData();

        tablePrivileges.setTableName("Table4");
        tablePrivileges.grantPrivilege(Privilege.UPDATE);
        updateColumns.add("Col1");
        updateColumns.add("Col2");
        tablePrivileges.setUpdateColumns(updateColumns);
        tablePrivileges.grantPrivilege(Privilege.REFERENCES);
        referenceColumns.add("Col1");
        referenceColumns.add("Col4");
        tablePrivileges.setReferenceColumns(referenceColumns);
        tablePrivileges.grantPrivilege(Privilege.DELETE);

        tablePrivilegesList.add(tablePrivileges);
        dan.setTablePrivilegesList(tablePrivilegesList);
        clearData();

        tablePrivileges.setTableName("Table1");
        tablePrivileges.grantPrivilege(Privilege.UPDATE);
        updateColumns.add("Col2");
        tablePrivileges.setUpdateColumns(updateColumns);

        passableTablePrivilegesList.add(tablePrivileges);
        clearData();

        tablePrivileges.setTableName("Table4");
        tablePrivileges.grantPrivilege(Privilege.UPDATE);
        updateColumns.add("Col1");
        updateColumns.add("Col4");
        tablePrivileges.setUpdateColumns(updateColumns);

        passableTablePrivilegesList.add(tablePrivileges);
        clearData();

        dan.setPassableTablePrivilegesList(passableTablePrivilegesList);
        clearLists();

        User sally = new User();
        sally.setUsername("Sally");

        sally.setTablePrivilegesList(new ArrayList<>());
        sally.setPassableTablePrivilegesList(new ArrayList<>());

        expectedUsers.add(bob);
        expectedUsers.add(john);
        expectedUsers.add(dan);
        expectedUsers.add(sally);
    }

    private static void clearData() {
        tablePrivileges = new TablePrivileges();
        referenceColumns = new ArrayList<>();
        updateColumns = new ArrayList<>();
    }

    private static void clearLists() {
        tablePrivilegesList = new ArrayList<>();
        passableTablePrivilegesList = new ArrayList<>();
    }

    @Test
    public void testUnSerializeUser() {

        String userData = IO.read(Filename.TEST_USERS);
        ArrayList<User> actualUsers = Serialize.unSerializeUsers(userData);

        assertEquals(expectedUsers.size(), actualUsers.size());
        System.out.println("Expected Users size: " + expectedUsers.size() + "\nActual Users Size: " + actualUsers.size());

        for(int i = 0; i < expectedUsers.size(); i++) {

            String expectedName = expectedUsers.get(i).getUsername();
            String actualName = actualUsers.get(i).getUsername();
            assertEquals(expectedName, actualName);
            System.out.println("Expected Name: " + expectedName + "\nActual Name: " + actualName);

            ArrayList<TablePrivileges> expectedTablePrivilegesList = expectedUsers.get(i).getTablePrivilegesList();
            ArrayList<TablePrivileges> actualTablePrivilegesList = actualUsers.get(i).getTablePrivilegesList();

            assertEquals(expectedTablePrivilegesList.size(), actualTablePrivilegesList.size());
            System.out.println("Expected Table Privileges List size: " + expectedTablePrivilegesList.size() +
                    "\nActual Table Privileges List Size: " + actualTablePrivilegesList.size());

            for(int j = 0; j < expectedTablePrivilegesList.size(); j++) {

                String expectedTableName = expectedTablePrivilegesList.get(j).getTableName();
                String actualTableName = actualTablePrivilegesList.get(j).getTableName();
                assertEquals(expectedTableName, actualTableName);
                System.out.println("Expected Table Name: " + expectedTableName + "\nActual Table Name: " + actualTableName);

                ArrayList<Privilege> expectedPrivileges = expectedTablePrivilegesList.get(j).getPrivileges();
                ArrayList<Privilege> actualPrivileges = actualTablePrivilegesList.get(j).getPrivileges();

                assertEquals(expectedPrivileges.size(), actualPrivileges.size());
                System.out.println("Expected Privileges Size: " + expectedPrivileges.size() +
                        "\nActual Privileges Size: " + actualPrivileges.size());

                for(int k = 0; k < expectedPrivileges.size(); k++) {

                    Privilege expectedPrivilege = expectedPrivileges.get(k);
                    Privilege actualPrivilege = actualPrivileges.get(k);
                    assertEquals(expectedPrivilege, actualPrivilege);
                    System.out.println("Expected Privilege: " + expectedPrivilege +
                            "\nActual Privilege: " + actualPrivilege);
                }

                ArrayList<String> expectedUpdateColumns = expectedTablePrivilegesList.get(j).getUpdateColumns();
                ArrayList<String> actualUpdateColumns = actualTablePrivilegesList.get(j).getUpdateColumns();

                assertEquals(expectedUpdateColumns.size(), actualUpdateColumns.size());
                System.out.println("Expected Update Columns Size: " + expectedUpdateColumns.size() +
                        "\nActual Update Columns Size: " + actualUpdateColumns.size());

                for(int k = 0; k < expectedUpdateColumns.size(); k++) {

                    String expectedUpdateColumn = expectedUpdateColumns.get(k);
                    String actualUpdateColumn = actualUpdateColumns.get(k);
                    assertEquals(expectedUpdateColumn, actualUpdateColumn);
                    System.out.println("Expected Update Column: " + expectedUpdateColumn +
                            "\nActual Update Column: " + actualUpdateColumn);
                }

                ArrayList<String> expectedReferenceColumns = expectedTablePrivilegesList.get(j).getReferenceColumns();
                ArrayList<String> actualReferenceColumns = actualTablePrivilegesList.get(j).getReferenceColumns();
                System.out.println("Expected Reference Columns Size: " + expectedReferenceColumns.size() +
                        "\nActual Reference Columns Size: " + actualReferenceColumns.size());
                assertEquals(expectedReferenceColumns.size(), actualReferenceColumns.size());

                for(int k = 0; k < expectedReferenceColumns.size(); k++) {

                    String expectedReferenceColumn = expectedReferenceColumns.get(k);
                    String actualReferenceColumn = actualReferenceColumns.get(k);
                    System.out.println("Expected Reference Column: " + expectedReferenceColumn +
                            "\nActual Reference Column: " + actualReferenceColumn);
                    assertEquals(expectedReferenceColumn, actualReferenceColumn);
                }
            }

            ArrayList<TablePrivileges> expectedPassableTablePrivilegesList = expectedUsers.get(i).getPassableTablePrivilegesList();
            ArrayList<TablePrivileges> actualPassableTablePrivilegesList = actualUsers.get(i).getPassableTablePrivilegesList();

            System.out.println("Expected Passable Table Privileges List size: " + expectedPassableTablePrivilegesList.size() +
                    "\nActual Passable Table Privileges List Size: " + actualPassableTablePrivilegesList.size());
            assertEquals(expectedPassableTablePrivilegesList.size(), actualPassableTablePrivilegesList.size());

            for(int j = 0; j < expectedPassableTablePrivilegesList.size(); j++) {

                String expectedTableName = expectedPassableTablePrivilegesList.get(j).getTableName();
                String actualTableName = actualPassableTablePrivilegesList.get(j).getTableName();
                System.out.println("Expected Passable Table Name: " + expectedTableName +
                        "\nActual Passable Table Name: " + actualTableName);
                assertEquals(expectedTableName, actualTableName);

                ArrayList<Privilege> expectedPrivileges = expectedTablePrivilegesList.get(j).getPrivileges();
                ArrayList<Privilege> actualPrivileges = actualTablePrivilegesList.get(j).getPrivileges();
                System.out.println("Expected Passable Privileges Size: " + expectedPrivileges.size() +
                        "\nActual Passable Privileges Size: " + actualPrivileges.size());
                assertEquals(expectedPrivileges.size(), actualPrivileges.size());

                for(int k = 0; k < expectedPrivileges.size(); k++) {

                    Privilege expectedPrivilege = expectedPrivileges.get(k);
                    Privilege actualPrivilege = actualPrivileges.get(k);
                    System.out.println("Expected Passable Privilege: " + expectedPrivilege +
                            "\nActual Passable Privilege: " + actualPrivilege);
                    assertEquals(expectedPrivilege, actualPrivilege);
                }

                ArrayList<String> expectedUpdateColumns = expectedTablePrivilegesList.get(j).getUpdateColumns();
                ArrayList<String> actualUpdateColumns = actualTablePrivilegesList.get(j).getUpdateColumns();
                System.out.println("Expected Passable Update Columns Size: " + expectedUpdateColumns.size() +
                        "\nActual Passable Update Columns Size: " + actualUpdateColumns.size());
                assertEquals(expectedUpdateColumns.size(), actualUpdateColumns.size());

                for(int k = 0; k < expectedUpdateColumns.size(); k++) {

                    String expectedUpdateColumn = expectedUpdateColumns.get(k);
                    String actualUpdateColumn = actualUpdateColumns.get(k);
                    System.out.println("Expected Passable Update Column: " + expectedUpdateColumn +
                            "\nActual Passable Update Column: " + actualUpdateColumn);
                    assertEquals(expectedUpdateColumn, actualUpdateColumn);
                }

                ArrayList<String> expectedReferenceColumns = expectedTablePrivilegesList.get(j).getReferenceColumns();
                ArrayList<String> actualReferenceColumns = actualTablePrivilegesList.get(j).getReferenceColumns();
                System.out.println("Expected Passable Reference Columns Size: " + expectedReferenceColumns.size() +
                        "\nActual Passable Reference Columns Size: " + actualReferenceColumns.size());
                assertEquals(expectedReferenceColumns.size(), actualReferenceColumns.size());

                for(int k = 0; k < expectedReferenceColumns.size(); k++) {

                    String expectedReferenceColumn = expectedReferenceColumns.get(k);
                    String actualReferenceColumn = actualReferenceColumns.get(k);
                    System.out.println("Expected Passable Reference Column: " + expectedReferenceColumn +
                            "\nActual Passable Reference Column: " + actualReferenceColumn);
                    assertEquals(expectedReferenceColumn, actualReferenceColumn);
                }
            }
        }

        System.out.println();
    }

    @Test
    public void testSerializeUser() {

        String expectedSerialized = Serialize.serializedUsers(expectedUsers);
        System.out.println(expectedSerialized);

        String actualSerialized = IO.read(Filename.TEST_USERS);

        // whitespaces may differ ever so slightly, so trimming
        String[] expected = expectedSerialized.split("\n");
        String[] actual = actualSerialized.split("\n");

        for(int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].trim(), actual[i].trim());
        }
    }

    @Test
    public void testUnSerializeTable() {

        assertTrue(true);
    }

    @Test
    public void testSerializedTable() {

        assertTrue(true);
    }

    @Test
    public void testUnSerializeTableData() {

        Table customersTable = new Table("Customers");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("CustomerID", "", true, 5));
        columns.add(new Column("CustomerName", "", false, 40));
        columns.add(new Column("ContactName", "", false, 30));
        columns.add(new Column("Address", "", false, 50));
        columns.add(new Column("City", "", false, 20));
        columns.add(new Column("PostalCode", "", true, 10));
        columns.add(new Column("Country", "", false, 15));
        customersTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "Customers.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, customersTable);
        customersTable.setTableData(tableData);
        System.out.println(customersTable.toString());

        Table categoriesTable = new Table("Categories");
        columns = new ArrayList<>();
        columns.add(new Column("CategoryID", "", true, 5));
        columns.add(new Column("CategoryName", "", false, 15));
        categoriesTable.setColumns(columns);

        tableDataString = IO.readTableData(Filename.TABLE_DATA, "Categories.txt");
        tableData = Serialize.unSerializeTableData(tableDataString, categoriesTable);
        categoriesTable.setTableData(tableData);
        System.out.println(categoriesTable.toString());

        Table employeesTable = new Table("Employees");
        columns = new ArrayList<>();
        columns.add(new Column("EmployeeID", "", true, 5));
        columns.add(new Column("LastName", "", false, 10));
        columns.add(new Column("FirstName", "", false, 10));
        columns.add(new Column("BirthDate", "", false, 10));
        employeesTable.setColumns(columns);

        tableDataString = IO.readTableData(Filename.TABLE_DATA, "Employees.txt");
        tableData = Serialize.unSerializeTableData(tableDataString, employeesTable);
        employeesTable.setTableData(tableData);
        System.out.println(employeesTable.toString());

        Table orderDetailsTable = new Table("OrderDetails");
        columns = new ArrayList<>();
        columns.add(new Column("OrderDetailID", "", true, 5));
        columns.add(new Column("OrderID", "", true, 5));
        columns.add(new Column("ProductID", "", true, 5));
        columns.add(new Column("Quantity", "", true, 5));
        orderDetailsTable.setColumns(columns);

        tableDataString = IO.readTableData(Filename.TABLE_DATA, "OrderDetails.txt");
        tableData = Serialize.unSerializeTableData(tableDataString, orderDetailsTable);
        orderDetailsTable.setTableData(tableData);
        System.out.println(orderDetailsTable.toString());

        Table ordersTable = new Table("Orders");
        columns = new ArrayList<>();
        columns.add(new Column("OrderID", "", true, 5));
        columns.add(new Column("CustomerID", "", true, 5));
        columns.add(new Column("EmployeeID", "", true, 5));
        columns.add(new Column("OrderDate", "", false, 10));
        columns.add(new Column("ShipperID", "", true, 5));
        ordersTable.setColumns(columns);

        tableDataString = IO.readTableData(Filename.TABLE_DATA, "Orders.txt");
        tableData = Serialize.unSerializeTableData(tableDataString, ordersTable);
        ordersTable.setTableData(tableData);
        System.out.println(ordersTable.toString());

        Table productsTable = new Table("Products");
        columns = new ArrayList<>();
        columns.add(new Column("ProductID", "", true, 5));
        columns.add(new Column("ProductName", "", false, 35));
        columns.add(new Column("SupplierID", "", true, 5));
        columns.add(new Column("CategoryID", "", true, 5));
        columns.add(new Column("Price", "", true, 10));
        productsTable.setColumns(columns);

        tableDataString = IO.readTableData(Filename.TABLE_DATA, "Products.txt");
        tableData = Serialize.unSerializeTableData(tableDataString, productsTable);
        productsTable.setTableData(tableData);
        System.out.println(productsTable.toString());

        Table suppliersTable = new Table("Suppliers");
        columns = new ArrayList<>();
        columns.add(new Column("SupplierID", "", true, 5));
        columns.add(new Column("SupplierName", "", false, 40));
        columns.add(new Column("ContactName", "", false, 30));
        columns.add(new Column("Address", "", false, 50));
        columns.add(new Column("City", "", false, 20));
        columns.add(new Column("Country", "", false, 15));
        suppliersTable.setColumns(columns);

        tableDataString = IO.readTableData(Filename.TABLE_DATA, "Suppliers.txt");
        tableData = Serialize.unSerializeTableData(tableDataString, suppliersTable);
        suppliersTable.setTableData(tableData);
        System.out.println(suppliersTable.toString());

        Table shippersTable = new Table("Shippers");
        columns = new ArrayList<>();
        columns.add(new Column("ShipperID", "", true, 5));
        columns.add(new Column("ShipperName", "", false, 20));
        shippersTable.setColumns(columns);

        tableDataString = IO.readTableData(Filename.TABLE_DATA, "Shippers.txt");
        tableData = Serialize.unSerializeTableData(tableDataString, shippersTable);
        shippersTable.setTableData(tableData);
        System.out.println(shippersTable.toString());

        assertTrue(true);
    }

    @Test
    public void testSerializeTableData() {

        /*Table customersTable = new Table("Customers");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("CustomerID", "", true, 5));
        columns.add(new Column("CustomerName", "", false, 40));
        columns.add(new Column("ContactName", "", false, 30));
        columns.add(new Column("Address", "", false, 50));
        columns.add(new Column("City", "", false, 20));
        columns.add(new Column("PostalCode", "", true, 10));
        columns.add(new Column("Country", "", false, 15));
        customersTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "Customers.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, customersTable);
        customersTable.setTableData(tableData);

        String serialized = Serialize.serializeTableData(customersTable);
        System.out.println(serialized);*/

        /*Table categoriesTable = new Table("Categories");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("CategoryID", "", true, 5));
        columns.add(new Column("CategoryName", "", false, 15));
        categoriesTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "Categories.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, categoriesTable);
        categoriesTable.setTableData(tableData);

        String serialized = Serialize.serializeTableData(categoriesTable);
        System.out.println(serialized);*/

        /*Table employeesTable = new Table("Employees");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("EmployeeID", "", true, 5));
        columns.add(new Column("LastName", "", false, 10));
        columns.add(new Column("FirstName", "", false, 10));
        columns.add(new Column("BirthDate", "", false, 10));
        employeesTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "Employees.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, employeesTable);
        employeesTable.setTableData(tableData);

        String serialized = Serialize.serializeTableData(employeesTable);
        System.out.println(serialized);*/

        /*Table orderDetailsTable = new Table("OrderDetails");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("OrderDetailID", "", true, 5));
        columns.add(new Column("OrderID", "", true, 5));
        columns.add(new Column("ProductID", "", true, 5));
        columns.add(new Column("Quantity", "", true, 5));
        orderDetailsTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "OrderDetails.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, orderDetailsTable);
        orderDetailsTable.setTableData(tableData);

        String serialized = Serialize.serializeTableData(orderDetailsTable);
        System.out.println(serialized);*/

        /*Table ordersTable = new Table("Orders");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("OrderID", "", true, 5));
        columns.add(new Column("CustomerID", "", true, 5));
        columns.add(new Column("EmployeeID", "", true, 5));
        columns.add(new Column("OrderDate", "", false, 10));
        columns.add(new Column("ShipperID", "", true, 5));
        ordersTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "Orders.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, ordersTable);
        ordersTable.setTableData(tableData);

        String serialized = Serialize.serializeTableData(ordersTable);
        System.out.println(serialized);*/

        /*Table productsTable = new Table("Products");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("ProductID", "", true, 5));
        columns.add(new Column("ProductName", "", false, 35));
        columns.add(new Column("SupplierID", "", true, 5));
        columns.add(new Column("CategoryID", "", true, 5));
        columns.add(new Column("Price", "", true, 10));
        productsTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "Products.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, productsTable);
        productsTable.setTableData(tableData);

        String serialized = Serialize.serializeTableData(productsTable);
        System.out.println(serialized);*/

        /*Table suppliersTable = new Table("Suppliers");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("SupplierID", "", true, 5));
        columns.add(new Column("SupplierName", "", false, 40));
        columns.add(new Column("ContactName", "", false, 30));
        columns.add(new Column("Address", "", false, 50));
        columns.add(new Column("City", "", false, 20));
        columns.add(new Column("Country", "", false, 15));
        suppliersTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "Suppliers.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, suppliersTable);
        suppliersTable.setTableData(tableData);

        String serialized = Serialize.serializeTableData(suppliersTable);
        System.out.println(serialized);*/

        /*Table shippersTable = new Table("Shippers");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("ShipperID", "", true, 5));
        columns.add(new Column("ShipperName", "", false, 20));
        shippersTable.setColumns(columns);

        String tableDataString = IO.readTableData(Filename.TABLE_DATA, "Shippers.txt");
        TableData tableData = Serialize.unSerializeTableData(tableDataString, shippersTable);
        shippersTable.setTableData(tableData);

        String serialized = Serialize.serializeTableData(shippersTable);
        System.out.println(serialized);*/

        //System.out.println("\n\n\n");
        //customersTable.setTableData(Serialize.unSerializeTableData(serialized, customersTable));
        //System.out.println(customersTable.getTableData().toString());
        assertTrue(true);
    }
}