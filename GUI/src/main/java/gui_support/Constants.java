package gui_support;

import gui_init.Environment;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class Constants extends support.Constants {
    public static final String GUI_APPLICATION_STARTED = "Drifty GUI (Graphical User Interface) Application Started !";
    public static final String GUI_APPLICATION_TERMINATED = "Drifty GUI (Graphical User Interface) Application Terminated!";
    public static final String TRYING_TO_DOWNLOAD_F = "Trying to download \"%s\" ...";
    public static final String WRITE_ACCESS_DENIED_F = "Write Access To \"%s\" DENIED!";
    public static final String FAILED_READING_STREAM = "Failed to get I/O operations channel to read from the data stream !";
    private static final Rectangle2D SCREEN_SIZE = Screen.getPrimary().getBounds();
    public static final double SCREEN_WIDTH = SCREEN_SIZE.getWidth();
    public static final double SCREEN_HEIGHT = SCREEN_SIZE.getHeight();

    // Graphics Files
    public static final URL DRIFTY_MAIN_PNG = Constants.class.getResource("/GUI/Backgrounds/DriftyMain.png");
    public static final URL SAVE_UP_PNG = Constants.class.getResource("/GUI/Buttons/Save/SaveUp.png");
    public static final URL SAVE_DOWN_PNG = Constants.class.getResource("/GUI/Buttons/Save/SaveDown.png");
    public static final URL START_UP_PNG = Constants.class.getResource("/GUI/Buttons/Start/StartUp.png");
    public static final URL START_DOWN_PNG = Constants.class.getResource("/GUI/Buttons/Start/StartDown.png");
    public static final URL LINK_PNG = Constants.class.getResource("/GUI/Labels/Link.png");
    public static final URL AUTO_PASTE_PNG = Constants.class.getResource("/GUI/Labels/AutoPaste.png");
    public static final URL DIRECTORY_PNG = Constants.class.getResource("/GUI/Labels/Directory.png");
    public static final URL FILENAME_PNG = Constants.class.getResource("/GUI/Labels/Filename.png");
    public static final URL DRIFTY_ICON = Constants.class.getResource("/GUI/Icons/AppIcon.png");

    // Stylesheets
    public static final URL SCENE_CSS = Constants.class.getResource("/GUI/CSS/Scene.css");
    public static final URL LIST_VIEW_CSS = Constants.class.getResource("/GUI/CSS/ListView.css");
    public static final URL TEXT_FIELD_CSS = Constants.class.getResource("/GUI/CSS/TextField.css");
    public static final URL CONTEXT_MENU_CSS = Constants.class.getResource("/GUI/CSS/ContextMenu.css");
    public static final URL LABEL_CSS = Constants.class.getResource("/GUI/CSS/Label.css");
    public static final URL CHECK_BOX_CSS = Constants.class.getResource("/GUI/CSS/CheckBox.css");
    public static final URL V_BOX_CSS = Constants.class.getResource("/GUI/CSS/VBox.css");
    public static final URL MENU_CSS = Constants.class.getResource("/GUI/CSS/Menu.css");
    public static final URL PROGRESS_BAR_CSS = Constants.class.getResource("/GUI/CSS/ProgressBar.css");
    public static final URL SCROLL_PANE_CSS = Constants.class.getResource("/GUI/CSS/ScrollPane.css");
    public static final URL BUTTON_CSS = Constants.class.getResource("/GUI/CSS/Button.css");

    // Font Files
    public static final URL MONACO_TTF = Constants.class.getResource("/GUI/Fonts/Monaco.ttf");

    // JavaFX Image Objects
    public static final Image IMG_MAIN_GUI_BANNER = new Image(Objects.requireNonNull(DRIFTY_MAIN_PNG).toExternalForm());
    public static final Image IMG_LINK_LABEL = new Image(Objects.requireNonNull(LINK_PNG).toExternalForm());
    public static final Image IMG_DIR_LABEL = new Image(Objects.requireNonNull(DIRECTORY_PNG).toExternalForm());
    public static final Image IMG_FILENAME_LABEL = new Image(Objects.requireNonNull(FILENAME_PNG).toExternalForm());
    public static final Image IMG_AUTO_PASTE_LABEL = new Image(Objects.requireNonNull(AUTO_PASTE_PNG).toExternalForm());
    public static final Image IMG_START_UP = new Image(Objects.requireNonNull(START_UP_PNG).toExternalForm());
    public static final Image IMG_START_DOWN = new Image(Objects.requireNonNull(START_DOWN_PNG).toExternalForm());
    public static final Image IMG_SAVE_UP = new Image(Objects.requireNonNull(SAVE_UP_PNG).toExternalForm());
    public static final Image IMG_SAVE_DOWN = new Image(Objects.requireNonNull(SAVE_DOWN_PNG).toExternalForm());

    // Colors
    public static final Color GREEN = Color.rgb(0, 150, 0);
    public static final Color RED = Color.rgb(157, 0, 0);
    public static final Color PURPLE = Color.rgb(125, 0, 75);
    public static final Color HOTPINK = Color.rgb(255, 0, 175);
    public static final Color BLACK = Color.rgb(0, 0, 0);
    public static final Color YELLOW = Color.rgb(255, 255, 0);
    public static final Color BLUE = Color.rgb(0, 100, 255);

    // Methods for obtaining consistent Stages and Scenes
    public static Stage getStage(String title, boolean isPrimaryStage) {
        Stage stage = new Stage();
        Image icon = new Image(Objects.requireNonNull(DRIFTY_ICON).toExternalForm());
        stage.getIcons().add(icon);
        stage.setTitle(title);
        if (isPrimaryStage) {
            stage.setOnCloseRequest(e -> {
                Environment.getMessageBroker().msgLogInfo(GUI_APPLICATION_TERMINATED);
                System.exit(0);
            });
            stage.setResizable(true);
        } else {
            stage.centerOnScreen();
        }
        return stage;
    }

    public static Scene getScene(Parent root) {
        Scene scene = new Scene(root);
        addCSS(scene, CHECK_BOX_CSS, CONTEXT_MENU_CSS, LABEL_CSS, LIST_VIEW_CSS, MENU_CSS, PROGRESS_BAR_CSS, SCENE_CSS, SCROLL_PANE_CSS, TEXT_FIELD_CSS, V_BOX_CSS, BUTTON_CSS);
        return scene;
    }

    public static void addCSS(Scene scene, URL ...css) {
        for (URL url : css) {
            scene.getStylesheets().add(url.toExternalForm());
        }
    }

    public static Font getMonaco(double size) {
        return new Font(Objects.requireNonNull(MONACO_TTF).toExternalForm(), size);
    }
}
