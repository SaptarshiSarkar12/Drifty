package Updater;
import static Enums.MessageType.INFO;
import Utils.Logger;
import static Utils.DriftyConstants.FAILED_TO_CREATE_LOG;
import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class Updater {
    private static DateFormat dateFormat;
    private static Calendar calendarObject = Calendar.getInstance();
    public static void main(String[] args) {
        String oldFilePath = args[0];
        String newFilePath = args[1];
        String applicationType = args[2];
        // args[2] == CLI/GUI

        try {
            Path oldPath = Path.of(oldFilePath);
            Path newPath = Path.of(newFilePath);
            Path copy = Files.copy(newPath, oldPath, StandardCopyOption.REPLACE_EXISTING);
            String logFilename = "Drifty " + applicationType + ".log";
            String dateAndTime = dateFormat.format(calendarObject.getTime());
            try (PrintWriter logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename, true))))) {
                logWriter.println(dateAndTime + " " + INFO + " - " + "Drifty Updated Sucessfully!");
            } catch (IOException e) {
                System.out.println(FAILED_TO_CREATE_LOG + "Drifty Updated Sucessfully!");
            }

            ProcessBuilder processBuilder = new ProcessBuilder(oldPath.toString());
            processBuilder.start();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}





