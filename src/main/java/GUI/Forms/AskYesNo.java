package GUI.Forms;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

/**
 * This class is meant to be used as a quick and dirty means of interacting with the
 * user when a yes / no question is needed or if we just need to notify the user of
 * something, in which case there is only an OK button offered.
 */
class AskYesNo {
    private final String lf = System.lineSeparator();
    private double width = 200;
    private double height = 150;
    private Stage stage;
    private Button btnYes;
    private Button btnNo;
    private Button btnOk;
    private Label message;
    private VBox vbox;
    public static boolean answerYes = false;
    private String msg = "";
    private boolean okOnly = false;
    private static boolean waiting = true;
    private final GetResponse answer = new GetResponse();

    public AskYesNo() {
        finish();
    }

    public AskYesNo(String message, boolean okOnly) {
        this.msg = message;
        this.okOnly = okOnly;
        finish();
    }

    public AskYesNo(String message) {
        this.msg = message;
        finish();
    }

    private void finish() {
        createControls();
        String[] lines = msg.split(lf);
        int maxChar = 0;
        for (String line : lines) {
            maxChar = Math.max(maxChar, line.length());
        }
        width = width + (maxChar * 5);
        height = height + (lines.length * 30);
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
        message = new Label("Are you sure?");
        message.setFont(Constants.getMonaco(17));
        if (!msg.isEmpty()) {
            message.setText(msg);
            message.setWrapText(true);
            message.setTextAlignment(TextAlignment.CENTER);
        }
        btnYes = newButton("Yes", e -> {
            answer.setAnswer(true);
            waiting = false;
            stage.close();
        });
        btnNo = newButton("No", e -> {
            answer.setAnswer(false);
            waiting = false;
            stage.close();
        });
        btnOk = newButton("OK", e -> {
            waiting = false;
            stage.close();
        });
        HBox hbox;
        if (okOnly) {
            hbox = new HBox(20, btnOk);
        }
        else {
            hbox = new HBox(20, btnYes, btnNo);
        }

        hbox.setAlignment(Pos.CENTER);
        vbox = new VBox(30, message, hbox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
    }

    public GetResponse getResponse() {
        if(Platform.isFxApplicationThread()) {
            showScene();
        }
        else {
            Platform.runLater(this::showScene);
            while(answer.inLimbo())
                sleep();
        }
        return answer;
    }

    public void showOK() {
        Platform.runLater(this::showScene);
    }

    private void showScene() {
        waiting = true;
        stage = Constants.getStage();
        Scene scene = Constants.getScene(vbox);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.centerOnScreen();
        stage.setOnCloseRequest(e->{
            answer.setAnswer(false);
            stage.close();
        });
        stage.showAndWait();
    }


    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
