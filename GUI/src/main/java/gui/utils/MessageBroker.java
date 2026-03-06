package gui.utils;

import settings.AppSettings;
import javafx.scene.paint.Color;
import properties.MessageCategory;
import properties.MessageType;
import ui.ConfirmationDialog;
import ui.UIController;

import java.util.Objects;

import static gui.support.Colors.*;
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
            case ERROR -> "Dark".equals(AppSettings.getGuiTheme()) ? BRIGHT_RED : DARK_RED;
            case INFO -> GREEN;
            default -> YELLOW;
        };
        switch (messageCategory) {
            case LINK -> Objects.requireNonNull(ui).setLinkOutput(color, message);
            case FILENAME -> Objects.requireNonNull(ui).setFilenameOutput(color, message);
            case DIRECTORY -> Objects.requireNonNull(ui).setDirOutput(color, message);
            case DOWNLOAD -> Objects.requireNonNull(ui).setDownloadOutput(color, message);
            case UPDATE -> {
                if (MessageType.ERROR.equals(messageType)) {
                    new ConfirmationDialog("Update Failed", message, true, true).getResponse();
                }
            }
            default -> {
                if (!message.isEmpty()) {
                    logger.log(messageType, message);
                }
            }
        }
    }
}
