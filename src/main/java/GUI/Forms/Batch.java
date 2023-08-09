package GUI.Forms;

import Enums.Program;
import Enums.Format;
import GUI.Support.*;
import GUI.experiment.AskYesNo;
import Preferences.AppSettings;
import Utils.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import static GUI.Forms.Constants.*;
import static javafx.scene.layout.AnchorPane.*;

public class Batch {
    private final double scale = .6;
    private final double width;
    private final double height;
    private Stage stage;
    private Folders folders;
    private ConcurrentLinkedDeque<Job> jobList;
    private static final BooleanProperty directoryExists = new SimpleBooleanProperty(false);
    private static final BooleanProperty gettingFilenames = new SimpleBooleanProperty(false);
    private final LinkedList<String> linkQue = new LinkedList<>();
    private final Random random = new Random(System.currentTimeMillis());
    private boolean countUp = true;
    private boolean jobPaste = false;
    private boolean firstRun = true;
    private boolean consoleOpen = false;
    private final Image imgAutoPaste = new Image(Constants.autoPasteLabelImage.toExternalForm());
    private final Image imgDirectory = new Image(Constants.directoryLabelImage.toExternalForm());
    private final Image imgFilename = new Image(Constants.filenameLabelImage.toExternalForm());
    private final Image imgLink = new Image(Constants.linkLabelImage.toExternalForm());
    private final ConsoleOut consoleOut;
    private String postMessage;
    private Process linkProcess;
    private Thread linkThread;
    private AnchorPane anchorPane;
    private ImageView ivBack;
    private ListView listView;
    private TextField tfLink;
    private TextField tfDir;
    private ImageView ivAutoLabel;
    private TextField tfFilename;
    private ImageView ivLinkLabel;
    private ImageView ivDirLabel;
    private ImageView ivFilenameLabel;
    private ImageView ivBtnSave;
    private ImageView ivBtnRunBatch;
    private ImageView ivBtnClose;
    private CheckBox cbAutoPaste;
    private static TextArea taOutput;
    private ImageView ivBtnConsole;
    private final Color black = Constants.BLACK;
    private Label lblLinkOut;
    private Label lblDirOut;
    private Label lblDownloadInfo;
    private Label lblFilenameOut;
    private Timer bounceTimer;
    private Timer clockTimer;
    private VBox vbox;

    public Batch(ConsoleOut consoleOut) {
        this.consoleOut = consoleOut;
        height = (int) screenSize.getHeight() * scale;
        width = (int) screenSize.getWidth() * scale;
        folders = AppSettings.get.folders();
        if (AppSettings.get.jobs() != null) {
            jobList = AppSettings.get.jobs().jobList();
        }
        else {
            jobList = new ConcurrentLinkedDeque<>();
        }
        Platform.runLater(() -> {
            createControls();
            setControlProperties();
            setControlActions();
            makeScene();
            tfDir.setText(folders.getDownloadFolder());
        });
    }

    private TextField newTextField() {
        TextField tf = new TextField();
        tf.setFont(new Font(monacoFont.toExternalForm(), 19));
        tf.setPrefHeight(45);
        return tf;
    }

    private HBox makeButtonBox() {
        Image btnDownloadUp = new Image(Constants.downloadUp.toExternalForm());
        Image btnDownloadDown = new Image(Constants.downloadDown.toExternalForm());
        Image btnBatchUp = new Image(Constants.batchUp.toExternalForm());
        Image btnBatchDown = new Image(Constants.batchDown.toExternalForm());
        ivBtnSave = imageViewButton(saveUp, saveDown, .5);
        ivBtnRunBatch = imageViewButton(runBatchUp, runBatchDown, .5);
        ivBtnClose = imageViewButton(closeUp, closeDown, .5);
        HBox box = new HBox(100, ivBtnSave, ivBtnRunBatch, ivBtnClose);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Label getSpacer() {
        Label label = new Label();
        label.setPrefWidth(screenSize.getWidth());
        return label;
    }

    private Label label() {
        Label label = new Label();
        label.setFont(new Font(monacoFont.toExternalForm(), 20));
        return label;
    }

    private ImageView imageToggle(double scale) {
        ImageView imageView = new ImageView(Constants.imgUpUp);
        imageView.setOnMousePressed(e -> imageView.setImage(Constants.imgUpDown));
        imageView.setOnMouseReleased(e -> imageView.setImage(Constants.imgUpUp));
        double width = Constants.imgUpUp.getWidth();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width * scale);
        anchorPane.getChildren().add(imageView);
        placeControl(imageView, -1, 20, -1, 20);
        return imageView;
    }

