package datastructure.tree.querytree.operator;

import datastructure.relation.table.component.Column;
import datastructure.tree.querytree.operator.Operator;

public class InnerJoin extends Operator {

    private Type type;
    private Column joinOnColumn1, joinOnColumn2;

    public InnerJoin(Column joinOnColumn1, Column joinOnColumn2) {
        this.joinOnColumn1 = joinOnColumn1;
        this.joinOnColumn2 = joinOnColumn2;
    }

    public Column getJoinOnColumn1() {
        return joinOnColumn1;
    }

    public Column getJoinOnColumn2() {
        return joinOnColumn2;
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

        StringBuilder print = new StringBuilder();

        print.append(type).append(" [").append(joinOnColumn1.getName());
        print.append(" + ").append(joinOnColumn2.getName()).append("]");

        return print.toString();
    }
}
