package systemcatalog.components;

import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.user.User;
import datastructures.user.component.Privilege;
import enums.InputType;
import utilities.Utilities;

import java.util.List;

/**
 * Responsible for ensuring that the current user has the correct privileges before
 * executing a statement. Has options for toggling on or off as well.
 * references - check alter
 */
public class SecurityChecker {

    private String errorMessage;
    private boolean isOn;

    public SecurityChecker() {
        errorMessage = "";
        isOn = true;
    }

    /**
     * @return the error message if an error occurred
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Resets the error message.
     */
    public void resetErrorMessage() {
        errorMessage = "";
    }

    /**
     * Turns the Verifier on.
     */
    public void turnOn() {
        isOn = true;
    }

    /**
     * Turns the Verifier off.
     */
    public void turnOff() {
        isOn = false;
    }

    /**
     * Returns whether the following input is valid with respect to the current user's privileges.
     * @param inputType is the type of input
     * @param filteredInput is the input after being filtered
     * @param currentUser is the current user of the system
     * @param tables are the tables of the system
     * @return whether the input is valid with respect to what's found on the system
     */
    public boolean isValid(InputType inputType, String[] filteredInput, User currentUser, List<Table> tables) {

        if (! isOn || currentUser.isDBA()) {
            return true;
        }

        switch(inputType) {
            case QUERY:
                return isValidQuery(filteredInput, currentUser, tables);
            case CREATE_TABLE:
                return isValidCreateTable(filteredInput, currentUser);
            case DROP_TABLE:
                return isValidDropTable(filteredInput, currentUser);
            case ALTER_TABLE:
                return isValidAlterTable(filteredInput, currentUser);
            case INSERT:
                return isValidInsert(filteredInput, currentUser);
            case DELETE:
                return isValidDelete(filteredInput, currentUser);
            case UPDATE:
                return isValidUpdate(filteredInput, currentUser);
            case GRANT:
                return isValidGrant(filteredInput, currentUser, tables);
            case REVOKE:
                return isValidRevoke(filteredInput, currentUser, tables);
            case BUILD_FILE_STRUCTURE:
                return isValidBuildFileStructure(filteredInput, currentUser);
            case REMOVE_FILE_STRUCTURE:
                return isValidRemoveFileStructure(filteredInput, currentUser);
            case UNKNOWN:
            default:
                return false;
        }
    }

    /**
     * Checks to see if the current user has the SELECT privilege for each table referenced in the Query.
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @param tables are the tables of the system
     * @return whether the current user has the correct table privileges for the QUERY
     */
    public boolean isValidQuery(String[] filteredInput, User currentUser, List<Table> tables) {

        RuleGraph queryRuleGraph = RuleGraphTypes.getQueryRuleGraph();
        List<String> tableNames = queryRuleGraph.getTokensAt(filteredInput, 13, 15, 18);

        for (String tableName : tableNames) {
            boolean hasPrivilege = currentUser.hasTablePrivilege(tableName, Privilege.SELECT);
            if (! hasPrivilege) {
                errorMessage = "User \"" + currentUser.getUsername() + "\" does not have the \"SELECT\" privilege\n" +
                        "on Table \"" + tableName + "\", therefore, the Query was not executed";
                return false;
            }
        }

        return true;
    }

    /**
     * Will always return true because the user is the one creating the table.
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the CREATE TABLE command
     */
    public boolean isValidCreateTable(String[] filteredInput, User currentUser) {
        return true;
    }

    /**
     * Will check if the current user has the ALTER privilege which determines if the table can be altered.
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the DROP TABLE command
     */
    public boolean isValidDropTable(String[] filteredInput, User currentUser) {

        RuleGraph dropTableRuleGraph = RuleGraphTypes.getDropTableRuleGraph();
        String tableName = dropTableRuleGraph.getTokensAt(filteredInput, 2).get(0);

        if (! currentUser.hasTablePrivilege(tableName, Privilege.ALTER)) {
            errorMessage = "User \"" + currentUser.getUsername() + "\" does not have the \"ALTER\" privilege\n" +
                    "on Table \"" + tableName + "\", therefore, the Drop Table command was not executed";
            return false;
        }

        return true;
    }

