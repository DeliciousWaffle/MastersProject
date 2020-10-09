package test.systemcatalog.components;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.Parser;
import utilities.Utilities;
import enums.InputType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ensuring that the System Catalog's Parser is operating as it should be.
 * This will focus on whether the input is syntactically correct as well as other stuff.
 * Note that none of the commands end with a ";", this gets automatically removed when retrieving
 * input from the terminal and is unnecessary here.
 */
class ParserTest {

    private static Parser parser;

    @BeforeAll
    public static void init() {
        parser = new Parser();
    }

    // QUERY -----------------------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT Col1 FROM Tab1",
            "SELECT * FROM Tab1",
            "SELECT MIN(Col1) FROM Tab1",
            "SELECT MAX(Col1) FROM Tab1",
            "SELECT AVG(Col1) FROM Tab1",
            "SELECT COUNT(Col1) FROM Tab1",
            "SELECT SUM(Col1) FROM Tab1",
            "SELECT Col1, MIN(Col2), MAX(Col3) FROM Tab1",
            "SELECT Col1, Col2 FROM Tab1",
            "SELECT Col1, Col2, Col3 FROM Tab1",
            "SELECT Col1 FROM Tab1, Tab2",
            "SELECT Col1 FROM Tab1, Tab2, Tab3",
            "SELECT Col1 FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 = Tab2.Col1",
            "SELECT Col1 FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 = Tab2.Col1 INNER JOIN Tab3 ON Tab2.Col2 = Tab3.Col2",
            "SELECT Col1 FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 != Tab2.Col1 INNER JOIN Tab3 ON Tab2.Col2 > Tab3.Col2 INNER JOIN Tab4 ON Tab3.Col3 = Tab4.Col3",
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 = \"Blah\"",
            "SELECT Col1 FROM Tab1 WHERE Col1 != 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 > 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 < 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 >= 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 <= 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1 AND Col2 > 2",
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1 AND Col2 > 2 AND Col3 = \"Blah\"",
            "SELECT a FROM b, c WHERE d = 1",
            "SELECT a FROM b, c WHERE d = 1 AND d != 2",
            "SELECT a FROM b JOIN c USING(d) WHERE e = 1",
            "SELECT a FROM b JOIN c USING(d) WHERE e = 1 OR d <= 2",
            "  SELECT    a FROM b",
            "SELECT MIN (    a) FROM b",
            "SELECT    SUM (a    ) FROM    b  WHERE c    != 1",
            "SELECT a FROM b  ,   c",
            "SELECT a FROM b   JOIN    c USING   (d) JOIN e   USING  (  f) JOIN g USING    (h  )",
            "SELECT   AVG (a  )   FROM b WHERE e =    1    AND d != 2",
            "\t SELECT a FROM\t b",
            "\t SELECT a \n FROM \tb",
            "\n\t SELECT a \t FROM \n b",
            "select a from b where c = 1",
            "SeLecT A fRoM B WhERe c = 1"
    })
    void testValidQueries(String query) {
        String[] filtered = Utilities.filterInput(query);
        assertTrue(parser.isValid(InputType.QUERY, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SLECT a FROM b",
            "SE LECT a FROM b",
            "SELECT a",
            "SELECT a b FROM c",
            "SELECT *, a FROM b",
            "SELECT a, MIN(b) FROM c",
            "SELECT a, a FROM b",
            "SELECT MIN(MIN(b))",
            "SELECT AVG(MIN) FROM b",
            "SELECT a FROM COUNT",
            "SELECT , FROM b",
            "SELECT a FROM b, b",
            "SELECT a FROM b JOIN b USING(c)",
            "SELECT a FROM b JOIN c",
            "SELECT a FROM b, c JOIN d USING(e)",
            "SELECT a FROM b, c, d JOIN e USING(f)",
            "SELECT a FROM b JOIN c USING(d), f",
            "SELECT a FROM b JOIN c USING(d) JOIN e USING(f) JOIN g USING(h), i, j",
            "SELECT a FROM b WHERE c",
            "SELECT a FROM b WHERE c == 1",
            "SELECT a FROM b WHERE c ! = 1",
            "SELECT a FROM b WHERE c > 1 AND OR d = 1",
            "SELECT a FROM b JOIN c USING(d) WHERE e =! 1",
            "SELECT a FROM b JOIN c USING(d) WHERE e = 1 OR d => 2",
            "SE\tLECT a FROM b",
            "SE\nLECT a FROM b",
            "SELECT a\nb FROM c",
            "SELECT a FROM b WHERE c < NotANumber",
            "SELECT a FROM b WHERE c >= NotANumber OR d <= NotANumber AND e < NotANumber"
    })
    void testInvalidQueries(String query) {
        String[] filtered = Utilities.filterInput(query);
        assertFalse(parser.isValid(InputType.QUERY, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // CREATE TABLE command --------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "CREATE TABLE table1(col1 NUMBER(1))",
            "CREATE TABLE table1(col1 NUMBER(1), col2 CHAR(1), col3 CHAR(1), col4 NUMBER(1))",
            "   CREATE TABLE    table1(  col1 NUMBER(1), col2   CHAR(1))",
            "\tCREATE TABLE\n    table1( \n col1 NUMBER(1), col2   CHAR(1))",
            "CREATE\n TABLE table1(col1 \nNUMBER(1), col2 \tCHAR(1))",
            "CREATE TABLE table1(col1 CHAR(1), col2 CHAR(1), col3 NUMBER(1))",
            "create table table1(col1 number(1))",
            "CreAtE TaBLE tAbLe1(CoL1 NUmbeR(1))"
    })
    void testValidCreateTableCommands(String createTable) {
        String[] filtered = Utilities.filterInput(createTable);
        assertTrue(parser.isValid(InputType.CREATE_TABLE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CREATE TABLE TABLE(col1 NUMBER(1))",
            "CREATE TABLE table1(col1 CHAR(NUMBER))",
            "CREATE",
            "CREA TE TABLE table1(col1 CHAR(1))",
            "CREATE TABLE table1(col1 CHAR(NotAChar))",
            "CREATE TABLE ta ble1(col1 CHAR(1))",
            "CREATE TABLE table1(col1, col2, col3)",
            "CREATE TABLE table1(col1 CHAR(1), col1 CHAR(1))",
            "CREAT\tE TABLE table1(col1 CHAR(1))",
            "CREAT\nE TABLE table1(col1 CHAR(1))",
            "CREATE TABLE table1()",
            "CREATE TABLE table1(col1 NUMBER(1), )"
    })
    void testInvalidCreateTableCommands(String createTable) {
        String[] filtered = Utilities.filterInput(createTable);
        assertFalse(parser.isValid(InputType.CREATE_TABLE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // DROP TABLE command ----------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE table1",
            "DROP table table1",
            "DROP   table    table1",
            "DroP \t\n taBLe table1",
            "\n\tdrop table table1"
    })
    void testValidDropTableCommands(String dropTable) {
        String[] filtered = Utilities.filterInput(dropTable);
        assertTrue(parser.isValid(InputType.DROP_TABLE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE table",
            "DROP TABLE",
            "DR OP TABLE table1",
            "D\tROP TABLE table1",
            "DROP TABLE ta\nble1",
            "DROP"
    })
    void testInvalidDropTableCommands(String dropTable) {
        String[] filtered = Utilities.filterInput(dropTable);
        assertFalse(parser.isValid(InputType.DROP_TABLE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // ALTER TABLE command ---------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {

    })
    void testValidAlterTableCommand(String alterTable) {
        String[] filtered = Utilities.filterInput(alterTable);
        assertTrue(parser.isValid(InputType.ALTER_TABLE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {

    })
    void testInvalidAlterTableCommand(String alterTable) {
        String[] filtered = Utilities.filterInput(alterTable);
        assertFalse(parser.isValid(InputType.ALTER_TABLE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // INSERT command --------------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "INSERT INTO table1 VALUES(1)",
            "INSERT INTO table1 VALUES(1, 2, 3, something)",
            "INSERT INTO table1 VALUES(something, 1, 2, 3)",
            "INSERT INTO table1 VALUES(1, something, 1, something)",
            "inSErT iNto tabLE1 VaLUes(1, 2, 3)",
            "  INSERT    INTO table1 VALUES   (1  )",
            "\tINSERT INTO table1 VALUES\n(1)",
    })
    void testValidInsertCommands(String insert) {
        String[] filtered = Utilities.filterInput(insert);
        assertTrue(parser.isValid(InputType.INSERT, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INSER INTO table1 VALUES(1)",
            "INSERT INTO INSERT VALUES(1)",
            "INSERT INTO table1 VALVES(1, 3)",
            "INSERT IN TO table1 VALUES(1)",
            "INSERT INTO TABLE VALUES(1)",
            "INSERT INTO table1 VALUES(MIN)",
            "IN SERT INTO table1 VALUES(1)",
            "I\tNSERT INTO table1 VALUES(1)",
            "INSERT IN\nTO table1 VALUE(something)"
    })
    void testInvalidInsertCommands(String insert) {
        String[] filtered = Utilities.filterInput(insert);
        assertFalse(parser.isValid(InputType.INSERT, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // DELETE command --------------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE FROM table1 WHERE col1 = 1",
            "DELETE FROM table1 WHERE col1 != something",
            "DELETE   FROM  table1   WHERE col1   >= 1",
            "\nDELETE FROM table1 \nWHERE col1 > 1",
            "DELETE \tFROM table1\tWHERE col1 < 1",
            "DeLeTE FRoM TabLe1 WHeRe cOl1 = 1",
    })
    void testValidDeleteCommands(String delete) {
        String[] filtered = Utilities.filterInput(delete);
        assertTrue(parser.isValid(InputType.DELETE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE",
            "DELTE FROM table1 WHERE col1 = 1",
            "DELETE FROM TABLE WHERE col1 = 1",
            "DELETE FROM table1 WHERE MAX = 1",
            "DELETE FROM table1 WHE\nRE col1 = 1",
            "DELETE FROM tab\tle1 WHERE col1 = 1",
            "DELETE FROM table1 WHERE col1 > NotANumber"
    })
    void testInvalidDeleteCommands(String delete) {
        String[] filtered = Utilities.filterInput(delete);
        assertFalse(parser.isValid(InputType.DELETE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // UPDATE command --------------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "UPDATE table1 SET col1 = 1",
            "UPDATE table1 SET col1 = 1 WHERE col2 = 2",
            "UPDATE table1 SET col1 = blah WHERE col2 = blah",
            "update table1 set col1 = 1 where col2 = 2",
            "  upDAte  TaBle1 SeT cOL1 = 1 WhERE coL2 = blah",
            "UPDATE \ntable1 SET \ncol1 = 1",
            "\tUPDATE table1\t SET col1 = 1 WHERE col2 = blah",
    })
    void testValidUpdateCommands(String update) {
        String[] filtered = Utilities.filterInput(update);
        assertTrue(parser.isValid(InputType.UPDATE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UPDATE",
            "UPDATE table1 SWET col1 = 1",
            "UPDATE table1 SET col1 = 1 WHERE col2 = GRANT",
            "UPDATE TABLE SET col1 = 1",
            "UPDATE table1 SET col 1 = 1",
            "UPDATE tabl\te1 SET col1 = 1 WHERE col2 = blah",
            "UPDATE table1 SE\nT col1 = 1 WHERE col2 = 1",
    })
    void testInValidUpdateCommands(String update) {
        String[] filtered = Utilities.filterInput(update);
        assertFalse(parser.isValid(InputType.UPDATE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // GRANT command ---------------------------------------------------------------------------------------------------
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
    void testValidGrantCommands(String grant) {
        String[] filtered = Utilities.filterInput(grant);
        assertTrue(parser.isValid(InputType.GRANT, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
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
    void testInvalidGrantCommands(String grant) {
        String[] filtered = Utilities.filterInput(grant);
        assertFalse(parser.isValid(InputType.GRANT, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // REVOKE command --------------------------------------------------------------------------------------------------
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
    void testValidRevokeCommands(String revoke) {
        String[] filtered = Utilities.filterInput(revoke);
        assertTrue(parser.isValid(InputType.REVOKE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
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

    void testInvalidRevokeCommands(String revoke) {
        String[] filtered = Utilities.filterInput(revoke);
        assertFalse(parser.isValid(InputType.REVOKE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // BUILD FILE STRUCTURE command ------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {

    })
    void testValidBuildFileStructureCommand(String buildFileStructure) {
        String[] filtered = Utilities.filterInput(buildFileStructure);
        assertTrue(parser.isValid(InputType.BUILD_FILE_STRUCTURE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {

    })
    void testInvalidBuildFileStructureCommand(String buildFileStructure) {
        String[] filtered = Utilities.filterInput(buildFileStructure);
        assertFalse(parser.isValid(InputType.BUILD_FILE_STRUCTURE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // REMOVE FILE STRUCTURE command -----------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {

    })
    void testValidRemoveFileStructureCommand(String removeFileStructure) {
        String[] filtered = Utilities.filterInput(removeFileStructure);
        assertTrue(parser.isValid(InputType.REMOVE_FILE_STRUCTURE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {

    })
    void testInvalidRemoveFileStructureCommand(String removeFileStructure) {
        String[] filtered = Utilities.filterInput(removeFileStructure);
        assertFalse(parser.isValid(InputType.BUILD_FILE_STRUCTURE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}