package preferences;

import java.util.prefs.Preferences;
import static preferences.Labels.*;

public class Clear {
    private static final Clear INSTANCE = new Clear();
    private final Preferences preferences = Labels.PREFERENCES;

    static Clear getInstance() {
        return INSTANCE;
    }

    public void lastYtDlpUpdateTime() {
        preferences.remove(LAST_YT_DLP_UPDATE_TIME);
    }

    public void lastFolder() {
        preferences.remove(LAST_FOLDER);
    }

    public void ytDlpVersion() {
        preferences.remove(YT_DLP_VERSION);
    }

    public void ffmpegVersion() {
        preferences.remove(FFMPEG_VERSION);
    }

    public void spotifyAccessToken() {
        preferences.remove(SPOTIFY_ACCESS_TOKEN);
    }

    public void ytDlpUpdating() {
        preferences.remove(YT_DLP_UPDATING);
    }

    public void isFfmpegWorking() {
        preferences.remove(IS_FFMPEG_WORKING);
    }
}
