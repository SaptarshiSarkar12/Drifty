package Utils;

import Backend.DefaultDownloadFolderLocationFinder;
import Backend.Drifty;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import static Utils.DriftyConstants.*;

public final class DriftyUtility {

    private DriftyUtility() {}

    static CreateLogs logger = CreateLogs.getInstance();
    /**
     * This method checks whether the link provided is of YouTube or not and returns the resultant boolean value accordingly.
     * @param url link to the file to be downloaded.
     * @return true if the url is of YouTube and false if it is not.
     */
    public static boolean isYoutubeLink(String url) {
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        return url.matches(pattern);
    }

    /**
     * @param link Link to the file that the user wants to download
     * @throws Exception if URL is not valid or cannot be connected to, then this Exception is thrown with proper message
     */
    public static void isURLValid(String link) throws Exception {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Faster validation and hence improves performance
            connection.connect();
        } catch (ConnectException e){
            throw new Exception(e);
        } catch (UnknownHostException unknownHost){
            try {
                URL projectWebsite = new URL(Drifty.projectWebsite);
                HttpURLConnection connectProjectWebsite = (HttpURLConnection) projectWebsite.openConnection();
                connectProjectWebsite.connect();
                throw new Exception("Link is invalid!"); // If our project website can be connected to, then the one entered by user is not a valid one! [NOTE: UnknownHostException is thrown if either internet is not connected or the website address is incorrect]
            } catch (UnknownHostException e) {
                throw new Exception("You are not connected to the Internet!");
            }
        }
    }

    /**
     * @return the filename if it is detected else null
     */
    public static String findFilenameInLink(String link) {
        // Check and inform user if the url contains filename.
        // Example : "example.com/file.txt" prints "Filename detected: file.txt"
        // example.com/file.json -> file.json
        String file = link.substring(link.lastIndexOf("/") + 1);
        int index = file.lastIndexOf(".");
        if (index < 0) {
            return null;
        }
        String extension = file.substring(index);
        // edge case 1 : "example.com/."
        if (extension.length() == 1) {
            return null;
        }
        // file.png?width=200 -> file.png
        String fileName = file.split("([?])")[0];
        logger.log(LOGGER_INFO, FILENAME_DETECTED + fileName);
        return fileName;
    }

    public static String saveToDefault() {
        String downloadsFolder;
        logger.log(LOGGER_INFO, TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER);
        if (!System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
            String home = System.getProperty(USER_HOME_PROPERTY);
            downloadsFolder = home + DOWNLOADS_FILE_PATH;
        } else {
            downloadsFolder = DefaultDownloadFolderLocationFinder.findPath() + System.getProperty("file.separator");
        }
        if (downloadsFolder.equals(System.getProperty("file.separator"))) {
            logger.log(LOGGER_ERROR, FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER);
        } else {
            logger.log(LOGGER_INFO, DEFAULT_DOWNLOAD_FOLDER + downloadsFolder);
        }
        return downloadsFolder;
    }

    public static void help() {
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m----==| DRIFTY CLI HELP |==----" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m            v2.0.0" + ANSI_RESET);
        System.out.println("For more information visit: https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\033[31;1mRequired parameter: File URL" + ANSI_RESET + " \033[3m(This must be the first argument you are passing)" + ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default     Description" + ANSI_RESET);
        System.out.println("-location   -l            Downloads                   The location on your computer where content downloaded using Drifty are placed.");
        System.out.println("-name       -n            Source                      Filename of the downloaded file.");
        System.out.println("-help       -h            N/A                         Provides concise information for Drifty CLI.\n");
        System.out.println("-version    -v            Current Version Number      Displays version number of Drifty.");
        System.out.println("\033[97;1mExample:" + ANSI_RESET + " \n> \033[37;1mjava Drifty_CLI https://example.com/object.png -n obj.png -l C:/Users/example" + ANSI_RESET);
        System.out.println("\033[37;3m* Requires java 18 or higher. \n" + ANSI_RESET);
    }

    /**
     * This function prints the banner of the application in the console.
     */
    public static void printBanner() {
        System.out.print("\033[H\033[2J");
        System.out.println(ANSI_PURPLE + BANNER_BORDER + ANSI_RESET);
        System.out.println(ANSI_CYAN + "  _____   _____   _____  ______  _______ __     __" + ANSI_RESET);
        System.out.println(ANSI_CYAN + " |  __ \\ |  __ \\ |_   _||  ____||__   __|\\ \\   / /" + ANSI_RESET);
        System.out.println(ANSI_CYAN + " | |  | || |__) |  | |  | |__      | |    \\ \\_/ /" + ANSI_RESET);
        System.out.println(ANSI_CYAN + " | |  | ||  _  /   | |  |  __|     | |     \\   / " + ANSI_RESET);
        System.out.println(ANSI_CYAN + " | |__| || | \\ \\  _| |_ | |        | |      | |  " + ANSI_RESET);
        System.out.println(ANSI_CYAN + " |_____/ |_|  \\_\\|_____||_|        |_|      |_|  " + ANSI_RESET);
        System.out.println(ANSI_PURPLE + BANNER_BORDER + ANSI_RESET);
    }

    /**
     * This method prints the banner without any colour of text except white.
     */
    public static void initialPrintBanner() {
        System.out.println(BANNER_BORDER);
        System.out.println("  _____   _____   _____  ______  _______ __     __");
        System.out.println(" |  __ \\ |  __ \\ |_   _||  ____||__   __|\\ \\   / /");
        System.out.println(" | |  | || |__) |  | |  | |__      | |    \\ \\_/ /");
        System.out.println(" | |  | ||  _  /   | |  |  __|     | |     \\   / ");
        System.out.println(" | |__| || | \\ \\  _| |_ | |        | |      | |  ");
        System.out.println(" |_____/ |_|  \\_\\|_____||_|        |_|      |_|  ");
        System.out.println(BANNER_BORDER);
    }
}
