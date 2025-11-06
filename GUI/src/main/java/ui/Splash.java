package ui;

import gui.support.Constants;
import gui.init.Environment;
import javafx.animation.PauseTransition;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import preferences.AppSettings;

import java.util.Objects;

public class Splash extends Preloader {
    private Stage stage;
    private Scene scene;

    @Override
    public void init() {
        Image splashImage = new Image(Objects.requireNonNull(Constants.SPLASH).toExternalForm());
        ImageView ivSplash = new ImageView(splashImage);
        double fitWidth = Constants.SCREEN_WIDTH * .45;
        ivSplash.setPreserveRatio(true);
        ivSplash.setFitWidth(fitWidth);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(ivSplash);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: transparent");
        scene = new Scene(vBox);
        scene.setFill(Color.TRANSPARENT);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = Constants.getStage("Splash", false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        try {
            stage.show();
        } catch (Exception e) {
            if (Environment.getMessageBroker() != null) {
                Environment.getMessageBroker().msgLogError("Error showing splash window: " + e.getMessage());
            } else {
                System.err.println("Error showing splash window: " + e.getMessage());
            }
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        StateChangeNotification.Type type = info.getType();
        if (Objects.requireNonNull(type) == StateChangeNotification.Type.BEFORE_START) {
            double time;
            if (AppSettings.GET.ytDlpUpdating()) {
                time = 2.0;
            } else {
                time = 1.0;
            }
            PauseTransition delay = new PauseTransition(Duration.seconds(time));
            delay.setOnFinished(event -> stage.close());
            delay.play();
        }
    }
}
