package datastructures.querytree.operator.types;

import datastructures.querytree.operator.Operator;
import utilities.OptimizerUtilities;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimpleSelection extends Operator {

    private final Type type;
    private String columnName, symbol, value;

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

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return new ArrayList<>(Arrays.asList(columnName));
    }

    @Override
    public Operator copy(Operator operator) {
        return new SimpleSelection((SimpleSelection) operator);
    }

    @Override
    public String toString() {

        String formattedValue = value;

        // enclose date and char values in quotes only if the value is not a join predicate and not a number
        if (! Utilities.isNumeric(value) && ! OptimizerUtilities.isPrefixed(value)) {
            formattedValue = "\"" + value + "\"";
        }

        return "σ" + " (" + columnName + " " + symbol + " " + formattedValue + ")";
    }
}