package ui;

import gui.init.Environment;
import settings.AppSettings;
import gui.support.Constants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import static utils.Utility.sleep;

public class ConfirmationDialog {
    enum State {
        YES_NO, OK, FILENAME
    }

    private static Scene scene;
    private final State state;
    private final String lf = System.lineSeparator();
    private static Button btnNo;
    private static Button btnOk;
    private static Button btnYes;
    private double width = 200;
    private double height = 150;
    private final String windowTitle;
    private final String msg;
    private boolean isUpdateError;
    private final GetConfirmationDialogResponse answer = new GetConfirmationDialogResponse();
    private Stage stage;
    private VBox vbox;
    private String filename = "";
    private TextField tfFilename;

    public ConfirmationDialog(String windowTitle, String message, boolean okOnly, boolean isUpdateError) {
        this.windowTitle = windowTitle;
        this.state = okOnly ? State.OK : State.YES_NO;
        this.isUpdateError = isUpdateError;
        if (isUpdateError) {
            this.msg = message + "\n\nPlease try again later or download the latest version from the below link :";
        }else {
            this.msg = message;
        }
        finish();
    }

    public ConfirmationDialog(String windowTitle, String message, String filename) {
        this.windowTitle = windowTitle;
        this.msg = message;
        this.filename = filename;
        this.state = State.FILENAME;
        finish();
    }

    public ConfirmationDialog(String windowTitle, String message) {
        this.windowTitle = windowTitle;
        this.msg = message;
        this.state = State.OK;
        finish();
    }

    private void finish() {
        String[] lines = this.msg.split(lf);
        int maxChar = 0;
        for (String line : lines) {
            maxChar = Math.max(maxChar, line.length());
        }
        width = width + (maxChar * 2);
        height = height + (lines.length * 40);
        int screenHeight = (int) Constants.SCREEN_HEIGHT;  // E.g.: 768
        int screenWidth = (int) Constants.SCREEN_WIDTH;    // E.g.: 1366
        if (width > screenWidth) {
            width = screenWidth * .9;
        }
        if (height > screenHeight) {
            height = screenHeight * .9;
        }
        createControls();
    }

    private Button newButton(String text, EventHandler<ActionEvent> event) {
        Button button = new Button(text);
        button.setMinWidth(80);
        button.setMaxWidth(80);
        button.setPrefWidth(80);
        button.setMinHeight(35);
        button.setMaxHeight(35);
        button.setPrefHeight(35);
        button.setOnAction(event);
        return button;
    }

    private void createControls() {
        Text text = new Text(msg);
        text.setFont(Constants.getMonaco(16));
        text.setTextAlignment(TextAlignment.LEFT);
        text.setWrappingWidth(width * .85);
        Hyperlink downloadLink = Constants.UI_COMPONENT_BUILDER_INSTANCE.buildHyperlink("Download the Latest Version", Font.font("Verdana", FontWeight.BOLD, 16), new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.25, Color.valueOf("#4158D0")), new Stop(1, Color.valueOf("#C850C0"))), "https://drifty.vercel.app/download");
        btnYes = newButton("Yes", e -> {
            answer.setAnswer(true);
            stage.close();
        });
        btnNo = newButton("No", e -> {
            answer.setAnswer(false);
            stage.close();
        });
        btnOk = newButton("OK", e -> stage.close());
        tfFilename = new TextField(filename);
        tfFilename.setMinWidth(width * .4);
        tfFilename.setMaxWidth(width * .8);
        tfFilename.setPrefWidth(width * .8);
        tfFilename.textProperty().addListener(((observable, oldValue, newValue) -> this.filename = newValue));
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);
        vbox = new VBox(30, text);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        if (state.equals(State.FILENAME)) {
            vbox.getChildren().add(tfFilename);
        }
        switch (state) {
            case OK -> {
                if (isUpdateError) {
                    vbox.getChildren().add(downloadLink);
                }
                hbox.getChildren().add(btnOk);
            }
            case YES_NO, FILENAME -> hbox.getChildren().addAll(btnYes, btnNo);
            default -> Environment.getMessageBroker().msgLogError("Unknown state in ConfirmationDialog : " + state);
        }
        vbox.getChildren().add(hbox);
    }

    public GetConfirmationDialogResponse getResponse() {
        if (Platform.isFxApplicationThread()) {
            showScene();
        }else {
            Platform.runLater(this::showScene);
            while (answer.isUnanswered()) {
                sleep(50);
            }
        }
        return answer;
    }

    private void showScene() {
        stage = Constants.getStage(windowTitle, false);
        scene = Constants.getScene(vbox);
        String theme = AppSettings.getGuiTheme();
        Theme.applyTheme(theme, scene);
        boolean isDark = "Dark".equals(theme);
        Theme.changeButtonStyle(isDark, btnYes);
        Theme.changeButtonStyle(isDark, btnNo);
        Theme.changeButtonStyle(isDark, btnOk);
        Theme.updateTextFields(isDark, false, tfFilename);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {
            answer.setAnswer(false);
            stage.close();
        });
        stage.setResizable(false);
        try {
            stage.showAndWait();
        }catch (Exception e) {
            Environment.getMessageBroker().msgLogError("Error displaying Confirmation Dialog: " + e.getMessage());
        }
    }

    public String getFilename() {
        return filename;
    }

    static Button getBtnNo() {
        return btnNo;
    }

    static Scene getScene() {
        return scene;
    }

    static Button getBtnYes() {
        return btnYes;
    }

    static Button getBtnOk() {
        return btnOk;
    }
}
