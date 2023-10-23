package Updater;
import java.io.IOException;
import java.nio.file.*;
import java.awt.Desktop;
public class Updater {


    public static void main(String[] args) {
        String oldFilePath = "";
        String newFilePath = "";

        replaceUpdate(oldFilePath , newFilePath);
    }

    public static void replaceUpdate(String oldFilePath ,String newFilePath){
        try {
            //Replacing new files
            Path oldPath = Path.of(oldFilePath);
            Path newPath = Path.of(newFilePath);
            Path copy = Files.copy(newPath, oldPath, StandardCopyOption.REPLACE_EXISTING);

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    // Open the new file with the default application
                    desktop.open(oldPath.toFile());
                } else {
                    System.out.println("Desktop doesn't support OPEN action.");
                }
            } else {
                System.out.println("Desktop is not supported.");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}





