package Preferences;
/**
 * This is the main class that is used to control all the user Preferences that remains consistent and intact even after Drifty reloads.
 */
public class AppSettings {
    // THESE ARE NOT CONSTANTS AND SHOULD NOT BE UPPER_SNAKE_CASE. THEY ARE INSTANCE REFERENCES THEREFORE camelCaseOnly
    public static final Get get = Get.getInstance();
    public static final Set set = Set.getInstance();
    public static final Clear clear = Clear.getInstance();
}
