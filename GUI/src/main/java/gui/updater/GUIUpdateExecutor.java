package gui.updater;

import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import properties.OS;
import ui.ConfirmationDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class GUIUpdateExecutor extends updater.UpdateExecutor {

    public GUIUpdateExecutor(File currentExecutableFile, File latestExecutableFile) {
        super(currentExecutableFile, latestExecutableFile);
    }

    @Override
    public boolean execute() {
        M.msgLogInfo("Download successful! Setting executable permission...");
        if (OS.isWindows()) { // Added logging for debugging purposes
            // Check if we have administrative privileges in windows
            ConfirmationDialog confirmationDialog = new ConfirmationDialog("Drifty Update", "canWrite: " + currentExecutableFile.canWrite() + "\n" + "canExecute: " + currentExecutableFile.canExecute() + "\n" + "canRead: " + currentExecutableFile.canRead() + "\n" + "Latest Executable - canWrite: " + latestExecutableFile.canWrite() + "\n" + "Latest Executable - canExecute: " + latestExecutableFile.canExecute() + "\n" + "Latest Executable - canRead: " + latestExecutableFile.canRead() + "\n" + "Do you want to continue?");
            if (confirmationDialog.getResponse().isYes()) {
                M.msgLogInfo("User confirmed to continue with the update...");
            } else {
                M.msgLogInfo("User cancelled the update...");
                return false;
            }
        }
        if (setExecutablePermissions()) {
            M.msgLogInfo("Executable permission set! Executing update...");
        } else {
            M.msgUpdateError("Failed to set executable permission for the latest version of Drifty!");
            return false;
        }
        if (OS.isMac()) {
            ProcResult executionResult = new ProcBuilder("open").withArgs(latestExecutableFile.getAbsolutePath()).withNoTimeout().ignoreExitStatus().run();
            if (executionResult.getExitValue() != 0) {
                M.msgUpdateError("Failed to open the installer for the latest version of Drifty!");
            } else {
                System.exit(0);
            }
        } else {
            String currentExecutablePathString = currentExecutableFile.toPath().toString();
            ProcessBuilder runCurrentExecutable = new ProcessBuilder(currentExecutableFile.getAbsolutePath());
            boolean isCurrentExecutableRenamed = currentExecutableFile.renameTo(Paths.get(currentExecutableFile.getParent()).resolve(currentExecutableFile.getName() + ".old").toFile());
            if (!isCurrentExecutableRenamed) {
                M.msgUpdateError("Failed to rename the current version of Drifty!");
                return false;
            }
            try {
                Files.move(latestExecutableFile.toPath(), Paths.get(currentExecutablePathString), StandardCopyOption.REPLACE_EXISTING);
                M.msgUpdateInfo("Update successful!");
            } catch (IOException e) {
                M.msgUpdateError("Failed to replace the current version of Drifty!");
                return false;
            }
            try {
                runCurrentExecutable.start();
            } catch (IOException e) {
                M.msgUpdateError("Failed to start the latest version of Drifty!");
            }
            if (OS.isWindows()) {
                System.exit(0);
            } else {
                try {
                    Files.deleteIfExists(Paths.get(currentExecutablePathString + ".old"));
                    System.exit(0);
                } catch (IOException e) {
                    M.msgUpdateError("Failed to delete the old version of Drifty!");
                }
            }
        }
        return true;
    }
}
