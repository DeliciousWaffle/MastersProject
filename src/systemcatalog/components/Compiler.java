package systemcatalog.components;

import datastructure.relation.table.component.Column;
import datastructure.relation.table.component.DataType;
import utilities.enums.InputType;
import datastructure.rulegraph.RuleGraph;
import datastructure.relation.table.Table;
import datastructure.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for executing a given input. If the input is a query, an associated result set
 * will be produced. If the input is a DML statement, then the change will be carried out here.
 */
public class Compiler {

    private RuleGraph ruleGraph;

    public Compiler() {}

    public void setRuleGraph(RuleGraph ruleGraph) { this.ruleGraph = ruleGraph; }

    public void executeInput(InputType inputType, String[] input, RuleGraph ruleGraph,
                             ArrayList<User> users, ArrayList<Table> tables) {
        switch(inputType) {
            case QUERY:
                executeQuery(input, ruleGraph, tables);
                break;
            case CREATE_TABLE:
                createTable(input, ruleGraph, tables);
                break;
            case DROP_TABLE:
                dropTable(input, ruleGraph, tables);
                break;
            case ALTER_TABLE:
                alterTable(input, ruleGraph, tables);
                break;
            case INSERT:
                insert(input, ruleGraph, tables);
                break;
            case DELETE:
                delete(input, ruleGraph, tables);
                break;
            case UPDATE:
                update(input, ruleGraph, tables);
                break;
            case BUILD_FILE_STRUCTURE:
                buildFileStructure(input, ruleGraph, tables);
                break;
            case REMOVE_FILE_STRUCTURE:
                removeFileStructure(input, ruleGraph, tables);
                break;
            case GRANT:
                grant(input, ruleGraph, users);
                break;
            case REVOKE:
                revoke(input, ruleGraph, users);
                break;
            case UNKNOWN:
            default:
                break;
        }
    }

    public void executeQuery(String[] query, RuleGraph ruleGraph, List<Table> tables) {

    }

    public void createTable(String[] createTable, RuleGraph ruleGraph, List<Table> tables) {

        // getting the table data
        String tableName = ruleGraph.getTokensAt(createTable, 2).get(0);
        List<String> columnNames = ruleGraph.getTokensAt(createTable, 4);
        List<String> dataTypeNames = ruleGraph.getTokensAt(createTable, 5, 6);
        List<String> sizeNames = ruleGraph.getTokensAt(createTable, 8);

        // convert the input into a table along with its column data
        Table table = new Table(tableName);
        List<Column> columns = new ArrayList<>();

        for(int i = 0; i < columnNames.size(); i++) {

            String columnName = columnNames.get(i);
            String dataTypeName = dataTypeNames.get(i);
            String sizeName = sizeNames.get(i);

            DataType dataType = DataType.convertToDataType(dataTypeName);
            int size = Integer.parseInt(sizeName);

            Column column = new Column(columnName, dataType, size);
            columns.add(column);
        }

        table.setColumns(columns);
        tables.add(table);
    }

    public void dropTable(String[] dropTable, RuleGraph ruleGraph, List<Table> tables) {

        // getting the table name to remove
        String tableNameToRemove = ruleGraph.getTokensAt(dropTable, 2).get(0);

        // remove the table
        for(int i = 0; i < tables.size(); i++) {
            String tableName = tables.get(i).getTableName();
            if(tableName.equalsIgnoreCase(tableNameToRemove)) {
                tables.remove(i);
                break;
            }
        }
    }

    public void alterTable(String[] alterTable, RuleGraph ruleGraph, List<Table> tables) {

        // getting the table, column data, and what to do
        String tableName = ruleGraph.getTokensAt(alterTable, 2).get(0);
        String alterType = ruleGraph.getTokensAt(alterTable, 3, 4, 5).get(0);

        if(alterType.equalsIgnoreCase("modify")) {

            String columnName = ruleGraph.getTokensAt(alterTable, 6).get(0);
            String dataTypeName = ruleGraph.getTokensAt(alterTable, 7, 8).get(0);
            String sizeName = ruleGraph.getTokensAt(alterTable, 10).get(0);

            DataType dataType = DataType.convertToDataType(dataTypeName);
            int size = Integer.parseInt(sizeName);

            outerLoop:
            for(Table table : tables) {
                if(table.getTableName().equalsIgnoreCase(tableName)) {
                    for(Column column : table.getColumns()) {
                        if(column.getName().equalsIgnoreCase(columnName)) {
                            column.setSize(size);
                            column.setDataType(dataType);
                            break outerLoop;
                        }
                    }
                }
            }

        } else if(alterType.equalsIgnoreCase("add")) {

            boolean isAddingKey = ! ruleGraph.getTokensAt(alterTable, 14).isEmpty();

            if(isAddingKey) {

                String keyTypeName = ruleGraph.getTokensAt(alterTable, 12, 13).get(0);
                String columnName = ruleGraph.getTokensAt(alterTable, 15).get(0);

                // TODO figure out conversions and stuff with keys
                if(keyTypeName.equalsIgnoreCase("foreign")) {

                } else if(keyTypeName.equalsIgnoreCase("primary")) {

                }

            } else {

                String columnName = ruleGraph.getTokensAt(alterTable, 6).get(0);

            }

        } else if(alterType.equalsIgnoreCase("drop")){
            String columnName = ruleGraph.getTokensAt(alterTable, 6).get(15);
        }
    }

    public void insert(String[] insert, RuleGraph ruleGraph, List<Table> tables) {

    }

    public void delete(String[] delete, RuleGraph ruleGraph, List<Table> tables) {

    }

    public void update(String[] update, RuleGraph ruleGraph, List<Table> tables) {

    }

    public void buildFileStructure(String[] buildFileStructure, RuleGraph ruleGraph, List<Table> tables) {

    }

    public void removeFileStructure(String[] removeFileStructure, RuleGraph ruleGraph, List<Table> tables) {

    }

    public void grant(String[] tokenizedInput, RuleGraph ruleGraph, List<User> users) {

    }

    public void revoke(String[] tokenizedInput, RuleGraph ruleGraph, List<User> users) {

    }
}