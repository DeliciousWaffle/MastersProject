package utilities.enums;

public enum Symbol {

    EQUAL("="), NOT_EQUAL("!="), GREATER_THAN(">"), LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">="), LESS_THAN_OR_EQUAL("<="), LEFT_PARENTHESES("("),
    RIGHT_PARENTHESES(")"), COMMA(","), DOUBLE_QUOTE("\""), SEMICOLON(";");

    private final String symbol;

    Symbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public static Symbol convertToSymbol(String toConvert) {
        switch(toConvert) {
            case "=":
                return EQUAL;
            case "!=":
                return NOT_EQUAL;
            case ">":
                return GREATER_THAN;
            case "<":
                return LESS_THAN;
            case ">=":
                return GREATER_THAN_OR_EQUAL;
            case "<=":
                return LESS_THAN_OR_EQUAL;
            case "(":
                return LEFT_PARENTHESES;
            case ")":
                return RIGHT_PARENTHESES;
            case ",":
                return COMMA;
            case "\"":
                return DOUBLE_QUOTE;
            case ";":
            default:
                return SEMICOLON;
        }
    }
}
