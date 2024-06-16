package ui;

import gui.preferences.AppSettings;
import gui.support.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Settings {

    private final String nl = System.lineSeparator();
    public static CheckBox autoPasteCheck = new CheckBox();

    public Settings() {
        Stage stage = Constants.getStage("Settings", false);
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);



        ImageView appIcon = new ImageView(Constants.IMG_SPLASH);
        appIcon.setFitWidth(Constants.SCREEN_WIDTH * .2);
        appIcon.setFitHeight(Constants.SCREEN_HEIGHT * .2);
        appIcon.setPreserveRatio(true);

        // the auto paste feat.
        Label lblAutoPaste = new Label("Auto-Paste" );
        lblAutoPaste.setAlignment(Pos.TOP_CENTER);
        lblAutoPaste.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblAutoPaste.setTextFill(LinearGradient.valueOf("linear-gradient(to right, #0f0c29, #302b63, #24243e)"));



        autoPasteCheck.setSelected(AppSettings.GET.alwaysAutoPaste());
        autoPasteCheck.setTranslateX(100);
        autoPasteCheck.setTranslateY(37);
        autoPasteCheck.setMaxWidth(5.0);
        autoPasteCheck.setSelected(UIController.isAutoPaste());
        autoPasteCheck.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.SET.mainAutoPaste(newValue)));
        autoPasteCheck.setOnAction( e-> {
            UIController.form.cbAutoPaste.setSelected(autoPasteCheck.isSelected());
        });


        // Dark-ligh Theme
        ChoiceBox<String> darkLightTheme = new ChoiceBox<>();
        darkLightTheme.getItems().addAll("Dark Theme", "Ligh Theme");
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


        root.getChildren().addAll(appIcon,darkLightTheme,autoPasteCheck ,lblAutoPaste);

        Scene settingsScene = Constants.getScene(root);
        stage.setMinHeight(Constants.SCREEN_HEIGHT * .55);
        stage.setMinWidth(Constants.SCREEN_WIDTH * .5);
        stage.setScene(settingsScene);
        stage.show();
    }

}
