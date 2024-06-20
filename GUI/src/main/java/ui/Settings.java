package ui;

import gui.init.Environment;
import gui.preferences.AppSettings;
import gui.support.Constants;
import javafx.application.Platform;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.Drifty_GUI;
import preferences.Get;

import java.io.File;
import java.util.Objects;
import java.util.prefs.Preferences;

import static preferences.Labels.LAST_FOLDER;
import static ui.UIController.DIRECTORY_EXISTS;

public class Settings {
    Scene settingsScene;
    private ChoiceBox<String> darkLightTheme = new ChoiceBox<>();

    Label lblDwnDir = new Label("Default Download Directory" );

    private  TextField tfCurrDir = new TextField(UIController.form.tfDir.getText());

    private final String nl = System.lineSeparator();
    public static CheckBox autoPasteCheck = new CheckBox();

    private void changeFormBtn(String theme){
        if(theme.equals("Dark")){
            UIController.form.btnStart.setGraphic(null);
            Image imageUp = new Image(Objects.requireNonNull(Constants.class.getResource("/Buttons/Start/StartUp Dark.png")).toExternalForm());
            Image imageDown = new Image(Objects.requireNonNull(Constants.class.getResource("/Buttons/Start/StartDown Dark.png")).toExternalForm());
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
        }else{
            UIController.form.btnStart.setGraphic(null);
            Image imageUp = new Image(Objects.requireNonNull(Constants.class.getResource("/Buttons/Start/StartUp.png")).toExternalForm());
            Image imageDown = new Image(Objects.requireNonNull(Constants.class.getResource("/Buttons/Start/StartDown.png")).toExternalForm());
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
    }
    Label lblTheme = new Label("Theme" );

    Label settingsHeading = new Label("Settings" );
    private Button button = new Button("Select Directory");
    private Stage stage = Constants.getStage("Settings", false);
    Label lblAutoPaste = new Label("Auto-Paste" );

    public Settings() {

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);

        creatTfDir();

        creatLabels();

        creatAutoPasteCheck();

        creatDirBtn();

        creatDarkThemeLogic();


        root.getChildren().addAll(darkLightTheme,autoPasteCheck ,lblTheme , lblAutoPaste , settingsHeading, button , lblDwnDir , tfCurrDir);
         settingsScene = Constants.getScene(root);
        Constants.addCSS(settingsScene , Constants.SCENE_CSS);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        stage.setScene(settingsScene);


        if(AppSettings.GET.mainTheme().toString().equals("Dark")){
            settingsScene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
            lblAutoPaste.setTextFill(Color.WHITE);
            lblTheme.setTextFill(Color.WHITE);
            tfCurrDir.setStyle("-fx-text-fill: white ; -fx-font-weight: Bold");
            settingsHeading.setTextFill(Color.WHITE);

        }else{
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

        darkLightTheme.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected: " + darkLightTheme.getItems().get(newValue.intValue()));
        });

        darkLightTheme.setValue(AppSettings.GET.mainTheme().equals("Dark")? "Dark Theme": "Ligh Theme");


        darkLightTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(AppSettings.GET.mainTheme().equals("LIGHT")){
                System.out.println("This is the light theme settings");
            }else{
                System.out.println("This is the dark theme settings");
                AppSettings.SET.mainTheme("Dark");
            }
        });

        darkLightTheme.setOnAction(e->{
            if(darkLightTheme.getValue().equals("Dark Theme")){
                AppSettings.SET.mainTheme("Dark");
                settingsScene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
                Drifty_GUI.scene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
                settingsScene.getStylesheets().remove(getClass().getResource("/CSS/Label.css").toExternalForm());

                lblAutoPaste.setTextFill(Color.WHITE);
                lblDwnDir.setTextFill(Color.WHITE);
                lblTheme.setTextFill(Color.WHITE);
                settingsHeading.setTextFill(Color.WHITE);
                UIController.form.tfDir.setStyle("-fx-text-fill: White;");
                if (Drifty_GUI.aboutScene != null){
                    Drifty_GUI.aboutScene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());

                }
                for (Node node : Drifty_GUI.aboutRoot.getChildren()) {
                    if (node instanceof Label) {
                        ((Label) node).setTextFill(Color.WHITE);
                    }
                }
                settingsHeading.setTextFill(Color.WHITE);
                UIController.form.tfDir.setStyle("-fx-text-fill: White;");
                UIController.form.tfFilename.setStyle("-fx-text-fill: White;");
                UIController.form.tfLink.setStyle("-fx-text-fill: White;");
                changeFormBtn("Dark");
                Constants.IMG_MAIN_GUI_BANNER = new Image(Objects.requireNonNull(Constants.class.getResource("/Backgrounds/DriftyMain Dark.png")).toExternalForm());
                MainGridPane.ivLogo.setImage(Constants.IMG_MAIN_GUI_BANNER);


                Constants.IMG_SPLASH = new Image(Objects.requireNonNull(Constants.class.getResource("/Splash Dark.png")).toExternalForm());
                Drifty_GUI.appIcon.setImage(Constants.IMG_SPLASH);
                tfCurrDir.setStyle("-fx-text-fill: white ; -fx-font-weight: Bold");
                lblDwnDir.setTextFill(Color.WHITE);
                button.setStyle(" -fx-text-fill: white;\n" +
                        "    -fx-font-weight: Bold;"+
                        "    -fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" +
                        "    -fx-border-color: black;"
                );
                button.setOnMousePressed(ev->{
                    button.setStyle(
                            "-fx-text-fill: white;" +
                                    "-fx-font-weight: Bold;"+
                                    " -fx-background-color: linear-gradient(rgb(11, 118, 220) 20%, rgb(33, 31, 131) 65%, rgb(2, 2, 168) 100%);\n" +
                                    "-fx-border-color: black;"

                    );        });
                button.setOnMouseReleased(ev->{
                    button.setStyle(" -fx-text-fill: white;\n" +
                            "-fx-font-weight: Bold;"+
                            "-fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" +
                            "    -fx-border-color: black;");
                });

            }
            else {
                AppSettings.SET.mainTheme("LIGHT");
                lblAutoPaste.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
                lblTheme.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
                settingsScene.getStylesheets().remove(getClass().getResource("/CSS/DarkTheme.css").toExternalForm());
                Drifty_GUI.scene.getStylesheets().remove(getClass().getResource("/CSS/DarkTheme.css").toExternalForm());
                settingsHeading.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
                if (Drifty_GUI.aboutScene != null){
                    Drifty_GUI.aboutScene.getStylesheets().remove(getClass().getResource("/CSS/DarkTheme.css").toExternalForm());
                }
                for (Node node : Drifty_GUI.aboutRoot.getChildren()) {
                    if (node instanceof Label) {
                        ((Label) node).setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
                    }
                }
                settingsHeading.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
                UIController.form.tfDir.setStyle("-fx-text-fill: Black;");
                UIController.form.tfFilename.setStyle("-fx-text-fill: Black;");
                UIController.form.tfLink.setStyle("-fx-text-fill: Black;");
                changeFormBtn("Light");



                Constants.IMG_MAIN_GUI_BANNER = new Image(Objects.requireNonNull(Constants.class.getResource("/Backgrounds/DriftyMain.png")).toExternalForm());
                MainGridPane.ivLogo.setImage(Constants.IMG_MAIN_GUI_BANNER);

                Constants.IMG_SPLASH = new Image(Objects.requireNonNull(Constants.class.getResource("/Splash.png")).toExternalForm());
                Drifty_GUI.appIcon.setImage(Constants.IMG_SPLASH);
                tfCurrDir.setStyle("-fx-text-fill: black ; -fx-font-weight: Bold");
                lblDwnDir.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));

                button.setStyle(" -fx-background-color: linear-gradient(rgb(54,151,225) 18%, rgb(121,218,232) 90%, rgb(126,223,255) 95%);\n" +
                        "-fx-border-color: black;");
                button.setOnMousePressed(ev->{
                    button.setStyle("    -fx-background-color: linear-gradient(rgb(126,223,255) 20%, rgb(121,218,232) 20%, rgb(54,151,225) 100%);\n");
                });
                button.setOnMouseReleased(ev->{
                    button.setStyle(" -fx-background-color: linear-gradient(rgb(54,151,225) 18%, rgb(121,218,232) 90%, rgb(126,223,255) 95%);\n" +
                            "-fx-border-color: black;");
                });


            }
        });


    }

    private void creatAutoPasteCheck() {
        autoPasteCheck.setSelected(AppSettings.GET.alwaysAutoPaste());
        autoPasteCheck.setTranslateX(160);
        autoPasteCheck.setTranslateY(115);
        autoPasteCheck.setMaxWidth(5.0);
        autoPasteCheck.setSelected(UIController.isAutoPaste());
        autoPasteCheck.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.SET.mainAutoPaste(newValue)));
        autoPasteCheck.setOnAction( e-> {
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
        lblDwnDir.setTextFill(AppSettings.GET.mainTheme().equals("Dark")? Color.WHITE : LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));



    }

    private void creatTfDir() {
        tfCurrDir.setMaxWidth(200);
        tfCurrDir.setTranslateY(-85);
        tfCurrDir.setTranslateX(150);



    }

    private void creatDirBtn() {
        button.setTranslateY(50);
        if(AppSettings.GET.mainTheme().equals("Dark")){
            button.setStyle(" -fx-text-fill: white;\n" +
                    "    -fx-font-weight: Bold;"+
                    "    -fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" +
                    "    -fx-border-color: black;"
            );
            button.setOnMousePressed(e->{
                button.setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-font-weight: Bold;"+
                                " -fx-background-color: linear-gradient(rgb(11, 118, 220) 20%, rgb(33, 31, 131) 65%, rgb(2, 2, 168) 100%);\n" +
                                "-fx-border-color: black;"

                );        });
            button.setOnMouseReleased(e->{
                button.setStyle(" -fx-text-fill: white;\n" +
                        "-fx-font-weight: Bold;"+
                        "-fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" +
                        "    -fx-border-color: black;");
            });

        }else{
            button.getStyleClass().add("button");

        }


        button.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedDirectory = chooser.showDialog(this.stage);
            if(selectedDirectory!=null){
                UIController.form.tfDir.setText(selectedDirectory.getAbsolutePath());
                tfCurrDir.setText(UIController.form.tfDir.getText());


            }else{
                UIController.form.tfDir.setText(Get.lastDownloadFolder());
                tfCurrDir.setText(UIController.form.tfDir.getText());

            }
            UIController.form.tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
                if (!newValue.equals(oldValue)) {
                    UIController.DIRECTORY_EXISTS.setValue(false);
                    if (newValue.isEmpty()) {
                        UIController.M.msgDirError("Directory cannot be empty!");
                    } else {
                        File folder = new File(newValue);
                        if (folder.exists() && folder.isDirectory()) {
                            UIController.M.msgDirInfo("Directory exists!");
                            DIRECTORY_EXISTS.setValue(true);
                        } else {
                            UIController.M.msgDirError("Directory does not exist or is not a directory!");
                        }
                    }
                }
            }));
        });


    }

}
