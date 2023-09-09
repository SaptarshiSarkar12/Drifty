package Utils;

import Backend.CopyYtDLP;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.OS;
import Enums.Program;
import Preferences.AppSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static Enums.Program.YT_DLP;

public class Environment {
    private static MessageBroker messageBroker = Environment.getMessageBroker();

    /*
    This method is called by both Drifty_CLI and Main classes.
    It first determines which yt-dlp program to copy out of resources based on the OS.
    Next, it figures out which path to use to store yt-dlp and the users batch list.
    Finally, it updates yt-dlp if it has not been updated in the last 24 hours.
    */
    public static void initializeEnvironment() throws IOException {
        messageBroker.sendMessage("OS : " + OS.getOSName(), MessageType.INFO, MessageCategory.LOG);
        String ytDLP = OS.isWindows() ? "yt-dlp.exe" : OS.isMac() ? "yt-dlp_macos" : "yt-dlp";
        String appUseFolderPath = OS.isWindows() ? Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath().toString() : Paths.get(System.getProperty("user.home"), ".config", "Drifty").toAbsolutePath().toString();
        Program.setExecutableName(ytDLP);
        Program.setDriftyPath(appUseFolderPath);
        InputStream ytDLPStream = ClassLoader.getSystemResourceAsStream(ytDLP);
        CopyYtDLP copyYtDlp = new CopyYtDLP();
        boolean ytDLPExists = copyYtDlp.copyYtDLP(ytDLPStream);
        if (ytDLPExists && isYtDLPUpdated()) {
            updateYt_dlp();
        }
        File folder = new File(appUseFolderPath);
        if (!folder.exists()) {
            try {
                Files.createDirectory(folder.toPath());
                messageBroker.sendMessage("Created Drifty folder : " + appUseFolderPath, MessageType.INFO, MessageCategory.INITIALIZATION);
            } catch (IOException e) {
                messageBroker.sendMessage("Failed to create Drifty folder: " + appUseFolderPath + " - " + e.getMessage(), MessageType.ERROR, MessageCategory.INITIALIZATION);
            }
        } else {
            messageBroker.sendMessage("Drifty folder already exists : " + appUseFolderPath, MessageType.INFO, MessageCategory.INITIALIZATION);
        }
    }

    public static void setMessageBroker(MessageBroker messageBroker) {
        Environment.messageBroker = messageBroker;
    }

    public static void updateYt_dlp() {
        messageBroker.sendMessage("Checking for component (yt-dlp) update ...", MessageType.INFO, MessageCategory.INITIALIZATION);
        String command = Program.get(YT_DLP);
        ProcessBuilder yt_dlpUpdateProcess = new ProcessBuilder(command, "-U");
        yt_dlpUpdateProcess.inheritIO();
        try {
            Process updateYt_dlp = yt_dlpUpdateProcess.start();
            updateYt_dlp.waitFor();
            AppSettings.set.lastDLPUpdateTime(System.currentTimeMillis());
        } catch (IOException e) {
            messageBroker.sendMessage("Failed to update yt-dlp! " + e.getMessage(), MessageType.ERROR, MessageCategory.INITIALIZATION);
        } catch (InterruptedException e) {
            messageBroker.sendMessage("Component (yt-dlp) update process was interrupted! " + e.getMessage(), MessageType.ERROR, MessageCategory.INITIALIZATION);
        }
    }

    public static boolean isYtDLPUpdated() {
        final long oneDay = 1000 * 60 * 60 * 24; // Value of one day (24 Hours) in milliseconds
        long timeSinceLastUpdate = System.currentTimeMillis() - AppSettings.get.lastDLPUpdateTime();
        return timeSinceLastUpdate >= oneDay;
    }

    public static MessageBroker getMessageBroker() {
        return messageBroker;
    }
}
