package utilities;

import utilities.enums.FileName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles IO operations.
 * TODO change back to real files not test ones
 */
public class IO {

    /**
     * Returns a completely unformatted string of data from the supplied filename.
     * @param filename the file to read from
     * @return unformatted data from that file
     */
    public static String readData(FileName filename) {

        StringBuilder data = new StringBuilder();

        try {

            File userFile = new File(getAbsPath(filename));
            Scanner scanner = new Scanner(userFile);

            while(scanner.hasNext()) {
                data.append(scanner.nextLine()).append("\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return data.toString();
    }

    /**
     * Reads the data within the table and returns it in a string format.
     */
    public static String readTableData(String tableFileName) {

        StringBuilder data = new StringBuilder();

        try {

            File userFile = new File(getAbsPath(tableFileName));
            Scanner scanner = new Scanner(userFile);

            while(scanner.hasNext()) {
                data.append(scanner.nextLine()).append("\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return data.toString();
    }

    /**
     * Writes a string of data to the supplied filename.
     * @param data is the data to write out
     */
    public static void writeData(String data, FileName filename) {

        try {

            FileWriter fileWriter = new FileWriter(getAbsPath(filename));

            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param filename is the local filename
     * @return absolute path to that filename
     */
    private static String getAbsPath(FileName filename) {

        // hack: gets the absolute path to the supplied filename
        File foo = new File(".");
        String absPath = foo.getAbsolutePath();

        // removes "\."
        absPath = absPath.substring(0, absPath.length() - 2);

        String localPath = "";

        switch(filename) {
            case OPTIONS:
                localPath = "\\options\\Options.txt";
                break;
            case TABLES:
                localPath = "\\tables\\Tables.txt";
                break;
            case USERS:
                localPath = "\\users\\TestUsers.txt"; // TODO change back to Users.txt
                break;
        }

        return absPath + "\\src\\files\\" + localPath;
    }

    private static String getAbsPath(String filename) {

        // hack: gets the absolute path to the supplied filename
        File foo = new File(".");
        String absPath = foo.getAbsolutePath();

        // removes "\."
        absPath = absPath.substring(0, absPath.length() - 2);

        return absPath + "\\src\\files\\tables\\data\\" + filename;
    }
}

