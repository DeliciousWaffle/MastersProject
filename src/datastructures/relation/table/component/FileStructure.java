package datastructures.relation.table.component;

public enum FileStructure {

    SECONDARY_B_TREE, CLUSTERED_B_TREE, HASH_TABLE, CLUSTERED_FILE, NONE, UNKNOWN;

    public static FileStructure convertToFileStructure(String toConvert) {
        toConvert = toConvert.toUpperCase();
        switch(toConvert) {
            case "SECONDARY BTREE":
                return SECONDARY_B_TREE;
            case "CLUSTERED BTREE":
                return CLUSTERED_B_TREE;
            case "HASH TABLE":
                return HASH_TABLE;
            case "CLUSTERED FILE":
                return CLUSTERED_FILE;
            case "NONE":
                return NONE;
            default:
                return UNKNOWN;
        }
    }
}
