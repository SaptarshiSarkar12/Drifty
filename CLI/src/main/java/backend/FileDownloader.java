package backend;

import cli.utils.Utility;
import init.Environment;
import properties.Program;
import support.DownloadMetrics;
import utils.MessageBroker;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cli.support.Constants.*;
import static properties.Program.YT_DLP;
import static utils.Utility.*;

public class FileDownloader implements Runnable {
    private static final MessageBroker M = Environment.getMessageBroker();
    private final DownloadMetrics downloadMetrics;
    private final int numberOfThreads;
    private final long threadMaxDataSize;
    private final String dir;
    private String fileName;
    private final String link;
    private URL url;

    public FileDownloader(String link, String fileName, String dir) {
        link = link.replace('\\', '/');
        if (!(link.startsWith("http://") || link.startsWith("https://"))) {
            link = "https://" + link;
        }
        if (link.startsWith("https://github.com/") || (link.startsWith("http://github.com/"))) {
            if (!link.endsWith("?raw=true")) {
                link = link + "?raw=true";
            }
        }
        this.link = link;
        this.fileName = fileName;
        this.dir = dir;
        this.downloadMetrics = new DownloadMetrics();
        this.numberOfThreads = downloadMetrics.getThreadCount();
        this.threadMaxDataSize = downloadMetrics.getMultiThreadingThreshold();
        downloadMetrics.setMultithreading(false);
    }

    public String getDir() {
        if (dir.endsWith(File.separator)) {
            return dir;
        } else {
            return dir + File.separator;
        }
    }

    private void downloadFile() {
        try {
            ReadableByteChannel readableByteChannel;
            try {
                boolean supportsMultithreading = downloadMetrics.isMultithreadingEnabled();
                long totalSize = downloadMetrics.getTotalSize();
                if (supportsMultithreading) {
                    List<FileOutputStream> fileOutputStreams = new ArrayList<>(numberOfThreads);
                    List<Long> partSizes = new ArrayList<>(numberOfThreads);
                    List<File> tempFiles = new ArrayList<>(numberOfThreads);
                    List<DownloaderThread> downloaderThreads = new ArrayList<>(numberOfThreads);
                    long partSize = Math.floorDiv(totalSize, numberOfThreads);
                    long start;
                    long end;
                    FileOutputStream fileOut;
                    File file;
                    for (int i = 0; i < numberOfThreads; i++) {
                        file = File.createTempFile(fileName.hashCode() + String.valueOf(i), ".tmp");
                        file.deleteOnExit(); // Deletes temporary file when JVM exits
                        fileOut = new FileOutputStream(file);
                        start = i == 0 ? 0 : ((i * partSize) + 1); // The start of the range of bytes to be downloaded by the thread
                        end = (numberOfThreads - 1) == i ? totalSize : ((i * partSize) + partSize); // The end of the range of bytes to be downloaded by the thread
                        DownloaderThread downloader = new DownloaderThread(url, fileOut, start, end);
                        downloader.start();
                        fileOutputStreams.add(fileOut);
                        partSizes.add(end - start);
                        downloaderThreads.add(downloader);
                        tempFiles.add(file);
                    }
                    ProgressBarThread progressBarThread = new ProgressBarThread(fileOutputStreams, partSizes, fileName, getDir(), totalSize, downloadMetrics);
                    progressBarThread.start();
                    M.msgDownloadInfo(String.format(DOWNLOADING_F, fileName));
                    // check if all the files are downloaded
                    while (!mergeDownloadedFileParts(fileOutputStreams, partSizes, downloaderThreads, tempFiles)) {
                        sleep(500);
                    }
                    // keep the main thread from closing the IO for a short amount of time so UI thread can finish and output
                } else {
                    InputStream urlStream = url.openStream();
                    readableByteChannel = Channels.newChannel(urlStream);
                    FileOutputStream fos = new FileOutputStream(getDir() + fileName);
                    ProgressBarThread progressBarThread = new ProgressBarThread(fos, totalSize, fileName, getDir(), downloadMetrics);
                    progressBarThread.start();
                    M.msgDownloadInfo(String.format(DOWNLOADING_F, fileName));
                    fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    // keep the main thread from closing the IO for a short amount of time so UI thread can finish and give output
                }
                downloadMetrics.setActive(false);
                Utility.sleep(1800);
            } catch (SecurityException e) {
                M.msgDownloadError("Write access to \"" + dir + fileName + "\" denied !");
            } catch (FileNotFoundException fileNotFoundException) {
                M.msgDownloadError(FILE_NOT_FOUND);
            } catch (IOException e) {
                M.msgDownloadError(FAILED_TO_DOWNLOAD_CONTENTS + e.getMessage());
            }
        } catch (NullPointerException e) {
            M.msgDownloadError(FAILED_READING_STREAM);
        }
    }

