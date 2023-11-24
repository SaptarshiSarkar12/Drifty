package Preferences;

import Enums.Program;
import GUI.Support.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.hildan.fxgson.FxGson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import static Enums.Program.JOB_FILE;
import static Enums.Program.JOB_HISTORY_FILE;
import static Preferences.Labels.*;

public class Set { // This class is used to set the user preferences
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = Labels.PREFERENCES;

    public Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void folders(Folders folders) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String value = gson.toJson(folders);
        AppSettings.CLEAR.folders();
        preferences.put(FOLDERS.toString(), value);
    }

    public void mainAutoPaste(boolean isMainAutoPasteEnabled) {
        AppSettings.CLEAR.mainAutoPaste();
        preferences.putBoolean(MAIN_AUTO_PASTE.toString(), isMainAutoPasteEnabled);
    }

    public void lastDLPUpdateTime(long value) {
        AppSettings.CLEAR.lastDLPUpdateTime();
        preferences.putLong(LAST_YT_DLP_UPDATE_TIME.toString(), value);
    }

    public void lastFolder(String lastFolderPath) {
        AppSettings.CLEAR.lastFolder();
        preferences.put(LAST_FOLDER.toString(), lastFolderPath);
    }

    public void jobs(Jobs jobs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String value = gson.toJson(jobs);
        AppSettings.CLEAR.jobs();
        Path jobBatchFile = Paths.get(Program.get(JOB_FILE));
        try {
            FileUtils.writeStringToFile(jobBatchFile.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void jobHistory(JobHistory jobHistory) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String value = gson.toJson(jobHistory);
        Path jobHistoryFile = Paths.get(Program.get(JOB_HISTORY_FILE));
        try {
            FileUtils.writeStringToFile(jobHistoryFile.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ytDlpVersion(String version) {
        AppSettings.CLEAR.ytDlpVersion();
        preferences.put(YT_DLP_VERSION.toString(), version);
    }

    public void spotDLVersion(String version) {
        AppSettings.CLEAR.spotDLVersion();
        preferences.put(SPOTDL_VERSION.toString(), version);
    }
}
