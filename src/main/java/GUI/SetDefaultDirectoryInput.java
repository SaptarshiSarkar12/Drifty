package GUI;

import Utils.DriftyUtility;
import javafx.scene.control.TextField;

public class SetDefaultDirectoryInput implements Runnable{
    @Override
    public void run() {
        TextField directory = Drifty_GUI.getDirectoryInputText();
        directory.setText(DriftyUtility.saveToDefault());
    }
}
