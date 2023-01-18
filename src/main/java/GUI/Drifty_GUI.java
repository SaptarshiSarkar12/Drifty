package GUI;

import Backend.DefaultDownloadFolderLocationFinder;
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
        input.setAlignment(Pos.CENTER);
        Text linkValidOrNot = new Text();
        EventHandler<ActionEvent> linkEnter = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                linkToFile = String.valueOf(linkInput.getCharacters());
                linkValidOrNot.setFont(Font.font("Algerian", FontWeight.MEDIUM, 15));
//                try {
//                    if (isURLValid(linkToFile)){
//                        linkValidOrNot.setText("Link is Valid");
//                        linkValidOrNot.setFill(Color.GREEN);
//                    } else {
//                        linkValidOrNot.setText("Invalid link! Please enter with URL protocol!");
//                        linkValidOrNot.setFill(Color.RED);
//                    }
//                } catch (Exception ignored) { // TODO

//                }
            }
        };
        validateLink.setOnAction(linkEnter);

        HBox directory = new HBox();

        Text customDirectory = new Text("Enter Custom Directory : ");
        customDirectory.setFont(Font.font("Arial", 20));
        TextField customDirectoryInput = new TextField();
        directory.setAlignment(Pos.CENTER);
        customDirectoryInput.setPrefColumnCount(58);

        Text choseDirectory = new Text("Choose Directory : ");
        choseDirectory.setFont(Font.font("Arial", 20));
        ComboBox<String> directoryChoice = new ComboBox<>();
        directoryChoice.getItems().addAll("Default Downloads Folder", "Custom Folder");
        Text defaultDirectory = new Text();
        EventHandler<ActionEvent> directoryChosen = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (directoryChoice.getSelectionModel().getSelectedItem().equals("Default Downloads Folder")){
                    directory.getChildren().addAll(defaultDirectory);
                    defaultDirectory.setText("Default Downloads Folder detected : " + DefaultDownloadFolderLocationFinder.findPath());
                    defaultDirectory.setFill(Color.ALICEBLUE);
                    defaultDirectory.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
                } else {
                    directory.getChildren().addAll(customDirectory, customDirectoryInput);
                }
            }
        };
        directoryChoice.setOnAction(directoryChosen);
        directory.getChildren().addAll(choseDirectory, directoryChoice);
        input.getChildren().addAll(link, validateLink, linkValidOrNot, directory);
    }

    public static void main(String[] args) {
        launch();
    }

    private static void initializeScreen(){
        driftyInitialWindow.setTitle("Drifty GUI [Under Development]");
        driftyInitialWindow.setMaximized(true);
        root.setSpacing(30);
        root.setMinHeight(497);
        root.setMaxWidth(962);

        menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem exit = new MenuItem("Exit");
        menu.getItems().addAll(exit);

        Menu help = new Menu("Help");
        MenuItem website = new MenuItem("Website");
        MenuItem about = new MenuItem("About");
        help.getItems().addAll(website, about);

        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);
        menuBar.getMenus().addAll(menu, help);

        EventHandler<ActionEvent> exitClicked = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.exit(0);
            }
        };
        exit.setOnAction(exitClicked);

        drifty = new Text("Drifty");
        drifty.setFont(Font.font("Times Roman", FontWeight.BOLD, 100));
        drifty.setFill(Color.ROYALBLUE);
        drifty.setStroke(Color.DEEPSKYBLUE);
        drifty.setCache(true);
        root.setAlignment(Pos.TOP_CENTER);
    }
}
