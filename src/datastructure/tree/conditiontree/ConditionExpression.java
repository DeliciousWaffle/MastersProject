package datastructure.tree.conditiontree;

import datastructure.tree.conditiontree.component.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of conditions that have either an AND or an OR between them.
 */
public class ConditionExpression {

    public enum Type {
        NONE, AND, OR
    }

    private List<Condition> conditions;
    private List<Type> operators; // can take on either AND, OR, or DONE

    public ConditionExpression(Condition condition) {
        conditions = new ArrayList<>();
        operators = new ArrayList<>();

        conditions.add(condition);
        operators.add(Type.NONE);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public List<Type> getOperators() {
        return operators;
    }

    public void and(Condition condition) {
        conditions.add(condition);
        operators.add(Type.AND);
    }

    public void or(Condition condition) {
        conditions.add(condition);
        operators.add(Type.OR);
    }
}