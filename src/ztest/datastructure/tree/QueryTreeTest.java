package ztest.datastructure.tree;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.trees.conditiontree.component.Condition;
import datastructures.trees.querytree.QueryTree;
import datastructures.trees.querytree.operator.Operator;
import datastructures.trees.querytree.operator.types.*;
import org.junit.jupiter.api.Test;
import utilities.enums.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTreeTest {

    public QueryTree getQueryTree() {

        List<String> pl1 = new ArrayList<>(Arrays.asList("col1", "col2", "col3"));
        List<String> pl2 = new ArrayList<>(Arrays.asList("col1", "col2", "jcol1"));
        List<String> pl3 = new ArrayList<>(Arrays.asList("col3", "col5", "jcol1"));
        List<String> pl4 = new ArrayList<>(Arrays.asList("jcol2"));
        List<String> pl5 = new ArrayList<>(Arrays.asList("col3", "col5", "jcol3"));

        List<String> sl1 = new ArrayList<>(Arrays.asList("jcol1", "=", "jcol3"));
        List<String> sl2 = new ArrayList<>(Arrays.asList("jcol1", "=", "jcol2"));
        List<String> sl3 = new ArrayList<>(Arrays.asList("col2", "=", "3"));
        List<String> sl4 = new ArrayList<>(Arrays.asList("col1", "=", "5"));

        Operator p1 = new Projection(pl1);
        Operator p2 = new Projection(pl2);
        Operator p3 = new Projection(pl3);
        Operator p4 = new Projection(pl4);
        Operator p5 = new Projection(pl5);

        Operator s1 = new SimpleSelection(sl1.get(0), sl1.get(1), sl1.get(2));
        Operator s2 = new SimpleSelection(sl2.get(0), sl2.get(1), sl2.get(2));
        Operator s3 = new SimpleSelection(sl3.get(0), sl3.get(1), sl3.get(2));
        Operator s4 = new SimpleSelection(sl4.get(0), sl4.get(1), sl4.get(2));

        Operator c1 = new CartesianProduct();
        Operator c2 = new CartesianProduct();

        Operator r1 = new Relation("r1");
        Operator r2 = new Relation("r2");
        Operator r3 = new Relation("r3");

        QueryTree queryTree = new QueryTree(p1);
        queryTree.add(new ArrayList<>(), QueryTree.Traversal.DOWN, s1);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN)), QueryTree.Traversal.DOWN, c1);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN)), QueryTree.Traversal.LEFT, p2);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT)), QueryTree.Traversal.DOWN, s2);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT, QueryTree.Traversal.DOWN)), QueryTree.Traversal.DOWN, c2);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT, QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN)), QueryTree.Traversal.LEFT, p3);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT, QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT)), QueryTree.Traversal.DOWN, s3);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT, QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT, QueryTree.Traversal.DOWN)), QueryTree.Traversal.DOWN, r1);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT, QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN)), QueryTree.Traversal.RIGHT, p4);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT, QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.RIGHT)), QueryTree.Traversal.DOWN, r2);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN)), QueryTree.Traversal.RIGHT, p5);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.RIGHT)), QueryTree.Traversal.DOWN, s4);
        queryTree.add(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.RIGHT, QueryTree.Traversal.DOWN)), QueryTree.Traversal.DOWN, r3);

        return queryTree;
    }

    @Test
    public void testSwap() {

        System.out.println("testSwap()");

        QueryTree queryTree = getQueryTree();
        queryTree.getOperatorsAndLocations().forEach((key, value) -> System.out.println(key + " " + value));
        System.out.println("\nAfter:\n");
        queryTree.swap(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT)), new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.RIGHT, QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN)));
        queryTree.getOperatorsAndLocations().forEach((key, value) -> System.out.println(key + " " + value));
    }

    @Test
    public void testSubtreeRemoval() {

        System.out.println("testSubtreeRemoval()");

        QueryTree queryTree = getQueryTree();
        queryTree.getOperatorsAndLocations().forEach((key, value) -> System.out.println(key + " " + value));
        System.out.println("\nSize: " + queryTree.getSize() + "\n");
        queryTree.removeSubtree(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.LEFT)));
        assertEquals(6, queryTree.getSize());
        queryTree.getOperatorsAndLocations().forEach((key, value) -> System.out.println(key + " " + value));

        System.out.println();

        queryTree = getQueryTree();
        queryTree.removeSubtree(new ArrayList<>(Arrays.asList(QueryTree.Traversal.DOWN, QueryTree.Traversal.DOWN, QueryTree.Traversal.RIGHT)));
        assertEquals(11, queryTree.getSize());
        queryTree.getOperatorsAndLocations().forEach((key, value) -> System.out.println(key + " " + value));
    }

