package Backend;

import Utils.MessageBroker;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import static Utils.DriftyConstants.*;
import static Utils.Utility.isYoutubeLink;

/**
 * This class deals with downloading the file.
 */
public class FileDownloader implements Runnable {
    /**
     * This is the message broker service instance which sends messages to the CLI or GUI.
     */
    private static final MessageBroker messageBroker = Drifty.getMessageBrokerInstance();
    /**
     * This is the number of parallel file downloading threads to download the file faster.
     */
    private static final int numberOfThreads = 3;
    /**
     * This is threshold (in bytes) to determine whether parallel downloading is to performed or not.
     * This value determines if multithreaded downloading will be used or not. If the size of the file to be downloaded exceeds this value, then multithreaded downloading will be in use, else not. Here, it has been taken as 50 MB.
     */
    private static final long threadingThreshold = 1024 * 1024 * 50;
    /**
     * The directory where the file is to be downloaded.
     */
    private static String dir;
    /**
     * The filename of the downloaded file.
     */
    private static String fileName;
    /**
     * The link to the file to be downloaded.
     */
    private static String link;
    /**
     * This is the total size of the file to be downloaded, determined during execution.
     */
    private static long totalSize;
    /**
     * The link of the file to be downloaded in URL format.
     */
    private static URL url;
    /**
     * This is a boolean value to determine if Multithreading is required or not.
     */
    private static boolean supportsMultithreading;

    /**
     * This is a constructor to initialise values of <b>link</b>, <b>fileName</b> and <b>dir</b> variables.
     * @param link     Link to the file that the user wants to download
     * @param fileName Filename of the file that the user wants to save as after it is downloaded
     * @param dir      Directory in which the file needs to be saved.
     */
    public FileDownloader(String link, String fileName, String dir) {
        FileDownloader.link = link;
        FileDownloader.fileName = fileName;
        FileDownloader.dir = dir;
        FileDownloader.supportsMultithreading = false;
    }

    /**
     * This function is used to get the value of dir variable.
     * @return The directory in which the user wants to save the file.
     */
    public static String getDir() {
        return dir;
    }

