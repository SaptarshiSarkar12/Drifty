package properties;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum Program {
    DRIFTY_PATH, JOB_FILE, JOB_HISTORY_FILE, YT_DLP, YT_DLP_EXECUTABLE_NAME, DENO, DENO_EXECUTABLE_NAME, DATABASE_PATH;

    private static String ytDLPExecutableName;
    private static String denoExecutableName;
    private static String driftyPath;

    public static void setYtDlpExecutableName(String name) {
        Program.ytDLPExecutableName = name;
    }

    public static void setDenoExecutableName(String name) {
        Program.denoExecutableName = name;
    }

    public static void setDriftyPath(String path) {
        Program.driftyPath = path;
    }

    public static String get(Program program) {
        return switch (program) {
            case YT_DLP_EXECUTABLE_NAME -> ytDLPExecutableName;
            case DENO_EXECUTABLE_NAME -> denoExecutableName;
            case DRIFTY_PATH -> driftyPath;
            case DATABASE_PATH -> Paths.get(Program.get(Program.DRIFTY_PATH)).resolve("drifty_" + Mode.getMode().name().toLowerCase() + ".db").toAbsolutePath().toString();
            case YT_DLP -> Paths.get(driftyPath).resolve(ytDLPExecutableName).toAbsolutePath().toString();
            case DENO -> {
                if (denoExecutableName == null) {
                    yield Paths.get(driftyPath).resolve("deno").toAbsolutePath().toString();
                }
                yield Paths.get(driftyPath).resolve(denoExecutableName).toAbsolutePath().toString();
            }
            case JOB_HISTORY_FILE -> Paths.get(driftyPath).resolve("JobHistory.json").toAbsolutePath().toString();
            case JOB_FILE -> Paths.get(driftyPath).resolve("Jobs.json").toAbsolutePath().toString();
        };
    }

    public static Path getJsonDataPath() {
        return Paths.get(driftyPath).resolve("JsonData");
    }
}
