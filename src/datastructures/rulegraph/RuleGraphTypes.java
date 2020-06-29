package datastructures.rulegraph;

/**
 * The only purpose of this class is to return RuleGraphs of a particular type.
 * This class just exists to keep code cleaner. Also code is written with
 * readability as a priority, so it's easy to see what the heck is happening.
 */
public class RuleGraphTypes {

    public RuleGraphTypes() {}

    public RuleGraph getQueryRuleGraph() {

        RuleGraph queryRuleGraph = new RuleGraph();

        queryRuleGraph.addRule("SELECT",    false, 0);
        queryRuleGraph.addRule("*",         false, 1);
        queryRuleGraph.addRule("ColumnName",true,  2);
        queryRuleGraph.addRule(",",         false, 3);
        queryRuleGraph.addRule("MIN",       false, 4);
        queryRuleGraph.addRule("MAX",       false, 5);
        queryRuleGraph.addRule("AVG",       false, 6);
        queryRuleGraph.addRule("COUNT",     false, 7);
        queryRuleGraph.addRule("SUM",       false, 8);
        queryRuleGraph.addRule("(",         false, 9);
        queryRuleGraph.addRule("ColumnName",true,  10);
        queryRuleGraph.addRule(")",         false, 11);
        queryRuleGraph.addRule("FROM",      false, 12);
        queryRuleGraph.addRule("TableName", true,  13);
        queryRuleGraph.addRule(",",         false, 14);
        queryRuleGraph.addRule("TableName", true,  15);
        queryRuleGraph.addRule("JOIN",      false, 16);
        queryRuleGraph.addRule("TableName", true,  17);
        queryRuleGraph.addRule("USING",     false, 18);
        queryRuleGraph.addRule("(",         false, 19);
        queryRuleGraph.addRule("ColumnName",true,  20);
        queryRuleGraph.addRule(")",         false, 21);
        queryRuleGraph.addRule(";",         false, 22);
        queryRuleGraph.addRule("WHERE",     false, 23);
        queryRuleGraph.addRule("ColumnName",true,  24);
        queryRuleGraph.addRule("=",         false, 25);
        queryRuleGraph.addRule("!=",        false, 26);
        queryRuleGraph.addRule(">",         false, 27);
        queryRuleGraph.addRule("<",         false, 28);
        queryRuleGraph.addRule(">=",        false, 29);
        queryRuleGraph.addRule("<=",        false, 30);
        queryRuleGraph.addRule("Constant",  true,  31);
        queryRuleGraph.addRule("AND",       false, 32);
        queryRuleGraph.addRule("OR",        false, 33);
        queryRuleGraph.addRule(";",         false, 34);

        queryRuleGraph.setChildren(0,  1, 2, 4, 5, 6, 7, 8);
        queryRuleGraph.setChildren(1,  12);
        queryRuleGraph.setChildren(2,  3, 12);
        queryRuleGraph.setChildren(3,  2);
        queryRuleGraph.setChildren(4,  9);
        queryRuleGraph.setChildren(5,  9);
        queryRuleGraph.setChildren(6,  9);
        queryRuleGraph.setChildren(7,  9);
        queryRuleGraph.setChildren(8,  9);
        queryRuleGraph.setChildren(9,  10);
        queryRuleGraph.setChildren(10, 11);
        queryRuleGraph.setChildren(11, 12);
        queryRuleGraph.setChildren(12, 13);
        queryRuleGraph.setChildren(13, 14, 16, 22, 23);
        queryRuleGraph.setChildren(14, 15);
        queryRuleGraph.setChildren(15, 14, 22, 23);
        queryRuleGraph.setChildren(16, 17);
        queryRuleGraph.setChildren(17, 18);
        queryRuleGraph.setChildren(18, 19);
        queryRuleGraph.setChildren(19, 20);
        queryRuleGraph.setChildren(20, 21);
        queryRuleGraph.setChildren(21, 16, 22, 23);
        queryRuleGraph.setChildren(22);
        queryRuleGraph.setChildren(23, 24);
        queryRuleGraph.setChildren(24, 25, 26, 27, 28, 29, 30);
        queryRuleGraph.setChildren(25, 31);
        queryRuleGraph.setChildren(26, 31);
        queryRuleGraph.setChildren(27, 31);
        queryRuleGraph.setChildren(28, 31);
        queryRuleGraph.setChildren(29, 31);
        queryRuleGraph.setChildren(30, 31);
        queryRuleGraph.setChildren(31, 32, 33, 34);
        queryRuleGraph.setChildren(32, 24);
        queryRuleGraph.setChildren(33, 24);
        queryRuleGraph.setChildren(34);

        return queryRuleGraph;
    }

