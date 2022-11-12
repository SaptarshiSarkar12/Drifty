import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

/**
 * This is the main class for the CLI version of Drifty.
 * @author Saptarshi Sarkar, AndrexUni, Anurag-Bharati, Naachiket Pant, Fonta22
 * @version 1.2.2
 */
public class Drifty_CLI {
    private static String downloadsFolder;
    private static final Scanner SC = new Scanner(System.in);
    public static CreateLogs logger = new CreateLogs("Drifty_CLI_LOG.log");
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    private static String fName = null;
    protected static boolean isYoutubeURL;
    public static String versionNumber = "v1.2.2";

    /**
     * This function is the main method of the whole application.
     * @param args Command Line Arguments as a String array.
     */
    public static void main(String[] args) {
        logger.log("INFO", "Application Started !");
        initialPrintBanner();
        if (args.length > 0){
            String URL = args[0];
            String name = null;
            String location = null;
            for (int i = 0; i < args.length; i++){
                if (Objects.equals(args[i], "-help") || Objects.equals(args[i], "-h")){
                    help();
                    System.exit(0);
                } else if (Objects.equals(args[i], "-name") || (Objects.equals(args[i], "-n"))){
                    name = args[i+1];
                } else if (Objects.equals(args[i], "-location") || (Objects.equals(args[i], "-l"))){
                    location = args[i+1];
                } else if (Objects.equals(args[i], "-version") || (Objects.equals(args[i], "-v"))) {
                    System.out.println("Drifty " + versionNumber);
                    System.exit(0);
                }
            }
            if (!isURLValid(URL)) {
                System.out.println("URL is invalid!");
                logger.log("ERROR", "URL is invalid!");
                System.exit(0);
            }
            isYoutubeURL = isYoutubeLink(URL);
            fName = (name==null) ? fName : name;
            if ((fName==null || !containsFilename(URL)) && (!isYoutubeURL)) {
                System.out.print("Enter the filename (with file extension) : ");
                fName = SC.nextLine();
            }
            downloadsFolder = location;
            if (downloadsFolder == null){
                saveToDefault();
            }
            else{
                if (System.getProperty("os.name").contains("Windows")) {
                    downloadsFolder = SC.nextLine().replace('/', '\\');
                    if (!(downloadsFolder.endsWith("\\"))) {
                        downloadsFolder = downloadsFolder + System.getProperty("file.separator");
                    }
                }
            }
            new FileDownloader(URL, fName, downloadsFolder).run();
            System.exit(0);
        }
        while(true) {
            fName = null;
            String link;
            while (true) {
                System.out.print("Enter the link to the file : ");
                link = SC.nextLine();
                isYoutubeURL = isYoutubeLink(link);
                if (isYoutubeURL){
                    break;
                }
                if (!isURLValid(link)) {
                    System.out.println("Invalid URL. Please enter again");
                } else if (!containsFilename(link)) {
                    System.out.println("Automatic file name detection failed!");
                    logger.log("ERROR", "Automatic file name detection failed!");
                    break;
                } else {
                    break;
                }
            }
            if (!isYoutubeURL) {
                if (fName != null) {
                    System.out.print("Would you like to rename this file? (Enter Y for yes and N for no) : ");
                    String renameFile = SC.nextLine().toLowerCase();
                    boolean yesOrNo = yesNoValidation(renameFile, "Would you like to rename this file? (Enter Y for yes and N for no) : ");
                    if (yesOrNo) {
                        System.out.print("Enter the filename (with file extension) : ");
                        fName = SC.nextLine();
                    }
                } else {
                    System.out.print("Enter the filename (with file extension) : ");
                    fName = SC.nextLine();
                }
            } else {
                System.out.println("Would you like to rename the YouTube video being downloaded? (Enter Y for yes and N for no) : ");
                String renameFile = SC.nextLine().toLowerCase();
                boolean yesOrNo = yesNoValidation(renameFile, System.out.println("Would you like to rename the YouTube video being downloaded? (Enter Y for yes and N for no) : "));
                if (yesOrNo) {
                    System.out.println("What would you like to name the YouTube video being downloaded? ");
                    fName = SC.nextLine();
                } else {
                    System.out.println("Please note that the YouTube video downloaded would be saved as [video id] video title");
                }
            }
            System.out.print("Do you want to download the file in your default downloads folder? (Enter Y for yes and N for no) : ");
            String default_folder = SC.nextLine().toLowerCase();
            boolean yesOrNo = yesNoValidation(default_folder, "Do you want to download the file in your default downloads folder? (Enter Y for yes and N for no) : ");
            if (yesOrNo) {
                saveToDefault();
            } else {
                enterDownloadsFolder();
            }
            new FileDownloader(link, fName, downloadsFolder).run();
            System.out.println("Press Q to Quit Or Press any Key to Continue");
            String quit = SC.nextLine().toLowerCase();
            if(quit.equals("q")){
                logger.log("INFO", "Application Terminated!");
                break;
            }
            printBanner();
        }
    }

