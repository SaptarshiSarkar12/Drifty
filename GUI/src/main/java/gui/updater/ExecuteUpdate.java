package gui.updater;

import gui.init.Environment;
import gui.utils.MessageBroker;
import main.Drifty_GUI;
import properties.OS;
import ui.ConfirmationDialog;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        if (!OS.isMac()) {
            boolean isExecutablePermissionGranted = latestExecutable.setExecutable(true);
            if (!isExecutablePermissionGranted) {
                M.msgUpdateError("Failed to set executable permission for the latest version of Drifty!");
                new ConfirmationDialog("Update Failed", "Failed to set executable permission for the latest version of Drifty!", true, true).getResponse();
                return false;
            }
        }
        boolean isWritePermissionGranted = latestExecutable.setWritable(true);
        if (!isWritePermissionGranted) {
            M.msgUpdateError("Failed to set write permission for the latest version of Drifty!");
            new ConfirmationDialog("Update Failed", "Failed to set write permission for the latest version of Drifty!", true, true).getResponse();
            return false;
        }
        boolean isReadPermissionGranted = latestExecutable.setReadable(true);
        if (!isReadPermissionGranted) {
            M.msgUpdateError("Failed to set read permission for the latest version of Drifty!");
            new ConfirmationDialog("Update Failed", "Failed to set read permission for the latest version of Drifty!", true, true).getResponse();
            return false;
        }
        return true;
    }

    public void executeUpdate() throws IOException {
        String currentExecutablePathString = currentExecutable.toPath().toString();
        boolean isCurrentExecutableRenamed = currentExecutable.renameTo(new File(currentExecutable.getName() + ".old"));
        if (!isCurrentExecutableRenamed) {
            M.msgUpdateError("Failed to replace the current version of Drifty!");
            new ConfirmationDialog("Update Failed", "Failed to replace the current version of Drifty!", true, true).getResponse();
            return;
        }
        Files.move(latestExecutable.toPath(), Paths.get(currentExecutablePathString), StandardCopyOption.REPLACE_EXISTING);
        M.msgUpdateInfo("Update successful!");
        ProcessBuilder processBuilder = new ProcessBuilder(Paths.get(URLDecoder.decode(Drifty_GUI.class.getProtectionDomain().getCodeSource().getLocation().getPath(), StandardCharsets.UTF_8)).toAbsolutePath().toString());
        processBuilder.start();
        new ConfirmationDialog("Update Successful", "Update was successfully installed!" + System.lineSeparator().repeat(2) + "Restarting Drifty...").getResponse();
        Files.deleteIfExists(Paths.get(currentExecutablePathString + ".old"));
    }
}
