package properties;

public enum Mode {
    CLI, GUI;

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

    public static Mode getMode() {
        return mode;
    }
}
