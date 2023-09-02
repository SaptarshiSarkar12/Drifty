package GUI.Forms;

import Enums.MessageCategory;
import Enums.MessageType;
import Preferences.AppSettings;
import Utils.Environment;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Settings {

    enum Item {
        MENU, PASTE
    }

    private Stage stage;
    private Scene scene;
    private GridPane gp;
    private CheckBox cbMenu;
    private CheckBox cbPaste;

    public void show() {
        createControls();
        setControlProperties();
        stage = Constants.getStage();
        stage.setOnCloseRequest(e -> {
            Environment.getMessageBroker().sendMessage("Settings closed", MessageType.INFO, MessageCategory.SETTINGS);
            stage.close();
        });
        stage.setAlwaysOnTop(true);
        scene = Constants.getScene(gp);
        stage.setScene(scene);
        stage.show();
    }

    private void createControls() {
        gp = new GridPane();
        gp.setHgap(20);
        gp.setVgap(10);
        gp.setPadding(new Insets(40));
        cbMenu = checkBox(Item.MENU);
        cbPaste = checkBox(Item.PASTE);
        gp.add(label("Menu Bar as System Menu"), 0, 0);
        gp.add(cbMenu, 1, 0);
        gp.add(label("Always Auto Paste"), 0, 1);
        gp.add(cbPaste, 1, 1);
    }

    private void setControlProperties() {
        cbMenu.selectedProperty().addListener((observable, oldValue, newValue) -> AppSettings.set.menuBarAsSystem(newValue));
        cbPaste.selectedProperty().addListener((observable, oldValue, newValue) -> AppSettings.set.alwaysAutoPaste(newValue));
    }

    private void setToolTips() {

    }

    private CheckBox checkBox(Item item) {
        CheckBox cb = new CheckBox();
        switch (item) {
            case MENU -> cb.setSelected(AppSettings.get.menuBarAsSystem());
            case PASTE -> cb.setSelected(AppSettings.get.alwaysAutoPaste());
        }
        return cb;
    }

    private Label label(String text) {
        Label label = new Label(text);
        label.setFont(Constants.getMonaco(16));
        label.setTextFill(Color.GHOSTWHITE);
        label.setAlignment(Pos.CENTER_RIGHT);
        return label;
    }
}
