package systemcatalog.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import utilities.Utilities;

// TODO remove
class PrivilegeParserTest {

    /*private static PrivilegeParser privilegeParser;

    @BeforeAll
    public static void init() {
        privilegeParser = new PrivilegeParser();
        privilegeParser.printMatrices();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GRANT ALTER ON table1 TO user1",
            "GRANT DELETE ON table1 TO user1",
            "GRANT INDEX ON table1 TO user1",
            "GRANT INSERT ON table1 TO user1",
            "GRANT SELECT ON table1 TO user1",
            "GRANT UPDATE(col1) ON table1 TO user1",
            "GRANT REFERENCES(col1) ON table1 TO user1",
            "GRANT ALTER, DELETE ON table1 TO user1",
            "GRANT ALTER, DELETE, INDEX, INSERT, SELECT, UPDATE(col1), REFERENCES(col1) ON table1 TO user1",
            "GRANT ALTER ON table1 TO user1, user2",
            "GRANT ALTER, DELETE ON table1 TO user1, user2",
            "GRANT UPDATE(col1), REFERENCES(col1, col2, col3) ON table1 TO user1, user2",
            "GRANT UPDATE(col1, col2, col3), REFERENCES(col1, col2, col3) ON table1 to user1",
            "  GRANT    ALTER ON  table1 TO   user1",
            "GrANt   AltER oN TaBLe1 To USer1",
            "\tGRANT\tALTER ON table1 TO user1",
            "GRANT\n ALTER \nON table1 TO\tuser1"
    })
    void testValidGrantCommand(String validGrantCommand) {

        validGrantCommand = Utilities.formatInput(validGrantCommand);
        privilegeParser.setPrivilegeStatement(validGrantCommand);
        System.out.println(validGrantCommand);
        assertTrue(privilegeParser.isValid());
        System.out.println("------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GRANT",
            "GRANT ALTR ON table1 TO user1",
            "GRANT ALTER ON TABLE TO user1",
            "GRANT ALTER, ALTER ON table1 TO user1",
            "GRANT ALTER, DELETE, ALTER ON table1 TO user1",
            "GRANT ALTER ON table1 TO user1, user1",
            "GRANT UPDATE(col1, col1) ON table1 TO user1",
            "GRANT REFERENCES(col1, col1) ON table1 TO user1",
            "GRANT UPDATE(col1), REFERENCES(col1), UPDATE(col1) ON table1 TO user1",
            "GRANT DELETE, ON table1 TO user1",
            "GRANT DE\tLETE ON table1 TO user1",
            "GR\nANT ALTER ON table1 TO user1",
    })
    void testInvalidGrantCommand(String invalidGrantCommand) {

        invalidGrantCommand = Utilities.formatInput(invalidGrantCommand);
        privilegeParser.setPrivilegeStatement(invalidGrantCommand);
        System.out.println(invalidGrantCommand);
        assertFalse(privilegeParser.isValid());
        System.out.println("------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "REVOKE ALTER ON table1 FROM user1",
            "REVOKE DELETE ON table1 FROM user1",
            "REVOKE INDEX ON table1 FROM user1",
            "REVOKE INSERT ON table1 FROM user1",
            "REVOKE SELECT ON table1 FROM user1",
            "REVOKE UPDATE(col1) ON table1 FROM user1",
            "REVOKE REFERENCES(col1) ON table1 FROM user1",
            "REVOKE ALTER, DELETE ON table1 FROM user1",
            "REVOKE ALTER, DELETE, INDEX, INSERT, SELECT, UPDATE(col1), REFERENCES(col1) ON table1 FROM user1",
            "REVOKE ALTER ON table1 FROM user1, user2",
            "REVOKE ALTER, DELETE ON table1 FROM user1, user2",
            "REVOKE UPDATE(col1), REFERENCES(col1, col2, col3) ON table1 FROM user1, user2",
            "REVOKE UPDATE(col1, col2, col3), REFERENCES(col1, col2, col3) ON table1 from user1",
            "  REVOKE    ALTER ON  table1 FROM   user1",
            "RevOKE   AltER oN TaBLe1 FRoM USer1",
            "\tREVOKE\tALTER ON table1 FROM user1",
            "REVOKE\n ALTER \nON table1 FROM\tuser1"
    })
    void testValidRevokeCommand(String validRevokeCommand) {

        validRevokeCommand = Utilities.formatInput(validRevokeCommand);
        privilegeParser.setPrivilegeStatement(validRevokeCommand);
        System.out.println(validRevokeCommand);
        assertTrue(privilegeParser.isValid());
        System.out.println("------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "REVOKE",
            "REVOKE ALTR ON table1 FROM user1",
            "REVOKE ALTER ON TABLE FROM user1",
            "REVOKE ALTER, ALTER ON table1 FROM user1",
            "REVOKE ALTER, DELETE, ALTER ON table1 FROM user1",
            "REVOKE ALTER ON table1 FROM user1, user1",
            "REVOKE UPDATE(col1, col1) ON table1 FROM user1",
            "REVOKE REFERENCES(col1, col1) ON table1 FROM user1",
            "REVOKE UPDATE(col1), REFERENCES(col1), UPDATE(col1) ON table1 FROM user1",
            "REVOKE DELETE, ON table1 FROM user1",
            "REVOKE DE\tLETE ON table1 FROM user1",
            "REV\nOKE ALTER ON table1 FROM user1",
    })

    void testInvalidRevokeCommand(String invalidRevokeCommand) {

        invalidRevokeCommand = Utilities.formatInput(invalidRevokeCommand);
        privilegeParser.setPrivilegeStatement(invalidRevokeCommand);
        System.out.println(invalidRevokeCommand);
        assertFalse(privilegeParser.isValid());
        System.out.println("------------------------------------------------");
    }*/
}