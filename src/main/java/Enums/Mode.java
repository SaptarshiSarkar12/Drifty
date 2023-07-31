package Enums;

public enum Mode {
    CLI_MODE, GUI_MODE;

    private static Mode mode = Mode.CLI_MODE;
    private static boolean guiLoaded = false;

    public static void setGUIMode() {
        Mode.mode = Mode.GUI_MODE;
    }

    public static void setGUILoaded() {
        guiLoaded = true;
    }

    public static boolean GUILoaded() {
        return guiLoaded;
    }

    public static boolean GUI(){
        return mode.equals(Mode.GUI_MODE);
    }

    public static boolean CLI(){
        return mode.equals(Mode.CLI_MODE);
    }
}
