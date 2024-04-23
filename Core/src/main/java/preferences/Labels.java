package preferences;

import java.util.prefs.Preferences;

public interface Labels {
    Preferences PREFERENCES = Preferences.userRoot().node("Drifty");
    String LAST_YT_DLP_UPDATE_TIME = "LAST_YT_DLP_UPDATE_TIME";
    String LAST_DRIFTY_UPDATE_TIME = "LAST_DRIFTY_UPDATE_TIME";
    String DRIFTY_UPDATE_AVAILABLE = "DRIFTY_UPDATE_AVAILABLE";
    String LAST_FOLDER = "LAST_FOLDER";
    String YT_DLP_VERSION = "YT_DLP_VERSION";
    String YT_DLP_UPDATING = "YT_DLP_UPDATING";
    String SPOTIFY_ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN";
    String IS_FFMPEG_WORKING = "IS_FFMPEG_WORKING";
    String FFMPEG_VERSION = "FFMPEG_VERSION";
    String UPDATE_CHANNEL = "UPDATE_CHANNEL";
}
