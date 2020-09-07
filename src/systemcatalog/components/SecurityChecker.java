package systemcatalog.components;

import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.user.User;
import datastructures.user.component.Privilege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Responsible for ensuring that the current user has the correct privileges before
 * executing a statement.
 */
public class SecurityChecker {

    private RuleGraph.Type ruleGraphType;
    private RuleGraph ruleGraphToUse;
    private String[] tokenizedInput;
    private User currentUser;
    private List<Table> tables;
    private boolean toggle;

    public SecurityChecker() {
        // by default, toggle the Security Checker on
        this.toggle = true;
    }

    // setters ---------------------------------------------------------------------------------------------------------

    public void setRuleGraphType(RuleGraph.Type ruleGraphType) {
        this.ruleGraphType = ruleGraphType;
    }

    public void setRuleGraphToUse(RuleGraph ruleGraphToUse) {
        this.ruleGraphToUse = ruleGraphToUse;
    }

    public void setTokenizedInput(String[] tokenizedInput) {
        this.tokenizedInput = tokenizedInput;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    // validation ------------------------------------------------------------------------------------------------------

    public boolean isValid() {

        // return true if the Security Checker is toggled off
        if(! toggle) {
            return true;
        }

        switch(ruleGraphType) {
            case QUERY:
                return isValidQuery();
            case CREATE_TABLE:
                return isValidCreateTable();
            case DROP_TABLE:
                return isValidDropTable();
            case ALTER_TABLE:
                return isValidAlterTable();
            case INSERT:
                return isValidInsert();
            case DELETE:
                return isValidDelete();
            case UPDATE:
                return isValidUpdate();
            case GRANT:
                isValidGrant();
            case REVOKE:
                isValidRevoke();
            case BUILD_FILE_STRUCTURE:
                isValidBuildFileStructure();
            case REMOVE_FILE_STRUCTURE:
                return isValidRemoveFileStructure();
            case UNKNOWN:
            default:
                return false;
        }
    }

    public boolean isValidQuery() {

        ArrayList<String> referencedTableNames = ruleGraphToUse.getTokensAt(tokenizedInput, 13, 15, 17);
        //ArrayList<String> referencedColumnNames = ruleGraph.getTokensAt(input, );
        HashMap<String, ArrayList<String>> tableColumnPairs = new HashMap<>();
        return true;
    }

    public boolean isValidCreateTable() {

        //String primaryKey = ruleGraph.getTokensAt(create, -1).get(0);
        //ArrayList<String> foreignKeys = ruleGraph.getTokensAt(create, -1);

        // TODO

        return true;
    }

    public boolean isValidDropTable() {
        String tableName = tokenizedInput[2];
        return currentUser.hasTablePrivilege(tableName, Privilege.ALTER);
    }

    public boolean isValidAlterTable() {
        String tableName = tokenizedInput[2];
        return currentUser.hasTablePrivilege(tableName, Privilege.ALTER);
    }

    public boolean isValidInsert() {
        String tableName = tokenizedInput[2];
        return currentUser.hasTablePrivilege(tableName, Privilege.INSERT);
    }

    public boolean isValidDelete() {
        String tableName = tokenizedInput[2];
        return currentUser.hasTablePrivilege(tableName, Privilege.DELETE);
    }

    public boolean isValidUpdate() {

        String tableName = tokenizedInput[2];
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
    }

    public boolean isValidPrivilegeCommand() {

        String tableName = ruleGraphToUse.getTokensAt(tokenizedInput, 20).get(0);

        // if WITH GRANT OPTION is used, make sure the user is allowed to pass on privileges to others
        ArrayList<String> withToken = ruleGraphToUse.getTokensAt(tokenizedInput, 24);
        boolean grantOptionUsed = ! withToken.isEmpty();

        if(grantOptionUsed) {

            ArrayList<String> updateToken = ruleGraphToUse.getTokensAt(tokenizedInput, 6);
            ArrayList<String> referencesToken = ruleGraphToUse.getTokensAt(tokenizedInput, 6);

            boolean isPassingUpdatePrivilege = ! updateToken.isEmpty();
            boolean isPassingReferencesPrivilege = ! referencesToken.isEmpty();

            if(isPassingUpdatePrivilege) {
                ArrayList<String> passedUpdateColumns = ruleGraphToUse.getTokensAt(tokenizedInput, 12);
                return currentUser.hasGrantedTablePrivilege(tableName, Privilege.UPDATE, passedUpdateColumns);
            }

            if(isPassingReferencesPrivilege) {
                ArrayList<String> passedReferencesColumns = ruleGraphToUse.getTokensAt(tokenizedInput, 16);
                return currentUser.hasGrantedTablePrivilege(tableName, Privilege.UPDATE, passedReferencesColumns);
            }
        }

        return true;
    }

    public boolean isValidBuildFileStructure() {
        String tableName = tokenizedInput[6];
        return currentUser.hasTablePrivilege(tableName, Privilege.INDEX);
    }

    public boolean isValidClusteredFile() {
        String tableName1 = tokenizedInput[4];
        String tableName2 = tokenizedInput[6];
        return currentUser.hasTablePrivilege(tableName1, Privilege.INDEX) &&
                currentUser.hasTablePrivilege(tableName2, Privilege.INDEX);
    }

    public boolean isValidGrant() {
        return false;
    }

    public boolean isValidRevoke() {
        return false;
    }

    public boolean isValidRemoveFileStructure() {
        return false;
    }
}