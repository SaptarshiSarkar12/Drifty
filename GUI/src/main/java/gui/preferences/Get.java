package gui.preferences;

import java.util.prefs.Preferences;

import static preferences.Labels.*;

public class Get extends preferences.Get {
    private static final Get INSTANCE = new Get();
    private final Preferences preferences = PREFERENCES;

    static Get getInstance() {
        return INSTANCE;
    }


    public String getFolders() {
        return preferences.get(FOLDERS, "");
    }

    public boolean isGuiAutoPasteEnabled() {
        return preferences.getBoolean(GUI_AUTO_PASTE, false);
    }

    public String getGuiTheme() {
        return preferences.get(GUI_THEME, "Light");
    }
}
