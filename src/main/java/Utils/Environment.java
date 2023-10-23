package Utils;

import Backend.CopyExecutables;
import Enums.OS;
import Enums.Program;
import Preferences.AppSettings;

import java.io.File;
import java.io.IOException;
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
        String ytDlpExecName = OS.isWindows() ? "yt-dlp.exe" : OS.isMac() ? "yt-dlp_macos" : "yt-dlp";
        String spotDLExecName = OS.isWindows() ? "spotdl_win.exe" : OS.isMac() ? "spotdl_macos" : "spotdl_linux";
        String driftyFolderPath = OS.isWindows() ? Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath().toString() : Paths.get(System.getProperty("user.home"), ".drifty").toAbsolutePath().toString();
        Program.setYt_DlpExecutableName(ytDlpExecName);
        Program.setSpotdlExecutableName(spotDLExecName);
        Program.setDriftyPath(driftyFolderPath);
        CopyExecutables copyExecutables = new CopyExecutables();
        boolean ytDLPExists = false;
        try {
            copyExecutables.copyExecutables(new String[]{ytDlpExecName, spotDLExecName});
        } catch (IOException e) {
            M.msgInitError("Failed to copy yt-dlp! " + e.getMessage());
            M.msgInitError("Failed to copy spotDL! " + e.getMessage());
        }
        if (ytDLPExists && !isYtDLPUpdated()) {
            updateYt_dlp();
        }
        File folder = new File(driftyFolderPath);
        if (!folder.exists()) {
            try {
                Files.createDirectory(folder.toPath());
                M.msgInitInfo("Created Drifty folder : " + driftyFolderPath);
            } catch (IOException e) {
                M.msgInitError("Failed to create Drifty folder: " + driftyFolderPath + " - " + e.getMessage());
            }
        } else {
            M.msgInitInfo("Drifty folder already exists : " + driftyFolderPath);
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
