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
        DATABASE_SCHEMA_DIAGRAM("helpscreen/DatabaseSchema.png"),
        DELETE_DIAGRAM("helpscreen/DeleteDiagram.png"),
        DROP_TABLE_DIAGRAM("helpscreen/DropTableDiagram.png"),
        ER_DIAGRAM("helpscreen/ERDiagram.png"),
        GRANT_DIAGRAM("helpscreen/GrantDiagram.png"),
        INSERT_DIAGRAM("helpscreen/InsertDiagram.png"),
        QUERY_DIAGRAM("helpscreen/QueryDiagram.png"),
        REMOVE_FILE_STRUCTURE_DIAGRAM("helpscreen/RemoveFileStructureDiagram.png"),
        REVOKE_DIAGRAM("helpscreen/RevokeDiagram.png"),
        UPDATE_DIAGRAM("helpscreen/UpdateDiagram.png"),

        // options screen assets
        QUESTION_MARK("optionsscreen/QuestionMark.png"),
        REFRESH_IMAGE("optionsscreen/Refresh.png"),
        SAVE_IMAGE("optionsscreen/Save.png"),

        // terminal screen assets
        DOLLAR_SIGN_IMAGE("terminalscreen/DollarSign.png"),
        ERASER_IMAGE("terminalscreen/Eraser.png"),
        FOLDER_IMAGE("terminalscreen/Folder.png"),
        PLAY_IMAGE("terminalscreen/Play.png"),
        RESULT_SET_IMAGE("terminalscreen/ResultSet.png"),
        TREE_IMAGE("terminalscreen/Tree.png"),
        PI_IMAGE("terminalscreen/PI.png");

        private final String path;

        Asset(String path) {
            this.path = "files/images/" + path;
        }

        public String getPath() {
            return path;
        }
    }

    public enum CSS {

        // dark ui
        BUTTON_STYLE("ButtonStyle.css"),
        CHOICE_BOX_STYLE("ChoiceBoxStyle.css"),
        SCROLL_PANE_STYLE("ScrollPaneStyle.css"),
        TEXT_AREA_STYLE("TextAreaStyle.css");

        private final String path;

        CSS(String path) {
            this.path = "files/css/" + path;
        }

        public String getPath() {
            return path;
        }
    }
}