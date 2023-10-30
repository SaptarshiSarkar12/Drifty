package Preferences;

import java.util.prefs.Preferences;

enum Labels {
    FOLDERS, MAIN_AUTO_PASTE, LAST_YT_DLP_UPDATE_TIME, LAST_FOLDER, JOBS, MENU_BAR_SYSTEM, ALWAYS_AUTO_PASTE, YT_DLP_VERSION, SPOTDL_VERSION, LAST_DRIFTY_UPDATE_TIME;
    public static final Preferences PREFERENCES = Preferences.userNodeForPackage(Labels.class);
}
