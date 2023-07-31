package Enums;

import java.nio.file.Paths;

public enum Program {
    NAME, PATH, COMMAND;

    private static String name;
    private static String path;

    public static void setName(String name) {
        Program.name = name;
    }

    public static void setPath(String path) {
        Program.path = path;
    }

    public static String get(Program program) {
        return switch (program) {
            case NAME -> name;
            case PATH -> path;
            case COMMAND -> Paths.get(path, name).toAbsolutePath().toString();
        };
    }
}
