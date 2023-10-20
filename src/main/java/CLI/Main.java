package CLI;

import Backend.FileDownloader;
import Enums.MessageType;
import Enums.OS;
import GUI.Support.Job;
import GUI.Support.JobHistory;
import Preferences.AppSettings;
import Utils.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

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
    protected static JobHistory jobHistory;
    protected static boolean isYoutubeURL;
    protected static boolean isInstagramLink;
    protected static boolean isInstagramImage;
    private static MessageBroker messageBroker;
    private static String link;
    private static String downloadsFolder;
    private static Utility utility;
    private static String fileName = null;
    private static boolean batchDownloading;
    private static String batchDownloadingFile;
    private static final String msg_FileExists_NoHistory = "\"%s\" exists in \"%s\" folder. It will be renamed to \"%s\".";
    private static final String msg_FileExists_HasHistory = "You have previously downloaded \"%s\" and it exists in \"%s\" folder. Do you want to download it again? ";

    public static void main(String[] args) {
        logger.log(MessageType.INFO, CLI_APPLICATION_STARTED);
        messageBroker = new MessageBroker(System.out);
        Environment.setMessageBroker(messageBroker);
        messageBroker.msgInitInfo("Initializing environment...");
        Environment.initializeEnvironment();
        messageBroker.msgInitInfo("Environment initialized successfully!");
        utility = new Utility();
        jobHistory = AppSettings.get.jobHistory();
        printBanner();
        if (args.length > 0) {
            link = args[0];
            String name = null;
            String location = null;
            if (args.length > 1) {
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
                    } else {
                        if (isURL(args[i])) {
                            link = args[i];

                        } else {
                            messageBroker.msgInitError("Invalid argument(s) passed!");
                            System.exit(1);
                        }
                    }
                }
            }
            if (!batchDownloading) {
                boolean isUrlValid;
                if (Utility.isURL(link)) {
                    isUrlValid = Utility.isLinkValid(link);

                } else {
                    isUrlValid = false;
                    messageBroker.msgLinkError("Link is invalid!");
                }
                if (isUrlValid) {
                    isYoutubeURL = isYoutube(link);
                    isInstagramLink = isInstagram(link);
                    if (name == null) {
                        if (fileName == null || fileName.isEmpty()) {
                            messageBroker.msgFilenameInfo("Retrieving filename from link...");
                            fileName = findFilenameInLink(link);
                        }
                    }
                    downloadsFolder = location;
                    downloadsFolder = getProperDownloadsFolder(downloadsFolder);
                    Job job = new Job(link, downloadsFolder, fileName, false);
                    checkHistoryAddJobsAndDownload(job);
                }
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
                link = SC.next();
                SC.nextLine();
                System.out.println("Validating link...");
                if (Utility.isURL(link)) {
                    Utility.isLinkValid(link);
                } else {
                    messageBroker.msgLinkError("Link is invalid!");
                    continue;
                }
                System.out.print("Download directory (\".\" for default or \"L\" for " + AppSettings.get.lastDownloadFolder() + ") : ");
                downloadsFolder = SC.next();
                downloadsFolder = getProperDownloadsFolder(downloadsFolder);
                isYoutubeURL = isYoutube(link);
                isInstagramLink = isInstagram(link);
                messageBroker.msgFilenameInfo("Retrieving filename from link...");
                fileName = findFilenameInLink(link);
                Job job = new Job(link, downloadsFolder, fileName, false);
                checkHistoryAddJobsAndDownload(job);
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
                    downloadsFolder = ".";
                } else {
                    downloadsFolder = data.get("directory").get(0);
                }
            } else if (data.containsKey("directories")) {
                numberOfDirectories = data.get("directories").size();
            } else {
                messageBroker.msgDirInfo("No directory specified. Default downloads folder will be used.");
                numberOfDirectories = 0;
                downloadsFolder = ".";
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
                messageBroker.msgStyleInfo(BANNER_BORDER);
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
                renameFilenameIfRequired(false);
                if (downloadsFolder.equals(".")) {
                    downloadsFolder = Utility.getHomeDownloadFolder().toString();
                } else if (downloadsFolder.equalsIgnoreCase("L")) {
                    downloadsFolder = AppSettings.get.lastDownloadFolder();
                } else if (downloadsFolder.isEmpty()) {
                    try {
                        downloadsFolder = data.get("directories").get(i);
                    } catch (Exception e) {
                        downloadsFolder = AppSettings.get.lastDownloadFolder();
                    }
                }
                Job job = new Job(link, downloadsFolder, fileName, false);
                checkHistoryAddJobsAndDownload(job);
            }
        } catch (FileNotFoundException e) {
            messageBroker.msgDownloadError("YAML Data file (" + batchDownloadingFile + ") not found ! " + e.getMessage());
        }
    }

    private static void renameFilenameIfRequired(boolean removeInputBufferFirst) { // Asks the user if the detected filename is to be used or not. If not, then the user is asked to enter a filename.
        if ((fileName == null || (fileName.isEmpty())) && (!isYoutubeURL && !isInstagramLink)) {
            System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
            if (removeInputBufferFirst) {
                SC.nextLine();
            }
            fileName = SC.nextLine();
        } else {
            System.out.print("Would you like to use this filename? (Enter Y for yes and N for no) : ");
            if (removeInputBufferFirst) {
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

    private static String getProperDownloadsFolder(String downloadsFolder) {
        if (downloadsFolder == null) {
            downloadsFolder = Utility.getHomeDownloadFolder().toString();
        } else if (downloadsFolder.equalsIgnoreCase("L")) {
            downloadsFolder = AppSettings.get.lastDownloadFolder();
        } else if (downloadsFolder.equals(".")) {
            downloadsFolder = Utility.getHomeDownloadFolder().toString();
        } else {
            downloadsFolder = Paths.get(downloadsFolder).toAbsolutePath().toString();
            if (OS.isWindows()) {
                downloadsFolder = downloadsFolder.replace('/', '\\');
            }
        }
        if (new File(downloadsFolder).exists()) {
            messageBroker.msgDirInfo("Download folder exists!");
        } else {
            messageBroker.msgDirError("Download folder does not exist!");
            try {
                Files.createDirectory(Path.of(downloadsFolder));
                messageBroker.msgDirInfo("Download folder created successfully!");
            } catch (IOException e) {
                messageBroker.msgDirError("Failed to create download folder! " + e.getMessage());
            }
        }
        AppSettings.set.lastFolder(downloadsFolder);
        return downloadsFolder;
    }

    public static void help() {
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m------------==| DRIFTY CLI HELP |==------------" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m                    " + VERSION_NUMBER + ANSI_RESET);
        System.out.println("\033[31;1mRequired parameter: File URL" + ANSI_RESET + " \033[3m(This must be the first argument you are passing unless you are using Batch Downloading)" + ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default                  Description" + ANSI_RESET);
        System.out.println("--batch      -b            N/A                      The path to the yaml/yml file containing the links and other arguments.");
        System.out.println("--location   -l            Downloads                The location on your computer where content downloaded using Drifty are placed.");
        System.out.println("--name       -n            Source                   Filename of the downloaded file.");
        System.out.println("--help       -h            N/A                      Provides concise information for Drifty CLI.");
        System.out.println("--version    -v            Current Version          Displays version number of Drifty.");
        System.out.println("\033[97;1mSee full documentation at https://github.com/SaptarshiSarkar12/Drifty#readme" + ANSI_RESET);
        System.out.println("For more information visit: ");
        System.out.println("\tProject Link - https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\tProject Website - " + DRIFTY_WEBSITE_URL);
    }

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

    private static void checkHistoryAddJobsAndDownload(Job job) {
        boolean doesFileExist = job.fileExists();
        boolean hasHistory = jobHistory.exists(link);
        boolean fileExists_HasHistory = doesFileExist && hasHistory;
        boolean fileExists_NoHistory = doesFileExist && !hasHistory;
        if (fileExists_NoHistory) {
            fileName = Utility.renameFile(fileName, downloadsFolder);
            System.out.printf(msg_FileExists_NoHistory + "\n", job.getFilename(), job.getDir(), fileName);
            renameFilenameIfRequired(true);
            job = new Job(link, downloadsFolder, fileName, false);
            jobHistory.addJob(job,true);
            FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
            downloader.run();
        } else if (fileExists_HasHistory) {
            System.out.printf(msg_FileExists_HasHistory, job.getFilename(), job.getDir());
            SC.nextLine(); // to remove whitespace from input buffer
            String choiceString = SC.nextLine().toLowerCase();
            boolean choice = utility.yesNoValidation(choiceString, String.format(msg_FileExists_HasHistory, job.getFilename(), job.getDir()));
            if (choice) {
                fileName = Utility.renameFile(fileName, downloadsFolder);
                System.out.println("New file name : " + fileName);
                renameFilenameIfRequired(false);
                job = new Job(link, downloadsFolder, fileName, false);
                jobHistory.addJob(job,true);
                FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
                downloader.run();
            }
        } else {
            jobHistory.addJob(job, true);
            renameFilenameIfRequired(true);
            FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
            downloader.run();
        }
    }
}
