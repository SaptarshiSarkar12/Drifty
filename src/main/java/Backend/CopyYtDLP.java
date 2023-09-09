package Backend;

import Enums.MessageCategory;
import Enums.MessageType;
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

public class CopyYtDLP {
    static final MessageBroker messageBroker = Environment.getMessageBroker();

    public boolean copyYtDLP(InputStream inputStream) throws IOException {
        Path ytDLPPath = Program.getYtDLPFullPath();
        if (!Files.exists(ytDLPPath)) {
            if(!ytDLPPath.toFile().getParentFile().exists()) {
                FileUtils.createParentDirectories(ytDLPPath.toFile());
            }
            try (OutputStream outputStream = Files.newOutputStream(ytDLPPath)) {
                if (inputStream != null) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                if (!Files.isExecutable(ytDLPPath)) {
                    ProcessBuilder makeExecutable = new ProcessBuilder("chmod", "+x", ytDLPPath.toString());
                    makeExecutable.inheritIO();
                    Process yt_dlp = makeExecutable.start();
                    yt_dlp.waitFor();
                }
            } catch (FileAlreadyExistsException e) {
                messageBroker.sendMessage("yt-dlp not copied to " + Program.get(Program.DRIFTY_PATH) + " because it already exists!", MessageType.WARN, MessageCategory.LOG);
            } catch (InterruptedException e) {
                messageBroker.sendMessage("Failed to make yt-dlp executable: " + e.getMessage(), MessageType.WARN, MessageCategory.LOG);
            } catch (IOException e) {
                messageBroker.sendMessage("Failed to copy yt-dlp executable: " + e.getMessage(), MessageType.ERROR, MessageCategory.INITIALIZATION);
            }
        }
        return Files.exists(ytDLPPath);
    }
}