package datastructures;

import datastructures.table.component.Column;
import utilities.enums.Symbol;

/**
 * A simple class representing the relational algebra selection operator.
 */
public class Selection {

    private Column column;
    private Symbol symbol;
    private String target;

    public Selection(Column column, Symbol symbol, String target) {
        this.column = column;
        this.symbol = symbol;
        this.target = target;
    }

    public Column getColumn() { return column; }

    public Symbol getSymbol() { return symbol; }

    public String getTarget() { return target; }
}
