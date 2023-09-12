package GUI.Forms;

import Enums.Domain;
import Enums.Program;
import Utils.Utility;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Enums.Program.YT_DLP;
import static Utils.DriftyConstants.*;

public class DownloadFile extends Task<Integer> {
    private static final long threadingThreshold = 1024 * 1024 * 50;
    private final StringProperty standardOut = new SimpleStringProperty();
    private final StringProperty errorOut = new SimpleStringProperty();
    private final StringProperty feedback = new SimpleStringProperty();
    //private final String regex = "(\\[download]\\s+)(\\d+\\.\\d+)(%)";
    private final String regex = "(\\[download]\\s+)(\\d+\\.\\d+)";
    private final Pattern pattern = Pattern.compile(regex);
    private final String lineFeed = System.lineSeparator();
    private final String link;
    private final String filename;
    private final String dir;
    private final Domain domain;
    private int result = -1;

    public DownloadFile(String link, String dir, String filename,
                        StringProperty linkProperty, StringProperty dirProperty, StringProperty filenameProperty,
                        DoubleProperty progressProperty)  {
        this.link = link;
        this.filename = filename;
        this.dir = dir;
        this.domain = Domain.getDomain(link);
        setProperties();
        Platform.runLater(() -> {
            linkProperty.setValue(link);
            filenameProperty.setValue(filename);
            dirProperty.setValue(dir);
            progressProperty.bind(this.progressProperty());
        });
    }

    @Override
    protected Integer call() throws Exception {
        updateProgress(0, 1);
        String outputFileName = Objects.requireNonNullElse(filename, DEFAULT_FILENAME);
        String command = Program.get(YT_DLP);
        outputFileName = Utility.cleanFilename(outputFileName);
        updateMessage("Trying to download " + outputFileName);
        String[] fullCommand = new String[]{command, "--quiet", "--progress", "-P", dir, link, "-o", outputFileName};
        ProcessBuilder pb = new ProcessBuilder(fullCommand);
        StringBuilder sb = new StringBuilder();
        for (String arg : pb.command()) sb.append(arg).append(" ");
        String msg = RUNNING_COMMAND + Program.get(Program.EXECUTABLE_NAME) + " " + sb;
        updateMessage(DOWNLOADING + outputFileName);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try {
            try (InputStream inputStream = process.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    feedback.setValue(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        result = process.waitFor();
        updateValue(result);
        String errorMessage = ((result == 0) ? SUCCESSFULLY_DOWNLOADED : FAILED_TO_DOWNLOAD) + outputFileName;
        updateMessage(errorMessage);
        updateProgress(0.0, 1.0);
        return result;
    }

    public int getResult() {
        return result;
    }
    double lastValue = 0.0;
    private void setProperties() {
        feedback.addListener(((observable, oldValue, newValue) -> {
            String regex = "";
            Matcher m = pattern.matcher(newValue);
            double value;
            if (m.find()) {
                value = Double.parseDouble(m.group(2));
                value = Math.ceil(value) / 100;
                updateProgress(value, 1.0);
            }
        }));
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
