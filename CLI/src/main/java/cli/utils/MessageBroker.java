package cli.utils;

import properties.MessageCategory;
import properties.MessageType;
import utils.Logger;

import java.io.PrintStream;

public class MessageBroker extends utils.MessageBroker {
    public MessageBroker(PrintStream consoleOutput) {
        output = consoleOutput;
        logger = Logger.getInstance();
    }

    public void msgStyleInfo(String message) {
        sendMessage(message, MessageType.INFO, MessageCategory.STYLE);
    }

    public void msgInputError(String message, boolean endWithNewLine) {
        this.endWithNewLine = endWithNewLine;
        sendMessage(message, MessageType.ERROR, MessageCategory.INPUT);
    }

    public void msgInputInfo(String message, boolean endWithNewLine) {
        this.endWithNewLine = endWithNewLine;
        sendMessage(message, MessageType.INFO, MessageCategory.INPUT);
    }

    public void msgHistoryWarning(String message, boolean endWithNewLine) {
        this.endWithNewLine = endWithNewLine;
        sendMessage(message, MessageType.WARN, MessageCategory.HISTORY);
    }
}
