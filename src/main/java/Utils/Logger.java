package Utils;
import Enums.Mode;
import Enums.MessageType;
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

    private Logger() {
        if (Mode.isCLI()) {
            logFilename = "Drifty CLI.log";
        } else {
            logFilename = "Drifty GUI.log";
        }
        filePath = FileSystems.getDefault().getPath(logFilename);
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    }

    public static Logger getInstance() {
        if (Mode.isCLI()) {
            if (CLILoggerInstance != null) {
                return CLILoggerInstance;
            }
            CLILoggerInstance = new Logger();
            return CLILoggerInstance;
        } else {
            if (GUILoggerInstance != null) {
                return GUILoggerInstance;
            }
            GUILoggerInstance = new Logger();
            return GUILoggerInstance;
        }
    }

    private void clearLog() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename, false))))) {
            isLogEmpty = true;
            out.write("");
        } catch (IOException e) {
            System.out.println(FAILED_TO_CLEAR_LOG);
        }
    }

    public void log(MessageType messageType, String logMessage) {
        String dateAndTime = dateFormat.format(calendarObject.getTime());
        if (!isLogEmpty) {
            clearLog();
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename, true))))) {
            isLogEmpty = true;
            out.println(dateAndTime + " " + messageType.toString() + " - " + logMessage);
        } catch (IOException e) {
            System.out.println(FAILED_TO_CREATE_LOG + logMessage);
        }
    }
}
