package systemcatalog;

import datastructures.misc.Log;
import datastructures.misc.Logger;
import datastructures.relation.resultset.ResultSet;
import datastructures.rulegraph.RuleGraph;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import systemcatalog.components.Parser;
import datastructures.user.User;
import utilities.enums.InputType;
import systemcatalog.components.Verifier;
import datastructures.relation.table.Table;
import systemcatalog.components.SecurityChecker;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds all of the components of the system catalog.
 */
public class SystemCatalog {

    // used for logging messages
    private Logger logger;

    // system catalog components that work together to make this application work
    private Parser          parser;
    private Verifier        verifier;
    private SecurityChecker securityChecker;
    private Optimizer       optimizer;
    private Compiler        compiler;

    // a list of rule graph types that each component will use to extract data from user input
    private List<RuleGraph> ruleGraphTypes;

    // following will only be used if the input is a query and it's successfully executed
    private ResultSet resultSet;
    private List<QueryTree> queryTreeStates;
    private List<String> recommendedFileStructures;

    public SystemCatalog() {

        this.logger = new Logger();

        this.parser          = new Parser();
        this.verifier        = new Verifier();
        this.securityChecker = new SecurityChecker();
        this.optimizer       = new Optimizer();
        this.compiler        = new Compiler();

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

        this.resultSet = new ResultSet();
        this.queryTreeStates = new ArrayList<>();
        this.recommendedFileStructures = new ArrayList<>();
    }

    public void executeInput(String input, List<Table> tables, List<User> users, User currentUser, Logger logger) {

        // filtering the input, splitting the input into tokens, and determining the rule graph type to use
        String[] tokenizedInput = Parser.formatAndTokenizeInput(input);
        RuleGraph.Type ruleGraphType = Parser.determineRuleGraphType(tokenizedInput);

        // couldn't determine a type, don't execute
        if(ruleGraphType == RuleGraph.Type.UNKNOWN) {
            logger.log(new Log(Log.Type.SIMPLE, "Error! Couldn't determine input type!"));
            return;
        }

        // get the rule graph to use
        RuleGraph ruleGraphToUse = ruleGraphTypes.get(ruleGraphType.index());

        // parser stuff
        parser.setRuleGraphType(ruleGraphType);
        parser.setRuleGraphToUse(ruleGraphToUse);
        parser.setTokenizedInput(tokenizedInput);
        if(! parser.validate()) {
            return;
        }

        // verifier stuff
        verifier.setRuleGraphType(ruleGraphType);
        verifier.setRuleGraphToUse(ruleGraphToUse);
        verifier.setTokenizedInput(tokenizedInput);
        verifier.setTables(tables);
        verifier.setUsers(users);
        if(! verifier.validate()) {
            return;
        }

        // security checker stuff
        securityChecker.setRuleGraphType(ruleGraphType);
        securityChecker.setRuleGraphToUse(ruleGraphToUse);
        securityChecker.setTokenizedInput(tokenizedInput);
        securityChecker.setCurrentUser(currentUser);
        securityChecker.setTables(tables);
        if(! securityChecker.validate()) {
            return;
        }

        // optimizer stuff, if the input is a query, get the query tree states, otherwise skip
        if(ruleGraphType == RuleGraph.Type.QUERY) {
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
        compiler.setRuleGraphToUse(ruleGraphToUse);
        compiler.setTokenizedInput(tokenizedInput);
        compiler.setTables(tables);
        compiler.setUsers(users);
        compiler.setQueryTreeToExecute(queryTreeToExecute);
        this.resultSet = compiler.executeInput();
    }

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

    /**
     * If a query was performed and successfully executed, returns a list of recommended file structures
     * for that query. Otherwise, an empty list is returned.
     * @return a list of recommended file structures for a query or an empty list
     */
    public List<String> getRecommendedFileStructures() {
        return recommendedFileStructures;
    }
}