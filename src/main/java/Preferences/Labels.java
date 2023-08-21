package Preferences;

import java.util.prefs.Preferences;

enum Labels {
    DEVMODE, FOLDERS, MAIN_AUTO_PASTE, BATCH_AUTO_PASTE, LAST_DLP_UPDATE_TIME, LAST_FOLDER, JOBS, START_MAX, START_TIME, JOB_HISTORY;
    public static final Preferences PREFERENCES = Preferences.userNodeForPackage(Labels.class);

    @Override
    public String toString() {
        if (super.equals(JOBS)) {
            return "jobs.json";
        }
        else if (super.equals(JOB_HISTORY)){
            return "jobHistory.json";
        }
        else {
            return super.toString();
        }
    }
}
