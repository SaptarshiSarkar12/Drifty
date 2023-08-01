package Enums;

/**
 * Used by MessageBroker
 */

public enum Type {
    INFORMATION, WARNING, ERROR;

    public String string() {
        return switch(this) {
            case INFORMATION -> "INFORMATION";
            case WARNING -> "WARNING";
            case ERROR -> "ERROR";
        };
    }
}
