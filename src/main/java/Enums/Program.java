package Enums;

import java.nio.file.Paths;

/**
 * This enum lets us find the temp path and the proper yt-dlp
 * program on startup where we set those values in this enum
 * and they remain here as a reference throughout the life
 * of the program. The COMMAND enum has the properly formatted
 * full path to the yt-dlp executable and should be used
 * whenever a Process is needed.
 */

public enum Program {
    NAME, PATH, COMMAND, BATCH_PATH;

    private static String name;
    private static String path;
    private static String batchPath;

    public static void setName(String name) {
        Program.name = name;
    }

    public static void setPath(String path) {
        Program.path = path;
    }

    public static void setBatchPath(String path) {
        Program.batchPath = path;
    }

    public static String get(Program program) {
        return switch (program) {
            case NAME -> name;
            case PATH -> path;
            case COMMAND -> Paths.get(path, name).toAbsolutePath().toString();
            case BATCH_PATH -> batchPath;
        };
    }
}
