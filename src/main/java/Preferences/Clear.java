package Preferences;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static Preferences.LABEL.*;

/**
 * See the AppSettings class for an explanation of this class
 */

public class Clear {

    public static final Clear INSTANCE = new Clear();

    private Clear() {
    }

    private final Preferences prefs = LABEL.prefs;

    public void folders() {
        prefs.remove(FOLDERS.Name());
    }

    public void mainAutoPaste() {
        prefs.remove(MAIN_AUTO_PASTE.Name());
    }

    public void batchAutoPaste() {
        prefs.remove(BATCH_AUTO_PASTE.Name());
    }

    public void updateTimestamp() {
        prefs.remove(UPDATE_TIMESTAMP.Name());
    }

    public void lastFolder() {
        prefs.remove(LAST_FOLDER.Name());
    }

    public void jobs() {
        prefs.remove(JOBS.Name());
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
