package Preferences;

import GUIFX.Support.Folders;
import GUIFX.Support.Jobs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public void devMode(boolean value) {
        AppSettings.clear.devMode();
        prefs.putBoolean(DEVMODE.Name(), value);
    }

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
        prefs.put(JOBS.Name(), value);
    }


}