    public RuleGraph getCreateTableRuleGraph() {

        RuleGraph createTableRuleGraph = new RuleGraph();

        createTableRuleGraph.addRule("CREATE",     false, 0);
        createTableRuleGraph.addRule("TABLE",      false, 1);
        createTableRuleGraph.addRule("TableName",  true,  2);
        createTableRuleGraph.addRule("(",          false, 3);
        createTableRuleGraph.addRule("ColumnName", true,  4);
        createTableRuleGraph.addRule("NUMBER",     false, 5);
        createTableRuleGraph.addRule("CHAR",       false, 6);
        createTableRuleGraph.addRule("(",          false, 7);
        createTableRuleGraph.addRule("Size",       true,  8);
        createTableRuleGraph.addRule(")",          false, 9);
        createTableRuleGraph.addRule(",",          false, 10);
        createTableRuleGraph.addRule(")",          false, 11);
        createTableRuleGraph.addRule(";",          false, 12);

        createTableRuleGraph.setChildren(0,  1);
        createTableRuleGraph.setChildren(1,  2);
        createTableRuleGraph.setChildren(2,  3);
        createTableRuleGraph.setChildren(3,  4);
        createTableRuleGraph.setChildren(4,  5, 6);
        createTableRuleGraph.setChildren(5,  7);
        createTableRuleGraph.setChildren(6,  7);
        createTableRuleGraph.setChildren(7,  8);
        createTableRuleGraph.setChildren(8,  9);
        createTableRuleGraph.setChildren(9,  10, 11);
        createTableRuleGraph.setChildren(10, 4);
        createTableRuleGraph.setChildren(11, 12);
        createTableRuleGraph.setChildren(12);

        return createTableRuleGraph;
    }

    public RuleGraph getAlterTableRuleGraph() {

        RuleGraph alterTableRuleGraph = new RuleGraph();

        alterTableRuleGraph.addRule("ALTER",      false, 0);
        alterTableRuleGraph.addRule("TABLE",      false, 1);
        alterTableRuleGraph.addRule("TableName",  true,  2);
        alterTableRuleGraph.addRule("ADD",        false, 3);
        alterTableRuleGraph.addRule("MODIFY",     false, 4);
        alterTableRuleGraph.addRule("DROP",       false, 5);
        alterTableRuleGraph.addRule("ColumnName", true,  6);
        alterTableRuleGraph.addRule("NUMBER",     false, 7);
        alterTableRuleGraph.addRule("CHAR",       false, 8);
        alterTableRuleGraph.addRule("(",          false, 9);
        alterTableRuleGraph.addRule("Size",       true,  10);
        alterTableRuleGraph.addRule(")",          false, 11);
        alterTableRuleGraph.addRule("ColumnName", true,  12);
        alterTableRuleGraph.addRule(";",          false, 13);

        return alterTableRuleGraph;
    }

    public RuleGraph getDropTableRuleGraph() {

        RuleGraph dropTableRuleGraph = new RuleGraph();

        dropTableRuleGraph.addRule("DROP",      false, 0);
        dropTableRuleGraph.addRule("TABLE",     false, 1);
        dropTableRuleGraph.addRule("TableName", true,  2);
        dropTableRuleGraph.addRule(";",         false, 3);

        dropTableRuleGraph.setChildren(0, 1);
        dropTableRuleGraph.setChildren(1, 2);
        dropTableRuleGraph.setChildren(2, 3);
        dropTableRuleGraph.setChildren(3);

        return dropTableRuleGraph;
    }

