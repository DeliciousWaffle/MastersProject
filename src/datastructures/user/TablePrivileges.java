package datastructures.user;

import utilities.enums.Privilege;

import java.util.ArrayList;

public class TablePrivileges {

    private String tableName;
    private ArrayList<Privilege> privileges;
    private boolean hasAllPrivileges;
    private ArrayList<String> updateColumns, referenceColumns;

    public TablePrivileges() {}

    public TablePrivileges(String tableName, ArrayList<Privilege> privileges) {

        this.tableName   = tableName;
        this.privileges  = privileges;
        hasAllPrivileges = false;
        updateColumns    = new ArrayList<>();
        referenceColumns = new ArrayList<>();
    }

    /**
     * Adds a privilege to the list of all privileges associated with this table.
     * If a privilege is present, it won't be added to the list.
     * @param privilege is what to add
     */
    public void grantPrivilege(Privilege privilege) {

        if(privilege == Privilege.ALL_PRIVILEGES) {
            grantAllPrivileges();
            return;
        }

        // don't be dumb
        if(privilege == Privilege.UPDATE || privilege == Privilege.REFERENCES) {
            System.out.println("In TablePrivileges.grantPrivilege()");
            System.out.println("Used wrong method!");
            return;
        }

        // don't add duplicate privileges to the list of privileges
        for(int i = 0; i < privileges.size(); i++) {
            Privilege current = privileges.get(i);
            if(current == privilege) {
                return;
            }
        }

        privileges.add(privilege);
    }

    /**
     * Adds either an UPDATE or REFERENCES privilege to the list of privileges.
     * @param privilege
     * @param columnNames
     */
    public void grantPrivilege(Privilege privilege, ArrayList<String> columnNames) {

        // don't be dumb
        if(! (privilege == Privilege.ALL_PRIVILEGES ||
                privilege == Privilege.UPDATE || privilege == Privilege.REFERENCES)) {
            System.out.println("In TablePrivileges.grantPrivilege()");
            System.out.println("Used wrong method!");
            return;
        }

        // don't add duplicate privileges to the list of privileges
        for(int i = 0; i < privileges.size(); i++) {
            Privilege current = privileges.get(i);
            if(current == privilege) {
                return;
            }
        }

        privileges.add(privilege);

        // set update or references columns
        if(privilege == Privilege.UPDATE) {
            updateColumns = columnNames;
        } else {
            referenceColumns = columnNames;
        }
    }

    /**
     * Grants all privileges to the user on this table.
     * This clears all privileges, update columns, and references columns.
     */
    public void grantAllPrivileges() {

        hasAllPrivileges = true;
        privileges = new ArrayList<>();
        updateColumns = new ArrayList<>();
        referenceColumns = new ArrayList<>();
    }

    /**
     * Removes a privilege from the list of all privileges associated with this table.
     * If the removal is valid, it will remove the item from the list, otherwise, it won't.
     * @param privilegeToRemove the privilege to remove
     */
    public void revokePrivilege(Privilege privilegeToRemove) {

        if(privilegeToRemove == Privilege.ALL_PRIVILEGES) {
            revokeAllPrivileges();
            return;
        }

        boolean successfullyRevoked = false;

        for(int i = 0; i < privileges.size(); i++) {

            Privilege current = privileges.get(i);

            if(current == privilegeToRemove) {

                // reset the lists
                if(current == Privilege.UPDATE) {
                    updateColumns = new ArrayList<>();
                }

                if(current == Privilege.REFERENCES) {
                    referenceColumns = new ArrayList<>();
                }

                privileges.remove(i);
                successfullyRevoked = true;
            }
        }

        if(! successfullyRevoked) {
            System.out.println("In TablePrivileges.revokePrivilege()");
            System.out.println("Could not remove: " + privilegeToRemove);
        }
    }

    /**
     * Removes all privileges that the user has on a table.
     * This also clears any update or references columns too.
     */
    public void revokeAllPrivileges() {

        hasAllPrivileges = false;
        privileges = new ArrayList<>();
        updateColumns = new ArrayList<>();
        referenceColumns = new ArrayList<>();
    }

    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getTableName() { return tableName; }

    public boolean hasAllPrivileges() {
        return hasAllPrivileges;
    }

    public void setPrivileges(ArrayList<Privilege> privileges) { this.privileges = privileges; }

    public ArrayList<Privilege> getPrivileges() { return privileges; }

    public void setUpdateColumns(ArrayList<String> updateColumns) {

        if(hasPrivilege(Privilege.UPDATE)) {
            this.updateColumns = updateColumns;
        } else {
            System.out.println("In TablePrivileges.setUpdateColumns()");
            System.out.println("No UPDATE privilege found");
        }
    }

    public ArrayList<String> getUpdateColumns() { return updateColumns;}

    public void setReferenceColumns(ArrayList<String> referenceColumns) {

        if(hasPrivilege(Privilege.REFERENCES)) {
            this.referenceColumns = referenceColumns;
        } else {
            System.out.println("In TablePrivileges.setReferenceColumns()");
            System.out.println("No REFERENCE privilege found");
        }
    }

    public ArrayList<String> getReferenceColumns() { return referenceColumns; }

    public boolean hasPrivilege(Privilege candidate) {

        for(Privilege current : privileges) {
            if(current == candidate) {
                return true;
            }
        }

        return false;
    }
}
