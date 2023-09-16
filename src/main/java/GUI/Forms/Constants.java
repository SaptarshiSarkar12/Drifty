package GUI.Forms;

import Utils.DriftyConstants;
import Utils.Environment;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;

/*
 * These are constants used by Drifty GUI.
 * It contains hooks into the resources folder for relevant files.
 */
class Constants {
    private static final Rectangle2D SCREEN_SIZE = Screen.getPrimary().getBounds();
    public static final double SCREEN_WIDTH = SCREEN_SIZE.getWidth();
    public static final double SCREEN_HEIGHT = SCREEN_SIZE.getHeight();
    /*
    Graphics Files
     */
    public static final URL DRIFTY_MAIN_PNG = Constants.class.getResource("/GUI/Backgrounds/DriftyMain.png");
    public static final URL SAVE_UP_PNG = Constants.class.getResource("/GUI/Buttons/Save/SaveUp.png");
    public static final URL SAVE_DOWN_PNG = Constants.class.getResource("/GUI/Buttons/Save/SaveDown.png");
    public static final URL START_UP_PNG = Constants.class.getResource("/GUI/Buttons/Start/StartUp.png");
    public static final URL START_DOWN_PNG = Constants.class.getResource("/GUI/Buttons/Start/StartDown.png");
    public static final URL LINK_PNG = Constants.class.getResource("/GUI/Labels/Link.png");
    public static final URL AUTO_PASTE_PNG = Constants.class.getResource("/GUI/Labels/AutoPaste.png");
    public static final URL DIRECTORY_PNG = Constants.class.getResource("/GUI/Labels/Directory.png");
    public static final URL FILENAME_PNG = Constants.class.getResource("/GUI/Labels/Filename.png");
    public static final URL ICON_1024_PNG = Constants.class.getResource("/GUI/Icons/AppIcon.png");

    /*
    Stylesheets
     */
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

    /*
    Font Files
     */
    public static final URL MONACO_TTF = Constants.class.getResource("/GUI/Fonts/Monaco.ttf");

    /*
    JavaFX Image Objects
     */
    public static final Image IMG_MAIN_GUI_BANNER = new Image(DRIFTY_MAIN_PNG.toExternalForm());
    public static final Image IMG_LINK_LABEL = new Image(LINK_PNG.toExternalForm());
    public static final Image IMG_DIR_LABEL = new Image(DIRECTORY_PNG.toExternalForm());
    public static final Image IMG_FILENAME_LABEL = new Image(FILENAME_PNG.toExternalForm());
    public static final Image IMG_AUTO_PASTE_LABEL = new Image(AUTO_PASTE_PNG.toExternalForm());
    public static final Image IMG_START_UP = new Image(START_UP_PNG.toExternalForm());
    public static final Image IMG_START_DOWN = new Image(START_DOWN_PNG.toExternalForm());
    public static final Image IMG_SAVE_UP = new Image(SAVE_UP_PNG.toExternalForm());
    public static final Image IMG_SAVE_DOWN = new Image(SAVE_DOWN_PNG.toExternalForm());

    /*
    Methods for obtaining consistent Stages and Scenes
     */
    public static Stage getStage() {
        Stage stage = new Stage();
        stage.centerOnScreen();
        Image icon;
        icon = new Image(ICON_1024_PNG.toExternalForm());
        stage.getIcons().add(icon);
        stage.setOnCloseRequest(e -> {
            Environment.getMessageBroker().msgLogInfo(DriftyConstants.GUI_APPLICATION_TERMINATED);
            System.exit(0);
        });
        stage.setTitle("Drifty GUI");
        return stage;
    }

    public static Stage getStage(Stage stage) {
        Image icon = new Image(ICON_1024_PNG.toExternalForm());
        stage.getIcons().add(icon);
        stage.setOnCloseRequest(e -> {
            Environment.getMessageBroker().msgLogInfo(DriftyConstants.GUI_APPLICATION_TERMINATED);
            System.exit(0);
        });
        stage.setResizable(true);
        stage.setTitle("Drifty GUI");
        return stage;
    }

    public static Scene getScene(Parent root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(CHECK_BOX_CSS.toExternalForm());
        scene.getStylesheets().add(CONTEXT_MENU_CSS.toExternalForm());
        scene.getStylesheets().add(LABEL_CSS.toExternalForm());
        scene.getStylesheets().add(LIST_VIEW_CSS.toExternalForm());
        scene.getStylesheets().add(MENU_CSS.toExternalForm());
        scene.getStylesheets().add(PROGRESS_BAR_CSS.toExternalForm());
        scene.getStylesheets().add(SCENE_CSS.toExternalForm());
        scene.getStylesheets().add(SCROLL_PANE_CSS.toExternalForm());
        scene.getStylesheets().add(TEXT_FIELD_CSS.toExternalForm());
        scene.getStylesheets().add(V_BOX_CSS.toExternalForm());
        scene.getStylesheets().add(BUTTON_CSS.toExternalForm());
        return scene;
    }

    public static Font getMonaco(double size) {
        return new Font(MONACO_TTF.toExternalForm(), size);
    }
}
