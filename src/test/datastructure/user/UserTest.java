package test.datastructure.user;

import datastructures.relation.table.Table;
import datastructures.user.User;
import datastructures.user.component.Privilege;
import datastructures.user.component.TablePrivileges;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserTest {

    private static List<Table> tables;

    @BeforeAll
    static void init() {
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES));
    }

    @Test
    public void testGrant1() {
        User bob = new User("Bob", new ArrayList<>(), new ArrayList<>());
        List<TablePrivileges> tablePrivileges = Arrays.asList(
                new TablePrivileges("Customers", Arrays.asList(
                        Privilege.ALTER,
                        Privilege.DELETE,
                        Privilege.INDEX,
                        Privilege.INDEX
                ))
        );
    }

}