    public RuleGraph getInsertRuleGraph() {

        RuleGraph insertRuleGraph = new RuleGraph();

        insertRuleGraph.addRule("INSERT",    false, 0);
        insertRuleGraph.addRule("INTO",      false, 1);
        insertRuleGraph.addRule("TableName", true,  2);
        insertRuleGraph.addRule("VALUES",    false, 3);
        insertRuleGraph.addRule("(",         false, 4);
        insertRuleGraph.addRule("Value",     true,  5);
        insertRuleGraph.addRule(",",         false, 6);
        insertRuleGraph.addRule(")",         false, 7);
        insertRuleGraph.addRule(";",         false, 8);

        insertRuleGraph.setChildren(0, 1);
        insertRuleGraph.setChildren(1, 2);
        insertRuleGraph.setChildren(2, 3);
        insertRuleGraph.setChildren(3, 4);
        insertRuleGraph.setChildren(4, 5);
        insertRuleGraph.setChildren(5, 6, 7);
        insertRuleGraph.setChildren(6, 5);
        insertRuleGraph.setChildren(7, 8);
        insertRuleGraph.setChildren(8);

        return insertRuleGraph;
    }

    public RuleGraph getDeleteRuleGraph() {

        RuleGraph deleteRuleGraph = new RuleGraph();

        deleteRuleGraph.addRule("DELETE",     false, 0);
        deleteRuleGraph.addRule("FROM",       false, 1);
        deleteRuleGraph.addRule("TableName",  true,  2);
        deleteRuleGraph.addRule("WHERE",      false, 3);
        deleteRuleGraph.addRule("ColumnName", true,  4);
        deleteRuleGraph.addRule("=",          false, 5);
        deleteRuleGraph.addRule("!=",         false, 6);
        deleteRuleGraph.addRule(">",          false, 7);
        deleteRuleGraph.addRule("<",          false, 8);
        deleteRuleGraph.addRule(">=",         false, 9);
        deleteRuleGraph.addRule("<=",         false, 10);
        deleteRuleGraph.addRule("Constant",   true,  11);
        deleteRuleGraph.addRule(";",          false, 12);

        deleteRuleGraph.setChildren(0,  1);
        deleteRuleGraph.setChildren(1,  2);
        deleteRuleGraph.setChildren(2,  3);
        deleteRuleGraph.setChildren(3,  4);
        deleteRuleGraph.setChildren(4,  5, 6, 7, 8, 9, 10);
        deleteRuleGraph.setChildren(5,  11);
        deleteRuleGraph.setChildren(6,  11);
        deleteRuleGraph.setChildren(7,  11);
        deleteRuleGraph.setChildren(8,  11);
        deleteRuleGraph.setChildren(9,  11);
        deleteRuleGraph.setChildren(10, 11);
        deleteRuleGraph.setChildren(11, 12);
        deleteRuleGraph.setChildren(12);

        return deleteRuleGraph;
    }

    public RuleGraph getUpdateRuleGraph() {

        RuleGraph updateRuleGraph = new RuleGraph();

        updateRuleGraph.addRule("UPDATE",     false, 0);
        updateRuleGraph.addRule("TableName",  true,  1);
        updateRuleGraph.addRule("SET",        false, 2);
        updateRuleGraph.addRule("ColumnName", true,  3);
        updateRuleGraph.addRule("=",          false, 4);
        updateRuleGraph.addRule("Constant",   true,  5);
        updateRuleGraph.addRule("WHERE",      false, 6);
        updateRuleGraph.addRule("ColumnName", true,  7);
        updateRuleGraph.addRule("=",          false, 8);
        updateRuleGraph.addRule("Constant",   true,  9);
        updateRuleGraph.addRule(";",          false, 10);

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

        return updateRuleGraph;
    }

