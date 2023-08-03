package Enums;

/**
 * This enum class specifies whether Drifty is opened in <b>GUI</b> or <b>CLI</b> mode
 */
public enum Mode {
    CLI_MODE, GUI_MODE;
    /**
     * The mode of the Drifty opened by the user. Possible values can be <b>CLI</b> and <b>GUI</b>. By default, the mode is CLI.
     */
    private static Mode mode = Mode.CLI_MODE;
    /**
     * This value tells whether the Drifty GUI screen is loaded or not.
     */
    private static boolean isGUILoaded = false;

    /**
     * This method sets the Mode to GUI.
     * This method is called by Drifty GUI Launcher.
     */
    public static void setGUIMode() {
        Mode.mode = Mode.GUI_MODE;
    }

    /**
     * This method sets the boolean value of the sGUILoaded variable to true after the Drifty GUI screen is loaded.
     * @param isGUILoadingCompleted true if GUI loading has been completed else, false
     */
    public static void setIsGUILoaded(boolean isGUILoadingCompleted) {
        isGUILoaded = isGUILoadingCompleted;
    }

    /**
     * This method is used to know whether the GUI has been loaded or not
     * @return True if Drifty GUI screen is loaded else false.
     */
    public static boolean getIsGUILoaded() {
        return isGUILoaded;
    }

    /**
     * This method is used to know if Drifty is opened in GUI Mode or not.
     * @return True if the application is opened in GUI mode.
     */
    public static boolean isGUI(){
        return mode.equals(Mode.GUI_MODE);
    }

    /**
     * This method is used to know if Drifty is opened in CLI Mode or not.
     * @return True if the application is opened in CLI mode.
     */
    public static boolean isCLI(){
        return mode.equals(Mode.CLI_MODE);
    }
}
