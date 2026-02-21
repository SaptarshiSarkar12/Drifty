package settings;

public class AppSettings {
    private final static AppSettings instance = new AppSettings();

    private long lastDriftyUpdateTime;
    private boolean isDriftyUpdateAvailable;
    private String latestDriftyVersionTag;
    private String newDriftyVersionName;
    private String ytDlpVersion;
    private long lastYtDlpUpdateTime;
    private boolean isYtDlpUpdating;
    private String ffmpegVersion;
    private boolean isFfmpegWorking;
    private String lastDownloadFolder;
    private String folders;
    private String spotifyAccessToken;
    private boolean isEarlyAccessEnabled;
    private boolean isGuiAutoPasteEnabled;
    private String guiTheme;


    public static long getLastYtDlpUpdateTime() {
        return instance.lastYtDlpUpdateTime;
    }

    public static void setLastYtDlpUpdateTime(long lastYtDlpUpdateTime) {
        instance.lastYtDlpUpdateTime = lastYtDlpUpdateTime;
        SET.setLastYtDlpUpdateTime(lastYtDlpUpdateTime);
    }

    public static String getLastDownloadFolder() {
        return instance.lastDownloadFolder;
    }

    public static void setLastDownloadFolder(String lastDownloadFolder) {
        instance.lastDownloadFolder = lastDownloadFolder;
        SET.setLastDownloadFolder(lastDownloadFolder);
    }

    public static String getYtDlpVersion() {
        return instance.ytDlpVersion;
    }

    public static void setYtDlpVersion(String ytDlpVersion) {
        instance.ytDlpVersion = ytDlpVersion;
        SET.setYtDlpVersion(ytDlpVersion);
    }

    public static String getFfmpegVersion() {
        return instance.ffmpegVersion;
    }

    public static void setFfmpegVersion(String ffmpegVersion) {
        instance.ffmpegVersion = ffmpegVersion;
        SET.setFfmpegVersion(ffmpegVersion);
    }

    public static String getSpotifyAccessToken() {
        return instance.spotifyAccessToken;
    }

    public static void setSpotifyAccessToken(String spotifyAccessToken) {
        instance.spotifyAccessToken = spotifyAccessToken;
        SET.setSpotifyAccessToken(spotifyAccessToken);
    }

    public static boolean isYtDlpUpdating() {
        return instance.isYtDlpUpdating;
    }

    public static void setYtDlpUpdating(boolean ytDlpUpdating) {
        instance.isYtDlpUpdating = ytDlpUpdating;
        SET.setYtDlpUpdating(ytDlpUpdating);
    }

    public static boolean isFfmpegWorking() {
        return instance.isFfmpegWorking;
    }

    public static void setFfmpegWorking(boolean ffmpegWorking) {
        instance.isFfmpegWorking = ffmpegWorking;
        SET.setFfmpegWorking(ffmpegWorking);
    }

    public static boolean isEarlyAccessEnabled() {
        return instance.isEarlyAccessEnabled;
    }

    public static void setEarlyAccessEnabled(boolean earlyAccessEnabled) {
        instance.isEarlyAccessEnabled = earlyAccessEnabled;
        SET.setEarlyAccessEnabled(earlyAccessEnabled);
    }

    public static String getNewDriftyVersionName() {
        return instance.newDriftyVersionName;
    }

    public static void setNewDriftyVersionName(String newDriftyVersionName) {
        instance.newDriftyVersionName = newDriftyVersionName;
        SET.setNewDriftyVersionName(newDriftyVersionName);
    }

    public static long getLastDriftyUpdateTime() {
        return instance.lastDriftyUpdateTime;
    }

    public static void setLastDriftyUpdateTime(long lastDriftyUpdateTime) {
        instance.lastDriftyUpdateTime = lastDriftyUpdateTime;
        SET.setLastDriftyUpdateTime(lastDriftyUpdateTime);
    }

    public static String getLatestDriftyVersionTag() {
        return instance.latestDriftyVersionTag;
    }

    public static void setLatestDriftyVersionTag(String latestDriftyVersionTag) {
        instance.latestDriftyVersionTag = latestDriftyVersionTag;
        SET.setLatestDriftyVersionTag(latestDriftyVersionTag);
    }

    public static boolean isDriftyUpdateAvailable() {
        return instance.isDriftyUpdateAvailable;
    }

    public static void setDriftyUpdateAvailable(boolean driftyUpdateAvailable) {
        instance.isDriftyUpdateAvailable = driftyUpdateAvailable;
        SET.setDriftyUpdateAvailable(driftyUpdateAvailable);
    }

    public static String getFolders() {
        return instance.folders;
    }

    public static void setFolders(String folders) {
        instance.folders = folders;
        SET.setFolders(folders);
    }

    public static boolean isGuiAutoPasteEnabled() {
        return instance.isGuiAutoPasteEnabled;
    }

    public static void setGuiAutoPasteEnabled(boolean guiAutoPasteEnabled) {
        instance.isGuiAutoPasteEnabled = guiAutoPasteEnabled;
        SET.setGuiAutoPasteEnabled(guiAutoPasteEnabled);
    }

    public static String getGuiTheme() {
        return instance.guiTheme;
    }

    public static void setGuiTheme(String guiTheme) {
        instance.guiTheme = guiTheme;
        SET.setGuiTheme(guiTheme);
    }


    private AppSettings() {
        GET.getLastDriftyUpdateTime();
        GET.isDriftyUpdateAvailable();
        GET.getLatestDriftyVersionTag();
        GET.getNewDriftyVersionName();
        GET.getYtDlpVersion();
        GET.getLastYtDlpUpdateTime();
        GET.isYtDlpUpdating();
        GET.getFfmpegVersion();
        GET.isFfmpegWorking();
        GET.getLastDownloadFolder();
        GET.getFolders();
        GET.getSpotifyAccessToken();
        GET.isEarlyAccessEnabled();
        GET.isGuiAutoPasteEnabled();
        GET.getGuiTheme();
    }

    public static final SettingsReader GET = SettingsAsPreferencesReader.getInstance();
    public static final SettingsWriter SET = SettingsAsPreferencesWriter.getInstance();

}