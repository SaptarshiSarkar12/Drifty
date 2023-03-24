package GUI;

import Backend.Drifty;
import Backend.ProgressBarThread;
import Utils.CreateLogs;
import Utils.Constants;
import Utils.Utility;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * This class deals with the Graphical User Interface (GUI) version of Drifty.
 * @since 2.0.0
 * @version 2.0.0
 */
public class Drifty_GUI extends Application {
    static CreateLogs logger = CreateLogs.getInstance();
    static VBox downloadLayout;
    static Stage driftyInitialWindow;
    static ProgressBar downloadProgressBar;
    static VBox root = new VBox();
    static Scene mainScene = new Scene(root);
    static MenuBar menuBar;
    static Text drifty;
    static VBox input;
    static float downloadProgress;
    static TextField linkInputText;
    static TextField directoryInputText;
    static TextField fileNameInputText;
    static String linkToFile;
    static String directoryForDownloading;
    static String fileName;
    static boolean isFileBeingDownloaded = false;
    static Text linkOutputText;
    static Text directoryOutputText;
    static Text fileNameOutputText;
    static Text downloadOutputText;
    static boolean isDownloadButtonPressed;
    static Button downloadButton;
    static boolean isYouTubeURL;

    /**
     * This is the main method which starts the Graphical User Interface (GUI) Application.
     * @since 2.0.0
     * @param mainWindow It is the main window of Graphical User Interface (GUI) version of Drifty - An object of Stage class in JavaFX
     */
    @Override
    public void start(Stage mainWindow) {
        logger.log(Constants.LOGGER_INFO, Constants.GUI_APPLICATION_STARTED); // log a message when the Graphical User Interface (GUI) version of Drifty is triggered to start
        driftyInitialWindow = mainWindow;
        initializeScreen(); // Initializing the screen
        root.getChildren().addAll(menuBar, drifty);
        initializeIOFields(); // Initializing the Input and Output fields
        setDefaultInputs(); // Setting default values for input fields
        startInstantInputValidating(); // Starting the tasks to instantly validate the inputs and show an output message accordingly

        mainWindow.setScene(mainScene); // Setting the main scene
        mainWindow.show(); // Showing the main window
    }

    /**
     * This method initializes the Input and Output fields of the Graphical User Interface (GUI) version of Drifty
     * @since 2.0.0
     */
    private void initializeIOFields() {
        input = new VBox(); // main starting screen
        input.setSpacing(30);

        VBox inputLayout = new VBox();
        inputLayout.setSpacing(20);

        VBox linkLayout = new VBox(); // Link layout
        HBox linkInputLayout = new HBox(); // link input layout
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

        VBox directoryLayout = new VBox(); // Directory Layout
        HBox directoryInputLayout = new HBox(); // directory input layout
        Text directoryText = new Text("Directory : ");
        directoryText.setFont(Font.font("Arial", 23));
        directoryInputText = new TextField(); // directory input area
        directoryInputText.setPrefColumnCount(50);
        directoryInputText.setAlignment(Pos.CENTER);
        directoryOutputText = new Text(); // directory output text area
        directoryInputLayout.getChildren().addAll(directoryText, directoryInputText);
        directoryInputLayout.setAlignment(Pos.CENTER);
        directoryLayout.getChildren().addAll(directoryInputLayout, directoryOutputText);
        directoryLayout.setAlignment(Pos.CENTER);

        VBox fileNameLayout = new VBox(); // file name I/O layout
        HBox fileNameInput = new HBox(); // file name input layout
        Text fileNameText = new Text("File Name (with extension) : ");
        fileNameText.setFont(Font.font("Arial", 23));
        fileNameInputText = new TextField(); // file name input area
        fileNameInputText.setPrefColumnCount(30);
        fileNameInputText.setAlignment(Pos.CENTER);
        fileNameInput.getChildren().addAll(fileNameText, fileNameInputText);
        fileNameInput.setAlignment(Pos.CENTER);
        fileNameOutputText = new Text(); // file name output
        fileNameLayout.getChildren().addAll(fileNameInput, fileNameOutputText);
        fileNameLayout.setAlignment(Pos.CENTER);

        downloadLayout = new VBox();
        downloadButton = new Button("Download");
        downloadOutputText = new Text();
        downloadLayout.getChildren().addAll(downloadOutputText, downloadButton);
        downloadLayout.setAlignment(Pos.CENTER);

        inputLayout.getChildren().addAll(linkLayout, directoryLayout, fileNameLayout);

        EventHandler<MouseEvent> download = MouseEvent -> {
            disableInputs();
            stopInstantInputValidating();
            saveInputs();

            download();

            enableInputs();
            setDefaultInputs();
            startInstantInputValidating();
        };
        downloadButton.setOnMouseClicked(download);

        input.getChildren().addAll(inputLayout);
        root.getChildren().addAll(input, downloadLayout);
    }

