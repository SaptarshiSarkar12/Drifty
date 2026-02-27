package support;

import init.Environment;
import utils.Utility;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Constants {
    public static final String APPLICATION_NAME = "Drifty";
    public static final String VERSION_NUMBER = "v2.1.0";
    public static final String DRIFTY_WEBSITE_URL = "https://drifty.vercel.app/";
    public static final String INVALID_LINK = "Link is invalid! Please check the link and try again.";
    public static final String FILENAME_DETECTION_ERROR = "Failed to detect the filename! A default name will be used instead.";
    public static final String TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER = "Trying to automatically detect default Downloads folder...";
    public static final String FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER_ERROR = "Failed to retrieve default download folder!";
    public static final String FOLDER_DETECTED = "Default download folder detected : ";
    public static final String FILENAME_DETECTED = "Filename detected : ";
    public static final String FAILED_TO_CREATE_LOG_ERROR = "Failed to create log : ";
    public static final String FAILED_TO_CLEAR_LOG_ERROR = "Failed to clear Log contents !";
    public static final String FILE_NOT_FOUND_ERROR = "An error occurred! Requested file does not exist, please check the url.";
    public static final String VIDEO_UNAVAILABLE_ERROR = "The requested video is unavailable, it has been deleted from the platform.";
    public static final String PERMISSION_DENIED_ERROR = "You do not have access to download the video, permission is denied.";
    public static final String DRIFTY_COMPONENT_NOT_EXECUTABLE_ERROR = "A Drifty component (yt-dlp) is not marked as executable.";
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0";
    public static final long ONE_DAY = 86400000; // Value of one day (24 Hours) in milliseconds
    public static URL updateURL;

    static {
        try {
            updateURL = Utility.getUpdateURL();
        }catch (MalformedURLException | URISyntaxException e) {
            Environment.getMessageBroker().msgUpdateError("Drifty update URL is invalid! " + e.getMessage());
            updateURL = null;
        }
    }

    /*
    Denoting a Constant with _F indicates that it needs to be used with String.format(), where %s is replaced with the
    string provided in String.format():
        String.format(CONSTANT_F, "String replacing %s")
     */
    public static final String DOWNLOADING_F = "Downloading \"%s\" ...";
    public static final String FAILED_CONNECTION_F = "Failed to connect to %s!";
    public static final String SUCCESSFULLY_DOWNLOADED_F = "Successfully downloaded \"%s\"";
    public static final String FAILED_TO_DOWNLOAD_F = "Failed to download %s!";
}
