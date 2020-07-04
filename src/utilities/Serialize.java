package utilities;

import datastructures.table.Table;
import datastructures.table.component.Column;
import datastructures.table.component.TableData;
import datastructures.user.TablePrivileges;
import datastructures.user.User;
import utilities.enums.Privilege;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Responsible for serializing and un-serializing all data so that it can be used in
 * a meaningful way. This essentially saves the states of all objects used so that they
 * be accessed later when the application is relaunched.
 * Right now, methods of this class is accessed statically for convenience. May change later.
 */
public class Serialize {

    /**
     * Formats raw user file data into something usable. Basically takes the contents of
     * the user file, parsers it, and returns a list of Users.
     * @param serializedUsers is data about the users in the system
     * @return list of users within the system
     */
    public static ArrayList<User> unSerializeUsers(String serializedUsers) {

        String[] lines = serializedUsers.split("\n");

        // remove any leading or trailing whitespace
        for(int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }

        ArrayList<User> users = new ArrayList<>();

        // "has a" relationship working downward
        // data associated with a single user
        User user = new User();

        // acts as a passable table privileges list too
        ArrayList<TablePrivileges> tablePrivilegesList = new ArrayList<>();

        // data associated with a single tablePrivilegesList or passableTablePrivilegesList
        TablePrivileges tablePrivileges = new TablePrivileges();

        // data associated with a single tablePrivileges
        ArrayList<String> updateColumns = new ArrayList<>();
        ArrayList<String> referenceColumns = new ArrayList<>();

        for(int i = 0; i < lines.length; i++) {

            String currentLine = lines[i];

            // lines that contain "DONE" determine when to add and instantiate variables
            switch(currentLine) {
                case "USER DONE":
                    users.add(user);
                    user = new User();
                    break;
                case "TABLE PRIVILEGES LIST DONE":
                    user.setTablePrivilegesList(tablePrivilegesList);
                    tablePrivilegesList = new ArrayList<>();
                    break;
                case "PASSABLE TABLE PRIVILEGES LIST DONE":
                    user.setPassableTablePrivilegesList(tablePrivilegesList);
                    tablePrivilegesList = new ArrayList<>();
                    break;
                case "TABLE PRIVILEGES DONE":
                    tablePrivilegesList.add(tablePrivileges);
                    tablePrivileges = new TablePrivileges();
                    break;
                case "UPDATE PRIVILEGE DONE":
                    tablePrivileges.setUpdateColumns(updateColumns);
                    updateColumns = new ArrayList<>();
                    break;
                case "REFERENCES PRIVILEGE DONE":
                    tablePrivileges.setReferenceColumns(referenceColumns);
                    referenceColumns = new ArrayList<>();
                    break;
                // lines that don't contain "DONE" have some form of data to add
                default: {
                    String[] tokens = currentLine.split(": ");
                    String type = tokens[0];
                    String data = tokens[1];

                    switch(type) {
                        case "User":
                            user.setUsername(data);
                            break;
                        case "TablePrivilegesList":
                        case "PassableTablePrivilegesList":
                            continue;
                        case "TablePrivileges":
                            tablePrivileges.setTableName(data);
                            break;
                        case "Privilege":
                            tablePrivileges.grantPrivilege(Privilege.convertToPrivilege(data));
                            break;
                        case "UpdateColumns":
                            String[] updateColumnsTokens = data.split("\\s+");
                            for(String updateColumn : updateColumnsTokens) {
                                updateColumns.add(updateColumn);
                            }
                            break;
                        case "ReferencesColumns":
                            String[] referenceColumnsTokens = data.split("\\s+");
                            for(String referenceColumn : referenceColumnsTokens) {
                                referenceColumns.add(referenceColumn);
                            }
                            break;
                        default: {
                            System.out.println("In Utilities.createUserData()");
                            System.out.println("Unknown Input @ Line "+ i + ": " + currentLine);
                            return new ArrayList<>();
                        }
                    }

                    break;
                }
            }
        }

        return users;
    }

