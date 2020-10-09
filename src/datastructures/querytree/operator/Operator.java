package datastructures.querytree.operator;

import java.util.List;

/**
 * Operator is short for relational algebra operator. The query tree's nodes will contain these operators and
 * produce result sets based on the current operation. Each node will always contain a type, a way to get the
 * column names referenced, and a way to produce a deep copy of themselves.
 */
public abstract class Operator {

    public enum Type {
        PROJECTION, SIMPLE_SELECTION, COMPOUND_SELECTION, AGGREGATE_SELECTION, AGGREGATION,
        CARTESIAN_PRODUCT, INNER_JOIN, RELATION, PIPELINED_EXPRESSION
    }

    public abstract Type getType();

    public abstract List<String> getReferencedColumnNames();

    public abstract Operator copy(Operator operator);
}