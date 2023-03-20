package GUI;

import Backend.Drifty;
import Utils.DriftyUtility;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

public class Drifty_GUI extends Application {
    static Stage driftyInitialWindow;
    static VBox root = new VBox();
    static Scene mainScene = new Scene(root);
    static MenuBar menuBar;
    static Text drifty;
    static VBox input;
    static TextField linkInputText;
    static TextField directoryInputText;
    static String linkToFile;
    static String directoryForDownloading;
    static String fileName;
    static boolean flag = true;
    static Text linkOutputText;
    static Text directoryOutputText;
    static Text fileNameOutputText;
    static Text downloadOutputText;
    static boolean isDownloadButtonPressed;
    static Button downloadButton;
    @Override
    public void start(Stage mainWindow) {
        driftyInitialWindow = mainWindow;
        initializeScreen();
        root.getChildren().addAll(menuBar, drifty);
        takeInputs();
        instantInputValidate();

        mainWindow.setScene(mainScene);
        mainWindow.show();
    }

    private void instantInputValidate() {
        Task<Void> validateLink = new Task<>() {
            @Override
            protected Void call() {
                String previous_url = "";
                while (flag) {
                    String url = String.valueOf(linkInputText.getCharacters());

                    if (!url.equals(previous_url)) { // checks whether the link is edited or not (helps in optimising and increase performance)
                        linkOutputText.setFill(Color.RED);
                        if (url.contains(" ")) {
                            linkOutputText.setText("Link should not contain whitespace characters!");
                            downloadButton.setDisable(true);
                        } else if (url.length() == 0) {
                            linkOutputText.setText("Link cannot be empty!");
                            downloadButton.setDisable(true);
                        } else {
                            try {
                                DriftyUtility.isURLValid(url);
                                linkOutputText.setFill(Color.GREEN);
                                linkOutputText.setText("Link is valid!");
                                downloadButton.setDisable(false);
                            } catch (Exception e) {
                                linkOutputText.setText(e.getMessage());
                                downloadButton.setDisable(true);
                            }
                        }
                        previous_url = url;
                    }
                }
                return null;
            }
        };
        new Thread(validateLink).start();

        Task<Void> validateDirectory = new Task<>() {
            @Override
            protected Void call() {
                String previous_directory = "";
                while (flag) {
                    String directory = String.valueOf(directoryInputText.getCharacters());
                    if (!directory.equals(previous_directory)){
                        directoryOutputText.setFill(Color.RED);
                        if (directory.length() == 0){
                            directoryOutputText.setText("Directory cannot be empty!");
                            downloadButton.setDisable(true);
                        } else {
                            File file = new File(directory);
                            if (file.exists() && file.isDirectory()) {
                                directoryOutputText.setFill(Color.GREEN);
                                directoryOutputText.setText("Directory exists!");
                                downloadButton.setDisable(false);
                            } else {
                                directoryOutputText.setText("Directory does not exist!");
                                downloadButton.setDisable(true);
                            }
                        }
                    }
                    previous_directory = directory;
                }
                return null;
            }
        };
        new Thread(validateDirectory).start();
    }

