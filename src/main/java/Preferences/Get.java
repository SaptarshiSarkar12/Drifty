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

public class Get { // This class is used to get the user preferences
    private final Preferences preferences = Labels.PREFERENCES;
    private static final Get INSTANCE = new Get();

    private Get() {}

    protected static Get getInstance() {
        return INSTANCE;
    }

    public Folders folders() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

    public long lastDLPUpdateTime() {
        return preferences.getLong(LAST_DLP_UPDATE_TIME.toString(), 1000L);
    }

    public String lastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString();
        return preferences.get(LAST_FOLDER.toString(), defaultPath);
    }

    public Jobs jobs() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Jobs jobs = new Jobs();
        Path batchPath = Paths.get(Program.get(Program.BATCH_PATH), JOBS.toString());
        try {
            String json = FileUtils.readFileToString(batchPath.toFile(), Charset.defaultCharset());
            if (!json.isEmpty()) {
                jobs = gson.fromJson(json, Jobs.class);
            }
            if (jobs == null) {
                return new Jobs();
            }
            return jobs;
        } catch (IOException ignored) {}
        return new Jobs();
    }

    public boolean startMax() {
        return preferences.getBoolean(START_MAX.toString(), false);
    }

    public long startTime() {
        return preferences.getLong(START_TIME.toString(), System.currentTimeMillis());
    }
}
