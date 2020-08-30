import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.TableData;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Temp {

    public static void main(String[] args) {
        List<Table> tables = new ArrayList<>();
        tables.addAll(Arrays.asList());
        //temp();
        customers();
    }

    public static void temp() {
        int[] a = new int[60];
        for(int i = 0; i < a.length; i++)
            a[i] = i + 1;
        System.out.println(Arrays.toString(a));
        StringBuilder s = new StringBuilder();
        try {
            File currentDirFile = Paths.get("src", "a.txt").toFile();
            Scanner sc = new Scanner(currentDirFile);
            while(sc.hasNextLine())
                s.append(sc.nextLine().trim()).append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] blah = s.toString().split("\n");
        for(int i = 0; i < blah.length; i++) {
            for(int j = i + 1; j < blah.length - 1; j++) {
                if(blah[i].equalsIgnoreCase(blah[j])) {
                    System.out.println(true);
                }
            }
        }
        String[] b = new String[60];
        String[] c = new String[60];
        for(int i = 0; i < blah.length; i++) {
            b[i] = blah[i].split(" ")[0];
            c[i] = blah[i].split(" ")[1];
        }
        s = new StringBuilder();
        for(String x : b) {
            s.append("\"").append(x).append("\"").append(",");
        }
        System.out.println(s.toString());
        s = new StringBuilder();
        for(String x : c) {
            s.append("\"").append(x).append("\"").append(",");
        }
        System.out.println(s.toString());
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

        int[] customerID = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60};
        String[] firstName= new String[] {"Genaro","Dane","Monty","Pat","Casey","Herb","Forrest","Enrique","Michale","Edgar","Thomas","Shirley","King","Chauncey","Tanner","Pablo","Kenton","Cole","Johnnie","Andrea","Mina","Lana","Shameka","Kimberely","Claudine","Valrie","Sherita","Denna","Judy","Shawanda","Dagmar","Angelika","Sharon","Wynona","Analisa","Aliza","Leonila","Luanne","Alana","Yetta","Neda","Malik","Francisco","Mattie","Melvin","Nolan","Lekisha","Creola","Bruce","Chante","Willodean","Efren","Signe","Keith","Dona","Tammi","Season","Lady","Sheree","Terry"};
        String[] lastName = new String[] {"Curnutt","Knapp","Tokarski","Devaughn","Pegg","Campisi","Levering","Brazell","Krogman","Linn","Swift","Mcgarr","Muir","Poirier","Lytch","Harbert","Serrato","Bermejo","Bakewell","Addington","Kennerly","Whiteman","Cockrill","Dantin","Meier","Sauter","Atwell","Hartt","Saine","Poynter","Trumble","Fichter","Soukup","Paulding","Larocca","Cacciatore","Askins","Covarrubias","Rhodes","Stutes","Weis","Loredo","Rau","Fujii","Turner","Lu","Luongo","Ohlsen","Sprvill","Perkin","Oubre","Paylor","Wolk","Cloyd","Kuehne","Brenes","Mathieson","Nance","Legg","Lemmer"};

        List<List<String>> td = new ArrayList<>();

        for(int i = 0; i < customerID.length; i++) {
            List<String> d = new ArrayList<>(Arrays.asList(Integer.toString(customerID[i]), firstName[i], lastName[i], Integer.toString(employeeID[i])));
            td.add(d);
        }
        table.setTableData(new TableData(new ArrayList<>(), td));
        System.out.println(table.toString());
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
