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

public class Settings {

    public CheckBox AUTO_PASTE_CHECKBOX;// Checkbox for auto-paste
    private static TextField TF_CURRENT_DIRECTORY;
    private Label LBL_DEFAULT_DOWNLOAD_DIR;
    private Label LBL_THEME;
    private Label SETTINGS_HEADING;
    private Label LBL_AUTO_PASTE;
    private static Button SELECT_DIRECTORY_BUTTON;
    private ChoiceBox<String> themeChoiceBox;

    public Stage getStage() {
        return stage;
    }

    public Stage stage;
    private static Scene settingsScene;

    public static TextField getTF_CURRENT_DIRECTORY() {
        return TF_CURRENT_DIRECTORY;
    }

    public static Button getSelectDirectoryButton() {
        return SELECT_DIRECTORY_BUTTON;
    }

    private void initializeComponents() {
        TF_CURRENT_DIRECTORY = new TextField(UIController.form.tfDir.getText());
        SELECT_DIRECTORY_BUTTON = new Button("Select Directory");
        stage = Constants.getStage("Settings", false);
        AUTO_PASTE_CHECKBOX = new CheckBox();
        LBL_DEFAULT_DOWNLOAD_DIR = new Label("Default Download Directory");
        LBL_THEME = new Label("Theme");
        SETTINGS_HEADING = new Label("Settings");
        LBL_AUTO_PASTE = new Label("Auto-Paste");
        themeChoiceBox = new ChoiceBox<>();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(themeChoiceBox, AUTO_PASTE_CHECKBOX, LBL_THEME, LBL_AUTO_PASTE, SETTINGS_HEADING, SELECT_DIRECTORY_BUTTON, LBL_DEFAULT_DOWNLOAD_DIR, TF_CURRENT_DIRECTORY);
        settingsScene = Constants.getScene(root);
        setupLayout();
        Constants.addCSS(settingsScene, Constants.LIGHT_THEME_CSS);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        setInitialTheme(AppSettings.GET.mainTheme());
    }

    private void setupLayout() {
        setupThemeChoice();
        createTfDirectory();
        createLabels();
        createAutoPasteCheck();
        createDirectoryButton();
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
        Color textColor = isDark ? Color.WHITE : null;
        LinearGradient gradientColor = isDark ? null : LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)");
        Constants.addCSS(settingsScene, isDark ? Constants.DARK_THEME_CSS : Constants.LIGHT_THEME_CSS);
        LBL_AUTO_PASTE.setTextFill(isDark ? textColor : gradientColor);
        LBL_THEME.setTextFill(isDark ? textColor : gradientColor);
        LBL_DEFAULT_DOWNLOAD_DIR.setTextFill(isDark ? textColor : gradientColor);
        TF_CURRENT_DIRECTORY.setStyle("-fx-text-fill: " + (isDark ? "white" : "black") + " ; -fx-font-weight: Bold");
        SETTINGS_HEADING.setTextFill(isDark ? textColor : gradientColor);
    }

    private void setupThemeChoice() {
        themeChoiceBox.getItems().addAll("Dark Theme", "Light Theme");
        themeChoiceBox.setTranslateY(210);
        themeChoiceBox.setTranslateX(130);
        themeChoiceBox.setValue(AppSettings.GET.mainTheme().equals("Dark") ? "Dark Theme" : "Light Theme");
        themeChoiceBox.setOnAction(e -> Theme.applyTheme(themeChoiceBox.getValue().equals("Dark Theme") ? "Dark" : "Light", settingsScene, Drifty_GUI.getScene(), About.getScene(), ConfirmationDialog.getScene()));
    }

    private void createAutoPasteCheck() {
        AUTO_PASTE_CHECKBOX.setSelected(AppSettings.GET.mainAutoPaste());
        AUTO_PASTE_CHECKBOX.setTranslateX(160);
        AUTO_PASTE_CHECKBOX.setTranslateY(115);
        AUTO_PASTE_CHECKBOX.setMaxWidth(5.0);
        AUTO_PASTE_CHECKBOX.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.SET.mainAutoPaste(newValue)));
    }

    private void createLabels() {
        Paint paint = LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)");
        setupLabel(SETTINGS_HEADING, "monospace", FontWeight.EXTRA_BOLD, 100, paint, -150);
        setupLabel(LBL_AUTO_PASTE, "Arial", FontWeight.EXTRA_BOLD, 20, paint, 45);
        setupLabel(LBL_THEME, "Arial", FontWeight.EXTRA_BOLD, 20, paint, 130);
        LBL_THEME.setTranslateX(-20);
        setupLabel(LBL_DEFAULT_DOWNLOAD_DIR, "Arial", FontWeight.BOLD, 20, paint, -50);
        LBL_DEFAULT_DOWNLOAD_DIR.setTranslateX(-160);
    }

    private void setupLabel(Label label, String fontName, FontWeight weight, double size, Paint gradient, double translateY) {
        label.setAlignment(Pos.TOP_CENTER);
        label.setFont(Font.font(fontName, weight, size));
        label.setTranslateY(translateY);
        label.setTextFill(gradient);
    }

    private void createTfDirectory() {
        TF_CURRENT_DIRECTORY.setMaxWidth(200);
        TF_CURRENT_DIRECTORY.setTranslateY(-85);
        TF_CURRENT_DIRECTORY.setTranslateX(150);
    }

    private void createDirectoryButton() {
        SELECT_DIRECTORY_BUTTON.setTranslateY(50);
        if (AppSettings.GET.mainTheme().equals("Dark")) {
            SELECT_DIRECTORY_BUTTON.setStyle(Constants.BUTTON_THEME_RELEASED);
            SELECT_DIRECTORY_BUTTON.setOnMousePressed(e -> SELECT_DIRECTORY_BUTTON.setStyle(Constants.BUTTON_THEME_PRESSED));
            SELECT_DIRECTORY_BUTTON.setOnMouseReleased(e -> SELECT_DIRECTORY_BUTTON.setStyle(Constants.BUTTON_THEME_RELEASED));
        } else {
            SELECT_DIRECTORY_BUTTON.getStyleClass().add("button");
        }
        SELECT_DIRECTORY_BUTTON.setOnAction(e -> handleDirectorySelection());
    }

    private void handleDirectorySelection() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = chooser.showDialog(this.stage);
        String directoryPath = (selectedDirectory != null ? selectedDirectory.getAbsolutePath() : AppSettings.GET.lastDownloadFolder());
        UIController.form.tfDir.setText(directoryPath);
        TF_CURRENT_DIRECTORY.setText(directoryPath);
    }
}
