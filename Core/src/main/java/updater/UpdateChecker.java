package updater;

import init.Environment;
import settings.AppSettings;
import properties.MessageCategory;
import support.Constants;
import utils.MessageBroker;
import utils.Utility;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateChecker {
    private static final String CURRENT_VERSION = Constants.VERSION_NUMBER.replace("v", "");
    private static final MessageBroker M = Environment.getMessageBroker();

    public static boolean isUpdateAvailable() {
        if (Utility.isOffline()) {
            M.msgLogError("Failed to check for updates! You are not connected to the internet.");
            return false;
        }
        String latestVersion;
        if (AppSettings.isEarlyAccessEnabled()) {
            latestVersion = getLatestPreReleaseVersion();
        }else {
            latestVersion = getLatestStableVersion();
        }
        M.msgLogInfo("Latest version : " + latestVersion);
        M.msgLogInfo("Current version : " + CURRENT_VERSION);
        if (latestVersion.isEmpty()) {
            return false;
        }
        AppSettings.setLastDriftyUpdateTime(System.currentTimeMillis());
        return compareVersions(CURRENT_VERSION, latestVersion);
    }

    private static String getLatestStableVersion() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases/latest"))
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            return getReleaseTag(request);
        }catch (URISyntaxException e) {
            M.msgUpdateError("Failed to fetch stable release data from GitHub api! " + e.getMessage());
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
        }catch (URISyntaxException e) {
            M.msgUpdateError("Failed to fetch pre-release data from GitHub api! " + e.getMessage());
            return "";
        }
    }

    private static String getReleaseTag(HttpRequest request) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            String responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            String tag = responseBody.split("\"tag_name\":\"")[1].split("\"")[0];
            AppSettings.setNewDriftyVersionName(responseBody.split("\"name\":\"")[1].split("\"")[0]);
            AppSettings.setLatestDriftyVersionTag(tag);
            return tag.replace("v", "");
        }catch (IOException e) {
            if (e.getMessage() == null) {
                M.msgLogError("Failed to check for updates! You may be offline.");
            }else {
                if (e.getMessage().contains("403")) {
                    M.msgUpdateError("Failed to check for updates! GitHub API rate limit exceeded. " + e.getMessage());
                }else {
                    M.msgUpdateError("Failed to check for updates! " + e.getMessage());
                }
            }
            return "";
        }catch (InterruptedException e) {
            M.msgUpdateError("User interrupted the update check! " + e.getMessage());
            return "";
        }
    }

    public static boolean compareVersions(String currentVersion, String latestVersion) {
        if (currentVersion.equals(latestVersion)) {
            return false;
        }
        String[] currentVersionParts = getTagParts(currentVersion);
        String[] latestVersionParts = getTagParts(latestVersion);
        // Check major, minor, and patch versions
        for (int i = 0; i < 3; i++) {
            int cvPart = Utility.parseStringToInt(currentVersionParts[i], "Failed to parse current version part " + i, MessageCategory.UPDATE);
            int lvPart = Utility.parseStringToInt(latestVersionParts[i], "Failed to parse latest version part " + i, MessageCategory.UPDATE);

            if (cvPart < lvPart) {
                return true;
            }else if (cvPart > lvPart) {
                return false;
            }
        }

        if (currentVersionParts.length == 3 && latestVersionParts.length == 5) {
            // Example: currentVersion = v2.1.0 and latestVersion = v2.1.0-beta.1
            // currentVersion is a stable release version, and the latestVersion is a pre-release version
            // So, the currentVersion is greater than latestVersion as currentVersion is a stable version
            return false;
        }else if (currentVersionParts.length == 5 && latestVersionParts.length == 3) {
            // Example: currentVersion = v2.1.0-beta.1 and latestVersion = v2.1.0
            // currentVersion is a pre-release version, and the latestVersion is a release version
            // So, the currentVersion is less than latestVersion as latestVersion is a stable version
            return true;
        }else if (currentVersionParts.length == 5 && latestVersionParts.length == 5) {
            if (AppSettings.isEarlyAccessEnabled()) { // If the user has enabled early access, then pre-release versions are considered
                // Both versions are pre-release versions
                int comparePreReleaseType = currentVersionParts[3].compareTo(latestVersionParts[3]); // alpha < beta < rc (compared lexicographically)
                // Example: currentVersion = v2.1.0-beta.1 and latestVersion = v2.1.0-alpha.1
                // beta > alpha, so the currentVersion is greater than the latestVersion. Return false
                // Example: currentVersion = v2.1.0-beta.1, and latestVersion = v2.1.0-rc.1
                // beta < rc, so the currentVersion is less than the latestVersion. Return true
                if (comparePreReleaseType < 0) {
                    return true;
                }else if (comparePreReleaseType > 0) {
                    return false;
                }else {
                    // Both versions have the same pre-release type (alpha, beta, rc)
                    // Higher Revision number means a newer version
                    int cvRevisionNumber = Utility.parseStringToInt(currentVersionParts[4], "Failed to parse current version's revision number", MessageCategory.UPDATE);
                    int lvRevisionNumber = Utility.parseStringToInt(latestVersionParts[4], "Failed to parse latest version's revision number", MessageCategory.UPDATE);
                    return cvRevisionNumber < lvRevisionNumber;
                }
            }
        }
        return false; // Both versions are equal
    }

    private static String[] getTagParts(String tag) {
        if (tag.contains("-")) {
            String[] versionAndReleaseCode = tag.replace("v", "").split("-"); // Example: v2.1.0-beta.1 => [2.1.0, beta.1]
            String[] releaseTypeAndNumber = versionAndReleaseCode[1].split("\\."); // Example: beta.1 => [beta, 1]
            String[] versionParts = versionAndReleaseCode[0].split("\\."); // Example: 2.1.0 => [2, 1, 0]
            return new String[]{versionParts[0], versionParts[1], versionParts[2], releaseTypeAndNumber[0], releaseTypeAndNumber[1]};
        }else {
            return tag.replace("v", "").split("\\."); // Example: v2.1.0 => [2, 1, 0]
        }
    }
}
