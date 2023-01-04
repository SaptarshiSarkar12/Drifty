package singleton;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static constants.DriftyConstants.*;

/**
 * This class deals with creating Log files for Drifty.
 */
public class CreateLogs {
    private static CreateLogs createLogsInstance;
    Path filePath;
    DateFormat dateFormat;
    boolean isLogEmpty;
    Calendar calendarObject = Calendar.getInstance();

    /**
     * This is the constructor used to initialise the variables in this class.
     */
    private CreateLogs() {
        filePath = FileSystems.getDefault().getPath(DRIFTY_CLI_LOG);
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    }

    public static CreateLogs getInstance() {
        if (createLogsInstance != null) return createLogsInstance;
        createLogsInstance = new CreateLogs();
        return createLogsInstance;
    }

    /**
     * This function clears the contents of the previous log file.
     */
    private void clearLog() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Drifty_CLI_LOG.log", false))))) {
            isLogEmpty = true;
            out.write("");
        } catch (IOException e) {
            System.out.println(FAILED_TO_CLEAR_LOG);
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
            System.out.println(FAILED_TO_CREATE_LOG + msg);
        }
    }
}
