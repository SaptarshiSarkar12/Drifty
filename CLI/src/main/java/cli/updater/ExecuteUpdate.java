package cli.updater;

import cli.init.Environment;
import cli.utils.MessageBroker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ExecuteUpdate {
    private static final MessageBroker M = Environment.getMessageBroker();
    private final File currentExecutable;
    private final File latestExecutable;

    public ExecuteUpdate(File currentExecutable, File latestExecutable) {
        this.currentExecutable = currentExecutable;
        this.latestExecutable = latestExecutable;
    }

    public boolean setExecutablePermission() {
        boolean isExecutablePermissionGranted = latestExecutable.setExecutable(true);
        if (!isExecutablePermissionGranted) {
            M.msgUpdateError("Failed to set executable permission for the latest executable!");
            return false;
        }
        boolean isWritablePermissionGranted = latestExecutable.setWritable(true);
        if (!isWritablePermissionGranted) {
            M.msgUpdateError("Failed to set write permission for the latest executable!");
            return false;
        }
        boolean isReadablePermissionGranted = latestExecutable.setReadable(true);
        if (!isReadablePermissionGranted) {
            M.msgUpdateError("Failed to set read permission for the latest executable!");
            return false;
        }
        return true;
    }

    public boolean executeUpdate() throws IOException {
        String currentExecutablePath = currentExecutable.toPath().toString();
        boolean isCurrentExecutableRenamed = currentExecutable.renameTo(new File(currentExecutable.getName() + ".old"));
        if (!isCurrentExecutableRenamed) {
            M.msgUpdateError("Failed to replace current executable with the latest one!");
            return false;
        }
        Files.move(latestExecutable.toPath(), Paths.get(currentExecutablePath), StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(Paths.get(currentExecutablePath + ".old"));
        M.msgUpdateInfo("Update successful!");
        M.msgUpdateInfo("Please restart Drifty to see the changes!");
        return true;
    }
}
