package test.datastructure.user.component;

import datastructures.relation.table.Table;
import datastructures.user.component.Privilege;
import datastructures.user.component.TablePrivileges;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing class used for making sure that TablePrivileges is working as it should be.
 */
public class TablePrivilegesTest {

    static List<Table> tables;

    @BeforeAll
    static void init() {
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES));
    }

    @Test
    void testGrant() {
        TablePrivileges tablePrivileges = new TablePrivileges();
        tablePrivileges.grantPrivilege(Privilege.DELETE);
        assertTrue(tablePrivileges.hasPrivilege(Privilege.DELETE));
        tablePrivileges.grantPrivilege(Privilege.UPDATE);
        assertTrue(tablePrivileges.hasPrivilege(Privilege.UPDATE));
        tablePrivileges.grantPrivileges(Arrays.asList(Privilege.ALTER, Privilege.INSERT, Privilege.REFERENCES));
        assertTrue(tablePrivileges.hasPrivilege(Privilege.INSERT));
    }

    @Test
    void testGrantAll() {
        TablePrivileges tablePrivileges = new TablePrivileges();
        Table table = tables.get(0);
        tablePrivileges.grantAllPrivileges(table);
        assertTrue(tablePrivileges.hasAllPrivileges(table));
    }

    @Test
    void testAddUpdateColumns() {
        TablePrivileges tablePrivileges = new TablePrivileges();
        tablePrivileges.grantPrivilege(Privilege.UPDATE);
        tablePrivileges.addUpdateColumns(Arrays.asList("Col1", "Col2", "Col3"));
        assertTrue(tablePrivileges.hasUpdateColumn("Col2"));
        assertFalse(tablePrivileges.hasUpdateColumn("Col4"));
    }

    @Test
    void testAddReferencesColumns() {
        TablePrivileges tablePrivileges = new TablePrivileges();
        tablePrivileges.grantPrivilege(Privilege.REFERENCES);
        tablePrivileges.addReferencesColumns(Arrays.asList("Col1", "Col2", "Col3"));
        assertTrue(tablePrivileges.hasReferencesColumn("Col2"));
        assertFalse(tablePrivileges.hasReferencesColumn("Col4"));
    }

    @Test
    void testRevoke() {
        TablePrivileges tablePrivileges = new TablePrivileges();
        Table table = tables.get(0);
        tablePrivileges.grantAllPrivileges(table);
        tablePrivileges.revokePrivilege(Privilege.ALTER);
        assertFalse(tablePrivileges.hasPrivilege(Privilege.ALTER));
    }

    @Test
    void testRevokeAll() {
        TablePrivileges tablePrivileges = new TablePrivileges();
        Table table = tables.get(0);
        tablePrivileges.grantAllPrivileges(table);
        tablePrivileges.revokeAllPrivileges();
        assertTrue(tablePrivileges.getPrivileges().isEmpty() &&
                tablePrivileges.getReferenceColumns().isEmpty() &&
                tablePrivileges.getUpdateColumns().isEmpty());
    }

    @Test
    void testRevokeWithUpdateColumns() {
        TablePrivileges tablePrivileges = new TablePrivileges();
        tablePrivileges.grantPrivilege(Privilege.UPDATE);
        tablePrivileges.addUpdateColumns(Arrays.asList("Col1", "Col2", "Col3"));
        tablePrivileges.removeUpdateColumn("Col1");
        assertFalse(tablePrivileges.hasUpdateColumn("Col1"));
    }

    @Test
    void testRevokeWithReferencesColumns() {
        TablePrivileges tablePrivileges = new TablePrivileges();
        tablePrivileges.grantPrivilege(Privilege.REFERENCES);
        tablePrivileges.addReferencesColumns(Arrays.asList("Col1", "Col2", "Col3"));
        tablePrivileges.removeReferencesColumn("Col1");
        assertFalse(tablePrivileges.hasReferencesColumn("Col1"));
    }
}