package Enums;

import java.nio.file.Paths;
public enum Program {
    NAME, // Name of the yt-dlp program
    PATH, // Path to the temp folder
    COMMAND, // command to call yt-dlp, the path along with the name of the yt-dlp program
    BATCH_PATH;

    private static String yt_dlpProgramName;
    private static String tempFolderPath;
    private static String batchPath;

    public static void setName(String name) {
        Program.yt_dlpProgramName = name;
    }

    public static void setPath(String path) {
        Program.tempFolderPath = path;
    }

    public static void setBatchPath(String path) {
        Program.batchPath = path;
    }

    public static String get(Program program) {
        return switch (program) {
            case NAME -> yt_dlpProgramName;
            case PATH -> tempFolderPath;
            case COMMAND -> Paths.get(tempFolderPath, yt_dlpProgramName).toAbsolutePath().toString();
            case BATCH_PATH -> batchPath;
        };
    }
}
