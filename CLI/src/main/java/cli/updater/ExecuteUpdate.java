package cli.updater;

import cli.init.Environment;
import cli.utils.MessageBroker;
import properties.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ExecuteUpdate {
    private static final MessageBroker M = Environment.getMessageBroker();
    private final File currentExecutableFile;
    private final File latestExecutableFile;

    public ExecuteUpdate(File currentExecutableFile, File latestExecutableFile) {
        this.currentExecutableFile = currentExecutableFile;
        this.latestExecutableFile = latestExecutableFile;
    }

    public boolean setExecutablePermission() {
        boolean isExecutablePermissionGranted = latestExecutableFile.setExecutable(true);
        if (!isExecutablePermissionGranted) {
            M.msgUpdateError("Failed to set executable permission for the latest executable!");
            return false;
        }
        boolean isWritablePermissionGranted = latestExecutableFile.setWritable(true);
        if (!isWritablePermissionGranted) {
            M.msgUpdateError("Failed to set write permission for the latest executable!");
            return false;
        }
        boolean isReadablePermissionGranted = latestExecutableFile.setReadable(true);
        if (!isReadablePermissionGranted) {
            M.msgUpdateError("Failed to set read permission for the latest executable!");
            return false;
        }
        return true;
    }

    public boolean executeUpdate() {
        String currentExecutablePath = currentExecutableFile.toPath().toString();
        boolean isCurrentExecutableRenamed = currentExecutableFile.renameTo(Paths.get(currentExecutableFile.getParent()).resolve(currentExecutableFile.getName() + ".old").toFile());
        if (!isCurrentExecutableRenamed) {
            M.msgUpdateError("Failed to rename the current executable!");
            return false;
        }
        try {
            Files.move(latestExecutableFile.toPath(), Paths.get(currentExecutablePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            M.msgUpdateError("Failed to replace the current executable with the latest version!");
            return false;
        }
        if (!OS.isWindows()) {
            try {
                Files.deleteIfExists(Paths.get(currentExecutablePath + ".old"));
            } catch (IOException e) {
                M.msgUpdateError("Failed to delete the old version of Drifty!");
                return false;
            }
        }
        return true;
    }
}
