package systemcatalog;

import datastructures.misc.Pair;
import datastructures.misc.Triple;
import datastructures.relation.resultset.ResultSet;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.querytree.QueryTree;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import datastructures.user.User;
import datastructures.relation.table.Table;
import systemcatalog.components.Compiler;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;
import systemcatalog.components.SecurityChecker;
import systemcatalog.components.Verifier;
import utilities.Utilities;
import enums.InputType;

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
    private final Parser parser;
    private final Verifier verifier;
    private final SecurityChecker securityChecker;
    private final Optimizer optimizer;
    private final Compiler compiler;

    // system data
    private List<Table> tables;
    private List<User> users;
    private User currentUser;

    // a list of rule graph types that each component will use to extract data from user input
    private final List<RuleGraph> ruleGraphTypes;

    // type of input that was last executed
    private InputType inputType;

    // information about whether the input was successfully executed
    private boolean successfullyExecuted;
    private String executionMessage;

    // following will only be used if the input is a query and it's successfully executed
    private ResultSet resultSet;
    private List<QueryTree> queryTreeStates;
    private String naiveRelationalAlgebra, optimizedRelationalAlgebra;
    private Pair<List<Triple<String, String, String>>, List<Pair<String, String>>> recommendedFileStructures;
    private Pair<String, String> costAnalysis;

    public SystemCatalog() {

        // creating the system catalog components
        parser = new Parser();
        verifier = new Verifier();
        securityChecker = new SecurityChecker();
        optimizer = new Optimizer();
        compiler = new Compiler();

        // loading table and user data
        tables = Serializer.unSerializeTables(IO.readCurrentData(FileType.CurrentData.CURRENT_TABLES));
        users = Serializer.unSerializeUsers(IO.readCurrentData(FileType.CurrentData.CURRENT_USERS));

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
        inputType = InputType.UNKNOWN;

        // setting input execution to empty
        successfullyExecuted = false;
        executionMessage = "";

        // creating query-specific data, initially empty
        resultSet = new ResultSet();
        queryTreeStates = new ArrayList<>();
        naiveRelationalAlgebra = "";
        optimizedRelationalAlgebra = "";
        recommendedFileStructures = new Pair<>(new ArrayList<>(), new ArrayList<>());
        costAnalysis = new Pair<>("", "");
    }

    // execution -------------------------------------------------------------------------------------------------------

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

        clear();

        // there may be an unknown error that I didn't account for, try catch prevents the app from crashing
        try {

            // filtering the input, splitting the input into tokens, and determining the rule graph type to use
            String[] filteredInput = Utilities.filterInput(input);
            inputType = Utilities.determineInputType(filteredInput);

            // couldn't determine a type, don't execute
            if (inputType == InputType.UNKNOWN) {
                executionMessage = "Could not determine the input type. Refer to the Syntax Diagrams " +
                        "located in the \"Help\" Screen section for more information.";
                return;
            }

            // get the rule graph to use
            RuleGraph ruleGraph = ruleGraphTypes.get(inputType.getIndex());

            if (! parser.isValid(inputType, filteredInput)) {
                executionMessage = parser.getErrorMessage();
                return;
            }

            if (! verifier.isValid(inputType, filteredInput, tables, users)) {
                executionMessage = verifier.getErrorMessage();
                return;
            }

            if (! securityChecker.isValid(inputType, filteredInput, currentUser, tables)) {
                executionMessage = securityChecker.getErrorMessage();
                return;
            }

            // extract information from Optimizer if the input type is a QUERY
            if (inputType == InputType.QUERY) {
                queryTreeStates = optimizer.getQueryTreeStates(filteredInput, tables);
                naiveRelationalAlgebra = optimizer.getNaiveRelationAlgebra(queryTreeStates);
                optimizedRelationalAlgebra = optimizer.getOptimizedRelationalAlgebra(queryTreeStates);
                recommendedFileStructures = optimizer.getRecommendedFileStructures(queryTreeStates);
                costAnalysis = optimizer.getCostAnalysis(queryTreeStates, tables);
            }

            // at this point, the query/DML statement was successfully executed
            successfullyExecuted = true;

            if (inputType == InputType.QUERY) {
                resultSet = compiler.executeQuery(queryTreeStates, tables);
                executionMessage = "QUERY was successfully executed!";
            } else {
                compiler.executeDML(inputType, filteredInput, tables, users);
                executionMessage = "DML statement was successfully executed!";
            }

        } catch(Exception e) {
            executionMessage = "Unknown Error Occurred, I screwed up somewhere!";
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
    public InputType getInputType() {
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

    // QUERY related ---------------------------------------------------------------------------------------------------

    /**
     * @return result set of the last successfully executed QUERY
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * @return a list of query tree states for the last successfully executed QUERY
     */
    public List<QueryTree> getQueryTreeStates() {
        return queryTreeStates;
    }

    /**
     * @return naive relational algebra for the last successfully executed QUERY
     */
    public String getNaiveRelationalAlgebra() {
        return naiveRelationalAlgebra;
    }

    /**
     * @return optimized relational algebra for the last successfully executed QUERY
     */
    public String getOptimizedRelationalAlgebra() {
        return optimizedRelationalAlgebra;
    }

    /**
     * @return recommended file structures to build for the last successfully executed QUERY
     */
    public Pair<List<Triple<String, String, String>>, List<Pair<String, String>>> getRecommendedFileStructures() {
        return recommendedFileStructures;
    }

    /**
     * @return the cost analysis of the last successfully executed QUERY
     */
    public String getCostAnalysis() {
        return costAnalysis;
    }

    // options related -------------------------------------------------------------------------------------------------

    /**
     * Turns the Verifier on.
     */
    public void turnOnVerifier() {
        verifier.turnOn();
    }

    /**
     * Turns the Verifier off.
     */
    public void turnOffVerifier() {
        verifier.turnOff();
    }

    /**
     * Turns on the Security Checker component of the system catalog.
     */
    public void turnOnSecurityChecker() {
        securityChecker.turnOn();
    }

    /**
     * Turns off the Security Checker component of the system catalog.
     */
    public void turnOffSecurityChecker() {
        securityChecker.turnOff();
    }

    /**
     * Turns join optimization on which has a large impact on query tree structure and query cost.
     */
    public void turnOnJoinOptimization() {
        optimizer.turnOnJoinOptimization();
    }

    /**
     * Turns off join optimization.
     */
    public void turnOffJoinOptimization() {
        optimizer.turnOffJoinOptimization();
    }

    /**
     * Restores the database. This means that data such as users and tables will be set back to their default values.
     */
    public void restoreDatabase() {

        // load the original data
        //tables = Serialize.unSerializeTables(IO.readCurrentData(FileType.OriginalData.ORIGINAL_TABLES));
        //users = Serialize.unSerializeUsers(IO.readCurrentData(FileType.CurrentData.CURRENT_USERS));

        // write the original data out as the current data
        //Serializer.serializeUsers(users);
        //Serializer.serializeTables(tables);

        User DBA = User.DatabaseAdministrator(tables);
        users.add(0, DBA);
        currentUser = DBA;

        clear();

        turnOnVerifier();
        turnOnSecurityChecker();
        turnOnJoinOptimization();
    }

    /**
     * Resets most of the values within the system catalog as well as its components. Called when the user clears
     * the input and output sections within the terminal area or when the system catalog is executing some input.
     */
    public void clear() {

        parser.resetErrorMessage();
        verifier.resetErrorMessage();
        securityChecker.resetErrorMessage();

        successfullyExecuted = false;
        executionMessage = "";

        resultSet = new ResultSet();
        queryTreeStates = new ArrayList<>();
        naiveRelationalAlgebra = "";
        optimizedRelationalAlgebra = "";
        recommendedFileStructures = "";
        costAnalysis = "";
    }

    // IO related ------------------------------------------------------------------------------------------------------

    /**
     * Stores the tables and their states in files. Called when the system closes.
     */
    public void writeOutTables() {
        IO.writeCurrentData(Serializer.serializeTables(tables), FileType.CurrentData.CURRENT_TABLES);
    }

    /**
     * Stores the users and their states in files. Called when the system closes.
     */
    public void writeOutUsers() {
        // don't write out the DBA!
        users.remove(0);
        IO.writeCurrentData(Serializer.serializeUsers(users), FileType.CurrentData.CURRENT_USERS);
    }
}