package GUI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Drifty_GUI extends Application {
    static Stage driftyInitialWindow;
    static VBox root = new VBox();
    static Scene mainScene = new Scene(root);
    static MenuBar menuBar;
    static Text drifty;
    static VBox input;
    @Override
    public void start(Stage mainWindow) {
        driftyInitialWindow = mainWindow;
        initializeScreen();
        takeInputs();

        root.getChildren().addAll(menuBar, drifty, input);
        mainWindow.setScene(mainScene);
        mainWindow.show();
    }

    private static void takeInputs() {
        input = new VBox();
        input.setSpacing(12);

        HBox link = new HBox();
        Text linkText = new Text("Link : ");
        linkText.setFont(Font.font("Arial", 20));
        TextField linkInput = new TextField();
        link.setAlignment(Pos.CENTER);
        linkInput.setPrefColumnCount(60);
        link.getChildren().addAll(linkText, linkInput);

        HBox directory = new HBox();
        Text directoryText = new Text("Directory : ");
        directoryText.setFont(Font.font("Arial", 20));
        TextField directoryInput = new TextField();
        directory.setAlignment(Pos.CENTER);
        directoryInput.setPrefColumnCount(58);
        directory.getChildren().addAll(directoryText, directoryInput);

        input.getChildren().addAll(link, directory);
    }

    public static void main(String[] args) {
        launch();
    }

    private static void initializeScreen(){
        driftyInitialWindow.setTitle("Drifty GUI [Under Development]");
        driftyInitialWindow.setMaximized(true);
        root.setSpacing(10);

        menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem exit = new MenuItem("Exit");
        menu.getItems().addAll(exit);

        Menu about = new Menu("About");
        MenuItem website = new MenuItem("Website");
        MenuItem help = new MenuItem("Help");
        about.getItems().addAll(website, help);

        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);
        menuBar.getMenus().addAll(menu, about);

        drifty = new Text("Drifty");
        drifty.setFont(Font.font("Times Roman", FontWeight.BOLD, 60));
        drifty.setFill(Color.ROYALBLUE);
        drifty.setStroke(Color.DEEPSKYBLUE);
        root.setAlignment(Pos.TOP_CENTER);
    }
}
