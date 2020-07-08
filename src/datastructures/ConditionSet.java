package datastructures;

import java.util.Stack;

/**
 * A list of conditions.
 */
public class ConditionSet {

    private Stack<Condition> andConditions;
    private Stack<Stack<Condition>> orConditions;
    boolean isComplete;
    private StringBuilder conditionListStringBuilder;

    public ConditionSet(Condition condition) {

        this.andConditions = new Stack<>();
        this.orConditions = new Stack<>();
        this.isComplete = false;
        this.conditionListStringBuilder = new StringBuilder();

        andConditions.push(condition);
        //conditionListStringBuilder.append("(").append(condition.getColumn()).append(" ").
        //        append(condition.getSymbol()).append(" ").append(condition.getTarget()).append(")");
    }

    public void and(Condition condition) {
        andConditions.push(condition);
    }

    public void or(Condition condition) {

        orConditions.push(andConditions);
        andConditions = new Stack<>();
        andConditions.push(condition);
    }

    public void complete() {
        if(! andConditions.isEmpty()) {
            orConditions.push(andConditions);
            andConditions = new Stack<>();
        }
        isComplete = true;
    }

    public Stack<Stack<Condition>> getConditions() {
        if(isComplete) {
            return orConditions;
        }
        return new Stack<Stack<Condition>>();
    }

    /*public boolean resolve(HashMap<String, String> columnRowPairs) {

        // add last remaining and stack
        if(! andConditions.isEmpty()) {
            orConditions.push(andConditions);
            andConditions = new Stack<>();
        }

        boolean metAllOrConditions = false;

        while(! orConditions.isEmpty()) {

            Stack<Condition> conditions = orConditions.pop();
            boolean hasAndConditions = conditions.size() > 1;

            if(hasAndConditions) {

                boolean metAllAndConditions = true;

                while (! conditions.isEmpty()) {
                    Condition condition = conditions.pop();
                    metAllAndConditions = metAllAndConditions && conditionMet(columnRowPairs, condition);
                }

                metAllOrConditions = metAllOrConditions || metAllAndConditions;

            } else {
                Condition condition = conditions.pop();
                metAllOrConditions = metAllOrConditions || conditionMet(columnRowPairs, condition);
            }
        }

        return metAllOrConditions;
    }

    private boolean conditionMet(HashMap<String, String> columnRowPairs, Condition condition) {

        // get the contents of each condition
        String columnName = condition.getColumn().getName();
        Symbol symbol = condition.getSymbol();
        String target = condition.getTarget();

        // get what we are looking for
        String possibleTarget = columnRowPairs.get(columnName);

        boolean isNumeric = condition.getColumn().getDataType() == DataType.NUMBER;

        // evaluate the condition
        if (isNumeric) {

            double targetNumber = Double.parseDouble(target);
            double possibleTargetNumber = Double.parseDouble(possibleTarget);

            switch (symbol) {
                case EQUAL:
                    if (targetNumber == possibleTargetNumber) {
                        return true;
                    }
                    break;
                case NOT_EQUAL:
                    if (targetNumber != possibleTargetNumber) {
                        return true;
                    }
                    break;
                case GREATER_THAN:
                    if (targetNumber > possibleTargetNumber) {
                        return true;
                    }
                    break;
                case LESS_THAN:
                    if (targetNumber < possibleTargetNumber) {
                        return true;
                    }
                    break;
                case GREATER_THAN_OR_EQUAL:
                    if (targetNumber >= possibleTargetNumber) {
                        return true;
                    }
                    break;
                case LESS_THAN_OR_EQUAL:
                    if (targetNumber <= possibleTargetNumber) {
                        return true;
                    }
                    break;
                default:
                    System.out.println("In ResultSet.projection()");
                    System.out.println("Symbol Used: " + symbol.toString());
                    return false;
            }

        // not a numeric value
        } else {
            switch (symbol) {
                case EQUAL:
                    if (target.equalsIgnoreCase(possibleTarget)) {
                        return true;
                    }
                    break;
                case NOT_EQUAL:
                    if (!target.equalsIgnoreCase(possibleTarget)) {
                        return true;
                    }
                    break;
                default:
                    System.out.println("In ResultSet.projection()");
                    System.out.println("Symbol Used: " + symbol.toString());
                    return false;
            }
        }

        return false;
    }*/
}