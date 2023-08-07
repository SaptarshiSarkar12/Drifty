package Backend;

import Enums.MessageCategory;
import Enums.MessageType;
import Utils.MessageBroker;
import Utils.Utility;

import java.io.IOException;
import java.io.PrintStream;

public class Drifty {
    public static String projectWebsite = "https://saptarshisarkar12.github.io/Drifty/";
    private static MessageBroker message = new MessageBroker(System.out);
    private static String downloadsFolder = null;
    private static String url;
    private static String fileName;

    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
        message = new MessageBroker();
    }

    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile, PrintStream outputStream) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
        message = new MessageBroker(outputStream);
    }

    public void start() {
        Utility utility = new Utility(message);
        message.sendMessage("Validating the link...", MessageType.INFORMATION, MessageCategory.LINK);
        if (url.contains(" ")) {
            message.sendMessage("Link should not contain whitespace characters!", MessageType.ERROR, MessageCategory.LINK);
            return;
        }


        else if (url.isEmpty()) {
            message.sendMessage("Link cannot be empty!", MessageType.ERROR, MessageCategory.LINK);
            return;
        }


        else {
            try {
                Utility.isURLValid(url);
                message.sendMessage("Link is valid!", MessageType.INFORMATION, MessageCategory.LINK);
            } catch (Exception e) {
                message.sendMessage(e.getMessage(), MessageType.ERROR, MessageCategory.LINK);
                return;
            }


        }


        if (downloadsFolder == null) {
            downloadsFolder = utility.saveToDefault();
        }


        else {
            downloadsFolder = downloadsFolder.replace('\\', '/');
            if (downloadsFolder.equals(".//") || downloadsFolder.equals("./")) {
                downloadsFolder = "";
            }


            else {
                try {
                    new CheckDirectory(downloadsFolder);
                } catch (IOException e) {
                    message.sendMessage(e.getMessage(), MessageType.ERROR, MessageCategory.DIRECTORY);
                    return;
                }


            }


        }


        if (((fileName == null) || (fileName.isEmpty())) && (!Utility.isYoutubeLink(url) && !Utility.isInstagramLink(url))) {
            fileName = utility.findFilenameInLink(url);
            if (fileName == null || fileName.isEmpty()) {
                message.sendMessage("Filename cannot be empty!", MessageType.ERROR, MessageCategory.FILENAME);
                return;
            }


        }


        new FileDownloader(url, fileName, downloadsFolder).run();
    }

    public void startGUIDownload() {
        new FileDownloader(url, fileName, downloadsFolder).run();
    }

    /**/
    public static MessageBroker getMessageBrokerInstance() {
        return message;
    }
}
