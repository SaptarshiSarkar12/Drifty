package Utils;

import Enums.MessageCategory;
import Enums.MessageType;
import org.apache.commons.io.FilenameUtils;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to walk through the directories that the user added as download
 * folders and will provide a list of files found in the directory and its
 * subdirectories, which is used to look for duplicate files.
 */
public class CheckFile implements Runnable {
    MessageBroker messageBroker = new MessageBroker();
    private final List<String> searchList;
    private final Path rootPath;
    public static boolean stopWalk = false;
    private FolderWalker folderWalker;

    public CheckFile(String rootPath, List<String> searchList) {
        this.rootPath = Paths.get(rootPath);
        this.searchList = searchList;
    }

     public CheckFile(String rootPath, String searchFile) {
        this.rootPath = Paths.get(rootPath);
        this.searchList = new ArrayList<>();
        this.searchList.add(searchFile);
    }

     public static void setStopWalk() {
        stopWalk = true;
    }

     public LinkedList<String> getFileList() {
        return folderWalker.fileList;
    }

     @Override
    public void run() {
        try {
            folderWalker = new FolderWalker(searchList);
            Files.walkFileTree(rootPath, folderWalker);
        } catch (IOException e) {
            messageBroker.sendMessage("Failed to walk through folders! " + e.getMessage(), MessageType.ERROR, MessageCategory.LOG);
        }
     }

    public static class FolderWalker extends SimpleFileVisitor<Path> {
        private final LinkedList<String> fileList = new LinkedList<>();
        private final List<String> fileNameSearch;
        public FolderWalker(List<String> fileNameSearch) {
            this.fileNameSearch = fileNameSearch;
        }

         @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
            if (stopWalk) return FileVisitResult.TERMINATE;
            return FileVisitResult.CONTINUE;
        }

         @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
            if (stopWalk) return FileVisitResult.TERMINATE;
            if (attrs.isRegularFile()) {
                String filename = FilenameUtils.getName(path.toAbsolutePath().toString());
                String fullPath = path.toAbsolutePath().toString();
                if (fileNameSearch.contains(filename)) {
                    if (!fileList.contains(fullPath)) {
                        fileList.addLast(fullPath);
                    }
                }
                if (fileList.size() >= fileNameSearch.size()) {
                    return FileVisitResult.TERMINATE;
                }
            }
            return FileVisitResult.CONTINUE;
        }

         @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            if (stopWalk) return FileVisitResult.TERMINATE;
            return FileVisitResult.CONTINUE;
        }

         @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) {
            if (stopWalk) return FileVisitResult.TERMINATE;
            return FileVisitResult.CONTINUE;
        }

    }
}
