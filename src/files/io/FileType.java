package files.io;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Used in conjunction with the IO class for reading and writing files. All files and their
 * path locations are in this class. Set up in a goofy way, but makes things clearer when I
 * have to inevitably come back to this and make changes.
 */
public final class FileType {

    // can't instantiate me!
    private FileType() {}

    public enum CurrentData {

        CURRENT_OPTIONS(Paths.get("options", "Options.txt")),
        CURRENT_USERS(Paths.get("users", "Users.txt")),
        CURRENT_TABLES(Paths.get("tables", "Tables.txt"));

        private final Path path;

        CurrentData(Path path) {
            this.path = Paths.get("src", "files", "systemdata", "current").resolve(path);
        }

        public Path getPath() {
            return path;
        }
    }

    public enum CurrentTableData {

        CURRENT_TABLE_DATA(Paths.get("src", "files", "systemdata", "current", "tables", "tabledata"));

        private final Path path;

        CurrentTableData(Path path) {
            this.path = path;
        }

        public Path getPath() {
            return path;
        }
    }

    public enum OriginalData {

        ORIGINAL_OPTIONS(Paths.get("options", "Options.txt")),
        ORIGINAL_USERS(Paths.get("users", "Users.txt")),
        ORIGINAL_TABLES(Paths.get("tables", "Tables.txt"));

        private final Path path;

        OriginalData(Path path) {
            this.path = Paths.get("src", "files", "systemdata", "original").resolve(path);
        }

        public Path getPath() {
            return path;
        }
    }

    public enum OriginalTableData {

        ORIGINAL_TABLE_DATA(Paths.get("src", "files", "systemdata", "original", "tables", "tabledata"));

        private final Path path;

        OriginalTableData(Path path) {
            this.path = path;
        }

        public Path getPath() {
            return path;
        }
    }

    public enum Asset {

        // help screen assets
        ALTER_TABLE_DIAGRAM("helpscreen/AlterTableDiagram.png"),
        BUILD_FILE_STRUCTURE_DIAGRAM("helpscreen/BuildFileStructureDiagram.png"),
        CREATE_TABLE_DIAGRAM("helpscreen/CreateTableDiagram.png"),
        DELETE_DIAGRAM("helpscreen/DeleteDiagram.png"),
        DROP_TABLE_DIAGRAM("helpscreen/DropTableDiagram.png"),
        GRANT_DIAGRAM("helpscreen/GrantDiagram.png"),
        INSERT_DIAGRAM("helpscreen/InsertDiagram.png"),
        QUERY_DIAGRAM("helpscreen/QueryDiagram.png"),
        REMOVE_FILE_STRUCTURE_DIAGRAM("helpscreen/RemoveFileStructureDiagram.png"),
        REVOKE_DIAGRAM("helpscreen/RevokeDiagram.png"),
        SCHEMA_DIAGRAM("helpscreen/SchemaDiagram.png"),
        UPDATE_DIAGRAM("helpscreen/UpdateDiagram.png"),

        // options screen assets
        MINUS_IMAGE("optionsscreen/Minus.png"),
        MOON_IMAGE("optionsscreen/Moon.png"),
        PLUS_IMAGE("optionsscreen/Plus.png"),
        QUESTION_MARK("optionsscreen/QuestionMark.png"),
        REFRESH_IMAGE("optionsscreen/Refresh.png"),
        SUN_IMAGE("optionsscreen/Sun.png"),

        // tables screen assets
        FOREIGN_KEY_IMAGE("tablesscreen/ForeignKey.png"),
        NO_KEY_IMAGE("tablesscreen/NoKey.png"),
        PRIMARY_KEY_IMAGE("tablesscreen/PrimaryKey.png"),

        // terminal screen assets
        PLAY_IMAGE("terminalscreen/Play.png"),
        X_IMAGE("terminalscreen/X.png"),
        TREE_IMAGE("terminalscreen/Tree.png"),
        DOLLAR_SIGN_IMAGE("terminalscreen/DollarSign.png"),
        FOLDER_IMAGE("terminalscreen/Folder.png"),

        // for query tree, a part of terminal screen
        RIGHT_ARROW("terminalscreen/querytreewindow/RightArrow.png"),
        LEFT_ARROW("terminalscreen/querytreewindow/LeftArrow.png");

        private final String path;

        Asset(String path) {
            this.path = "files/assets/" + path;
        }

        public String getPath() {
            return path;
        }
    }

    public enum CSS {

        BUTTON_STYLE("ButtonStyle.css"),
        CHOICE_BOX_STYLE("ChoiceBoxStyle.css"),
        DARK_MODE("DarkMode.css"),
        LIGHT_MODE("LightMode.css"),
        SCROLL_PANE_STYLE("ScrollPaneStyle.css"),
        TEXT_AREA_STYLE("TextArea.css");

        private final String path;

        CSS(String path) {
            this.path = "files/css/" + path;
        }

        public String getPath() {
            return path;
        }
    }
}