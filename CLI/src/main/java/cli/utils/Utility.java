package cli.utils;

import cli.init.Environment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

import static cli.support.Constants.FILENAME_DETECTED;
import static cli.support.Constants.FILENAME_DETECTION_ERROR;
import static cli.support.Constants.ENTER_Y_OR_N;

public class Utility extends utils.Utility {
    private static final Scanner SC = ScannerFactory.getInstance();

    public static String findFilenameInLink(String link) {
        String filename = "";
        if (isInstagram(link) || isYoutube(link)) {
            LinkedList<String> linkMetadataList = Utility.getYtDlpMetadata(link);
            for (String json : Objects.requireNonNull(linkMetadataList)) {
                filename = Utility.getFilenameFromJson(json);
            }
        } else {
            // Example: "example.com/file.txt" prints "Filename detected: file.txt"
            // example.com/file.json -> file.json
            String file = link.substring(link.lastIndexOf("/") + 1);
            if (file.isEmpty()) {
                M.msgFilenameError(FILENAME_DETECTION_ERROR);
                return null;
            }
            int index = file.lastIndexOf(".");
            if (index < 0) {
                M.msgFilenameError(FILENAME_DETECTION_ERROR);
                return null;
            }
            String extension = file.substring(index);
            // edge case 1: "example.com/."
            if (extension.length() == 1) {
                M.msgFilenameError(FILENAME_DETECTION_ERROR);
                return null;
            }
            // file.png?width=200 -> file.png
            filename = file.split("([?])")[0];
            M.msgFilenameInfo(FILENAME_DETECTED + "\"" + filename + "\"");
        }
        return filename;
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
            M.msgLogError("Failed to get search results for the song with filters! Trying without filters ...");
            searchResults = getYoutubeSearchResults(query, false);
            searchedWithFilters = false;
            if (searchResults == null) {
                M.msgDownloadError("Song is exclusive to Spotify and cannot be downloaded!");
                return null;
            }
        }
        String matchedId = getMatchingVideoID(Objects.requireNonNull(searchResults), duration, artistNames);
        if (matchedId.isEmpty()) {
            if (searchedWithFilters) {
                M.msgLogError("Failed to get a matching video ID for the song with filters! Trying without filters ...");
                searchResults = getYoutubeSearchResults(query, false);
                matchedId = getMatchingVideoID(Objects.requireNonNull(searchResults), duration, artistNames);
                if (matchedId.isEmpty()) {
                    M.msgDownloadError("Song is exclusive to Spotify and cannot be downloaded!");
                    return null;
                }
            } else {
                M.msgDownloadError("Song is exclusive to Spotify and cannot be downloaded!");
                return null;
            }
        }
        return "https://www.youtube.com/watch?v=" + matchedId;
    }
}
