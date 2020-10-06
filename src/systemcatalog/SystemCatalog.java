package systemcatalog;

import datastructures.relation.resultset.ResultSet;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import files.io.FileType;
import files.io.IO;
import files.io.Serialize;
import datastructures.user.User;
import datastructures.relation.table.Table;
import systemcatalog.components.Compiler;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;
import systemcatalog.components.SecurityChecker;
import systemcatalog.components.Verifier;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for controlling all the data that will be used in the system. Starts
 * off by loading all the tables, users, and other data this application will need.
 * Data contained in this class will be passed to other classes which will make changes
 * to the data. Once the user closes the application, their changes get written so that
 * when they re-launch the application, their changes will still be there.
 */
public class SystemCatalog {

    // system catalog components
    private Parser parser;
    private Verifier verifier;
    private SecurityChecker securityChecker;
    private Optimizer optimizer;
    private Compiler compiler;

    // system data
    private List<Table> tables;
    private List<User> users;
    private User currentUser;

    // a list of rule graph types that each component will use to extract data from user input
    private final List<RuleGraph> ruleGraphTypes;

    // type of input that was last executed
    private RuleGraph.Type inputType;

    // information about whether the input was successfully executed
    private boolean successfullyExecuted;
    private String executionMessage;

    // following will only be used if the input is a query and it's successfully executed
    private ResultSet resultSet;
    private List<QueryTree> queryTreeStates;
    private String naiveRelationalAlgebra, optimizedRelationalAlgebra, recommendedFileStructures, costAnalysis;

    public SystemCatalog() {

        parser = new Parser();
        verifier = new Verifier();
        securityChecker = new SecurityChecker();
        optimizer = new Optimizer();
        compiler = new Compiler();

        // loading table and user data
        tables = Serialize.unSerializeTables(IO.readCurrentData(FileType.CurrentData.CURRENT_TABLES));
        users = Serialize.unSerializeUsers(IO.readCurrentData(FileType.CurrentData.CURRENT_USERS));

        // set the current user as the DBA who has all privileges on every table
        User DBA = User.DatabaseAdministrator(tables);
        setCurrentUser(DBA);
        users.add(0, DBA);

        // create and add the rule graph types to use
        ruleGraphTypes = Arrays.asList(
                RuleGraphTypes.getQueryRuleGraph(),
                RuleGraphTypes.getCreateTableRuleGraph(),
                RuleGraphTypes.getAlterTableRuleGraph(),
                RuleGraphTypes.getDropTableRuleGraph(),
                RuleGraphTypes.getInsertRuleGraph(),
                RuleGraphTypes.getDeleteRuleGraph(),
                RuleGraphTypes.getUpdateRuleGraph(),
                RuleGraphTypes.getGrantRuleGraph(),
                RuleGraphTypes.getRevokeRuleGraph(),
                RuleGraphTypes.getBuildFileStructureRuleGraph(),
                RuleGraphTypes.getRemoveFileStructureRuleGraph()
        );

        // setting the input type as unknown for now
        inputType = RuleGraph.Type.UNKNOWN;

        // setting input execution to empty
        successfullyExecuted = false;
        executionMessage = "";

        // creating query-specific data, initially empty
        resultSet = new ResultSet();
        queryTreeStates = new ArrayList<>();
        naiveRelationalAlgebra = "";
        optimizedRelationalAlgebra = "";
        recommendedFileStructures = "";
        costAnalysis = "";
    }

    // execution related -----------------------------------------------------------------------------------------------

    /**
     * Given a raw string of input, executes it. Well there is more involved than that. Raw input is
     * filtered, formatted, and tokenized. It gets passed to the Parser to ensure that it is
     * syntactically correct. Then it's passed to a Verifier to make sure that it makes sense
     * with respect to the data stored on the system. Afterwards, the Security Checker ensures that
     * the current user has the correct privileges before execution. The Optimizer creates an
     * execution strategy for the input. Finally, the Compiler executes the input.
     * @param input is the input to execute
     */
    public void executeInput(String input) {

        successfullyExecuted = false; // assume the input is invalid until it passes all checks

        // there may be an unknown error that I didn't account for, this prevents the app from crashing
        try {

            // filtering the input, splitting the input into tokens, and determining the rule graph type to use
            String[] filteredInput = Utilities.filterInput(input);
            inputType = Utilities.determineRuleGraphType(filteredInput);

            // couldn't determine a type, don't execute
            if (inputType == RuleGraph.Type.UNKNOWN) {
                executionMessage = "Could not determine the input type. Refer to the Syntax Diagrams " +
                        "located in the \"Help\" Screen section for more information.";
                return;
            }

            // get the rule graph to use
            RuleGraph ruleGraph = ruleGraphTypes.get(inputType.index());

            if (! parser.isValid(inputType, ruleGraph, filteredInput)) {
                executionMessage = parser.getErrorMessage();
                return;
            }

            if (! verifier.isValid(inputType, filteredInput, tables, users)) {
                executionMessage = verifier.getExecutionMessage();
                return;
            }

            // security checker stuff
            securityChecker.setRuleGraphType(inputType);
            securityChecker.setRuleGraphToUse(ruleGraph);
            securityChecker.setTokenizedInput(filteredInput);
            securityChecker.setCurrentUser(currentUser);
            securityChecker.setTables(tables);
            if (!securityChecker.isValid()) {
                return;
            }

            // optimizer stuff, if the input is a query, get the query tree states, otherwise skip
            if (inputType == RuleGraph.Type.QUERY) {
                this.queryTreeStates = optimizer.getQueryTreeStates(filteredInput, tables);
                //this.naiveRelationalAlgebra = optimizer.getNaiveRelationalAlgebra();
                //this.optimizedRelationalAlgebra = optimizer.getOptimizedRelationalAlgebra();
                //this.recommendedFileStructures = optimizer.getRecommendedFileStructures();
            } else {
                this.queryTreeStates = new ArrayList<>();
                this.recommendedFileStructures = "";
            }

            // compiler stuff, if the input is a query, get the result set, otherwise make the changes to the system
            compiler.setRuleGraphType(inputType);
            compiler.setRuleGraphToUse(ruleGraph);
            compiler.setTokenizedInput(filteredInput);
            compiler.setTables(tables);
            compiler.setUsers(users);
            if (inputType == RuleGraph.Type.QUERY) {
                compiler.setQueryTreeStates(queryTreeStates);
                this.resultSet = compiler.executeQuery();
            } else {
                compiler.executeDML();
            }

            // made it through everything, successful execution
            logger.setSuccessfullyExecuted(true);

        } catch(Exception e) {

            e.printStackTrace();
        }
    }

