package datastructures.user.component;

import java.util.ArrayList;
import java.util.List;

public class TablePrivileges {

    private String tableName;
    private List<Privilege> privileges;
    private List<String> updateColumns, referenceColumns;

    public TablePrivileges() {

        this.tableName        = "";
        this.privileges       = new ArrayList<>();
        this.updateColumns    = new ArrayList<>();
        this.referenceColumns = new ArrayList<>();
    }

    public TablePrivileges(String tableName, List<Privilege> privileges) {

        this.tableName        = tableName;
        this.privileges       = privileges;
        this.updateColumns    = new ArrayList<>();
        this.referenceColumns = new ArrayList<>();
    }

    public TablePrivileges(String tableName, List<Privilege> privileges,
                           List<String> updateColumns, List<String> referenceColumns) {
        this(tableName, privileges);
        this.updateColumns = updateColumns;
        this.referenceColumns = referenceColumns;
    }

    public TablePrivileges(TablePrivileges toCopy) {

        this.tableName = toCopy.tableName;
        this.privileges = new ArrayList<>();
        this.privileges.addAll(toCopy.privileges);
        this.updateColumns = new ArrayList<>();
        this.updateColumns.addAll(toCopy.updateColumns);
        this.referenceColumns = new ArrayList<>();
        this.referenceColumns.addAll(toCopy.referenceColumns);
    }

    /**
     * Adds a privilege to the list of all privileges associated with this table.
     * If a privilege is present, it won't be added to the list.
     * @param privilege is what to add
     */
    public void grantPrivilege(Privilege privilege) {

        if(privilege == Privilege.ALL_PRIVILEGES) {
            privileges = Privilege.getAllPrivileges();

        } else {

            // don't add duplicate privileges to the list of privileges
            for(Privilege current : privileges) {
                if(current == privilege) {
                    return;
                }
            }

            privileges.add(privilege);
        }
    }

    /**
     * Removes a privilege from the list of all privileges associated with this table.
     * @param privilegeToRemove the privilege to remove
     */
    public void revokePrivilege(Privilege privilegeToRemove) {

        if(privilegeToRemove == Privilege.ALL_PRIVILEGES) {

            privileges = new ArrayList<>();
            updateColumns = new ArrayList<>();
            referenceColumns = new ArrayList<>();

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

    public boolean hasAllPrivileges() {

        for(Privilege privilege : privileges) {
            if(privilege == Privilege.ALL_PRIVILEGES) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes all privileges that the user has on a table.
     * This also clears any update or references columns too.
     */
    public void revokeAllPrivileges() {

        privileges = new ArrayList<>();
        updateColumns = new ArrayList<>();
        referenceColumns = new ArrayList<>();
    }

    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getTableName() { return tableName; }

    public void setPrivileges(List<Privilege> privileges) { this.privileges = privileges; }

    public List<Privilege> getPrivileges() { return privileges; }

    public void setUpdateColumns(List<String> updateColumns) {

        if(hasPrivilege(Privilege.UPDATE)) {
            this.updateColumns = updateColumns;
        } else {
            System.out.println("In TablePrivileges.setUpdateColumns()");
            System.out.println("No UPDATE privilege found");
        }
    }

    public void addUpdateColumn(String updateColumnToAdd) {

        // don't add a duplicate update column
        for(String updateColumn : updateColumns) {
            if(updateColumn.equalsIgnoreCase(updateColumnToAdd)) {
                return;
            }
        }

        updateColumns.add(updateColumnToAdd);
    }

    public List<String> getUpdateColumns() { return updateColumns;}

    public void setReferenceColumns(List<String> referenceColumns) {

        if(hasPrivilege(Privilege.REFERENCES)) {
            this.referenceColumns = referenceColumns;
        } else {
            System.out.println("In TablePrivileges.setReferenceColumns()");
            System.out.println("No REFERENCE privilege found");
        }
    }

    public void addReferenceColumn(String referenceColumnToAdd) {

        // don't add a duplicate reference column
        for(String referenceColumn : referenceColumns) {
            if(referenceColumn.equalsIgnoreCase(referenceColumnToAdd)) {
                return;
            }
        }

        referenceColumns.add(referenceColumnToAdd);
    }

    public List<String> getReferenceColumns() { return referenceColumns; }

    public boolean hasPrivilege(Privilege candidate) {

        for(Privilege current : privileges) {
            if(current == candidate) {
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

        if(! privileges.isEmpty()) {

            for(Privilege privilege : privileges) {
                stringBuilder.append(privilege).append(", ");
            }

            // remove ", "
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        } else {

            stringBuilder.append("None");
        }

        stringBuilder.append("\n").append("\t\t").append("Update Columns: ");

        if(! updateColumns.isEmpty()) {

            for(String updateColumn : updateColumns) {
                stringBuilder.append(updateColumn).append(", ");
            }

            // remove ", "
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        } else {

            stringBuilder.append("None");
        }

        stringBuilder.append("\n").append("\t\t").append("Reference Columns: ");

        if(! referenceColumns.isEmpty()) {

            for(String referenceColumn : referenceColumns) {
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