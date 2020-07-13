package systemcatalog.components;

import datastructure.rulegraph.RuleGraph;
import datastructure.relation.table.Table;
import datastructure.relation.table.component.Column;
import datastructure.user.User;
import utilities.enums.InputType;
import datastructure.user.component.Privilege;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Responsible for ensuring that the current user has the correct privileges before
 * executing a statement.
 */
public class SecurityChecker {

    private RuleGraph ruleGraph;

    public SecurityChecker() {}

    public void setRuleGraph(RuleGraph ruleGraph) { this.ruleGraph = ruleGraph; }

    public boolean isValid(InputType inputType, String[] input, User user, ArrayList<Table> tables) {

        // don't bother with the rest of the checking if we don't need to
        if(user.hasAllPrivileges()) {
            return true;
        }

        switch(inputType) {
            case QUERY:
                isValidQuery(input, user, tables);
            case CREATE_TABLE:
                isValidCreateTable(input, user);
            case DROP_TABLE:
                isValidDropTable(input, user);
            case ALTER_TABLE:
                isValidAlterTable(input, user);
            case INSERT:
                isValidInsert(input, user, tables);
            case DELETE:
                isValidDelete(input, user, tables);
            case UPDATE:
                isValidUpdate(input, user, tables);
            case GRANT:
            case REVOKE:
                isValidPrivilegeCommand(input, user);
            case BUILD_SECONDARY_B_TREE:
            case BUILD_CLUSTERED_B_TREE:
            case BUILD_HASH_TABLE:
                isValidBuildFileStructure(input, user, tables);
            case BUILD_CLUSTERED_FILE:
                isValidClusteredFile(input, user, tables);
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
}
