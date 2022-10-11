import java.awt.*;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

/**
 * This is the main class for the CLI version of Drifty.
 */
public class Drifty_CLI {
    private static String downloadsFolder;
    private static final Scanner SC = new Scanner(System.in);
    public static CreateLogs logger = new CreateLogs("Drifty_CLI_LOG.log");
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    private static boolean flag = false;
    private static String fName = null;

    /**
     * This function is the main method of the whole application.
     * @param args Command Line Arguments as a String array.
     */
    public static void main(String[] args) {
        logger.log("INFO", "Application Started !");
        if (!flag) {
            printBanner(); 	 
        }
        flag = true;
        if (args.length > 0){
            String URL = args[0];
            String name = null;
            String location = null;
            for (int i = 0; i<args.length;i++){
                if (Objects.equals(args[i], "-help") || Objects.equals(args[i], "-h")){
                    help();
                    System.exit(0);
                } else if (Objects.equals(args[i], "-name") || (Objects.equals(args[i], "-n"))){
                    name = args[i+1];
                } else if (Objects.equals(args[i], "-location") || (Objects.equals(args[i], "-l"))){
                    location = args[i+1];
                }
            }
            if (!isURLValid(URL)){
                System.exit(0);
            }
            containsFile(URL);
            fName = (name==null) ? fName : name;
            if (fName==null && !isYoutubeURL(URL)){
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
            FileDownloader fDownload = new FileDownloader(URL, fName, downloadsFolder);
            fDownload.run();
            System.exit(0);
        }
        while(true) {
            fName = null;
            System.out.print("Enter the link to the file : ");
            String link = SC.next();
            while (true) {
                if (isYoutubeURL(link)){
                    System.out.println("YOUTUBE URL DETECTED");
                    break;
                }
                if (!isURLValid(link)) {
                    System.out.println("Invalid URL. Please enter again");
                    System.out.print("Enter the link to the file : ");
                    link = SC.next();
                } else if (!containsFile(link)) {
                    System.out.println("Automatic file name detection failed!");
                    logger.log("ERROR", "Automatic file name detection failed!");
                    break;
                } else {
                    break;
                }
            }
            SC.nextLine();
            while (true){
                if (isYoutubeURL(link)){
                    break;
                }
                if (fName != null) {
                    System.out.print("Would you like to rename this file? (Enter Y for yes and N for no) : ");
                    char rename_file = SC.nextLine().toLowerCase().charAt(0);
                    if (rename_file == 'y') {
                        System.out.print("Enter the filename (with file extension) : ");
                        fName = SC.nextLine();
                        break;
                    } else if (rename_file =='n'){
                        break;
                    }
                    else {
                        System.out.println("Invalid input!");
                        logger.log("ERROR", "Invalid input");
                    }
                } else{
                    System.out.print("Enter the filename (with file extension) : ");
                    fName = SC.nextLine();
                    break;
                }
            }
            while (true) {
                System.out.print("Do you want to download the file in your default downloads folder? (Enter Y for yes and N for no) : ");
                char default_folder = SC.nextLine().toLowerCase().charAt(0);
                if (default_folder == 'y') {
                    saveToDefault();
                } else if (default_folder == 'n') {
                    enterDownloadsFolder();
                } else {
                    System.out.println("Invalid input!");
                    logger.log("ERROR", "Invalid input");
                    continue;
                }
                break;
            }
            FileDownloader fDownload = new FileDownloader(link, fName, downloadsFolder);
            fDownload.run();
            System.out.println("Press Q to Quit Or Press any Key to Continue");
            String quit = SC.nextLine();
            if(quit.equals("Q") || quit.equals("q")){
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

    public static boolean isYoutubeURL(String url) {
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        return url.matches(pattern);
    }

    /**
     * @param link Link to the file that the user wants to download
     * @return true if the filename is detected and false if the filename is not detected
     */
    private static boolean containsFile(String link){
        // Check and inform user if the url contains file.
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
        return true;
    }

    /**
     * This function provides help about how to use the application through command line arguments.
     */
    private static void help(){
        System.out.println(ANSI_RESET+"\n\033[38;31;48;40;1m----==| DRIFTY CLI HELP |==----"+ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m            v1.1.0"+ANSI_RESET);
        System.out.println("For more information visit: https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\033[31;1mRequired parameter: File URL"+ANSI_RESET+" \033[3m(This must be the first argument you are passing)"+ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default     Description"+ANSI_RESET);
        System.out.println("-location   -l            Downloads   The location on your computer where content downloaded from Drifty are placed.");
        System.out.println("-name       -n            Source      Renames file.");
        System.out.println("-help       -h            N/A         Provides concise information for Drifty CLI.\n");
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
}