    public RuleGraph getGrantRuleGraph() {

        RuleGraph grantRuleGraph = new RuleGraph();

        grantRuleGraph.addRule("GRANT",      false, 0);
        grantRuleGraph.addRule("ALTER",      false, 1);
        grantRuleGraph.addRule("DELETE",     false, 2);
        grantRuleGraph.addRule("INDEX",      false, 3);
        grantRuleGraph.addRule("INSERT",     false, 4);
        grantRuleGraph.addRule("SELECT",     false, 5);
        grantRuleGraph.addRule("UPDATE",     false, 6);
        grantRuleGraph.addRule("REFERENCES", false, 7);
        grantRuleGraph.addRule("ALL",        false, 8);
        grantRuleGraph.addRule("PRIVILEGES", false, 9);
        grantRuleGraph.addRule(",",          false, 10);
        grantRuleGraph.addRule("(",          false, 11);
        grantRuleGraph.addRule("ColumnName", true,  12);
        grantRuleGraph.addRule(",",          false, 13);
        grantRuleGraph.addRule(")",          false, 14);
        grantRuleGraph.addRule("(",          false, 15);
        grantRuleGraph.addRule("ColumnName", true,  16);
        grantRuleGraph.addRule(",",          false, 17);
        grantRuleGraph.addRule(")",          false, 18);
        grantRuleGraph.addRule("ON",         false, 19);
        grantRuleGraph.addRule("TableName",  true,  20);
        grantRuleGraph.addRule("TO",         false, 21);
        grantRuleGraph.addRule("UserName",   true,  22);
        grantRuleGraph.addRule(",",          false, 23);
        grantRuleGraph.addRule("WITH",       false, 24);
        grantRuleGraph.addRule("GRANT",      false, 25);
        grantRuleGraph.addRule("OPTION",     false, 26);
        grantRuleGraph.addRule(";",          false, 27);

        grantRuleGraph.setChildren(0,  1, 2, 3, 4, 5, 6, 7, 8);
        grantRuleGraph.setChildren(1,  10, 19);
        grantRuleGraph.setChildren(2,  10, 19);
        grantRuleGraph.setChildren(3,  10, 19);
        grantRuleGraph.setChildren(4,  10, 19);
        grantRuleGraph.setChildren(5,  10, 19);
        grantRuleGraph.setChildren(6,  11);
        grantRuleGraph.setChildren(7,  15);
        grantRuleGraph.setChildren(8,  9);
        grantRuleGraph.setChildren(9,  19);
        grantRuleGraph.setChildren(10, 1, 2, 3, 4, 5, 6, 7);
        grantRuleGraph.setChildren(11, 12);
        grantRuleGraph.setChildren(12, 13, 14);
        grantRuleGraph.setChildren(13, 12);
        grantRuleGraph.setChildren(14, 10, 19);
        grantRuleGraph.setChildren(15, 16);
        grantRuleGraph.setChildren(16, 17, 18);
        grantRuleGraph.setChildren(17, 16);
        grantRuleGraph.setChildren(18, 10, 19);
        grantRuleGraph.setChildren(19, 20);
        grantRuleGraph.setChildren(20, 21);
        grantRuleGraph.setChildren(21, 22);
        grantRuleGraph.setChildren(22, 23, 24, 27);
        grantRuleGraph.setChildren(23, 22);
        grantRuleGraph.setChildren(24, 25);
        grantRuleGraph.setChildren(25, 26);
        grantRuleGraph.setChildren(26, 27);

        return grantRuleGraph;
    }

