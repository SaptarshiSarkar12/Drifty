package GUI.Forms;

import Backend.FileDownloader;
import Enums.Colors;
import Enums.Domain;
import Enums.MessageCategory;
import Enums.MessageType;
import GUI.Support.FileExtensions;
import GUI.Support.Folders;
import GUI.Support.Job;
import GUI.Support.Jobs;
import Preferences.AppSettings;
import Utils.CheckFile;
import Utils.Environment;
import Utils.MessageBroker;
import Utils.Utility;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static Enums.Colors.*;
import static GUI.Forms.Constants.*;

public class FormLogic {
    public static final FormLogic INSTANCE = new FormLogic();
    public static MainGridPane form;
    private static final MessageBroker messageBroker = Environment.getMessageBroker();
    private static final BooleanProperty directoryExists = new SimpleBooleanProperty(false);
    private static final BooleanProperty processingBatch = new SimpleBooleanProperty(false);
    private static final BooleanProperty updatingBatch = new SimpleBooleanProperty(false);
    private static final BooleanProperty verifyingLinks = new SimpleBooleanProperty(false);
    private static boolean linkValid = false;
    private final String nl = System.lineSeparator();
    private final FileExtensions fileExtensions;
    private ConcurrentLinkedDeque<Job> jobList;
    private final ConcurrentLinkedDeque<Job> jobHistoryList;
    private Folders folders;
    private Job selectedJob;

