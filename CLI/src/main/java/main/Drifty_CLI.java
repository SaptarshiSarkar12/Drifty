package main;

import backend.FileDownloader;
import cli.utils.MessageBroker;
import cli.init.Environment;
import org.yaml.snakeyaml.Yaml;
import preferences.AppSettings;
import properties.MessageType;
import properties.OS;
import support.Job;
import support.JobHistory;
import utils.Logger;
import cli.utils.ScannerFactory;
import cli.utils.Utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cli.support.Constants.*;
import static cli.utils.Utility.*;

public class Drifty_CLI {
    public static final Logger LOGGER = Logger.getInstance();
    protected static final Scanner SC = ScannerFactory.getInstance();
    protected static JobHistory jobHistory;
    protected static boolean isYoutubeURL;
    protected static boolean isInstagramLink;
    protected static boolean isSpotifyLink;
    private static MessageBroker messageBroker;
    private static String link;
    private static String downloadsFolder;
    private static Utility utility;
    private static String fileName;
    private static boolean batchDownloading;
    private static String batchDownloadingFile;
    private static final String MSG_FILE_EXISTS_NO_HISTORY = "\"%s\" exists in \"%s\" folder. It will be renamed to \"%s\".";
    private static final String MSG_FILE_EXISTS_HAS_HISTORY = "You have previously downloaded \"%s\" and it exists in \"%s\" folder.\nDo you want to download it again? ";

