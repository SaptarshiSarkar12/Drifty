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
    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile, Text linkOutputTextArea, Text directoryOutputTextArea, Text downloadOutputTextArea, Text fileNameOutputTextArea) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
        messageBroker = new MessageBroker("GUI", linkOutputTextArea, directoryOutputTextArea, downloadOutputTextArea, fileNameOutputTextArea);
    }
    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile, PrintStream outputStream) {
        Drifty.url = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
        messageBroker = new MessageBroker("CLI", outputStream);
    }

    public void start() {
        boolean errorFlag = false;
        messageBroker.sendMessage("Validating the link...", DriftyConstants.LOGGER_INFO, "link");
        if (url.contains(" ")) {
            messageBroker.sendMessage("Link should not contain whitespace characters!", DriftyConstants.LOGGER_ERROR, "link");
            errorFlag = true;
        } else if (url.length() == 0) {
            messageBroker.sendMessage("Link cannot be empty!", DriftyConstants.LOGGER_ERROR, "link");
            errorFlag = true;
        } else {
            try {
                DriftyUtility.isURLValid(url);
                messageBroker.sendMessage("Link is valid!", DriftyConstants.LOGGER_INFO, "link");
            } catch (Exception e) {
                messageBroker.sendMessage(e.getMessage(), DriftyConstants.LOGGER_ERROR, "link");
                errorFlag = true;
            }
        }

        if (downloadsFolder == null){
            downloadsFolder = DriftyUtility.saveToDefault();
        } else {
            try {
                new CheckDirectory(downloadsFolder);
            } catch (IOException e) {
                messageBroker.sendMessage(e.getMessage(), DriftyConstants.LOGGER_ERROR, "directory");
                errorFlag = true;
            }
        }

        if (((fileName == null) || (fileName.length() == 0)) && (!DriftyUtility.isYoutubeLink(url))) {
            fileName = DriftyUtility.findFilenameInLink(url);
            if (fileName == null || fileName.length() == 0) {
                messageBroker.sendMessage("Filename cannot be empty!", DriftyConstants.LOGGER_ERROR, "renameFile");
                errorFlag = true;
            }
        }

        if (!errorFlag) {
            new FileDownloader(url, fileName, downloadsFolder).run();
        }
    }
}
