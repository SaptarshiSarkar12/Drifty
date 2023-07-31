package Backend;

import Enums.Category;
import Enums.Type;
import Utils.MessageBroker;
import Utils.Utility;

import java.io.IOException;
import java.io.PrintStream;

/**
 * This method is the Backend of Drifty which does the main part of Downloading the file from the website URL.
 *
 * @version 2.0.0
 * @since 2.0.0
 */
public class Drifty {
    public static String projectWebsite = "https://saptarshisarkar12.github.io/Drifty/";
    private static MessageBroker message = new MessageBroker(System.out);
    private static String downloadsFolder = null;
    private static String url;
    private static String fileName;

    /**
     * This is the <b>constructor of Backend of Drifty</b> that configures it to be able to work with <b>Graphical User Interface (GUI) Functionalities</b>.
     *
     * @param url                         Link to the website collected from the user through inputs.
     * @param downloadsDirectory          The directory where the file will be downloaded ("./" for Default Downloads Folder).
     * @param fileNameOfTheDownloadedFile File string to keep for the file to be downloaded.
     * @since 2.0.0
     */
    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
        message = new MessageBroker();
    }

    /**
     * This is the <b>constructor of Backend of Drifty</b> that configures it to be able to work with <b>Command Line Interface (CLIString) Functionalities</b>.
     *
     * @param url                         Link to the website collected from the user through inputs.
     * @param downloadsDirectory          The directory where the file will be downloaded ("./" for Default Downloads Folder).
     * @param fileNameOfTheDownloadedFile File string to keep for the file to be downloaded.
     * @param outputStream                The Output Stream where the Backend will give its outputs (Usually, in this case - 'System.out' [Standard Output Stream])
     * @since 2.0.0
     */
    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile, PrintStream outputStream) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
        message = new MessageBroker(outputStream);
    }

    /**
     * This is the main method where the Drifty Backend starts the backend process of Validating the inputs followed by downloading the file in the required folder.
     */
    public void start() {
        Utility utility = new Utility(message);
        message.send("Validating the link...", Type.INFORMATION, Category.LINK);
        if (url.contains(" ")) {
            message.send("Link should not contain whitespace characters!", Type.ERROR, Category.LINK);
            return;
        }
        else if (url.length() == 0) {
            message.send("Link cannot be empty!", Type.ERROR, Category.LINK);
            return;
        }
        else {
            try {
                Utility.isURLValid(url);
                message.send("Link is valid!", Type.INFORMATION, Category.LINK);
            } catch (Exception e) {
                message.send(e.getMessage(), Type.ERROR, Category.LINK);
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
                    message.send(e.getMessage(), Type.ERROR, Category.DIRECTORY);
                    return;
                }
            }
        }

        if (((fileName == null) || (fileName.length() == 0)) && (!Utility.isYoutubeLink(url) && !Utility.isInstagramLink(url))) {
            fileName = utility.findFilenameInLink(url);
            if (fileName == null || fileName.length() == 0) {
                message.send("Filename cannot be empty!", Type.ERROR, Category.FILENAME);
                return;
            }
        }
        new FileDownloader(url, fileName, downloadsFolder).run();
    }

    public void startGUIDownload() {
        new FileDownloader(url, fileName, downloadsFolder).run();
    }

    /**
     * This method returns the message broker instance.
     *
     * @return the message broker instance.
     */
    public static MessageBroker getMessageBrokerInstance() {
        return message;
    }
}
