package Backend;
import Enums.MessageCategory;
import Enums.MessageType;
import Utils.MessageBroker;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static Utils.DriftyConstants.*;
/**
 * This class checks if a directory exists or not. if it doesn't, the directory is created.
 */
class CheckDirectory {
    private static final MessageBroker message = Drifty.getMessageBrokerInstance();
    CheckDirectory(String dir) throws IOException {
        if (!(checkIfFolderExists(dir))) {
            Path directory = FileSystems.getDefault().getPath(dir);
            Files.createDirectory(directory);
            message.sendMessage(DIRECTORY_CREATED, MessageType.INFO, MessageCategory.DIRECTORY);
        } else {
            message.sendMessage("Directory is valid !", MessageType.INFO, MessageCategory.DIRECTORY);
        }
    }

    private static boolean checkIfFolderExists(String folderName) {
        boolean found = false;
        try {
            File file = new File(folderName);
            if (file.exists() && file.isDirectory()) {
                found = true;
            }
        } catch (Exception e) {
            message.sendMessage(ERROR_WHILE_CHECKING_FOR_DIRECTORY, MessageType.ERROR, MessageCategory.DIRECTORY);
        }
        return found;
    }
}
