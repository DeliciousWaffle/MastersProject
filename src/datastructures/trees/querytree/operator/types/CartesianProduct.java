package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class CartesianProduct extends Operator {

    public CartesianProduct() {}

    @Override
    public Type getType() {
        return Type.CARTESIAN_PRODUCT;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return new ArrayList<>();
    }

    @Override
    public Operator copy(Operator operator) {
        return new CartesianProduct();
    }

    @Override
    public String toString() {
        return "✕";
    }
}