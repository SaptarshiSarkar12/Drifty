package Enums;

import java.nio.file.Path;
import java.nio.file.Paths;
public enum Program {
    EXECUTABLE_NAME, YT_DLP, DRIFTY_PATH, JOB_HISTORY_FILE, JOB_FILE;

    private static String ytDLP;
    private static String driftyPath;

    public static void setExecutableName(String name) {
        Program.ytDLP = name;
    }

    public static void setDriftyPath(String path) { Program.driftyPath = path;}

    public static String get(Program program) {
        return switch (program) {
            case EXECUTABLE_NAME -> ytDLP;
            case DRIFTY_PATH -> driftyPath;
            case YT_DLP -> Paths.get(driftyPath, ytDLP).toAbsolutePath().toString();
            case JOB_HISTORY_FILE -> Paths.get(driftyPath, "JobHistory.json").toAbsolutePath().toString();
            case JOB_FILE -> Paths.get(driftyPath, "Jobs.json").toAbsolutePath().toString();
        };
    }

    public static Path getYtDLPFullPath() {
        return Paths.get(driftyPath, ytDLP);
    }
    public static Path getJsonDataPath() {
        return Paths.get(driftyPath, "JsonData");
    }
}
