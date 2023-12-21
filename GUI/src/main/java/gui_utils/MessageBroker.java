package gui_utils;

import javafx.scene.paint.Color;
import properties.MessageCategory;
import properties.MessageType;
import ui.FormsController;

import java.util.Objects;

import static gui_support.Constants.*;
import static properties.MessageCategory.LOG;

public class MessageBroker extends utils.MessageBroker {
    public MessageBroker() {
        super();
    }

    @Override
    protected void sendMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        FormsController ui;
        if (!messageCategory.equals(LOG)) {
            ui = FormsController.INSTANCE;
        } else {
            ui = null;
        }
        Color color = switch (messageType) {
            case ERROR -> RED;
            case INFO -> GREEN;
            default -> YELLOW;
        };
        switch (messageCategory) {
            case LINK -> Objects.requireNonNull(ui).setLinkOutput(color, message);
            case FILENAME -> Objects.requireNonNull(ui).setFilenameOutput(color, message);
            case DIRECTORY -> Objects.requireNonNull(ui).setDirOutput(color, message);
            case DOWNLOAD -> Objects.requireNonNull(ui).setDownloadOutput(color, message);
            default -> {
                if (!message.isEmpty()) {
                    logger.log(messageType, message);
                }
            }
        }
    }
}
