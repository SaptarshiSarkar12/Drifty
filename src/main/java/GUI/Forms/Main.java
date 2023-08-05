package GUI.Forms;

import Backend.Drifty;
import Enums.*;
import GUI.Support.AskYesNo;
import GUI.Support.Folders;
import GUI.Support.Job;
import GUI.Support.ManageFolders;
import Preferences.Settings;
import Utils.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utils.DriftyConstants.GUI_APPLICATION_STARTED;
import static javafx.scene.layout.AnchorPane.*;


public class Main extends Application {
    private final Stage stage = new Stage();
    private final double scale = .55;
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
    private final URL monacoFont = getClass().getResource("/GUI/Fonts/Monaco.ttf");
    private String errorMessage;
    private Batch batch;
    private static Main INSTANCE;
    private double width;
    private double height;
    private boolean isYouTubeURL;
    private float downloadProgress;
    private static ProgressBar progressBar;
    private ConsoleOut consoleOut;
    private Folders folders;
    private String backPath;
    private String linkToFile;
    private String directoryForDownloading;
    private String fileName;
    private AnchorPane anchorPane;
    private TextField linkTextField;
    private TextField directoryTextField;
    private TextField filenameTextField;
    private ImageView ivBack;
    private ImageView downloadButton;
    private ImageView batchButton;
    private ImageView autoPasteLabel;
    private ImageView linkLabel;
    private ImageView directoryLabel;
    private ImageView filenameLabel;
    private CheckBox checkBoxForAutoPaste;
    private Label linkOutputLabel;
    private Label directoryOutputLabel;
    private Label downloadOutputLabel;
    private Label filenameOutputLabel;
    private ImageView consoleButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        folders = Settings.GET_PREFERENCES.getFolders();
        logger.log(MessageType.INFORMATION, GUI_APPLICATION_STARTED); // log a message when the Graphical User Interface (GUI) version of Drifty is triggered to start
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // E.g.: java.awt.Dimension[width=1366,height=768]
        height = (int) screenSize.getHeight(); // E.g.: 768
        width = (int) screenSize.getWidth(); // E.g.: 1366
        backPath = Constants.guiBackground.toString();
        createControls();
        createScene();
        showScene();
        setControlProperties();
        setControlActions();
        INSTANCE = this;
        batch = new Batch(consoleOut);
    }

    /**
     * Methods for external communication inbound
     */

    public static void setJobError(String message, int errorLevel) {
        INSTANCE.errorMessage = message;
        jobError.setValue(errorLevel);
    }

    /**
     * This method updates the progress bar in Drifty GUI screen
     * @param progress an integer value denoting the progress
     */
    public static void updateProgress(double progress) {
        if (progress > 0.0 && progress < 0.99) {
            progressProperty.setValue(progress);
        }
        else {
            progressProperty.setValue(0.0);
        }
    }

    /**
     * This method sets the {@link #downloadInProgress} with the boolean value provided to it as a parameter
     * @param value true if the file download process is in progress
     */
    public static void setDownloadInProgress(boolean value) {
        downloadInProgress.setValue(value);
    }

    /**
     * This method starts the batch downloader
     * @param jobList linked deque list of the jobs to perform in the batch downloading system of Drifty
     */
    public static void runBatch(ConcurrentLinkedDeque<Job> jobList) {
        INSTANCE.jobList = jobList;
        INSTANCE.batchDownloader();
    }

    /**
     * This method sets the output message with the color in the respective sections, for the GUI
     * @param message output message to be shown in the Drifty GUI screen
     * @param messageType type of the output message
     * @param messageCategory category of the output message
     */
    public static void setMessage(String message, MessageType messageType, MessageCategory messageCategory) {
        Color color = switch (messageType) {
            case INFORMATION -> INSTANCE.green;
            case ERROR -> INSTANCE.red;
            case WARNING -> INSTANCE.orange;
        };
        switch (messageCategory) {
            case LINK -> Platform.runLater(() -> INSTANCE.setLinkOutput(color,message));
            case DIRECTORY -> Platform.runLater(() -> INSTANCE.setDirOutput(color,message));
            case DOWNLOAD -> Platform.runLater(() -> INSTANCE.setDownloadOutput(color, message));
            case FILENAME -> Platform.runLater(() -> INSTANCE.setFilenameOutput(color,message));
        }
    }

    /**
     * Form initialization And Control
     */
    private void createControls() {
        anchorPane = new AnchorPane();
        ivBack = new ImageView(new Image(backPath));
        ivBack.setPreserveRatio(true);
        ivBack.setFitWidth(width);
        anchorPane.getChildren().add(ivBack);
        double left = 205 * scale;
        double right = 160 * scale;
        double delta = 195 * scale;
        double top = 255 * scale;
        double vOffset = 85 * scale;
        progressBar = progressBar(200 * scale, 165 * scale, top + 30);
        top += 183 * scale;
        linkLabel = imageView(imgLink,left,top-25, (scale + scale/4));
        autoPasteLabel = imageView(imgAutoPaste,left + (width - left) * scale * 1.345,top-28, (scale + scale/4));
        linkTextField = addTextFieldWithProperties(left, right, top);
        linkOutputLabel = addLabelWithProperties("", left, right, top + vOffset);
        top += delta;
        directoryLabel = imageView(imgDirectory,left,top-25, (scale + scale/6));
        directoryTextField = addTextFieldWithProperties(left, right, top);
        directoryOutputLabel = addLabelWithProperties("", left, right, top + vOffset);
        top += delta; // 170
        filenameLabel = imageView(imgFilename,left,top-25, (scale + scale/8));
        filenameTextField = addTextFieldWithProperties(left, right, top);
        filenameOutputLabel = addLabelWithProperties("", left, right, top + vOffset);
        downloadOutputLabel = addLabelWithProperties("", left, right, top + (vOffset * 1.45));
        Image btnDownloadUp = new Image(Constants.downloadUp.toExternalForm());
        Image btnDownloadDown = new Image(Constants.downloadDown.toExternalForm());
        Image btnBatchUp = new Image(Constants.batchUp.toExternalForm());
        Image btnBatchDown = new Image(Constants.batchDown.toExternalForm());
        double buttonWidth = btnDownloadUp.getWidth() * scale * .95;
        double buttonOffset = buttonWidth / 2;
        double btnPlace = width / 4;
        double placement = btnPlace - buttonOffset;
        downloadButton = imageViewButton(btnDownloadUp, btnDownloadDown, placement, 0, buttonWidth);
        placement = (btnPlace * 3) - buttonOffset;
        batchButton = imageViewButton(btnBatchUp, btnBatchDown, placement, 0, buttonWidth);
        checkBoxForAutoPaste = checkbox(width * .089, height * .34);
        consoleButton = imageToggle(width/2 - 15, 6.5);
        menuBar(getMenuItemsOfMenu(), getHelpMenuItems());
    }

    /**
     * This method sets the control properties to the text fields and buttons in the Drifty GUI screen
     */
    private void setControlProperties() {
        directoryTextField.setText(folders.getDownloadFolder());
        directoryExists.setValue(new File(directoryTextField.getText()).exists());
        jobError.addListener(((observable, oldValue, newValue) -> {
            System.out.println("jobError : " + newValue);
            if (newValue.equals(-5)) {
                return;
            }
            int value = (int) newValue;
            Platform.runLater(() -> {
                if (value == 0) {
                    downloadOutputLabel.setTextFill(green);
                } else {
                    setDownloadOutput(red, errorMessage);
                }
            });
            jobError.setValue(-5);
            waitingForErrorCode = false;
        }));
        BooleanBinding checkState = checkBoxForAutoPaste.selectedProperty().not().not();
        BooleanBinding disableDownloadButton = downloadInProgress.or(directoryExists.not());
        BooleanBinding disableInputs = downloadInProgress.not();
        BooleanBinding hasText = linkTextField.textProperty().isEmpty().not().and(directoryTextField.textProperty().isEmpty().not().and(filenameTextField.textProperty().isEmpty().not()));
        downloadButton.visibleProperty().bind(hasText);
        linkTextField.setAlignment(Pos.CENTER_LEFT);
        linkTextField.editableProperty().bind(disableInputs);
        linkOutputLabel.setAlignment(Pos.CENTER_LEFT);
        directoryTextField.setAlignment(Pos.CENTER_LEFT);
        directoryTextField.editableProperty().bind(disableInputs);
        filenameTextField.setAlignment(Pos.CENTER_LEFT);
        filenameTextField.editableProperty().bind(disableInputs);
        directoryOutputLabel.setAlignment(Pos.CENTER_LEFT);
        filenameOutputLabel.setAlignment(Pos.CENTER_LEFT);
        downloadOutputLabel.setAlignment(Pos.CENTER_LEFT);
        downloadButton.disableProperty().bind(disableDownloadButton);
        Tooltip.install(checkBoxForAutoPaste, new Tooltip("When checked, will paste contents of clipboard into" + systemDefaultLineSeparator + "Link field when switching back to this screen."));
    }

    private void setControlActions() {
        checkBoxForAutoPaste.setSelected(Settings.GET_PREFERENCES.getIsMainAutoPasteEnabled());
        checkBoxForAutoPaste.selectedProperty().addListener(((observable, oldValue, newValue) -> Settings.SET_PREFERENCES.setIsMainAutoPasteEnabled(newValue)));
        downloadButton.setOnMouseClicked(e -> new Thread(() -> {
            if (confirmDownload()) {
                jobList.clear();
                jobList.add(new Job(linkTextField.getText(), directoryTextField.getText(), filenameTextField.getText()));
                batchDownloader();
            }
        }).start());
        batchButton.setOnMouseClicked(e -> batch.show());
        stage.focusedProperty().addListener(((observable, wasFocused, isFocused) -> {
            if (firstRun) {
                firstRun = false;
                return;
            }
            if (isFocused && checkBoxForAutoPaste.isSelected()) {
                linkTextField.setText(getClipboardText());
            }
            if (isFocused && !wasFocused) {
                consoleOut.rePosition(width, height, stage.getX(), stage.getY());
            }
        }));
        directoryTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                directoryExists.setValue(false);
                if (newValue.isEmpty()) {
                    setDirOutput(red,"Directory cannot be empty!");
                }
                else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        delayFolderSave(newValue, folder);
                        setDirOutput(green,"Directory exists!");
                        directoryExists.setValue(true);
                    }
                    else {
                        setDirOutput(red,"Directory does not exist or is not a directory!");
                    }
                }
            }
        }));
        linkTextField.textProperty().addListener(((observable, oldValue, newValue) -> verifyLink(oldValue, newValue)));
        consoleButton.setOnMouseClicked(e-> toggleConsole());
    }

    private void createScene() {
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(anchorPane);
        scene.setOnContextMenuRequested(e -> getRightClickContextMenu().show(scene.getWindow(), e.getScreenX(), e.getScreenY()));
        anchorPane.getStylesheets().add(Constants.contextMenuCSS.toExternalForm());
        anchorPane.getStylesheets().add(Constants.labelCSS.toExternalForm());
        anchorPane.getStylesheets().add(Constants.menuCSS.toExternalForm());
        stage.setScene(scene);
    }

    /**
     * This method sets the properties of the scene and makes it visible
     */
    private void showScene() {
        stage.setMaxWidth(width);
        stage.setMinWidth(width);
        stage.setWidth(width);
        stage.setMaxHeight(height);
        stage.setMinHeight(height);
        stage.setHeight(height);
        stage.show();
        if (consoleOut == null) {
            consoleOut = new ConsoleOut(width, height, stage.getX(), stage.getY());
        }
        Mode.setIsGUILoaded(true);
    }

    /**
     * This method opens the console when the console button is toggled in Drifty GUI
     */
    private void toggleConsole() {
        if (!consoleOpen) {
            consoleButton.setImage(Constants.imgDownUp);
            consoleButton.setOnMousePressed(e -> consoleButton.setImage(Constants.imgDownDown));
            consoleButton.setOnMouseReleased(e -> consoleButton.setImage(Constants.imgDownUp));
            consoleOut.show();
            consoleOpen = true;
        }
        else {
            consoleButton.setImage(Constants.imgUpUp);
            consoleButton.setOnMousePressed(e -> consoleButton.setImage(Constants.imgUpDown));
            consoleButton.setOnMouseReleased(e -> consoleButton.setImage(Constants.imgUpUp));
            consoleOut.hide();
            consoleOpen = false;
        }
    }

    /**
     * This method fetches the last download directory entered by the user. If it is empty, then, the default download directory is set in the directory placeholder
     */
    private void getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String lastFolder = folders.getDownloadFolder();
        String initFolder = lastFolder.isEmpty() ? System.getProperty("user.home") : lastFolder;
        directoryChooser.setInitialDirectory(new File(initFolder));
        File directory = directoryChooser.showDialog(stage.getOwner());
        firstRun = true;
        if (directory != null) {
            directoryTextField.setText(directory.getAbsolutePath());
        }
    }

    /**
     * This method is used to open the requested website in the user's machine's default web browser
     * @param websiteURL link to the website to open
     * @param websiteType category of the website open (like project website, feature request website, contact us webpage, etc.)
     */
    private void openWebsite(String websiteURL, String websiteType) {
        if (OS.isNix()) { // for Linux / Unix systems
            try {
                String[] commandsToOpenWebsite = {"xdg-open", websiteURL};
                Runtime openWebsite = Runtime.getRuntime();
                openWebsite.exec(commandsToOpenWebsite);
            } catch (IOException e) {
                logger.log(MessageType.ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }
        } else if (OS.isWinMac()) { // For macOS and Windows systems
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(websiteURL));
            } catch (IOException | URISyntaxException e) {
                logger.log(MessageType.ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }
        } else {
            logger.log(MessageType.ERROR, "Cannot open requested website " + websiteType + "! System Unsupported!");
        }
    }

    /**
     * This method creates a text field object with the size parameters provided
     * @param left the space from the left size of the scene
     * @param right the space from the right of the scene
     * @param top the space from the top of the scene
     * @return the TextField object with the fonts and styles added
     */
    private TextField addTextFieldWithProperties(double left, double right, double top) {
        TextField textField = new TextField();
        double h = 50;
        textField.setMinHeight(h);
        textField.setMaxHeight(h);
        textField.setPrefHeight(h);
        anchorPane.getChildren().add(textField);
        placeControl(textField, left, right, top, -1);
        textField.setFont(new Font("Arial", 18));
        textField.getStylesheets().add(Constants.textFieldCSS.toExternalForm());
        return textField;
    }

    /**
     * This method creates a Label object with the size parameters provided
     * @param left the space from the left size of the scene
     * @param right the space from the right of the scene
     * @param top the space from the top of the scene
     * @return the Label object with the fonts and styles added
     */
    private Label addLabelWithProperties(String text, double left, double right, double top) {
        Label label = new Label(text);
        label.setFont(new Font(monacoFont.toExternalForm(), 18));
        anchorPane.getChildren().add(label);
        placeControl(label, left, right, top, -1);
        return label;
    }

    /**
     * This method changes the Console Up/Down image on certain actions
     * @param right the space from the right side of the screen
     * @param bottom the space from the bottom side of the screen
     * @return ImageView object containing the appropriate properties
     */
    private ImageView imageToggle(double right, double bottom) {
        ImageView imageView = new ImageView(Constants.imgUpUp);
        imageView.setOnMousePressed(e -> imageView.setImage(Constants.imgUpDown));
        imageView.setOnMouseReleased(e -> imageView.setImage(Constants.imgUpUp));
        double w = 100 * scale;
        imageView.setFitWidth(w);
        imageView.setPreserveRatio(true);
        anchorPane.getChildren().add(imageView);
        placeControl(imageView, -1, right, -1, bottom);
        return imageView;
    }

    /**
     * This method changes the image on certain actions
     * @param bottom  the space from the bottom of the screen
     * @param imageDown the image of the down arrow icon
     * @param imageUp the image of the up arrow icon
     * @param left  the space from the left side of the screen
     * @param scale the fractional ratio to which the new image will be transformed
     * @return ImageView object containing the appropriate properties
     */
    private ImageView imageViewButton(Image imageUp, Image imageDown, double left, double bottom, double scale) {
        ImageView imageView = new ImageView(imageUp);
        anchorPane.getChildren().add(imageView);
        placeControl(imageView, left, -1, -1, bottom);
        imageView.setOnMouseReleased(e -> imageView.setImage(imageUp));
        imageView.setOnMousePressed(e -> imageView.setImage(imageDown));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(200);
        return imageView;
    }

    private ImageView imageView(Image image, double left, double top, double scale) {
        ImageView imageView = new ImageView(image);
        anchorPane.getChildren().add(imageView);
        placeControl(imageView, left, -1, top, -1);
        imageView.setPreserveRatio(true);
        double width = imageView.getImage().getWidth();
        imageView.setFitWidth(width * scale);
        return imageView;
    }

    /**
     * This method adds the checkbox [used by Auto-Paste] to the GUI screen
     * @param right the space from the right side of the screen
     * @param top the space from the top of the screen
     * @return a Checkbox object with the required properties
     */
    private CheckBox checkbox(double right, double top) {
        CheckBox checkbox = new CheckBox();
        checkbox.getStylesheets().add(Constants.checkBoxCSS.toExternalForm());
        anchorPane.getChildren().add(checkbox);
        placeControl(checkbox, -1, right, top, -1);
        return checkbox;
    }

    /**
     * This method creates a Progress Bar at the required position
     * @param left the space from the left side of the screen
     * @param right the space from the right side of the screen
     * @param top the space from the top of the screen
     * @return a Progress Bar object with the required position
     */
    private ProgressBar progressBar(double left, double right, double top) {
        ProgressBar progressBar = new ProgressBar(0.0);
        anchorPane.getChildren().add(progressBar);
        placeControl(progressBar, left, right, top, -1);
        progressBar.getStylesheets().add(Constants.progressBarCSS.toExternalForm());
        progressBar.getStyleClass().add("transparent-progress-bar");
        progressBar.progressProperty().bind(progressProperty);
        return progressBar;
    }

    /**
     * This method places the GUI elements in the appropriate position which is determined using the parameters passed to it
     * @param node the element which is to be placed in the GUI scene
     * @param left the space from the left side of the screen
     * @param right the space from the right side of the screen
     * @param top the space from the top of the screen
     * @param bottom the space from the bottom of the screen
     */
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

    /**
     * The menu bar of Drifty GUI screen
     * @param menus the menu items to be placed in the menu bar
     */
    private void menuBar(Menu... menus) {
        MenuBar menuBar = new MenuBar(menus);
        anchorPane.getChildren().add(menuBar);
        placeControl(menuBar, 0, 0, 0, -1);
    }

    /**
     * This method is used to get the menu items to be present in the <b>Menu section</b> of the GUI screen
     * @return a Menu object with the menu items for Menu section
     */
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

    /**
     * This method is used to get the menu items to be present in the <b>Help section</b> of the GUI screen
     * @return a Menu object with the menu items for Help section
     */
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

    /**
     * This method is used to get a context menu on click, specific for Directory
     * @return a ContextMenu object for the directory related options
     */
    private ContextMenu getRightClickContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        miAdd.setOnAction(e -> getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            folders = Settings.GET_PREFERENCES.getFolders();
        });
        ContextMenu contextMenu = new ContextMenu(miAdd, miDir);
        contextMenu.getStyleClass().add("rightClick");
        return contextMenu;
    }

    /**
     * This method is used to verify the link entered by the user
     * @param PreviousLink The link that was entered previously since the start of the Drifty GUI program
     * @param presentLink The link that has been entered just now
     */
    private void verifyLink(String PreviousLink, String presentLink) {
        if (!PreviousLink.equals(presentLink)) {
            if (downloadInProgress.getValue().equals(false) && processingBatch.getValue().equals(false)) {
                setLinkOutput(green,"Validating link ...");
                linkValid.setValue(false);
                if (presentLink.contains(" ")) {
                    Platform.runLater(() -> setLinkOutput(red,"Link should not contain whitespace characters!"));
                }
                else if (!isURL(presentLink)) {
                    Platform.runLater(() -> setLinkOutput(red,"String is not a URL"));
                }
                else {
                    try {
                        Utility.isURLValid(presentLink);
                        Platform.runLater(()-> setLinkOutput(green,"Valid URL"));
                        linkValid.setValue(true);
                    }
                    catch (Exception e) {
                        String errorMessage = e.getMessage();
                        Platform.runLater(()-> setLinkOutput(red,errorMessage));
                    }
                }
                if (linkValid.getValue().equals(true)) {
                    getFilename(presentLink);
                }
            }
        }
    }

    /**
     * This method is used to get the filename of a YouTube or Instagram video [By default, the filename is the video title]
     * @param link the link to the YouTube or Instagram video
     */
    private void getFilename(String link) {
        new Thread(() -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String filename = utility.findFilenameInLink(link);
            if (filename == null || filename.isEmpty()) {
                Platform.runLater(() -> filenameOutputLabel.setTextFill(green));
                gettingFilename = true;
                bounceFilename();
                LinkedList<String> jsonMetaData = Utility.getLinkMetadata(link);
                JsonElement element = JsonParser.parseString(jsonMetaData.getFirst());
                String json = gson.toJson(element);
                if (jsonMetaData.size() > 1) {
                    batch.setLink(link);
                    linkTextField.clear();
                    setLinkOutput(green,"");
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
                filenameTextField.setText(finalFilename);
                setFilenameOutput(green,"");
            });
        }).start();
    }

    /**
     * This method checks if the entered link is a URL or not
     * @param text the link (in String format) entered by the user
     * @return true if it is an url else false
     */
    private boolean isURL(String text) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * This method sets the output message [regarding Filename process] color according to the color provided as input
     * @param color color of the message
     * @param message the output message to be shown in the Filename output section
     */
    private void setFilenameOutput(Color color, String message) {
        filenameOutputLabel.setTextFill(color);
        filenameOutputLabel.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setFilenameOutput(green,""));
            }).start();
        }
    }

    /**
     * This method sets the output message [regarding Link process] color according to the color provided as input
     * @param color color of the message
     * @param message the output message to be shown in the link output section
     */
    private void setLinkOutput(Color color, String message) {
        linkOutputLabel.setTextFill(color);
        linkOutputLabel.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setLinkOutput(green,""));
            }).start();
        }
    }

    /**
     * This method sets the output message [regarding Directory process] color according to the color provided as input
     * @param color color of the message
     * @param message the output message to be shown in the directory output section
     */
    private void setDirOutput(Color color, String message) {
        directoryOutputLabel.setTextFill(color);
        directoryOutputLabel.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDirOutput(green,""));
            }).start();
        }
    }

    /**
     * This method sets the output message [regarding Download process] color according to the color provided as input
     * @param color color of the message
     * @param message the output message to be shown in the download output section
     */
    private void setDownloadOutput(Color color, String message) {
        downloadOutputLabel.setTextFill(color);
        downloadOutputLabel.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDownloadOutput(green,""));
            }).start();
        }
    }

    /**
     * This method is used to get the last copied text from the clipboard
     * @return the last copied text from the clipboard, in String format
     */
    private static String getClipboardText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            setMessage("Failed to get the last copied Text from the clipboard! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        }
        return null;
    }

    /**
     * This method is used to check if a file has already been downloaded,
     * and asks for confirmation from the user, if it needs to be downloaded again
     * @return true if the file needs to be downloaded again, else false
     */
    private boolean confirmDownload() {
        String filename = filenameTextField.getText();
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

    /**
     * This method is used to check if the files have already been downloaded,
     * and asks for confirmation from the user, if they need to be downloaded again
     */
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

    /**
     * This method performs Batch Downloading for Drifty GUI
     */
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
                        linkTextField.setText(linkToFile);
                        directoryTextField.setText(directoryForDownloading);
                        filenameTextField.setText(fileName);
                    });
                    if (Utility.isYoutubeLink(linkToFile)) {
                        isYouTubeURL = true;
                    }
                    Platform.runLater(() -> {
                        linkTextField.setText(linkToFile);
                        directoryTextField.setText(directoryForDownloading);
                        filenameTextField.setText(fileName);
                    });
                    sleep(1000);
                    download();
                    while (waitingForErrorCode) {
                        sleep(100);
                    }
                    Platform.runLater(() -> {
                        filenameTextField.clear();
                        linkTextField.clear();
                    });
                    if (jobError.getValue() != 0 && jobError.getValue() != -5) {
                        System.out.println("JOB ERROR VALUE: " + jobError.getValue());
                        failedJobList.add(job);
                        job.setError(downloadOutputLabel.getText());
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
                    setDownloadOutput(green,outMessage);
                    setLinkOutput(green,"");
                    setDirOutput(green,"");
                    setFilenameOutput(green,"");
                    linkTextField.clear();
                });
                if (Settings.GET_PREFERENCES.getJobs() != null) {
                    Settings.GET_PREFERENCES.getJobs().setJobList(failedJobList);
                    if (!failedJobList.isEmpty()) {
                        batch.setFailedList(failedJobList);
                    }
                }
            }
            processingBatch.setValue(false);
        }).start();
    }

    /**
     * This method is used to save the directory entered by the user after a gap of 5 seconds
     * @param folderString the directory in String format
     * @param folder a File object pointing to the folder in which the user wants to save the downloaded file
     */
    private void delayFolderSave(String folderString, File folder) {
        // If the user is typing a file path into the field, we don't want to save every folder 'hit' so we wait 5 seconds
        // and if the String is still the same value, then we commit the folder to the list.
        new Thread(() -> {
            sleep(3000);
            if (directoryTextField.getText().equals(folderString)) {
                folders.addFolder(folder.getAbsolutePath());
                System.out.println("Folder Added: " + folder.getAbsolutePath());
            }
        }).start();
    }

    /**
     * This method sets the filename in the appropriate text field area, after the filename gets retrieved
     */
    private void bounceFilename() {
        if (processingBatch.getValue().equals(false)) {
            new Thread(() -> {
                int count = 1;
                Platform.runLater(() -> setFilenameOutput(green,"Retrieving Filename"));
                while (gettingFilename) {
                    sleep(900);
                    if (count < 0) {
                        count = 1;
                        countUp = true;
                    }
                    final String out = "Retrieving Filename" + ".".repeat(count);
                    Platform.runLater(() -> setFilenameOutput(green,out));
                    if (countUp) {
                        count++;
                    }
                    else {
                        count--;
                    }
                    if (count == 4) {
                        countUp = false;
                    }
                    if (count == 1) {
                        countUp = true;
                    }
                }
                Platform.runLater(() -> setFilenameOutput(green,""));
            }).start();
        }
    }

    /**
     * This method is used to verify the link
     * @return true if the link is valid, else false
     */
    private boolean verifyLink() {
        boolean valid = false;
        try {
            Utility.isURLValid(linkTextField.getText());
            Platform.runLater(()-> setLinkOutput(green,"Valid URL"));
            valid = true;
        }
        catch (Exception e) {
            String errorMessage = e.getMessage();
            Platform.runLater(()-> setLinkOutput(red, errorMessage));
        }
        if (!valid) {
            new AskYesNo("The link provided is not a valid URL", true).isYes();
        }
         return valid;
    }

    /**
     * This method is used to verify the Directory
     * @return true if the directory is valid, else false
     */
    private boolean verifyDirectory() {
        File file = new File(directoryTextField.getText());
        boolean valid = file.exists();
        if (!valid) {
            new AskYesNo("The Download folder does not exist", true).isYes();
        }
        return file.exists();
    }

    /**
     * This method is used to verify the Filename
     * @return true if the filename is valid, else false
     */
    private boolean verifyFilename() {
        String pattern = "^[a-zA-Z0-9_.-]+$";
        String filename = filenameTextField.getText();
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

    /**
     * The main download method for executing file downloads in Drifty GUI
     */
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

    /**
     * This method is used to make the calling method to wait for the time in millisecond passed
     * @param time the time to make the calling thread to keep waiting
     */
    public static void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            setMessage("Link Metadata extracting thread failed to wait for " + time + ". It got interrupted. " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        }
    }
}
