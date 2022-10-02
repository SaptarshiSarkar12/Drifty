import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateLogs {
    static String clsName;
    static DateFormat df;
    static boolean isLogEmpty;
    static Path filePath;
    static Calendar calObj = Calendar.getInstance();
    public CreateLogs(String logFileName, String className){
        filePath = FileSystems.getDefault().getPath(logFileName);
        clsName = className;
        df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    }

    public void log(String type, String msg){
        String dateAndTime = df.format(calObj.getTime());
        if (!isLogEmpty){
            clearLog();
        }
        try {
            isLogEmpty = true;
//            Files.writeString(filePath, dateAndTime + " " + type.toUpperCase() + " - " + msg + "\n", StandardOpenOption.APPEND);
            BufferedWriter writer = Files.newBufferedWriter(filePath);
            writer.write(dateAndTime + " " + type.toUpperCase() + " - " + msg + "\n");
        } catch (IOException e) {
            System.out.println("Failed to create log : " + msg);
        }
    }

    private static void clearLog(){
//        try {
//            Files.writeString(filePath, "");
//        } catch (IOException e) {
//            System.out.println("Failed to clear Log contents !");
//            Drifty_CLI.logger.log("ERROR", "Failed to clear Log contents !");
//        }
    }
}
