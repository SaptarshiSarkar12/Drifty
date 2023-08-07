package Enums;
import java.nio.file.Paths;
/**
 * This class contains all the configurations of Drifty required throughout its life.
 */
public enum DriftyConfig {
    NAME,
    PATH,
    YT_DLP_COMMAND,
    BATCH_PATH;

    private static String yt_dlpProgramName;
    private static String yt_dlpProgramPath;
    private static String batchPath;

    public static void setYt_dlpProgramName(String name) {
        DriftyConfig.yt_dlpProgramName = name;
    }

    public static void setYt_dlpProgramPath(String path) {
        DriftyConfig.yt_dlpProgramPath = path;
    }

    public static void setBatchPath(String path) {
        DriftyConfig.batchPath = path;
    }

    public static String getConfig(DriftyConfig driftyConfig) {
        return switch (driftyConfig) {
            case NAME -> yt_dlpProgramName;
            case PATH -> yt_dlpProgramPath;
            case YT_DLP_COMMAND -> Paths.get(yt_dlpProgramPath, yt_dlpProgramName).toAbsolutePath().toString();
            case BATCH_PATH -> batchPath;
        };
    }
}
