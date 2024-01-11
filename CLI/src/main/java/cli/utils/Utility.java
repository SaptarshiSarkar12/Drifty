package cli.utils;

import cli.init.Environment;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

import static cli.support.Constants.FILENAME_DETECTED;
import static cli.support.Constants.FILENAME_DETECTION_ERROR;
import static cli.support.Constants.ENTER_Y_OR_N;

public class Utility extends utils.Utility {
    private static final Scanner SC = ScannerFactory.getInstance();

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
}
