package CLI;

import Backend.Drifty;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.OS;
import Utils.*;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;

import static Utils.DriftyConstants.*;
import static Utils.Utility.*;

/**
 * This is the main class for the CLI (Command Line Interface) version of Drifty.
 *
 * @version 2.0.0
 */
public class Drifty_CLI {
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

    public static void main(String[] args) {
        logger.log(MessageType.INFO, CLI_APPLICATION_STARTED);
        messageBroker = new MessageBroker(System.out);
        utility = new Utility(messageBroker);
        printBanner();
        messageBroker.sendMessage("Initializing environment...", MessageType.INFO, MessageCategory.INITIALIZATION);
        Environment.initializeEnvironment();
        messageBroker.sendMessage("Environment initialized successfully!", MessageType.INFO, MessageCategory.INITIALIZATION);
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
                isInstagramLink = isInstagramLink(link);
                fileName = Objects.requireNonNullElse(name, fileName);
                messageBroker.sendMessage("Retrieving filename from link...", MessageType.INFO, MessageCategory.DOWNLOAD);
                if (!isYoutubeURL && !isInstagramLink) {
                    fileName = utility.findFilenameInLink(link);
                } else {
                    LinkedList<String> linkMetadataList = Utility.getLinkMetadata(link);
                    for (String json : linkMetadataList) {
                        fileName = Utility.getFilenameFromJson(json) + ".mp4";
                    }
                }
                takeFileNameInputIfNull();
                downloadsFolder = location;
                if (downloadsFolder == null) {
                    downloadsFolder = utility.saveToDefault();
                } else {
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
                        messageBroker.sendMessage("The given file should be a YAML file!", MessageType.ERROR, MessageCategory.LOG);
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
                isInstagramLink = isInstagramLink(link);
                messageBroker.sendMessage("Retrieving filename from link...", MessageType.INFO, MessageCategory.DOWNLOAD);
                if (!isYoutubeURL && !isInstagramLink) {
                    fileName = utility.findFilenameInLink(link);
                } else {
                    LinkedList<String> linkMetadataList = Utility.getLinkMetadata(link);
                    for (String json : linkMetadataList) {
                        fileName = Utility.getFilenameFromJson(json) + ".mp4";
                    }
                }
                takeFileNameInputIfNull();
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
            messageBroker.sendMessage("Trying to load YAML data file (" + batchDownloadingFile + ") ...", MessageType.INFO, MessageCategory.LOG);
            InputStreamReader yamlDataFile = new InputStreamReader(new FileInputStream(batchDownloadingFile));
            Map<String, List<String>> data = yamlParser.load(yamlDataFile);
            messageBroker.sendMessage("YAML data file (" + batchDownloadingFile + ") loaded successfully", MessageType.INFO, MessageCategory.LOG);
            int numberOfLinks = data.get("links").size();
            int numberOfFileNames = data.get("fileNames").size();
            int numberOfDirectories = 0;
            if (data.containsKey("directory")) {
                numberOfDirectories = 1;
                if (data.get("directory").get(0).isEmpty()) {
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
            messageBroker.sendMessage("You have provided\n\t" + linkMessage + "\n\t" + directoryMessage + "\n\t" + fileNameMessage, MessageType.INFO, MessageCategory.LOG);
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
                } else if (directory.isEmpty()) {
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
            messageBroker.sendMessage("YAML Data file (" + batchDownloadingFile + ") not found ! " + e.getMessage(), MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
    }

    private static void takeFileNameInputIfNull() {
        if ((fileName == null || (fileName.isEmpty())) && (!isYoutubeURL && !isInstagramLink)) {
            System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
            fileName = SC.nextLine();
        } else {
            if (isYoutubeURL) {
                System.out.print("Do you like to use the video title as the filename? (Enter Y for yes and N for no) : ");
                SC.nextLine(); // To remove 'whitespace' from input buffer.
                String choiceString = SC.nextLine().toLowerCase();
                boolean choice = utility.yesNoValidation(choiceString, "Do you like to use the video title as the filename? (Enter Y for yes and N for no) : ");
                if (!choice) {
                    System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                    fileName = SC.nextLine();
                }
            } else if (isInstagramLink) {
                System.out.print("Is the instagram link of a video? (Enter Y for video and N for image) : ");
                SC.nextLine(); // To remove 'whitespace' from input buffer.
                String choiceString = SC.nextLine().toLowerCase();
                boolean choice = utility.yesNoValidation(choiceString, "Is the instagram link of a video? (Enter Y for video and N for image) : ");
                if (!choice) {
                    System.out.print("Please enter the filename for the Instagram image with the file extension (filename.extension [usually png]) : ");
                    fileName = SC.nextLine();
                    isInstagramImage = true;
                } else {
                    isInstagramImage = false;
                }
            } else {
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

    public static boolean getIsInstagramImage() {
        return isInstagramImage;
    }

    public static boolean getIsInstagramLink() {
        return isInstagramLink;
    }
}
