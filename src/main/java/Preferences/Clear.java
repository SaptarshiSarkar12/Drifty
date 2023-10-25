package Preferences;

import java.util.prefs.Preferences;

import static Preferences.Labels.*;

public final class Clear { // This class is used to clear the user preferences
    private static final Clear INSTANCE = new Clear();
    private final Preferences preferences = Labels.PREFERENCES;

    private Clear() {}

    static Clear getInstance() {
        return INSTANCE;
    }

    public void folders() {
        preferences.remove(FOLDERS.toString());
    }

    public void autoPaste() {
        preferences.remove(AUTO_PASTE.toString());
    }

    public void lastDLPUpdateTime() {
        preferences.remove(LAST_YTDLP_UPDATE_TIME.toString());
    }

    public void lastDriftyUpdateTime() {
        preferences.remove(LAST_DRIFTY_UPDATE_TIME.toString());
    }

    public void lastFolder() {
        preferences.remove(LAST_FOLDER.toString());
    }

    public void jobs() {
        preferences.remove(JOBS.toString());
    }

    public void menuBarAsSystem() {
        preferences.remove(MENU_BAR_SYSTEM.toString());
    }

    public void alwaysAutoPaste() {
        preferences.remove(ALWAYS_AUTO_PASTE.toString());
    }
}
