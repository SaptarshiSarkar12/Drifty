package settings;

import utils.SpotifyTokenEncryptor;

import javax.crypto.*;
import java.util.prefs.Preferences;

import static settings.PreferenceNames.*;

public class SettingsAsPreferencesWriter implements SettingsWriter {
    private static final SettingsAsPreferencesWriter INSTANCE = new SettingsAsPreferencesWriter();
    private final Preferences preferences = PreferenceNames.PREFERENCES;

    static SettingsAsPreferencesWriter getInstance() {
        return INSTANCE;
    }

    @Override
    public void setLastYtDlpUpdateTime(long value) {
        preferences.putLong(LAST_YT_DLP_UPDATE_TIME, value);
    }

    @Override
    public void setLastFolder(String lastFolderPath) {
        preferences.put(LAST_FOLDER, lastFolderPath);
    }

    @Override
    public void setYtDlpVersion(String version) {
        preferences.put(YT_DLP_VERSION, version);
    }

    @Override
    public void setFfmpegVersion(String version) {
        preferences.put(FFMPEG_VERSION, version);
    }

    @Override
    public void setSpotifyAccessToken(String token) {
        String encrypted = SpotifyTokenEncryptor.encryptToken(token);
        preferences.put(SPOTIFY_ACCESS_TOKEN, encrypted);
    }

    @Override
    public void setYtDlpUpdating(boolean isInitializing) {
        preferences.putBoolean(YT_DLP_UPDATING, isInitializing);
    }

    @Override
    public void setFfmpegWorking(boolean isWorking) {
        preferences.putBoolean(IS_FFMPEG_WORKING, isWorking);
    }

    @Override
    public void setEarlyAccess(boolean isEarlyAccess) {
        preferences.putBoolean(EARLY_ACCESS, isEarlyAccess);
    }

    @Override
    public void setNewDriftyVersionName(String versionName) {
        preferences.put(NEW_DRIFTY_VERSION_NAME, versionName);
    }

    @Override
    public void setLastDriftyUpdateTime(long value) {
        preferences.putLong(LAST_DRIFTY_UPDATE_TIME, value);
    }

    @Override
    public void setLatestDriftyVersionTag(String tag) {
        preferences.put(LATEST_DRIFTY_VERSION_TAG, tag);
    }

    @Override
    public void setDriftyUpdateAvailable(boolean isUpdateAvailable) {
        preferences.putBoolean(DRIFTY_UPDATE_AVAILABLE, isUpdateAvailable);
    }

    @Override
    public void setFolders(String value) {
        preferences.put(FOLDERS, value);
    }

    @Override
    public void setGuiAutoPasteEnabled(boolean enabled) {
        preferences.putBoolean(GUI_AUTO_PASTE, enabled);
    }

    @Override
    public void setGuiTheme(String theme) {
        preferences.put(GUI_THEME, theme);
    }
}
