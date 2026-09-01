package backend;

import gui.init.Environment;
import gui.support.SplitDownloadMetrics;
import gui.utils.MessageBroker;
import properties.FileState;
import properties.LinkType;
import properties.Program;
import support.DownloadMetrics;
import support.DownloadProgressListener;
import support.DownloadResult;
import support.Job;
import utils.DbConnection;
import utils.UnitConverter;
import utils.Utility;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;

import static gui.support.Constants.*;
import static init.Environment.currentSessionId;

public class FileDownloader implements Callable<DownloadResult> {
    private static final MessageBroker M = Environment.getMessageBroker();
    private static final String YT_DLP = Program.get(Program.YT_DLP);
    private final DownloadProgressListener progressListener;
    private final Job job;
    private final String downloadLink;
    private final String dir;
    private final LinkType type;
    private final AtomicLong totalTransferred = new AtomicLong();
    private String filename;

    public FileDownloader(Job job, DownloadProgressListener progressListener) {
        this.job = job;
        this.downloadLink = job.getDownloadLink();
        this.filename = Utility.cleanFilename(job.getFilename());
        this.dir = job.getDir();
        this.type = LinkType.getLinkType(this.downloadLink);
        this.progressListener = progressListener == null ? new DownloadProgressListener() {
        } : progressListener;
    }

    @Override
    public DownloadResult call() {
        if (this.downloadLink == null && LinkType.getLinkType(job.getSourceLink()).equals(LinkType.SPOTIFY)) {
            String message = "Song is exclusive to Spotify and cannot be downloaded!";
            M.msgDownloadError(message);
            return new DownloadResult(job, false, 0, -1, message);
        }

        String startDownloadingTime = timestamp();
        int fileId = -1;
        long totalSize = -1;
        long downloadedSize = 0;
        boolean success = false;
        String message = "";
        boolean started = false;

        try {
            DbConnection db = DbConnection.getInstance();
            fileId = db.prepareFileForDownload(filename, job.getSourceLink(), downloadLink, job.getDir(), startDownloadingTime, currentSessionId);

            switch (type) {
                case YOUTUBE, INSTAGRAM -> {
                    progressListener.onStart(job, -1);
                    started = true;
                    success = downloadYoutubeOrInstagram(LinkType.getLinkType(job.getSourceLink()).equals(LinkType.SPOTIFY));
                }
                case OTHER -> {
                    URL url = new URI(downloadLink).toURL();
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.connect();
                    totalSize = con.getHeaderFieldLong("Content-Length", -1);
                    if (filename.isEmpty()) {
                        String[] webPaths = url.getFile().trim().split("/");
                        filename = Utility.cleanFilename(webPaths[webPaths.length - 1]);
                        db.updateFileName(fileId, filename);
                    }
                    progressListener.onStart(job, totalSize);
                    started = true;
                    if (UnitConverter.getValue(totalSize, UnitConverter.MB) > 50 && "bytes".equalsIgnoreCase(con.getHeaderField("Accept-Ranges"))) {
                        success = splitDownload(url, totalSize);
                    } else {
                        success = downloadFile(url, totalSize);
                    }
                }
                default -> {
                    message = INVALID_LINK;
                    M.msgLinkError(INVALID_LINK);
                }
            }

            Path downloadedFile = Paths.get(dir).resolve(filename);
            if (Files.exists(downloadedFile)) {
                downloadedSize = Files.size(downloadedFile);
            }
            db.updateFileInfo(fileId, success ? FileState.COMPLETED : FileState.FAILED, timestamp(), success ? (int) downloadedSize : 0);
            if (success) {
                message = String.format(SUCCESSFULLY_DOWNLOADED_F, filename);
            } else if (message.isEmpty()) {
                message = String.format(FAILED_TO_DOWNLOAD_F, filename);
            }
        } catch (MalformedURLException | URISyntaxException e) {
            message = INVALID_LINK;
            M.msgLinkError(INVALID_LINK);
            updateFailure(fileId);
        } catch (InvalidPathException e) {
            message = "The downloaded file path (" + Paths.get(dir, filename) + ") is invalid! " + e.getMessage();
            M.msgDownloadError(message);
            updateFailure(fileId);
        } catch (UnknownHostException e) {
            message = "You are not connected to the internet!";
            M.msgDownloadError(message);
            updateFailure(fileId);
        } catch (IOException e) {
            message = String.format(FAILED_CONNECTION_F, downloadLink);
            M.msgDownloadError(message);
            updateFailure(fileId);
        } catch (SQLException e) {
            message = "Failed to update database: " + e.getMessage();
            M.msgDownloadError(message);
        } finally {
            if (started) {
                progressListener.onComplete(new DownloadResult(job, success, downloadedSize, totalSize, message));
            }
        }

        return new DownloadResult(job, success, downloadedSize, totalSize, message);
    }

