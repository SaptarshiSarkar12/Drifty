package GUIFX;

import GUIFX.Support.StringPropertyPrintStream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.layout.AnchorPane.*;

/**
 * This class is the pop down console form that receives the 'System.out' capture
 * from the 'StringPropertyPrintStream' class. It only has one TextArea as its only control.
 */

public class ConsoleOut {

    public ConsoleOut(double width, double height, double posX, double posY) {
        this.width = width;
        this.mainX = posX;
        this.mainY = posY;
        this.mainHeight = height;
        makeControls();
        makeScene();
        captureConsole();
        ConsoleOut.INSTANCE = this;
    }

    private static ConsoleOut INSTANCE;
    private Stage stage;
    private final StringProperty consoleProperty = new SimpleStringProperty();
    private final AnchorPane ap = new AnchorPane();
    private final double width;
    private final double height = 250;
    private final double mainHeight;
    private final double mainX;
    private final double mainY;
    private double progress;
    private final TextArea textArea = textArea(0, 0, 0);
    private final URL textAreaCSS = getClass().getResource("/FX/CSS/TextArea.css");
    private final String regex = "(\\[download]\\s+)(\\d+\\.\\d+)(%)";
    private final Pattern pattern = Pattern.compile(regex);

    private void makeControls() {
        ap.setStyle("-fx-background-color: transparent;");
        ap.getStyleClass().add("anchor-pane-transparent"); // Add the custom CSS class
        ap.setPrefHeight(height);
        ap.setMinHeight(height);
        ap.setMaxHeight(height);
    }

    public void rePosition(double width, double height, double posX, double posY) {
        ap.setMinWidth(width);
        ap.setMaxWidth(width);
        ap.setPrefWidth(width);
        textArea.setMinWidth(width);
        textArea.setMaxWidth(width);
        textArea.setPrefWidth(width);
        stage.setX(posX);
        stage.setY(posY + height);
    }

    private void makeScene() {
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(ap);
        scene.getStylesheets().add(textAreaCSS.toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setX(mainX);
        stage.setY(mainY + mainHeight);
    }

    private void captureConsole() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream customOut = new StringPropertyPrintStream(baos, consoleProperty);
        System.setOut(customOut);
    }

    public static void append(String text) {
        INSTANCE.textArea.appendText(text);
    }

    private TextArea textArea(double left, double right, double bottom) {
        TextArea textArea = new TextArea();
        textArea.setMinHeight(height);
        textArea.setMaxHeight(height);
        textArea.setPrefHeight(height);
        textArea.setMinWidth(width);
        textArea.setMaxWidth(width);
        textArea.setPrefWidth(width);
        textArea.getStyleClass().add("text-area-style"); // Add the custom CSS class
        textArea.setEditable(false);
        ap.getChildren().add(textArea);
        placeControl(textArea, left, right, -1, bottom);
        textArea.setFont(new Font("Arial", 13));
        consoleProperty.addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.contains(System.lineSeparator())) {
                    setProgress(newValue);
                }
            }
        }));
        return textArea;
    }

    private void setProgress(String line) {
        if (!line.isEmpty()) {
            String[] lines = line.split(System.lineSeparator());
            if (lines.length > 2) {
                String text = lines[lines.length - 2];
                Matcher matcher = pattern.matcher(text);
                double value = 0.0;
                while (matcher.find()) {
                    value = Double.parseDouble(matcher.group(2)) / 100;
                }
                MainGUI.updateProgress(value);
            }
        }
    }

    private void placeControl(Node node, double left, double right, double top, double bottom) {
        if (top != -1) setTopAnchor(node, top);
        if (bottom != -1) setBottomAnchor(node, bottom);
        if (left != -1) setLeftAnchor(node, left);
        if (right != -1) setRightAnchor(node, right);
    }

    public void show() {
        if (stage == null) {
            makeScene();
        }
        stage.show();
    }

    public void hide() {
        if (stage != null) {
            stage.close();
        }
    }
}
