package main;

import backend.FileDownloader;
import cli.init.Environment;
import cli.updater.CLIUpdateExecutor;
import cli.utils.MessageBroker;
import cli.utils.ScannerFactory;
import cli.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import preferences.AppSettings;
import properties.MessageType;
import properties.OS;
import properties.Program;
import support.Job;
import support.JobHistory;
import updater.UpdateChecker;
import utils.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

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
    private static final String YAML_FILENAME = "links.yml";
    private static String yamlFilePath;

    public static void main(String[] args) {
        LOGGER.log(MessageType.INFO, CLI_APPLICATION_STARTED);
        messageBroker = new MessageBroker(System.out);
        Environment.setCLIMessageBroker(messageBroker);
        utility = new Utility();
        checkAndUpdateDrifty(true);
        messageBroker.msgInitInfo("Initializing environment...");
        Environment.initializeEnvironment();
        messageBroker.msgInitInfo("Environment initialized successfully!");
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
                        if (i + 1 < args.length) {
                            name = args[i + 1];
                            i++; // Skip the next iteration as we have already processed this argument
                        } else {
                            messageBroker.msgInitError("No filename specified!");
                            Environment.terminate(1);
                        }
                    }
                    case LOCATION_FLAG, LOCATION_FLAG_SHORT -> {
                        if (i + 1 < args.length) {
                            location = args[i + 1];
                            i++; // Skip the next iteration as we have already processed this argument
                        } else {
                            messageBroker.msgInitError("No download directory specified!");
                            Environment.terminate(1);
                        }
                    }
                    case VERSION_FLAG, VERSION_FLAG_SHORT -> {
                        printVersion();
                        Environment.terminate(0);
                    }
                    case UPDATE_FLAG, UPDATE_FLAG_SHORT -> {
                        if (Utility.isOffline()) {
                            messageBroker.msgUpdateError("Failed to check for updates! You are not connected to the internet.");
                            Environment.terminate(1);
                        } else {
                            checkAndUpdateDrifty(false);
                        }
                    }
                    case EARLY_ACCESS_FLAG, EARLY_ACCESS_FLAG_SHORT -> {
                        AppSettings.SET.earlyAccess(!AppSettings.GET.earlyAccess());
                        messageBroker.msgInitInfo("Early access mode " + (AppSettings.GET.earlyAccess() ? "enabled!" : "disabled!"));
                        Environment.terminate(0);
                    }
                    case ADD_FLAG -> {
                        setYamlFilePath();
                        if (i + 1 < args.length) {
                            for (int j = i + 1; j < args.length; j++) {
                                addUrlToFile(args[j]);
                            }
                            i = args.length; // Skip the remaining iterations as we have already processed these arguments
                            Environment.terminate(0);
                        } else {
                            messageBroker.msgBatchError("No URL(s) provided for adding!");
                            Environment.terminate(1);
                        }
                    }
                    case LIST_FLAG -> {
                        setYamlFilePath();
                        listUrls();
                    }
                    case GET_FLAG -> {
                        setYamlFilePath();
                        batchDownloading = true;
                        batchDownloadingFile = yamlFilePath;
                        ensureYamlFileExists();
                        if (isEmptyYaml(loadYamlData())) {
                            Environment.terminate(1);
                        }
                        batchDownloader();
                        removeAllUrls();
                        Environment.terminate(0);
                    }
                    case REMOVE_FLAG -> {
                        setYamlFilePath();
                        if (i + 1 >= args.length) {
                            messageBroker.msgBatchError("No line number provided for removal!");
                            Environment.terminate(1);
                        }
                        if ("all".equalsIgnoreCase(args[i + 1])) {
                            messageBroker.msgInputInfo(REMOVE_ALL_URL_CONFIRMATION, false);
                            String choiceString = SC.nextLine().toLowerCase();
                            boolean choice = utility.yesNoValidation(choiceString, REMOVE_ALL_URL_CONFIRMATION);
                            if (choice) {
                                removeAllUrls();
                            }
                        } else {
                            String[] indexStr = Arrays.copyOfRange(args, i + 1, args.length);
                            removeUrl(indexStr);
                        }
                        i = args.length; // Skip the remaining iterations as we have already processed these arguments
                        Environment.terminate(0);
                    }
                    case BATCH_FLAG, BATCH_FLAG_SHORT -> {
                        if (i + 1 < args.length) {
                            batchDownloadingFile = args[i + 1];
                            if (!(batchDownloadingFile.endsWith(".yml") || batchDownloadingFile.endsWith(".yaml"))) {
                                messageBroker.msgBatchError("The data file should be a YAML file!");
                                Environment.terminate(1);
                            }
                            if (!Paths.get(batchDownloadingFile).toFile().exists()) {
                                messageBroker.msgBatchError("YAML data file \"" + batchDownloadingFile + "\" does not exist!");
                                Environment.terminate(1);
                            }
                            i++; // Skip the next iteration as we have already processed this argument
                            batchDownloader();
                        } else {
                            messageBroker.msgInitError("No batch file specified!");
                            Environment.terminate(1);
                        }
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
                            if (fileName != null && !fileName.isEmpty()) {
                                verifyJobAndDownload(false);
                            }
                        }
                    } else {
                        fileName = name;
                        messageBroker.msgFilenameInfo("Filename provided : " + fileName);
                        verifyJobAndDownload(false);
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
                        if (fileName != null && !fileName.isEmpty()) {
                            verifyJobAndDownload(true);
                        }
                    }
                } else {
                    if (isInstagram(link)) {
                        link = formatInstagramLink(link);
                    }
                    else if (isYoutube(link)) {
                        try {
                            link = formatYoutubeLink(link);
                        } catch (IllegalArgumentException | URISyntaxException | MalformedURLException e) {
                            messageBroker.msgLinkError("Failed to process the YouTube link: " + e.getMessage());
                            messageBroker.msgInputInfo(QUIT_OR_CONTINUE, true);
                            String choice = SC.next().toLowerCase().strip();
                            if ("q".equals(choice)) {
                                LOGGER.log(MessageType.INFO, CLI_APPLICATION_TERMINATED);
                                break;
                            }
                            printBanner();
                            continue;
                        }
                    }
                    messageBroker.msgFilenameInfo("Retrieving filename from link...");
                    fileName = findFilenameInLink(link);
                    if (fileName != null && !fileName.isEmpty()) {
                        verifyJobAndDownload(true);
                    } else {
                        messageBroker.msgFilenameError("Failed to retrieve filename from link!");
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
        if (!isDriftyUpdateChecked() || !askForInstallingUpdate) {
            messageBroker.msgInitInfo("Checking for updates...");
            if (Utility.isOffline()) {
                messageBroker.msgUpdateWarning("Failed to check for updates! You are not connected to the internet.");
                if (!askForInstallingUpdate) { // For the case when updates are checked in the background
                    Environment.terminate(1);
                }
            } else if (UpdateChecker.isUpdateAvailable()) {
                handleUpdateAvailable(askForInstallingUpdate);
            } else {
                messageBroker.msgUpdateInfo("Drifty is up to date!");
                if (!askForInstallingUpdate) { // For the case when updates are checked in the background
                    Environment.terminate(0);
                }
            }
        }
    }

    private static void handleUpdateAvailable(boolean askForInstallingUpdate) {
        messageBroker.msgUpdateInfo("Update available!");
        messageBroker.msgUpdateInfo("Latest version : " + AppSettings.GET.latestDriftyVersionTag() + " (" + AppSettings.GET.newDriftyVersionName() + ")");
        if (Environment.hasAdminPrivileges()) {
            boolean choice = true;
            if (askForInstallingUpdate) {
                choice = getUserConfirmation();
            }
            if (!choice) {
                messageBroker.msgUpdateInfo("Drifty update cancelled!");
            } else {
                downloadAndUpdate();
            }
        } else {
            handleAdminPrivilegesRequired(askForInstallingUpdate);
        }
    }

    private static boolean getUserConfirmation() {
        messageBroker.msgUpdateInfo("Do you want to download the update? (Enter Y for yes and N for no) : ");
        String choiceString = SC.nextLine().toLowerCase();
        return utility.yesNoValidation(choiceString, "Do you want to download the update? (Enter Y for yes and N for no) : ");
    }

    private static void downloadAndUpdate() {
        messageBroker.msgUpdateInfo("Downloading update...");
        if (!downloadUpdate()) {
            messageBroker.msgUpdateError("Failed to update Drifty!");
            Environment.terminate(1);
        } else {
            messageBroker.msgUpdateInfo("Update successful!");
            messageBroker.msgUpdateInfo("Please restart Drifty to see the changes!");
            Environment.terminate(0);
        }
    }

    private static void handleAdminPrivilegesRequired(boolean askForInstallingUpdate) {
        if (askForInstallingUpdate) {
            messageBroker.msgUpdateWarning("Drifty update requires administrator privileges!");
            messageBroker.msgUpdateWarning("Please run Drifty with administrator privileges to update!");
        } else {
            messageBroker.msgUpdateError("Drifty update requires administrator privileges!");
            messageBroker.msgUpdateError("Please run Drifty with administrator privileges to update!");
            Environment.terminate(1);
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
            FileDownloader downloader = new FileDownloader(updateURL.toString(), currentExecutableFile.getName(), tmpFolder.toString(), false);
            downloader.run();
            if (latestExecutableFile.exists() && latestExecutableFile.isFile() && latestExecutableFile.length() > 0) {
                // If the latest executable was successfully downloaded, set the executable permission and execute the update.
                CLIUpdateExecutor updateExecutor = new CLIUpdateExecutor(currentExecutableFile, latestExecutableFile);
                return updateExecutor.execute();
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
        if (AppSettings.GET.ffmpegVersion().isEmpty()) {
            Thread ffmpegVersion = new Thread(utils.Utility::setFfmpegVersion);
            ffmpegVersion.start();
            while (ffmpegVersion.isAlive()) {
                sleep(100);
            }
        }
        System.out.println("FFMPEG version : " + AppSettings.GET.ffmpegVersion());
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
        Yaml yamlParser = Utility.getYamlParser();
        messageBroker.msgLogInfo("Trying to load YAML data file (" + batchDownloadingFile + ") ...");
        try (FileInputStream yamlInputStream = new FileInputStream(batchDownloadingFile); InputStreamReader yamlDataFile = new InputStreamReader(yamlInputStream)) {
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
                if (!isURL(link)) {
                    messageBroker.msgLinkError("Invalid URL : " + link);
                    continue;
                }
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
                } else if (fileName != null && !fileName.isEmpty()) {
                    verifyJobAndDownload(false);
                }
            }
        } catch (IOException e) {
            messageBroker.msgDownloadError("Failed to load YAML data file (" + batchDownloadingFile + ") ! " + e.getMessage());
        } catch (YAMLException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("duplicate key")) {
                messageBroker.msgBatchError("Duplicate keys are not allowed in the YAML file!");
            } else if (errorMessage.contains("recursive key")) {
                messageBroker.msgBatchError("Recursive keys are not allowed in the YAML file!");
            } else {
                messageBroker.msgBatchError("An unknown error occurred while parsing the YAML file! " + errorMessage);
            }
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
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m------------------------==| DRIFTY CLI HELP |==------------------------" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m\t\t\t\t\t\t\t" + VERSION_NUMBER + ANSI_RESET);
        System.out.println("\033[31;1mRequired parameter: File URL" + ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName           ShortForm     Default                  Description" + ANSI_RESET);
        System.out.println("--batch         -b            N/A                      The path to the YAML file containing the links and other arguments");
        System.out.println("--location      -l            Downloads folder         The folder where the downloaded file(s) will be saved");
        System.out.println("--name          -n            Source                   Filename of the downloaded file");
        System.out.println("--help          -h            N/A                      Prints this help menu");
        System.out.println("--version       -v            Current Version          Displays version number of Drifty");
        System.out.println("--update        -u            N/A                      Updates Drifty CLI to the latest version");
        System.out.println("--early-access  -ea           Disabled by default      Toggle early access mode");
        System.out.println("--add           N/A           N/A                      Add URL(s) to the links queue");
        System.out.println("--remove        N/A           N/A                      Remove URL(s) from the links queue");
        System.out.println("--list          N/A           N/A                      List all the URLs in the links queue");
        System.out.println("--get           N/A           N/A                      Download all the URLs in the links queue");
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

    private static String normalizeUrl(String urlString) {
        try {
            URI uri = new URI(urlString.trim());
            String scheme = uri.getScheme();
            String authority = uri.getAuthority();
            String path = uri.getPath();
            String query = uri.getQuery();
            // Normalize the scheme to lowercase
            if (scheme != null) {
                scheme = scheme.toLowerCase();
            }
            // Decode path
            if (path != null) {
                path = path.replaceAll("%2F", "/");
            }
            // Reconstruct the URI without the unwanted components
            URI normalizedUri = new URI(scheme, authority, path, query, null);
            // Remove trailing slash from the path, except for root '/'
            String normalizedUrl = normalizedUri.toString();
            if (!"/".equals(path) && normalizedUrl.endsWith("/")) {
                normalizedUrl = normalizedUrl.substring(0, normalizedUrl.length() - 1);
            }
            return normalizedUrl;
        } catch (URISyntaxException e) {
            messageBroker.msgLinkError("Invalid URL: " + e.getMessage());
            return urlString;
        } catch (Exception e) {
            messageBroker.msgLinkError("An unexpected error occurred during URL normalization: " + e.getMessage());
            return urlString;
        }
    }

    private static void ensureYamlFileExists() {
        // Check if the links queue file exists, and create it if it doesn't
        File yamlFile = new File(yamlFilePath);
        messageBroker.msgLogInfo("Checking if links queue file (" + yamlFilePath + ") exists...");
        if (!yamlFile.exists()) {
            try {
                boolean isNewFileCreated = yamlFile.createNewFile();
                boolean isReadable = yamlFile.setReadable(true, true);
                boolean isWritable = yamlFile.setWritable(true, true);
                if (isNewFileCreated && isReadable && isWritable) {
                    messageBroker.msgLogInfo("New links queue file created: " + yamlFilePath);
                } else if (!isNewFileCreated) {
                    messageBroker.msgBatchError("Failed to create links queue file: " + yamlFilePath);
                    Environment.terminate(1);
                } else {
                    messageBroker.msgBatchError("Failed to set file permissions for the links queue file: " + yamlFilePath);
                    Environment.terminate(1);
                }
            } catch (IOException e) {
                messageBroker.msgBatchError("Error creating links queue file: " + e.getMessage());
                Environment.terminate(1);
            }
        }
    }

    private static boolean isEmptyYaml(Map<String, List<String>> data) {
        if (data == null || !data.containsKey("links") || data.get("links").isEmpty()) {
            messageBroker.msgLinkError("No link is present in the links queue!\n" + "Please run with \"--add <link>\" to add a link to the list.");
            return true;
        }
        return false;
    }

    private static Map<String, List<String>> loadYamlData() {
        Yaml yamlParser = Utility.getYamlParser();
        Map<String, List<String>> data = null;
        ensureYamlFileExists(); // Ensure the YAML file exists before trying to read

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(yamlFilePath))) {
            data = yamlParser.load(reader);
            if (data == null) {
                data = new HashMap<>();
            }
            data.computeIfAbsent("links", k -> new ArrayList<>()); // Ensure 'links' list exists
        } catch (YAMLException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("duplicate key")) {
                messageBroker.msgBatchError("Duplicate keys are not allowed in the YAML file!");
            } else if (errorMessage.contains("recursive key")) {
                messageBroker.msgBatchError("Recursive keys are not allowed in the YAML file!");
            } else {
                messageBroker.msgBatchError("An unknown error occurred while parsing the YAML file! " + errorMessage);
            }
        } catch (IOException e) {
            messageBroker.msgBatchError("Error reading YAML file: " + e.getMessage());
        }
        return data;
    }

    private static void saveYamlData(Map<String, List<String>> data) {
        Yaml yaml = new Yaml();
        File originalFile = new File(yamlFilePath);
        File backupFile = new File(yamlFilePath + ".bak");

        // Create a backup of the original file
        try {
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            messageBroker.msgLinkError("Failed to create a backup of the YAML file: " + e.getMessage());
            messageBroker.msgLinkError("Aborting the operation to prevent data loss!");
            Environment.terminate(1);
        }

        // Proceed with writing the updated data to the YAML file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(originalFile))) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            messageBroker.msgBatchError("An error occurred while writing to the YAML file: " + e.getMessage());
            // Attempt to restore from backup in case of write error
            try {
                Files.copy(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                messageBroker.msgBatchError("Restored the original YAML file from backup due to write error.");
            } catch (IOException restoreException) {
                messageBroker.msgBatchError("Failed to restore the original YAML file from backup: " + restoreException.getMessage());
            }
        } finally {
            // Clean up: Delete the backup file if everything went smoothly
            if (backupFile.exists() && !backupFile.delete()) {
                messageBroker.msgLogError("Failed to delete the backup file: " + backupFile.getName());
            }
        }
    }

    private static void listUrls() {
        try {
            Map<String, List<String>> data = loadYamlData();
            if (isEmptyYaml(data)) {
                Environment.terminate(1);
            }
            List<String> urls = data.get("links");
            messageBroker.msgDownloadInfo("List of links:");
            for (int i = 0; i < urls.size(); i++) {
                messageBroker.msgLinkInfo((i + 1) + ". " + urls.get(i));
            }
            Environment.terminate(0);
        } catch (Exception e) {
            messageBroker.msgBatchError("An error occurred while listing the links: " + e.getMessage());
            Environment.terminate(1);
        }
    }

    private static void removeUrl(String[] args) {
        try {
            Map<String, List<String>> data = loadYamlData();
            if (isEmptyYaml(data)) {
                Environment.terminate(1);
            }
            List<String> urls = data.get("links");
            Set<Integer> uniqueIndices = new TreeSet<>(Collections.reverseOrder()); // TreeSet to sort in reverse order automatically
            for (String indexStr : args) {
                int index;
                try {
                    index = Integer.parseInt(indexStr);
                    if (index < 0 || index > urls.size()) {
                        if (urls.size() == 1) {
                            messageBroker.msgInputError("Invalid line number '" + index + "'. Please provide '1' to remove the only link in the list!", true);
                        } else {
                            messageBroker.msgInputError("Invalid line number '" + index + "'. Please provide a number between 1 and " + urls.size() + "!", true);
                        }
                        Environment.terminate(1); // Exit the program if the input is invalid
                        return;
                    }
                    uniqueIndices.add(index);
                } catch (NumberFormatException e) {
                    messageBroker.msgInputError("Invalid format. Please provide a numeric input.", true);
                    return;
                }
            }
            for (int index : uniqueIndices) {
                String removedUrl = urls.remove(index - 1);
                messageBroker.msgLinkInfo("Removed URL: " + removedUrl);
            }
            saveYamlData(data); // Save updated YAML data
        } catch (Exception e) {
            messageBroker.msgBatchError("An error occurred while removing a link: " + e.getMessage());
            Environment.terminate(1);
        }
    }


    private static void removeAllUrls() {
        try {
            // Delete the YAML file to remove all URLs
            File yamlFile = new File(yamlFilePath);
            if (yamlFile.exists()) {
                if (yamlFile.delete()) {
                    if (batchDownloading) {
                        messageBroker.msgLogInfo("All links removed successfully from the YAML file.");
                    } else {
                        messageBroker.msgLinkInfo("All links removed successfully.");
                    }
                } else {
                    messageBroker.msgLinkError("Failed to remove all links!");
                    Environment.terminate(1);
                }
            } else {
                messageBroker.msgLinkError("No links to remove!");
                Environment.terminate(1);
            }
        } catch (Exception e) {
            messageBroker.msgLinkError("An error occurred while removing all URLs: " + e.getMessage());
            Environment.terminate(1);
        }
    }

    private static void addUrlToFile(String urlString) {
        if (!Utility.isURL(urlString)) {
            messageBroker.msgInputError("Error: \"" + urlString + "\" is not a valid link!", true);
            return;
        }
        try {
            Map<String, List<String>> data = loadYamlData();
            urlString = normalizeUrl(urlString);
            List<String> urls = data.get("links");
            if (!urls.contains(urlString)) {
                urls.add(urlString); // Add the URL if it doesn't exist
                saveYamlData(data); // Save the updated data back to the YAML file
                messageBroker.msgLinkInfo("Link added: " + urlString);
            } else {
                messageBroker.msgInputError("Link already exists: \"" + urlString + "\"", true);
            }
        } catch (NullPointerException e) {
            messageBroker.msgBatchError("Failed to add link to the YAML file! The YAML data is null.");
            Environment.terminate(1);
        } catch (Exception e) {
            messageBroker.msgBatchError("An unknown error occurred while adding URL to the YAML file: " + e.getMessage());
            Environment.terminate(1);
        }
    }

    private static void setYamlFilePath() {
        try {
            yamlFilePath = Paths.get(Program.get(Program.DRIFTY_PATH)).resolve(YAML_FILENAME).toString();
        } catch (InvalidPathException e) {
            messageBroker.msgBatchError("Failed to initialize YAML file path! Invalid path: " + e.getMessage());
            Environment.terminate(1);
        }
    }
}
