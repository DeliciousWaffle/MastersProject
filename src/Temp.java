import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.TableData;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class Temp {

    public static void main(String[] args) {
        List<Table> tables = new ArrayList<>();
        tables.addAll(Arrays.asList());
        customers();
    }

    public static Table customers() {
        String tableName = "Customers";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("CustomerID", DataType.NUMBER, 5),
                new Column("FirstName", DataType.CHAR, 15),
                new Column("LastName", DataType.CHAR, 15),
                new Column("EmployeeID", DataType.NUMBER, 5)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("CustomerID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Employees", "EmployeeID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);

        int[] a = new int[54];
        for(int i = 0; i < a.length; i++)
            a[i] = i + 1;
        StringBuilder s = new StringBuilder();
        try {
            Scanner sc = new Scanner(new File("a.txt"));
            while(sc.hasNextLine())
                s.append(sc.nextLine().trim()).append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] b = s.toString().split("\n");
        s = new StringBuilder();
        for(String x : b) {
            s.append("\"").append(x).append("\"").append(",");
        }

        table.setTableData(null);
        return table;
    }

    public static Table products() {
        String tableName = "Products";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("ProductID", DataType.NUMBER, 5),
                new Column("ProductName", DataType.CHAR, 15),
                new Column("Price", DataType.NUMBER, 5)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("ProductID"));
        Map<String, String> foreignKeys = new HashMap<>();
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
        table.setTableData(null);
        return table;
    }

    public static Table stores() {
        String tableName = "Stores";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("StoreID", DataType.NUMBER, 5),
                new Column("StoreName", DataType.CHAR, 15),
                new Column("Address", DataType.CHAR, 15),
                new Column("City", DataType.CHAR, 15),
                new Column("State", DataType.CHAR, 2),
                new Column("ManagerID", DataType.NUMBER, 5)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("CustomerID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Employees", "ManagerID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
        table.setTableData(null);
        return table;
    }

    public static Table employees() {
        String tableName = "Employees";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("EmployeeID", DataType.NUMBER, 5),
                new Column("FirstName", DataType.CHAR, 15),
                new Column("LastName", DataType.CHAR, 15),
                new Column("PhoneNumber", DataType.CHAR, 12),
                new Column("Address", DataType.CHAR, 15),
                new Column("City", DataType.CHAR, 15),
                new Column("Salary", DataType.NUMBER, 6)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("EmployeeID"));
        Map<String, String> foreignKeys = new HashMap<>();
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
        table.setTableData(null);
        return table;
    }

    public static Table suppliers() {
        String tableName = "Suppliers";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("SupplierID", DataType.NUMBER, 5),
                new Column("SupplierName", DataType.CHAR, 15),
                new Column("PhoneNumber", DataType.CHAR, 12),
                new Column("Address", DataType.CHAR, 15),
                new Column("City", DataType.CHAR, 15),
                new Column("State", DataType.CHAR, 2)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("SupplierID"));
        Map<String, String> foreignKeys = new HashMap<>();
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
        table.setTableData(null);
        return table;
    }

    public static Table shippingDetails() {
        String tableName = "ShippingDetails";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("ProductID", DataType.NUMBER, 5),
                new Column("SupplierID", DataType.NUMBER, 5),
                new Column("StoreID", DataType.NUMBER, 5),
                new Column("Quantity", DataType.NUMBER, 6)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("ProductID", "SupplierID", "StoreID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Products", "ProductID");
        foreignKeys.put("Suppliers", "SupplierID");
        foreignKeys.put("Stores", "StoreID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
        table.setTableData(null);
        return table;
    }

    public static Table purchaseDetails() {
        String tableName = "PurchaseDetails";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("CustomerID", DataType.NUMBER, 5),
                new Column("ProductID", DataType.NUMBER, 5),
                new Column("Quantity", DataType.NUMBER, 6),
                new Column("PaymentMethod", DataType.CHAR, 10)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("CustomerID", "ProductID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Customers", "CustomerID");
        foreignKeys.put("Products", "ProductID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
        table.setTableData(null);
        return table;
    }

    public static Table inventoryDetails() {
        String tableName = "InventoryDetails";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("StoreID", DataType.NUMBER, 5),
                new Column("ProductID", DataType.CHAR, 15),
                new Column("Quantity", DataType.CHAR, 15)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("StoreID", "ProductID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Stores", "StoreID");
        foreignKeys.put("Products", "ProductID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
        table.setTableData(null);
        return table;
    }
}
