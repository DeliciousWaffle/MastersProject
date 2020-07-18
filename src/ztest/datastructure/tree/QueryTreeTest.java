package ztest.datastructure.tree;

import datastructure.relation.table.component.Column;
import datastructure.relation.table.component.DataType;
import datastructure.tree.conditiontree.component.Condition;
import datastructure.tree.querytree.component.*;
import datastructure.tree.querytree.operator.Aggregation;
import datastructure.tree.querytree.operator.Operator;
import datastructure.tree.querytree.operator.Projection;
import datastructure.tree.querytree.operator.Selection;
import org.junit.jupiter.api.Test;
import utilities.enums.Symbol;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTreeTest {

    @Test
    public void testCopy1() {

        Column c1 = new Column("c1", DataType.NUMBER, 30);
        Column c2 = new Column("c2", DataType.CHAR, 15);
        Column c3 = new Column("c3", DataType.NUMBER, 15);

        List<Column> columns1 = new ArrayList<>();
        columns1.add(c1);
        List<Column> columns2 = new ArrayList<>();
        columns2.add(c2);
        columns2.add(c3);

        Operator o1 = new Aggregation(columns1, columns2);
        System.out.println(o1);

        Operator o2 = o1.copy(o1);
        System.out.println(o2);

        System.out.println("Making changes!");
        Aggregation a1 = (Aggregation) o1;
        a1.getAggregateColumns().add(new Column("NewColumn", DataType.DATE, 10));
        Aggregation a2 = (Aggregation) o2;
        a2.getGroupByColumns().get(0).setName("Blah");
        System.out.println(o1);
        System.out.println(o2);

        assertTrue(true);
    }

    @Test
    public void testCopy2() {

        Column c1 = new Column("A", DataType.NUMBER, 10);
        Column c2 = new Column("B", DataType.NUMBER, 10);
        List<Column> columns = new ArrayList<>();
        columns.add(c1);
        columns.add(c2);
        Operator projection = new Projection(columns);
        Condition con1 = new Condition(c1, Symbol.EQUAL, "5");
        List<Condition> conditions = new ArrayList<>();
        conditions.add(con1);
        Operator selection = new Selection(conditions);

        QueryTreeNode q1 = new QueryTreeNode(projection, null);
        QueryTreeNode q2 = new QueryTreeNode(selection, q1);
    }
}