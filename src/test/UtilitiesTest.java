package test;

import datastructures.user.TablePrivileges;
import datastructures.user.User;
import org.junit.jupiter.api.Test;
import utilities.IO;
import utilities.Utilities;
import utilities.enums.FileName;
import utilities.enums.Privilege;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
// TODO figure out why table3 is not being set for bob
// TODO add a way to print out user data too
class UtilitiesTest {

    ArrayList<TablePrivileges> tablePrivilegesList;
    ArrayList<TablePrivileges> passableTablePrivilegesList;
    TablePrivileges tablePrivileges;
    ArrayList<String> referenceColumns;
    ArrayList<String> updateColumns;

    private void clearData() {
        tablePrivileges = new TablePrivileges();
        referenceColumns = new ArrayList<>();
        updateColumns = new ArrayList<>();
    }

    private void clearLists() {
        tablePrivilegesList = new ArrayList<>();
        passableTablePrivilegesList = new ArrayList<>();
    }

    @Test
    public void testCreateUserData() {

        ArrayList<User> expected = new ArrayList<>();

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

        expected.add(bob);
        expected.add(john);
        expected.add(dan);
        expected.add(sally);
//TODO
        //for(User user : expected) {
        //    System.out.println(user);
        //}
        String userData = IO.readData(FileName.USERS);
        ArrayList<User> actual = Utilities.unSerializeUserData(userData);

        System.out.println("\n\n");
        // TODO
        for(User user : actual) {
            System.out.println(user);
        }
        assertEquals(expected.size(), actual.size());
        System.out.println("Expected Users size: " + expected.size() + "\nActual Users Size: " + actual.size());

        for(int i = 0; i < expected.size(); i++) {

            String expectedName = expected.get(i).getUsername();
            String actualName = actual.get(i).getUsername();
            assertEquals(expectedName, actualName);
            System.out.println("Expected Name: " + expectedName + "\nActual Name: " + actualName);

            ArrayList<TablePrivileges> expectedTablePrivilegesList = expected.get(i).getTablePrivilegesList();
            ArrayList<TablePrivileges> actualTablePrivilegesList = actual.get(i).getTablePrivilegesList();

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

            ArrayList<TablePrivileges> expectedPassableTablePrivilegesList = expected.get(i).getPassableTablePrivilegesList();
            ArrayList<TablePrivileges> actualPassableTablePrivilegesList = actual.get(i).getPassableTablePrivilegesList();

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
}