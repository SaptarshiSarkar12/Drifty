package Utils;

import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;

import java.io.PrintStream;

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

    public void sendMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        if (Mode.isCLI()) {
            if (!messageCategory.equals(MessageCategory.LOG))
                output.println(message);
            logger.log(messageType, message);
        } else if (Mode.guiLoaded()) {
            logger.log(messageType, message);
        }
    }

    public PrintStream getOutput() {
        return output;
    }
}
