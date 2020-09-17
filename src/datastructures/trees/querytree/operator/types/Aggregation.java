package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aggregation extends Operator {

    private final List<String> groupByColumnNames, aggregationTypes, aggregatedColumnNames;

    public Aggregation(List<String> groupByColumnNames, List<String> aggregationTypes, List<String> aggregatedColumnNames) {
        this.groupByColumnNames = groupByColumnNames;
        this.aggregationTypes = aggregationTypes;
        this.aggregatedColumnNames = aggregatedColumnNames;
    }

    public Aggregation(Aggregation toCopy) {
        this.groupByColumnNames = new ArrayList<>();
        this.groupByColumnNames.addAll(toCopy.groupByColumnNames);
        this.aggregationTypes = new ArrayList<>();
        this.aggregationTypes.addAll(toCopy.aggregationTypes);
        this.aggregatedColumnNames = new ArrayList<>();
        this.aggregatedColumnNames.addAll(toCopy.aggregatedColumnNames);
    }

    public List<String> getGroupByColumnNames() {
        return groupByColumnNames;
    }

    public List<String> getAggregationTypes() {
        return aggregationTypes;
    }

    public List<String> getAggregatedColumnNames() {
        return aggregatedColumnNames;
    }

    @Override
    public Type getType() {
        return Type.AGGREGATION;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return Stream.concat(groupByColumnNames.stream(), aggregatedColumnNames.stream())
                .distinct()
                .collect(Collectors.toList());
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

        print.append("\uD835\uDCA2 ");

        if(aggregatedColumnNames.size() == 1) {
            print.append(aggregationTypes.get(0)).append("(").append(aggregatedColumnNames.get(0)).append(")");
        } else {
            for(int i = 0; i < aggregationTypes.size(); i++) {
                String aggregationType = aggregationTypes.get(i);
                String columnName = aggregatedColumnNames.get(i);
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