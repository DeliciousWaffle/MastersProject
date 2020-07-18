package datastructure.tree.querytree.operator;

import datastructure.relation.table.Table;

public class Relation extends Operator {

    private Type type;
    private Table table;

    public Relation(Table table) {
        this.type = Type.RELATION;
        this.table = table;
    }

    public Relation(Relation toCopy) {
        this.type = Type.RELATION;
        this.table = new Table(toCopy.table);
    }

    public Table getTable() {
        return table;
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
        StringBuilder print = new StringBuilder();
        print.append(type).append(" [").append(table.getTableName()).append("]");
        return print.toString();
    }
}