package ui;

import gui.init.Environment;
import gui.preferences.AppSettings;
import gui.support.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.Drifty_GUI;
import java.io.File;
import java.util.Objects;

public class Settings {
    public static final CheckBox AUTO_PASTE_CHECK = new CheckBox();
    private final ChoiceBox<String> themeCheckBox = new ChoiceBox<>();
    public static final TextField tfCurrDir = new TextField(UIController.form.tfDir.getText());
    static Button button = new Button("Select Directory");
    private final Stage stage = Constants.getStage("Settings", false);
    private static Scene settingsScene;
    static Label lblDefaultDownloadDir = new Label("Default Download Directory");
    static Label lblTheme = new Label("Theme");
    static Label settingsHeading = new Label("Settings");
    static Label lblAutoPaste = new Label("Auto-Paste");
    private Image getImageForButton(String theme, String buttonType) {
        String imagePath;
        if (buttonType.equals("StartUp") || buttonType.equals("StartDown")) {
            imagePath = "/Buttons/Start/" + buttonType + theme + ".png";
        } else {
            imagePath = "/Buttons/Save/" + buttonType + theme + ".png";
        }
        return new Image(Objects.requireNonNull(Constants.class.getResource(imagePath)).toExternalForm());
    }

    private void setupButtonGraphics(String theme) {
        Image imageStartUp = getImageForButton(theme, "StartUp");
        Image imageStartDown = getImageForButton(theme, "StartDown");
        ImageView ivStartUp = MainGridPane.newImageView(imageStartUp, 0.45);
        ImageView ivStartDown = MainGridPane.newImageView(imageStartDown, 0.45);
        UIController.form.btnStart.setOnMousePressed(ev -> UIController.form.btnStart.setGraphic(ivStartDown));
        UIController.form.btnStart.setOnMouseReleased(ev -> UIController.form.btnStart.setGraphic(ivStartUp));
        UIController.form.btnStart.setGraphic(ivStartUp);

        Image imageSaveUp = getImageForButton(theme, "SaveUp");
        Image imageSaveDown = getImageForButton(theme, "SaveDown");
        ImageView ivSaveUp = MainGridPane.newImageView(imageSaveUp, 0.45);
        ImageView ivSaveDown = MainGridPane.newImageView(imageSaveDown, 0.45);
        UIController.form.btnSave.setOnMousePressed(ev -> UIController.form.btnSave.setGraphic(ivSaveDown));
        UIController.form.btnSave.setOnMouseReleased(ev -> UIController.form.btnSave.setGraphic(ivSaveUp));
        UIController.form.btnSave.setGraphic(ivSaveUp);
    }
    public void creatSettings() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);
        creatDarkThemeLogic();
        creatTfDir();
        creatLabels();
        creatAutoPasteCheck();
        creatDirButton();
        root.getChildren().addAll(themeCheckBox, AUTO_PASTE_CHECK, lblTheme, lblAutoPaste, settingsHeading, button, lblDefaultDownloadDir, tfCurrDir);
        settingsScene = Constants.getScene(root);
        Constants.addCSS(settingsScene, Constants.LIGHT_THEME_CSS);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        stage.setScene(settingsScene);
        setInitialTheme(AppSettings.GET.mainTheme());
        stage.show();
    }
    private void setInitialTheme(String theme) {
        if (theme.equals("Dark")) {
            Constants.addCSS(settingsScene, Constants.DARK_THEME_CSS);
            lblAutoPaste.setTextFill(Color.WHITE);
            lblTheme.setTextFill(Color.WHITE);
            tfCurrDir.setStyle("-fx-text-fill: white ; -fx-font-weight: Bold");
            settingsHeading.setTextFill(Color.WHITE);
        } else {
            Constants.addCSS(settingsScene, Constants.LIGHT_THEME_CSS);
            lblAutoPaste.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            tfCurrDir.setStyle("-fx-text-fill: black ; -fx-font-weight: Bold");
            lblDefaultDownloadDir.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            settingsHeading.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        }
    }

    private void creatDarkThemeLogic() {
        themeCheckBox.getItems().addAll("Dark Theme", "Light Theme");
        themeCheckBox.setTranslateY(210);
        themeCheckBox.setTranslateX(130);
        themeCheckBox.setValue(AppSettings.GET.mainTheme().equals("Dark") ? "Dark Theme" : "Light Theme");
        themeCheckBox.setOnAction(e -> {
            Theme.applyTheme(themeCheckBox.getValue().equals("Dark Theme") ? "Dark" : "Light",
                    settingsScene, Drifty_GUI.getScene(), Drifty_GUI.getAboutScene(), ConfirmationDialog.getScene());
        });
    }

    private void creatAutoPasteCheck() {
        AUTO_PASTE_CHECK.setSelected(AppSettings.GET.mainAutoPaste());
        AUTO_PASTE_CHECK.setTranslateX(160);
        AUTO_PASTE_CHECK.setTranslateY(115);
        AUTO_PASTE_CHECK.setMaxWidth(5.0);
        AUTO_PASTE_CHECK.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.SET.mainAutoPaste(newValue)));
    }
    private void creatLabels() {
        settingsHeading.setAlignment(Pos.TOP_CENTER);
        settingsHeading.setFont(Font.font("monospace", FontWeight.EXTRA_BOLD, 100));
        settingsHeading.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        settingsHeading.setTranslateY(-150);


        lblAutoPaste.setAlignment(Pos.TOP_CENTER);
        lblAutoPaste.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        lblAutoPaste.setTranslateY(45);
        lblAutoPaste.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));


        lblTheme.setAlignment(Pos.TOP_CENTER);
        lblTheme.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        lblTheme.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        lblTheme.setTranslateY(130);
        lblTheme.setTranslateX(-20);


        lblDefaultDownloadDir.setTranslateX(-160);
        lblDefaultDownloadDir.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblDefaultDownloadDir.setTranslateY(-50);
        lblDefaultDownloadDir.setStyle(
                "-fx-font-weight: Bold ; -fx-font-size:20px"
        );
        lblDefaultDownloadDir.setTextFill(AppSettings.GET.mainTheme().equals("Dark") ? Color.WHITE : LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
    }
    private void creatTfDir() {
        tfCurrDir.setMaxWidth(200);
        tfCurrDir.setTranslateY(-85);
        tfCurrDir.setTranslateX(150);
    }
    private void creatDirButton() {
        button.setTranslateY(50);
        if (AppSettings.GET.mainTheme().equals("Dark")) {
            button.setStyle(Constants.BTN_THEME);
            button.setOnMousePressed(e -> {
                button.setStyle(Constants.BTN_THEME_PRESSED);
            });
            button.setOnMouseReleased(e -> {
                button.setStyle(Constants.BTN_THEME);
            });
        } else {
            button.getStyleClass().add("button");
        }
        button.setOnAction(e -> {
            handleDirectorySelection();
        });
    }
    private void handleDirectorySelection() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = chooser.showDialog(this.stage);
        String directoryPath = selectedDirectory != null ? selectedDirectory.getAbsolutePath() : AppSettings.GET.lastDownloadFolder();
        UIController.form.tfDir.setText(directoryPath);
        tfCurrDir.setText(directoryPath);
        UIController.form.tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                UIController.getDirectoryExists().setValue(false);
                if (newValue.isEmpty()) {
                    Environment.getMessageBroker().msgDirError("Directory cannot be empty!");
                } else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        Environment.getMessageBroker().msgDirInfo("Directory exists!");
                        UIController.getDirectoryExists().setValue(true);
                    } else {
                        Environment.getMessageBroker().msgDirError("Directory does not exist or is not a directory!");
                    }
                }
            }
        }));
    }
}
