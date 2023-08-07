package GUI.Forms;

import Backend.Drifty;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;
import Enums.OS;
import GUI.Support.AskYesNo;
import GUI.Support.Folders;
import GUI.Support.Job;
import GUI.Support.ManageFolders;
import Preferences.AppSettings;
import Utils.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static GUI.Forms.Constants.monacoFont;
import static GUI.Forms.Constants.screenSize;
import static Utils.DriftyConstants.GUI_APPLICATION_STARTED;
import static javafx.scene.layout.AnchorPane.*;

public class Main {
    private Stage stage;
    private double scale = .45;
    private final Utility utility = new Utility(new MessageBroker(System.out));
    private static final BooleanProperty downloadInProgress = new SimpleBooleanProperty(false);
    private static final BooleanProperty processingBatch = new SimpleBooleanProperty(false);
    private static final BooleanProperty linkValid = new SimpleBooleanProperty(false);
    private static final BooleanProperty directoryExists = new SimpleBooleanProperty(false);
    private static final DoubleProperty progressProperty = new SimpleDoubleProperty(0.0);
    private static final IntegerProperty jobError = new SimpleIntegerProperty(-5);
    private final ConcurrentLinkedDeque<Job> failedJobList = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<Job> jobList = new ConcurrentLinkedDeque<>();
    private final Logger logger = Logger.getInstance();
    private final Color green = Constants.GREEN;
    private final Color red = Constants.RED;
    private final Color orange = Constants.ORANGE;
    private final String systemDefaultLineSeparator = System.lineSeparator();
    private final Image imgAutoPaste = new Image(Constants.autoPasteLabelImage.toExternalForm());
    private final Image imgDirectory = new Image(Constants.directoryLabelImage.toExternalForm());
    private final Image imgFilename = new Image(Constants.filenameLabelImage.toExternalForm());
    private final Image imgLink = new Image(Constants.linkLabelImage.toExternalForm());
    private boolean firstRun = true;
    private boolean gettingFilename = false;
    private boolean countUp = true;
    private boolean consoleOpen = false;
    private boolean waitingForErrorCode = true;
    private String errorMessage;
    private Batch batch;
    private static Main INSTANCE;
    private final double width;
    private final double height;
    private boolean isYouTubeURL;
    private float downloadProgress;
    private static ProgressBar pBar;
    private MenuBar menuBar;
    private ConsoleOut consoleOut;
    private Folders folders;
    private String linkToFile;
    private String directoryForDownloading;
    private String fileName;
    private AnchorPane anchorPane;
    private BorderPane borderPane;
    private TextField tfLink;
    private TextField tfDir;
    private TextField tfFilename;
    private ImageView ivLinkLabel;
    private ImageView ivDirLabel;
    private ImageView ivFilenameLabel;
    private ImageView ivBtnDownload;
    private ImageView ivBtnBatch;
    private ImageView ivAutoLabel;
    private CheckBox cbAutoPaste;
    private Label lblLinkOut;
    private Label lblDirOut;
    private Label lblDownloadInfo;
    private Label lblFilenameOut;
    private ImageView ivBtnConsole;
    private VBox vbox;
    private Scene scene;

    private final double absWidth;
    private final double absHeight;

    public Main() {
        absWidth = screenSize.getWidth();
        absHeight = screenSize.getHeight();
        width = absWidth * scale; // E.g.: 768
        height = absHeight * scale; // E.g.: 1366
        System.out.println("CurSize: " + absWidth + " x " + absHeight );
        System.out.println("NewSize: " + width + " x " + height );
    }

    public void start() {
        Platform.runLater(() -> stage = Constants.getStage());
        folders = AppSettings.get.folders();
        logger.log(MessageType.INFORMATION, GUI_APPLICATION_STARTED); // log a message when the Graphical User Interface (GUI) version of Drifty is triggered to start
        scale = screenSize.getHeight() / screenSize.getWidth();
        createControls();
        setControlProperties();
        setControlActions();
        INSTANCE = this;
        batch = new Batch(consoleOut);
        createScene();
        showScene();
    }

