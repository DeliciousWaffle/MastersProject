package datastructures.user;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.user.component.TablePrivileges;
import datastructures.user.component.Privilege;

import java.util.ArrayList;
import java.util.List;

public class User {

    // TODO major refactor and continue working on the compiler
    // only here to distinguish the DBA from other users, the DBA has access to everything
    public enum Type {
        DATABASE_ADMINISTRATOR, OTHER
    }

    private String username;
    private final Type type;
    private List<TablePrivileges> tablePrivilegesList;
    private List<TablePrivileges> passableTablePrivilegesList;

    public User() {

        this.username = "";
        this.type = Type.OTHER;
        this.tablePrivilegesList = new ArrayList<>();
        this.passableTablePrivilegesList = new ArrayList<>();
    }

    public User(String username, List<TablePrivileges> tablePrivilegesList,
                List<TablePrivileges> passableTablePrivilegesList) {

        this.username = username;
        this.type = Type.OTHER;
        this.tablePrivilegesList = tablePrivilegesList;
        this.passableTablePrivilegesList = passableTablePrivilegesList;
    }

    /**
     * Helper constructor for creating a database administrator, can't be accessed normally.
     * Will have access to everything within the system.
     */
    private User(String username, Type type, List<TablePrivileges> tablePrivilegesList,
                 List<TablePrivileges> passableTablePrivilegesList) {
        this.username = username;
        this.type = type;
        this.tablePrivilegesList = tablePrivilegesList;
        this.passableTablePrivilegesList = passableTablePrivilegesList;
    }

    /**
     * Returns a special user that will have access to everything within the system.
     * Calls the private constructor for creation.
     * @return the Database Administrator
     */
    public static User DatabaseAdministrator(List<Table> tables) {

        List<TablePrivileges> tablePrivilegesList = new ArrayList<>();
        List<TablePrivileges> passableTablePrivilegesList = new ArrayList<>();

        for(Table table : tables) {

            String tableName = table.getTableName();
            List<Privilege> privileges = Privilege.getAllPrivileges();
            List<String> updateColumns = new ArrayList<>();
            List<String> referenceColumns = new ArrayList<>();

            // need to get the update and reference columns
            for(Column column : table.getColumns()) {
                String columnName = column.getName();
                updateColumns.add(columnName);
                referenceColumns.add(columnName);
            }

            TablePrivileges tablePrivileges = new TablePrivileges(tableName, privileges,
                    updateColumns, referenceColumns);

            tablePrivilegesList.add(tablePrivileges);
            passableTablePrivilegesList.add(tablePrivileges);
        }

        return new User("DBA", Type.DATABASE_ADMINISTRATOR,
                tablePrivilegesList, passableTablePrivilegesList);
    }

    public void setUsername(String username) { this.username = username; }

    public String getUsername() { return username; }

    public boolean isDBA() {
        return type == Type.DATABASE_ADMINISTRATOR;
    }

    public void setTablePrivilegesList(List<TablePrivileges> tablePrivilegesList) {
        this.tablePrivilegesList = tablePrivilegesList;
    }

    /**
     * Adds table privileges to the list of table privileges available, if the user
     * already has privileges with the associated table, adds only new stuff.
     */
    public void addTablePrivileges(TablePrivileges tablePrivilegesToAdd) {

        // search through table privileges, checking if we already have the one added
        TablePrivileges duplicateTablePrivileges = null;

        for(TablePrivileges tablePrivileges : tablePrivilegesList) {
            String tableName = tablePrivileges.getTableName();
            if(tableName.equalsIgnoreCase(tablePrivilegesToAdd.getTableName())) {
                duplicateTablePrivileges = tablePrivileges;
                break;
            }
        }

        boolean hasDuplicateTablePrivileges = duplicateTablePrivileges != null;

        // if we have a duplicate, don't add a new table privileges to the list
        if(hasDuplicateTablePrivileges) {
            handleDuplicates(tablePrivilegesToAdd, duplicateTablePrivileges);
        } else {
            this.tablePrivilegesList.add(tablePrivilegesToAdd);
        }
    }

    public List<TablePrivileges> getTablePrivilegesList() {
        return tablePrivilegesList;
    }

    public TablePrivileges getTablePrivileges(String tableName) {

        for(TablePrivileges current : tablePrivilegesList) {
            if(current.getTableName().equalsIgnoreCase(tableName)) {
                return current;
            }
        }

        return new TablePrivileges();
    }

    public void setPassableTablePrivilegesList(List<TablePrivileges> passableTablePrivilegesList) {
        this.passableTablePrivilegesList = passableTablePrivilegesList;
    }

