package GUI.Forms;

import Enums.Out;
import GUI.Support.Constants;
import GUI.Support.StringPropertyPrintStream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
        setProperties();
        captureOutputs();
        ConsoleOut.INSTANCE = this;
    }

    private static ConsoleOut INSTANCE;
    private Stage stage;
    private final StringProperty standardOut = new SimpleStringProperty();
    private final StringProperty errorOut = new SimpleStringProperty();
    private final AnchorPane ap = new AnchorPane();
    private final double width;
    private final double height = 250;
    private final double mainHeight;
    private final double mainX;
    private final double mainY;
    private double progress;
    private static String lastStandardTA;
    private static String lastErrorTA;
    private final URL textAreaCSS = getClass().getResource("/GUI/CSS/TextArea.css");
    private final String regex = "(\\[download]\\s+)(\\d+\\.\\d+)(%)";
    private final Pattern pattern = Pattern.compile(regex);
    private TextArea taStandard;
    private TextArea taError;
    private TabPane tabPane;
    private Tab tabStandard;
    private Tab tabError;

    private void makeControls() {
        ap.setStyle("-fx-background-color: transparent;");
        ap.getStyleClass().add("anchor-pane-transparent"); // Add the custom CSS class
        ap.setPrefHeight(height);
        ap.setMinHeight(height);
        ap.setMaxHeight(height);
        tabStandard = tab("Standard");
        tabError = tab("Errors");
        taStandard = textArea("text-area-standard");
        taError = textArea("text-area-error");
        tabPane = tabPane();
        tabStandard.setContent(taStandard);
        tabError.setContent(taError);
        tabPane.getTabs().setAll(tabStandard,tabError);
        button(10,10);
    }

    private void setProperties(){
        standardOut.addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.contains(System.lineSeparator())) {
                    setProgress(newValue);
                }
            }
        }));
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

    private void captureOutputs() {
        ByteArrayOutputStream baosStandard = new ByteArrayOutputStream();
        PrintStream printOutStandard = new StringPropertyPrintStream(baosStandard, standardOut, Out.STANDARD);

        ByteArrayOutputStream baosError = new ByteArrayOutputStream();
        PrintStream printOutError = new StringPropertyPrintStream(baosError, errorOut, Out.ERROR);

        System.setOut(printOutStandard);
        System.setErr(printOutError);
    }

    public static void appendText(String text, Out type) {
        String taString;
        switch(type) {
            case STANDARD -> {
                taString = INSTANCE.taStandard.getText() + text;
                if (!taString.equals(lastStandardTA)) {
                    INSTANCE.taStandard.appendText(text);
                    lastStandardTA = INSTANCE.taStandard.getText();
                }
            }
            case ERROR  -> {
                taString = INSTANCE.taError.getText() + text;
                if (!taString.equals(lastErrorTA)) {
                    INSTANCE.taError.appendText(text);
                    lastErrorTA = INSTANCE.taError.getText();
                }
            }
        }
    }

    public static void clear() {
        INSTANCE.taStandard.clear();
        INSTANCE.taError.clear();
    }

    public static String getStandardOut() {
        return INSTANCE.taStandard.getText();
    }

    private TextArea textArea(String style) {
        TextArea textArea = new TextArea();
        textArea.setMinHeight(height);
        textArea.setMaxHeight(height);
        textArea.setPrefHeight(height);
        textArea.setMinWidth(width);
        textArea.setMaxWidth(width);
        textArea.setPrefWidth(width);
        textArea.getStyleClass().add(style); // Add the custom CSS class
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", 13));
        return textArea;
    }

    private Tab tab(String title) {
        Tab tab = new Tab(title);
        tab.getStyleClass().add("tab-normal");
        return tab;
    }

    private TabPane tabPane() {
        TabPane tabPane = new TabPane();
        ap.getChildren().add(tabPane);
        placeControl(tabPane, 0, 0, 0, 0);
        tabPane.getStylesheets().add(Constants.tabsCSS.toExternalForm());
        return tabPane;
    }

    private void button(double right, double bottom) {
        ImageView button = new ImageView(Constants.imgCopyUp);
        button.getStyleClass().add("text-area-style"); // Add the custom CSS class
        button.setOpacity(.01);
        double width = Constants.imgCopyUp.getWidth();
        button.setFitWidth(width * .5);
        button.setPreserveRatio(true);
        button.setOnMouseEntered(e-> button.setOpacity(1));
        button.setOnMouseExited(e-> button.setOpacity(.01));
        button.setOnMousePressed(e-> button.setImage(Constants.imgCopyDown));
        button.setOnMouseReleased(e-> button.setImage(Constants.imgCopyUp));
        ap.getChildren().add(button);
        placeControl(button, -1, right, -1, bottom);
        button.setOnMouseClicked(e->{
            int tabIndex = tabPane.getSelectionModel().getSelectedIndex();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            String taContent = tabIndex == 0 ? taStandard.getText() : taError.getText();
            content.putString(taContent);
            clipboard.setContent(content);
        });
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
                Main.updateProgress(value);
            }
        }
    }

    private void placeControl(Node node, double left, double right, double top, double bottom) {
        if (top != -1) setTopAnchor(node, top);
        if (bottom != -1) setBottomAnchor(node, bottom);
        if (left != -1) setLeftAnchor(node, left);
        if (right != -1) setRightAnchor(node, right);
    }

    public void rePosition(double width, double height, double posX, double posY) {
        ap.setMinWidth(width);
        ap.setMaxWidth(width);
        ap.setPrefWidth(width);
        taStandard.setMinWidth(width);
        taStandard.setMaxWidth(width);
        taStandard.setPrefWidth(width);
        stage.setX(posX);
        stage.setY(posY + height);
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
