package cli.updater;

import properties.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
            return false;
        }
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