/*
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

        System.out.println("Test add1");
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

    @Test
    public void testAdd2() {

        System.out.println("Test add2");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        Operator cartesianProduct = new CartesianProduct();
        queryTree.add(traversals, QueryTree.Traversal.UP, cartesianProduct);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testAdd3() {

        System.out.println("Test add3");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        Operator cartesianProduct = new CartesianProduct();
        queryTree.add(traversals, QueryTree.Traversal.DOWN, cartesianProduct);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testAdd4() {

        System.out.println("Test add4");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);

        Operator cartesianProduct = new CartesianProduct();
        queryTree.add(traversals, QueryTree.Traversal.LEFT, cartesianProduct);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testAdd5() {

        System.out.println("Test add5");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);

        Operator cartesianProduct = new CartesianProduct();
        queryTree.add(traversals, QueryTree.Traversal.RIGHT, cartesianProduct);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testAdd6() {

        System.out.println("Test add6");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);

        Operator innerJoin = new InnerJoin(
                new Column("a", DataType.NUMBER, 1),
                new Column("b", DataType.NUMBER, 3)
        );
        queryTree.add(traversals, QueryTree.Traversal.UP, innerJoin);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testRemoveLeft() {

        System.out.println("Test Remove Left");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        traversals.add(QueryTree.Traversal.LEFT);
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.add(traversals, QueryTree.Traversal.LEFT, new CartesianProduct());
        queryTree.remove(traversals, QueryTree.Traversal.LEFT);

        System.out.println(queryTree.getStructure());

        queryTree = getQueryTree();
        traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.remove(traversals, QueryTree.Traversal.LEFT);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testRemoveRight() {

        System.out.println("Test Remove Right");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        traversals.add(QueryTree.Traversal.LEFT);
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.add(traversals, QueryTree.Traversal.RIGHT, new CartesianProduct());
        queryTree.remove(traversals, QueryTree.Traversal.RIGHT);

        System.out.println(queryTree.getStructure());

        queryTree = getQueryTree();
        traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.remove(traversals, QueryTree.Traversal.RIGHT);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testRemoveDown() {

        System.out.println("Test Remove Down");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        traversals.add(QueryTree.Traversal.LEFT);
        queryTree.remove(traversals, QueryTree.Traversal.DOWN);

        System.out.println(queryTree.getStructure());

        queryTree = getQueryTree();
        traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        traversals.add(QueryTree.Traversal.LEFT);
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.add(traversals, QueryTree.Traversal.DOWN, new CartesianProduct());
        queryTree.remove(traversals, QueryTree.Traversal.DOWN);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testRemoveUp() {

        System.out.println("Test Remove Up");
        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.remove(traversals, QueryTree.Traversal.UP);

        System.out.println(queryTree.getStructure());

        queryTree = getQueryTree();
        traversals = new ArrayList<>();
        queryTree.add(traversals, QueryTree.Traversal.UP, new CartesianProduct());
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.remove(traversals, QueryTree.Traversal.UP);

        System.out.println(queryTree.getStructure());
    }

    @Test
    public void testCopy() {

        System.out.println("In testCopy");

        QueryTree queryTree = getQueryTree();
        QueryTree copy = new QueryTree(queryTree);

        System.out.println(queryTree.getStructure());
        System.out.println(copy.getStructure());

        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.set(traversals, QueryTree.Traversal.UP, new CartesianProduct());

        System.out.println("After changes");
        System.out.println(queryTree.getStructure());
        System.out.println(copy.getStructure());
    }

    @Test
    public void testSet() {

        System.out.println("Test set");

        QueryTree queryTree = getQueryTree();
        List<QueryTree.Traversal> traversals = new ArrayList<>();
        traversals.add(QueryTree.Traversal.DOWN);
        queryTree.set(traversals, QueryTree.Traversal.UP, new CartesianProduct());

        System.out.println(queryTree.getStructure());
    }*/
}