package CLI;

import Backend.Drifty;
import Enums.Category;
import Enums.OS;
import Enums.Type;
import GUI.Launcher;
import Utils.*;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static Utils.DriftyConstants.*;
import static Utils.Utility.*;

/**
 * This is the main class for the CLIString (Command Line Interface) version of Drifty.
 *
 * @version 2.0.0
 */
public class Drifty_CLI {
    /**
     * Logger instance for the CLIString version of Drifty
     */
    public static final Logger logger = Logger.getInstance();
    /**
     * Scanner instance for the CLIString of Drifty
     */
    protected static final Scanner SC = ScannerFactory.getInstance();
    /**
     * Boolean value which determines if the given link is an YouTube video URL or not
     */
    protected static boolean isYoutubeURL;
    /**
     * Boolean value which determines if the given link is an Instagram media URL or not
     */
    protected static boolean isInstagramLink;
    /**
     * Boolean value which determines if the given link is an Instagram image URL or not
     */
    protected static boolean isInstagramImage;
    /**
     * Message broker instance which helps to send messages to the output stream
     */
    private static MessageBroker message;
    private static String link;
    private static Utility utility;
    private static String directory;
    private static String fileName = null;
    /**
     * Boolean value which determines if batch downloading is triggered by the user
     */
    private static boolean batchDownloading;
    /**
     * The path of the YAML/YML data file which contains the required data, in String format
     */
    private static String batchDownloadingFile;

    /**
     * This function is the main method of the whole application.
     *
     * @param args Command Line Arguments as a String array.
     */
    public static void main(String[] args) {
        logger.log(Type.INFORMATION, CLI_APPLICATION_STARTED);
        message = new MessageBroker(System.out);
        utility = new Utility(message);
        printBanner();
        String downloadsFolder;
        if (args.length > 0) {
            link = args[0];
            String name = null;
            String location = null;
            for (int i = 0; i < args.length; i++) {
                if (TheseStrings.areEqual(args[i], HELP_FLAG) || TheseStrings.areEqual(args[i], HELP_FLAG_SHORT)) {
                    help();
                    System.exit(0);
                }
                else if (TheseStrings.areEqual(args[i], GUI_FLAG) || (TheseStrings.areEqual(args[i], GUI_FLAG_SHORT))) {
                    Launcher.main(args);
                    return;
                }
                else if (TheseStrings.areEqual(args[i], NAME_FLAG) || (TheseStrings.areEqual(args[i], NAME_FLAG_SHORT))) {
                    name = args[i + 1];
                }
                else if (TheseStrings.areEqual(args[i], LOCATION_FLAG) || (TheseStrings.areEqual(args[i], LOCATION_FLAG_SHORT))) {
                    location = args[i + 1];
                }
                else if (TheseStrings.areEqual(args[i], VERSION_FLAG) || (TheseStrings.areEqual(args[i], VERSION_FLAG_SHORT))) {
                    System.out.println(APPLICATION_NAME + " " + VERSION_NUMBER);
                    System.exit(0);
                }
                else if ((TheseStrings.areEqual(args[i], BATCH_FLAG)) || (TheseStrings.areEqual(args[i], BATCH_FLAG_SHORT))) {
                    batchDownloading = true;
                    batchDownloadingFile = args[i + 1];
                    batchDownloader();
                }
            }
            if (!batchDownloading) {
                isYoutubeURL = isYoutubeLink(link);
                isInstagramLink = isInstagramLink(link);
                fileName = (name == null) ? fileName : name;
                if (!isYoutubeURL && !isInstagramLink) {
                    fileName = utility.findFilenameInLink(link);
                }
                takeFileNameInputIfNull();
                downloadsFolder = location;
                if (downloadsFolder == null) {
                    downloadsFolder = utility.saveToDefault();
                }
                else {
                    if (OS.isWindows()) {
                        downloadsFolder = downloadsFolder.replace('/', '\\');
                        if (!(downloadsFolder.endsWith("\\"))) {
                            downloadsFolder = downloadsFolder + System.getProperty("file.separator");
                        }
                    }
                }
                Drifty backend = new Drifty(link, downloadsFolder, fileName, System.out);
                backend.start();
            }
            logger.log(Type.INFORMATION, CLI_APPLICATION_TERMINATED);
            System.exit(0);
        }
        while (true) {
            while (true) {
                System.out.println("Select download option :");
                System.out.println("\t1. Batch Download (Download Multiple files)");
                System.out.println("\t2. Single File Download (Download One file at a time)");
                int choice = SC.nextInt();
                if (choice == 1) {
                    batchDownloading = true;
                    System.out.print("Enter the path to the YAML data file : ");
                    batchDownloadingFile = SC.next();
                    SC.nextLine();
                    if (!(batchDownloadingFile.endsWith(".yml") || batchDownloadingFile.endsWith(".yaml"))) {
                        message.send("The given file should be a YAML file!", Type.ERROR, Category.LOG);
                    }
                    else {
                        batchDownloader();
                        break;
                    }
                }
                else if (choice == 2) {
                    batchDownloading = false;
                    break;
                }
                else {
                    System.out.println("Invalid Input!");
                }
            }
            if (!batchDownloading) {
                System.out.print(ENTER_FILE_LINK);
                String link = SC.next();
                SC.nextLine();
                System.out.print("Enter the download directory (Enter \".\" for default downloads folder) : ");
                downloadsFolder = SC.next();
                isYoutubeURL = isYoutubeLink(link);
                isInstagramLink = isInstagramLink(link);
                if (!isYoutubeURL && !isInstagramLink) {
                    fileName = utility.findFilenameInLink(link);
                }
                takeFileNameInputIfNull();
                Drifty backend = new Drifty(link, downloadsFolder, fileName, System.out);
                backend.start();
            }
            System.out.println(QUIT_OR_CONTINUE);
            String choice = SC.next().toLowerCase();
            if (choice.equals("q")) {
                logger.log(Type.INFORMATION, CLI_APPLICATION_TERMINATED);
                break;
            }
            printBanner();
        }
    }

