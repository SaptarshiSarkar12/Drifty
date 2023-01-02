import singleton.CreateLogs;
import singleton.ScannerFactory;
import utility.DriftyUtility;
import validation.DriftyValidation;

import java.util.Objects;
import java.util.Scanner;

import static constants.DriftyConstants.*;


/**
 * This is the main class for the CLI version of Drifty.
 *
 * @author Saptarshi Sarkar, Akshat Jain, Anurag Bharati, Naachiket Pant, Fonta22
 * @version 1.2.2
 */
public class Drifty_CLI {
    public static final CreateLogs logger = CreateLogs.getInstance();

    protected static final Scanner SCANNER = ScannerFactory.getInstance();
    protected static boolean isYoutubeURL;
    private static String downloadsFolder;
    private static String fileName = null;

    /**
     * This function is the main method of the whole application.
     *
     * @param args Command Line Arguments as a String array.
     */
    public static void main(String[] args) {
        logger.log(LOGGER_INFO, APPLICATION_STARTED);
        DriftyUtility.initialPrintBanner();
        if (args.length > 0) {
            String URL = args[0];
            String name = null;
            String location = null;
            for (int i = 0; i < args.length; i++) {
                if (Objects.equals(args[i], HELP_FLAG) || Objects.equals(args[i], HELP_FLAG_SHORT)) {
                    DriftyUtility.help();
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
            if (!DriftyUtility.isURLValid(URL)) {
                System.out.println(INVALID_URL);
                logger.log(LOGGER_ERROR, INVALID_URL);
                System.exit(0);
            }
            isYoutubeURL = DriftyUtility.isYoutubeLink(URL);
            fileName = (name == null) ? fileName : name;
            if ((fileName == null || !containsFilename(URL)) && (!isYoutubeURL)) {
                System.out.print(FILE_NAME_WITH_EXTENSION);
                fileName = SCANNER.nextLine();
            }
            downloadsFolder = location;
            if (downloadsFolder == null) {
                saveToDefault();
            } else {
                if (System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
                    downloadsFolder = SCANNER.nextLine().replace('/', '\\');
                    if (!(downloadsFolder.endsWith("\\"))) {
                        downloadsFolder = downloadsFolder + System.getProperty("file.separator");
                    }
                }
            }
            new FileDownloader(URL, fileName, downloadsFolder).run();
            System.exit(0);
        }
        while (true) {
            fileName = null;
            String link;
            while (true) {
                System.out.print(FILE_LINK);
                link = SCANNER.nextLine();
                isYoutubeURL = DriftyUtility.isYoutubeLink(link);
                if (isYoutubeURL) {
                    break;
                }
                if (!DriftyUtility.isURLValid(link)) {
                    System.out.println(INVALID_URL_ENTER_AGAIN);
                } else if (!containsFilename(link)) {
                    System.out.println(AUTOMATIC_FILE_DETECTION);
                    logger.log(LOGGER_ERROR, AUTOMATIC_FILE_DETECTION);
                    break;
                } else {
                    break;
                }
            }
            System.out.print(DOWNLOAD_DEFAULT_LOCATION);
            String defaultFolder = SCANNER.nextLine().toLowerCase();
            boolean yesOrNo = DriftyValidation.yesNoValidation(defaultFolder, DOWNLOAD_DEFAULT_LOCATION);
            if (yesOrNo) {
                saveToDefault();
            } else {
                enterDownloadsFolder();
            }
            if (!isYoutubeURL) {
                if (fileName != null) {
                    System.out.print(RENAME_FILE);
                    String renameFile = SCANNER.nextLine().toLowerCase();
                    yesOrNo = DriftyValidation.yesNoValidation(renameFile, RENAME_FILE);
                    if (yesOrNo) {
                        System.out.print(FILE_NAME_WITH_EXTENSION);
                        fileName = SCANNER.nextLine();
                    }
                } else {
                    System.out.print(FILE_NAME_WITH_EXTENSION);
                    fileName = SCANNER.nextLine();
                }
            }
            new FileDownloader(link, fileName, downloadsFolder).run();
            System.out.println(QUIT_OR_CONTINUE);
            String quit = SCANNER.nextLine().toLowerCase();
            if (quit.equals("q")) {
                logger.log(LOGGER_INFO, APPLICATION_TERMINATED);
                break;
            }
            DriftyUtility.printBanner();
        }
    }

    /**
     * This function takes a folder path as input from the user, where the file will be saved.
     */
    private static void enterDownloadsFolder() {
        System.out.print(DIRECTORY_TO_DOWNLOAD_FILE);
        downloadsFolder = SCANNER.nextLine();
        if (System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
            downloadsFolder = SCANNER.nextLine().replace('/', '\\');
            if (!(downloadsFolder.endsWith("\\"))) {
                downloadsFolder = downloadsFolder + System.getProperty("file.separator");
            }
        }
        logger.log(LOGGER_INFO, "Custom Directory Entered : " + downloadsFolder);
    }

    /**
     * This function tries to detect the default downloads folder and save the file in that folder
     */
    private static void saveToDefault() {
        System.out.println(TRYING_TO_AUTO_DETECT_FILE);
        logger.log(LOGGER_ERROR, TRYING_TO_AUTO_DETECT_FILE);
        if (!System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
            String home = System.getProperty(USER_HOME_PROPERTY);
            downloadsFolder = home + DOWNLOADS_FILE_PATH;
        } else {
            downloadsFolder = DefaultDownloadFolderLocationFinder.findPath() + System.getProperty("file.separator");
        }
        if (downloadsFolder.equals(System.getProperty("file.separator")) || downloadsFolder == null) {
            System.out.println(FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER);
            logger.log(LOGGER_ERROR, FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER);
            enterDownloadsFolder();
        } else {
            System.out.println(DEFAULT_DOWNLOAD_FOLDER + downloadsFolder);
            logger.log(LOGGER_INFO, DEFAULT_DOWNLOAD_FOLDER + downloadsFolder);
        }
    }


    /**
     * @param link Link to the file that the user wants to download
     * @return true if the filename is detected and false if the filename is not detected
     */
    private static boolean containsFilename(String link) {
        // Check and inform user if the url contains filename.
        // Example : "example.com/file.txt" prints "Filename detected: file.txt"
        // example.com/file.json -> file.json
        String file = link.substring(link.lastIndexOf("/") + 1);
        int index = file.lastIndexOf(".");
        if (index < 0) {
            return false;
        }
        String extension = file.substring(index);
        // edge case 1 : "example.com/."
        if (extension.length() == 1) {
            return false;
        }
        // file.png?width=200 -> file.png
        fileName = file.split("([?])")[0];
        System.out.println(FILENAME_DETECTED + fileName);
        logger.log(LOGGER_INFO, FILENAME_DETECTED + fileName);
        return true;
    }

}
