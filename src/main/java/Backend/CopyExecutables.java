package Backend;

import Enums.Program;
import Utils.Environment;
import Utils.MessageBroker;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CopyExecutables {
    private static final MessageBroker M = Environment.getMessageBroker();

    public final void copyExecutables(final String[] executableNames) throws IOException {
        for (String executableName : executableNames) {
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
                        ProcessBuilder makeExecutable = new ProcessBuilder("chmod", "+x", executablePath.toString());
                        makeExecutable.inheritIO();
                        Process chmod = makeExecutable.start();
                        chmod.waitFor();
                    }
                } catch (FileAlreadyExistsException e) {
                    M.msgLogWarning(executableName + " not copied to " + Program.get(Program.DRIFTY_PATH) + " because it already exists!");
                } catch (InterruptedException e) {
                    M.msgLogWarning("Failed to make " + executableName + " executable: " + e.getMessage());
                } catch (IOException e) {
                    M.msgInitError("Failed to copy " + executableName + " executable: " + e.getMessage());
                }
            }
        }
    }
}
