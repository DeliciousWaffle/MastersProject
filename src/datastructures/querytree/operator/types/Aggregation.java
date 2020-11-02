package datastructures.querytree.operator.types;

import datastructures.querytree.operator.Operator;
import utilities.OptimizerUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aggregation extends Operator {

    private static final String FANCY_G = "\uD835\uDCA2 ";
    private List<String> groupByColumnNames, aggregationTypes, aggregatedColumnNames;

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

    public void setGroupByColumnNames(List<String> groupByColumnNames) {
        this.groupByColumnNames = groupByColumnNames;
    }

    public List<String> getAggregationTypes() {
        return aggregationTypes;
    }

    public void setAggregationTypes(List<String> aggregationTypes) {
        this.aggregationTypes = aggregationTypes;
    }

    public List<String> getAggregatedColumnNames() {
        return aggregatedColumnNames;
    }

    public void setAggregatedColumnNames(List<String> aggregatedColumnNames) {
        this.aggregatedColumnNames = aggregatedColumnNames;
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

        if (groupByColumnNames.size() == 1) {
            String groupByColumnName = groupByColumnNames.get(0);
            if (! OptimizerUtilities.isAmbiguousColumnName(groupByColumnName, getReferencedColumnNames())) {
                groupByColumnName = OptimizerUtilities.removePrefixedColumnName(groupByColumnName);
            }
            print.append(groupByColumnName);
        } else if(groupByColumnNames.size() > 1) {
            for (String groupByColumnName : groupByColumnNames) {
                if (! OptimizerUtilities.isAmbiguousColumnName(groupByColumnName, getReferencedColumnNames())) {
                    groupByColumnName = OptimizerUtilities.removePrefixedColumnName(groupByColumnName);
                }
                print.append(groupByColumnName).append(", ");
            }
            // remove ", "
            print.deleteCharAt(print.length() - 1);
            print.deleteCharAt(print.length() - 1);
        }

        // add a space if there are columns to group by
        if (! groupByColumnNames.isEmpty()) {
            print.append(" ");
        }

        print.append(FANCY_G);

        if (aggregatedColumnNames.size() == 1) {
            String aggregatedColumnName = aggregatedColumnNames.get(0);
            if (! OptimizerUtilities.isAmbiguousColumnName(aggregatedColumnName, getReferencedColumnNames())) {
                aggregatedColumnName = OptimizerUtilities.removePrefixedColumnName(aggregatedColumnName);
            }
            aggregatedColumnName = aggregationTypes.get(0) + "(" + aggregatedColumnName + ")";
            print.append(aggregatedColumnName);
        } else {
            for (int i = 0; i < aggregationTypes.size(); i++) {
                String aggregationType = aggregationTypes.get(i);
                String aggregatedColumnName = aggregatedColumnNames.get(i);
                if (! OptimizerUtilities.isAmbiguousColumnName(aggregatedColumnName, getReferencedColumnNames())) {
                    aggregatedColumnName = OptimizerUtilities.removePrefixedColumnName(aggregatedColumnName);
                }
                aggregatedColumnName = aggregationType + "(" + aggregatedColumnName + ")";
                print.append(aggregatedColumnName).append(", ");
            }
            // remove ", "
            print.delete(print.length() - 2, print.length());
        }

        return print.toString();
    }
}