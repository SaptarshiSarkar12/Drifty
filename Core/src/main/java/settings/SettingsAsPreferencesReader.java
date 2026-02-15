package settings;

import utils.SpotifyTokenEncryptor;

import java.nio.file.Paths;
import java.util.prefs.Preferences;

import static settings.PreferenceNames.*;

public class SettingsAsPreferencesReader implements SettingsReader {
    private static final SettingsAsPreferencesReader INSTANCE = new SettingsAsPreferencesReader();
    private final Preferences preferences = PreferenceNames.PREFERENCES;

    static SettingsAsPreferencesReader getInstance() {
        return INSTANCE;
    }

    @Override
    public long getLastYtDlpUpdateTime() {
        return preferences.getLong(LAST_YT_DLP_UPDATE_TIME, 1000L);
    }

    @Override
    public String getLastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString();
        return preferences.get(LAST_FOLDER, defaultPath);
    }

    @Override
    public String getYtDlpVersion() {
        return preferences.get(YT_DLP_VERSION, "");
    }

    @Override
    public String getFfmpegVersion() {
        return preferences.get(FFMPEG_VERSION, "");
    }

    @Override
    public String getSpotifyAccessToken() {
        String encrypted = preferences.get(SPOTIFY_ACCESS_TOKEN, "");
        return SpotifyTokenEncryptor.decrypt(encrypted);
    }

    @Override
    public boolean isYtDlpUpdating() {
        return preferences.getBoolean(YT_DLP_UPDATING, false);
    }

    @Override
    public boolean isFfmpegWorking() {
        return preferences.getBoolean(IS_FFMPEG_WORKING, false);
    }

    @Override
    public boolean isEarlyAccessEnabled() {
        return preferences.getBoolean(EARLY_ACCESS, false);
    }

    @Override
    public String getNewDriftyVersionName() {
        return preferences.get(NEW_DRIFTY_VERSION_NAME, "");
    }

    @Override
    public long getLastDriftyUpdateTime() {
        return preferences.getLong(LAST_DRIFTY_UPDATE_TIME, 1000L);
    }

    @Override
    public String getLatestDriftyVersionTag() {
        return preferences.get(LATEST_DRIFTY_VERSION_TAG, "");
    }

    @Override
    public boolean isDriftyUpdateAvailable() {
        return preferences.getBoolean(DRIFTY_UPDATE_AVAILABLE, false);
    }

    @Override
    public String getFolders() {
        return preferences.get(FOLDERS, "");
    }

    @Override
    public boolean isGuiAutoPasteEnabled() {
        return preferences.getBoolean(GUI_AUTO_PASTE, false);
    }

    @Override
    public String getGuiTheme() {
        return preferences.get(GUI_THEME, "Light");
    }
}
