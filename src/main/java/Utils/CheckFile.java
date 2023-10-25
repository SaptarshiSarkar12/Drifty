package Utils;

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
    private static final MessageBroker M = Environment.getMessageBroker();
    public static boolean stopWalk = false;
    private final List<String> searchList;
    private final Path rootPath;
    private final boolean findOneFile;
    private FolderWalker folderWalker;

    public CheckFile(String rootPath, String searchFile) {
        this.rootPath = Paths.get(rootPath);
        this.searchList = new ArrayList<>();
        this.searchList.add(searchFile);
        this.findOneFile = true;
    }

    public boolean fileFound() {
        return folderWalker.fileFound();
    }

    @Override
    public void run() {
        try {
            folderWalker = new FolderWalker(searchList, findOneFile);
            Files.walkFileTree(rootPath, folderWalker);
        } catch (IOException e) {
            M.msgLogError("Failed to walk through folders! " + e.getMessage());
        }
    }

    public static class FolderWalker extends SimpleFileVisitor<Path> {
        private final LinkedList<String> fileList = new LinkedList<>();
        private final List<String> fileNameSearch;
        private final boolean findOneFile;
        private boolean fileFound = false;

        public FolderWalker(List<String> fileNameSearch, boolean findOneFile) {
            this.findOneFile = findOneFile;
            this.fileNameSearch = fileNameSearch;
        }

        public boolean fileFound() {
            return fileFound;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
            if (stopWalk) {
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
            if (stopWalk) {
                return FileVisitResult.TERMINATE;
            }
            if (attrs.isRegularFile()) {
                String filename = FilenameUtils.getName(path.toAbsolutePath().toString());
                String fullPath = path.toAbsolutePath().toString();
                if (fileNameSearch.contains(filename)) {
                    if (findOneFile) {
                        fileFound = true;
                        return FileVisitResult.TERMINATE;
                    } else {
                        if (!fileList.contains(fullPath)) {
                            fileList.addLast(fullPath);
                        }
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
            if (stopWalk) {
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) {
            if (stopWalk) {
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

    }
}
