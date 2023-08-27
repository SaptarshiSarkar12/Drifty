package GUI.Forms;

import Enums.MessageCategory;
import Enums.MessageType;
import GUI.Support.Folders;
import GUI.Support.Job;
import GUI.Support.JobHistory;
import GUI.Support.Jobs;
import Preferences.AppSettings;
import Utils.CheckFile;
import Utils.Environment;
import Utils.MessageBroker;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static GUI.Forms.Constants.*;

public class FormLogic {
    public static final FormLogic INSTANCE = new FormLogic();
    private static MainGridPane form;
    private static final MessageBroker messageBroker = Environment.getMessageBroker();
    private static final BooleanProperty directoryExists = new SimpleBooleanProperty(false);
    private static final BooleanProperty downloadInProgress = new SimpleBooleanProperty(false);
    private static final BooleanProperty processingBatch = new SimpleBooleanProperty(false);
    private static final BooleanProperty linkValid = new SimpleBooleanProperty(false);
    private static final BooleanProperty updatingBatch = new SimpleBooleanProperty(false);
    private static boolean clearingLink = false;
    private final String nl = System.lineSeparator();
    private ConcurrentLinkedDeque<Job> jobList;
    private Folders folders;
    private Job selectedJob;
    private final JobHistory jobHistory;

    private FormLogic() {
        folders = AppSettings.get.folders();
        jobList = AppSettings.get.jobs().jobList();
        jobHistory = AppSettings.get.jobHistory();
        commitJobListToListView();
    }

