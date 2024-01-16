package support;

public class Constants {
    public static final String APPLICATION_NAME = "Drifty";
    public static final String VERSION_NUMBER = "v2.1.0";
    public static final String DRIFTY_WEBSITE_URL = "https://saptarshisarkar12.github.io/Drifty/";
    public static final String INVALID_LINK = "Link is invalid! Please check the link and try again.";
    public static final String FILENAME_DETECTION_ERROR = "Failed to detect the filename! A default name will be used instead.";
    public static final String TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER = "Trying to automatically detect default Downloads folder...";
    public static final String FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER = "Failed to retrieve default download folder!";
    public static final String FOLDER_DETECTED = "Default download folder detected : ";
    public static final String FILENAME_DETECTED = "Filename detected : ";
    public static final String FAILED_TO_CREATE_LOG = "Failed to create log : ";
    public static final String FAILED_TO_CLEAR_LOG = "Failed to clear Log contents !";
    public static final String FILE_NOT_FOUND = "An error occurred! Requested file does not exist, please check the url.";
    public static final String VIDEO_UNAVAILABLE = "The requested video is unavailable, it has been deleted from the platform.";
    public static final String PERMISSION_DENIED = "You do not have access to download the video, permission is denied.";
    public static final String DRIFTY_COMPONENT_NOT_EXECUTABLE = "A Drifty component (yt-dlp) is not marked as executable.";
    public static final long ONE_DAY = 1000 * 60 * 60 * 24; // Value of one day (24 Hours) in milliseconds

    /*
    Denoting a Constant with _F indicates that it needs to be used with String.format(), where %s is replaced with the
    string provided in String.format():
        String.format(CONSTANT_F, "String replacing %s")
     */
    public static final String DOWNLOADING_F = "Downloading \"%s\" ...";
    public static final String FAILED_CONNECTION_F = "Failed to connect to %s!";
    public static final String SUCCESSFULLY_DOWNLOADED_F = "Successfully downloaded %s!";
    public static final String FAILED_TO_DOWNLOAD_F = "Failed to download %s!";
}
