package Utils;

import Enums.MessageType;
import Enums.Mode;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static Utils.DriftyConstants.FAILED_TO_CLEAR_LOG;
import static Utils.DriftyConstants.FAILED_TO_CREATE_LOG;

/**
 * This class deals with creating Log files for Drifty.
 */
public final class Logger {
    boolean isLogEmpty;
    private static Logger cliLoggerInstance;
    private static Logger guiLoggerInstance;
    private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Calendar calendarObject = Calendar.getInstance();
    private final String logFilename;

    private Logger() {
        if (Mode.isCLI()) {
            logFilename = "Drifty CLI.log";
        } else {
            logFilename = "Drifty GUI.log";
        }
    }

    public static Logger getInstance() {
        if (Mode.isCLI()) {
            if (cliLoggerInstance != null) {
                return cliLoggerInstance;
            }
            cliLoggerInstance = new Logger();
            return cliLoggerInstance;
        } else {
            if (guiLoggerInstance != null) {
                return guiLoggerInstance;
            }
            guiLoggerInstance = new Logger();
            return guiLoggerInstance;
        }
    }

    private void clearLog() {
        try (PrintWriter logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename, false))))) {
            isLogEmpty = true;
            logWriter.write("");
        } catch (IOException e) {
            System.out.println(FAILED_TO_CLEAR_LOG);
        }
    }

    public void log(MessageType messageType, String logMessage) {
        String currentTimeStamp = TIMESTAMP_FORMAT.format(calendarObject.getTime());
        if (!isLogEmpty) {
            clearLog();
        }
        try (PrintWriter logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename, true))))) {
            isLogEmpty = true;
            logWriter.println(currentTimeStamp + " " + messageType.toString() + " - " + logMessage);
        } catch (IOException e) {
            System.out.println(FAILED_TO_CREATE_LOG + "\" " + logMessage + " \"");
        }
    }
}
