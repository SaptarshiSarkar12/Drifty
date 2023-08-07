package Utils;

import Backend.CopyYtDlp;
import Backend.Drifty;
import Enums.DriftyConfig;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.OS;
import GUI.Launcher;
import Preferences.AppSettings;
import org.buildobjects.process.ProcBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used by the {@link Launcher} and the {@link Backend.FileDownloader} classes.
 * {@link Launcher} calls the {@link #initializeEnvironment()} method to initialize the environment which
 * includes figuring out which operating system it is running on, and
 * the name of the yt-dlp program to use based on the operating system.
 * It then copies the yt-dlp program from its resources to the system's default temporary folder.
 * <p>
 * This class also has methods to tell whether the yt-dlp program has been updated within the last
 * 24 hours, and if it hasn't been updated, the {@link #updateYt_dlp()} will update it.
 * <p>
 * If yt-dlp does not exist in the temporary folder when Drifty starts,
 * it will be copied and yt-dlp updates will be checked.
 * The timestamp for that update will be stored, and within 24 hours from that time,
 * no more yt-dlp updates will be checked.
 */
public class Environment {
    /**
     * The message broker instance used to send messages to the required output stream automatically
     * and to store the message in a log file.
     */
    private static final MessageBroker messageBroker = Drifty.getMessageBrokerInstance();

    /**
     * This method sets the environment (initializing paths,
     * creating configuration directory, yt-dlp program name, etc.) for Drifty
     */
    public static void initializeEnvironment() {
        messageBroker.sendMessage("User OS is : " + OS.getOSName(), MessageType.INFORMATION, MessageCategory.LOG);
        String yt_dlpProgramName;
        if (OS.isWindows()) {
            yt_dlpProgramName = "yt-dlp.exe";
        }
        else if (OS.isMac()) {
            yt_dlpProgramName = "yt-dlp_macos";
        }
        else {
            yt_dlpProgramName = "yt-dlp";
        }
        DriftyConfig.setYt_dlpProgramName(yt_dlpProgramName);
        DriftyConfig.setYt_dlpProgramPath(Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().toString());
        messageBroker.sendMessage("yt-dlp program name is : " + yt_dlpProgramName, MessageType.INFORMATION, MessageCategory.LOG);
        CopyYtDlp copyYtDlp = new CopyYtDlp();
        try {
            if(!copyYtDlp.copyToTemp()) {
                AppSettings.set.lastDLPUpdateTime(System.currentTimeMillis());
            }
        } catch (IOException e) {
            messageBroker.sendMessage("Failed  to set the time of last yt-dlp update as preference! " + e.getMessage(), MessageType.ERROR, MessageCategory.LOG);
        }
        String batchPath;
        if (OS.isWindows()) {
            batchPath = Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath().toString();
        }
        else {
            batchPath = Paths.get(System.getProperty("user.home"),".config", "Drifty").toAbsolutePath().toString();
        }
        File folder = new File(batchPath);
        if (!folder.exists()) {
            try {
                Files.createDirectory(folder.toPath());
            } catch (IOException e) {
                messageBroker.sendMessage("Failed to create Drifty configuration directory ! " + e.getMessage(), MessageType.ERROR, MessageCategory.DIRECTORY);
            }
        }
        DriftyConfig.setBatchPath(batchPath);
    }

    /**
     * This method is used to update yt-dlp program
     */
    public static void updateYt_dlp() {
        messageBroker.sendMessage("Checking for component (yt-dlp) update ...", MessageType.INFORMATION, MessageCategory.DOWNLOAD);
        String command = DriftyConfig.getConfig(DriftyConfig.YT_DLP_COMMAND);
        ProcBuilder yt_dlpUpdateProcess = new ProcBuilder(command)
                .withArg("-U")
                .withOutputStream(System.out)
                .withErrorStream(System.err);
        yt_dlpUpdateProcess.run();
        AppSettings.set.lastDLPUpdateTime(System.currentTimeMillis());
    }

    /**
     * This method is used to check if the update for yt-dlp has been checked or not in the last 24 hours
     * @return True if any update for yt-dlp has been checked in the last 24 hours, else false is returned
     */
    public static boolean isUpdateForYt_dlpChecked() {
        final long oneDayInMilliSeconds = 1000 * 60 * 60 * 24; // Value of one day (24 Hours) in milliseconds
        return (System.currentTimeMillis() - AppSettings.get.lastDLPUpdateTime()) < oneDayInMilliSeconds;
    }
}
