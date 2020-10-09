package enums;

/**
 * Represents the various types of input that the system accepts. Each type has an index associated with it to make
 * using lists easier.
 */
public enum InputType {

    QUERY(0), CREATE_TABLE(1), DROP_TABLE(2), ALTER_TABLE(3), INSERT(4), DELETE(5),
    UPDATE(6), GRANT(7), REVOKE(8), BUILD_FILE_STRUCTURE(9), REMOVE_FILE_STRUCTURE(10),
    UNKNOWN(-1);

    private final int index;

    InputType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    /**
     * @param inputType is the input type
     * @return whether the input type is a data manipulation language statement (input that makes changes to
     * the system data in some form)
     */
    public boolean isDMLStatement(InputType inputType) {
        return inputType != QUERY && inputType != UNKNOWN;
    }
}