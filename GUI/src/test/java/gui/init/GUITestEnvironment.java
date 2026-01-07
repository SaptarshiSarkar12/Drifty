package gui.init;

import javafx.stage.Stage;
import main.Drifty_GUI;
import org.testfx.framework.junit5.ApplicationTest;
import properties.Mode;
import ui.Splash;

public class GUITestEnvironment extends ApplicationTest {
    @Override
    public void start(Stage stage) {
        Drifty_GUI app = new Drifty_GUI();
        Mode.setGUIMode();
        System.setProperty("javafx.preloader", Splash.class.getCanonicalName());
        app.init();
        app.start(stage);
    }
}
