package GUI.Forms;

import Backend.DownloaderThread;
import Enums.LinkType;
import Enums.Program;
import Enums.Unit;
import GUI.Support.Job;
import GUI.Support.SplitDownloadMetrics;
import Utils.Environment;
import Utils.MessageBroker;
import Utils.Utility;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utils.DriftyConstants.*;

public class DownloadFile extends Task<Integer> {
    private static final MessageBroker M = Environment.getMessageBroker();
    private final String YT_DLP = Program.get(Program.YT_DLP);
    private final StringProperty progressProperty = new SimpleStringProperty();
    private final String link;
    private final String filename;
    private final String dir;
    private final LinkType type;
    private int exitCode = 1;
    private boolean done = false;
    private final Job job;
    private final DecimalFormat format = new DecimalFormat("#.00");
    private final AtomicLong totalTransferred = new AtomicLong();
    private final AtomicLong totalSpeedValue = new AtomicLong();

    public DownloadFile(Job job,
                        StringProperty linkProperty, StringProperty dirProperty, StringProperty filenameProperty,
                        StringProperty downloadMessage,
                        IntegerProperty transferSpeedProperty,
                        DoubleProperty progressProperty) {
        this.job = job;
        this.link = job.getLink();
        this.filename = Utility.cleanFilename(job.getFilename());
        this.dir = job.getDir();
        this.type = LinkType.fromLink(link);
        setProperties();
        Platform.runLater(() -> {
            linkProperty.setValue(link);
            filenameProperty.setValue(filename);
            dirProperty.setValue(dir);
            progressProperty.bind(this.progressProperty());
            downloadMessage.bind(this.messageProperty());
            transferSpeedProperty.bind(this.valueProperty());
        });
    }

    @Override
    protected Integer call() throws IOException, InterruptedException {
        updateProgress(0, 1);
        sendInfoMessage(String.format(TRYING_TO_DOWNLOAD_F, filename));
        switch (type) {
            case YOU_TUBE, INSTAGRAM -> downloadYoutubeOrInstagram();
            case OTHER -> splitDownload();
        }
        updateProgress(0.0, 1.0);
        done = true;
        return exitCode;
    }

    private void downloadYoutubeOrInstagram() throws InterruptedException, IOException {
        String[] fullCommand = new String[]{YT_DLP, "--quiet", "--progress", "-P", dir, link, "-o", filename};
        ProcessBuilder processBuilder = new ProcessBuilder(fullCommand);
        sendInfoMessage(String.format(DOWNLOADING_F, filename));
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                progressProperty.setValue(line);
                //System.out.println(line);
            }
        }
        exitCode = process.waitFor();
        sendFinalMessage("");
    }

