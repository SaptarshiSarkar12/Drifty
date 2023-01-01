package singleton;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class deals with creating Log files for Drifty.
 */
public class CreateLogs {
    static DateFormat dateFormat;
    static boolean isLogEmpty;
    static Path filePath;
    static Calendar calendarObject = Calendar.getInstance();

    /**
     * This is the constructor used to initialise the variables in this class.
     *
     * @param logFileName Filename of the Log file.
     */
    public CreateLogs(String logFileName) {
        filePath = FileSystems.getDefault().getPath(logFileName);
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    }

    /**
     * This function clears the contents of the previous log file.
     */
    private static void clearLog() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Drifty_CLI_LOG.log", false))))) {
            isLogEmpty = true;
            out.write("");
        } catch (IOException e) {
            System.out.println("Failed to clear Log contents !");
//            Drifty_CLI.logger.log("ERROR", "Failed to clear Log contents !");
        }
    }

    /**
     * This function actually writes the entries to the log file.
     *
     * @param type Type of the Log (acceptable values - INFO, WARN, ERROR).
     * @param msg  Log message.
     */
    public void log(String type, String msg) {
        String dateAndTime = dateFormat.format(calendarObject.getTime());
        if (!isLogEmpty) {
            clearLog();
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Drifty_CLI_LOG.log", true))))) {
            isLogEmpty = true;
            out.println(dateAndTime + " " + type.toUpperCase() + " - " + msg);
        } catch (IOException e) {
            System.out.println("Failed to create log : " + msg);
        }
    }
}
