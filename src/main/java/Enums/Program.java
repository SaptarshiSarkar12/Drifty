package Enums;

import java.nio.file.Paths;
public enum Program {
    NAME,
    PATH,
    COMMAND,
    BATCH_PATH;

    private static String yt_dlpProgramName;
    private static String yt_dlpProgramPath;
    private static String batchPath;

    public static void setName(String name) {
        Program.yt_dlpProgramName = name;
    }

    public static void setPath(String path) {
        Program.yt_dlpProgramPath = path;
    }

    public static void setBatchPath(String path) {
        Program.batchPath = path;
    }

    public static String get(Program program) {
        return switch (program) {
            case NAME -> yt_dlpProgramName;
            case PATH -> yt_dlpProgramPath;
            case COMMAND -> Paths.get(yt_dlpProgramPath, yt_dlpProgramName).toAbsolutePath().toString();
            case BATCH_PATH -> batchPath;
        };
    }
}
