package Utils;

import java.util.Scanner;

public final class ScannerFactory { // This class is used to get a scanner object
    private static Scanner scanner;

    private ScannerFactory() {
    }

    public static Scanner getInstance() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }
}
