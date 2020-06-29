package systemcatalog.components;

import utilities.enums.InputType;
import datastructures.rulegraph.RuleGraph;
import datastructures.table.Table;
import datastructures.user.User;

import java.util.ArrayList;

/**
 * Responsible for executing a given input. If the input is a query, an associated result set
 * will be produced. If the input is a DML statement, then the change will be carried out here.
 */
public class Compiler {

    private RuleGraph ruleGraph;

    public Compiler() {}

    public void setRuleGraph(RuleGraph ruleGraph) { this.ruleGraph = ruleGraph; }

    public void executeInput(InputType inputType, String[] input, ArrayList<User> users, ArrayList<Table> tables) {
        switch(inputType) {
            case QUERY:
                executeQuery(input, tables);
            case CREATE_TABLE:
                createTable(input, tables);
            case DROP_TABLE:
                dropTable(input, tables);
            case ALTER_TABLE:
                alterTable(input, tables);
            case INSERT:
                insert(input, tables);
            case DELETE:
                delete(input, tables);
            case UPDATE:
                update(input, tables);
            case GRANT:
                grant(input, users);
            case REVOKE:
                revoke(input, users);
            case UNKNOWN:
            default:
                return;
        }
    }

    public void executeQuery(String[] query, ArrayList<Table> tables) {

    }

    public void createTable(String[] createTable, ArrayList<Table> tables) {

    }

    public void dropTable(String[] dropTable, ArrayList<Table> tables) {

    }

    public void alterTable(String[] alterTable, ArrayList<Table> tables) {

    }

    public void insert(String[] insert, ArrayList<Table> tables) {

    }

    public void delete(String[] delete, ArrayList<Table> tables) {

    }

    public void update(String[] update, ArrayList<Table> tables) {

    }

    public void grant(String[] tokenizedInput, ArrayList<User> users) {

    }

    public void revoke(String[] tokenizedInput, ArrayList<User> users) {

    }
}
