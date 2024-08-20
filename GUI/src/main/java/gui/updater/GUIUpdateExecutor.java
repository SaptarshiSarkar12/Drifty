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
            // Check if the application runner user has adequate permissions to replace the current executable
            if (Paths.get(currentExecutablePathString).getParent().toFile().canWrite()) {
                M.msgLogInfo("User has write permissions to the current executable directory!");
                ConfirmationDialog confirmationDialog = new ConfirmationDialog("User Permissions", "User has write permissions to the current executable directory!\n\nDo you want to replace the current version of Drifty with the latest version?", true, false);
                if (confirmationDialog.getResponse().isYes()) {
                    M.msgLogInfo("User has confirmed to replace the current version of Drifty with the latest version!");
                } else {
                    M.msgLogInfo("User has denied to replace the current version of Drifty with the latest version!");
                    return false;
                }
            } else {
                M.msgUpdateError("User does not have write permissions to the current executable directory!");
                ConfirmationDialog confirmationDialog = new ConfirmationDialog("User Permissions", "User does not have write permissions to the current executable directory!\n\nDo you want to run the latest version of Drifty without replacing the current version?", true, false);
                confirmationDialog.getResponse().isYes();
            }
            try {
                Files.move(latestExecutableFile.toPath(), Paths.get(currentExecutablePathString), StandardCopyOption.REPLACE_EXISTING);
                M.msgUpdateInfo("Update successful!");
            } catch (IOException e) {
                M.msgUpdateError("Failed to replace the current version of Drifty!\n" + e.getMessage());
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
