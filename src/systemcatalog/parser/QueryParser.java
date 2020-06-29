package systemcatalog.parser;

// TODO remove
public class QueryParser {

    /*RuleGraph ruleGraph;
    String query;

    public QueryParser() {

        ruleGraph = new RuleGraph();

        // add the rules to the systemcatalog.parser
        ruleGraph.addRule("SELECT",    false, 0);
        ruleGraph.addRule("*",         false, 1);
        ruleGraph.addRule("ColumnName",true,  2);
        ruleGraph.addRule(",",         false, 3);
        ruleGraph.addRule("MIN",       false, 4);
        ruleGraph.addRule("MAX",       false, 5);
        ruleGraph.addRule("AVG",       false, 6);
        ruleGraph.addRule("COUNT",     false, 7);
        ruleGraph.addRule("SUM",       false, 8);
        ruleGraph.addRule("(",         false, 9);
        ruleGraph.addRule("ColumnName",true,  10);
        ruleGraph.addRule(")",         false, 11);
        ruleGraph.addRule("FROM",      false, 12);
        ruleGraph.addRule("TableName", true,  13);
        ruleGraph.addRule(",",         false, 14);
        ruleGraph.addRule("TableName", true,  15);
        ruleGraph.addRule("JOIN",      false, 16);
        ruleGraph.addRule("TableName", true,  17);
        ruleGraph.addRule("USING",     false, 18);
        ruleGraph.addRule("(",         false, 19);
        ruleGraph.addRule("ColumnName",true,  20);
        ruleGraph.addRule(")",         false, 21);
        ruleGraph.addRule(";",         false, 22);
        ruleGraph.addRule("WHERE",     false, 23);
        ruleGraph.addRule("ColumnName",true,  24);
        ruleGraph.addRule("=",         false, 25);
        ruleGraph.addRule("!=",        false, 26);
        ruleGraph.addRule(">",         false, 27);
        ruleGraph.addRule("<",         false, 28);
        ruleGraph.addRule(">=",        false, 29);
        ruleGraph.addRule("<=",        false, 30);
        ruleGraph.addRule("Constant",  true,  31);
        ruleGraph.addRule("AND",       false, 32);
        ruleGraph.addRule("OR",        false, 33);
        ruleGraph.addRule(";",         false, 34);

        // construct an adjacency list for each node
        ruleGraph.setChildren(0, 1, 2, 4, 5, 6, 7, 8);
        ruleGraph.setChildren(1, 12);
        ruleGraph.setChildren(2, 3, 12);
        ruleGraph.setChildren(3, 2);
        ruleGraph.setChildren(4, 9);
        ruleGraph.setChildren(5, 9);
        ruleGraph.setChildren(6, 9);
        ruleGraph.setChildren(7, 9);
        ruleGraph.setChildren(8, 9);
        ruleGraph.setChildren(9, 10);
        ruleGraph.setChildren(10, 11);
        ruleGraph.setChildren(11, 12);
        ruleGraph.setChildren(12, 13);
        ruleGraph.setChildren(13, 14, 16, 22, 23);
        ruleGraph.setChildren(14, 15);
        ruleGraph.setChildren(15, 14, 22, 23);
        ruleGraph.setChildren(16, 17);
        ruleGraph.setChildren(17, 18);
        ruleGraph.setChildren(18, 19);
        ruleGraph.setChildren(19, 20);
        ruleGraph.setChildren(20, 21);
        ruleGraph.setChildren(21, 16, 22, 23);
        ruleGraph.setChildren(22);
        ruleGraph.setChildren(23, 24);
        ruleGraph.setChildren(24, 25, 26, 27, 28, 29, 30);
        ruleGraph.setChildren(25, 31);
        ruleGraph.setChildren(26, 31);
        ruleGraph.setChildren(27, 31);
        ruleGraph.setChildren(28, 31);
        ruleGraph.setChildren(29, 31);
        ruleGraph.setChildren(30, 31);
        ruleGraph.setChildren(31, 32, 33, 34);
        ruleGraph.setChildren(32, 24);
        ruleGraph.setChildren(33, 24);
        ruleGraph.setChildren(34);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public boolean isValid() {

        boolean isSyntacticallyCorrect = ruleGraph.isSyntacticallyCorrect(query);

        // following methods need a syntactically correct query, that's why early return
        if(! isSyntacticallyCorrect) {
            return false;
        }

        // don't accept duplicate values where they are not supposed to be
        boolean hasDuplicateColumnNamesInSelectClause = ruleGraph.hasDuplicatesAt(query, 2);
        boolean hasDuplicateTableNamesInFromClause    = ruleGraph.hasDuplicatesAt(query, 13, 15);
        boolean hasDuplicateTableNamesInJoinClause    = ruleGraph.hasDuplicatesAt(query, 13, 17);

        // don't accept keyword values in the wrong places
        boolean hasIllegalKeyword = ruleGraph.hasIllegalKeyword(query);

        // enforce correct data types (>, <, >=, <= must be associated with a number data type)
        boolean hasIllegalNumeric = ruleGraph.hasIllegalNumericAt(query,31, 27, 28, 29, 30);

        return ! hasDuplicateColumnNamesInSelectClause && ! hasDuplicateTableNamesInFromClause
                && ! hasDuplicateTableNamesInJoinClause && ! hasIllegalKeyword && ! hasIllegalNumeric;
    }

    public void printMatrix() {
        ruleGraph.printRuleSet();
    }*/
}
