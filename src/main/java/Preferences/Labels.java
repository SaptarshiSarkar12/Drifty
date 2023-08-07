package Preferences;

import java.util.prefs.Preferences;

enum Labels {
    DEVMODE, FOLDERS, MAIN_AUTO_PASTE, BATCH_AUTO_PASTE, LAST_DLP_UPDATE_TIME, LAST_FOLDER, JOBS, START_MAX, START_TIME;

    public static final Preferences prefs = Preferences.userNodeForPackage(Labels.class);

    @Override
    public String toString() {
        if (super.equals(JOBS)) {
            return "jobs.json";
        } else {
            return super.toString();
        }
    }
}
