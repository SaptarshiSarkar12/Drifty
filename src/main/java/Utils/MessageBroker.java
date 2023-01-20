package Utils;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.io.PrintStream;

public class MessageBroker {
    CreateLogs logger = CreateLogs.getInstance();
    String appType;
    Text link;
    Text dir;
    Text renameFile;
    Text download;
    PrintStream output;
    public MessageBroker(String applicationType, PrintStream consoleOutput){
        appType = applicationType;
        output = consoleOutput;
    }

    public MessageBroker(String applicationType, Text link, Text dir, Text download, Text renameFile){
        appType = applicationType;
        this.link = link;
        this.dir = dir;
        this.download = download;
        this.renameFile = renameFile;
    }

    public void sendMessage(String message, String messageType, String messageCategory){
        if (appType.equals("CLI")){
            output.println(message);
            logger.log(messageType, message);
        } else if (appType.equals("GUI")){
            Color color = Color.BLACK;
            if (messageType.equals(DriftyConstants.LOGGER_INFO)){
                color = Color.GREEN;
            } else if (messageType.equals(DriftyConstants.LOGGER_ERROR)) {
                color = Color.RED;
            } else if (messageType.equals(DriftyConstants.LOGGER_WARN)) {
                color = Color.YELLOW;
            } else {
                logger.log(DriftyConstants.LOGGER_ERROR, "Invalid message type provided to message broker!");
            }
            if (messageCategory.equals("link")){
                link.setText(message);
                link.setFill(color);
                logger.log(messageType, message);
            } else if (messageCategory.equals("directory")) {
                dir.setText(message);
                dir.setFill(color);
                logger.log(messageType, message);
            } else if (messageCategory.equals("download")) {
                download.setText(message);
                download.setFill(color);
                logger.log(messageType, message);
            } else if (messageCategory.equals("renameFile")) {
                renameFile.setText(message);
                renameFile.setFill(color);
                logger.log(messageType, message);
            } else {
                logger.log(DriftyConstants.LOGGER_ERROR, "Invalid message category provided to message broker!");
            }
        } else {
            logger.log(DriftyConstants.LOGGER_ERROR, "Invalid application type provided to message broker!");
        }
    }
}
