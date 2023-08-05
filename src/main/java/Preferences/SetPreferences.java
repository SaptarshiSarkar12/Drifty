package Preferences;

import Enums.DriftyConfig;
import GUI.Support.Folders;
import GUI.Support.Jobs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import static Preferences.Labels.*;

/**
 * This class is used to set the user preferences for Drifty
 */
public class SetPreferences {
    /**
     * The instance / object of this class through which the preferences are set
     */
    private static final SetPreferences INSTANCE = new SetPreferences();
    /**
     * The preferences object to store the preferences' data
     */
    private final Preferences prefs = Labels.prefs;

    /**
     * The default constructor has been made private so that no object of this class can be created, by external classes
     */
    private SetPreferences() {}

    /**
     * This method is used to return the object of this class
     * @return the object of this class
     */
    protected static SetPreferences getInstance() {
        return INSTANCE;
    }

    public void devMode(boolean value) {
        AppSettings.clear.devMode();
        prefs.putBoolean(DEVMODE.toString(), value);
    }

    /**
     * This method is used to set the folders where the files will be downloaded
     * @param folders An object of type {@link Folders} containing the list of folders
     */
    public void setFolders(Folders folders) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String value = gson.toJson(folders);
        AppSettings.clear.folders();
        prefs.put(FOLDERS.toString(), value);
    }

    /**
     * This method is used to set if Auto-Paste is enabled for <b>Single File download system</b> of Drifty
     * @param isMainAutoPasteEnabled boolean value regarding the auto-paste option
     */
    public void setIsMainAutoPasteEnabled(boolean isMainAutoPasteEnabled) {
        AppSettings.clear.mainAutoPaste();
        prefs.putBoolean(MAIN_AUTO_PASTE.toString(), isMainAutoPasteEnabled);
    }

    /**
     * This method is used to set if Auto-Paste is enabled for <b>Batch download system</b> of Drifty
     * @param isBatchAutoPasteEnabled boolean value regarding the auto-paste option
     */
    public void setIsBatchAutoPasteEnabled(boolean isBatchAutoPasteEnabled) {
        AppSettings.clear.batchAutoPaste();
        prefs.putBoolean(BATCH_AUTO_PASTE.toString(), isBatchAutoPasteEnabled);
    }

    /**
     * This method is used to set the last time when the yt-dlp program was checked for any update
     * @param lastYt_DlpUpdateTime The last time in milliseconds, when yt-dlp was checked for any update
     */
    public void setLastYt_DlpUpdateTime(long lastYt_DlpUpdateTime) {
        AppSettings.clear.updateTimestamp();
        prefs.putLong(LAST_UPDATE_TIME.toString(), lastYt_DlpUpdateTime);
    }

    /**
     * This method is used to set the last folder path provided by the user
     * @param lastFolderPath The path of the last folder provided to Drifty, in string format
     */
    public void setLastFolder(String lastFolderPath) {
        AppSettings.clear.lastFolder();
        prefs.put(LAST_FOLDER.toString(), lastFolderPath);
    }

    /**
     * This method is used to set the new data required for batch downloading jobs in Drifty
     * @param jobs A collection of all the required links, file name and directory
     */
    public void setBatchDownloadJobs(Jobs jobs) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String value = gson.toJson(jobs);
        AppSettings.clear.jobs();
        Path batchPath = Paths.get(DriftyConfig.getConfig(DriftyConfig.BATCH_PATH), JOBS.toString());
        try {
            FileUtils.writeStringToFile(batchPath.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startMax(boolean value) {
        AppSettings.clear.startMax();
        prefs.putBoolean(START_MAX.toString(), value);
    }
}