    /**
     * Adds table privileges to the list of table privileges available, if the user
     * already has privileges with the associated table, adds only new stuff.
     */
    public void addPassableTablePrivileges(TablePrivileges tablePrivilegesToAdd) {

        // search through passable table privileges, checking if we already have the one added
        TablePrivileges duplicateTablePrivileges = null;

        for(TablePrivileges tablePrivileges : passableTablePrivilegesList) {
            String tableName = tablePrivileges.getTableName();
            if(tableName.equalsIgnoreCase(tablePrivilegesToAdd.getTableName())) {
                duplicateTablePrivileges = tablePrivileges;
                break;
            }
        }

        boolean hasDuplicateTablePrivileges = duplicateTablePrivileges != null;

        // if we have a duplicate, don't add a new table privileges to the list
        if(hasDuplicateTablePrivileges) {
            handleDuplicates(tablePrivilegesToAdd, duplicateTablePrivileges);
        } else {
            this.passableTablePrivilegesList.add(tablePrivilegesToAdd);
        }
    }

    public List<TablePrivileges> getPassableTablePrivilegesList() {
        return passableTablePrivilegesList;
    }

    /**
     * Revokes the table privileges provided from the user, this removes what privileges they
     * can pass on as well.
     */
    public void revokeTablePrivileges(TablePrivileges tablePrivilegesToRevoke) {

        // removing the table privileges
        for(TablePrivileges tablePrivileges : tablePrivilegesList) {
            String tableName = tablePrivileges.getTableName();
            if(tableName.equalsIgnoreCase(tablePrivilegesToRevoke.getTableName())) {
                for(Privilege privilegeToRevoke: tablePrivilegesToRevoke.getPrivileges()) {
                    tablePrivileges.revokePrivilege(privilegeToRevoke);
                }
            }
        }

        // removing the passable table privileges
        for(TablePrivileges tablePrivileges : passableTablePrivilegesList) {
            String tableName = tablePrivileges.getTableName();
            if(tableName.equalsIgnoreCase(tablePrivilegesToRevoke.getTableName())) {
                for(Privilege privilegeToRevoke: tablePrivilegesToRevoke.getPrivileges()) {
                    tablePrivileges.revokePrivilege(privilegeToRevoke);
                }
            }
        }
    }

    public void revokeAllTablePrivileges(String tableNameToRemove) {
        for(TablePrivileges tablePrivileges : tablePrivilegesList) {
            String tableName = tablePrivileges.getTableName();
            if(tableName.equalsIgnoreCase(tableNameToRemove)) {
                tablePrivileges.revokeAllPrivileges();
                break;
            }
        }
        for(TablePrivileges passableTablePrivileges : passableTablePrivilegesList) {
            String tableName = passableTablePrivileges.getTableName();
            if(tableName.equalsIgnoreCase(tableNameToRemove)) {
                passableTablePrivileges.revokeAllPrivileges();
                break;
            }
        }
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
                                     List<String> candidateColumnNames) {

        TablePrivileges referencedTablePrivileges = null;

        for(TablePrivileges tablePrivilege : tablePrivilegesList) {
            if(tablePrivilege.getTableName().equalsIgnoreCase(candidateTable)) {
                referencedTablePrivileges = tablePrivilege;
            }
        }

        List<String> updateOrReferenceColumns = null;

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
                                     List<String> candidateColumnNames) {

        TablePrivileges referencedTablePrivileges = null;

        for(TablePrivileges tablePrivilege : passableTablePrivilegesList) {
            if(tablePrivilege.getTableName().equalsIgnoreCase(candidateTable)) {
                referencedTablePrivileges = tablePrivilege;
            }
        }

        List<String> updateOrReferenceColumns = null;

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

    private void handleDuplicates(TablePrivileges tablePrivilegesToAdd, TablePrivileges duplicateTablePrivileges) {

        // adding the privileges, Table Privileges takes care of duplicates
        for(Privilege privilegeToAdd : tablePrivilegesToAdd.getPrivileges()) {
            duplicateTablePrivileges.grantPrivilege(privilegeToAdd);
        }

        // add only new update columns, Table Privileges takes care of duplicates
        for(String updateColumnToAdd : tablePrivilegesToAdd.getUpdateColumns()) {
            duplicateTablePrivileges.addUpdateColumn(updateColumnToAdd);
        }

        // add only new reference columns, Table Privileges takes care of duplicates
        for(String referenceColumnToAdd : tablePrivilegesToAdd.getReferenceColumns()) {
            duplicateTablePrivileges.addReferenceColumn(referenceColumnToAdd);
        }
    }

    /**
     * @return a string representation of the data within this object
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Username: ").append(username).append("\n");
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