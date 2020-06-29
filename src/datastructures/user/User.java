package datastructures.user;

import utilities.enums.Privilege;

import java.util.ArrayList;
import java.util.Scanner;

public class User {

    private String username;
    private boolean hasAllPrivileges;
    private ArrayList<TablePrivileges> tablePrivileges;
    private ArrayList<TablePrivileges> passableTablePrivileges;

    public User() {}

    public User(String username, boolean hasAllPrivileges, ArrayList<TablePrivileges> tablePrivileges,
                ArrayList<TablePrivileges> passableTablePrivileges) {

        this.username = username;
        this.hasAllPrivileges = hasAllPrivileges;
        this.tablePrivileges = tablePrivileges;
        this.passableTablePrivileges = passableTablePrivileges;
    }

    /**
     * Special user of the system that can basically do whatever he wants.
     * @return the DBA
     */
    public static User DatabaseAdministrator() {
        return new User("DatabaseAdministrator", true, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Formats raw user file data into something usable.
     * @param usersFileData is data about the users in the system
     */
    public static ArrayList<User> createUserData(String usersFileData) {

        ArrayList<User> users = new ArrayList<>();

        String username = "";

        ArrayList<TablePrivileges> tablePrivilegesList = new ArrayList<>();
        ArrayList<TablePrivileges> passableTablePrivilegesList = new ArrayList<>();
        TablePrivileges tablePrivileges = new TablePrivileges();
        String tableName = "";
        ArrayList<Privilege> privileges = new ArrayList<>();
        Privilege privilege;

        ArrayList<String> updateColumns = new ArrayList<>();
        ArrayList<String> referenceColumns = new ArrayList<>();

        Scanner scanner = new Scanner(usersFileData);

        while(scanner.hasNext()) {

            String currentLine = scanner.nextLine();
            currentLine = currentLine.trim();

            if(currentLine.equals("USER DONE")) {
                users.add(new User(username, false, tablePrivilegesList, passableTablePrivilegesList));
                continue;
            }

            String[] tokens = currentLine.split(": ");
            String type = tokens[0];
            String data = tokens[1];

            switch(type) {
                case "User":
                    username = data;
                    break;
                case "TablePrivileges":
                    continue;
                case "Table":
                    tableName = data;
                    break;
                case "Privilege":
                    privilege = Privilege.convertToPrivilege(data);
                    break;
                case "References Columns":
                    referenceColumns = new ArrayList<>(); // TODO
                    break;
                case "Update Columns":
                    updateColumns = new ArrayList<>();
                    break;
                case "PassableTablePrivileges":
                    passableTablePrivilegesList = new ArrayList<>();
                    break;
                default:
                    System.out.println("In User.createUserData()");
                    System.out.println("Unknown Input");
                    return new ArrayList<User>();
            }

        }

        return users;
    }

    public void setUserName(String username) { this.username = username; }

    public String getUsername() { return username; }

    public void setHasAllPrivileges(boolean hasAllPrivileges) { this.hasAllPrivileges = hasAllPrivileges; }

    public boolean hasAllPrivileges() { return hasAllPrivileges; }

    public void setTablePrivileges(ArrayList<TablePrivileges> tablePrivileges) {
        this.tablePrivileges = tablePrivileges;
    }

    public ArrayList<TablePrivileges> getTablePrivileges() { return tablePrivileges; }

    public TablePrivileges getTablePrivileges(String tableName) {

        for(TablePrivileges current : tablePrivileges) {
            if(current.getTableName().equalsIgnoreCase(tableName)) {
                return current;
            }
        }

        return null;
    }

    public ArrayList<TablePrivileges> getPassableTablePrivileges() { return passableTablePrivileges; }

    public void setPassableTablePrivileges(ArrayList<TablePrivileges> passableTablePrivileges) {
        this.passableTablePrivileges = passableTablePrivileges;
    }

    public boolean hasTablePrivilege(String candidateTable, Privilege candidatePrivilege) {

        for(TablePrivileges tablePrivilege : tablePrivileges) {
            if(tablePrivilege.getTableName().equalsIgnoreCase(candidateTable)) {
                if(tablePrivilege.hasPrivilege(candidatePrivilege)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasTablePrivilege(String candidateTable, Privilege candidatePrivilege,
                                     ArrayList<String> candidateColumnNames) {

        TablePrivileges referencedTablePrivileges = null;

        for(TablePrivileges tablePrivilege : tablePrivileges) {
            if(tablePrivilege.getTableName().equalsIgnoreCase(candidateTable)) {
                referencedTablePrivileges = tablePrivilege;
            }
        }

        ArrayList<String> updateOrReferenceColumns;

        if(candidatePrivilege == Privilege.UPDATE) {
            updateOrReferenceColumns = referencedTablePrivileges.getUpdateColumns();
        } else {
            updateOrReferenceColumns = referencedTablePrivileges.getReferenceColumns();
        }

        for(String candidateColumn : candidateColumnNames) {

            boolean foundColumn = false;

            for(String column : updateOrReferenceColumns) {
                if(column.equalsIgnoreCase(candidateColumn)) {
                    foundColumn = true;
                }
            }

            if(! foundColumn) {
                return false;
            }
        }

        return true;
    }

    public boolean hasGrantedTablePrivilege(String candidateTable, Privilege candidatePrivilege) {

        for(TablePrivileges tablePrivilege : passableTablePrivileges) {
            if(tablePrivilege.getTableName().equalsIgnoreCase(candidateTable)) {
                if(tablePrivilege.hasPrivilege(candidatePrivilege)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasGrantedTablePrivilege(String candidateTable, Privilege candidatePrivilege,
                                     ArrayList<String> candidateColumnNames) {

        TablePrivileges referencedTablePrivileges = null;

        for(TablePrivileges tablePrivilege : passableTablePrivileges) {
            if(tablePrivilege.getTableName().equalsIgnoreCase(candidateTable)) {
                referencedTablePrivileges = tablePrivilege;
            }
        }

        ArrayList<String> updateOrReferenceColumns;

        if(candidatePrivilege == Privilege.UPDATE) {
            updateOrReferenceColumns = referencedTablePrivileges.getUpdateColumns();
        } else {
            updateOrReferenceColumns = referencedTablePrivileges.getReferenceColumns();
        }

        for(String candidateColumn : candidateColumnNames) {

            boolean foundColumn = false;

            for(String column : updateOrReferenceColumns) {
                if(column.equalsIgnoreCase(candidateColumn)) {
                    foundColumn = true;
                }
            }

            if(! foundColumn) {
                return false;
            }
        }

        return true;
    }

    public void printUserData() {

        StringBuilder userData = new StringBuilder();

        userData.append("Username: ").append(username).append("\n");

        for(TablePrivileges tablePrivilege : tablePrivileges) {

            String tableName = tablePrivilege.getTableName();
            userData.append("\t").append(tableName).append("\n");

            for(Privilege privilege : tablePrivilege.getPrivileges()) {

                userData.append("\t\t").append(privilege).append("\n");

                if(tablePrivilege.hasPrivilege(Privilege.UPDATE)) {
                    for(String column : tablePrivilege.getUpdateColumns()) {
                        userData.append("\t\t\t").append(column).append("\n");
                    }
                }

                if(tablePrivilege.hasPrivilege(Privilege.REFERENCES)) {
                    for(String column : tablePrivilege.getReferenceColumns()) {
                        userData.append("\t\t\t").append(column).append("\n");
                    }
                }
            }
        }

        System.out.println(userData.toString());
    }
}