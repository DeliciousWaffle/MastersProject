package file.io;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum Filename {

    OPTIONS         (Paths.get("src", "file", "options", "Options.txt")),
    USERS           (Paths.get("src", "file", "users", "Users.txt")),
    TABLES          (Paths.get("src", "file", "tables", "Tables.txt")),
    TABLE_DATA      (Paths.get("src", "file", "tables", "data")),
    TEST_OPTIONS    (Paths.get("src", "file", "options", "TestOptions.txt")),
    TEST_USERS      (Paths.get("src", "file", "users", "Users.txt")),
    TEST_TABLES     (Paths.get("src", "file", "tables", "Tables.txt")),
    TEST_TABLE_DATA (Paths.get("src", "file", "tables", "data"));

    private Path path;

    Filename(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
