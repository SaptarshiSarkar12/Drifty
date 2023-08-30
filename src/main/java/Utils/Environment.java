package Utils;
import Backend.CopyYtDlp;
import Enums.MessageType;
import Enums.Program;
import Enums.MessageCategory;
import Enums.OS;
import Preferences.AppSettings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Environment {
    private static MessageBroker messageBroker;

    /*
    This method is called by both Drifty_CLI and Main classes.
    It first determines which yt-dlp program to copy out of resources based on the OS.
    Next, it figures out which path to use to store yt-dlp and the users batch list.
    Finally, it updates yt-dlp if it has not been updated in the last 24 hours.
    */
    public static void initializeEnvironment() {
        messageBroker = Environment.getMessageBroker();
        messageBroker.sendMessage("OS : " + OS.getOSName(), MessageType.INFO, MessageCategory.LOG);
        String yt_dlpProgramName;
        if (OS.isWindows()) {
            yt_dlpProgramName = "yt-dlp.exe";
        } else if (OS.isMac()) {
            yt_dlpProgramName = "yt-dlp_macos";
        } else {
            yt_dlpProgramName = "yt-dlp";
        }
        String configFolderPath;
        if (OS.isWindows()) {
            configFolderPath = Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath().toString();
        } else {
            configFolderPath = Paths.get(System.getProperty("user.home"),".config", "Drifty").toAbsolutePath().toString();
        }
        Program.setName(yt_dlpProgramName);
        Program.setPath(configFolderPath);
        InputStream yt_dlpProgramStream = ClassLoader.getSystemResourceAsStream(yt_dlpProgramName);
        CopyYtDlp copyYtDlp = new CopyYtDlp();
        try {
            boolean isCopySuccessful = true;
            if (!Files.exists(Paths.get(configFolderPath, yt_dlpProgramName))) {
                isCopySuccessful = copyYtDlp.copyToTemp(yt_dlpProgramStream);
            }
            if (isCopySuccessful && !isUpdateForYt_dlpChecked()) {
                updateYt_dlp();
            }
        } catch (IOException e) {
            messageBroker.sendMessage("Failed  to set the time of last yt-dlp update as preference! " + e.getMessage(), MessageType.ERROR, MessageCategory.INITIALIZATION);
        }
        File folder = new File(configFolderPath);
        if (!folder.exists()) {
            try {
                Files.createDirectory(folder.toPath());
            } catch (IOException e) {
                messageBroker.sendMessage("Failed to create Drifty configuration directory ! " + e.getMessage(), MessageType.ERROR, MessageCategory.INITIALIZATION);
            }
        }
        Program.setDataPath(configFolderPath);
    }

    public static void setMessageBroker(MessageBroker messageBroker) {
        Environment.messageBroker = messageBroker;
    }

    public static void updateYt_dlp() {
        messageBroker.sendMessage("Checking for component (yt-dlp) update ...", MessageType.INFO, MessageCategory.INITIALIZATION);
        String command = Program.get(Program.COMMAND);
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

    public static boolean isUpdateForYt_dlpChecked() {
        final long oneDayInMilliSeconds = 1000 * 60 * 60 * 24; // Value of one day (24 Hours) in milliseconds
        return (System.currentTimeMillis() - AppSettings.get.lastDLPUpdateTime()) < oneDayInMilliSeconds;
    }

    public static MessageBroker getMessageBroker() {
        return messageBroker;
    }
}
