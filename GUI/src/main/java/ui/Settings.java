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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.Drifty_GUI;

import java.io.File;

public class Settings {
    public static final CheckBox AUTO_PASTE_CHECKBOX = new CheckBox(); // Checkbox for auto-paste
    public static final TextField TF_CURRENT_DIRECTORY = new TextField(UIController.form.tfDir.getText());
    private static final Label LBL_DEFAULT_DOWNLOAD_DIR = new Label("Default Download Directory");
    private static final Label LBL_THEME = new Label("Theme");
    private static final Label SETTINGS_HEADING = new Label("Settings");
    private static final Label LBL_AUTO_PASTE = new Label("Auto-Paste");
    static final Button button = new Button("Select Directory");
    private final ChoiceBox<String> themeChoiceBox = new ChoiceBox<>();
    private final Stage stage = Constants.getStage("Settings", false);
    private static Scene settingsScene;

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);
        createDarkThemeLogic();
        createTfDirectory();
        createLabels();
        createAutoPasteCheck();
        createDirectoryButton();
        root.getChildren().addAll(themeChoiceBox, AUTO_PASTE_CHECKBOX, LBL_THEME, LBL_AUTO_PASTE, SETTINGS_HEADING, button, LBL_DEFAULT_DOWNLOAD_DIR, TF_CURRENT_DIRECTORY);
        settingsScene = Constants.getScene(root);
        Constants.addCSS(settingsScene, Constants.LIGHT_THEME_CSS);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        stage.setScene(settingsScene);
        setInitialTheme(AppSettings.GET.mainTheme());
        stage.showAndWait();
    }

    private void setInitialTheme(String theme) {
        if (theme.equals("Dark")) {
            Constants.addCSS(settingsScene, Constants.DARK_THEME_CSS);
            LBL_AUTO_PASTE.setTextFill(Color.WHITE);
            LBL_THEME.setTextFill(Color.WHITE);
            TF_CURRENT_DIRECTORY.setStyle("-fx-text-fill: white ; -fx-font-weight: Bold");
            SETTINGS_HEADING.setTextFill(Color.WHITE);
        } else {
            Constants.addCSS(settingsScene, Constants.LIGHT_THEME_CSS);
            LBL_AUTO_PASTE.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            TF_CURRENT_DIRECTORY.setStyle("-fx-text-fill: black ; -fx-font-weight: Bold");
            LBL_DEFAULT_DOWNLOAD_DIR.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            SETTINGS_HEADING.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        }
    }

    private void createDarkThemeLogic() {
        themeChoiceBox.getItems().addAll("Dark Theme", "Light Theme");
        themeChoiceBox.setTranslateY(210);
        themeChoiceBox.setTranslateX(130);
        themeChoiceBox.setValue(AppSettings.GET.mainTheme().equals("Dark") ? "Dark Theme" : "Light Theme");
        themeChoiceBox.setOnAction(e -> Theme.applyTheme(themeChoiceBox.getValue().equals("Dark Theme") ? "Dark" : "Light",
                settingsScene, Drifty_GUI.getScene(), About.getScene(), ConfirmationDialog.getScene()));
    }

    private void createAutoPasteCheck() {
        AUTO_PASTE_CHECKBOX.setSelected(AppSettings.GET.mainAutoPaste());
        AUTO_PASTE_CHECKBOX.setTranslateX(160);
        AUTO_PASTE_CHECKBOX.setTranslateY(115);
        AUTO_PASTE_CHECKBOX.setMaxWidth(5.0);
        AUTO_PASTE_CHECKBOX.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.SET.mainAutoPaste(newValue)));
    }

    private void createLabels() {
        SETTINGS_HEADING.setAlignment(Pos.TOP_CENTER);
        SETTINGS_HEADING.setFont(Font.font("monospace", FontWeight.EXTRA_BOLD, 100));
        SETTINGS_HEADING.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        SETTINGS_HEADING.setTranslateY(-150);

        LBL_AUTO_PASTE.setAlignment(Pos.TOP_CENTER);
        LBL_AUTO_PASTE.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        LBL_AUTO_PASTE.setTranslateY(45);
        LBL_AUTO_PASTE.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));

        LBL_THEME.setAlignment(Pos.TOP_CENTER);
        LBL_THEME.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        LBL_THEME.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        LBL_THEME.setTranslateY(130);
        LBL_THEME.setTranslateX(-20);

        LBL_DEFAULT_DOWNLOAD_DIR.setTranslateX(-160);
        LBL_DEFAULT_DOWNLOAD_DIR.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        LBL_DEFAULT_DOWNLOAD_DIR.setTranslateY(-50);
        LBL_DEFAULT_DOWNLOAD_DIR.setStyle("-fx-font-weight: Bold ; -fx-font-size:20px");
        LBL_DEFAULT_DOWNLOAD_DIR.setTextFill(AppSettings.GET.mainTheme().equals("Dark") ? Color.WHITE : LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
    }

    private void createTfDirectory() {
        TF_CURRENT_DIRECTORY.setMaxWidth(200);
        TF_CURRENT_DIRECTORY.setTranslateY(-85);
        TF_CURRENT_DIRECTORY.setTranslateX(150);
    }

    private void createDirectoryButton() {
        button.setTranslateY(50);
        if (AppSettings.GET.mainTheme().equals("Dark")) {
            button.setStyle(Constants.BUTTON_THEME_RELEASED);
            button.setOnMousePressed(e -> button.setStyle(Constants.BUTTON_THEME_PRESSED));
            button.setOnMouseReleased(e -> button.setStyle(Constants.BUTTON_THEME_RELEASED));
        } else {
            button.getStyleClass().add("button");
        }
        button.setOnAction(e -> handleDirectorySelection());
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
