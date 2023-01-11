package GUI;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
public class Drifty_GUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Label label = new Label("Hello World");
        label.setAlignment(Pos.CENTER);
        primaryStage.setScene(new Scene(label, 300, 250));
        primaryStage.setTitle("Drifty GUI Application [Under Development]");
        primaryStage.show();
    }
}