    private ImageView imageViewButton(URL imageUp, URL imageDown, double scale) {
        Image iu = new Image(imageUp.toExternalForm());
        Image id = new Image(imageDown.toExternalForm());
        ImageView imageView = new ImageView(iu);
        double width = iu.getWidth();
        imageView.setOnMouseReleased(e -> imageView.setImage(iu));
        imageView.setOnMousePressed(e -> imageView.setImage(id));
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

    private void placeControl(Node node, double left, double right, double top, double bottom) {
        if (top != -1) setTopAnchor(node, top);
        if (bottom != -1) setBottomAnchor(node, bottom);
        if (left != -1) setLeftAnchor(node, left);
        if (right != -1) setRightAnchor(node, right);
    }

    private void createControls() {
        anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(width);
        anchorPane.setPrefHeight(height);
        Image imgBanner = new Image(Constants.batchGUIBanner.toExternalForm());
        ImageView ivBanner = imageView(imgBanner, .8);
        VBox boxBanner = new VBox(30, ivBanner, getSpacer(), getSpacer());
        boxBanner.setAlignment(Pos.CENTER);
        ivLinkLabel = imageView(imgLink, .8);
        ivAutoLabel = imageView(imgAutoPaste, .8);
        cbAutoPaste = new CheckBox();
        HBox boxLinkLabel = new HBox(10, ivLinkLabel, getSpacer(), ivAutoLabel, cbAutoPaste);
        tfLink = newTextField();
        lblLinkOut = label();
        HBox boxLinkOut = new HBox(lblLinkOut);
        boxLinkOut.setAlignment(Pos.CENTER_LEFT);
        ivDirLabel = imageView(imgDirectory, .8);
        HBox boxDirLabel = new HBox(ivDirLabel);
        boxDirLabel.setAlignment(Pos.CENTER_LEFT);
        tfDir = newTextField();
        lblDirOut = label();
        HBox boxLblDirOut = new HBox(lblDirOut);
        boxLblDirOut.setAlignment(Pos.CENTER_LEFT);
        ivFilenameLabel = imageView(imgFilename, .8);
        HBox boxFilenameLabel = new HBox(ivFilenameLabel);
        boxFilenameLabel.setAlignment(Pos.CENTER_LEFT);
        tfFilename = newTextField();
        lblFilenameOut = label();
        HBox boxLblFilenameOut = new HBox(lblFilenameOut);
        boxLblFilenameOut.setAlignment(Pos.CENTER_LEFT);
        lblDownloadInfo = label();
        HBox boxLblDownloadInfo = new HBox(lblDownloadInfo);
        boxLblDownloadInfo.setAlignment(Pos.CENTER_LEFT);
        VBox vboxMain = new VBox(10, boxLinkLabel, tfLink, boxLinkOut, boxDirLabel, tfDir, boxLblDirOut, boxFilenameLabel, tfFilename, boxLblFilenameOut, boxLblDownloadInfo);
        listView = listView();
        HBox boxListView = new HBox(40, listView, vboxMain);
        vbox = new VBox(10, boxBanner, boxListView, makeButtonBox());
        vbox.setPadding(new Insets(50, 50, 0, 50));
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(width);
        vbox.setPrefHeight(height);
        ivBtnConsole = imageToggle(.5);
        anchorPane.getChildren().add(vbox);
    }

    private void setControlProperties() {
        cbAutoPaste.setSelected(AppSettings.get.batchAutoPaste());
        tfDir.setText(folders.getDownloadFolder());
        tfLink.textProperty().addListener(((observable, oldLink, newLink) -> {
            if (!oldLink.equals(newLink)) {
                checkLink(newLink);
            }
        }));
        tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                directoryExists.setValue(false);
                if (newValue.isEmpty()) {
                    String folderPath = AppSettings.get.lastDownloadFolder();
                    if (folderPath.isEmpty()) {
                        setDirOut(Constants.RED, "Directory cannot be empty!");
                    }
                    else {
                        new Thread(() -> {
                            sleep(200);
                            Platform.runLater(() -> tfDir.setText(folderPath));
                        }).start();
                    }
                }
                else {
                    File file = new File(newValue);
                    if (file.exists() && file.isDirectory()) {
                        setDirOut(Constants.GREEN, "Directory OK");
                        directoryExists.setValue(true);
                    }
                    else {
                        setDirOut(Constants.RED, "Directory does not exist or is not a directory!");
                    }
                }
                if (directoryExists.getValue().equals(true)) {
                    folders.addFolder(newValue);
                }
            }
        }));
        listView.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 1) {
                Job job = (Job) listView.getSelectionModel().getSelectedItem();
                if (job != null) {
                    jobPaste = true;
                    String error = job.getError();
                    tfLink.setText(job.getLink());
                    tfDir.setText(job.getDir());
                    tfFilename.setText(job.getFilename());
                    if (error != null) {
                        if (!error.isEmpty()) {
                            setFileOut(Constants.RED, error);
                        }
                    }
                }
            }
        });
        listView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                Job job = (Job) listView.getSelectionModel().getSelectedItem();
                if (job != null) {
                    jobList.remove(job);
                    commitJobListToListView();
                    clearControls();
                }
            }
        });
        listView.setContextMenu(getListMenu());
        BooleanBinding listPopulated = listView.itemsProperty().isNotNull().and(listView.itemsProperty().asString().isEmpty().not());
        BooleanBinding hasText = tfLink.textProperty().isEmpty().not().and(tfDir.textProperty().isEmpty().not().and(tfFilename.textProperty().isEmpty().not()));
        BooleanBinding isListEmpty = Bindings.isEmpty(listView.getItems()).not();
        ivBtnRunBatch.visibleProperty().bind(isListEmpty);
        ivBtnSave.visibleProperty().bind(hasText);
        Tooltip.install(tfLink, new Tooltip("Paste in a link and it will be added to the batch once name resolution has been attempted.\nSelect Auto Paste to have the contents of your clipboard automatically pasted here when\nyou switch back to this window."));
        Tooltip.install(tfDir, new Tooltip("Right click anywhere to add or manage directories\nLast directory added becomes download folder."));
        Tooltip.install(tfFilename, new Tooltip("This will be the name of the file that gets written to the Directory above."));
    }

    private void setControlActions() {
        ivBtnClose.setOnMouseClicked(e -> close());
        ivBtnSave.setOnMouseClicked(e -> saveBatch());
        ivBtnRunBatch.setOnMouseClicked(e -> runBatch());
        cbAutoPaste.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                AppSettings.set.batchAutoPaste(newValue);
            }
        }));
        ivBtnConsole.setOnMouseClicked(e -> toggleConsole(false));
    }

    private void triageInbound(String newLink) {
        try {
            boolean valid = false;
            String[] urls = newLink.split(" ");
            if (urls.length > 1) {
                for (String url : urls) {
                    if (!Utility.urlIsValid(url))
                        return;
                }
                newLink = newLink.replaceAll(" ", "");
            }
            else {
                if (!Utility.urlIsValid(newLink)) {
                    return;
                }
            }
            tfLink.setText(newLink);
        } catch (Exception ignored) {
        }
    }

    public void makeScene() {
        stage = Constants.getStage();
        stage.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (firstRun) {
                firstRun = false;
                return;
            }
            if (newValue && cbAutoPaste.isSelected()) {
                String newLink = getClipboardText();
                triageInbound(newLink);
            }
        }));
        Scene scene = new Scene(anchorPane);
        scene.widthProperty().addListener(((observable, oldValue, newValue) -> {
            vbox.setPrefWidth((double) newValue);
            if (consoleOut != null) {
                consoleOut.setWidth((double) newValue);
            }
        }));
        scene.heightProperty().addListener(((observable, oldValue, newValue) -> vbox.setPrefHeight((double) newValue)));
        scene.setOnContextMenuRequested(e -> getContextMenu().show(scene.getWindow(), e.getScreenX(), e.getScreenY()));
        scene.getStylesheets().add(contextMenuCSS.toExternalForm());
        scene.getStylesheets().add(labelCSS.toExternalForm());
        scene.getStylesheets().add(menuCSS.toExternalForm());
        scene.getStylesheets().add(checkBoxCSS.toExternalForm());
        scene.getStylesheets().add(textFieldCSS.toExternalForm());
        scene.getStylesheets().add(vBoxCSS.toExternalForm());
        scene.getStylesheets().add(sceneCSS.toExternalForm());
        scene.getStylesheets().add(listViewCSS.toExternalForm());
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
        //stage.fullScreenProperty().addListener(((observable, oldValue, newValue) -> ivBtnConsole.setVisible(!newValue)));
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setMinHeight(height);
    }

    public void show() {
        stage.show();
        commitJobListToListView();
        consoleOut.rePosition(width, height, stage.getX(), stage.getY());
        ivBtnConsole.toFront();
    }

    private void close() {
        consoleOut.hide();
        if (linkThread != null && (linkThread.getState().equals(Thread.State.RUNNABLE) || linkThread.getState().equals(Thread.State.TIMED_WAITING))) {
            AskYesNo askYesNo = new AskYesNo();
            if (askYesNo.isYes()) {
                linkThread.interrupt();
                while (linkThread.getState().equals(Thread.State.RUNNABLE) || linkThread.getState().equals(Thread.State.TIMED_WAITING)) {
                    sleep(10);
                }
                stage.close();
                return;
            }
            return;
        }
        stage.close();
    }

    private ContextMenu getListMenu() {
        MenuItem miDel = new MenuItem("Delete");
        MenuItem miClear = new MenuItem("Clear");
        MenuItem miInfo = new MenuItem("Information");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        miDel.setOnAction(e -> {
            Job job = (Job) listView.getSelectionModel().getSelectedItem();
            if (job != null) {
                jobList.remove(job);
                commitJobListToListView();
                clearControls();
            }
        });
        miClear.setOnAction(e -> {
            jobList.clear();
            commitJobListToListView();
            tfLink.clear();
            tfFilename.clear();
            setLinkOut(Constants.GREEN, "");
            setDirOut(Constants.GREEN, "");
            setFileOut(Constants.GREEN, "");
        });
        miInfo.setOnAction(e -> info());
        return new ContextMenu(miDel, miClear, separator, miInfo);
    }

    private ContextMenu getContextMenu() {
        MenuItem miAdd = new MenuItem("Add Directory");
        MenuItem miDir = new MenuItem("Manage Directories");
        MenuItem miInfo = new MenuItem("Information");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        miAdd.setOnAction(e -> getDirectory());
        miDir.setOnAction(e -> {
            ManageFolders manage = new ManageFolders();
            manage.showScene();
            folders = AppSettings.get.folders();
        });
        miInfo.setOnAction(e -> info());
        return new ContextMenu(miAdd, miDir, separator, miInfo);
    }

    private ListView listView() {
        ListView<Job> listView = new ListView<>();
        listView.setMinWidth(300);
        return listView;
    }

    private void clearControls() {
        Platform.runLater(() -> {
            tfLink.clear();
            tfDir.clear();
            tfFilename.clear();
        });
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
            folders.addFolder(directory.getAbsolutePath());
        }
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

    public void setFailedList(ConcurrentLinkedDeque<Job> jobList) {
        this.jobList = jobList;
        Platform.runLater(() -> {
            commitJobListToListView();
            tfLink.clear();
            tfFilename.clear();
            setLinkOut(Constants.RED, "These jobs failed, click on one to find out why and re-try them if you wish");
            setDirOut(Constants.GREEN, "");
            setFileOut(Constants.GREEN, "");
        });
    }

    private void setFileOut(Color color, String message) {
        Platform.runLater(() -> {
            lblFilenameOut.setTextFill(color);
            lblFilenameOut.setText(message);
        });
    }

    private void setDirOut(Color color, String message) {
        Platform.runLater(() -> {
            lblDirOut.setTextFill(color);
            lblDirOut.setText(message);
        });
    }

    public void setLink(String link) {
        Platform.runLater(() -> tfLink.setText(link));
    }

    private void setLinkOut(Color color, String message) {
        Platform.runLater(() -> {
            lblLinkOut.setTextFill(color);
            lblLinkOut.setText(message);
        });
    }

    int bounceCount = 1;

    private TimerTask bounceTask() {
        return new TimerTask() {
            @Override
            public void run() {
                final String out = "Retrieving Filename" + ".".repeat(bounceCount);
                Platform.runLater(() -> setFileOut(Constants.GREEN, out));
                bounceCount = countUp ? bounceCount + 1 : bounceCount - 1;
                countUp = bounceCount != 5 && (bounceCount == 1 || countUp);
            }
        };
    }

    private long startTime = 0;
    private boolean listFound = false;

    private TimerTask clockTask() {
        long HOUR = Constants.HOUR;
        long MINUTE = Constants.MINUTE;
        long SECOND = Constants.SECOND;
        return new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long millis = now - startTime;
                long hours = millis / HOUR;
                millis = millis % HOUR;
                long minutes = millis / MINUTE;
                millis = millis % MINUTE;
                long seconds = millis / SECOND;
                String strHours = (hours < 10) ? "0" + hours : String.valueOf(hours);
                String strMinutes = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
                String strSeconds = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);
                String finalTime = (hours == 0) ? strMinutes + ":" + strSeconds : strHours + ":" + strMinutes + ":" + strSeconds;
                setFileOut(Color.DARKBLUE, finalTime);
                if (gettingFilenames.getValue().equals(false)) {
                    clockTimer.cancel();
                    clockTimer = null;
                    setFileOut(Constants.GREEN, "");
                }
                File tempFolder = Paths.get(Program.get(Program.PATH), "Drifty").toFile();
                if (tempFolder.exists()) {
                    List<File> fileList = new ArrayList<>();
                    File[] files = tempFolder.listFiles();
                    for (File file : files) {
                        String ext = FilenameUtils.getExtension(file.getAbsolutePath());
                        if (ext.endsWith("json")) {
                            try {
                                String contents = FileUtils.readFileToString(file, Charset.defaultCharset());
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                JsonElement element = JsonParser.parseString(contents);
                                String json = gson.toJson(element);
                                String filename = Utility.getFilenameFromJson(json);
                                String urlLink = Utility.getURLFromJson(json);
                                if (!urlLink.isEmpty()) {
                                    ext = FilenameUtils.getExtension(filename);
                                    if (!Format.isValid(ext)) {
                                        filename = Utility.cleanFilename(filename) + ".mp4";
                                    }
                                }
                                jobList.add(new Job(urlLink, tfDir.getText(), filename));
                                commitJobListToListView();
                                fileList.add(file);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    for (File file : fileList) {
                        try {
                            FileUtils.forceDelete(file);
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        };
    }

    public ConcurrentLinkedDeque<Job> getJobList() {
        return jobList;
    }

    private void checkForList() {
        new Thread(() -> {
            boolean run = true;
            String search = ".json";
            startTime = System.currentTimeMillis();
            while (gettingFilenames.getValue().equals(true) && run) {
                String console = ConsoleOut.getStandardOut();
                int occurrences = 0;
                int index = console.indexOf(search);
                while (index != -1) {
                    occurrences++;
                    console = ConsoleOut.getStandardOut();
                    index = console.indexOf(search, index + 1);
                }
                if (occurrences > 1) {
                    if (clockTimer == null) {
                        clockTimer = new Timer();
                        clockTimer.scheduleAtFixedRate(clockTask(), 500, 250);
                        setLinkOut(Color.DARKBLUE, "Extracting multiple files from possible list");
                        listFound = true;
                    }
                    run = false;
                }
                sleep(100);
            }
            if (bounceTimer != null) {
                bounceTimer.cancel();
                bounceTimer = null;
            }
        }).start();
    }

    private void findName(String link) {
        gettingFilenames.setValue(true);
        new Thread(() -> {
            sleep(1000);
            Platform.runLater(() -> {
                ConsoleOut.clear();
                jobPaste = true;
                tfLink.setText(link);
                tfFilename.clear();
                setLinkOut(Constants.GREEN, "Extracting data from link, please be patient");
                setFileOut(Constants.GREEN, "Retrieving Filename");
            });
            if (bounceTimer == null) {
                bounceTimer = new Timer();
                bounceTimer.scheduleAtFixedRate(bounceTask(), 100, 2700);
            }
            checkForList();
            LinkedList<String> jsonMetadataList = Utility.getLinkMetadata(link);
            if (!listFound) {
                gettingFilenames.setValue(false);
                createBatch(jsonMetadataList, link);
                setFileOut(Constants.TEAL, "Done - Job Added");
            }
            else {
                setFileOut(Constants.TEAL, "Done - Jobs Added");
                listFound = false;
            }
            sleep(1000);
        }).start();
    }

    private void checkLink(String link) {
        if (link.contains(" ")) {
            setLink("");
            triageInbound(link);
        }
        if (link.isEmpty()) {
            setLinkOut(Constants.GREEN, "");
            return;
        }
        if (jobPaste) {
            setLinkOut(Constants.GREEN, "");
            jobPaste = false;
            return;
        }
        if (link.contains("") || link.contains("\\n")) {
            setLinkOut(Constants.GREEN, "");
            processLinks(link);
            return;
        }
        Job job = jobExists(link);
        if (job == null) {
            setLinkOut(Constants.GREEN, "Validating link ...");
            if (link.matches(".+\\s+.+")) {
                setLinkOut(Constants.RED, "Link should not contain whitespace characters!");
            }
            else {
                try {
                    Utility.isURLValid(link);
                    setLinkOut(Constants.GREEN, "Valid URL");
                    findName(link);
                } catch (Exception e) {
                    String errorMessage = e.getMessage();
                    setLinkOut(Constants.RED, errorMessage);
                }
            }
        }
        else {
            setLinkOut(Constants.ORANGE, "Duplicate Link");
            tfDir.setText(job.getDir());
            new Thread(() -> {
                for (int x = 0; x < 3; x++) {
                    Platform.runLater(() -> tfFilename.clear());
                    sleep(80);
                    Platform.runLater(() -> tfFilename.setText(job.getFilename()));
                    sleep(80);
                }
            }).start();
        }
    }

    private void processLinks(String links) {
        new Thread(() -> {
            String[] parts = links.split("[\n]");
            if (parts.length > 1) {
                Platform.runLater(() -> setLinkOut(Constants.GREEN, "Validating multiple links"));
            }
            for (String link : parts) {
                Platform.runLater(() -> tfLink.setText(link));
                findName(link);
                while (gettingFilenames.getValue().equals(true)) {
                    sleep(500);
                }
            }
        }).start();
    }

    private Job jobExists(String link) {
        if (jobList != null) {
            for (Job job : jobList) {
                if (job.getLink().equals(link)) {
                    return job;
                }
            }
        }
        return null;
    }

    private void createBatch(LinkedList<String> jsonMetadataList, String link) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filename;
        String directory = tfDir.getText();
        if (jsonMetadataList.size() > 1) {
            Platform.runLater(() -> setFileOut(Constants.GREEN, "Creating Multiple Jobs"));
        }
        for (String contents : jsonMetadataList) {
            JsonElement element = JsonParser.parseString(contents);
            String json = gson.toJson(element);
            filename = Utility.getFilenameFromJson(json);
            String urlLink = Utility.getURLFromJson(json);
            if (!urlLink.isEmpty()) {
                String ext = FilenameUtils.getExtension(filename);
                if (!Format.isValid(ext)) {
                    filename = Utility.cleanFilename(filename) + ".mp4";
                }
            }
            if (jobList != null) {
                jobList.add(new Job(urlLink, tfDir.getText(), filename));
                final String finalFilename = filename;
                Platform.runLater(() -> tfFilename.setText(finalFilename));
            }
        }
        commitJobListToListView();
        Platform.runLater(() -> {
            tfLink.clear();
            tfFilename.clear();
        });
    }

    private static String getClipboardText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) (clipboard.getData(DataFlavor.stringFlavor));
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveBatch() {
        String link = tfLink.getText();
        String dir = tfDir.getText();
        String filename = tfFilename.getText();
        boolean save = !link.isEmpty() && !dir.isEmpty() && !filename.isEmpty();
        Job removeJob = null;
        if (save) {
            for (Job job : jobList) {
                if (job.getLink().equals(link)) {
                    removeJob = job;
                    break;
                }
            }
            if (removeJob != null) {
                jobList.remove(removeJob);
            }
            Job job = new Job(link, dir, filename);
            jobList.add(job);
            commitJobListToListView();
        }
    }

    private void commitJobListToListView() {
        Platform.runLater(() -> {
            if (jobList != null) {
                if (jobList.isEmpty()) {
                    listView.getItems().clear();
                }
                else {
                    if (jobList.size() > 1) {
                        ArrayList<Job> sortList = new ArrayList<>(jobList);
                        sortList.sort(Comparator.comparing(Job::toString));
                        jobList = new ConcurrentLinkedDeque<>(sortList);
                    }
                    listView.getItems().setAll(jobList);
                }
            }
            if (AppSettings.get.jobs() != null) {
                AppSettings.get.jobs().setJobList(jobList);
            }
            else {
                new Jobs().setJobList(jobList);
            }
        });
    }

    private void runBatch() {
        Main.runBatch(jobList);
        close();
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void info() {
        double h = 20;
        double n = 16;
        TextFlow tf = new TextFlow();
        tf.getChildren().add(text("Link:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("The batch form lets you easily create batches for downloading. You start by pasting your web links into the Link field. Once a link has been put into Link field, Drifty will start to process it where it will attempt to determine the name of the file being downloaded. Once it has done that, you will see the filename show up in the list box on the left.\n\n", false, black, n));
        tf.getChildren().add(text("You can also add multiple links if they are separated with a single space. But you cannot have any spaces in the url itself or the link will not validate.\n\n", false, black, n));
        tf.getChildren().add(text("Checking the ", false, black, n));
        tf.getChildren().add(text("Auto Paste ", true, black, n));
        tf.getChildren().add(text("option will let you go to another window and put a link in the clipboard then when you come back to this window, the link will be pasted into the Link field automatically.\n\n", false, black, n));
        tf.getChildren().add(text("If you paste in a link that happens to extract multiple files for downloading, such as a Youtube play list, Drifty will attempt to get all of the filenames from each one in the list. There will be a timer present that indicates the thread is working. This can take a long time depending on how many files are in the playlist, so be patient. You will not see any files in the job list until it has gone through every file in the list.\n\nHowever, you can click on the arrow to pop out the console viewer and see the process oin action.\n\n", false, black, n));
        tf.getChildren().add(text("Directory:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Right clicking anywhere on the form brings up a menu where you can add directories to use as download folders. As you add more directories, they accumulate and persist between reloads. The last directory that you add will be considered the current download directory. Drifty will look through all of the added folders for a matching filename and it will let you know when it finds duplicates and give you the option to not download them again, once you start the batch job.\n\n", false, black, n));
        tf.getChildren().add(text("Right clicking on the form and choosing to edit the directory list pulls up a form with all of the directories you have added. Click on one to remove it if necessary.\n\n", false, black, n));
        tf.getChildren().add(text("Filename:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Drifty tries to get the name of the file from the link. This process can take a little time but not usually more than 5 to 10 seconds. If the file has no extension, then the extension of '.mp4' is added automatically. Also, If Drifty cannot determine the name of the file, you can type in whatever filename you'd like, then click on Save to commit that to the job in the list. You can also determine the download format of the file by setting the filename extension to one of these options: 3gp, aac, flv, m4a, mp3, mp4, ogg, wav, webm.\n\n", false, black, n));
        tf.getChildren().add(text("Job list:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Once the list box on the left has the jobs in it that you want, you can click on each one in turn and the link, download directory and filename will be placed into the related fields so you can edit them if you need to. Just click on Save when you're done editing.\n\n", false, black, n));
        tf.getChildren().add(text("You can right click on any item in the list to remove it or clear out the job list completely.\n\n", false, black, n));
        tf.getChildren().add(text("The Job list and the directory list will persist between program reloads. The job list will empty out once all files have successfully downloaded. You start the batch by clicking on the Run Batch button. Any files that fail to download will get recycled back into a batch list on this form. I found that they will download usually after a second attempt.\n\n", false, black, n));
        tf.getChildren().add(text("Auto Paste:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("The whole point of Auto Paste is to help speed things up. When you check the box, then go out to your browser and find links that you want to download, just copy them into your clip board then ALT+TAB back to Drifty or just click on it to make it the active window. Drifty will sense that it has been made the active screen and the contents of your clipboard will be analyzed to make sure it is a valid URL, then it will process it if so.\n\n", false, black, n));
        tf.getChildren().add(text("Multi-link pasting:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Another way to speed things up it to copy and paste links into a notepad of some kind, then just put a single space between each link so that all the links are on a single line, then paste that line into the Link field and Drifty will start processing them in turn and build up your batch for you.\n\nAnother thing you can do is grab a YouTube playlist and Drifty will process extract all of the videos from the playlist and build a batch from the list (or add to your existing batch).\n\n", false, black, n));
        tf.getChildren().add(text("Console (Arrow):\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Clicking on the arrow will extend the console which shows the output from the yt-dlp program. If you hover the mouse over the bottom right corner, a Copy button will appear. Clicking on it will copy all of the text from the console into your clipboard.\n", false, black, n));
        //tf.getChildren().add(text("\n\n",false,black,13));
        double width = 500;
        double height = 700;
        Button btnOK = new Button("OK");
        Stage stage = Constants.getStage();
        stage.setWidth(width);
        stage.setHeight(height + 100);
        stage.initStyle(StageStyle.TRANSPARENT);
        VBox vox = new VBox(tf);
        vox.setPrefWidth(width - 35);
        vox.setPrefHeight(height - 75);
        vox.setPadding(new Insets(15));
        ScrollPane scrollPane = new ScrollPane(vox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefWidth(width);
        scrollPane.setPrefHeight(height);
        VBox vBox = new VBox(20, scrollPane, btnOK);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        vBox.getStylesheets().add(Constants.upDown.toString());
        vBox.setPadding(new Insets(10));
        stage.setAlwaysOnTop(true);
        btnOK.setOnAction(e -> stage.close());
        stage.showAndWait();
    }

    private Text text(String string, boolean bold, Color color, double size) {
        Text text = new Text(string);
        text.setFont(new Font(monacoFont.toExternalForm(), size));
        text.setFill(color);
        if (bold) text.setStyle("-fx-font-weight: bold");
        text.setWrappingWidth(710);
        text.setLineSpacing(.5);
        return text;
    }
}
