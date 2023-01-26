package Backend;

import Utils.DriftyConstants;
import Utils.DriftyUtility;
import Utils.MessageBroker;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Drifty {
    protected static MessageBroker messageBroker;
    private static String downloadsFolder = null;
    private static String url;
    private static String fileName;
    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile, Text link, Text dir, Text download, Text renameFile) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
        messageBroker = new MessageBroker("GUI", link, dir, download, renameFile);
    }
    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile, PrintStream outputStream) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
        messageBroker = new MessageBroker("CLI", outputStream);
    }

    public void start() {
        messageBroker.sendMessage("Validating the link...", DriftyConstants.LOGGER_INFO, "link");
        if (!url.contains(" ")) {
            try {
                DriftyUtility.isURLValid(url);
                messageBroker.sendMessage("Link is valid!", DriftyConstants.LOGGER_INFO, "link");
            } catch (Exception e) {
                messageBroker.sendMessage(e.getMessage(), DriftyConstants.LOGGER_ERROR, "link");
            }
        } else {
            messageBroker.sendMessage("Link should not contain whitespace characters!", DriftyConstants.LOGGER_ERROR, "link");
        }

        if (downloadsFolder == null){
            downloadsFolder = DriftyUtility.saveToDefault();
        } else {
            try {
                new CheckDirectory(downloadsFolder);
            } catch (IOException e) {
                messageBroker.sendMessage(e.getMessage(), DriftyConstants.LOGGER_ERROR, "directory");
            }
        }

        if ((fileName == null || fileName.length() == 0) && (!DriftyUtility.isYoutubeLink(url))) {
            fileName = DriftyUtility.findFilenameInLink(url);
            if (fileName == null || fileName.length() == 0) {
                messageBroker.sendMessage("Filename cannot be empty!", DriftyConstants.LOGGER_ERROR, "renameFile");
            }
        }

        new FileDownloader(url, fileName, downloadsFolder).run();
    }

    public static void main(String[] args) throws IOException {
//        new Drifty("https://github.com/SaptarshiSarkar12/Drifty/blob/master/README.md", null, "read.md", System.out).start();
        System.out.println(File.createTempFile("yt-dlp", ".exe").getPath());
    }
}
