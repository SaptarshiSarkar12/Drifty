package Utils;

import Backend.CopyYtDLP;
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
    private static MessageBroker M = Environment.getMessageBroker();

    /*
    This method is called by both CLI.Main and GUI.Forms.Main classes.
    It first determines which yt-dlp program to copy out of resources based on the OS.
    Next, it figures out which path to use to store yt-dlp and the users batch list.
    Finally, it updates yt-dlp if it has not been updated in the last 24 hours.
    */
    public static void initializeEnvironment() {
        M.msgLogInfo("OS : " + OS.getOSName());
        String ytDLP = OS.isWindows() ? "yt-dlp.exe" : OS.isMac() ? "yt-dlp_macos" : "yt-dlp";
        String appUseFolderPath = OS.isWindows() ? Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath().toString() : Paths.get(System.getProperty("user.home"), ".config", "Drifty").toAbsolutePath().toString();
        Program.setExecutableName(ytDLP);
        Program.setDriftyPath(appUseFolderPath);
        InputStream ytDLPStream = ClassLoader.getSystemResourceAsStream(ytDLP);
        CopyYtDLP copyYtDlp = new CopyYtDLP();
        boolean ytDLPExists = false;
        try {
            ytDLPExists = copyYtDlp.copyYtDLP(ytDLPStream);
        } catch (IOException e) {
            M.msgInitError("Failed to copy yt-dlp! " + e.getMessage());
        }
        if (ytDLPExists && !isYtDLPUpdated()) {
            updateYt_dlp();
        }
        File folder = new File(appUseFolderPath);
        if (!folder.exists()) {
            try {
                Files.createDirectory(folder.toPath());
                M.msgInitInfo("Created Drifty folder : " + appUseFolderPath);
            } catch (IOException e) {
                M.msgInitError("Failed to create Drifty folder: " + appUseFolderPath + " - " + e.getMessage());
            }
        } else {
            M.msgInitInfo("Drifty folder already exists : " + appUseFolderPath);
        }
    }

    public static void setMessageBroker(MessageBroker messageBroker) {
        Environment.M = messageBroker;
    }

    public static void updateYt_dlp() {
        M.msgInitInfo("Checking for component (yt-dlp) update ...");
        String command = Program.get(YT_DLP);
        ProcessBuilder yt_dlpUpdateProcess = new ProcessBuilder(command, "-U");
        yt_dlpUpdateProcess.inheritIO();
        try {
            Process updateYt_dlp = yt_dlpUpdateProcess.start();
            updateYt_dlp.waitFor();
            AppSettings.set.lastDLPUpdateTime(System.currentTimeMillis());
        } catch (IOException e) {
            M.msgInitError("Failed to update yt-dlp! " + e.getMessage());
        } catch (InterruptedException e) {
            M.msgInitError("Component (yt-dlp) update process was interrupted! " + e.getMessage());
        }
    }

    public static boolean isYtDLPUpdated() {
        final long oneDay = 1000 * 60 * 60 * 24; // Value of one day (24 Hours) in milliseconds
        long timeSinceLastUpdate = System.currentTimeMillis() - AppSettings.get.lastDLPUpdateTime();
        return timeSinceLastUpdate <= oneDay;
    }

    public static MessageBroker getMessageBroker() {
        return M;
    }
}
