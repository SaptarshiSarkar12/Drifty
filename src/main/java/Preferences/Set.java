package Preferences;

import Enums.DriftyConfig;
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

public class Set {
    private Set() {}

    private static final Set INSTANCE = new Set();
    private final Preferences prefs = Labels.prefs;

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void folders(Folders folders) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String value = gson.toJson(folders);
        AppSettings.clear.folders();
        prefs.put(FOLDERS.toString(), value);
    }

    public void mainAutoPaste(boolean isMainAutoPasteEnabled) {
        AppSettings.clear.mainAutoPaste();
        prefs.putBoolean(MAIN_AUTO_PASTE.toString(), isMainAutoPasteEnabled);
    }

    public void batchAutoPaste(boolean isBatchAutoPasteEnabled) {
        AppSettings.clear.batchAutoPaste();
        prefs.putBoolean(BATCH_AUTO_PASTE.toString(), isBatchAutoPasteEnabled);
    }

    public void lastDLPUpdateTime(long lastYt_DlpUpdateTime) {
        AppSettings.clear.lastDLPUpdateTime();
        prefs.putLong(LAST_DLP_UPDATE_TIME.toString(), lastYt_DlpUpdateTime);
    }

    public void lastFolder(String lastFolderPath) {
        AppSettings.clear.lastFolder();
        prefs.put(LAST_FOLDER.toString(), lastFolderPath);
    }

    public void jobs(Jobs jobs) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String value = gson.toJson(jobs);
        AppSettings.clear.jobs();
        Path batchPath = Paths.get(DriftyConfig.getConfig(DriftyConfig.BATCH_PATH), JOBS.toString());
        try {
            FileUtils.writeStringToFile(batchPath.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startMax(boolean value) {
        AppSettings.clear.startMax();
        prefs.putBoolean(START_MAX.toString(), value);
    }

    public void startTime() {
        AppSettings.clear.startTime();
        prefs.putLong(START_TIME.toString(), System.currentTimeMillis());
    }
}
