package utilities;

import datastructures.relation.table.Table;
import datastructures.user.User;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Contains random stuff that I don't know where to place just yet.
 */
public class Utils {

    public List<Table> getTablesFromTableNames(List<Table> tableNames, List<Table> tables) {
        return null;
    }

    public Table getTableFromTableName(String tableName, List<Table> tables) {
        try {
            return tables
                    .stream()
                    .filter(table -> table.getTableName().equalsIgnoreCase(tableName))
                    .findFirst()
                    .get();
        } catch(NoSuchElementException e) {
            System.out.println("In Utilities.getTableReference()");
            e.printStackTrace();
        }
        return new Table();
    }

    public User getUserFromUsername(String username, List<User> users) {
        try {
            return users.stream()
                    .filter(user -> user.getUsername().equalsIgnoreCase(username))
                    .findFirst()
                    .get();
        } catch(NoSuchElementException e) {
            System.out.println("In Utilities.getUserReference()");
            e.printStackTrace();
        }
        return new User();
    }


}