    // input related ---------------------------------------------------------------------------------------------------

    /**
     * @return whether the input was successfully executed
     */
    public boolean wasSuccessfullyExecuted() {
        return successfullyExecuted;
    }

    /**
     * @return contains a message about what happened during execution
     */
    public String getExecutionMessage() {
        return executionMessage;
    }

    /**
     * @return the type of input that was last executed.
     */
    public RuleGraph.Type getInputType() {
        return inputType;
    }

    // user related ----------------------------------------------------------------------------------------------------

    /**
     * Sets the current user to the one supplied
     * @param currentUser is the current user to set
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @return a list of users of the system
     */
    public List<User> getUsers() {
        return users;
    }

    // table related ---------------------------------------------------------------------------------------------------

    /**
     * @return a list of tables of system
     */
    public List<Table> getTables() {
        return tables;
    }

    // query related ---------------------------------------------------------------------------------------------------

    /**
     * If a query was performed and successfully executed, returns the result set of that execution.
     * Otherwise, an empty result set is returned;
     * @return result set of a successful execution or an empty result set
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * If a query was performed and successfully executed, returns a list of states that the query tree
     * took on during the optimization process. Otherwise, an empty list is returned.
     * @return a list of query tree states that the query tree took on during the optimization process
     * or an empty list
     */
    public List<QueryTree> getQueryTreeStates() {
        return queryTreeStates;
    }

    public String getNaiveRelationalAlgebra() {
        return naiveRelationalAlgebra;
    }

    public String getOptimizedRelationalAlgebra() {
        return optimizedRelationalAlgebra;
    }

    /**
     * If a query was performed and successfully executed, returns a list of recommended file structures
     * for that query. Otherwise, an empty list is returned.
     * @return a list of recommended file structures for a query or an empty list
     */
    public String getRecommendedFileStructures() {
        return recommendedFileStructures;
    }

    public String getCostAnalysis() {
        return costAnalysis;
    }

    // Options related -------------------------------------------------------------------------------------------------

    /**
     * Toggles whether the system catalog should use the Verifier component to make referential
     * checks with the data located on the system.
     */
    public void toggleVerifier() {
        verifier.toggle();
    }

    /**
     * Toggles whether the system catalog optimizes the ordering of joins. This has an impact on
     * what the query tree will look like along with query costs.
     */
    public void toggleJoinOptimization() {
        optimizer.toggleRearrangeLeafNodes();
    }

    /**
     * Restores the database. This means that data such as users and tables will be restored back to
     * their defaults.
     */
    public void restoreDatabase() {

        logger.clear();

        // load the original data
        //tables = Serialize.unSerializeTables(IO.readCurrentData(FileType.OriginalData.ORIGINAL_TABLES));
        //users = Serialize.unSerializeUsers(IO.readCurrentData(FileType.CurrentData.CURRENT_USERS));

        User DBA = User.DatabaseAdministrator(tables);
        setCurrentUser(DBA);
        users.add(0, DBA);

        this.inputType = RuleGraph.Type.UNKNOWN;

        this.resultSet = new ResultSet();
        this.queryTreeStates = new ArrayList<>();
        this.recommendedFileStructures = "";

        // writing out the original table and user data

    }

    // IO related ------------------------------------------------------------------------------------------------------

    /**
     * Stores the tables and their states in files. Called when the system closes.
     */
    public void writeOutTables() {
        IO.writeCurrentData(Serialize.serializeTables(tables), FileType.CurrentData.CURRENT_TABLES);
    }

    /**
     * Stores the users and their states in files. Called when the system closes.
     */
    public void writeOutUsers() {
        IO.writeCurrentData(Serialize.serializedUsers(users), FileType.CurrentData.CURRENT_USERS);
    }
}