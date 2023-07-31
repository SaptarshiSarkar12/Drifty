package GUIFX;

import Enums.Mode;
import Enums.OS;
import Preferences.Init;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
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

    private static final Taskbar taskbar   = Taskbar.getTaskbar();
    private final URL icon1024             = getClass().getResource("/FX/Icons/Icon1024.png");
    private final URL icon512              = getClass().getResource("/FX/Icons/Icon512.png");
    private static final JFrame jFrame     = new JFrame();
    private static final Launcher INSTANCE = new Launcher();

    public static void main(String[] args) {
        Mode.setGUIMode();
        Init.environment();
        System.setProperty("apple.awt.UIElement", "false");
        Toolkit.getDefaultToolkit();
        setTaskbarDockIcon();
        MainGUI.main(args);
    }

    private static void setTaskbarDockIcon() {
        try {
            Image image = ImageIO.read(INSTANCE.icon512);
            if (OS.isMac()) {
                image = ImageIO.read(INSTANCE.icon1024);
                taskbar.setIconImage(image);
            }
            else {
                jFrame.setUndecorated(true);
                jFrame.setIconImage(image);
                jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
                jFrame.pack();
                jFrame.setVisible(true);
                jFrame.setSize(new Dimension(26, 26));
            }
        }
        catch (UnsupportedOperationException ignored) {
            System.out.println("This os does not support taskbar.setIconImage()");
        }
        catch (final SecurityException e) {
            System.out.println("There was a security exception for: 'taskbar.setIconImage()'");
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
