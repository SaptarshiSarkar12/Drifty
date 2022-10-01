import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateLogs {
    static String fName;
    static String clsName;
    static DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    static Calendar calObj = Calendar.getInstance();
    public CreateLogs(String logFileName, String className){
        fName = logFileName;
        clsName = className;
    }

    public void log(String type, String msg){
        String dateAndTime = df.format(calObj.getTime());
        System.out.println(dateAndTime);
        Path fileName = Path.of(fName);
        try {
            Files.writeString(fileName, dateAndTime + type.toUpperCase() + " - " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
