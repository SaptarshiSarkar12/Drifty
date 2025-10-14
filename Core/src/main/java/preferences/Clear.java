package preferences;

import java.util.prefs.Preferences;

import lombok.extern.slf4j.Slf4j;

import static preferences.Labels.*;

@Slf4j
public class Clear {
    private static final Clear INSTANCE = new Clear();

    private final Preferences preferences = Labels.PREFERENCES;

    static Clear getInstance() {
        return INSTANCE;
    }

    private void remove(String key) {
        preferences.remove(key);
        log.debug("Cleared preference: {}", key);
    }

    public void lastYtDlpUpdateTime() {
        remove(LAST_YT_DLP_UPDATE_TIME);
    }

    public void lastFolder() {
        remove(LAST_FOLDER);
    }

    public void ytDlpVersion() {
        remove(YT_DLP_VERSION);
    }

    public void ffmpegVersion() {
        remove(FFMPEG_VERSION);
    }

    public void spotifyAccessToken() {
        remove(SPOTIFY_ACCESS_TOKEN);
    }

    public void ytDlpUpdating() {
        remove(YT_DLP_UPDATING);
    }

    public void isFfmpegWorking() {
        remove(IS_FFMPEG_WORKING);
    }

    public void earlyAccess() {
        remove(EARLY_ACCESS);
    }

    public void newDriftyVersionName() {
        remove(NEW_DRIFTY_VERSION_NAME);
    }

    public void lastDriftyUpdateTime() {
        remove(LAST_DRIFTY_UPDATE_TIME);
    }

    public void latestDriftyVersionTag() {
        remove(LATEST_DRIFTY_VERSION_TAG);
    }

    public void driftyUpdateAvailable() {
        remove(DRIFTY_UPDATE_AVAILABLE);
    }

    public void jobs() {
        remove(JOBS);
    }
}
