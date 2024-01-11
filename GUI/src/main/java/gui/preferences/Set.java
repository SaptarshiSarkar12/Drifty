package gui.preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gui.support.Folders;
import gui.support.Jobs;
import org.apache.commons.io.FileUtils;
import org.hildan.fxgson.FxGson;
import properties.Program;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import static gui.preferences.Labels.FOLDERS;
import static gui.preferences.Labels.MAIN_AUTO_PASTE;
import static properties.Program.JOB_FILE;

public final class Set extends preferences.Set {
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = Labels.PREFERENCES;

    private Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void folders(Folders folders) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String value = gson.toJson(folders);
        AppSettings.CLEAR.folders();
        preferences.put(FOLDERS.toString(), value);
    }

    public void mainAutoPaste(boolean isMainAutoPasteEnabled) {
        AppSettings.CLEAR.mainAutoPaste();
        preferences.putBoolean(MAIN_AUTO_PASTE.toString(), isMainAutoPasteEnabled);
    }

    public void jobs(Jobs jobs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String value = gson.toJson(jobs);
        AppSettings.CLEAR.jobs();
        Path jobBatchFile = Paths.get(Program.get(JOB_FILE));
        try {
            FileUtils.writeStringToFile(jobBatchFile.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
