package gui.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import gui.init.Environment;
import settings.AppSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hildan.fxgson.FxGson;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Folders {
    private static final Gson GSON = FxGson.addFxSupport(new GsonBuilder()).setPrettyPrinting().create();
    private final LinkedList<String> folders = new LinkedList<>();

    public Folders() {
        readFolders();
        cleanupFolders();
    }

    public void addFolder(String folder) {
        folders.remove(folder);
        folders.addLast(folder);
        AppSettings.setFolders(foldersToString());
        AppSettings.setLastDownloadFolder(folder);
    }

    public void removeFolder(String folder) {
        folders.remove(folder);
        if (folder.equals(AppSettings.getLastDownloadFolder())){
            AppSettings.setLastDownloadFolder("");
        }
        AppSettings.setFolders(foldersToString());
    }

    public String getDownloadFolder() {
        return AppSettings.getLastDownloadFolder();
    }

    private void cleanupFolders() {
        List<String> removeList = new ArrayList<>();
        for (String folder : folders) {
            Path path = Paths.get(folder);
            if (!path.toFile().exists()) {
                removeList.add(folder);
            }else {
                if (!path.toFile().isDirectory()) {
                    removeList.add(folder);
                }
            }
        }
        for (String folder : removeList) {
            folders.remove(folder);
        }
        AppSettings.setFolders(foldersToString());
    }

    public ObservableList<String> getFolders() {
        return FXCollections.observableArrayList(folders);
    }

    private void readFolders() {
        folders.clear();
        String json = AppSettings.getFolders();
        if (json != null && !json.isEmpty()) {
            try {
                String[] folderArray = GSON.fromJson(json, String[].class);
                folders.addAll(Arrays.asList(folderArray));
            }catch (JsonSyntaxException e) {
                Environment.getMessageBroker().msgLogError("Syntax Error in Folders Json: " + e.getMessage());
            }
        }
    }

    private String foldersToString() {
        return GSON.toJson(folders);
    }

}
