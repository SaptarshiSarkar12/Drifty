package GUI.experiment;

import GUI.Support.Folders;
import GUI.Support.Job;
import Preferences.AppSettings;
import Utils.Utility;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Worker;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static GUI.experiment.Constants.*;

public class FormLogic {

    private static final FormLogic INSTANCE = new FormLogic();
    private MainGridPane form;
    private static final BooleanProperty directoryExists = new SimpleBooleanProperty(false);
    private static final BooleanProperty downloadInProgress = new SimpleBooleanProperty(false);
    private static final BooleanProperty processingBatch = new SimpleBooleanProperty(false);
    private static final BooleanProperty linkValid = new SimpleBooleanProperty(false);
    private final String lineFeed = System.lineSeparator();
    private final Color green = GREEN;
    private final Color red = RED;
    private final Color orange = ORANGE;
    private final Folders folders;

    private FormLogic() {
        folders = AppSettings.get.folders();
    }

    public static void initLogic(MainGridPane pane) {
        INSTANCE.start(pane);
    }

    private void start(MainGridPane pane) {
        form = pane;
        setControlProperties();
        setControlActions();
    }

    private void setControlProperties() {
        form.tfDir.setText(folders.getDownloadFolder());
        directoryExists.setValue(new File(form.tfDir.getText()).exists());
        BooleanBinding disableStartButton = downloadInProgress.or(directoryExists.not());
        BooleanBinding disableInputs = downloadInProgress.not();
        BooleanBinding hasText = form.tfLink.textProperty().isEmpty().not().and(form.tfDir.textProperty().isEmpty().not().and(form.tfFilename.textProperty().isEmpty().not()));
        form.ivBtnStart.visibleProperty().bind(hasText);
        form.tfLink.editableProperty().bind(disableInputs);
        form.tfDir.editableProperty().bind(disableInputs);
        form.tfFilename.editableProperty().bind(disableInputs);
        form.ivBtnStart.disableProperty().bind(disableStartButton);
        Tooltip.install(form.cbAutoPaste, new Tooltip("When checked, will paste contents of clipboard into" + lineFeed + "Link field when switching back to this screen."));
        form.cbAutoPaste.setSelected(AppSettings.get.mainAutoPaste());
        form.cbAutoPaste.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.set.mainAutoPaste(newValue)));
        form.ivBtnStart.setOnMouseClicked(e -> new Thread(() -> {
/*
            if (confirmDownload()) {
                jobList.clear();
                jobList.add(new Job(tfLink.getText(), tfDir.getText(), tfFilename.getText()));
                batchDownloader();
            }
*/

        }).start());
        form.tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
 /*           if (!newValue.equals(oldValue)) {
                directoryExists.setValue(false);
                if (newValue.isEmpty()) {
                    setDirOutput(red, "Directory cannot be empty!");
                }

                else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        delayFolderSave(newValue, folder);
                        setDirOutput(green, "Directory exists!");
                        directoryExists.setValue(true);
                    }

                    else {
                        setDirOutput(red, "Directory does not exist or is not a directory!");
                    }

                }

            }
*/
        }));
        form.tfLink.textProperty().addListener(((observable, oldValue, newValue) -> verifyLink(oldValue, newValue)));
    }

    private void setControlActions() {
        form.ivBtnStart.setOnMouseClicked(e->downloadFiles());
    }

    private void getFilenames(String link) {
        Worker<LinkedList<Job>> worker = new GetFilename(link,form.tfDir.getText());

    }

    private void downloadFiles() {

    }

    private void verifyLink(String PreviousLink, String presentLink) {
        if (!PreviousLink.equals(presentLink)) {
            if (downloadInProgress.getValue().equals(false) && processingBatch.getValue().equals(false)) {
                setLinkOutput(green, "Validating link ...");
                linkValid.setValue(false);
                if (presentLink.contains(" ")) {
                    Platform.runLater(() -> setLinkOutput(red, "Link should not contain whitespace characters!"));
                }

                else if (!isURL(presentLink)) {
                    Platform.runLater(() -> setLinkOutput(red, "String is not a URL"));
                }

                else {
                    try {
                        Utility.isURLValid(presentLink);
                        Platform.runLater(() -> setLinkOutput(green, "Valid URL"));
                        linkValid.setValue(true);
                    } catch (Exception e) {
                        String errorMessage = e.getMessage();
                        Platform.runLater(() -> setLinkOutput(red, errorMessage));
                    }

                }

                if (linkValid.getValue().equals(true)) {
                    getFilenames(presentLink);
                }

            }

        }

    }

    private boolean isURL(String text) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.matches();
    }
    private void setLinkOutput(Color color, String message) {
        form.lblLinkOut.setTextFill(color);
        form.lblLinkOut.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setLinkOutput(green, ""));
            }).start();
        }

    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}


