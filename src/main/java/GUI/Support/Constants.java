package GUI.Support;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;

/**
 * These are constants shared between Main and Batch and is a package private class.
 * It contains hooks into the resources folder for relevant files.
 */

public class Constants {
    public static final URL guiBackground  = Constants.class.getResource("/FX/Backgrounds/DriftyMainFX.png");
    public static final URL backPath       = Constants.class.getResource("/FX/Backgrounds/DriftyBatchFX.png");

    public static final URL runBatchUp     = Constants.class.getResource("/FX/Buttons/RunBatch/RunBatchUp.png");
    public static final URL runBatchDown   = Constants.class.getResource("/FX/Buttons/RunBatch/RunBatchDown.png");
    public static final URL batchUp        = Constants.class.getResource("/FX/Buttons/Batch/BatchUp.png");
    public static final URL batchDown      = Constants.class.getResource("/FX/Buttons/Batch/BatchDown.png");
    public static final URL downloadUp     = Constants.class.getResource("/FX/Buttons/Download/DownloadUp.png");
    public static final URL downloadDown   = Constants.class.getResource("/FX/Buttons/Download/DownloadDown.png");
    public static final URL closeUp        = Constants.class.getResource("/FX/Buttons/Close/CloseUp.png");
    public static final URL closeDown      = Constants.class.getResource("/FX/Buttons/Close/CloseDown.png");
    public static final URL saveUp         = Constants.class.getResource("/FX/Buttons/Save/SaveUp.png");
    public static final URL saveDown       = Constants.class.getResource("/FX/Buttons/Save/SaveDown.png");
    public static final URL copyUp         = Constants.class.getResource("/FX/Buttons/Copy/CopyUp.png");
    public static final URL copyDown       = Constants.class.getResource("/FX/Buttons/Copy/CopyDown.png");
    public static final URL upUP           = Constants.class.getResource("/FX/Buttons/UpDown/UpUp.png");
    public static final URL upDown         = Constants.class.getResource("/FX/Buttons/UpDown/UpDown.png");
    public static final URL downUp         = Constants.class.getResource("/FX/Buttons/UpDown/DownUp.png");
    public static final URL downDown       = Constants.class.getResource("/FX/Buttons/UpDown/DownDown.png");

    public static final URL lblAutoPaste   = Constants.class.getResource("/FX/Labels/AutoPaste.png");
    public static final URL lblDirectory   = Constants.class.getResource("/FX/Labels/Directory.png");
    public static final URL lblFilename    = Constants.class.getResource("/FX/Labels/Filename.png");
    public static final URL lblLink        = Constants.class.getResource("/FX/Labels/Link.png");

    public static final URL tabsCSS        = Constants.class.getResource("/FX/CSS/Tabs.css");
    public static final URL listViewCSS    = Constants.class.getResource("/FX/CSS/ListView.css");
    public static final URL textFieldCSS   = Constants.class.getResource("/FX/CSS/TextField.css");
    public static final URL contextMenuCSS = Constants.class.getResource("/FX/CSS/ContextMenu.css");
    public static final URL labelCSS       = Constants.class.getResource("/FX/CSS/Label.css");
    public static final URL checkBoxCSS    = Constants.class.getResource("/FX/CSS/CheckBox.css");
    public static final URL vBoxCSS        = Constants.class.getResource("/FX/CSS/VBox.css");
    public static final URL menuCSS        = Constants.class.getResource("/FX/CSS/Menu.css");
    public static final URL progressBarCSS = Constants.class.getResource("/FX/CSS/ProgressBar.css");

    public static final URL monaco         = Constants.class.getResource("/FX/Fonts/Monaco.ttf");
    public static final Image imgUpUp      = new Image(upUP.toExternalForm());
    public static final Image imgUpDown    = new Image(upDown.toExternalForm());
    public static final Image imgDownUp    = new Image(downUp.toExternalForm());
    public static final Image imgDownDown  = new Image(downDown.toExternalForm());
    public static final Image imgCopyUp    = new Image(copyUp.toExternalForm());
    public static final Image imgCopyDown  = new Image(copyDown.toExternalForm());
    public static final Color GREEN  = Color.rgb(0  , 255, 0);
    public static final Color TEAL   = Color.rgb(0  , 255, 255);
    public static final Color RED    = Color.rgb(157, 0  , 0);
    public static final Color ORANGE = Color.rgb(180, 80 , 0);
    public static final Color BLACK  = Color.rgb(0  , 0  , 0);

    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR   = MINUTE * 60;
}
