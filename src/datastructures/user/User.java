package datastructures.user;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.user.component.TablePrivileges;
import datastructures.user.component.Privilege;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO rework this class a little bit, need to handle the case in which the user is being granted
 * new table privileges and other stuff
 * Class represents a user of the system. The user can take the form of a database administrator or a regular
 * user. The DBA has access to all tables and can basically do whatever he wants. Regular users need to
 * be granted privileges in order to perform operations. Each user has a list of table privileges which is
 * essentially a list of privileges on a single table. These table privileges determine what kinds of commands
 * the user can execute. Similarly, the have a list of table privileges that they are allowed to pass on to
 * other users called "grantedTablePrivileges". These get added to when a user (who has the privilege(s)) grants
 * privileges to another user and include the "WITH GRANT OPTION" allowing that user to pass on those privileges
 * to other users if they wish.
 */
public class User {

    // only here to distinguish the DBA from other users, the DBA has access to everything
    public enum Type {
        DATABASE_ADMINISTRATOR, OTHER
    }

    private String username;
    private final Type userType;
    private List<TablePrivileges> tablePrivilegesList;
    private List<TablePrivileges> grantedTablePrivilegesList;

    /**
     * Default constructor, should only be called when serializing or un-serializing data.
     */
    public User() {
        this.username = "";
        this.userType = Type.OTHER;
        this.tablePrivilegesList = new ArrayList<>();
        this.grantedTablePrivilegesList = new ArrayList<>();
    }

    public User(String username, List<TablePrivileges> tablePrivilegesList,
                List<TablePrivileges> grantedTablePrivilegesList) {
        this.username = username;
        this.userType = Type.OTHER;
        this.tablePrivilegesList = tablePrivilegesList;
        this.grantedTablePrivilegesList = grantedTablePrivilegesList;
    }

    /**
     * Helper constructor for the method below which creates a database administrator. This
     * constructor can't be accessed normally. Will have access to everything within the system.
     */
    private User(String username, Type userType, List<TablePrivileges> tablePrivilegesList,
                 List<TablePrivileges> grantedTablePrivilegesList) {
        this.username = username;
        this.userType = userType;
        this.tablePrivilegesList = tablePrivilegesList;
        this.grantedTablePrivilegesList = grantedTablePrivilegesList;
    }

    /**
     * Returns a special user that will have access to everything within the system.
     * Calls the private constructor for creation.
     * @param tables are all the tables in the system
     * @return the Database Administrator
     */
    public static User DatabaseAdministrator(List<Table> tables) {

        List<TablePrivileges> tablePrivilegesList = new ArrayList<>();
        List<TablePrivileges> passableTablePrivilegesList = new ArrayList<>();

        for (Table table : tables) {

            String tableName = table.getTableName();
            List<Privilege> privileges = Privilege.getAllPrivilegesExceptUnknown();
            List<String> updateColumns = new ArrayList<>();
            List<String> referenceColumns = new ArrayList<>();

            // need to get the update and reference columns
            for (Column column : table.getColumns()) {
                String columnName = column.getColumnName();
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
        return userType == Type.DATABASE_ADMINISTRATOR;
    }

    public void setTablePrivilegesList(List<TablePrivileges> tablePrivilegesList) {
        this.tablePrivilegesList = tablePrivilegesList;
    }

    /**
     * Adds table privileges to the list of table privileges available, if the user
     * already has privileges with the associated table, adds only new stuff.
     * @param tablePrivilegesToAdd are the table privileges to add
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
            tablePrivilegesList.add(tablePrivilegesToAdd);
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

    public TablePrivileges getGrantedTablePrivileges(String tableName) {

        for (TablePrivileges current : grantedTablePrivilegesList) {
            if(current.getTableName().equalsIgnoreCase(tableName)) {
                return current;
            }
        }

        return new TablePrivileges();
    }

    public void setGrantedTablePrivilegesList(List<TablePrivileges> grantedTablePrivilegesList) {
        this.grantedTablePrivilegesList = grantedTablePrivilegesList;
    }

    /**
     * Adds table privileges to the list of table privileges available, if the user
     * already has privileges with the associated table, adds only new stuff.
     */
    public void addPassableTablePrivileges(TablePrivileges tablePrivilegesToAdd) {

        // search through passable table privileges, checking if we already have the one added
        TablePrivileges duplicateTablePrivileges = null;

        for(TablePrivileges tablePrivileges : grantedTablePrivilegesList) {
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
            this.grantedTablePrivilegesList.add(tablePrivilegesToAdd);
        }
    }

    public List<TablePrivileges> getGrantedTablePrivilegesList() {
        return grantedTablePrivilegesList;
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
        for(TablePrivileges tablePrivileges : grantedTablePrivilegesList) {
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
        for(TablePrivileges passableTablePrivileges : grantedTablePrivilegesList) {
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

        assert candidatePrivilege == Privilege.UPDATE || candidatePrivilege == Privilege.REFERENCES;

        TablePrivileges referencedTablePrivileges = null;

        for(TablePrivileges tablePrivilege : tablePrivilegesList) {
            if(tablePrivilege.getTableName().equalsIgnoreCase(candidateTable)) {
                referencedTablePrivileges = tablePrivilege;
            }
        }

        // doesn't have any privileges with the associated table
        if (referencedTablePrivileges == null) {
            return false;
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

        for(TablePrivileges tablePrivilege : grantedTablePrivilegesList) {
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

        for(TablePrivileges tablePrivilege : grantedTablePrivilegesList) {
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
            duplicateTablePrivileges.addReferencesColumn(referenceColumnToAdd);
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

        if(! grantedTablePrivilegesList.isEmpty()) {

            stringBuilder.append("\n");

            for (TablePrivileges tablePrivilege : grantedTablePrivilegesList) {
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
}