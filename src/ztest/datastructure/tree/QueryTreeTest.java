package ztest.datastructure.tree;

import datastructure.relation.table.Table;
import datastructure.relation.table.component.Column;
import datastructure.relation.table.component.DataType;
import datastructure.tree.conditiontree.component.Condition;
import datastructure.tree.querytree.QueryTree;
import datastructure.tree.querytree.component.*;
import datastructure.tree.querytree.operator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilities.enums.Symbol;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTreeTest {

    @Test
    public QueryTree getQueryTree() {

        List<Column> columns = new ArrayList<>();
        Column a = new Column("A", DataType.NUMBER, 10);
        Column b = new Column("B", DataType.CHAR, 5);
        Column c = new Column("C", DataType.NUMBER, 5);
        columns.add(a);
        columns.add(b);
        columns.add(c);
        Operator projection = new Projection(columns);

        Operator cartesianProduct = new CartesianProduct();

        List<Condition> conditions1 = new ArrayList<>();
        conditions1.add(new Condition(a, Symbol.EQUAL, "5"));
        conditions1.add(new Condition(c, Symbol.NOT_EQUAL, "Blah"));
        Operator selection1 = new Selection(conditions1);

        Operator relation1 = new Relation(new Table("Table1"));

        List<Condition> conditions2 = new ArrayList<>();
        conditions2.add(new Condition(b, Symbol.GREATER_THAN, "7"));
        Operator selection2 = new Selection(conditions2);

        Operator relation2 = new Relation(new Table("Table2"));

        List<QueryTree.Traversal> traversals = new ArrayList<>();
        QueryTree queryTree = new QueryTree(projection);
        queryTree.add(traversals, QueryTree.Traversal.DOWN, cartesianProduct);
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.add(traversals, QueryTree.Traversal.LEFT, selection1);
        queryTree.add(traversals, QueryTree.Traversal.RIGHT, selection2);

        traversals.add(QueryTree.Traversal.LEFT);
        queryTree.add(traversals, QueryTree.Traversal.DOWN, relation1);
        traversals.remove(traversals.size() - 1);
        traversals.add(QueryTree.Traversal.RIGHT);
        queryTree.add(traversals, QueryTree.Traversal.DOWN, relation2);

        assertTrue(true);
        return queryTree;
    }

    @Test
    public void testForEach() {

        System.out.println("Test for each");
        QueryTree queryTree = getQueryTree();
        for(Operator o : queryTree) {
            System.out.println(o);
        }
        System.out.println("\n" + queryTree.getStructure());
        assertTrue(true);
    }

    @Test
    public void testAdd1() {

        System.out.println("Test inserts");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        Operator cartesianProduct = new CartesianProduct();
        traversals.add(QueryTree.Traversal.DOWN);
        traversals.add(QueryTree.Traversal.LEFT);
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.add(traversals, QueryTree.Traversal.LEFT, cartesianProduct);
        queryTree.add(traversals, QueryTree.Traversal.RIGHT, cartesianProduct.copy(cartesianProduct));

        System.out.println(queryTree.getStructure());
    }
}