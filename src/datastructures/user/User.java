package datastructures.user;

import utilities.Privilege;

import java.util.ArrayList;

public class User {

    private String userName;
    private boolean allPrivileges;
    private ArrayList<TablePrivileges> tablePrivileges;
    private ArrayList<TablePrivileges> grantedTablePrivileges;

    public User() {
        userName = "Database Administrator";
        allPrivileges = true;
    }

    public User(String userName, ArrayList<TablePrivileges> tablePrivileges) {

        this.userName = userName;
        allPrivileges = false;
        this.tablePrivileges = tablePrivileges;
        grantedTablePrivileges = new ArrayList<>();
    }

    public void setUserName(String userName) { this.userName = userName; }

    public String getUserName() { return userName; }

    public void setAllPrivileges(boolean allPrivileges) { this.allPrivileges = allPrivileges; }

    public boolean hasAllPrivileges() { return allPrivileges; }

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

    public ArrayList<TablePrivileges> getGrantedTablePrivileges() { return grantedTablePrivileges; }

    public void setGrantedTablePrivileges(ArrayList<TablePrivileges> grantedTablePrivileges) {
        this.grantedTablePrivileges = grantedTablePrivileges;
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

        for(TablePrivileges tablePrivilege : grantedTablePrivileges) {
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

        for(TablePrivileges tablePrivilege : grantedTablePrivileges) {
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

        userData.append("Username: ").append(userName).append("\n");

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