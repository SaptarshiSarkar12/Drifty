package updater;

import init.Environment;
import preferences.AppSettings;
import properties.MessageType;
import support.Constants;
import utils.Logger;
import utils.MessageBroker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CheckUpdate {
    private static final Logger LOGGER = Logger.getInstance();
    private static final String CURRENT_VERSION = Constants.VERSION_NUMBER.replace("v", "");
    private static final MessageBroker M = Environment.getMessageBroker();

    private static String getLatestStableVersion() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases/latest"))
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            return getReleaseTag(request);
        } catch (URISyntaxException e) {
            M.msgUpdateError("Failed to fetch stable release data from GitHub api! " + e.getMessage());
            return "";
        }
    }

    private static String getReleaseTag(HttpRequest request) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            String responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            String tag = responseBody.split("\"tag_name\":\"")[1].split("\"")[0];
            if (AppSettings.GET.updateChannel().equals("stable")) {
                AppSettings.SET.newDriftyVersionName(responseBody.split("\"name\":\"")[1].split("\"")[0]);
                return tag.replace("v", "");
            } else {
                if (tag.contains(AppSettings.GET.updateChannel())) {
                    AppSettings.SET.newDriftyVersionName(responseBody.split("\"name\":\"")[1].split("\"")[0]);
                    return tag.replace("v", "");
                } else {
                    AppSettings.SET.newDriftyVersionName("");
                    return Constants.VERSION_NUMBER.replace("v", "");
                }
            }
        } catch (IOException e) {
            M.msgUpdateError("Failed to connect to GitHub server! " + e.getMessage());
            return "";
        } catch (InterruptedException e) {
            M.msgUpdateError("User interrupted the update check! " + e.getMessage());
            return "";
        }
    }

    private static String getLatestPreReleaseVersion() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases"))
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            return getReleaseTag(request);
        } catch (URISyntaxException e) {
            M.msgUpdateError("Failed to fetch pre-release data from GitHub api! " + e.getMessage());
            return "";
        }
    }

    public static boolean isUpdateAvailable() {
        String latestVersion;
        if (AppSettings.GET.updateChannel().equals("stable")) {
            latestVersion = getLatestStableVersion();
        } else {
            latestVersion = getLatestPreReleaseVersion();
        }
        LOGGER.log(MessageType.INFO, "Latest version : " + latestVersion);
        LOGGER.log(MessageType.INFO, "Current version : " + CURRENT_VERSION);
        if (latestVersion.isEmpty()) {
            return false;
        }
        String[] currentVersionPartsString = CURRENT_VERSION.split("\\.");
        String[] latestVersionPartsString = latestVersion.split("\\.");
        for (int i = 0; i < 3; i++) {
            try {
                if (Integer.parseInt(currentVersionPartsString[i]) < Integer.parseInt(latestVersionPartsString[i])) {
                    return true;
                }
            } catch (NumberFormatException e) {
                M.msgUpdateError("Failed to parse version number! " + e.getMessage());
            }
        }
        return false;
    }
}
