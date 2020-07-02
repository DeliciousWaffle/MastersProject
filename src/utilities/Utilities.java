package utilities;

import datastructures.table.ResultSet;
import datastructures.user.TablePrivileges;
import datastructures.user.User;
import utilities.enums.Privilege;

import java.util.ArrayList;

/**
 * For now, basically where I stick methods that don't necessarily belong to any other class.
 */
public class Utilities {

    /**
     * Formats raw user file data into something usable. Basically takes the contents of
     * the user file, parsers it, and returns a list of Users.
     * @param usersFileData is data about the users in the system
     * @return list of users within the system
     */
    public static ArrayList<User> unSerializeUserData(String usersFileData) {

        String[] lines = usersFileData.split("\n");

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
    public static String serializeUserData(ArrayList<User> users) {

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

    public static ResultSet unSerializeTableData(String tableData) {

        String[] tableRows = tableData.split("\n");
        int numRows = tableRows.length;
        int numCols = tableRows[0].split("\\s+").length;

        String[][] data = new String[numRows][numCols];

        for(int rows = 0; rows < numRows; rows++) {
            data[rows] = tableRows[rows].split("\\s+");
        }

        return new ResultSet(new String[]{}, data);
    }

    public static String serializeTableData(ResultSet resultSet) {

        StringBuilder toSerialize = new StringBuilder();

        String[][] data = resultSet.getData();



        return toSerialize.toString();
    }
}
