package Preferences;

import java.util.prefs.Preferences;

enum Labels {
    DEVMODE, FOLDERS, MAIN_AUTO_PASTE, LAST_DLP_UPDATE_TIME, LAST_FOLDER, JOBS, MENU_BAR_SYSTEM, ALWAYS_AUTO_PASTE;
    public static final Preferences PREFERENCES = Preferences.userNodeForPackage(Labels.class);
}
