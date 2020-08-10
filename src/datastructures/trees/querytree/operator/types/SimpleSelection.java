package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

public class SimpleSelection extends Operator {

    private final Type type;
    private final String columnName, symbol, value;

    public SimpleSelection(String columnName, String symbol, String value) {
        this.type = Type.SIMPLE_SELECTION;
        this.columnName = columnName;
        this.symbol = symbol;
        this.value = value;
    }

    public SimpleSelection(SimpleSelection toCopy) {
        this.type = Type.SIMPLE_SELECTION;
        this.columnName = toCopy.columnName;
        this.symbol = toCopy.symbol;
        this.value = toCopy.value;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return new SimpleSelection((SimpleSelection) operator);
    }

    @Override
    public String toString() {
        return "\u03C3" + " [" + columnName + " " + symbol + " " + value + "]";
    }
}