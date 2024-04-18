package backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gui.init.Environment;
import gui.support.SplitDownloadMetrics;
import gui.utils.MessageBroker;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import properties.LinkType;
import properties.Program;
import support.DownloadMetrics;
import support.Job;
import ui.UIController;
import utils.UnitConverter;
import utils.Utility;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gui.support.Colors.GREEN;
import static gui.support.Colors.RED;
import static gui.support.Constants.*;

public class FileDownloader extends Task<Integer> {
    private static final MessageBroker M = Environment.getMessageBroker();
    private static final String YT_DLP = Program.get(Program.YT_DLP);
    private final StringProperty progressProperty = new SimpleStringProperty();
    private String link;
    private final String filename;
    private final String dir;
    private final String spotifySongMetadata;
    private final LinkType type;
    private int exitCode = 1;
    private boolean done;
    private final Job job;
    private final AtomicLong totalTransferred = new AtomicLong();
    private final AtomicLong totalSpeedValue = new AtomicLong();
    private static final String PERCENTAGE_REGEX = "([0-9.]+)%";
    private static final Pattern PERCENTAGE_PATTERN = Pattern.compile(PERCENTAGE_REGEX);
    private static final String ETA_REGEX = "(\\d+\\.\\d+)([a-zA-Z/]+) ETA ([0-9:]+)";
    private static final Pattern ETA_PATTERN = Pattern.compile(ETA_REGEX);
    private int updateCount;
    private double speedSum = 0.0;
    private double lastProgress;

