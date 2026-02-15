package settings;


public interface SettingsReader {

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

}
