package datastructure.tree.querytree.operator;

import datastructure.relation.table.component.Column;
import datastructure.tree.querytree.operator.Operator;

import java.util.List;

public class Projection extends Operator {

    private Type type;
    private List<Column> projectionColumns;

    public Projection(List<Column> projectionColumns) {
        this.type = Type.PROJECTION;
        this.projectionColumns = projectionColumns;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return null;
    }

    public List<Column> getProjectionColumns() {
        return projectionColumns;
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        print.append(type).append(" [");

        if(projectionColumns.size() == 1) {
            print.append(projectionColumns.get(0).getName());
        } else {
            for(Column projectionColumn : projectionColumns) {
                print.append(projectionColumn.getName()).append(", ");
            }
            // remove ", "
            print.deleteCharAt(print.length() - 1);
            print.deleteCharAt(print.length() - 1);
        }

        print.append("]");

        return print.toString();
    }
}