    public FileDownloader(Job job, StringProperty linkProperty, StringProperty dirProperty, StringProperty filenameProperty, StringProperty downloadMessage, IntegerProperty transferSpeedProperty, DoubleProperty progressProperty) {
        this.job = job;
        this.link = job.getLink();
        this.filename = Utility.cleanFilename(job.getFilename());
        this.dir = job.getDir();
        this.type = LinkType.getLinkType(link);
        if (this.type.equals(LinkType.SPOTIFY)) {
            this.spotifySongMetadata = job.getSpotifyMetadataJson();
        } else {
            this.spotifySongMetadata = null;
        }
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
    protected Integer call() {
        updateProgress(0, 1);
        sendInfoMessage(String.format(TRYING_TO_DOWNLOAD_F, filename));
        switch (type) {
            case YOUTUBE, INSTAGRAM -> downloadYoutubeOrInstagram(false);
            case SPOTIFY -> {
                link = getSpotifyDownloadLink(spotifySongMetadata);
                if (link == null) {
                    break;
                }
                downloadYoutubeOrInstagram(true);
            }
            case OTHER -> splitDecision();
            default -> sendFinalMessage(INVALID_LINK);
        }
        updateProgress(0.0, 1.0);
        done = true;
        return exitCode;
    }

    private void downloadYoutubeOrInstagram(boolean isSpotifySong) {
        String[] fullCommand = new String[]{YT_DLP, "--quiet", "--progress", "-P", dir, link, "-o", filename, "-f", (isSpotifySong ? "bestaudio" : "mp4")};
        ProcessBuilder processBuilder = new ProcessBuilder(fullCommand);
        sendInfoMessage(String.format(DOWNLOADING_F, filename));
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            M.msgDownloadError("Failed to start download process for \"" + filename + "\"");
        } catch (Exception e) {
            String msg = e.getMessage();
            String[] messageArray = msg.split(",");
            if (messageArray.length >= 1 && messageArray[0].toLowerCase().trim().replaceAll(" ", "").contains("cannotrunprogram")) { // If yt-dlp program is not marked as executable
                M.msgDownloadError(DRIFTY_COMPONENT_NOT_EXECUTABLE);
            } else if (messageArray.length >= 1 && "permissiondenied".equals(messageArray[1].toLowerCase().trim().replaceAll(" ", ""))) { // If a private YouTube / Instagram video is asked to be downloaded
                M.msgDownloadError(PERMISSION_DENIED);
            } else if ("videounavailable".equals(messageArray[0].toLowerCase().trim().replaceAll(" ", ""))) { // If YouTube / Instagram video is unavailable
                M.msgDownloadError(VIDEO_UNAVAILABLE);
            } else {
                M.msgDownloadError("An Unknown Error occurred! " + e.getMessage());
            }
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(process).getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                progressProperty.setValue(line);
            }
        } catch (IOException e) {
            M.msgDownloadError("Failed to read download process status for \"" + filename + "\"");
        }
        try {
            exitCode = Objects.requireNonNull(process).waitFor();
        } catch (InterruptedException e) {
            M.msgDownloadError("Failed to wait for download process to finish for \"" + filename + "\"");
        }
        if (isSpotifySong && exitCode == 0) {
            sendInfoMessage("Converting to mp3 ...");
            String conversionProcessMessage = Utility.convertToMp3(Paths.get(dir, filename).toAbsolutePath());
            if (conversionProcessMessage.contains("Failed")) {
                sendFinalMessage(conversionProcessMessage);
                exitCode = 1;
            } else {
                sendFinalMessage("Successfully converted to mp3!");
            }
        }
        sendFinalMessage("");
    }

    private void splitDecision() {
        long fileSize;
        try {
            URL url = new URI(link).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            fileSize = con.getHeaderFieldLong("Content-Length", -1);
        } catch (MalformedURLException | URISyntaxException e) {
            M.msgLinkError(INVALID_LINK);
            exitCode = 1;
            return;
        } catch (UnknownHostException e) {
            M.msgDownloadError("You are not connected to the internet!");
            exitCode = 1;
            return;
        } catch (IOException e) {
            M.msgDownloadError(String.format(FAILED_CONNECTION_F, link));
            exitCode = 1;
            return;
        }
        if (UnitConverter.getValue(fileSize, UnitConverter.MB) > 50) {
            splitDownload();
        } else {
            downloadFile();
        }
    }

    private Runnable split(SplitDownloadMetrics sdm) {
        return () -> {
            InputStream in = null;
            HttpURLConnection con;
            FileOutputStream fos = null;
            try {
                URL url = sdm.getUrl();
                long start = sdm.getStart();
                long end = sdm.getEnd();
                fos = sdm.getFileOutputStream();
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Range", "bytes=" + start + "-" + end); // stating how many bytes of data to be sent by the server.
                con.connect();
                in = con.getInputStream();
                byte[] buffer = new byte[8192]; // 8KB per thread
                int bytesRead;
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
                    Objects.requireNonNull(fos).close();
                    Objects.requireNonNull(in).close();
                } catch (IOException ignored) {
                }
            }
        };
    }

    private void splitDownload() {
        String message = "";
        URL url = null;
        String path = job.getFile().getAbsolutePath();
        try {
            int numParts = new DownloadMetrics().getThreadCount();
            url = new URI(link).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            long fileSize = con.getHeaderFieldLong("Content-Length", -1);
            long partSize = fileSize / numParts;

            LinkedList<SplitDownloadMetrics> list = new LinkedList<>();
            for (int x = 0; x < numParts; x++) {
                long startByte = x == 0 ? 0 : (x * partSize) + 1;
                long endByte = (numParts - 1) == x ? fileSize : ((x * partSize) + partSize);
                SplitDownloadMetrics sdm = new SplitDownloadMetrics(x, startByte, endByte, filename, url);
                list.addLast(sdm);
                new Thread(split(sdm)).start();
            }
            String totalSize = UnitConverter.format(fileSize, 2);
            boolean loop = true;
            boolean stopThreads = false;
            long start = System.currentTimeMillis();
            long end;
            while (loop) {
                boolean allDone = true;
                for (SplitDownloadMetrics sdm : list) {
                    if (sdm.failed()) {
                        stopThreads = true;
                    }
                    if (sdm.running()) {
                        allDone = false;
                    }
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
                    String totalDownloaded = UnitConverter.format(totalBytes, 2);
                    double bitsTransferred = (double) totalSpeedValue.get() / seconds;
                    String msg = "Downloading at " + UnitConverter.format(bitsTransferred, 2) + "/s (Downloaded " + totalDownloaded + " out of " + totalSize + ")";
                    updateMessage(msg);
                    totalSpeedValue.set(0);
                }
                loop = !allDone;
            }
            updateProgress(0.0, 1.0);
            updateMessage("Merging Files");
            String msg = "Saving file to download folder";
            FileOutputStream fos = new FileOutputStream(job.getFile());
            long position = 0;
            for (int i = 0; i < numParts; i++) {
                updateMessage(msg);
                File f = list.get(i).getFile();
                FileInputStream fs = new FileInputStream(f);
                ReadableByteChannel rbs = Channels.newChannel(fs);
                fos.getChannel().transferFrom(rbs, position, f.length());
                position += f.length();
                msg = msg + ".";
                fs.close();
                rbs.close();
            }
            fos.close();
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
        } catch (UnknownHostException e) {
            message = "You are not connected to the internet!";
            exitCode = 1;
        } catch (IOException e) {
            message = String.format(FAILED_CONNECTION_F, url);
            exitCode = 1;
        } catch (NullPointerException e) {
            message = FAILED_READING_STREAM;
            exitCode = 1;
        }
        sendFinalMessage(message);
    }

    private void downloadFile() {
        String message = "";
        Path path = Paths.get(dir, filename);
        URL url = null;
        try {
            url = new URI(link).toURL();
            URLConnection con = url.openConnection();
            con.connect();

            long fileLength = con.getHeaderFieldLong("Content-Length", -1);
            sendInfoMessage(String.format(DOWNLOADING_F, filename));
            String totalSize = UnitConverter.format(fileLength, 2);
            try (
                    InputStream in = con.getInputStream();
                    FileOutputStream out = new FileOutputStream(path.toFile())
                    )
            {
                int bytesRead;
                long totalBytesRead = 0;
                long start = System.currentTimeMillis();
                double bytesInTime = 0.0;
                byte[] buffer = new byte[8192]; // 8KB
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    double progressValue = (double) totalBytesRead / fileLength;
                    updateProgress(progressValue, 1.0);
                    bytesInTime += bytesRead;
                    long end = System.currentTimeMillis();
                    double seconds = (end - start) / 1000.0;
                    if (seconds >= 1.5) {
                        start = end;
                        String totalDownloaded = UnitConverter.format(totalBytesRead, 2);
                        double bytesTransferredPerSecond = bytesInTime / seconds;
                        String msg = "Downloading at " + UnitConverter.format(bytesTransferredPerSecond, 2) + "/s (Downloaded " + totalDownloaded + " out of " + totalSize + ")";
                        updateMessage(msg);
                        bytesInTime = 0;
                    }
                }
                exitCode = 0;
            }
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
            message = FAILED_READING_STREAM;
            exitCode = 1;
        }
        sendFinalMessage(message);
    }

    private void setProperties() {
        progressProperty.addListener(((observable, oldValue, newValue) -> {
            Matcher m1 = PERCENTAGE_PATTERN.matcher(newValue);
            Matcher m2 = ETA_PATTERN.matcher(newValue);
            double value = 0.0;
            double progress = 0.0;
            if (m1.find()) {
                /*
                As yt-dlp throws its progress numbers all over the place to where
                sometimes it's lower than it was the previous time, causing the
                progress bar to bounce forward and backward during the download, which
                did not look proper since progress bars are only supposed to go up.

                In order to minimize this as much as possible, I used the
                max method of Math to keep it locked in. However, sometimes the
                value jumps to over a hundred which will cause the progress bar to
                stick at that max number.

                So I put a check in the second regex match (m2.find()) because if
                we are still matching, then the file is still downloading, and it
                checks the value of progress and if it's too high, then it sets the
                lastProgress back to the number that makes sense.
                 */
                value = parseStringToDouble(m1.group(1));
                progress = Math.max(value, lastProgress);
                lastProgress = progress;
                updateProgress(progress / 100, 1.0);
            }
            if (m2.find()) {
                updateCount++;
                double spd = parseStringToDouble(m2.group(1));
                speedSum += spd;
                if (updateCount == 50) {
                    updateCount = 0;
                    double averageSpeed = speedSum / 35;
                    speedSum = 0.0;
                    String speed = String.format("%06.2f", averageSpeed);
                    String units = m2.group(2);
                    String[] parts = m2.group(3).split(":");
                    int hours = parts.length > 0 ? Utility.parseStringToInt(parts[0]) : 0;
                    int minutes = parts.length > 1 ? Utility.parseStringToInt(parts[1]) : 0;
                    int seconds = parts.length > 2 ? Utility.parseStringToInt(parts[2]) : 0;
                    String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    updateMessage(speed + " " + units + " ETA " + time);
                    if (progress > 99) {
                        lastProgress = value;
                    }
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
            UIController.setDownloadInfoColor(GREEN);
            msg = message.isEmpty() ? String.format(SUCCESSFULLY_DOWNLOADED_F, filename) : message;
            M.msgDownloadInfo(msg);
        } else {
            UIController.setDownloadInfoColor(RED);
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

    public String getSpotifyDownloadLink(String spotifyMetadataJson) {
        sendInfoMessage("Trying to get download link...");
        if (spotifyMetadataJson == null) {
            sendFinalMessage("Song metadata is missing!");
            return null;
        }
        JsonObject jsonObject = JsonParser.parseString(spotifyMetadataJson).getAsJsonObject();
        String songName = jsonObject.get("songName").getAsString();
        int duration = jsonObject.get("duration").getAsInt();
        JsonArray artists = jsonObject.get("artists").getAsJsonArray();
        ArrayList<String> artistNames = new ArrayList<>(artists.size());
        for (int i = 0; i < artists.size(); i++) {
            artistNames.add(artists.get(i).getAsString());
        }
        String query = (String.join(", ", artistNames) + " - " + songName).toLowerCase();
        ArrayList<HashMap<String, Object>> searchResults = Utility.getYoutubeSearchResults(query, true);
        boolean searchedWithFilters = true;
        if (searchResults == null) {
            M.msgLogError("Failed to get search results for the song with filters! Trying without filters ...");
            searchResults = Utility.getYoutubeSearchResults(query, false);
            searchedWithFilters = false;
            if (searchResults == null) {
                sendFinalMessage("Song is exclusive to Spotify and cannot be downloaded!");
                return null;
            }
        }
        String matchedId = Utility.getMatchingVideoID(Objects.requireNonNull(searchResults), duration, artistNames);
        if (matchedId.isEmpty()) {
            if (searchedWithFilters) {
                M.msgLogError("Failed to get a matching video ID for the song with filters! Trying without filters ...");
                searchResults = Utility.getYoutubeSearchResults(query, false);
                matchedId = Utility.getMatchingVideoID(Objects.requireNonNull(searchResults), duration, artistNames);
                if (matchedId.isEmpty()) {
                    sendFinalMessage("Song is exclusive to Spotify and cannot be downloaded!");
                    return null;
                }
            } else {
                sendFinalMessage("Song is exclusive to Spotify and cannot be downloaded!");
                return null;
            }
        }
        sendInfoMessage("Download link retrieved successfully!");
        return "https://www.youtube.com/watch?v=" + matchedId;
    }

    private double parseStringToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
