package src;

import java.util.Scanner;

public class Drifty_CLI {
    private static String downloadsFolder;
    private static final Scanner SC = new Scanner(System.in);
    private static boolean flag = false;
    public static void main(String[] args) {
        if (!flag) {
            System.out.println("====================================================================");
            System.out.println("\t\t\t   DRIFTY CLI");
            System.out.println("====================================================================");
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
                    enterDownloadsFolder();
                } else {
                    System.out.println("Default download folder detected : " + downloadsFolder);
                }
            } else if (default_folder == 'n') {
                enterDownloadsFolder();
            } else {
                System.out.println("Invalid input!");
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