    public RuleGraph getRevokeRuleGraph() {

        RuleGraph revokeRuleGraph = new RuleGraph();

        revokeRuleGraph.addRule("REVOKE",     false, 0);
        revokeRuleGraph.addRule("ALTER",      false, 1);
        revokeRuleGraph.addRule("DELETE",     false, 2);
        revokeRuleGraph.addRule("INDEX",      false, 3);
        revokeRuleGraph.addRule("INSERT",     false, 4);
        revokeRuleGraph.addRule("SELECT",     false, 5);
        revokeRuleGraph.addRule("UPDATE",     false, 6);
        revokeRuleGraph.addRule("REFERENCES", false, 7);
        revokeRuleGraph.addRule("ALL",        false, 8);
        revokeRuleGraph.addRule("PRIVILEGES", false, 9);
        revokeRuleGraph.addRule(",",          false, 10);
        revokeRuleGraph.addRule("(",          false, 11);
        revokeRuleGraph.addRule("ColumnName", true,  12);
        revokeRuleGraph.addRule(",",          false, 13);
        revokeRuleGraph.addRule(")",          false, 14);
        revokeRuleGraph.addRule("(",          false, 15);
        revokeRuleGraph.addRule("ColumnName", true,  16);
        revokeRuleGraph.addRule(",",          false, 17);
        revokeRuleGraph.addRule(")",          false, 18);
        revokeRuleGraph.addRule("ON",         false, 19);
        revokeRuleGraph.addRule("TableName",  true,  20);
        revokeRuleGraph.addRule("FROM",       false, 21);
        revokeRuleGraph.addRule("UserName",   true,  22);
        revokeRuleGraph.addRule(",",          false, 23);
        revokeRuleGraph.addRule(";",          false, 24);

        revokeRuleGraph.setChildren(0,  1, 2, 3, 4, 5, 6, 7, 8);
        revokeRuleGraph.setChildren(1,  10, 19);
        revokeRuleGraph.setChildren(2,  10, 19);
        revokeRuleGraph.setChildren(3,  10, 19);
        revokeRuleGraph.setChildren(4,  10, 19);
        revokeRuleGraph.setChildren(5,  10, 19);
        revokeRuleGraph.setChildren(6,  11);
        revokeRuleGraph.setChildren(7,  15);
        revokeRuleGraph.setChildren(8,  9);
        revokeRuleGraph.setChildren(9,  19);
        revokeRuleGraph.setChildren(10, 1, 2, 3, 4, 5, 6, 7);
        revokeRuleGraph.setChildren(11, 12);
        revokeRuleGraph.setChildren(12, 13, 14);
        revokeRuleGraph.setChildren(13, 12);
        revokeRuleGraph.setChildren(14, 10, 19);
        revokeRuleGraph.setChildren(15, 16);
        revokeRuleGraph.setChildren(16, 17, 18);
        revokeRuleGraph.setChildren(17, 16);
        revokeRuleGraph.setChildren(18, 10, 19);
        revokeRuleGraph.setChildren(19, 20);
        revokeRuleGraph.setChildren(20, 21);
        revokeRuleGraph.setChildren(21, 22);
        revokeRuleGraph.setChildren(22, 23, 24);
        revokeRuleGraph.setChildren(23, 22);

        return revokeRuleGraph;
    }

    public RuleGraph getSecondaryBTreeRuleGraph() {

        RuleGraph secondaryBTreeRuleGraph = new RuleGraph();

        secondaryBTreeRuleGraph.addRule("BUILD",      false, 0);
        secondaryBTreeRuleGraph.addRule("SECONDARY",  false, 1);
        secondaryBTreeRuleGraph.addRule("B-TREE",     false, 2);
        secondaryBTreeRuleGraph.addRule("ON",         false, 3);
        secondaryBTreeRuleGraph.addRule("ColumnName", true,  4);
        secondaryBTreeRuleGraph.addRule("IN",         false, 5);
        secondaryBTreeRuleGraph.addRule("TableName",  true,  6);
        secondaryBTreeRuleGraph.addRule(";",          false, 7);

        secondaryBTreeRuleGraph.setChildren(0, 1);
        secondaryBTreeRuleGraph.setChildren(1, 2);
        secondaryBTreeRuleGraph.setChildren(2, 3);
        secondaryBTreeRuleGraph.setChildren(3, 4);
        secondaryBTreeRuleGraph.setChildren(4, 5);
        secondaryBTreeRuleGraph.setChildren(5, 6);
        secondaryBTreeRuleGraph.setChildren(6, 7);

        return secondaryBTreeRuleGraph;
    }

