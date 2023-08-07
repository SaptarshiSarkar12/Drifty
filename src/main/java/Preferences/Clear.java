package Preferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import static Preferences.Labels.*;
public class Clear {
    private Clear() {}
    public static final Clear INSTANCE = new Clear();
    private final Preferences prefs = Labels.prefs;
    protected static Clear getInstance() {
        return INSTANCE;
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
    public void lastDLPUpdateTime() {
        prefs.remove(LAST_DLP_UPDATE_TIME.toString());
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
    public void startTime() {
        prefs.remove(START_TIME.toString());
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
