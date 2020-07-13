package datastructure.relation.table.component;

public enum FileStructure {

    SECONDARY_B_TREE, CLUSTERED_B_TREE, HASH_TABLE, CLUSTERED_FILE, NONE, UNKNOWN;

    public static FileStructure convertToFileStructure(String toConvert) {
        switch(toConvert) {
            case "SECONDARY_B_TREE":
                return SECONDARY_B_TREE;
            case "CLUSTERED_B_TREE":
                return CLUSTERED_B_TREE;
            case "HASH_TABLE":
                return HASH_TABLE;
            case "CLUSTERED_FILE":
                return CLUSTERED_FILE;
            case "NONE":
                return NONE;
            default:
                return UNKNOWN;
        }
    }
}
