package utils;

import properties.MessageCategory;
import properties.MessageType;

import java.io.PrintStream;

import static properties.MessageCategory.LOG;

public class MessageBroker {
    protected Logger logger;
    protected PrintStream output = new PrintStream(System.out);
    protected boolean endWithNewLine = true;

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
        sendMessage(message, MessageType.INFO, LOG);
    }

    public void msgLogError(String message) {
        sendMessage(message, MessageType.ERROR, LOG);
    }

    public void msgLogWarning(String message) {
        sendMessage(message, MessageType.WARN, LOG);
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

    public void msgError(String message, MessageCategory messageCategory) {
        sendMessage(message, MessageType.ERROR, messageCategory);
    }

    public void msgUpdateError(String message) {
        sendMessage(message, MessageType.ERROR, MessageCategory.UPDATE);
    }

    public void msgUpdateInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.UPDATE);
    }

    protected void sendMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        logger.log(messageType, message);
        message = switch (messageType) {
            case INFO -> messageCategory == MessageCategory.INPUT || messageCategory == MessageCategory.UPDATE ? "\033[94m" + message + "\033[0m" : "\033[92m" + message + "\033[0m";
            case WARN -> "\033[93m" + message + "\033[0m";
            case ERROR -> "\033[91m" + message + "\033[0m";
        };
        if (!messageCategory.equals(LOG)) {
            if (endWithNewLine) {
                output.println(message);
            }else {
                output.print(message);
                this.endWithNewLine = true; // Reset to default
            }
        }
    }
}