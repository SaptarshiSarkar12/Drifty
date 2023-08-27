package Backend;

import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Program;
import Utils.Environment;
import Utils.MessageBroker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CopyYtDlp {
    static MessageBroker messageBroker = Environment.getMessageBroker();

    public boolean copyToTemp(InputStream inputStream) throws IOException {
        String yt_dlpFileName = Program.get(Program.NAME);
        Path yt_dlpTempFilePath = Paths.get(Program.get(Program.PATH) + System.getProperty("file.separator") + yt_dlpFileName);
        try (OutputStream outputStream = Files.newOutputStream(yt_dlpTempFilePath)) {
            if (inputStream != null) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            if (!Files.isExecutable(yt_dlpTempFilePath)){
                ProcessBuilder makeExecutable = new ProcessBuilder("chmod", "+x", yt_dlpTempFilePath.toString());
                makeExecutable.inheritIO();
                Process yt_dlp = makeExecutable.start();
                yt_dlp.waitFor();
            }
        } catch (FileAlreadyExistsException e){
            messageBroker.sendMessage("Skipping copying yt-dlp to " + Program.get(Program.PATH) + " folder as it is already present!", MessageType.WARN, MessageCategory.LOG);
        } catch (InterruptedException e) {
            messageBroker.sendMessage("Failed to make executable the yt-dlp file in temporary directory. " + e.getMessage(), MessageType.WARN, MessageCategory.LOG);
        }
        return Files.exists(yt_dlpTempFilePath);
    }
}