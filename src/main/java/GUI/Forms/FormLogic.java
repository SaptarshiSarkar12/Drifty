package GUI.Forms;

import GUI.Support.Folders;
import GUI.Support.Job;
import GUI.Support.Jobs;
import Preferences.AppSettings;
import Utils.CheckFile;
import Utils.Utility;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static GUI.Forms.Constants.*;

public class FormLogic {

    private static final FormLogic INSTANCE = new FormLogic();
    private static MainGridPane form;
    private static final BooleanProperty directoryExists = new SimpleBooleanProperty(false);
    private static final BooleanProperty downloadInProgress = new SimpleBooleanProperty(false);
    private static final BooleanProperty processingBatch = new SimpleBooleanProperty(false);
    private static final BooleanProperty linkValid = new SimpleBooleanProperty(false);
    private static final BooleanProperty updatingBatch = new SimpleBooleanProperty(false);
    private static boolean clearingLink = false;
    private ConcurrentLinkedDeque<Job> jobList;
    private final String lineFeed = System.lineSeparator();
    private Folders folders;
    private Job selectedJob;

    private FormLogic() {
        folders = AppSettings.get.folders();
        jobList = AppSettings.get.jobs().jobList();
        commitJobListToListView();
    }

    public static void addJob(ConcurrentLinkedDeque<Job> list) {
        for (Job job : list) {
            boolean hasJob = INSTANCE.jobList.stream().anyMatch(jb -> jb.getFilename().equals(job.getFilename()));
            if (!hasJob) {
                INSTANCE.jobList.addLast(job);
            }
        }
        INSTANCE.commitJobListToListView();
    }

    public static void setColor(Color color) {
        form.lblDownloadInfo.setTextFill(color);
    }

    public static void bumpFolders() {
        INSTANCE.folders = AppSettings.get.folders();
    }

    public static void setDir(String path) {
        form.tfDir.setText(path);
    }

    public static void initLogic(MainGridPane pane) {
        INSTANCE.start(pane);
    }

    private void start(MainGridPane pane) {
        form = pane;
        setControlProperties();
        setControlActions();
    }

