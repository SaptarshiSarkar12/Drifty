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
import preferences.Get;

import java.io.File;
import java.util.Objects;


public class Settings {
    Scene settingsScene;
    private ChoiceBox<String> darkLightTheme = new ChoiceBox<>();

    Label lblDwnDir = new Label("Default Download Directory");

    private TextField tfCurrDir = new TextField(UIController.form.tfDir.getText());

    private final String nl = System.lineSeparator();
    public static CheckBox autoPasteCheck = new CheckBox();

    private void setupButtonGraphics(String theme) {
        boolean isDark = theme.equals("Dark");
        String imagePath = isDark ? "/Buttons/Start/StartUp Dark.png" : "/Buttons/Start/StartUp.png";
        Image imageUp = new Image(Objects.requireNonNull(Constants.class.getResource(imagePath)).toExternalForm());
        Image imageDown = new Image(Objects.requireNonNull(Constants.class.getResource(imagePath.replace("Up", "Down"))).toExternalForm());
        ImageView imageViewUp = new ImageView(imageUp);
        ImageView imageViewDn = new ImageView(imageDown);
        double width = imageUp.getWidth();
        imageViewUp.setPreserveRatio(true);
        imageViewUp.setFitWidth(width * 0.45);
        imageViewDn.setPreserveRatio(true);
        imageViewDn.setFitWidth(width * 0.45);
        UIController.form.btnStart.setOnMousePressed(ev -> UIController.form.btnStart.setGraphic(imageViewDn));
        UIController.form.btnStart.setOnMouseReleased(ev -> UIController.form.btnStart.setGraphic(imageViewUp));
        UIController.form.btnStart.setGraphic(imageViewUp);
    }

    Label lblTheme = new Label("Theme");

    Label settingsHeading = new Label("Settings");
    private Button button = new Button("Select Directory");
    private Stage stage = Constants.getStage("Settings", false);
    Label lblAutoPaste = new Label("Auto-Paste");

    public void creatSettings() {

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);

        creatDarkThemeLogic();

        creatTfDir();

        creatLabels();

        creatAutoPasteCheck();

        creatDirBtn();


        root.getChildren().addAll(darkLightTheme, autoPasteCheck, lblTheme, lblAutoPaste, settingsHeading, button, lblDwnDir, tfCurrDir);
        settingsScene = Constants.getScene(root);
        Constants.addCSS(settingsScene, Constants.SCENE_CSS);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        stage.setScene(settingsScene);


        if (AppSettings.GET.mainTheme().equals("Dark")) {
            settingsScene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
            lblAutoPaste.setTextFill(Color.WHITE);
            lblTheme.setTextFill(Color.WHITE);
            tfCurrDir.setStyle("-fx-text-fill: white ; -fx-font-weight: Bold");
            settingsHeading.setTextFill(Color.WHITE);

        } else {
            tfCurrDir.setStyle("-fx-text-fill: black ; -fx-font-weight: Bold");
            lblDwnDir.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            settingsScene.getStylesheets().add(Constants.SCENE_CSS.toExternalForm());
            lblAutoPaste.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            settingsHeading.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        }
        stage.show();

    }

    private void creatDarkThemeLogic() {
        darkLightTheme.getItems().addAll("Dark Theme", "Light Theme");
        darkLightTheme.setTranslateY(210);
        darkLightTheme.setTranslateX(130);

        darkLightTheme.setValue(AppSettings.GET.mainTheme().equals("Dark") ? "Dark Theme" : "Light Theme");


        darkLightTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (AppSettings.GET.mainTheme().equals("LIGHT")) {
                AppSettings.SET.mainTheme("Light");
            } else {
                AppSettings.SET.mainTheme("Dark");
            }
        });

        darkLightTheme.setOnAction(e -> {
            applyTheme(darkLightTheme.getValue().equals("Dark Theme") ? "Dark" : "Light");
        });


    }

    private void applyTheme(String theme) {

        boolean isDark = theme.equals("Dark");
        AppSettings.SET.mainTheme(isDark ? "Dark" : "Light");
        if (isDark) {
            settingsScene.getStylesheets().remove(getClass().getResource("/CSS/Label.css").toExternalForm());
            settingsScene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
            Drifty_GUI.getScene().getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
            if (Drifty_GUI.getAboutScene() != null) {
                Drifty_GUI.getAboutScene().getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
            }
        } else {
            AppSettings.SET.mainTheme("LIGHT");
            settingsScene.getStylesheets().remove(getClass().getResource("/CSS/DarkTheme.css").toExternalForm());
            Drifty_GUI.getScene().getStylesheets().remove(getClass().getResource("/CSS/DarkTheme.css").toExternalForm());
            settingsScene.getStylesheets().add(Constants.SCENE_CSS.toExternalForm());
            Drifty_GUI.getScene().getStylesheets().add(Constants.SCENE_CSS.toExternalForm());
            if (Drifty_GUI.getAboutScene() != null) {
                Drifty_GUI.getAboutScene().getStylesheets().clear();
                Drifty_GUI.getAboutScene().getStylesheets().add(Constants.SCENE_CSS.toExternalForm());

            }
        }


        Paint color = isDark ? Color.WHITE : LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)");
        // Labels
        lblAutoPaste.setTextFill(color);
        lblDwnDir.setTextFill(color);
        lblTheme.setTextFill(color);
        settingsHeading.setTextFill(color);
        for (Node node : Drifty_GUI.getAboutRoot().getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setTextFill(color);
            }
        }

        String style = isDark ? "-fx-text-fill: White;" : "-fx-text-fill: Black;";
        // TextFields
        UIController.form.tfDir.setStyle(style);
        UIController.form.tfFilename.setStyle(style);
        UIController.form.tfLink.setStyle(style);
        tfCurrDir.setStyle(style + "-fx-font-weight: Bold");

        setupButtonGraphics("Light");

        // Banner and Logo
        String bannerPath = isDark ? "/Backgrounds/DriftyMain Dark.png" : "/Backgrounds/DriftyMain.png";
        String splashPath = isDark ? "/Splash Dark.png" : "/Splash.png";
        Constants.IMG_MAIN_GUI_BANNER = new Image(Objects.requireNonNull(Constants.class.getResource(bannerPath)).toExternalForm());
        MainGridPane.ivLogo.setImage(Constants.IMG_MAIN_GUI_BANNER);
        Constants.IMG_SPLASH = new Image(Objects.requireNonNull(Constants.class.getResource(splashPath)).toExternalForm());
        Drifty_GUI.getAppIcon().setImage(Constants.IMG_SPLASH);


