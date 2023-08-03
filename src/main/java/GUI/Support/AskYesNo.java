package GUI.Support;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * This class is meant to be used as a quick and dirty means of interacting with the
 * user when a yes / no question is needed or if we just need to notify the user of
 * something, in which case there is only an OK button offered.
 */
public class AskYesNo {
    private double width = 200;
    private double height = 150;
    private Stage stage;
    private Button btnYes;
    private Button btnNo;
    private Button btnOk;
    private Label message;
    private VBox vbox;
    private boolean answerYes = false;
    private boolean waiting = true;
    private String msg = "";
    private final String lf = System.lineSeparator();
    private boolean okOnly = false;

    public AskYesNo() {
        createControls();
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
        String[] lines = msg.split(lf);
        int maxChar = 0;
        for (String line : lines) {
            maxChar = Math.max(maxChar, line.length());
        }
        width = width + (maxChar * 5);
        height = height + (lines.length * 30);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // E.g.: java.awt.Dimension[width=1366,height=768]
        int screenHeight = (int) screenSize.getHeight(); // E.g.: 768
        int screenWidth = (int) screenSize.getWidth(); // E.g.: 1366
        if (width > screenWidth) {
            width = screenWidth * .9;
        }
        if (height > screenHeight) {
            height = screenHeight * .9;
        }
        createControls();
    }

    private void createControls() {
        message = new Label("Are you sure?");
        if (!msg.isEmpty()) {
            message.setText(msg);
            message.setWrapText(true);
        }
        btnYes = new Button("Yes");
        btnNo = new Button("No");
        btnOk = new Button("OK");
        HBox hbox;
        if (okOnly) {
             hbox = new HBox(20, btnOk);
        }
        else {
            hbox = new HBox(20, btnYes, btnNo);
        }

        hbox.setAlignment(Pos.CENTER);
        vbox = new VBox(15, message, hbox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        btnYes.setOnAction(e->{
            answerYes = true;
            waiting = false;
            stage.close();
        });
        btnNo.setOnAction(e->{
            answerYes = false;
            waiting = false;
            stage.close();
        });
        btnOk.setOnAction(e->stage.close());
    }

    public boolean isYes() {
        Platform.runLater(()->{
            stage = new Stage();
            stage.setWidth(width);
            stage.setHeight(height);
            Scene scene = new Scene(vbox);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        });
        while(waiting) {
            sleep(100);
        }
        return answerYes;
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
