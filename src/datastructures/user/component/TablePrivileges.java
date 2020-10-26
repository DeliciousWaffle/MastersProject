package datastructures.user.component;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static datastructures.user.component.Privilege.*;

/**
 * Represents a list of privileges on a particular table.
 */
public class TablePrivileges {

    private String tableName;
    private List<Privilege> privileges;
    private List<String> updateColumns, referenceColumns;

    /**
     * Default constructor that should only be used for serialization or un-serialization.
     */
    public TablePrivileges() {
        this.tableName = "";
        this.privileges = new ArrayList<>();
        this.updateColumns = new ArrayList<>();
        this.referenceColumns = new ArrayList<>();
    }

    /**
     * Constructor consisting of the table and list of privileges granted for that associated table.
     * @param tableName is the table name
     * @param privileges is the list of privileges granted on this particular table
     */
    public TablePrivileges(String tableName, List<Privilege> privileges) {
        this.tableName = tableName;
        this.privileges = privileges;
        this.updateColumns = new ArrayList<>();
        this.referenceColumns = new ArrayList<>();
    }

    /**
     * Constructor that's the same as the above, but with the inclusion of update and reference columns.
     * @param tableName is the table name
     * @param privileges is the list of privileges granted on this particular table
     * @param updateColumns are the columns associated with the UPDATE privilege
     * @param referenceColumns are the columns associated with the REFERENCES privilege
     */
    public TablePrivileges(String tableName, List<Privilege> privileges,
                           List<String> updateColumns, List<String> referenceColumns) {
        this(tableName, privileges);
        this.updateColumns = updateColumns;
        this.referenceColumns = referenceColumns;
    }

    /**
     * Creates a deep copy of a TablePrivileges object.
     * @param toCopy is the TablePrivileges to copy
     */
    public TablePrivileges(TablePrivileges toCopy) {
        this.tableName = toCopy.tableName;
        this.privileges = new ArrayList<>(toCopy.privileges);
        this.updateColumns = new ArrayList<>(toCopy.updateColumns);
        this.referenceColumns = new ArrayList<>(toCopy.referenceColumns);
    }

    // getters and setters

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setUpdateColumns(List<String> updateColumns) {
        assert hasPrivilege(UPDATE);
        this.updateColumns = updateColumns;
    }

    public List<String> getUpdateColumns() {
        return updateColumns;
    }

    public void setReferenceColumns(List<String> referenceColumns) {
        assert hasPrivilege(REFERENCES);
        this.referenceColumns = referenceColumns;
    }

    public List<String> getReferenceColumns() {
        return referenceColumns;
    }

    // utility methods

    /**
     * Adds a privilege to the list of all privileges associated with this table. Should not be used
     * for granting all privileges. If a privilege is present, it won't be added to the list.
     * @param privilege is the privilege to add except for all privileges, omitting duplicates
     */
    public void grantPrivilege(Privilege privilege) {

        assert privilege != ALL_PRIVILEGES;

        // don't add duplicate privileges to the list of privileges
        for (Privilege current : privileges) {
            if (current == privilege) {
                return;
            }
        }

        privileges.add(privilege);
    }

    /**
     * Adds privileges to the list of all privileges associated with this table. The list should not
     * contain the all privileges privilege. If a privilege is present, it won't be added to the list.
     * @param privileges are the privilege to add except for all privileges, omitting duplicates
     */
    public void grantPrivileges(List<Privilege> privileges) {
        for (Privilege privilege : privileges) {
            grantPrivilege(privilege);
        }
    }

    /**
     * Grants all privileges on the given table.
     * @param table is a reference to the table in order to get all of its columns
     */
    public void grantAllPrivileges(Table table) {

        // grant all privileges
        privileges = getAllPrivilegesExceptUnknown();

        // obtain a mapping of column names for UPDATE and REFERENCES privileges
        List<String> columnNames = table.getColumns().stream()
                .map(Column::getColumnName)
                .collect(Collectors.toList());

        updateColumns = new ArrayList<>(columnNames);
        referenceColumns = new ArrayList<>(columnNames);
    }

    /**
     * @param columnToAdd is the column to add that's associated
     * with the UPDATE privilege, omitting duplicates
     */
    public void addUpdateColumn(String columnToAdd) {

        // don't add a duplicate update column
        for (String updateColumn : updateColumns) {
            if (updateColumn.equalsIgnoreCase(columnToAdd)) {
                return;
            }
        }

        updateColumns.add(columnToAdd);
    }

    /**
     * @param columnsToAdd is a list of columns to add that's associated
     * with the UPDATE privilege, omitting duplicates
     */
    public void addUpdateColumns(List<String> columnsToAdd) {
        for (String columnToAdd : columnsToAdd) {
            addUpdateColumn(columnToAdd);
        }
    }

    /**
     * @param columnToAdd is the column to add that's associated
     * with the REFERENCES privilege, omitting duplicates
     */
    public void addReferencesColumn(String columnToAdd) {

        // don't add a duplicate reference column
        for (String referenceColumn : referenceColumns) {
            if (referenceColumn.equalsIgnoreCase(columnToAdd)) {
                return;
            }
        }

        referenceColumns.add(columnToAdd);
    }

    /**
     * @param columnsToAdd is a list of columns to add that's associated
     * with the REFERENCES privilege, omitting duplicates
     */
    public void addReferencesColumns(List<String> columnsToAdd) {
        for (String columnToAdd : columnsToAdd) {
            addReferencesColumn(columnToAdd);
        }
    }

