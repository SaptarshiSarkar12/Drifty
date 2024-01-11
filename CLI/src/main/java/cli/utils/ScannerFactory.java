package cli.utils;

import java.util.Scanner;

public final class ScannerFactory {
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
