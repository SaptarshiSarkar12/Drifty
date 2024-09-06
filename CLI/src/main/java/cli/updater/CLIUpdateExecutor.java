package cli.updater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CLIUpdateExecutor extends updater.UpdateExecutor {
    public CLIUpdateExecutor(File currentExecutableFile, File latestExecutableFile) {
        super(currentExecutableFile, latestExecutableFile);
    }

    @Override
    public boolean execute() {
        M.msgLogInfo("Setting executable permission for the latest version of Drifty...");
        if (setLatestExecutablePermissions()) {
            M.msgLogInfo("Executing update...");
        } else {
            M.msgUpdateError("Failed to set executable permission for the latest version of Drifty!");
            return false;
        }
        try {
            Files.deleteIfExists(oldExecutableFile.toPath());
        } catch (IOException e) {
            M.msgUpdateError("Failed to delete the old version of Drifty!");
        }
        boolean isCurrentExecutableRenamed = currentExecutableFile.renameTo(oldExecutableFile);
        if (!isCurrentExecutableRenamed) {
            M.msgUpdateError("Failed to rename the current executable!");
            return false;
        }
        if (replaceCurrentExecutable()) {
            cleanup();
            return true;
        } else {
            return false;
        }
    }
}
