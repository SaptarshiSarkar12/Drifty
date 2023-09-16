package GUI.Forms;

import Enums.*;
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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static Enums.Colors.*;
import static GUI.Forms.Constants.MONACO_TTF;
import static GUI.Forms.Constants.getStage;

public class FormLogic {
    public static final FormLogic INSTANCE = new FormLogic();
    public static MainGridPane form;
    private static final MessageBroker M = Environment.getMessageBroker();
    private static final BooleanProperty directoryExists = new SimpleBooleanProperty(false);
    private static final BooleanProperty processingBatch = new SimpleBooleanProperty(false);
    private static final BooleanProperty updatingBatch = new SimpleBooleanProperty(false);
    private static final BooleanProperty verifyingLinks = new SimpleBooleanProperty(false);
    private final String nl = System.lineSeparator();
    private int speedValueUpdateCount = 0;
    private int speedValue = 0;
    private Folders folders;
    private Job selectedJob;

    /*
    Single instance model only constructor
     */
    private FormLogic() {
        folders = AppSettings.get.folders();
    }

    /*
    Methods for initializing the various controls that are on the form - MainGridPane
     */
    private void start(MainGridPane pane) {
        form = pane;
        setControlProperties();
        setControlActions();
        form.tfLink.requestFocus();
        commitJobListToListView();
    }

