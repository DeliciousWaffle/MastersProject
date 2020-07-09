package test;

import datastructures.Condition;
import datastructures.ConditionExpression;
import datastructures.table.ResultSet;
import datastructures.table.Table;
import datastructures.table.component.Column;
import files.IO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utilities.Serialize;
import utilities.enums.Filename;
import utilities.enums.Symbol;

import java.util.ArrayList;

/**
 * Currently using W3school's website for comparisons. Not perfect, will need to change later.
 */
class ResultSetTest {

    /*
    Customers
    Categories
    Employees
    OrderDetails
    Orders
    Products
    Suppliers
    Shippers
     */
    private static ArrayList<Table> tables;

    @BeforeAll
    public static void init() {

        String serialized = IO.read(Filename.TEST_TABLES);
        tables = Serialize.unSerializeTables(serialized);
    }

    @Test
    public void testProjection() {

        System.out.println("Test Projection -------------------------------------------------------------------------");

        /*ResultSet customersTable = new ResultSet(tables.get(0));
        ArrayList<Column> toProject = new ArrayList<>();
        toProject.add(customersTable.getColumns().get(0)); // customer id
        toProject.add(customersTable.getColumns().get(6)); // country
        toProject.add(customersTable.getColumns().get(4)); // city
        customersTable = customersTable.projection(toProject);
        System.out.println(customersTable.toString());

        customersTable = new ResultSet(tables.get(0));
        toProject = new ArrayList<>();
        toProject.add(customersTable.getColumns().get(0));
        toProject.add(customersTable.getColumns().get(1)); // customer name
        customersTable = customersTable.projection(toProject);
        System.out.println(customersTable.toString());*/
    }

    @Test
    public void testSelection() {

        System.out.println("Test Selection --------------------------------------------------------------------------");

        ResultSet customersTable = new ResultSet(tables.get(0));

        Column customersID = customersTable.getColumns().get(0);
        Column customersName = customersTable.getColumns().get(1);
        Column contactName = customersTable.getColumns().get(2);
        Column city = customersTable.getColumns().get(4);
        Column country = customersTable.getColumns().get(6);

        System.out.println("Single condition, returns customers from Germany");
        System.out.println(customersTable.selection(
                new Condition(country, Symbol.EQUAL, "Germany")).toString());

        System.out.println("Single condition, returns customers not from Germany");
        System.out.println(customersTable.selection(
                new Condition(country, Symbol.NOT_EQUAL, "Germany")).toString());

        System.out.println("Single condition, returns customer ids > 10");
        System.out.println(customersTable.selection(
                new Condition(customersID, Symbol.GREATER_THAN, "10")).toString());

        System.out.println("Single condition, returns customer ids >= 10");
        System.out.println(customersTable.selection(
                new Condition(customersID, Symbol.GREATER_THAN_OR_EQUAL, "10")).toString());

        System.out.println("Single condition, returns customer ids < 10");
        System.out.println(customersTable.selection(
                new Condition(customersID, Symbol.LESS_THAN, "10")).toString());

        System.out.println("Single condition, returns customer ids <= 10");
        System.out.println(customersTable.selection(
                new Condition(customersID, Symbol.LESS_THAN_OR_EQUAL, "10")).toString());

        System.out.println("OR condition, returns customers from Germany or France");
        ConditionExpression conditionExpression = new ConditionExpression(
                new Condition(country, Symbol.EQUAL, "Germany")
        );
        conditionExpression.or(new Condition(country, Symbol.EQUAL, "France"));
        System.out.println(customersTable.selection(conditionExpression).toString());

        System.out.println("OR condition, returns customers from Germany or France or have id number 5");
        conditionExpression = new ConditionExpression(
                new Condition(country, Symbol.EQUAL, "Germany")
        );
        conditionExpression.or(new Condition(country, Symbol.EQUAL, "France"));
        conditionExpression.or(new Condition(customersID, Symbol.EQUAL, "5"));

        System.out.println(customersTable.selection(conditionExpression).toString());

        System.out.println("AND condition, returns customers from Germany and from the city Berlin");
        conditionExpression = new ConditionExpression(
                new Condition(country, Symbol.EQUAL, "Germany")
        );
        conditionExpression.and(new Condition(city, Symbol.EQUAL, "Berlin"));
        System.out.println(customersTable.selection(conditionExpression).toString());

        System.out.println("AND condition with nothing returned");
        conditionExpression = new ConditionExpression(
                new Condition(country, Symbol.EQUAL, "Germany")
        );
        conditionExpression.and(new Condition(city, Symbol.EQUAL, "Paris"));
        System.out.println(customersTable.selection(conditionExpression).toString());

        System.out.println("AND condition, returns customers from France who are from Paris with id num 57");
        conditionExpression = new ConditionExpression(
                new Condition(country, Symbol.EQUAL, "France")
        );
        conditionExpression.and(new Condition(city, Symbol.EQUAL, "Paris"));
        conditionExpression.and(new Condition(customersID, Symbol.EQUAL, "57"));
        System.out.println(customersTable.selection(conditionExpression).toString());

        System.out.println("MIX condition, returns customers from Germany and from the city Berlin or " +
                        "with id num greater than 50");
        conditionExpression = new ConditionExpression(
                new Condition(country, Symbol.EQUAL, "Germany")
        );
        conditionExpression.and(new Condition(city, Symbol.EQUAL, "Berlin"));
        conditionExpression.or(new Condition(customersID, Symbol.GREATER_THAN, "50"));
        System.out.println(customersTable.selection(conditionExpression).toString());

        System.out.println("MIX condition, returns customers from Germany and from the city Berlin or " +
                "customers from France and from the city Paris");
        conditionExpression = new ConditionExpression(
                new Condition(country, Symbol.EQUAL, "Germany")
        );
        conditionExpression.and(new Condition(city, Symbol.EQUAL, "Berlin"));
        conditionExpression.or(new Condition(country, Symbol.EQUAL, "France"));
        conditionExpression.and(new Condition(city, Symbol.EQUAL, "Paris"));
        System.out.println(customersTable.selection(conditionExpression).toString());

        // TODO test with crazier stuff
    }

    @Test
    public void testCartesianProduct() {

        System.out.println("Test Cartesian Product ------------------------------------------------------------------");

        /*ResultSet customersTable = new ResultSet(tables.get(0));
        ResultSet shippersTable = new ResultSet(tables.get(7));
        customersTable = customersTable.cartesianProduct(shippersTable);
        System.out.println(customersTable.toString());*/
    }
}