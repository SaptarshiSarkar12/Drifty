package GUI;

import Utils.DriftyUtility;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ValidateLinkThread implements Runnable{
    static boolean flag = true;
    @Override
    public void run() {
        while (flag) {
            TextField link = Drifty_GUI.getLinkInputText();
            String url = String.valueOf(link.getCharacters());
            Text linkOutput = Drifty_GUI.getLinkOutputText();

            linkOutput.setFill(Color.RED);
            if (url.contains(" ")) {
                linkOutput.setText("Link should not contain whitespace characters!");
            } else if (url.length() == 0) {
                linkOutput.setText("Link cannot be empty!");
            } else {
                try {
                    DriftyUtility.isURLValid(url);
                    linkOutput.setText("Link is valid!");
                    linkOutput.setFill(Color.GREEN);
                } catch (Exception e) {
                    linkOutput.setText(e.getMessage());
                }
            }
        }
    }

    protected synchronized static void stop() {
        flag = false;
    }
}
