package Updater;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static Enums.MessageType.INFO;

public class Updater {
    private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Calendar DEFAULT_CALENDAR = Calendar.getInstance();
    private static final String TIMESTAMP = TIMESTAMP_FORMAT.format(DEFAULT_CALENDAR.getTime());
    private static String logFilename;
    private static String applicationType;

    public static void main(String[] args) {
        String oldExecLocation = args[0];
        String latestExecLocation = args[1];
        applicationType = args[2];
        String parentDirectory = oldExecLocation.substring(0, oldExecLocation.lastIndexOf(File.separator));
        logFilename = Paths.get(parentDirectory, "Drifty " + applicationType + ".log").toAbsolutePath().toString();
        Path originalExecPath = Paths.get(oldExecLocation);
        Path latestExecPath = Paths.get(latestExecLocation);
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
            Files.delete(originalExecPath);
            try (InputStream inputStream = Files.newInputStream(latestExecPath)) {
                Files.copy(inputStream, originalExecPath);
            }
            if (Files.exists(originalExecPath)) {
                if (!Files.isExecutable(originalExecPath)) {
                    Files.setPosixFilePermissions(originalExecPath, Set.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
                }
                log("Drifty updated successfully!");
            } else {
                log("Drifty failed to update!");
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(originalExecPath.toFile());
            } else {
                Files.createFile(Paths.get(System.getProperty("user.dir"), "Drifty Update " + applicationType + ".log"));
            }
        } catch (IOException | InterruptedException e) {
            log("Drifty failed to update!");
        } finally {
            try {
                Files.delete(latestExecPath);
            } catch (IOException e) {
                log("Failed to delete the latest Drifty executable!");
            }
        }
    }

    private static void log(String message) {
        try (PrintWriter logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename, true))))) {
            logWriter.println(TIMESTAMP + " " + INFO + " - " + message);
        } catch (IOException e) {
            System.out.println("Failed to create log : " + "\"" + message + "\"");
        }
    }
}





