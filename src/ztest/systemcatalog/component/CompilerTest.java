package ztest.systemcatalog.component;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.TableData;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.user.User;
import datastructures.user.component.Privilege;
import datastructures.user.component.TablePrivileges;
import systemcatalog.components.Compiler;
import systemcatalog.components.Parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompilerTest {

    private static Compiler compiler;

    @BeforeAll
    public static void init() {
        compiler = new Compiler();
    }

    public Table getTable() {
        Table table = new Table("Tab1");

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("Col1", DataType.NUMBER, 5));
        columns.add(new Column("Col2", DataType.CHAR, 10));
        columns.add(new Column("Col3", DataType.CHAR, 10));
        table.setColumns(columns);

        List<String> row1 = new ArrayList<>(Arrays.asList("1", "john", "us"));
        List<String> row2 = new ArrayList<>(Arrays.asList("2", "mike", "germany"));
        List<String> row3 = new ArrayList<>(Arrays.asList("3", "tyler", "germany"));
        List<String> row4 = new ArrayList<>(Arrays.asList("4", "gavin", "mexico"));
        List<List<String>> data = new ArrayList<>(Arrays.asList(row1, row2, row3, row4));
        table.setTableData(new TableData(
                new ArrayList<>(Arrays.asList(5, 10, 10)), data));

        System.out.println(table);

        return table;
    }

    public User getUser() {
        User user = new User();

        user.setUsername("Fred");
        List<TablePrivileges> tablePrivileges = new ArrayList<>();
        List<Privilege> privileges = new ArrayList<>();
        privileges.add(Privilege.ALTER);
        privileges.add(Privilege.REFERENCES);
        List<String> referencesColumns = new ArrayList<>();
        referencesColumns.add("Col2");
        TablePrivileges toAdd = new TablePrivileges("Tab1", privileges);
        toAdd.setReferenceColumns(referencesColumns);
        tablePrivileges.add(toAdd);
        user.setTablePrivilegesList(tablePrivileges);

        System.out.println(user);

        return user;
    }

   /* @ParameterizedTest
    @ValueSource(strings = {
    })
    public void testQuery() {
        System.out.println("testQuery() -----------------------------------------------------------------------------");
        assertTrue(true);
    }
*/
    /*@Test
    public void testCreateTable() {
        System.out.println("testCreateTable() -----------------------------------------------------------------------");

        RuleGraph ruleGraph = new RuleGraphTypes().getCreateTableRuleGraph();

        // single column
        String input = "create table tab1(col1 number(5))";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<Table> tables = new ArrayList<>();
        compiler.createTable(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // multiple columns
        input = "create table tab1(col1 number(5), col2 char(10), col3 number(1))";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>();
        compiler.createTable(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        assertTrue(true);
    }

    @Test
    public void testDropTable() {
        System.out.println("testDropTable() -------------------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getDropTableRuleGraph();

        String input = "drop table tab1";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<Table> tables = new ArrayList<>();
        tables.add(getTable());
        compiler.dropTable(tokens, ruleGraph, tables);
        System.out.println(tables.isEmpty());


        assertTrue(true);
    }

    @Test
    public void testAlterTable() {
        System.out.println("testAlterTable() ------------------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getAlterTableRuleGraph();

        // TODO: results in strange formatting, fix
        // modify column
        String input = "alter table tab1 modify col1 char(10)";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<Table> tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.alterTable(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // add column
        input = "alter table tab1 add col99 number(10)";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.alterTable(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // add primary/foreign keys
        input = "alter table tab1 add primary key col1";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.alterTable(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // drop column
        input = "alter table tab1 drop col1";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.alterTable(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // drop primary/foreign keys
        input = "alter table tab1 drop primary key col1";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.alterTable(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        assertTrue(true);
    }

    @Test
    public void testInsert() {
        System.out.println("testInsert() ----------------------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getInsertRuleGraph();

        // regular insert
        String input = "insert into tab1 values(1, b, c)";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<Table> tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.insert(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // insert with less columns, addition of null values
        input = "insert into tab1 values(1)";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.insert(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        assertTrue(true);
    }

    @Test
    public void testDelete() {
        System.out.println("testDelete() ----------------------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getDeleteRuleGraph();

        // =
        String input = "delete from tab1 where col2 = john";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<Table> tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.delete(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // !=
        input = "delete from tab1 where col2 != john";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.delete(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // >
        input = "delete from tab1 where col1 > 1";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.delete(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // <
        input = "delete from tab1 where col1 < 5";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.delete(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        assertTrue(true);
    }

    @Test
    public void testUpdate() {
        System.out.println("testUpdate() ----------------------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getUpdateRuleGraph();

        // whole column
        String input = "update tab1 set col1 = zzz";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<Table> tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.update(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // single value
        input = "update tab1 set col1 = zzz where col3 = mexico";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.update(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        assertTrue(true);
    }

    @Test
    public void testBuildFileStructure() {
        System.out.println("testBuildFileStructure() ----------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getBuildFileStructureRuleGraph();

        // build hash, secondary, and clustered b trees
        String input = "build hash table on col1 in tab1";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<Table> tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.buildFileStructure(tokens, ruleGraph, tables);
        for(Column column : tables.get(0).getColumns()) {
            System.out.println(column + " " + column.getFileStructure());
        }

        // build cluster file - no file structures prev built
        input = "build clustered file on tab1 and tab2";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.buildFileStructure(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        // build cluster file - file structures prev built
        input = "build hash table on col1 in tab1";
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.buildFileStructure(tokens, ruleGraph, tables);
        for(Column column : tables.get(0).getColumns()) {
            System.out.println(column + " " + column.getFileStructure());
        }
        input = "build clustered file on tab1 and tab2";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        compiler.buildFileStructure(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));
        for(Column column : tables.get(0).getColumns()) {
            System.out.println(column + " " + column.getFileStructure());
        }

        assertTrue(true);
    }

    @Test
    public void testRemoveFileStructure() {
        System.out.println("testRemoveFileStructure() ---------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getRemoveFileStructureRuleGraph();

        // remove hash, secondary, and clustered b trees
        String input = "remove file structure on col1 in tab1";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        System.out.println(ruleGraph.isSyntacticallyCorrect(tokens));
        List<Table> tables = new ArrayList<>(Arrays.asList(getTable()));
        tables.get(0).getColumns().get(0).setFileStructure(FileStructure.CLUSTERED_B_TREE);
        for(Column column : tables.get(0).getColumns()) {
            System.out.println(column + " " + column.getFileStructure());
        }
        compiler.removeFileStructure(tokens, ruleGraph, tables);
        for(Column column : tables.get(0).getColumns()) {
            System.out.println(column + " " + column.getFileStructure());
        }

        // remove cluster file
        input = "remove clustered file on tab1 and tab2";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        tables = new ArrayList<>(Arrays.asList(getTable()));
        tables.get(0).setClusteredWith("tab2");
        System.out.println(tables.get(0));
        compiler.removeFileStructure(tokens, ruleGraph, tables);
        System.out.println(tables.get(0));

        assertTrue(true);
    }

    @Test
    public void testGrant() {
        System.out.println("testGrant() -----------------------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getGrantRuleGraph();

        // grant normal
        String input = "grant insert, select on tab1 to fred";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<User> users = new ArrayList<>(Arrays.asList(getUser()));
        List<Table> tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.grant(tokens, ruleGraph, users, tables);
        System.out.println(users.get(0));

        // grant normal with grant option
        input = "grant insert, select on tab1 to fred with grant option";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        users = new ArrayList<>(Arrays.asList(getUser()));
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.grant(tokens, ruleGraph, users, tables);
        System.out.println(users.get(0));

        // grant all privileges
        input = "grant all privileges on tab1 to fred";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        users = new ArrayList<>(Arrays.asList(getUser()));
        tables = new ArrayList<>(Arrays.asList(getTable()));
        compiler.grant(tokens, ruleGraph, users, tables);
        System.out.println(users.get(0));

        assertTrue(true);
    }
*/
    @Test
    public void testRevoke() {
        System.out.println("testRevoke() ----------------------------------------------------------------------------");
        RuleGraph ruleGraph = new RuleGraphTypes().getRevokeRuleGraph();

        // revoke normal
        String input = "revoke alter, references(col2) on tab1 from fred";
        System.out.println(input);
        String[] tokens = new Parser().formatAndTokenizeInput(input);
        List<User> users = new ArrayList<>(Arrays.asList(getUser()));
        compiler.revoke(tokens, ruleGraph, users);
        System.out.println(users.get(0));

        // revoke all privileges
        input = "revoke all privileges on tab1 from fred";
        System.out.println(input);
        tokens = new Parser().formatAndTokenizeInput(input);
        users = new ArrayList<>(Arrays.asList(getUser()));
        compiler.revoke(tokens, ruleGraph, users);
        System.out.println(users.get(0));

        assertTrue(true);
    }
}