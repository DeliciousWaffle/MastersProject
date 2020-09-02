import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.TableData;
import datastructures.user.User;
import files.io.FileType;
import files.io.IO;
import files.io.Serialize;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//https://www.mockaroo.com/ https://www.name-generator.org.uk/?i=2jhmefnt https://www.randomlists.com/food?dup=false&qty=180

public class Temp {

    public static void main(String[] args) {
        List<Table> tables = new ArrayList<>();
        tables.addAll(Arrays.asList(
        //temp();
        customers(),
        products(),
        employees(),
        stores(),
        suppliers(),
        customerPurchaseDetails(),
        employeePurchaseDetails(),
        shippingDetails(),
        inventoryDetails()
        ));
        String serialized = Serialize.serializeTables(tables);
        IO.writeCurrentData(serialized, FileType.CurrentData.CURRENT_TABLES);
        for(Table table : tables) {
            String tableName = table.getTableName();
            String serializedTableData = Serialize.serializeTableData(table);
            IO.writeCurrentTableData(serializedTableData, FileType.CurrentTableData.CURRENT_TABLE_DATA, tableName + ".txt");
        }
        tables = Serialize.unSerializeTables(IO.readCurrentData(FileType.CurrentData.CURRENT_TABLES));
        for(Table table : tables) {
            System.out.println(table.toString());
            System.out.println("---------------------------------------------------------------------------");
        }
        List<User> users = Serialize.unSerializeUsers(IO.readCurrentData(FileType.CurrentData.CURRENT_USERS));
        for(User user : users) {
            System.out.println(user.toString());
            System.out.println("---------------------------------------------------------------------------");
        }
    }

    public static void temp() {

        // quantity
        Stream.generate(new Random()::nextDouble).limit(180).forEach(e -> System.out.print(((int) (Math.abs(e * 9)+1 )) + ","));

        //for(int i = 0; i < 180; i++) {
        //    System.out.print((i+1) + ",");
        //}
        //Stream.of(address).filter(e -> e.length() > 30).forEach(System.out::println);
        /*StringBuilder s = new StringBuilder();
        try {
            File currentDirFile = Paths.get("src", "a.txt").toFile();
            Scanner sc = new Scanner(currentDirFile);
            while(sc.hasNextLine())
                s.append(sc.nextLine().trim()).append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] blah = s.toString().split("\n");
        s = new StringBuilder();
        for(int i  = 0; i < blah.length; i++) {
            String a = blah[i];
            char[] aa = a.toCharArray();
            aa[0] = Character.toUpperCase(aa[0]);
            for(int j = 1; j < aa.length; j++) {

                if(aa[j - 1] == ' ') {
                    aa[j] = Character.toUpperCase(aa[j]);
                }
            }
            blah[i] = String.copyValueOf(aa);
            blah[i] = "\"" + blah[i] + "\"";
        }
        System.out.println(Arrays.toString(blah));

        /*for(String x : b) {
            s.append("\"").append(x).append("\"").append(",");
        }
        System.out.println(s.toString());
        s = new StringBuilder();
        for(String x : c) {
            s.append("\"").append(x).append("\"").append(",");
        }

        String[] start = new String[123];
        String[] end = new String[123];

        for(int i = 0; i < start.length; i++) {

            boolean valid = false;

            while(! valid) {

                try {
                    String year = "20";
                    int rand = (int) (Math.random() * 4);
                    switch (rand) {
                        case 0:
                            year += "18";
                            break;
                        case 1:
                            year += "19";
                            break;
                        case 2:
                            year += "20";
                            break;
                        case 3:
                            year += "21";
                            break;
                    }
                    String mm = Integer.toString((int) (Math.random() * 12) + 1);
                    String dd = Integer.toString((int) (Math.random() * 31) + 1);

                    if (mm.length() == 1) {
                        mm = "0" + mm;
                    }

                    if (dd.length() == 1) {
                        dd = "0" + dd;
                    }

                    LocalDate date1 = LocalDate.parse(year + "-" + mm + "-" + dd);
                    LocalDate date2 = date1.plusDays((int)(Math.random() * 300) + 1);
                    start[i] = "\""+date1.toString()+"\"";
                    end[i] = "\""+date2.toString()+"\"";
                    valid = true;
                }catch(Exception e) {}
            }
        }*/
    }

    // DONE
    public static Table customers() {
        String tableName = "Customers";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("CustomerID", DataType.NUMBER, 5),
                new Column("FirstName", DataType.CHAR, 15),
                new Column("LastName", DataType.CHAR, 15)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("CustomerID"));
        Map<String, String> foreignKeys = new HashMap<>();
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);

        int[] customerID = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60};
        String[] firstName= new String[] {"Genaro","Dane","Monty","Pat","Casey","Herb","Forrest","Enrique","Michale","Edgar","Thomas","Shirley","King","Chauncey","Tanner","Pablo","Kenton","Cole","Johnnie","Andrea","Mina","Lana","Shameka","Kimberely","Claudine","Valrie","Sherita","Denna","Judy","Shawanda","Dagmar","Angelika","Sharon","Wynona","Analisa","Aliza","Leonila","Luanne","Alana","Yetta","Neda","Malik","Francisco","Mattie","Melvin","Nolan","Lekisha","Creola","Bruce","Chante","Willodean","Efren","Signe","Keith","Dona","Tammi","Season","Lady","Sheree","Terry"};
        String[] lastName = new String[] {"Curnutt","Knapp","Tokarski","Devaughn","Pegg","Campisi","Levering","Brazell","Krogman","Linn","Swift","Mcgarr","Muir","Poirier","Lytch","Harbert","Serrato","Bermejo","Bakewell","Addington","Kennerly","Whiteman","Cockrill","Dantin","Meier","Sauter","Atwell","Hartt","Saine","Poynter","Trumble","Fichter","Soukup","Paulding","Larocca","Cacciatore","Askins","Covarrubias","Rhodes","Stutes","Weis","Loredo","Rau","Fujii","Turner","Lu","Luongo","Ohlsen","Sprvill","Perkin","Oubre","Paylor","Wolk","Cloyd","Kuehne","Brenes","Mathieson","Nance","Legg","Lemmer"};
        List<List<String>> td = new ArrayList<>();
        for(int i = 0; i < 60; i++) {
            List<String> d = new ArrayList<>(Arrays.asList(Integer.toString(customerID[i]), firstName[i], lastName[i]));
            td.add(d);
        }
        // max(column name.length, column.size) for each padding amount entry
        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(10, 15, 15)), td));
        //System.out.println(table.toString());
        return table;
    }