    public void downloadFromYouTube() {
        String outputFileName = Objects.requireNonNullElse(fileName, DEFAULT_FILENAME);
        String fileDownloadMessage;
        if (outputFileName.equals(DEFAULT_FILENAME)) {
            fileDownloadMessage = "the YouTube Video";
        } else {
            fileDownloadMessage = outputFileName;
        }
        M.msgDownloadInfo("Trying to download \"" + fileDownloadMessage + "\" ...");
        ProcessBuilder processBuilder = new ProcessBuilder(Program.get(YT_DLP), "--quiet", "--progress", "-P", dir, link, "-o", outputFileName);
        processBuilder.inheritIO();
        M.msgDownloadInfo(String.format(DOWNLOADING_F, fileDownloadMessage));
        int exitValueOfYtDlp = -1;
        try {
            Process ytDlp = processBuilder.start();
            ytDlp.waitFor();
            exitValueOfYtDlp = ytDlp.exitValue();
        } catch (IOException e) {
            M.msgDownloadError("An I/O error occurred while initialising YouTube video downloader! " + e.getMessage());
        } catch (InterruptedException e) {
            M.msgDownloadError("The YouTube video download process was interrupted by user! " + e.getMessage());
        }
        if (exitValueOfYtDlp == 0) {
            M.msgDownloadInfo(String.format(SUCCESSFULLY_DOWNLOADED_F, fileDownloadMessage));
        } else if (exitValueOfYtDlp == 1) {
            M.msgDownloadError(String.format(FAILED_TO_DOWNLOAD_F, fileDownloadMessage));
        }
    }

    public boolean mergeDownloadedFileParts(List<FileOutputStream> fileOutputStreams, List<Long> partSizes, List<DownloaderThread> downloaderThreads, List<File> tempFiles) throws IOException {
        // check if all files are downloaded
        int completed = 0;
        FileOutputStream fileOutputStream;
        DownloaderThread downloaderThread;
        long partSize;
        for (int i = 0; i < numberOfThreads; i++) {
            fileOutputStream = fileOutputStreams.get(i);
            partSize = partSizes.get(i);
            downloaderThread = downloaderThreads.get(i);
            if (fileOutputStream.getChannel().size() < partSize) {
                if (!downloaderThread.isAlive()) {
                    M.msgDownloadError("Error encountered while downloading the file! Please try again.");
                }
            } else if (!downloaderThread.isAlive()) {
                completed++;
            }
        }
        // check if it is merged-able
        if (completed == numberOfThreads) {
            fileOutputStream = new FileOutputStream(getDir() + fileName);
            long position = 0;
            for (int i = 0; i < numberOfThreads; i++) {
                File f = tempFiles.get(i);
                FileInputStream fs = new FileInputStream(f);
                ReadableByteChannel rbs = Channels.newChannel(fs);
                fileOutputStream.getChannel().transferFrom(rbs, position, f.length());
                position += f.length();
            }
            fileOutputStream.close();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        boolean isYouTubeLink = isYoutube(link);
        boolean isInstagramLink = isInstagram(link);
        try {
            // If the link is of a YouTube or Instagram video, then the following block of code will execute.
            if (isYouTubeLink || isInstagramLink) {
                try {
                    if (isYouTubeLink) {
                        downloadFromYouTube();
                    } else {
                        downloadFromInstagram();
                    }
                } catch (InterruptedException e) {
                    M.msgDownloadError(USER_INTERRUPTION);
                } catch (Exception e) {
                    if (isYouTubeLink) {
                        M.msgDownloadError(YOUTUBE_DOWNLOAD_FAILED);
                    } else {
                        M.msgDownloadError(INSTAGRAM_DOWNLOAD_FAILED);
                    }
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
            } else {
                url = new URI(link).toURL();
                URLConnection openConnection = url.openConnection();
                openConnection.connect();
                long totalSize = openConnection.getHeaderFieldLong("Content-Length", -1);
                downloadMetrics.setTotalSize(totalSize);
                String acceptRange = openConnection.getHeaderField("Accept-Ranges");
                downloadMetrics.setMultithreading((totalSize > threadMaxDataSize) && ("bytes".equalsIgnoreCase(acceptRange)));
                if (fileName.isEmpty()) {
                    String[] webPaths = url.getFile().trim().split("/");
                    fileName = webPaths[webPaths.length - 1];
                }
                M.msgDownloadInfo("Trying to download \"" + fileName + "\" ...");
                downloadFile();
            }
        } catch (MalformedURLException | URISyntaxException e) {
            M.msgLinkError(INVALID_LINK);
        } catch (IOException e) {
            M.msgDownloadError(String.format(FAILED_CONNECTION_F, url));
        }
    }

    private void downloadFromInstagram() throws InterruptedException, IOException {
        String outputFileName = Objects.requireNonNullElse(fileName, DEFAULT_FILENAME);
        String fileDownloadMessage;
        if (outputFileName.equals(DEFAULT_FILENAME)) {
            fileDownloadMessage = "the Instagram Video";
        } else {
            fileDownloadMessage = outputFileName;
        }
        M.msgDownloadInfo("Trying to download \"" + fileDownloadMessage + "\" ...");
        ProcessBuilder processBuilder = new ProcessBuilder(Program.get(YT_DLP), "--quiet", "--progress", "-P", dir, link, "-o", outputFileName); // The command line arguments tell `yt-dlp` to download the video and to save it to the specified directory.
        processBuilder.inheritIO();
        M.msgDownloadInfo(String.format(DOWNLOADING_F, fileDownloadMessage));
        Process instagramDownloadProcess = processBuilder.start(); // Starts the download process
        instagramDownloadProcess.waitFor();
        int exitStatus = instagramDownloadProcess.exitValue();
        if (exitStatus == 0) {
            M.msgDownloadInfo(String.format(SUCCESSFULLY_DOWNLOADED_F, fileDownloadMessage));
        } else if (exitStatus == 1) {
            M.msgDownloadError(String.format(FAILED_TO_DOWNLOAD_F, fileDownloadMessage));
        }
    }
}
