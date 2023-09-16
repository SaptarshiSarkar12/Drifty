package Enums;
import java.text.DecimalFormat;

public enum Unit {

    B, KB, MB, GB, TB;

    private static final DecimalFormat two = new DecimalFormat("#.00");
    private static final DecimalFormat one = new DecimalFormat("#.0");

    private static Unit findUnit(long bytes) {
        double converted = (double) bytes;
        return findUnit(converted);
    }

    private static Unit findUnit(double bytes) {
        Unit unit = B;
        double finalBytes = bytes;
        if(bytes > 1000) {
            unit = KB;
            finalBytes = bytes / 1024;
        }
        if(finalBytes > 1000) {
            unit = MB;
            finalBytes = bytes / 1024 / 1024;
        }
        if(finalBytes > 1000) {
            unit = GB;
            finalBytes = bytes / 1024 / 1024 / 1024;
        }
        if(finalBytes > 1000) {
            unit = TB;
        }
        return unit;
    }

    public static String format(double bytes, int decimalPlaces) {
        Unit unit = findUnit(bytes);
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