//        // handeling the button styles
        String backColorRealesd = isDark ? "-fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" :
                " -fx-background-color: linear-gradient(rgb(54,151,225) 18%, rgb(121,218,232) 90%, rgb(126,223,255) 95%);";


        String backColorPressed = isDark ? " -fx-background-color: linear-gradient(rgb(11, 118, 220) 20%, rgb(33, 31, 131) 65%, rgb(2, 2, 168) 100%);" :
                " -fx-background-color: linear-gradient(rgb(126,223,255) 20%, rgb(121,218,232) 20%, " +
                        "rgb(54,151,225) 100%);";

        button.setStyle(style +
                "    -fx-font-weight: Bold;" +
                backColorRealesd +
                "    -fx-border-color: black;"
        );
        button.setOnMousePressed(ev -> {
            button.setStyle(
                    style +
                            "-fx-font-weight: Bold;" +
                            backColorPressed +
                            "-fx-border-color: black;"

            );
        });
        button.setOnMouseReleased(ev -> {
            button.setStyle(style +
                    "-fx-font-weight: Bold;" +
                    backColorRealesd +
                    "    -fx-border-color: black;");
        });


    }

    private void creatAutoPasteCheck() {
        autoPasteCheck.setSelected(AppSettings.GET.alwaysAutoPaste());
        autoPasteCheck.setTranslateX(160);
        autoPasteCheck.setTranslateY(115);
        autoPasteCheck.setMaxWidth(5.0);
        autoPasteCheck.setSelected(UIController.isAutoPaste());
        autoPasteCheck.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.SET.mainAutoPaste(newValue)));
        autoPasteCheck.setOnAction(e -> {
            UIController.form.cbAutoPaste.setSelected(autoPasteCheck.isSelected());
        });
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


        lblDwnDir.setTranslateX(-160);
        lblDwnDir.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblDwnDir.setTranslateY(-50);
        lblDwnDir.setStyle(
                "-fx-font-weight: Bold ; -fx-font-size:20px"
        );
        lblDwnDir.setTextFill(AppSettings.GET.mainTheme().equals("Dark") ? Color.WHITE : LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));


    }

    private void creatTfDir() {
        tfCurrDir.setMaxWidth(200);
        tfCurrDir.setTranslateY(-85);
        tfCurrDir.setTranslateX(150);


    }

    private void creatDirBtn() {
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
        String directoryPath = selectedDirectory != null ? selectedDirectory.getAbsolutePath() : Get.lastDownloadFolder();
        UIController.form.tfDir.setText(directoryPath);
        tfCurrDir.setText(directoryPath);
        UIController.form.tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                UIController.getDirExist().setValue(false);
                if (newValue.isEmpty()) {
                    Environment.getMessageBroker().msgDirError("Directory cannot be empty!");
                } else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        Environment.getMessageBroker().msgDirInfo("Directory exists!");
                        UIController.getDirExist().setValue(true);
                    } else {
                        Environment.getMessageBroker().msgDirError("Directory does not exist or is not a directory!");
                    }
                }
            }
        }));
    }


}
