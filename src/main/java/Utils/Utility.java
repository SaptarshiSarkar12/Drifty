package Utils;

import Backend.DefaultDownloadFolderLocationFinder;
import Backend.Drifty;
import Enums.DriftyConfig;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.OS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.buildobjects.process.ProcBuilder;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utils.DriftyConstants.*;

/**
 * This is the class for the utility methods used by Drifty CLI as well as Drifty GUI
 */
public final class Utility {
    static MessageBroker messageBroker;
    private static final Scanner SC = ScannerFactory.getInstance();
    private static Thread linkThread;
    private static boolean interrupted;
    private static long startTime;

    public static void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    public static long timeSinceStart() {
        return System.currentTimeMillis() - startTime;
    }

    public Utility(MessageBroker messageBroker) {
        Utility.messageBroker = messageBroker;
    }

    /**
     * This method checks whether the link provided is of YouTube or not and returns the resultant boolean value accordingly.
     *
     * @param url link to the file to be downloaded
     * @return true if the url is of YouTube and false if it is not.
     */
    public static boolean isYoutubeLink(String url) {
        String pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+";
        return url.matches(pattern);
    }

    /**
     * This method checks whether the link provided is of Instagram or not and returns the resultant boolean value accordingly.
     *
     * @param url link to the file to be downloaded
     * @return true if the url is of Instagram and false if it is not.
     */
    public static boolean isInstagramLink(String url) {
        String pattern = "(https?://(?:www\\.)?instagr(am|.am)?(\\.com)?/p/([^/?#&]+)).*";
        return url.matches(pattern);
    }

    /**
     * @param link Link to the file that the user wants to download
     * @throws Exception if the URL is not valid or cannot be connected to, then this Exception is thrown with proper message
     */
    public static void isURLValid(String link) throws Exception {
        try {
            URL url = URI.create(link).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Faster validation and hence improves performance
            connection.connect();
        } catch (ConnectException e) {
            throw new Exception(e);
        } catch (UnknownHostException unknownHost) {
            try {
                URL projectWebsite = URI.create(Drifty.projectWebsite).toURL();
                HttpURLConnection connectProjectWebsite = (HttpURLConnection) projectWebsite.openConnection();
                connectProjectWebsite.connect();
                throw new Exception("Link is invalid!"); // If our project website can be connected to, then the one entered by user is not valid! [NOTE: UnknownHostException is thrown if either internet is not connected or the website address is incorrect]
            } catch (UnknownHostException e) {
                throw new Exception("You are not connected to the Internet!");
            }
        }
    }

