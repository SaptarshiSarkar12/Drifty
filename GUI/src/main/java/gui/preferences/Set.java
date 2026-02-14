package gui.preferences;

import java.util.prefs.Preferences;

import static preferences.Labels.*;

public final class Set extends preferences.Set {
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = PREFERENCES;

    private Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void setFolders(String value) {
        preferences.put(FOLDERS, value);
    }

    public void setGuiAutoPasteEnabled(boolean enabled) {
        preferences.putBoolean(GUI_AUTO_PASTE, enabled);
    }

    public void setGuiTheme(String theme) {
        preferences.put(GUI_THEME, theme);
    }
}
