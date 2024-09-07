package utils;

import init.Environment;
import properties.MessageType;
import properties.Mode;

import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static support.Constants.FAILED_TO_CLEAR_LOG;
import static support.Constants.FAILED_TO_CREATE_LOG;

public final class Logger {
    boolean isLogEmpty;
    private static Logger cliLoggerInstance;
    private static Logger guiLoggerInstance;
    private final DateFormat dateFormat;
    private final Calendar calendarObject = Calendar.getInstance();
    private final String logFilename;
    private final File logFile;

    private Logger() {
        if (Mode.isCLI()) {
            logFilename = "Drifty CLI.log";
        } else {
            logFilename = "Drifty GUI.log";
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logFile = determineLogFile();
    }

    private File determineLogFile() {
        if (!Environment.isAdministrator()) {
            try {
                return Files.createTempFile(logFilename.split("\\.")[0], ".log").toFile();
            } catch (IOException e) {
                System.err.println(FAILED_TO_CREATE_LOG + logFilename);
            }
        }
        return new File(logFilename);
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
        try (PrintWriter logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, false))))) {
            isLogEmpty = true;
            logWriter.write("");
        } catch (IOException e) {
            System.err.println(FAILED_TO_CLEAR_LOG);
        }
    }

    public void log(MessageType messageType, String logMessage) {
        String dateAndTime = dateFormat.format(calendarObject.getTime());
        if (!isLogEmpty) {
            clearLog();
        }
        try (PrintWriter logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true))))) {
            isLogEmpty = true;
            logWriter.println(dateAndTime + " " + messageType.toString() + " - " + logMessage);
        } catch (IOException e) {
            System.err.println(FAILED_TO_CREATE_LOG + logMessage);
        }
    }
}
