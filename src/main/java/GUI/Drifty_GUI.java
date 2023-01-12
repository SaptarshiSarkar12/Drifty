package GUI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Drifty_GUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Hello World");
        label.setAlignment(Pos.CENTER);
        primaryStage.setScene(new Scene(label, 800, 300));
        primaryStage.setTitle("Drifty GUI [Under Development]");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
