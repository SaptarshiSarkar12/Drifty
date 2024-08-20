package gui.updater;

import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import properties.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            // Check if the application runner user has adequate permissions to replace the current executable
            try {
                String windowsDriveLetter = currentExecutablePathString.substring(0, 2);
                M.msgLogInfo("Drive letter: " + windowsDriveLetter);
                Path testFilePath = Files.createFile(Paths.get(windowsDriveLetter).resolve("Windows").resolve("System32").resolve("test.txt"));
                Files.deleteIfExists(testFilePath);
            } catch (IOException e) {
                M.msgUpdateError("Insufficient permissions to replace the current version of Drifty!");
                M.msgLogInfo("Please run Drifty as an administrator to update the application." + e.getMessage());
                return false;
            }
            boolean isCurrentExecutableRenamed = currentExecutableFile.renameTo(Paths.get(currentExecutableFile.getParent()).resolve(currentExecutableFile.getName() + ".old").toFile());
            if (!isCurrentExecutableRenamed) {
                M.msgUpdateError("Failed to rename the current version of Drifty!");
                return false;
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
