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
    private static MessageBroker messageBroker;
    private static String link;
    private static Utility utility;
    private static String directory;
    private static String fileName = null;
    private static boolean batchDownloading;
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
                    batchDownloader();
                    break;
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
                Drifty backend = new Drifty(link, downloadsFolder, fileName, System.out);
                backend.start();
            }
            System.out.println(QUIT_OR_CONTINUE);
            String choice = SC.nextLine().toLowerCase();
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
            InputStreamReader yamlDataFile = new InputStreamReader(new FileInputStream(batchDownloadingFile));
            Map<String, List<String>> data = yamlInstance.load(yamlDataFile);
            for (int i = 0; i < data.size() - 1; i++) {
                link = data.get("links").get(i);
                fileName = data.get("fileNames").get(i);
                if (data.containsKey("directory")) {
                    directory = data.get("directory").get(0);
                } else {
                    directory = data.get("directories").get(i);
                }
                if (directory.equals(".")){
                    directory = utility.saveToDefault();
                }
                Drifty backend = new Drifty(link, directory, fileName, System.out);
                backend.start();
            }
        } catch (FileNotFoundException e) {
            messageBroker.sendMessage("YAML Data File not found ! " + e.getMessage(), LOGGER_ERROR, "download");
        }
    }
}
