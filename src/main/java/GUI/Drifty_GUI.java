package GUI;

import Utils.MessageBroker;
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

import static Utils.DriftyUtility.findFilenameInLink;
import static Utils.DriftyUtility.isYoutubeLink;


public class Drifty_GUI extends Application {
    static Stage driftyInitialWindow;
    static VBox root = new VBox();
    static Scene mainScene = new Scene(root);
    static MenuBar menuBar;
    static Text drifty;
    static VBox input;
    static String directoryForDownloading;
    static String linkToFile;
    static String fileName;
    static Text linkValidOrNot;
    static TextField directoryText;
    @Override
    public void start(Stage mainWindow) {
        driftyInitialWindow = mainWindow;
        MessageBroker messageBroker = new MessageBroker("GUI", linkValidOrNot, directoryText, , );
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
        linkValidOrNot = new Text();

        VBox directory = new VBox();

        Text customDirectory = new Text("Enter Custom Directory : ");
        customDirectory.setFont(Font.font("Arial", 20));
        TextField customDirectoryInput = new TextField();
        directory.setAlignment(Pos.CENTER);
        customDirectoryInput.setPrefColumnCount(58);

        Text choseDirectory = new Text("Choose Directory : ");
        choseDirectory.setFont(Font.font("Arial", 20));
        ComboBox<String> directoryChoice = new ComboBox<>();
        directoryChoice.getItems().addAll("Default Downloads Folder", "Custom Folder");
        directoryText = new TextField();
        EventHandler<ActionEvent> directoryChosen = actionEvent -> {
            if (directoryChoice.getSelectionModel().getSelectedItem().equals("Custom Folder")){
                directory.getChildren().addAll(customDirectory, customDirectoryInput);
            } else {
                directoryForDownloading = ".";
            }
        };
        directoryChoice.setOnAction(directoryChosen);
        directory.getChildren().addAll(choseDirectory, directoryChoice);

        VBox validateInputs = new VBox();
        Button validateInput = new Button();

        VBox fileNameLayout = new VBox();

        HBox renameFileInput = new HBox();
        Text renameFile = new Text("File Name (with extension) : ");
        renameFile.setFont(Font.font("Arial", 20));
        TextField fileNameText = new TextField();
        renameFileInput.getChildren().addAll(renameFile, fileNameText);

        Text renameFileOutputText = new Text();
        fileNameLayout.getChildren().addAll(renameFileInput, renameFileOutputText);

        boolean fileIsPreviouslyAskedToBeRenamed = false;
        input.getChildren().addAll(link, linkValidOrNot, directory, validateInputs);
        EventHandler<ActionEvent> clickedToValidateInputs = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!fileIsPreviouslyAskedToBeRenamed) {
                    linkToFile = String.valueOf(linkInput.getCharacters()); // Get Link
                    boolean isYoutubeURL = isYoutubeLink(linkToFile);
                    String fileNameFromLink = findFilenameInLink(linkToFile);
                    if ((fileNameFromLink == null || (fileNameFromLink.length() == 0)) && (!isYoutubeURL)) {
                        input.getChildren().addAll(fileNameLayout);
                    }
                } else {
                    linkToFile = String.valueOf(linkInput.getCharacters());
                    directoryForDownloading = String.valueOf(directoryText.getCharacters());
                    fileName = String.valueOf(fileNameText.getCharacters());
                }
            }
        };
        validateInput.setOnAction(clickedToValidateInputs);
        validateInputs.getChildren().addAll(validateInput);

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
