package updater;

import init.Environment;
import utils.MessageBroker;

import java.io.File;

public abstract class UpdateExecutor {
    protected static final MessageBroker M = Environment.getMessageBroker();
    protected File currentExecutableFile;
    protected File latestExecutableFile;

    protected UpdateExecutor() {
    }

    public UpdateExecutor(File currentExecutableFile, File latestExecutableFile) {
        this.currentExecutableFile = currentExecutableFile;
        this.latestExecutableFile = latestExecutableFile;
    }

    public boolean setExecutablePermissions() {
        boolean isExecutablePermissionGranted = latestExecutableFile.setExecutable(true);
        if (!isExecutablePermissionGranted) {
            M.msgUpdateError("Failed to set executable permission for the latest version of Drifty!");
            return false;
        }
        boolean isWritePermissionGranted = latestExecutableFile.setWritable(true);
        if (!isWritePermissionGranted) {
            M.msgUpdateError("Failed to set write permission for the latest version of Drifty!");
            return false;
        }
        boolean isReadPermissionGranted = latestExecutableFile.setReadable(true);
        if (!isReadPermissionGranted) {
            M.msgUpdateError("Failed to set read permission for the latest version of Drifty!");
            return false;
        }
        return true;
    }

    public abstract boolean execute();
}
