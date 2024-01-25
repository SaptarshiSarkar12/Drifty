package ui;

import gui.init.Environment;
import gui.support.Constants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import static utils.Utility.sleep;

public class ConfirmationDialog {
    enum State {
        YES_NO, OK, FILENAME
    }

    private final State state;
    private final String lf = System.lineSeparator();
    private double width = 200;
    private double height = 150;
    private Stage stage;
    private VBox vbox;
    private final String windowTitle;
    private final String msg;
    private String filename = "";
    private final GetConfirmationDialogResponse answer = new GetConfirmationDialogResponse();

    public ConfirmationDialog(String windowTitle, String message, boolean okOnly) {
        this.windowTitle = windowTitle;
        this.msg = message;
        this.state = okOnly ? State.OK : State.YES_NO;
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
        button.setFont(Constants.getMonaco(17));
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
        Label message = new Label("Are you sure?");
        message.setFont(Constants.getMonaco(17));
        if (!msg.isEmpty()) {
            message.setText(msg);
            message.setWrapText(true);
            message.setTextAlignment(TextAlignment.CENTER);
        }
        Button btnYes = newButton("Yes", e -> {
            answer.setAnswer(true);
            stage.close();
        });
        Button btnNo = newButton("No", e -> {
            answer.setAnswer(false);
            stage.close();
        });
        Button btnOk = newButton("OK", e -> stage.close());
        TextField tfFilename = new TextField(filename);
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
            case OK -> hbox.getChildren().add(btnOk);
            case YES_NO, FILENAME -> hbox.getChildren().addAll(btnYes, btnNo);
            default -> Environment.getMessageBroker().msgLogError("Unknown state in ConfirmationDialog : " + state);
        }
        vbox.getChildren().add(hbox);
    }

    public GetConfirmationDialogResponse getResponse() {
        if (Platform.isFxApplicationThread()) {
            showScene();
        } else {
            Platform.runLater(this::showScene);
            while (answer.isUnanswered()) {
                sleep(50);
            }
        }
        return answer;
    }

    private void showScene() {
        stage = Constants.getStage(windowTitle, false);
        Scene scene = Constants.getScene(vbox);
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
        stage.showAndWait();
    }

    public String getFilename() {
        return filename;
    }
}