    public static boolean urlIsValid(String link) {
        try {
            URL url = URI.create(link).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Faster validation and hence improves performance
            connection.connect();
            URL projectWebsite = URI.create(Drifty.projectWebsite).toURL();
            HttpURLConnection connectProjectWebsite = (HttpURLConnection) projectWebsite.openConnection();
            connectProjectWebsite.connect();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * This method finds <b>the name of the file from the link</b> provided.
     *
     * @param link The download link of the file to be downloaded.
     * @return the filename if it is detected else null.
     */
    public String findFilenameInLink(String link) {
        // Check and inform user if the url contains filename.
        // Example: "example.com/file.txt" prints "Filename detected: file.txt"
        // example.com/file.json -> file.json
        String file = link.substring(link.lastIndexOf("/") + 1);
        int index = file.lastIndexOf(".");
        if (index < 0) {
            messageBroker.sendMessage(AUTO_FILE_NAME_DETECTION_FAILED, MessageType.ERROR, MessageCategory.FILENAME);
            return null;
        }
        String extension = file.substring(index);
        // edge case 1 : "example.com/."
        if (extension.length() == 1) {
            messageBroker.sendMessage(AUTO_FILE_NAME_DETECTION_FAILED, MessageType.ERROR, MessageCategory.FILENAME);
            return null;
        }
        // file.png?width=200 -> file.png
        String fileName = file.split("([?])")[0];
        messageBroker.sendMessage(FILENAME_DETECTED + fileName, MessageType.INFORMATION, MessageCategory.FILENAME);
        return fileName;
    }

    /**
     * This method finds the default downloads folder and creates log accordingly.
     *
     * @return The path of the default download folder.
     */
    public String saveToDefault() {
        String downloadsFolder;
        messageBroker.sendMessage(TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER, MessageType.INFORMATION, MessageCategory.DIRECTORY);
        if (!OS.isWindows()) {
            String home = System.getProperty(USER_HOME_PROPERTY);
            downloadsFolder = home + DOWNLOADS_FILE_PATH;
        }
        else {
            downloadsFolder = DefaultDownloadFolderLocationFinder.findPath() + System.getProperty("file.separator");
        }
        if (downloadsFolder.equals(System.getProperty("file.separator"))) {
            messageBroker.sendMessage(FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER, MessageType.ERROR, MessageCategory.DIRECTORY);
        }
        else {
            messageBroker.sendMessage(DEFAULT_DOWNLOAD_FOLDER + downloadsFolder, MessageType.INFORMATION, MessageCategory.DIRECTORY);
        }
        return downloadsFolder;
    }

    /**
     * This method performs Yes-No validation and returns the boolean value accordingly.
     *
     * @param input        Input String to validate.
     * @param printMessage The message to print to re-input the confirmation.
     * @return true if the user enters Y [Yes] and false if not.
     */
    public boolean yesNoValidation(String input, String printMessage) {
        while (input.isEmpty()) {
            System.out.println(ENTER_Y_OR_N);
            messageBroker.sendMessage(ENTER_Y_OR_N, MessageType.ERROR, MessageCategory.LOG);
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
        }
        char choice = input.charAt(0);
        if (choice == 'y') {
            return true;
        }
        else if (choice == 'n') {
            return false;
        }
        else {
            System.out.println("Invalid input!");
            messageBroker.sendMessage("Invalid input!", MessageType.ERROR, MessageCategory.LOG);
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
            yesNoValidation(input, printMessage);
        }
        return false;
    }

    /**
     * This is the help method of Drifty that gets printed in the console when the correct help flag has been passed as a parameter to Drifty CLI.
     */
    public static void help() {
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m------------==| DRIFTY CLI HELP |==------------" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m                    " + VERSION_NUMBER + ANSI_RESET);
        System.out.println("\033[31;1mRequired parameter: File URL" + ANSI_RESET + " \033[3m(This must be the first argument you are passing unless you are using Batch Downloading)" + ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default                  Description" + ANSI_RESET);
        System.out.println("-batch      -b            N/A                      The path to the yaml/yml file containing the links and other arguments.");
        System.out.println("-location   -l            Downloads                The location on your computer where content downloaded using Drifty are placed.");
        System.out.println("-name       -n            Source                   Filename of the downloaded file.");
        System.out.println("-help       -h            N/A                      Provides concise information for Drifty CLI.");
        System.out.println("-version    -v            Current Version          Displays version number of Drifty.");
        System.out.println("\033[97;1mSee full documentation at https://github.com/SaptarshiSarkar12/Drifty#readme" + ANSI_RESET);
        System.out.println("\033[97;1mExample:" + ANSI_RESET + " \n> \033[37;1mjava DriftyCLI https://example.com/object.png -n obj.png -l C:/Users/example" + ANSI_RESET);
        System.out.println("\033[37;3m* Requires java 20 or higher. \n" + ANSI_RESET);
        System.out.println("For more information visit: ");
        System.out.println("\tProject Link - https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\tProject Website - " + Drifty.projectWebsite);
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
     * <p><u>Returns calculated multiple</u></p> from current X, Y using supplied multiplier
     *
     * @param currentX   - current X value
     * @param currentY   - current Y value
     * @param multiplier - Use a fraction to reduce and larger than one to increase
     * @return double array with X, Y [0, 1]
     */
    public static double[] fraction(double currentX, double currentY, double multiplier) {
        double[] result = new double[2];
        result[0] = currentX * multiplier;
        result[1] = currentY * multiplier;
        return result;
    }

    /**
     * This method is used to get the metadata in LinkedList String format from a json file generated by {@link #getYT_IGLinkMetadata(String, String)}
     *
     * @param link link to the YouTube or Instagram video
     * @return a LinkedList of type String
     */
    public static LinkedList<String> getLinkMetadata(String link) {
        try {
            LinkedList<String> list = new LinkedList<>();
            File tempFolder = Paths.get(DriftyConfig.getConfig(DriftyConfig.PATH), "Drifty").toFile();
            if (tempFolder.exists() && tempFolder.isDirectory()) {
                FileUtils.forceDelete(tempFolder); // Deletes the previously generated temporary directory for Drifty
            }
            tempFolder.mkdir();
            linkThread = new Thread(getYT_IGLinkMetadata(tempFolder.getAbsolutePath(), link));
            linkThread.start();
            while ((linkThread.getState().equals(Thread.State.RUNNABLE) || linkThread.getState().equals(Thread.State.TIMED_WAITING)) && !linkThread.isInterrupted()) {
                sleep(100);
                interrupted = linkThread.isInterrupted();
            }
            if (interrupted) {
                FileUtils.forceDelete(tempFolder);
                return null;
            }
            File[] files = tempFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String ext = FilenameUtils.getExtension(file.getAbsolutePath());
                    if (ext.toLowerCase().contains("json")) {
                        String linkMetadata = FileUtils.readFileToString(file, Charset.defaultCharset());
                        list.addLast(linkMetadata);
                    }
                }
                FileUtils.forceDelete(tempFolder); // delete the metadata files of Drifty from the temp directory
            }
            return list;
        } catch (IOException e) {
            messageBroker.sendMessage("Failed to perform I/O operations on link metadata! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
            return null;
        }
    }

    public static String getURLFromJson(String json) {
        String regexLink = "(\"webpage_url\": \")(.+)(\")";
        String urlLink = "";
        Pattern p = Pattern.compile(regexLink);
        Matcher m = p.matcher(json);
        if (m.find()) {
            urlLink = StringEscapeUtils.unescapeJava(m.group(2));
        }
        return urlLink;
    }

    public static String getFilenameFromJson(String json) {
        String filename;
        String regexFilename = "(\"title\": \")(.+)(\",)";
        Pattern p = Pattern.compile(regexFilename);
        Matcher m = p.matcher(json);
        if (m.find()) {
            filename = m.group(2);
        } else {
            filename = "Unknown Filename";
        }
        return cleanFilename(filename);
    }

    /**
     * This method filters the filename and removes any illegal characters not supported by the operating system
     *
     * @param filename filename as detected or as provided by the user
     * @return the formatted and filtered string
     */
    public static String cleanFilename(String filename) {
        String fn = StringEscapeUtils.unescapeJava(filename);
        return fn.replaceAll("[^a-zA-Z0-9-._ ]+", "");
    }

    /**
     * This method provides a Runnable object to get the metadata of a YouTube or Instagram Link,
     * containing information about Video Title, etc.
     *
     * @param folderPath Path to the temporary folder based on Operating System
     * @param link       the link to the YouTube or Instagram video
     * @return a Runnable object with the metadata of the YouTube or Instagram link
     */
    private static Runnable getYT_IGLinkMetadata(String folderPath, String link) {
        return () -> {
            String command = DriftyConfig.getConfig(DriftyConfig.YT_DLP_COMMAND);
            String[] args = new String[]{"--write-info-json", "--skip-download", "--restrict-filenames", "-P", folderPath, link};
            new ProcBuilder(command).withArgs(args)
                    .withOutputStream(System.out)
                    .withErrorStream(System.err)
                    .withNoTimeout()
                    .run();
        };
    }

    /**
     * This method is used to make the calling method to wait for the time in millisecond passed
     * @param time the time to make the calling thread to keep waiting
     */
    public static void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            messageBroker.sendMessage("The calling method failed to sleep for " + time + " milliseconds. It got interrupted. " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        }
    }
}
