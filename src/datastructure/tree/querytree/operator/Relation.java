package datastructure.tree.querytree.operator;

public class Relation extends Operator {

    private Type type;
    private String tableName;

    public Relation(String tableName) {
        this.type = Type.RELATION;
        this.tableName = tableName;
    }

    public Relation(Relation toCopy) {
        this.type = Type.RELATION;
        this.tableName = toCopy.tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public Type getType() {
        return Type.RELATION;
    }

    @Override
    public Operator copy(Operator operator) {
        return new Relation((Relation) operator);
    }

    @Override
    public String toString() {
        return tableName;
    }
}