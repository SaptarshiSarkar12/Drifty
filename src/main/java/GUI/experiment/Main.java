package GUI.experiment;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static javafx.scene.layout.AnchorPane.*;


public class Main {

    public Main() {
        createScene();
    }

    private Stage stage;
    private Scene scene;
    private MainGridPane gridPane;

    private void createScene() {
        AnchorPane ap = new AnchorPane();
        gridPane = new MainGridPane();
        placeControl(gridPane,40,40,40,40);
        ap.getChildren().add(gridPane);
        stage = new Stage();
        scene = new Scene(ap);
        stage.setScene(scene);
        stage.show();
    }

    private void placeControl(Node node, double left, double right, double top, double bottom) {
        if (top != -1) {
            setTopAnchor(node, top);
        }
        if (bottom != -1) {
            setBottomAnchor(node, bottom);
        }
        if (left != -1) {
            setLeftAnchor(node, left);
        }
        if (right != -1) {
            setRightAnchor(node, right);
        }
    }

}
