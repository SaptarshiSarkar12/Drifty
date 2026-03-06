package settings;

public class AppSettings {
    private static final SettingsEditor EDITOR = SettingsAsPreferencesEditor.getInstance();
    private static final AppSettings INSTANCE = new AppSettings();

    private volatile long lastDriftyUpdateTime;
    private volatile boolean isDriftyUpdateAvailable;
    private volatile String latestDriftyVersionTag;
    private volatile String newDriftyVersionName;
    private volatile String ytDlpVersion;
    private volatile long lastYtDlpUpdateTime;
    private volatile boolean isYtDlpUpdating;
    private volatile String ffmpegVersion;
    private volatile boolean isFfmpegWorking;
    private volatile String lastDownloadFolder;
    private volatile String folders;
    private volatile String spotifyAccessToken;
    private volatile boolean isEarlyAccessEnabled;
    private volatile boolean isGuiAutoPasteEnabled;
    private volatile String guiTheme;


    public static long getLastYtDlpUpdateTime() {
        return INSTANCE.lastYtDlpUpdateTime;
    }

    public static void setLastYtDlpUpdateTime(long lastYtDlpUpdateTime) {
        INSTANCE.lastYtDlpUpdateTime = lastYtDlpUpdateTime;
        EDITOR.setLastYtDlpUpdateTime(lastYtDlpUpdateTime);
    }

    public static String getLastDownloadFolder() {
        return INSTANCE.lastDownloadFolder;
    }

    public static void setLastDownloadFolder(String lastDownloadFolder) {
        INSTANCE.lastDownloadFolder = lastDownloadFolder;
        EDITOR.setLastDownloadFolder(lastDownloadFolder);
    }

    public static String getYtDlpVersion() {
        return INSTANCE.ytDlpVersion;
    }

    public static void setYtDlpVersion(String ytDlpVersion) {
        INSTANCE.ytDlpVersion = ytDlpVersion;
        EDITOR.setYtDlpVersion(ytDlpVersion);
    }

    public static String getFfmpegVersion() {
        return INSTANCE.ffmpegVersion;
    }

    public static void setFfmpegVersion(String ffmpegVersion) {
        INSTANCE.ffmpegVersion = ffmpegVersion;
        EDITOR.setFfmpegVersion(ffmpegVersion);
    }

    public static String getSpotifyAccessToken() {
        return INSTANCE.spotifyAccessToken;
    }

    public static void setSpotifyAccessToken(String spotifyAccessToken) {
        INSTANCE.spotifyAccessToken = spotifyAccessToken;
        //EDITOR.setSpotifyAccessToken(spotifyAccessToken); not to be saved, will refresh on startup
    }

    public static boolean isYtDlpUpdating() {
        return INSTANCE.isYtDlpUpdating;
    }

    public static void setYtDlpUpdating(boolean ytDlpUpdating) {
        INSTANCE.isYtDlpUpdating = ytDlpUpdating;
        EDITOR.setYtDlpUpdating(ytDlpUpdating);
    }

    public static boolean isFfmpegWorking() {
        return INSTANCE.isFfmpegWorking;
    }

    public static void setFfmpegWorking(boolean ffmpegWorking) {
        INSTANCE.isFfmpegWorking = ffmpegWorking;
        EDITOR.setFfmpegWorking(ffmpegWorking);
    }

    public static boolean isEarlyAccessEnabled() {
        return INSTANCE.isEarlyAccessEnabled;
    }

    public static void setEarlyAccessEnabled(boolean earlyAccessEnabled) {
        INSTANCE.isEarlyAccessEnabled = earlyAccessEnabled;
        EDITOR.setEarlyAccessEnabled(earlyAccessEnabled);
    }

    public static String getNewDriftyVersionName() {
        return INSTANCE.newDriftyVersionName;
    }

    public static void setNewDriftyVersionName(String newDriftyVersionName) {
        INSTANCE.newDriftyVersionName = newDriftyVersionName;
        EDITOR.setNewDriftyVersionName(newDriftyVersionName);
    }

    public static long getLastDriftyUpdateTime() {
        return INSTANCE.lastDriftyUpdateTime;
    }

    public static void setLastDriftyUpdateTime(long lastDriftyUpdateTime) {
        INSTANCE.lastDriftyUpdateTime = lastDriftyUpdateTime;
        EDITOR.setLastDriftyUpdateTime(lastDriftyUpdateTime);
    }

    public static String getLatestDriftyVersionTag() {
        return INSTANCE.latestDriftyVersionTag;
    }

    public static void setLatestDriftyVersionTag(String latestDriftyVersionTag) {
        INSTANCE.latestDriftyVersionTag = latestDriftyVersionTag;
        EDITOR.setLatestDriftyVersionTag(latestDriftyVersionTag);
    }

    public static boolean isDriftyUpdateAvailable() {
        return INSTANCE.isDriftyUpdateAvailable;
    }

    public static void setDriftyUpdateAvailable(boolean driftyUpdateAvailable) {
        INSTANCE.isDriftyUpdateAvailable = driftyUpdateAvailable;
        EDITOR.setDriftyUpdateAvailable(driftyUpdateAvailable);
    }

    public static String getFolders() {
        return INSTANCE.folders;
    }

    public static void setFolders(String folders) {
        INSTANCE.folders = folders;
        EDITOR.setFolders(folders);
    }

    public static boolean isGuiAutoPasteEnabled() {
        return INSTANCE.isGuiAutoPasteEnabled;
    }

    public static void setGuiAutoPasteEnabled(boolean guiAutoPasteEnabled) {
        INSTANCE.isGuiAutoPasteEnabled = guiAutoPasteEnabled;
        EDITOR.setGuiAutoPasteEnabled(guiAutoPasteEnabled);
    }

    public static String getGuiTheme() {
        return INSTANCE.guiTheme;
    }

    public static void setGuiTheme(String guiTheme) {
        INSTANCE.guiTheme = guiTheme;
        EDITOR.setGuiTheme(guiTheme);
    }


    private AppSettings() {
        lastDriftyUpdateTime = EDITOR.getLastDriftyUpdateTime();
        isDriftyUpdateAvailable = EDITOR.isDriftyUpdateAvailable();
        latestDriftyVersionTag = EDITOR.getLatestDriftyVersionTag();
        newDriftyVersionName = EDITOR.getNewDriftyVersionName();
        ytDlpVersion = EDITOR.getYtDlpVersion();
        lastYtDlpUpdateTime = EDITOR.getLastYtDlpUpdateTime();
        isYtDlpUpdating = EDITOR.isYtDlpUpdating();
        ffmpegVersion = EDITOR.getFfmpegVersion();
        isFfmpegWorking = EDITOR.isFfmpegWorking();
        lastDownloadFolder = EDITOR.getLastDownloadFolder();
        folders = EDITOR.getFolders();
        spotifyAccessToken = EDITOR.getSpotifyAccessToken();
        isEarlyAccessEnabled = EDITOR.isEarlyAccessEnabled();
        isGuiAutoPasteEnabled = EDITOR.isGuiAutoPasteEnabled();
        guiTheme = EDITOR.getGuiTheme();
    }


}