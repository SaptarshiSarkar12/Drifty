package Backend;

import CLI.Drifty_CLI;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;
import Enums.Program;
import Utils.Environment;
import Utils.MessageBroker;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Utils.DriftyConstants.*;
import static Utils.Utility.isYoutubeLink;

/**
 * This class deals with downloading the file.
 */
public class FileDownloader implements Runnable {
    private static Process process;
    private static final MessageBroker messageBroker = Drifty.getMessageBrokerInstance();
    private static final int numberOfThreads = 3;
    private static final long threadingThreshold = 1024 * 1024 * 50;
    private static String dir;
    private static String fileName;
    private static String link;
    private static long totalSize;
    private static URL url;
    private static String yt_dlpProgramName;
    private static boolean supportsMultithreading;
    private static boolean isYt_dlpCheckedForUpdate = true;

    public FileDownloader(String link, String fileName, String dir) {
        FileDownloader.link = link;
        FileDownloader.fileName = fileName;
        FileDownloader.dir = dir;
        FileDownloader.supportsMultithreading = false;
        if (Mode.isGUI()) {
            setYt_dlpProgramName(Program.get(Program.NAME));
        }
        if (isYoutubeLink(link) || !Drifty_CLI.getIsInstagramImage()) {
            setYt_dlpProgramName(Program.get(Program.NAME));
        }
    }

    public static String getDir() {
        return dir;
    }