    /**
     * Removes a privilege from the list of privileges on this table. If removing all privileges,
     * self explanatory. If removing UPDATE or REFERENCES privilege, removes the associated column names too.
     * @param privilegeToRemove is the privilege to remove, if the privilege is
     */
    public void revokePrivilege(Privilege privilegeToRemove) {
        if (privilegeToRemove == ALL_PRIVILEGES) {
            revokeAllPrivileges();
        } else {
            for (int i = 0; i < privileges.size(); i++) {
                Privilege current = privileges.get(i);
                if (current == privilegeToRemove) {
                    // reset respective lists if update or references encountered
                    if (current == Privilege.UPDATE) {
                        updateColumns = new ArrayList<>();
                    }
                    if (current == Privilege.REFERENCES) {
                        referenceColumns = new ArrayList<>();
                    }
                    privileges.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * Removes all privileges and clears any update or references columns too.
     */
    public void revokeAllPrivileges() {
        privileges = new ArrayList<>();
        updateColumns = new ArrayList<>();
        referenceColumns = new ArrayList<>();
    }

    /**
     * Removes the update column from the list of update columns. If the column is not
     * present, does not perform the removal.
     * @param columnToRemove is the column to remove
     */
    public void removeUpdateColumn(String columnToRemove) {
        for (int i = 0; i < updateColumns.size(); i++) {
            if (columnToRemove.equalsIgnoreCase(updateColumns.get(i))) {
                updateColumns.remove(i);
                break;
            }
        }
    }

    /**
     * Removes the update columns from the list of update columns. If the column is not
     * present, does not perform the removal.
     * @param columnsToRemove are the columns to remove
     */
    public void removeUpdateColumns(List<String> columnsToRemove) {
        for (String columnToRemove : columnsToRemove) {
            removeUpdateColumn(columnToRemove);
        }
    }

    /**
     * Removes the reference column from the list of reference columns. If the column is not
     * present, does not perform the removal.
     * @param columnToRemove is the column to remove
     */
    public void removeReferencesColumn(String columnToRemove) {
        for (int i = 0; i < referenceColumns.size(); i++) {
            if (columnToRemove.equalsIgnoreCase(referenceColumns.get(i))) {
                referenceColumns.remove(i);
                break;
            }
        }
    }

    /**
     * Removes the reference columns from the list of reference columns. If the column is not
     * present, does not perform the removal.
     * @param columnsToRemove are the columns to remove
     */
    public void removeReferencesColumns(List<String> columnsToRemove) {
        for (String columnToRemove : columnsToRemove) {
            removeReferencesColumn(columnToRemove);
        }
    }

    /**
     * @param candidate is the privilege to check
     * @return whether the candidate privilege exists
     */
    public boolean hasPrivilege(Privilege candidate) {

        for (Privilege current : privileges) {
            if (current == candidate) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether all privileges are granted on the given table. Also makes sure that all
     * columns are present for UPDATE and REFERENCES privileges.
     * @param table is the table to check - used for checking the columns in the UPDATE and REFERENCES privileges
     * @return whether all privileges are present in the given table
     */
    public boolean hasAllPrivileges(Table table) {

        boolean hasAllPrivileges = hasPrivilege(ALTER) && hasPrivilege(DELETE) &&
                hasPrivilege(INDEX) && hasPrivilege(INSERT) && hasPrivilege(SELECT) &&
                hasPrivilege(UPDATE) && hasPrivilege(REFERENCES);

        if (! hasAllPrivileges) {
            return false;
        }

        // getting all the column names from the table
        List<String> columnNames = table.getColumns().stream()
                .map(Column::getColumnName)
                .collect(Collectors.toList());

        // check all update columns
        for (String updateColumn : updateColumns) {
            boolean foundUpdateColumnInTable = false;
            for (String tableColumn : columnNames) {
                if (updateColumn.equalsIgnoreCase(tableColumn)) {
                    foundUpdateColumnInTable = true;
                    break;
                }
            }
            if (! foundUpdateColumnInTable) {
                return false;
            }
        }

        // check all reference columns
        for (String referencedColumn : referenceColumns) {
            boolean foundReferenceColumnInTable = false;
            for (String tableColumn : columnNames) {
                if (referencedColumn.equalsIgnoreCase(tableColumn)) {
                    foundReferenceColumnInTable = true;
                    break;
                }
            }
            if (! foundReferenceColumnInTable) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param candidate is the update column to check
     * @return whether the candidate exists in the list of update columns
     */
    public boolean hasUpdateColumn(String candidate) {

        for (String updateColumn : updateColumns) {
            if (updateColumn.equalsIgnoreCase(candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param candidate is the references column to check
     * @return whether the candidate exists in the list of references columns
     */
    public boolean hasReferencesColumn(String candidate) {

        for (String referenceColumn : referenceColumns) {
            if (referenceColumn.equalsIgnoreCase(candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return a string representation of the data within this object
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t\t").append("Privileges: ");

        if (! privileges.isEmpty()) {

            for (Privilege privilege : privileges) {
                stringBuilder.append(privilege).append(", ");
            }

            // remove ", "
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        } else {
            stringBuilder.append("None");
        }

        stringBuilder.append("\n").append("\t\t").append("Update Columns: ");

        if (! updateColumns.isEmpty()) {

            for (String updateColumn : updateColumns) {
                stringBuilder.append(updateColumn).append(", ");
            }

            // remove ", "
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        } else {
            stringBuilder.append("None");
        }

        stringBuilder.append("\n").append("\t\t").append("Reference Columns: ");

        if (! referenceColumns.isEmpty()) {
            for (String referenceColumn : referenceColumns) {
                stringBuilder.append(referenceColumn).append(", ");
            }

            // remove ", "
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        } else {
            stringBuilder.append("None");
        }

        return stringBuilder.toString();
    }
}