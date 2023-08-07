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
public class Get {
    private Get() {}
    private static final Get INSTANCE = new Get();
    private final Preferences prefs = Labels.prefs;
    protected static Get getInstance() {
        return INSTANCE;
    }
    public Folders folders() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Folders folders = new Folders();
        String json = prefs.get(FOLDERS.toString(), "");
        if (!json.isEmpty()) {
            folders = gson.fromJson(json, Folders.class);
        }
        folders.checkFolders();
        return folders;
    }
    public boolean mainAutoPaste() {
        return prefs.getBoolean(MAIN_AUTO_PASTE.toString(), false);
    }
    public boolean batchAutoPaste() {
        return prefs.getBoolean(BATCH_AUTO_PASTE.toString(), false);
    }
    public long lastDLPUpdateTime() {
        return prefs.getLong(LAST_DLP_UPDATE_TIME.toString(), 1000L);
    }
    public String lastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"),"Downloads").toAbsolutePath().toString();
        return prefs.get(LAST_FOLDER.toString(), defaultPath);
    }
    public Jobs jobs() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Jobs jobs = new Jobs();
        Path batchPath = Paths.get(DriftyConfig.getConfig(DriftyConfig.BATCH_PATH), JOBS.toString());
        try {
            String json = FileUtils.readFileToString(batchPath.toFile(), Charset.defaultCharset());
            if (!json.isEmpty()) {
                jobs = gson.fromJson(json, Jobs.class);
            }
            return jobs;
        } catch (IOException ignored) {}
        return null;
    }
    public boolean startMax() {
        return prefs.getBoolean(START_MAX.toString(), false);
    }
    public long startTime() {
        return prefs.getLong(START_TIME.toString(), System.currentTimeMillis());
    }
}
