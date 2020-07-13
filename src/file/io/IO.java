package file.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Handles IO operations on the files used for this application.
 */
public class IO {

    /**
     * Returns a completely unformatted string of data from the supplied filename.
     * The filename provided can't be table data.
     * @param filename the file to read from that is NOT table data
     * @return unformatted data from that file
     */
    public static String read(Filename filename) {

        // prevent myself from being dumb
        if(filename == Filename.TABLE_DATA || filename == Filename.TEST_TABLE_DATA) {
            System.out.println("In IO.read()");
            System.out.println("Used wrong method!");
            return "null";
        }

        StringBuilder data = new StringBuilder();

        try {

            File file = filename.getPath().toFile();
            Scanner scanner = new Scanner(file);

            while(scanner.hasNext()) {
                data.append(scanner.nextLine()).append("\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "null";
        }

        return data.toString();
    }

    /**
     * Returns a completely unformatted string of data from the supplied filename.
     * The filename provided must be table data.
     * @param filename is the file to read from the IS table data
     * @param tableDataName is the name of the table data (format: <TableDataName>.txt)
     */
    public static String readTableData(Filename filename, String tableDataName) {

        // prevent myself from being dumb
        if(! (filename == Filename.TABLE_DATA || filename == Filename.TEST_TABLE_DATA)) {
            System.out.println("In IO.readTableData()");
            System.out.println("Used wrong method!");
            return "null";
        }

        StringBuilder data = new StringBuilder();

        try {

            Path joinedPath = filename.getPath().resolve(tableDataName);
            File file = joinedPath.toFile();
            Scanner scanner = new Scanner(file);

            while(scanner.hasNext()) {
                data.append(scanner.nextLine()).append("\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "null";
        }

        return data.toString();
    }

    /**
     * Writes a string of data to the supplied filename.
     * @param filename is the name of the file that is NOT table data
     * @param data is the data to write out
     */
    public static void write(Filename filename, String data) {

        // prevent myself from being dumb
        if(filename == Filename.TABLE_DATA || filename == Filename.TEST_TABLE_DATA) {
            System.out.println("In IO.write()");
            System.out.println("Used wrong method!");
            return;
        }

        try {

            File file = filename.getPath().toFile();
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a string of data to the supplied table data name.
     * @param filename is the name of the file that IS table data
     * @param tableDataName is the name of the table data file
     * @param data is the data to write out
     */
    public static void writeTableData(Filename filename, String tableDataName, String data) {

        // prevent myself from being dumb
        if(! (filename == Filename.TABLE_DATA || filename == Filename.TEST_TABLE_DATA)) {
            System.out.println("In IO.writeTableData()");
            System.out.println("Used wrong method!");
            return;
        }

        try {

            Path combined = filename.getPath().resolve(tableDataName);
            File file = combined.toFile();
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}