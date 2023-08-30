package Utils;

import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;
import GUI.Forms.FormLogic;
import javafx.scene.paint.Color;

import java.io.PrintStream;

import static Enums.MessageCategory.LOG;
import static javafx.scene.paint.Color.*;

public class MessageBroker {
    Logger logger;
    PrintStream output;

    public MessageBroker(PrintStream consoleOutput) {
        output = consoleOutput;
        logger = Logger.getInstance();
    }

    public MessageBroker() {
        logger = Logger.getInstance();
    }

    public void logOnly(String message, MessageType messageType) {
        output.println(message);
        logger.log(messageType, message);
    }

    public void sendMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        if (Mode.isCLI()) {
            if (!messageCategory.equals(LOG)) {
                output.println(message);
            }
            logger.log(messageType, message);
        }
        else if (Mode.isGUI()) {
            if (!message.isEmpty()) {
                logger.log(messageType, message);
            }
            Color color = switch (messageType) {
                case ERROR -> RED;
                case INFO -> GREEN;
                default -> YELLOW;
            };
            switch (messageCategory) {
                case LINK -> FormLogic.INSTANCE.setLinkOutput(color, message);
                case FILENAME -> FormLogic.INSTANCE.setFilenameOutput(color, message);
                case DIRECTORY -> FormLogic.INSTANCE.setDirOutput(color, message);
                case DOWNLOAD -> FormLogic.INSTANCE.setDownloadOutput(color, message);
            }
        }
    }
}
