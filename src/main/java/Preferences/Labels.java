package Preferences;

import java.util.prefs.Preferences;

/**
 * This class contains the name of the configurations that Drifty uses throughout its whole execution
 */
public enum Labels {
    DEVMODE,
    /**
     * This is the label for the preferences related to the path of the folders where the file gets downloaded
     */
    FOLDERS,
    /**
     * This label is related to the Auto-Paste option in the <b>single file download system</b> of Drifty
     */
    MAIN_AUTO_PASTE,
    /**
     * This label is related to the Auto-Paste option in the <b>batch</b> file download system of Drifty
     */
    BATCH_AUTO_PASTE,
    /**
     * This label is for the last time when any update for yt-dlp was checked
     */
    YT_DLP_UPDATE_TIME,
    /**
     * This label is for the last download folder entered by the user
     */
    LAST_FOLDER,
    /**
     * This label is for the <b>Batch</b> download jobs
     */
    JOBS;

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