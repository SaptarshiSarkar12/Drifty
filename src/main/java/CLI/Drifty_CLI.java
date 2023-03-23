package CLI;

import Backend.Drifty;
import Utils.CreateLogs;
import Utils.ScannerFactory;

import java.util.Objects;
import java.util.Scanner;

import static Utils.Constants.*;
import static Utils.Utility.*;

/**
 * This is the main class for the CLI (Command Line Interface) version of Drifty.
 * @version 2.0.0
 */
public class Drifty_CLI {
    public static final CreateLogs logger = CreateLogs.getInstance();
    protected static final Scanner SC = ScannerFactory.getInstance();
    protected static boolean isYoutubeURL;
    private static String fileName = null;

    /**
     * This function is the main method of the whole application.
     * @param args Command Line Arguments as a String array.
     */
    public static void main(String[] args) {
        logger.log(LOGGER_INFO, CLI_APPLICATION_STARTED);
        initialPrintBanner();
        String downloadsFolder;
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
            isYoutubeURL = isYoutubeLink(URL);
            fileName = (name == null) ? fileName : name;
            fileName = findFilenameInLink(URL);
            if ((fileName == null || (fileName.length() == 0)) && (!isYoutubeURL)) {
                System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                fileName = SC.nextLine();
            }
            downloadsFolder = location;
            if (downloadsFolder == null) {
                downloadsFolder = saveToDefault();
            } else {
                if (System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
                    downloadsFolder = downloadsFolder.replace('/', '\\');
                    if (!(downloadsFolder.endsWith("\\"))) {
                        downloadsFolder = downloadsFolder + System.getProperty("file.separator");
                    }
                }
            }
            Drifty backend = new Drifty(URL, downloadsFolder, fileName, System.out);
            backend.start();
            logger.log(LOGGER_INFO, CLI_APPLICATION_TERMINATED);
            System.exit(0);
        }
        while (true) {
            System.out.print(ENTER_FILE_LINK);
            String link = SC.next();
            SC.nextLine();
            System.out.print("Enter the download directory (Enter \".\" for default downloads folder) : ");
            downloadsFolder = SC.next();
            isYoutubeURL = isYoutubeLink(link);
            fileName = findFilenameInLink(link);
            if ((fileName == null || (fileName.length() == 0)) && (!isYoutubeURL)) {
                System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                fileName = SC.nextLine();
            } else {
                System.out.println(FILENAME_DETECTED + fileName);
                System.out.print(RENAME_FILE);
                SC.nextLine(); // To remove 'whitespace' from input buffer.
                String choiceString = SC.nextLine();
                boolean choice = yesNoValidation(choiceString, RENAME_FILE);
                if (choice){
                    System.out.print(ENTER_FILE_NAME_WITH_EXTENSION);
                }
            }
            Drifty backend = new Drifty(link, downloadsFolder, fileName, System.out);
            backend.start();
            System.out.println(QUIT_OR_CONTINUE);
            SC.nextLine();
            String quit = SC.nextLine().toLowerCase();
            if (quit.equals("q")) {
                logger.log(LOGGER_INFO, CLI_APPLICATION_TERMINATED);
                break;
            }
            printBanner();
        }
    }
}
