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

    /**
     * Basic constructor for creating everything that a user would have in the system.
     * @param username is the username of the user
     * @param tablePrivilegesList is a list of table privileges that the user has
     * @param grantedTablePrivilegesList is a list of granted table privileges that the user has
     */
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
     * The only differentiating feature is the presence of a user type
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
            List<Privilege> privileges = Privilege.getAllNonSpecialPrivileges();
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

    // setters and getters

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public User.Type getUserType() {
        return userType;
    }

    public void setTablePrivilegesList(List<TablePrivileges> tablePrivilegesList) {
        this.tablePrivilegesList = tablePrivilegesList;
    }

    public List<TablePrivileges> getTablePrivilegesList() {
        return tablePrivilegesList;
    }

    public void setGrantedTablePrivilegesList(List<TablePrivileges> grantedTablePrivilegesList) {
        this.grantedTablePrivilegesList = grantedTablePrivilegesList;
    }

    public List<TablePrivileges> getGrantedTablePrivilegesList() {
        return grantedTablePrivilegesList;
    }

    // utility methods

    /**
     * @return whether or not the current user is the DBA
     */
    public boolean isDBA() {
        return userType == Type.DATABASE_ADMINISTRATOR;
    }

    /**
     * @param tableNameToCheck is the name of the table privileges to check
     * @return whether a table privileges already exists within the list of table privileges
     */
    public boolean hasTablePrivileges(String tableNameToCheck) {

        boolean foundTablePrivileges = false;

        for (TablePrivileges tablePrivileges : tablePrivilegesList) {
            String tableName = tablePrivileges.getTableName();
            if (tableNameToCheck.equalsIgnoreCase(tableName)) {
                foundTablePrivileges = true;
                break;
            }
        }

        return foundTablePrivileges;
    }

    /**
     * Returns whether the user has all the privileges (including UPDATE and REFERENCES privilege
     * columns) on the given table.
     * @param table is the table to check
     * @return whether the user has all privileges for the given table
     */
    public boolean hasAllTablePrivileges(Table table) {
        TablePrivileges tablePrivileges = getTablePrivileges(table.getTableName());
        boolean tableExists = tablePrivileges != null;
        return tableExists && tablePrivileges.hasAllPrivileges(table);
    }

    /**
     * @param candidate is the name of the granted table privileges to check
     * @return whether a granted table privileges already exists within the list of granted table privileges
     */
    public boolean hasGrantedTablePrivileges(String candidate) {

        boolean foundGrantedTablePrivileges = false;

        for (TablePrivileges grantedTablePrivileges : grantedTablePrivilegesList) {
            String tableName = grantedTablePrivileges.getTableName();
            if (candidate.equalsIgnoreCase(tableName)) {
                foundGrantedTablePrivileges = true;
                break;
            }
        }

        return foundGrantedTablePrivileges;
    }

    /**
     * @param tableName is the name of the table privileges to get
     * @return a reference to the table privileges from the given table name or null if one is not found
     */
    public TablePrivileges getTablePrivileges(String tableName) {

        for (TablePrivileges tablePrivileges : tablePrivilegesList) {
            if (tableName.equalsIgnoreCase(tablePrivileges.getTableName())) {
                return tablePrivileges;
            }
        }

        return null;
    }

    /**
     * @param tableName is the name of the granted table privileges to get
     * @return a reference to the granted table privileges from the given table name or null if one is not found
     */
    public TablePrivileges getGrantedTablePrivileges(String tableName) {

        for (TablePrivileges grantedTablePrivileges : grantedTablePrivilegesList) {
            if (tableName.equalsIgnoreCase(grantedTablePrivileges.getTableName())) {
                return grantedTablePrivileges;
            }
        }

        return null;
    }

    /**
     * Adds table privileges to the list of table privileges available, if the user
     * already has privileges with the associated table, adds only new privileges to the table.
     * @param toAdd are the table privileges to add, omitting duplicate privileges
     */
    public void addTablePrivileges(TablePrivileges toAdd) {
        // table privileges already exists within the list of table privileges, only add new stuff
        if (hasTablePrivileges(toAdd.getTableName())) {
            // get a reference to the table privileges that already exists and add the new data
            // the TablePrivileges class omits duplicates by default
            TablePrivileges alreadyExists = getTablePrivileges(toAdd.getTableName());
            alreadyExists.grantPrivileges(toAdd.getPrivileges());
            alreadyExists.addUpdateColumns(toAdd.getUpdateColumns());
            alreadyExists.addReferencesColumns(toAdd.getReferenceColumns());
        // otherwise, just add the new data to the list
        } else {
            tablePrivilegesList.add(toAdd);
        }
    }

    /**
     * Adds a granted table privileges to the list of granted table privileges available, if the user
     * already has privileges with the associated table, adds only new privileges to the table.
     * @param toAdd are the granted table privileges to add, omitting duplicate privileges
     */
    public void addGrantedTablePrivileges(TablePrivileges toAdd) {
        if (hasGrantedTablePrivileges(toAdd.getTableName())) {
            TablePrivileges alreadyExists = getGrantedTablePrivileges(toAdd.getTableName());
            alreadyExists.grantPrivileges(toAdd.getPrivileges());
            alreadyExists.addUpdateColumns(toAdd.getUpdateColumns());
            alreadyExists.addReferencesColumns(toAdd.getReferenceColumns());
        } else {
            grantedTablePrivilegesList.add(toAdd);
        }
    }

    /**
     * Finds the table privileges and granted table privileges in the corresponding lists and removes
     * their privileges, update columns, and references columns that appear in the table privileges to revoke.
     * @param toRevoke contains the privileges, update columns, and references columns of the table
     * privileges and granted table privileges to remove
     */
    public void revokeTablePrivilegesAndGrantedTablePrivileges(TablePrivileges toRevoke) {

        TablePrivileges tablePrivileges = getTablePrivileges(toRevoke.getTableName());
        assert tablePrivileges != null;
        tablePrivileges.revokePrivileges(toRevoke.getPrivileges());
        tablePrivileges.removeUpdateColumns(toRevoke.getUpdateColumns());
        tablePrivileges.removeReferencesColumns(toRevoke.getReferenceColumns());
        // will need to add back the update and references privileges if columns remain
        if (! tablePrivileges.getUpdateColumns().isEmpty()) {
            tablePrivileges.grantPrivilege(Privilege.UPDATE);
        }
        if (! tablePrivileges.getReferenceColumns().isEmpty()) {
            tablePrivileges.grantPrivilege(Privilege.REFERENCES);
        }

        TablePrivileges grantedTablePrivileges = getGrantedTablePrivileges(toRevoke.getTableName());
        assert grantedTablePrivileges != null;
        grantedTablePrivileges.revokePrivileges(toRevoke.getPrivileges());
        grantedTablePrivileges.removeUpdateColumns(toRevoke.getUpdateColumns());
        grantedTablePrivileges.removeReferencesColumns(toRevoke.getReferenceColumns());
        if (! tablePrivileges.getUpdateColumns().isEmpty()) {
            tablePrivileges.grantPrivilege(Privilege.UPDATE);
        }
        if (! tablePrivileges.getReferenceColumns().isEmpty()) {
            tablePrivileges.grantPrivilege(Privilege.REFERENCES);
        }
    }

    /**
     * Finds the table privileges and granted table privileges in the corresponding lists and removes them.
     * @param toRemove is the table name of the table privileges to remove
     */
    public void revokeAllTablePrivilegesAndGrantedTablePrivileges(String toRemove) {

        for (int i = 0; i < tablePrivilegesList.size(); i++) {
            String tableName = tablePrivilegesList.get(i).getTableName();
            if (tableName.equalsIgnoreCase(toRemove)) {
                tablePrivilegesList.remove(i);
                break;
            }
        }

        for (int i = 0; i < grantedTablePrivilegesList.size(); i++) {
            String tableName = grantedTablePrivilegesList.get(i).getTableName();
            if (tableName.equalsIgnoreCase(toRemove)) {
                grantedTablePrivilegesList.remove(i);
                break;
            }
        }
    }

    /**
     * Completely removes the table privileges with the given name from the user.
     * Only called when a table gets dropped in the system
     * @param tableName is the name of table privileges to remove
     */
    public void removeTablePrivileges(String tableName) {
        for (int i = 0; i < tablePrivilegesList.size(); i++) {
            String currentName = tablePrivilegesList.get(i).getTableName();
            if (currentName.equalsIgnoreCase(tableName)) {
                tablePrivilegesList.remove(i);
                break;
            }
        }
    }

    /**
     * @param tableName is the table name to check
     * @param toCheck is the privilege to check in the given table name
     * @return whether there exists a privilege in the associated table privileges list
     */
    public boolean hasPrivilegeOnTable(String tableName, Privilege toCheck) {
        TablePrivileges tablePrivileges = getTablePrivileges(tableName);
        boolean tableExists = tablePrivileges != null;
        return tableExists && tablePrivileges.hasPrivilege(toCheck);
    }

    /**
     * @param tableName is the table name to check
     * @param toCheck is the granted privilege to check in the given table name
     * @return whether there exists a privilege in the associated granted table privileges list
     */
    public boolean hasGrantedPrivilegeOnTable(String tableName, Privilege toCheck) {
        TablePrivileges grantedTablePrivileges = getGrantedTablePrivileges(tableName);
        boolean tableExists = grantedTablePrivileges != null;
        return tableExists && grantedTablePrivileges.hasPrivilege(toCheck);
    }

    /**
     * @param tableName is the table name to check
     * @param columnsToCheck are the update columns to check
     * @return whether all update columns are present for the associated table privileges
     */
    public boolean hasUpdateColumnsOnTable(String tableName, List<String> columnsToCheck) {
        TablePrivileges tablePrivileges = getTablePrivileges(tableName);
        boolean tableExists = tablePrivileges != null;
        return tableExists && tablePrivileges.hasUpdateColumns(columnsToCheck);
    }

    /**
     * @param tableName is the table name to check
     * @param columnsToCheck are the references columns to check
     * @return whether all references columns are present for the associated table privileges
     */
    public boolean hasReferencesColumnsOnTable(String tableName, List<String> columnsToCheck) {
        TablePrivileges tablePrivileges = getTablePrivileges(tableName);
        boolean tableExists = tablePrivileges != null;
        return tableExists && tablePrivileges.hasReferencesColumns(columnsToCheck);
    }

    /**
     * @param tableName is the table name to check
     * @param columnsToCheck are the update columns to check
     * @return whether all update columns are present for the associated granted table privileges
     */
    public boolean hasGrantedUpdateColumnsOnTable(String tableName, List<String> columnsToCheck) {
        TablePrivileges grantedTablePrivileges = getGrantedTablePrivileges(tableName);
        boolean tableExists = grantedTablePrivileges != null;
        return tableExists && grantedTablePrivileges.hasUpdateColumns(columnsToCheck);
    }

    /**
     * @param tableName is the table name to check
     * @param columnsToCheck are the update columns to check
     * @return whether all references columns are present for the associated granted table privileges
     */
    public boolean hasGrantedReferencesColumnsOnTable(String tableName, List<String> columnsToCheck) {
        TablePrivileges grantedTablePrivileges = getGrantedTablePrivileges(tableName);
        boolean tableExists = grantedTablePrivileges != null;
        return tableExists && grantedTablePrivileges.hasReferencesColumns(columnsToCheck);
    }

    /**
     * @return a string representation of the data within this object
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Username: ").append(username).append("\n");
        stringBuilder.append("Privileges: ");

        if (! tablePrivilegesList.isEmpty()) {

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

        if (! grantedTablePrivilegesList.isEmpty()) {

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