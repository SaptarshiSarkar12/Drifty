package ui;

import gui.init.Environment;
import gui.preferences.AppSettings;
import gui.support.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.Objects;

public class Settings {
    public static final CheckBox AUTO_PASTE_CHECK = new CheckBox();
    private final ChoiceBox<String> themeCheckBox = new ChoiceBox<>();
    private final TextField tfCurrDir = new TextField(UIController.form.tfDir.getText());
    private final Button button = new Button("Select Directory");
    private final Stage stage = Constants.getStage("Settings", false);
    Scene settingsScene;
    Label lblDefaultDownloadDir = new Label("Default Download Directory");
    Label lblTheme = new Label("Theme");
    Label settingsHeading = new Label("Settings");
    Label lblAutoPaste = new Label("Auto-Paste");

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
        themeCheckBox.setOnAction(e -> applyTheme(themeCheckBox.getValue().equals("Dark Theme") ? "Dark" : "Light"));
    }

    private void updateTextColors(boolean isDark) {
        // Labels
        Paint color = isDark ? Color.WHITE : LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)");
        lblAutoPaste.setTextFill(color);
        lblTheme.setTextFill(color);
        lblDefaultDownloadDir.setTextFill(color);
        settingsHeading.setTextFill(color);
        for (Node node : Drifty_GUI.getAboutRoot().getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setTextFill(color);
            }
        }
        // TextFields
        String style = isDark ? "-fx-text-fill: White;" : "-fx-text-fill: Black;";
        UIController.form.tfDir.setStyle(style);
        UIController.form.tfFilename.setStyle(style);
        UIController.form.tfLink.setStyle(style);
        tfCurrDir.setStyle(style + "-fx-font-weight: Bold");
    }

    private void applyTheme(String theme) {
        boolean isDark = theme.equals("Dark");
        AppSettings.SET.mainTheme(theme);
        updateCSS(isDark);
        updateTextColors(isDark);
        setupButtonGraphics(theme);
        // Banner and Logo
        changeImages(theme);
        // handling the button styles
        changeButtonStyle(isDark);
    }

    private void changeButtonStyle(boolean isDark) {
        String style = isDark ? "-fx-text-fill: White;" : "-fx-text-fill: Black;";
        String backColorRealesd = isDark ? "-fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" : "-fx-background-color: linear-gradient(rgb(54,151,225) 18%, rgb(121,218,232) 90%, rgb(126,223,255) 95%);";
        String backColorPressed = isDark ? " -fx-background-color: linear-gradient(rgb(11, 118, 220) 20%, rgb(33, 31, 131) 65%, rgb(2, 2, 168) 100%);" : "-fx-background-color: linear-gradient(rgb(126,223,255) 20%, rgb(121,218,232) 20%, rgb(54,151,225) 100%);";
        button.setStyle(style + "-fx-font-weight: Bold;" + backColorRealesd + "-fx-border-color: black;");
        button.setOnMousePressed(ev -> button.setStyle(
            style + "-fx-font-weight: Bold;" + backColorPressed + "-fx-border-color: black;"
        ));
        button.setOnMouseReleased(ev -> button.setStyle(
            style + "-fx-font-weight: Bold;" + backColorRealesd + "-fx-border-color: black;"
        ));
    }

    private void changeImages(String theme) {
        String bannerPath = "/Backgrounds/DriftyMain" + theme + ".png";
        String splashPath = "/Splash" + theme + ".png";
        Constants.imgMainGuiBanner = new Image(Objects.requireNonNull(Constants.class.getResource(bannerPath)).toExternalForm());
        MainGridPane.ivLogo.setImage(Constants.imgMainGuiBanner);
        Constants.imgSplash = new Image(Objects.requireNonNull(Constants.class.getResource(splashPath)).toExternalForm());
        Drifty_GUI.getAppIcon().setImage(Constants.imgSplash);
    }

    private void updateCSS(boolean isDark) {
        if (isDark) {
            settingsScene.getStylesheets().remove(Objects.requireNonNull(Settings.class.getResource("/CSS/Label.css")).toExternalForm());
            Constants.addCSS(settingsScene, Constants.DARK_THEME_CSS);
            Drifty_GUI.getScene().getStylesheets().add(Objects.requireNonNull(Constants.DARK_THEME_CSS).toExternalForm());
            if (Drifty_GUI.getAboutScene() != null) {
                Drifty_GUI.getAboutScene().getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
            }
        } else {
            AppSettings.SET.mainTheme("Light");
            settingsScene.getStylesheets().remove(Objects.requireNonNull(Settings.class.getResource("/CSS/DarkTheme.css")).toExternalForm());
            Drifty_GUI.getScene().getStylesheets().remove(Objects.requireNonNull(Settings.class.getResource("/CSS/DarkTheme.css")).toExternalForm());
            settingsScene.getStylesheets().add(Objects.requireNonNull(Constants.LIGHT_THEME_CSS).toExternalForm());
            Drifty_GUI.getScene().getStylesheets().add(Constants.LIGHT_THEME_CSS.toExternalForm());
            if (Drifty_GUI.getAboutScene() != null) {
                Drifty_GUI.getAboutScene().getStylesheets().clear();
                Drifty_GUI.getAboutScene().getStylesheets().add(Constants.LIGHT_THEME_CSS.toExternalForm());
            }
        }

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
            button.setStyle(" -fx-text-fill: white;\n" +
                    "    -fx-font-weight: Bold;" +
                    "    -fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" +
                    "    -fx-border-color: black;"
            );
            button.setOnMousePressed(e -> {
                button.setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-font-weight: Bold;" +
                                " -fx-background-color: linear-gradient(rgb(11, 118, 220) 20%, rgb(33, 31, 131) 65%, rgb(2, 2, 168) 100%);\n" +
                                "-fx-border-color: black;"

                );
            });
            button.setOnMouseReleased(e -> {
                button.setStyle(" -fx-text-fill: white;\n" +
                        "-fx-font-weight: Bold;" +
                        "-fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" +
                        "    -fx-border-color: black;");
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
