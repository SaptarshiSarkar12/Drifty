package gui.updater;

import gui.init.Environment;
import preferences.AppSettings;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import properties.OS;

import java.io.File;
import java.io.IOException;

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
            return executeMacUpdate();
        } else {
            ProcessBuilder runCurrentExecutable = new ProcessBuilder(currentExecutableFile.getAbsolutePath());
            cleanup(true); // This will delete the old executable created previously
            if (renameCurrentExecutable()) {
                if (replaceCurrentExecutable()) {
                    try {
                        runCurrentExecutable.start();
                    } catch (IOException e) {
                        M.msgUpdateError("Failed to start the latest version of Drifty!");
                    }
                    cleanup(false);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean executeMacUpdate() {
        ProcResult executionResult = new ProcBuilder("open").withArgs(latestExecutableFile.getAbsolutePath()).withNoTimeout().ignoreExitStatus().run();
        if (executionResult.getExitValue() != 0) {
            M.msgUpdateError("Failed to open the installer for the latest version of Drifty! Error code: " + executionResult.getExitValue());
            return false;
        } else {
            AppSettings.SET.setDriftyUpdateAvailable(false); // Reset the update flag
            Environment.terminate(0);
        }
        return true;
    }
}
