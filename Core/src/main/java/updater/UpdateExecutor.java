package updater;

import init.Environment;
import settings.AppSettings;
import properties.OS;
import utils.MessageBroker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class UpdateExecutor {
    protected static final MessageBroker M = Environment.getMessageBroker();
    protected File oldExecutableFile;
    protected File currentExecutableFile;
    protected File latestExecutableFile;

    protected UpdateExecutor(File currentExecutableFile, File latestExecutableFile) {
        this.currentExecutableFile = currentExecutableFile;
        this.latestExecutableFile = latestExecutableFile;
        this.oldExecutableFile = Paths.get(currentExecutableFile.getParent()).resolve(currentExecutableFile.getName() + ".old").toFile();
    }

    protected boolean setLatestExecutablePermissions() {
        return setPermission("executable") && setPermission("writeable") && setPermission("readable");
    }

    private boolean setPermission(String permissionType) {
        boolean granted = switch (permissionType) {
            case "executable" -> latestExecutableFile.setExecutable(true);
            case "writeable" -> latestExecutableFile.setWritable(true);
            case "readable" -> latestExecutableFile.setReadable(true);
            default -> false;
        };
        if (!granted) {
            M.msgUpdateError("Failed to set " + permissionType + " permission for the latest version of Drifty!");
        }
        return granted;
    }

    protected void cleanup(boolean deleteImmediately) {
        AppSettings.setDriftyUpdateAvailable(false); // Reset the update flag
        try {
            if (deleteImmediately || !OS.isWindows()) {
                Files.deleteIfExists(oldExecutableFile.toPath());
            }
else {
                oldExecutableFile.deleteOnExit();
            }
            if (!deleteImmediately) {
                Environment.terminate(0);
            }
        }
catch (IOException e) {
            M.msgUpdateError("Failed to delete the old version of Drifty!\n" + e.getMessage());
        }
    }

    protected boolean replaceCurrentExecutable() {
        try {
            Files.move(latestExecutableFile.toPath(), currentExecutableFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            M.msgUpdateInfo("Update successful!");
        }
catch (IOException e) {
            try {
                Files.copy(latestExecutableFile.toPath(), currentExecutableFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
catch (Exception ex) {
                M.msgUpdateError("Failed to replace the current version of Drifty!\n" + ex.getMessage());
                return false;
            }
        }
        return true;
    }

    protected boolean renameCurrentExecutable() {
        boolean isCurrentExecutableRenamed = currentExecutableFile.renameTo(oldExecutableFile);
        if (!isCurrentExecutableRenamed) {
            M.msgUpdateError("Failed to rename the current executable!");
            return false;
        }
        return true;
    }

    protected abstract boolean execute();
}
