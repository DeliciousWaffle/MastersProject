package utilities.enums;

public enum Privilege {

    ALTER, DELETE, INDEX, INSERT, SELECT, UPDATE, REFERENCES, ALL_PRIVILEGES, UNKNOWN;

    public static Privilege convertToPrivilege(String toConvert) {
        switch(toConvert) {
            case "ALTER":
                return ALTER;
            case "DELETE":
                return DELETE;
            case "INDEX":
                return INDEX;
            case "INSERT":
                return INSERT;
            case "SELECT":
                return SELECT;
            case "UPDATE":
                return UPDATE;
            case "REFERENCES":
                return REFERENCES;
            case "ALL_PRIVILEGES":
                return ALL_PRIVILEGES;
            default:
                return UNKNOWN;
        }
    }
}
