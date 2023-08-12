package Backend;

import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Program;
import Utils.MessageBroker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class CopyYtDlp {
    static MessageBroker messageBroker = Drifty.getMessageBrokerInstance();

    public boolean copyToTemp(InputStream inputStream) throws IOException{
        String yt_dlpFileName = Program.get(Program.NAME);
        Path yt_dlpTempFilePath = Paths.get(Program.get(Program.PATH) + yt_dlpFileName);
        try (InputStream stream = inputStream) {
            // convert stream to file
            Files.copy(stream, yt_dlpTempFilePath);
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