    private void createScene() {
        scene = new Scene(anchorPane);
        scene.setOnContextMenuRequested(e -> getRightClickContextMenu().show(scene.getWindow(), e.getScreenX(), e.getScreenY()));
        scene.getStylesheets().add(Constants.contextMenuCSS.toExternalForm());
        scene.getStylesheets().add(Constants.labelCSS.toExternalForm());
        scene.getStylesheets().add(Constants.menuCSS.toExternalForm());
        scene.getStylesheets().add(Constants.checkBoxCSS.toExternalForm());
        scene.getStylesheets().add(Constants.textFieldCSS.toExternalForm());
        scene.getStylesheets().add(Constants.vBoxCSS.toExternalForm());
        scene.getStylesheets().add(Constants.sceneCSS.toExternalForm());
        scene.getStylesheets().add(Constants.progressBarCSS.toExternalForm());
        scene.widthProperty().addListener(((observable, oldValue, newValue) -> {
            vbox.setPrefWidth((double) newValue);
            if (consoleOut != null) {
                consoleOut.setWidth((double) newValue);
            }

        }));
        scene.heightProperty().addListener(((observable, oldValue, newValue) -> vbox.setPrefHeight((double) newValue)));
        stage.xProperty().addListener(((observable, oldValue, newValue) -> {
            if (consoleOut != null) {
                consoleOut.rePosition((double) newValue, stage.getY());
            }

        }));
        stage.yProperty().addListener(((observable, oldValue, newValue) -> {
            if (consoleOut != null) {
                consoleOut.rePosition(stage.getX(), (double) newValue);
            }

        }));
        stage.fullScreenProperty().addListener(((observable, oldValue, newValue) -> ivBtnConsole.setVisible(!newValue)));
        stage.focusedProperty().addListener(((observable, wasFocused, isFocused) -> {
            if (firstRun) {
                firstRun = false;
                return;
            }

            if (isFocused && cbAutoPaste.isSelected()) {
                tfLink.setText(getClipboardText());
            }

            if (isFocused && !wasFocused) {
                if (consoleOut != null) {
                    consoleOut.rePosition(width, height, stage.getX(), stage.getY());
                }

            }

        }));
    }

    private void showScene() {
        new Thread(() -> {
            while (Utility.timeSinceStart() < 4500 && !Mode.devMode()) {
                sleep(50);
            }

            Splash.almostDone();
            while (Splash.animationNotDone() && !Mode.devMode()) {
                sleep(10);
            }

            Platform.runLater(() -> {
                stage.setScene(scene);
                stage.setWidth(width);
                stage.setHeight(height * 1.2);
                Mode.setGuiLoaded(true);
                stage.show();
                if (AppSettings.get.startMax()) {
                    toggleFullScreen();
                }

                Splash.close();
                menuBar.toFront();
            });
        }).start();
    }

