package Enums;

/**
 * This enum class specifies whether Drifty is opened in <b>GUI</b> or <b>CLI</b> mode
 */
public enum Mode {
    CLI, GUI;
    private static Mode mode = Mode.CLI;
    private static boolean isGUILoaded = false;
    private static boolean developer = false;

    public static void setDev() {
        developer = true;
    }

    public static boolean devMode() {
        return developer;
    }

    public static void setGUIMode() {
        Mode.mode = Mode.GUI;
    }

    public static boolean isGUI() {
        return mode.equals(Mode.GUI);
    }

    public static boolean isCLI() {
        return mode.equals(Mode.CLI);
    }
}
