package preferences;

import java.util.prefs.Preferences;

public interface Labels {
    Preferences PREFERENCES = Preferences.userNodeForPackage(Labels.class);
    String LAST_YT_DLP_UPDATE_TIME = "LAST_YT_DLP_UPDATE_TIME";
    String LAST_FOLDER = "LAST_FOLDER";
    String YT_DLP_VERSION = "YT_DLP_VERSION";
    String SPOTDL_VERSION = "SPOTDL_VERSION";
    String YT_DLP_UPDATING = "YT_DLP_UPDATING";
}
