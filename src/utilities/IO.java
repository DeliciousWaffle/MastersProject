package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

//TODO change TestUsers.txt to Users.txt

public class IO {

    /**
     * Returns a completely unformatted string of data from the supplied filename.
     * @param filename the file to read from
     * @return unformatted data from that file
     */
    public static String readData(String filename) {

        StringBuilder data = new StringBuilder();

        try {

            File userFile = new File(getPath("\\users\\TestUsers.txt"));
            Scanner scanner = new Scanner(userFile);

            while(scanner.hasNext()) {
                data.append(scanner.next());
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        return data.toString();
    }

    /**
     * Writes data to the supplied filename.
     * @param data is the data to write out
     */
    public static void writeData(String data, String filename) {

        try {

            FileWriter fileWriter = new FileWriter(getPath(filename));

            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private static String getPath(String endingStuff) {

        // hack: gets the absolute path to users.txt
        File path = new File(".");
        String absPath = path.getAbsolutePath();
        absPath = absPath.substring(0, absPath.length() - 2); // removes "\."

        return absPath + "\\src\\files\\users\\" + endingStuff;
    }
}