    private void setControlProperties() {
        downloadInProgress.addListener(((observable, oldValue, newValue) -> {
            System.out.println("downloadInProgress old: " + oldValue + " new: " + newValue);
            System.out.println("listView: " + form.listView.itemsProperty().isNotNull().get());
        }));
        form.tfDir.setText(folders.getDownloadFolder());
        directoryExists.setValue(new File(form.tfDir.getText()).exists());
        BooleanBinding disableStartButton = downloadInProgress.or(directoryExists.not());
        BooleanBinding hideStartButton = form.listView.itemsProperty().isNotNull().and(downloadInProgress.not());
        BooleanBinding disableInputs = downloadInProgress.not().or(updatingBatch.not());
        BooleanBinding hasText = form.tfLink.textProperty().isEmpty().not().and(form.tfDir.textProperty().isEmpty().not().and(form.tfFilename.textProperty().isEmpty().not()));
        form.ivBtnSave.visibleProperty().bind(updatingBatch);
        form.listView.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 1) {
                Job job = (Job) form.listView.getSelectionModel().getSelectedItem();
                if (job != null) {
                    updatingBatch.setValue(true);
                    selectedJob = job;
                    String error = job.getError();
                    form.tfLink.setText(job.getLink());
                    form.tfDir.setText(job.getDir());
                    form.tfFilename.setText(job.getFilename());
                    if (error != null) {
                        if (!error.isEmpty()) {
                            setFileOut(RED, error);
                        }
                    }
                }
            }
        });
        form.listView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                Job job = (Job) form.listView.getSelectionModel().getSelectedItem();
                if (job != null) {
                    jobList.remove(job);
                    commitJobListToListView();
                    clearControls();
                }
            }
        });
        form.listView.setContextMenu(getListMenu());
        form.ivBtnStart.visibleProperty().bind(hideStartButton);
        form.tfLink.editableProperty().bind(disableInputs);
        form.tfDir.editableProperty().bind(disableInputs);
        form.tfFilename.editableProperty().bind(disableInputs);
        Tooltip.install(form.cbAutoPaste, new Tooltip("When checked, will paste contents of clipboard into" + lineFeed + "Link field when switching back to this screen."));
        form.cbAutoPaste.setSelected(AppSettings.get.mainAutoPaste());
        form.cbAutoPaste.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.set.mainAutoPaste(newValue)));
        form.ivBtnSave.setOnMouseClicked(e -> new Thread(() -> {
            String link = form.tfLink.getText();
            String filename = form.tfFilename.getText();
            String dir = form.tfDir.getText();
            if (Paths.get(dir).toFile().exists() && filename.length() > 3) {
                jobList.remove(selectedJob);
                jobList.add(new Job(link, dir, filename));
                commitJobListToListView();
            }
            clearLink();
            form.tfFilename.clear();
            updatingBatch.setValue(false);
        }).start());
        form.tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                directoryExists.setValue(false);
                if (newValue.isEmpty()) {
                    setDirOutput(RED, "Directory cannot be empty!");
                }
                else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        delayFolderSave(newValue, folder);
                        setDirOutput(GREEN, "Directory exists!");
                        directoryExists.setValue(true);
                    }
                    else {
                        setDirOutput(RED, "Directory does not exist or is not a directory!");
                    }
                }
            }
        }));
        form.tfLink.textProperty().addListener(((observable, oldValue, newValue) -> verifyLink(oldValue, newValue)));
    }

    private void setControlActions() {
        form.ivBtnStart.setOnMouseClicked(e -> new Thread(() -> {
            if (!form.listView.getItems().isEmpty() && downloadInProgress.getValue().equals(false)) {
                System.out.println("confirmDownload");
                if (confirmDownload()) {
                    System.out.println("batchDownloader");
                    batchDownloader();
                }
            }

        }).start());
        form.tfDir.setOnAction(e -> updateBatch());
        form.tfFilename.setOnAction(e -> updateBatch());
    }

    private void updateBatch() {
        updatingBatch.setValue(false);
        if (selectedJob != null) {
            Job job = new Job(form.tfLink.getText(), form.tfDir.getText(), form.tfFilename.getText());
            jobList.remove(selectedJob);
            jobList.addLast(job);
            commitJobListToListView();
        }
        selectedJob = null;
    }

    private void batchDownloader() {
        processingBatch.setValue(true);
        updatingBatch.setValue(false);
        form.lblDownloadInfo.setTextFill(GREEN);
        new Thread(() -> {
            if (!jobList.isEmpty()) {
                checkFiles();
                final int totalNumberOfFiles = jobList.size();
                System.out.println("Number of files to download : " + totalNumberOfFiles);
                int fileCount = 0;
                for (Job job : jobList) {
                    fileCount++;
                    String processingFileText = "Processing file " + fileCount + " of " + totalNumberOfFiles + ": " + job;
                    System.out.println(processingFileText);
                    downloadInProgress.setValue(true);
                    Platform.runLater(() -> {
                        form.tfLink.setText(job.getLink());
                        form.tfDir.setText(job.getDir());
                        form.tfFilename.setText(job.getFilename());
                    });
                    Task<Integer> task = new DownloadFile(job.getLink(), job.getFilename(), job.getDir());
                    Thread thread = new Thread(task);
                    Platform.runLater(() -> {
                        form.lblDownloadInfo.textProperty().bind(((Worker<Integer>) task).messageProperty());
                        form.pBar.progressProperty().bind(((Worker<Integer>) task).progressProperty());
                    });
                    thread.start();
                    while (thread.getState().equals(Thread.State.RUNNABLE)) {
                        sleep(10);
                    }
                    downloadInProgress.setValue(false);
                    Platform.runLater(() -> {
                        if (((Worker<Integer>) task).valueProperty().get() == 0) {
                            jobList.remove(job);
                            commitJobListToListView();
                        }
                    });
                    sleep(3000);
                }
            }
            processingBatch.setValue(false);
        }).start();
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
            StringBuilder sb = new StringBuilder("The following files already exist:" + lineFeed + lineFeed);
            for (String path : pathJobMap.keySet()) {
                sb.append(path).append(lineFeed);
            }

            sb.append(lineFeed).append("Do you want to download them again?");
            AskYesNo ask = new AskYesNo(sb.toString());
            if (!ask.isYes()) {
                for (Job job : pathJobMap.values()) {
                    jobList.remove(job);
                }
            }
        }
    }

    private void setFilenameOutput(Color color, String message) {
        form.lblFilenameOut.setTextFill(color);
        form.lblFilenameOut.setText(message);
        if (color.equals(RED)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setFilenameOutput(GREEN, ""));
            }).start();
        }

    }

    private void setDownloadOutput(Color color, String message) {
        form.lblDownloadInfo.setTextFill(color);
        form.lblDownloadInfo.setText(message);
        if (color.equals(RED)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDownloadOutput(GREEN, ""));
            }).start();
        }

    }

    private boolean confirmDownload() {
        String filename = form.tfFilename.getText();
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
                String msg = "The file: " + lineFeed + lineFeed + filePath + lineFeed + lineFeed + " Already exists, Do you wish to download it again?";
                AskYesNo ask = new AskYesNo(msg);
                return ask.isYes();
            }
        }
        return true;
    }

    private void setDirOutput(Color color, String message) {
        form.lblDirOut.setTextFill(color);
        form.lblDirOut.setText(message);
        if (color.equals(RED)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDirOutput(GREEN, ""));
            }).start();
        }

    }

    private void delayFolderSave(String folderString, File folder) {
        // If the user is typing a file path into the field, we don't want to save every folder 'hit' so we wait 5 seconds
        // and if the String is still the same value, then we commit the folder to the list.
        new Thread(() -> {
            sleep(3000);
            if (form.tfDir.getText().equals(folderString)) {
                folders.addFolder(folder.getAbsolutePath());
                System.out.println("Folder Added: " + folder.getAbsolutePath());
            }

        }).start();
    }

    private void getFilenames(String link) {
        Task<ConcurrentLinkedDeque<Job>> task = new GetFilename(link, form.tfDir.getText());
        Worker<ConcurrentLinkedDeque<Job>> worker = task;
        Platform.runLater(() -> {
            form.lblDownloadInfo.textProperty().bind(worker.messageProperty());
            form.pBar.progressProperty().bind(worker.progressProperty());
        });
        new Thread(() -> {
            downloadInProgress.setValue(true);
            Thread getFilenameThread = new Thread(task);
            getFilenameThread.setDaemon(true);
            getFilenameThread.start();
            sleep(2000);
            form.lblDownloadInfo.setTextFill(GREEN);
            while (!getFilenameThread.getState().equals(Thread.State.TERMINATED) && !getFilenameThread.getState().equals(Thread.State.BLOCKED)) {
                Platform.runLater(() -> {
                    if (worker.valueProperty().get() != null) {
                        for (Job job : worker.valueProperty().get()) {
                            if (!jobList.contains(job)) {
                                jobList.add(job);
                                System.err.println("Added Job: " + job.getFilename());
                                commitJobListToListView();
                            }
                        }
                    }
                });
                sleep(50);
            }
            sleep(500);
            Platform.runLater(() -> {
                if (worker.getValue() != null) {
                    for (Job job : worker.getValue()) {
                        if (!jobList.contains(job)) {
                            jobList.add(job);
                        }
                        commitJobListToListView();
                    }
                }
                clearControls();
            });
            downloadInProgress.setValue(false);
        }).start();
    }

    private ContextMenu getListMenu() {
        MenuItem miDel = new MenuItem("Delete");
        MenuItem miClear = new MenuItem("Clear");
        MenuItem miInfo = new MenuItem("Information");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        miDel.setOnAction(e -> {
            Job job = (Job) form.listView.getSelectionModel().getSelectedItem();
            if (job != null) {
                jobList.remove(job);
                commitJobListToListView();
                clearControls();
            }
        });
        miClear.setOnAction(e -> {
            jobList.clear();
            commitJobListToListView();
            clearLink();
            form.tfFilename.clear();
            form.listView.getItems().clear();
            setLinkOut(GREEN, "");
            setDirOut(GREEN, "");
            setFileOut(GREEN, "");
        });
        miInfo.setOnAction(e -> info());
        return new ContextMenu(miDel, miClear, separator, miInfo);
    }

    private void downloadFiles() {

    }

    private void verifyLink(String PreviousLink, String presentLink) {
        if (!PreviousLink.equals(presentLink)) {
            if (clearingLink) {
                clearingLink = false;
                Platform.runLater(() -> form.lblLinkOut.setText(""));
                return;
            }
            if (downloadInProgress.getValue().equals(false) && processingBatch.getValue().equals(false) && updatingBatch.getValue().equals(false)) {
                setLinkOutput(GREEN, "Validating link ...");
                linkValid.setValue(false);
                if (presentLink.contains(" ")) {
                    Platform.runLater(() -> setLinkOutput(RED, "Link should not contain whitespace characters!"));
                }
                else if (!isURL(presentLink)) {
                    Platform.runLater(() -> setLinkOutput(RED, "String is not a URL"));
                }
                else {
                    try {
                        Utility.isURLValid(presentLink);
                        Platform.runLater(() -> setLinkOutput(GREEN, "Valid URL"));
                        linkValid.setValue(true);
                    } catch (Exception e) {
                        String errorMessage = e.getMessage();
                        Platform.runLater(() -> setLinkOutput(RED, errorMessage));
                    }
                }
                if (linkValid.getValue().equals(true)) {
                    getFilenames(presentLink);
                }
            }
        }
    }

    private boolean isURL(String text) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    private void setLinkOutput(Color color, String message) {
        form.lblLinkOut.setTextFill(color);
        form.lblLinkOut.setText(message);
        if (color.equals(RED)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setLinkOutput(GREEN, ""));
            }).start();
        }

    }

    private void clearLink() {
        clearingLink = true;
        Platform.runLater(() -> form.tfLink.clear());
    }

    private void clearControls() {
        Platform.runLater(() -> {
            clearLink();
            form.tfFilename.clear();
            form.lblDownloadInfo.textProperty().unbind();
            form.lblDownloadInfo.setText("");
            form.lblDirOut.setText("");
            form.lblFilenameOut.setText("");
            form.lblLinkOut.setText("");
        });
    }

    protected static void setLinkOut(Color color, String message) {
        Platform.runLater(() -> {
            form.lblLinkOut.setTextFill(color);
            form.lblLinkOut.setText(message);
        });
    }

    private void setDirOut(Color color, String message) {
        Platform.runLater(() -> {
            form.lblDirOut.setTextFill(color);
            form.lblDirOut.setText(message);
        });
    }

    private void setFileOut(Color color, String message) {
        Platform.runLater(() -> {
            form.lblFilenameOut.setTextFill(color);
            form.lblFilenameOut.setText(message);
        });
    }

    private void commitJobListToListView() {
        Platform.runLater(() -> {
            if (jobList != null) {
                if (jobList.isEmpty()) {
                    form.listView.getItems().clear();
                }
                else {
                    if (jobList.size() > 1) {
                        ArrayList<Job> sortList = new ArrayList<>(jobList);
                        sortList.sort(Comparator.comparing(Job::toString));
                        jobList = new ConcurrentLinkedDeque<>(sortList);
                    }
                    form.listView.getItems().setAll(jobList);
                }
            }
            new Jobs().setJobList(jobList); //this uses the Jobs class to set the Property.
        });
    }

    private Text text(String string, boolean bold, Color color, double size) {
        Text text = new Text(string);
        text.setFont(new Font(MONACO_TTF.toExternalForm(), size));
        text.setFill(color);
        if (bold) text.setStyle("-fx-font-weight: bold");
        text.setWrappingWidth(710);
        text.setLineSpacing(.5);
        return text;
    }

    private void info() {
        double h = 20;
        double n = 16;
        TextFlow tf = new TextFlow();
        tf.getChildren().add(text("Link:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("The batch form lets you easily create batches for downloading. You start by pasting your web links into the Link field. Once a link has been put into Link field, Drifty will start to process it where it will attempt to determine the name of the file being downloaded. Once it has done that, you will see the filename show up in the list box on the left.\n\n", false, BLACK, n));
        tf.getChildren().add(text("You can also add multiple links if they are separated with a single space. But you cannot have any spaces in the url itself or the link will not validate.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Checking the ", false, BLACK, n));
        tf.getChildren().add(text("Auto Paste ", true, BLACK, n));
        tf.getChildren().add(text("option will let you go to another window and put a link in the clipboard then when you come back to this window, the link will be pasted into the Link field automatically.\n\n", false, BLACK, n));
        tf.getChildren().add(text("If you paste in a link that happens to extract multiple files for downloading, such as a Youtube play list, Drifty will attempt to get all of the filenames from each one in the list. There will be a timer present that indicates the thread is working. This can take a long time depending on how many files are in the playlist, so be patient. You will not see any files in the job list until it has gone through every file in the list.\n\nHowever, you can click on the arrow to pop out the console viewer and see the process oin action.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Directory:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Right clicking anywhere on the form brings up a menu where you can add directories to use as download folders. As you add more directories, they accumulate and persist between reloads. The last directory that you add will be considered the current download directory. Drifty will look through all of the added folders for a matching filename and it will let you know when it finds duplicates and give you the option to not download them again, once you start the batch job.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Right clicking on the form and choosing to edit the directory list pulls up a form with all of the directories you have added. Click on one to remove it if necessary.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Filename:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Drifty tries to get the name of the file from the link. This process can take a little time but not usually more than 5 to 10 seconds. If the file has no extension, then the extension of '.mp4' is added automatically. Also, If Drifty cannot determine the name of the file, you can type in whatever filename you'd like, then click on Save to commit that to the job in the list. You can also determine the download format of the file by setting the filename extension to one of these options: 3gp, aac, flv, m4a, mp3, mp4, ogg, wav, webm.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Job list:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Once the list box on the left has the jobs in it that you want, you can click on each one in turn and the link, download directory and filename will be placed into the related fields so you can edit them if you need to. Just click on Save when you're done editing.\n\n", false, BLACK, n));
        tf.getChildren().add(text("You can right click on any item in the list to remove it or clear out the job list completely.\n\n", false, BLACK, n));
        tf.getChildren().add(text("The Job list and the directory list will persist between program reloads. The job list will empty out once all files have successfully downloaded. You start the batch by clicking on the Run Batch button. Any files that fail to download will get recycled back into a batch list on this form. I found that they will download usually after a second attempt.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Auto Paste:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("The whole point of Auto Paste is to help speed things up. When you check the box, then go out to your browser and find links that you want to download, just copy them into your clip board then ALT+TAB back to Drifty or just click on it to make it the active window. Drifty will sense that it has been made the active screen and the contents of your clipboard will be analyzed to make sure it is a valid URL, then it will process it if so.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Multi-link pasting:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Another way to speed things up it to copy and paste links into a notepad of some kind, then just put a single space between each link so that all the links are on a single line, then paste that line into the Link field and Drifty will start processing them in turn and build up your batch for you.\n\nAnother thing you can do is grab a YouTube playlist and Drifty will process extract all of the videos from the playlist and build a batch from the list (or add to your existing batch).\n\n", false, BLACK, n));
        tf.getChildren().add(text("Console (Arrow):\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Clicking on the arrow will extend the console which shows the output from the yt-dlp program. If you hover the mouse over the bottom right corner, a Copy button will appear. Clicking on it will copy all of the text from the console into your clipboard.\n", false, BLACK, n));
        //tf.getChildren().add(text("\n\n",false,BLACK,13));
        double width = 500;
        double height = 700;
        Button btnOK = new Button("OK");
        Stage stage = getStage();
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
        Scene scene = Constants.getScene(vBox);
        stage.setScene(scene);
        vBox.getStylesheets().add(Objects.requireNonNull(UP_DOWN_PNG).toString());
        vBox.setPadding(new Insets(10));
        stage.setAlwaysOnTop(true);
        btnOK.setOnAction(e -> stage.close());
        stage.showAndWait();
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}


