package GUI.Forms;

import Backend.Drifty;
import Enums.*;
import GUI.Support.Constants;
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

    private final Stage stage                               = new Stage();
    private final double scale                              = .55;
    private final Utility utility                           = new Utility(new MessageBroker(System.out));
    private static final BooleanProperty downloadInProgress = new SimpleBooleanProperty(false);
    private static final BooleanProperty processingBatch    = new SimpleBooleanProperty(false);
    private static final BooleanProperty linkValid          = new SimpleBooleanProperty(false);
    private static final BooleanProperty directoryExists    = new SimpleBooleanProperty(false);
    private static final DoubleProperty progressProperty    = new SimpleDoubleProperty(0.0);
    private static final IntegerProperty jobError           = new SimpleIntegerProperty(-5);
    private final ConcurrentLinkedDeque<Job> failedJobList  = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<Job> jobList              = new ConcurrentLinkedDeque<>();
    private final Logger logger                             = Logger.getInstance();
    private final Color green                               = Color.rgb(0, 255, 0);
    private final Color red                                 = Color.rgb(177, 20 , 0);
    private final Color orange                              = Color.rgb(180, 80 , 0);
    private final String lf                                 = System.lineSeparator();
    private final Image imgAutoPaste                        = new Image(Constants.lblAutoPaste.toExternalForm());
    private final Image imgDirectory                        = new Image(Constants.lblDirectory.toExternalForm());
    private final Image imgFilename                         = new Image(Constants.lblFilename.toExternalForm());
    private final Image imgLink                             = new Image(Constants.lblLink.toExternalForm());
    private boolean firstRun                                = true;
    private boolean gettingFilename                         = false;
    private boolean countUp                                 = true;
    private boolean consoleOpen                             = false;
    private boolean waitingForErrorCode                     = true;
    private final URL monaco                                = getClass().getResource("/FX/Fonts/Monaco.ttf");
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
    private TextField tfLink;
    private TextField tfDir;
    private TextField tfFilename;
    private ImageView ivBack;
    private ImageView btnDownload;
    private ImageView btnBatch;
    private ImageView lblAutoPaste;
    private ImageView lblLink;
    private ImageView lblDirectory;
    private ImageView lblFilename;
    private CheckBox cbPaste;
    private Label lblLinkOutput;
    private Label lblDirectoryOutput;
    private Label lblDownloadOutput;
    private Label lblFilenameOutput;
    private ImageView btnConsole;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        folders = AppSettings.get.folders();
        logger.log(Type.INFORMATION, GUI_APPLICATION_STARTED); // log a message when the Graphical User Interface (GUI) version of Drifty is triggered to start
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // E.g.: java.awt.Dimension[width=1366,height=768]
        height = (int) screenSize.getHeight(); // E.g.: 768
        width = (int) screenSize.getWidth(); // E.g.: 1366
        double[] newDim = Utility.fraction(width, height, scale);
        width = newDim[0];
        height = newDim[1];
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
        INSTANCE.doBatch();
    }

    public static void setMessage(String message, Type messageType, Category category) {
        Color color = switch (messageType) {
            case INFORMATION -> INSTANCE.green;
            case ERROR -> INSTANCE.red;
            case WARNING -> INSTANCE.orange;
        };
        switch (category) {
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
        lblLink = imageView(imgLink,left,top-25, (scale + scale/4));
        lblAutoPaste = imageView(imgAutoPaste,left + (width - left) * scale * 1.345,top-28, (scale + scale/4));
        tfLink = textField(left, right, top);
        lblLinkOutput = label("", left, right, top + vOffset);
        top += delta;
        lblDirectory = imageView(imgDirectory,left,top-25, (scale + scale/6));
        tfDir = textField(left, right, top);
        lblDirectoryOutput = label("", left, right, top + vOffset);
        top += delta; //170
        lblFilename = imageView(imgFilename,left,top-25, (scale + scale/8));
        tfFilename = textField(left, right, top);
        lblFilenameOutput = label("", left, right, top + vOffset);
        lblDownloadOutput = label("", left, right, top + (vOffset * 1.45));
        Image btnDownloadUp = new Image(Constants.downloadUp.toExternalForm());
        Image btnDownloadDown = new Image(Constants.downloadDown.toExternalForm());
        Image btnBatchUp = new Image(Constants.batchUp.toExternalForm());
        Image btnBatchDown = new Image(Constants.batchDown.toExternalForm());
        double buttonWidth = btnDownloadUp.getWidth() * scale * .95;
        double buttonOffset = buttonWidth / 2;
        double btnPlace = width / 4;
        double placement = btnPlace - buttonOffset;
        btnDownload = imageViewButton(btnDownloadUp, btnDownloadDown, placement, 0, buttonWidth);
        placement = (btnPlace * 3) - buttonOffset;
        btnBatch = imageViewButton(btnBatchUp, btnBatchDown, placement, 0, buttonWidth);
        cbPaste = checkbox(width * .089, height * .34);
        btnConsole = imageViewToggle(width/2 - 15, 6.5);
        menuBar(getMenuMenuItems(), getHelpMenuItems());
    }

    private void setControlProperties() {
        tfDir.setText(folders.getDownloadFolder());
        directoryExists.setValue(new File(tfDir.getText()).exists());
        jobError.addListener(((observable, oldValue, newValue) -> {
            System.out.println("jobError: " + newValue);
            if (newValue.equals(-5)) {
                return;
            }
            int value = (int) newValue;
            Platform.runLater(() -> {
                if (value == 0)
                    lblDownloadOutput.setTextFill(green);
                else
                    setDownloadOutput(red,errorMessage);
            });
            jobError.setValue(-5);
            waitingForErrorCode = false;
        }));
        BooleanBinding checkState = cbPaste.selectedProperty().not().not();
        BooleanBinding disableDownloadButton = downloadInProgress.or(directoryExists.not());
        BooleanBinding disableInputs = downloadInProgress.not();
        BooleanBinding hasText = tfLink.textProperty().isEmpty().not().and(tfDir.textProperty().isEmpty().not().and(tfFilename.textProperty().isEmpty().not()));
        btnDownload.visibleProperty().bind(hasText);
        tfLink.setAlignment(Pos.CENTER_LEFT);
        tfLink.editableProperty().bind(disableInputs);
        lblLinkOutput.setAlignment(Pos.CENTER_LEFT);
        tfDir.setAlignment(Pos.CENTER_LEFT);
        tfDir.editableProperty().bind(disableInputs);
        tfFilename.setAlignment(Pos.CENTER_LEFT);
        tfFilename.editableProperty().bind(disableInputs);
        lblDirectoryOutput.setAlignment(Pos.CENTER_LEFT);
        lblFilenameOutput.setAlignment(Pos.CENTER_LEFT);
        lblDownloadOutput.setAlignment(Pos.CENTER_LEFT);
        btnDownload.disableProperty().bind(disableDownloadButton);
        Tooltip.install(cbPaste, new Tooltip("When checked, will paste contents of clipboard into" + lf + "Link field when switching back to this screen."));
    }

    private void setControlActions() {
        cbPaste.setSelected(AppSettings.get.mainAutoPaste());
        cbPaste.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.set.mainAutoPaste(newValue)));
        btnDownload.setOnMouseClicked(e -> new Thread(() -> {
            if (confirmDownload()) {
                jobList.clear();
                jobList.add(new Job(tfLink.getText(), tfDir.getText(), tfFilename.getText()));
                doBatch();
            }
        }).start());
        btnBatch.setOnMouseClicked(e -> batch.show());
        stage.focusedProperty().addListener(((observable, wasFocused, isFocused) -> {
            if (firstRun) {
                firstRun = false;
                return;
            }
            if (isFocused && cbPaste.isSelected()) {
                tfLink.setText(getClipboardText());
            }
            if (isFocused && !wasFocused) {
                consoleOut.rePosition(width, height, stage.getX(), stage.getY());
            }
        }));
        tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                directoryExists.setValue(false);
                if (newValue.length() == 0) {
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
        tfLink.textProperty().addListener(((observable, oldValue, newValue) -> verifyLink(oldValue, newValue)));
        btnConsole.setOnMouseClicked(e-> toggleConsole());
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
        Mode.setGUILoaded();
    }

    private void toggleConsole() {
        if (!consoleOpen) {
            btnConsole.setImage(Constants.imgDownUp);
            btnConsole.setOnMousePressed(e -> btnConsole.setImage(Constants.imgDownDown));
            btnConsole.setOnMouseReleased(e -> btnConsole.setImage(Constants.imgDownUp));
            consoleOut.show();
            consoleOpen = true;
        }
        else {
            btnConsole.setImage(Constants.imgUpUp);
            btnConsole.setOnMousePressed(e -> btnConsole.setImage(Constants.imgUpDown));
            btnConsole.setOnMouseReleased(e -> btnConsole.setImage(Constants.imgUpUp));
            consoleOut.hide();
            consoleOpen = false;
        }
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
                logger.log(Type.ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }
        }
        else if (OS.isWinMac()) { // For macOS and Windows systems
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(websiteURL));
            } catch (IOException | URISyntaxException e) {
                logger.log(Type.ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }
        }
    }

    /**
     * Control Creation Methods
     */

    private TextField textField(double left, double right, double top) {
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

    private Label label(String text, double left, double right, double top) {
        Label label = new Label(text);
        label.setFont(new Font(monaco.toExternalForm(), 18));
        anchorPane.getChildren().add(label);
        placeControl(label, left, right, top, -1);
        return label;
    }

    private Label labelAnchor(String text, double left, double right, double top) {
        Label label = new Label(text);
        label.setFont(new Font(monaco.toExternalForm(), 18));
        anchorPane.getChildren().add(label);
        placeControl(label, left, right, top, -1);
        return label;
    }

    private ImageView imageViewToggle(double right, double bottom) {
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

    private CheckBox checkbox(double right, double top) {
        CheckBox checkbox = new CheckBox();
        //checkbox.setStyle("-fx-background-color: transparent;");
        checkbox.getStylesheets().add(Constants.checkBoxCSS.toExternalForm());
        anchorPane.getChildren().add(checkbox);
        placeControl(checkbox, -1, right, top, -1);
        return checkbox;
    }

    private ProgressBar progressBar(double left, double right, double top) {
        ProgressBar progressBar = new ProgressBar(0.0);
        anchorPane.getChildren().add(progressBar);
        placeControl(progressBar, left, right, top, -1);
        progressBar.getStylesheets().add(Constants.progressBarCSS.toExternalForm());
        progressBar.getStyleClass().add("transparent-progress-bar");
        progressBar.progressProperty().bind(progressProperty);
        return progressBar;
    }

    private void placeControl(Node node, double left, double right, double top, double bottom) {
        if (top != -1) setTopAnchor(node, top);
        if (bottom != -1) setBottomAnchor(node, bottom);
        if (left != -1) setLeftAnchor(node, left);
        if (right != -1) setRightAnchor(node, right);
    }

    private void menuBar(Menu... menus) {
        MenuBar menuBar = new MenuBar(menus);
        anchorPane.getChildren().add(menuBar);
        placeControl(menuBar, 0, 0, 0, -1);
    }

    private Menu getMenuMenuItems() {
        Menu menu = new Menu("Menu");
        MenuItem website = new MenuItem("Project Website");
        website.setOnAction(e -> openWebsite(Drifty.projectWebsite, "project website"));
        MenuItem about = new MenuItem("About");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            logger.log(Type.INFORMATION, DriftyConstants.GUI_APPLICATION_TERMINATED);
            System.exit(0);
        });
        menu.getItems().setAll(website, about, exit);
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

    /**
     * YT-DLP Actions And Form Logic
     */

    private void verifyLink(String oldValue, String newValue) {
        if (!oldValue.equals(newValue)) {
            if (downloadInProgress.getValue().equals(false) && processingBatch.getValue().equals(false)) {
                setLinkOutput(green,"Validating link ...");
                linkValid.setValue(false);
                if (newValue.contains(" ")) {
                    Platform.runLater(() -> setLinkOutput(red,"Link should not contain whitespace characters!"));
                }
                else if (!isURL(newValue)) {
                    Platform.runLater(() -> setLinkOutput(red,"String is not a URL"));
                }
                else {
                    try {
                        Utility.isURLValid(newValue);
                        Platform.runLater(()-> setLinkOutput(green,"Valid URL"));
                        linkValid.setValue(true);
                    }
                    catch (Exception e) {
                        String errorMessage = e.getMessage();
                        Platform.runLater(()-> setLinkOutput(red,errorMessage));
                    }
                }
                if (linkValid.getValue().equals(true)) {
                    getFilename(newValue);
                }
            }
        }
    }

    private void getFilename(String link) {
        new Thread(() -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String filename = utility.findFilenameInLink(link);
            if (filename == null || filename.isEmpty()) {
                Platform.runLater(() -> lblFilenameOutput.setTextFill(green));
                gettingFilename = true;
                bounceFilename();
                LinkedList<String> jsonMetaData = Utility.getJsonLinkMetadata(link);
                JsonElement element = JsonParser.parseString(jsonMetaData.getFirst());
                String json = gson.toJson(element);
                if (jsonMetaData.size() > 1) {
                    batch.setLink(link);
                    tfLink.clear();
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
                tfFilename.setText(finalFilename);
                setFilenameOutput(green,"");
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
        lblFilenameOutput.setTextFill(color);
        lblFilenameOutput.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setFilenameOutput(green,""));
            }).start();
        }
    }

    private void setLinkOutput(Color color, String message) {
        lblLinkOutput.setTextFill(color);
        lblLinkOutput.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setLinkOutput(green,""));
            }).start();
        }
    }

    private void setDirOutput(Color color, String message) {
        lblDirectoryOutput.setTextFill(color);
        lblDirectoryOutput.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDirOutput(green,""));
            }).start();
        }
    }

    private void setDownloadOutput(Color color, String message) {
        lblDownloadOutput.setTextFill(color);
        lblDownloadOutput.setText(message);
        if (color.equals(red)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDownloadOutput(green,""));
            }).start();
        }
    }

    private static String getClipboardText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean notReady() {
        return downloadInProgress.getValue().equals(true);
    }

    private boolean confirmDownload() {
        String filename = tfFilename.getText();
        for (String folder : folders.getFolders()) {
            boolean fileExists = false;
            String filePath = "";
            FileWalker walker = new FileWalker(folder, filename);
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
                String msg = "The file: " + lf + lf + filePath + lf + lf + " Already exists, Do you wish to download it again?";
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
            FileWalker walker = new FileWalker(folder, files);
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
            StringBuilder sb = new StringBuilder("The following files already exist:" + lf + lf);
            for (String path : pathJobMap.keySet()) {
                sb.append(path).append(lf);
            }
            sb.append(lf).append("Do you want to download them again?");
            AskYesNo ask = new AskYesNo(sb.toString());
            if (!ask.isYes()) {
                for (Job job : pathJobMap.values()) {
                    jobList.remove(job);
                }
            }
        }
    }

    private void doBatch() {
        processingBatch.setValue(true);
        new Thread(() -> {
            failedJobList.clear();
            if (!jobList.isEmpty()) {
                checkFiles();
                final int max = jobList.size();
                System.out.println("jobList size: " + max);
                int count = 0;
                for (Job job : jobList) {
                    count++;
                    String jobCount = "Processing job " + count + " of " + max + ": " + job;
                    System.out.println("jobCount: "+jobCount);
                    Platform.runLater(() -> setFilenameOutput(green,jobCount));
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
                        job.setError(lblDownloadOutput.getText());
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
        // and if the String is still the same value then we commit the folder to the list.
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
     * Download Methods
     */

    private boolean verifyLink() {
        boolean valid = false;
        try {
            Utility.isURLValid(tfLink.getText());
            Platform.runLater(()-> setLinkOutput(green,"Valid URL"));
            valid = true;
        }
        catch (Exception e) {
            String errorMessage = e.getMessage();
            Platform.runLater(()-> setLinkOutput(red,errorMessage));
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

    /**
     * Class Utilities
     */

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
