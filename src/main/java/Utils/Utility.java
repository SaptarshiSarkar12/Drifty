package Utils;

import Backend.DownloadFolderLocator;
import Enums.OS;
import Enums.Program;
import Preferences.AppSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.buildobjects.process.ProcBuilder;
import org.hildan.fxgson.FxGson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Enums.Program.SPOTDL;
import static Enums.Program.YT_DLP;
import static Utils.DriftyConstants.*;

public final class Utility {
    private static final Random RANDOM_GENERATOR = new Random(System.currentTimeMillis());
    private static final MessageBroker M = Environment.getMessageBroker();
    private static final Scanner SC = ScannerFactory.getInstance();
    private static boolean interrupted;

    public static boolean isYoutube(String url) {
        String pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+";
        return url.matches(pattern);
    }

    public static boolean isInstagram(String url) {
        String pattern = "(https?://(?:www\\.)?instagr(am|.am)?(\\.com)?/(p|reel)/([^/?#&]+)).*";
        return url.matches(pattern);
    }

    public static boolean isSpotify(String url) {
        String pattern = "(https?://(open.spotify\\.com|play\\.spotify\\.com)/(track|album|playlist)/[a-zA-Z0-9]+).*";
        return url.matches(pattern);
    }

    public static boolean isExtractableLink(String link) {
        return isYoutube(link) || isInstagram(link) || isSpotify(link);
    }

    public static boolean isLinkValid(String link) {
        try {
            URL url = URI.create(link).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Faster validation and hence improves performance
            connection.connect();
            M.msgLinkInfo("Link is valid!");
            return true;
        } catch (ConnectException e) {
            M.msgLinkError("Connection to the link timed out! Please check your internet connection. " + e.getMessage());
        } catch (UnknownHostException unknownHost) {
            try {
                URL projectWebsite = URI.create(DRIFTY_WEBSITE_URL).toURL();
                HttpURLConnection connectProjectWebsite = (HttpURLConnection) projectWebsite.openConnection();
                connectProjectWebsite.connect();
                M.msgLinkError("Link is invalid!"); // If our project website can be connected to, then the one entered by user is not valid! [NOTE: UnknownHostException is thrown if either internet is not connected or the website address is incorrect]
            } catch (UnknownHostException e) {
                M.msgLinkError("You are not connected to the Internet!");
            } catch (MalformedURLException e) {
                M.msgLinkError("The link is not correctly formatted! " + e.getMessage());
            } catch (IOException e) {
                M.msgLinkError("Failed to connect to the project website! " + e.getMessage());
            }
        } catch (ProtocolException e) {
            M.msgLinkError("An error occurred with the protocol! " + e.getMessage());
        } catch (MalformedURLException e) {
            M.msgLinkError("The link is not correctly formatted! " + e.getMessage());
        } catch (IOException e) {
            M.msgLinkError("Failed to connect to " + link + " ! " + e.getMessage());
        } catch (IllegalArgumentException e) {
            M.msgLinkError(link + " is not a URL; error: " + e.getMessage());
        }
        return false;
    }

    public static String findFilenameInLink(String link) {
        String filename = "";
        if (isInstagram(link) || isYoutube(link)) {
            LinkedList<String> linkMetadataList = Utility.getLinkMetadata(link);
            for (String json : Objects.requireNonNull(linkMetadataList)) {
                filename = Utility.getFilenameFromJson(json);
            }
        } else if (isSpotify(link)) {
            LinkedList<String> linkMetadataList = Utility.getLinkMetadata(link);
            for (String json : Objects.requireNonNull(linkMetadataList)) {
                filename = Utility.extractSpotifyFilename(json);
            }
        } else {
            // Example: "example.com/file.txt" prints "Filename detected: file.txt"
            // example.com/file.json -> file.json
            String file = link.substring(link.lastIndexOf("/") + 1);
            if (file.isEmpty()) {
                M.msgFilenameError(AUTO_FILE_NAME_DETECTION_FAILED);
                return null;
            }
            int index = file.lastIndexOf(".");
            if (index < 0) {
                M.msgFilenameError(AUTO_FILE_NAME_DETECTION_FAILED);
                return null;
            }
            String extension = file.substring(index);
            // edge case 1: "example.com/."
            if (extension.length() == 1) {
                M.msgFilenameError(AUTO_FILE_NAME_DETECTION_FAILED);
                return null;
            }
            // file.png?width=200 -> file.png
            filename = file.split("([?])")[0];
            M.msgFilenameInfo(FILENAME_DETECTED + "\"" + filename + "\"");
        }
        return filename;
    }