    public static void main(String[] args) {
        LOGGER.log(MessageType.INFO, CLI_APPLICATION_STARTED);
        messageBroker = new MessageBroker(System.out);
        Environment.setMessageBroker(messageBroker);
        messageBroker.msgInitInfo("Initializing environment...");
        Environment.initializeEnvironment();
        messageBroker.msgInitInfo("Environment initialized successfully!");
        utility = new Utility();
        jobHistory = AppSettings.GET.jobHistory();
        printBanner();
        if (args.length > 0) {
            link = null;
            String name = null;
            String location = null;
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case HELP_FLAG, HELP_FLAG_SHORT -> {
                        help();
                        System.exit(0);
                    }
                    case NAME_FLAG, NAME_FLAG_SHORT -> name = args[i + 1];
                    case LOCATION_FLAG, LOCATION_FLAG_SHORT -> location = args[i + 1];
                    case VERSION_FLAG, VERSION_FLAG_SHORT -> {
                        printVersion();
                        System.exit(0);
                    }
                    case BATCH_FLAG, BATCH_FLAG_SHORT -> {
                        batchDownloading = true;
                        batchDownloadingFile = args[i + 1];
                        batchDownloader();
                    }
                    default -> {
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
                    messageBroker.msgLinkError(INVALID_LINK);
                }
                if (isUrlValid) {
                    isYoutubeURL = isYoutube(link);
                    isInstagramLink = isInstagram(link);
                    isSpotifyLink = isSpotify(link);
                    downloadsFolder = location;
                    downloadsFolder = getProperDownloadsFolder(downloadsFolder);
                    if ((name == null) && (fileName == null || fileName.isEmpty())) {
                        if (isSpotifyLink && link.contains("playlist")) {
                            handleSpotifyPlaylist();
                        } else {
                            if (isInstagram(link) && !link.contains("?utm_source=ig_embed")) {
                                if (link.contains("?")) {
                                    link = link.substring(0, link.indexOf("?")) + "?utm_source=ig_embed";
                                } else {
                                    link = link + "?utm_source=ig_embed";
                                }
                            }
                            messageBroker.msgFilenameInfo("Retrieving filename from link...");
                            fileName = findFilenameInLink(link);
                            if (!Objects.requireNonNull(fileName).isEmpty()) {
                                Job job = new Job(link, downloadsFolder, fileName, false);
                                checkHistoryAndDownload(job, false);
                            }
                        }
                    }
                }
            }
            LOGGER.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
            System.exit(0);
        }
        while (true) {
            while (true) {
                messageBroker.msgInputInfo("Select download option :", true);
                messageBroker.msgInputInfo("\t1. Batch Download (Download Multiple files)", true);
                messageBroker.msgInputInfo("\t2. Single File Download (Download One file at a time)", true);
                int choice = SC.nextInt();
                if (choice == 1) {
                    batchDownloading = true;
                    messageBroker.msgInputInfo("Enter the path to the YAML data file : ", false);
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
                    messageBroker.msgInputError("Invalid Input!", true);
                }
            }
            if (!batchDownloading) {
                messageBroker.msgInputInfo(ENTER_FILE_LINK, false);
                link = SC.next();
                SC.nextLine();
                messageBroker.msgInputInfo("Validating link...", true);
                if (Utility.isURL(link)) {
                    Utility.isLinkValid(link);
                } else {
                    messageBroker.msgLinkError(INVALID_LINK);
                    continue;
                }
                messageBroker.msgInputInfo("Download directory (\".\" for default or \"L\" for " + AppSettings.GET.lastDownloadFolder() + ") : ", false);
                downloadsFolder = SC.next();
                downloadsFolder = getProperDownloadsFolder(downloadsFolder);
                isYoutubeURL = isYoutube(link);
                isInstagramLink = isInstagram(link);
                isSpotifyLink = isSpotify(link);
                if (isSpotifyLink && link.contains("playlist")) {
                    handleSpotifyPlaylist();
                } else {
                    if (isInstagram(link) && !link.contains("?utm_source=ig_embed")) {
                        if (link.contains("?")) {
                            link = link.substring(0, link.indexOf("?")) + "?utm_source=ig_embed";
                        } else {
                            link = link + "?utm_source=ig_embed";
                        }
                    }
                    messageBroker.msgFilenameInfo("Retrieving filename from link...");
                    fileName = findFilenameInLink(link);
                    if (!Objects.requireNonNull(fileName).isEmpty()) {
                        Job job = new Job(link, downloadsFolder, fileName, false);
                        checkHistoryAndDownload(job, true);
                    }
                }
            }
            messageBroker.msgInputInfo(QUIT_OR_CONTINUE, true);
            String choice = SC.next().toLowerCase();
            if ("q".equals(choice)) {
                LOGGER.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
                break;
            }
            printBanner();
        }
    }

    private static void printVersion() {
        System.out.println("\033[1m" + APPLICATION_NAME + " " + VERSION_NUMBER + ANSI_RESET);
        if (AppSettings.GET.ytDlpVersion().isEmpty()) {
            Utility.setYtDlpVersion().run();
        }
        if (AppSettings.GET.spotDLVersion().isEmpty()) {
            Utility.setSpotDLVersion().run();
        }
        System.out.println("yt-dlp version : " + AppSettings.GET.ytDlpVersion());
        System.out.println("spotDL version : " + AppSettings.GET.spotDLVersion());
    }

    private static void handleSpotifyPlaylist() {
        messageBroker.msgFilenameInfo("Retrieving the number of tracks in the playlist...");
        LinkedList<String> linkMetadataList = Utility.getLinkMetadata(link);
        String json = makePretty(Objects.requireNonNull(linkMetadataList).getFirst());
        String playlistLengthRegex = "(\"list_length\": )(.+)";
        Pattern playlistLengthPattern = Pattern.compile(playlistLengthRegex);
        Matcher lengthMatcher = playlistLengthPattern.matcher(json);
        int numberOfSongs = 0;
        if (lengthMatcher.find()) {
            numberOfSongs = Integer.parseInt(lengthMatcher.group(2));
            messageBroker.msgFilenameInfo("Number of tracks in the playlist : " + numberOfSongs);
        } else {
            messageBroker.msgFilenameError("Failed to retrieve the number of tracks in the playlist!");
        }
        for (int i = 0; i < numberOfSongs; i++) {
            messageBroker.msgStyleInfo(BANNER_BORDER);
            String linkRegex = "(\"url\": \")(.+)(\",)";
            Pattern linkPattern = Pattern.compile(linkRegex);
            Matcher linkMatcher = linkPattern.matcher(json);
            if (linkMatcher.find(i)) {
                link = linkMatcher.group(2);
            } else {
                messageBroker.msgLinkError("Failed to retrieve link from playlist!");
                continue;
            }
            messageBroker.msgLinkInfo("[" + (i + 1) + "/" + numberOfSongs + "] " + "Processing link : " + link);
            if (fileName != null) {
                String filenameRegex = "(\"name\": \")(.+)(\",)";
                Pattern filenamePattern = Pattern.compile(filenameRegex);
                Matcher filenameMatcher = filenamePattern.matcher(json);
                if (filenameMatcher.find(i)) {
                    fileName = cleanFilename(filenameMatcher.group(2)) + ".mp3";
                    messageBroker.msgFilenameInfo(FILENAME_DETECTED + "\"" + fileName + "\"");
                } else {
                    fileName = cleanFilename("Unknown_Filename_") + randomString(15) + ".mp3";
                    messageBroker.msgFilenameError(FILENAME_DETECTION_ERROR);
                }
            }
            Job job = new Job(link, downloadsFolder, fileName, false);
            checkHistoryAndDownload(job, false);
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
                if (data.get("directory").getFirst().isEmpty()) {
                    downloadsFolder = ".";
                } else {
                    downloadsFolder = data.get("directory").getFirst();
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
                if (".".equals(downloadsFolder)) {
                    downloadsFolder = Utility.getHomeDownloadFolder();
                } else if ("L".equalsIgnoreCase(downloadsFolder)) {
                    downloadsFolder = AppSettings.GET.lastDownloadFolder();
                } else if (downloadsFolder.isEmpty()) {
                    try {
                        downloadsFolder = data.get("directories").get(i);
                    } catch (Exception e) {
                        downloadsFolder = AppSettings.GET.lastDownloadFolder();
                    }
                }
                if (data.containsKey("fileNames") && !data.get("fileNames").get(i).isEmpty()) {
                    fileName = data.get("fileNames").get(i);
                } else {
                    if (isSpotifyLink && link.contains("playlist")) {
                        fileName = null;
                    } else {
                        if (isInstagram(link) && !link.contains("?utm_source=ig_embed")) {
                            if (link.contains("?")) {
                                link = link.substring(0, link.indexOf("?")) + "?utm_source=ig_embed";
                            } else {
                                link = link + "?utm_source=ig_embed";
                            }
                        }
                        messageBroker.msgFilenameInfo("Retrieving filename from link...");
                        fileName = findFilenameInLink(link);
                    }
                }
                if (isSpotifyLink && link.contains("playlist")) {
                    handleSpotifyPlaylist();
                }
                Job job = new Job(link, downloadsFolder, fileName, false);
                checkHistoryAndDownload(job, false);
            }
        } catch (FileNotFoundException e) {
            messageBroker.msgDownloadError("YAML Data file (" + batchDownloadingFile + ") not found ! " + e.getMessage());
        }
    }

    private static void renameFilenameIfRequired(boolean removeInputBufferFirst) { // Asks the user if the detected filename is to be used or not. If not, then the user is asked to enter a filename.
        if ((fileName == null || (fileName.isEmpty())) && (!isYoutubeURL && !isInstagramLink && !isSpotifyLink)) {
            messageBroker.msgInputInfo(ENTER_FILE_NAME_WITH_EXTENSION, false);
            if (removeInputBufferFirst) {
                SC.nextLine();
            }
            fileName = SC.nextLine();
        } else {
            messageBroker.msgInputInfo("Would you like to use this filename? (Enter Y for yes and N for no) : ", false);
            if (removeInputBufferFirst) {
                SC.nextLine(); // To remove 'whitespace' from input buffer. The whitespace will not be present in the input buffer if the user is using batch downloading because only yml file is parsed but no user input is taken.
            }
            String choiceString = SC.nextLine().toLowerCase();
            boolean choice = utility.yesNoValidation(choiceString, "Would you like to use this filename? (Enter Y for yes and N for no) : ");
            if (!choice) {
                messageBroker.msgInputInfo(ENTER_FILE_NAME_WITH_EXTENSION, false);
                fileName = SC.nextLine();
            }
        }
    }

    private static String getProperDownloadsFolder(String downloadsFolder) {
        if (downloadsFolder == null) {
            downloadsFolder = Utility.getHomeDownloadFolder();
        } else if ("L".equalsIgnoreCase(downloadsFolder)) {
            downloadsFolder = AppSettings.GET.lastDownloadFolder();
        } else if (".".equals(downloadsFolder)) {
            downloadsFolder = Utility.getHomeDownloadFolder();
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
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m-----------------------==| DRIFTY CLI HELP |==----------------------" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m\t\t\t\t\t\t\t\t" + VERSION_NUMBER + ANSI_RESET);
        System.out.println("\033[31;1mRequired parameter: File URL" + ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default                  Description" + ANSI_RESET);
        System.out.println("--batch      -b            N/A                      The path to the yaml/yml file containing the links and other arguments");
        System.out.println("--location   -l            Downloads                The location on your computer where file downloaded using Drifty are placed");
        System.out.println("--name       -n            Source                   Filename of the downloaded file");
        System.out.println("--help       -h            N/A                      Prints this help menu");
        System.out.println("--version    -v            Current Version          Displays version number of Drifty");
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

    private static void checkHistoryAndDownload(Job job, boolean removeInputBufferFirst) {
        boolean doesFileExist = job.fileExists();
        boolean hasHistory = jobHistory.exists(link);
        boolean fileExistsHasHistory = doesFileExist && hasHistory;
        boolean fileExistsNoHistory = doesFileExist && !hasHistory;
        if (fileExistsNoHistory) {
            fileName = Utility.renameFile(fileName, downloadsFolder);
            messageBroker.msgHistoryWarning(String.format(MSG_FILE_EXISTS_NO_HISTORY + "\n", job.getFilename(), job.getDir(), fileName), false);
            renameFilenameIfRequired(true);
            if (isSpotifyLink) {
                link = Utility.getSpotifyDownloadLink(link);
            }
            if (link != null) {
                job = new Job(link, downloadsFolder, fileName, false);
                jobHistory.addJob(job, true);
                FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
                downloader.run();
            }
        } else if (fileExistsHasHistory) {
            messageBroker.msgHistoryWarning(String.format(MSG_FILE_EXISTS_HAS_HISTORY, job.getFilename(), job.getDir()), false);
            if (removeInputBufferFirst) {
                SC.nextLine();
            }
            String choiceString = SC.nextLine().toLowerCase();
            boolean choice = utility.yesNoValidation(choiceString, String.format(MSG_FILE_EXISTS_HAS_HISTORY, job.getFilename(), job.getDir()));
            if (choice) {
                fileName = Utility.renameFile(fileName, downloadsFolder);
                messageBroker.msgFilenameInfo("New file name : " + fileName);
                renameFilenameIfRequired(false);
                if (isSpotifyLink) {
                    link = Utility.getSpotifyDownloadLink(link);
                }
                if (link != null) {
                    job = new Job(link, downloadsFolder, fileName, false);
                    jobHistory.addJob(job, true);
                    FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
                    downloader.run();
                }
            }
        } else {
            jobHistory.addJob(job, true);
            renameFilenameIfRequired(removeInputBufferFirst);
            if (isSpotifyLink) {
                link = Utility.getSpotifyDownloadLink(link);
            }
            if (link != null) {
                FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder);
                downloader.run();
            }
        }
    }
}