// DONE
    public static Table products() {
        String tableName = "Products";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("ProductID", DataType.NUMBER, 5),
                new Column("ProductName", DataType.CHAR, 25),
                new Column("Price", DataType.NUMBER, 1)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("ProductID"));
        Map<String, String> foreignKeys = new HashMap<>();
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
//180
        int[] productID = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180};
        String[] productName = new String[] {"Chicken Chowder", "Navy Beans", "Prawns", "Sherry", "Nectarines", "Condensed Milk", "White Chocolate", "Baguette", "Rice Vinegar", "Turtle", "Grouper", "Pheasants", "Geese", "Vanilla Bean", "Water Chestnuts", "Spaghetti Squash", "Carrots", "Red Beans", "Cookies", "Succotash", "Lamb", "Potato Chips", "Jicama", "Pepper", "Potatoes", "Allspice", "Pork", "Herring", "Borscht", "Watermelons", "Beef", "Halibut", "Squash", "Tomatoes", "Ricotta Cheese", "Tarragon", "Havarti Cheese", "Sugar", "Plantains", "Coconut Milk", "Soymilk", "Tonic Water", "Pomegranates", "Romano Cheese", "Molasses", "Broccoli", "Ketchup", "Colby Cheese", "Peaches", "Cabbage", "Bruschetta", "Kahlua", "Split Peas", "Buckwheat", "Chard", "Chili Powder", "Bagels", "Summer Squash", "Wine", "Pumpkins", "Cinnamon", "Bard", "Hot Sauce", "Cream Of Tartar", "Goji Berry", "Andouille Sausage", "Bananas", "Granola", "Jelly Beans", "Orange Peels", "Eel", "Soybeans", "Cloves", "Spearmint", "Barley Sugar", "Wine Vinegar", "Lima Beans", "Bay Leaves", "Snap Peas", "Vinegar", "Green Beans", "Remoulade", "Avocados", "Raspberries", "Snow Peas", "Lobsters", "Feta Cheese", "Cumin", "Kidney Beans", "Dill", "Fish Sauce", "Rhubarb", "Tomato Sauce", "Sausages", "Quail", "Acorn Squash", "Melons", "Rose Water", "Sweet Chili Sauce", "Vanilla", "Gorgonzola", "Anchovies", "Cannellini Beans", "Corn Flour", "Corn", "Crabs", "Raw Sugar", "Romaine Lettuce", "Date Sugar", "Cream", "Red Snapper", "Lemon Juice", "Wild Rice", "Aioli", "Brazil Nuts", "Cooking Wine", "Red Pepper Flakes", "Lemon Grass", "Pistachios", "Broth", "Coconuts", "Tomato Paste", "Mustard Seeds", "Marshmallows", "Plum Tomatoes", "Chutney", "Amaretto", "Tea", "Chipotle Peppers", "Cornstarch", "Pico De Gallo", "Bean Sauce", "Red Cabbage", "Cod", "Angelica", "Salsa", "Raisins", "Alfredo Sauce", "Bass", "Turnips", "Cactus", "Mint", "Shitakes", "Lentils", "Peas", "Bourbon", "Milk", "Liver", "Breadcrumbs", "Maraschino Cherries", "Salmon", "Hoisin Sauce", "Mascarpone", "Hash Browns", "Won Ton Skins", "Mackerel", "Apple Pie Spice", "Kiwi", "Rabbits", "Sauerkraut", "Sweet Potatoes", "Bean Sprouts", "Asiago Cheese", "Walnuts", "Broccoli Raab", "Shallots", "Almond Butter", "Celery", "Fennel Seeds", "Creme Fraiche", "Swordfish", "Almond Extract", "Chives", "Pancetta", "Berries", "Tuna", "Irish Cream Liqueur", "Vegemite", "Shrimp", "Horseradish"};
        int[] price = new int[] {3,3,9,6,1,7,6,8,9,8,9,5,2,4,6,7,4,4,4,9,1,1,2,1,6,6,4,7,2,2,4,1,7,2,8,7,9,5,1,6,5,5,7,7,4,2,6,7,9,7,8,4,5,6,3,8,2,2,6,1,5,2,8,9,2,8,7,1,4,8,5,3,2,7,5,1,8,5,4,2,2,4,6,9,1,5,7,8,5,2,2,3,6,2,8,9,7,5,7,9,5,2,2,1,5,4,1,3,6,9,8,8,3,7,6,8,3,5,8,2,7,7,3,1,9,8,4,4,7,2,4,7,6,9,5,6,2,3,7,1,9,8,2,6,3,7,7,2,6,4,9,2,4,6,1,6,2,3,7,5,4,2,9,6,1,9,6,2,8,4,7,8,2,3,9,1,9,3,7,8};
        List<List<String>> td = new ArrayList<>();
        for(int i = 0; i < 180 ; i++) {
            List<String> d = new ArrayList<>(Arrays.asList(Integer.toString(productID[i]), productName[i], Integer.toString(price[i])));
            td.add(d);
        }

        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(9, 25, 5)), td));
        //System.out.println(table.toString());
        return table;
    }
