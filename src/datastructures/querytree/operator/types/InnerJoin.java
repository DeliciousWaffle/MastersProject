package datastructures.querytree.operator.types;

import datastructures.querytree.operator.Operator;

import java.util.Arrays;
import java.util.List;

public class InnerJoin extends Operator {

    private String firstJoinColumnName, symbol, secondJoinColumnName;

    public InnerJoin(String firstJoinColumnName, String symbol, String secondJoinColumnName) {
        this.firstJoinColumnName = firstJoinColumnName;
        this.symbol = symbol;
        this.secondJoinColumnName = secondJoinColumnName;
    }

    public InnerJoin(InnerJoin toCopy) {
        this.firstJoinColumnName = toCopy.firstJoinColumnName;
        this.symbol = toCopy.symbol;
        this.secondJoinColumnName = toCopy.secondJoinColumnName;
    }

    public String getFirstJoinColumnName() {
        return firstJoinColumnName;
    }

    public void setFirstJoinColumnName(String firstJoinColumnName) {
        this.firstJoinColumnName = firstJoinColumnName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSecondJoinColumnName() {
        return secondJoinColumnName;
    }

    public void setSecondJoinColumnName(String secondJoinColumnName) {
        this.secondJoinColumnName = secondJoinColumnName;
    }

    @Override
    public Type getType() {
        return Type.INNER_JOIN;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return Arrays.asList(firstJoinColumnName, secondJoinColumnName);
    }

    @Override
    public Operator copy(Operator operator) {
        return new InnerJoin((InnerJoin) operator);
    }

    @Override
    public String toString() {
        return "⨝" + " (" + firstJoinColumnName + " " + symbol + " " + secondJoinColumnName + ")";
    }
}