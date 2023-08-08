package GUI.experiment;

import Enums.DriftyConfig;
import Enums.Format;
import Enums.Out;
import GUI.Support.StringPropertyPrintStream;
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

public class DownloadFile extends Task {

    private final StringProperty standardOut = new SimpleStringProperty();
    private final StringProperty errorOut = new SimpleStringProperty();
    private final String regex = "(\\[download]\\s+)(\\d+\\.\\d+)(%)";
    private final Pattern pattern = Pattern.compile(regex);
    private final String link;
    private final String filename;
    private final String dir;

    public DownloadFile(String link, String filename, String dir) {
        this.link = link;
        this.filename = filename;
        this.dir = dir;
        captureOutputs();
        setProperties();
    }

    @Override
    protected Object call() throws Exception {
        String outputFileName = Objects.requireNonNullElse(filename, DEFAULT_FILENAME);
        String command = DriftyConfig.getConfig(DriftyConfig.YT_DLP_COMMAND);
        outputFileName = Utility.cleanFilename(outputFileName);
        updateMessage("Trying to download " + outputFileName);
        String ext = FilenameUtils.getExtension(outputFileName).toLowerCase();
        String[] fullCommand = (Format.isValid(ext)) ?
                new String[]{command, "--quiet", "--progress", "-P", dir, link, "-f", ext, "-o", outputFileName} :
                new String[]{command, "--quiet", "--progress", "-P", dir, link, "-o", outputFileName};
        ProcessBuilder pb = new ProcessBuilder(fullCommand);
        StringBuilder sb = new StringBuilder();
        for (String arg : pb.command()) sb.append(arg).append(" ");
        String msg = RUNNING_COMMAND + DriftyConfig.getConfig(DriftyConfig.NAME) + " " + sb;
        updateMessage(DOWNLOADING + outputFileName);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try {
            try (InputStream inputStream = process.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (this.isCancelled()) {
                        break;
                    }
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int result = process.waitFor();
        String errorMessage = ((result == 0) ? SUCCESSFULLY_DOWNLOADED : FAILED_TO_DOWNLOAD) + outputFileName;
        updateMessage(errorMessage);
        return getValue();
    }

    private void captureOutputs() {
        ByteArrayOutputStream baosStandard = new ByteArrayOutputStream();
        PrintStream printOutStandard = new StringPropertyPrintStream(baosStandard, standardOut, Out.STANDARD);
        ByteArrayOutputStream baosError = new ByteArrayOutputStream();
        PrintStream printOutError = new StringPropertyPrintStream(baosError, errorOut, Out.ERROR);
        System.setOut(printOutStandard);
        System.setErr(printOutError);
    }

    private void setProperties() {
        standardOut.addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.contains(System.lineSeparator())) {
                    setProgress(newValue);
                }
            }
        }));
    }

    private void setProgress(String line) {
        if (!line.isEmpty()) {
            String[] lines = line.split(System.lineSeparator());
            if (lines.length > 2) {
                String text = lines[lines.length - 2];
                Matcher matcher = pattern.matcher(text);
                double value = 0.0;
                while (matcher.find()) {
                    value = Double.parseDouble(matcher.group(2)) / 100;
                }
                this.updateProgress(value, 1.0);
            }
        }
    }


}
