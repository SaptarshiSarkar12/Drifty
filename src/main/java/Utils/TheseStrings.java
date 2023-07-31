package Utils;

/**
 * This class is only used for code aesthetics, making the code easier to read and write
 * when comparing two String objects where a null pointer exception cannot be thrown.
 */

public class TheseStrings {
    public static boolean areEqual(Object obj1, Object obj2) {
        return (obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2));
    }
}
