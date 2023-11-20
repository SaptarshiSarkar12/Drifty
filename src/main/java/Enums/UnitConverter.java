package Enums;

import java.text.DecimalFormat;

public enum UnitConverter {
    B, KB, MB, GB, TB;

    public static double getValue(long bytes, UnitConverter unit) {
        double temp = (double) bytes;
        return switch (unit) {
            case B -> bytes;
            case KB -> temp / 1024;
            case MB -> temp / 1024 / 1024;
            case GB -> temp / 1024 / 1024 / 1024;
            case TB -> temp / 1024 / 1024 / 1024 / 1024;
        };
    }

    private static UnitConverter findUnit(double bytes) {
        UnitConverter unit = B;
        double finalBytes = bytes;
        if (bytes > 1000) {
            unit = KB;
            finalBytes = bytes / 1024;
        }
        if (finalBytes > 1000) {
            unit = MB;
            finalBytes = bytes / 1024 / 1024;
        }
        if (finalBytes > 1000) {
            unit = GB;
            finalBytes = bytes / 1024 / 1024 / 1024;
        }
        if (finalBytes > 1000) {
            unit = TB;
        }
        return unit;
    }

    public static String format(double bytes, int decimalPlaces) {
        UnitConverter unit = findUnit(bytes);
        DecimalFormat format = new DecimalFormat(decimalPlaces == 2 ? "#.00" : "#.0");
        return switch (unit) {
            case B -> format.format(bytes) + " B";
            case KB -> format.format(bytes / 1024) + " KB";
            case MB -> format.format(bytes / 1024 / 1024) + " MB";
            case GB -> format.format(bytes / 1024 / 1024 / 1024) + " GB";
            case TB -> format.format(bytes / 1024 / 1024 / 1024 / 1024) + " TB";
        };
    }

    public static String format(long bytes, int decimalPlaces) {
        double converted = (double) bytes;
        return format(converted, decimalPlaces);
    }
}