    private void setControlProperties() {
        setDir(folders.getDownloadFolder());
        directoryExists.setValue(new File(getDir()).exists());

        BooleanBinding disableStartButton = form.listView.itemsProperty().isNotNull().not().or(processingBatch).or(directoryExists.not()).or(verifyingLinks);
        BooleanBinding disableInputs = processingBatch.or(verifyingLinks);
        BooleanBinding hasText = form.tfLink.textProperty().isEmpty().not().and(form.tfDir.textProperty().isEmpty().not().and(form.tfFilename.textProperty().isEmpty().not()));

        form.btnSave.visibleProperty().bind(updatingBatch);
        form.btnStart.disableProperty().bind(disableStartButton);
        form.tfDir.disableProperty().bind(disableInputs);
        form.tfFilename.disableProperty().bind(disableInputs);
        form.tfLink.disableProperty().bind(disableInputs);

        form.listView.setContextMenu(getListMenu());

        Tooltip.install(form.cbAutoPaste, new Tooltip("When checked, will paste contents of clipboard into" + nl + "Link field when switching back to this screen."));
        Tooltip.install(form.tfLink, new Tooltip("URL must be a valid URL without spaces." + nl + " Add multiple URLs by pasting them in from the clipboard and separating each URL with a space."));
        Tooltip.install(form.tfFilename, new Tooltip("If the filename you enter already exists in the download folder, it will" + nl + "automatically be renamed to avoid file over-writes."));
        Tooltip.install(form.tfDir, new Tooltip("Right click anywhere to add a new download folder." + nl + "Drifty will accumulate a list of download folders" + nl + "so that duplicate downloads can be detected."));
        form.listView.setOnMouseClicked(e -> {
            Job job = (Job) form.listView.getSelectionModel().getSelectedItem();
            setLink(job.getLink());
            setDir(job.getDir());
            setFilename(job.getFilename());
            selectJob(job);
        });
        form.cbAutoPaste.setSelected(AppSettings.get.mainAutoPaste());
        form.cbAutoPaste.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.set.mainAutoPaste(newValue)));
        form.tfDir.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                directoryExists.setValue(false);
                if (newValue.isEmpty()) {
                    M.msgDirError("Directory cannot be empty!");
                }
                else {
                    File folder = new File(newValue);
                    if (folder.exists() && folder.isDirectory()) {
                        delayFolderSave(newValue, folder);
                        M.msgDirInfo("Directory exists!");
                        directoryExists.setValue(true);
                    }
                    else {
                        M.msgDirError("Directory does not exist or is not a directory!");
                    }
                }
            }
        }));
        setDirContextMenu();
    }

    private void setControlActions() {
        form.btnSave.setOnAction(e -> new Thread(() -> {
            String link = getLink();
            String filename = getFilename();
            String dir = getDir();
            if (Paths.get(dir, filename).toFile().exists()) {
                AskYesNo ask = new AskYesNo("This will overwrite the existing file" + nl.repeat(2) + "Is this what you want to do?");
                if (ask.getResponse().isNo()) {
                    filename = renameFile(filename, dir);
                }
                removeJobFromList(selectedJob);
                addJob(new Job(link, dir, filename, selectedJob.repeatOK()));
            }
            else {
                removeJobFromList(selectedJob);
                addJob(new Job(link, dir, filename, selectedJob.repeatOK()));
            }
            clearLink();
            clearFilename();
            updatingBatch.setValue(false);
        }).start());
        form.btnStart.setOnAction(e -> new Thread(() -> {
            if (processingBatch.getValue().equals(true))
                return;
            if (!form.listView.getItems().isEmpty() && processingBatch.getValue().equals(false)) {
                clearLink();
                clearFilename();
                clearFilenameOutput();
                new Thread(batchDownloader()).start();
            }
        }).start());
        form.tfDir.setOnAction(e -> updateBatch());
        form.tfFilename.setOnAction(e -> updateBatch());
        form.tfLink.setOnKeyTyped(e -> processLink());
        form.listView.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 1) {
                Job job = (Job) form.listView.getSelectionModel().getSelectedItem();
                if (job != null) {
                    selectJob(job);
                    setLink(job.getLink());
                    setDir(job.getDir());
                    setFilename(job.getFilename());
                }
            }
        });
        form.listView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                Job job = (Job) form.listView.getSelectionModel().getSelectedItem();
                if (job != null) {
                    removeJobFromList(job);
                    clearControls();
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
                }
                else {
                    links = new String[]{userText};
                }
                verifyingLinks.setValue(true);
                for (String link : links) {
                    Thread verify = new Thread(verifyLink(link));
                    verify.start();
                    while (!verify.getState().equals(Thread.State.TERMINATED)) {
                        sleep(500);
                    }
                }
                verifyingLinks.setValue(false);
                clearLink();
                clearFilename();
            }
        }).start();
    }

    /*
    These methods are the meat of this class. They handle all the various processing that happens when the
    user engages the form and attempts to download links.

    Runnables are used to prevent the form from pin-wheeling (macs) or hour-glassing(others) so that the appearance
    application freeze never happens. Runnables are assigned to Tasks.
     */

    private Runnable verifyLink(String link) {
        /*
        When adding links to the jobList, only YouTube and Instagram links will be put through the process of
        searching the link for more than one download, in case the link happens to be a link to a playlist. This
        will probably be far more common with YouTube links.

        If the link does not test positive for YouTube or Instagram, then it is merely added to the jobList as a job
        with only the link and the download folder given to the Job class. However, the Job class will take all
        the text after the last forward slash in the link and set it as the filename for that job.

        Users should be instructed to click through each job in the list and make sure the filename is what they
        want. They can change it after clicking on the job in the list then clicking on the save button.
         */
        return () -> {
            if (link.isEmpty()) {
                return;
            }
            String message;
            String filename;
            String dir;
            if (!linkInJobList(link)) {
                M.msgLinkInfo("Validating link...");
                if (Utility.linkValid(link)) {
                    if (getHistory().exists(link)) {
                        Job job = getHistory().getJob(link);
                        filename = job.getFilename();
                        dir = getDir();
                        if(dir == null) {
                            System.err.println("dir is null");
                            System.exit(0);
                        }
                        String intro = "You have downloaded this link before. The filename is:" + nl + filename + nl.repeat(2);
                        String folder = fileExists(filename);
                        if (!folder.isEmpty()) {
                            message = intro + "And the file exists in this download folder:" + nl + folder + nl.repeat(2) +
                                    "If you wish to download it again, it will be given the name shown below, or you can change it as you wish." + nl.repeat(2) +
                                    "YES will add the job to the list with new filename. NO will do nothing.";
                        }
                        else {
                            message = intro + "However, the file does not exist in any of your download folders." + nl.repeat(2) +
                                    "Do you still wish to download this file?";
                        }
                        AskYesNo ask = new AskYesNo(message, renameFile(filename, dir));
                        if (ask.getResponse().isYes()) {
                            filename = ask.getFilename();
                            addJob(new Job(link, dir, filename, true));
                        }
                    }
                    else if (Utility.isExtractableLink(link)) {
                        Thread getNames = new Thread(getFilenames(link));
                        getNames.start();
                        while (!getNames.getState().equals(Thread.State.TERMINATED)) {
                            sleep(150);
                        }
                    }
                    else {
                        addJob(new Job(link, getDir()));
                    }
                }
                clearFilename();
                clearLink();
                clearLinkOutput();
                clearFilenameOutput();
            }
            else {
                M.msgLinkError("Link already in job list");
                clearLink();
            }
        };
    }

    private Runnable getFilenames(String link) {
        return () -> {
            //Using a Worker Task, this method gets the filename(s) from the link.
            Task<ConcurrentLinkedDeque<Job>> task = new GetFilename(link, getDir());
            Worker<ConcurrentLinkedDeque<Job>> worker = task;
            Platform.runLater(() -> {
            /*
            These bindings allow the Worker thread to post relevant information to the UI including the progress bar which
            accurately depicts the remaining number of filenames to extract from the link. However, if there is only one filename
            to extract, the progress bar goes through a static animation to indicate that the program is not frozen.
            The controls that are bound to the thread cannot have their text updated while they ae bound or else an error
            will be thrown and possibly the program execution halted.
            */
                form.lblDownloadInfo.textProperty().bind(worker.messageProperty());
                form.pBar.progressProperty().bind(worker.progressProperty());
            });

            /*
            This parent thread allows us to repeatedly check the Worker Task Thread for new filenames found so that we can add them
            to the job batch as they are discovered. Doing this in this thread keeps the UI from appearing frozen to the user.
            We use the checkHistoryAddJobs method to look for discovered filenames. If we didn't do it this way, then we would need
            to wait until all filenames are discovered then add the jobs to the batch list in one action. Doing it this way
            gives the user more consistent feedback of the process while it is happening. This matters when a link contains
            a lot of files because each file discovered takes a while and when there are even hundreds of files, this process
            can appear to take a long time, so constant feedback for the user becomes relevant.
             */

            setLink(link);
            Thread getFilenameThread = new Thread(task);
            getFilenameThread.setDaemon(true);
            getFilenameThread.start();
            sleep(2000);
            form.lblDownloadInfo.setTextFill(GREEN);
            while (!getFilenameThread.getState().equals(Thread.State.TERMINATED) && !getFilenameThread.getState().equals(Thread.State.BLOCKED)) {
                checkHistoryAddJobs(worker);
                sleep(50);
            }
            sleep(500);
            checkHistoryAddJobs(worker); // Check one last time
            clearControls();
        };
    }

    private Runnable batchDownloader() {
        /*
        When the jobList is iterated, the link from a Job is passed through the LinkType enum class where the file
        extension is compared against a list of known BINARY file extensions. That list exists in the file called
        BinaryExtensions.json in the Resources GUI folder. What matters in terms of what a BINARY file is, is whether
        or not the file contains plain text - where you could open it in a text editor, and it would have actual words
        in it, or if it is a truly binary file such as .zip, .exe, .dll, .dmg, .iso etc.

        Binary files are best handled by yt-dlp and will be handed to the GUI downloader task where download progress
        will be displayed as well as all appropriate logging etc.

        YouTube and Instagram files also get sent to the GUI DownloadFile class which is a class that extends a
        modern version of Task where feedback is attainable through various methods.

        File extensions that are not in the binary extension list, are sent to the CLI file downloader, where the
        progress bar will be bound to the relevant variables in that class so that download progress can still be
        shown in the GUI
         */

        return () -> {
            processingBatch.setValue(true);
            updatingBatch.setValue(false);
            form.lblDownloadInfo.setTextFill(GREEN);
            IntegerProperty speedValueProperty = new SimpleIntegerProperty();
            speedValueProperty.addListener(((observable, oldValue, newValue) -> {
                if(!oldValue.equals(newValue)) {
                    speedValue += (int) newValue;
                    speedValueUpdateCount++;
                    if(speedValueUpdateCount == 5) {
                        int speed = speedValue / 5;
                        speedValueUpdateCount = 0;
                        speedValue = 0;
                        setFilenameOutput(GREEN,speed + " /s");
                    }
                }
            }));
            if (getJobs().notNull() && getJobs().isNotEmpty()) {
                final int totalFiles = getJobs().jobList().size();
                int fileCount = 0;
                LinkedList<Job> tempJobList = new LinkedList<>(getJobs().jobList());
                for (Job job : tempJobList) {
                    fileCount++;
                    M.msgBatchInfo("Processing file " + fileCount + " of " + totalFiles + ": " + job);
                    DownloadFile downloadFile = new DownloadFile(job,
                                                                 form.tfLink.textProperty(),
                                                                 form.tfDir.textProperty(),
                                                                 form.tfFilename.textProperty(),
                                                                 form.lblDownloadInfo.textProperty(),
                                                                 speedValueProperty,
                                                                 form.pBar.progressProperty());
                    Task<Integer> task = downloadFile;
                    try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
                        Future<Integer> future = (Future<Integer>) executor.submit(task);
                        while(downloadFile.notDone()) {
                            sleep(500);
                        }
                        int exitCode = downloadFile.getExitCode();
                        if(exitCode == 0) { //Success
                            removeJobFromList(job);
                            getHistory().addJob(job);
                        }
                    }
                }
            }
            clearLink();
            clearLinkOutput();
            clearFilename();
            clearFilenameOutput();
            processingBatch.setValue(false);
        };
    }

    private String fileExists(String filename) {
        for (String folder : AppSettings.get.folders().getFolders()) {
            CheckFile checkFile = new CheckFile(folder, filename);
            Thread thread = new Thread(checkFile);
            thread.start();
            while (!thread.getState().equals(Thread.State.TERMINATED)) {
                sleep(100);
            }
            if (checkFile.fileFound())
                return folder;
        }
        return "";
    }

    private boolean linkInJobList(String link) {
        for (Job job : getJobs().jobList()) {
            if (job.getLink().equals(link))
                return true;
        }
        return false;
    }

    private void addJob(Job newJob) {
        Job oldJob = null;
        for (Job job : getJobs().jobList()) {
            if (job.matchesLink(newJob)) {
                oldJob = job;
                break;
            }
        }
        if (oldJob != null) {
            getJobs().remove(oldJob);
        }
        getJobs().add(newJob);
        commitJobListToListView();
    }

    private void removeJobFromList(Job oldJob) {
        getJobs().remove(oldJob);
        commitJobListToListView();
        M.msgBatchInfo("Job Removed: " + oldJob.getLink());
    }

    private void updateBatch() {
        updatingBatch.setValue(false);
        if (selectedJob != null) {
            Job job = new Job(getLink(), getDir(), getFilename(), selectedJob.repeatOK());
            removeJobFromList(selectedJob);
            addJob(job);
        }
        selectedJob = null;
    }

    private String renameFile(String filename, String dir) {
        Path path = Paths.get(dir, filename);
        String newFilename = filename;
        int fileNum = -1;
        String baseName = FilenameUtils.getBaseName(filename.replaceAll(" \\(\\d+\\)\\.", "."));
        String ext = "." + FilenameUtils.getExtension(filename);
        while (path.toFile().exists()) {
            fileNum += 1;
            newFilename = baseName + " (" + fileNum + ")" + ext;
            path = Paths.get(dir, newFilename);
        }
        return newFilename;
    }

    private void checkHistoryAddJobs(Worker<ConcurrentLinkedDeque<Job>> worker) {
        String pastJobNoFile = "You have downloaded %s in the past, but the file does not exist in your download folder." + nl.repeat(2) + " Click Yes if you still wish to download this file. Otherwise, click No.";
        String pastJobFileExists = "You have downloaded %s in the past, and the file exists in your download folder." + nl.repeat(2) +
                "It will be renamed as shown here, or you may change the filename to your liking." + nl.repeat(2) +
                "Clicking Yes will commit the job with the shown filename, while clicking No will not add this file to the job list.";
        String fileExistsString = "This file:" + nl.repeat(2) + "%s" + nl.repeat(2) + "Exists in in the download folder." + nl.repeat(2) +
                "It will be renamed as shown here, or you may change the filename to your liking." + nl.repeat(2) +
                "Clicking Yes will commit the job with the shown filename, while clicking No will not add this file to the job list.";
        Platform.runLater(() -> {
            String message;
            AskYesNo ask = new AskYesNo("");
            boolean addJob;
            if (worker.valueProperty().get() != null) {
                for (Job job : worker.valueProperty().get()) {
                    boolean fileExists = job.fileExists();
                    boolean hasHistory = getHistory().exists(job.getLink());
                    boolean existsHasHistory = fileExists && hasHistory;
                    boolean existsNoHistory = fileExists && !hasHistory;
                    boolean fileHasHistory = hasHistory && !fileExists;
                    if (!getJobs().jobList().contains(job)) {
                        if (existsHasHistory) {
                            message = String.format(pastJobFileExists, job.getFilename());
                            ask = new AskYesNo(message, renameFile(job.getFilename(), job.getDir()));
                        }
                        else if (existsNoHistory) {
                            message = String.format(fileExistsString, job.getFilename());
                            ask = new AskYesNo(message, false);
                        }
                        else if (fileHasHistory) {
                            message = String.format(pastJobNoFile, job.getFilename());
                            ask = new AskYesNo(message, false);
                        }
                        if (fileHasHistory || existsHasHistory || existsNoHistory) {
                            addJob = ask.getResponse().isYes();
                            if (addJob) {
                                String newFilename = ask.getFilename();
                                boolean repeatDownload = newFilename.equals(job.getFilename());
                                String filename = newFilename.isEmpty() ? job.getFilename() : newFilename;
                                if (!filename.isEmpty()) {
                                    addJob(new Job(job.getLink(), job.getDir(), filename, repeatDownload));
                                }
                            }
                        }
                        else
                            addJob(job);

                    }
                }
            }
        });
    }

    private void delayFolderSave(String folderString, File folder) {
        /*
        If the user is typing a file path into the field, we don't want to save every folder 'hit' so we wait 3 seconds
        and if the String is still the same value, then we commit the folder to the list.
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
        MenuItem miInfo = new MenuItem("Information");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        miDel.setOnAction(e -> {
            Job job = (Job) form.listView.getSelectionModel().getSelectedItem();
            if (job != null) {
                removeJobFromList(job);
                clearControls();
            }
        });
        miClear.setOnAction(e -> {
            getJobs().clear();
            commitJobListToListView();
            clearLink();
            clearFilename();
            form.listView.getItems().clear();
            M.msgLinkInfo("");
            M.msgFilenameInfo("");
            M.msgDirInfo("");
        });
        miInfo.setOnAction(e -> help());
        return new ContextMenu(miDel, miClear, separator, miInfo);
    }

    private void setDirContextMenu() {
        Folders folders = AppSettings.get.folders();
        ContextMenu cm = new ContextMenu();
        for (String folder : folders.getFolders()) {
            MenuItem mi = new MenuItem(folder);
            mi.setOnAction(e -> setDir(folder));
            cm.getItems().add(mi);
        }
        MenuItem mi = new MenuItem("Add Folder");
        mi.setOnAction(e -> getDirectory());
        cm.getItems().add(mi);
        cm.getStyleClass().add("rightClick");
        form.tfDir.setContextMenu(cm);
    }

    /*
    These are all public static methods and are grouped together because they represent the code that is
    accessible from anywhere in the entire code base.
     */

    public static void initLogic(MainGridPane pane) {
        INSTANCE.start(pane);
    }

    public static void setDownloadInfoColor(Color color) {
        form.lblDownloadInfo.setTextFill(color);
    }

    public static void resetDownloadFoldersToActiveList() {
        INSTANCE.folders = AppSettings.get.folders();
    }

    public static boolean isAutoPaste() {
        return form.cbAutoPaste.isSelected() || AppSettings.get.alwaysAutoPaste();
    }

    public static void addJob(@NotNull ConcurrentLinkedDeque<Job> list) {
        /*
        This takes the list passed in as argument and compares it against the jobs in the current jobList
        then it only adds jobs that do not exist.
         */
        for (Job job : list) {
            boolean hasJob = INSTANCE.getJobs().jobList().stream().anyMatch(jb -> jb.getFilename().equals(job.getFilename()));
            if (!hasJob) {
                INSTANCE.addJob(job);
            }
        }
    }

    public static void clearJobHistory() {
        /*
        Called from the Edit menu, this wipes out the job history that is stored in the users file system
         */
        INSTANCE.getHistory().clear();
    }

    public static void pasteFromClipboard(String text) {
        INSTANCE.setLink(text);
        INSTANCE.processLink();
    }

    /*
    These methods are used to set or clear the TextFields themselves. setDir is public static because it is
    called by class Main.
     */

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

    public void setLinkOutput(@NotNull Color color, String message) {
        if (color.equals(Colors.GREEN) || color.equals(Colors.PURPLE) || color.equals(Colors.HOTPINK)) {
            form.lblLinkOut.getStyleClass().add("outline");
        }
        form.lblLinkOut.setTextFill(color);
        Platform.runLater(() -> {
            form.lblLinkOut.getStyleClass().clear();
            form.lblLinkOut.setText(message);
            if (color.equals(RED) || color.equals(YELLOW)) {
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
            if (color.equals(Colors.GREEN) || color.equals(Colors.PURPLE) || color.equals(Colors.HOTPINK)) {
                form.lblDirOut.getStyleClass().add("outline");
            }
            form.lblDirOut.setTextFill(color);
            form.lblDirOut.setText(message);
            if (color.equals(RED) || color.equals(YELLOW)) {
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
            if (color.equals(Colors.GREEN) || color.equals(Colors.PURPLE) || color.equals(Colors.HOTPINK)) {
                form.lblFilenameOut.getStyleClass().add("outline");
            }
            form.lblFilenameOut.setTextFill(color);
            form.lblFilenameOut.setText(message);
            if (color.equals(RED) || color.equals(YELLOW)) {
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
        if(processingBatch.getValue().equals(false)) {
            Platform.runLater(() -> {
                form.lblDownloadInfo.getStyleClass().clear();
                if (color.equals(Colors.GREEN) || color.equals(Colors.PURPLE) || color.equals(Colors.HOTPINK)) {
                    form.lblDownloadInfo.getStyleClass().add("outline");
                }
                form.lblDownloadInfo.textProperty().unbind();
                form.lblDownloadInfo.setTextFill(color);
                form.lblDownloadInfo.setText(message);
                if (color.equals(RED) || color.equals(YELLOW)) {
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

    /*
    These methods are for general form flow
     */

    private void commitJobListToListView() {
        Platform.runLater(() -> {
            if (getJobs().notNull()) {
                if (getJobs().isEmpty()) {
                    form.listView.getItems().clear();
                }
                else {
                    //Remove duplicate jobs if any
                    Set<String> encounteredLinks = new HashSet<>();
                    ConcurrentLinkedDeque<Job> duplicates = getJobs().jobList().stream()
                            .filter(job -> !encounteredLinks.add(job.getLink()))
                            .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
                    for (Job job : duplicates) {
                        removeJobFromList(job);
                    }
                    //Sort the Job list
                    ArrayList<Job> sortList = new ArrayList<>(getJobs().jobList());
                    sortList.sort(Comparator.comparing(Job::toString));
                    getJobs().setList(new ConcurrentLinkedDeque<>(sortList));
                }
                //Assign the jobList to the ListView
                form.listView.getItems().setAll(getJobs().jobList());
            }
        });
    }

    private void help() {
        double h = 20;
        double n = 16;
        TextFlow tf = new TextFlow();
        tf.getChildren().add(text("Link:\n", true, BLUE, h));
        tf.getChildren().add(text("The Drifty GUI lets you easily create batches for downloading, or download a single file. You start by pasting your web links into the Link field. Once a link has been put into Link field, Drifty will start to process it where it will attempt to determine the name of the file being downloaded. Once it has done that, you will see the filename show up in the batch list on the left.\n\n", false, BLACK, n));
        tf.getChildren().add(text("The URLs you paste into the link field must be valid URLs or Drifty wont process them.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Checking the ", false, BLACK, n));
        tf.getChildren().add(text("Auto Paste ", true, BLACK, n));
        tf.getChildren().add(text("option will let you go to another window and put a link in the clipboard then when you come back to Drifty, the link will be pasted into the Link field automatically and processed then added to the batch list.\n\n", false, BLACK, n));
        tf.getChildren().add(text("If you paste in a link that happens to extract multiple files for downloading, such as a Youtube play list, Drifty will first attempt to get the number of files in the list, then it will ask you if you would like it to obtain all of the filenames in the list. The progress bar will indicate how many filenames have been obtained and the batch list will populate with download jobs with each new filename discovered.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Directory:\n", true, BLUE, h));
        tf.getChildren().add(text("Right clicking anywhere on the form brings up a menu where you can add directories to use as download folders. As you add more directories, they accumulate and persist between reloads. The last directory that you add last will be considered the current download directory. When you click start which begins the download process for all jobs in the batch list, Drifty will look through all of the added folders for a matching filenames and it will let you know when it finds duplicates and give you the option to not download them again.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Right clicking on the form and choosing to edit the directory list pulls up a form with all of the directories you have added. Click on one to remove it if necessary.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Filename:\n", true, BLUE, h));
        tf.getChildren().add(text("Drifty tries to get the name of the file from the link. This process can take a little time but not usually more than 10 to 20 seconds. By default, Drifty adds the extension of 'mp4' to video file downloads because these have the highest chance of success. If Drifty cannot determine the name of the file, you can type in whatever filename you'd like, then click on Save to commit that to the job in the list. You can also determine the download format of the file by setting the filename extension to one of these options: 3gp, aac, flv, m4a, mp3, mp4, ogg, wav, webm.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Job list:\n", true, BLUE, h));
        tf.getChildren().add(text("Once the list box on the left has the jobs in it that you want, you can click on each one in turn and the link, download directory and filename will be placed into the related fields so you can edit them if you need to. Just click on Save when you're done editing.\n\n", false, BLACK, n));
        tf.getChildren().add(text("You can right click on any item in the list to remove it or clear out the job list completely.\n\n", false, BLACK, n));
        tf.getChildren().add(text("The Job list and the directory list will persist between program reloads. The job list will empty out one at a time as each file in the list is downloaded. You start the batch by clicking on the Start button. Any files that fail to download will get recycled back into the list on this form. I found that they will download usually after a second attempt.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Auto Paste:\n", true, BLUE, h));
        tf.getChildren().add(text("The whole point of Auto Paste is to help speed things up. When you check the box, then go out to your browser and find links that you want to download, just copy them into your clip board then ALT+TAB back to Drifty or just click on it to make it the active window. Drifty will sense that it has been made the active screen and the contents of your clipboard will be analyzed to make sure it is a valid URL, then it will process it if so.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Multi-link pasting:\n", true, BLUE, h));
        tf.getChildren().add(text("Another way to speed things up it to copy and paste links into a notepad of some kind, then just put a single space between each link so that all the links are on a single line, then paste that line into the Link field and Drifty will start processing them in turn and build up your batch for you.\n\n", false, BLACK, n));
        tf.getChildren().add(text("Youtube Playlists:\n", true, BLUE, h));
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

    private @NotNull Text text(String string, boolean bold, Color color, double size) {
        // This is used by the help() method for custom text formatting
        Text text = new Text(string);
        text.setFont(new Font(MONACO_TTF.toExternalForm(), size));
        text.setFill(color);
        if (bold) text.setStyle("-fx-font-weight: bold;");
        text.setWrappingWidth(710);
        text.setLineSpacing(.5);
        return text;
    }

    private void getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String lastFolder = AppSettings.get.folders().getDownloadFolder();
        String initFolder = lastFolder.isEmpty() ? System.getProperty("user.home") : lastFolder;
        directoryChooser.setInitialDirectory(new File(initFolder));
        File directory = directoryChooser.showDialog(null);
        if (directory != null) {
            setDir(directory.getAbsolutePath());
        }
    }

    private Jobs getJobs() {
        return AppSettings.get.jobs();
    }

    private JobHistory getHistory() {
        return AppSettings.get.jobHistory();
    }

    private void selectJob(Job job) {
        selectedJob = job;
        updatingBatch.setValue(true);
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}