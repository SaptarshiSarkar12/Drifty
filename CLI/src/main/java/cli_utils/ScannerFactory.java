package cli_utils;

import java.util.Scanner;

public class ScannerFactory {
    private static Scanner scanner;

    private ScannerFactory() {}

    public static Scanner getInstance() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }
}
