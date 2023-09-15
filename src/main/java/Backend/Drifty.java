package Backend;

import Preferences.AppSettings;
import Utils.Environment;
import Utils.MessageBroker;
import Utils.Utility;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Drifty {
    public static final String projectWebsite = "https://saptarshisarkar12.github.io/Drifty/";
    private static final MessageBroker M = Environment.getMessageBroker();
    private static String downloadsFolder = null;
    private static String url;
    private static String filename;

    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        filename = fileNameOfTheDownloadedFile;
    }

    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile, PrintStream outputStream) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        filename = fileNameOfTheDownloadedFile;
    }

    public void start() {
        Utility utility = new Utility();
        M.msgLinkInfo("Validating the link...");
        if (url.contains(" ")) {
            M.msgLinkError("Link should not contain whitespace characters!");
            return;
        }
        else if (url.isEmpty()) {
            M.msgLinkError("Link cannot be empty!");
            return;
        }
        else {
            boolean isUrlValid = Utility.linkValid(url);
            if (!isUrlValid) {
                return;
            }
        }
        if (downloadsFolder == null || downloadsFolder.equals(".")) {
            downloadsFolder = Utility.getHomeDownloadFolder();
        }
        else if (downloadsFolder.toLowerCase().contains("l")) {
            downloadsFolder = AppSettings.get.lastDownloadFolder();
        }
        Path path = Paths.get(downloadsFolder);
        downloadsFolder = path.toAbsolutePath().toString();
        AppSettings.get.folders().addFolder(downloadsFolder);
        if (!path.toFile().exists()) {
            M.msgDirError("Download folder does not exist!");
            System.out.println("Specified download folder does not exist!");
            return;
        }
        if (filename == null) {
            M.msgFilenameError("Filename cannot be null!");
        }
        else if (filename.isEmpty()) {
            M.msgFilenameError("Filename cannot be empty!");
        }
        new FileDownloader(url, filename, downloadsFolder).run();
    }
}
