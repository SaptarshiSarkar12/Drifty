package main;

import backend.FileDownloader;
import cli.init.Environment;
import cli.utils.MessageBroker;
import cli.utils.ScannerFactory;
import cli.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.LoaderOptions;
import cli.init.Environment;
import cli.updater.ExecuteUpdate;
import cli.utils.MessageBroker;
import cli.utils.ScannerFactory;
import cli.utils.Utility;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import preferences.AppSettings;
import properties.MessageType;
import properties.OS;
import properties.Program;
import support.Constants;
import support.Job;
import support.JobHistory;
import utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static cli.support.Constants.*;
import static cli.utils.Utility.*;
import static updater.CheckUpdate.isUpdateAvailable;

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
        Environment.setCLIMessageBroker(messageBroker);
        checkAndUpdateDrifty(true);
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
                        Environment.terminate(0);
                    }
                    case NAME_FLAG, NAME_FLAG_SHORT -> {
                        try {
                            name = args[i + 1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            messageBroker.msgInitError("No filename specified!");
                            Environment.terminate(1);
                        }
                    }
                    case LOCATION_FLAG, LOCATION_FLAG_SHORT -> {
                        try {
                            location = args[i + 1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            messageBroker.msgInitError("No download location specified!");
                            Environment.terminate(1);
                        }
                    }
                    case VERSION_FLAG, VERSION_FLAG_SHORT -> {
                        printVersion();
                        Environment.terminate(0);
                    }
                    case UPDATE_FLAG, UPDATE_FLAG_SHORT -> checkAndUpdateDrifty(false);
                    case BATCH_FLAG, BATCH_FLAG_SHORT -> {
                        batchDownloading = true;
                        try {
                            batchDownloadingFile = args[i + 1];
                            if (!(batchDownloadingFile.endsWith(".yml") || batchDownloadingFile.endsWith(".yaml"))) {
                                messageBroker.msgBatchError("The data file should be a YAML file!");
                                Environment.terminate(1);
                            }
                            if (!Paths.get(batchDownloadingFile).toFile().exists()) {
                                messageBroker.msgBatchError("YAML data file \"" + batchDownloadingFile + "\" does not exist!");
                                Environment.terminate(1);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            messageBroker.msgInitError("No batch file specified!");
                            Environment.terminate(1);
                        }
                        batchDownloader();
                    }
                    default -> {
                        if (isURL(args[i])) {
                            link = args[i];
                        } else {
                            messageBroker.msgInitError("Invalid argument(s) passed!");
                            Environment.terminate(1);
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
                            if (isInstagram(link)) {
                                link = formatInstagramLink(link);
                            }
                            messageBroker.msgFilenameInfo("Retrieving filename from link...");
                            HashMap<String, Object> spotifyMetadata;
                            if (isSpotifyLink) {
                                spotifyMetadata = Utility.getSpotifySongMetadata(link);
                                if (spotifyMetadata != null && !spotifyMetadata.isEmpty()) {
                                    fileName = spotifyMetadata.get("name").toString() + ".webm";
                                    messageBroker.msgFilenameInfo(FILENAME_DETECTED + "\"" + fileName + "\"");
                                } else {
                                    messageBroker.msgFilenameError(FILENAME_DETECTION_ERROR);
                                }
                            } else {
                                fileName = findFilenameInLink(link);
                            }
                            if (!Objects.requireNonNull(fileName).isEmpty()) {
                                verifyJobAndDownload(false);
                            }
                        }
                    }
                }
            }
            LOGGER.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
            Environment.terminate(0);
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
                        if (!Paths.get(batchDownloadingFile).toFile().exists()) {
                            messageBroker.msgBatchError("YAML data file \"" + batchDownloadingFile + "\" does not exist!");
                        }
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
                link = SC.next().strip();
                SC.nextLine();
                messageBroker.msgInputInfo("Validating link...", true);
                if (Utility.isURL(link)) {
                    Utility.isLinkValid(link);
                } else {
                    messageBroker.msgLinkError(INVALID_LINK);
                    continue;
                }
                messageBroker.msgInputInfo("Download directory (\".\" for default or \"L\" for " + AppSettings.GET.lastDownloadFolder() + ") : ", false);
                downloadsFolder = SC.next().strip();
                downloadsFolder = getProperDownloadsFolder(downloadsFolder);
                isYoutubeURL = isYoutube(link);
                isInstagramLink = isInstagram(link);
                isSpotifyLink = isSpotify(link);
                if (isSpotifyLink) {
                    if (link.contains("playlist")) {
                        handleSpotifyPlaylist();
                    } else {
                        messageBroker.msgFilenameInfo("Retrieving filename from link...");
                        HashMap<String, Object> spotifyMetadata = Utility.getSpotifySongMetadata(link);
                        if (spotifyMetadata != null && !spotifyMetadata.isEmpty()) {
                            fileName = spotifyMetadata.get("songName").toString() + ".webm";
                            messageBroker.msgFilenameInfo(FILENAME_DETECTED + "\"" + fileName + "\"");
                        } else {
                            messageBroker.msgFilenameError(FILENAME_DETECTION_ERROR);
                        }
                        if (!Objects.requireNonNull(fileName).isEmpty()) {
                            verifyJobAndDownload(true);
                        }
                    }
                } else {
                    if (isInstagram(link)) {
                        link = formatInstagramLink(link);
                    }
                    messageBroker.msgFilenameInfo("Retrieving filename from link...");
                    fileName = findFilenameInLink(link);
                    if (!Objects.requireNonNull(fileName).isEmpty()) {
                        verifyJobAndDownload(true);
                    }
                }
            }
            messageBroker.msgInputInfo(QUIT_OR_CONTINUE, true);
            String choice = SC.next().toLowerCase().strip();
            if ("q".equals(choice)) {
                LOGGER.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
                break;
            }
            printBanner();
        }
        Environment.terminate(0);
    }

    private static void checkAndUpdateDrifty(boolean askForInstallingUpdate) {
        messageBroker.msgInitInfo("Checking for updates...");
        if (!isDriftyUpdateChecked()) {
            if (isUpdateAvailable()) {
                messageBroker.msgUpdateInfo("Update available!");
                boolean choice = true;
                if (askForInstallingUpdate) {
                    messageBroker.msgUpdateInfo("Do you want to download the update? (Enter Y for yes and N for no) : ");
                    String choiceString = SC.nextLine().toLowerCase();
                    choice = utility.yesNoValidation(choiceString, "Do you want to download the update? (Enter Y for yes and N for no) : ");
                }
                if (!choice) {
                    messageBroker.msgUpdateInfo("Drifty update cancelled!");
                } else {
                    messageBroker.msgUpdateInfo("Downloading update...");
                    if (!downloadUpdate()) {
                        messageBroker.msgUpdateError("Failed to update Drifty!");
                    } else {
                        messageBroker.msgUpdateInfo("Update successful!");
                        messageBroker.msgUpdateInfo("Please restart Drifty to see the changes!");
                        System.exit(0);
                    }
                }
            }
        }
    }

    private static boolean isDriftyUpdateChecked() {
        long timeSinceLastUpdate = System.currentTimeMillis() - AppSettings.GET.lastDriftyUpdateTime();
        return timeSinceLastUpdate <= ONE_DAY;
    }

    private static boolean downloadUpdate() {
        try {
            // "Current executable" means the executable currently running i.e., the one that is outdated.
            File currentExecutableFile = new File(Drifty_CLI.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // "Latest executable" means the executable that is to be downloaded and installed i.e., the latest version.
            // "tmpFolder" is the temporary folder where the latest executable will be downloaded to.
            File tmpFolder = Files.createTempDirectory("Drifty").toFile();
            tmpFolder.deleteOnExit();
            File latestExecutableFile = Paths.get(tmpFolder.getPath()).resolve(currentExecutableFile.getName()).toFile();
            FileDownloader downloader = new FileDownloader(Constants.updateURL.toString(), currentExecutableFile.getName(), tmpFolder.toString());
            downloader.run();
            if (latestExecutableFile.exists() && latestExecutableFile.isFile() && latestExecutableFile.length() > 0) {
                // If the latest executable was successfully downloaded, set the executable permission and execute the update.
                ExecuteUpdate updateExecutor = new ExecuteUpdate(currentExecutableFile, latestExecutableFile);
                messageBroker.msgLogInfo("Setting executable permission for the latest version of Drifty...");
                if (updateExecutor.setExecutablePermission()) {
                    messageBroker.msgLogInfo("Executing update...");
                    return updateExecutor.executeUpdate();
                }
            } else {
                messageBroker.msgUpdateError("Failed to download update!");
                return false;
            }
        } catch (IOException e) {
            messageBroker.msgUpdateError("Failed to create temporary folder for downloading update! " + e.getMessage());
        } catch (URISyntaxException e) {
            messageBroker.msgUpdateError("Failed to get the location of the current executable! " + e.getMessage());
        } catch (Exception e) {
            messageBroker.msgUpdateError("Failed to update Drifty! " + e.getMessage());
        }
        return false;
    }

    private static void printVersion() {
        System.out.println("\033[1m" + APPLICATION_NAME + " " + VERSION_NUMBER + ANSI_RESET);
        if (AppSettings.GET.ytDlpVersion().isEmpty()) {
            Utility.setYtDlpVersion().run();
        }
        System.out.println("yt-dlp version : " + AppSettings.GET.ytDlpVersion());
        if (AppSettings.GET.isFfmpegWorking()) {
            if (AppSettings.GET.ffmpegVersion().isEmpty()) {
                Thread ffmpegVersion = new Thread(utils.Utility::setFfmpegVersion);
                ffmpegVersion.start();
                while (ffmpegVersion.isAlive()) {
                    sleep(100);
                }
            }
            System.out.println("FFMPEG version : " + AppSettings.GET.ffmpegVersion());
        }
    }

    private static void handleSpotifyPlaylist() {
        messageBroker.msgFilenameInfo("Retrieving the number of tracks in the playlist...");
        ArrayList<HashMap<String, Object>> playlistMetadata = Utility.getSpotifyPlaylistMetadata(link);
        if (!batchDownloading) {
            SC.nextLine(); // To remove 'whitespace' from input buffer. The whitespace will not be present in the input buffer if the user is using batch downloading because only yml file is parsed but no user input is taken.
        }
        if (playlistMetadata != null && !playlistMetadata.isEmpty()) {
            for (HashMap<String, Object> songMetadata : playlistMetadata) {
                messageBroker.msgStyleInfo(BANNER_BORDER);
                messageBroker.msgLinkInfo("[" + (playlistMetadata.indexOf(songMetadata) + 1) + "/" + playlistMetadata.size() + "] " + "Processing link : " + songMetadata.get("link"));
                link = songMetadata.get("link").toString();
                fileName = cleanFilename(songMetadata.get("songName").toString()) + ".webm";
                messageBroker.msgFilenameInfo(FILENAME_DETECTED + "\"" + fileName + "\"");
                songMetadata.remove("link");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String songMetadataJson = gson.toJson(songMetadata);
                checkHistoryAndDownload(new Job(link, downloadsFolder, fileName, songMetadataJson, false), false);
            }
        } else {
            messageBroker.msgLinkError("Failed to retrieve playlist metadata!");
        }
    }

    private static void batchDownloader() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        loaderOptions.setAllowRecursiveKeys(false);
        loaderOptions.setProcessComments(false);
        Yaml yamlParser = new Yaml(new SafeConstructor(loaderOptions));
        messageBroker.msgLogInfo("Trying to load YAML data file (" + batchDownloadingFile + ") ...");
        try (
                FileInputStream yamlInputStream = new FileInputStream(batchDownloadingFile);
                InputStreamReader yamlDataFile = new InputStreamReader(yamlInputStream)
                )
        {
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
                        if (isInstagram(link)) {
                            link = formatInstagramLink(link);
                        }
                        messageBroker.msgFilenameInfo("Retrieving filename from link...");
                        if (isSpotifyLink) {
                            HashMap<String, Object> spotifyMetadata = Utility.getSpotifySongMetadata(link);
                            if (spotifyMetadata != null && !spotifyMetadata.isEmpty()) {
                                fileName = spotifyMetadata.get("songName").toString() + ".webm";
                                messageBroker.msgFilenameInfo(FILENAME_DETECTED + "\"" + fileName + "\"");
                            } else {
                                fileName = cleanFilename("Unknown_Filename_") + randomString(15) + ".webm";
                                messageBroker.msgFilenameError(FILENAME_DETECTION_ERROR);
                            }
                        } else {
                            fileName = findFilenameInLink(link);
                        }
                    }
                }
                if (isSpotifyLink && link.contains("playlist")) {
                    handleSpotifyPlaylist();
                } else if (!Objects.requireNonNull(fileName).isEmpty()) {
                    verifyJobAndDownload(false);
                }
            }
        } catch (IOException e) {
            messageBroker.msgDownloadError("Failed to load YAML data file (" + batchDownloadingFile + ") ! " + e.getMessage());
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
        System.out.println("--version    -v            N/A                      Displays version number of Drifty");
        System.out.println("--update     -u            N/A                      Updates Drifty CLI to the latest version");
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

    private static void verifyJobAndDownload(boolean removeInputBufferFirst) {
        Job job;
        if (isSpotifyLink) {
            File spotifyMetadataFile = Program.getJsonDataPath().resolve("spotify-metadata.json").toFile();
            if (spotifyMetadataFile.exists()) {
                try {
                    String json = FileUtils.readFileToString(spotifyMetadataFile, Charset.defaultCharset());
                    job = new Job(link, downloadsFolder, fileName, json, false);
                } catch (IOException e) {
                    messageBroker.msgFilenameError("Failed to read Spotify metadata file! " + e.getMessage());
                    return;
                }
            } else {
                messageBroker.msgFilenameError("Spotify metadata file not found!");
                return;
            }
        } else {
            job = new Job(link, downloadsFolder, fileName, false);
        }
        checkHistoryAndDownload(job, removeInputBufferFirst);
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
                messageBroker.msgDownloadInfo("Trying to get download link for \"" + link + "\"");
                link = Utility.getSpotifyDownloadLink(job.getSpotifyMetadataJson());
            }
            if (link != null) {
                job = new Job(link, downloadsFolder, fileName, false);
                jobHistory.addJob(job, true);
                FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder, isSpotifyLink);
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
                    FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder, isSpotifyLink);
                    downloader.run();
                }
            }
        } else {
            jobHistory.addJob(job, true);
            renameFilenameIfRequired(removeInputBufferFirst);
            if (isSpotifyLink) {
                messageBroker.msgDownloadInfo("Trying to get download link for \"" + link + "\"");
                link = Utility.getSpotifyDownloadLink(job.getSpotifyMetadataJson());
            }
            if (link != null) {
                FileDownloader downloader = new FileDownloader(link, fileName, downloadsFolder, isSpotifyLink);
                downloader.run();
            }
        }
    }
}
