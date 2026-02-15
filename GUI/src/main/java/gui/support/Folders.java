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
    private final LinkedList<String> folders = new LinkedList<>();

    public Folders() {
        readFolders();
        cleanupFolders();
    }

    public void addFolder(String folder) {
        folders.remove(folder);
        folders.addLast(folder);
        AppSettings.SET.setFolders(foldersToString());
        AppSettings.SET.setLastFolder(folder);
    }

    public void removeFolder(String folder) {
        folders.remove(folder);
        AppSettings.SET.setFolders(foldersToString());
    }

    public String getDownloadFolder() {
        return AppSettings.GET.getLastDownloadFolder();
    }

    private void cleanupFolders() {
        List<String> removeList = new ArrayList<>();
        for (String folder : folders) {
            Path path = Paths.get(folder);
            if (!path.toFile().exists()) {
                removeList.add(folder);
            } else {
                if (!path.toFile().isDirectory()) {
                    removeList.add(folder);
                }
            }
        }
        for (String folder : removeList) {
            folders.remove(folder);
        }
        AppSettings.SET.setFolders(foldersToString());
    }

    public ObservableList<String> getFolders() {
        return FXCollections.observableArrayList(folders);
    }

    private void readFolders() {
        folders.clear();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String json = AppSettings.GET.getFolders();
        if (!json.isEmpty()) {
            try {
                String[] folderArray = gson.fromJson(json, String[].class);
                folders.addAll(Arrays.asList(folderArray));
            }
            catch(JsonSyntaxException e) {
                Environment.getMessageBroker().msgLogError("Syntax Error in Folders Json: " + e.getMessage());
            }
        }
    }

    private String foldersToString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        return gson.toJson(folders);
    }

}