    public RuleGraph getClusteredBTreeRuleGraph() {

        RuleGraph clusteredBTreeRuleGraph = new RuleGraph();

        clusteredBTreeRuleGraph.addRule("BUILD",      false, 0);
        clusteredBTreeRuleGraph.addRule("CLUSTERED",  false, 1);
        clusteredBTreeRuleGraph.addRule("B-TREE",     false, 2);
        clusteredBTreeRuleGraph.addRule("ON",         false, 3);
        clusteredBTreeRuleGraph.addRule("ColumnName", true,  4);
        clusteredBTreeRuleGraph.addRule("IN",         false, 5);
        clusteredBTreeRuleGraph.addRule("TableName",  true,  6);
        clusteredBTreeRuleGraph.addRule(";",          false, 7);

        clusteredBTreeRuleGraph.setChildren(0, 1);
        clusteredBTreeRuleGraph.setChildren(1, 2);
        clusteredBTreeRuleGraph.setChildren(2, 3);
        clusteredBTreeRuleGraph.setChildren(3, 4);
        clusteredBTreeRuleGraph.setChildren(4, 5);
        clusteredBTreeRuleGraph.setChildren(5, 6);
        clusteredBTreeRuleGraph.setChildren(6, 7);

        return clusteredBTreeRuleGraph;
    }

    public RuleGraph getHashTableRuleGraph() {

        RuleGraph hashTableRuleGraph = new RuleGraph();

        hashTableRuleGraph.addRule("BUILD",      false, 0);
        hashTableRuleGraph.addRule("HASH",       false, 1);
        hashTableRuleGraph.addRule("TABLE",      false, 2);
        hashTableRuleGraph.addRule("ON",         false, 3);
        hashTableRuleGraph.addRule("ColumnName", true,  4);
        hashTableRuleGraph.addRule("IN",         false, 5);
        hashTableRuleGraph.addRule("TableName",  true,  6);
        hashTableRuleGraph.addRule(";",          false, 7);

        hashTableRuleGraph.setChildren(0, 1);
        hashTableRuleGraph.setChildren(1, 2);
        hashTableRuleGraph.setChildren(2, 3);
        hashTableRuleGraph.setChildren(3, 4);
        hashTableRuleGraph.setChildren(4, 5);
        hashTableRuleGraph.setChildren(5, 6);
        hashTableRuleGraph.setChildren(6, 7);

        return hashTableRuleGraph;
    }

    public RuleGraph getClusteredFileRuleGraph() {

        RuleGraph clusteredFileRuleGraph = new RuleGraph();

        clusteredFileRuleGraph.addRule("BUILD",     false, 0);
        clusteredFileRuleGraph.addRule("CLUSTERED", false, 1);
        clusteredFileRuleGraph.addRule("FILE",      false, 2);
        clusteredFileRuleGraph.addRule("ON",        false, 3);
        clusteredFileRuleGraph.addRule("TableName", true,  4);
        clusteredFileRuleGraph.addRule("AND",       false, 5);
        clusteredFileRuleGraph.addRule("TableName", true,  6);
        clusteredFileRuleGraph.addRule(";",         false, 7);

        clusteredFileRuleGraph.setChildren(0, 1);
        clusteredFileRuleGraph.setChildren(1, 2);
        clusteredFileRuleGraph.setChildren(2, 3);
        clusteredFileRuleGraph.setChildren(3, 4);
        clusteredFileRuleGraph.setChildren(4, 5);
        clusteredFileRuleGraph.setChildren(5, 6);
        clusteredFileRuleGraph.setChildren(6, 7);

        return clusteredFileRuleGraph;
    }
}
