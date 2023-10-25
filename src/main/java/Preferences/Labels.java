package Preferences;

import java.util.prefs.Preferences;

enum Labels {
    FOLDERS, AUTO_PASTE, LAST_YTDLP_UPDATE_TIME, LAST_FOLDER, JOBS, MENU_BAR_SYSTEM, ALWAYS_AUTO_PASTE, LAST_DRIFTY_UPDATE_TIME;
    public static final Preferences PREFERENCES = Preferences.userNodeForPackage(Labels.class);
}
