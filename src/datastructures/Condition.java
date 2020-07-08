package datastructures;

import datastructures.table.component.Column;
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

    public Column getColumn() { return column; }

    public Symbol getSymbol() { return symbol; }

    public String getTarget() { return target; }

    @Override
    public String toString() {
        return new StringBuilder().append(column).append(" ").append(symbol).
                append(" ").append(target).toString();
    }
}
