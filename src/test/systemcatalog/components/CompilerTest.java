package test.systemcatalog.components;

import datastructures.querytree.QueryTree;
import datastructures.relation.resultset.ResultSet;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.user.User;
import enums.InputType;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Compiler;
import systemcatalog.components.Parser;
import systemcatalog.components.Verifier;
import utilities.Utilities;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for ensuring that the System Catalog's Compiler is operating as it should be.
 * This means that when input is executed, this makes sure that the output is correct.
 */
public class CompilerTest {

    private static Optimizer optimizer;
    private static Compiler compiler;
    private static List<Table> tables;
    private static List<User> users;

    @BeforeAll
    public static void init() {
        optimizer = new Optimizer();
        compiler = new Compiler();
        tables = Serializer.unSerializeTables(IO.readOriginalData(FileType.OriginalData.ORIGINAL_TABLES));
        users = Serializer.unSerializeUsers(IO.readOriginalData(FileType.OriginalData.ORIGINAL_USERS));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT FirstName FROM Customers",
            "SELECT Customers.FirstName FROM Customers",
            "SELECT FirstName, LastName FROM Customers",
            "SELECT LastName, FirstName FROM Customers",
            "SELECT * FROM Customers",
            "SELECT * FROM Customers WHERE CustomerID = 1",
            "SELECT * FROM Customers WHERE FirstName = \"Genaro\"",
            "SELECT * FROM Customers WHERE FirstName = \"Genaro\" AND CustomerID = -1",
            "SELECT * FROM Customers WHERE CustomerID != 1",
            "SELECT * FROM Customers WHERE CustomerID != 1 AND CustomerID != 20",
            "SELECT * FROM Customers WHERE FirstName != \"Genaro\" AND CustomerID != 20",
            "SELECT CustomerID FROM Customers WHERE CustomerID > 20",
            "SELECT CustomerID FROM Customers WHERE CustomerId < 30",
            "SELECT CustomerID FROM Customers WHERE CustomerId > 20 AND CustomerID < 30",
            "SELECT * FROM CustomerPurchaseDetails WHERE DatePurchased > \"2021-01-01\"",
            "SELECT * FROM CustomerPurchaseDetails WHERE DatePurchased < \"2021-01-01\"",
            "SELECT * FROM Customers INNER JOIN CustomerPurchaseDetails ON Customers.CustomerID = CustomerPurchaseDetails.CustomerID",
            "SELECT COUNT(FirstName) FROM Customers",
            "SELECT AVG(FirstName) FROM Customers", // would be invalid, but allow anyways
            "SELECT COUNT(CustomerID) FROM Customers",
            "SELECT SUM(CustomerID) FROM Customers",
            "SELECT COUNT(CustomerID), SUM(CustomerID) FROM Customers",
            "SELECT SUM(DatePurchased) FROM CustomerPurchaseDetails",
            "SELECT AVG(DatePurchased) FROM CustomerPurchaseDetails",
            "SELECT SUM(DatePurchased), AVG(DatePurchased), COUNT(CustomerID) FROM CustomerPurchaseDetails",
            "SELECT AVG(Price) FROM Products",
            "SELECT AVG(DiscountAmount) FROM EmployeePurchaseDetails",
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod",
            "SELECT PaymentMethod, Quantity, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod, Quantity",
            "SELECT PaymentMethod, COUNT(PaymentMethod), AVG(Quantity) FROM CustomerPurchaseDetails GROUP BY PaymentMethod",
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails WHERE PaymentMethod = \"American Express\" GROUP By PaymentMethod",
            "SELECT PaymentMethod, COUNT(PaymentMethod) FROM CustomerPurchaseDetails GROUP BY PaymentMethod HAVING COUNT(PaymentMethod) > 5"
    })
    public void testQuery(String query) {
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);
        ResultSet resultSet = compiler.executeQuery(queryTrees, tables);
        System.out.println(resultSet);

        Parser p = new Parser();
        if (! p.isValid(InputType.QUERY, filtered)) {
            System.out.println(p.getErrorMessage());
            fail();
        }

        Verifier v = new Verifier();
        if (! v.isValid(InputType.QUERY, filtered, tables, users)) {
            System.out.println(v.getErrorMessage());
            fail();
        }
    }

    @Test
    public void test() {
        String query = "SELECT FirstName FROM Products, Customers";
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);
        ResultSet resultSet = compiler.executeQuery(queryTrees, tables);
        try {
            FileWriter fileWriter = new FileWriter("Temp.txt");
            fileWriter.append(resultSet.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        String query = "SELECT * FROM CustomerPurchaseDetails";
        System.out.println(query);
        String[] filtered = Utilities.filterInput(query);
        List<QueryTree> queryTrees = optimizer.getQueryTreeStates(filtered, tables);
        ResultSet resultSet = compiler.executeQuery(queryTrees, tables);

        resultSet.orderByAsc(new ArrayList<>(Arrays.asList("CustomerPurchaseDetails.PaymentMethod", "CustomerPurchaseDetails.Quantity")));
        System.out.println(resultSet);
    }

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
*//*
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
    }*/
}