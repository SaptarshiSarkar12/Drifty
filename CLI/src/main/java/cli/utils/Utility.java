package cli.utils;

import cli.init.Environment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.util.*;

import static cli.support.Constants.*;

public class Utility extends utils.Utility {
    private static final Scanner SC = ScannerFactory.getInstance();

    public static String findFilenameInLink(String link) {
        String filename = "";
        if (isInstagram(link) || isYoutube(link)) {
            LinkedList<String> linkMetadataList = Utility.getYtDlpMetadata(link);
            for (String json : Objects.requireNonNull(linkMetadataList)) {
                filename = Utility.getFilenameFromJson(json);
            }
            if (filename.isEmpty()) {
                msgBroker.msgFilenameError("Filename detection failed!");
                return null;
            }
        } else {
            // Example: "example.com/file.txt" prints "Filename detected: file.txt"
            // example.com/file.json -> file.json
            String file = link.substring(link.lastIndexOf("/") + 1);
            if (file.isEmpty()) {
                msgBroker.msgFilenameError(FILENAME_DETECTION_ERROR);
                return null;
            }
            int index = file.lastIndexOf(".");
            if (index < 0) {
                msgBroker.msgFilenameError(FILENAME_DETECTION_ERROR);
                return null;
            }
            String extension = file.substring(index);
            // edge case 1: "example.com/."
            if (extension.length() == 1) {
                msgBroker.msgFilenameError(FILENAME_DETECTION_ERROR);
                return null;
            }
            // file.png?width=200 -> file.png
            filename = file.split("([?])")[0];
            msgBroker.msgFilenameInfo(FILENAME_DETECTED + "\"" + filename + "\"");
        }
        return filename;
    }

    public boolean yesNoValidation(String input, String printMessage) {
        while (input.isEmpty()) {
            Environment.getMessageBroker().msgInputError(ENTER_Y_OR_N, true);
            msgBroker.msgLogError(ENTER_Y_OR_N);
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
            msgBroker.msgLogError("Invalid input!");
            Environment.getMessageBroker().msgInputInfo(printMessage, false);
            input = SC.nextLine().toLowerCase();
            return yesNoValidation(input, printMessage);
        }
    }

    public static String getSpotifyDownloadLink(String spotifyMetadataJson) {
        JsonObject jsonObject = JsonParser.parseString(spotifyMetadataJson).getAsJsonObject();
        String songName = jsonObject.get("songName").getAsString();
        int duration = jsonObject.get("duration").getAsInt();
        JsonArray artists = jsonObject.get("artists").getAsJsonArray();
        ArrayList<String> artistNames = new ArrayList<>(artists.size());
        for (int i = 0; i < artists.size(); i++) {
            artistNames.add(artists.get(i).getAsString());
        }
        String query = (String.join(", ", artistNames) + " - " + songName).toLowerCase();
        ArrayList<HashMap<String, Object>> searchResults = getYoutubeSearchResults(query, true);
        boolean searchedWithFilters = true;
        if (searchResults == null) {
            msgBroker.msgLogError("Failed to get search results for the song with filters! Trying without filters ...");
            searchResults = getYoutubeSearchResults(query, false);
            searchedWithFilters = false;
            if (searchResults == null) {
                msgBroker.msgDownloadError("Song is exclusive to Spotify and cannot be downloaded!");
                return null;
            }
        }
        String matchedId = getMatchingVideoID(Objects.requireNonNull(searchResults), duration, artistNames);
        if (matchedId.isEmpty()) {
            if (searchedWithFilters) {
                msgBroker.msgLogError("Failed to get a matching video ID for the song with filters! Trying without filters ...");
                searchResults = getYoutubeSearchResults(query, false);
                matchedId = getMatchingVideoID(Objects.requireNonNull(searchResults), duration, artistNames);
                if (matchedId.isEmpty()) {
                    msgBroker.msgDownloadError("Song is exclusive to Spotify and cannot be downloaded!");
                    return null;
                }
            } else {
                msgBroker.msgDownloadError("Song is exclusive to Spotify and cannot be downloaded!");
                return null;
            }
        }
        return "https://www.youtube.com/watch?v=" + matchedId;
    }

    public static Yaml getYamlParser() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        loaderOptions.setAllowRecursiveKeys(false);
        loaderOptions.setProcessComments(false);
        Yaml yamlParser = new Yaml(new SafeConstructor(loaderOptions));
        msgBroker.msgLogInfo("YAML parser initialized successfully");
        return yamlParser;
    }
}
