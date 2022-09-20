import java.util.Scanner;

public class Drifty_CLI {
    private static String downloadsFolder;
    private static final Scanner SC = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("\u001B[32m" + "====================================================================" + "\u001B[0m");
        System.out.println("\u001B[34m\t\t\t\t\t\t\t\t" + "DRIFTY\t" + "\u001B[0m");
        System.out.println("\u001B[32m" + "====================================================================" + "\u001B[0m");

        System.out.print("Enter the link to the file : ");
        String link = SC.next();
        SC.nextLine();
        System.out.print("Enter the filename (with file extension) : ");
        String fName = SC.nextLine();
        System.out.print("Do you want to download the file in your default downloads folder? (Enter Y for yes and N for no) : ");
        char default_folder = SC.nextLine().toLowerCase().charAt(0);
        switch (default_folder){
            case 'y' -> {
                downloadsFolder = DefaultDownloadFolderLocationFinder.findPath();
                if (downloadsFolder == null){
                    System.err.println("Failed to retrieve default download folder!");
                    enterDownloadsFolder();
                }
            }
            case 'n' -> {
                enterDownloadsFolder();
            }
            default -> System.err.println("Invalid input!");
        }

    }

    private static void enterDownloadsFolder(){
        System.out.print("Enter the directory in which you want to download the file : ");
        downloadsFolder = SC.nextLine();
    }
}
