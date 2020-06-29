package utilities.enums;

public enum Symbol {

    EQUAL("="), NOT_EQUAL("!="), GREATER_THAN(">"), LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">="), LESS_THAN_OR_EQUAL("<="), LEFT_PARENTHESES("("),
    RIGHT_PARENTHESES(")"), COMMA(","), SEMICOLON(";");

    private String symbol;

    Symbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
