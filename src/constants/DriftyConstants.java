package constants;

public final class DriftyConstants {

    public static final String VERSION_NUMBER = "v1.2.2";
    public static final String APPLICATION_NAME = "Drifty";
    public static final String DRIFTY_CLI_LOG = "Drift_CLI_LOG.log";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String DOWNLOADS_FILE_PATH = "/Downloads/";
    public static final String LOGGER_INFO = "INFO";
    public static final String LOGGER_ERROR = "ERROR";
    public static final String APPLICATION_STARTED = "Application Started !";
    public static final String APPLICATION_TERMINATED = "Application Terminated!";
    public static final String INVALID_URL = "URL is invalid!";
    public static final String INVALID_LINK = "Invalid Link!";
    public static final String INVALID_URL_ENTER_AGAIN = "Invalid URL. Please enter again";
    public static final String INVALID_DIRECTORY = "Invalid Directory Entered !";
    public static final String AUTOMATIC_FILE_DETECTION = "Automatic file name detection failed!";
    public static final String TRYING_TO_AUTO_DETECT_FILE = "Trying to auto-detect default Downloads folder...";
    public static final String TRYING_TO_DOWNLOAD_FILE = "Trying to download the file ...";
    public static final String HELP_FLAG = "-help";
    public static final String NAME_FLAG = "-name";
    public static final String VERSION_FLAG = "-version";
    public static final String LOCATION_FLAG = "-location";
    public static final String HELP_FLAG_SHORT = "-h";
    public static final String NAME_FLAG_SHORT = "-n";
    public static final String VERSION_FLAG_SHORT = "-v";
    public static final String LOCATION_FLAG_SHORT = "-l";
    public static final String FILE_NAME_WITH_EXTENSION = "Enter the filename (with file extension) : ";
    public static final String FILE_LINK = "Enter the link to the file : ";
    public static final String OS_NAME = "os.name";
    public static final String WINDOWS_OS_NAME = "Windows";
    public static final String USER_HOME_PROPERTY = "user.home";
    public static final String DOWNLOAD_DEFAULT_LOCATION = "Do you want to download the file in your default downloads folder? (Enter Y for yes and N for no) : ";
    public static final String RENAME_FILE = "Would you like to rename this file? (Enter Y for yes and N for no) : ";
    public static final String QUIT_OR_CONTINUE = "Press Q to Quit Or Press any Key to Continue";
    public static final String DIRECTORY_TO_DOWNLOAD_FILE = "Enter the directory in which you want to download the file : ";
    public static final String FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER = "Failed to retrieve default download folder!";
    public static final String FAILED_TO_CONNECT_TO_URL = "Failed to connect to ";
    public static final String DEFAULT_DOWNLOAD_FOLDER = "Default download folder detected : ";
    public static final String FILENAME_DETECTED = "Filename detected : ";
    public static final String BANNER_BORDER = "====================================================================";
    public static final String FAILED_TO_CREATE_LOG = "Failed to create log : ";
    public static final String FAILED_TO_CLEAR_LOG = "Failed to clear Log contents !";
    public static final String FAILED_TO_CREATE_DIRECTORY = "Failed to create the directory : ";

    public static final String DOWNLOADED = "Downloaded ";
    public static final String DOWNLOADING = "Downloading ";
    public static final String DOWNLOAD_FAILED = "Download failed!";
    public static final String SUCCESSFULLY = " successfully !";
    public static final String SUCCESSFULLY_DOWNLOADED_FILE = "Successfully downloaded the file!";
    public static final String OFF_SIZE = " of size ";
    public static final String DIRECTORY_CREATED = "Directory Created";
    public static final String ERROR_WHILE_CHECKING_FOR_DIRECTORY = "Error while checking for directory !";

    public static final String FAILED_TO_DOWNLOAD_FILES = "Failed to download the file!";
    public static final String FAILED_TO_DOWNLOAD_CONTENTS = "Failed to download the contents ! ";
    public static final String FAILED_TO_READ_DATA_STREAM = "Failed to get I/O operations channel to read from the data stream !";

    public static final String FAILED_TO_DOWNLOAD_YOUTUBE_VIDEO = "Failed to download YouTube video!";
    public static final String FAILED_TO_INITIALISE_YOUTUBE_VIDEO = "Failed to initialise YouTube video downloader!";
    public static final String THREAD_ERROR_ENCOUNTERED = "Error: thread encountered an error";

    public static final String GETTING_READY_TO_DOWNLOAD_FILE = "Getting ready to download the file...";
    public static final String USER_INTERRUPTION = "User interrupted while downloading the file!";

    public static final String ENTER_Y_OR_N = "Please enter Y for yes and N for no!";

    private DriftyConstants() {
    }


}
