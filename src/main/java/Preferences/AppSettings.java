package Preferences;

/**
 * This is the main class used to control all the user Preferences
 * that remains consistent and intact even after Drifty restarts.
 */
public final class AppSettings {
    public static final Get GET = Get.getInstance();
    public static final Set SET = Set.getInstance();
    public static final Clear CLEAR = Clear.getInstance();

    private AppSettings() {
    }
}
