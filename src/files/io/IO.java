package files.io;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Handles IO operations on the files used for this application.
 */
public final class IO {

    // can't instantiate me!
    private IO() {}

    // read operations -------------------------------------------------------------------------------------------------

    public static String readCurrentData(FileType.CurrentData currentDataFileType) {
        return read(currentDataFileType.getPath());
    }

    public static String readCurrentTableData(FileType.CurrentTableData currentTableDataFileType, String tableName) {
        return read(currentTableDataFileType.getPath().resolve(tableName));
    }

    public static String readOriginalData(FileType.OriginalData originalDataFileType) {
        return read(originalDataFileType.getPath());
    }

    public static String readOriginalTableData(FileType.OriginalTableData originalTableDataFileType, String tableName) {
        return read(originalTableDataFileType.getPath().resolve(tableName));
    }

    public static Image readAsset(FileType.Asset assetFileType) {
        return new Image(assetFileType.getPath());
    }

    public static String readCSS(FileType.CSS cssFileType) {
        return cssFileType.getPath();
    }

    private static String read(Path path) {

        StringBuilder data = new StringBuilder();

        try {

            File file = path.toFile();
            Scanner scanner = new Scanner(file);

            while(scanner.hasNext()) {
                data.append(scanner.nextLine()).append("\n");
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        return data.toString();
    }

    // write operations ------------------------------------------------------------------------------------------------

    // only allowed to overwrite current data, not original data
    public static void writeCurrentData(String data, FileType.CurrentData currentDataFileType) {
        write(data, currentDataFileType.getPath());
    }

    public static void writeCurrentTableData(String data, FileType.CurrentTableData currentTableDataFileType,
                                             String tableName) {
        write(data, currentTableDataFileType.getPath().resolve(tableName + ".txt"));
    }

    private static void write(String data, Path path) {

        try {

            File file = path.toFile();
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}