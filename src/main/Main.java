package main;

/**
 * Masters Project that emulates a subset of SQL's System Catalog. The System Catalog
 * is responsible for validating queries, DDL statements, and making sure users
 * have the permissions to perform an action. It consists of a Compiler, Parser,
 * Verifier, Optimizer, and Security Checker. Each component plays a role to ensure
 * actions are correctly being carried out. Run this file to start the program.
 * @autor       Jake Bussa
 * @version     1.0
 * @since       2020-04-12
 */

public class Main {

    /*private QueryParser queryParser;
    private DMLParser dmlParser;
    private PrivilegeParser privilegeParser;

    private QueryVerifier queryVerifier;
    private DMLVerifier dmlVerifier;

    private ArrayList<User> users;
    private SecurityChecker securityChecker;

    private queryOptimizer queryOptimizer;

    private QueryCompiler queryCompiler;
    private DMLCompiler dmlCompiler;

    public Main() {

        queryParser = new QueryParser();
        dmlParser = new DMLParser();
        privilegeParser = new PrivilegeParser();

        queryVerifier = new QueryVerifier();
        dmlVerifier = new DMLVerifier();

        users = new ArrayList<>();
        securityChecker = new SecurityChecker();

        queryOptimizer = new QueryOptimizer();

        queryCompiler = new QueryCompiler();
        dmlCompiler = new DMLCompiler();

        start();
    }

    private void start() {

        String input = getInput();
        boolean valid = parserStuff(input);
        if(! valid) p("Fail!");
        else p("Success!");
        valid = verifierStuff(input);
        if(! valid) return;
        valid = securityCheckerStuff(input);
        if(! valid) return;
        optimizerStuff(input);
        compilerStuff(input);
    }

    private String getInput() {

        StringBuilder sb = new StringBuilder();

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            boolean done = false;

            while (!done) {

                char token = (char) br.read();

                if (token == ';') {
                    done = true;
                } else {
                    sb.append(token);
                }
            }

            br.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private boolean parserStuff(String input) {

        input = Utilities.formatInput(input);

        switch(Utilities.getInputType(input)) {
            case "QUERY":
                queryParser.setQuery(input);
                return queryParser.isValid();
            case "DML":
                dmlParser.setDMLStatement(input);
                return dmlParser.isValid();
            case "PRIVILEGE":
                privilegeParser.setPrivilegeStatement(input);
                return privilegeParser.isValid();
        }

        // BOGUS
        return false;
    }

    private boolean verifierStuff(String input) {
        return false;
    }

    private boolean securityCheckerStuff(String input) {
        return false;
    }

    private void optimizerStuff(String input) {

    }
    private void compilerStuff(String input) {

    }

    private void p(String input) {

        System.out.println(input);
    }

    public static void main(String[] args) {

        new Main();
    }*/
}
