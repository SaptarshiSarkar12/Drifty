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

    public void setFolders(String value) {
        preferences.put(FOLDERS.toString(), value);
    }

    public void setGuiAutoPasteEnabled(boolean enabled) {
        preferences.putBoolean(GUI_AUTO_PASTE.toString(), enabled);
    }

    public void setGuiTheme(String theme) {
        preferences.put(GUI_THEME.toString(), theme);
    }
}
