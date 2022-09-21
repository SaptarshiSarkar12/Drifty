package src;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

class CheckDirectory {
    CheckDirectory(String dir) throws IOException {
        if (!(checkIfFolderExists(dir))){
            Path directory = FileSystems.getDefault().getPath(dir);
            Files.createDirectory(directory);
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
            System.out.println(Drifty_CLI.COLOR_RED + "Error while checking for directory !" + Drifty_CLI.COLOR_RESET);
        }
        return found;
    }
}
