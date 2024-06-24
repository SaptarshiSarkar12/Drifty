package ui;

import gui.preferences.AppSettings;
import gui.support.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.Drifty_GUI;

import java.io.File;

import static gui.support.Constants.UI_COMPONENT_BUILDER_INSTANCE;

public class Settings {
    private static Button selectDirectoryButton;
    private static Scene settingsScene;
    private static TextField tfCurrentDirectory;
    private CheckBox autoPasteCheckbox;
    private Label lblDefaultDownloadDir;
    private Label lblTheme;
    private Label lblSettingsHeading;
    private Label lblAutoPaste;
    private ChoiceBox<String> themeChoiceBox;
    private Stage stage;

    private void initializeComponents() {
        initializeUIComponents();
        configureScene();
    }

    private void initializeUIComponents() {
        setupThemeChoice();
        createTfDirectory();
        createLabels();
        createAutoPasteCheck();
        createDirectoryButton();
    }

    private void configureScene() {
        stage = Constants.getStage("Settings", false);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(themeChoiceBox, autoPasteCheckbox, lblTheme, lblAutoPaste, lblSettingsHeading, selectDirectoryButton, lblDefaultDownloadDir, tfCurrentDirectory);
        settingsScene = Constants.getScene(root);
        Constants.addCSS(settingsScene, Constants.LIGHT_THEME_CSS);
        setInitialTheme(AppSettings.GET.mainTheme());
    }

    public void show() {
        if (stage != null && stage.isShowing()) {
            stage.toFront();
        } else {
            initializeComponents();
            stage.setScene(settingsScene);
            stage.showAndWait();
        }
    }

    private void setInitialTheme(String theme) {
        boolean isDark = theme.equals("Dark");
        Constants.addCSS(settingsScene, isDark ? Constants.DARK_THEME_CSS : Constants.LIGHT_THEME_CSS);
        applyStyleToLabels(isDark, lblTheme, lblAutoPaste, lblDefaultDownloadDir, lblSettingsHeading);
        tfCurrentDirectory.setStyle("-fx-text-fill: " + (isDark ? "white" : "black") + " ; -fx-font-weight: Bold");
    }

    private void applyStyleToLabels(boolean isDark, Label... labels) {
        Color darkThemeTextFill = Color.WHITE;
        LinearGradient lightThemeTextFill = LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)");
        for (Label label : labels) {
            label.setTextFill(isDark ? darkThemeTextFill : lightThemeTextFill);
        }
    }

    private void setupThemeChoice() {
        themeChoiceBox = new ChoiceBox<>();
        themeChoiceBox.getItems().addAll("Dark Theme", "Light Theme");
        themeChoiceBox.setTranslateY(210);
        themeChoiceBox.setTranslateX(130);
        themeChoiceBox.setValue(AppSettings.GET.mainTheme().equals("Dark") ? "Dark Theme" : "Light Theme");
        themeChoiceBox.setOnAction(e -> Theme.applyTheme(themeChoiceBox.getValue().equals("Dark Theme") ? "Dark" : "Light", settingsScene, Drifty_GUI.getScene(), About.getScene(), ConfirmationDialog.getScene()));
    }

    private void createAutoPasteCheck() {
        autoPasteCheckbox = new CheckBox();
        autoPasteCheckbox.setSelected(AppSettings.GET.mainAutoPaste());
        autoPasteCheckbox.setTranslateX(160);
        autoPasteCheckbox.setTranslateY(115);
        autoPasteCheckbox.setMaxWidth(5.0);
        autoPasteCheckbox.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.SET.mainAutoPaste(newValue)));
    }

    private void createLabels() {
        Paint textFill = LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)");
        lblSettingsHeading = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Settings", Font.font("monospace", FontWeight.EXTRA_BOLD, 100), textFill, 0 ,-150);
        lblAutoPaste = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Auto-Paste", Font.font("Arial", FontWeight.EXTRA_BOLD, 20), textFill, 0, 45);
        lblTheme = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Theme", Font.font("Arial", FontWeight.EXTRA_BOLD, 20), textFill, -20, 130);
        lblDefaultDownloadDir = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Default Download Directory", Font.font("Arial", FontWeight.BOLD, 20), textFill, -160, -50);
        lblDefaultDownloadDir.setTranslateX(-160);
    }

    private void createTfDirectory() {
        tfCurrentDirectory = new TextField(UIController.form.tfDir.getText());
        tfCurrentDirectory.setMaxWidth(200);
        tfCurrentDirectory.setTranslateY(-85);
        tfCurrentDirectory.setTranslateX(150);
    }

    private void createDirectoryButton() {
        selectDirectoryButton = new Button("Select Directory");
        selectDirectoryButton.setTranslateY(50);
        if (AppSettings.GET.mainTheme().equals("Dark")) {
            selectDirectoryButton.setStyle(Constants.BUTTON_RELEASED);
            selectDirectoryButton.setOnMousePressed(e -> selectDirectoryButton.setStyle(Constants.BUTTON_PRESSED));
            selectDirectoryButton.setOnMouseReleased(e -> selectDirectoryButton.setStyle(Constants.BUTTON_RELEASED));
        } else {
            selectDirectoryButton.getStyleClass().add("button");
        }
        selectDirectoryButton.setOnAction(e -> handleDirectorySelection());
    }

    private void handleDirectorySelection() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = chooser.showDialog(this.stage);
        String directoryPath = (selectedDirectory != null ? selectedDirectory.getAbsolutePath() : AppSettings.GET.lastDownloadFolder());
        UIController.form.tfDir.setText(directoryPath);
        tfCurrentDirectory.setText(directoryPath);
    }

    public static TextField getTfCurrentDirectory() {
        return tfCurrentDirectory;
    }

    public static Button getSelectDirectoryButton() {
        return selectDirectoryButton;
    }
}
