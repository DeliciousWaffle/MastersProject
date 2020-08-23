package systemcatalog;

import datastructures.misc.Log;
import datastructures.misc.Logger;
import datastructures.relation.resultset.ResultSet;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import files.io.FileType;
import files.io.IO;
import files.io.Serialize;
import systemcatalog.components.Parser;
import datastructures.user.User;
import systemcatalog.components.Verifier;
import datastructures.relation.table.Table;
import systemcatalog.components.SecurityChecker;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for controlling all the data that will be used in the system. Starts
 * off by loading all the tables, users, and other data this application will need.
 * Data contained in this class will be passed to other classes which will make changes
 * to the data. Once the user closes the application, their changes get written so that
 * when they re-launch the application, their changes will still be there.
 */
public class SystemCatalog {

    // system catalog components that work together to make this application work
    private final Parser parser;
    private final Verifier verifier;
    private final SecurityChecker securityChecker;
    private final Optimizer optimizer;
    private final Compiler compiler;

    // system data
    private final List<Table> tables;
    private final List<User> users;
    private User currentUser;

    // used for logging messages
    private final Logger logger;

    // a list of rule graph types that each component will use to extract data from user input
    private final List<RuleGraph> ruleGraphTypes;

    // type of input that was last executed
    RuleGraph.Type inputType;

    // following will only be used if the input is a query and it's successfully executed
    private ResultSet resultSet;
    private List<QueryTree> queryTreeStates;
    private List<String> recommendedFileStructures;

    public SystemCatalog() {

        // create the system catalog components
        this.parser = new Parser();
        this.verifier = new Verifier();
        this.securityChecker = new SecurityChecker();
        this.optimizer = new Optimizer();
        this.compiler = new Compiler();

        // create the logger
        this.logger = new Logger();

        // create and add the rule graph types to use
        this.ruleGraphTypes  = new ArrayList<>();

        ruleGraphTypes.add(RuleGraphTypes.getQueryRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getCreateTableRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getAlterTableRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getDropTableRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getInsertRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getDeleteRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getUpdateRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getGrantRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getRevokeRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getBuildFileStructureRuleGraph());
        ruleGraphTypes.add(RuleGraphTypes.getRemoveFileStructureRuleGraph());

        // loading table and user data
        this.tables = Serialize.unSerializeTables(IO.readCurrentData(FileType.CurrentData.CURRENT_TABLES));
        this.users = Serialize.unSerializeUsers(IO.readCurrentData(FileType.CurrentData.CURRENT_USERS));

        // set the current user as the DBA who has all privileges on every table
        User DBA = User.DatabaseAdministrator(tables);
        setCurrentUser(DBA);
        users.add(0, DBA);

        // setting the input type as unknown for now
        this.inputType = RuleGraph.Type.UNKNOWN;

        // creating query-specific data, initially empty
        this.resultSet = new ResultSet();
        this.queryTreeStates = new ArrayList<>();
        this.recommendedFileStructures = new ArrayList<>();
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

        System.out.println("Execute Input");

        // used for logging what happened during the execution and whether it was successful
        logger.clear();

        // there may be some unknown bugs that I haven't taken care of yet, prevent the app from crashing
        try {

            // filtering the input, splitting the input into tokens, and determining the rule graph type to use
            String[] tokenizedInput = Parser.formatAndTokenizeInput(input);
            this.inputType = Parser.determineRuleGraphType(tokenizedInput);

            // couldn't determine a type, don't execute
            if (inputType == RuleGraph.Type.UNKNOWN) {
                logger.log(new Log(Log.Type.SIMPLE, "Error! Couldn't determine input type!"));
                return;
            }

            // get the rule graph to use
            RuleGraph ruleGraphToUse = ruleGraphTypes.get(inputType.index());

            // parser stuff
            parser.setRuleGraphType(inputType);
            parser.setRuleGraphToUse(ruleGraphToUse);
            parser.setTokenizedInput(tokenizedInput);
            if (! parser.isValid()) {
                return;
            }

            // verifier stuff
            verifier.setRuleGraphType(inputType);
            verifier.setRuleGraphToUse(ruleGraphToUse);
            verifier.setTokenizedInput(tokenizedInput);
            verifier.setTables(tables);
            verifier.setUsers(users);
            if (! verifier.isValid()) {
                return;
            }

            // security checker stuff
            securityChecker.setRuleGraphType(inputType);
            securityChecker.setRuleGraphToUse(ruleGraphToUse);
            securityChecker.setTokenizedInput(tokenizedInput);
            securityChecker.setCurrentUser(currentUser);
            securityChecker.setTables(tables);
            if (! securityChecker.isValid()) {
                return;
            }

            // optimizer stuff, if the input is a query, get the query tree states, otherwise skip
            if (inputType == RuleGraph.Type.QUERY) {
                optimizer.setRuleGraphToUse(ruleGraphToUse);
                optimizer.setTokenizedInput(tokenizedInput);
                optimizer.setTables(tables);
                optimizer.optimize();
                this.queryTreeStates = optimizer.getQueryTreeStates();
                this.recommendedFileStructures = optimizer.getRecommendedFileStructures();
            } else {
                this.queryTreeStates = new ArrayList<>();
                this.recommendedFileStructures = new ArrayList<>();
            }

            // compiler stuff, if the input is a query, get the result set, otherwise make the changes to the system
            compiler.setRuleGraphType(inputType);
            compiler.setRuleGraphToUse(ruleGraphToUse);
            compiler.setTokenizedInput(tokenizedInput);
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
            System.out.println("Some unknown error occurred in SystemCatalog.execute()");
            e.printStackTrace();
        }
    }

    // input related ---------------------------------------------------------------------------------------------------

    /**
     * @return the logger which gives information about what happened during execution
     */
    public Logger getLogger() {
        return logger;
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
        return resultSet == null ? new ResultSet() : resultSet;
    }

    /**
     * If a query was performed and successfully executed, returns a list of states that the query tree
     * took on during the optimization process. Otherwise, an empty list is returned.
     * @return a list of query tree states that the query tree took on during the optimization process
     * or an empty list
     */
    public List<QueryTree> getQueryTreeStates() {
        return queryTreeStates == null ? new ArrayList<>() : queryTreeStates;
    }

    /**
     * If a query was performed and successfully executed, returns a list of recommended file structures
     * for that query. Otherwise, an empty list is returned.
     * @return a list of recommended file structures for a query or an empty list
     */
    public List<String> getRecommendedFileStructures() {
        return recommendedFileStructures == null ? new ArrayList<>() : recommendedFileStructures;
    }

    // Options related -------------------------------------------------------------------------------------------------

    /**
     * Determines whether the system catalog should use the Verifier component to make referential
     * checks with the data located on the system.
     * @param toggle set to true to use the Verifier, false to not use the Verifier
     */
    public void toggleVerifier(boolean toggle) {
        verifier.setToggle(toggle);
    }

    /**
     * Determines whether the system catalog should use the Security Checker component to check
     * that the current user has the correct privileges to execute input.
     * @param toggle set to true to use the Security Checker, false to not use the Security Checker
     */
    public void toggleSecurityChecker(boolean toggle) {
        securityChecker.setToggle(toggle);
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