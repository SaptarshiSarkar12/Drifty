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

public class Set {

    public static final Set INSTANCE = new Set();

    private Set() {
    }

    private final Preferences prefs = LABEL.prefs;

    public void folders(Folders folders) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String value = gson.toJson(folders);
        AppSettings.clear.folders();
        prefs.put(FOLDERS.Name(), value);
    }

    public void mainAutoPaste(boolean value) {
        AppSettings.clear.mainAutoPaste();
        prefs.putBoolean(MAIN_AUTO_PASTE.Name(), value);
    }

    public void batchAutoPaste(boolean value) {
        AppSettings.clear.batchAutoPaste();
        prefs.putBoolean(BATCH_AUTO_PASTE.Name(), value);
    }

    public void updateTimestamp(long value) {
        AppSettings.clear.updateTimestamp();
        prefs.putLong(UPDATE_TIMESTAMP.Name(), value);
    }

    public void lastFolder(String value) {
        AppSettings.clear.lastFolder();
        prefs.put(LAST_FOLDER.Name(), value);
    }

    public void jobs(Jobs jobs) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String value = gson.toJson(jobs);
        AppSettings.clear.jobs();
        Path batchPath = Paths.get(Program.get(Program.BATCH_PATH), JOBS.Name());
        try {
            FileUtils.writeStringToFile(batchPath.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
