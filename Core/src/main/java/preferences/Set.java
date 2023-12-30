package preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.hildan.fxgson.FxGson;
import properties.Program;
import support.JobHistory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import static preferences.Labels.*;
import static properties.Program.JOB_HISTORY_FILE;

public class Set {
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = Labels.PREFERENCES;

    public Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void lastYtDlpUpdateTime(long value) {
        AppSettings.CLEAR.lastYtDlpUpdateTime();
        preferences.putLong(LAST_YT_DLP_UPDATE_TIME, value);
    }

    public void lastFolder(String lastFolderPath) {
        AppSettings.CLEAR.lastFolder();
        preferences.put(LAST_FOLDER, lastFolderPath);
    }

    public void jobHistory(JobHistory jobHistory) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String value = gson.toJson(jobHistory);
        Path jobHistoryFile = Paths.get(Program.get(JOB_HISTORY_FILE));
        try {
            FileUtils.writeStringToFile(jobHistoryFile.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ytDlpVersion(String version) {
        AppSettings.CLEAR.ytDlpVersion();
        preferences.put(YT_DLP_VERSION, version);
    }

    public void spotDLVersion(String version) {
        AppSettings.CLEAR.spotDLVersion();
        preferences.put(SPOTDL_VERSION, version);
    }

    public void ytDlpUpdating(boolean isInitializing) {
        AppSettings.CLEAR.ytDlpUpdating();
        preferences.putBoolean(YT_DLP_UPDATING, isInitializing);
    }
}
