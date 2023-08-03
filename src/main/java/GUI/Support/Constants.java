package GUI.Support;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;

/**
 * These are constants used by Drifty GUI.
 * It contains hooks into the resources folder for relevant files.
 */
public class Constants {
    public static final URL guiBackground = Constants.class.getResource("/GUI/Backgrounds/DriftyMainBackground.png");
    public static final URL backPath = Constants.class.getResource("/GUI/Backgrounds/DriftyBatchBackground.png");

    public static final URL runBatchUp = Constants.class.getResource("/GUI/Buttons/RunBatch/RunBatchUp.png");
    public static final URL runBatchDown = Constants.class.getResource("/GUI/Buttons/RunBatch/RunBatchDown.png");
    public static final URL batchUp = Constants.class.getResource("/GUI/Buttons/Batch/BatchUp.png");
    public static final URL batchDown = Constants.class.getResource("/GUI/Buttons/Batch/BatchDown.png");
    public static final URL downloadUp = Constants.class.getResource("/GUI/Buttons/Download/DownloadUp.png");
    public static final URL downloadDown = Constants.class.getResource("/GUI/Buttons/Download/DownloadDown.png");
    public static final URL closeUp = Constants.class.getResource("/GUI/Buttons/Close/CloseUp.png");
    public static final URL closeDown = Constants.class.getResource("/GUI/Buttons/Close/CloseDown.png");
    public static final URL saveUp = Constants.class.getResource("/GUI/Buttons/Save/SaveUp.png");
    public static final URL saveDown = Constants.class.getResource("/GUI/Buttons/Save/SaveDown.png");
    public static final URL copyUp = Constants.class.getResource("/GUI/Buttons/Copy/CopyUp.png");
    public static final URL copyDown = Constants.class.getResource("/GUI/Buttons/Copy/CopyDown.png");
    public static final URL upUP = Constants.class.getResource("/GUI/Buttons/UpDown/UpUp.png");
    public static final URL upDown = Constants.class.getResource("/GUI/Buttons/UpDown/UpDown.png");
    public static final URL downUp = Constants.class.getResource("/GUI/Buttons/UpDown/DownUp.png");
    public static final URL downDown = Constants.class.getResource("/GUI/Buttons/UpDown/DownDown.png");

    public static final URL autoPasteLabelImage = Constants.class.getResource("/GUI/Labels/AutoPaste.png");
    public static final URL directoryLabelImage = Constants.class.getResource("/GUI/Labels/Directory.png");
    public static final URL filenameLabelImage = Constants.class.getResource("/GUI/Labels/Filename.png");
    public static final URL linkLabelImage = Constants.class.getResource("/GUI/Labels/Link.png");

    public static final URL tabsCSS = Constants.class.getResource("/GUI/CSS/Tabs.css");
    public static final URL listViewCSS = Constants.class.getResource("/GUI/CSS/ListView.css");
    public static final URL textFieldCSS = Constants.class.getResource("/GUI/CSS/TextField.css");
    public static final URL contextMenuCSS = Constants.class.getResource("/GUI/CSS/ContextMenu.css");
    public static final URL labelCSS = Constants.class.getResource("/GUI/CSS/Label.css");
    public static final URL checkBoxCSS = Constants.class.getResource("/GUI/CSS/CheckBox.css");
    public static final URL vBoxCSS = Constants.class.getResource("/GUI/CSS/VBox.css");
    public static final URL menuCSS = Constants.class.getResource("/GUI/CSS/Menu.css");
    public static final URL progressBarCSS = Constants.class.getResource("/GUI/CSS/ProgressBar.css");

    public static final URL monacoFont = Constants.class.getResource("/GUI/Fonts/Monaco.ttf");
    public static final Image imgUpUp = new Image(upUP.toExternalForm());
    public static final Image imgUpDown = new Image(upDown.toExternalForm());
    public static final Image imgDownUp = new Image(downUp.toExternalForm());
    public static final Image imgDownDown = new Image(downDown.toExternalForm());
    public static final Image imgCopyUp = new Image(copyUp.toExternalForm());
    public static final Image imgCopyDown = new Image(copyDown.toExternalForm());
    public static final Color GREEN = Color.rgb(0  , 255, 0);
    public static final Color TEAL = Color.rgb(0  , 255, 255);
    public static final Color RED = Color.rgb(157, 0  , 0);
    public static final Color ORANGE = Color.rgb(180, 80 , 0);
    public static final Color BLACK = Color.rgb(0  , 0  , 0);
    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
}
