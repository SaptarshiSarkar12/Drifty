package Enums;

/**
 * This enum class specifies whether Drifty is opened in <b>GUI</b> or <b>CLI</b> or <b>UPDATE</b> mode.
 */
public enum Mode {
    CLI, GUI, UPDATE;

    private static Mode mode = Mode.CLI;

    public static void setGUIMode() {
        Mode.mode = Mode.GUI;
    }

    public static boolean isGUI() {
        return mode.equals(Mode.GUI);
    }

    public static boolean isCLI() {
        return mode.equals(Mode.CLI);
    }

    public static void setUpdateMode() {
        Mode.mode = Mode.UPDATE;
    }

    public static boolean isUpdating() {
        return mode.equals(Mode.UPDATE);
    }

    public static void setMode(Mode mode) { // This method is used to set the mode (CLI or GUI) in which Drifty is running
        Mode.mode = mode;
    }

    public static Mode getMode() { // This method returns the mode (CLI or GUI) in which Drifty is running
        return mode;
    }
}
