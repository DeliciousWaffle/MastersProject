package test;

import datastructures.Selection;
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

import static org.junit.jupiter.api.Assertions.*;

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

        ResultSet customersTable = new ResultSet(tables.get(0));
        ArrayList<Column> toProject = new ArrayList<>();
        toProject.add(customersTable.getColumns().get(0)); // customer id
        toProject.add(customersTable.getColumns().get(6)); // country
        toProject.add(customersTable.getColumns().get(4)); // city
// TODO fix table problem
        customersTable.projection(toProject);
        System.out.println(customersTable.toString());
    }

    @Test
    public void testSelection() {

        ResultSet customersTable = new ResultSet(tables.get(0));

        Column customersID = customersTable.getColumns().get(0);
        Column customersName = customersTable.getColumns().get(1);
        Column country = customersTable.getColumns().get(6);

        //ArrayList<Selection> selections = new ArrayList<>();
        //selections.add(new Selection(customersID, Symbol.EQUAL, "1"));
        //customersTable.selection(selections);
        //System.out.println(customersTable.toString());

        /*customersTable = new ResultSet(tables.get(0));
        selections = new ArrayList<>();
        selections.add(new Selection(customersName, Symbol.NOT_EQUAL, ""));
        selections.add(new Selection(country, Symbol.EQUAL, ""));
        customersTable.selection(selections);
        System.out.println(customersTable.toString());*/
    }
}