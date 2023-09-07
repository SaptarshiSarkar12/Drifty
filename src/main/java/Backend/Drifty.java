package Backend;

import Enums.MessageCategory;
import Enums.MessageType;
import Utils.Environment;
import Utils.MessageBroker;
import Utils.Utility;

import java.io.IOException;
import java.io.PrintStream;

public class Drifty {
    public static final String projectWebsite = "https://saptarshisarkar12.github.io/Drifty/";
    private static final MessageBroker messageBroker = Environment.getMessageBroker();
    private static String downloadsFolder = null;
    private static String url;
    private static String fileName;

    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
    }

    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile, PrintStream outputStream) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
    }

    public void start() {
        Utility utility = new Utility();
        messageBroker.sendMessage("Validating the link...", MessageType.INFO, MessageCategory.LINK);
        if (url.contains(" ")) {
            messageBroker.sendMessage("Link should not contain whitespace characters!", MessageType.ERROR, MessageCategory.LINK);
            return;
        } else if (url.isEmpty()) {
            messageBroker.sendMessage("Link cannot be empty!", MessageType.ERROR, MessageCategory.LINK);
            return;
        } else {
            boolean isUrlValid = Utility.isURLValid(url);
            if (!isUrlValid) {
                return;
            }
        }
        if (downloadsFolder == null || downloadsFolder.equals(".")) {
            downloadsFolder = Utility.getFormattedDefaultDownloadsFolder();
        }
        downloadsFolder = downloadsFolder.replace('\\', '/');
        try {
            new CheckDirectory(downloadsFolder);
        } catch (IOException e) {
            messageBroker.sendMessage(e.getMessage(), MessageType.ERROR, MessageCategory.DIRECTORY);
            return;
        }
        if (fileName == null) {
            messageBroker.sendMessage("Filename cannot be null!", MessageType.ERROR, MessageCategory.FILENAME);
        } else if (fileName.isEmpty()) {
            messageBroker.sendMessage("Filename cannot be empty!", MessageType.ERROR, MessageCategory.FILENAME);
        }
        new FileDownloader(url, fileName, downloadsFolder).run();
    }

    public void startGUIDownload() {
        new FileDownloader(url, fileName, downloadsFolder).run();
    }
}
