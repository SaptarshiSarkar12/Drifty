package GUI;

import Utils.DriftyConstants;
import Utils.DriftyUtility;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ValidateLinkThread implements Runnable{
    @Override
    public void run() {
        TextField link = Drifty_GUI.getLinkInputText();
        String url = String.valueOf(link.getCharacters());
        Text linkOutput = Drifty_GUI.getLinkOutputText();
        String out = "";

        if (url.contains(" ")) {
            out = "Link should not contain whitespace characters!";
        } else if (url.length() == 0) {
            out = "Link cannot be empty!";
        } else {
            try {
                DriftyUtility.isURLValid(url);
                out = "Link is valid!";
            } catch (Exception e) {
                out = e.getMessage();
            }
        }

        linkOutput.setText(out);
    }
}
