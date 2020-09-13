package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

public class InnerJoin extends Operator {

    private final Type type;
    private final String joinOnColumn1, symbol, joinOnColumn2;

    public InnerJoin(String joinOnColumn1, String symbol, String joinOnColumn2) {
        this.type = Type.INNER_JOIN;
        this.joinOnColumn1 = joinOnColumn1;
        this.symbol = symbol;
        this.joinOnColumn2 = joinOnColumn2;
    }

    public InnerJoin(InnerJoin toCopy) {
        this.type = Type.INNER_JOIN;
        this.joinOnColumn1 = toCopy.joinOnColumn1;
        this.symbol = toCopy.symbol;
        this.joinOnColumn2 = toCopy.joinOnColumn2;
    }

    public String getJoinOnColumn1() {
        return joinOnColumn1;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getJoinOnColumn2() {
        return joinOnColumn2;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return new InnerJoin((InnerJoin) operator);
    }

    @Override
    public String toString() {
        return "\u2A1D" + " (" + joinOnColumn1 + " " + symbol + " " + joinOnColumn2 + ")";
    }
}