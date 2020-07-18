package datastructure.tree.querytree.operator;

public abstract class Operator {

    public enum Type {
        PROJECTION, SELECTION, AGGREGATION, CARTESIAN_PRODUCT, INNER_JOIN
    }

    public abstract Type getType();

    public abstract Operator copy(Operator operator);
}
