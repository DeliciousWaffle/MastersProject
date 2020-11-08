package datastructures.querytree.operator.types;

import datastructures.querytree.operator.Operator;
import systemcatalog.components.Optimizer;
import utilities.OptimizerUtilities;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundSelection extends Operator {

    private List<String> columnNames, symbols, values;

    public CompoundSelection(List<String> columnNames, List<String> symbols, List<String> values) {
        this.columnNames = columnNames;
        this.symbols = symbols;
        this.values = values;
    }

    public CompoundSelection(CompoundSelection toCopy) {
        this.columnNames = new ArrayList<>();
        this.columnNames.addAll(toCopy.columnNames);
        this.symbols = new ArrayList<>();
        this.symbols.addAll(toCopy.symbols);
        this.values = new ArrayList<>();
        this.values.addAll(toCopy.values);
    }

    public void add(String columnName, String symbol, String value) {
        columnNames.add(columnName);
        symbols.add(symbol);
        values.add(value);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public Type getType() {
        return Type.COMPOUND_SELECTION;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return columnNames.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Operator copy(Operator operator) {
        return new CompoundSelection((CompoundSelection) operator);
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        print.append("σ").append(" (");

        for (int i = 0; i < symbols.size(); i++) {

            String columnName = columnNames.get(i);
            String symbol = symbols.get(i);
            String value = values.get(i);

            String formattedValue = value;

            // enclose date and char values in quotes only if the value is not a join predicate and not a number
            if (! Utilities.isNumeric(value) && ! OptimizerUtilities.isPrefixed(value)) {
                formattedValue = "\"" + formattedValue + "\"";
            }

            print.append(columnName).append(" ").append(symbol).append(" ").append(formattedValue);
            print.append(" ").append("∧").append(" ");
        }

        // remove logical conjunction and spaces
        print.delete(print.length() - 3, print.length());
        print.append(")");

        return print.toString();
    }
}