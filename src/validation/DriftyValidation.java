package validation;

import singleton.CreateLogs;

import static constants.DriftyConstants.*;

import java.util.Scanner;


public final class DriftyValidation {

    public static final CreateLogs logger = CreateLogs.getInstance();
    private static Scanner SC = new Scanner(System.in);

    /**
     * This method performs Yes-No validation and returns the boolean value accordingly.
     *
     * @param input        Input String to validate.
     * @param printMessage The message to print to re-input the confirmation.
     * @return true if the user enters Y [Yes] and false if not.
     */
    public static boolean yesNoValidation(String input, String printMessage) {

        while (true) {
            if (input.length() == 0) {
                System.out.println(ENTER_Y_OR_N);
                logger.log(LOGGER_ERROR, ENTER_Y_OR_N);
            } else {
                break;
            }
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
        }
        char choice = input.charAt(0);
        if (choice == 'y') {
            return true;
        } else if (choice == 'n') {
            return false;
        } else {
            System.out.println("Invalid input!");
            logger.log("ERROR", "Invalid input");
            System.out.print(printMessage);
            input = SC.nextLine().toLowerCase();
            yesNoValidation(input, printMessage);
        }
        return false;
    }
}
