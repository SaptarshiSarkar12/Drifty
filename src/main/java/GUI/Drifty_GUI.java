package GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Drifty_GUI extends Application {
    @Override
    public void start(Stage mainWindow) {
        mainWindow.setTitle("Drifty GUI [Under Development]");
        mainWindow.setMaximized(true);

        BorderPane root = new BorderPane();
        Scene mainScene = new Scene(root);
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");

        MenuItem exit = new MenuItem("Exit");
        menu.getItems().addAll(exit);
        root.setTop(menuBar);
        menuBar.getMenus().addAll(menu);
        mainWindow.setScene(mainScene);
        mainWindow.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
