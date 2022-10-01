import java.util.Scanner;

public class Drifty_CLI {
    private static String downloadsFolder;
    private static final Scanner SC = new Scanner(System.in);
    static CreateLogs cl = new CreateLogs("Logs.log", Drifty_CLI.class.getName());
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    private static boolean flag = false;
    public static void main(String[] args) {
        cl.log("INFO", "Application Started !");
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
        System.out.print("Enter the link to the file : ");
        String link = SC.next();
        SC.nextLine();
        System.out.print("Enter the filename (with file extension) : ");
        String fName = SC.nextLine();
        while (true) {
            System.out.print("Do you want to download the file in your default downloads folder? (Enter Y for yes and N for no) : ");
            char default_folder = SC.nextLine().toLowerCase().charAt(0);
            if (default_folder == 'y') {
                downloadsFolder = DefaultDownloadFolderLocationFinder.findPath() + System.getProperty("file.separator");
                if (downloadsFolder == null) {
                    System.out.println("Failed to retrieve default download folder!");
                    cl.log("ERROR", "Failed to retrieve default download folder!");
                    enterDownloadsFolder();
                } else {
                    System.out.println("Default download folder detected : " + downloadsFolder);
                    cl.log("INFO", "Default download folder detected : " + downloadsFolder);
                }
            } else if (default_folder == 'n') {
                enterDownloadsFolder();
            } else {
                System.out.println("Invalid input!");
                cl.log("ERROR", "Invalid input");
                continue;
            }
            break;
        }
        FileDownloader fDownload = new FileDownloader(link, fName, downloadsFolder);
        fDownload.run();
    }

    private static void enterDownloadsFolder(){
        System.out.print("Enter the directory in which you want to download the file : ");
        downloadsFolder = SC.nextLine().replace('/', '\\');
        if (!(downloadsFolder.endsWith("\\"))) {
            downloadsFolder = downloadsFolder + System.getProperty("file.separator");
        }
    }
}
