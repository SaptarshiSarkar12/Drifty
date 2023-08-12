package Preferences;

import Enums.Program;
import GUI.Support.Folders;
import GUI.Support.Jobs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import static Preferences.Labels.*;

public class Set { // This class is used to set the user preferences
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = Labels.PREFERENCES;

    private Set() {}

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void folders(Folders folders) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String value = gson.toJson(folders);
        AppSettings.clear.folders();
        preferences.put(FOLDERS.toString(), value);
    }

    public void mainAutoPaste(boolean isMainAutoPasteEnabled) {
        AppSettings.clear.mainAutoPaste();
        preferences.putBoolean(MAIN_AUTO_PASTE.toString(), isMainAutoPasteEnabled);
    }

    public void batchAutoPaste(boolean isBatchAutoPasteEnabled) {
        AppSettings.clear.batchAutoPaste();
        preferences.putBoolean(BATCH_AUTO_PASTE.toString(), isBatchAutoPasteEnabled);
    }

    public void lastYt_DlpUpdateTime(long lastYt_DlpUpdateTime) {
        AppSettings.clear.lastDLPUpdateTime();
        preferences.putLong(LAST_DLP_UPDATE_TIME.toString(), lastYt_DlpUpdateTime);
    }

    public void lastFolder(String lastFolderPath) {
        AppSettings.clear.lastFolder();
        preferences.put(LAST_FOLDER.toString(), lastFolderPath);
    }

    public void batchDownloadJobs(Jobs jobs) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String value = gson.toJson(jobs);
        AppSettings.clear.jobs();
        Path batchPath = Paths.get(Program.get(Program.BATCH_PATH), JOBS.toString());
        try {
            FileUtils.writeStringToFile(batchPath.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void startMax(boolean value) {
        AppSettings.clear.startMax();
        preferences.putBoolean(START_MAX.toString(), value);
    }

    public void startTime() {
        AppSettings.clear.startTime();
        preferences.putLong(START_TIME.toString(), System.currentTimeMillis());
    }
}