package gui.preferences;

import java.util.prefs.Preferences;

import static gui.preferences.Labels.*;

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
        preferences.remove(GUI_AUTO_PASTE.toString());
    }

    public void mainTheme() {
        preferences.remove(GUI_THEME.toString());
    }
}
