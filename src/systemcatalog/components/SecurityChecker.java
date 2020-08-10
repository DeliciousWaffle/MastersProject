package systemcatalog.components;

import datastructures.rulegraph.RuleGraph;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.user.User;
import utilities.enums.InputType;
import datastructures.user.component.Privilege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Responsible for ensuring that the current user has the correct privileges before
 * executing a statement.
 */
public class SecurityChecker {

    private RuleGraph ruleGraph;

    public SecurityChecker() {}

    public void setRuleGraph(RuleGraph ruleGraph) { this.ruleGraph = ruleGraph; }

    public boolean isValid(InputType inputType, String[] input, User user, ArrayList<Table> tables) {

        switch(inputType) {
            case QUERY:
                return isValidQuery(input, user, tables);
            case CREATE_TABLE:
                return isValidCreateTable(input, user);
            case DROP_TABLE:
                return isValidDropTable(input, user);
            case ALTER_TABLE:
                return isValidAlterTable(input, user);
            case INSERT:
                return isValidInsert(input, user, tables);
            case DELETE:
                return isValidDelete(input, user, tables);
            case UPDATE:
                return isValidUpdate(input, user, tables);
            case GRANT:
                isValidGrant(input, user);
            case REVOKE:
                isValidRevoke(input, user);
            case BUILD_FILE_STRUCTURE:
                isValidBuildFileStructure(input, user, tables);
            case REMOVE_FILE_STRUCTURE:
                return isValidRemoveFileStructure(input, user, tables);
            case UNKNOWN:
            default:
                return false;
        }
    }

    public boolean isValidQuery(String[] query, User user, ArrayList<Table> tables) {

        ArrayList<String> referencedTableNames = ruleGraph.getTokensAt(query, 13, 15, 17);
        //ArrayList<String> referencedColumnNames = ruleGraph.getTokensAt(input, );
        HashMap<String, ArrayList<String>> tableColumnPairs = new HashMap<>();
        return true;
    }

    public boolean isValidCreateTable(String[] create, User user) {

        //String primaryKey = ruleGraph.getTokensAt(create, -1).get(0);
        //ArrayList<String> foreignKeys = ruleGraph.getTokensAt(create, -1);

        // TODO

        return true;
    }

    /**
     * @param dropTable the table to drop
     * @param user the current user
     * @return whether the user can drop the table supplied
     */
    public boolean isValidDropTable(String[] dropTable, User user) {
        String tableName = dropTable[2];
        return user.hasTablePrivilege(tableName, Privilege.ALTER);
    }

    public boolean isValidAlterTable(String[] alterTable, User user) {
        String tableName = alterTable[2];
        return user.hasTablePrivilege(tableName, Privilege.ALTER);
    }

    public boolean isValidInsert(String[] insert, User user, ArrayList<Table> tables) {
        String tableName = insert[2];
        return user.hasTablePrivilege(tableName, Privilege.INSERT);
    }

    public boolean isValidDelete(String[] delete, User user, ArrayList<Table> tables) {
        String tableName = delete[2];
        return user.hasTablePrivilege(tableName, Privilege.DELETE);
    }

    public boolean isValidUpdate(String[] update, User user, ArrayList<Table> tables) {

        String tableName = update[2];
        ArrayList<String> columnNames = new ArrayList<>();

        for(Table table : tables) {
            if(table.getTableName().equalsIgnoreCase(tableName)) {
                for(Column column : table.getColumns()) {
                    columnNames.add(column.getName());
                }
                break;
            }
        }

        return user.hasTablePrivilege(tableName, Privilege.UPDATE, columnNames);
    }

    public boolean isValidPrivilegeCommand(String[] privilegeCommand, User user) {

        String tableName = ruleGraph.getTokensAt(privilegeCommand, 20).get(0);

        // if WITH GRANT OPTION is used, make sure the user is allowed to pass on privileges to others
        ArrayList<String> withToken = ruleGraph.getTokensAt(privilegeCommand, 24);
        boolean grantOptionUsed = ! withToken.isEmpty();

        if(grantOptionUsed) {

            ArrayList<String> updateToken = ruleGraph.getTokensAt(privilegeCommand, 6);
            ArrayList<String> referencesToken = ruleGraph.getTokensAt(privilegeCommand, 6);

            boolean isPassingUpdatePrivilege = ! updateToken.isEmpty();
            boolean isPassingReferencesPrivilege = ! referencesToken.isEmpty();

            if(isPassingUpdatePrivilege) {
                ArrayList<String> passedUpdateColumns = ruleGraph.getTokensAt(privilegeCommand, 12);
                return user.hasGrantedTablePrivilege(tableName, Privilege.UPDATE, passedUpdateColumns);
            }

            if(isPassingReferencesPrivilege) {
                ArrayList<String> passedReferencesColumns = ruleGraph.getTokensAt(privilegeCommand, 16);
                return user.hasGrantedTablePrivilege(tableName, Privilege.UPDATE, passedReferencesColumns);
            }
        }

        return true;
    }

    public boolean isValidBuildFileStructure(String[] buildFileStructure, User user, ArrayList<Table> tables) {
        String tableName = buildFileStructure[6];
        return user.hasTablePrivilege(tableName, Privilege.INDEX);
    }

    public boolean isValidClusteredFile(String[] clusteredFile, User user, ArrayList<Table> tables) {
        String tableName1 = clusteredFile[4];
        String tableName2 = clusteredFile[6];
        return user.hasTablePrivilege(tableName1, Privilege.INDEX) &&
                user.hasTablePrivilege(tableName2, Privilege.INDEX);
    }

    public boolean isValidGrant(String[] grant, User user) {
        return false;
    }

    public boolean isValidRevoke(String[] revoke, User user) {
        return false;
    }

    public boolean isValidRemoveFileStructure(String[] removeFileStructure, User user, List<Table> tables) {
        return false;
    }
}
