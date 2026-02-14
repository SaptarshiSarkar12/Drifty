package gui.preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gui.support.Folders;
import org.hildan.fxgson.FxGson;

import java.util.prefs.Preferences;

import static gui.preferences.Labels.*;

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
        return preferences.getBoolean(GUI_AUTO_PASTE.toString(), false);
    }

    public String mainTheme() {
        return preferences.get(GUI_THEME.toString(), "Light");
    }

    public boolean alwaysAutoPaste() {
        return preferences.getBoolean(ALWAYS_AUTO_PASTE.toString(), false);
    }
}
