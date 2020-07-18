package datastructure.tree.querytree.operator;

public class CartesianProduct extends Operator {

    private Type type;

    public CartesianProduct() {
        this.type = Type.CARTESIAN_PRODUCT;
    }

    public CartesianProduct(CartesianProduct toCopy) {
        this.type = Type.CARTESIAN_PRODUCT;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return new CartesianProduct((CartesianProduct) (operator));
    }

    @Override
    public String toString() {
        StringBuilder print = new StringBuilder();
        print.append("[").append(type).append("]");
        return print.toString();
    }
}