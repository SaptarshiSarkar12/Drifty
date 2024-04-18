package preferences;

import java.util.prefs.Preferences;

public interface Labels {
    Preferences PREFERENCES = Preferences.userNodeForPackage(Labels.class);
    String LAST_YT_DLP_UPDATE_TIME = "LAST_YT_DLP_UPDATE_TIME";
    String LAST_FOLDER = "LAST_FOLDER";
    String YT_DLP_VERSION = "YT_DLP_VERSION";
    String YT_DLP_UPDATING = "YT_DLP_UPDATING";
    String SPOTIFY_ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN";
    String IS_FFMPEG_WORKING = "IS_FFMPEG_WORKING";
    String FFMPEG_VERSION = "FFMPEG_VERSION";
}
