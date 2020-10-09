package systemcatalog.components;

import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.user.User;
import enums.InputType;

import java.util.List;

/**
 * Responsible for ensuring that the current user has the correct privileges before
 * executing a statement. Has options for toggling on or off as well.
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

        if (!isOn) {
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
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @param tables are the tables of the system
     * @return whether the current user has the correct table privileges for the QUERY
     */
    public boolean isValidQuery(String[] filteredInput, User currentUser, List<Table> tables) {

        RuleGraph queryRuleGraph = RuleGraphTypes.getQueryRuleGraph();

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the CREATE TABLE command
     */
    public boolean isValidCreateTable(String[] filteredInput, User currentUser) {

        RuleGraph createTableRuleGraph = RuleGraphTypes.getCreateTableRuleGraph();

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the DROP TABLE command
     */
    public boolean isValidDropTable(String[] filteredInput, User currentUser) {

        RuleGraph dropTableRuleGraph = RuleGraphTypes.getDropTableRuleGraph();

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the ALTER TABLE command
     */
    public boolean isValidAlterTable(String[] filteredInput, User currentUser) {

        RuleGraph alterTableRuleGraph = RuleGraphTypes.getAlterTableRuleGraph();

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the INSERT command
     */
    public boolean isValidInsert(String[] filteredInput, User currentUser) {

        RuleGraph insertRuleGraph = RuleGraphTypes.getInsertRuleGraph();

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the DELETE command
     */
    public boolean isValidDelete(String[] filteredInput, User currentUser) {

        RuleGraph deleteRuleGraph = RuleGraphTypes.getDeleteRuleGraph();

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the UPDATE command
     */
    public boolean isValidUpdate(String[] filteredInput, User currentUser) {

        /*String tableName = tokenizedInput[2];
        ArrayList<String> columnNames = new ArrayList<>();

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                for(Column column : table.getColumns()) {
                    columnNames.add(column.getColumnName());
                }
                break;
            }
        }

        return currentUser.hasTablePrivilege(tableName, Privilege.UPDATE, columnNames);
         */
        RuleGraph updateRuleGraph = RuleGraphTypes.getUpdateRuleGraph();

        return true;
    }

    /**
     * @param filteredInput is the filtered input
     * @param currentUser is the current user of the system
     * @return whether the current user has the correct table privileges for the GRANT command
     */
    public boolean isValidGrant(String[] filteredInput, User currentUser, List<Table> tables) {

        RuleGraph grantRuleGraph = RuleGraphTypes.getGrantRuleGraph();

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