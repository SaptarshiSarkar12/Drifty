package ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gui.init.Environment;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import properties.Program;
import support.Job;
import utils.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;

import static gui.support.Colors.GREEN;
import static utils.Utility.*;

public class GetFilename extends Task<ConcurrentLinkedDeque<Job>> {
    private final String link;
    private final String dir;
    private int fileCount;
    private int filesProcessed;
    private final ConcurrentLinkedDeque<Job> jobList = new ConcurrentLinkedDeque<>();
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
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(getJson(), 100, 1);
        if (isSpotify(link)) {
            Utility.getSpotifySongMetadata(link);
        } else {
            Utility.getYtDlpMetadata(link);
        }
        sleep(1000); // give timerTask enough time to do its last run
        timer.cancel();
        jobList.clear();
        UIController.setDownloadInfoColor(GREEN);
        updateMessage("File(s) added to batch.");
        progTimer.cancel();
        updateProgress(0, 1);
        return jobList;
    }

    private TimerTask getJson() {
        File appFolder = Program.getJsonDataPath().toFile();
        return new TimerTask() {
            @Override
            public void run() {
                ConcurrentLinkedDeque<Job> jobList = new ConcurrentLinkedDeque<>();
                ConcurrentLinkedDeque<File> deleteList = new ConcurrentLinkedDeque<>();
                File[] files = appFolder.listFiles();
                if (files == null) {
                    return;
                }
                for (File file : files) {
                    try {
                        if ("yt-metadata.info.json".equals(file.getName())) {
                            String jsonString = Files.readString(file.toPath());
                            if (isYoutube(link) && link.contains("playlist")) {
                                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                                JsonArray entries = jsonObject.get("entries").getAsJsonArray();
                                fileCount = entries.size();
                                for (JsonElement entry : entries) {
                                    JsonObject entryObject = entry.getAsJsonObject();
                                    String videoLink = entryObject.get("url").getAsString();
                                    String filename = Utility.cleanFilename(entryObject.get("title").getAsString()).concat(".mp4");
                                    updateMessage("Filename detected: " + filename);
                                    jobList.addLast(new Job(videoLink, dir, filename, false));
                                }
                            } else {
                                String filename = Utility.getFilenameFromJson(jsonString);
                                updateMessage("Filename detected: " + filename);
                                jobList.addLast(new Job(link, dir, filename, false));
                            }
                        } else if ("spotify-metadata.json".equals(file.getName())) {
                            String jsonString = FileUtils.readFileToString(file, Charset.defaultCharset());
                            String filename = Utility.getSpotifyFilename(jsonString);
                            updateMessage("Filename detected: " + filename);
                            jobList.addLast(new Job(link, dir, filename, jsonString, false));
                        } else {
                            continue;
                        }
                        if (fileCount > 1) {
                            filesProcessed++;
                            updateProgress(filesProcessed, fileCount);
                        }
                        UIController.addJob(jobList);
                        deleteList.addLast(file);
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
}
