package properties;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum Program {
    DRIFTY_PATH, JOB_FILE, JOB_HISTORY_FILE, YT_DLP, YT_DLP_EXECUTABLE_NAME, FFMPEG_EXECUTABLE_NAME, FFMPEG, DATABASE_PATH;

    private static String ytDLPExecutableName;
    private static String ffmpegExecutableName;
    private static String driftyPath;

    public static void setYtDlpExecutableName(String name) {
        Program.ytDLPExecutableName = name;
    }

    public static void setFfmpegExecutableName(String name) {
        Program.ffmpegExecutableName = name;
    }

    public static void setDriftyPath(String path) {
        Program.driftyPath = path;
    }

    public static String get(Program program) {
        return switch (program) {
            case YT_DLP_EXECUTABLE_NAME -> ytDLPExecutableName;
            case FFMPEG_EXECUTABLE_NAME -> ffmpegExecutableName;
            case DRIFTY_PATH -> driftyPath;
            case DATABASE_PATH -> Paths.get(Program.get(Program.DRIFTY_PATH)).resolve("drifty_" + Mode.getMode().name().toLowerCase() + ".db").toAbsolutePath().toString();
            case YT_DLP -> Paths.get(driftyPath, ytDLPExecutableName).toAbsolutePath().toString();
            case FFMPEG -> Paths.get(driftyPath, ffmpegExecutableName).toAbsolutePath().toString();
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
}
