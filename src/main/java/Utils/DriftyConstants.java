package Utils;

/**
 * This file contains some String constants which are repeatedly used in the whole project.
 */
public final class DriftyConstants {
    /**
     * This is a private constructor of this class.
     */
    private DriftyConstants() {
    }
    public static final String VERSION_NUMBER = "v2.1.0";
    public static final String APPLICATION_NAME = "Drifty";
    public static final String DRIFTY_WEBSITE_URL = "https://saptarshisarkar12.github.io/Drifty/";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String DOWNLOADS_FILE_PATH = "/Downloads/";
    public static final String CLI_APPLICATION_STARTED = "Drifty CLI (Command Line Interface) Application Started !";
    public static final String GUI_APPLICATION_STARTED = "Drifty GUI (Graphical User Interface) Application Started !";
    public static final String CLI_APPLICATION_TERMINATED = "Drifty CLI (Command Line Interface) Application Terminated!";
    public static final String GUI_APPLICATION_TERMINATED = "Drifty GUI (Graphical User Interface) Application Terminated!";
    public static final String INVALID_LINK = "Invalid Link!";
    public static final String AUTO_FILE_NAME_DETECTION_FAILED = "Failed to detect the filename! Either the file name or the extension was missing in the url.\nThe url must be of the form of https://www.example.com/filename.extension.";
    public static final String FILENAME_DETECTION_ERROR = "Failed to detect the filename! A default name will be used instead.";
    public static final String TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER = "Trying to automatically detect default Downloads folder...";
    public static final String HELP_FLAG = "--help";
    public static final String NAME_FLAG = "--name";
    public static final String VERSION_FLAG = "--version";
    public static final String LOCATION_FLAG = "--location";
    public static final String BATCH_FLAG = "--batch";
    public static final String HELP_FLAG_SHORT = "-h";
    public static final String NAME_FLAG_SHORT = "-n";
    public static final String VERSION_FLAG_SHORT = "-v";
    public static final String LOCATION_FLAG_SHORT = "-l";
    public static final String BATCH_FLAG_SHORT = "-b";
    public static final String ENTER_FILE_NAME_WITH_EXTENSION = "Please enter the filename with file extension (filename.extension) : ";
    public static final String ENTER_FILE_LINK = "Enter the link to the file (in the form of https://www.example.com/filename.extension) or a YouTube/Instagram Video link or Spotify link : ";
    public static final String USER_HOME_PROPERTY = "user.home";
    public static final String QUIT_OR_CONTINUE = "Enter Q to Quit Or any other key to Continue";
    public static final String FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER = "Failed to retrieve default download folder!";
    public static final String DEFAULT_DOWNLOAD_FOLDER = "Default download folder detected : ";
    public static final String FILENAME_DETECTED = "Filename detected : ";
    public static final String BANNER_BORDER = "====================================================================";
    public static final String FAILED_TO_CREATE_LOG = "Failed to create log : ";
    public static final String FAILED_TO_CLEAR_LOG = "Failed to clear Log contents !";
    public static final String SUCCESSFULLY_DOWNLOADED = "Successfully downloaded ";
    public static final String DOWNLOAD_FAILED = "Download failed!";
    public static final String OF_SIZE = " of size ";
    public static final String FILE_NOT_FOUND = "An error occurred! Requested file does not exist, please check the url.";
    public static final String FAILED_TO_DOWNLOAD_CONTENTS = "Failed to download the contents ! ";
    public static final String FAILED_READING_STREAM = "Failed to get I/O operations channel to read from the data stream !";
    public static final String YOUTUBE_DOWNLOAD_FAILED = "Failed to download YouTube video!";
    public static final String INSTAGRAM_DOWNLOAD_FAILED = "Failed to download Instagram video!";
    public static final String SPOTIFY_DOWNLOAD_FAILED = "Failed to download Spotify audio!";
    public static final String VIDEO_UNAVAILABLE = "The requested video is unavailable, it has been deleted from the platform.";
    public static final String PERMISSION_DENIED = "You do not have access to download the video, permission is denied.";
    public static final String DRIFTY_COMPONENT_NOT_EXECUTABLE = "A Drifty component (yt-dlp) is not marked as executable.";
    public static final String FAILED_TO_READ_DATA_STREAM = "Failed to get I/O operations channel to read from the data stream !";
    public static final String USER_INTERRUPTION = "User interrupted while downloading the YouTube/Instagram Video!";
    public static final String ENTER_Y_OR_N = "Please enter Y for yes and N for no!";
    public static final String DEFAULT_FILENAME = "%(title)s.%(ext)s";
    /*
    Denoting a Constant with _F indicates that it needs to be used with String.format(), where %s is replaced with the
    string provided in String.format():
        String.format(CONSTANT_F, "String replacing %s")

    Multiple %s values can be used where each one is replaced in the constant using String.format() with more commas:
        String.Format(CONSTANT_F, "String One", "String Two", "String Three); etc.
     */
    public static final String DOWNLOADING_F = "Downloading \"%s\" ...";
    public static final String TRYING_TO_DOWNLOAD_F = "Trying to download \"%s\" ...";
    public static final String WRITE_ACCESS_DENIED_F = "Write Access To \"%s\" DENIED!";
    public static final String FAILED_CONNECTION_F = "Failed to connect to %s!";
    public static final String SUCCESSFULLY_DOWNLOADED_F = "Successfully downloaded %s!";
    public static final String FAILED_TO_DOWNLOAD_F = "Failed to download %s!";
}
