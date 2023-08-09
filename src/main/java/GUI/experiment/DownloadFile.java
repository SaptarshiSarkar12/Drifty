package GUI.experiment;

import Enums.Program;
import Enums.Format;
import Utils.Utility;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utils.DriftyConstants.*;

public class DownloadFile extends Task<Integer> {

    private final StringProperty standardOut = new SimpleStringProperty();
    private final StringProperty errorOut = new SimpleStringProperty();
    private final StringProperty feedback = new SimpleStringProperty();
    private final String regex = "(\\[download]\\s+)(\\d+\\.\\d+)(%)";
    private final Pattern pattern = Pattern.compile(regex);
    private final String lineFeed = System.lineSeparator();
    private final String link;
    private final String filename;
    private final String dir;

    public DownloadFile(String link, String filename, String dir) {
        this.link = link;
        this.filename = filename;
        this.dir = dir;
        setProperties();
    }

    @Override
    protected Integer call() throws Exception {
        String outputFileName = Objects.requireNonNullElse(filename, DEFAULT_FILENAME);
        String command = Program.get(Program.COMMAND);
        outputFileName = Utility.cleanFilename(outputFileName);
        updateMessage("Trying to download " + outputFileName);
        String ext = FilenameUtils.getExtension(outputFileName).toLowerCase();
        String[] fullCommand = (Format.isValid(ext)) ?
                new String[]{command, "--quiet", "--progress", "-P", dir, link, "-f", ext, "-o", outputFileName} :
                new String[]{command, "--quiet", "--progress", "-P", dir, link, "-o", outputFileName};
        ProcessBuilder pb = new ProcessBuilder(fullCommand);
        StringBuilder sb = new StringBuilder();
        for (String arg : pb.command()) sb.append(arg).append(" ");
        String msg = RUNNING_COMMAND + Program.get(Program.NAME) + " " + sb;
        updateMessage(DOWNLOADING + outputFileName);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        StringBuilder sbOutput = new StringBuilder();
        try {
            try (InputStream inputStream = process.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (this.isCancelled()) {
                        break;
                    }
                    sbOutput.append(line);
                    feedback.setValue(sbOutput.toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int result = process.waitFor();
        updateValue(result);
        String errorMessage = ((result == 0) ? SUCCESSFULLY_DOWNLOADED : FAILED_TO_DOWNLOAD) + outputFileName;
        updateMessage(errorMessage);
        updateProgress(0.0,1.0);
        return result;
    }

    private void setProperties() {
        feedback.addListener(((observable, oldValue, newValue) -> {
            String[] list = newValue.split(lineFeed);
            if(list.length > 3) {
                String line = list[list.length -2];
                Matcher m = pattern.matcher(line);
                double value;
                if(m.find()) {
                    value = Double.parseDouble(m.group(2)) / 100;
                    updateProgress(value,1.0);
                }
            }
        }));
    }
}
