package GUI.Forms;

import Preferences.AppSettings;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Settings {
    enum Item {
        MENU, PASTE
    }

    public void show() {
        AskYesNo ask = new AskYesNo("Settings coming soon", true);
        ask.showOK();
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
