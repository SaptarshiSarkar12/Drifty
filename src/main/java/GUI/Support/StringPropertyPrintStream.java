package GUI.Support;
import Enums.Out;
import GUI.Forms.ConsoleOut;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
/**
 * This is a special class that allows redirection of 'System.out' to be
 * sent to a StringProperty which is assigned to the pop down console form.
 * Having the data sent to a String property does two things: It allows us
 * to farm the data for download progress which we then use for the progress
 * bar, and it allows us to bind the StringProperty with a TextArea that
 * comprises the console pop down form so the user can see the output of
 * yt-dlp as it does its thing.
 *</P>
 * newText is a global String. When text streams in from the standard
 * output stream (System.out), it gets written to two places ...
 * the stringProperty which is used to farm for download progress,
 * and also to newText, which gets read from the TimerTask method: updateTask()
 * which fires periodically and it uses the newText variable to engage the
 * TextArea's .appendText() method via a public static method in the
 * ConsoleOut GUI called appendStandard()
 */
public class StringPropertyPrintStream extends PrintStream {
    private final StringProperty stringProperty;
    private final LinkedList<String> newText = new LinkedList<>();
    private final Timer timer = new Timer();
    private final Out type;
    public StringPropertyPrintStream(OutputStream out, StringProperty stringProperty, Out type) {
        super(out);
        this.stringProperty = stringProperty;
        timer.scheduleAtFixedRate(updateTask(), 500, 500);
        this.type = type;
    }
    @Override
    public void write(byte[] buf, int off, int len) {
        if (buf != null) {
            super.write(buf, off, len);
            String output = new String(buf, off, len);
            String currentValue = stringProperty.get();
            stringProperty.set(currentValue + output);
            newText.addLast(output);
        }
    }
    private TimerTask updateTask() {
        return new TimerTask() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < newText.size(); x++) {
                    String add = newText.removeFirst();
                    if (add != null && !add.isEmpty()) {
                        sb.append(add);
                    }
                }
                Platform.runLater(() -> ConsoleOut.appendText(sb.toString(), type));
            }
        };
    }
}
