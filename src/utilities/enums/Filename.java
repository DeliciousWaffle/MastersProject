package utilities.enums;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum Filename {

    OPTIONS         (Paths.get("src", "files", "options", "Options.txt")),
    USERS           (Paths.get("src", "files", "users", "Users.txt")),
    TABLES          (Paths.get("src", "files", "tables", "Tables.txt")),
    TABLE_DATA      (Paths.get("src", "files", "tables", "data")),
    TEST_OPTIONS    (Paths.get("src", "files", "options", "TestOptions.txt")),
    TEST_USERS      (Paths.get("src", "files", "users", "TestUsers.txt")),
    TEST_TABLES     (Paths.get("src", "files", "tables", "TestTables.txt")),
    TEST_TABLE_DATA (Paths.get("src", "files", "tables", "data"));

    private Path path;

    Filename(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
