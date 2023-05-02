package Utils;

import Backend.DefaultDownloadFolderLocationFinder;
import Backend.Drifty;

import java.net.*;
import java.util.Scanner;

import static Utils.DriftyConstants.*;

public final class Utility {
    private static final Scanner SC = ScannerFactory.getInstance();
    private Utility() {}

    static MessageBroker messageBroker = Drifty.getMessageBrokerInstance();
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
            URL url = URI.create(link).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Faster validation and hence improves performance
            connection.connect();
        } catch (ConnectException e){
            throw new Exception(e);
        } catch (UnknownHostException unknownHost){
            try {
                URL projectWebsite = URI.create(Drifty.projectWebsite).toURL();
                HttpURLConnection connectProjectWebsite = (HttpURLConnection) projectWebsite.openConnection();
                connectProjectWebsite.connect();
                throw new Exception("Link is invalid!"); // If our project website can be connected to, then the one entered by user is not a valid one! [NOTE: UnknownHostException is thrown if either internet is not connected or the website address is incorrect]
            } catch (UnknownHostException e) {
                throw new Exception("You are not connected to the Internet!");
            }
        }
    }

    /**
     * This method finds <b>the name of the file from the link</b> provided.
     * @param link The download link of the file to be downloaded.
     * @return the filename if it is detected else null.
     */
    public static String findFilenameInLink(String link) {
        // Check and inform user if the url contains filename.
        // Example : "example.com/file.txt" prints "Filename detected: file.txt"
        // example.com/file.json -> file.json
        String file = link.substring(link.lastIndexOf("/") + 1);
        int index = file.lastIndexOf(".");
        if (index < 0) {
            messageBroker.sendMessage(AUTO_FILE_NAME_DETECTION_FAILED, LOGGER_ERROR, "Filename");
            return null;
        }
        String extension = file.substring(index);
        // edge case 1 : "example.com/."
        if (extension.length() == 1) {
            messageBroker.sendMessage(AUTO_FILE_NAME_DETECTION_FAILED, LOGGER_ERROR, "Filename");
            return null;
        }
        // file.png?width=200 -> file.png
        String fileName = file.split("([?])")[0];
        messageBroker.sendMessage(FILENAME_DETECTED + fileName, LOGGER_INFO, "Filename");
        return fileName;
    }

    /**
     * This method finds the default downloads folder and create log accordingly.
     * @return The path of the default download folder.
     */
    public static String saveToDefault() {
        String downloadsFolder;
        messageBroker.sendMessage(TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER, LOGGER_INFO, "directory");
        if (!System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
            String home = System.getProperty(USER_HOME_PROPERTY);
            downloadsFolder = home + DOWNLOADS_FILE_PATH;
        } else {
            downloadsFolder = DefaultDownloadFolderLocationFinder.findPath() + System.getProperty("file.separator");
        }
        if (downloadsFolder.equals(System.getProperty("file.separator"))) {
            messageBroker.sendMessage(FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER, LOGGER_ERROR, "directory");
        } else {
            messageBroker.sendMessage(DEFAULT_DOWNLOAD_FOLDER + downloadsFolder, LOGGER_INFO, "directory");
        }
        return downloadsFolder;
    }

    /**
     * This method performs Yes-No validation and returns the boolean value accordingly.
     * @param input        Input String to validate.
     * @param printMessage The message to print to re-input the confirmation.
     * @return true if the user enters Y [Yes] and false if not.
     */
    public static boolean yesNoValidation(String input, String printMessage) {
        while (input.length() == 0) {
            System.out.println(ENTER_Y_OR_N);
            messageBroker.sendMessage(ENTER_Y_OR_N, LOGGER_ERROR, "only log");
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
        }
        char choice = input.charAt(0);
        if (choice == 'y') {
            return true;
        } else if (choice == 'n') {
            return false;
        } else {
            System.out.println("Invalid input!");
            messageBroker.sendMessage("Invalid input!", LOGGER_ERROR, "only log");
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
            yesNoValidation(input, printMessage);
        }
        return false;
    }

    /**
     * This is the help method of Drifty that gets printed in the console when correct help flag has been passed as a parameter to Drifty CLI.
     */
    public static void help() {
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m----==| DRIFTY CLI HELP |==----" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m            " + VERSION_NUMBER + ANSI_RESET);
        System.out.println("For more information visit: ");
        System.out.println("\tProject Link - https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\tProject Website - " + Drifty.projectWebsite);
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