    private void createControls() {
        anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(width);
        anchorPane.setPrefHeight(height);
        borderPane = new BorderPane();
        borderPane.setPrefWidth(width);
        borderPane.setPrefHeight(height);
        Image imgBanner = new Image(Constants.mainGUIBanner.toExternalForm());
        ImageView ivBanner = imageView(imgBanner, .5);
        pBar = pbar();
        HBox bannerBox = new HBox(ivBanner);
        bannerBox.setAlignment(Pos.CENTER);
        bannerBox.setPadding(new Insets(10));
        HBox pBar = new HBox(Main.pBar);
        pBar.setAlignment(Pos.CENTER);
        pBar.setPadding(new Insets(10));
        menuBar = menuBar(getMenuItemsOfMenu(), getWindowMenu(), getHelpMenuItems());
        anchorPane.getChildren().addAll(menuBar, bannerBox, pBar);
        placeControl(menuBar, 0, 0, 0, -1);
        double top;
        top = Utility.reMap(45, 0, 1120, 0, absHeight) * .85;
        placeControl(bannerBox, 0, 0, top, -1);
        top = Utility.reMap(155, 0, 1120, 0, absHeight) * .85;
        placeControl(pBar, 30, 30, top, -1);
        ivLinkLabel = imageView(imgLink, scale);
        ivAutoLabel = imageView(imgAutoPaste, scale);
        cbAutoPaste = new CheckBox();
        HBox boxLinkLabel = newHBox(Pos.CENTER_LEFT, ivLinkLabel, getSpacer(), ivAutoLabel, cbAutoPaste);
        tfLink = newTextField();
        lblLinkOut = label("");
        HBox boxLinkOut = newHBox(Pos.CENTER_LEFT, lblLinkOut);
        ivDirLabel = imageView(imgDirectory, scale);
        HBox boxDirLabel = newHBox(Pos.CENTER_LEFT, ivDirLabel);
        tfDir = newTextField();
        lblDirOut = label("");
        HBox boxLblDirOut = newHBox(Pos.CENTER_LEFT, lblDirOut);
        ivFilenameLabel = imageView(imgFilename, scale);
        HBox boxFilenameLabel = newHBox(Pos.CENTER_LEFT, ivFilenameLabel);
        tfFilename = newTextField();
        lblFilenameOut = label("");
        HBox boxLblFilenameOut = newHBox(Pos.CENTER_LEFT, lblFilenameOut);
        lblDownloadInfo = label("");
        HBox boxLblDownloadInfo = newHBox(Pos.CENTER_LEFT, lblDownloadInfo);
        vbox = new VBox(5, boxLinkLabel, tfLink, boxLinkOut, boxDirLabel, tfDir, boxLblDirOut, boxFilenameLabel, tfFilename, boxLblFilenameOut, boxLblDownloadInfo);
        vbox.setPadding(new Insets(-125 * scale, 30, 0, 30));
        vbox.setAlignment(Pos.CENTER);
        borderPane.setBottom(makeButtonBox());
        anchorPane.getChildren().addAll(borderPane, vbox);
        placeControl(borderPane, 0, 0, 0, 0);
        top = Utility.reMap(350, 0, 1120, 0, absHeight) * .85;
        double bottom = Utility.reMap(300, 1120, 0, 0, absHeight) * .85;
        placeControl(vbox, 0, 0, top, 150);
    }

    private void setControlProperties() {
        tfDir.setText(folders.getDownloadFolder());
        directoryExists.setValue(new File(tfDir.getText()).exists());
        jobError.addListener(((observable, oldValue, newValue) -> {
            System.out.println("jobError : " + newValue);
            if (newValue.equals(-5)) {
                return;
            }

            int value = (int) newValue;
            Platform.runLater(() -> {
                if (value == 0) {
                    lblDownloadInfo.setTextFill(green);
                }

                else {
                    setDownloadOutput(red, errorMessage);
                }

            });
            jobError.setValue(-5);
            waitingForErrorCode = false;
        }));
        BooleanBinding checkState = cbAutoPaste.selectedProperty().not().not();
        BooleanBinding disableDownloadButton = downloadInProgress.or(directoryExists.not());
        BooleanBinding disableInputs = downloadInProgress.not();
        BooleanBinding hasText = tfLink.textProperty().isEmpty().not().and(tfDir.textProperty().isEmpty().not().and(tfFilename.textProperty().isEmpty().not()));
        ivBtnDownload.visibleProperty().bind(hasText);
        tfLink.setAlignment(Pos.CENTER_LEFT);
        tfLink.editableProperty().bind(disableInputs);
        lblLinkOut.setAlignment(Pos.CENTER_LEFT);
        tfDir.setAlignment(Pos.CENTER_LEFT);
        tfDir.editableProperty().bind(disableInputs);
        tfFilename.setAlignment(Pos.CENTER_LEFT);
        tfFilename.editableProperty().bind(disableInputs);
        lblDirOut.setAlignment(Pos.CENTER_LEFT);
        lblFilenameOut.setAlignment(Pos.CENTER_LEFT);
        lblDownloadInfo.setAlignment(Pos.CENTER_LEFT);
        ivBtnDownload.disableProperty().bind(disableDownloadButton);
        Tooltip.install(cbAutoPaste, new Tooltip("When checked, will paste contents of clipboard into" + systemDefaultLineSeparator + "Link field when switching back to this screen."));
    }

