package datastructure.tree.querytree.operator;

import datastructure.tree.conditiontree.component.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a selection relational algebra operator. As of right now,
 * only supports a single condition or a list of conditions in which each
 * has an AND operator between them.
 */
public class Selection extends Operator {

    private Type type;
    private List<Condition> conditions;

    public Selection(List<Condition> conditions) {
        this.type = Type.SELECTION;
        this.conditions = conditions;
    }

    public Selection(Selection toCopy) {
        this.type = Type.SELECTION;
        this.conditions = new ArrayList<>();
        for(Condition toCopyCondition : toCopy.conditions) {
            this.conditions.add(new Condition(toCopyCondition));
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Operator copy(Operator operator) {
        return new Selection((Selection) operator);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        print.append(type).append(" [");

        if(conditions.size() == 1) {
            print.append(conditions.get(0));
        } else {
            for(Condition condition : conditions) {
                print.append(condition).append(", ");
            }
            // remove ", "
            print.deleteCharAt(print.length() - 1);
            print.deleteCharAt(print.length() - 1);
        }

        print.append("]");

        return print.toString();
    }
}