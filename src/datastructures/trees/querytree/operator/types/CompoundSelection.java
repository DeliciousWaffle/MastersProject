package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class CompoundSelection extends Operator {

    private final Type type;
    private final List<String> columnNames, symbols, values;

    public CompoundSelection(List<String> columnNames, List<String> symbols, List<String> values) {
        this.type = Type.COMPOUND_SELECTION;
        this.columnNames = columnNames;
        this.symbols = symbols;
        this.values = values;
    }

    public CompoundSelection(CompoundSelection toCopy) {
        this.type = Type.COMPOUND_SELECTION;
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

    public List<String> getSymbols() {
        return symbols;
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return new CompoundSelection((CompoundSelection) operator);
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        print.append("\u03C3").append(" (");

        for(int i = 0; i < symbols.size(); i++) {

            String columnName = columnNames.get(i);
            String symbol = symbols.get(i);
            String value = values.get(i);

            print.append(columnName).append(" ").append(symbol).append(" ").append(value);

            // logical conjunction unicode value
            print.append(" ").append("\u2227").append(" ");
        }

        // remove logical conjunction and spaces
        print.deleteCharAt(print.length() - 1);
        print.deleteCharAt(print.length() - 1);
        print.deleteCharAt(print.length() - 1);

        print.append(")");

        return print.toString();
    }
}