package Enums;

/**
 * This class provides the possible types of messages that will be sent to the Message Broker
 */
public enum MessageType {
    INFORMATION, WARNING, ERROR;

    public String string() {
        return switch(this) {
            case INFORMATION -> "INFORMATION";
            case WARNING -> "WARNING";
            case ERROR -> "ERROR";
        };
    }
}
