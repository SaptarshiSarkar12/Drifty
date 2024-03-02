package utils;

import init.Environment;
import org.apache.commons.io.FileUtils;
import properties.Program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CopyExecutables {
    private static final MessageBroker M = Environment.getMessageBroker();
    private final String[] executableNames;

    public CopyExecutables(final String[] executableNames) {
        this.executableNames = executableNames;
    }

    public final void start() throws IOException {
        for (String executableName : executableNames) {
            if (executableName == null || executableName.isEmpty()) {
                M.msgLogError("Executable name is null or empty!");
                continue;
            }
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(executableName);
            Path executablePath = Program.getExecutablesPath(executableName);
            if (!Files.exists(executablePath)) {
                if (!executablePath.toFile().getParentFile().exists()) {
                    FileUtils.createParentDirectories(executablePath.toFile());
                }
                try (OutputStream outputStream = Files.newOutputStream(executablePath)) {
                    if (inputStream != null) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    if (!Files.isExecutable(executablePath)) {
                        if (executablePath.toFile().setExecutable(true)) {
                            M.msgLogInfo(executableName + " is now executable!");
                        } else {
                            M.msgLogError("Failed to make " + executableName + " executable!");
                        }
                    }
                    if (executableName.startsWith("ffmpeg")) {
                        new Thread(Utility::setFfmpegVersion).start();
                    }
                } catch (FileAlreadyExistsException e) {
                    M.msgLogWarning(executableName + " not copied to " + Program.get(Program.DRIFTY_PATH) + " because it already exists!");
                } catch (IOException e) {
                    M.msgInitError("Failed to copy " + executableName + " executable: " + e.getMessage());
                }
            }
        }
    }
}
