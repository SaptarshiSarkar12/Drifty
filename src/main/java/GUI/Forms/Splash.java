package GUI.Forms;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Splash extends Application {


    private static Splash INSTANCE;

    public static void main(String[] args) {
        launch(args);
    }

    public static void close() {
        INSTANCE.stage.close();
    }

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = Constants.getStage(primaryStage);
        INSTANCE = this;
        stage = Constants.getStage();
        Image image = new Image(Constants.SPLASH.toExternalForm());
        double width = image.getWidth();
        double fitWidth = Constants.screenSize.getWidth() * .45;
        ImageView ivSplash = new ImageView(image);
        ivSplash.setPreserveRatio(true);
        ivSplash.setFitWidth(fitWidth);
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: transparent");
        pane.getChildren().add(ivSplash);
        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.show();
        new Thread(() -> new Main().start()).start();
    }

}
