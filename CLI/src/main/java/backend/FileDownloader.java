package backend;

import cli.utils.Utility;
import init.Environment;
import properties.FileState;
import properties.LinkType;
import properties.Program;
import support.DownloadMetrics;
import support.DownloadProgressListener;
import support.DownloadResult;
import support.Job;
import utils.DbConnection;
import utils.MessageBroker;
import utils.UnitConverter;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static cli.support.Constants.*;
import static init.Environment.currentSessionId;
import static utils.Utility.sleep;

public class FileDownloader implements Runnable, Callable<DownloadResult> {
    private static final MessageBroker M = Environment.getMessageBroker();
    private final Job job;
    private final DownloadMetrics downloadMetrics;
    private final DownloadProgressListener progressListener;
    private final int numberOfThreads;
    private final long threadMaxDataSize;
    private final String dir;
    private final String fileLink;
    private final String downloadLink;
    private final Path directoryPath;
    private final LinkType linkType;
    private final boolean aggregateMode;
    private String fileName;
    private URL url;

    public FileDownloader(Job job) {
        this(job, null);
    }

    public FileDownloader(Job job, DownloadProgressListener progressListener) {
        this.job = job;
        this.fileLink = job.getSourceLink();
        this.downloadLink = job.getDownloadLink();
        this.linkType = LinkType.getLinkType(this.downloadLink);
        this.fileName = job.getFilename();
        this.dir = job.getDir();
        this.directoryPath = Paths.get(dir).toAbsolutePath();
        this.downloadMetrics = new DownloadMetrics();
        this.aggregateMode = progressListener != null;
        this.progressListener = progressListener == null ? new DownloadProgressListener() {
        } : progressListener;
        this.numberOfThreads = downloadMetrics.getThreadCount();
        this.threadMaxDataSize = downloadMetrics.getMultiThreadingThreshold();
        downloadMetrics.setMultithreading(false);
    }

    @Override
    public DownloadResult call() {
        String startDownloadingTime = timestamp();
        int fileId = -1;
        boolean started = false;
        long totalSize = -1;
        long downloadedSize = 0;
        boolean success = false;
        String message = "";

        try {
            DbConnection db = DbConnection.getInstance();
            fileId = db.prepareFileForDownload(fileName, fileLink, downloadLink, directoryPath.toString(), startDownloadingTime, currentSessionId);

            if (linkType.equals(LinkType.YOUTUBE) || linkType.equals(LinkType.INSTAGRAM)) {
                progressListener.onStart(job, -1);
                started = true;
                success = downloadYoutubeOrInstagram(LinkType.getLinkType(job.getSourceLink()).equals(LinkType.SPOTIFY));
            } else {
                url = new URI(downloadLink).toURL();
                URLConnection openConnection = url.openConnection();
                openConnection.connect();
                totalSize = openConnection.getHeaderFieldLong("Content-Length", 0);
                downloadMetrics.setTotalSize(totalSize);
                String acceptRange = openConnection.getHeaderField("Accept-Ranges");
                downloadMetrics.setMultithreading((totalSize > threadMaxDataSize) && ("bytes".equalsIgnoreCase(acceptRange)));
                if (fileName.isEmpty()) {
                    String[] webPaths = url.getFile().trim().split("/");
                    fileName = webPaths[webPaths.length - 1];
                    db.updateFileName(fileId, fileName);
                }
                progressListener.onStart(job, totalSize);
                started = true;
                M.msgDownloadInfo("Trying to download \"" + fileName + "\" ...");
                success = downloadMetrics.isMultithreadingEnabled() ? downloadFileInParts(totalSize) : downloadFileSequentially(totalSize);
            }

            Path downloadedFilePath = directoryPath.resolve(fileName);
            if (Files.exists(downloadedFilePath)) {
                downloadedSize = Files.size(downloadedFilePath);
            }

            db.updateFileInfo(fileId, success ? FileState.COMPLETED : FileState.FAILED, timestamp(), success ? (int) downloadedSize : 0);
            message = success ? String.format(SUCCESSFULLY_DOWNLOADED_F, fileName) : String.format(FAILED_TO_DOWNLOAD_F, fileName);
        } catch (MalformedURLException | URISyntaxException e) {
            message = INVALID_LINK;
            M.msgLinkError(INVALID_LINK);
            updateFailure(fileId);
        } catch (InvalidPathException e) {
            message = "The downloaded file path (" + directoryPath.resolve(fileName) + ") is invalid! " + e.getMessage();
            M.msgDownloadError(message);
            updateFailure(fileId);
        } catch (IOException e) {
            message = String.format(FAILED_CONNECTION_F, url == null ? downloadLink : url);
            M.msgDownloadError(message);
            updateFailure(fileId);
        } catch (SQLException e) {
            message = "An error occurred while trying to connect to the database! " + e.getMessage();
            M.msgDownloadError(message);
        } catch (RuntimeException e) {
            message = e.getMessage() == null ? String.format(FAILED_TO_DOWNLOAD_F, fileName) : e.getMessage();
            updateFailure(fileId);
        } finally {
            if (started) {
                progressListener.onComplete(new DownloadResult(job, success, downloadedSize, totalSize, message));
            }
        }

        return new DownloadResult(job, success, downloadedSize, totalSize, message);
    }