    private void setControlActions() {
        cbAutoPaste.setSelected(AppSettings.get.mainAutoPaste());
        cbAutoPaste.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.set.mainAutoPaste(newValue)));
        ivBtnDownload.setOnMouseClicked(e -> new Thread(() -> {
            if (confirmDownload()) {
                jobList.clear();
                jobList.add(new Job(tfLink.getText(), tfDir.getText(), tfFilename.getText()));
                batchDownloader();
            }

        }).start());
        ivBtnBatch.setOnMouseClicked(e -> batch.show());
        tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                directoryExists.setValue(false);
                if (newValue.isEmpty()) {
                    setDirOutput(red, "Directory cannot be empty!");
                }

                else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        delayFolderSave(newValue, folder);
                        setDirOutput(green, "Directory exists!");
                        directoryExists.setValue(true);
                    }

                    else {
                        setDirOutput(red, "Directory does not exist or is not a directory!");
                    }

                }

            }

        }));
        tfLink.textProperty().addListener(((observable, oldValue, newValue) -> verifyLink(oldValue, newValue)));
        ivBtnConsole.setOnMouseClicked(e -> toggleConsole(false));
    }

    public static void setJobError(String message, int errorLevel) {
        INSTANCE.errorMessage = message;
        jobError.setValue(errorLevel);
    }

    public static void updateProgress(double progress) {
        if (progress > 0.0 && progress < 0.99) {
            progressProperty.setValue(progress);
        }

        else {
            progressProperty.setValue(0.0);
        }

    }

    public static void setDownloadInProgress(boolean value) {
        downloadInProgress.setValue(value);
    }

    public static void runBatch(ConcurrentLinkedDeque<Job> jobList) {
        INSTANCE.jobList = jobList;
        INSTANCE.batchDownloader();
    }

    public static void setMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        Color color = switch (messageType) {
            case INFORMATION -> INSTANCE.green;
            case ERROR -> INSTANCE.red;
            case WARNING -> INSTANCE.orange;
        };
        switch (messageCategory) {
            case LINK -> Platform.runLater(() -> INSTANCE.setLinkOutput(color, message));
            case DIRECTORY -> Platform.runLater(() -> INSTANCE.setDirOutput(color, message));
            case DOWNLOAD -> Platform.runLater(() -> INSTANCE.setDownloadOutput(color, message));
            case FILENAME -> Platform.runLater(() -> INSTANCE.setFilenameOutput(color, message));
        }

    }

    private HBox newHBox(Pos align, Node... nodes) {
        HBox box = new HBox(nodes);
        box.setPadding(new Insets(0, 0, 0, 0));
        box.setAlignment(align);
        return box;
    }

    private ProgressBar pbar() {
        ProgressBar pbar = new ProgressBar();
        pbar.setPrefWidth(screenSize.getWidth());
        pbar.progressProperty().bind(progressProperty);
        progressProperty.setValue(1);
        return pbar;
    }

    private TextField newTextField() {
        TextField tf = new TextField();
        tf.setFont(new Font(monacoFont.toExternalForm(), 19 * scale));
        tf.setPrefHeight(45 * scale);
        return tf;
    }

    private HBox makeButtonBox() {
        Image btnDownloadUp = new Image(Constants.downloadUp.toExternalForm());
        Image btnDownloadDown = new Image(Constants.downloadDown.toExternalForm());
        Image btnBatchUp = new Image(Constants.batchUp.toExternalForm());
        Image btnBatchDown = new Image(Constants.batchDown.toExternalForm());
        ivBtnDownload = imageViewButton(btnDownloadUp, btnDownloadDown);
        ivBtnBatch = imageViewButton(btnBatchUp, btnBatchDown);
        ivBtnConsole = imageToggle(.5);
        HBox box = new HBox(100, ivBtnDownload, ivBtnConsole, ivBtnBatch);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Label getSpacer() {
        Label label = new Label();
        label.setPrefWidth(screenSize.getWidth());
        return label;
    }

    private Label label(String text) {
        Label label = new Label(text);
        label.setFont(new Font(monacoFont.toExternalForm(), 20 * scale));
        return label;
    }

    private ImageView imageToggle(double scale) {
        ImageView imageView = new ImageView(Constants.imgUpUp);
        imageView.setOnMousePressed(e -> imageView.setImage(Constants.imgUpDown));
        imageView.setOnMouseReleased(e -> imageView.setImage(Constants.imgUpUp));
        double width = Constants.imgUpUp.getWidth();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width * scale);
        return imageView;
    }

    private ImageView imageViewButton(Image imageUp, Image imageDown) {
        ImageView imageView = new ImageView(imageUp);
        double width = imageUp.getWidth();
        imageView.setOnMouseReleased(e -> imageView.setImage(imageUp));
        imageView.setOnMousePressed(e -> imageView.setImage(imageDown));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width * scale);
        return imageView;
    }

    private ImageView imageView(Image image, double ratio) {
        ImageView iv = new ImageView(image);
        double width = image.getWidth();
        iv.setPreserveRatio(true);
        iv.setFitWidth(width * ratio);
        return iv;
    }

    private void toggleConsole(boolean close) {
        if (consoleOpen || close) {
            ivBtnConsole.setImage(Constants.imgUpUp);
            ivBtnConsole.setOnMousePressed(e -> ivBtnConsole.setImage(Constants.imgUpDown));
            ivBtnConsole.setOnMouseReleased(e -> ivBtnConsole.setImage(Constants.imgUpUp));
            consoleOut.hide();
            consoleOpen = false;
        }

        else {
            ivBtnConsole.setImage(Constants.imgDownUp);
            ivBtnConsole.setOnMousePressed(e -> ivBtnConsole.setImage(Constants.imgDownDown));
            ivBtnConsole.setOnMouseReleased(e -> ivBtnConsole.setImage(Constants.imgDownUp));
            consoleOut.show();
            consoleOpen = true;
        }

    }

    private void toggleFullScreen() {
        if (!stage.isFullScreen()) {
            toggleConsole(true);
        }

        stage.setFullScreen(!stage.isFullScreen());
    }

    private void getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String lastFolder = folders.getDownloadFolder();
        String initFolder = lastFolder.isEmpty() ? System.getProperty("user.home") : lastFolder;
        directoryChooser.setInitialDirectory(new File(initFolder));
        File directory = directoryChooser.showDialog(stage.getOwner());
        firstRun = true;
        if (directory != null) {
            tfDir.setText(directory.getAbsolutePath());
        }

    }

    private void openWebsite(String websiteURL, String websiteType) {
        if (OS.isNix()) { // for Linux / Unix systems
            try {
                String[] commandsToOpenWebsite = {"xdg-open", websiteURL};
                Runtime openWebsite = Runtime.getRuntime();
                openWebsite.exec(commandsToOpenWebsite);
            } catch (IOException e) {
                logger.log(MessageType.ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }

        }

        else if (OS.isWinMac()) { // For macOS and Windows systems
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(websiteURL));
            } catch (IOException | URISyntaxException e) {
                logger.log(MessageType.ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }

        }

        else {
            logger.log(MessageType.ERROR, "Cannot open requested website " + websiteType + "! System Unsupported!");
        }

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

    private MenuBar menuBar(Menu... menus) {
        MenuBar menuBar = new MenuBar(menus);
        menuBar.setPrefWidth(screenSize.getWidth());
        menuBar.setUseSystemMenuBar(true);
        return menuBar;
    }

    private Menu getMenuItemsOfMenu() {
        Menu menu = new Menu("Menu");
        MenuItem website = new MenuItem("Project Website");
        website.setOnAction(e -> openWebsite(Drifty.projectWebsite, "project website"));
        MenuItem about = new MenuItem("About");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            logger.log(MessageType.INFORMATION, DriftyConstants.GUI_APPLICATION_TERMINATED);
            System.exit(0);
        });
        menu.getItems().setAll(website, about, exit);
        return menu;
    }

    private Menu getWindowMenu() {
        Menu menu = new Menu("Window");
        MenuItem fullScreen = new MenuItem("Toggle Full Screen");
        fullScreen.setOnAction(e -> toggleFullScreen());
        menu.getItems().setAll(fullScreen);
        return menu;
    }

    private Menu getHelpMenuItems() {
        Menu menu = new Menu("Help");
        MenuItem contactUs = new MenuItem("Contact Us");
        MenuItem contribute = new MenuItem("Contribute");
        MenuItem bug = new MenuItem("Report a Bug");
        MenuItem securityVulnerability = new MenuItem("Report a Security Vulnerability");
        MenuItem feature = new MenuItem("Suggest a Feature");
        contactUs.setOnAction(e -> openWebsite("https://saptarshisarkar12.github.io/Drifty/contact.html", "contact us webpage"));
        contribute.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty", "repository website for contribution"));
        bug.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=bug%2CApp&template=Bug-for-application.yaml&title=%5BBUG%5D+", "issue webpage to file a Bug"));
        securityVulnerability.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/security/advisories/new", "Security Vulnerability webpage"));
        feature.setOnAction(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=enhancement%2CApp&template=feature-request-application.yaml&title=%5BFEATURE%5D+", "issue webpage to suggest feature"));
        menu.getItems().setAll(contactUs, contribute, bug, securityVulnerability, feature);
        return menu;
    }

    private ContextMenu getRightClickContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        miAdd.setOnAction(e -> getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            folders = AppSettings.get.folders();
        });
        ContextMenu contextMenu = new ContextMenu(miAdd, miDir);
        contextMenu.getStyleClass().add("rightClick");
        return contextMenu;
    }

    private void verifyLink(String PreviousLink, String presentLink) {
        if (!PreviousLink.equals(presentLink)) {
            if (downloadInProgress.getValue().equals(false) && processingBatch.getValue().equals(false)) {
                setLinkOutput(green, "Validating link ...");
                linkValid.setValue(false);
                if (presentLink.contains(" ")) {
                    Platform.runLater(() -> setLinkOutput(red, "Link should not contain whitespace characters!"));
                }

                else if (!isURL(presentLink)) {
                    Platform.runLater(() -> setLinkOutput(red, "String is not a URL"));
                }

                else {
                    try {
                        Utility.isURLValid(presentLink);
                        Platform.runLater(() -> setLinkOutput(green, "Valid URL"));
                        linkValid.setValue(true);
                    } catch (Exception e) {
                        String errorMessage = e.getMessage();
                        Platform.runLater(() -> setLinkOutput(red, errorMessage));
                    }

                }

                if (linkValid.getValue().equals(true)) {
                    getFilename(presentLink);
                }

            }

        }

    }

    private void getFilename(String link) {
        new Thread(() -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String filename = utility.findFilenameInLink(link);
            if (filename == null || filename.isEmpty()) {
                Platform.runLater(() -> lblFilenameOut.setTextFill(green));
                gettingFilename = true;
                bounceFilename();
                LinkedList<String> jsonMetaData = Utility.getLinkMetadata(link);
                JsonElement element = JsonParser.parseString(jsonMetaData.getFirst());
                String json = gson.toJson(element);
                if (jsonMetaData.size() > 1) {
                    batch.setLink(link);
                    tfLink.clear();
                    setLinkOutput(green, "");
                    return;
                }

                else if ((jsonMetaData.size() == 1)) {
                    filename = Utility.getFilenameFromJson(json) + ".mp4";
                }

                else {
                    filename = "Unknown_Filename.mp4";
                }

            }

            String finalFilename = filename;
            gettingFilename = false;
            Platform.runLater(() -> {
                tfFilename.setText(finalFilename);
                setFilenameOutput(green, "");
            });
        }).start();
    }

    private boolean isURL(String text) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    private void setFilenameOutput(Color color, String message) {
        lblFilenameOut.setTextFill(color);
        lblFilenameOut.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setFilenameOutput(green, ""));
            }).start();
        }

    }

    private void setLinkOutput(Color color, String message) {
        lblLinkOut.setTextFill(color);
        lblLinkOut.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setLinkOutput(green, ""));
            }).start();
        }

    }

    private void setDirOutput(Color color, String message) {
        lblDirOut.setTextFill(color);
        lblDirOut.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDirOutput(green, ""));
            }).start();
        }

    }

    private void setDownloadOutput(Color color, String message) {
        lblDownloadInfo.setTextFill(color);
        lblDownloadInfo.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDownloadOutput(green, ""));
            }).start();
        }

    }

    private static String getClipboardText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            setMessage("Failed to get the last copied Text from the clipboard! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        }

        return null;
    }

    private boolean confirmDownload() {
        String filename = tfFilename.getText();
        for (String folder : folders.getFolders()) {
            boolean fileExists = false;
            String filePath = "";
            CheckFile walker = new CheckFile(folder, filename);
            Thread thread = new Thread(walker);
            thread.start();
            while (thread.getState().equals(Thread.State.RUNNABLE)) {
                sleep(100);
            }

            for (String path : walker.getFileList()) {
                if (path.contains(filename)) {
                    fileExists = true;
                    filePath = path;
                    break;
                }

            }

            if (fileExists) {
                String msg = "The file: " + systemDefaultLineSeparator + systemDefaultLineSeparator + filePath + systemDefaultLineSeparator + systemDefaultLineSeparator + " Already exists, Do you wish to download it again?";
                AskYesNo ask = new AskYesNo(msg);
                return ask.isYes();
            }

        }

        return true;
    }

    private void checkFiles() {
        Map<String, Job> pathJobMap = new HashMap<>();
        List<String> files = new ArrayList<>();
        for (Job job : jobList) {
            files.add(job.getFilename());
        }

        for (String folder : folders.getFolders()) {
            CheckFile walker = new CheckFile(folder, files);
            Thread thread = new Thread(walker);
            thread.start();
            while (thread.getState().equals(Thread.State.RUNNABLE)) {
                sleep(100);
            }

            LinkedList<String> list = walker.getFileList();
            for (Job job : jobList) {
                for (String path : list) {
                    if (path.contains(job.getFilename())) {
                        pathJobMap.put(path, job);
                    }

                }

            }

        }

        if (!pathJobMap.isEmpty()) {
            StringBuilder sb = new StringBuilder("The following files already exist:" + systemDefaultLineSeparator + systemDefaultLineSeparator);
            for (String path : pathJobMap.keySet()) {
                sb.append(path).append(systemDefaultLineSeparator);
            }

            sb.append(systemDefaultLineSeparator).append("Do you want to download them again?");
            AskYesNo ask = new AskYesNo(sb.toString());
            if (!ask.isYes()) {
                for (Job job : pathJobMap.values()) {
                    jobList.remove(job);
                }

            }

        }

    }

    private void batchDownloader() {
        processingBatch.setValue(true);
        new Thread(() -> {
            failedJobList.clear();
            if (!jobList.isEmpty()) {
                checkFiles();
                final int totalNumberOfFiles = jobList.size();
                System.out.println("Number of files to download : " + totalNumberOfFiles);
                int fileCount = 0;
                for (Job job : jobList) {
                    fileCount++;
                    String processingFileText = "Processing file " + fileCount + " of " + totalNumberOfFiles + ": " + job;
                    System.out.println(processingFileText);
                    Platform.runLater(() -> setFilenameOutput(green, processingFileText));
                    linkToFile = job.getLink();
                    directoryForDownloading = job.getDir();
                    fileName = job.getFilename();
                    Platform.runLater(() -> {
                        tfLink.setText(linkToFile);
                        tfDir.setText(directoryForDownloading);
                        tfFilename.setText(fileName);
                    });
                    if (Utility.isYoutubeLink(linkToFile)) {
                        isYouTubeURL = true;
                    }

                    Platform.runLater(() -> {
                        tfLink.setText(linkToFile);
                        tfDir.setText(directoryForDownloading);
                        tfFilename.setText(fileName);
                    });
                    sleep(1000);
                    download();
                    while (waitingForErrorCode) {
                        sleep(100);
                    }

                    Platform.runLater(() -> {
                        tfFilename.clear();
                        tfLink.clear();
                    });
                    if (jobError.getValue() != 0 && jobError.getValue() != -5) {
                        System.out.println("JOB ERROR VALUE: " + jobError.getValue());
                        failedJobList.add(job);
                        job.setError(lblDownloadInfo.getText());
                    }

                    else {
                        jobList.remove(job);
                    }

                    downloadInProgress.setValue(false);
                    sleep(3000);
                }

                int errors = failedJobList.size();
                String outMessage = (errors == 0) ? "Done - All downloads were successful!" : "Done - There were " + errors + " failed downloads, click on the Batch button to try those files again.";
                Platform.runLater(() -> {
                    setDownloadOutput(green, outMessage);
                    setLinkOutput(green, "");
                    setDirOutput(green, "");
                    setFilenameOutput(green, "");
                    tfLink.clear();
                });
                if (AppSettings.get.jobs() != null) {
                    AppSettings.get.jobs().setJobList(failedJobList);
                    if (!failedJobList.isEmpty()) {
                        batch.setFailedList(failedJobList);
                    }

                }

            }

            processingBatch.setValue(false);
        }).start();
    }

    private void delayFolderSave(String folderString, File folder) {
        // If the user is typing a file path into the field, we don't want to save every folder 'hit' so we wait 5 seconds
        // and if the String is still the same value, then we commit the folder to the list.
        new Thread(() -> {
            sleep(3000);
            if (tfDir.getText().equals(folderString)) {
                folders.addFolder(folder.getAbsolutePath());
                System.out.println("Folder Added: " + folder.getAbsolutePath());
            }

        }).start();
    }

    private void bounceFilename() {
        if (processingBatch.getValue().equals(false)) {
            new Thread(() -> {
                int count = 1;
                Platform.runLater(() -> setFilenameOutput(green, "Retrieving Filename"));
                while (gettingFilename) { //As long as the launching thread is still getting the filename, we will run this loop
                    sleep(900);
                    if (count < 0) { //This changes the direction of the counter from down to up
                        count = 1;
                        countUp = true;
                    }

                    final String out = "Retrieving Filename" + ".".repeat(count); //This is what we print in the label, adding the number of dots according to the counter
                    Platform.runLater(() -> setFilenameOutput(green, out));
                    if (countUp) { //If we are in up mode, then add one to the counter, otherwise, take one away.
                        count++;
                    }

                    else {
                        count--;
                    }

                    if (count == 4) { // When the counter is equal to 4 (we have 4 dots showing) then reverse the counter which starts taking dots away
                        countUp = false;
                    }

                    if (count == 1) {
                        countUp = true;
                    }

                }

                Platform.runLater(() -> setFilenameOutput(green, ""));
            }).start();
        }

    }

    private boolean verifyLink() {
        boolean valid = false;
        try {
            Utility.isURLValid(tfLink.getText());
            Platform.runLater(() -> setLinkOutput(green, "Valid URL"));
            valid = true;
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Platform.runLater(() -> setLinkOutput(red, errorMessage));
        }

        if (!valid) {
            new AskYesNo("The link provided is not a valid URL", true).isYes();
        }

        return valid;
    }

    private boolean verifyDirectory() {
        File file = new File(tfDir.getText());
        boolean valid = file.exists();
        if (!valid) {
            new AskYesNo("The Download folder does not exist", true).isYes();
        }

        return file.exists();
    }

    private boolean verifyFilename() {
        String pattern = "^[a-zA-Z0-9_.-]+$";
        String filename = tfFilename.getText();
        File file = new File(filename);
        boolean valid;
        try {
            valid = file.getCanonicalFile().getName().equals(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!valid) {
            new AskYesNo("The Filename provided is not a valid filename", true).isYes();
        }

        return valid;
    }

    private void download() {
        boolean proceed = verifyLink() && verifyDirectory() && verifyFilename();
        if (proceed) {
            waitingForErrorCode = true;
            downloadInProgress.setValue(true);
            Drifty backend = new Drifty(linkToFile, directoryForDownloading, fileName);
            Thread thread = new Thread(backend::startGUIDownload);
            thread.start();
        }

    }

    public static void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            setMessage("Link Metadata extracting thread failed to wait for " + time + ". It got interrupted. " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        }

    }
}
