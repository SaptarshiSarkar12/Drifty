package CLI;

import Utils.CreateLogs;
import Backend.FileDownloader;
import Utils.ScannerFactory;
import java.util.Objects;
import java.util.Scanner;
import Backend.Drifty;

import static Utils.DriftyUtility.*;
import static Utils.DriftyConstants.*;

/**
 * This is the main class for the CLI version of Drifty.
 * @author Saptarshi Sarkar, Akshat Jain, Anurag Bharati, Naachiket Pant, Fonta22
 * @version 1.2.2
 */
public class Drifty_CLI {
    public static final CreateLogs logger = CreateLogs.getInstance();
    protected static final Scanner SC = ScannerFactory.getInstance();
    protected static boolean isYoutubeURL;
    private static String downloadsFolder;
    private static String fileName = null;

    /**
     * This function is the main method of the whole application.
     * @param args Command Line Arguments as a String array.
     */
    public static void main(String[] args) throws Exception {
        logger.log(LOGGER_INFO, APPLICATION_STARTED);
        initialPrintBanner();
        if (args.length > 0) {
            String URL = args[0];
            String name = null;
            String location = null;
            for (int i = 0; i < args.length; i++) {
                if (Objects.equals(args[i], HELP_FLAG) || Objects.equals(args[i], HELP_FLAG_SHORT)) {
                    help();
                    System.exit(0);
                } else if (Objects.equals(args[i], NAME_FLAG) || (Objects.equals(args[i], NAME_FLAG_SHORT))) {
                    name = args[i + 1];
                } else if (Objects.equals(args[i], LOCATION_FLAG) || (Objects.equals(args[i], LOCATION_FLAG_SHORT))) {
                    location = args[i + 1];
                } else if (Objects.equals(args[i], VERSION_FLAG) || (Objects.equals(args[i], VERSION_FLAG_SHORT))) {
                    System.out.println(APPLICATION_NAME + " " + VERSION_NUMBER);
                    System.exit(0);
                }
            }
//            System.out.println("Validating the link..."); // TODO - Remove the code
//            try {
//                isURLValid(URL);
//                System.out.println("The link is valid!");
//                logger.log(LOGGER_INFO, "The link is valid!");
//            } catch (Exception e){
//                System.out.println(e.getMessage());
//                logger.log(LOGGER_ERROR, e.getMessage());
//                logger.log(LOGGER_INFO, APPLICATION_TERMINATED);
//                System.exit(0);
//            }
            isYoutubeURL = isYoutubeLink(URL);
            fileName = (name == null) ? fileName : name;
            fileName = findFilenameInLink(URL);
            if ((fileName == null || (fileName.length() == 0)) && (!isYoutubeURL)) {
                System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                fileName = SC.nextLine();
            }
            downloadsFolder = location;
            if (downloadsFolder == null) {
                saveToDefault();
            } else {
                if (System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
                    downloadsFolder = SC.nextLine().replace('/', '\\');
                    if (!(downloadsFolder.endsWith("\\"))) {
                        downloadsFolder = downloadsFolder + System.getProperty("file.separator");
                    }
                }
            }
            Drifty backend = new Drifty(URL, downloadsFolder, fileName, System.out);
            backend.start();
            logger.log(LOGGER_INFO, APPLICATION_TERMINATED);
            System.exit(0);
        }
        while (true) {
            fileName = null;
            String link;
            while (true) {
                System.out.print(ENTER_FILE_LINK);
                link = SC.nextLine();
                isYoutubeURL = isYoutubeLink(link);
                if (isYoutubeURL) {
                    break;
                }
                System.out.println("Validating the link...");
                try {
                    boolean isValid = isURLValid(link);
                    if (isValid){
                        System.out.println("The link is valid!");
                        logger.log(LOGGER_INFO, "The link is valid!");
                        break;
                    }
                } catch (Exception e){
                    System.out.println(e.getMessage());
                    logger.log(LOGGER_ERROR, e.getMessage());
                }
            }
            System.out.print(DOWNLOAD_DEFAULT_LOCATION);
            String defaultFolder = SC.nextLine().toLowerCase();
            boolean yesOrNo = yesNoValidation(defaultFolder, DOWNLOAD_DEFAULT_LOCATION);
            if (yesOrNo) {
                downloadsFolder = saveToDefault();
            } else {
                enterDownloadsFolder();
            }
            if (!isYoutubeURL) {
                fileName = findFilenameInLink(link);
            }
            if (!isYoutubeURL && (fileName.length() == 0)) {
                System.out.println(AUTO_FILE_DETECTION_FAILED);
                logger.log(LOGGER_ERROR, AUTO_FILE_DETECTION_FAILED);
            }
            if (!isYoutubeURL) {
                if (fileName != null && (fileName.length() != 0)) {
                    System.out.print(RENAME_FILE);
                    String renameFile = SC.nextLine().toLowerCase();
                    yesOrNo = yesNoValidation(renameFile, RENAME_FILE);
                    if (yesOrNo) {
                        System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                        fileName = SC.nextLine();
                    }
                } else {
                    System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                    fileName = SC.nextLine();
                }
            }
            new FileDownloader(link, fileName, downloadsFolder).run();
            System.out.println(QUIT_OR_CONTINUE);
            String quit = SC.nextLine().toLowerCase();
            if (quit.equals("q")) {
                logger.log(LOGGER_INFO, APPLICATION_TERMINATED);
                break;
            }
            printBanner();
        }
    }

    /**
     * This function takes a folder path as input from the user, where the file will be saved.
     */
    private static void enterDownloadsFolder() {
        System.out.print(DIRECTORY_TO_DOWNLOAD_FILE);
        downloadsFolder = SC.nextLine();
        if (System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
            downloadsFolder = SC.nextLine().replace('/', '\\');
            if (!(downloadsFolder.endsWith("\\"))) {
                downloadsFolder = downloadsFolder + System.getProperty("file.separator");
            }
        }
        logger.log(LOGGER_INFO, "Custom Directory Entered : " + downloadsFolder);
    }
}
