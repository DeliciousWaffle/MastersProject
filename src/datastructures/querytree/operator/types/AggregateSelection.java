package datastructures.querytree.operator.types;

import datastructures.querytree.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class AggregateSelection extends Operator {

    private final List<String> aggregateTypes, columnNames, symbols, values;

    public AggregateSelection(List<String> aggregateTypes, List<String> columnNames,
                              List<String> symbols, List<String> values) {
        this.aggregateTypes = aggregateTypes;
        this.columnNames = columnNames;
        this.symbols = symbols;
        this.values = values;
    }

    public AggregateSelection(AggregateSelection toCopy) {
        this.aggregateTypes = new ArrayList<>();
        this.aggregateTypes.addAll(toCopy.aggregateTypes);
        this.columnNames = new ArrayList<>();
        this.columnNames.addAll(toCopy.columnNames);
        this.symbols = new ArrayList<>();
        this.symbols.addAll(toCopy.symbols);
        this.values = new ArrayList<>();
        this.values.addAll(toCopy.values);
    }

    public void add(String aggregateType, String columnName, String symbol, String value) {
        aggregateTypes.add(aggregateType);
        columnNames.add(columnName);
        symbols.add(symbol);
        values.add(value);
    }

    public List<String> getAggregateTypes() {
        return aggregateTypes;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public Operator.Type getType() {
        return Type.AGGREGATE_SELECTION;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return columnNames;
    }

    @Override
    public Operator copy(Operator operator) {
        return new AggregateSelection((AggregateSelection) operator);
    }

    @Override
    public String toString() {
        StringBuilder print = new StringBuilder();

        print.append("σ (");

        for(int i = 0; i < aggregateTypes.size(); i++) {

            String aggregateType = aggregateTypes.get(i);
            String columnName = columnNames.get(i);
            String symbol = symbols.get(i);
            String value = values.get(i);

            print.append(aggregateType).append("(").append(columnName).append(")");
            print.append(" ").append(symbol).append(" ").append(value);

            // logical conjunction unicode value
            print.append(" ").append("∧").append(" ");
        }

        // remove logical conjunction and spaces
        print.deleteCharAt(print.length() - 1);
        print.deleteCharAt(print.length() - 1);
        print.deleteCharAt(print.length() - 1);

        print.append("))");

        return print.toString();
    }
}