package Utils;

import Enums.Mode;
import Enums.Type;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static Utils.DriftyConstants.FAILED_TO_CLEAR_LOG;
import static Utils.DriftyConstants.FAILED_TO_CREATE_LOG;

/**
 * This class deals with creating Log files for Drifty.
 */
public class Logger {
    private static Logger CLILoggerInstance;
    private static Logger GUILoggerInstance;
    Path filePath;
    DateFormat dateFormat;
    boolean isLogEmpty;
    Calendar calendarObject = Calendar.getInstance();
    String logFilename;

    /**
     * This is the constructor used to initialise the variables in this class.
     */
    private Logger() {
        if (Mode.CLI()) {
            logFilename = "DriftyCLI.log";
        }
        else {
            logFilename = "DriftyGUI.log";
        }
        filePath = FileSystems.getDefault().getPath(logFilename);
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    }

    public static Logger getInstance() {
        if (Mode.CLI()) {
            if (CLILoggerInstance != null) {
                return CLILoggerInstance;
            }
            CLILoggerInstance = new Logger();
            return CLILoggerInstance;
        }
        else {
            if (GUILoggerInstance != null) {
                return GUILoggerInstance;
            }
            GUILoggerInstance = new Logger();
            return GUILoggerInstance;
        }
    }

    /**
     * This function clears the contents of the previous log file.
     */
    private void clearLog() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename, false))))) {
            isLogEmpty = true;
            out.write("");
        } catch (IOException e) {
            System.out.println(FAILED_TO_CLEAR_LOG);
        }
    }

    /**
     * This function actually writes the output messages to the log file.
     *
     * @param type Type of the Log (acceptable values - INFO, WARN, ERROR).
     * @param msg  Log message.
     */
    public void log(Type type, String msg) {
        String dateAndTime = dateFormat.format(calendarObject.getTime());
        if (!isLogEmpty) {
            clearLog();
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename, true))))) {
            isLogEmpty = true;
            out.println(dateAndTime + " " + type.string() + " - " + msg);
        } catch (IOException e) {
            System.out.println(FAILED_TO_CREATE_LOG + msg);
        }
    }
}
