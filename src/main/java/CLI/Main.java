package CLI;

import Backend.Drifty;
import Enums.MessageType;
import Enums.OS;
import Preferences.AppSettings;
import Utils.*;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static Utils.DriftyConstants.*;
import static Utils.Utility.*;

/**
 * This is the main class for the CLI (Command Line Interface) version of Drifty.
 *
 * @version 2.0.0
 */
public class Main {
    public static final Logger logger = Logger.getInstance();
    protected static final Scanner SC = ScannerFactory.getInstance();
    protected static boolean isYoutubeURL;
    protected static boolean isInstagramLink;
    protected static boolean isInstagramImage;
    private static MessageBroker messageBroker;
    private static String link;
    private static Utility utility;
    private static String directory;
    private static String fileName = null;
    private static boolean batchDownloading;
    private static String batchDownloadingFile;

    public static void main(String[] args) throws IOException {
        logger.log(MessageType.INFO, CLI_APPLICATION_STARTED);
        Environment.setMessageBroker(new MessageBroker(System.out));
        messageBroker = Environment.getMessageBroker();
        messageBroker.msgInitInfo("Initializing environment...");
        Environment.initializeEnvironment();
        messageBroker.msgInitInfo("Environment initialized successfully!");
        messageBroker = Environment.getMessageBroker();
        utility = new Utility();
        printBanner();
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
                isYoutubeURL = isYoutube(link);
                isInstagramLink = isInstagram(link);
                fileName = Objects.requireNonNullElse(name, fileName);
                messageBroker.msgFilenameInfo("Retrieving filename from link...");
                fileName = findFilenameInLink(link);
                renameFilenameIfRequired();
                downloadsFolder = location;
                if (downloadsFolder == null) {
                    downloadsFolder = AppSettings.get.lastDownloadFolder();
                } else {
                    if (OS.isWindows()) {
                        downloadsFolder = downloadsFolder.replace('/', '\\');
                    }
                }
                Drifty backend = new Drifty(link, downloadsFolder, fileName, System.out);
                backend.start();
            }
            logger.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
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
                        messageBroker.msgBatchError("The data file should be a YAML file!");
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
                System.out.print("Download directory (\".\" for default or \"L\" for " + AppSettings.get.lastDownloadFolder() + ") : ");
                downloadsFolder = SC.next();
                isYoutubeURL = isYoutube(link);
                isInstagramLink = isInstagram(link);
                messageBroker.msgFilenameInfo("Retrieving filename from link...");
                fileName = findFilenameInLink(link);
                renameFilenameIfRequired();
                Drifty backend = new Drifty(link, downloadsFolder, fileName, System.out);
                backend.start();
            }
            System.out.println(QUIT_OR_CONTINUE);
            String choice = SC.next().toLowerCase();
            if (choice.equals("q")) {
                logger.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
                break;
            }
            printBanner();
        }
    }

    private static void batchDownloader() {
        Yaml yamlParser = new Yaml();
        try {
            messageBroker.msgLogInfo("Trying to load YAML data file (" + batchDownloadingFile + ") ...");
            InputStreamReader yamlDataFile = new InputStreamReader(new FileInputStream(batchDownloadingFile));
            Map<String, List<String>> data = yamlParser.load(yamlDataFile);
            messageBroker.msgLogInfo("YAML data file (" + batchDownloadingFile + ") loaded successfully");
            int numberOfLinks;
            try {
                numberOfLinks = data.get("links").size();
            } catch (NullPointerException e) {
                messageBroker.msgLinkInfo("No links specified. Exiting...");
                return;
            }
            int numberOfFileNames;
            if (data.containsKey("fileNames")) {
                numberOfFileNames = data.get("fileNames").size();
            } else {
                messageBroker.msgFilenameInfo("No filename specified. Filename will be retrieved from the link.");
                numberOfFileNames = 0;
            }
            int numberOfDirectories;
            if (data.containsKey("directory")) {
                numberOfDirectories = 1;
                if (data.get("directory").get(0).isEmpty()) {
                    directory = ".";
                } else {
                    directory = data.get("directory").get(0);
                }
            } else if (data.containsKey("directories")) {
                numberOfDirectories = data.get("directories").size();
            } else {
                messageBroker.msgDirInfo("No directory specified. Default downloads folder will be used.");
                numberOfDirectories = 0;
                directory = ".";
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
            messageBroker.msgBatchInfo("You have provided\n\t" + linkMessage + "\n\t" + directoryMessage + "\n\t" + fileNameMessage);
            for (int i = 0; i < numberOfLinks; i++) {
                messageBroker.msgStyleInfo("==================================================");
                link = data.get("links").get(i);
                messageBroker.msgLinkInfo("[" + (i + 1) + "/" + numberOfLinks + "] " + "Processing link : " + link);
                isYoutubeURL = isYoutube(link);
                isInstagramLink = isInstagram(link);
                if (data.containsKey("fileNames") && !data.get("fileNames").get(i).isEmpty()) {
                    fileName = data.get("fileNames").get(i);
                } else {
                    messageBroker.msgFilenameInfo("Retrieving filename from link...");
                    fileName = findFilenameInLink(link);
                }
                renameFilenameIfRequired();
                if (directory.equals(".")) {
                    directory = AppSettings.get.lastDownloadFolder();
                } else if (directory.isEmpty()) {
                    try {
                        directory = data.get("directories").get(i);
                    } catch (Exception e) {
                        directory = AppSettings.get.lastDownloadFolder();
                    }
                }
                Drifty backend = new Drifty(link, directory, fileName, System.out);
                backend.start();
            }
        } catch (FileNotFoundException e) {
            messageBroker.msgDownloadError("YAML Data file (" + batchDownloadingFile + ") not found ! " + e.getMessage());
        }
    }

    private static void renameFilenameIfRequired() { // Asks the user if the detected filename is to be used or not. If not, then the user is asked to enter a filename.
        if ((fileName == null || (fileName.isEmpty())) && (!isYoutubeURL && !isInstagramLink)) {
            System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
            fileName = SC.nextLine();
        } else {
            System.out.print("Would you like to use this filename? (Enter Y for yes and N for no) : ");
            if (!batchDownloading) {
                SC.nextLine(); // To remove 'whitespace' from input buffer. The whitespace will not be present in the input buffer if the user is using batch downloading because only yml file is parsed but no user input is taken.
            }
            String choiceString = SC.nextLine().toLowerCase();
            boolean choice = utility.yesNoValidation(choiceString, "Would you like to use this filename? (Enter Y for yes and N for no) : ");
            if (!choice) {
                System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                fileName = SC.nextLine();
            }
        }
    }

    public static boolean getIsInstagramLink() {
        return isInstagramLink;
    }
}
