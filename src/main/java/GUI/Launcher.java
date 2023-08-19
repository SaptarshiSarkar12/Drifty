package GUI;

import Enums.Mode;
import GUI.Forms.Main;
import Preferences.AppSettings;
import Utils.Environment;
import Utils.Utility;


/*
 * Because Java Compiler is not yet module-capable, we need to launch JavaFX from a
 * separate class in order to get the GUIs to load completely.
 * <p>
 * This class also sets the Mode enum to GUI which can be used throughout
 * the code to check which mode the code is running in.
 */
public class Launcher {
    public static void main(String[] args) {
        Mode.setGUIMode();
        Utility.setStartTime();
        System.setProperty("apple.awt.UIElement", "false");
        for (String arg : args) {
            if (arg.toLowerCase().contains("--enablemaxstart")) {
                AppSettings.set.startMax(true);
            }
            if (arg.toLowerCase().contains("--disablemaxstart")) {
                AppSettings.set.startMax(false);
            }
            if (arg.toLowerCase().contains("--devmode")) {
                Mode.setDev();
            }
        }
        Environment.initializeEnvironment();
        Main.main(args);
    }
}