    public static String getHomeDownloadFolder() {
        String downloadsFolder;
        M.msgDirInfo(TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER);
        if (!OS.isWindows()) {
            String home = System.getProperty(USER_HOME_PROPERTY);
            downloadsFolder = home + DOWNLOADS_FILE_PATH;
        } else {
            downloadsFolder = DownloadFolderLocator.findPath() + System.getProperty("file.separator");
        }
        if (downloadsFolder.equals(System.getProperty("file.separator"))) {
            M.msgDirError(FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER);
        } else {
            M.msgDirInfo(DEFAULT_DOWNLOAD_FOLDER + downloadsFolder);
        }
        return downloadsFolder;
    }

    public boolean yesNoValidation(String input, String printMessage) {
        while (input.isEmpty()) {
            Environment.getMessageBroker().msgInputError(ENTER_Y_OR_N, true);
            M.msgLogError(ENTER_Y_OR_N);
            Environment.getMessageBroker().msgInputInfo(printMessage, false);
            input = SC.nextLine().toLowerCase();
        }
        char choice = input.charAt(0);
        if (choice == 'y') {
            return true;
        } else if (choice == 'n') {
            return false;
        } else {
            Environment.getMessageBroker().msgInputError("Invalid input!", true);
            M.msgLogError("Invalid input!");
            Environment.getMessageBroker().msgInputInfo(printMessage, false);
            input = SC.nextLine().toLowerCase();
            yesNoValidation(input, printMessage);
        }
        return false;
    }

