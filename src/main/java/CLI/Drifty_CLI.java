package CLI;

import Backend.Drifty;
import Utils.Logger;
import Utils.MessageBroker;
import Utils.ScannerFactory;
import Utils.Utility;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import static Utils.DriftyConstants.*;
import static Utils.Utility.*;

/**
 * This is the main class for the CLI (Command Line Interface) version of Drifty.
 * @version 2.0.0
 */
public class Drifty_CLI {
    /**
     * Logger instance for the CLI version of Drifty
     */
    public static final Logger logger = Logger.getInstance("CLI");
    /**
     * Scanner instance for the CLI of Drifty
     */
    protected static final Scanner SC = ScannerFactory.getInstance();
    /**
     * Boolean value which determines if the given link is an YouTube video URL or not
     */
    protected static boolean isYoutubeURL;
    /**
     * Message broker instance which helps to send messages to the output stream
     */
    private static MessageBroker messageBroker;
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
     * @param args Command Line Arguments as a String array.
     */
    public static void main(String[] args) {
        logger.log(LOGGER_INFO, CLI_APPLICATION_STARTED);
        messageBroker = new MessageBroker("CLI", System.out);
        utility = new Utility(messageBroker);
        initialPrintBanner();
        String downloadsFolder;
        if (args.length > 0) {
            link = args[0];
            String name = null;
            String location = null;
            for (int i = 0; i < args.length; i++) {
                if (Objects.equals(args[i], HELP_FLAG) || Objects.equals(args[i], HELP_FLAG_SHORT)) {
                    help();
                    System.exit(0);
                } else if (Objects.equals(args[i], NAME_FLAG) || (Objects.equals(args[i], NAME_FLAG_SHORT))) {
                    name = args[i + 1];
                } else if (Objects.equals(args[i], LOCATION_FLAG) || (Objects.equals(args[i], LOCATION_FLAG_SHORT))) {
                    location = args[i + 1];
                } else if (Objects.equals(args[i], VERSION_FLAG) || (Objects.equals(args[i], VERSION_FLAG_SHORT))) {
                    System.out.println(APPLICATION_NAME + " " + VERSION_NUMBER);
                    System.exit(0);
                } else if ((Objects.equals(args[i], BATCH_FLAG)) || (Objects.equals(args[i], BATCH_FLAG_SHORT))) {
                    batchDownloading = true;
                    batchDownloadingFile = args[i + 1];
                    batchDownloader();
                }
            }
            if (!batchDownloading) {
                isYoutubeURL = isYoutubeLink(link);
                fileName = (name == null) ? fileName : name;
                if (!isYoutubeURL) {
                    fileName = utility.findFilenameInLink(link);
                }
                takeFileNameInputIfNull();
                downloadsFolder = location;
                if (downloadsFolder == null) {
                    downloadsFolder = utility.saveToDefault();
                } else {
                    if (System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
                        downloadsFolder = downloadsFolder.replace('/', '\\');
                        if (!(downloadsFolder.endsWith("\\"))) {
                            downloadsFolder = downloadsFolder + System.getProperty("file.separator");
                        }
                    }
                }
                Drifty backend = new Drifty(link, downloadsFolder, fileName, System.out);
                backend.start();
            }
            logger.log(LOGGER_INFO, CLI_APPLICATION_TERMINATED);
            System.exit(0);
        }
        while (true) {
            while (true) {
                System.out.println("Select download options :");
                System.out.println("\t1. Batch Download (Download Multiple files)");
                System.out.println("\t2. Individual Download (Download One file at a time)");
                int choice = SC.nextInt();
                if (choice == 1) {
                    batchDownloading = true;
                    System.out.print("Enter the path to the YAML data file : ");
                    batchDownloadingFile = SC.next();
                    SC.nextLine();
                    if (!(batchDownloadingFile.endsWith(".yml") || batchDownloadingFile.endsWith(".yaml"))){
                        messageBroker.sendMessage("The given file should be a YAML file!", LOGGER_ERROR, "only log");
                    } else {
                        batchDownloader();
                        break;
                    }
                } else if (choice == 2) {
                    batchDownloading = false;
                    break;
                } else {
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
                if (!isYoutubeURL) {
                    fileName = utility.findFilenameInLink(link);
                }
                if ((fileName == null || (fileName.length() == 0)) && (!isYoutubeURL)) {
                    System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                    fileName = SC.next();
                } else {
                    if (isYoutubeURL) {
                        System.out.print(RENAME_VIDEO_TITLE);
                    } else {
                        System.out.print(RENAME_FILE);
                    }
                    SC.nextLine(); // To remove 'whitespace' from input buffer.
                    String choiceString = SC.nextLine();
                    boolean choice = utility.yesNoValidation(choiceString, RENAME_FILE);
                    if (choice) {
                        System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                        fileName = SC.nextLine();
                    }
                }
                Drifty backend = new Drifty(link, downloadsFolder, fileName, System.out);
                backend.start();
            }
            System.out.println(QUIT_OR_CONTINUE);
            String choice = SC.nextLine().trim().toLowerCase();
            if (choice.equals("q")) {
                logger.log(LOGGER_INFO, CLI_APPLICATION_TERMINATED);
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
            messageBroker.sendMessage("Trying to load YAML data file (" + batchDownloadingFile + ") ...", LOGGER_INFO, "only log");
            InputStreamReader yamlDataFile = new InputStreamReader(new FileInputStream(batchDownloadingFile));
            Map<String, List<String>> data = yamlInstance.load(yamlDataFile);
            messageBroker.sendMessage("YAML data file (" + batchDownloadingFile + ") loaded successfully", LOGGER_INFO, "only log");
            int numberOfLinks = data.get("links").size();
            int numberOfFileNames = data.get("fileNames").size();
            int numberOfDirectories = 0;
            if (data.containsKey("directory")) {
                numberOfDirectories = 1;
                if (data.get("directory").get(0).length() == 0) {
                    directory = ".";
                } else {
                    directory = data.get("directory").get(0);
                }
            } else if (data.containsKey("directories")) {
                numberOfDirectories = data.get("directories").size();
            }
            String linkMessage;
            if (numberOfLinks == 1) {
                linkMessage = numberOfLinks + " link";
            } else {
                linkMessage = numberOfLinks + " links";
            }
            String directoryMessage;
            if (numberOfDirectories == 1) {
                directoryMessage = numberOfDirectories + " directory";
            } else {
                directoryMessage = numberOfDirectories + " directories";
            }
            String fileNameMessage;
            if (numberOfFileNames == 1) {
                fileNameMessage = numberOfFileNames + " filename";
            } else {
                fileNameMessage = numberOfFileNames + " filenames";
            }
            messageBroker.sendMessage("You have provided\n\t" + linkMessage + "\n\t" + directoryMessage + "\n\t" + fileNameMessage, LOGGER_INFO, "only log");
            for (int i = 0; i < numberOfLinks; i++) {
                link = data.get("links").get(i);
                try {
                    fileName = data.get("fileNames").get(i);
                } catch (Exception e) {
                    fileName = utility.findFilenameInLink(link);
                    takeFileNameInputIfNull();
                }
                if (directory.equals(".")){
                    directory = utility.saveToDefault();
                } else if (directory.equals("")) {
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
            messageBroker.sendMessage("YAML Data file (" + batchDownloadingFile + ") not found ! " + e.getMessage(), LOGGER_ERROR, "download");
        }
    }

    /**
     * Takes the filename as input from the user interactively if the system cannot find the filename i.e. if it is null
     */
    private static void takeFileNameInputIfNull() {
        if ((fileName == null || (fileName.length() == 0)) && (!isYoutubeURL)) {
            System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
            fileName = SC.nextLine();
        } else {
            if (isYoutubeURL) {
                System.out.print("Do you like to use the video title as the filename? (Enter Y for yes and N for no) : ");
            } else {
                System.out.print(RENAME_FILE);
            }
            SC.nextLine(); // To remove 'whitespace' from input buffer.
            String choiceString = SC.nextLine();
            boolean choice = utility.yesNoValidation(choiceString, RENAME_FILE);
            if (!choice) {
                System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                fileName = SC.nextLine();
            }
        }
    }
}
