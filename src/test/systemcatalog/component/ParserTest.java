package test.systemcatalog.component;

class ParserTest {

    /*private static Parser parser;

    @BeforeAll
    public static void init() {
        parser = new Parser();
    }

    // queries ---------------------------------------------------------------------------------------------------------
    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT a FROM b",
            "SELECT * FROM b",
            "SELECT MIN(a) FROM b",
            "SELECT MAX(a) FROM b",
            "SELECT AVG(a) FROM b",
            "SELECT COUNT(a) FROM b",
            "SELECT SUM(a) FROM b",
            "SELECT a, b FROM c",
            "SELECT a, b, c FROM d",
            "SELECT a FROM b, c, d, e",
            "SELECT a FROM b JOIN c USING(d)",
            "SELECT a FROM b JOIN c USING(d) JOIN e USING(f) JOIN g USING(h)",
            "SELECT a FROM b JOIN c USING(d) JOIN e USING(d)",
            "SELECT a FROM b WHERE c = 1",
            "SELECT a FROM b WHERE c != 1",
            "SELECT a FROM b WHERE c > 1",
            "SELECT a FROM b WHERE c < 1",
            "SELECT a FROM b WHERE c >= 1",
            "SELECT a FROM b WHERE c <= 1",
            "SELECT a FROM b WHERE c = 1 AND d > 2",
            "SELECT a FROM b WHERE c = 1 OR d <= 2",
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
    void testValidQueries(String validQuery) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(validQuery);
        parser.setRuleGraph(new RuleGraphTypes().getQueryRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertTrue(parser.isValid(InputType.QUERY, validQueryTokens));
        System.out.println("------------------------------------------------");
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
    void testInvalidQueries(String invalidQuery) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(invalidQuery);
        parser.setRuleGraph(new RuleGraphTypes().getQueryRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertFalse(parser.isValid(InputType.QUERY, validQueryTokens));
        System.out.println("------------------------------------------------");
    }

    // dml statements --------------------------------------------------------------------------------------------------
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
    void testValidCreateTableStatements(String validCreateTableStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(validCreateTableStatement);
        parser.setRuleGraph(new RuleGraphTypes().getCreateTableRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertTrue(parser.isValid(InputType.CREATE_TABLE, validQueryTokens));
        System.out.println("------------------------------------------------");
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
    void testInvalidCreateTableStatements(String invalidCreateTableStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(invalidCreateTableStatement);
        parser.setRuleGraph(new RuleGraphTypes().getCreateTableRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertFalse(parser.isValid(InputType.CREATE_TABLE, validQueryTokens));
        System.out.println("------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE table1",
            "DROP table table1",
            "DROP   table    table1",
            "DroP \t\n taBLe table1",
            "\n\tdrop table table1"
    })
    void testValidDropTableStatements(String validDropTableStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(validDropTableStatement);
        parser.setRuleGraph(new RuleGraphTypes().getDropTableRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertTrue(parser.isValid(InputType.DROP_TABLE, validQueryTokens));
        System.out.println("------------------------------------------------");
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
    void testInvalidDropTableStatements(String invalidDropTableStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(invalidDropTableStatement);
        parser.setRuleGraph(new RuleGraphTypes().getDropTableRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertFalse(parser.isValid(InputType.DROP_TABLE, validQueryTokens));
        System.out.println("------------------------------------------------");
    }

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
    void testValidInsertStatements(String insertStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(insertStatement);
        parser.setRuleGraph(new RuleGraphTypes().getInsertRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertTrue(parser.isValid(InputType.INSERT, validQueryTokens));
        System.out.println("------------------------------------------------");
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
    void testInvalidInsertStatements(String invalidInsertStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(invalidInsertStatement);
        parser.setRuleGraph(new RuleGraphTypes().getInsertRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertFalse(parser.isValid(InputType.INSERT, validQueryTokens));
        System.out.println("------------------------------------------------");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE FROM table1 WHERE col1 = 1",
            "DELETE FROM table1 WHERE col1 != something",
            "DELETE   FROM  table1   WHERE col1   >= 1",
            "\nDELETE FROM table1 \nWHERE col1 > 1",
            "DELETE \tFROM table1\tWHERE col1 < 1",
            "DeLeTE FRoM TabLe1 WHeRe cOl1 = 1",
    })
    void testValidDeleteStatements(String validDeleteStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(validDeleteStatement);
        parser.setRuleGraph(new RuleGraphTypes().getDeleteRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertTrue(parser.isValid(InputType.DELETE, validQueryTokens));
        System.out.println("------------------------------------------------");
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
    void testInvalidDeleteStatements(String invalidDeleteStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(invalidDeleteStatement);
        parser.setRuleGraph(new RuleGraphTypes().getDeleteRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertFalse(parser.isValid(InputType.DELETE, validQueryTokens));
        System.out.println("------------------------------------------------");
    }

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
    void testValidUpdateStatements(String validUpdateStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(validUpdateStatement);
        parser.setRuleGraph(new RuleGraphTypes().getUpdateRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertTrue(parser.isValid(InputType.UPDATE, validQueryTokens));
        System.out.println("------------------------------------------------");
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
    void testInValidUpdateStatements(String invalidUpdateStatement) {

        String[] validQueryTokens = parser.formatAndTokenizeInput(invalidUpdateStatement);
        parser.setRuleGraph(new RuleGraphTypes().getUpdateRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertFalse(parser.isValid(InputType.UPDATE, validQueryTokens));
        System.out.println("------------------------------------------------");
    }

    // for testing grant/revoke commands -------------------------------------------------------------------------------
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

        String[] validQueryTokens = parser.formatAndTokenizeInput(validGrantCommand);
        parser.setRuleGraph(new RuleGraphTypes().getGrantRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertTrue(parser.isValid(InputType.GRANT, validQueryTokens));
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

        String[] validQueryTokens = parser.formatAndTokenizeInput(invalidGrantCommand);
        parser.setRuleGraph(new RuleGraphTypes().getGrantRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertFalse(parser.isValid(InputType.GRANT, validQueryTokens));
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

        String[] validQueryTokens = parser.formatAndTokenizeInput(validRevokeCommand);
        parser.setRuleGraph(new RuleGraphTypes().getRevokeRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertTrue(parser.isValid(InputType.REVOKE, validQueryTokens));
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

        String[] validQueryTokens = parser.formatAndTokenizeInput(invalidRevokeCommand);
        parser.setRuleGraph(new RuleGraphTypes().getRevokeRuleGraph());
        System.out.println(Arrays.toString(validQueryTokens));
        assertFalse(parser.isValid(InputType.REVOKE, validQueryTokens));
        System.out.println("------------------------------------------------");
    }

    // TODO add testing for ALTER and BUILD commands
    // alter command ---------------------------------------------------------------------------------------------------
    // build command ---------------------------------------------------------------------------------------------------

     */
}