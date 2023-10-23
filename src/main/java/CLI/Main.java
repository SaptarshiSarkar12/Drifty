package CLI;

import Backend.FileDownloader;
import Enums.MessageType;
import Enums.OS;
import GUI.Support.Job;
import GUI.Support.JobHistory;
import Preferences.AppSettings;
import Updater.Updater;
import Utils.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
    public static final Logger LOGGER = Logger.getInstance();
    protected static final Scanner SC = ScannerFactory.getInstance();
    protected static JobHistory jobHistory;
    protected static boolean isYoutubeURL;
    protected static boolean isInstagramLink;
    protected static boolean isSpotifyLink;
    protected static boolean isInstagramImage;
    private static MessageBroker messageBroker;
    private static String link;
    private static String downloadsFolder;
    private static Utility utility;
    private static String fileName = null;
    private static boolean batchDownloading;
    private static String batchDownloadingFile;
    private static final String MSG_FILE_EXISTS_NO_HISTORY = "\"%s\" exists in \"%s\" folder. It will be renamed to \"%s\".";
    private static final String MSG_FILE_EXISTS_HAS_HISTORY = "You have previously downloaded \"%s\" and it exists in \"%s\" folder.\nDo you want to download it again? ";

    public static void main(String[] args) {
        LOGGER.log(MessageType.INFO, CLI_APPLICATION_STARTED);
        messageBroker = new MessageBroker(System.out);
        Environment.setMessageBroker(messageBroker);
        try {
            if (checkUpdate()){
                return;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        messageBroker.msgInitInfo("Initializing environment...");
        Environment.initializeEnvironment();
        messageBroker.msgInitInfo("Environment initialized successfully!");
        utility = new Utility();
        jobHistory = AppSettings.GET.jobHistory();
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
                    if (name == null) {
                        if (fileName == null || fileName.isEmpty()) {
                            messageBroker.msgFilenameInfo("Retrieving filename from link...");
                            fileName = findFilenameInLink(link);
                        }
                    }
                    downloadsFolder = location;
                    downloadsFolder = getProperDownloadsFolder(downloadsFolder);
                    Job job = new Job(link, downloadsFolder, fileName, false);
                    checkHistoryAddJobsAndDownload(job, false);
                }
            }
            LOGGER.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
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
                System.out.print("Download directory (\".\" for default or \"L\" for " + AppSettings.GET.lastDownloadFolder() + ") : ");
                downloadsFolder = SC.next();
                downloadsFolder = getProperDownloadsFolder(downloadsFolder);
                isYoutubeURL = isYoutube(link);
                isInstagramLink = isInstagram(link);
                isSpotifyLink = isSpotify(link);
                messageBroker.msgFilenameInfo("Retrieving filename from link...");
                fileName = findFilenameInLink(link);
                Job job = new Job(link, downloadsFolder, fileName, false);
                checkHistoryAddJobsAndDownload(job, true);
            }
            System.out.println(QUIT_OR_CONTINUE);
            String choice = SC.next().toLowerCase();
            if (choice.equals("q")) {
                LOGGER.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
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
                isSpotifyLink = isSpotify(link);
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
                    downloadsFolder = AppSettings.GET.lastDownloadFolder();
                } else if (downloadsFolder.isEmpty()) {
                    try {
                        downloadsFolder = data.get("directories").get(i);
                    } catch (Exception e) {
                        downloadsFolder = AppSettings.GET.lastDownloadFolder();
                    }
                }
                Job job = new Job(link, downloadsFolder, fileName, false);
                checkHistoryAddJobsAndDownload(job, false);
            }
        } catch (FileNotFoundException e) {
            messageBroker.msgDownloadError("YAML Data file (" + batchDownloadingFile + ") not found ! " + e.getMessage());
        }
    }

    private static void renameFilenameIfRequired(boolean removeInputBufferFirst) { // Asks the user if the detected filename is to be used or not. If not, then the user is asked to enter a filename.
        if ((fileName == null || (fileName.isEmpty())) && (!isYoutubeURL && !isInstagramLink && !isSpotifyLink)) {
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
            downloadsFolder = AppSettings.GET.lastDownloadFolder();
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
        AppSettings.SET.lastFolder(downloadsFolder);
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

    private static void checkHistoryAddJobsAndDownload(Job job, boolean removeInputBufferFirst) {
        boolean doesFileExist = job.fileExists();
        boolean hasHistory = jobHistory.exists(link);
        boolean fileExistsHasHistory = doesFileExist && hasHistory;
        boolean fileExistsNoHistory = doesFileExist && !hasHistory;
        if (fileExistsNoHistory) {
            fileName = Utility.renameFile(fileName, downloadsFolder);
            System.out.printf(MSG_FILE_EXISTS_NO_HISTORY + "\n", job.getFilename(), job.getDir(), fileName);
            renameFilenameIfRequired(true);
            if (isSpotifyLink) {
                link = Utility.getSpotifyDownloadLink(link);
            }
            job = new Job(link, downloadsFolder, fileName, false);
            jobHistory.addJob(job, true);
            FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
            downloader.run();
        } else if (fileExistsHasHistory) {
            System.out.printf(MSG_FILE_EXISTS_HAS_HISTORY, job.getFilename(), job.getDir());
            if (removeInputBufferFirst) {
                SC.nextLine();
            }
            String choiceString = SC.nextLine().toLowerCase();
            boolean choice = utility.yesNoValidation(choiceString, String.format(MSG_FILE_EXISTS_HAS_HISTORY, job.getFilename(), job.getDir()));
            if (choice) {
                fileName = Utility.renameFile(fileName, downloadsFolder);
                System.out.println("New file name : " + fileName);
                renameFilenameIfRequired(false);
                if (isSpotifyLink) {
                    link = Utility.getSpotifyDownloadLink(link);
                }
                job = new Job(link, downloadsFolder, fileName, false);
                jobHistory.addJob(job, true);
                FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
                downloader.run();
            }
        } else {
            jobHistory.addJob(job, true);
            renameFilenameIfRequired(true);
            if (isSpotifyLink) {
                link = Utility.getSpotifyDownloadLink(link);
            }
            FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
            downloader.run();
        }
    }

    public static boolean checkUpdate() throws URISyntaxException {
        String latestVersion = getLatestVersion();
        if (isNewerVersion(latestVersion , VERSION_NUMBER)) {
            String Link;
            String oldFilePath = String.valueOf(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String newFilePath = "";
            String OS_NAME = OS.getOSName();
            if (OS_NAME.contains("win")) {
                Link = "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-CLI.exe";
                String fileName = "Drifty_CLI.exe";
                String dirPath = String.valueOf(Paths.get(System.getenv("LOCALAPPDATA"), "Drifty", "updates"));
                FileDownloader downloader =  new FileDownloader(Link , fileName , dirPath);
                downloader.run();
                newFilePath = dirPath +'\\' + fileName;
            } else if (OS_NAME.contains("mac")) {
                Link = "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-CLI_macos";
                String fileName = "Drifty-CLI_macos";
                String dirPath = ".drifty/updates";
                FileDownloader downloader =  new FileDownloader(Link , fileName , dirPath);
                downloader.run();
                newFilePath = dirPath +'\\' + fileName;
            } else if (OS_NAME.contains("linux")) {
                Link = "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-CLI_linux";
                String fileName = "Drifty-CLI_linux";
                String dirPath = ".drifty/updates";
                FileDownloader downloader = new FileDownloader(Link, fileName, dirPath);
                downloader.run();
                newFilePath = dirPath +'\\' + fileName;
            }
            Updater.replaceUpdate(oldFilePath , newFilePath);
            return true;
        }
        return false;
    }

    private static String getLatestVersion() {
        try {
            URL url = new URI("https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases/latest").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            reader.close();

            // Parse JSON response to get the "tag_name"
            // For simplicity, we're assuming the tag_name is a string enclosed in double quotes
            int start = response.indexOf("\"tag_name\":\"") + 12;
            int end = response.indexOf("\"", start);
            System.out.println( response.substring(start, end));
            return response.substring(start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isNewerVersion(String newVersion, String currentVersion) {
        // Split version strings into arrays of integers
        String[] newVersionParts = newVersion.replaceAll("[^\\d.]", "").split("\\.");
        String[] currentVersionParts = currentVersion.replaceAll("[^\\d.]", "").split("\\.");

        // Convert parts to integers
        int[] newVersionNumbers = new int[newVersionParts.length];
        int[] currentVersionNumbers = new int[currentVersionParts.length];

        for (int i = 0; i < newVersionParts.length; i++) {
            newVersionNumbers[i] = Integer.parseInt(newVersionParts[i]);
        }

        for (int i = 0; i < currentVersionParts.length; i++) {
            currentVersionNumbers[i] = Integer.parseInt(currentVersionParts[i]);
        }

        // Compare version numbers
        for (int i = 0; i < Math.min(newVersionNumbers.length, currentVersionNumbers.length); i++) {
            if (newVersionNumbers[i] > currentVersionNumbers[i]) {
                return true; // New version is greater
            } else if (newVersionNumbers[i] < currentVersionNumbers[i]) {
                return false; // Current version is greater
            }
        }

        // If all compared parts are equal, consider the longer version as newer
        return newVersionNumbers.length > currentVersionNumbers.length;
    }
}