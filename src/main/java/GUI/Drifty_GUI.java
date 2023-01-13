package GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    static String linkToFile;
    @Override
    public void start(Stage mainWindow) {
        driftyInitialWindow = mainWindow;
        initializeScreen();
        takeInputs();

        root.getChildren().addAll(menuBar, drifty, input);
        mainWindow.setScene(mainScene);
        mainWindow.sizeToScene();
        mainWindow.show();
    }

    private static void takeInputs() {
        input = new VBox();
        input.setSpacing(20);

        HBox link = new HBox();
        Text linkText = new Text("Link : ");
        linkText.setFont(Font.font("Arial", 20));
        TextField linkInput = new TextField();
        link.setAlignment(Pos.CENTER);
        linkInput.setPrefColumnCount(60);
        link.getChildren().addAll(linkText, linkInput);

        Button validateLink = new Button("Validate Link");
        EventHandler<ActionEvent> linkEnter = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                linkToFile = String.valueOf(linkInput.getCharacters());
                Text linkValidOrNot = new Text();
                linkValidOrNot.setFont(Font.font("Algerian", FontWeight.MEDIUM, 12));
                if (CLI.utility.DriftyUtility.isURLValid(linkToFile)){
                    linkValidOrNot.setText("Link is Valid");
                } else {
                    linkValidOrNot.setText("Invalid link! Please enter again!");
                }
                input.getChildren().add(linkValidOrNot);
            }
        };
        validateLink.setOnAction(linkEnter);

        HBox directory = new HBox();
        Text directoryText = new Text("Directory : ");
        directoryText.setFont(Font.font("Arial", 20));
        TextField directoryInput = new TextField();
        directory.setAlignment(Pos.CENTER);
        directoryInput.setPrefColumnCount(58);
        directory.getChildren().addAll(directoryText, directoryInput);

        input.getChildren().addAll(link, validateLink, directory);
    }

    public static void main(String[] args) {
        launch();
    }

    private static void initializeScreen(){
        driftyInitialWindow.setTitle("Drifty GUI [Under Development]");
        driftyInitialWindow.setMaximized(true);
        root.setSpacing(15);

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
