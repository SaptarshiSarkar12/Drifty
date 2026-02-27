package ui;

import settings.AppSettings;
import gui.init.Environment;
import gui.support.Constants;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
    private CheckBox earlyAccessCheckbox;
    private Label lblDefaultDownloadDir;
    private Label lblTheme;
    private Label lblSettingsHeading;
    private Label lblAutoPaste;
    private Label lblEarlyAccess;
    private ChoiceBox<String> themeChoiceBox;
    private Stage stage;
    private GridPane root;

    private void initializeComponents() {
        initializeUIComponents();
        configureScene();
    }

    private void initializeUIComponents() {
        setupThemeChoice();
        createTfDirectory();
        createLabels();
        createAutoPasteCheck();
        createEarlyAccessCheck();
        createDirectoryButton();
    }

    private void configureScene() {
        stage = Constants.getStage("Settings", false);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        stage.setMaxHeight(Constants.SCREEN_HEIGHT * .6);
        stage.setMaxWidth(Constants.SCREEN_WIDTH * .6);
        configureLayout();
        settingsScene = Constants.getScene(root);
        Constants.addCSS(settingsScene, Constants.LIGHT_THEME_CSS);
        setInitialTheme(AppSettings.getGuiTheme());
    }

    private void configureLayout() {
        root = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(); // For the first column, it will take 50% the width of the window
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints(); // For the second column, it will take 50% the width of the window
        column2.setPercentWidth(50);
        root.getColumnConstraints().addAll(column1, column2);
        root.setHgap(20);
        root.setVgap(10);
        root.setPadding(new Insets(10, 20, 20, 20));
        addComponents();
        setHAlignments();
        setHGrowsAlways(lblSettingsHeading, lblAutoPaste, autoPasteCheckbox, lblEarlyAccess, earlyAccessCheckbox, lblTheme, themeChoiceBox, lblDefaultDownloadDir, tfCurrentDirectory, selectDirectoryButton);
    }

    public void show() {
        try {
            if (stage != null && stage.isShowing()) {
                stage.toFront();
            }else {
                initializeComponents();
                stage.setScene(settingsScene);
                stage.showAndWait();
            }
        }catch (Exception e) {
            Environment.getMessageBroker().msgLogError("Error displaying Settings window: " + e.getMessage());
            try {
                new ConfirmationDialog("Failed to open Settings", "An error occurred while opening Settings.\n\n" + e.getMessage(), true, false).getResponse();
            }catch (Exception ignored) {
            }
        }
    }

    private void setHAlignments() {
        GridPane.setHalignment(lblSettingsHeading, HPos.CENTER);
        GridPane.setHalignment(lblAutoPaste, HPos.RIGHT);
        GridPane.setHalignment(lblEarlyAccess, HPos.RIGHT);
        GridPane.setHalignment(lblTheme, HPos.RIGHT);
        GridPane.setHalignment(lblDefaultDownloadDir, HPos.RIGHT);
        GridPane.setHalignment(selectDirectoryButton, HPos.CENTER);
    }

    private void setHGrowsAlways(Node... nodes) {
        for (Node node : nodes) {
            GridPane.setHgrow(node, Priority.ALWAYS);
        }
    }

    private void addComponents() {
        root.add(lblSettingsHeading, 0, 0, 2, 1);
        root.add(lblAutoPaste, 0, 1);
        root.add(autoPasteCheckbox, 1, 1);
        root.add(lblEarlyAccess, 0, 2);
        root.add(earlyAccessCheckbox, 1, 2);
        root.add(lblTheme, 0, 3);
        root.add(themeChoiceBox, 1, 3);
        root.add(lblDefaultDownloadDir, 0, 4);
        root.add(tfCurrentDirectory, 1, 4);
        root.add(selectDirectoryButton, 1, 5);
    }

    private void setInitialTheme(String theme) {
        boolean isDark = "Dark".equals(theme);
        Constants.addCSS(settingsScene, isDark ? Constants.DARK_THEME_CSS : Constants.LIGHT_THEME_CSS);
        applyStyleToLabels(isDark, lblTheme, lblAutoPaste, lblEarlyAccess, lblDefaultDownloadDir, lblSettingsHeading);
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
        themeChoiceBox.setValue("Dark".equals(AppSettings.getGuiTheme()) ? "Dark Theme" : "Light Theme");
        themeChoiceBox.setOnAction(e -> Theme.applyTheme("Dark Theme".equals(themeChoiceBox.getValue()) ? "Dark" : "Light", settingsScene, Drifty_GUI.getScene(), About.getScene(), UIController.getInfoScene(), ManageFolders.scene, ConfirmationDialog.getScene()));
    }

    private void createAutoPasteCheck() {
        autoPasteCheckbox = new CheckBox();
        autoPasteCheckbox.setSelected(AppSettings.isGuiAutoPasteEnabled());
        autoPasteCheckbox.setMaxWidth(5.0);
        autoPasteCheckbox.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.setGuiAutoPasteEnabled(newValue)));
    }

    private void createEarlyAccessCheck() {
        earlyAccessCheckbox = new CheckBox();
        earlyAccessCheckbox.setSelected(AppSettings.isEarlyAccessEnabled());
        earlyAccessCheckbox.setMaxWidth(5.0);
        earlyAccessCheckbox.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.setEarlyAccessEnabled(newValue)));
    }

    private void createLabels() {
        Paint textFill = LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)");
        lblSettingsHeading = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Settings", Font.font("monospace", FontWeight.EXTRA_BOLD, 100), textFill);
        lblAutoPaste = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Auto-Paste", Font.font("Arial", FontWeight.EXTRA_BOLD, 20), textFill);
        lblEarlyAccess = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Get Early Access Updates", Font.font("Arial", FontWeight.EXTRA_BOLD, 20), textFill);
        lblTheme = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Theme", Font.font("Arial", FontWeight.EXTRA_BOLD, 20), textFill);
        lblDefaultDownloadDir = UI_COMPONENT_BUILDER_INSTANCE.buildLabel("Default Download Directory", Font.font("Arial", FontWeight.BOLD, 20), textFill);
    }

    private void createTfDirectory() {
        tfCurrentDirectory = new TextField(UIController.form.tfDir.getText());
        tfCurrentDirectory.setMaxWidth(Double.MAX_VALUE);
        tfCurrentDirectory.setEditable(false);
    }

    private void createDirectoryButton() {
        selectDirectoryButton = new Button("Select Directory");
        if ("Dark".equals(AppSettings.getGuiTheme())) {
            selectDirectoryButton.setStyle(Constants.BUTTON_RELEASED);
            selectDirectoryButton.setOnMousePressed(e -> selectDirectoryButton.setStyle(Constants.BUTTON_PRESSED));
            selectDirectoryButton.setOnMouseReleased(e -> selectDirectoryButton.setStyle(Constants.BUTTON_RELEASED));
        }else {
            selectDirectoryButton.getStyleClass().add("button");
        }
        selectDirectoryButton.setOnAction(e -> handleDirectorySelection());
    }

    private void handleDirectorySelection() {
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            String userHome = System.getProperty("user.home");
            chooser.setInitialDirectory(new File(userHome));
            String defaultPath = AppSettings.getLastDownloadFolder();
            if (defaultPath == null) {
                defaultPath = userHome;
            }
            File selectedDirectory = chooser.showDialog(this.stage);
            String directoryPath = selectedDirectory != null ? selectedDirectory.getAbsolutePath() : defaultPath;
            UIController.form.tfDir.setText(directoryPath);
            tfCurrentDirectory.setText(directoryPath);
        }catch (Exception e) {
            Environment.getMessageBroker().msgLogError("Error selecting directory: " + e.getMessage());
            try {
                new ConfirmationDialog("Failed to select directory", "An error occurred while opening the Directory Chooser.\n\n" + e.getMessage(), true, false).getResponse();
            }catch (Exception ignored) {
            }
        }
    }

    public static TextField getTfCurrentDirectory() {
        return tfCurrentDirectory;
    }

    public static Button getSelectDirectoryButton() {
        return selectDirectoryButton;
    }
}