    /**
     * This function takes a folder path as input from the user, where the file will be saved.
     */
    private static void enterDownloadsFolder(){
        System.out.print("Enter the directory in which you want to download the file : ");
        downloadsFolder = SC.nextLine();
        if (System.getProperty("os.name").contains("Windows")) {
            downloadsFolder = SC.nextLine().replace('/', '\\');
            if (!(downloadsFolder.endsWith("\\"))) {
                downloadsFolder = downloadsFolder + System.getProperty("file.separator");
            }
        }
        logger.log("INFO", "Custom Directory Entered : " + downloadsFolder);
    }

    /**
     * This function tries to detect the default downloads folder and save the file in that folder
     */
    private static void saveToDefault(){
        System.out.println("Trying to auto-detect default Downloads folder...");
        logger.log("INFO", "Trying to auto-detect default Downloads folder...");
        if (!System.getProperty("os.name").contains("Windows")) {
            String home = System.getProperty("user.home");
            downloadsFolder = home + "/Downloads/";
        }
        else {
            downloadsFolder = DefaultDownloadFolderLocationFinder.findPath() + System.getProperty("file.separator");
        }
        if (downloadsFolder.equals(System.getProperty("file.separator")) || downloadsFolder.equals(null)) {
            System.out.println("Failed to retrieve default download folder!");
            logger.log("ERROR", "Failed to retrieve default download folder!");
            enterDownloadsFolder();
        } else {
            System.out.println("Default download folder detected : " + downloadsFolder);
            logger.log("INFO", "Default download folder detected : " + downloadsFolder);
        }
    }