    private static void downloadFile() {
        try {
            ReadableByteChannel readableByteChannel;
            try {
                if (FileDownloader.supportsMultithreading) {
                    List<FileOutputStream> fileOutputStreams = new ArrayList<>(FileDownloader.numberOfThreads);
                    List<Long> partSizes = new ArrayList<>(FileDownloader.numberOfThreads);
                    List<File> tempFiles = new ArrayList<>(FileDownloader.numberOfThreads);
                    List<DownloaderThread> downloaderThreads = new ArrayList<>(FileDownloader.numberOfThreads);
                    long partSize = Math.floorDiv(totalSize, FileDownloader.numberOfThreads);
                    long start, end;
                    FileOutputStream fileOut;
                    File file;
                    for (int i = 0; i < FileDownloader.numberOfThreads; i++) {
                        file = File.createTempFile(fileName.hashCode() + String.valueOf(i), ".tmp");
                        file.deleteOnExit(); // Deletes temporary file when JVM exits
                        fileOut = new FileOutputStream(file);
                        start = (i == 0) ? 0 : ((i * partSize) + 1); // The start of the range of bytes to be downloaded by the thread
                        end = ((FileDownloader.numberOfThreads - 1) == i) ? totalSize : ((i * partSize) + partSize); // The end of the range of bytes to be downloaded by the thread
                        DownloaderThread downloader = new DownloaderThread(url, fileOut, start, end);
                        downloader.start();
                        fileOutputStreams.add(fileOut);
                        partSizes.add(end - start);
                        downloaderThreads.add(downloader);
                        tempFiles.add(file);
                    }

                    ProgressBarThread progressBarThread = new ProgressBarThread(fileOutputStreams, partSizes, fileName, totalSize);
                    progressBarThread.start();
                    messageBroker.sendMessage(DOWNLOADING + fileName + " ...", MessageType.INFO, MessageCategory.DOWNLOAD);
                    // check if all the files are downloaded
                    try {
                        while (!mergeDownloadedFileParts(fileOutputStreams, partSizes, downloaderThreads, tempFiles)) {
                            Thread.sleep(1000);
                        }
                        progressBarThread.setDownloading(false);
                        // keep the main thread from closing the IO for short amt. of time so UI thread can finish and output
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {}
                    } catch (InterruptedException ignored) {}
                } else {
                    InputStream urlStream = url.openStream();
                    System.out.println();
                    readableByteChannel = Channels.newChannel(urlStream);
                    FileOutputStream fos = new FileOutputStream(dir + fileName);
                    ProgressBarThread progressBarThread = new ProgressBarThread(fos, totalSize, fileName);
                    progressBarThread.start();
                    messageBroker.sendMessage("Downloading " + fileName + " ...", MessageType.INFO, MessageCategory.DOWNLOAD);
                    fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    progressBarThread.setDownloading(false);
                    // keep the main thread from closing the IO for a short amount of time so UI thread can finish and give output
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ignored) {}
                }
            } catch (SecurityException e) {
                messageBroker.sendMessage("Write access to " + dir + fileName + " denied !", MessageType.ERROR, MessageCategory.DOWNLOAD);
            } catch (FileNotFoundException fileNotFoundException) {
                messageBroker.sendMessage(FILE_NOT_FOUND, MessageType.ERROR, MessageCategory.DOWNLOAD);
            } catch (IOException e) {
                messageBroker.sendMessage(FAILED_TO_DOWNLOAD_CONTENTS, MessageType.ERROR, MessageCategory.DOWNLOAD);
            }
        } catch (NullPointerException e) {
            messageBroker.sendMessage(FAILED_TO_READ_DATA_STREAM, MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
    }

    public static void downloadFromYouTube(String dirOfYt_dlp) {
        String outputFileName = Objects.requireNonNullElse(fileName, DEFAULT_FILENAME);
        String fileDownloadMessage;
        if (outputFileName.equals(DEFAULT_FILENAME)) {
            fileDownloadMessage = "the YouTube Video";
        } else {
            fileDownloadMessage = outputFileName;
        }
        ProcessBuilder processBuilder;
        messageBroker.sendMessage("Trying to download " + fileDownloadMessage + " ...", MessageType.INFO, MessageCategory.DOWNLOAD);
        if ((dir.isEmpty()) || (dir.equalsIgnoreCase("."))) {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", link, "-o", outputFileName);
        } else {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", "-P", dir, link, "-o", outputFileName);
        }
        processBuilder.inheritIO();
        messageBroker.sendMessage(DOWNLOADING + fileDownloadMessage + " ...", MessageType.INFO, MessageCategory.DOWNLOAD);
        Process yt_dlp = null;
        try {
            yt_dlp = processBuilder.start();
            yt_dlp.waitFor();
        } catch (IOException e) {
            messageBroker.sendMessage("An I/O error occurred while initialising YouTube video downloader! " + e.getMessage(), MessageType.ERROR, MessageCategory.DOWNLOAD);
        } catch (InterruptedException e) {
            messageBroker.sendMessage("The YouTube video download process was interrupted by user! " + e.getMessage(), MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
        int exitValueOfYt_Dlp = yt_dlp.exitValue();
        if (exitValueOfYt_Dlp == 0) {
            messageBroker.sendMessage(SUCCESSFULLY_DOWNLOADED + fileDownloadMessage + " !", MessageType.INFO, MessageCategory.DOWNLOAD);
        } else if (exitValueOfYt_Dlp == 1) {
            messageBroker.sendMessage(FAILED_TO_DOWNLOAD + fileDownloadMessage + " !", MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
    }

    public static boolean mergeDownloadedFileParts(List<FileOutputStream> fileOutputStreams, List<Long> partSizes, List<DownloaderThread> downloaderThreads, List<File> tempFiles) throws IOException {
        // check if all files are downloaded
        int completed = 0;
        FileOutputStream fileOutputStream;
        DownloaderThread downloaderThread;
        long partSize;
        for (int i = 0; i < FileDownloader.numberOfThreads; i++) {
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
        if (completed == FileDownloader.numberOfThreads) {
            fileOutputStream = new FileOutputStream(dir + fileName);
            long position = 0;
            for (int i = 0; i < FileDownloader.numberOfThreads; i++) {
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

    private static void setYt_dlpProgramName(String yt_dlpProgramName) {
        FileDownloader.yt_dlpProgramName = yt_dlpProgramName;
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
        if (dir.isEmpty()) {
            dir = System.getProperty("user.home");
        }
        if (!(dir.endsWith("\\"))) {
            dir = dir + System.getProperty("file.separator");
        }
        try {
            boolean isInstagramLink = Drifty_CLI.getIsInstagramLink();
            boolean isInstagramImage = false;
            if (isInstagramLink) {
                isInstagramImage = Drifty_CLI.getIsInstagramImage();
            }
            // If the link is of a YouTube or Instagram video, then the following block of code will execute.
            if (isYoutubeLink(link) || (!isInstagramImage && isInstagramLink)) {
                try {
                    isYt_dlpCheckedForUpdate = Environment.isUpdateForYt_dlpChecked();
                    String directoryOfYt_dlp = Program.get(Program.PATH);
                    if (!isYt_dlpCheckedForUpdate) {
                        Environment.updateYt_dlp();
                    }
                    if (isYoutubeLink(link)) {
                        downloadFromYouTube(directoryOfYt_dlp);
                    } else if (!isInstagramImage) {
                        downloadFromInstagram(directoryOfYt_dlp);
                    }
                } catch (IOException e) {
                    messageBroker.sendMessage(GETTING_READY_TO_DOWNLOAD_FILE, MessageType.INFO, MessageCategory.DOWNLOAD);
                    try {
                        String tempDir = Program.get(Program.PATH);
                        if (!isYt_dlpCheckedForUpdate) {
                            Environment.updateYt_dlp();
                        }
                        if (isYoutubeLink(link)) {
                            downloadFromYouTube(tempDir);
                        } else if (!isInstagramImage) {
                            downloadFromInstagram(tempDir);
                        }
                    } catch (InterruptedException ie) {
                        messageBroker.sendMessage(USER_INTERRUPTION, MessageType.ERROR, MessageCategory.DOWNLOAD);
                    } catch (Exception e1) {
                        messageBroker.sendMessage(FAILED_TO_DOWNLOAD_YOUTUBE_VIDEO, MessageType.ERROR, MessageCategory.DOWNLOAD);
                        String msg = e1.getMessage();
                        String[] messageArray = msg.split(",");
                        if (messageArray.length >= 1 && messageArray[0].toLowerCase().trim().replaceAll(" ", "").contains("cannotrunprogram")) { // If yt-dlp program is not marked as executable
                            messageBroker.sendMessage(DRIFTY_COMPONENT_NOT_EXECUTABLE, MessageType.ERROR, MessageCategory.DOWNLOAD);
                        } else if (messageArray.length >= 1 && messageArray[1].toLowerCase().trim().replaceAll(" ", "").equals("permissiondenied")) { // If a private YouTube video is asked to be downloaded
                            messageBroker.sendMessage(PERMISSION_DENIED_YOUTUBE_VIDEO, MessageType.ERROR, MessageCategory.DOWNLOAD);
                        } else if (messageArray[0].toLowerCase().trim().replaceAll(" ", "").equals("videounavailable")) { // If YouTube Video is unavailable
                            messageBroker.sendMessage(YOUTUBE_VIDEO_UNAVAILABLE, MessageType.ERROR, MessageCategory.DOWNLOAD);
                        } else {
                            messageBroker.sendMessage(e.getMessage(), MessageType.ERROR, MessageCategory.DOWNLOAD);
                        }
                    }
                } catch (InterruptedException e) {
                    messageBroker.sendMessage(USER_INTERRUPTION, MessageType.ERROR, MessageCategory.DOWNLOAD);
                }
            } else {
                if (link.endsWith("/") || link.endsWith("\\")) {
                    link += "media";
                } else {
                    link += "/media";
                }
                url = new URI(link).toURL();
                URLConnection openConnection = url.openConnection();
                openConnection.connect();
                totalSize = openConnection.getHeaderFieldLong("Content-Length", -1);
                String acceptRange = openConnection.getHeaderField("Accept-Ranges");
                FileDownloader.supportsMultithreading = (totalSize > threadingThreshold) && (acceptRange != null) && (acceptRange.equalsIgnoreCase("bytes"));
                if (fileName.isEmpty()) {
                    String[] webPaths = url.getFile().trim().split("/");
                    fileName = webPaths[webPaths.length - 1];
                }
                messageBroker.sendMessage(TRYING_TO_DOWNLOAD_FILE, MessageType.INFO, MessageCategory.DOWNLOAD);
                downloadFile();
            }
        } catch (MalformedURLException | URISyntaxException e) {
            messageBroker.sendMessage(INVALID_LINK, MessageType.ERROR, MessageCategory.LINK);
        } catch (IOException e) {
            messageBroker.sendMessage(FAILED_TO_CONNECT_TO_URL + url + " !", MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
    }

    public static void downloadFromInstagram(String dirOfYt_dlp) throws InterruptedException, IOException {
        String outputFileName = Objects.requireNonNullElse(fileName, DEFAULT_FILENAME);
        String fileDownloadMessage;
        if (outputFileName.equals(DEFAULT_FILENAME)) {
            fileDownloadMessage = "the Instagram Video";
        } else {
            fileDownloadMessage = outputFileName;
        }
        ProcessBuilder processBuilder; // Creates a new ProcessBuilder object
        messageBroker.sendMessage("Trying to download " + fileDownloadMessage + " ...", MessageType.INFO, MessageCategory.DOWNLOAD);
        Rectangle2D screenSize = Screen.getPrimary().getBounds();
        int height = (int) screenSize.getHeight();  // E.g.: 768
        int width = (int) screenSize.getWidth();    // E.g.: 1366
        if (dir.isEmpty() || (dir.equalsIgnoreCase("."))) {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", link, "-o", outputFileName); // The command line arguments tell `yt-dlp` to download the video and to save it to the specified directory
        } else {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", "-P", dir, link, "-o", outputFileName); // The command line arguments tell `yt-dlp` to download the video and to save it to the specified directory.
        }
        processBuilder.inheritIO();
        messageBroker.sendMessage(DOWNLOADING + fileDownloadMessage + " ...", MessageType.INFO, MessageCategory.DOWNLOAD);
        Process yt_dlp = processBuilder.start(); // Starts the download process
        yt_dlp.waitFor();
        int exitValueOfYt_Dlp = yt_dlp.exitValue();
        if (exitValueOfYt_Dlp == 0) {
            messageBroker.sendMessage(SUCCESSFULLY_DOWNLOADED + fileDownloadMessage + " !", MessageType.INFO, MessageCategory.DOWNLOAD);
        } else if (exitValueOfYt_Dlp == 1) {
            messageBroker.sendMessage(FAILED_TO_DOWNLOAD + fileDownloadMessage + " !", MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
    }
}