package Preferences;

import Enums.MessageCategory;
import Enums.MessageType;
import Utils.MessageBroker;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static Preferences.Labels.*;

public class Clear { // This class is used to clear the user preferences
    MessageBroker messageBroker = new MessageBroker();
    public static final Clear INSTANCE = new Clear();
    private final Preferences preferences = Labels.PREFERENCES;

    private Clear() {}

    protected static Clear getInstance() {
        return INSTANCE;
    }

    public void folders() {
        preferences.remove(FOLDERS.toString());
    }

    public void mainAutoPaste() {
        preferences.remove(MAIN_AUTO_PASTE.toString());
    }

    public void batchAutoPaste() {
        preferences.remove(BATCH_AUTO_PASTE.toString());
    }

    public void lastDLPUpdateTime() {
        preferences.remove(LAST_DLP_UPDATE_TIME.toString());
    }

    public void lastFolder() {
        preferences.remove(LAST_FOLDER.toString());
    }

    public void jobs() {
        preferences.remove(JOBS.toString());
    }

    public void startMax() {
        preferences.remove(START_MAX.toString());
    }

    public void startTime() {
        preferences.remove(START_TIME.toString());
    }

    public void jobHistory() {
        preferences.remove(JOB_HISTORY.toString());
    }

    public void clearAll() {
        try {
            preferences.clear();
        } catch (BackingStoreException e) {
            messageBroker.sendMessage("Failed to clear all the preferences! " + e.getMessage(), MessageType.ERROR, MessageCategory.LOG);
        }
    }
}