    /**
     * Takes all the users within the system and converts all of their data into
     * a string which will eventually be written out to disk. Stores every users'
     * state so that it can be restored when the application is re-launched.
     * @param users the users within the system
     * @return string representation of all the users within the system to write out
     */
    public static String serializedUsers(ArrayList<User> users) {

        StringBuilder toSerialize = new StringBuilder();

        for(User user : users) {

            toSerialize.append("User: ").append(user.getUsername()).append("\n");

            ArrayList<TablePrivileges> tablePrivilegesList = user.getTablePrivilegesList();
            toSerialize.append("\t").append("TablePrivilegesList: ");

            if(! tablePrivilegesList.isEmpty()) {

                toSerialize.append("NOT EMPTY").append("\n");

                for(TablePrivileges tablePrivileges : tablePrivilegesList) {

                    toSerialize.append("\t\t").append("TablePrivileges: ").
                            append(tablePrivileges.getTableName()).append("\n");

                    ArrayList<Privilege> privileges = tablePrivileges.getPrivileges();

                    for(Privilege privilege : privileges) {

                        toSerialize.append("\t\t\t").append("Privilege: ").append(privilege).append("\n");

                        if(privilege == Privilege.UPDATE) {

                            ArrayList<String> updateColumns = tablePrivileges.getUpdateColumns();
                            toSerialize.append("\t\t\t\t").append("UpdateColumns: ");

                            for(String updateColumn : updateColumns) {
                                toSerialize.append(updateColumn).append(" ");
                            }

                            // remove " "
                            toSerialize.deleteCharAt(toSerialize.length() - 1);

                            toSerialize.append("\n");
                            toSerialize.append("\t\t\t").append("UPDATE PRIVILEGE DONE").append("\n");
                        }

                        if(privilege == Privilege.REFERENCES) {

                            ArrayList<String> referenceColumns = tablePrivileges.getReferenceColumns();
                            toSerialize.append("\t\t\t\t").append("ReferencesColumns: ");

                            for(String referenceColumn : referenceColumns) {
                                toSerialize.append(referenceColumn).append(" ");
                            }

                            // remove " "
                            toSerialize.deleteCharAt(toSerialize.length() - 1);

                            toSerialize.append("\n");
                            toSerialize.append("\t\t\t").append("REFERENCES PRIVILEGE DONE").append("\n");
                        }
                    }

                    toSerialize.append("\t\t").append("TABLE PRIVILEGES DONE").append("\n");
                }

            } else {
                toSerialize.append("EMPTY").append("\n");
            }

            toSerialize.append("\t").append("TABLE PRIVILEGES LIST DONE").append("\n");

            // huge repeat of a bunch for passable table privileges
            ArrayList<TablePrivileges> passableTablePrivilegesList = user.getPassableTablePrivilegesList();
            toSerialize.append("\t").append("PassableTablePrivilegesList: ");

            if(! passableTablePrivilegesList.isEmpty()) {

                toSerialize.append("NOT EMPTY").append("\n");

                for(TablePrivileges tablePrivileges : passableTablePrivilegesList) {

                    toSerialize.append("\t\t").append("TablePrivileges: ").
                            append(tablePrivileges.getTableName()).append("\n");

                    ArrayList<Privilege> privileges = tablePrivileges.getPrivileges();

                    for(Privilege privilege : privileges) {

                        toSerialize.append("\t\t\t").append("Privilege: ").append(privilege).append("\n");

                        if(privilege == Privilege.UPDATE) {

                            ArrayList<String> updateColumns = tablePrivileges.getUpdateColumns();
                            toSerialize.append("\t\t\t\t").append("UpdateColumns: ");

                            for(String updateColumn : updateColumns) {
                                toSerialize.append(updateColumn).append(" ");
                            }

                            // remove " "
                            toSerialize.deleteCharAt(toSerialize.length() - 1);

                            toSerialize.append("\n");
                            toSerialize.append("\t\t\t").append("UPDATE PRIVILEGE DONE").append("\n");
                        }

                        if(privilege == Privilege.REFERENCES) {

                            ArrayList<String> referenceColumns = tablePrivileges.getReferenceColumns();
                            toSerialize.append("\t\t\t\t").append("ReferencesColumns: ");

                            for(String referenceColumn : referenceColumns) {
                                toSerialize.append(referenceColumn).append(" ");
                            }

                            // remove " "
                            toSerialize.deleteCharAt(toSerialize.length() - 1);

                            toSerialize.append("\n");
                            toSerialize.append("\t\t\t").append("REFERENCES PRIVILEGE DONE").append("\n");
                        }
                    }

                    toSerialize.append("\t\t").append("TABLE PRIVILEGES DONE").append("\n");
                }

            } else {
                toSerialize.append("EMPTY").append("\n");
            }

            toSerialize.append("\t").append("PASSABLE TABLE PRIVILEGES LIST DONE").append("\n");
            toSerialize.append("USER DONE").append("\n");
        }

        return toSerialize.toString();
    }

