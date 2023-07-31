package Preferences;

import java.util.prefs.Preferences;

public enum LABEL {

    DEVMODE,
    FOLDERS,
    MAIN_AUTO_PASTE,
    BATCH_AUTO_PASTE,
    UPDATE_TIMESTAMP,
    LAST_FOLDER,
    JOBS;

    public String Name(LABEL this) {
        return switch (this) {
            case DEVMODE -> "DEVMODE";
            case FOLDERS -> "FOLDERS";
            case MAIN_AUTO_PASTE -> "MAIN_AUTO_PASTE";
            case BATCH_AUTO_PASTE -> "BATCH_AUTO_PASTE";
            case UPDATE_TIMESTAMP -> "UPDATE_TIMESTAMP";
            case LAST_FOLDER -> "LAST_FOLDER";
            case JOBS -> "JOBS";
        };

    }

    public static final Preferences prefs = Preferences.userNodeForPackage(LABEL.class);

}
