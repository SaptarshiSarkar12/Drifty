package Preferences;

import java.util.prefs.Preferences;

/**
 * This class contains the name of the configurations that Drifty uses throughout its whole execution
 */
public enum Labels {
    DEVMODE, FOLDERS, MAIN_AUTO_PASTE, BATCH_AUTO_PASTE, LAST_UPDATE_TIME, LAST_FOLDER, JOBS, START_MAX;

    /**
     * This is the object of {@link java.util.prefs.Preferences} class of java, to store the user preferences for Drifty
     */
    public static final Preferences prefs = Preferences.userNodeForPackage(Labels.class);

    /**
     * This method has overridden the default Enum class <b>toString()</b> method to return the job list filename instead of the name of the label
     * @return the name of the label [enum] in string format. For JOBS enum, it will return the job list filename.
     */
    @Override
    public String toString() {
        if (super.equals(JOBS)) {
            return "jobs.json";
        } else {
            return super.toString();
        }
    }
}