//DONE
    public static Table employees() {
        String tableName = "Employees";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("EmployeeID", DataType.NUMBER, 5),
                new Column("FirstName", DataType.CHAR, 15),
                new Column("LastName", DataType.CHAR, 15),
                new Column("PhoneNumber", DataType.CHAR, 12),
                new Column("Address", DataType.CHAR, 30),
                new Column("City", DataType.CHAR, 25),
                new Column("State", DataType.CHAR, 2),
                new Column("Salary", DataType.NUMBER, 6)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("EmployeeID"));
        Map<String, String> foreignKeys = new HashMap<>();
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
        int[] employeeID = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40};
        String[] firstName = new String[] {"Margarita","Geoffrey","Lauren","Wayne","Nathan","Justin","Sally","Wilma","Hubert","Carole","Johanna","Mattie","Martin","Alejandro","Jo","Darrin","Ronnie","Sean","Carlos","Walter","Roman","Cornelius","Patrick","Julio","Teresa","Leonard","Aaron","Micheal","Angel","Rose","Grady","Shelly","Levi","Dewey","Gretchen","Eula","Oscar","Darrel","Albert","Lorenzo"};
        String[] lastName = new String[] {"Rodgers","Francis","Pearson","Bryant","Scott","Henderson","Thornton","Tyler","Garcia","Ingram","Morgan","Gardner","Boone","Washington","Wade","Dixon","Carpenter","Morales","Simpson","Abbott","Barker","Norris","Cooper","Mitchell","Rice","Barker","Cook","Tate","Jones","Malone","Sanchez","Rose","Reyes","Bryan","Austin","Howard","Carroll","Love","Fuller","Warner"};
        String[] phoneNumbers = new String[] {"475-976-4932","878-452-7088","637-215-2895","942-499-0423","888-829-5040","201-255-6458","211-416-7793","879-424-9951","572-405-6749","737-352-4334","683-272-9329","741-272-2549","693-896-2042","430-305-1829","672-868-8318","762-879-4790","258-857-8421","929-236-5542","669-871-9570","404-311-5286","747-741-7619","706-313-8188","584-609-2210","275-858-5361","783-426-1622","585-491-4594","368-767-7731","301-630-0706","442-572-4555","948-251-3350","404-448-2915","416-894-9239","393-736-7988","478-678-5602","807-392-4524","956-793-0865","846-988-1480","615-547-6676","723-754-5741","666-280-8847"};
        String[] address = new String[] {"7054 Goldcrest Down","1212 N All Saints' Villas Road","5081 Ellesmere Mount","9773 Oakham Mount","816 N Great Flatt","2597 W Bowness Woods","3928 S Gloucester By-Pass","6396 S Brackley Mews","8185 Sandown Dell","1958 E Whitamore Row","7107 S Dudley Strand","1009 N Stanmore Bridge","134 E White Horse Cedars","1426 Netherton Springs","5226 W Linnet Paddocks","4236 Wickham Hawthorns","5502 W Archer Las","7541 N Kentmere Furlong","2227 N Colham Green Road","8193 N Thistle Nook","3796 E Poplar Town","394 N Newtown Alley","3434 N Braemar Valley","7286 Duff Terrace","300 Summerhill Cottages","7970 N Stanmore Newydd","11 N Hartley Parkway","6310 N Higham Wharf","1130 N Heron Court","3739 E Cuckoo Street","6775 N Falkland Woods","2506 N Royal Loke","5223 W Barrack Crescent","9490 S Stafford Villas","1740 E Glencoe Glebe","6114 N Witham Willows","3915 W Tudor Nook","7850 W Oakham Promenade","5979 S Bloxwich Lane","1255 E Herdings Court"};
        String[] city = new String[] {"Waru", "Qingfa", "Valença do Douro", "Chengji", "Glagahdowo", "Si Satchanalai", "Kuala Lumpur", "Tingzhou", "Huangdu", "Melchor de Mencos", "Şūrān", "Chelgard", "Kuzovatovo", "Psychikó", "Moravská Třebová", "Springfield", "Kudowa-Zdrój", "Paris", "Ḩāfiz Moghul", "Karangboyo", "Zielonka", "São Miguel dos Campos", "Kaduy", "Laguna", "Hannover", "San Jose", "Xibin", "Şānūr", "Cezi", "Turija", "Matara", "Kim Sơn", "Rathnew", "De Mayo", "Wenxian Chengguanzhen", "Tubarão", "Las Tejerías", "Jishigang", "Lianglin", "Lemban"};
        String[] state = new String[] {"NC", "OK", "IA", "MD", "FL", "GA", "KS", "MI", "ID", "MI", "AK", "AZ", "AK", "CT", "ND", "NH", "IN", "NY", "CT", "UT", "VT", "VT", "MI", "NV", "AR", "CA", "KY", "CO", "CT", "AR", "TX", "CT", "TN", "HI", "AL", "IA", "TX", "OH", "UT", "OH"};
        int[] salary = new int[] {9929,83336,99514,66416,39489,25935,10526,38639,40351,78584,99192,54803,91020,3787,58819,75639,3661,70657,55313,4176,4944,65300,22826,41999,92178,77548,27371,6886,45063,65952,81513,30952,10928,682,7350,95278,97099,84895,87341,46909};
        List<List<String>> td = new ArrayList<>();
        for(int i = 0; i < 40; i++) {
            List<String> row = new ArrayList<>(Arrays.asList(Integer.toString(employeeID[i]), firstName[i], lastName[i], phoneNumbers[i], address[i], city[i], state[i], Integer.toString(salary[i])));
            td.add(row);
        }

        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(10, 15, 15, 12, 30, 25, 5, 6)), td));
        //System.out.println(table.toString());
        return table;
    }

    public static Table stores() {
        String tableName = "Stores";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("StoreID", DataType.NUMBER, 5),
                new Column("StoreName", DataType.CHAR, 25),
                new Column("Address", DataType.CHAR, 30),
                new Column("City", DataType.CHAR, 25),
                new Column("State", DataType.CHAR, 2),
                new Column("ManagerID", DataType.NUMBER, 5)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("StoreID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Employees", "ManagerID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
//13
        int[] storeID = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};
        String[] storeName = new String[] {"Jake-Mart", "JaKohl's", "J.B. Maxx", "Busseijer", "Jake's Club", "Place with Food", "Beds, Bussa's, and Beyond", "Bussarget", "Jakecy's", "JBPenney", "Jakestrom", "By-Vee", "Bussa's Beef"};
        String[] address = new String[] {"9486 Cranbourne Hill","4536 E Mallow Hollow","724 S Wilna Road","443 N Atholl Links","3697 Fenwick Garth","8834 W Hill View Limes","5263 N Thames Glebe","8015 E Lambourne Acre","7927 S Brick Acre","8537 Victory Terrace","6795 Thames Wharf","6504 W Back Esplanade","6901 E Allington Loan","9048 Sandscale Terrace","4791 W Picton Link","4426 Hatfield Bottom","2043 W Palmer Piece","7228 N Greaves Oak","183 W Argyll Holt","3985 N Beechcroft Approach","7453 Copley Glen","2369 N Egerton Field","3023 N Back Windsor Grove","867 S Oaks Lawn","4326 St Martins Bridge","4926 Newbridge Passage","8302 N River Gardens"};
        String[] city = new String[] {"Dubova", "Telč", "Almirante", "Belo sur Tsiribihina", "Masparrito", "Velká nad Veličkou", "Santa Cruz da Graciosa", "Palilula", "Stockholm", "Eirunepé", "Żabieniec", "Qalaikhumb", "Banjar Bongangede", "Néos Skopós", "Tatarsk", "Rebrikha", "Horta", "Malgobek", "Negreiros", "Shuangquan", "Malikisi", "Shakhun’ya", "Sarapul", "Mariposa", "Cikalong", "Sukagawa", "Morro do Chapéu"};
        String[] state = new String[] {"CT", "NC", "OH", "KY", "LA", "CT", "NY", "FL", "OH", "AR", "AK", "VT", "GA", "UT", "KS", "KY", "MA", "CA", "NE", "IN", "TN", "NH", "ID", "NC", "ID", "CA", "AZ"};
        int[] managerID = new int[] {1,4,7,13,14,20,23,25,27,28,31,35,39};

        List<List<String>> td = new ArrayList<>();
        for(int i = 0; i < 13; i++) {
            List<String> d = new ArrayList<>(Arrays.asList(Integer.toString(storeID[i]), storeName[i], address[i], city[i], state[i], Integer.toString(managerID[i])));
            td.add(d);
        }
        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(7, 25, 30, 25, 5, 9)), td));
        //System.out.println(table.toString());
        return table;
    }

    // DONE
    public static Table suppliers() {
        String tableName = "Suppliers";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("SupplierID", DataType.NUMBER, 5),
                new Column("SupplierName", DataType.CHAR, 25),
                new Column("PhoneNumber", DataType.CHAR, 12),
                new Column("Address", DataType.CHAR, 30),
                new Column("City", DataType.CHAR, 15),
                new Column("State", DataType.CHAR, 2)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("SupplierID"));
        Map<String, String> foreignKeys = new HashMap<>();
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
//7
        int[] supplierID = new int[] {1, 2, 3, 4, 5, 6, 7};
        String[] supplierName = new String[] {"Freddy's Food Delivery", "John's Jeneric Goods", "Freaky Fast Freights", "Dan's Suppliery", "Goods Goods Good", "Quickery Hickory's", "Another Supplier Name"};
        String[] phoneNumber = new String[] {"696-564-140","947-827-969","146-694-895","375-544-439","548-589-187","326-411-305","999-460-961","203-752-245","339-352-978","440-024-937","524-227-670","702-096-023","570-896-840"};
        String[] address = new String[] {"425 N Digby Lea","2312 Wainwell Mews","8684 Elwyn Road","939 S Greenside Park","3260 S Cumberlege Close","5461 N Waterford Drive","4424 N Milton Hills","2854 W Albemarle Quadrant","6475 Swan Glade","6777 N Percy Drive","4764 W Bailey Mews","9843 S Ridge Lodge","8913 Glastonbury Rise"};
        String[] city = new String[] {"Jinhe", "Zhongxing", "Khal’ch", "Ferreiras", "Gantang", "Guilmaro", "Cork", "Ban Phai", "Shencang", "Girona", "Tuzhai", "Kuching", "Nanyuan"};
        String[] state = new String[] {"LA", "MN", "MA", "WV", "AL", "AR", "WI", "GA", "MI", "GA", "NC", "VA", "OR"};

        List<List<String>> td = new ArrayList<>();

        for(int i = 0; i < 7; i++) {
            List<String> d = new ArrayList<>(Arrays.asList(Integer.toString(supplierID[i]), supplierName[i], phoneNumber[i], address[i], city[i], state[i]));
            td.add(d);
        }

        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(10, 25, 12, 30, 15, 5)), td));
        //System.out.println(table.toString());
        return table;
    }

    public static Table customerPurchaseDetails() {
        String tableName = "CustomerPurchaseDetails";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("CustomerID", DataType.NUMBER, 5),
                new Column("ProductID", DataType.NUMBER, 5),
                new Column("Quantity", DataType.NUMBER, 2),
                new Column("PaymentMethod", DataType.CHAR, 20)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("CustomerID", "ProductID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Customers", "CustomerID");
        foreignKeys.put("Products", "ProductID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);

        int[] customerID = new int[] {1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 6, 7, 7, 8, 8, 9, 9, 9, 9, 10, 10, 10, 11, 11, 12, 12, 13, 13, 13, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16, 16, 17, 17, 17, 18, 19, 19, 19, 20, 20, 20, 20, 21, 22, 23, 23, 24, 24, 25, 25, 25, 26, 26, 26, 26, 27, 28, 28, 28, 28, 28, 29, 29, 29, 30, 30, 30, 30, 30, 31, 31, 31, 32, 32, 33, 34, 34, 34, 34, 35, 36, 37, 37, 38, 39, 40, 40, 40, 40, 41, 41, 41, 42, 42, 43, 43, 44, 44, 45, 46, 46, 46, 46, 47, 47, 47, 47, 48, 48, 48, 48, 48, 49, 49, 49, 49, 49, 50, 50, 51, 51, 51, 52, 52, 53, 53, 53, 54, 54, 54, 55, 56, 56, 56, 56, 56, 57, 57, 58, 58, 58, 58, 59, 59, 59, 60, 60};
        System.out.println(customerID.length);
        int[] productID = new int[] {90, 176, 140, 172, 11, 149, 130, 75, 86, 109, 128, 5, 74, 76, 33, 113, 5, 43, 20, 144, 128, 85, 76, 157, 174, 112, 165, 152, 27, 41, 129, 55, 67, 30, 169, 125, 78, 44, 131, 144, 154, 143, 20, 161, 29, 158, 33, 57, 40, 129, 153, 10, 65, 135, 33, 54, 172, 147, 102, 110, 119, 175, 41, 9, 54, 73, 99, 38, 177, 9, 64, 166, 156, 110, 151, 167, 157, 142, 32, 165, 3, 125, 148, 139, 140, 122, 97, 5, 80, 167, 103, 25, 170, 95, 25, 50, 142, 174, 51, 29, 119, 59, 71, 81, 19, 144, 49, 35, 11, 108, 48, 162, 99, 112, 62, 141, 138, 141, 172, 163, 119, 69, 109, 166, 1, 60, 110, 89, 178, 131, 170, 73, 3, 107, 88, 130, 10, 107, 132, 113, 157, 125, 28, 136, 162, 143, 104, 23, 7, 166, 137, 61, 98, 27, 14, 115, 54, 78, 76, 77};
        int[] quantity = new int[] {13, 6, 1, 11, 15, 3, 14, 4, 7, 1, 14, 19, 16, 4, 16, 18, 18, 1, 14, 4, 4, 20, 13, 7, 20, 16, 4, 1, 15, 7, 15, 12, 17, 3, 1, 7, 13, 11, 6, 15, 9, 5, 12, 5, 20, 3, 8, 6, 13, 13, 11, 14, 18, 8, 10, 3, 4, 6, 8, 19, 15, 12, 15, 6, 3, 15, 7, 17, 15, 13, 1, 17, 13, 15, 6, 1, 1, 8, 16, 8, 11, 1, 19, 4, 2, 9, 8, 15, 18, 5, 14, 14, 8, 3, 19, 14, 15, 12, 5, 6, 17, 15, 19, 1, 20, 10, 18, 3, 4, 9, 11, 8, 9, 4, 8, 17, 6, 4, 4, 18, 16, 1, 16, 9, 9, 5, 18, 11, 18, 15, 18, 10, 17, 4, 4, 17, 19, 2, 14, 19, 5, 13, 17, 17, 8, 18, 11, 1, 2, 10, 6, 17, 14, 20, 10, 17, 18, 4, 2, 13};
        String[] paymentMethod = new String[] {"American Express", "Visa", "Capital One", "Check", "Chase", "Visa", "Visa", "Check", "Discover", "Chase", "American Express", "Visa", "Discover", "Capital One", "Visa", "Visa", "Chase", "Chase", "Visa", "Chase", "American Express", "Check", "Master Card", "Visa", "American Express", "American Express", "Visa", "Discover", "Discover", "Master Card", "Cash", "Chase", "American Express", "Master Card", "Master Card", "Cash", "Cash", "Capital One", "Visa", "Cash", "Visa", "Cash", "American Express", "Cash", "Cash", "Check", "Check", "Master Card", "Discover", "Cash", "Capital One", "Chase", "American Express", "Chase", "Capital One", "American Express", "Cash", "Check", "Chase", "Discover", "Check", "Visa", "Check", "American Express", "Master Card", "American Express", "Visa", "Cash", "American Express", "Visa", "Master Card", "Master Card", "Discover", "Master Card", "Capital One", "Master Card", "Chase", "Cash", "Capital One", "Chase", "Visa", "Chase", "Chase", "American Express", "Cash", "Capital One", "Chase", "Master Card", "Master Card", "Chase", "Check", "Chase", "Capital One", "American Express", "Check", "Cash", "Discover", "Discover", "Check", "Discover", "Master Card", "Capital One", "Master Card", "Visa", "Capital One", "American Express", "Chase", "Discover", "Discover", "Capital One", "Visa", "Chase", "Discover", "American Express", "Chase", "American Express", "Capital One", "Cash", "Discover", "American Express", "Visa", "Visa", "Capital One", "Chase", "American Express", "Capital One", "Discover", "Check", "Check", "Capital One", "Chase", "Cash", "American Express", "Chase", "Discover", "Visa", "Visa", "Discover", "American Express", "Visa", "Chase", "Check", "Visa", "Chase", "Cash", "Master Card", "Visa", "Chase", "Check", "Check", "Check", "Visa", "Visa", "Cash", "Master Card", "Check", "Discover", "Visa", "Cash", "Chase"};

        List<List<String>> td = new ArrayList<>();

        for(int i = 0; i < 160; i++) {
            td.add(new ArrayList<>(Arrays.asList(Integer.toString(customerID[i]), Integer.toString(productID[i]), Integer.toString(quantity[i]), paymentMethod[i])));
        }

        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(10, 9, 8, 20)), td));
        //System.out.println(table.toString());
        return table;
    }

    public static Table employeePurchaseDetails() {
        String tableName = "EmployeePurchaseDetails";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("EmployeeID", DataType.NUMBER, 5),
                new Column("ProductID", DataType.NUMBER, 5),
                new Column("Quantity", DataType.NUMBER, 2),
                new Column("PaymentMethod", DataType.CHAR, 20),
                new Column("DiscountAmount", DataType.NUMBER, 5)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("EmployeeID", "ProductID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Employees", "EmployeeID");
        foreignKeys.put("Products", "ProductID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);

        int[] employeeID = new int[] {1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 7, 8, 8, 9, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 15, 15, 15, 16, 16, 16, 16, 17, 17, 17, 17, 18, 19, 19, 19, 19, 20, 20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 23, 23, 24, 24, 24, 24, 25, 26, 27, 27, 27, 27, 28, 29, 29, 29, 29, 29, 30, 30, 31, 31, 31, 31, 31, 32, 32, 33, 33, 33, 34, 35, 35, 35, 36, 36, 36, 36, 36, 37, 37, 37, 38, 38, 38, 38, 39, 39, 40, 40, 40};
        int[] productID = new int[] {138, 34, 85, 116, 162, 67, 45, 90, 73, 19, 119, 110, 40, 35, 143, 55, 71, 2, 64, 179, 62, 69, 152, 164, 38, 166, 15, 122, 61, 160, 130, 107, 100, 131, 39, 81, 6, 143, 135, 40, 160, 92, 177, 9, 1, 164, 13, 97, 150, 53, 44, 77, 129, 62, 89, 79, 177, 180, 68, 45, 103, 18, 98, 38, 37, 132, 142, 133, 5, 179, 109, 14, 120, 166, 149, 59, 13, 51, 157, 120, 170, 126, 19, 153, 133, 163, 73, 27, 17, 120, 110, 32, 74, 36, 110, 28, 94, 167, 2, 87, 16, 79, 160, 137, 172, 178, 11, 110, 169, 120, 47, 26, 169, 176, 14, 33, 123, 101, 45, 124, 120, 31};
        int[] quantity = new int[] {5, 6, 17, 11, 20, 5, 14, 20, 2, 5, 14, 4, 5, 13, 19, 1, 9, 1, 19, 1, 7, 13, 9, 19, 11, 10, 5, 14, 14, 17, 4, 18, 11, 9, 1, 8, 15, 15, 9, 14, 6, 16, 4, 11, 15, 5, 3, 14, 14, 5, 19, 1, 9, 7, 13, 6, 16, 3, 8, 12, 10, 9, 16, 3, 15, 6, 2, 4, 8, 16, 9, 4, 5, 10, 11, 20, 20, 19, 2, 16, 5, 15, 9, 11, 2, 5, 3, 16, 6, 20, 7, 2, 10, 19, 7, 6, 20, 7, 2, 6, 4, 1, 7, 4, 1, 7, 5, 17, 17, 1, 4, 5, 10, 17, 18, 6, 17, 20, 3, 18, 9, 10};
        String[] paymentMethod = new String[] {"Capital One", "American Express", "Chase", "Discover", "Check", "Cash", "Chase", "Visa", "Check", "Discover", "Capital One", "Chase", "Chase", "Discover", "Discover", "Master Card", "Cash", "American Express", "American Express", "Check", "Master Card", "Capital One", "Master Card", "Master Card", "Check", "Cash", "Master Card", "American Express", "Visa", "Visa", "Chase", "Check", "Check", "Visa", "American Express", "Capital One", "American Express", "Cash", "Capital One", "Discover", "Cash", "American Express", "Capital One", "Master Card", "Capital One", "Capital One", "American Express", "American Express", "Cash", "Visa", "Visa", "Cash", "Capital One", "American Express", "Chase", "Visa", "Check", "Visa", "Chase", "Check", "Visa", "Visa", "Cash", "Cash", "Visa", "Visa", "Cash", "Master Card", "Master Card", "American Express", "American Express", "Chase", "Cash", "Capital One", "Cash", "Chase", "Master Card", "Capital One", "Discover", "Capital One", "Discover", "Capital One", "Visa", "Cash", "Capital One", "Chase", "American Express", "Master Card", "Master Card", "Capital One", "Chase", "Discover", "Discover", "Chase", "Master Card", "Cash", "Cash", "American Express", "Visa", "Chase", "American Express", "Master Card", "American Express", "Cash", "American Express", "Discover", "Visa", "Chase", "Visa", "Master Card", "Check", "Discover", "Check", "Master Card", "Chase", "Cash", "Cash", "Visa", "Visa", "Cash", "American Express", "Cash"};
        int[] discountAmount = new int[] {5, 9, 1, 7, 1, 5, 5, 6, 9, 1, 1, 3, 6, 8, 6, 1, 1, 1, 6, 4, 3, 1, 6, 6, 5, 1, 7, 1, 6, 7, 9, 3, 2, 2, 9, 6, 8, 8, 8, 6, 7, 1, 2, 8, 1, 2, 3, 6, 9, 7, 9, 3, 1, 2, 9, 5, 1, 6, 3, 3, 2, 9, 3, 4, 3, 6, 6, 3, 4, 8, 8, 3, 2, 4, 2, 8, 9, 5, 5, 7, 6, 7, 4, 3, 2, 7, 3, 2, 6, 9, 3, 7, 2, 6, 6, 5, 4, 1, 6, 9, 9, 4, 4, 5, 7, 8, 4, 6, 5, 7, 9, 3, 7, 3, 9, 3, 7, 2, 5, 7, 6, 8};

        List<List<String>> td = new ArrayList<>();

        for(int i = 0; i < 122; i++) {
            td.add(new ArrayList<>(Arrays.asList(Integer.toString(employeeID[i]), Integer.toString(productID[i]), Integer.toString(quantity[i]), paymentMethod[i], Integer.toString(discountAmount[i]))));
        }

        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(10, 9, 8, 20, 14)), td));
        //System.out.println(table.toString());
        return table;
    }

    public static Table shippingDetails() {
        String tableName = "ShippingDetails";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("SupplierID", DataType.NUMBER, 5),
                new Column("StoreID", DataType.NUMBER, 5),
                new Column("ProductID", DataType.NUMBER, 5),
                new Column("Quantity", DataType.NUMBER, 4),
                new Column("DateShipped", DataType.CHAR, 10),
                new Column("DateArrived", DataType.CHAR, 10)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("SupplierID", "StoreID", "ProductID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Suppliers", "SupplierID");
        foreignKeys.put("Stores", "StoreID");
        foreignKeys.put("Products", "ProductID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);

        //123


        int[] supplierID = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
        int[] productID = new int[] {103, 150, 91, 137, 155, 154, 93, 165, 116, 27, 40, 176, 161, 177, 84, 131, 170, 89, 37, 73, 132, 56, 61, 132, 121, 51, 150, 111, 107, 2, 43, 39, 69, 29, 119, 157, 155, 148, 91, 84, 62, 95, 146, 119, 126, 1, 156, 114, 11, 7, 122, 141, 105, 57, 90, 39, 64, 127, 10, 123, 40, 123, 12, 176, 164, 83, 177, 167, 148, 125, 37, 51, 7, 64, 147, 13, 136, 14, 21, 59, 6, 124, 46, 53, 173, 123, 26, 118, 102, 141, 96, 102, 128, 135, 91, 162, 138, 45, 150, 40, 102, 80, 113, 143, 14, 155, 114, 32, 114, 165, 123, 122, 179, 77, 129, 41, 80, 72, 49, 40, 44, 52, 148};
        int[] storeID = new int[] {4, 8, 8, 12, 7, 4, 11, 1, 11, 3, 10, 7, 2, 1, 12, 5, 12, 11, 6, 10, 13, 4, 12, 10, 6, 9, 10, 7, 2, 7, 2, 2, 7, 4, 9, 13, 11, 11, 10, 10, 9, 12, 13, 8, 8, 9, 13, 1, 7, 12, 13, 2, 8, 7, 1, 2, 4, 6, 13, 3, 12, 13, 7, 5, 2, 11, 11, 8, 9, 6, 9, 3, 3, 4, 4, 2, 5, 9, 4, 12, 2, 10, 8, 1, 4, 6, 10, 5, 13, 10, 12, 10, 2, 8, 12, 3, 3, 2, 3, 10, 7, 5, 13, 1, 1, 5, 5, 5, 3, 13, 10, 6, 13, 7, 9, 11, 8, 3, 1, 3, 5, 10, 7};
        int[] quantity = new int[] {89, 88, 61, 78, 69, 67, 88, 22, 14, 5, 71, 3, 53, 47, 20, 53, 8, 89, 37, 2, 21, 29, 29, 40, 61, 78, 90, 51, 57, 3, 9, 8, 50, 88, 71, 68, 60, 25, 21, 76, 27, 87, 69, 67, 30, 13, 44, 17, 71, 3, 31, 29, 23, 29, 9, 14, 46, 16, 46, 49, 66, 47, 69, 88, 80, 60, 80, 55, 46, 58, 3, 59, 2, 28, 64, 68, 56, 30, 10, 85, 39, 25, 75, 59, 16, 74, 43, 83, 51, 43, 18, 68, 87, 9, 59, 6, 29, 58, 45, 59, 18, 79, 11, 42, 77, 73, 68, 2, 65, 87, 19, 80, 14, 73, 19, 13, 47, 81, 45, 33, 46, 71, 64};
        String[] dateShipped = new String[] {"2019-05-25", "2020-09-08", "2019-01-24", "2021-08-13", "2018-12-06", "2021-09-26", "2018-12-07", "2018-08-18", "2020-02-01", "2021-07-23", "2018-11-08", "2018-06-21", "2020-10-03", "2019-06-06", "2019-12-08", "2020-07-05", "2019-09-18", "2018-04-05", "2018-02-22", "2018-08-14", "2020-01-15", "2018-03-30", "2021-03-27", "2018-10-07", "2021-02-07", "2018-12-21", "2020-04-21", "2018-07-12", "2018-05-31", "2021-10-06", "2019-05-17", "2021-03-10", "2020-01-21", "2021-01-09", "2020-10-30", "2021-01-21", "2020-07-19", "2021-01-30", "2020-03-10", "2020-11-29", "2018-03-10", "2021-05-22", "2021-08-26", "2021-11-18", "2018-01-29", "2021-12-18", "2018-09-01", "2019-10-25", "2021-03-13", "2019-12-18", "2019-09-14", "2018-12-03", "2021-05-17", "2020-07-26", "2018-03-18", "2019-01-02", "2019-07-14", "2019-01-01", "2019-01-15", "2021-09-04", "2020-10-27", "2020-11-17", "2020-01-07", "2019-12-09", "2018-08-27", "2020-02-21", "2021-11-11", "2019-11-21", "2020-06-27", "2019-05-15", "2021-04-11", "2018-07-06", "2018-06-13", "2020-06-30", "2021-07-12", "2019-09-01", "2021-05-31", "2018-03-18", "2020-04-29", "2018-07-26", "2019-02-21", "2018-12-10", "2021-08-23", "2020-08-11", "2020-02-10", "2018-04-09", "2021-05-16", "2018-12-06", "2019-03-31", "2018-06-12", "2019-05-29", "2019-03-26", "2018-03-27", "2018-03-29", "2018-08-19", "2019-07-21", "2021-07-02", "2018-07-11", "2021-09-07", "2018-04-15", "2018-07-10", "2019-12-14", "2020-10-14", "2021-07-25", "2018-09-14", "2021-03-30", "2021-06-14", "2018-11-26", "2021-04-22", "2020-09-01", "2019-09-24", "2018-02-04", "2018-11-23", "2021-03-11", "2019-09-02", "2019-03-16", "2018-10-12", "2020-05-26", "2018-01-05", "2021-10-01", "2020-12-10", "2019-04-20", "2020-11-14"};
        String[] dateArrived = new String[] {"2020-01-19", "2020-09-24", "2019-03-05", "2021-12-31", "2019-02-16", "2022-02-07", "2019-04-05", "2018-12-13", "2020-02-19", "2021-08-14", "2019-05-30", "2019-03-18", "2020-10-05", "2019-08-19", "2020-07-19", "2021-04-03", "2020-03-08", "2018-11-29", "2018-11-17", "2018-11-29", "2020-09-20", "2018-07-29", "2021-07-11", "2018-12-20", "2021-07-14", "2019-04-21", "2020-07-01", "2018-09-28", "2019-03-24", "2022-02-08", "2019-10-20", "2021-09-07", "2020-05-02", "2021-05-25", "2021-04-12", "2021-06-10", "2020-11-19", "2021-09-16", "2020-11-02", "2021-05-03", "2018-07-06", "2022-01-29", "2022-01-30", "2022-07-22", "2018-05-16", "2021-12-29", "2019-02-17", "2020-04-21", "2021-10-29", "2020-09-07", "2020-03-21", "2019-06-09", "2021-06-28", "2021-05-05", "2018-04-15", "2019-02-18", "2020-01-27", "2019-10-11", "2019-02-01", "2022-04-23", "2021-02-12", "2021-02-14", "2020-06-20", "2020-09-01", "2018-09-18", "2020-08-25", "2022-03-19", "2020-03-05", "2020-11-23", "2020-01-26", "2021-11-04", "2019-04-30", "2018-07-07", "2020-11-18", "2021-08-04", "2020-03-07", "2021-09-30", "2018-06-18", "2020-08-11", "2019-03-30", "2019-07-30", "2019-04-26", "2022-04-17", "2020-09-03", "2020-10-06", "2018-12-06", "2021-10-10", "2019-08-02", "2019-10-02", "2018-12-11", "2020-01-12", "2019-10-06", "2018-11-05", "2018-11-03", "2018-11-26", "2020-03-03", "2021-11-28", "2019-04-18", "2022-04-28", "2018-06-23", "2018-11-27", "2020-08-04", "2021-04-19", "2022-01-13", "2018-11-11", "2021-10-04", "2022-01-12", "2019-03-26", "2021-07-29", "2020-11-10", "2019-12-09", "2018-02-12", "2019-07-02", "2021-07-24", "2019-10-25", "2019-09-26", "2019-03-21", "2020-06-14", "2018-05-11", "2022-04-21", "2021-03-22", "2019-08-27", "2021-09-05"};

        List<List<String>> td = new ArrayList<>();

        for(int i = 0; i < 123; i++) {
            td.add(new ArrayList<>(Arrays.asList(Integer.toString(supplierID[i]), Integer.toString(productID[i]), Integer.toString(storeID[i]), Integer.toString(quantity[i]), dateShipped[i], dateArrived[i])));
        }

        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(10, 7, 9, 8, 11, 11)), td));
        //System.out.println(table.toString());
        return table;
    }

    public static Table inventoryDetails() {
        String tableName = "InventoryDetails";
        List<Column> columns = new ArrayList<>(Arrays.asList(
                new Column("StoreID", DataType.NUMBER, 5),
                new Column("ProductID", DataType.NUMBER, 5),
                new Column("Quantity", DataType.NUMBER, 4)
        ));
        List<String> primaryKeys = new ArrayList<>(Arrays.asList("StoreID", "ProductID"));
        Map<String, String> foreignKeys = new HashMap<>();
        foreignKeys.put("Stores", "StoreID");
        foreignKeys.put("Products", "ProductID");
        Table table = new Table(tableName, columns, primaryKeys, foreignKeys);
//310

        int[] storeID = new int[] {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13};
        int[] productID = new int[] {39, 148, 164, 153, 178, 147, 16, 105, 20, 38, 173, 58, 149, 14, 46, 167, 135, 4, 62, 34, 95, 24, 131, 51, 164, 160, 154, 179, 17, 38, 160, 161, 12, 179, 72, 65, 32, 94, 148, 27, 58, 50, 72, 167, 56, 158, 180, 171, 148, 10, 51, 16, 152, 139, 35, 176, 27, 154, 66, 54, 45, 163, 161, 148, 98, 65, 78, 10, 149, 35, 126, 24, 23, 153, 123, 147, 176, 128, 104, 44, 66, 98, 6, 93, 101, 27, 171, 175, 89, 118, 145, 72, 8, 125, 164, 94, 108, 19, 63, 49, 67, 80, 21, 123, 116, 46, 28, 91, 156, 132, 21, 141, 105, 180, 79, 63, 104, 7, 34, 8, 61, 25, 47, 67, 139, 49, 171, 62, 67, 169, 138, 7, 112, 116, 46, 98, 180, 25, 173, 160, 44, 88, 37, 138, 137, 172, 35, 120, 107, 111, 51, 85, 97, 141, 51, 82, 162, 22, 162, 10, 15, 117, 153, 121, 126, 138, 163, 70, 52, 111, 77, 71, 106, 56, 143, 97, 63, 123, 60, 86, 37, 79, 130, 92, 89, 171, 68, 67, 41, 18, 102, 158, 37, 175, 160, 110, 45, 90, 103, 89, 151, 118, 121, 70, 151, 1, 63, 139, 37, 23, 155, 121, 37, 119, 63, 28, 100, 67, 135, 144, 43, 35, 169, 144, 160, 111, 17, 94, 155, 110, 143, 104, 116, 131, 86, 152, 169, 138, 48, 166, 32, 23, 125, 50, 29, 101, 145, 25, 103, 176, 51, 40, 135, 109, 126, 29, 111, 118, 118, 113, 142, 100, 134, 171, 22, 125, 75, 41, 100, 66, 88, 165, 157, 153, 157, 158, 44, 42, 45, 33, 138, 154, 148, 160, 144, 5, 7, 82, 21, 7, 8, 59, 158, 60, 10, 155, 16, 171, 35, 121, 23, 101, 76, 13, 123, 93, 171, 69, 52, 111};
        int[] quantity = new int[] {384, 390, 245, 612, 331, 812, 172, 782, 845, 925, 109, 164, 982, 423, 699, 500, 656, 41, 14, 296, 326, 471, 403, 915, 724, 115, 893, 212, 702, 572, 475, 74, 141, 246, 264, 46, 516, 974, 458, 645, 165, 112, 190, 902, 524, 82, 895, 119, 578, 989, 539, 814, 583, 563, 141, 391, 658, 314, 318, 72, 205, 529, 227, 262, 227, 424, 121, 610, 536, 551, 966, 688, 513, 345, 974, 389, 583, 503, 673, 849, 774, 762, 119, 969, 805, 505, 200, 337, 934, 554, 509, 716, 400, 452, 163, 48, 680, 509, 65, 329, 904, 894, 539, 584, 766, 116, 966, 630, 929, 265, 37, 233, 469, 547, 514, 815, 156, 716, 908, 841, 448, 222, 364, 597, 920, 63, 94, 156, 459, 568, 141, 940, 988, 590, 892, 437, 653, 875, 743, 320, 374, 723, 526, 413, 437, 351, 853, 710, 296, 826, 300, 907, 113, 779, 459, 731, 616, 618, 234, 678, 768, 387, 398, 644, 364, 969, 350, 315, 144, 825, 232, 156, 194, 84, 14, 236, 452, 358, 973, 197, 865, 312, 990, 114, 360, 680, 451, 134, 98, 109, 131, 297, 622, 953, 352, 104, 415, 881, 845, 36, 77, 967, 231, 400, 786, 740, 767, 591, 980, 541, 1, 123, 205, 397, 289, 524, 907, 548, 724, 518, 531, 721, 848, 756, 676, 189, 703, 698, 189, 705, 156, 194, 47, 402, 184, 693, 587, 845, 752, 4, 129, 524, 193, 22, 343, 184, 569, 836, 758, 598, 289, 221, 666, 535, 393, 578, 289, 945, 287, 21, 495, 619, 405, 447, 536, 875, 209, 119, 238, 144, 188, 488, 841, 687, 22, 737, 213, 55, 886, 798, 471, 308, 936, 400, 138, 480, 399, 37, 848, 226, 297, 633, 263, 116, 799, 45, 301, 578, 366, 518, 102, 53, 356, 632, 341, 534, 516, 296, 137, 305};

        List<List<String>> td = new ArrayList<>();

        for(int i = 0; i < 310; i++) {
            td.add(new ArrayList<>(Arrays.asList(Integer.toString(storeID[i]), Integer.toString(productID[i]), Integer.toString(quantity[i]))));
        }

        table.setTableData(new TableData(new ArrayList<>(Arrays.asList(7, 9, 8)), td));
        //System.out.println(table.toString());
        return table;
    }
}