    /**
     * Make sure the user has the ALTER and REFERENCES privileges. REFERENCES comes up if using
     * add or drop with foreign keys.
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the ALTER TABLE command
     */
    public boolean isValidAlterTable(String[] filteredInput, User currentUser) {

        RuleGraph alterTableRuleGraph = RuleGraphTypes.getAlterTableRuleGraph();
        String tableName = alterTableRuleGraph.getTokensAt(filteredInput, 2).get(0);

        if (! currentUser.hasTablePrivilege(tableName, Privilege.ALTER)) {
            errorMessage = "User \"" + currentUser.getUsername() + "\" does not have the \"ALTER\" privilege\n" +
                    "on Table \"" + tableName + "\", therefore, the Alter Table command was not executed";
            return false;
        }

        boolean hasForeignKey = ! alterTableRuleGraph.getTokensAt(filteredInput, 13).isEmpty();

        if (hasForeignKey) {
            List<String> referencedColumn = alterTableRuleGraph.getTokensAt(filteredInput, 16);
            if (! currentUser.hasTablePrivilege(tableName, Privilege.REFERENCES, referencedColumn)) {
                errorMessage = "User \"" + currentUser.getUsername() + "\" does not have the \"REFERENCES\" " +
                        "privilege\n for \"" + referencedColumn.get(0) + "\", therefore, the Alter Table command was not executed";
                return false;
            }
        }

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the INSERT command
     */
    public boolean isValidInsert(String[] filteredInput, User currentUser) {

        RuleGraph insertRuleGraph = RuleGraphTypes.getInsertRuleGraph();
        String tableName = insertRuleGraph.getTokensAt(filteredInput, 2).get(0);

        if (! currentUser.hasTablePrivilege(tableName, Privilege.INSERT)) {
            errorMessage = "User \"" + currentUser.getUsername() + "\" does not have the \"INSERT\" privilege\n" +
                    "on Table \"" + tableName + "\", therefore, the Insert command was not executed";
            return false;
        }

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the DELETE command
     */
    public boolean isValidDelete(String[] filteredInput, User currentUser) {

        RuleGraph deleteRuleGraph = RuleGraphTypes.getDeleteRuleGraph();
        String tableName = deleteRuleGraph.getTokensAt(filteredInput, 2).get(0);

        if (! currentUser.hasTablePrivilege(tableName, Privilege.DELETE)) {
            errorMessage = "User \"" + currentUser.getUsername() + "\" does not have the \"DELETE\" privilege\n" +
                    "on Table \"" + tableName + "\", therefore, the Delete command was not executed";
            return false;
        }

        return true;
    }

    /**
     * Makes sure that the current user has all the columns in the UPDATE privilege for the given table.
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the UPDATE command
     */
    public boolean isValidUpdate(String[] filteredInput, User currentUser) {

        RuleGraph updateRuleGraph = RuleGraphTypes.getUpdateRuleGraph();
        String tableName = updateRuleGraph.getTokensAt(filteredInput, 1).get(0);
        List<String> columnNames = updateRuleGraph.getTokensAt(filteredInput, 3, 10);

        if (! currentUser.hasTablePrivilege(tableName, Privilege.UPDATE, columnNames)) {
            errorMessage = "User \"" + currentUser.getUsername() + "\" does not have the \"UPDATE\" privilege on \"" +
                    tableName + "\"\n for both Column \"" + columnNames.get(0) + "\" and \" " + columnNames.get(1) +
                    "\", therefore, the update command was not executed";
            return false;
        }

        return true;
    }

    /**
     * Checks to see if the current user has the passable table privileges needed
     * in order to use the GRANT command.
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the GRANT command
     */
    public boolean isValidGrant(String[] filteredInput, User currentUser, List<Table> tables) {

        RuleGraph grantRuleGraph = RuleGraphTypes.getGrantRuleGraph();
        String tableName = grantRuleGraph.getTokensAt(filteredInput, 20).get(0);

        // make sure user has passable privileges for alter, delete, index, insert, and select for the given table
        List<String> privilegeNames = grantRuleGraph.getTokensAt(filteredInput, 1, 2, 3, 4, 5);

        for (String privilege : privilegeNames) {
            if (! currentUser.hasGrantedTablePrivilege(tableName, Privilege.convertToPrivilege(privilege))) {
                errorMessage = "User \"" + currentUser.getUsername() + "\" does not have the passable \"" + privilege +
                        "\" privilege\n on Table \"" + tableName + "\", therefore, the Grant command was not executed";
                return false;
            }
        }

        // make sure user has the update passable privilege for the given table
        boolean grantingUpdate = ! grantRuleGraph.getTokensAt(filteredInput, 6).isEmpty();

        if (grantingUpdate) {
            List<String> updateColumnNames = grantRuleGraph.getTokensAt(filteredInput, 12);
            if (! currentUser.hasGrantedTablePrivilege(tableName, Privilege.UPDATE, updateColumnNames)) {
                errorMessage = "User \"" + currentUser.getUsername() + "\" does not have all the passable \"UPDATE\"" +
                        "privilege columns on Table\n\"" + tableName + "\", therefore, " +
                        "the Grant command was not executed";
                return false;
            }
        }

        // make sure the user has the reference privilege for the given table
        boolean grantingReferences = ! grantRuleGraph.getTokensAt(filteredInput, 7).isEmpty();

        if (grantingReferences) {
            List<String> updateColumnNames = grantRuleGraph.getTokensAt(filteredInput, 16);
            if (! currentUser.hasGrantedTablePrivilege(tableName, Privilege.REFERENCES, updateColumnNames)) {
                errorMessage = "User \"" + currentUser.getUsername() + "\" does not have all the passable " +
                        "\"REFERENCES\" privilege columns on Table\n\"" + tableName + "\", therefore, " +
                        "the Grant command was not executed";
                return false;
            }
        }

        // make sure the user either has all privileges for a given table
        boolean grantingAllPrivileges = ! grantRuleGraph.getTokensAt(filteredInput, 8).isEmpty();

        if (grantingAllPrivileges) {
            Table referencedTable = Utilities.getReferencedTable(tableName, tables);
            assert referencedTable != null;
            boolean hasAllPrivileges = Utilities.hasAllGrantedPrivilegesOnTable(currentUser, referencedTable);
            if (! hasAllPrivileges) {
                errorMessage = "User \"" + currentUser.getUsername() + "\" does not have all the passable " +
                        "privileges on Table\n\"" + tableName + "\", therefore, " +
                        "the Grant command was not executed";;
                return false;
            }
        }

        return false;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the REVOKE command
     */
    public boolean isValidRevoke(String[] filteredInput, User currentUser, List<Table> tables) {

        RuleGraph revokeRuleGraph = RuleGraphTypes.getRevokeRuleGraph();

        return false;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the BUILD FILE STRUCTURE command
     */
    public boolean isValidBuildFileStructure(String[] filteredInput, User currentUser) {
        /*String tableName = tokenizedInput[6];
        return currentUser.hasTablePrivilege(tableName, Privilege.INDEX);*/
        RuleGraph buildFileStructureRuleGraph = RuleGraphTypes.getBuildFileStructureRuleGraph();

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the REMOVE FILE STRUCTURE command
     */
    public boolean isValidRemoveFileStructure(String[] filteredInput, User currentUser) {

        RuleGraph removeFileStructureRuleGraph = RuleGraphTypes.getRemoveFileStructureRuleGraph();

        return false;
    }
}