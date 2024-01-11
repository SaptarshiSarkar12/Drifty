package cli.support;

public class Constants extends support.Constants {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String CLI_APPLICATION_STARTED = "Drifty CLI (Command Line Interface) Application Started !";
    public static final String CLI_APPLICATION_TERMINATED = "Drifty CLI (Command Line Interface) Application Terminated!";
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
    public static final String ENTER_FILE_LINK = "Enter the link to the file (in the form of https://www.example.com/filename.extension) or a YouTube/Instagram Video link : ";
    public static final String QUIT_OR_CONTINUE = "Enter Q to Quit Or any other key to Continue";

    public static final String BANNER_BORDER = "====================================================================";
    public static final String FAILED_TO_DOWNLOAD_CONTENTS = "Failed to download the contents ! ";
    public static final String FAILED_READING_STREAM = "Failed to get I/O operations channel to read from the data stream !";
    public static final String DEFAULT_FILENAME = "%(title)s.%(ext)s";
    public static final String SUCCESSFULLY_DOWNLOADED = "Successfully downloaded ";
    public static final String OF_SIZE = " of size ";
    public static final String DOWNLOAD_FAILED = "Download failed!";
    public static final String USER_INTERRUPTION = "User interrupted while downloading the YouTube/Instagram Video!";
    public static final String YOUTUBE_DOWNLOAD_FAILED = "Failed to download YouTube video!";
    public static final String INSTAGRAM_DOWNLOAD_FAILED = "Failed to download Instagram video!";
    public static final String ENTER_Y_OR_N = "Please enter Y for yes and N for no!";
}
