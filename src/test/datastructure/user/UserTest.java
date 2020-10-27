package test.datastructure.user;

import datastructures.relation.table.Table;
import datastructures.user.User;
import datastructures.user.component.Privilege;
import datastructures.user.component.TablePrivileges;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    static List<Table> tables;
    static User bob;

    @BeforeAll
    static void init() {
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES));
        bob = Serializer.unSerializeUsers(IO.readOriginalData(FileType.OriginalData.ORIGINAL_USERS)).get(0);
        System.out.println(bob);
    }

    @BeforeEach
    void reset() {
        bob = Serializer.unSerializeUsers(IO.readOriginalData(FileType.OriginalData.ORIGINAL_USERS)).get(0);
    }

    @Test
    void testGrantTablePrivileges() {
        bob.addTablePrivileges(new TablePrivileges("Blah",
                Arrays.asList(Privilege.INDEX, Privilege.REFERENCES, Privilege.DELETE),
                new ArrayList<>(),
                Arrays.asList("Col1", "Col2", "Col3")));
        assertTrue(bob.hasPrivilegeOnTable("Blah", Privilege.INDEX));
        assertTrue(bob.hasReferencesColumnsOnTable("Blah", Arrays.asList("Col1", "Col2")));
    }

    @Test
    void testGrantTablePrivileges2() {
        System.out.println("testGrantTablePrivileges2()");
        bob.addTablePrivileges(new TablePrivileges("Blah",
                new ArrayList<>(Arrays.asList(Privilege.INDEX, Privilege.REFERENCES, Privilege.DELETE)),
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("Col1", "Col2", "Col3"))));
        System.out.println("Before:\n" + bob);
        bob.addTablePrivileges(new TablePrivileges("Blah",
                new ArrayList<>(Arrays.asList(Privilege.INDEX, Privilege.DELETE, Privilege.ALTER)),
                new ArrayList<>(Arrays.asList("Col1", "Col2")),
                new ArrayList<>(Arrays.asList("Col2", "Col3", "Col4"))));
        System.out.println("\nAfter:\n" + bob);
    }

    @Test
    void testGrantAllTablePrivileges() {
        User billy = new User("Billy", new ArrayList<>(), new ArrayList<>());
        TablePrivileges tablePrivileges = new TablePrivileges();
        tablePrivileges.grantAllPrivileges(tables.get(0));
        billy.addTablePrivileges(tablePrivileges);
        assertTrue(billy.hasAllTablePrivileges(tables.get(0)));
    }

    @Test
    void testRevokeTablePrivileges() {
        bob.revokeTablePrivilegesAndGrantedTablePrivileges(new TablePrivileges(
                "Customers",
                new ArrayList<>(Arrays.asList(Privilege.INDEX, Privilege.UPDATE, Privilege.UPDATE))
        ));
        System.out.println(bob);
    }

    @Test
    void testRevokeAllTablePrivileges() {
        System.out.println("testRevokeAllTablePrivileges()");
        System.out.println("Before:");
        System.out.println(bob);
        bob.revokeAllTablePrivilegesAndGrantedTablePrivileges(tables.get(0).getTableName());
        System.out.println("\nAfter:\n" + bob);
    }

}