package utilities;

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
     * @param users
     */
    public static void serializeUserData(ArrayList<User> users) {

    }
}