    @Override
    public void run() {
        call();
    }

    private boolean downloadFileSequentially(long totalSize) throws IOException {
        Path targetPath = directoryPath.resolve(fileName);
        long totalBytesRead = 0;
        long bytesInTime = 0;
        long start = System.currentTimeMillis();
        M.msgDownloadInfo(String.format(DOWNLOADING_F, fileName));

        try (
                InputStream in = url.openStream();
                ReadableByteChannel readableByteChannel = Channels.newChannel(in);
                FileOutputStream fos = new FileOutputStream(targetPath.toFile())
        ) {
            if (aggregateMode) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    progressListener.onProgress(job, bytesRead, totalBytesRead, totalSize);
                    bytesInTime += bytesRead;
                    maybeLogSpeed(totalSize, totalBytesRead, bytesInTime, start);
                    if ((System.currentTimeMillis() - start) >= 1500) {
                        start = System.currentTimeMillis();
                        bytesInTime = 0;
                    }
                }
            } else {
                ProgressBarThread progressBarThread = new ProgressBarThread(fos, totalSize, fileName, dir, downloadMetrics);
                progressBarThread.start();
                fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                downloadMetrics.setActive(false);
                Utility.sleep(1800);
            }
        }

        if (aggregateMode) {
            M.msgDownloadInfo(String.format(SUCCESSFULLY_DOWNLOADED_F, fileName) + OF_SIZE + UnitConverter.format(totalBytesRead, 2) + " at \"" + targetPath.toAbsolutePath() + "\"");
        }
        return true;
    }

    private boolean downloadFileInParts(long totalSize) throws IOException {
        List<FileOutputStream> fileOutputStreams = new ArrayList<>(numberOfThreads);
        List<Long> partSizes = new ArrayList<>(numberOfThreads);
        List<File> tempFiles = new ArrayList<>(numberOfThreads);
        List<DownloaderThread> downloaderThreads = new ArrayList<>(numberOfThreads);
        long partSize = Math.floorDiv(totalSize, numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            File file = Files.createTempFile(fileName.hashCode() + String.valueOf(i), ".tmp").toFile();
            FileOutputStream fileOut = new FileOutputStream(file);
            long start = i == 0 ? 0 : ((long) i * partSize) + 1;
            long end = (numberOfThreads - 1) == i ? totalSize : ((long) i * partSize) + partSize;
            DownloaderThread downloader = new DownloaderThread(url, fileOut, start, end);
            downloader.start();
            fileOutputStreams.add(fileOut);
            partSizes.add(end - start);
            downloaderThreads.add(downloader);
            tempFiles.add(file);
        }

        if (!aggregateMode) {
            ProgressBarThread progressBarThread = new ProgressBarThread(fileOutputStreams, partSizes, fileName, dir, totalSize, downloadMetrics);
            progressBarThread.start();
        }

        M.msgDownloadInfo(String.format(DOWNLOADING_F, fileName));
        boolean merged = false;
        long previousTotal = 0;
        while (!merged) {
            long currentTotal = 0;
            for (FileOutputStream fileOutputStream : fileOutputStreams) {
                currentTotal += fileOutputStream.getChannel().size();
            }
            if (aggregateMode && currentTotal > previousTotal) {
                progressListener.onProgress(job, currentTotal - previousTotal, currentTotal, totalSize);
                previousTotal = currentTotal;
            }
            merged = mergeDownloadedFileParts(fileOutputStreams, partSizes, downloaderThreads, tempFiles);
            if (!merged) {
                sleep(250);
            }
        }
        downloadMetrics.setActive(false);
        if (!aggregateMode) {
            Utility.sleep(1800);
        }
        for (File tempFile : tempFiles) {
            Files.deleteIfExists(tempFile.toPath());
        }
        return true;
    }

    public boolean mergeDownloadedFileParts(List<FileOutputStream> fileOutputStreams, List<Long> partSizes, List<DownloaderThread> downloaderThreads, List<File> tempFiles) throws IOException {
        int completed = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            FileOutputStream fileOutputStream = fileOutputStreams.get(i);
            long partSize = partSizes.get(i);
            DownloaderThread downloaderThread = downloaderThreads.get(i);
            if (fileOutputStream.getChannel().size() < partSize) {
                if (!downloaderThread.isAlive()) {
                    throw new IOException("Error encountered while downloading the file! Please try again.");
                }
            } else if (!downloaderThread.isAlive()) {
                completed++;
            }
        }

        if (completed == numberOfThreads) {
            try (FileOutputStream fos = new FileOutputStream(directoryPath.resolve(fileName).toFile())) {
                long position = 0;
                for (File f : tempFiles) {
                    try (FileInputStream fs = new FileInputStream(f); ReadableByteChannel rbs = Channels.newChannel(fs)) {
                        fos.getChannel().transferFrom(rbs, position, f.length());
                        position += f.length();
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean downloadYoutubeOrInstagram(boolean isSpotifySong) throws IOException {
        String[] fullCommand = new String[]{Program.get(Program.YT_DLP), "--quiet", "--progress", "-P", dir, downloadLink, "-o", fileName, "-t", (isSpotifySong ? "mp3" : "mp4"), "--js-runtimes", "deno:" + Program.get(Program.DENO)};
        ProcessBuilder processBuilder = new ProcessBuilder(fullCommand);
        processBuilder.inheritIO();
        M.msgDownloadInfo(String.format(DOWNLOADING_F, fileName));
        Process process = processBuilder.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Failed to wait for download process to finish for \"" + fileName + "\"", e);
        }

        int exitValueOfYtDlp = process.exitValue();
        if (exitValueOfYtDlp == 0) {
            Path downloadedFilePath = directoryPath.resolve(fileName);
            long downloadedSize = Files.exists(downloadedFilePath) ? Files.size(downloadedFilePath) : 0;
            M.msgDownloadInfo(String.format(SUCCESSFULLY_DOWNLOADED_F, fileName) + " (" + UnitConverter.format(downloadedSize, 2) + ")");
            return true;
        }

        if (exitValueOfYtDlp == 1) {
            M.msgDownloadError(String.format(FAILED_TO_DOWNLOAD_F, fileName));
            return false;
        }

        M.msgDownloadError("An Unknown Error occurred! Exit code: " + exitValueOfYtDlp);
        return false;
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

    private void maybeLogSpeed(long totalSize, long totalBytesRead, long bytesInTime, long start) {
        if (!aggregateMode) {
            return;
        }
        long end = System.currentTimeMillis();
        double seconds = (end - start) / 1000.0;
        if (seconds >= 1.5) {
            String totalDownloaded = UnitConverter.format(totalBytesRead, 2);
            double bytesTransferredPerSecond = bytesInTime / seconds;
            M.msgDownloadInfo("Downloading at " + UnitConverter.format(bytesTransferredPerSecond, 2) + "/s (Downloaded " + totalDownloaded + " out of " + UnitConverter.format(totalSize, 2) + ")");
        }
    }

    private String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
