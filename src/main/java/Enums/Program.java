package Enums;

import java.nio.file.Paths;
public enum Program {
    NAME, PATH, COMMAND, DATA_PATH;

    private static String yt_dlpProgramName;
    private static String configFolderPath;
    private static String dataPath;

    public static void setName(String name) {
        Program.yt_dlpProgramName = name;
    }

    public static void setPath(String path) {
        Program.configFolderPath = path + System.getProperty("file.separator");
    }

    public static void setDataPath(String path) {
        Program.dataPath = path;
    }

    public static String get(Program program) {
        return switch (program) {
            case NAME -> yt_dlpProgramName;
            case PATH -> configFolderPath;
            case COMMAND -> Paths.get(configFolderPath, yt_dlpProgramName).toAbsolutePath().toString();
            case DATA_PATH -> dataPath;
        };
    }
}
