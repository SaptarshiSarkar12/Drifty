package gui.updater;

import gui.init.Environment;
import gui.preferences.AppSettings;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import properties.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GUIUpdateExecutor extends updater.UpdateExecutor {

    public GUIUpdateExecutor(File currentExecutableFile, File latestExecutableFile) {
        super(currentExecutableFile, latestExecutableFile);
    }

    @Override
    public boolean execute() {
        M.msgLogInfo("Download successful! Setting executable permission...");
        if (setLatestExecutablePermissions()) {
            M.msgLogInfo("Executable permission set! Executing update...");
        } else {
            M.msgUpdateError("Failed to set executable permission for the latest version of Drifty!");
            return false;
        }
        if (OS.isMac()) {
            ProcResult executionResult = new ProcBuilder("open").withArgs(latestExecutableFile.getAbsolutePath()).withNoTimeout().ignoreExitStatus().run();
            if (executionResult.getExitValue() != 0) {
                M.msgUpdateError("Failed to open the installer for the latest version of Drifty!");
                return false;
            } else {
                AppSettings.CLEAR.driftyUpdateAvailable(); // Reset the update flag
                Environment.terminate(0);
            }
        } else {
            ProcessBuilder runCurrentExecutable = new ProcessBuilder(currentExecutableFile.getAbsolutePath());
            try {
                Files.deleteIfExists(oldExecutableFile.toPath());
            } catch (IOException e) {
                M.msgUpdateError("Failed to delete the old version of Drifty!");
            }
            boolean isCurrentExecutableRenamed = currentExecutableFile.renameTo(oldExecutableFile);
            if (!isCurrentExecutableRenamed) {
                M.msgUpdateError("Failed to rename the current version of Drifty!");
                return false;
            }
            if (replaceCurrentExecutable()) {
                try {
                    runCurrentExecutable.start();
                } catch (IOException e) {
                    M.msgUpdateError("Failed to start the latest version of Drifty!");
                }
                cleanup();
            } else {
                return false;
            }
        }
        return true;
    }
}
