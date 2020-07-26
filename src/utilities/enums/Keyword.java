package utilities.enums;

public enum Keyword {
    SELECT, MIN, MAX, AVG, COUNT, SUM, FROM, JOIN, USING, WHERE, AND, OR,
    GROUP, BY, HAVING, CREATE, TABLE, NUMBER, CHAR, DROP, INSERT, INTO, VALUES, DELETE, UPDATE,
    SET, GRANT, ALTER, INDEX, REFERENCES, ALL, PRIVILEGES, TO, REVOKE, ON;

    public static boolean isAggregateFunction(Keyword candidate) {
        return  candidate == Keyword.MIN || candidate == Keyword.MAX   ||
                candidate == Keyword.AVG || candidate == Keyword.COUNT ||
                candidate == Keyword.SUM;
    }
}