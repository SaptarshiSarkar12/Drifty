package gui.preferences;

import java.util.prefs.Preferences;

import static gui.preferences.Labels.*;

public final class Set extends preferences.Set {
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = Labels.PREFERENCES;

    private Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void folders(String value) {
        AppSettings.CLEAR.folders();
        preferences.put(FOLDERS.toString(), value);
    }

    public void mainAutoPaste(boolean isMainAutoPasteEnabled) {
        AppSettings.CLEAR.mainAutoPaste();
        preferences.putBoolean(MAIN_AUTO_PASTE.toString(), isMainAutoPasteEnabled);
    }

    public void mainTheme(String theme) {
        AppSettings.CLEAR.mainTheme();
        preferences.put(MAIN_THEME.toString(), theme);
    }
}
