package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class Aggregation extends Operator {

    private final Type type;
    private final List<String> groupByColumnNames, aggregationTypes, columnNames;

    public Aggregation(List<String> groupByColumnNames, List<String> aggregationTypes, List<String> columnNames) {
        this.type = Type.AGGREGATION;
        this.groupByColumnNames = groupByColumnNames;
        this.aggregationTypes = aggregationTypes;
        this.columnNames = columnNames;
    }

    public Aggregation(Aggregation toCopy) {
        this.type = Type.AGGREGATION;
        this.groupByColumnNames = new ArrayList<>();
        this.groupByColumnNames.addAll(toCopy.groupByColumnNames);
        this.aggregationTypes = new ArrayList<>();
        this.aggregationTypes.addAll(toCopy.aggregationTypes);
        this.columnNames = new ArrayList<>();
        this.columnNames.addAll(toCopy.columnNames);
    }

    public List<String> getGroupByColumnNames() {
        return groupByColumnNames;
    }

    public List<String> getAggregationTypes() {
        return aggregationTypes;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return new Aggregation((Aggregation) operator);
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        if(groupByColumnNames.size() == 1) {
            print.append(groupByColumnNames.get(0));
        } else if(groupByColumnNames.size() > 1) {
            for(String groupByColumnName : groupByColumnNames) {
                print.append(groupByColumnName).append(", ");
            }
            // remove ", "
            print.deleteCharAt(print.length() - 1);
            print.deleteCharAt(print.length() - 1);
        }

        // add a space if there are columns to group by
        if(! groupByColumnNames.isEmpty()) {
            print.append(" ");
        }

        print.append("G ");

        if(columnNames.size() == 1) {
            print.append(aggregationTypes.get(0)).append("(").append(columnNames.get(0)).append(")");
        } else {
            for(int i = 0; i < aggregationTypes.size(); i++) {
                String aggregationType = aggregationTypes.get(i);
                String columnName = columnNames.get(i);
                String formatted = aggregationType + "(" + columnName + ")";
                print.append(formatted).append(", ");
            }
            // remove ", "
            print.deleteCharAt(print.length() - 1);
            print.deleteCharAt(print.length() - 1);
        }

        return print.toString();
    }
}