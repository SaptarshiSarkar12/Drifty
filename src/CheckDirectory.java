import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
class CheckDirectory {
    private static final Logger dLog= LogManager.getLogger(CheckDirectory.class.getName());
    CheckDirectory(String dir) throws IOException {
        if (!(checkIfFolderExists(dir))){
            Path directory = FileSystems.getDefault().getPath(dir);
            Files.createDirectory(directory);
            dLog.info("Directory Created");
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
            System.out.println("Error while checking for directory !");
            dLog.error("Error while checking for directory !");
        }
        return found;
    }
}
