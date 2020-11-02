package datastructures.querytree.operator.types;

import datastructures.querytree.operator.Operator;
import utilities.OptimizerUtilities;

import java.util.ArrayList;
import java.util.List;

public class Projection extends Operator {

    private List<String> columnNames;

    public Projection(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public Projection(Projection toCopy) {
        this.columnNames = new ArrayList<>();
        this.columnNames.addAll(toCopy.columnNames);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public Type getType() {
        return Type.PROJECTION;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return columnNames;
    }

    @Override
    public Operator copy(Operator operator) {
        return new Projection((Projection) operator);
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        print.append("π").append(" (");

        if (columnNames.size() == 1) {
            String columnName = columnNames.get(0);
            if (! OptimizerUtilities.isAmbiguousColumnName(columnName, columnNames)) {
                columnName = OptimizerUtilities.removePrefixedColumnName(columnName);
            }
            print.append(columnName);

        } else {
            for (String columnName : columnNames) {
                if (! OptimizerUtilities.isAmbiguousColumnName(columnName, columnNames)) {
                    columnName = OptimizerUtilities.removePrefixedColumnName(columnName);
                }
                print.append(columnName).append(", ");
            }
            // remove ", "
            print.delete(print.length() - 2, print.length());
        }

        print.append(")");

        return print.toString();
    }
}