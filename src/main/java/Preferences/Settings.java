package Preferences;

/**
 * This is main class that is used to control all the user Preferences that remains consistent and intact even after Drifty reloads.
 */
public class Settings {
    /**
     * This is the instance of the Get Preferences class.
     * It is used to get the preferences for Drifty
     */
    public static final GetPreferences GET_PREFERENCES = GetPreferences.getInstance();
    /**
     * This is the instance of the Set Preferences class.
     * It is used to set the preferences according to the user.
     */
    public static final SetPreferences SET_PREFERENCES = SetPreferences.getInstance();
    /**
     * This is the instance of the Get Preferences class.
     * It is used to clear the preferences (either all at once, or only the selected preferences) for Drifty
     */
    public static final ClearPreferences CLEAR_PREFERENCES = ClearPreferences.INSTANCE;
}