    /**
     * Formats raw table file data into something usable. Basically takes the contents of
     * the tables file, parsers it, and returns a list of Tables.
     * @param serializedTables is data about the tables in the system
     * @return list of tables within the system
     */
    public static ArrayList<Table> unSerializeTables(String serializedTables) {

        return new ArrayList<>();
    }

    /**
     * Takes all the tables within the system and converts all of their data into
     * a string which will eventually be written out to disk. Stores every tables'
     * state so that it can be restored when the application is re-launched.
     * @param tables a list of tables within the system
     * @return string representation of all the tables within the system to write out
     */
    public static String serializeTables(ArrayList<Table> tables) {

        return "";
    }

    // TODO will need to split along the size of each column!!!

    /**
     * Formats raw table data (ie. the rows and columns of a table) into something usable.
     * Basically takes the contents of a table data file, parsers it, and returns the Table Data.
     * @param serializedTableData is data from a single table
     * @param table contains information about what the table data means
     * @return the table data
     */
    public static TableData unSerializeTableData(String serializedTableData, Table table) {

        // string.split() will be very useful here
        String[] tableRows = serializedTableData.split("\n");

        int numRows = tableRows.length;
        int numCols = tableRows[0].split("\\t+").length;

        // used for formatting purposes
        ArrayList<Integer> paddingAmountList = new ArrayList<>();

        for(Column column : table.getColumns()) {

            //int columnNameLength = column.getName().length();
            //int columnSize = column.size();
            //int paddingAmount = columnNameLength + columnSize;
System.out.println(column.size());
            paddingAmountList.add(column.size());
        }
System.out.println();
        // skip the column names and dashed lines
        String[][] splitTableData = new String[numRows - 2][];

        for(int rows = 2; rows < numRows; rows++) {
            splitTableData[rows - 2] = tableRows[rows].split("\\t+");
        }

        // TODO remove
        /*for(String[] rows : splitTableData) {
            StringBuilder sb = new StringBuilder();
            for(String cols : rows) {
                sb.append("\"").append(cols).append("\"").append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            System.out.println(sb.toString());
        }*/

        // convert 2D array to 2D array list for Table Data
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();

        for(int rows = 0; rows < numRows - 2; rows++) {

            ArrayList<String> columns = new ArrayList<>();
            // TODO remove
            //System.out.println(Arrays.toString(splitTableData[rows]));
            for(int cols = 0; cols < numCols; cols++) {
                columns.add(splitTableData[rows][cols]);
            }

            tableData.add(columns);
        }

        return new TableData(paddingAmountList, tableData);
    }

    /**
     * Takes the table data of a table and converts it into a string which will
     * eventually be written out to disk. This means a single tables' table data
     * can be restored when the application is re-launched.
     * @param table contains information about what the table data means
     * @return string representation of the table data to write out
     */
    public static String serializeTableData(Table table) {

        StringBuilder toSerialize = new StringBuilder();

        // to make things pretty and organized
        StringBuilder columnNames = new StringBuilder();
        StringBuilder dashes = new StringBuilder();

        // adding column names so we know what we're looking at
        for(Column column : table.getColumns()) {

            String columnName = column.getName();

            StringBuilder spaces = new StringBuilder();
            int columnNameLength = columnName.length();
            int maxNumSpaces = column.size();
            int numSpacesToPad = Math.abs(maxNumSpaces - columnNameLength);

            for(int i = 0; i < numSpacesToPad; i++) {
                spaces.append(" ");
            }

            for(int i = 0; i < maxNumSpaces; i++) {
                dashes.append("-");
            }

            columnNames.append(columnName).append(spaces).append(" ");
            dashes.append(" ");
        }

        toSerialize.append(columnNames).append("\n");
        toSerialize.append(dashes).append("\n");

        // add the table data
        TableData tableData = table.getTableData();
        toSerialize.append(tableData.toString());

        return toSerialize.toString();
    }
}