    private static void takeInputs() {
        input = new VBox(); // main starting screen
        input.setSpacing(30);

        VBox inputLayout = new VBox();
        inputLayout.setSpacing(20);

        VBox linkLayout = new VBox(); // Link layout
        HBox linkInputLayout = new HBox(); // link input
        Text linkText = new Text("Link : ");
        linkText.setFont(Font.font("Arial", 23));
        linkInputText = new TextField(); // link input area
        linkInputText.setAlignment(Pos.CENTER);
        linkInputText.setPrefColumnCount(60);
        linkInputLayout.getChildren().addAll(linkText, linkInputText);
        linkInputLayout.setAlignment(Pos.CENTER);
        linkOutputText = new Text(); // link output
        linkLayout.getChildren().addAll(linkInputLayout, linkOutputText);
        linkLayout.setAlignment(Pos.CENTER);

        VBox directoryLayout = new VBox();
        HBox directoryInputLayout = new HBox(); // directory choosing menu layout
        Text directoryText = new Text("Directory : ");
        directoryText.setFont(Font.font("Arial", 23));
        directoryInputText = new TextField();
        new SetDefaultDirectoryInput().run();
        directoryInputText.setPrefColumnCount(50);
        directoryInputText.setAlignment(Pos.CENTER);
        directoryOutputText = new Text(); // directory output text
        directoryInputLayout.getChildren().addAll(directoryText, directoryInputText);
        directoryInputLayout.setAlignment(Pos.CENTER);
        directoryLayout.getChildren().addAll(directoryInputLayout, directoryOutputText);
        directoryLayout.setAlignment(Pos.CENTER);

        VBox fileNameLayout = new VBox(); // file name I/O layout
        HBox fileNameInput = new HBox(); // file name input layout
        Text fileNameText = new Text("File Name (with extension) : ");
        fileNameText.setFont(Font.font("Arial", 23));
        TextField fileNameInputText = new TextField(); // file name input area
        fileNameInputText.setPrefColumnCount(30);
        fileNameInputText.setAlignment(Pos.CENTER);
        fileNameInput.getChildren().addAll(fileNameText, fileNameInputText);
        fileNameInput.setAlignment(Pos.CENTER);
        fileNameOutputText = new Text(); // file name output
        fileNameLayout.getChildren().addAll(fileNameInput, fileNameOutputText);
        fileNameLayout.setAlignment(Pos.CENTER);

        VBox downloadLayout = new VBox();
        downloadButton = new Button("Download");
        downloadOutputText = new Text();
        downloadLayout.getChildren().addAll(downloadButton, downloadOutputText);
        downloadLayout.setAlignment(Pos.CENTER);

        Button downloadCancelButton = new Button("Cancel Download");
        downloadCancelButton.setAlignment(Pos.CENTER);

        EventHandler<MouseEvent> download = MouseEvent -> {
            isDownloadButtonPressed = true;
            linkInputText.setEditable(false);
            directoryInputText.setEditable(false);
            fileNameInputText.setEditable(false);

            linkToFile = String.valueOf(linkInputText.getCharacters());
            if (directoryForDownloading == null){
                directoryForDownloading = String.valueOf(directoryInputText.getCharacters());
            }
            fileName = String.valueOf(fileNameInputText.getCharacters());
            Drifty backend = new Drifty(linkToFile, null, fileName, linkOutputText, directoryOutputText, downloadOutputText, fileNameOutputText);
            backend.start();

            linkInputText.setEditable(true);
            directoryInputText.setEditable(true);
            fileNameInputText.setEditable(true);
            isDownloadButtonPressed = false;
        };
        downloadButton.setOnMouseClicked(download);
        inputLayout.getChildren().addAll(linkLayout, directoryLayout, fileNameLayout);

        input.getChildren().addAll(inputLayout);
        root.getChildren().addAll(input, downloadLayout);
    }

    public static void main(String[] args) {
        launch();
    }

    private static void initializeScreen(){
        driftyInitialWindow.setTitle("Drifty GUI");
        driftyInitialWindow.setMaximized(true);
        driftyInitialWindow.setResizable(false);
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

        EventHandler<ActionEvent> exitClicked = actionEvent -> {
            stopInstantInputValidate();
            System.exit(0);
        };
        exit.setOnAction(exitClicked);

        drifty = new Text("Drifty");
        drifty.setFont(Font.font("Times Roman", FontWeight.BOLD, 100));
        drifty.setFill(Color.ROYALBLUE);
        drifty.setStroke(Color.DEEPSKYBLUE);
        drifty.setCache(true);
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(90);
    }

    protected static TextField getDirectoryInputText() {
        return directoryInputText;
    }

    protected static TextField getLinkInputText() {
        return linkInputText;
    }

    protected static Text getLinkOutputText() {
        return linkOutputText;
    }

    private static void stopInstantInputValidate(){
        flag = false;
    }
}
