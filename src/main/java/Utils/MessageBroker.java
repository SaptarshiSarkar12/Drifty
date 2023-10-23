package Utils;

import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;
import GUI.Forms.FormsController;
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

    public void msgDownloadInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.DOWNLOAD);
    }

    public void msgDownloadError(String message) {
        sendMessage(message, MessageType.ERROR, MessageCategory.DOWNLOAD);
    }

    public void msgLinkInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.LINK);
    }

    public void msgLinkError(String message) {
        sendMessage(message, MessageType.ERROR, MessageCategory.LINK);
    }

    public void msgLogInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.LOG);
    }

    public void msgLogError(String message) {
        sendMessage(message, MessageType.ERROR, MessageCategory.LOG);
    }

    public void msgLogWarning(String message) {
        sendMessage(message, MessageType.WARN, MessageCategory.LOG);
    }

    public void msgInitInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.INITIALIZATION);
    }

    public void msgInitError(String message) {
        sendMessage(message, MessageType.ERROR, MessageCategory.INITIALIZATION);
    }

    public void msgDirInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.DIRECTORY);
    }

    public void msgDirError(String message) {
        sendMessage(message, MessageType.ERROR, MessageCategory.DIRECTORY);
    }

    public void msgFilenameInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.FILENAME);
    }

    public void msgFilenameError(String message) {
        sendMessage(message, MessageType.ERROR, MessageCategory.FILENAME);
    }

    public void msgBatchInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.BATCH);
    }

    public void msgBatchError(String message) {
        sendMessage(message, MessageType.ERROR, MessageCategory.BATCH);
    }

    public void msgStyleInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.STYLE);
    }

    private void sendMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        if (Mode.isCLI()) {
            if (!messageCategory.equals(LOG)) {
                output.println(message);
            }
            logger.log(messageType, message);
        } else if (Mode.isGUI()) {
            FormsController ui;
            if (!messageCategory.equals(LOG)) {
                ui = FormsController.INSTANCE;
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
                default -> logger.log(messageType, message);
            }
        }
    }
}
