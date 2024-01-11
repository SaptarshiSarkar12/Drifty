package ui;

import gui.init.Environment;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import properties.Program;
import support.Job;
import utils.Utility;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gui.support.Colors.GREEN;
import static properties.Program.YT_DLP;
import static utils.Utility.isSpotify;
import static utils.Utility.sleep;

public class GetFilename extends Task<ConcurrentLinkedDeque<Job>> {
    private final String link;
    private final String dir;
    private final String regex = "(\\[download] Downloading item \\d+ of )(\\d+)";
    private final Pattern pattern = Pattern.compile(regex);
    private final String lineFeed = System.lineSeparator();
    private int result = -1;
    private int fileCount;
    private int filesProcessed;
    private final ConcurrentLinkedDeque<Job> jobList = new ConcurrentLinkedDeque<>();
    private final StringProperty feedback = new SimpleStringProperty();
    boolean dirUp = true;

    public GetFilename(String link, String dir) {
        this.link = link;
        this.dir = dir;
    }

    @Override
    protected ConcurrentLinkedDeque<Job> call() {
        updateProgress(0, 1);
        this.updateMessage("Retrieving Filename(s)");
        Timer progTimer = new Timer();
        progTimer.scheduleAtFixedRate(runProgress(), 2500, 150);
        Thread thread = new Thread(getFileCount());
        result = -1;
        thread.start();
        while (result == -1) {
            sleep(500);
        }
        boolean proceed = true;
        if (fileCount > 0) {
            progTimer.cancel();
            updateProgress(0, 1);
            progTimer = null;
            ConfirmationDialog ask = new ConfirmationDialog("Confirmation", "There are " + fileCount + " files in this list. Proceed and get all filenames?");
            proceed = ask.getResponse().isYes();
        }
        if (proceed) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(getJson(), 1000, 150);
            Utility.getLinkMetadata(link);
            timer.cancel();
            sleep(500); //give timerTask enough time to do its last run
            jobList.clear();
            UIController.setDownloadInfoColor(GREEN);
            updateMessage("File(s) added to batch.");
            if (progTimer != null) {
                progTimer.cancel();
            }
            updateProgress(0, 1);
        }
        return jobList;
    }

    private TimerTask getJson() {
        File appFolder = Program.getJsonDataPath().toFile();
        return new TimerTask() {
            @Override
            public void run() {
                ConcurrentLinkedDeque<File> deleteList = new ConcurrentLinkedDeque<>();
                ConcurrentLinkedDeque<Job> jobList = new ConcurrentLinkedDeque<>();
                File[] files = appFolder.listFiles();
                for (File file : Objects.requireNonNull(files)) {
                    try {
                        String ext = FilenameUtils.getExtension(file.getAbsolutePath());
                        if ("json".equalsIgnoreCase(ext) || "spotdl".equalsIgnoreCase(ext)) {
                            String jsonString = FileUtils.readFileToString(file, Charset.defaultCharset());
                            String filename;
                            if (isSpotify(link)) {
                                filename = Utility.extractSpotifyFilename(jsonString);
                                String baseName = FilenameUtils.getBaseName(filename);
                                filename = baseName + ".mp3";
                            } else {
                                filename = Utility.getFilenameFromJson(jsonString);
                                String baseName = FilenameUtils.getBaseName(filename);
                                filename = baseName + ".mp4";
                            }
                            updateMessage("Found file: " + filename);
                            jobList.addLast(new Job(link, dir, filename, false));
                            if (fileCount > 1) {
                                filesProcessed++;
                                updateProgress(filesProcessed, fileCount);
                            }
                            UIController.addJob(jobList);
                            deleteList.addLast(file);
                        }
                    } catch (IOException e) {
                        Environment.getMessageBroker().msgFilenameError("Failed to get filename(s) from link: " + link);
                        Environment.getMessageBroker().msgLogError(e.getMessage());
                    }
                }
                for (File file : deleteList) {
                    try {
                        if (file.exists()) {
                            FileUtils.forceDelete(file);
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        };
    }


    private TimerTask runProgress() {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    double value = getProgress();
                    value = dirUp ? value + .01 : value - .01;
                    if (value > 1.0) {
                        dirUp = !dirUp;
                        value = 1.0;
                    }
                    if (value < 0) {
                        dirUp = !dirUp;
                        value = 0.0;
                    }
                    updateProgress(value, 1.0);
                });
            }
        };
    }

    private Runnable getFileCount() {
        return () -> {
            feedback.addListener(((observable, oldValue, newValue) -> {
                String[] list = newValue.split(lineFeed);
                if (list.length > 3) {
                    for (String line : list) {
                        Matcher m = pattern.matcher(line);
                        if (m.find()) {
                            fileCount = Integer.parseInt(m.group(2));
                            break;
                        }
                    }
                }
            }));
            try {
                String command = Program.get(YT_DLP);
                String[] args = new String[]{command, "--flat-playlist", "--skip-download", "-P", dir, link};
                ProcessBuilder pb = new ProcessBuilder(args);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                StringJoiner joiner = new StringJoiner(lineFeed);
                try {
                    try (
                        InputStream inputStream = process.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
                            )
                    {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (this.isCancelled()) {
                                break;
                            }
                            joiner.add(line);
                            feedback.setValue(joiner.toString());
                        }
                    }
                } catch (IOException e) {
                    Environment.getMessageBroker().msgFilenameError("Failed to get filename(s) from link: " + link);
                }
                result = process.waitFor();
            } catch (IOException | InterruptedException e) {
                Environment.getMessageBroker().msgFilenameError("Failed to get filename(s) from link: " + link);
            }
        };
    }
}
