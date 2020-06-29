package utilities;

/**
 * Class used for operations that can be used in other classes.
 */
// TODO remove class
public class Utilities {
/*
    public static final String[] KEYWORDS = {"SELECT", "MIN", "MAX", "AVG", "COUNT", "SUM", "FROM",
            "JOIN", "USING", "WHERE", "AND", "OR", "CREATE", "TABLE", "NUMBER", "CHAR", "DROP", "INSERT", "INTO",
            "VALUES", "DELETE", "UPDATE", "SET", "GRANT", "ALTER", "INDEX", "REFERENCES", "ALL", "PRIVILEGES", "TO", "REVOKE", "ON"};

    public static final String[] SYMBOLS = {"=", "!=", ">", "<", ">=", "<=", "(", ")", ",", ";"};

    // can't instantiate me!
    private Utilities() {}

    public static String[] formatAndTokenizeInput(String input) {

        input = input.toLowerCase();

        // capitalize keywords within input
        for(String keyword : KEYWORDS) {
            input = input.replaceAll(keyword.toLowerCase(), keyword);
        }

        // remove spaces at the beginning of the input
        input = input.replaceAll("^\\s+", "");

        // tokenize via spaces (removes redundant spaces too)
        String[] tokens = input.split("\\s+");
        StringBuilder formatted = new StringBuilder();

        // re-add the spaces
        for(String token : tokens) {
            formatted.append(token).append(" ");
        }

        // re-add ";"
        formatted.append(";");

        // TODO might be error-prone, length changes in loop
        // adding spaces between ",", ")", "(" too
        for(int i = 1; i < formatted.length() - 1; i++) {

            char token = formatted.charAt(i);

            if(token == ',' || token == ')' || token == '(') {

                // is a space needed before/after?
                char beforeToken = formatted.charAt(i - 1);
                char afterToken = formatted.charAt(i + 1);
                int offset = 0;

                if(beforeToken != ' ') {
                    formatted.insert(i, " ");
                    offset = 1;
                }

                if(afterToken != ' ') {
                    formatted.insert(i + 1 + offset, " ");
                }
            }
        }

        return formatted.toString().split("\\s+");
    }

    public static String getInputType(String formattedInput) {

        String firstToken = formattedInput.split("\\s+")[0];

        switch(firstToken) {
            case "SELECT":
                return "QUERY";
            case "GRANT":
                return "PRIVILEGE";
            default:
                return "DML";
        }
    }*/
}
