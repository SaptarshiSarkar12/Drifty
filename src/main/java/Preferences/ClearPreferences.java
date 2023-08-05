package Preferences;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static Preferences.Labels.*;

/**
 * This class is used to clear the user preferences for Drifty
 */
public class ClearPreferences {
    /**
     * The instance / object of this class through which the preferences are cleared
     */
    public static final ClearPreferences INSTANCE = new ClearPreferences();
    private final Preferences prefs = Labels.prefs;

    /**
     * The default constructor has been made private so that no object of this class can be created, by external classes
     */
    private ClearPreferences() {}

    /**
     * This method is used to return the object of this class
     * @return the object of this class
     */
    protected static ClearPreferences getInstance() {
        return INSTANCE;
    }

    public void devMode() {
        prefs.remove(DEVMODE.toString());
    }

    public void folders() {
        prefs.remove(FOLDERS.toString());
    }

    public void mainAutoPaste() {
        prefs.remove(MAIN_AUTO_PASTE.toString());
    }

    public void batchAutoPaste() {
        prefs.remove(BATCH_AUTO_PASTE.toString());
    }

    public void updateTimestamp() {
        prefs.remove(LAST_UPDATE_TIME.toString());
    }

    public void lastFolder() {
        prefs.remove(LAST_FOLDER.toString());
    }

    public void jobs() {
        prefs.remove(JOBS.toString());
    }

    public void startMax() {
        prefs.remove(START_MAX.toString());
    }

    public void clearAll() {
        try {
            prefs.clear();
        }
        catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }
}
