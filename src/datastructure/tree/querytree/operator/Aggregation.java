package datastructure.tree.querytree.operator;

import datastructure.relation.table.component.Column;

import java.util.ArrayList;
import java.util.List;

public class Aggregation extends Operator {

    private Type type;
    private List<Column> groupByColumns, columnsToAggregate;

    public Aggregation(List<Column> groupByColumns, List<Column> columnsToAggregate) {
        this.type = Type.AGGREGATION;
        this.groupByColumns = groupByColumns;
        this.columnsToAggregate = columnsToAggregate;
    }

    public Aggregation(Aggregation toCopy) {
        this.type = Type.AGGREGATION;
        this.groupByColumns = new ArrayList<>();
        for(Column groupByColumn : toCopy.groupByColumns) {
            this.groupByColumns.add(new Column(groupByColumn));
        }
        this.columnsToAggregate = new ArrayList<>();
        for(Column aggregateColumn : toCopy.columnsToAggregate) {
            this.columnsToAggregate.add(new Column(aggregateColumn));
        }
    }

    public List<Column> getGroupByColumns() {
        return groupByColumns;
    }

    public List<Column> getColumnsToAggregate() {
        return columnsToAggregate;
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

        print.append(type).append(" [");

        if(groupByColumns.size() == 1) {
            print.append(groupByColumns.get(0).getName());
        } else {
            for(Column groupByColumn : groupByColumns) {
                print.append(groupByColumn.getName()).append(", ");
            }
            // remove ", "
            print.deleteCharAt(print.length() - 1);
            print.deleteCharAt(print.length() - 1);
        }

        print.append(" G ");

        if(columnsToAggregate.size() == 1) {
            print.append(columnsToAggregate.get(0).getName());
        } else {
            for(Column aggregateColumn : columnsToAggregate) {
                print.append(aggregateColumn.getName()).append(", ");
            }
            // remove ", "
            print.deleteCharAt(print.length() - 1);
            print.deleteCharAt(print.length() - 1);
        }

        print.append("]");

        return print.toString();
    }
}
