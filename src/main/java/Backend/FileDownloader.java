package Backend;

import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Program;
import Utils.Environment;
import Utils.MessageBroker;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Enums.Program.YT_DLP;
import static Utils.DriftyConstants.*;
import static Utils.Utility.isInstagramLink;
import static Utils.Utility.isYoutubeLink;

/**
 * This class deals with downloading the file.
 */
public class FileDownloader implements Runnable {
    private final MessageBroker messageBroker = Environment.getMessageBroker();
    private final DownloadMetrics downloadMetrics;
    private final int numberOfThreads;
    private final long threadingThreshold;
    private final String dir;
    private String fileName;
    private String link;
    private URL url;
    private String yt_dlpProgramName;

    public FileDownloader(String link, String fileName, String dir) {
        this.link = link;
        this.fileName = fileName;
        this.dir = dir;
        this.downloadMetrics = new DownloadMetrics();
        this.numberOfThreads = downloadMetrics.getThreadCount();
        this.threadingThreshold = downloadMetrics.getThreadingThreshold();
        downloadMetrics.setMultithreaded(false);
        setYt_dlpProgramName(Program.get(Program.EXECUTABLE_NAME));
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
                boolean supportsMultithreading = downloadMetrics.isMultithreaded();
                long totalSize = downloadMetrics.getTotalSize();
                if (supportsMultithreading) {
                    List<FileOutputStream> fileOutputStreams = new ArrayList<>(numberOfThreads);
                    List<Long> partSizes = new ArrayList<>(numberOfThreads);
                    List<File> tempFiles = new ArrayList<>(numberOfThreads);
                    List<DownloaderThread> downloaderThreads = new ArrayList<>(numberOfThreads);
                    long partSize = Math.floorDiv(totalSize, numberOfThreads);
                    long start, end;
                    FileOutputStream fileOut;
                    File file;
                    for (int i = 0; i < numberOfThreads; i++) {
                        file = File.createTempFile(fileName.hashCode() + String.valueOf(i), ".tmp");
                        file.deleteOnExit(); // Deletes temporary file when JVM exits
                        fileOut = new FileOutputStream(file);
                        start = (i == 0) ? 0 : ((i * partSize) + 1); // The start of the range of bytes to be downloaded by the thread
                        end = ((numberOfThreads - 1) == i) ? totalSize : ((i * partSize) + partSize); // The end of the range of bytes to be downloaded by the thread
                        DownloaderThread downloader = new DownloaderThread(url, fileOut, start, end);
                        downloader.start();
                        fileOutputStreams.add(fileOut);
                        partSizes.add(end - start);
                        downloaderThreads.add(downloader);
                        tempFiles.add(file);
                    }

                    ProgressBarThread progressBarThread = new ProgressBarThread(fileOutputStreams, partSizes, fileName, dir, totalSize, downloadMetrics);
                    progressBarThread.start();
                    messageBroker.sendMessage(DOWNLOADING + "\"" + fileName + "\" ...", MessageType.INFO, MessageCategory.DOWNLOAD);
                    // check if all the files are downloaded
                    try {
                        while (!mergeDownloadedFileParts(fileOutputStreams, partSizes, downloaderThreads, tempFiles)) {
                            Thread.sleep(1000);
                        }
                        downloadMetrics.setActive(false);
                        // keep the main thread from closing the IO for short amt. of time so UI thread can finish and output
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {}
                    } catch (InterruptedException ignored) {}
                } else {
                    InputStream urlStream = url.openStream();
                    readableByteChannel = Channels.newChannel(urlStream);
                    FileOutputStream fos = new FileOutputStream(getDir() + fileName);
                    ProgressBarThread progressBarThread = new ProgressBarThread(fos, totalSize, fileName, downloadMetrics);
                    progressBarThread.start();
                    messageBroker.sendMessage(DOWNLOADING + "\"" + fileName + "\" ...", MessageType.INFO, MessageCategory.DOWNLOAD);
                    fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    downloadMetrics.setActive(false);
                    // keep the main thread from closing the IO for a short amount of time so UI thread can finish and give output
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ignored) {}
                }
            } catch (SecurityException e) {
                messageBroker.sendMessage("Write access to \"" + dir + fileName + "\" denied !", MessageType.ERROR, MessageCategory.DOWNLOAD);
            } catch (FileNotFoundException fileNotFoundException) {
                messageBroker.sendMessage(FILE_NOT_FOUND, MessageType.ERROR, MessageCategory.DOWNLOAD);
            } catch (IOException e) {
                messageBroker.sendMessage(FAILED_TO_DOWNLOAD_CONTENTS + e.getMessage(), MessageType.ERROR, MessageCategory.DOWNLOAD);
            }
        } catch (NullPointerException e) {
            messageBroker.sendMessage(FAILED_TO_READ_DATA_STREAM, MessageType.ERROR, MessageCategory.DOWNLOAD);
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
        messageBroker.sendMessage("Trying to download \"" + fileDownloadMessage + "\" ...", MessageType.INFO, MessageCategory.DOWNLOAD);
        ProcessBuilder processBuilder = new ProcessBuilder(Program.get(YT_DLP), "--quiet", "--progress", "-P", dir, link, "-o", outputFileName);
        processBuilder.inheritIO();
        messageBroker.sendMessage(DOWNLOADING + "\"" + fileDownloadMessage + "\" ...", MessageType.INFO, MessageCategory.DOWNLOAD);
        int exitValueOfYt_Dlp = -1;
        try {
            Process yt_dlp = processBuilder.start();
            yt_dlp.waitFor();
            exitValueOfYt_Dlp = yt_dlp.exitValue();
        } catch (IOException e) {
            messageBroker.sendMessage("An I/O error occurred while initialising YouTube video downloader! " + e.getMessage(), MessageType.ERROR, MessageCategory.DOWNLOAD);
        } catch (InterruptedException e) {
            messageBroker.sendMessage("The YouTube video download process was interrupted by user! " + e.getMessage(), MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
        if (exitValueOfYt_Dlp == 0) {
            messageBroker.sendMessage(SUCCESSFULLY_DOWNLOADED + fileDownloadMessage + " !", MessageType.INFO, MessageCategory.DOWNLOAD);
        } else if (exitValueOfYt_Dlp == 1) {
            messageBroker.sendMessage(FAILED_TO_DOWNLOAD + fileDownloadMessage + " !", MessageType.ERROR, MessageCategory.DOWNLOAD);
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
                if (!downloaderThread.isAlive()) throw new IOException(THREAD_ERROR_ENCOUNTERED);
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

    private void setYt_dlpProgramName(String yt_dlpProgramName) {
        this.yt_dlpProgramName = yt_dlpProgramName;
    }

    @Override
    public void run() {
        link = link.replace('\\', '/');
        if (!(link.startsWith("http://") || link.startsWith("https://"))) {
            link = "https:///" + link;
        }
        if (link.startsWith("https://github.com/") || (link.startsWith("http://github.com/"))) {
            if (!(link.endsWith("?raw=true"))) {
                link = link + "?raw=true";
            }
        }
        boolean isYouTubeLink = isYoutubeLink(link);
        boolean isInstagramLink = isInstagramLink(link);
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
                    messageBroker.sendMessage(USER_INTERRUPTION, MessageType.ERROR, MessageCategory.DOWNLOAD);
                } catch (Exception e) {
                    if (isYouTubeLink) {
                        messageBroker.sendMessage(FAILED_TO_DOWNLOAD_YOUTUBE_VIDEO, MessageType.ERROR, MessageCategory.DOWNLOAD);
                    } else {
                        messageBroker.sendMessage(FAILED_TO_DOWNLOAD_INSTAGRAM_VIDEO, MessageType.ERROR, MessageCategory.DOWNLOAD);
                    }
                    String msg = e.getMessage();
                    String[] messageArray = msg.split(",");
                    if (messageArray.length >= 1 && messageArray[0].toLowerCase().trim().replaceAll(" ", "").contains("cannotrunprogram")) { // If yt-dlp program is not marked as executable
                        messageBroker.sendMessage(DRIFTY_COMPONENT_NOT_EXECUTABLE, MessageType.ERROR, MessageCategory.DOWNLOAD);
                    } else if (messageArray.length >= 1 && messageArray[1].toLowerCase().trim().replaceAll(" ", "").equals("permissiondenied")) { // If a private YouTube / Instagram video is asked to be downloaded
                        messageBroker.sendMessage(PERMISSION_DENIED_YT_IG_VIDEO, MessageType.ERROR, MessageCategory.DOWNLOAD);
                    } else if (messageArray[0].toLowerCase().trim().replaceAll(" ", "").equals("videounavailable")) { // If YouTube / Instagram video is unavailable
                        messageBroker.sendMessage(VIDEO_UNAVAILABLE, MessageType.ERROR, MessageCategory.DOWNLOAD);
                    } else {
                        messageBroker.sendMessage("An Unknown Error occurred! " + e.getMessage(), MessageType.ERROR, MessageCategory.DOWNLOAD);
                    }
                }
            } else {
                url = new URI(link).toURL();
                URLConnection openConnection = url.openConnection();
                openConnection.connect();
                long totalSize = openConnection.getHeaderFieldLong("Content-Length", -1);
                downloadMetrics.setTotalSize(totalSize);
                String acceptRange = openConnection.getHeaderField("Accept-Ranges");
                downloadMetrics.setMultithreaded((totalSize > threadingThreshold) && (acceptRange != null) && (acceptRange.equalsIgnoreCase("bytes")));
                if (fileName.isEmpty()) {
                    String[] webPaths = url.getFile().trim().split("/");
                    fileName = webPaths[webPaths.length - 1];
                }
                messageBroker.sendMessage("Trying to download \"" + fileName + "\" ...", MessageType.INFO, MessageCategory.DOWNLOAD);
                downloadFile();
            }
        } catch (MalformedURLException | URISyntaxException e) {
            messageBroker.sendMessage(INVALID_LINK, MessageType.ERROR, MessageCategory.LINK);
        } catch (IOException e) {
            messageBroker.sendMessage(FAILED_TO_CONNECT_TO_URL + url + " !", MessageType.ERROR, MessageCategory.DOWNLOAD);
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
        messageBroker.sendMessage("Trying to download \"" + fileDownloadMessage + "\" ...", MessageType.INFO, MessageCategory.DOWNLOAD);
        ProcessBuilder processBuilder = new ProcessBuilder(Program.get(YT_DLP), "--quiet", "--progress", "-P", dir, link, "-o", outputFileName); // The command line arguments tell `yt-dlp` to download the video and to save it to the specified directory.
        processBuilder.inheritIO();
        messageBroker.sendMessage(DOWNLOADING + "\"" + fileDownloadMessage + "\" ...", MessageType.INFO, MessageCategory.DOWNLOAD);
        Process yt_dlp = processBuilder.start(); // Starts the download process
        yt_dlp.waitFor();
        int exitValueOfYt_Dlp = yt_dlp.exitValue();
        if (exitValueOfYt_Dlp == 0) {
            messageBroker.sendMessage(SUCCESSFULLY_DOWNLOADED + fileDownloadMessage + " !", MessageType.INFO, MessageCategory.DOWNLOAD);
        } else if (exitValueOfYt_Dlp == 1) {
            messageBroker.sendMessage(FAILED_TO_DOWNLOAD + fileDownloadMessage + " !", MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
    }

    public DownloadMetrics getDownloadMetrics() {
        return this.downloadMetrics;
    }
}