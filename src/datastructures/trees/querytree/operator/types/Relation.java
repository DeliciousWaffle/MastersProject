package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class Relation extends Operator {

    private String tableName;

    public Relation(String tableName) {
        this.tableName = tableName;
    }

    public Relation(Relation toCopy) {
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
    public List<String> getReferencedColumnNames() {
        return new ArrayList<>();
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