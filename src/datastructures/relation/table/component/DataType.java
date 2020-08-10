package datastructures.relation.table.component;

public enum DataType {

    CHAR, NUMBER, DATE, UNKNOWN;

    public static DataType convertToDataType(String toConvert) {
        toConvert = toConvert.toUpperCase();
        switch(toConvert) {
            case "CHAR":
                return CHAR;
            case "NUMBER":
                return NUMBER;
            case "DATE":
                return DATE;
            default:
                return UNKNOWN;
        }
    }
}
