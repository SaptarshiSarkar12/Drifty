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

public class CopySpotdl {
    private static final MessageBroker M = Environment.getMessageBroker();

    public boolean copySpotdl(InputStream inputStream) throws IOException {
        Path spotdlPath = Program.getSpotdlFullPath();

        if (!Files.exists(spotdlPath)) {
            if (!spotdlPath.toFile().getParentFile().exists()) {
                FileUtils.createParentDirectories(spotdlPath.toFile());
            }
            try (OutputStream outputStream = Files.newOutputStream(spotdlPath)) {
                if (inputStream != null) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                if (!Files.isExecutable(spotdlPath)) {
                    // Adjust this command for making the file executable on your OS
                    ProcessBuilder makeExecutable = new ProcessBuilder("chmod", "+x", spotdlPath.toString());
                    makeExecutable.inheritIO();
                    Process spotdlProcess = makeExecutable.start();
                    spotdlProcess.waitFor();
                }
            } catch (FileAlreadyExistsException e) {
                M.msgLogWarning("spotdl not copied to " + Program.get(Program.DRIFTY_PATH) + " because it already exists!");
            } catch (InterruptedException e) {
                M.msgLogWarning("Failed to make spotdl executable: " + e.getMessage());
            } catch (IOException e) {
                M.msgInitError("Failed to copy spotdl executable: " + e.getMessage());
            }
        }
        return Files.exists(spotdlPath);
    }
}