    private boolean downloadYoutubeOrInstagram(boolean isSpotifySong) throws IOException {
        String[] fullCommand = new String[]{YT_DLP, "--quiet", "--progress", "-P", dir, downloadLink, "-o", filename, "-t", (isSpotifySong ? "mp3" : "mp4"), "--js-runtimes", "deno:" + Program.get(Program.DENO)};
        ProcessBuilder processBuilder = new ProcessBuilder(fullCommand);
        M.msgDownloadInfo(String.format(DOWNLOADING_F, filename));
        Process process = processBuilder.start();
        try {
            if (!process.waitFor(10, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                throw new IOException(
                        "Download process timed out for \"" + filename + "\""
                );
            }

            int exitCode = process.exitValue();

            if (exitCode == 0) {
                M.msgDownloadInfo(
                        String.format(SUCCESSFULLY_DOWNLOADED_F, filename)
                );
                return true;
            }

            M.msgDownloadError(
                    String.format(FAILED_TO_DOWNLOAD_F, filename)
            );
            return false;

        } catch (InterruptedException e) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();

            throw new IOException(
                    "Failed to wait for download process to finish for \"" + filename + "\"",
                    e
            );
        }
    }

    private boolean splitDownload(URL url, long fileSize) throws IOException {
        int numParts = new DownloadMetrics().getThreadCount();
        long partSize = fileSize / numParts;

        LinkedList<SplitDownloadMetrics> list = new LinkedList<>();

        for (int x = 0; x < numParts; x++) {
            long startByte = x == 0 ? 0 : ((long) x * partSize) + 1;
            long endByte = (numParts - 1) == x
                    ? fileSize
                    : ((long) x * partSize) + partSize;

            SplitDownloadMetrics sdm =
                    new SplitDownloadMetrics(x, startByte, endByte, filename, url);

            list.addLast(sdm);
            new Thread(split(sdm), "gui-split-download-" + x).start();
        }

        long previousTotal = 0;
        boolean loop = true;
        boolean stopThreads = false;

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

                boolean workersRunning;

                do {
                    workersRunning = false;

                    for (SplitDownloadMetrics sdm : list) {
                        if (sdm.running()) {
                            workersRunning = true;
                            break;
                        }
                    }

                    if (workersRunning) {
                        Utility.sleep(50);
                    }
                } while (workersRunning);

                for (SplitDownloadMetrics sdm : list) {
                    File tempFile = sdm.getFile();

                    if (tempFile != null) {
                        Files.deleteIfExists(tempFile.toPath());
                    }
                }

                return false;
            }

            long currentTotal = totalTransferred.get();

            if (currentTotal > previousTotal) {
                progressListener.onProgress(
                        job,
                        currentTotal - previousTotal,
                        currentTotal,
                        fileSize
                );

                previousTotal = currentTotal;
            }

            loop = !allDone;

            if (loop) {
                Utility.sleep(250);
            }
        }

        Path targetPath = Paths.get(dir, filename);

        try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
            long position = 0;

            for (int i = 0; i < numParts; i++) {
                File f = list.get(i).getFile();

                try (
                        FileInputStream fs = new FileInputStream(f);
                        ReadableByteChannel rbs = Channels.newChannel(fs)
                )
                {
                    fos.getChannel().transferFrom(
                            rbs,
                            position,
                            f.length()
                    );

                    position += f.length();
                }

                Files.deleteIfExists(f.toPath());
            }
        }

        M.msgDownloadInfo(
                String.format(SUCCESSFULLY_DOWNLOADED_F, filename)
        );

        return true;
    }

    private Runnable split(SplitDownloadMetrics sdm) {
        return () -> {
            InputStream in = null;
            FileOutputStream fos = null;
            try {
                URL url = sdm.getUrl();
                fos = sdm.getFileOutputStream();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Range", "bytes=" + sdm.getStart() + "-" + sdm.getEnd());
                con.connect();
                in = con.getInputStream();
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalTransferred.addAndGet(bytesRead);
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
                } catch (Exception ignored) {
                }
                try {
                    Objects.requireNonNull(in).close();
                } catch (Exception ignored) {
                }
            }
        };
    }

    private boolean downloadFile(URL url, long fileLength) throws IOException {
        Path path = Paths.get(dir, filename);
        M.msgDownloadInfo(String.format(DOWNLOADING_F, filename));
        try (
                InputStream in = url.openStream();
                FileOutputStream out = new FileOutputStream(path.toFile())
        )
        {
            int bytesRead;
            long totalBytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                progressListener.onProgress(job, bytesRead, totalBytesRead, fileLength);
            }
        }
        M.msgDownloadInfo(String.format(SUCCESSFULLY_DOWNLOADED_F, filename));
        return true;
    }

    private void updateFailure(int fileId) {
        if (fileId < 0) {
            return;
        }
        try {
            DbConnection.getInstance().updateFileInfo(fileId, FileState.FAILED, timestamp(), 0);
        } catch (SQLException ignored) {
        }
    }

    private String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
