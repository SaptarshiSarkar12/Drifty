package Utils;

import Backend.DownloadFolderLocator;
import Backend.Drifty;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.OS;
import Enums.Program;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.buildobjects.process.ProcBuilder;
import org.hildan.fxgson.FxGson;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utils.DriftyConstants.*;

public final class Utility {
    static MessageBroker messageBroker = Environment.getMessageBroker();
    private static final Scanner SC = ScannerFactory.getInstance();
    private static Thread linkThread;
    private static boolean interrupted;
    private static long startTime;

    public static void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    public static long timeSinceStart() {
        return System.currentTimeMillis() - startTime;
    }

    public Utility() {}

    public static boolean isYoutubeLink(String url) {
        String pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+";
        return url.matches(pattern);
    }

    public static boolean isInstagramLink(String url) {
        String pattern = "(https?://(?:www\\.)?instagr(am|.am)?(\\.com)?/p/([^/?#&]+)).*";
        return url.matches(pattern);
    }

    public static boolean isURLValid(String link) {
        try {
            URL url = URI.create(link).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Faster validation and hence improves performance
            connection.connect();
            messageBroker.sendMessage("Link is valid!", MessageType.INFO, MessageCategory.LINK);
            return true;
        } catch (ConnectException e) {
            messageBroker.sendMessage("Connection to the link timed out! Please check your internet connection. " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        } catch (UnknownHostException unknownHost) {
            try {
                URL projectWebsite = URI.create(Drifty.projectWebsite).toURL();
                HttpURLConnection connectProjectWebsite = (HttpURLConnection) projectWebsite.openConnection();
                connectProjectWebsite.connect();
                messageBroker.sendMessage("Link is invalid!", MessageType.ERROR, MessageCategory.LINK); // If our project website can be connected to, then the one entered by user is not valid! [NOTE: UnknownHostException is thrown if either internet is not connected or the website address is incorrect]
            } catch (UnknownHostException e) {
                messageBroker.sendMessage("You are not connected to the Internet!", MessageType.ERROR, MessageCategory.LINK);
            } catch (MalformedURLException e) {
                messageBroker.sendMessage("The link is not correctly formatted! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
            } catch (IOException e) {
                messageBroker.sendMessage("Failed to connect to the project website! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
            }
        } catch (ProtocolException e) {
            messageBroker.sendMessage("An error occurred with the protocol! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        } catch (MalformedURLException e) {
            messageBroker.sendMessage("The link is not correctly formatted! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        } catch (IOException e) {
            messageBroker.sendMessage("Failed to connect to " + link + " ! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        }
        return false;
    }

    public static boolean urlIsValid(String link) {
        try {
            URL url = URI.create(link).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Faster validation and hence improves performance
            connection.connect();
            URL projectWebsite = URI.create(Drifty.projectWebsite).toURL();
            HttpURLConnection connectProjectWebsite = (HttpURLConnection) projectWebsite.openConnection();
            connectProjectWebsite.connect();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static String findFilenameInLink(String link) {
        String fileName = "";
        if (isInstagramLink(link) || isYoutubeLink(link)) {
            LinkedList<String> linkMetadataList = Utility.getLinkMetadata(link);
            for (String json : linkMetadataList) {
                fileName = Utility.getFilenameFromJson(json);
            }
        } else {
            // Example: "example.com/file.txt" prints "Filename detected: file.txt"
            // example.com/file.json -> file.json
            String file = link.substring(link.lastIndexOf("/") + 1);
            int index = file.lastIndexOf(".");
            if (index < 0) {
                messageBroker.sendMessage(AUTO_FILE_NAME_DETECTION_FAILED, MessageType.ERROR, MessageCategory.FILENAME);
                return null;
            }
            String extension = file.substring(index);
            // edge case 1: "example.com/."
            if (extension.length() == 1) {
                messageBroker.sendMessage(AUTO_FILE_NAME_DETECTION_FAILED, MessageType.ERROR, MessageCategory.FILENAME);
                return null;
            }
            // file.png?width=200 -> file.png
            fileName = file.split("([?])")[0];
            messageBroker.sendMessage(FILENAME_DETECTED + fileName, MessageType.INFO, MessageCategory.FILENAME);
        }
        return fileName;
    }

    public static String getFormattedDefaultDownloadsFolder() {
        String downloadsFolder;
        messageBroker.sendMessage(TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER, MessageType.INFO, MessageCategory.DIRECTORY);
        if (!OS.isWindows()) {
            String home = System.getProperty(USER_HOME_PROPERTY);
            downloadsFolder = home + DOWNLOADS_FILE_PATH;
        } else {
            downloadsFolder = DownloadFolderLocator.findPath() + System.getProperty("file.separator");
        }

        if (downloadsFolder.equals(System.getProperty("file.separator"))) {
            messageBroker.sendMessage(FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER, MessageType.ERROR, MessageCategory.DIRECTORY);
        } else {
            messageBroker.sendMessage(DEFAULT_DOWNLOAD_FOLDER + downloadsFolder, MessageType.INFO, MessageCategory.DIRECTORY);
        }
        return downloadsFolder;
    }

    public boolean yesNoValidation(String input, String printMessage) {
        while (input.isEmpty()) {
            System.out.println(ENTER_Y_OR_N);
            messageBroker.sendMessage(ENTER_Y_OR_N, MessageType.ERROR, MessageCategory.LOG);
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
        }

        char choice = input.charAt(0);
        if (choice == 'y') {
            return true;
        }

        else if (choice == 'n') {
            return false;
        }

        else {
            System.out.println("Invalid input!");
            messageBroker.sendMessage("Invalid input!", MessageType.ERROR, MessageCategory.LOG);
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
            yesNoValidation(input, printMessage);
        }
        return false;
    }

    public static void help() {
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m------------==| DRIFTY CLI HELP |==------------" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m                    " + VERSION_NUMBER + ANSI_RESET);
        System.out.println("\033[31;1mRequired parameter: File URL" + ANSI_RESET + " \033[3m(This must be the first argument you are passing unless you are using Batch Downloading)" + ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default                  Description" + ANSI_RESET);
        System.out.println("-batch      -b            N/A                      The path to the yaml/yml file containing the links and other arguments.");
        System.out.println("-location   -l            Downloads                The location on your computer where content downloaded using Drifty are placed.");
        System.out.println("-name       -n            Source                   Filename of the downloaded file.");
        System.out.println("-help       -h            N/A                      Provides concise information for Drifty CLI.");
        System.out.println("-version    -v            Current Version          Displays version number of Drifty.");
        System.out.println("\033[97;1mSee full documentation at https://github.com/SaptarshiSarkar12/Drifty#readme" + ANSI_RESET);
        System.out.println("\033[97;1mExample:" + ANSI_RESET + " \n> \033[37;1mjava DriftyCLI https://example.com/object.png -n obj.png -l C:/Users/example" + ANSI_RESET);
        System.out.println("\033[37;3m* Requires java 20 or higher. \n" + ANSI_RESET);
        System.out.println("For more information visit: ");
        System.out.println("\tProject Link - https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\tProject Website - " + Drifty.projectWebsite);
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

    public static double[] fraction(double currentX, double currentY, double multiplier) {
        double[] result = new double[2];
        result[0] = currentX * multiplier;
        result[1] = currentY * multiplier;
        return result;
    }

    public static LinkedList<String> getLinkMetadata(String link) {
        try {
            LinkedList<String> list = new LinkedList<>();
            File driftyConfigFolder = Paths.get(Program.get(Program.PATH), "Drifty").toFile();
            if (driftyConfigFolder.exists() && driftyConfigFolder.isDirectory()) {
                FileUtils.forceDelete(driftyConfigFolder); // Deletes the previously generated temporary directory for Drifty
            }
            driftyConfigFolder.mkdir();
            linkThread = new Thread(getYT_IGLinkMetadata(driftyConfigFolder.getAbsolutePath(), link));
            linkThread.start();
            while ((linkThread.getState().equals(Thread.State.RUNNABLE) || linkThread.getState().equals(Thread.State.TIMED_WAITING)) && !linkThread.isInterrupted()) {
                sleep(100);
                interrupted = linkThread.isInterrupted();
            }

            if (interrupted) {
                FileUtils.forceDelete(driftyConfigFolder);
                return null;
            }

            File[] files = driftyConfigFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String ext = FilenameUtils.getExtension(file.getAbsolutePath());
                    if (ext.toLowerCase().contains("json")) {
                        String linkMetadata = FileUtils.readFileToString(file, Charset.defaultCharset());
                        list.addLast(linkMetadata);
                    }

                }

                FileUtils.forceDelete(driftyConfigFolder); // delete the metadata files of Drifty from the config directory
            }

            return list;
        } catch (IOException e) {
            messageBroker.sendMessage("Failed to perform I/O operations on link metadata! " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
            return null;
        }

    }

    public static String getURLFromJson(String jsonString) {
        String json = makePretty(jsonString);
        String regexLink = "(\"webpage_url\": \")(.+)(\")";
        String extractedUrl = "";
        Pattern p = Pattern.compile(regexLink);
        Matcher m = p.matcher(json);
        if (m.find()) {
            extractedUrl = StringEscapeUtils.unescapeJava(m.group(2));
        }
        return extractedUrl;
    }

    public static String makePretty(String json) {
        // The regex strings won't match unless the json string is converted to pretty format
        GsonBuilder g = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(g).setPrettyPrinting().create();
        JsonElement element = JsonParser.parseString(json);
        return gson.toJson(element);
    }

    public static String getFilenameFromJson(String jsonString) {
        String json = makePretty(jsonString);
        String fileName;
        String regexFilename = "(\"title\": \")(.+)(\",)";
        Pattern p = Pattern.compile(regexFilename);
        Matcher m = p.matcher(json);
        if (m.find()) {
            fileName = cleanFilename(m.group(2)) + ".mp4";
            messageBroker.sendMessage(FILENAME_EXTRACTED + "\"" + fileName + "\"", MessageType.INFO, MessageCategory.FILENAME);
        } else {
            fileName = cleanFilename("Unknown Filename") + ".mp4";
            messageBroker.sendMessage(AUTO_FILE_NAME_DETECTION_FAILED_YT_IG, MessageType.ERROR, MessageCategory.FILENAME);
        }
        return fileName;
    }

    public static String cleanFilename(String filename) {
        String fn = StringEscapeUtils.unescapeJava(filename);
        return fn.replaceAll("[^a-zA-Z0-9-._ ]+", "");
    }

    private static Runnable getYT_IGLinkMetadata(String folderPath, String link) {
        return () -> {
            String command = Program.get(Program.COMMAND);
            String[] args = new String[]{"--write-info-json", "--skip-download", "--restrict-filenames", "-P", folderPath, link};
            new ProcBuilder(command)
                .withArgs(args)
                .withErrorStream(System.err)
                .withNoTimeout()
                .run();
        };
    }

    public static boolean isURL(String text) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static double reMap(double sourceNumber, double fromRangeStart, double fromRangeEnd, double toRangeStart, double toRangeEnd, int decimalPrecision) {
        // Both reMap methods will map a number in a range to a different range. So let's say you have a number, such as 25, and it came from a range of
        // values that go from 0 to 500. And you want to find the equivalent number in the range of 5,000 to 100,000, these classes do exactly that.
        // They also follow the strict algebraic way of accomplishing such a remap so they from any range to any other range where the numbers can
        // be positive or negative in any order in any way, it doesn't matter. The mapping will be accurate every time.
        double deltaA = fromRangeEnd - fromRangeStart;
        double deltaB = toRangeEnd - toRangeStart;
        double scale = deltaB / deltaA;
        double negA = -1 * fromRangeStart;
        double offset = (negA * scale) + toRangeStart;
        double finalNumber = (sourceNumber * scale) + offset;
        int calcScale = (int) Math.pow(10, decimalPrecision);
        return (double) Math.round(finalNumber * calcScale) / calcScale;
    }

    public static int reMap(double sourceNumber, double fromRangeStart, double fromRangeEnd, double toRangeStart, double toRangeEnd) {
        return (int) reMap(sourceNumber, fromRangeStart, fromRangeEnd, toRangeStart, toRangeEnd, 0);
    }

    public static void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            messageBroker.sendMessage("The calling method failed to sleep for " + time + " milliseconds. It got interrupted. " + e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
        }
    }
}
