package settings;


public interface SettingsEditor {

    long getLastYtDlpUpdateTime();

    String getLastDownloadFolder();

    String getYtDlpVersion();

    String getFfmpegVersion();

    String getSpotifyAccessToken();

    boolean isYtDlpUpdating();

    boolean isFfmpegWorking();

    boolean isEarlyAccessEnabled();

    String getNewDriftyVersionName();

    long getLastDriftyUpdateTime();

    String getLatestDriftyVersionTag();

    boolean isDriftyUpdateAvailable();

    String getFolders();

    boolean isGuiAutoPasteEnabled();

    String getGuiTheme();

    void setLastYtDlpUpdateTime(long value);

    void setLastDownloadFolder(String lastFolderPath);

    void setYtDlpVersion(String version);

    void setFfmpegVersion(String version);

    void setSpotifyAccessToken(String token);

    void setYtDlpUpdating(boolean isInitializing);

    void setFfmpegWorking(boolean isWorking);

    void setEarlyAccessEnabled(boolean isEarlyAccess);

    void setNewDriftyVersionName(String versionName);

    void setLastDriftyUpdateTime(long value);

    void setLatestDriftyVersionTag(String tag);

    void setDriftyUpdateAvailable(boolean isUpdateAvailable);

    void setFolders(String value);

    void setGuiAutoPasteEnabled(boolean enabled);

    void setGuiTheme(String theme);
}
