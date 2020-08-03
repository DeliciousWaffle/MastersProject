package systemcatalog;

import datastructure.relation.resultset.ResultSet;
import datastructure.rulegraph.RuleGraph;
import datastructure.rulegraph.type.RuleGraphTypes;
import systemcatalog.components.Parser;
import datastructure.user.User;
import utilities.enums.InputType;
import systemcatalog.components.Verifier;
import datastructure.relation.table.Table;
import systemcatalog.components.SecurityChecker;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Compiler;

import java.util.ArrayList;

/**
 * Class that holds all of the components that make this program entire run.
 * Basically a massive encapsulation burrito because some members need data
 * from other members in order to function.
 */
public class SystemCatalog {

    private Parser          parser;
    private Verifier        verifier;
    private SecurityChecker securityChecker;
    private Optimizer       optimizer;
    private Compiler        compiler;

    private User                  currentUser;
    private ArrayList<User>       users;
    private ArrayList<Table>      tables;
    private ArrayList<RuleGraph>  ruleGraphs;

    private ResultSet resultSet;
    private String    executionCode;

    public SystemCatalog() {

        parser          = new Parser();
        verifier        = new Verifier();
        securityChecker = new SecurityChecker();
        optimizer       = new Optimizer();
        compiler        = new Compiler();

        currentUser = new User();
        users       = new ArrayList<>();
        tables      = new ArrayList<>();
        ruleGraphs  = new ArrayList<>();

        loadRuleGraphs();
    }


    private void loadRuleGraphs() {

        RuleGraphTypes ruleGraphTypes = new RuleGraphTypes();

        ruleGraphs.add(ruleGraphTypes.getQueryRuleGraph());
        ruleGraphs.add(ruleGraphTypes.getCreateTableRuleGraph());
        ruleGraphs.add(ruleGraphTypes.getDropTableRuleGraph());
        ruleGraphs.add(ruleGraphTypes.getInsertRuleGraph());
        ruleGraphs.add(ruleGraphTypes.getDeleteRuleGraph());
        ruleGraphs.add(ruleGraphTypes.getUpdateRuleGraph());
        ruleGraphs.add(ruleGraphTypes.getGrantRuleGraph());
        ruleGraphs.add(ruleGraphTypes.getRevokeRuleGraph());
    }

    public void executeInput(String input) {

        String[] tokenizedInput = parser.formatAndTokenizeInput(input);
        InputType inputType = parser.determineInputType(tokenizedInput);

        if(inputType == InputType.UNKNOWN) {

            System.out.println("In SystemCatalog.executeInput()");
            System.out.println("Invalid input type: " + tokenizedInput[0]);
            executionCode = "Invalid Input";
        }

        // parser, verifier, and security checker will work for any input type
        // parser validation
        parser.setRuleGraph(ruleGraphs.get(inputType.getCode()));

        if(! parser.isValid(inputType, tokenizedInput)) {
            return;
        }

        // verifier validation
        verifier.setRuleGraph(ruleGraphs.get(inputType.getCode()));

        if(! verifier.isValid(inputType, tokenizedInput, users, tables)) {
            return;
        }

        // security checker validation
        securityChecker.setRuleGraph(ruleGraphs.get(inputType.getCode()));

        if(! securityChecker.isValid(inputType, tokenizedInput, currentUser, tables)) {
            return;
        }

        // after making it through all the checks, execute the input
        // TODO: figure out what to do with optimizer
        //compiler.executeInput(inputType, tokenizedInput, ruleGraphs.get(inputType.getCode()), users, tables);
    }

    /**
     * After executing a user's input, this returns whether it was successful. If not, returns
     * some kind of error message.
     * @return
     */
    public String getExecutionCode() {
        return executionCode;
    }
}
