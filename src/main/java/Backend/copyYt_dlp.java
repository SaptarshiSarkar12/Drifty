package Backend;

import Utils.MessageBroker;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static Utils.Constants.*;

public class copyYt_dlp {
    static MessageBroker messageBroker = Drifty.messageBroker;
    private static String tempDir = System.getProperty("java.io.tmpdir");
    public void copyToTemp() throws IOException{
        String yt_dlpFileName;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("nux") || osName.contains("nix")){
            yt_dlpFileName = "yt-dlp";
        } else if (osName.contains("win")) {
            yt_dlpFileName = "yt-dlp.exe";
        } else if (osName.contains("mac")){
            yt_dlpFileName = "yt-dlp_macos";
        } else {
            yt_dlpFileName = "yt-dlp";
        }
        try {
            Files.copy(Path.of("./src/main/resources/" + yt_dlpFileName), Path.of(getTempDir() + yt_dlpFileName));
        } catch (FileAlreadyExistsException e){
            messageBroker.sendMessage("Skipping copying yt-dlp to " + getTempDir() + " folder as it is already present!", LOGGER_INFO, "download");
        }
    }

    public static String getTempDir(){
        if (!tempDir.endsWith(System.getProperty("file.separator"))){
            tempDir += System.getProperty("file.separator");
        }
        return tempDir;
    }
}
