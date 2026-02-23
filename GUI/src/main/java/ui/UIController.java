package ui;

import backend.FileDownloader;
import data.JobService;
import gui.init.Environment;
import settings.AppSettings;
import gui.support.Constants;
import gui.support.Folders;
import gui.support.GUIDownloadConfiguration;
import gui.updater.GUIUpdateExecutor;
import gui.utils.CheckFile;
import gui.utils.MessageBroker;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.Drifty_GUI;
import properties.OS;
import support.Job;
import support.JobHistory;
import support.Jobs;
import utils.DbConnection;
import utils.Utility;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.*;

import static gui.support.Colors.*;
import static init.Environment.currentSessionId;
import static utils.Utility.renameFile;
import static utils.Utility.sleep;

public final class UIController {
    public static final UIController INSTANCE = new UIController();
    public static MainGridPane form;
    private Stage helpStage;
    private GUIDownloadConfiguration downloadConfig;
    private static final MessageBroker M = Environment.getMessageBroker();
    private static final BooleanProperty DIRECTORY_EXISTS = new SimpleBooleanProperty(false);
    private static final BooleanProperty PROCESSING_BATCH = new SimpleBooleanProperty(false);
    private static final BooleanProperty UPDATING_BATCH = new SimpleBooleanProperty(false);
    private static final BooleanProperty VERIFYING_LINKS = new SimpleBooleanProperty(false);
    private final String nl = System.lineSeparator();
    private int speedValueUpdateCount;
    private int speedValue;
    private static final TextFlow INFO_TF = new TextFlow();
    private static Scene infoScene;
    private String filename;
    private Folders folders;
    private Job selectedJob;
    private Jobs jobs;

    public static Scene getInfoScene() {
        return infoScene;
    }

    public static TextFlow getInfoTf() {
        return INFO_TF;
    }

    /*
    Single instance model-only constructor
    */
    private UIController() {
        folders = new Folders();
        jobs = JobService.getJobs();
    }

    /*
    Methods for initializing the various controls that are on the form - MainGridPane
     */
    private void start(MainGridPane pane) {
        new Thread(() -> {
            if (AppSettings.isDriftyUpdateAvailable()) {
                M.msgLogInfo("A new version of Drifty is available!");
                showUpdateDialog();
            }
        }).start();
        form = pane;
        setControlProperties();
        setControlActions();
        form.tfLink.requestFocus();
        commitJobListToListView();
    }

    public void showUpdateDialog() {
        if (OS.isMac() || Environment.isAdministrator()) { // If the user is running as an administrator, they can update the application. Otherwise, they will be prompted to run as an administrator. For Mac, the user will always be prompted to update the application as a `.pkg` file will just be opened to install the update (the user has to manually install the update).
            ConfirmationDialog ask = new ConfirmationDialog("Update Available", "A new version of Drifty is available!" + nl.repeat(2) + AppSettings.getNewDriftyVersionName() + nl.repeat(2) + "Do you want to download and install the update?", false, false);
            if (ask.getResponse().isYes()) {
                downloadUpdate();
            }
        } else {
            ConfirmationDialog ask = new ConfirmationDialog("Update Available", "A new version of Drifty is available!" + nl.repeat(2) + AppSettings.getNewDriftyVersionName() + nl.repeat(2) + "Unfortunately, you do not have the necessary permissions to update the application." + nl.repeat(2) + "Please run Drifty as an administrator to update the application.", true, false);
            ask.getResponse();
        }
    }

