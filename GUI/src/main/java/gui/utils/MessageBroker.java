package gui.utils;

import javafx.scene.paint.Color;
import properties.MessageCategory;
import properties.MessageType;
import ui.UIController;

import java.util.Objects;

import static gui.support.Colors.RED;
import static gui.support.Colors.GREEN;
import static gui.support.Colors.YELLOW;
import static properties.MessageCategory.LOG;

public class MessageBroker extends utils.MessageBroker {
    public MessageBroker() {
        super();
    }

    @Override
    protected void sendMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        UIController ui;
        if (!messageCategory.equals(LOG)) {
            ui = UIController.INSTANCE;
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
