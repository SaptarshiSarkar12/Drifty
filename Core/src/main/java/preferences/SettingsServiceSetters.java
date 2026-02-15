package preferences;

public interface SettingsServiceSetters {
    void setLastYtDlpUpdateTime(long value);

    void setLastFolder(String lastFolderPath);

    void setYtDlpVersion(String version);

    void setFfmpegVersion(String version);

    void setSpotifyAccessToken(String token);

    void setYtDlpUpdating(boolean isInitializing);

    void setFfmpegWorking(boolean isWorking);

    void setEarlyAccess(boolean isEarlyAccess);

    void setNewDriftyVersionName(String versionName);

    void setLastDriftyUpdateTime(long value);

    void setLatestDriftyVersionTag(String tag);

    void setDriftyUpdateAvailable(boolean isUpdateAvailable);

    void setFolders(String value);

    void setGuiAutoPasteEnabled(boolean enabled);

    void setGuiTheme(String theme);
}
