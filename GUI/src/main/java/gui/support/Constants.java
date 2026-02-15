package gui.support;

import gui.init.Environment;
import settings.AppSettings;
import gui.utils.UIComponentBuilder;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class Constants extends support.Constants {
    public static final UIComponentBuilder UI_COMPONENT_BUILDER_INSTANCE = UIComponentBuilder.getInstance();
    public static final String BUTTON_RELEASED =
            "-fx-text-fill: white;" +
                    "-fx-font-weight: Bold;" +
                    "-fx-background-color: linear-gradient(rgb(0, 53, 105) 20%, rgb(26, 21, 129) 65%, rgb(0, 0, 65) 100%);" +
                    "-fx-border-color: white;";
    public static final String BUTTON_PRESSED =
            "-fx-text-fill: white;" +
                    "-fx-font-weight: Bold;" +
                    " -fx-background-color: linear-gradient(rgb(11, 118, 220) 20%, rgb(33, 31, 131) 65%, rgb(2, 2, 168) 100%);\n" +
                    "-fx-border-color: white;";
    public static final String GUI_APPLICATION_TERMINATED = "Drifty GUI (Graphical User Interface) Application Terminated!";
    public static final String TRYING_TO_DOWNLOAD_F = "Trying to download \"%s\" ...";
    public static final String WRITE_ACCESS_DENIED_F = "Write Access To \"%s\" DENIED!";
    public static final String FAILED_READING_STREAM = "Failed to get I/O operations channel to read from the data stream !";
    private static final Rectangle2D SCREEN_SIZE = Screen.getPrimary().getBounds();
    public static final double SCREEN_WIDTH = SCREEN_SIZE.getWidth();
    public static final double SCREEN_HEIGHT = SCREEN_SIZE.getHeight();

    // Graphics Files
    public static final URL DRIFTY_MAIN_PNG = "Dark".equals(AppSettings.GET.getGuiTheme()) ? Constants.class.getResource("/Backgrounds/DriftyMainDark.png") : Constants.class.getResource("/Backgrounds/DriftyMainLight.png");
    public static final URL SAVE_UP_LIGHT_PNG = Constants.class.getResource("/Buttons/Save/SaveUpLight.png");
    public static final URL SAVE_UP_DARK_PNG = Constants.class.getResource("/Buttons/Save/SaveUpDark.png");
    public static final URL SAVE_DOWN_LIGHT_PNG = Constants.class.getResource("/Buttons/Save/SaveDownLight.png");
    public static final URL SAVE_DOWN_DARK_PNG = Constants.class.getResource("/Buttons/Save/SaveDownDark.png");
    public static final URL START_UP_LIGHT_PNG = Constants.class.getResource("/Buttons/Start/StartUpLight.png");
    public static final URL START_UP_DARK_PNG = Constants.class.getResource("/Buttons/Start/StartUpDark.png");
    public static final URL START_DOWN_LIGHT_PNG = Constants.class.getResource("/Buttons/Start/StartDownLight.png");
    public static final URL START_DOWN_DARK_PNG = Constants.class.getResource("/Buttons/Start/StartDownDark.png");
    public static final URL LINK_PNG = Constants.class.getResource("/Labels/Link.png");
    public static final URL AUTO_PASTE_PNG = Constants.class.getResource("/Labels/AutoPaste.png");
    public static final URL DIRECTORY_PNG = Constants.class.getResource("/Labels/Directory.png");
    public static final URL FILENAME_PNG = Constants.class.getResource("/Labels/Filename.png");
    public static final URL DRIFTY_ICON = Constants.class.getResource("/Icons/AppIcon.png");
    public static final URL SPLASH = "Dark".equals(AppSettings.GET.getGuiTheme()) ? Constants.class.getResource("/SplashDark.png") : Constants.class.getResource("/SplashLight.png");

    // Stylesheets
    public static final URL LIGHT_THEME_CSS = Constants.class.getResource("/CSS/LightTheme.css");
    public static final URL DARK_THEME_CSS = Constants.class.getResource("/CSS/DarkTheme.css");
    public static final URL LIST_VIEW_CSS = Constants.class.getResource("/CSS/ListView.css");
    public static final URL TEXT_FIELD_CSS = Constants.class.getResource("/CSS/TextField.css");
    public static final URL CONTEXT_MENU_CSS = Constants.class.getResource("/CSS/ContextMenu.css");
    public static final URL LABEL_CSS = Constants.class.getResource("/CSS/Label.css");
    public static final URL CHECK_BOX_CSS = Constants.class.getResource("/CSS/CheckBox.css");
    public static final URL V_BOX_CSS = Constants.class.getResource("/CSS/VBox.css");
    public static final URL MENU_CSS = Constants.class.getResource("/CSS/Menu.css");
    public static final URL PROGRESS_BAR_CSS = Constants.class.getResource("/CSS/ProgressBar.css");
    public static final URL SCROLL_PANE_CSS = Constants.class.getResource("/CSS/ScrollPane.css");
    public static final URL BUTTON_CSS = Constants.class.getResource("/CSS/Button.css");

    // Font Files
    public static final URL MONACO_TTF = Constants.class.getResource("/Fonts/Monaco.ttf");

    // JavaFX Image Objects
    public static Image imgMainGuiBanner = new Image(Objects.requireNonNull(DRIFTY_MAIN_PNG).toExternalForm());
    public static Image imgSplash = new Image(Objects.requireNonNull(SPLASH).toExternalForm());
    public static final Image IMG_LINK_LABEL = new Image(Objects.requireNonNull(LINK_PNG).toExternalForm());
    public static final Image IMG_DIR_LABEL = new Image(Objects.requireNonNull(DIRECTORY_PNG).toExternalForm());
    public static final Image IMG_FILENAME_LABEL = new Image(Objects.requireNonNull(FILENAME_PNG).toExternalForm());
    public static final Image IMG_AUTO_PASTE_LABEL = new Image(Objects.requireNonNull(AUTO_PASTE_PNG).toExternalForm());
    public static final Image IMG_START_UP_LIGHT = new Image(Objects.requireNonNull(START_UP_LIGHT_PNG).toExternalForm());
    public static final Image IMG_START_UP_DARK = new Image(Objects.requireNonNull(START_UP_DARK_PNG).toExternalForm());
    public static final Image IMG_START_DOWN_LIGHT = new Image(Objects.requireNonNull(START_DOWN_LIGHT_PNG).toExternalForm());
    public static final Image IMG_START_DOWN_DARK = new Image(Objects.requireNonNull(START_DOWN_DARK_PNG).toExternalForm());
    public static final Image IMG_SAVE_UP_LIGHT = new Image(Objects.requireNonNull(SAVE_UP_LIGHT_PNG).toExternalForm());
    public static final Image IMG_SAVE_UP_DARK = new Image(Objects.requireNonNull(SAVE_UP_DARK_PNG).toExternalForm());
    public static final Image IMG_SAVE_DOWN_LIGHT = new Image(Objects.requireNonNull(SAVE_DOWN_LIGHT_PNG).toExternalForm());
    public static final Image IMG_SAVE_DOWN_DARK = new Image(Objects.requireNonNull(SAVE_DOWN_DARK_PNG).toExternalForm());

    // Methods for getting consistent Stages and Scenes
    public static Stage getStage(String title, boolean isPrimaryStage) {
        Stage stage = new Stage();
        Image icon = new Image(Objects.requireNonNull(DRIFTY_ICON).toExternalForm());
        stage.getIcons().add(icon);
        stage.setTitle(title);
        if (isPrimaryStage) {
            stage.setOnCloseRequest(e -> {
                Environment.getMessageBroker().msgLogInfo(GUI_APPLICATION_TERMINATED);
                Environment.terminate(0);
            });
            stage.setResizable(true);
        } else {
            stage.centerOnScreen();
        }
        return stage;
    }

    public static Scene getScene(Parent root) {
        Scene scene = new Scene(root);
        addCSS(scene, CHECK_BOX_CSS, CONTEXT_MENU_CSS, LABEL_CSS, LIST_VIEW_CSS, MENU_CSS, LIGHT_THEME_CSS, PROGRESS_BAR_CSS, SCROLL_PANE_CSS, TEXT_FIELD_CSS, V_BOX_CSS, BUTTON_CSS);
        return scene;
    }

    public static void addCSS(Scene scene, URL... css) {
        for (URL url : css) {
            scene.getStylesheets().add(url.toExternalForm());
        }
    }

    public static Font getMonaco(double size) {
        return new Font(Objects.requireNonNull(MONACO_TTF).toExternalForm(), size);
    }
}
