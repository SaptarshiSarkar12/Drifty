package cli.updater;

import java.io.File;

public class CLIUpdateExecutor extends updater.UpdateExecutor {
    public CLIUpdateExecutor(File currentExecutableFile, File latestExecutableFile) {
        super(currentExecutableFile, latestExecutableFile);
    }

    @Override
    public boolean execute() {
        M.msgLogInfo("Setting executable permission for the latest version of Drifty...");
        if (setLatestExecutablePermissions()) {
            M.msgLogInfo("Executable permission set! Executing update...");
        }else {
            M.msgUpdateError("Failed to set executable permission for the latest version of Drifty!");
            return false;
        }
        cleanup(true); // This will delete the old executable created previously
        if (renameCurrentExecutable()) {
            if (replaceCurrentExecutable()) {
                cleanup(false);
                return true;
            }
        }
        return false;
    }
}
