package datastructures.datacontroller;

import datastructures.misc.Logger;
import datastructures.options.Options;
import datastructures.relation.resultset.ResultSet;
import datastructures.relation.table.Table;
import datastructures.trees.querytree.QueryTree;
import datastructures.user.User;
import files.io.FileType;
import files.io.IO;
import files.io.Serialize;
import systemcatalog.SystemCatalog;
import systemcatalog.components.*;
import systemcatalog.components.Compiler;

import java.util.List;

/**
 * Responsible for controlling all the data that will be used in the system. Starts
 * off by loading all the tables, users, and other data this application will need.
 * Data contained in this class will be passed to other classes which will make changes
 * to the data. Once the user closes the application, their changes get written so that
 * when they re-launch the application, their changes will still be there.
 */
public class DataController {

    // system data
    private List<Table> tables;
    private List<User> users;
    private User currentUser;

    // logger
    private Logger logger;

    // system catalog
    private SystemCatalog systemCatalog;

    public DataController() {

        // load all tables
        this.tables = Serialize.unSerializeTables(IO.readCurrentData(FileType.CurrentData.CURRENT_TABLES));

        // load all users
        this.users = Serialize.unSerializeUsers(IO.readCurrentData(FileType.CurrentData.CURRENT_USERS));

        // set the current user as the DBA who has all privileges on every table
        this.currentUser = User.DatabaseAdministrator(tables);

        this.logger = new Logger();
        this.systemCatalog = new SystemCatalog();
    }

    public void executeInput(String input) {
        systemCatalog.executeInput(input, tables, users, currentUser, logger);
    }

    // query specific methods

    public ResultSet getResultSet() {
        return systemCatalog.getResultSet();
    }

    public List<QueryTree> getQueryTreeStates() {
        return systemCatalog.getQueryTreeStates();
    }

    public List<String> getRecommendedFileStructures() {
        return systemCatalog.getRecommendedFileStructures();
    }
}