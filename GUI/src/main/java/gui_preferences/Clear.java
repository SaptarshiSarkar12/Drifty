package gui_preferences;

import java.util.prefs.Preferences;

import static gui_preferences.Labels.*;

public class Clear extends preferences.Clear {
    private static final Clear INSTANCE = new Clear();
    private final Preferences preferences = Labels.PREFERENCES;

    static Clear getInstance() {
        return INSTANCE;
    }

    public void folders() {
        preferences.remove(FOLDERS.toString());
    }

    public void mainAutoPaste() {
        preferences.remove(MAIN_AUTO_PASTE.toString());
    }

    public void jobs() {
        preferences.remove(JOBS.toString());
    }
}
