package Preferences;

import Enums.Program;
import GUI.Support.*;
import Utils.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hildan.fxgson.FxGson;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import static Preferences.Labels.*;

public class Get { // This class is used to get the user preferences
    private final Preferences preferences = Labels.PREFERENCES;
    private static final Get INSTANCE = new Get();

    private Get() {}

    protected static Get getInstance() {
        return INSTANCE;
    }

    public Folders folders() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Folders.class, new FoldersTypeAdapter());
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        Folders folders = new Folders();
        String json = preferences.get(FOLDERS.toString(), "");
        if (!json.isEmpty()) {
            folders = gson.fromJson(json, Folders.class);
        }
        folders.checkFolders();
        return folders;
    }

    public boolean mainAutoPaste() {
        return preferences.getBoolean(MAIN_AUTO_PASTE.toString(), false);
    }

    public boolean batchAutoPaste() {
        return preferences.getBoolean(BATCH_AUTO_PASTE.toString(), false);
    }

    public long lastYt_dlpUpdateTime() {
        return preferences.getLong(LAST_YT_DLP_UPDATE_TIME.toString(), 1000L);
    }

    public String lastDownloadFolder() {
        String defaultPath = Paths.get(Utility.getFormattedDefaultDownloadsFolder()).toAbsolutePath().toString();
        return preferences.get(LAST_FOLDER.toString(), defaultPath);
    }

    public Jobs jobs() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Jobs.class, new JobsTypeAdapter());
        gsonBuilder.registerTypeAdapter(Job.class, new JobTypeAdapter());
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        Jobs jobs;
        Path jobBatchFile = Paths.get(Program.get(Program.DATA_PATH), JOBS.toString());
        try {
            String json = FileUtils.readFileToString(jobBatchFile.toFile(), Charset.defaultCharset());
            if (json != null && !json.isEmpty()) {
                jobs = gson.fromJson(json, Jobs.class);
                return jobs;
            }
        } catch (IOException ignored) {}
        return new Jobs();
    }

    public boolean startMax() {
        return preferences.getBoolean(START_MAX.toString(), false);
    }

    public long startTime() {
        return preferences.getLong(START_TIME.toString(), System.currentTimeMillis());
    }

    public JobHistory jobHistory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Jobs.class, new JobsTypeAdapter());
        gsonBuilder.registerTypeAdapter(Job.class, new JobTypeAdapter());
        gsonBuilder.registerTypeAdapter(JobHistory.class, new JobHistoryTypeAdapter());
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        JobHistory jobHistory;
        Path jobHistoryFile = Paths.get(Program.get(Program.DATA_PATH), JOB_HISTORY.toString());
        try {
            String json = FileUtils.readFileToString(jobHistoryFile.toFile(), Charset.defaultCharset());
            if (json != null && !json.isEmpty()) {
                jobHistory = gson.fromJson(json, JobHistory.class);
                return jobHistory;
            }
        } catch (IOException ignored) {}
        return new JobHistory();
    }
}