    /**
     * This method deals with downloading multiple files with a single click using a YAML/YML file, the path of which is provided by the user.
     */
    private static void batchDownloader() {
        Yaml yamlInstance = new Yaml();
        try {
            message.send("Trying to load YAML data file (" + batchDownloadingFile + ") ...", Type.INFORMATION, Category.LOG);
            InputStreamReader yamlDataFile = new InputStreamReader(new FileInputStream(batchDownloadingFile));
            Map<String, List<String>> data = yamlInstance.load(yamlDataFile);
            message.send("YAML data file (" + batchDownloadingFile + ") loaded successfully", Type.INFORMATION, Category.LOG);
            int numberOfLinks = data.get("links").size();
            int numberOfFileNames = data.get("fileNames").size();
            int numberOfDirectories = 0;
            if (data.containsKey("directory")) {
                numberOfDirectories = 1;
                if (data.get("directory").get(0).length() == 0) {
                    directory = ".";
                }
                else {
                    directory = data.get("directory").get(0);
                }
            }
            else if (data.containsKey("directories")) {
                numberOfDirectories = data.get("directories").size();
            }
            String linkMessage;
            if (numberOfLinks == 1) {
                linkMessage = numberOfLinks + " link";
            }
            else {
                linkMessage = numberOfLinks + " links";
            }
            String directoryMessage;
            if (numberOfDirectories == 1) {
                directoryMessage = numberOfDirectories + " directory";
            }
            else {
                directoryMessage = numberOfDirectories + " directories";
            }
            String fileNameMessage;
            if (numberOfFileNames == 1) {
                fileNameMessage = numberOfFileNames + " filename";
            }
            else {
                fileNameMessage = numberOfFileNames + " filenames";
            }
            message.send("You have provided\n\t" + linkMessage + "\n\t" + directoryMessage + "\n\t" + fileNameMessage, Type.INFORMATION, Category.LOG);
            for (int i = 0; i < numberOfLinks; i++) {
                link = data.get("links").get(i);
                try {
                    fileName = data.get("fileNames").get(i);
                } catch (Exception e) {
                    fileName = utility.findFilenameInLink(link);
                    takeFileNameInputIfNull();
                }
                if (directory.equals(".")) {
                    directory = utility.saveToDefault();
                }
                else if (directory.equals("")) {
                    try {
                        directory = data.get("directories").get(i);
                    } catch (Exception e) {
                        directory = ".";
                    }
                }
                Drifty backend = new Drifty(link, directory, fileName, System.out);
                backend.start();
            }
        } catch (FileNotFoundException e) {
            message.send("YAML Data file (" + batchDownloadingFile + ") not found ! " + e.getMessage(), Type.ERROR, Category.DOWNLOAD);
        }
    }

    /**
     * Takes the filename as input from the user interactively if the system cannot find the filename i.e. if it is null
     */
    private static void takeFileNameInputIfNull() {
        if ((fileName == null || (fileName.length() == 0)) && (!isYoutubeURL && !isInstagramLink)) {
            System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
            fileName = SC.nextLine();
        }
        else {
            if (isYoutubeURL) {
                System.out.print("Do you like to use the video title as the filename? (Enter Y for yes and N for no) : ");
                SC.nextLine(); // To remove 'whitespace' from input buffer.
                String choiceString = SC.nextLine().toLowerCase();
                boolean choice = utility.yesNoValidation(choiceString, "Do you like to use the video title as the filename? (Enter Y for yes and N for no) : ");
                if (!choice) {
                    System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                    fileName = SC.nextLine();
                }
            }
            else if (isInstagramLink) {
                System.out.print("Is the instagram link of a video? (Enter Y for video and N for image) : ");
                SC.nextLine(); // To remove 'whitespace' from input buffer.
                String choiceString = SC.nextLine().toLowerCase();
                boolean choice = utility.yesNoValidation(choiceString, "Is the instagram link of a video? (Enter Y for video and N for image) : ");
                if (!choice) {
                    System.out.print("Please enter the filename for the Instagram image with the file extension (filename.extension [usually png]) : ");
                    fileName = SC.nextLine();
                    isInstagramImage = true;
                }
                else {
                    isInstagramImage = false;
                }
            }
            else {
                System.out.print(RENAME_FILE);
                SC.nextLine(); // To remove whitespace from the input buffer
                String choiceString = SC.nextLine().toLowerCase();
                boolean choice = utility.yesNoValidation(choiceString, ENTER_FILE_NAME_WITH_EXTENSION);
                if (choice) {
                    System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                    fileName = SC.nextLine();
                }
            }
        }
    }

    /**
     * This method returns true if the given Instagram link is of an image
     *
     * @return True if the Instagram link is of an image else false (for video) [Image/Video is decided by user]
     * @since v2.0.0
     */
    public static boolean getIsInstagramImage() {
        return isInstagramImage;
    }

    /**
     * This method returns true if the given link is of Instagram
     * @return True if the given link is of Instagram else false
     * @since v2.0.0
     */
    public static boolean getIsInstagramLink() {
        return isInstagramLink;
    }
}
