package Utils;

import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;
import GUI.Forms.FormLogic;
import javafx.scene.paint.Color;

import java.io.PrintStream;

import static Enums.MessageCategory.LOG;
import static Enums.Colors.*;

public class MessageBroker {
    private final Logger logger;
    private PrintStream output = new PrintStream(System.out);

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
            FormLogic ui;
            if (!messageCategory.equals(LOG)) {
                ui = FormLogic.INSTANCE;
            } else {
                ui = null;
            }
            if (!message.isEmpty()) {
                logger.log(messageType, message);
            }
            Color color = switch (messageType) {
                case ERROR -> RED;
                case INFO -> GREEN;
                default -> YELLOW;
            };
            switch (messageCategory) {
                case LINK -> ui.setLinkOutput(color, message);
                case FILENAME -> ui.setFilenameOutput(color, message);
                case DIRECTORY -> ui.setDirOutput(color, message);
                case DOWNLOAD -> ui.setDownloadOutput(color, message);
            }
        }
    }
}
