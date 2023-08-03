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
 * This class is used to get the user preferences for Drifty
 */
public class GetPreferences {
    /**
     * The instance / object of this class through which the preferences are retrieved
     */
    private static final GetPreferences INSTANCE = new GetPreferences();
    /**
     * This is the object of the Java Preferences class used to store preferences
     */
    private final Preferences prefs = Labels.prefs;

    /**
     * The default constructor has been made private so that no object of this class can be created, by external classes
     */
    private GetPreferences() {}

    /**
     * This method is used to return the object of this class
     * @return the object of this class
     */
    protected static GetPreferences getInstance() {
        return INSTANCE;
    }

    public boolean devMode() {
        return prefs.getBoolean(DEVMODE.toString(), false);
    }

    /**
     * This method is used to get the list of the Folders where the file will get downloaded
     * @return an object of type {@link Folders} with the list of folders in it
     */
    public Folders getFolders() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Folders folders = new Folders();
        String json = prefs.get(FOLDERS.toString(), "");
        if (!json.isEmpty()) {
            folders = gson.fromJson(json, Folders.class);
        }
        folders.checkFolders();
        return folders;
    }

    /**
     * This method is used to get the boolean value for the auto-paste option in <b>Single File Download</b> system of Drifty
     * @return true if auto-paste option is enabled for <b>Single File Download</b> system of Drifty, else false
     */
    public boolean getIsMainAutoPasteEnabled() {
        return prefs.getBoolean(MAIN_AUTO_PASTE.toString(), false);
    }

    /**
     * This method is used to get the boolean value for the auto-paste option in <b>Batch File Download</b> system of Drifty
     * @return true if auto-paste option is enabled for <b>Batch File Download</b> system of Drifty, else false
     */
    public boolean getIsBatchAutoPasteEnabled() {
        return prefs.getBoolean(BATCH_AUTO_PASTE.toString(), false);
    }

    /**
     * This method is used to get the last time in millisecond, when yt-dlp has been checked for update
     * @return the time of last yt-dlp update check in millisecond
     */
    public long getLastYt_DlpUpdateTime() {
        return prefs.getLong(YT_DLP_UPDATE_TIME.toString(), 1000L);
    }

    /**
     * This method is used to get the last download folder, provided by the user to Drifty
     * @return the last download folder entered by the user
     */
    public String getLastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"),"Downloads").toAbsolutePath().toString();
        return prefs.get(LAST_FOLDER.toString(), defaultPath);
    }

    /**
     * This method is used to get the list of jobs to be executed in the <b>batch download system of Drifty</b>
     * @return an object of type {@link Jobs} containing the list of jobs to execute
     */
    public Jobs getJobs() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Jobs jobs = new Jobs();
        Path batchPath = Paths.get(DriftyConfig.getConfig(DriftyConfig.BATCH_PATH), JOBS.toString());
        try {
            String json = FileUtils.readFileToString(batchPath.toFile(), Charset.defaultCharset());
            if (!json.isEmpty()) {
                jobs = gson.fromJson(json, Jobs.class);
            }
            return jobs;
        } catch (IOException ignored) {}
        return null;
    }
}