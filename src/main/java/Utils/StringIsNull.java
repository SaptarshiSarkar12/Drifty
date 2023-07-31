package Utils;

/**
 * This class is only used for code aesthetics, making the code easier to read and write
 * when needing to replace a mull String with a populated String.
 */

public class StringIsNull {
    public static String replace(String original, String defaultValue) {
        return (original != null) ? original : defaultValue;
    }
}
