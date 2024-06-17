package ui;

import gui.preferences.AppSettings;
import gui.support.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import main.Drifty_GUI;

public class Settings {

    private final String nl = System.lineSeparator();
    public static CheckBox autoPasteCheck = new CheckBox();

    public Settings() {
        Stage stage = Constants.getStage("Settings", false);
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);

        Label settingsHeading = new Label("Settings" );
        settingsHeading.setAlignment(Pos.TOP_CENTER);
        settingsHeading.setFont(Font.font("monospace", FontWeight.EXTRA_BOLD, 100));
        settingsHeading.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        settingsHeading.setTranslateY(-160);





        // the auto paste feat.
        Label lblAutoPaste = new Label("Auto-Paste" );
        lblAutoPaste.setAlignment(Pos.TOP_CENTER);
        lblAutoPaste.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblAutoPaste.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        Label lblTheme = new Label("Theme" );
        lblTheme.setAlignment(Pos.TOP_CENTER);
        lblTheme.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTheme.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
        lblTheme.setTranslateY(100);
        lblTheme.setTranslateX(-20);



        autoPasteCheck.setSelected(AppSettings.GET.alwaysAutoPaste());
        autoPasteCheck.setTranslateX(130);
        autoPasteCheck.setTranslateY(70);
        autoPasteCheck.setMaxWidth(5.0);
        autoPasteCheck.setSelected(UIController.isAutoPaste());
        autoPasteCheck.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.SET.mainAutoPaste(newValue)));
        autoPasteCheck.setOnAction( e-> {
            UIController.form.cbAutoPaste.setSelected(autoPasteCheck.isSelected());
        });


        // Dark-ligh Theme
        ChoiceBox<String> darkLightTheme = new ChoiceBox<>();
        darkLightTheme.getItems().addAll("Dark Theme", "Ligh Theme");
        darkLightTheme.setTranslateY(175);
        darkLightTheme.setTranslateX(130);

        darkLightTheme.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected: " + darkLightTheme.getItems().get(newValue.intValue()));
        });

        darkLightTheme.setValue(AppSettings.GET.mainTheme().equals("Dark")? "Dark Theme": "Ligh Theme");
//        darkLightTheme.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
//                AppSettings.SET.mainTheme(Boolean.parseBoolean(newValue))));

        darkLightTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(AppSettings.GET.mainTheme().equals("LIGHT")){
                System.out.println("This is the light theme settings");
            }else{
                System.out.println("This is the dark theme settings");
                AppSettings.SET.mainTheme("Dark");
            }
        });
        root.getChildren().addAll(darkLightTheme,autoPasteCheck ,lblTheme , lblAutoPaste , settingsHeading);
        Scene settingsScene = Constants.getScene(root);

        darkLightTheme.setOnAction(e->{
            if(darkLightTheme.getValue().equals("Dark Theme")){
                AppSettings.SET.mainTheme("Dark");
                settingsScene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
                Drifty_GUI.scene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
                lblAutoPaste.setTextFill(Color.WHITE);
                lblTheme.setTextFill(Color.WHITE);
                settingsScene.getStylesheets().remove(getClass().getResource("/CSS/Label.css").toExternalForm());
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

            }
        });
        Constants.addCSS(settingsScene , Constants.SCENE_CSS);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        stage.setScene(settingsScene);
        if(AppSettings.GET.mainTheme().toString().equals("Dark")){
            settingsScene.getStylesheets().add(Constants.DARK_THEME_CSS.toExternalForm());
            lblAutoPaste.setTextFill(Color.WHITE);
            lblTheme.setTextFill(Color.WHITE);
            settingsHeading.setTextFill(Color.WHITE);

        }else{
            settingsScene.getStylesheets().add(Constants.SCENE_CSS.toExternalForm());
            lblAutoPaste.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));
            settingsHeading.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));


        }
        stage.show();

    }

}
