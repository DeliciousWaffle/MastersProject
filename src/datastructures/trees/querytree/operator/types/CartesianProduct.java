package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

public class CartesianProduct extends Operator {

    private final Type type;

    public CartesianProduct() {
        this.type = Type.CARTESIAN_PRODUCT;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return new CartesianProduct();
    }

    @Override
    public String toString() {
        return "\u2715";
    }
}