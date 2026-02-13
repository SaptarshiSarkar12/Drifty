package gui.support;

import gui.preferences.AppSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Folders {
    private final LinkedList<String> folders = new LinkedList<>();

    public void addFolder(String folder) {
        folders.remove(folder);
        folders.addLast(folder);
        AppSettings.SET.folders(this);
        AppSettings.SET.setLastFolder(folder);
    }

    public void removeFolder(String folder) {
        folders.remove(folder);
        AppSettings.SET.folders(this);
    }

    public String getDownloadFolder() {
        return AppSettings.GET.getLastDownloadFolder();
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
        AppSettings.SET.folders(this);
    }

    public ObservableList<String> getFolders() {
        return FXCollections.observableArrayList(folders);
    }
}
