package systemcatalog.parser;

// TODO remove
public class PrivilegeParser {

    /*private ArrayList<RuleGraph> ruleGraphs;
    private String privilegeStatement;

    public PrivilegeParser() {

        ruleGraphs = new ArrayList<>();

        RuleGraph grantRuleGraph = new RuleGraph();
        RuleGraph revokeRuleGraph = new RuleGraph();

        // adding each rule to its associated graph systemcatalog.parser
        grantRuleGraph.addRule("GRANT",       false, 0);
        grantRuleGraph.addRule("ALTER",       false, 1);
        grantRuleGraph.addRule("DELETE",      false, 2);
        grantRuleGraph.addRule("INDEX",       false, 3);
        grantRuleGraph.addRule("INSERT",      false, 4);
        grantRuleGraph.addRule("SELECT",      false, 5);
        grantRuleGraph.addRule("UPDATE",      false, 6);
        grantRuleGraph.addRule("REFERENCES",  false, 7);
        grantRuleGraph.addRule(",",           false, 8);
        grantRuleGraph.addRule("ON",          false, 9);
        grantRuleGraph.addRule("TableName",   true, 10);
        grantRuleGraph.addRule("TO",          false, 11);
        grantRuleGraph.addRule("UserName",    true, 12);
        grantRuleGraph.addRule(",",           false, 13);
        grantRuleGraph.addRule(";",           false, 14);
        grantRuleGraph.addRule("(",           false, 15);
        grantRuleGraph.addRule("ColumnName",  true, 16);
        grantRuleGraph.addRule(",",           false, 17);
        grantRuleGraph.addRule(")",           false, 18);
        grantRuleGraph.addRule("(",           false, 19);
        grantRuleGraph.addRule("ColumnName",  true, 20);
        grantRuleGraph.addRule(",",           false, 21);
        grantRuleGraph.addRule(")",           false, 22);

        revokeRuleGraph.addRule("REVOKE",     false, 0);
        revokeRuleGraph.addRule("ALTER",      false, 1);
        revokeRuleGraph.addRule("DELETE",     false, 2);
        revokeRuleGraph.addRule("INDEX",      false, 3);
        revokeRuleGraph.addRule("INSERT",     false, 4);
        revokeRuleGraph.addRule("SELECT",     false, 5);
        revokeRuleGraph.addRule("UPDATE",     false, 6);
        revokeRuleGraph.addRule("REFERENCES", false, 7);
        revokeRuleGraph.addRule(",",          false, 8);
        revokeRuleGraph.addRule("ON",         false, 9);
        revokeRuleGraph.addRule("TableName",  true, 10);
        revokeRuleGraph.addRule("FROM",       false, 11);
        revokeRuleGraph.addRule("UserName",   true, 12);
        revokeRuleGraph.addRule(",",          false, 13);
        revokeRuleGraph.addRule(";",          false, 14);
        revokeRuleGraph.addRule("(",          false, 15);
        revokeRuleGraph.addRule("ColumnName", true, 16);
        revokeRuleGraph.addRule(",",          false, 17);
        revokeRuleGraph.addRule(")",          false, 18);
        revokeRuleGraph.addRule("(",          false, 19);
        revokeRuleGraph.addRule("ColumnName", true, 20);
        revokeRuleGraph.addRule(",",          false, 21);
        revokeRuleGraph.addRule(")",          false, 22);

        // setting the valid rule transitions for each rule in each graph systemcatalog.parser
        grantRuleGraph.setChildren(0, 1, 2, 3, 4, 5, 6, 7);
        grantRuleGraph.setChildren(1, 8, 9);
        grantRuleGraph.setChildren(2, 8, 9);
        grantRuleGraph.setChildren(3, 8, 9);
        grantRuleGraph.setChildren(4, 8, 9);
        grantRuleGraph.setChildren(5, 8, 9);
        grantRuleGraph.setChildren(6, 15);
        grantRuleGraph.setChildren(7, 19);
        grantRuleGraph.setChildren(8, 1, 2, 3, 4, 5, 6, 7);
        grantRuleGraph.setChildren(9, 10);
        grantRuleGraph.setChildren(10, 11);
        grantRuleGraph.setChildren(11, 12);
        grantRuleGraph.setChildren(12, 13, 14);
        grantRuleGraph.setChildren(13, 12);
        grantRuleGraph.setChildren(14);
        grantRuleGraph.setChildren(15, 16);
        grantRuleGraph.setChildren(16, 17, 18);
        grantRuleGraph.setChildren(17, 16);
        grantRuleGraph.setChildren(18, 8, 9);
        grantRuleGraph.setChildren(19, 20);
        grantRuleGraph.setChildren(20, 21, 22);
        grantRuleGraph.setChildren(21, 20);
        grantRuleGraph.setChildren(22, 8, 9);

        revokeRuleGraph.setChildren(0, 1, 2, 3, 4, 5, 6, 7);
        revokeRuleGraph.setChildren(1, 8, 9);
        revokeRuleGraph.setChildren(2, 8, 9);
        revokeRuleGraph.setChildren(3, 8, 9);
        revokeRuleGraph.setChildren(4, 8, 9);
        revokeRuleGraph.setChildren(5, 8, 9);
        revokeRuleGraph.setChildren(6, 15);
        revokeRuleGraph.setChildren(7, 19);
        revokeRuleGraph.setChildren(8, 1, 2, 3, 4, 5, 6, 7);
        revokeRuleGraph.setChildren(9, 10);
        revokeRuleGraph.setChildren(10, 11);
        revokeRuleGraph.setChildren(11, 12);
        revokeRuleGraph.setChildren(12, 13, 14);
        revokeRuleGraph.setChildren(13, 12);
        revokeRuleGraph.setChildren(14);
        revokeRuleGraph.setChildren(15, 16);
        revokeRuleGraph.setChildren(16, 17, 18);
        revokeRuleGraph.setChildren(17, 16);
        revokeRuleGraph.setChildren(18, 8, 9);
        revokeRuleGraph.setChildren(19, 20);
        revokeRuleGraph.setChildren(20, 21, 22);
        revokeRuleGraph.setChildren(21, 20);
        revokeRuleGraph.setChildren(22, 8, 9);

        // finally add all the junk to our list
        ruleGraphs.add(grantRuleGraph);
        ruleGraphs.add(revokeRuleGraph);
    }

    public void setPrivilegeStatement(String privilegeStatement) {
        this.privilegeStatement = privilegeStatement;
    }

    public String getPrivilegeStatement() {
        return privilegeStatement;
    }

    public boolean isValid() {

        int graphParserToUse = getGraphParserToUse();
        boolean foundValidGraphParser = graphParserToUse != -1;

        if(! foundValidGraphParser) {
            return false;
        }

        boolean isSyntacticallyCorrect =
                ruleGraphs.get(graphParserToUse).isSyntacticallyCorrect(privilegeStatement);

        if(! isSyntacticallyCorrect) {
            return false;
        }

        // Eg. prevent stuff like GRANT ALTER, ALTER ON table1 TO user1
        boolean hasDuplicateAlter     = ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 1);
        boolean hasDuplicateDelete    = ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 1);
        boolean hasDuplicateIndex     = ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 1);
        boolean hasDuplicateInsert    = ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 1);
        boolean hasDuplicateSelect    = ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 1);
        boolean hasDuplicateUpdate    = ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 1);
        boolean hasDuplicateReference = ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 1);

        boolean hasDuplicateUsernames =
                ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 12);
        boolean hasDuplicateUpdateColumns =
                ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 16);
        boolean hasDuplicateReferenceColumns =
                ruleGraphs.get(graphParserToUse).hasDuplicatesAt(privilegeStatement, 20);

        boolean hasIllegalKeyword = ruleGraphs.get(graphParserToUse).hasIllegalKeyword(privilegeStatement);

        return ! hasDuplicateAlter && ! hasDuplicateDelete && ! hasDuplicateIndex && ! hasDuplicateInsert &&
                ! hasDuplicateSelect && ! hasDuplicateUpdate && ! hasDuplicateReference &&
                ! hasDuplicateUsernames && ! hasDuplicateUpdateColumns && ! hasDuplicateReferenceColumns &&
                ! hasIllegalKeyword;
    }

    public int getGraphParserToUse() {

        String firstToken = privilegeStatement.split("\\s+")[0];

        switch(firstToken) {
            case "GRANT":
                return 0;
            case "REVOKE":
                return 1;
            default:
                System.out.println("In PrivilegeParser.getGraphParserToUse()");
                System.out.println("Invalid token: " + firstToken);
                return -1;
        }
    }

    public void printMatrices() {

        for(RuleGraph ruleGraph : ruleGraphs) {
            System.out.println("Graph Parser: " + ruleGraph.getRule(0));
            ruleGraph.printRuleSet();
        }
    }*/
}