/*
    private String getValueString(double baseValue) {
        double total = c.convert(baseValue, BYTE);
        String result = total + " Bytes";
        if (total > 1000) {
            total = c.convert(baseValue, KILOBYTE_B1000);
            result = c.convertToString(baseValue, KILOBYTE_B1000);
        }
        if (total > 1000) {
            total = c.convert(baseValue, MEGABYTE_B1000);
            result = c.convertToString(baseValue, MEGABYTE_B1000);
        }
        if (total > 1000) {
            result = c.convertToString(baseValue, GIGABYTE_B1000);
        }
        return result;
    }
*/

    private Runnable split(SplitDownloadMetrics sdm) {
        return () -> {
            InputStream in = null;
            HttpURLConnection con;
            FileOutputStream fos = null;
            int id = sdm.getId();
            try {
                URL url = sdm.getUrl();
                long start = sdm.getStart();
                long end = sdm.getEnd();
                fos = sdm.getFileOutputStream();
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Range", "bytes=" + start + "-" + end); // stating how many bytes of data to be sent by the server.
                con.connect();
                in = con.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                long totalBytesRead = 0;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalTransferred.addAndGet(bytesRead);
                    totalSpeedValue.addAndGet(bytesRead);
                    if (sdm.stop()) {
                        sdm.setFailed();
                        break;
                    }
                }
                sdm.setSuccess();
            } catch (IOException ignored) {
                sdm.setFailed();
            } finally {
                try {
                    fos.close();
                    in.close();
                } catch (IOException ignored) {
                }
            }
        };
    }

    private void splitDownload() {
        try {
            long numParts = 3L;
            URL url = new URI(link).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            long fileSize = con.getHeaderFieldLong("Content-Length", -1);
            long partSize = fileSize / numParts;

            LinkedList<SplitDownloadMetrics> list = new LinkedList<>();
            for (int x = 0; x < numParts; x++) {
                long startByte = (x == 0) ? 0 : (x * partSize) + 1;
                long endByte = ((numParts - 1) == x) ? fileSize : ((x * partSize) + partSize);
                SplitDownloadMetrics sdm = new SplitDownloadMetrics(x, startByte, endByte, filename, url);
                list.addLast(sdm);
                new Thread(split(sdm)).start();
            }
            String totalSize = Unit.format(fileSize, 2);
            boolean loop = true;
            boolean stopThreads = false;
            long start = System.currentTimeMillis();
            long end;
            while (loop) {
                boolean allDone = true;
                for (SplitDownloadMetrics sdm : list) {
                    if (sdm.failed())
                        stopThreads = true;
                    if (sdm.running())
                        allDone = false;
                }
                if (stopThreads) {
                    for (SplitDownloadMetrics sdm : list) {
                        sdm.setStop();
                    }
                }
                double progress = (double) (totalTransferred.get()) / fileSize;
                updateProgress(progress, 1.0);
                end = System.currentTimeMillis();
                double seconds = (end - start) / 1000.0;
                if (seconds >= 1.5) {
                    start = end;
                    long totalBytes = totalTransferred.get();
                    String totalDownloaded = Unit.format(totalBytes, 2);
                    double bitsTransferred = (double) totalSpeedValue.get() / seconds;
                    String msg = "Downloading " + totalSize + " at " + Unit.format(bitsTransferred, 2) + "/s (Total: " + totalDownloaded + ")";
                    updateMessage(msg);
                    totalSpeedValue.set(0);
                }
                loop = !allDone;
            }
            updateProgress(0.0, 1.0);
            updateMessage("Merging Files");
            FileOutputStream fos = new FileOutputStream(job.getFile());
            long position = 0;
            for (int i = 0; i < numParts; i++) {
                File f = list.get(i).getFile();
                FileInputStream fs = new FileInputStream(f);
                ReadableByteChannel rbs = Channels.newChannel(fs);
                fos.getChannel().transferFrom(rbs, position, f.length());
                position += f.length();
            }
            fos.close();
            exitCode = 0;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyFileContents(File sourceFile, OutputStream outputStream) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            byte[] buffer = new byte[8192]; // Adjust buffer size as needed
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private void downloadFile() {
        String message = "";
        Path path = Paths.get(dir, filename);
        URL url = null;
        FileOutputStream out = null;
        InputStream in = null;
        try {
            url = new URI(link).toURL();
            URLConnection con = url.openConnection();
            con.connect();

            long fileLength = con.getHeaderFieldLong("Content-Length", -1);
            String ff = Unit.format(fileLength, 2);
            String acceptRange = con.getHeaderField("Accept-Ranges");

            sendInfoMessage(String.format(DOWNLOADING_F, filename));
            String totalSize = Unit.format(fileLength, 2);
            in = con.getInputStream();
            out = new FileOutputStream(path.toFile());
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            long start = System.currentTimeMillis();
            long end;
            double bytesInTime = 0.0;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                double progressValue = ((double) totalBytesRead / fileLength);
                updateProgress(progressValue, 1.0);
                bytesInTime += bytesRead;
                end = System.currentTimeMillis();
                double seconds = (end - start) / 1000.0;
                if (seconds >= 1.5) {
                    start = end;
                    ;
                    String totalDownloaded = Unit.format(totalBytesRead, 2);
                    double bitsTransferred = bytesInTime / 10 / seconds;
                    String msg = "Downloading " + totalSize + " at " + Unit.format(bitsTransferred * 100, 2) + "its/s (Total: " + totalDownloaded + ")";
                    updateMessage(msg);
                    bytesInTime = 0;
                }
            }
            exitCode = 0;
        } catch (MalformedURLException | URISyntaxException e) {
            M.msgLinkError(INVALID_LINK);
            exitCode = 1;
        } catch (SecurityException e) {
            message = String.format(WRITE_ACCESS_DENIED_F, path);
            exitCode = 1;
        } catch (FileNotFoundException e) {
            message = FILE_NOT_FOUND;
            exitCode = 1;
        } catch (IOException e) {
            message = String.format(FAILED_CONNECTION_F, url);
            exitCode = 1;
        } catch (NullPointerException e) {
            message = FAILED_READIND_STREAM;
            exitCode = 1;
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException ignored) {
            }
        }
        sendFinalMessage(message);
    }

    private static final String regex1 = "([0-9.]+)%";
    private static final Pattern p1 = Pattern.compile(regex1);
    private static final String regex2 = "(\\d+\\.\\d+)([a-zA-Z/]+) ETA ([0-9:]+)";
    private static final Pattern p2 = Pattern.compile(regex2);
    private int updateCount = 0;
    private double speedSum = 0.0;
    private double lastProgress;

    private void setProperties() {
        progressProperty.addListener(((observable, oldValue, newValue) -> {
            Matcher m1 = p1.matcher(newValue);
            Matcher m2 = p2.matcher(newValue);
            double value = 0.0;
            double progress = 0.0;
            if (m1.find()) {
                /*
                Because yt-dlp throws its progress numbers all over the place to where
                sometimes it's lower than it was the previous time, causing the
                progress bar bounce forward and backward during the download, which
                did not look proper since progress bars are only supposed to go up.

                In order to minimize this as much as possible, I used the
                max method of Math to keep it locked in. However, sometimes the
                value jumps to over a hundred which will cause the progress bar to
                stick at that max number.

                So I put a check in the second regex match (m2.find()) because if
                we are still matching then the file is still downloading, and it
                checks the value of progress and if it's too high, then it sets the
                lastProgress back to the number that makes sense.
                 */
                value = Double.parseDouble(m1.group(1));
                progress = Math.max(value, lastProgress);
                lastProgress = progress;
                updateProgress(progress / 100, 1.0);
            }
            if (m2.find()) {
                updateCount++;
                double spd = Double.parseDouble(m2.group(1));
                speedSum += spd;
                if (updateCount == 50) {
                    updateCount = 0;
                    double averageSpeed = speedSum / 35;
                    speedSum = 0.0;
                    String speed = String.format("%06.2f", averageSpeed);
                    String units = m2.group(2);
                    String[] parts = m2.group(3).split(":");
                    int hours = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
                    int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                    int seconds = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
                    String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    updateMessage(speed + " " + units + " ETA " + time);
                    if (progress > 99)
                        lastProgress = value;
                }
            }
        }));
    }

    private void sendInfoMessage(String message) {
        updateMessage(message);
        M.msgDownloadInfo(message);
    }

    private void sendFinalMessage(String message) {
        String msg;
        if (exitCode == 0) {
            msg = message.isEmpty() ? String.format(SUCCESSFULLY_DOWNLOADED_F, filename) : message;
            M.msgDownloadInfo(msg);
        }
        else {
            msg = message.isEmpty() ? String.format(FAILED_TO_DOWNLOAD_F, filename) : message;
            M.msgDownloadError(msg);
        }
        updateMessage(msg);
    }

    public boolean notDone() {
        return !done;
    }

    public int getExitCode() {
        return exitCode;
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