    /*
    Single instance model only constructor
     */
    private FormLogic() {
        folders = AppSettings.get.folders();
        jobList = AppSettings.get.jobs().jobList();
        jobHistoryList = AppSettings.get.jobHistory().getJobList();
        fileExtensions = AppSettings.get.getFileExtensions(EXTENSIONS_JSON);
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
        directoryExists.setValue(new File(form.tfDir.getText()).exists());

        BooleanBinding disableStartButton = form.listView.itemsProperty().isNotNull().not().or(processingBatch).or(directoryExists.not()).or(verifyingLinks);
        BooleanBinding disableInputs = processingBatch.or(verifyingLinks);
        BooleanBinding hasText = form.tfLink.textProperty().isEmpty().not().and(form.tfDir.textProperty().isEmpty().not().and(form.tfFilename.textProperty().isEmpty().not()));

        form.btnSave.visibleProperty().bind(updatingBatch);
        form.btnStart.disableProperty().bind(disableStartButton);
        form.tfDir.disableProperty().bind(disableInputs);
        form.tfFilename.disableProperty().bind(disableInputs);
        form.tfLink.disableProperty().bind(disableInputs);

        form.listView.setContextMenu(getListMenu());
        form.tfFilename.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (fileExists(newValue)) {
                    setFilename(renameFile(newValue, form.tfDir.getText()));
                    new Thread(() -> {
                        //This waits for the clearFilenameOutput() that will be executed from the previous setFilename() call.
                        sleep(500);
                        messageBroker.sendMessage("Filename (" + oldValue + ") already exists in folder (" + form.tfDir.getText() + "). Renamed to \"" + newValue + "\" to prevent over-write.", MessageType.ERROR, MessageCategory.FILENAME);
                    }).start();
                }
                else {
                    clearFilenameOutput();
                }
            }
        });

        Tooltip.install(form.cbAutoPaste, new Tooltip("When checked, will paste contents of clipboard into" + nl + "Link field when switching back to this screen."));
        Tooltip.install(form.tfLink, new Tooltip("URL must be a valid URL without spaces." + nl + " Add multiple URLs by pasting them in from the clipboard and separating each URL with a space."));
        Tooltip.install(form.tfFilename, new Tooltip("If the filename you enter already exists in the download folder, it will" + nl + "automatically be renamed to avoid file over-writes."));
        Tooltip.install(form.tfDir, new Tooltip("Right click anywhere to add a new download folder." + nl + "Drifty will accumulate a list of download folders" + nl + "so that duplicate downloads can be detected."));

        form.cbAutoPaste.setSelected(AppSettings.get.mainAutoPaste());
        form.cbAutoPaste.selectedProperty().addListener(((observable, oldValue, newValue) -> AppSettings.set.mainAutoPaste(newValue)));
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
        setDirContextMenu();
    }

    private void setControlActions() {
        form.btnSave.setOnAction(e -> new Thread(() -> {
            String link = form.tfLink.getText();
            String filename = form.tfFilename.getText();
            String dir = form.tfDir.getText();
            if (Paths.get(dir).toFile().exists() && filename.length() > 3) {
                removeJob(selectedJob);
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
                Thread download = new Thread(batchDownloader());
                download.start();
            }

        }).start());
        form.tfDir.setOnAction(e -> updateBatch());
        form.tfFilename.setOnAction(e -> updateBatch());
        form.tfLink.setOnKeyTyped(e -> new Thread(() -> {
            String first = form.tfLink.getText();
            sleep(1000);
            String userText = form.tfLink.getText();
            if (userText.equals(first)) {
                if (userText.contains("  ") || userText.contains(System.lineSeparator())) {
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
        }).start());
        form.listView.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 1) {
                Job job = (Job) form.listView.getSelectionModel().getSelectedItem();
                if (job != null) {
                    updatingBatch.setValue(true);
                    selectedJob = job;
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
                    removeJob(job);
                    clearControls();
                }
            }
        });
    }

    /*
    These methods are the meat of this class. They handle all the various processing that happens when the
    user engages the form and attempts to download links.

    Runnables are used to prevent the form from pin-wheeling (macs) or hour-glassing(others) so that the appearance
    application freeze never happens. Runnables are assigned to Tasks.
     */

    private Runnable batchDownloader() {
        /*
        When the jobList is iterated, the link from a Job is passed through the Domain enum class where the file
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
            checkFiles();
            if (jobList != null && !jobList.isEmpty()) {
                final int totalNumberOfFiles = jobList.size();
                int fileCount = 0;
                for (Job job : jobList) {
                    if (jobList.isEmpty())
                        break;
                    fileCount++;
                    messageBroker.sendMessage("Processing file " + fileCount + " of " + totalNumberOfFiles + ": " + job, MessageType.INFO, MessageCategory.BATCH);
                    if (job.fileExists()) {
                        Platform.runLater(() -> messageBroker.sendMessage(job.getFilename() + " Exists already!", MessageType.ERROR, MessageCategory.FILENAME));
                        removeJob(job);
                        jobHistoryList.addLast(job);
                        sleep(2500);
                        for (double x = 1; x >= 0; x -= .05) {
                            final double opacity = x;
                            Platform.runLater(() -> form.lblDownloadInfo.setOpacity(opacity));
                            sleep(75);
                        }
                        Platform.runLater(() -> form.lblDownloadInfo.setOpacity(1));
                        clearDownloadOutput();
                        continue;
                    }
                    setLink(job.getLink());
                    setDir(job.getDir());
                    setFilename(job.getFilename());
                    Domain domain = Domain.getDomain(job.getLink(), fileExtensions);
                    switch (domain) {
                        case YOUTUBE, INSTAGRAM, BINARY_FILE -> {
                            Task<Integer> task = new DownloadFile(job, domain);
                            Thread thread = new Thread(task);
                            Platform.runLater(() -> {
                                form.lblDownloadInfo.textProperty().bind(((Worker<Integer>) task).messageProperty());
                                form.pBar.progressProperty().bind(((Worker<Integer>) task).progressProperty());
                            });
                            thread.start();
                            while (thread.getState().equals(Thread.State.RUNNABLE)) {
                                sleep(1000);
                            }
                            Platform.runLater(() -> {
                                if (((Worker<Integer>) task).valueProperty().get() == 0) {
                                    removeJob(job);
                                    addJobHistory(job);
                                }
                                else {
                                    Thread lastChance = new Thread(new FileDownloader(job));
                                    lastChance.start();
                                    while (lastChance.getState().equals(Thread.State.RUNNABLE)) {
                                        sleep(500);
                                    }
                                    removeJob(job);
                                    addJobHistory(job);
                                }
                            });
                        }
                        case OTHER -> {
                            Thread download = new Thread(new FileDownloader(job));
                            download.start();
                            while (download.getState().equals(Thread.State.RUNNABLE)) {
                                sleep(100);
                            }
                            removeJob(job);
                            addJobHistory(job);
                        }
                    }
                    sleep(3000);
                }
            }
            clearLink();
            clearFilename();
            clearFilenameOutput();
            processingBatch.setValue(false);
            Platform.runLater(() -> {
                form.pBar.progressProperty().unbind();
                form.pBar.setProgress(0.0);
            });
        };
    }

    private Runnable verifyLink(String presentLink) {
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
            if (presentLink.isEmpty()) {
                return;
            }
            if (!linkInJobList(presentLink)) {
                if (processingBatch.getValue().equals(false) && updatingBatch.getValue().equals(false)) {
                    messageBroker.sendMessage("Validating link...", MessageType.INFO, MessageCategory.LINK);
                    linkValid = Utility.isURLValid(presentLink);
                    if (!linkValid) {
                        messageBroker.sendMessage("Link is not a valid URL! Check for spaces.", MessageType.ERROR, MessageCategory.LINK);
                    }
                    else {
                        Job job = hasHistory(presentLink);
                        if (job != null) {
                            messageBroker.sendMessage("Link exists in past activity: \"" + job.getLink() + "\"", MessageType.WARN, MessageCategory.LINK);
                            AskYesNo ask = new AskYesNo("You have processed this link once before," + nl + "If you continue, and the file exists in the download folder," + nl + "the file(s) will be renamed to avoid over-writing any existing file(s)." + nl + "Do you wish to continue?");
                            if (ask.getResponse().isYes()) {
                                if (job.fileExists()) {
                                    String filename = renameFile(job.getFilename(), job.getDir());
                                    setFilename(filename);
                                    job.setFilename(filename);
                                    job.repeatApproved();
                                }
                                addJob(job);
                                selectedJob = job;
                                Platform.runLater(() -> form.tfFilename.requestFocus());
                            }
                            else {
                                clearLink();
                            }
                            return;
                        }
                        if (Utility.isExtractableLink(presentLink)) {
                            Thread getNames = new Thread(getFilenames(presentLink));
                            getNames.start();
                            while (!getNames.getState().equals(Thread.State.TERMINATED)) {
                                sleep(150);
                            }
                        }
                        else {
                            addJob(new Job(presentLink, form.tfDir.getText()));
                            clearLink();
                        }
                    }
                }
            }
            else {
                messageBroker.sendMessage("Link already in job list", MessageType.ERROR, MessageCategory.LINK);
                clearLink();
            }
        };
    }

    private boolean linkInJobList(String link) {
        for (Job job : jobList) {
            if (job.getLink().equals(link))
                return true;
        }
        return false;
    }

    private void addJob(Job newJob) {
        Job oldJob = null;
        for (Job job : jobList) {
            if (job.matchesLink(newJob)) {
                oldJob = job;
                break;
            }
        }
        if (oldJob != null) {
            jobList.remove(oldJob);
        }
        jobList.add(newJob);
        commitJobListToListView();
    }

    private void addJobHistory(Job newJob) {
        jobHistoryList.addLast(newJob);
        AppSettings.get.jobHistory().setJobList(jobHistoryList);
    }

    private void addDuplicateJob(@NotNull Job job) {
        String link = job.getLink();
        String dir = job.getDir();
        String filename = renameFile(job.getFilename(), job.getDir());
        addJob(new Job(link, dir, filename, true));
    }

    private void removeJob(Job job) {
        jobList.remove(job);
        commitJobListToListView();
        messageBroker.sendMessage("Job Removed: " + job.getLink(), MessageType.INFO, MessageCategory.BATCH);
    }

    private void updateBatch() {
        updatingBatch.setValue(false);
        if (selectedJob != null) {
            Job job = new Job(form.tfLink.getText(), form.tfDir.getText(), form.tfFilename.getText(), selectedJob.repeatOK());
            removeJob(selectedJob);
            addJob(job);
        }
        selectedJob = null;
    }

    private boolean fileExists(String filename) {
        Path downloadFolder = Paths.get(form.tfDir.getText());
        CheckFile checkFile = new CheckFile(downloadFolder, filename);
        Thread thread = new Thread(checkFile);
        thread.start();
        while (thread.getState().equals(Thread.State.RUNNABLE)) {
            sleep(250);
        }
        return checkFile.fileFound();
    }

    private Job hasHistory(String link) {
        if (jobHistoryList.isEmpty())
            return null;
        for (Job job : jobHistoryList) {
            if (job.getLink().equals(link)) {
                return job;
            }
        }
        return null;
    }

    private String renameFile(String filename, String dir) {
        Path path = Paths.get(dir, filename);
        String newFilename = filename;
        int fileNum = -1;
        if (path.toFile().exists()) {
            File folder = new File(dir);
            File[] files = folder.listFiles();
            for (File file : files) {
                String name = file.getName();
                if (name.startsWith(filename)) {
                    if (name.contains("(") && name.contains(")")) {
                        int num = getNumberFromFilename(filename);
                        fileNum = Math.max(num, fileNum);
                    }
                }
            }
        }
        while (path.toFile().exists()) {
            String base = FilenameUtils.getBaseName(filename.replaceAll(" \\(\\d+\\)\\.", "."));
            String ext = "." + FilenameUtils.getExtension(filename);
            fileNum += 1;
            newFilename = base + " (" + fileNum + ")" + ext;
            path = Paths.get(dir, newFilename);
        }
        return newFilename;
    }

    private int getNumberFromFilename(String filename) {
        int number = -1;
        String regex = "(\\()(\\d+)(\\))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(filename);
        if (m.find()) {
            number = Integer.parseInt(m.group(m.groupCount() - 1));
        }
        return number;
    }

    private void checkFiles() {
        //This is only used when the user hits Start, and it checks duplicate downloads even though they were checked during the initial job add process because the
        //job batch persists between reloads, and we can't be sure that there were no changes to the files in the download folders between reloads.
        List<Job> deleteList = new ArrayList<>();
        List<Job> replaceList = new ArrayList<>();
        for (Job job : jobHistoryList) {
            for (Job listJob : jobList) {
                if (job.getLink().equals(listJob.getLink()) && listJob.noRepeat()) {
                    for (String folder : folders.getFolders()) {
                        Path path = Paths.get(folder, listJob.getFilename());
                        if (path.toFile().exists()) {
                            deleteList.add(job);
                            AskYesNo ask = new AskYesNo("You have already downloaded the file : " + nl +
                                                                listJob.getFilename() + nl.repeat(2) +
                                                                "And it exists here : " + nl + folder + nl.repeat(2) +
                                                                "This job will download the file into this folder:" + nl + listJob.getDir() + nl.repeat(2) +
                                                                "If the folders are the same, it will be given a slightly different filename because" + nl + "Drifty will not over-write an existing file." + nl.repeat(2) +
                                                                "Do you want to download it again?");
                            if (ask.getResponse().isYes()) {
                                String filename = renameFile(job.getFilename(), job.getDir());
                                setFilename(filename);
                                replaceList.add(new Job(job.getLink(), job.getDir(), filename, true));
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (Job job : deleteList) {
            removeJob(job);
        }
        for (Job job : replaceList) {
            addJob(job);
        }
        commitJobListToListView();
    }

    private Runnable getFilenames(String link) {
        return () -> {
            //Using a Worker Task, this method gets the filename(s) from the link.
            Task<ConcurrentLinkedDeque<Job>> task = new GetFilename(link, form.tfDir.getText());
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

    private void checkHistoryAddJobs(Worker<ConcurrentLinkedDeque<Job>> worker) {
        String pastJob = "You have downloaded %s in the past. " + nl.repeat(2) + "If you still wish to download this file again, it will be given a unique name so that the existing file is not over-written" + nl.repeat(2) + "Do you still want to download this file?";
        String fileExists = "This file:" + nl.repeat(2) + "%s" + nl.repeat(2) + "Exists in folder:" + nl.repeat(2) + "%s" + nl.repeat(2) + "If you download it again, it will be given a slightly different name to avoid over-writing the existing file." + nl.repeat(2) + "Do you wish to download it again?";
        Platform.runLater(() -> {
            if (worker.valueProperty().get() != null) {
                for (Job job : worker.valueProperty().get()) {
                    if (!jobList.contains(job)) {
                        if (jobHistoryMatch(job)) {
                            AskYesNo ask = new AskYesNo(String.format(pastJob, job.getFilename()));
                            if (ask.getResponse().isYes()) {
                                addDuplicateJob(job);
                            }
                        }
                        else if (jobFileExists(job)) {
                            AskYesNo ask = new AskYesNo(String.format(fileExists, job.getFilename(), job.getDir()));
                            if (ask.getResponse().isYes()) {
                                addDuplicateJob(job);
                            }
                        }
                        else {
                            addJob(job);
                        }
                    }
                }
            }
        });
    }

    public boolean jobFileExists(Job newJob) {
        for (Job job : jobHistoryList) {
            if (job.getLink().equals(newJob.getLink()) && job.getFilename().equals(newJob.getFilename())) {
                for (String folder : AppSettings.get.folders().getFolders()) {
                    Path downloadPath = Paths.get(folder);
                    CheckFile checkFile = new CheckFile(downloadPath, job.getFilename());
                    Thread thread = new Thread(checkFile);
                    thread.start();
                    while (thread.getState().equals(Thread.State.RUNNABLE)) {
                        sleep(100);
                    }
                    return checkFile.fileFound();
                }
            }
        }
        return false;
    }

    private boolean jobHistoryMatch(Job newJob) {
        for (Job job : jobHistoryList) {
            if (job.matches(newJob))
                return true;
        }
        return false;
    }

    private void delayFolderSave(String folderString, File folder) {
        /*
        If the user is typing a file path into the field, we don't want to save every folder 'hit' so we wait 3 seconds
        and if the String is still the same value, then we commit the folder to the list.
        */
        new Thread(() -> {
            sleep(3000);
            if (form.tfDir.getText().equals(folderString)) {
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
                removeJob(job);
                clearControls();
            }
        });
        miClear.setOnAction(e -> {
            jobList.clear();
            commitJobListToListView();
            clearLink();
            clearFilename();
            form.listView.getItems().clear();
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.LINK);
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.FILENAME);
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.DIRECTORY);
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

    public static void bindToProgressbar(DoubleProperty progress) {
        form.pBar.progressProperty().unbind();
        form.pBar.progressProperty().bind(progress);
    }

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
            boolean hasJob = INSTANCE.jobList.stream().anyMatch(jb -> jb.getFilename().equals(job.getFilename()));
            if (!hasJob) {
                INSTANCE.addJob(job);
            }
        }
    }

    public static void clearJobHistory() {
        /*
        Called from the Edit menu, this wipes out the job history that is stored in the users file system
         */
        AppSettings.clear.jobHistory();
        INSTANCE.jobHistoryList.clear();
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
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.DOWNLOAD);
            messageBroker.sendMessage("", MessageType.INFO, MessageCategory.FILENAME);
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

    private void clearDownloadOutput() {
        setDownloadOutput(GREEN, "");
    }

    private void clearControls() {
        clearLink();
        clearFilename();
        messageBroker.sendMessage("", MessageType.INFO, MessageCategory.LINK);
        messageBroker.sendMessage("", MessageType.INFO, MessageCategory.DIRECTORY);
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
                            removeJob(job);
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

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