    private void start(MainGridPane pane) {
        form = pane;
        setControlProperties();
        setControlActions();
        form.tfLink.requestFocus();
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

    public static void initLogic(MainGridPane pane) {
        INSTANCE.start(pane);
    }

    public static boolean isAutoPaste() {
        return form.cbAutoPaste.isSelected();
    }

    private void verifyLink(String PreviousLink, String presentLink) {
        new Thread(() -> {
            if (!PreviousLink.equals(presentLink)) {
                if (clearingLink) {
                    clearingLink = false;
                    messageBroker.sendMessage("", MessageType.INFO, MessageCategory.LINK);
                    return;
                }
                if (downloadInProgress.getValue().equals(false) && processingBatch.getValue().equals(false) && updatingBatch.getValue().equals(false)) {
                    messageBroker.sendMessage("Validating link...", MessageType.INFO, MessageCategory.LINK);
                    checkFiles();
                    linkValid.setValue(false);
                    if (presentLink.contains(" ")) {
                        messageBroker.sendMessage("Link should not contain whitespace characters!", MessageType.ERROR, MessageCategory.LINK);
                    }
                    else if (!Utility.isURL(presentLink)) {
                        messageBroker.sendMessage("Link is not a valid URL!", MessageType.ERROR, MessageCategory.LINK);
                    }
                    else {
                        if (linkExistsButUserProceeds(presentLink)) {
                            boolean dupLink = false;
                            String dupFilename = "";
                            for (Object jobObject : form.listView.getItems()) {
                                Job job = (Job) jobObject;
                                if (job.getLink().equals(presentLink)) {
                                    dupLink = true;
                                    dupFilename = job.getFilename();
                                    break;
                                }
                            }
                            if (dupLink) {
                                messageBroker.sendMessage("Already in batch: \"" + dupFilename + "\"", MessageType.WARN, MessageCategory.LINK);
                            }
                            else {
                                boolean isUrlValid = Utility.isURLValid(presentLink);
                                if (isUrlValid) {
                                    linkValid.setValue(true);
                                }
                            }
                        }
                        else {
                            boolean isUrlValid = Utility.isURLValid(presentLink);
                            if (isUrlValid) {
                                linkValid.setValue(true);
                            }
                        }
                    }
                    if (linkValid.getValue().equals(true)) {
                        getFilenames(presentLink);
                    }
                }
            }
        }).start();
    }

    private void setControlProperties() {
        setDir(folders.getDownloadFolder());
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
                    setLink(job.getLink());
                    setDir(job.getDir());
                    setFilename(job.getFilename());
                    if (error != null) {
                        if (!error.isEmpty()) {
                            messageBroker.sendMessage(error, MessageType.ERROR, MessageCategory.FILENAME);
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
        form.tfDir.editableProperty().bind(disableInputs);
        form.tfFilename.editableProperty().bind(disableInputs);
        Tooltip.install(form.cbAutoPaste, new Tooltip("When checked, will paste contents of clipboard into" + nl + "Link field when switching back to this screen."));
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
                    messageBroker.sendMessage("Directory cannot be empty!", MessageType.ERROR, MessageCategory.DIRECTORY);
                }
                else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        delayFolderSave(newValue, folder);
                        messageBroker.sendMessage("Directory exists!", MessageType.INFO, MessageCategory.DIRECTORY);
                        directoryExists.setValue(true);
                    }
                    else {
                        messageBroker.sendMessage("Directory does not exist or is not a directory!", MessageType.ERROR, MessageCategory.DIRECTORY);
                    }
                }
            }
        }));
        form.tfLink.editableProperty().bind(disableInputs);
        form.tfLink.textProperty().addListener(((observable, oldValue, newValue) -> verifyLink(oldValue, newValue)));
    }

    private void setControlActions() {
        form.ivBtnStart.setOnMouseClicked(e -> new Thread(() -> {
            if (!form.listView.getItems().isEmpty() && downloadInProgress.getValue().equals(false)) {
                clearLink();
                clearFilename();
                batchDownloader();
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
            if (jobList != null && !jobList.isEmpty()) {
                final int totalNumberOfFiles = jobList.size();
                int fileCount = 0;
                for (Job job : jobList) {
                    fileCount++;
                    String processingFileText = "Processing file " + fileCount + " of " + totalNumberOfFiles + ": " + job;
                    if (fileExists(job.getFilename())) {
                        Platform.runLater(() -> messageBroker.sendMessage(job.getFilename() + " Exists already!", MessageType.WARN, MessageCategory.FILENAME));
                        jobList.remove(job);
                        jobHistory.addJob(job);
                        commitJobListToListView();
                        sleep(2500);
                        for (double x = 1; x >= 0; x -= .05) {
                            final double opacity = x;
                            Platform.runLater(() -> form.lblDownloadInfo.setOpacity(opacity));
                            sleep(75);
                        }
                        Platform.runLater(() -> {
                            form.lblDownloadInfo.setText("");
                            form.lblDownloadInfo.setOpacity(1);
                        });
                        continue;
                    }
                    downloadInProgress.setValue(true);
                    setLink(job.getLink());
                    setDir(job.getDir());
                    setFilename(job.getFilename());
                    Task<Integer> task = new DownloadFile(job.getLink(), job.getFilename(), job.getDir());
                    Thread thread = new Thread(task);
                    Platform.runLater(() -> {
                        form.lblDownloadInfo.textProperty().bind(((Worker<Integer>) task).messageProperty());
                        form.pBar.progressProperty().bind(((Worker<Integer>) task).progressProperty());
                    });
                    thread.start();
                    while (thread.getState().equals(Thread.State.RUNNABLE)) {
                        sleep(1000);
                    }
                    downloadInProgress.setValue(false);
                    Platform.runLater(() -> {
                        if (((Worker<Integer>) task).valueProperty().get() == 0) {
                            jobList.remove(job);
                            jobHistory.addJob(job);
                            commitJobListToListView();
                        }
                    });
                    sleep(3000);
                }
            }
            processingBatch.setValue(false);
            clearLink();
            clearFilename();
        }).start();
    }

    private boolean fileExists(String filename) {
        for (String folder : AppSettings.get.folders().getFolders()) {
            Path downloadFolder = Paths.get(folder);
            CheckFile checkFile = new CheckFile(downloadFolder, filename);
            Thread thread = new Thread(checkFile);
            thread.start();
            while (thread.getState().equals(Thread.State.RUNNABLE)) {
                sleep(250);
            }
            return checkFile.fileFound();
        }
        return false;
    }

    private boolean linkExistsButUserProceeds(String link) {
        List<Job> deleteList = new ArrayList<>();
        for (Job job : jobHistory.getHistory()) {
            if (job.getLink().equals(link)) {
                AskYesNo ask = new AskYesNo("You have processed this link once before, do you wish to continue?");
                if (ask.getResponse().isYes()) {
                    ask = new AskYesNo("You must change the name of the file if you wish to download this file\n" +
                            "again because Drifty will not overwrite an existing file.", true);
                    ask.showOK();
                    form.tfFilename.setText(job.getFilename());
                    jobList.addLast(job);
                    commitJobListToListView();
                    updatingBatch.setValue(true);
                    selectedJob = job;
                    Platform.runLater(() -> form.tfFilename.requestFocus());
                    return false;
                }
                else {
                    clearLink();
                    return false;
                }
            }
        }
        return false;
    }

    private void checkFiles() {
        List<Job> deleteList = new ArrayList<>();
        for (Job job : jobHistory.getHistory()) {
            for (Job listJob : jobList) {
                if (job.getLink().equals(listJob.getLink())) {
                    for (String folder : folders.getFolders()) {
                        CheckFile walker = new CheckFile(folder, job.getFilename());
                        Thread thread = new Thread(walker);
                        thread.start();
                        while (thread.getState().equals(Thread.State.RUNNABLE)) {
                            sleep(100);
                        }
                        if (walker.fileFound()) {
                            AskYesNo ask = new AskYesNo("You have already downloaded the file:" + nl.repeat(2) + job.getFilename() + nl.repeat(2) +
                                    "And it exists in this download folder:" + folder + nl.repeat(2) + "Do you want to download it again?");
                            if (ask.getResponse().isNo()) {
                                deleteList.add(job);
                            }
                        }
                    }
                }
            }
        }
        for (Job job : deleteList) {
            jobList.remove(job);
            commitJobListToListView();
        }
    }

    private void delayFolderSave(String folderString, File folder) {
        // If the user is typing a file path into the field, we don't want to save every folder 'hit' so we wait 5 seconds
        // and if the String is still the same value, then we commit the folder to the list.
        new Thread(() -> {
            sleep(3000);
            if (form.tfDir.getText().equals(folderString)) {
                folders.addFolder(folder.getAbsolutePath());
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
                                if (jobHistory.jobMatch(job)) {
                                    AskYesNo ask = new AskYesNo("You have downloaded " + job.getFilename() + " in the past. Do you still want to add it to the job batch?");
                                    if (ask.getResponse().isYes())
                                        jobList.add(job);
                                    else
                                        continue;
                                }
                                if (jobHistory.jobFileExists(job)) {
                                    AskYesNo ask = new AskYesNo("This file: " + job.getFilename() + " already exists in one of your download folders. Do you want to download it again?");
                                    if (ask.getResponse().isNo())
                                        continue;
                                }
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
                if (worker.valueProperty().get() != null) {
                    for (Job job : worker.valueProperty().get()) {
                        if (!jobList.contains(job)) {
                            if (jobHistory.jobMatch(job)) {
                                AskYesNo ask = new AskYesNo("You have downloaded " + job.getFilename() + " in the past. Do you still want to add it to the job batch?");
                                if (ask.getResponse().isYes()) {
                                    jobList.add(job);
                                } else
                                    continue;
                            }
                            if (jobHistory.jobFileExists(job)) {
                                AskYesNo ask = new AskYesNo("This file: " + job.getFilename() + " already exists in one of your download folders. Do you want to download it again?");
                                if (ask.getResponse().isNo()) {
                                    continue;
                                }
                            }
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
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.LINK);
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.FILENAME);
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.DIRECTORY);
        });
        miInfo.setOnAction(e -> help());
        return new ContextMenu(miDel, miClear, separator, miInfo);
    }

    /*
    These methods are used to set or clear the TextFields themselves;
     */
    public static void setLink(String link) {
        Platform.runLater(() -> form.tfLink.setText(link));
    }

    private void clearLink() {
        clearingLink = true;
        Platform.runLater(() -> {
            form.tfLink.clear();
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.LINK);
        });
    }

    public static void setDir(String path) {
        Platform.runLater(() -> form.tfDir.setText(path));
    }

    public static void setFilename(String filename) {
        Platform.runLater(() -> form.tfFilename.setText(filename));
    }

    private void clearFilename() {
        Platform.runLater(() -> {
            setFilename("");
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.DOWNLOAD);
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.FILENAME);
        });
    }

    private void clearControls() {
        clearLink();
        clearFilename();
        messageBroker.sendMessage("", MessageType.INFO, MessageCategory.LINK);
        messageBroker.sendMessage("", MessageType.INFO, MessageCategory.DIRECTORY);
    }

    /*
    These methods control the labels under the TextFields (arranged in the order they appear on the form)
     */

    public void setLinkOutput(Color color, String message) {
        Platform.runLater(() -> {
            form.lblLinkOut.setTextFill(color);
            form.lblLinkOut.setText(message);
        });
        if (color.equals(RED)) {
            new Thread(() -> {
                sleep(4500);
                setLinkOutput(GREEN, "");
            }).start();
        }
    }

    public void setDirOutput(Color color, String message) {
        form.lblDirOut.setTextFill(color);
        form.lblDirOut.setText(message);
        if (color.equals(RED)) {
            new Thread(() -> {
                sleep(3000);
                Platform.runLater(() -> setDirOutput(GREEN, ""));
            }).start();
        }

    }

    public void setFilenameOutput(Color color, String message) {
        Platform.runLater(() -> {
            form.lblFilenameOut.setTextFill(color);
            form.lblFilenameOut.setText(message);
            if (color.equals(RED)) {
                new Thread(() -> {
                    sleep(3000);
                    Platform.runLater(() -> setFilenameOutput(GREEN, ""));
                }).start();
            }
        });
    }

    public void setDownloadOutput(Color color, String message) {
        Platform.runLater(() -> {
            form.lblDownloadInfo.textProperty().unbind();
            form.lblDownloadInfo.setTextFill(color);
            form.lblDownloadInfo.setText(message);
            if (color.equals(RED)) {
                new Thread(() -> {
                    sleep(3000);
                    Platform.runLater(() -> setDownloadOutput(GREEN, ""));
                }).start();
            }
        });

    }

    /*
    These methods are for general form flow
     */
    private void commitJobListToListView() {
        Platform.runLater(() -> {
            if (jobList != null) {
                if (jobList.isEmpty()) {
                    form.listView.getItems().clear();
                }
                else {
                    if (jobList.size() > 1) {
                        //Remove duplicate jobs if any
                        Set<String> encounteredLinks = new HashSet<>();
                        ConcurrentLinkedDeque<Job> duplicates = jobList.stream()
                                .filter(job -> !encounteredLinks.add(job.getLink()))
                                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
                        for (Job job : duplicates) {
                            jobList.remove(job);
                        }
                        //Sort the Job list
                        ArrayList<Job> sortList = new ArrayList<>(jobList);
                        sortList.sort(Comparator.comparing(Job::toString));
                        jobList = new ConcurrentLinkedDeque<>(sortList);
                    }
                    //Assign the jobList to the ListView
                    form.listView.getItems().setAll(jobList);
                }
            }
            new Jobs().setJobList(jobList); //this uses the Jobs class to set the Property.
        });
    }

    private void help() {
        double h = 20;
        double n = 16;
        TextFlow tf = new TextFlow();
        tf.getChildren().add(text("Link:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("The Drifty GUI lets you easily create batches for downloading, or download a single file. You start by pasting your web links into the Link field. Once a link has been put into Link field, Drifty will start to process it where it will attempt to determine the name of the file being downloaded. Once it has done that, you will see the filename show up in the batch list on the left.\n\n", false, BLACK, n));
        tf.getChildren().add(text("The URLs you paste into the link field must be valid URLs or Drifty wont process them.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Checking the ", false, BLACK, n));
        tf.getChildren().add(text("Auto Paste ", true, BLACK, n));
        tf.getChildren().add(text("option will let you go to another window and put a link in the clipboard then when you come back to Drifty, the link will be pasted into the Link field automatically and processed then added to the batch list.\n\n", false, BLACK, n));
        tf.getChildren().add(text("If you paste in a link that happens to extract multiple files for downloading, such as a Youtube play list, Drifty will first attempt to get the number of files in the list, then it will ask you if you would like it to obtain all of the filenames in the list. The progress bar will indicate how many filenames have been obtained and the batch list will populate with download jobs with each new filename discovered.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Directory:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Right clicking anywhere on the form brings up a menu where you can add directories to use as download folders. As you add more directories, they accumulate and persist between reloads. The last directory that you add last will be considered the current download directory. When you click start which begins the download process for all jobs in the batch list, Drifty will look through all of the added folders for a matching filenames and it will let you know when it finds duplicates and give you the option to not download them again.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Right clicking on the form and choosing to edit the directory list pulls up a form with all of the directories you have added. Click on one to remove it if necessary.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Filename:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Drifty tries to get the name of the file from the link. This process can take a little time but not usually more than 10 to 20 seconds. By default, Drifty adds the extension of 'mp4' to video file downloads because these have the highest chance of success. If Drifty cannot determine the name of the file, you can type in whatever filename you'd like, then click on Save to commit that to the job in the list. You can also determine the download format of the file by setting the filename extension to one of these options: 3gp, aac, flv, m4a, mp3, mp4, ogg, wav, webm.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Job list:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Once the list box on the left has the jobs in it that you want, you can click on each one in turn and the link, download directory and filename will be placed into the related fields so you can edit them if you need to. Just click on Save when you're done editing.\n\n", false, BLACK, n));
        tf.getChildren().add(text("You can right click on any item in the list to remove it or clear out the job list completely.\n\n", false, BLACK, n));
        tf.getChildren().add(text("The Job list and the directory list will persist between program reloads. The job list will empty out one at a time as each file in the list is downloaded. You start the batch by clicking on the Start button. Any files that fail to download will get recycled back into the list on this form. I found that they will download usually after a second attempt.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Auto Paste:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("The whole point of Auto Paste is to help speed things up. When you check the box, then go out to your browser and find links that you want to download, just copy them into your clip board then ALT+TAB back to Drifty or just click on it to make it the active window. Drifty will sense that it has been made the active screen and the contents of your clipboard will be analyzed to make sure it is a valid URL, then it will process it if so.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Multi-link pasting:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Another way to speed things up it to copy and paste links into a notepad of some kind, then just put a single space between each link so that all the links are on a single line, then paste that line into the Link field and Drifty will start processing them in turn and build up your batch for you.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Youtube Playlists:\n", true, Color.BLUE, h));
        tf.getChildren().add(text("Another thing you can do is grab a YouTube playlist and Drifty will extract all of the videos from the playlist and build a batch from the list (or add to your existing batch).\n\n", false, BLACK, n));        //tf.getChildren().add(text("\n\n",false,BLACK,13));
        tf.setStyle("-fx-background-color: transparent");

        double width = 500;
        double height = 700;
        Button btnOK = new Button("OK");
        Stage stage = getStage();
        stage.setWidth(width);
        stage.setHeight(height + 100);
        //stage.initStyle(StageStyle.TRANSPARENT);
        VBox vox = new VBox(20, tf);
        vox.setPrefWidth(width - 35);
        vox.setPrefHeight(height - 75);
        vox.setPadding(new Insets(30));
        vox.setAlignment(Pos.CENTER);
        vox.setStyle("-fx-background-color: transparent");
        ScrollPane scrollPane = new ScrollPane(vox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefWidth(width);
        scrollPane.setPrefHeight(height);
        scrollPane.setFitToWidth(true);
        Scene scene = Constants.getScene(scrollPane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Help");
        stage.setOnCloseRequest(e -> stage.close());
        VBox.setVgrow(vox, Priority.ALWAYS);
        VBox.setVgrow(tf, Priority.ALWAYS);
        btnOK.setOnAction(e -> stage.close());
        scrollPane.setVvalue(0.0);
        stage.showAndWait();
    }

    private Text text(String string, boolean bold, Color color, double size) {
        // This is used by the help() method for custom text formatting
        Text text = new Text(string);
        text.setFont(new Font(MONACO_TTF.toExternalForm(), size));
        text.setFill(color);
        if (bold) text.setStyle("-fx-font-weight: bold;");
        text.setWrappingWidth(710);
        text.setLineSpacing(.5);
        return text;
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


