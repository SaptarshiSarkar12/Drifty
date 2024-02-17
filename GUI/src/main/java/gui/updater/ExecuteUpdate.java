package gui.updater;

import gui.init.Environment;
import gui.utils.MessageBroker;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import properties.OS;
import ui.ConfirmationDialog;

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
            M.msgUpdateError("Failed to set executable permission for the latest version of Drifty!");
            new ConfirmationDialog("Update Failed", "Failed to set executable permission for the latest version of Drifty!", true, true).getResponse();
            return false;
        }
        boolean isWritePermissionGranted = latestExecutableFile.setWritable(true);
        if (!isWritePermissionGranted) {
            M.msgUpdateError("Failed to set write permission for the latest version of Drifty!");
            new ConfirmationDialog("Update Failed", "Failed to set write permission for the latest version of Drifty!", true, true).getResponse();
            return false;
        }
        boolean isReadPermissionGranted = latestExecutableFile.setReadable(true);
        if (!isReadPermissionGranted) {
            M.msgUpdateError("Failed to set read permission for the latest version of Drifty!");
            new ConfirmationDialog("Update Failed", "Failed to set read permission for the latest version of Drifty!", true, true).getResponse();
            return false;
        }
        return true;
    }

    public void executeUpdate() throws IOException {
        if (OS.isMac()) {
            ProcResult executionResult = new ProcBuilder("open").withArgs(latestExecutableFile.getAbsolutePath()).withNoTimeout().ignoreExitStatus().run();
            if (executionResult.getExitValue() != 0) {
                M.msgUpdateError("Failed to open the installer for the latest version of Drifty!");
                new ConfirmationDialog("Update Failed", "Failed to open the installer for the latest version of Drifty!", true, true).getResponse();
            } else {
                System.exit(0);
            }
        } else {
            String currentExecutablePathString = currentExecutableFile.toPath().toString();
            ProcessBuilder runCurrentExecutable = new ProcessBuilder(currentExecutableFile.getAbsolutePath());
            boolean isCurrentExecutableRenamed = currentExecutableFile.renameTo(new File(Paths.get(currentExecutableFile.getParent()).resolve(currentExecutableFile.getName() + ".old").toString()));
            if (!isCurrentExecutableRenamed) {
                M.msgUpdateError("Failed to replace the current version of Drifty!");
                new ConfirmationDialog("Update Failed", "Failed to replace the current version of Drifty!", true, true).getResponse();
                return;
            }
            Files.move(latestExecutableFile.toPath(), Paths.get(currentExecutablePathString), StandardCopyOption.REPLACE_EXISTING);
            M.msgUpdateInfo("Update successful!");
            runCurrentExecutable.start();
            new ConfirmationDialog("Update Successful", "Update was successfully installed!" + System.lineSeparator().repeat(2) + "Restarting Drifty...").getResponse();
            Files.deleteIfExists(Paths.get(currentExecutablePathString + ".old"));
        }
    }
}
