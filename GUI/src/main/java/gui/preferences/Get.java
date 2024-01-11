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

import static gui.preferences.Labels.*;
import static properties.Program.JOB_FILE;

public class Get extends preferences.Get {
    private static final Get INSTANCE = new Get();
    private final Preferences preferences = Labels.PREFERENCES;

    static Get getInstance() {
        return INSTANCE;
    }

    public Folders folders() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        Folders folders = new Folders();
        String json = preferences.get(FOLDERS.toString(), "");
        if (!json.isEmpty()) {
            folders = gson.fromJson(json, Folders.class);
        }
        folders.checkFolders();
        return folders;
    }

    public boolean mainAutoPaste() {
        return preferences.getBoolean(MAIN_AUTO_PASTE.toString(), false);
    }

    public Jobs jobs() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        Jobs jobs;
        Path jobBatchFile = Paths.get(Program.get(JOB_FILE));
        try {
            String json = FileUtils.readFileToString(jobBatchFile.toFile(), Charset.defaultCharset());
            if (json != null && !json.isEmpty()) {
                jobs = gson.fromJson(json, Jobs.class);
                return jobs;
            }
        } catch (IOException ignored) {
        }
        return new Jobs();
    }

    public boolean alwaysAutoPaste() {
        return preferences.getBoolean(ALWAYS_AUTO_PASTE.toString(), false);
    }
}
