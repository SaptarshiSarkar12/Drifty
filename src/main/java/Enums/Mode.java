package Enums;

/**
 * This enum class specifies whether Drifty is opened in <b>GUI</b> or <b>CLI</b> mode
 */
public enum Mode {
    CLI_MODE, GUI_MODE;
    private static Mode mode = Mode.CLI_MODE;
    private static boolean isGUILoaded = false;

    public static void setGUIMode() {
        Mode.mode = Mode.GUI_MODE;
    }

    public static void setIsGUILoaded(boolean isGUILoadingCompleted) {
        isGUILoaded = isGUILoadingCompleted;
    }

    public static boolean getIsGUILoaded() {
        return isGUILoaded;
    }

    public static boolean isGUI() {
        return mode.equals(Mode.GUI_MODE);
    }

    public static boolean isCLI() {
        return mode.equals(Mode.CLI_MODE);
    }
}
