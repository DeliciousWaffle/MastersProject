package datastructure.user;

import datastructure.user.component.TablePrivileges;
import datastructure.user.component.Privilege;

import java.util.ArrayList;

public class User {

    private String username;
    private boolean hasAllPrivileges;
    private ArrayList<TablePrivileges> tablePrivilegesList;
    private ArrayList<TablePrivileges> passableTablePrivilegesList;

    public User() {

        this.username = "";
        this.hasAllPrivileges = false;
        this.tablePrivilegesList = new ArrayList<>();
        this.passableTablePrivilegesList = new ArrayList<>();
    }

    public User(String username, boolean hasAllPrivileges, ArrayList<TablePrivileges> tablePrivilegesList,
                ArrayList<TablePrivileges> passableTablePrivilegesList) {

        this.username = username;
        this.hasAllPrivileges = false;
        this.tablePrivilegesList = tablePrivilegesList;
        this.passableTablePrivilegesList = passableTablePrivilegesList;
    }

    /**
     * Special user of the system that can basically do whatever he wants.
     * @return the Database Administrator
     */
    public static User DatabaseAdministrator() {
        return new User("DatabaseAdministrator", true, new ArrayList<>(), new ArrayList<>());
    }

    public void setUsername(String username) { this.username = username; }

    public String getUsername() { return username; }

    public boolean hasAllPrivileges() { return hasAllPrivileges; }

    public void addTablePrivileges(TablePrivileges tablePrivileges) {
        this.tablePrivilegesList.add(tablePrivileges);
    }

    public void setTablePrivilegesList(ArrayList<TablePrivileges> tablePrivilegesList) {
        this.tablePrivilegesList = tablePrivilegesList;
    }

    public ArrayList<TablePrivileges> getTablePrivilegesList() { return tablePrivilegesList; }

    public TablePrivileges getTablePrivileges(String tableName) {

        for(TablePrivileges current : tablePrivilegesList) {
            if(current.getTableName().equalsIgnoreCase(tableName)) {
                return current;
            }
        }

        return new TablePrivileges();
    }

    public ArrayList<TablePrivileges> getPassableTablePrivilegesList() { return passableTablePrivilegesList; }

    public void setPassableTablePrivilegesList(ArrayList<TablePrivileges> passableTablePrivilegesList) {
        this.passableTablePrivilegesList = passableTablePrivilegesList;
    }

    public boolean hasTablePrivilege(String candidateTable, Privilege candidatePrivilege) {

        for(TablePrivileges tablePrivilege : tablePrivilegesList) {
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

        for(TablePrivileges tablePrivilege : tablePrivilegesList) {
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

        for(TablePrivileges tablePrivilege : passableTablePrivilegesList) {
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

        for(TablePrivileges tablePrivilege : passableTablePrivilegesList) {
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

    /**
     * @return a string representation of the data within this object
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Username: ").append(username).append("\n");
        stringBuilder.append("Has All Privileges: ").append(hasAllPrivileges ? "Yes" : "No").append("\n");
        stringBuilder.append("Privileges: ");

        if(! tablePrivilegesList.isEmpty()) {

            stringBuilder.append("\n");

            for (TablePrivileges tablePrivilege : tablePrivilegesList) {
                stringBuilder.append("\t").append("Privileges On Table: ").append(tablePrivilege.getTableName()).
                        append("\n").append(tablePrivilege).append("\n");
            }

            // remove "\n"
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        } else {

            stringBuilder.append("None");
        }

        stringBuilder.append("\n").append("Passable Privileges: ");

        if(! passableTablePrivilegesList.isEmpty()) {

            stringBuilder.append("\n");

            for (TablePrivileges tablePrivilege : passableTablePrivilegesList) {
                stringBuilder.append("\t").append("Privileges On Table: ").append(tablePrivilege.getTableName()).
                        append("\n").append(tablePrivilege).append("\n");
            }

            // remove "\n"
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        } else {

            stringBuilder.append("None");
        }

        return stringBuilder.toString();
    }

    public void printUserData() {

        StringBuilder userData = new StringBuilder();

        userData.append("Username: ").append(username).append("\n");

        for(TablePrivileges tablePrivilege : tablePrivilegesList) {

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