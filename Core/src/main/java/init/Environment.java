package init;

import preferences.AppSettings;
import properties.OS;
import properties.Program;
import utils.CopyExecutables;
import utils.MessageBroker;
import utils.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static properties.Program.YT_DLP;

public class Environment {
    private static MessageBroker msgBroker = Environment.getMessageBroker();

    /*
    This method is called by both CLI.Main and GUI.Forms.Main classes.
    It first determines which yt-dlp program to copy out of resources based on the OS.
    Next, it figures out which path to use to store yt-dlp and the users batch list.
    Finally, it updates yt-dlp if it has not been updated in the last 24 hours.
    */
    public static void initializeEnvironment() {
        msgBroker.msgLogInfo("OS : " + OS.getOSName());
        String ytDlpExecName = OS.isWindows() ? "yt-dlp.exe" : OS.isMac() ? "yt-dlp_macos" : "yt-dlp";
        String spotDLExecName = OS.isWindows() ? "spotdl_win.exe" : OS.isMac() ? "spotdl_macos" : "spotdl_linux";
        String driftyFolderPath = OS.isWindows() ? Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath().toString() : Paths.get(System.getProperty("user.home"), ".drifty").toAbsolutePath().toString();
        Program.setYtDlpExecutableName(ytDlpExecName);
        Program.setSpotdlExecutableName(spotDLExecName);
        Program.setDriftyPath(driftyFolderPath);
        CopyExecutables copyExecutables = new CopyExecutables();
        boolean ytDLPExists = false;
        try {
            copyExecutables.copyExecutables(new String[]{ytDlpExecName, spotDLExecName});
            ytDLPExists = true;
        } catch (IOException e) {
            msgBroker.msgInitError("Failed to copy yt-dlp! " + e.getMessage());
            msgBroker.msgInitError("Failed to copy spotDL! " + e.getMessage());
        }
        if (ytDLPExists && !isYtDLPUpdated()) {
            checkAndUpdateYtDlp();
        }
        new Thread(Utility.setYtDlpVersion()).start();
        new Thread(Utility.setSpotDLVersion()).start();
        File folder = new File(driftyFolderPath);
        if (!folder.exists()) {
            try {
                Files.createDirectory(folder.toPath());
                msgBroker.msgInitInfo("Created Drifty folder : " + driftyFolderPath);
            } catch (IOException e) {
                msgBroker.msgInitError("Failed to create Drifty folder: " + driftyFolderPath + " - " + e.getMessage());
            }
        } else {
            msgBroker.msgInitInfo("Drifty folder already exists : " + driftyFolderPath);
        }
    }

    public static void setMessageBroker(MessageBroker messageBroker) {
        Environment.msgBroker = messageBroker;
    }

    public static void checkAndUpdateYtDlp() {
        AppSettings.SET.ytDlpUpdating(true);
        msgBroker.msgInitInfo("Checking for component (yt-dlp) update ...");
        String command = Program.get(YT_DLP);
        ProcessBuilder ytDlpUpdateProcess = new ProcessBuilder(command, "-U");
        ytDlpUpdateProcess.inheritIO();
        try {
            Process ytDlpUpdateTask = ytDlpUpdateProcess.start();
            ytDlpUpdateTask.waitFor();
            AppSettings.SET.lastYtDlpUpdateTime(System.currentTimeMillis());
        } catch (IOException e) {
            msgBroker.msgInitError("Failed to update yt-dlp! " + e.getMessage());
        } catch (InterruptedException e) {
            msgBroker.msgInitError("Component (yt-dlp) update process was interrupted! " + e.getMessage());
        } finally {
            AppSettings.SET.ytDlpUpdating(false);
        }
    }

    public static boolean isYtDLPUpdated() {
        final long oneDay = 1000 * 60 * 60 * 24; // Value of one day (24 Hours) in milliseconds
        long timeSinceLastUpdate = System.currentTimeMillis() - AppSettings.GET.lastYtDlpUpdateTime();
        return timeSinceLastUpdate <= oneDay;
    }

    public static MessageBroker getMessageBroker() {
        return msgBroker;
    }
}
