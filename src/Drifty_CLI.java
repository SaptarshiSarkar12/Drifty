import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

public class Drifty_CLI {
    private static String downloadsFolder;
    private static final Scanner SC = new Scanner(System.in);
    public static CreateLogs logger = new CreateLogs("Drifty_CLI_LOG.log", Drifty_CLI.class.getName());
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    private static boolean flag = false;
    private static String fName = null;
    
    public static void main(String[] args) {
        logger.log("INFO", "Application Started !");
        if (!flag) {
            System.out.println(ANSI_PURPLE+"===================================================================="+ANSI_RESET);
            System.out.println(ANSI_CYAN+"  _____   _____   _____  ______  _______ __     __"+ANSI_RESET);
            System.out.println(ANSI_CYAN+" |  __ \\ |  __ \\ |_   _||  ____||__   __|\\ \\   / /"+ANSI_RESET);
            System.out.println(ANSI_CYAN+" | |  | || |__) |  | |  | |__      | |    \\ \\_/ /"+ANSI_RESET);
            System.out.println(ANSI_CYAN+" | |  | ||  _  /   | |  |  __|     | |     \\   / "+ANSI_RESET);
            System.out.println(ANSI_CYAN+" | |__| || | \\ \\  _| |_ | |        | |      | |  "+ANSI_RESET);
            System.out.println(ANSI_CYAN+" |_____/ |_|  \\_\\|_____||_|        |_|      |_|  "+ANSI_RESET);
            System.out.println(ANSI_PURPLE+"===================================================================="+ANSI_RESET);
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
            if (!validURL(URL)){
                System.exit(0);
            }
            containsFile(URL);
            fName = name==null? fName: name;
            if (fName==null){
                System.out.print("Enter the filename (with file extension) : ");
                fName = SC.nextLine();
            }
            downloadsFolder = location;
            if (downloadsFolder == null){
                saveToDefault();
            } else{
                downloadsFolder = downloadsFolder.replace('/', '\\');
                if (!(downloadsFolder.endsWith("\\"))) {
                    downloadsFolder = downloadsFolder + System.getProperty("file.separator");
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
                if (!validURL(link)) {
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
        }
    }

    private static void enterDownloadsFolder(){
        System.out.print("Enter the directory in which you want to download the file : ");
        downloadsFolder = SC.nextLine().replace('/', '\\');
        if (!(downloadsFolder.endsWith("\\"))) {
            downloadsFolder = downloadsFolder + System.getProperty("file.separator");
        }
    }

    private static void saveToDefault(){
        System.out.println("Trying to auto-detect default Downloads folder...");
        logger.log("INFO", "Trying to auto-detect default Downloads folder...");
        downloadsFolder = DefaultDownloadFolderLocationFinder.findPath() + System.getProperty("file.separator");
        if (downloadsFolder.equals(System.getProperty("file.separator"))) {
            System.out.println("Failed to retrieve default download folder!");
            logger.log("ERROR", "Failed to retrieve default download folder!");
            enterDownloadsFolder();
        } else {
            System.out.println("Default download folder detected : " + downloadsFolder);
            logger.log("INFO", "Default download folder detected : " + downloadsFolder);
        }
    }

    private static boolean validURL(String link){
        try{
            new URL(link).toURI();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    /**
     * Check and inform user if the url contains file.
     * Example : "example.com/file.txt" prints "Filename detected: file.txt"
     **/
    private static boolean containsFile(String link){
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

    private static String getCurrentTimeAsName(){
        return new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
    }

    private static void help(){
        System.out.println(ANSI_RESET+"\n\033[38;31;48;40;1m--=| DRIFTY CLI HELP |=--"+ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m        v 1.1.0"+ANSI_RESET);
        System.out.println("For more information visit: https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\033[31;1mRequired parameter: File URL"+ANSI_RESET+" \033[3m(This must be the first arg. you pass)"+ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default     Description"+ANSI_RESET);
        System.out.println("-location   -l            Downloads   The location on your computer where content downloaded from Drifty are placed.");
        System.out.println("-name       -n            Source      Renames file.");
        System.out.println("-help       -h            N/A         Provides concise information for Drifty CLI.\n");
        System.out.println("\033[97;1mExample:" + ANSI_RESET + " \n> \033[37;1mjava Drifty_CLI https://example.com/object.png -n obj.png -l C:/Users/example"+ ANSI_RESET );
        System.out.println("\033[37;3m* Requires java 18 or higher. \n"+ANSI_RESET);
    }
}