    /**
     * @param link Link to the file that the user wants to download
     * @return true if link is valid and false if link is invalid
     */
    private static boolean isURLValid(String link){
        try{
            new URL(link).toURI();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * This method checks whether the link provided is of YouTube or not and returns the resultant boolean value accordingly.
     * @param url link to the file to be downloaded.
     * @return true if the url is of YouTube and false if it is not.
     */
    public static boolean isYoutubeLink(String url) {
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        return url.matches(pattern);
    }

    /**
     * @param link Link to the file that the user wants to download
     * @return true if the filename is detected and false if the filename is not detected
     */
    private static boolean containsFilename(String link){
        // Check and inform user if the url contains filename.
        // Example : "example.com/file.txt" prints "Filename detected: file.txt"
        // example.com/file.json -> file.json
        String file = link.substring(link.lastIndexOf("/")+1);
        int index = file.lastIndexOf(".");
        if (index < 0 || index > file.length()){
            return false;
        }
        String extension = file.substring(index);
        // edge case 1 : "example.com/."
        if (extension.length()==1){
            return false;
        }
        // file.png?width=200 -> file.png
        fName = file.split("([?])")[0];
        System.out.println("Filename detected : " + fName);
        logger.log("INFO", "Filename detected : " + fName);
        return true;
    }

    /**
     * This method performs Yes-No validation and returns the boolean value accordingly.
     * @param input Input String to validate.
     * @param printMsg The message to print to re-input the confirmation.
     * @return true if the user enters Y [Yes] and false if not.
     */
    public static boolean yesNoValidation(String input, String printMsg){
        while (true) {
            if (input.length() == 0) {
                System.out.println("Please enter Y for yes and N for no!");
                logger.log("ERROR", "Please enter Y for yes and N for no!");
            } else {
                break;
            }
            System.out.print(printMsg);
            input = SC.nextLine().toLowerCase();
        }
        char choice = input.charAt(0);
        if (choice == 'y') {
            return true;
        } else if (choice =='n'){
            return false;
        }
        else {
            System.out.println("Invalid input!");
            logger.log("ERROR", "Invalid input");
            System.out.print(printMsg);
            input = SC.nextLine().toLowerCase();
            yesNoValidation(input, printMsg);
        }
        return false;
    }

    /**
     * This function provides help about how to use the application through command line arguments.
     */
    private static void help(){
        System.out.println(ANSI_RESET+"\n\033[38;31;48;40;1m----==| DRIFTY CLI HELP |==----"+ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m            v1.2.2"+ANSI_RESET);
        System.out.println("For more information visit: https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\033[31;1mRequired parameter: File URL"+ANSI_RESET+" \033[3m(This must be the first argument you are passing)"+ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default     Description"+ANSI_RESET);
        System.out.println("-location   -l            Downloads                   The location on your computer where content downloaded from Drifty are placed.");
        System.out.println("-name       -n            Source                      Renames file.");
        System.out.println("-help       -h            N/A                         Provides concise information for Drifty CLI.\n");
        System.out.println("-version    -v            Current Version Number      Displays version number of Drifty.");
        System.out.println("\033[97;1mExample:" + ANSI_RESET + " \n> \033[37;1mjava Drifty_CLI https://example.com/object.png -n obj.png -l C:/Users/example"+ ANSI_RESET );
        System.out.println("\033[37;3m* Requires java 18 or higher. \n"+ANSI_RESET);
    }

    /**
     * This function prints the banner of the application in the console.
     */
    private static void printBanner(){
        System.out.print("\033[H\033[2J");
        System.out.println(ANSI_PURPLE+"===================================================================="+ANSI_RESET);
        System.out.println(ANSI_CYAN+"  _____   _____   _____  ______  _______ __     __"+ANSI_RESET);
        System.out.println(ANSI_CYAN+" |  __ \\ |  __ \\ |_   _||  ____||__   __|\\ \\   / /"+ANSI_RESET);
        System.out.println(ANSI_CYAN+" | |  | || |__) |  | |  | |__      | |    \\ \\_/ /"+ANSI_RESET);
        System.out.println(ANSI_CYAN+" | |  | ||  _  /   | |  |  __|     | |     \\   / "+ANSI_RESET);
        System.out.println(ANSI_CYAN+" | |__| || | \\ \\  _| |_ | |        | |      | |  "+ANSI_RESET);
        System.out.println(ANSI_CYAN+" |_____/ |_|  \\_\\|_____||_|        |_|      |_|  "+ANSI_RESET);
        System.out.println(ANSI_PURPLE+"===================================================================="+ANSI_RESET);
    }

    /**
     * This method prints the banner without any colour of text except white.
     */
    private static void initialPrintBanner(){
        System.out.println("====================================================================");
        System.out.println("  _____   _____   _____  ______  _______ __     __");
        System.out.println(" |  __ \\ |  __ \\ |_   _||  ____||__   __|\\ \\   / /");
        System.out.println(" | |  | || |__) |  | |  | |__      | |    \\ \\_/ /");
        System.out.println(" | |  | ||  _  /   | |  |  __|     | |     \\   / ");
        System.out.println(" | |__| || | \\ \\  _| |_ | |        | |      | |  ");
        System.out.println(" |_____/ |_|  \\_\\|_____||_|        |_|      |_|  ");
        System.out.println("====================================================================");
    }
}
