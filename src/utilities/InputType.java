package utilities;

public enum InputType {

    QUERY(0), CREATE_TABLE(1), DROP_TABLE(2), ALTER_TABLE(3), INSERT(4), DELETE(5),
    UPDATE(6), GRANT(7), REVOKE(8), BUILD_SECONDARY_B_TREE(9), BUILD_CLUSTERED_B_TREE(10),
    BUILD_HASH_TABLE(11), BUILD_CLUSTERED_FILE(12), UNKNOWN(-1);

    private int code;

    InputType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
