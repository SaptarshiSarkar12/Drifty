package utility;

import java.net.URL;

import static constants.DriftyConstants.*;

public final class DriftyUtility {

    private DriftyUtility() {
    }

    /**
     * This method checks whether the link provided is of YouTube or not and returns the resultant boolean value accordingly.
     *
     * @param url link to the file to be downloaded.
     * @return true if the url is of YouTube and false if it is not.
     */
    public static boolean isYoutubeLink(String url) {
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        return url.matches(pattern);
    }

    /**
     * @param link Link to the file that the user wants to download
     * @return true if link is valid and false if link is invalid
     */
    public static boolean isURLValid(String link) {
        try {
            new URL(link).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void help() {
        System.out.println(ANSI_RESET + "\n\033[38;31;48;40;1m----==| DRIFTY CLI HELP |==----" + ANSI_RESET);
        System.out.println("\033[38;31;48;40;0m            v1.2.2" + ANSI_RESET);
        System.out.println("For more information visit: https://github.com/SaptarshiSarkar12/Drifty/");
        System.out.println("\033[31;1mRequired parameter: File URL" + ANSI_RESET + " \033[3m(This must be the first argument you are passing)" + ANSI_RESET);
        System.out.println("\033[33;1mOptional parameters:");
        System.out.println("\033[97;1mName        ShortForm     Default     Description" + ANSI_RESET);
        System.out.println("-location   -l            Downloads                   The location on your computer where content downloaded from Drifty are placed.");
        System.out.println("-name       -n            Source                      Renames file.");
        System.out.println("-help       -h            N/A                         Provides concise information for Drifty CLI.\n");
        System.out.println("-version    -v            Current Version Number      Displays version number of Drifty.");
        System.out.println("\033[97;1mExample:" + ANSI_RESET + " \n> \033[37;1mjava Drifty_CLI https://example.com/object.png -n obj.png -l C:/Users/example" + ANSI_RESET);
        System.out.println("\033[37;3m* Requires java 18 or higher. \n" + ANSI_RESET);
    }

    /**
     * This function prints the banner of the application in the console.
     */
    public static void printBanner() {
        System.out.print("\033[H\033[2J");
        System.out.println(ANSI_PURPLE + BANNER_BORDER + ANSI_RESET);
        System.out.println(ANSI_CYAN + "  _____   _____   _____  ______  _______ __     __" + ANSI_RESET);
        System.out.println(ANSI_CYAN + " |  __ \\ |  __ \\ |_   _||  ____||__   __|\\ \\   / /" + ANSI_RESET);
        System.out.println(ANSI_CYAN + " | |  | || |__) |  | |  | |__      | |    \\ \\_/ /" + ANSI_RESET);
        System.out.println(ANSI_CYAN + " | |  | ||  _  /   | |  |  __|     | |     \\   / " + ANSI_RESET);
        System.out.println(ANSI_CYAN + " | |__| || | \\ \\  _| |_ | |        | |      | |  " + ANSI_RESET);
        System.out.println(ANSI_CYAN + " |_____/ |_|  \\_\\|_____||_|        |_|      |_|  " + ANSI_RESET);
        System.out.println(ANSI_PURPLE + BANNER_BORDER + ANSI_RESET);
    }

    /**
     * This method prints the banner without any colour of text except white.
     */
    public static void initialPrintBanner() {
        System.out.println(BANNER_BORDER);
        System.out.println("  _____   _____   _____  ______  _______ __     __");
        System.out.println(" |  __ \\ |  __ \\ |_   _||  ____||__   __|\\ \\   / /");
        System.out.println(" | |  | || |__) |  | |  | |__      | |    \\ \\_/ /");
        System.out.println(" | |  | ||  _  /   | |  |  __|     | |     \\   / ");
        System.out.println(" | |__| || | \\ \\  _| |_ | |        | |      | |  ");
        System.out.println(" |_____/ |_|  \\_\\|_____||_|        |_|      |_|  ");
        System.out.println(BANNER_BORDER);
    }

}
