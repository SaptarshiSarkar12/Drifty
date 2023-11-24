package Enums;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum Program {
    YT_DLP_EXECUTABLE_NAME, SPOTDL_EXECUTABLE_NAME, YT_DLP, DRIFTY_PATH, JOB_HISTORY_FILE, JOB_FILE, SPOTDL;

    private static String ytDLP;
    private static String driftyPath;
    private static String spotdl;

    public static void setYtDlpExecutableName(String name) {
        Program.ytDLP = name;
    }

    public static void setSpotdlExecutableName(String name) {
        Program.spotdl = name;
    }

    public static void setDriftyPath(String path) {
        Program.driftyPath = path;
    }

    public static String get(Program program) {
        return switch (program) {
            case YT_DLP_EXECUTABLE_NAME -> ytDLP;
            case SPOTDL_EXECUTABLE_NAME -> spotdl;
            case DRIFTY_PATH -> driftyPath;
            case YT_DLP -> Paths.get(driftyPath, ytDLP).toAbsolutePath().toString();
            case SPOTDL -> Paths.get(driftyPath, spotdl).toAbsolutePath().toString();
            case JOB_HISTORY_FILE -> Paths.get(driftyPath, "JobHistory.json").toAbsolutePath().toString();
            case JOB_FILE -> Paths.get(driftyPath, "Jobs.json").toAbsolutePath().toString();
        };
    }

    public static Path getExecutablesPath(String executableName) {
        return Paths.get(driftyPath, executableName).toAbsolutePath();
    }

    public static Path getJsonDataPath() {
        return Paths.get(driftyPath, "JsonData");
    }

    public static Path getSpotdlDataPath() {
        return Paths.get(driftyPath, "JsonData/metafile.spotdl");
    }
}
