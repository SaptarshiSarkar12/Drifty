package GUI;

import Enums.Mode;
import GUI.Forms.Main;
import Preferences.AppSettings;
import Utils.Environment;
import Utils.Utility;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Because JavaCompiler is not yet module capable, we need to launch JavaFX from a
 * separate class in order to get the GUIs to load at all. This also means that
 * the project cannot be a Java Module project, but we hope this will change
 * in the near future when JavaPackager becomes module friendly.
 * <p>
 * This class also sets the Mode enum to GUI which can be used throughout
 * the code to check which mode the code is running in.
 */
public class Launcher {
    private final URL icon1024 = getClass().getResource("/GUI/Icons/Icon1024.png");
    private final URL icon512 = getClass().getResource("/GUI/Icons/Icon512.png");
    private static final JFrame jFrame = new JFrame();
    private static final Launcher GUI_LAUNCHER = new Launcher();

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
        Toolkit.getDefaultToolkit();
        Environment.initializeEnvironment();
        Main.main(args);
    }
}
