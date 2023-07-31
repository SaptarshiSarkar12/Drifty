package Utils;

import Backend.DefaultDownloadFolderLocationFinder;
import Backend.Drifty;
import Enums.Category;
import Enums.OS;
import Enums.Program;
import Enums.Type;
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
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utils.DriftyConstants.*;

public final class Utility {

    MessageBroker message;
    private static final Scanner SC = ScannerFactory.getInstance();
    private static final Random random = new Random(System.currentTimeMillis());
    private static Process linkProcess;
    private static Thread linkThread;
    private static boolean interrupted;


    public Utility(MessageBroker messageBroker) {
        message = messageBroker;
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
     * @throws Exception if URL is not valid or cannot be connected to, then this Exception is thrown with proper message
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
                throw new Exception("Link is invalid!"); // If our project website can be connected to, then the one entered by user is not a valid one! [NOTE: UnknownHostException is thrown if either internet is not connected or the website address is incorrect]
            } catch (UnknownHostException e) {
                throw new Exception("You are not connected to the Internet!");
            }
        }
    }

    /**
     * This method finds <b>the name of the file from the link</b> provided.
     *
     * @param link The download link of the file to be downloaded.
     * @return the filename if it is detected else null.
     */
    public String findFilenameInLink(String link) {
        // Check and inform user if the url contains filename.
        // Example : "example.com/file.txt" prints "Filename detected: file.txt"
        // example.com/file.json -> file.json
        String file = link.substring(link.lastIndexOf("/") + 1);
        int index = file.lastIndexOf(".");
        if (index < 0) {
            message.send(AUTO_FILE_NAME_DETECTION_FAILED, Type.ERROR, Category.FILENAME);
            return null;
        }
        String extension = file.substring(index);
        // edge case 1 : "example.com/."
        if (extension.length() == 1) {
            message.send(AUTO_FILE_NAME_DETECTION_FAILED, Type.ERROR, Category.FILENAME);
            return null;
        }
        // file.png?width=200 -> file.png
        String fileName = file.split("([?])")[0];
        message.send(FILENAME_DETECTED + fileName, Type.INFORMATION, Category.FILENAME);
        return fileName;
    }

    /**
     * This method finds the default downloads folder and create log accordingly.
     *
     * @return The path of the default download folder.
     */
    public String saveToDefault() {
        String downloadsFolder;
        message.send(TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER, Type.INFORMATION, Category.DIRECTORY);
        if (!OS.isWindows()) {
            String home = System.getProperty(USER_HOME_PROPERTY);
            downloadsFolder = home + DOWNLOADS_FILE_PATH;
        }
        else {
            downloadsFolder = DefaultDownloadFolderLocationFinder.findPath() + System.getProperty("file.separator");
        }
        if (downloadsFolder.equals(System.getProperty("file.separator"))) {
            message.send(FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER, Type.ERROR, Category.DIRECTORY);
        }
        else {
            message.send(DEFAULT_DOWNLOAD_FOLDER + downloadsFolder, Type.INFORMATION, Category.DIRECTORY);
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
        while (input.length() == 0) {
            System.out.println(ENTER_Y_OR_N);
            message.send(ENTER_Y_OR_N, Type.ERROR, Category.LOG);
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
            message.send("Invalid input!", Type.ERROR, Category.LOG);
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
            yesNoValidation(input, printMessage);
        }
        return false;
    }

    /**
     * This is the help method of Drifty that gets printed in the console when correct help flag has been passed as a parameter to Drifty CLIString.
     */
    public static void help() {
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m------------==| DRIFTY CLIString HELP |==------------" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m                    " + VERSION_NUMBER + ANSI_RESET);
        System.out.println("\033[31;1mRequired parameter: File URL" + ANSI_RESET + " \033[3m(This must be the first argument you are passing unless you are using Batch Downloading)" + ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default                  Description" + ANSI_RESET);
        System.out.println("-batch      -b            N/A                      The path to the yaml/yml file containing the links and other arguments.");
        System.out.println("-location   -l            Downloads                The location on your computer where content downloaded using Drifty are placed.");
        System.out.println("-name       -n            Source                   Filename of the downloaded file.");
        System.out.println("-help       -h            N/A                      Provides concise information for Drifty CLIString.");
        System.out.println("-version    -v            Current Version          Displays version number of Drifty.");
        System.out.println("\033[97;1mSee full documentation at https://github.com/SaptarshiSarkar12/Drifty#readme" + ANSI_RESET);
        System.out.println("\033[97;1mExample:" + ANSI_RESET + " \n> \033[37;1mjava Drifty_CLI https://example.com/object.png -n obj.png -l C:/Users/example" + ANSI_RESET);
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


    /*
    These classes are used by the JavaFX classes
     */

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

    public static LinkedList<String> getJsonLinkMetadata(String link) {
        try {
            LinkedList<String> list = new LinkedList<>();
            File folder = Paths.get(Program.get(Program.PATH), "Drifty").toFile();
            if (folder.exists() && folder.isDirectory()) {
                FileUtils.forceDelete(folder);
            }
            folder.mkdir();
            linkThread = new Thread(linkScan(folder.getAbsolutePath(), link));
            linkThread.start();
            while ((linkThread.getState().equals(Thread.State.RUNNABLE) || linkThread.getState().equals(Thread.State.TIMED_WAITING)) && !linkThread.isInterrupted()) {
                sleep(100);
                interrupted = linkThread.isInterrupted();
            }
            if (interrupted) {
                FileUtils.forceDelete(folder);
                return null;
            }
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String ext = FilenameUtils.getExtension(file.getAbsolutePath());
                    if (ext.toLowerCase().contains("json")) {
                        String json = FileUtils.readFileToString(file, Charset.defaultCharset());
                        list.addLast(json);
                    }
                }
                FileUtils.forceDelete(folder);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        }
        else {
            filename = "Unknown Filename";
        }
        return cleanFilename(filename);
    }

    public static String cleanFilename(String filename) {
        String fn = StringEscapeUtils.unescapeJava(filename);
        return fn.replaceAll("[^a-zA-Z0-9-._ ]+", "");
    }

    private static Runnable linkScan(String folderPath, String link) {
        return () -> {
            String command = Program.get(Program.COMMAND);
            String[] args = new String[]{"--write-info-json", "--skip-download", "--restrict-filenames", "-P", folderPath, link};
            new ProcBuilder(command).withArgs(args)
                    .withOutputStream(System.out)
                    .withErrorStream(System.err)
                    .withNoTimeout()
                    .run();
        };
    }

    private static void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanTemps() {
        File user = new File(System.getProperty("user.home"));
        File[] files = user.listFiles();
        for (File file : files) {
            String name = FilenameUtils.getName(file.getAbsolutePath());
            if (name.matches("\\.Drifty_\\w{3,}") && file.isDirectory()) {
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
