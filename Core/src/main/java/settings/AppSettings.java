package settings;

public class AppSettings {
    private final static AppSettings instance = new AppSettings();

    public static final SettingsEditor EDITOR = SettingsAsPreferencesEditor.getInstance();

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
        EDITOR.setLastYtDlpUpdateTime(lastYtDlpUpdateTime);
    }

    public static String getLastDownloadFolder() {
        return instance.lastDownloadFolder;
    }

    public static void setLastDownloadFolder(String lastDownloadFolder) {
        instance.lastDownloadFolder = lastDownloadFolder;
        EDITOR.setLastDownloadFolder(lastDownloadFolder);
    }

    public static String getYtDlpVersion() {
        return instance.ytDlpVersion;
    }

    public static void setYtDlpVersion(String ytDlpVersion) {
        instance.ytDlpVersion = ytDlpVersion;
        EDITOR.setYtDlpVersion(ytDlpVersion);
    }

    public static String getFfmpegVersion() {
        return instance.ffmpegVersion;
    }

    public static void setFfmpegVersion(String ffmpegVersion) {
        instance.ffmpegVersion = ffmpegVersion;
        EDITOR.setFfmpegVersion(ffmpegVersion);
    }

    public static String getSpotifyAccessToken() {
        return instance.spotifyAccessToken;
    }

    public static void setSpotifyAccessToken(String spotifyAccessToken) {
        instance.spotifyAccessToken = spotifyAccessToken;
        EDITOR.setSpotifyAccessToken(spotifyAccessToken);
    }

    public static boolean isYtDlpUpdating() {
        return instance.isYtDlpUpdating;
    }

    public static void setYtDlpUpdating(boolean ytDlpUpdating) {
        instance.isYtDlpUpdating = ytDlpUpdating;
        EDITOR.setYtDlpUpdating(ytDlpUpdating);
    }

    public static boolean isFfmpegWorking() {
        return instance.isFfmpegWorking;
    }

    public static void setFfmpegWorking(boolean ffmpegWorking) {
        instance.isFfmpegWorking = ffmpegWorking;
        EDITOR.setFfmpegWorking(ffmpegWorking);
    }

    public static boolean isEarlyAccessEnabled() {
        return instance.isEarlyAccessEnabled;
    }

    public static void setEarlyAccessEnabled(boolean earlyAccessEnabled) {
        instance.isEarlyAccessEnabled = earlyAccessEnabled;
        EDITOR.setEarlyAccessEnabled(earlyAccessEnabled);
    }

    public static String getNewDriftyVersionName() {
        return instance.newDriftyVersionName;
    }

    public static void setNewDriftyVersionName(String newDriftyVersionName) {
        instance.newDriftyVersionName = newDriftyVersionName;
        EDITOR.setNewDriftyVersionName(newDriftyVersionName);
    }

    public static long getLastDriftyUpdateTime() {
        return instance.lastDriftyUpdateTime;
    }

    public static void setLastDriftyUpdateTime(long lastDriftyUpdateTime) {
        instance.lastDriftyUpdateTime = lastDriftyUpdateTime;
        EDITOR.setLastDriftyUpdateTime(lastDriftyUpdateTime);
    }

    public static String getLatestDriftyVersionTag() {
        return instance.latestDriftyVersionTag;
    }

    public static void setLatestDriftyVersionTag(String latestDriftyVersionTag) {
        instance.latestDriftyVersionTag = latestDriftyVersionTag;
        EDITOR.setLatestDriftyVersionTag(latestDriftyVersionTag);
    }

    public static boolean isDriftyUpdateAvailable() {
        return instance.isDriftyUpdateAvailable;
    }

    public static void setDriftyUpdateAvailable(boolean driftyUpdateAvailable) {
        instance.isDriftyUpdateAvailable = driftyUpdateAvailable;
        EDITOR.setDriftyUpdateAvailable(driftyUpdateAvailable);
    }

    public static String getFolders() {
        return instance.folders;
    }

    public static void setFolders(String folders) {
        instance.folders = folders;
        EDITOR.setFolders(folders);
    }

    public static boolean isGuiAutoPasteEnabled() {
        return instance.isGuiAutoPasteEnabled;
    }

    public static void setGuiAutoPasteEnabled(boolean guiAutoPasteEnabled) {
        instance.isGuiAutoPasteEnabled = guiAutoPasteEnabled;
        EDITOR.setGuiAutoPasteEnabled(guiAutoPasteEnabled);
    }

    public static String getGuiTheme() {
        return instance.guiTheme;
    }

    public static void setGuiTheme(String guiTheme) {
        instance.guiTheme = guiTheme;
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