package test.systemcatalog.components;

import datastructures.rulegraph.types.RuleGraphTypes;
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
        /*System.out.println(RuleGraphTypes.getQueryRuleGraph());
        System.out.println(RuleGraphTypes.getCreateTableRuleGraph());
        System.out.println(RuleGraphTypes.getAlterTableRuleGraph());
        System.out.println(RuleGraphTypes.getDropTableRuleGraph());
        System.out.println(RuleGraphTypes.getInsertRuleGraph());
        System.out.println(RuleGraphTypes.getDeleteRuleGraph());
        System.out.println(RuleGraphTypes.getUpdateRuleGraph());
        System.out.println(RuleGraphTypes.getGrantRuleGraph());
        System.out.println(RuleGraphTypes.getRevokeRuleGraph());
        System.out.println(RuleGraphTypes.getBuildFileStructureRuleGraph());
        System.out.println(RuleGraphTypes.getRemoveFileStructureRuleGraph());*/
    }

    // QUERY -----------------------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT Col1 FROM Tab1", // simple query
            "SELECT Tab1.Col1 FROM Tab1", // simple query with column prefixed with table name
            "SELECT * FROM Tab1", // simple query using "*"
            "SELECT MIN(Col1) FROM Tab1", // following use aggregate functions
            "SELECT MAX(Col1) FROM Tab1",
            "SELECT AVG(Col1) FROM Tab1",
            "SELECT COUNT(Col1) FROM Tab1",
            "SELECT SUM(Col1) FROM Tab1",
            "SELECT MIN(Tab1.Col1) FROM Tab1", // aggregate function with column prefixed with table name
            "SELECT MIN(Col1), MAX(Col2) FROM Tab1", // two aggregate functions
            "SELECT Col1, MIN(Col2), MAX(Col3) FROM Tab1", // one column and two aggregate functions
            "SELECT Col1, Col2 FROM Tab1", // multiple columns
            "SELECT Col1, Col2, Col3 FROM Tab1",
            "SELECT Col1 FROM Tab1, Tab2", // multiple tables
            "SELECT Col1 FROM Tab1, Tab2, Tab3",
            "SELECT Col1 FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 = Tab2.Col1", // joins
            "SELECT Col1 FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 = Tab2.Col1 INNER JOIN Tab3 ON Tab2.Col2 = Tab3.Col2",
            "SELECT Col1 FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 != Tab2.Col1 INNER JOIN Tab3 ON Tab2.Col2 > Tab3.Col2 INNER JOIN Tab4 ON Tab3.Col3 = Tab4.Col3",
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1", // where clauses
            "SELECT Col1 FROM Tab1 WHERE Col1 = \"Blah\"", // where clause with a string in the predicate
            "SELECT Col1 FROM Tab1 WHERE Col1 = \"12-03-2019\"", // where clause with date value in predicate
            "SELECT Col1 FROM Tab1 WHERE Col1 != 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 > 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 < 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 >= 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 <= 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 = -1", // negative number in predicate
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1.23", // not a whole number
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1 AND Col2 > 2", // multiple predicates
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1 AND Col2 > 2 AND Col3 = \"Blah\"",
            "SELECT Col1 FROM Tab1 WHERE Tab1.Col1 = \"Blah\"", // prefixed predicate
            "SELECT Col1, Col2, Col3 FROM Tab1, Tab2, Tab3 WHERE Col1 = 4 AND Col2 < 7 AND Col3 != \"Blah\"", // multiple columns, tables, and where clause predicates
            "SELECT Col1, Col2, Col3 FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 != Tab2.Col1 INNER JOIN Tab3 ON Tab2.Col2 > Tab3.Col2 INNER JOIN Tab4 ON Tab3.Col3 = Tab4.Col3 WHERE Col1 = 4 AND Col2 < 7 AND Col3 != \"Blah\"", // multiple columns, joins, and where clause predicates
            "SELECT Col1 FROM Tab1 GROUP BY Col1", // simple group by clauses
            "SELECT Col1, SUM(Col2) FROM Tab1 GROUP BY Col1",
            "SELECT Col1, Col2, SUM(Col3) FROM Tab1 GROUP BY Col1, Col2",
            "SELECT Col1, SUM(Col2) FROM Tab1, Tab2, Tab3 GROUP BY Col1", // group by clauses mixed with other stuff
            "SELECT Col1, SUM(Col2) FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 != Tab2.Col1 INNER JOIN Tab3 ON Tab2.Col2 > Tab3.Col2 INNER JOIN Tab4 ON Tab3.Col3 = Tab4.Col3 GROUP BY Col1",
            "SELECT Col1, SUM(Col2) FROM Tab1 WHERE Col1 = 1 GROUP BY Col1",
            "SELECT Col1 FROM Tab1 GROUP BY Col1 HAVING COUNT(Col1) = \"Blah\"", // simple having clause
            "SELECT Col1, SUM(Col2) FROM Tab1 GROUP BY Col1 HAVING COUNT(Col1) = 1",
            "SELECT Col1, Col2, SUM(Col3) FROM Tab1 GROUP BY Col1, Col2 HAVING MIN(Col1) != 1",
            "SELECT Col1, SUM(Col2) FROM Tab1, Tab2, Tab3 GROUP BY Col1 HAVING MAX(Col1) = 1", // having clauses mixed with other stuff
            "SELECT Col1, SUM(Col2) FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 != Tab2.Col1 INNER JOIN Tab3 ON Tab2.Col2 > Tab3.Col2 INNER JOIN Tab4 ON Tab3.Col3 = Tab4.Col3 GROUP BY Col1 HAVING AVG(Col1) = 1",
            "SELECT Col1, SUM(Col2) FROM Tab1 WHERE Col1 = \"Blah\" GROUP BY Col1 HAVING SUM(Col1) = 1"
    })
    void testValidQueries(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        boolean isValid = parser.isValid(InputType.QUERY, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SLECT Col1 FROM Tab1", // simple misspelling
            "SE LECT Col1 FROM Tab1", // junk between words
            "SE\tLECT Col1 FROM Tab1",
            "SE\nLECT Col1 FROM Tab1",
            "SELECT Col1\nCol2 FROM Tab1",
            "SELECT 1, 2, 3 FROM Tab1", // having a number in place of a column
            "SELECT JOIN FROM Tab1", // having a reserved word in place of a column
            "SELECT Col1 FROM", // missing table
            "SELECT Col1", // missing from clause
            "SELECT Col1 Col2 FROM Tab1", // missing commas in select clause
            "SELECT *, Col1 FROM Tab1", // "*" used with columns
            "SELECT *, MAX(Col1) FROM Tab1",
            "SELECT Col1, Col1 FROM Tab1", // duplicate columns in select clause
            "SELECT MIN(MIN(Col1)) FROM Tab1", // using an aggregation within an aggregation
            "SELECT AVG(MIN) FROM Tab1", // illegal use of reserved words
            "SELECT Col1 FROM COUNT",
            "SELECT , FROM Tab1", // missing columns
            "SELECT Col1 FROM Tab1, Tab1", // duplicate tables in from clause
            "SELECT Col1 FROM Tab1 INNER JOIN Tab1 ON Tab1.Col1 = Tab1.Col2", // duplicate tables being joined
            "SELECT Col1 FROM Tab1 WHERE Col1 = \"Blah", // missing double quote
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1 OR Col2 = 2", // using or in where clause
            "SELECT Col1 FROM Tab1 WHERE Col1 = 1 Col2 = 2", // forgot the and in where clause
            "SELECT Col1 FROM Tab1 INNER JOIN Tab2 ON Tab1.Col1 = Tab2.Col1, Tab3", // combining cartesian product with joins
            "SELECT Col1 FROM Tab3, Tab1 INNER JOIN Tab2 ON Tab1.Col1 = Tab2.Col1",
            "SELECT Col1 FROM Tab1 WHERE Col1", // forgot predicate in where clause
            "SELECT Col1 FROM Tab1 WHERE Col1 == 1", // using "==" instead of "="
            "SELECT Col1 FROM Tab1 WHERE Col1 ! = 1", // bad space in where clause
            "SELECT Col1 FROM Tab1 WHERE Col1 =! 1", // mistakes in WHERE clause
            "SELECT Col1 FROM Tab1 WHERE Col1 => 1",
            "SELECT Col1 FROM Tab1 WHERE Col1 = \"1\"", // having a number in predicate as a string
            "SELECT Col1 FROM Tab1 WHERE Col1 = \"1.23\"",
            "SELECT Col1 FROM Tab1 WHERE Col1 < \"NotANumber\"", // not using a number for a comparison
            "SELECT Col1 FROM Tab1 WHERE Col1 > \"10-2-2019\"", // date is not formatted correctly
            "SELECT Col1 FROM Tab1 WHERE Col1 = abc", // having a string value as a numeric
            "SELECT COUNT(Col1) FROM Tab1 GROUP BY Col1 HAVING Col1 > 5", // forgetting aggregation type in having clause
    })
    void testInvalidQueries(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        boolean isValid = parser.isValid(InputType.QUERY, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // CREATE TABLE command --------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "CREATE TABLE Tab1(Col1 NUMBER(1))", // simple
            "CREATE TABLE Tab1(Col1 NUMBER(3, 4))", // contains a decimal
            "CREATE TABLE Tab1(Col1 NUMBER(1), Col2 CHAR(1), Col3 DATE, Col4 NUMBER(1, 2))", // longer
    })
    void testValidCreateTableCommands(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = parser.isValid(InputType.CREATE_TABLE, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CREATE TABLE TABLE(Col1 NUMBER(1))", // using reserved word table for table name
            "CREATE TABLE Tab1(Col1 CHAR(NUMBER))", // invalid number in size
            "CREATE", // missing a bunch of stuff
            "CREATE TABLE Tab1(Col1 DATE(3))", // putting a size for the date
            "CREATE TABLE Tab1(Col1 NUMBER(-1))", // putting a negative for size
            "CREATE TABLE Tab1(Col1 NUMBER(1), Col1 NUMBER(1))", // duplicate column names
            "CREATE TABLE Tab1(Col1 CHAR(StringValue))", // size is not numeric
            "CREATE TABLE Ta b1(Col1 CHAR(1))",
            "CREATE TABLE Tab1(Col1, Col2, Col3)", // forgetting data types
            "CREAT\tE TABLE table1(col1 CHAR(1))", // junk where it's not supposed to be
            "CREAT\nE TABLE table1(col1 CHAR(1))",
            "CREATE TABLE Tab1()", // missing column data
            "CREATE TABLE Tab1(Col1 NUMBER(1), )",
            "CREATE TABLE Tab1(Col1 CHAR(1.3))", // decimal value
            "CREATE TABLE Tab1(Col1 CHAR(1, 1.3))"
    })
    void testInvalidCreateTableCommands(String createTable) {
        System.out.println(createTable);
        String[] filtered = Utilities.filterInput(createTable);
        boolean isValid = parser.isValid(InputType.CREATE_TABLE, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // DROP TABLE command ----------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE Tab1", // very simple
    })
    void testValidDropTableCommands(String dropTable) {
        System.out.println(dropTable);
        String[] filtered = Utilities.filterInput(dropTable);
        boolean isValid = parser.isValid(InputType.DROP_TABLE, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE Table", // reserved word in place of table name
            "DROP TABLE", // missing table name
            "DR OP TABLE table1", // junk in between stuff
            "D\tROP TABLE table1",
            "DROP TABLE ta\nble1",
            "DROP",
            "DROP TABLE 1" // number in place of table name
    })
    void testInvalidDropTableCommands(String dropTable) {
        System.out.println(dropTable);
        String[] filtered = Utilities.filterInput(dropTable);
        boolean isValid = parser.isValid(InputType.DROP_TABLE, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // ALTER TABLE command ---------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "ALTER TABLE Tab1 MODIFY Col1 NUMBER(5)",
            "ALTER TABLE Tab1 MODIFY Col1 CHAR(5)",
            "ALTER TABLE Tab1 MODIFY Col1 DATE",
            "ALTER TABLE Tab1 ADD Col1 NUMBER(5)",
            "ALTER TABLE Tab1 ADD Col1 CHAR(5)",
            "ALTER TABLE Tab1 ADD Col1 DATE",
            "ALTER TABLE Tab1 ADD PRIMARY KEY Col1",
            "ALTER TABLE Tab1 ADD FOREIGN KEY Col1",
            "ALTER TABLE Tab1 DROP Col1",
            "ALTER TABLE Tab1 DROP PRIMARY KEY Col1",
            "ALTER TABLE Tab1 DROP FOREIGN KEY Col1"
    })
    void testValidAlterTableCommand(String alterTable) {
        System.out.println(alterTable);
        String[] filtered = Utilities.filterInput(alterTable);
        boolean isValid = parser.isValid(InputType.ALTER_TABLE, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ALTER TABLE Table MODIFY Col1 NUMBER(5)", // using table for a table name
            "ALTER TABLE Tab1 MODIFY Col1 DATE(5)", // size for date
            "ALTER TABLE Tab1 MODIFY PRIMARY KEY Col1", // modifying a key
            "ALTER TABLE Tab1 ADD Col1 CHAR(-5)", // negative size
            "ALTER TABLE Tab1 ADD Col1 CHAR(1.3)", // non integer size
            "ALTER TABLE Tab1 ADD 1 CHAR(1)", // numeric column name
            "ALTER TABLE Tab1 DROP PRIMARY Col1" // forgot key
    })
    void testInvalidAlterTableCommand(String alterTable) {
        System.out.println(alterTable);
        String[] filtered = Utilities.filterInput(alterTable);
        boolean isValid = parser.isValid(InputType.ALTER_TABLE, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // INSERT command --------------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "INSERT INTO Tab1 VALUES(1)", // simple
            "INSERT INTO Tab1 VALUES(1, -2, 3.1, \"Blah\", \"10-02-2020\")" // complex
    })
    void testValidInsertCommands(String insert) {
        System.out.println(insert);
        String[] filtered = Utilities.filterInput(insert);
        boolean isValid = parser.isValid(InputType.INSERT, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertTrue(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INSER INTO Tab1 VALUES(1)", // misspelling
            "INSERT INTO Table VALUES(1)", // reserved word misplacement
            "INSERT INTO Tab1 VALUES(\"1\")", // string value numeric
            "INSERT INTO Tab1 VALUES(Blah)", // numeric value string
            "IN SERT INTO Tab1 VALUES(1)", // random stuff in between stuff
            "I\tNSERT INTO Tab1 VALUES(1)",
            "INSERT IN\nTO Tab1 VALUES(1)"
    })
    void testInvalidInsertCommands(String insert) {
        System.out.println(insert);
        String[] filtered = Utilities.filterInput(insert);
        boolean isValid = parser.isValid(InputType.INSERT, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // DELETE command --------------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE FROM Tab1 WHERE Col1 = 1", // simple delete statements
            "DELETE FROM Tab1 WHERE Col1 != \"Blah\"",
            "DELETE FROM Tab1 WHERE Col1 > \"10-22-2020\"",
            "DELETE FROM Tab1 WHERE Col1 < ",
            "DELETE FROM Tab1 WHERE Col1 >= 1",
            "DELETE FROM Tab1 WHERE Col1 <= 1",
    })
    void testValidDeleteCommands(String delete) {
        System.out.println(delete);
        String[] filtered = Utilities.filterInput(delete);
        boolean isValid = parser.isValid(InputType.DELETE, filtered);
        System.out.println("Error Code: " + parser.getErrorMessage());
        assertFalse(isValid);
        System.out.println("-----------------------------------------------------------------------------------------");
    }
// TODO working on this
    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE", // missing a lot
            "DELETE FROM Tab1 WHERE Col1 > \"Blah\"", // should be a date
            "DELETE FROM Tab1 WHERE Col1 = Blah", // should be numeric
            "DELETE FROM Tab1 WHERE Col1 = \"1\"", // should be non numeric
            "DELETE FROM table1 WHE\nRE col1 = 1",
            "DELETE FROM tab\tle1 WHERE col1 = 1",
            "DELETE FROM table1 WHERE col1 > NotANumber"
    })
    void testInvalidDeleteCommands(String delete) {
        String[] filtered = Utilities.filterInput(delete);
        assertFalse(parser.isValid(InputType.DELETE, filtered));
        System.out.println("-----------------------------------------------------------------------------------------");
    }
/*
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
    }*/
}