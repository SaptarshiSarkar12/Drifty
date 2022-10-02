import java.net.URL;
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
     * Example : "example.com/file.txt" prints "[INFO] File found: file.txt"
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
        System.out.println("File Name detected : " + fName);
        return true;
    }
}
