package GUIFX.Support;

import GUIFX.ConsoleOut;
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
 */

public class StringPropertyPrintStream extends PrintStream {

    private final StringProperty stringProperty;
    private final LinkedList<String> newText = new LinkedList<>();
    private final Timer timer = new Timer();

    public StringPropertyPrintStream(OutputStream out, StringProperty stringProperty) {
        super(out);
        this.stringProperty = stringProperty;
        timer.scheduleAtFixedRate(updateTask(), 500, 500);
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
                for (int x = 0; x < newText.size() - 1; x++) {
                    sb.append(newText.removeFirst());
                }
                Platform.runLater(() -> ConsoleOut.append(sb.toString()));
            }
        };
    }
}
