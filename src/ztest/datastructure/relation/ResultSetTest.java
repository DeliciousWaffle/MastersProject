package ztest.datastructure.relation;

import datastructure.relation.resultset.ResultSet;
import datastructure.relation.table.Table;
import datastructure.relation.table.component.Column;
import file.io.IO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import file.io.Serialize;
import file.io.Filename;
import utilities.enums.Keyword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private static ResultSet customers, categories, employees, orderDetails, orders, products, suppliers, shippers;

    @BeforeAll
    public static void init() {

        String serialized = IO.read(Filename.TEST_TABLES);
        ArrayList<Table> tables = Serialize.unSerializeTables(serialized);

        customers = new ResultSet(tables.get(0));
        categories = new ResultSet(tables.get(1));
        employees = new ResultSet(tables.get(2));
        orderDetails = new ResultSet(tables.get(3));
        orders = new ResultSet(tables.get(4));
        products = new ResultSet(tables.get(5));
        suppliers = new ResultSet(tables.get(6));
        shippers = new ResultSet(tables.get(7));
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

        /*ResultSet customersTable = new ResultSet(tables.get(0));

        Column customersID = customersTable.getColumns().get(0);
        Column customersName = customersTable.getColumns().get(1);
        Column contactName = customersTable.getColumns().get(2);
        Column city = customersTable.getColumns().get(4);
        Column postalCode = customersTable.getColumns().get(5);
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
        System.out.println(customersTable.selection(conditionExpression).toString());*/

        /*System.out.println("MIX condition, returns customers whose id numbers are 10 or 12 or " +
                "come from Mexico and have postal code 05021");
        conditionExpression = new ConditionExpression(
                new Condition(customersID, Symbol.EQUAL, "10")
        );
        conditionExpression.or(new Condition(customersID, Symbol.EQUAL, "12"));
        conditionExpression.or(new Condition(city, Symbol.EQUAL, "Mexico"));
        conditionExpression.and(new Condition(postalCode, Symbol.EQUAL, "05021"));
        System.out.println(customersTable.selection(conditionExpression).toString());*/
    }

    @Test
    public void testCartesianProduct() {

        System.out.println("Test Cartesian Product ------------------------------------------------------------------");

        ResultSet cartesianProduct = customers.cartesianProduct(suppliers);
        //IO.writeTableData(Filename.TEST_TABLE_DATA, "remove.txt", cartesianProduct.toString());
        //System.out.println(customers);
        //System.out.println(suppliers);
    }

    @Test
    public void testNaturalJoin() {

        System.out.println("Test Natural Join -----------------------------------------------------------------------");

        /*System.out.println("Suppliers and Customers that come from the same country");
        Column country = customers.getColumns().get(6);
        ResultSet naturalJoin = customers.joinUsing(suppliers, country);
        System.out.println(naturalJoin.toString());*/
    }

    @Test
    public void testInnerJoin() {

        System.out.println("Test Inner Join -------------------------------------------------------------------------");
    }

    @Test
    public void testGroupBy() {

        System.out.println("Test Group By ---------------------------------------------------------------------------");

        Column quantity = orderDetails.getColumns().get(3);
        Map<Keyword, Column> min = new HashMap<>();
        min.put(Keyword.MIN, quantity);
        Map<Keyword, Column> max = new HashMap<>();
        max.put(Keyword.MAX, quantity);
        Map<Keyword, Column> avg = new HashMap<>();
        avg.put(Keyword.AVG, quantity);
        Map<Keyword, Column> count = new HashMap<>();
        count.put(Keyword.COUNT, quantity);
        Map<Keyword, Column> sum = new HashMap<>();
        sum.put(Keyword.SUM, quantity);

        System.out.println("Finding min quantity from OrderDetails");
        System.out.println(orderDetails.groupBy(min));

        System.out.println("Finding max quantity from OrderDetails");
        System.out.println(orderDetails.groupBy(max));

        System.out.println("Finding avg quantity from OrderDetails");
        System.out.println(orderDetails.groupBy(avg));

        System.out.println("Finding the count of quantity from OrderDetails");
        System.out.println(orderDetails.groupBy(count));

        System.out.println("Finding the sum of quantity from OrderDetails");
        System.out.println(orderDetails.groupBy(sum));

        HashMap<Keyword, Column> all = new HashMap<>();
        all.putAll(min);
        all.putAll(max);
        all.putAll(avg);
        all.putAll(count);
        all.putAll(sum);

        System.out.println("Finding everything on the quantity from OrderDetails");
        System.out.println(orderDetails.groupBy(all));
    }

    @Test
    public void testHaving() {

        System.out.println("Test Having -----------------------------------------------------------------------------");
    }

    @Test
    public void testOrderBy() {

        /*System.out.println("Test Order By ---------------------------------------------------------------------------");

        System.out.println("Ordering customers in ascending order by customer name");
        System.out.println(customers.orderByAsc(customers.getColumns().get(1)));

        System.out.println("Ordering order details by quantity ascending");
        System.out.println(orderDetails.orderByAsc(orderDetails.getColumns().get(3)));

        System.out.println("Ordering customers in descending order by country");
        System.out.println(customers.orderByDesc(customers.getColumns().get(6)));*/
    }

    @Test
    public void testCombinations() {

        System.out.println("Test Combinations -----------------------------------------------------------------------");
    }
}