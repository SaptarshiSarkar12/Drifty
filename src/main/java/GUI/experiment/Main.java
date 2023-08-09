package GUI.experiment;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static GUI.experiment.Constants.*;
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
        gridPane = new MainGridPane(FormType.MAIN);
        placeControl(gridPane,40,40,40,40);
        ap.getChildren().add(gridPane);
        stage = new Stage();
        scene = new Scene(ap);
        scene.getStylesheets().add(contextMenuCSS.toExternalForm());
        scene.getStylesheets().add(labelCSS.toExternalForm());
        scene.getStylesheets().add(menuCSS.toExternalForm());
        scene.getStylesheets().add(checkBoxCSS.toExternalForm());
        scene.getStylesheets().add(textFieldCSS.toExternalForm());
        scene.getStylesheets().add(vBoxCSS.toExternalForm());
        scene.getStylesheets().add(sceneCSS.toExternalForm());
        scene.getStylesheets().add(progressBarCSS.toExternalForm());
        scene.getStylesheets().add(listViewCSS.toExternalForm());
        stage.setScene(scene);
        stage.show();
        FormLogic.initLogic(gridPane);
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
