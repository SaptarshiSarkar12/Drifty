package Utils;

import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;
import GUI.Forms.FormLogic;
import javafx.scene.paint.Color;

import java.io.PrintStream;

public class MessageBroker {
    Logger logger;
    PrintStream output;
    public static final Color GREEN = Color.rgb(0, 255, 0);
    public static final Color TEAL = Color.rgb(0, 255, 255);
    public static final Color RED = Color.rgb(157, 0, 0);
    public static final Color PURPLE = Color.rgb(125, 0, 75);
    public static final Color BLACK = Color.rgb(0, 0, 0);
    public static final Color YELLOW = Color.rgb(255, 255, 0);

    public MessageBroker(PrintStream consoleOutput) {
        output = consoleOutput;
        logger = Logger.getInstance();
    }

    public MessageBroker() {
        logger = Logger.getInstance();
    }

    public void sendMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        if (Mode.isCLI()) {
            if (!messageCategory.equals(MessageCategory.LOG)) {
                output.println(message);
            }
            logger.log(messageType, message);
        } else if (Mode.isGUI()) {
            if (!message.isEmpty()) {
                logger.log(messageType, message);
            }
            Color color;
            if (messageType.equals(MessageType.ERROR)) {
                color = RED;
            } else if (messageType.equals(MessageType.INFO)) {
                color = GREEN;
            } else {
                color = YELLOW;
            }
            if (messageCategory.equals(MessageCategory.LINK)) {
                FormLogic.INSTANCE.setLinkOutput(color, message);
            } else if (messageCategory.equals(MessageCategory.FILENAME)) {
                FormLogic.INSTANCE.setFilenameOutput(color, message);
            } else if (messageCategory.equals(MessageCategory.DIRECTORY)) {
                FormLogic.INSTANCE.setDirOutput(color, message);
            } else if (messageCategory.equals(MessageCategory.DOWNLOAD)) {
                FormLogic.INSTANCE.setDownloadOutput(color, message);
            }
        }
    }
}