    public static LinkedList<String> getLinkMetadata(String link) {
        try {
            LinkedList<String> list = new LinkedList<>();
            File driftyJsonFolder = Program.getJsonDataPath().toFile();
            if (driftyJsonFolder.exists() && driftyJsonFolder.isDirectory()) {
                FileUtils.forceDelete(driftyJsonFolder); // Deletes the previously generated temporary directory for Drifty
            }
            if (!driftyJsonFolder.mkdir()) {
                M.msgLinkError("Failed to create temporary directory for Drifty to get link metadata!");
                return null;
            }
            Thread linkThread;
            if (isSpotify(link)) {
                linkThread = new Thread(spotdlJsonData(Program.getSpotdlDataPath().toFile().getAbsolutePath(), link));
            } else {
                linkThread = new Thread(ytDLPJsonData(driftyJsonFolder.getAbsolutePath(), link));
            }
            try {
                linkThread.start();
            } catch (Exception e) {
                M.msgLinkError("Failed to start thread to get link metadata! " + e.getMessage());
                return null;
            }
            while (!linkThread.getState().equals(Thread.State.TERMINATED) && !linkThread.isInterrupted()) {
                sleep(100);
                interrupted = linkThread.isInterrupted();
            }
            if (interrupted) {
                FileUtils.forceDelete(driftyJsonFolder);
                return null;
            }
            File[] files = driftyJsonFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String ext = FilenameUtils.getExtension(file.getAbsolutePath());
                    if (ext.toLowerCase().contains("json") || ext.toLowerCase().contains("spotdl")) {
                        String linkMetadata = FileUtils.readFileToString(file, Charset.defaultCharset());
                        list.addLast(linkMetadata);
                    }
                }
                FileUtils.forceDelete(driftyJsonFolder); // delete the metadata files of Drifty from the config directory
            }
            return list;
        } catch (IOException e) {
            M.msgLinkError("Failed to perform I/O operations on link metadata! " + e.getMessage());
            return null;
        }
    }

    public static String makePretty(String json) {
        // The regex strings won't match unless the json string is converted to pretty format
        GsonBuilder g = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(g).setPrettyPrinting().create();
        JsonElement element = JsonParser.parseString(json);
        return gson.toJson(element);
    }

    public static String renameFile(String filename, String dir) {
        Path path = Paths.get(dir, filename);
        String newFilename = filename;
        int fileNum = -1;
        String baseName = FilenameUtils.getBaseName(filename.replaceAll(" \\(\\d+\\)\\.", "."));
        String ext = "." + FilenameUtils.getExtension(filename);
        while (path.toFile().exists()) {
            fileNum += 1;
            newFilename = baseName + " (" + fileNum + ")" + ext;
            path = Paths.get(dir, newFilename);
        }
        return newFilename;
    }

    public static String getFilenameFromJson(String jsonString) {
        String json = makePretty(jsonString);
        String filename;
        String regexFilename = "(\"title\": \")(.+)(\",)";
        Pattern p = Pattern.compile(regexFilename);
        Matcher m = p.matcher(json);
        if (m.find()) {
            filename = cleanFilename(m.group(2)) + ".mp4";
            M.msgFilenameInfo(FILENAME_DETECTED + "\"" + filename + "\"");
        } else {
            filename = cleanFilename("Unknown_Filename_") + randomString(15) + ".mp4";
            M.msgFilenameError(FILENAME_DETECTION_ERROR);
        }
        return filename;
    }

    public static String extractSpotifyFilename(String spotdlData) {
        String json = makePretty(spotdlData);
        String filename;
        String regex = "(\"name\": \")(.+)(\",)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(json);
        if (m.find()) {
            filename = cleanFilename(m.group(2)) + ".mp3";
            M.msgFilenameInfo(FILENAME_DETECTED + "\"" + filename + "\"");
        } else {
            filename = cleanFilename("Unknown_Filename_") + randomString(15) + ".mp3";
            M.msgFilenameError(FILENAME_DETECTION_ERROR);
        }
        return filename;
    }

    public static String cleanFilename(String filename) {
        String fn = StringEscapeUtils.unescapeJava(filename);
        return fn.replaceAll("[^a-zA-Z0-9-._)<(> ]+", "").strip();
    }

    private static Runnable ytDLPJsonData(String folderPath, String link) {
        return () -> {
            String[] command = new String[]{Program.get(YT_DLP), "--write-info-json", "--skip-download", "--restrict-filenames", "-P", folderPath, link};
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("ERROR") || line.contains("WARNING")) {
                        if (line.contains("unable to extract username")) {
                            M.msgLinkError("The Instagram post/reel is private!");
                            break;
                        } else if (line.contains("The playlist does not exist")) {
                            M.msgLinkError("The YouTube playlist does not exist or is private!");
                            break;
                        } else if (line.contains("Video unavailable")) {
                            M.msgLinkError("The YouTube video is unavailable!");
                        } else {
                            M.msgLinkError("Failed to retrieve filename!");
                        }
                    }
                }
            } catch (Exception e) {
                M.msgLinkError("Failed to get link metadata! " + e.getMessage());
            }
        };
    }

    private static Runnable spotdlJsonData(String folderPath, String link) {
        return () -> {
            String command = Program.get(SPOTDL);
            String[] args = new String[]{"save", link, "--save-file", folderPath};
            new ProcBuilder(command)
                    .withArgs(args)
                    .withErrorStream(System.err)
                    .withNoTimeout()
                    .run();
        };
    }

    public static boolean isURL(String text) {
        String regex = "^(http(s)?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            M.msgLinkError("The calling method failed to sleep for " + time + " milliseconds. It got interrupted. " + e.getMessage());
        }
    }

    public static String randomString(int characterCount) {
        String source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int count = source.length();
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < characterCount; x++) {
            int index = RANDOM_GENERATOR.nextInt(count);
            sb.append(source.charAt(index));
        }
        return sb.toString();
    }

    public static String getSpotifyDownloadLink(String link) {
        M.msgDownloadInfo("Trying to get download link for \"" + link + "\"");
        // Remove si parameter from the link
        link = link.replaceAll("\\?si=.*", "");
        String spotDLPath = Program.get(Program.SPOTDL);
        ProcessBuilder processBuilder = new ProcessBuilder(spotDLPath, "url", link);
        Process process;
        try {
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(process).getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("Processing query:") && line.startsWith("http")) {
                        M.msgDownloadInfo("Download link retrieved successfully!");
                        return line;
                    } else if (line.contains("No results found for song")) {
                        M.msgDownloadError("Song is exclusive to Spotify and cannot be downloaded!");
                        return null;
                    }
                }
            } catch (IOException e) {
                M.msgDownloadError("Failed to get download link for \"" + link + "\"!");
                return null;
            }
        } catch (IOException e) {
            M.msgDownloadError("Failed to get download link for \"" + link + "\"!");
        }
        return null;
    }

    public static Runnable setYtDlpVersion() {
        return () -> {
            String command = Program.get(YT_DLP);
            ProcessBuilder getYtDlpVersion = new ProcessBuilder(command, "--version");
            Process ytDlpVersionTask;
            try {
                ytDlpVersionTask = getYtDlpVersion.start();
            } catch (IOException e) {
                M.msgInitError("Failed to get yt-dlp version! " + e.getMessage());
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ytDlpVersionTask).getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    AppSettings.SET.ytDlpVersion(line);
                }
            } catch (IOException e) {
                M.msgInitError("Failed to get yt-dlp version! " + e.getMessage());
            }
        };
    }

    public static Runnable setSpotDLVersion() {
        return () -> {
            String command = Program.get(SPOTDL);
            ProcessBuilder getSpotdlVersion = new ProcessBuilder(command, "--version");
            Process spotdlVersionTask;
            try {
                spotdlVersionTask = getSpotdlVersion.start();
            } catch (IOException e) {
                M.msgInitError("Failed to get spotDL version! " + e.getMessage());
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(spotdlVersionTask).getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    AppSettings.SET.spotDLVersion(line);
                }
            } catch (IOException e) {
                M.msgInitError("Failed to get spotDL version! " + e.getMessage());
            }
        };
    }
}
