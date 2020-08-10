package datastructures.trees.conditiontree.component;

import datastructures.relation.table.component.Column;
import utilities.enums.Symbol;

/**
 * A simple class representing a condition.
 */
public class Condition {

    private Column column;
    private Symbol symbol;
    private String target;

    public Condition(Column column, Symbol symbol, String target) {
        this.column = column;
        this.symbol = symbol;
        this.target = target;
    }

    public Condition(Condition toCopy) {
        this.column = new Column(toCopy.column);
        this.symbol = toCopy.symbol;
        this.target = toCopy.target;
    }

    public Column getColumn() { return column; }

    public Symbol getSymbol() { return symbol; }

    public String getTarget() { return target; }

    @Override
    public String toString() {
        StringBuilder print = new StringBuilder();
        print.append(column.getName()).append(" ").append(symbol).append(" ").append(target);
        return print.toString();
    }
}