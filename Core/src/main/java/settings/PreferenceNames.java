package settings;

import java.util.prefs.Preferences;

public class  PreferenceNames {
    static Preferences PREFERENCES = Preferences.userNodeForPackage(AppSettings.class);
    static String LAST_YT_DLP_UPDATE_TIME = "LAST_YT_DLP_UPDATE_TIME";
    static String LAST_FOLDER = "LAST_FOLDER";
    static String YT_DLP_VERSION = "YT_DLP_VERSION";
    static String YT_DLP_UPDATING = "YT_DLP_UPDATING";
    static String SPOTIFY_ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN";
    static String IS_FFMPEG_WORKING = "IS_FFMPEG_WORKING";
    static String FFMPEG_VERSION = "FFMPEG_VERSION";
    static String NEW_DRIFTY_VERSION_NAME = "NEW_DRIFTY_VERSION_NAME";
    static String LATEST_DRIFTY_VERSION_TAG = "LATEST_DRIFTY_VERSION_TAG";
    static String LAST_DRIFTY_UPDATE_TIME = "LAST_DRIFTY_UPDATE_TIME";
    static String EARLY_ACCESS = "EARLY_ACCESS";
    static String DRIFTY_UPDATE_AVAILABLE = "DRIFTY_UPDATE_AVAILABLE";
    static String FOLDERS = "FOLDERS";
    static String GUI_AUTO_PASTE = "GUI_AUTO_PASTE";
    static String GUI_THEME = "GUI_THEME";
}
