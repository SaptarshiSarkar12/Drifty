package GUI.Support;

import Preferences.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class manages the folders that the user adds as locations to
 * download files to. it stores itself in JSON format via the
 * Preferences class through the '{@link Settings#SET_PREFERENCES}' class.
 */

public class Folders {

    private final LinkedList<String> folders = new LinkedList<>();

    public void addFolder(String folder) {
        folders.remove(folder);
        folders.addLast(folder);
        Settings.SET_PREFERENCES.setFolders(this);
        Settings.SET_PREFERENCES.setLastFolder(folder);
    }

    public void removeFolder(String folder) {
        folders.remove(folder);
        Settings.SET_PREFERENCES.setFolders(this);
    }

    public String getDownloadFolder() {
        return Settings.GET_PREFERENCES.getLastDownloadFolder();
    }

    public void checkFolders() {
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
        Settings.SET_PREFERENCES.setFolders(this);
    }

    public ObservableList<String> getFolders() {
        return FXCollections.observableArrayList(folders);
    }
}