    /**
     * This method deals with downloading the file.
     */
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
                        file = File.createTempFile(fileName.hashCode() + "" + i, ".tmp");
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
                    messageBroker.sendMessage(DOWNLOADING + fileName + " ...", LOGGER_INFO, "download");
                    // check if all file are downloaded
                    try {
                        while (!merge(fileOutputStreams, partSizes, downloaderThreads, tempFiles)) {
                            Thread.sleep(1000);
                        }
                        progressBarThread.setDownloading(false);
                        // keep main thread from closing the IO for short amt. of time so UI thread can finish and output
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    } catch (InterruptedException ignored) {}
                } else {
                    InputStream urlStream = url.openStream();
                    System.out.println();
                    readableByteChannel = Channels.newChannel(urlStream);

                    FileOutputStream fos = new FileOutputStream(dir + fileName);
                    ProgressBarThread progressBarThread = new ProgressBarThread(fos, totalSize, fileName);
                    progressBarThread.start();
                    messageBroker.sendMessage("Downloading " + fileName + " ...", LOGGER_INFO, "download");
                    fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    progressBarThread.setDownloading(false);
                    // keep main thread from closing the IO for short amount of time so UI thread can finish and give output
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ignored) {}
                }

            } catch (SecurityException e) {
                messageBroker.sendMessage("Write access to " + dir + fileName + " denied !", LOGGER_ERROR, "download");
            } catch (FileNotFoundException fileNotFoundException) {
                messageBroker.sendMessage(FILENOTFOUND, LOGGER_ERROR, "download");
            } catch (IOException e) {
                messageBroker.sendMessage(FAILED_TO_DOWNLOAD_CONTENTS, LOGGER_ERROR, "download");
            }
        } catch (NullPointerException e) {
            messageBroker.sendMessage(FAILED_TO_READ_DATA_STREAM, LOGGER_ERROR, "download");
        }
    }

    /**
     * This method deals with downloading videos from YouTube in mp4 format.
     * @param dirOfYt_dlp The directory of yt-dlp file. Default - "". If Drifty is run from its jar file, this argument will have the directory where yt-dlp has been extracted to (the temporary files' folder).
     * @throws InterruptedException When the I/O operation is interrupted using keyboard or such type of inputs.
     * @throws IOException When an I/O problem appears while downloading the YouTube video.
     */
    public static void downloadFromYouTube(String dirOfYt_dlp) throws InterruptedException, IOException {
        String outputFileName;
        if (fileName != null){
            outputFileName = fileName;
        } else {
            outputFileName = "%(title)s.%(ext)s";
        }
        String fileDownloadMessagePart;
        if (outputFileName.equals("%(title)s.%(ext)s")){
            fileDownloadMessagePart = "the YouTube Video";
        } else {
            fileDownloadMessagePart = outputFileName;
        }
        ProcessBuilder processBuilder;
        messageBroker.sendMessage("Trying to download " + fileDownloadMessagePart + " ...", LOGGER_INFO, "download");
        String yt_dlpProgramName;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("nux") || osName.contains("nix")){
            yt_dlpProgramName = "yt-dlp";
        } else if (osName.contains("win")) {
            yt_dlpProgramName = "yt-dlp.exe";
        } else if (osName.contains("mac")){
            yt_dlpProgramName = "yt-dlp_macos";
        } else {
            yt_dlpProgramName = "yt-dlp";
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // E.g.: java.awt.Dimension[width=1366,height=768]
        int height = (int) screenSize.getHeight(); // E.g.: 768
        int width = (int) screenSize.getWidth(); // E.g.: 1366
        if ((dir.length() == 0) || (dir.equalsIgnoreCase("."))){
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", link, "-o", outputFileName, "-f", "[height<=" + height + "][width<=" + width + "]");
            // , "--progress-template", "Downloading : %(progress._percent_str)s"
        } else {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", "-P", dir, link, "-o", outputFileName, "-f", "[height<=" + height + "][width<=" + width + "]");
            // , "--progress-template", "Downloading : %(progress._percent_str)s"
        }
        processBuilder.inheritIO();
        messageBroker.sendMessage(DOWNLOADING + fileDownloadMessagePart + " ...", LOGGER_INFO, "download");
        Process yt_dlp = processBuilder.start();
        yt_dlp.waitFor();
        int exitValueOfYt_Dlp = yt_dlp.exitValue();
        if (exitValueOfYt_Dlp == 0) {
            messageBroker.sendMessage(SUCCESSFULLY_DOWNLOADED + fileDownloadMessagePart + " !", LOGGER_INFO, "download");
        } else if (exitValueOfYt_Dlp == 1) {
            messageBroker.sendMessage(FAILED_TO_DOWNLOAD + fileDownloadMessagePart + " !", LOGGER_ERROR, "download");
        }
    }

    /**
     * This method check if all the downloader threads are completed correctly and merges the downloaded parts.
     * @param fileOutputStreams FileOutputStream of all the parts
     * @param partSizes         Size each of the parts
     * @param downloaderThreads DownloaderThreads of all the parts
     * @param tempFiles         Temporary files containing the parts
     * @return true if merge is successful and false if the file is still being downloaded
     * @throws IOException if the threads exit without downloading the whole part or if there are any IO error thrown by  getChannel().size() method of FileOutputStream
     */
    public static boolean merge(List<FileOutputStream> fileOutputStreams, List<Long> partSizes, List<DownloaderThread> downloaderThreads, List<File> tempFiles) throws IOException {
        // check if all file are downloaded
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
            } else if (!downloaderThread.isAlive()) completed++;
        }

        // check if it is merge-able
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

    /**
     * This is method which deals with the main part of opening and establishing connections and downloading the file.
     */
    @Override
    public void run() {
        link = link.replace('\\', '/');
        if (!(link.startsWith("http://") || link.startsWith("https://"))) {
            link = "http://" + link;
        }
        if (link.startsWith("https://github.com/") || (link.startsWith("http://github.com/"))) {
            if (!(link.endsWith("?raw=true"))) {
                link = link + "?raw=true";
            }
        }
        if (dir.length() == 0) {
            dir = System.getProperty("user.dir");
        }
        if (!(dir.endsWith("\\"))) {
            dir = dir + System.getProperty("file.separator");
        }
        try {
            // If link is of an YouTube video, then the following block of code will execute.
            if (isYoutubeLink(link)) {
                try {
                    downloadFromYouTube("./src/main/resources/");
                } catch (IOException e) {
                    try {
                        messageBroker.sendMessage(GETTING_READY_TO_DOWNLOAD_FILE, LOGGER_INFO, "download");
                        copyYt_dlp cy = new copyYt_dlp();
                        cy.copyToTemp();
                        try {
                            String tempDir = copyYt_dlp.getTempDir();
                            downloadFromYouTube(tempDir);
                        } catch (InterruptedException ie) {
                            messageBroker.sendMessage(USER_INTERRUPTION, LOGGER_ERROR, "download");
                        } catch (IOException io1) {
                            messageBroker.sendMessage(FAILED_TO_DOWNLOAD_YOUTUBE_VIDEO, LOGGER_ERROR, "download");
                            String message = e.getMessage();
                            String[] messageArray = message.split(",");
                            if(messageArray.length >= 1 && messageArray[1].toLowerCase().trim().replaceAll(" ", "").equals("permissiondenied")) {
                                messageBroker.sendMessage(PERMISSIONDENIED_YOUTUBE_VIDEO, LOGGER_ERROR, "download");
                            }
                            else {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (IOException io) {
                        messageBroker.sendMessage(FAILED_TO_INITIALISE_YOUTUBE_VIDEO, LOGGER_ERROR, "download");
                    }
                } catch (InterruptedException e) {
                    messageBroker.sendMessage(USER_INTERRUPTION, LOGGER_ERROR, "download");
                }
            } else {
                url = new URL(link);
                URLConnection openConnection = url.openConnection();
                openConnection.connect();
                totalSize = openConnection.getHeaderFieldLong("Content-Length", -1);

                String acceptRange = openConnection.getHeaderField("Accept-Ranges");
                FileDownloader.supportsMultithreading = (totalSize > threadingThreshold) && (acceptRange != null) && (acceptRange.equalsIgnoreCase("bytes"));

                if (fileName.length() == 0) {
                    String[] webPaths = url.getFile().trim().split("/");
                    fileName = webPaths[webPaths.length - 1];
                }
                messageBroker.sendMessage(TRYING_TO_DOWNLOAD_FILE, LOGGER_INFO, "download");
                downloadFile();
            }
        } catch (MalformedURLException e) {
            messageBroker.sendMessage(INVALID_LINK, LOGGER_ERROR, "link");
        } catch (IOException e) {
            messageBroker.sendMessage(FAILED_TO_CONNECT_TO_URL + url + " !", LOGGER_ERROR, "download");
        }
    }
}
