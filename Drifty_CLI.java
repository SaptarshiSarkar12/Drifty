import java.util.Scanner;

public class Drifty_CLI {
    private static String downloadsFolder;
    private static final String COLOR_BRIGHT_BLUE_BOLD = "\033[1;94m";
    private static final String COLOR_RED = "\u001B[31m";
    private static final String COLOR_CYAN_BOLD = "\033[1;36m";
    private static final String COLOR_PURPLE = "\u001B[35m";
    private static final String COLOR_GREEN = "\u001B[32m";
    private static final String COLOR_BLUE = "\u001B[34m";
    private static final String COLOR_RESET = "\u001B[0m";
    private static final Scanner SC = new Scanner(System.in);
    private static final String COLOR_BLUE_BOLD = "\033[1;34m";
    private static final String COLOR_CYAN = "\u001B[36m";
    public static void main(String[] args) {
        System.out.println(COLOR_GREEN + "====================================================================" + COLOR_RESET);
        System.out.println(COLOR_BRIGHT_BLUE_BOLD + "\t\t\tDRIFTY" + COLOR_RESET);
        System.out.println(COLOR_GREEN + "====================================================================" + COLOR_RESET);

        System.out.print(COLOR_PURPLE + "Enter the link to the file : ");
        String link = SC.next();
        SC.nextLine();
        System.out.print("Enter the filename (with file extension) : " + COLOR_RESET);
        String fName = SC.nextLine();
        while (true) {
            System.out.print(COLOR_BLUE + "Do you want to download the file in your default downloads folder? (Enter Y for yes and N for no) : " + COLOR_RESET);
            char default_folder = SC.nextLine().toLowerCase().charAt(0);
            switch (default_folder) {
                case 'y' -> {
                    downloadsFolder = DefaultDownloadFolderLocationFinder.findPath();
                    if (downloadsFolder == null) {
                        System.out.println(COLOR_RED + "Failed to retrieve default download folder!" + COLOR_RESET);
                        enterDownloadsFolder();
                    } else {
                        System.out.println(COLOR_PURPLE + "Default download folder detected : " + COLOR_CYAN_BOLD + downloadsFolder + COLOR_RESET);
                    }
                }
                case 'n' -> enterDownloadsFolder();
                default -> {
                    System.out.println(COLOR_RED + "Invalid input!" + COLOR_RESET);
                    continue;
                }
            }
            break;
        }
        FileDownloader fDownload = new FileDownloader(link, fName, downloadsFolder);
        fDownload.run();
    }

    private static void enterDownloadsFolder(){
        System.out.print(COLOR_PURPLE + "Enter the directory in which you want to download the file : " + COLOR_RESET);
        downloadsFolder = SC.nextLine();
    }
}
