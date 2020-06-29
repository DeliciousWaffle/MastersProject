package systemcatalog.parser;

// TODO remove
public class DMLParser {
/*
    private ArrayList<RuleGraph> ruleGraphs;
    private String dmlStatement;

    public DMLParser() {

        ruleGraphs = new ArrayList<>();

        RuleGraph createTableRuleGraph = new RuleGraph();
        RuleGraph dropTableRuleGraph = new RuleGraph();
        RuleGraph insertRuleGraph = new RuleGraph();
        RuleGraph deleteRuleGraph = new RuleGraph();
        RuleGraph updateRuleGraph = new RuleGraph();

        // adding each rule to its associated graph systemcatalog.parser
        createTableRuleGraph.addRule("CREATE",     false, 0);
        createTableRuleGraph.addRule("TABLE",      false, 1);
        createTableRuleGraph.addRule("TableName",  true, 2);
        createTableRuleGraph.addRule("(",          false, 3);
        createTableRuleGraph.addRule("ColumnName", true, 4);
        createTableRuleGraph.addRule("NUMBER",     false, 5);
        createTableRuleGraph.addRule("CHAR",       false, 6);
        createTableRuleGraph.addRule("(",          false, 7);
        createTableRuleGraph.addRule("Size",       true, 8);
        createTableRuleGraph.addRule(")",          false, 9);
        createTableRuleGraph.addRule(",",          false, 10);
        createTableRuleGraph.addRule(")",          false, 11);
        createTableRuleGraph.addRule(";",          false, 12);

        dropTableRuleGraph.addRule("DROP",         false, 0);
        dropTableRuleGraph.addRule("TABLE",        false, 1);
        dropTableRuleGraph.addRule("TableName",    true, 2);
        dropTableRuleGraph.addRule(";",            false, 3);

        insertRuleGraph.addRule("INSERT",          false, 0);
        insertRuleGraph.addRule("INTO",            false, 1);
        insertRuleGraph.addRule("TableName",       true, 2);
        insertRuleGraph.addRule("VALUES",          false, 3);
        insertRuleGraph.addRule("(",               false, 4);
        insertRuleGraph.addRule("Value",           true, 5);
        insertRuleGraph.addRule(",",               false, 6);
        insertRuleGraph.addRule(")",               false, 7);
        insertRuleGraph.addRule(";",               false, 8);

        deleteRuleGraph.addRule("DELETE",          false, 0);
        deleteRuleGraph.addRule("FROM",            false, 1);
        deleteRuleGraph.addRule("TableName",       true, 2);
        deleteRuleGraph.addRule("WHERE",           false, 3);
        deleteRuleGraph.addRule("ColumnName",      true, 4);
        deleteRuleGraph.addRule("=",               false, 5);
        deleteRuleGraph.addRule("!=",              false, 6);
        deleteRuleGraph.addRule(">",               false, 7);
        deleteRuleGraph.addRule("<",               false, 8);
        deleteRuleGraph.addRule(">=",              false, 9);
        deleteRuleGraph.addRule("<=",              false, 10);
        deleteRuleGraph.addRule("Constant",        true, 11);
        deleteRuleGraph.addRule(";",               false, 12);

        updateRuleGraph.addRule("UPDATE",          false, 0);
        updateRuleGraph.addRule("TableName",       true, 1);
        updateRuleGraph.addRule("SET",             false, 2);
        updateRuleGraph.addRule("ColumnName",      true, 3);
        updateRuleGraph.addRule("=",               false, 4);
        updateRuleGraph.addRule("Constant",        true, 5);
        updateRuleGraph.addRule("WHERE",           false, 6);
        updateRuleGraph.addRule("ColumnName",      true, 7);
        updateRuleGraph.addRule("=",               false, 8);
        updateRuleGraph.addRule("Constant",        true, 9);
        updateRuleGraph.addRule(";",               false, 10);

        // setting the valid rule transitions for each rule in each graph systemcatalog.parser
        createTableRuleGraph.setChildren(0, 1);
        createTableRuleGraph.setChildren(1, 2);
        createTableRuleGraph.setChildren(2, 3);
        createTableRuleGraph.setChildren(3, 4);
        createTableRuleGraph.setChildren(4, 5, 6);
        createTableRuleGraph.setChildren(5, 7);
        createTableRuleGraph.setChildren(6, 7);
        createTableRuleGraph.setChildren(7, 8);
        createTableRuleGraph.setChildren(8, 9);
        createTableRuleGraph.setChildren(9, 10, 11);
        createTableRuleGraph.setChildren(10, 4);
        createTableRuleGraph.setChildren(11, 12);
        createTableRuleGraph.setChildren(12);

        dropTableRuleGraph.setChildren(0, 1);
        dropTableRuleGraph.setChildren(1, 2);
        dropTableRuleGraph.setChildren(2, 3);
        dropTableRuleGraph.setChildren(3);

        insertRuleGraph.setChildren(0, 1);
        insertRuleGraph.setChildren(1, 2);
        insertRuleGraph.setChildren(2, 3);
        insertRuleGraph.setChildren(3, 4);
        insertRuleGraph.setChildren(4, 5);
        insertRuleGraph.setChildren(5, 6, 7);
        insertRuleGraph.setChildren(6, 5);
        insertRuleGraph.setChildren(7, 8);
        insertRuleGraph.setChildren(8);

        deleteRuleGraph.setChildren(0, 1);
        deleteRuleGraph.setChildren(1, 2);
        deleteRuleGraph.setChildren(2, 3);
        deleteRuleGraph.setChildren(3, 4);
        deleteRuleGraph.setChildren(4, 5, 6, 7, 8, 9, 10);
        deleteRuleGraph.setChildren(5, 11);
        deleteRuleGraph.setChildren(6, 11);
        deleteRuleGraph.setChildren(7, 11);
        deleteRuleGraph.setChildren(8, 11);
        deleteRuleGraph.setChildren(9, 11);
        deleteRuleGraph.setChildren(10, 11);
        deleteRuleGraph.setChildren(11, 12);
        deleteRuleGraph.setChildren(12);

        updateRuleGraph.setChildren(0, 1);
        updateRuleGraph.setChildren(1, 2);
        updateRuleGraph.setChildren(2, 3);
        updateRuleGraph.setChildren(3, 4);
        updateRuleGraph.setChildren(4, 5);
        updateRuleGraph.setChildren(5, 6, 10);
        updateRuleGraph.setChildren(6, 7);
        updateRuleGraph.setChildren(7, 8);
        updateRuleGraph.setChildren(8, 9);
        updateRuleGraph.setChildren(9, 10);
        updateRuleGraph.setChildren(10);

        // finally add all the junk to our list
        ruleGraphs.add(createTableRuleGraph);
        ruleGraphs.add(dropTableRuleGraph);
        ruleGraphs.add(insertRuleGraph);
        ruleGraphs.add(deleteRuleGraph);
        ruleGraphs.add(updateRuleGraph);
    }

    public void setDMLStatement(String dmlStatement) {
        this.dmlStatement = dmlStatement;
    }

    public String getDMLStatement() {
        return dmlStatement;
    }

    public boolean isValid() {

        int graphParserToUse = getGraphParserToUse();
        boolean foundValidGraphParser = graphParserToUse != -1;

        if(! foundValidGraphParser) {
            return false;
        }

        boolean isSyntacticallyCorrect = ruleGraphs.get(graphParserToUse).isSyntacticallyCorrect(dmlStatement);

        if(! isSyntacticallyCorrect) {
            return false;
        }

        // only dml statement with possible duplication is CREATE TABLE's columns
        boolean hasDuplicates = false;

        if(graphParserToUse == 0) {
            hasDuplicates = ruleGraphs.get(0).hasDuplicatesAt(dmlStatement, 4);
        }

        boolean hasIllegalKeyword = ruleGraphs.get(graphParserToUse).hasIllegalKeyword(dmlStatement);
        boolean hasIllegalNumeric = false;

        if(graphParserToUse == 0) {
            hasIllegalNumeric = ruleGraphs.get(0).hasIllegalNumericAt(dmlStatement, 8);
        }

        if(graphParserToUse == 3) {
            hasIllegalNumeric = ruleGraphs.get(3).hasIllegalNumericAt(dmlStatement, 11, 7, 8, 9, 10);
        }

        return ! hasDuplicates && ! hasIllegalKeyword && ! hasIllegalNumeric;
    }

    public int getGraphParserToUse() {

        String firstToken = dmlStatement.split("\\s+")[0];

        switch(firstToken) {
            case "CREATE":
                return 0;
            case "DROP":
                return 1;
            case "INSERT":
                return 2;
            case "DELETE":
                return 3;
            case "UPDATE":
                return 4;
            default:
                System.out.println("In DMLParser.setGraphParserToUse()");
                System.out.println("Invalid token: " + firstToken);
                return -1;
        }
    }

    public void printAllMatrices() {

        for(RuleGraph ruleGraph : ruleGraphs) {
            System.out.println("Graph Parser: " + ruleGraph.getRule(0));
            ruleGraph.printRuleSet();
        }
    }*/
}
