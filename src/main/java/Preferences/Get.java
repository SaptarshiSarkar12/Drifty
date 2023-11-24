package Preferences;

import Enums.Program;
import GUI.Support.Folders;
import GUI.Support.JobHistory;
import GUI.Support.Jobs;
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

public final class Get { // This class is used to get the user preferences
    private static final Get INSTANCE = new Get();
    private final Preferences preferences = Labels.PREFERENCES;

    private Get() {
    }

    static Get getInstance() {
        return INSTANCE;
    }

    public Folders folders() {
        GsonBuilder gsonBuilder = new GsonBuilder();
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

    public long lastDLPUpdateTime() {
        return preferences.getLong(LAST_YT_DLP_UPDATE_TIME.toString(), 1000L);
    }

    public String lastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString();
        return preferences.get(LAST_FOLDER.toString(), defaultPath);
    }

    public Jobs jobs() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        Jobs jobs;
        Path jobBatchFile = Paths.get(Program.get(JOB_FILE));
        try {
            String json = FileUtils.readFileToString(jobBatchFile.toFile(), Charset.defaultCharset());
            if (json != null && !json.isEmpty()) {
                jobs = gson.fromJson(json, Jobs.class);
                return jobs;
            }
        } catch (IOException ignored) {
        }
        return new Jobs();
    }

    public JobHistory jobHistory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        JobHistory jobHistory;
        Path jobHistoryFile = Paths.get(Program.get(JOB_HISTORY_FILE));
        try {
            if (!jobHistoryFile.toFile().exists()) {
                jobHistory = new JobHistory();
                String json = gson.toJson(jobHistory);
                FileUtils.write(jobHistoryFile.toFile(), json, Charset.defaultCharset());
            }
            String json = FileUtils.readFileToString(jobHistoryFile.toFile(), Charset.defaultCharset());
            if (json != null && !json.isEmpty()) {
                jobHistory = gson.fromJson(json, JobHistory.class);
                return jobHistory;
            }
        } catch (IOException ignored) {
        }
        return new JobHistory();
    }

    public boolean alwaysAutoPaste() {
        return preferences.getBoolean(ALWAYS_AUTO_PASTE.toString(), false);
    }

    public String ytDlpVersion() {
        return preferences.get(YT_DLP_VERSION.toString(), "");
    }

    public String spotDLVersion() {
        return preferences.get(SPOTDL_VERSION.toString(), "");
    }
}
