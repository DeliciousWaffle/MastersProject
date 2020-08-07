package file.io;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum Filename {

    ORIGINAL_OPTIONS    (Paths.get("src", "file", "original", "options", "Options.txt")),
    ORIGINAL_USERS      (Paths.get("src", "file", "original", "user",    "Users.txt")),
    ORIGINAL_TABLES     (Paths.get("src", "file", "original", "table",   "Tables.txt")),
    ORIGINAL_TABLE_DATA (Paths.get("src", "file", "original", "table",   "tabledata")),
    CURRENT_OPTIONS     (Paths.get("src", "file", "current",  "option",  "TestOptions.txt")),
    CURRENT_USERS       (Paths.get("src", "file", "current",  "user",    "Users.txt")),
    CURRENT_TABLES      (Paths.get("src", "file", "current",  "table",   "Tables.txt")),
    CURRENT_TABLE_DATA  (Paths.get("src", "file", "current",  "table",   "tabledata"));

    private Path path;

    Filename(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}