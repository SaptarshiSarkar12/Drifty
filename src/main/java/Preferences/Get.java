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

import static Preferences.LABEL.*;

/**
 * See the AppSettings class for an explanation of this class
 */

public class Get {

    public static final Get INSTANCE = new Get();

    private Get() {
    }

    private final Preferences prefs = LABEL.prefs;

    public Folders folders() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Folders folders = new Folders();
        String json = prefs.get(FOLDERS.Name(), "");
        if (!json.isEmpty()) {
            folders = gson.fromJson(json, Folders.class);
        }
        folders.checkFolders();
        return folders;
    }

    public boolean mainAutoPaste() {
        return prefs.getBoolean(MAIN_AUTO_PASTE.Name(), false);
    }

    public boolean batchAutoPaste() {
        return prefs.getBoolean(BATCH_AUTO_PASTE.Name(), false);
    }

    public long updateTimestamp() {
        return prefs.getLong(UPDATE_TIMESTAMP.Name(), 1000L);
    }

    public String lastFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"),"Downloads").toAbsolutePath().toString();
        return prefs.get(LAST_FOLDER.Name(), defaultPath);
    }

    public Jobs jobs() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Jobs jobs = new Jobs();
        Path batchPath = Paths.get(Program.get(Program.BATCH_PATH), JOBS.Name());
        try {
            String json = FileUtils.readFileToString(batchPath.toFile(), Charset.defaultCharset());
            if (!json.isEmpty()) {
                jobs = gson.fromJson(json, Jobs.class);
            }
            return jobs;
        } catch (IOException ignored) {}
        return null;
    }
}
