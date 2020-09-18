package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

import java.util.Arrays;
import java.util.List;

public class InnerJoin extends Operator {

    private final String joinOnColumn1, symbol, joinOnColumn2;

    public InnerJoin(String joinOnColumn1, String symbol, String joinOnColumn2) {
        this.joinOnColumn1 = joinOnColumn1;
        this.symbol = symbol;
        this.joinOnColumn2 = joinOnColumn2;
    }

    public InnerJoin(InnerJoin toCopy) {
        this.joinOnColumn1 = toCopy.joinOnColumn1;
        this.symbol = toCopy.symbol;
        this.joinOnColumn2 = toCopy.joinOnColumn2;
    }

    public String getFirstJoinColumnName() {
        return joinOnColumn1;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSecondJoinColumnName() {
        return joinOnColumn2;
    }

    @Override
    public Type getType() {
        return Type.INNER_JOIN;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return Arrays.asList(joinOnColumn1, joinOnColumn2);
    }

    @Override
    public Operator copy(Operator operator) {
        return new InnerJoin((InnerJoin) operator);
    }

    @Override
    public String toString() {
        return "⨝" + " (" + joinOnColumn1 + " " + symbol + " " + joinOnColumn2 + ")";
    }
}