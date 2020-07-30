package ztest.systemcatalog.component;

import datastructure.rulegraph.RuleGraph;
import datastructure.rulegraph.type.RuleGraphTypes;
import systemcatalog.components.Compiler;
import systemcatalog.components.Parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompilerTest {

    private Compiler compiler;

    @BeforeAll
    public void init() {
        compiler = new Compiler();
    }

    @ParameterizedTest
    @ValueSource(strings = {
    })
    public void testQuery() {
        System.out.println("testQuery()");
        assertTrue(true);
    }

    @Test
    public void testCreateTable() {
        System.out.println("testCreateTable()");

        RuleGraph ruleGraph = new RuleGraphTypes().getCreateTableRuleGraph();

        // single column
        String input = "create table tab1(col1 number(5))";
        String[] tokens = new Parser().formatAndTokenizeInput(input);


        // multiple columns
        input = "create table tab1(col1 number(5), col2 char(10), col3 number(1))";
        tokens = new Parser().formatAndTokenizeInput(input);

        assertTrue(true);
    }

    @Test
    public void testDropTable() {
        System.out.println("testDropTable()");
        RuleGraph ruleGraph = new RuleGraphTypes().getDeleteRuleGraph();
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        assertTrue(true);
    }

    @Test
    public void testAlterTable() {
        System.out.println("testAlterTable()");
        RuleGraph ruleGraph = new RuleGraphTypes().getAlterTableRuleGraph();
        // modify column
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        // modify primary/foreign keys
        tokens = new Parser().formatAndTokenizeInput(input);
        // add column
        tokens = new Parser().formatAndTokenizeInput(input);
        // add primary/foreign keys
        tokens = new Parser().formatAndTokenizeInput(input);
        // drop column
        tokens = new Parser().formatAndTokenizeInput(input);
        assertTrue(true);
    }

    @Test
    public void testInsert() {
        System.out.println("testInsert()");
        RuleGraph ruleGraph = new RuleGraphTypes().getInsertRuleGraph();
        // regular insert

        // insert with less columns, addition of null values

        assertTrue(true);
    }

    @Test
    public void testDelete() {
        System.out.println("testDelete()");
        RuleGraph ruleGraph = new RuleGraphTypes().getDeleteRuleGraph();
        // =

        // !=

        // >

        // <

        assertTrue(true);
    }

    @Test
    public void testUpdate() {
        System.out.println("testUpdate()");
        RuleGraph ruleGraph = new RuleGraphTypes().getUpdateRuleGraph();
        // whole column

        // single value

        assertTrue(true);
    }

    @Test
    public void testBuildFileStructure() {
        System.out.println("testBuildFileStructure()");
        RuleGraph ruleGraph = new RuleGraphTypes().getBuildFileStructureRuleGraph();
        // build hash, secondary, and clustered b trees

        // build cluster file - no file structures prev built

        // build cluster file - file structures prev built

        assertTrue(true);
    }

    @Test
    public void testRemoveFileStructure() {
        System.out.println("testRemoveFileStructure()");
        RuleGraph ruleGraph = new RuleGraphTypes().getRemoveFileStructureRuleGraph();
        // remove hash, secondary, and clustered b trees

        // remove cluster file

        assertTrue(true);
    }

    @Test
    public void testGrant() {
        System.out.println("testGrant()");
        RuleGraph ruleGraph = new RuleGraphTypes().getGrantRuleGraph();
        // grant normal

        // grant all privileges


        assertTrue(true);
    }

    @Test
    public void testRevoke() {
        System.out.println("testRevoke()");
        RuleGraph ruleGraph = new RuleGraphTypes().getRevokeRuleGraph();
        // revoke normal

        // revoke all privileges


        assertTrue(true);
    }
}