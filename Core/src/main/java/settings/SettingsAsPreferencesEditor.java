package settings;

import init.Environment;

import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class SettingsAsPreferencesEditor implements SettingsEditor {


    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(AppSettings.class);
    private static final String LAST_YT_DLP_UPDATE_TIME = "LAST_YT_DLP_UPDATE_TIME";
    private static final String LAST_FOLDER = "LAST_FOLDER";
    private static final String YT_DLP_VERSION = "YT_DLP_VERSION";
    private static final String YT_DLP_UPDATING = "YT_DLP_UPDATING";
    private static final String SPOTIFY_ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN";
    private static final String IS_FFMPEG_WORKING = "IS_FFMPEG_WORKING";
    private static final String FFMPEG_VERSION = "FFMPEG_VERSION";
    private static final String NEW_DRIFTY_VERSION_NAME = "NEW_DRIFTY_VERSION_NAME";
    private static final String LATEST_DRIFTY_VERSION_TAG = "LATEST_DRIFTY_VERSION_TAG";
    private static final String LAST_DRIFTY_UPDATE_TIME = "LAST_DRIFTY_UPDATE_TIME";
    private static final String EARLY_ACCESS = "EARLY_ACCESS";
    private static final String DRIFTY_UPDATE_AVAILABLE = "DRIFTY_UPDATE_AVAILABLE";
    private static final String FOLDERS = "FOLDERS";
    private static final String GUI_AUTO_PASTE = "GUI_AUTO_PASTE";
    private static final String GUI_THEME = "GUI_THEME";
    
    private static final SettingsAsPreferencesEditor INSTANCE = new SettingsAsPreferencesEditor();

    static SettingsAsPreferencesEditor getInstance() {
        return INSTANCE;
    }

    @Override
    public long getLastYtDlpUpdateTime() {
        return PREFERENCES.getLong(LAST_YT_DLP_UPDATE_TIME, 1000L);
    }

    @Override
    public String getLastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString();
        return PREFERENCES.get(LAST_FOLDER, defaultPath);
    }

    @Override
    public String getYtDlpVersion() {
        return PREFERENCES.get(YT_DLP_VERSION, "");
    }

    @Override
    public String getFfmpegVersion() {
        return PREFERENCES.get(FFMPEG_VERSION, "");
    }

    @Override
    public String getSpotifyAccessToken() {
        return PREFERENCES.get(SPOTIFY_ACCESS_TOKEN, "");
    }

    @Override
    public boolean isYtDlpUpdating() {
        return PREFERENCES.getBoolean(YT_DLP_UPDATING, false);
    }

    @Override
    public boolean isFfmpegWorking() {
        return PREFERENCES.getBoolean(IS_FFMPEG_WORKING, false);
    }

    @Override
    public boolean isEarlyAccessEnabled() {
        return PREFERENCES.getBoolean(EARLY_ACCESS, false);
    }

    @Override
    public String getNewDriftyVersionName() {
        return PREFERENCES.get(NEW_DRIFTY_VERSION_NAME, "");
    }

    @Override
    public long getLastDriftyUpdateTime() {
        return PREFERENCES.getLong(LAST_DRIFTY_UPDATE_TIME, 1000L);
    }

    @Override
    public String getLatestDriftyVersionTag() {
        return PREFERENCES.get(LATEST_DRIFTY_VERSION_TAG, "");
    }

    @Override
    public boolean isDriftyUpdateAvailable() {
        return PREFERENCES.getBoolean(DRIFTY_UPDATE_AVAILABLE, false);
    }

    @Override
    public String getFolders() {
        return PREFERENCES.get(FOLDERS, "");
    }

    @Override
    public boolean isGuiAutoPasteEnabled() {
        return PREFERENCES.getBoolean(GUI_AUTO_PASTE, false);
    }

    @Override
    public String getGuiTheme() {
        return PREFERENCES.get(GUI_THEME, "Light");
    }


    @Override
    public void setLastYtDlpUpdateTime(long value) {
        PREFERENCES.putLong(LAST_YT_DLP_UPDATE_TIME, value);
    }

    @Override
    public void setLastDownloadFolder(String lastFolderPath) {
        PREFERENCES.put(LAST_FOLDER, lastFolderPath);
    }

    @Override
    public void setYtDlpVersion(String version) {
        PREFERENCES.put(YT_DLP_VERSION, version);
    }

    @Override
    public void setFfmpegVersion(String version) {
        PREFERENCES.put(FFMPEG_VERSION, version);
    }

    @Override
    public void setSpotifyAccessToken(String token) {
        PREFERENCES.put(SPOTIFY_ACCESS_TOKEN, token);
    }

    @Override
    public void setYtDlpUpdating(boolean isYtDlpUpdating) {
        PREFERENCES.putBoolean(YT_DLP_UPDATING, isYtDlpUpdating);
    }

    @Override
    public void setFfmpegWorking(boolean isWorking) {
        PREFERENCES.putBoolean(IS_FFMPEG_WORKING, isWorking);
    }

    @Override
    public void setEarlyAccessEnabled(boolean isEarlyAccess) {
        PREFERENCES.putBoolean(EARLY_ACCESS, isEarlyAccess);
    }

    @Override
    public void setNewDriftyVersionName(String versionName) {
        PREFERENCES.put(NEW_DRIFTY_VERSION_NAME, versionName);
    }

    @Override
    public void setLastDriftyUpdateTime(long value) {
        PREFERENCES.putLong(LAST_DRIFTY_UPDATE_TIME, value);
    }

    @Override
    public void setLatestDriftyVersionTag(String tag) {
        PREFERENCES.put(LATEST_DRIFTY_VERSION_TAG, tag);
    }

    @Override
    public void setDriftyUpdateAvailable(boolean isUpdateAvailable) {
        PREFERENCES.putBoolean(DRIFTY_UPDATE_AVAILABLE, isUpdateAvailable);
        flush();
    }


    @Override
    public void setFolders(String value) {
        PREFERENCES.put(FOLDERS, value);
    }

    @Override
    public void setGuiAutoPasteEnabled(boolean enabled) {
        PREFERENCES.putBoolean(GUI_AUTO_PASTE, enabled);
    }

    @Override
    public void setGuiTheme(String theme) {
        PREFERENCES.put(GUI_THEME, theme);
    }


    private void flush() {
        try {
            PREFERENCES.flush();
        }
catch (BackingStoreException e) {
            Environment.getMessageBroker().msgUpdateError("Could not complete storage of Preference after update: " + e.getMessage());
        }
    }
}