    private void downloadUpdate() {
        String previouslySelectedDir = getDir(); // Save the download folder selected before the update was initiated.
        try {
            getJobs();
            // "Current executable" means the executable currently running i.e., the one that is outdated.
            File currentExecutableFile = new File(Drifty_GUI.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // "Latest executable" means the executable that is to be downloaded and installed i.e., the latest version.
            // "tmpFolder" is the temporary folder where the latest executable will be downloaded to.
            File tmpFolder = Files.createTempDirectory("Drifty").toFile();
            tmpFolder.deleteOnExit();
            // For Mac, the latest executable is a ".pkg" file as it is the only working way to update the application, i.e., by installing a new version.
            // For other OS, the latest executable name along with the extension is the same as that of the current executable.
            String latestExecutableName = OS.isMac() ? "Drifty_GUI.pkg" : currentExecutableFile.getName();
            File latestExecutableFile = Paths.get(tmpFolder.getPath()).resolve(latestExecutableName).toFile();
            // Get the download queue already present in the application before adding the latest executable to it. This is done to ensure that the latest executable is downloaded first and alone.
            ConcurrentLinkedDeque<Job> currentDownloadQueue = jobs.jobList();
            // Clear the download queue to download only the latest executable to prevent any other downloads from interfering with the update process.
            jobs.clear();

            // Download the latest executable
            Job updateJob = new Job(Constants.updateURL.toString(), latestExecutableFile.getParent(), latestExecutableFile.getName(), Constants.updateURL.toString());
            addJob(updateJob);
            Thread downloadUpdate = new Thread(batchDownloader());
            downloadUpdate.start();
            while (!downloadUpdate.getState().equals(Thread.State.TERMINATED)) {
                sleep(500);
            }
            setDir(previouslySelectedDir); // Reset the download folder to the one that was selected before the update was initiated.
            AppSettings.setLastDownloadFolder(previouslySelectedDir); // Reset the download folder to the one that was selected before the update was initiated.
            // Reset the download queue to the previous state.
            jobs.setList(currentDownloadQueue);
            if (latestExecutableFile.exists() && latestExecutableFile.isFile() && latestExecutableFile.length() > 0) {
                // If the latest executable was successfully downloaded, set the executable permission and execute the update.
                GUIUpdateExecutor updateExecutor = new GUIUpdateExecutor(currentExecutableFile, latestExecutableFile);
                updateExecutor.execute();
            } else {
                M.msgUpdateError("Failed to download update!");
            }
        } catch (IOException e) {
            M.msgUpdateError("Failed to create temporary folder for downloading update! " + e.getMessage());
        } catch (URISyntaxException e) {
            M.msgUpdateError("Failed to get the location of the current executable! " + e.getMessage());
        } catch (Exception e) {
            M.msgUpdateError("Failed to update! An unknown error occurred! " + e.getMessage());
        }
    }

    private void setControlProperties() {
        setDir(folders.getDownloadFolder());
        DIRECTORY_EXISTS.setValue(new File(getDir()).exists());

        BooleanBinding disableStartButton = form.listView.itemsProperty().isNotNull().not().or(PROCESSING_BATCH).or(DIRECTORY_EXISTS.not()).or(VERIFYING_LINKS);
        BooleanBinding disableInputs = PROCESSING_BATCH.or(VERIFYING_LINKS);
        BooleanBinding disableLinkInput = UPDATING_BATCH.or(PROCESSING_BATCH).or(VERIFYING_LINKS);
        form.btnSave.visibleProperty().bind(UPDATING_BATCH);
        form.btnStart.disableProperty().bind(disableStartButton);
        form.tfDir.disableProperty().bind(disableInputs);
        form.tfFilename.disableProperty().bind(disableInputs);
        form.tfLink.disableProperty().bind(disableLinkInput);
        form.listView.setContextMenu(getListMenu());

        if ("Dark".equals(AppSettings.getGuiTheme())) {
            form.tfDir.setStyle("-fx-text-fill: White;");
            form.tfFilename.setStyle("-fx-text-fill: White;");
            form.tfLink.setStyle("-fx-text-fill: White;");
        }

        Tooltip.install(form.cbAutoPaste, new Tooltip("When checked, will paste contents of clipboard into" + nl + "Link field when switching back to this screen."));
        Tooltip.install(form.tfLink, new Tooltip("URL must be a valid URL without spaces." + nl + " Add multiple URLs by pasting them in from the clipboard and separating each URL with a space."));
        Tooltip.install(form.tfFilename, new Tooltip("If the filename you enter already exists in the download folder, it will" + nl + "automatically be renamed to avoid file over-writes."));
        Tooltip.install(form.tfDir, new Tooltip("Right click anywhere to add a new download folder." + nl + "Drifty will accumulate a list of download folders" + nl + "so that duplicate downloads can be detected."));
        form.cbAutoPaste.setSelected(AppSettings.isGuiAutoPasteEnabled());
        form.tfDir.textProperty().addListener(((_, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                DIRECTORY_EXISTS.setValue(false);
                if (newValue.isEmpty()) {
                    M.msgDirError("Directory cannot be empty!");
                } else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        delayFolderSave(newValue, folder);
                        M.msgDirInfo("Directory exists!");
                        DIRECTORY_EXISTS.setValue(true);
                    } else {
                        M.msgDirError("Directory does not exist or is not a directory!");
                    }
                }
            }
        }));
        setDirContextMenu();
    }

    private void setControlActions() {
        form.btnSave.setOnAction(_ -> new Thread(() -> {
            UPDATING_BATCH.setValue(true);
            String link = getLink();
            filename = getFilename();
            String dir = getDir();
            if (Paths.get(dir, filename).toFile().exists()) {
                ConfirmationDialog ask = new ConfirmationDialog("Overwrite Existing File", "This will overwrite the existing file" + nl.repeat(2) + "Is this what you want to do?");
                if (ask.getResponse().isNo()) {
                    filename = renameFile(filename, dir);
                }
            }
            addJob(new Job(link, dir, filename, selectedJob.getDownloadLink()));
            clearLink();
            clearFilename();
            setDir(folders.getDownloadFolder());
            UPDATING_BATCH.setValue(false);
        }).start());
        form.btnStart.setOnAction(_ -> new Thread(() -> {
            if (PROCESSING_BATCH.getValue().equals(true)) {
                return;
            }
            if (!form.listView.getItems().isEmpty() && PROCESSING_BATCH.getValue().equals(false)) {
                clearLink();
                clearFilename();
                clearFilenameOutput();
                new Thread(batchDownloader()).start();
            }
        }).start());
        form.tfDir.setOnAction(_ -> updateBatch());
        form.tfFilename.setOnAction(_ -> updateBatch());
        form.tfLink.setOnKeyTyped(_ -> processLink());
        form.listView.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 1) {
                if (UPDATING_BATCH.getValue().equals(true)) {
                    clearControls();
                    setDir(folders.getDownloadFolder());
                    UPDATING_BATCH.setValue(false);
                } else {
                    Job job = form.listView.getSelectionModel().getSelectedItem();
                    if (job != null) {
                        selectJob(job);
                        setLink(job.getSourceLink());
                        setDir(job.getDir());
                        setFilename(job.getFilename());
                    }
                }
            }
        });
        form.listView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                Job job = form.listView.getSelectionModel().getSelectedItem();
                if (job != null) {
                    removeJobFromList(job);
                    clearControls();
                    UPDATING_BATCH.setValue(false);
                }
            }
        });
    }

    private void processLink() {
        new Thread(() -> {
            String first = getLink();
            sleep(1000);
            String userText = getLink();
            if (userText.equals(first)) {
                if (userText.contains("  ") || userText.contains(System.lineSeparator())) {
                    M.msgLinkError("Link should not contain whitespace characters!");
                    clearLink();
                    return;
                }
                String[] links;
                if (userText.contains(" ")) {
                    links = userText.trim().split(" ");
                } else {
                    links = new String[]{userText};
                }
                VERIFYING_LINKS.setValue(true);
                for (String link : links) {
                    verifyLinksAndWaitFor(link);
                }
                VERIFYING_LINKS.setValue(false);
                clearLink();
                clearFilename();
            }
        }).start();
    }

    private void verifyLinksAndWaitFor(String link) {
        Thread verify = new Thread(verifyLink(link));
        verify.start();
        while (!verify.getState().equals(Thread.State.TERMINATED)) {
            sleep(500);
        }
    }

    /*
    These methods are the meat of this class. They handle all the various processing that happens when the
    user engages the form and attempts to download links.

    Runnables are used to prevent the form from pin-wheeling (Macs) or hour-glassing(others) so that the appearance
    application freeze never happens. Runnables are assigned to Tasks.
     */
    private Runnable verifyLink(String link) {
        /*
        This method is called when the user pastes a link into the Link field. It checks the link to see if it is valid.

        If it is, it will then check to see if the link has been downloaded before. If it has, it will ask the user if they want to download it again. If they do, it will automatically rename the file to avoid overwriting the existing file.
        If the file has not been downloaded before, it will add the link to the job list and begin the process of extracting the filename from the link. If it is a Spotify URL (song or playlist), then the final download link will be retrieved from the Spotify API.

        If the link is not valid, the user will be informed and the link field will be cleared.

        Users should be instructed to click through each job in the list and make sure the filename is what they
        want. They can change it after clicking on the job in the list, then clicking on the save button.
        They can deselect the job by clicking on the list again with CTRL held down. Alternatively, they can click on the Save button to save the job.
        Also, they can press the DELETE key to remove the selected job from the list.
         */
        return () -> {
            if (link.isEmpty()) {
                return;
            }
            String message;
            String dir;
            if (!linkInJobList(link)) {
                M.msgLinkInfo("Validating link...");
                if (Utility.isLinkValid(link)) {
                    if (getHistory().exists(link)) {
                        Job job = getHistory().getJob(link);
                        String filename = job.getFilename();
                        dir = getDir();
                        if (dir == null) {
                            M.msgDirError("Download folder is not set!");
                            Environment.terminate(1);
                        }
                        String intro = "You have downloaded this link before. The filename is:" + nl + filename + nl.repeat(2);
                        String folder = fileExists(filename);
                        String windowTitle;
                        if (!folder.isEmpty()) {
                            message = intro + "The file already exists in the selected download folder:" + nl + folder + nl.repeat(2) + "Choose 'YES' to download and automatically rename the file to avoid overwriting the existing one. Alternatively, you can manually change the filename below to your preference." + nl.repeat(2) + "Choose 'NO' if you do not wish to download the file again.";
                            windowTitle = "File Already Exists";
                        } else {
                            message = intro + "The file does not exist in any of your designated download folders." + nl.repeat(2) + "This is a good opportunity to download it without concerns of duplicating existing files. Click 'YES' to proceed with the download, or 'NO' if you decide not to download.";
                            windowTitle = "File Already Downloaded";
                        }
                        ConfirmationDialog ask = new ConfirmationDialog(windowTitle, message, renameFile(filename, dir));
                        if (ask.getResponse().isYes()) {
                            filename = ask.getFilename();
                            downloadConfig = new GUIDownloadConfiguration(link, dir, filename);
                        }
                    } else {
                        downloadConfig = new GUIDownloadConfiguration(link, getDir(), null); // Filename is null because it will be retrieved from the link
                    }
                    downloadConfig.sanitizeLink();
                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                    executor.scheduleWithFixedDelay(this::commitJobListToListView, 0, 100, TimeUnit.MILLISECONDS);
                    Thread getNames = new Thread(getFilenames(downloadConfig));
                    getNames.start();
                    while (!getNames.getState().equals(Thread.State.TERMINATED)) {
                        sleep(150);
                    }
                    executor.shutdown();
                }
                clearFilename();
                clearLink();
                clearLinkOutput();
                clearFilenameOutput();
            } else {
                M.msgLinkError("Link already in job list");
                clearLink();
                UPDATING_BATCH.setValue(false);
            }
        };
    }

    private Runnable getFilenames(GUIDownloadConfiguration config) {
        return () -> {
            // Using a Worker Task, this method calls the FileMetadataRetriever class to retrieve all the required metadata for the file(s) to be downloaded and adds them to the jobList.
            Task<Void> task = new FileMetadataRetriever(config);
            Platform.runLater(() -> {
                /*
                These bindings allow the Worker thread to post relevant information to the UI, including the progress bar which
                accurately depicts the remaining number of filenames to extract from the link. However, if there is only one filename
                to extract, the progress bar goes through a static animation to indicate that the program is not frozen.
                The controls that are bound to the thread cannot have their text updated while they are bound, or else an error will be thrown and possibly the program execution halted.
                */
                form.lblDownloadInfo.textProperty().bind(task.messageProperty());
                form.pBar.progressProperty().bind(task.progressProperty());
            });
            setLink(config.getLink());
            Thread retrieveFileData = new Thread(task);
            retrieveFileData.setDaemon(true);
            retrieveFileData.start();
            sleep(2000);
            form.lblDownloadInfo.setTextFill(GREEN);
            while (!retrieveFileData.getState().equals(Thread.State.TERMINATED) && !retrieveFileData.getState().equals(Thread.State.BLOCKED)) {
                sleep(50);
            }
            sleep(500);
            clearControls();
        };
    }

    private Runnable batchDownloader() {
        return () -> {
            PROCESSING_BATCH.setValue(true);
            UPDATING_BATCH.setValue(false);
            form.lblDownloadInfo.setTextFill(GREEN);
            IntegerProperty speedValueProperty = new SimpleIntegerProperty();
            speedValueProperty.addListener(((_, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    speedValue += (int) newValue;
                    speedValueUpdateCount++;
                    if (speedValueUpdateCount == 5) {
                        int speed = speedValue / 5;
                        speedValueUpdateCount = 0;
                        speedValue = 0;
                        setFilenameOutput(GREEN, "Speed: " + speed + " KB/s");
                    }
                }
            }));
            if (jobs.notNull() && !jobs.isEmpty()) {
                final int totalFiles = jobs.jobList().size();
                int fileCount = 0;
                LinkedList<Job> tempJobList = new LinkedList<>(jobs.jobList());
                for (Job job : tempJobList) {
                    fileCount++;
                    M.msgBatchInfo("Processing file " + fileCount + " of " + totalFiles + ": " + job);
                    FileDownloader downloadFile = new FileDownloader(job, form.tfLink.textProperty(), form.tfDir.textProperty(), form.tfFilename.textProperty(), form.lblDownloadInfo.textProperty(), speedValueProperty, form.pBar.progressProperty());
                    try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
                        executor.submit(downloadFile);
                        while (downloadFile.notDone()) {
                            sleep(500);
                        }
                        int exitCode = downloadFile.getExitCode();
                        removeJobFromList(job);
                        setDownloadInfoColor(GREEN);
                        if (exitCode == 0) { // Success
                            getHistory().addJob(job, false);
                        }
                    }
                }
            }
            clearLink();
            clearLinkOutput();
            clearFilename();
            clearFilenameOutput();
            PROCESSING_BATCH.setValue(false);
        };
    }

    private String fileExists(String filename) {
        for (String folder : folders.getFolders()) {
            CheckFile checkFile = new CheckFile(folder, filename);
            Thread thread = new Thread(checkFile);
            thread.start();
            while (!thread.getState().equals(Thread.State.TERMINATED)) {
                sleep(100);
            }
            if (checkFile.fileFound()) {
                return folder;
            }
        }
        return "";
    }

    private boolean linkInJobList(String link) {
        for (Job job : jobs.jobList()) {
            if (job.getSourceLink().equals(link)) {
                return true;
            }
        }
        return false;
    }

    private void addJob(Job newJob) {
        Job oldJob = null;
        for (Job job : jobs.jobList()) {
            if (job.matchesLink(newJob)) {
                oldJob = job;
                break;
            }
        }
        if (oldJob != null) {
            jobs.remove(oldJob);
            try {
                DbConnection dbConnection = DbConnection.getInstance();
                dbConnection.updateFile(
                        newJob.getFilename(),
                        oldJob.getSourceLink(),
                        newJob.getDir()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                DbConnection dbConnection = DbConnection.getInstance();
                dbConnection.addFileRecordToQueue(
                        newJob.getFilename(),
                        newJob.getSourceLink(),
                        newJob.getDownloadLink(),
                        newJob.getDir(),
                        currentSessionId
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Job Added: " + newJob.getFilename());
        }
        jobs.add(newJob);
        commitJobListToListView();
    }

    private void removeJobFromList(Job oldJob) {
        jobs.remove(oldJob);
        try {
            DbConnection dbConnection = DbConnection.getInstance();
            dbConnection.deleteQueuedFile(
                    oldJob.getSourceLink(),
                    oldJob.getDir(),
                    oldJob.getFilename()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        commitJobListToListView();
        M.msgBatchInfo("Job Removed: " + oldJob.getSourceLink());
    }

    private void updateBatch() {
        UPDATING_BATCH.setValue(false);
        if (selectedJob != null) {
            Job job = new Job(selectedJob.getSourceLink(), getDir(), getFilename(), selectedJob.getDownloadLink());
            removeJobFromList(selectedJob);
            addJob(job);
        }
        selectedJob = null;
    }

    private void delayFolderSave(String folderString, File folder) {
        /*
        If the user is typing a file path into the field, we don't want to save every folder 'hit'
        so we wait 3 seconds, and if the String is still the same value, then we commit the folder to the list.
        */
        new Thread(() -> {
            sleep(3000);
            if (getDir().equals(folderString)) {
                if (folder.exists() && folder.isDirectory()) {
                    folders.addFolder(folder.getAbsolutePath());
                    setDirContextMenu();
                }
            }
        }).start();
    }

    private ContextMenu getListMenu() {
        MenuItem miDel = new MenuItem("Delete");
        MenuItem miClear = new MenuItem("Clear");
        MenuItem miHelp = new MenuItem("Help");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        miDel.setOnAction(_ -> {
            Job job = form.listView.getSelectionModel().getSelectedItem();
            if (job != null) {
                removeJobFromList(job);
                clearControls();
            }
        });
        miClear.setOnAction(_ -> {
            jobs.clear();
            commitJobListToListView();
            clearLink();
            clearFilename();
            UPDATING_BATCH.setValue(false);
            form.listView.getItems().clear();
            M.msgLinkInfo("");
            M.msgFilenameInfo("");
            M.msgDirInfo("");
        });
        miHelp.setOnAction(_ -> handleHelpWindow());
        return new ContextMenu(miDel, miClear, separator, miHelp);
    }

    private void setDirContextMenu() {
        ContextMenu cm = new ContextMenu();
        for (String folder : folders.getFolders()) {
            MenuItem mi = new MenuItem(folder);
            mi.setOnAction(_ -> setDir(folder));
            cm.getItems().add(mi);
        }
        MenuItem mi = new MenuItem("Add Folder");
        mi.setOnAction(_ -> getDirectory());
        cm.getItems().add(mi);
        cm.getStyleClass().add("rightClick");
        form.tfDir.setContextMenu(cm);
    }

    public static void initLogic(MainGridPane pane) {
        INSTANCE.start(pane);
    }

    public static void setDownloadInfoColor(Color color) {
        form.lblDownloadInfo.setTextFill(color);
    }

    public static void resetDownloadFoldersToActiveList() {
        INSTANCE.folders = new Folders();
    }

    public static boolean isAutoPaste() {
        return form.cbAutoPaste.isSelected();
    }

    public static void clearJobHistory() {
        /*
        Called from the Edit menu, this wipes out the job history which is stored in the users file system
         */
        INSTANCE.getHistory().clear();
        try {
            DbConnection dbConnection = DbConnection.getInstance();
            dbConnection.deleteFilesHistory();
        } catch (SQLException e) {
            ConfirmationDialog ask = new ConfirmationDialog("Error", "Failed to clear job history! " + e.getMessage(), true, false);
            ask.getResponse();
            M.msgLogError("SQL Exception: " + e.getMessage());
        }
    }

    public static void pasteFromClipboard(String text) {
        INSTANCE.setLink(text);
        INSTANCE.processLink();
    }

    private void setLink(String link) {
        Platform.runLater(() -> form.tfLink.setText(link));
    }

    public static void setDir(String path) {
        Platform.runLater(() -> form.tfDir.setText(path));
    }

    private void setFilename(String filename) {
        Platform.runLater(() -> form.tfFilename.setText(filename));
    }

    private void clearLink() {
        Platform.runLater(() -> {
            form.tfLink.clear();
            form.tfLink.requestFocus();
        });
    }

    private void clearFilename() {
        Platform.runLater(() -> {
            setFilename("");
            M.msgDownloadInfo("");
            M.msgFilenameInfo("");
        });
    }

    /*
    These methods control the labels under the TextFields (arranged in the order they appear on the form)
     */

    public void setLinkOutput(Color color, String message) {
        if (color.equals(GREEN) || color.equals(PURPLE) || color.equals(HOTPINK)) {
            form.lblLinkOut.getStyleClass().add("outline");
        }
        form.lblLinkOut.setTextFill(color);
        Platform.runLater(() -> {
            form.lblLinkOut.getStyleClass().clear();
            form.lblLinkOut.setText(message);
            if (color.equals(DARK_RED) || color.equals(YELLOW)) {
                new Thread(() -> {
                    sleep(5000);
                    clearLinkOutput();
                }).start();
            }
        });
    }

    private void clearLinkOutput() {
        setLinkOutput(GREEN, "");
    }

    public void setDirOutput(Color color, String message) {
        Platform.runLater(() -> {
            form.lblDirOut.getStyleClass().clear();
            if (color.equals(GREEN) || color.equals(PURPLE) || color.equals(HOTPINK)) {
                form.lblDirOut.getStyleClass().add("outline");
            }
            form.lblDirOut.setTextFill(color);
            form.lblDirOut.setText(message);
            if (color.equals(DARK_RED) || color.equals(YELLOW)) {
                new Thread(() -> {
                    sleep(5000);
                    clearDirOutput();
                }).start();
            }
        });
    }

    private void clearDirOutput() {
        setDirOutput(GREEN, "");
    }

    public void setFilenameOutput(Color color, String message) {
        Platform.runLater(() -> {
            form.lblFilenameOut.getStyleClass().clear();
            if (color.equals(GREEN) || color.equals(PURPLE) || color.equals(HOTPINK)) {
                form.lblFilenameOut.getStyleClass().add("outline");
            }
            form.lblFilenameOut.setTextFill(color);
            form.lblFilenameOut.setText(message);
            if (color.equals(DARK_RED) || color.equals(YELLOW)) {
                new Thread(() -> {
                    sleep(5000);
                    clearFilenameOutput();
                }).start();
            }
        });
    }

    private void clearFilenameOutput() {
        setFilenameOutput(GREEN, "");
    }

    public void setDownloadOutput(Color color, String message) {
        if (PROCESSING_BATCH.getValue().equals(false)) {
            Platform.runLater(() -> {
                form.lblDownloadInfo.getStyleClass().clear();
                if (color.equals(GREEN) || color.equals(PURPLE) || color.equals(HOTPINK)) {
                    form.lblDownloadInfo.getStyleClass().add("outline");
                }
                form.lblDownloadInfo.textProperty().unbind();
                form.lblDownloadInfo.setTextFill(color);
                form.lblDownloadInfo.setText(message);
                if (color.equals(DARK_RED) || color.equals(YELLOW)) {
                    new Thread(() -> {
                        sleep(5000);
                        clearDownloadOutput();
                    }).start();
                }
            });
        }
    }

    private void clearDownloadOutput() {
        setDownloadOutput(GREEN, "");
    }

    private void clearControls() {
        clearLink();
        clearFilename();
        M.msgLinkInfo("");
        M.msgDirInfo("");
    }

    private String getLink() {
        return form.tfLink.getText();
    }

    private String getDir() {
        return form.tfDir.getText();
    }

    private String getFilename() {
        return form.tfFilename.getText();
    }

    private void commitJobListToListView() {
        Platform.runLater(() -> {
            if (jobs.notNull()) {
                if (jobs.isEmpty()) {
                    form.listView.getItems().clear();
                } else {
                    // Assign the jobList to the ListView
                    form.listView.getItems().setAll(jobs.jobList());
                }
            }
        });
    }

    private void help() {
        Color textColor = "Dark".equals(AppSettings.getGuiTheme()) ? Color.WHITE : Color.BLACK;
        Color headingsColor = "Dark".equals(AppSettings.getGuiTheme()) ? Color.LIGHTGREEN : Color.DARKBLUE;
        double h = 20;
        double n = 16;
        INFO_TF.getChildren().add(text("Link:\n", true, headingsColor, h));
        INFO_TF.getChildren().add(text("The Drifty GUI lets you easily create batches for downloading, or download a single file. You start by pasting your web links into the Link field. Once a link has been put into Link field, Drifty will start to process it where it will attempt to determine the name of the file being downloaded. Once it has done that, you will see the filename show up in the batch list on the left.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("The URLs you paste into the link field must be valid URLs or Drifty wont process them.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("Checking the ", false, textColor, n));
        INFO_TF.getChildren().add(text("Auto Paste ", true, textColor, n));
        INFO_TF.getChildren().add(text("option will let you go to another window and put a link in the clipboard then when you come back to Drifty, the link will be pasted into the Link field automatically and processed then added to the batch list.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("If you paste in a link that happens to extract multiple files for downloading, such as a Youtube play list, Drifty will first attempt to get the number of files in the list, then it will ask you if you would like it to obtain all of the filenames in the list. The progress bar will indicate how many filenames have been obtained and the batch list will populate with download jobs with each new filename discovered.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("Directory:\n", true, headingsColor, h));
        INFO_TF.getChildren().add(text("Right clicking anywhere on the form brings up a menu where you can add directories to use as download folders. As you add more directories, they accumulate and persist between reloads. The last directory that you add last will be considered the current download directory. When you click start which begins the download process for all jobs in the batch list, Drifty will look through all of the added folders for a matching filenames and it will let you know when it finds duplicates and give you the option to not download them again.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("Right clicking on the form and choosing to edit the directory list pulls up a form with all of the directories you have added. Click on one to remove it if necessary.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("Filename:\n", true, headingsColor, h));
        INFO_TF.getChildren().add(text("Drifty tries to get the name of the file from the link. This process can take a little time but not usually more than 10 to 20 seconds. By default, Drifty adds the extension of 'mp4' to video file downloads because these have the highest chance of success. If Drifty cannot determine the name of the file, you can type in whatever filename you'd like, then click on Save to commit that to the job in the list. You can also determine the download format of the file by setting the filename extension to one of these options: 3gp, aac, flv, m4a, mp3, mp4, ogg, wav, webm.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("Job list:\n", true, headingsColor, h));
        INFO_TF.getChildren().add(text("Once the list box on the left has the jobs in it that you want, you can click on each one in turn and the link, download directory and filename will be placed into the related fields so you can edit them if you need to. Just click on Save when you're done editing.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("You can right click on any item in the list to remove it or clear out the job list completely.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("The Job list and the directory list will persist between program reloads. The job list will empty out one at a time as each file in the list is downloaded. You start the batch by clicking on the Start button. Any files that fail to download will get recycled back into the list on this form. I found that they will download usually after a second attempt.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("Auto Paste:\n", true, headingsColor, h));
        INFO_TF.getChildren().add(text("The whole point of Auto Paste is to help speed things up. When you check the box, then go out to your browser and find links that you want to download, just copy them into your clip board then ALT+TAB back to Drifty or just click on it to make it the active window. Drifty will sense that it has been made the active screen and the contents of your clipboard will be analyzed to make sure it is a valid URL, then it will process it if so.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("Multi-link pasting:\n", true, headingsColor, h));
        INFO_TF.getChildren().add(text("Another way to speed things up it to copy and paste links into a notepad of some kind, then just put a single space between each link so that all the links are on a single line, then paste that line into the Link field and Drifty will start processing them in turn and build up your batch for you.\n\n", false, textColor, n));
        INFO_TF.getChildren().add(text("Youtube Playlists:\n", true, headingsColor, h));
        INFO_TF.getChildren().add(text("Another thing you can do is grab a YouTube playlist and Drifty will extract all of the videos from the playlist and build a batch from the list (or add to your existing batch).\n\n", false, textColor, n));
        INFO_TF.setStyle("-fx-background-color: transparent");

        double width = 500;
        double height = 700;
        helpStage = Constants.getStage("Help", false);
        helpStage.setWidth(width);
        helpStage.setHeight(height + 100);
        VBox vox = new VBox(20, INFO_TF);
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
        infoScene = Constants.getScene(scrollPane);
        infoScene.setFill(Color.TRANSPARENT);
        if ("Dark".equals(AppSettings.getGuiTheme())) {
            Theme.applyTheme("Dark", infoScene);
        }
        helpStage.setScene(infoScene);
        helpStage.setAlwaysOnTop(true);
        helpStage.setTitle("Help");
        helpStage.setOnCloseRequest(_ -> helpStage.close());
        VBox.setVgrow(vox, Priority.ALWAYS);
        VBox.setVgrow(INFO_TF, Priority.ALWAYS);
        scrollPane.setVvalue(0.0);
        try {
            helpStage.showAndWait();
        } catch (Exception e) {
            Environment.getMessageBroker().msgLogError("Error displaying Help window: " + e.getMessage());
        }
    }

    public void handleHelpWindow() {
        if (helpStage != null && helpStage.isShowing()) {
            helpStage.toFront();
        } else {
            help();
        }
    }

    private Text text(String string, boolean bold, Color color, double size) {
        // This is used by the help() method for custom text formatting
        Text text = new Text(string);
        text.setFont(new Font("monospace", size));
        text.setFill(color);
        if (bold) {
            text.setStyle("-fx-font-weight: bold;");
        }
        text.setWrappingWidth(710);
        text.setLineSpacing(.5);
        return text;
    }

    public static void getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String lastFolder = AppSettings.getLastDownloadFolder();
        String initFolder = (lastFolder == null || lastFolder.isEmpty()) ? Utility.getHomeDownloadFolder() : lastFolder;
        directoryChooser.setInitialDirectory(new File(initFolder));
        File directory = directoryChooser.showDialog(null);
        if (directory != null) {
            setDir(directory.getAbsolutePath());
        }
    }

    private void getJobs() {
        jobs = JobService.getJobs();
    }

    private JobHistory getHistory() {
        return JobService.getJobHistory();
    }

    private void selectJob(Job job) {
        selectedJob = job;
        UPDATING_BATCH.setValue(true);
    }
}
