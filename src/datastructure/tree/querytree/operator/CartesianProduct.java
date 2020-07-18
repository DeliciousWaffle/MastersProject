package datastructure.tree.querytree.operator;

import datastructure.tree.querytree.operator.Operator;

public class CartesianProduct extends Operator {

    private Type type;

    public CartesianProduct() {
        this.type = Type.CARTESIAN_PRODUCT;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return null;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("[").append(type).append("]").toString();
    }
}
