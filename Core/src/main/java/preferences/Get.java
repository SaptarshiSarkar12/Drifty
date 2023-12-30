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

public class Get {
    private static final Get INSTANCE = new Get();
    private final Preferences preferences = Labels.PREFERENCES;

    static Get getInstance() {
        return INSTANCE;
    }

    public long lastYtDlpUpdateTime() {
        return preferences.getLong(LAST_YT_DLP_UPDATE_TIME, 1000L);
    }

    public String lastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString();
        return preferences.get(LAST_FOLDER, defaultPath);
    }

    public JobHistory jobHistory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        JobHistory jobHistory;
        Path jobHistoryFile = Paths.get(Program.get(JOB_HISTORY_FILE));
        try {
            if (!jobHistoryFile.toFile().exists()) {
                jobHistory = new JobHistory();
                String json = gson.toJson(jobHistory);
                FileUtils.write(jobHistoryFile.toFile(), json, Charset.defaultCharset());
            }
            String json = FileUtils.readFileToString(jobHistoryFile.toFile(), Charset.defaultCharset());
            if (json != null && !json.isEmpty()) {
                jobHistory = gson.fromJson(json, JobHistory.class);
                return jobHistory;
            }
        } catch (IOException ignored) {
        }
        return new JobHistory();
    }

    public String ytDlpVersion() {
        return preferences.get(YT_DLP_VERSION, "");
    }

    public String spotDLVersion() {
        return preferences.get(SPOTDL_VERSION, "");
    }

    public boolean ytDlpUpdating() {
        return preferences.getBoolean(YT_DLP_UPDATING, false);
    }
}