    /**
     * This method <b>sets the default values for the input fields</b> in the Graphical User Interface (GUI) version of Drifty.
     * @since 2.0.0
     */
    private void setDefaultInputs() {
        Task<Void> setDefaultDirectory = new Task<>() {
            @Override
            protected Void call() {
                String defaultDirectory = Utility.saveToDefault();
                directoryInputText.setText(defaultDirectory);
                return null;
            }
        };
        setDefaultDirectory.run();

        Task<Void> setDefaultFilename = new Task<>() {
            @Override
            protected Void call() {
                String previous_url = "";
                while (!isFileBeingDownloaded) {
                    String url = String.valueOf(linkInputText.getCharacters());
                    if (!url.equals(previous_url)) {
                        if (!Utility.isYoutubeLink(url)) {
                            String fileName = Utility.findFilenameInLink(url);
                            if (fileName != null) {
                                fileNameInputText.setText(fileName);
                            } else {
                                fileNameInputText.clear();
                            }
                        }
                        previous_url = url;
                    }
                }
                return null;
            }
        };
        new Thread(setDefaultFilename).start();
    }

    /**
     * This method <b>starts</b> the instant input validating task from validating the inputs as the user types them in the respective input fields.
     * @since 2.0.0
     */
    private void startInstantInputValidating() {
        Task<Void> validateLink = new Task<>() {
            @Override
            protected Void call() {
                String previous_url = "";
                while (!isFileBeingDownloaded) {
                    String url = String.valueOf(linkInputText.getCharacters());

                    if (!url.equals(previous_url)) { // checks whether the link is edited or not (helps in optimising and improves performance)
                        linkOutputText.setFill(Color.GREEN);
                        linkOutputText.setText("Validating link ...");
                        if (url.contains(" ")) {
                            linkOutputText.setFill(Color.RED);
                            linkOutputText.setText("Link should not contain whitespace characters!");
                            downloadButton.setDisable(true);
                        } else if (url.length() == 0) {
                            linkOutputText.setFill(Color.RED);
                            linkOutputText.setText("Link cannot be empty!");
                            downloadButton.setDisable(true);
                        } else {
                            try {
                                Utility.isURLValid(url);
                                linkOutputText.setFill(Color.GREEN);
                                linkOutputText.setText("Link is valid!");
                                downloadButton.setDisable(false);
                            } catch (Exception e) {
                                linkOutputText.setFill(Color.RED);
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
                while (!isFileBeingDownloaded) {
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

    /**
     * This method <b>enables</b> the input fields in the Graphical User Interface (GUI) version of Drifty, to be editable.
     * @since 2.0.0
     */
    private void enableInputs() {
        linkInputText.setEditable(true);
        directoryInputText.setEditable(true);
        fileNameInputText.setEditable(true);
        isDownloadButtonPressed = false;
    }

    /**
     * This method <b>disables</b> the input fields in the Graphical User Interface (GUI) version of Drifty, to be editable.
     * @since 2.0.0
     */
    private void disableInputs() {
        isDownloadButtonPressed = true;
        linkInputText.setEditable(false);
        directoryInputText.setEditable(false);
        fileNameInputText.setEditable(false);
    }

    /**
     * This method deals with <b>initializing and calling the Backend</b> to <i>download</i> the file.
     * @since 2.0.0
     */
    private void download() {
        Drifty backend = new Drifty(linkToFile, directoryForDownloading, fileName, linkOutputText, directoryOutputText, downloadOutputText, fileNameOutputText);
        downloadButton.setVisible(false);
        downloadProgressBar = new ProgressBar();
        downloadProgressBar.setScaleX(3);
        downloadProgressBar.setScaleY(1);
        downloadLayout.getChildren().addAll(downloadProgressBar);
        if (isYouTubeURL){
            /*
            Does not work !
             */
//            try {
//                System.out.println("Creating file...");
//                Files.createFile(Path.of("./output.txt"));
//                System.setOut(new PrintStream("./output.txt"));
//            } catch (IOException e) {
//                System.out.println("Failed to create file!");
//            }
//            Task<Void> getYtDlpProgress = new Task<>() {
//                @Override
//                protected Void call() {
//                    getYouTubeVideoDownloadProgress();
//                    return null;
//                }
//            };
//            new Thread(getYtDlpProgress).start();
        } else {
            Task<Void> setProgress = new Task<>() {
                @Override
                protected Void call() {
                    setDownloadProgress();
                    return null;
                }
            };
            new Thread(setProgress).start();
        }
        Task<Void> startDownload = new Task<>() {
            @Override
            protected Void call() {
                backend.start();
                return null;
            }
        };
        Thread download = new Thread(startDownload);
        download.start();
        Task<Void> recreateDownloadSetup = new Task<>() {
            @Override
            protected Void call() {
                while (true){
                    if (!download.isAlive()){
                        downloadProgressBar.setVisible(false);
                        downloadButton.setVisible(true);
                        break;
                    }
                }
                return null;
            }
        };
        new Thread(recreateDownloadSetup).start();
    }

    public static synchronized void getYouTubeVideoDownloadProgress() {
        Scanner sc = new Scanner("./output.txt");
        while (isFileBeingDownloaded) {
            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
        }
    }

    /**
     * This method <b>sets the download progress</b> in the GUI Application.
     */
    private void setDownloadProgress() {
        Task<Void> getProgress = new Task<>() {
            @Override
            protected Void call() {
                while (isFileBeingDownloaded) {
                    downloadProgress = ProgressBarThread.getTotalDownloadPercent();
                }
                return null;
            }
        };
        new Thread(getProgress).start();
        Task<Void> setProgress = new Task<>() {
            @Override
            protected Void call() {
                while (isFileBeingDownloaded) {
                    downloadProgressBar.setProgress(downloadProgress/100);
                }
                return null;
            }
        };
        new Thread(setProgress).start();
    }

    /**
     * This method <b>receives the input</b> from the input fields and stores it in variables.
     * @since 2.0.0
     */
    private void saveInputs() {
        linkToFile = String.valueOf(linkInputText.getCharacters());
        if (Utility.isYoutubeLink(linkToFile)){
            isYouTubeURL = true;
        }
        if (directoryForDownloading == null){
            directoryForDownloading = String.valueOf(directoryInputText.getCharacters());
        }
        fileName = String.valueOf(fileNameInputText.getCharacters());
    }

    /**
     * This is the main method which <b>initiates launching</b> the Graphical User Interface (GUI) version of Drifty.
     * @param args Arguments to the application is presently ignored.
     * @since 2.0.0
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * This method <b>opens the website link (provided as parameter) in the default web browser</b>.
     * @param websiteURL This is the <b>String representation</b> of the <i>website link</i> to open. E.g.: <a href="https://saptarshisarkar12.github.io/Drifty">"https://saptarshisarkar12.github.io/Drifty"</a>, <a href="https://github.com/SaptarshiSarkar12/Drifty">"https://github.com/SaptarshiSarkar12/Drifty"</a>, etc.
     * @param websiteType This is <b>type of the website</b> to be opened. E.g.: "<b>Project Website</b>", "<b>Contact Us Webpage</b>", etc.
     */
    private void openWebsite(String websiteURL, String websiteType){
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("nux") || osName.contains("nix")){ // for Linux / Unix systems
            try {
                String[] commandsToOpenWebsite = {"xdg-open", websiteURL};
                Runtime openWebsite = Runtime.getRuntime();
                openWebsite.exec(commandsToOpenWebsite);
            } catch (IOException e) {
                logger.log(Constants.LOGGER_ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }
        } else if (osName.contains("win") || osName.contains("mac")) { // For macOS and Windows systems
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(websiteURL));
            } catch (IOException | URISyntaxException e) {
                logger.log(Constants.LOGGER_ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }
        }
    }

    /**
     * This method initializes the <b>main screen of the main Window</b> (default) with properties such as <b>window title</b>, <b>position on screen</b>, etc.
     */
    private void initializeScreen(){
        driftyInitialWindow.setTitle("Drifty GUI (Graphical User Interface) - An Open-Source File Downloader System");
        driftyInitialWindow.setMaximized(true);
        driftyInitialWindow.setResizable(false);
        root.setSpacing(30);

        menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem website = new MenuItem("Project Website");
        MenuItem about = new MenuItem("About");
        MenuItem exit = new MenuItem("Exit");
        menu.getItems().addAll(website, about, exit);

        Menu help = new Menu("Help");
        MenuItem contactUs = new MenuItem("Contact Us");
        MenuItem contribute = new MenuItem("Contribute");
        MenuItem bug = new MenuItem("Report a Bug");
        MenuItem securityVulnerability = new MenuItem("Report a Security Vulnerability");
        MenuItem feature = new MenuItem("Suggest a Feature");
        help.getItems().addAll(contactUs, contribute, bug, securityVulnerability, feature);

        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);
        menuBar.getMenus().addAll(menu, help);

        EventHandler<ActionEvent> projectWebsiteClicked = actionEvent -> openWebsite(Drifty.projectWebsite, "project website");
        EventHandler<ActionEvent> contactUsWebsiteClicked = actionEvent -> openWebsite("https://saptarshisarkar12.github.io/Drifty/contact.html", "contact us webpage");
        EventHandler<ActionEvent> contributeWebsiteClicked = actionEvent -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty", "repository website for contribution");
        EventHandler<ActionEvent> bugWebsiteClicked = actionEvent -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=bug%2CApp&template=Bug-for-application.yaml&title=%5BBUG%5D+", "issue webpage to file a Bug");
        EventHandler<ActionEvent> securityVulnerabilityWebsiteClicked = actionEvent -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/security/advisories/new", "Security Vulnerability webpage");
        EventHandler<ActionEvent> featureWebsiteClicked = actionEvent -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=enhancement%2CApp&template=feature-request-application.yaml&title=%5BFEATURE%5D+", "issue webpage to suggest feature");
        EventHandler<ActionEvent> exitClicked = actionEvent -> {
            stopInstantInputValidating();
            driftyInitialWindow.close();
            logger.log(Constants.LOGGER_INFO, Constants.GUI_APPLICATION_TERMINATED);
            System.exit(0);
        };
        EventHandler<WindowEvent> close = WindowEvent -> {
            stopInstantInputValidating();
            driftyInitialWindow.close();
            logger.log(Constants.LOGGER_INFO, Constants.GUI_APPLICATION_TERMINATED);
            System.exit(0);
        };

        website.setOnAction(projectWebsiteClicked); // The project website will open.
        contactUs.setOnAction(contactUsWebsiteClicked);
        contribute.setOnAction(contributeWebsiteClicked); // The GitHub repository where this project is hosted, will open.
        bug.setOnAction(bugWebsiteClicked); // The website to report a bug will open.
        securityVulnerability.setOnAction(securityVulnerabilityWebsiteClicked); // The website to report a security vulnerability will open.
        feature.setOnAction(featureWebsiteClicked); // The website to suggest feature will open.
        exit.setOnAction(exitClicked); // When app window will be closed by clicking on exit from the menu, the app will exit completely.
        driftyInitialWindow.setOnCloseRequest(close); // When app window will be closed by clicking on system default cross button, the app will exit completely.

        drifty = new Text("Drifty");
        drifty.setFont(Font.font("Times Roman", FontWeight.BOLD, 100));
        drifty.setFill(Color.ROYALBLUE);
        drifty.setStroke(Color.DEEPSKYBLUE);
        drifty.setCache(true);
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(90);
    }

    /**
     * This method <b>stops</b> the instant input validator task from validating the inputs as the user types them in the respective input fields.
     */
    private void stopInstantInputValidating(){
        isFileBeingDownloaded = true;
    }

    /**
     * This method sets the value of the boolean <b>IsFileBeingDownloaded</b>.
     * @param value If the file is being downloaded, <code>true</code> should be passed else <code>false</code>.
     */
    public static void setIsFileBeingDownloaded(boolean value) {
        isFileBeingDownloaded = value;
    }
}
