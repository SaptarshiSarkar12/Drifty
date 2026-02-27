package init;

import settings.AppSettings;
import properties.OS;
import properties.Program;
import updater.UpdateChecker;
import utils.CopyExecutables;
import utils.DbConnection;
import utils.MessageBroker;
import utils.Utility;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static properties.Program.YT_DLP;

public class Environment {
    private static MessageBroker msgBroker;
    private static boolean isAdministrator;
    private static DbConnection dbConnection;
    public static int currentSessionId;

    /*
    This method is called by both CLI.Main and GUI.Forms.Main classes.
    It first determines which yt-dlp program to copy out of resources based on the OS.
    Next, it figures out which path to use to store yt-dlp and the users batch list.
    Finally, it updates yt-dlp if it has not been updated in the last 24 hours.
    */
    public static void initializeEnvironment() {
        msgBroker.msgLogInfo("OS : " + OS.getOSName());
        isAdministrator = hasAdminPrivileges();
        Utility.initializeUtility(); // Lazy initialization of the MessageBroker in Utility class
        new Thread(() -> AppSettings.setDriftyUpdateAvailable(UpdateChecker.isUpdateAvailable())).start();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(Utility.setSpotifyAccessToken(), 0, 3480, java.util.concurrent.TimeUnit.SECONDS); // Thread to refresh Spotify access token every 58 minutes
        String ffmpegExecName = "";
        String osArch = System.getProperty("os.arch");
        if (osArch.contains("arm") || osArch.contains("aarch64")) {
            if (OS.isMac()) {
                ffmpegExecName = "ffmpeg_macos-arm64";
            } else {
                msgBroker.msgInitError("FFMPEG does not support ARM architecture!"); // TODO: Add support for ARM architecture via GitHub Actions
                AppSettings.setFfmpegWorking(false);
            }
        } else {
            ffmpegExecName = OS.isWindows() ? "ffmpeg.exe" : OS.isMac() ? "ffmpeg_macos-x64" : "ffmpeg";
        }
        Program.setFfmpegExecutableName(ffmpegExecName);
        String ytDlpExecName = OS.isWindows() ? "yt-dlp.exe" : OS.isMac() ? "yt-dlp_macos" : "yt-dlp";
        String driftyFolderPath = OS.isWindows() ? Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath().toString() : Paths.get(System.getProperty("user.home"), ".drifty").toAbsolutePath().toString();
        Program.setYtDlpExecutableName(ytDlpExecName);
        Program.setDriftyPath(driftyFolderPath);
        CopyExecutables copyExecutables = new CopyExecutables(new String[]{ytDlpExecName, ffmpegExecName});
        try {
            copyExecutables.start();
            if (!isYtDLPUpdated() && !Utility.isOffline()) {
                checkAndUpdateYtDlp();
            }
        } catch (IOException e) {
            if (AppSettings.isFfmpegWorking()) {
                msgBroker.msgInitError("Failed to copy yt-dlp and ffmpeg executables! " + e.getMessage());
            } else {
                msgBroker.msgInitError("Failed to copy yt-dlp executable! " + e.getMessage());
            }
        }
        try {
            dbConnection = DbConnection.getInstance();
            dbConnection.createTables();

            String startDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            currentSessionId = dbConnection.addSessionRecord(startDate);
        } catch (SQLException e) {
            msgBroker.msgInitError("Failed to setup the database: " + e.getMessage());
            Environment.terminate(1);
        }
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

    public static void terminate(int exitCode) {
        String endDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        try {
            dbConnection.updateSessionEndDate(currentSessionId, endDate);
        } catch (SQLException e) {
            msgBroker.msgLogError("Failed to update session end date: " + e.getMessage());
        }

        AppSettings.setSpotifyAccessToken("");
        System.exit(exitCode);
    }

    public static void setMessageBroker(MessageBroker messageBroker) {
        Environment.msgBroker = messageBroker;
    }

    public static void checkAndUpdateYtDlp() {
        AppSettings.setYtDlpUpdating(true);
        msgBroker.msgInitInfo("Checking for component (yt-dlp) update ...");
        String command = Program.get(YT_DLP);
        ProcessBuilder ytDlpUpdateProcess = new ProcessBuilder(command, "-U");
        ytDlpUpdateProcess.inheritIO();
        try {
            Process ytDlpUpdateTask = ytDlpUpdateProcess.start();
            ytDlpUpdateTask.waitFor();
            AppSettings.setLastYtDlpUpdateTime(System.currentTimeMillis());
        } catch (IOException e) {
            msgBroker.msgInitError("Failed to update yt-dlp! " + e.getMessage());
        } catch (InterruptedException e) {
            msgBroker.msgInitError("Component (yt-dlp) update process was interrupted! " + e.getMessage());
        } finally {
            AppSettings.setYtDlpUpdating(false);
            Utility.setYtDlpVersion().run();
        }
    }

    public static boolean isYtDLPUpdated() {
        final long oneDay = 1000 * 60 * 60 * 24; // Value of one day (24 Hours) in milliseconds
        long timeSinceLastUpdate = System.currentTimeMillis() - AppSettings.getLastYtDlpUpdateTime();
        return timeSinceLastUpdate <= oneDay;
    }

    public static boolean hasAdminPrivileges() {
        try {
            Path currentExecutableFolderPath = Paths.get(Utility.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            Path adminTestFilePath = currentExecutableFolderPath.resolve("adminTestFile.txt");
            Files.createFile(adminTestFilePath);
            Files.deleteIfExists(adminTestFilePath);
            return true;
        } catch (URISyntaxException e) {
            System.out.println("Failed to get the current executable path! " + e.getMessage());
            return false;
        } catch (AccessDeniedException e) {
            System.out.println("You are not running Drifty as an administrator! " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("Failed to create a file in the current executable folder! " + e.getMessage());
            return false;
        }
    }

    public static boolean isAdministrator() {
        return isAdministrator;
    }

    public static MessageBroker getMessageBroker() {
        return msgBroker;
    }
}
