package datastructure.tree.querytree.operator;

import datastructure.relation.table.component.Column;

import java.util.ArrayList;
import java.util.List;

public class Projection extends Operator {

    private Type type;
    private List<Column> columnsToProject;

    public Projection(List<Column> columnsToProject) {
        this.type = Type.PROJECTION;
        this.columnsToProject = columnsToProject;
    }

    public Projection(Projection toCopy) {
        this.type = Type.PROJECTION;
        this.columnsToProject = new ArrayList<>();
        for(Column toCopyProjectionColumn : toCopy.columnsToProject) {
            this.columnsToProject.add(new Column(toCopyProjectionColumn));
        }
    }

    public List<Column> getColumnsToProject() {
        return columnsToProject;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return new Projection((Projection) operator);
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        print.append(type).append(" [");

        if(columnsToProject.size() == 1) {
            print.append(columnsToProject.get(0).getName());
        } else {
            for(Column projectionColumn : columnsToProject) {
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