package Preferences;

/**
 * This is main class that is used to control all the user Preferences that remains consistent and intact even after Drifty reloads.
 */
public class AppSettings {

    // THESE ARE NOT CONSTANTS AND SHOULD NOT BE UPPER_SNAKE_CASE. THEY ARE INSTANCE REFERENCES THEREFORE camelCaseOnly

    public static final GetPreferences get = GetPreferences.getInstance();
    public static final SetPreferences set = SetPreferences.getInstance();
    public static final ClearPreferences clear = ClearPreferences.INSTANCE;
}
