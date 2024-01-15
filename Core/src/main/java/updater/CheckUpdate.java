package updater;

import init.Environment;
import properties.MessageType;
import support.Constants;
import utils.Logger;
import utils.MessageBroker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class CheckUpdate {
    private static final Logger logger = Logger.getInstance();
    private static final String CURRENT_VERSION = Constants.VERSION_NUMBER.replace("v", "");
    private static final MessageBroker M = Environment.getMessageBroker();

    private static String getLatestVersion() {
        try {
            URL latestVersionAPI = new URI("https://saptarshisarkar12.github.io/Drifty/api/version/latest").toURL();
            latestVersionAPI.openConnection();
            try (Scanner scanner = new Scanner(latestVersionAPI.openStream())) {
                return scanner.nextLine();
            }
        } catch (URISyntaxException e) {
            M.msgUpdateError("API URL is invalid! " + e.getMessage());
        } catch (IOException e) {
            M.msgUpdateError("Failed to connect to API! " + e.getMessage());
        }
        return "";
    }

    public static boolean isUpdateAvailable() {
        String latestVersion = getLatestVersion();
        logger.log(MessageType.INFO, "Latest version : " + latestVersion);
        logger.log(MessageType.INFO, "Current version : " + CURRENT_VERSION);
        if (latestVersion.isEmpty()) {
            return false;
        }
        String[] currentVersionPartsString = CURRENT_VERSION.split("\\.");
        String[] latestVersionPartsString = latestVersion.split("\\.");
        for (int i = 0; i < 3; i++) {
            if (Integer.parseInt(currentVersionPartsString[i]) < Integer.parseInt(latestVersionPartsString[i])) {
                return true;
            }
        }
        return false;
    }
}
