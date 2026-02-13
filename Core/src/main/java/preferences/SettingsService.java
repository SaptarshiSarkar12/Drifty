package preferences;

public interface SettingsService {
    long getLastYtDlpUpdateTime();

    String getLastDownloadFolder();

    String getYtDlpVersion();

    String getFfmpegVersion();

    String getSpotifyAccessToken();

    boolean isYtDlpUpdating();

    boolean isFfmpegWorking();

    boolean isEarlyAccessEnabled();

    String getNewDriftyVersionName();

    long lastDriftyUpdateTime();

    String latestDriftyVersionTag();

    boolean driftyUpdateAvailable();
}
