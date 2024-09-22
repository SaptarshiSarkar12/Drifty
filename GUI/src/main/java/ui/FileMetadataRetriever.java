package ui;

import gui.support.GUIDownloadConfiguration;
import javafx.application.Platform;
import javafx.concurrent.Task;
import utils.Utility;

import java.util.Timer;
import java.util.TimerTask;

import static gui.support.Colors.GREEN;

public class FileMetadataRetriever extends Task<Void> {
    private final GUIDownloadConfiguration config;
    // Progress bar direction
    boolean dirUp = true;

    public FileMetadataRetriever(GUIDownloadConfiguration config) {
        this.config = config;
    }

    @Override
    protected Void call() {
        updateProgress(0, 1);
        this.updateMessage("Retrieving Filename(s)");
        Timer progTimer = new Timer();
        progTimer.scheduleAtFixedRate(runProgress(), 0, 150);
        Thread prepareFileData = new Thread(config::prepareFileData);
        prepareFileData.start();
        while (config.getFileCount() == 0 && config.getStatusCode() == 0) {
            Utility.sleep(100);
        }
        int fileCount = config.getFileCount();
        while (prepareFileData.isAlive()) {
            int filesProcessed = config.getFilesProcessed();
            updateProgress(filesProcessed, fileCount);
        }
        UIController.setDownloadInfoColor(GREEN);
        updateMessage("File(s) added to batch.");
        progTimer.cancel();
        updateProgress(0, 1);
        return null;
    }

    private TimerTask runProgress() {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    double value = getProgress();
                    dirUp = (value >= 1.0 || value <= 0.0) != dirUp;
                    value += dirUp ? 0.01 : -0.01;
                    updateProgress(value, 1.0);
                });
            }
        };
    }
}
