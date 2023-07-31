package Backend;

import CLI.Drifty_CLI;
import Enums.*;
import GUIFX.MainGUI;
import Preferences.Init;
import Utils.MessageBroker;
import Utils.StringIsNull;
import Utils.Utility;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.*;
import java.net.*;
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
     * This is the message broker service instance which sends messages to the CLIString or GUI.
     */
    private static final MessageBroker message = Drifty.getMessageBrokerInstance();
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
     * The yt-dlp program name specific to the Operating System
     * <ul>
     *     <li><b>yt-dlp</b> for <b><i>Linux or Unix Systems</i></b></li>
     *     <li><b>yt-dlp.exe</b> for <b><i>Windows</i></b></li>
     *     <li><b>yt-dlp_macos</b> for <b><i>MacOS</i></b></li>
     *     <li><b>yt-dlp</b> for <b><i>Other Operating Systems</i></b></li>
     * </ul>
     */
    private static String yt_dlpProgramName;
    /**
     * This is a boolean value to determine if Multithreading is required or not.
     */
    private static boolean supportsMultithreading;
    /**
     * This boolean value is used to trigger an update of the yt-dlp program itself
     */
    private static boolean updated = true;

    /**
     * This is a constructor to initialise values of <b>link</b>, <b>fileName</b> and <b>dir</b> variables.
     *
     * @param link     Link to the file that the user wants to download
     * @param fileName Filename of the file that the user wants to save as after it is downloaded
     * @param dir      Directory in which the file needs to be saved.
     */
    public FileDownloader(String link, String fileName, String dir) {
        FileDownloader.link = link;
        FileDownloader.fileName = fileName;
        FileDownloader.dir = dir;
        FileDownloader.supportsMultithreading = false;
        if (Mode.GUI()) {
            setYt_dlpProgramName(Program.get(Program.NAME));
        }
        if (isYoutubeLink(link) || !Drifty_CLI.getIsInstagramImage()) {
            setYt_dlpProgramName(Program.get(Program.NAME));
        }
    }

    /**
     * This function is used to get the value of dir variable.
     *
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
                        file = File.createTempFile(fileName.hashCode() + String.valueOf(i), ".tmp");
                        file.deleteOnExit(); // Deletes temporary file when JVM exits
                        fileOut = new FileOutputStream(file);
                        start = (i == 0) ?
                                0 :
                                ((i * partSize) + 1); // The start of the range of bytes to be downloaded by the thread
                        end = ((FileDownloader.numberOfThreads - 1) == i) ?
                                totalSize :
                                ((i * partSize) + partSize); // The end of the range of bytes to be downloaded by the thread
                        DownloaderThread downloader = new DownloaderThread(url, fileOut, start, end);
                        downloader.start();
                        fileOutputStreams.add(fileOut);
                        partSizes.add(end - start);
                        downloaderThreads.add(downloader);
                        tempFiles.add(file);
                    }

                    ProgressBarThread progressBarThread = new ProgressBarThread(fileOutputStreams, partSizes, fileName, totalSize);
                    progressBarThread.start();
                    message.send(DOWNLOADING + fileName + " ...", Type.INFORMATION, Category.DOWNLOAD);
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
                    } catch (InterruptedException ignored) {
                    }
                }
                else {
                    InputStream urlStream = url.openStream();
                    System.out.println();
                    readableByteChannel = Channels.newChannel(urlStream);

                    FileOutputStream fos = new FileOutputStream(dir + fileName);
                    ProgressBarThread progressBarThread = new ProgressBarThread(fos, totalSize, fileName);
                    progressBarThread.start();
                    message.send("Downloading " + fileName + " ...", Type.INFORMATION, Category.DOWNLOAD);
                    fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    progressBarThread.setDownloading(false);
                    // keep main thread from closing the IO for short amount of time so UI thread can finish and give output
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ignored) {
                    }
                }

            } catch (SecurityException e) {
                message.send("Write access to " + dir + fileName + " denied !", Type.ERROR, Category.DOWNLOAD);
            } catch (FileNotFoundException fileNotFoundException) {
                message.send(FILE_NOT_FOUND, Type.ERROR, Category.DOWNLOAD);
            } catch (IOException e) {
                message.send(FAILED_TO_DOWNLOAD_CONTENTS, Type.ERROR, Category.DOWNLOAD);
            }
        } catch (NullPointerException e) {
            message.send(FAILED_TO_READ_DATA_STREAM, Type.ERROR, Category.DOWNLOAD);
        }
    }

    /**
     * This method deals with downloading videos from YouTube in mp4 format.
     *
     * @param dirOfYt_dlp The directory of yt-dlp file. Default - "". If Drifty is run from its jar file, this argument will have the directory where yt-dlp has been extracted to (the temporary files' folder).
     * @throws InterruptedException When the I/O operation is interrupted using keyboard or such type of inputs.
     * @throws IOException          When an I/O problem appears while downloading the YouTube video.
     */
    public static void downloadFromYouTube(String dirOfYt_dlp) throws InterruptedException, IOException {
        String outputFileName = StringIsNull.replace(fileName, DEFAULT_FILENAME);
        String fileDownloadMessage;
        if (outputFileName.equals(DEFAULT_FILENAME)) {
            fileDownloadMessage = "the YouTube Video";
        }
        else {
            fileDownloadMessage = outputFileName;
        }
        ProcessBuilder processBuilder;
        message.send("Trying to download " + fileDownloadMessage + " ...", Type.INFORMATION, Category.DOWNLOAD);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // E.g.: java.awt.Dimension[width=1366,height=768]
        int height = (int) screenSize.getHeight(); // E.g.: 768
        int width = (int) screenSize.getWidth(); // E.g.: 1366
        if ((dir.length() == 0) || (dir.equalsIgnoreCase("."))) {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", link, "-o", outputFileName, "-f", "[height<=" + height + "][width<=" + width + "]");
            // , "--progress-template", "Downloading : %(progress._percent_str)s"
        }
        else {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", "-P", dir, link, "-o", outputFileName, "-f", "[height<=" + height + "][width<=" + width + "]");
            // , "--progress-template", "Downloading : %(progress._percent_str)s"
        }
        processBuilder.inheritIO();
        message.send(DOWNLOADING + fileDownloadMessage + " ...", Type.INFORMATION, Category.DOWNLOAD);
        Process yt_dlp = processBuilder.start();
        yt_dlp.waitFor();
        int exitValueOfYt_Dlp = yt_dlp.exitValue();
        if (exitValueOfYt_Dlp == 0) {
            message.send(SUCCESSFULLY_DOWNLOADED + fileDownloadMessage + " !", Type.INFORMATION, Category.DOWNLOAD);
        }
        else if (exitValueOfYt_Dlp == 1) {
            message.send(FAILED_TO_DOWNLOAD + fileDownloadMessage + " !", Type.ERROR, Category.DOWNLOAD);
        }
    }

    private static Process process;

    /**
     * This method deals with downloading videos from YouTube in mp4 format.
     *
     * @throws InterruptedException When the I/O operation is interrupted using keyboard or such type of inputs.
     * @throws IOException          When an I/O problem appears while downloading the YouTube video.
     */
    public static void downloadGUI() throws InterruptedException, IOException {
        String outputFileName = StringIsNull.replace(fileName, DEFAULT_FILENAME);
        String command = Program.get(Program.COMMAND);
        outputFileName = Utility.cleanFilename(outputFileName);
        message.send("Trying to download " + outputFileName + " ...", Type.INFORMATION, Category.DOWNLOAD);
        String ext = FilenameUtils.getExtension(outputFileName).toLowerCase();
        String[] fullCommand = (Format.isValid(ext)) ?
                new String[]{command, "--quiet", "--progress", "-P", dir, link, "-f", ext, "-o", outputFileName} :
                new String[]{command, "--quiet", "--progress", "-P", dir, link, "-o", outputFileName};
        ProcessBuilder pb = new ProcessBuilder(fullCommand);
        StringBuilder sb = new StringBuilder();
        for (String arg : pb.command()) sb.append(arg).append(" ");
        String msg = RUNNING_COMMAND + Program.get(Program.NAME) + " " + sb;
        System.out.println(message);
        message.send(DOWNLOADING + outputFileName + " ...", Type.INFORMATION, Category.DOWNLOAD);
        message.send(msg, Type.INFORMATION, Category.DOWNLOAD);
        pb.redirectErrorStream(true);
        process = pb.start();
        try {
            try (InputStream inputStream = process.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int result = process.waitFor();
        String errorMessage = ((result == 0) ? SUCCESSFULLY_DOWNLOADED : FAILED_TO_DOWNLOAD) + outputFileName;
        MainGUI.setJobError(errorMessage, result);
    }

    /**
     * This method checks if all the downloader threads are completed correctly and merges the downloaded parts.
     *
     * @param fileOutputStreams FileOutputStream of all the parts
     * @param partSizes         Size each of the parts
     * @param downloaderThreads DownloaderThreads of all the parts
     * @param tempFiles         Temporary files containing the parts
     * @return <b>true</b> if <i>merge is successful</i> and <b>false</b> if the file is <i>still being downloaded</i>
     * @throws IOException if the threads exit without downloading the whole part or if there are any IO error thrown by <b>getChannel().size() method of FileOutputStream</b>
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
            }
            else if (!downloaderThread.isAlive()) {
                completed++;
            }
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
     * This method is used to set yt-dlp Program string
     *
     * @param yt_dlpProgramName The yt-dlp program name specific to the OS
     */
    private static void setYt_dlpProgramName(String yt_dlpProgramName) {
        FileDownloader.yt_dlpProgramName = yt_dlpProgramName;
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
            // If link is of an YouTube or Instagram video, then the following block of code will execute.
            if (isYoutubeLink(link) || (!isInstagramImage && isInstagramLink)) {
                try {
                    updated = Init.isUpdated();
                    String directoryOfYt_dlp = Program.get(Program.PATH);
                    if (!updated) {
                        Init.updateProgram();
                    }
                    if (Mode.GUI()) {
                        downloadGUI();
                    }
                    if (isYoutubeLink(link)) {
                        downloadFromYouTube(directoryOfYt_dlp);
                    }
                    else if (!isInstagramImage) {
                        downloadFromInstagram(directoryOfYt_dlp);
                    }
                } catch (IOException e) {
                    message.send(GETTING_READY_TO_DOWNLOAD_FILE, Type.INFORMATION, Category.DOWNLOAD);
                    try {
                        String tempDir = Program.get(Program.PATH);
                        if (!updated) {
                            Init.updateProgram();
                        }
                        if (isYoutubeLink(link)) {
                            downloadFromYouTube(tempDir);
                        }
                        else if (!isInstagramImage) {
                            downloadFromInstagram(tempDir);
                        }
                    } catch (InterruptedException ie) {
                        message.send(USER_INTERRUPTION, Type.ERROR, Category.DOWNLOAD);
                    } catch (Exception e1) {
                        message.send(FAILED_TO_DOWNLOAD_YOUTUBE_VIDEO, Type.ERROR, Category.DOWNLOAD);
                        String msg = e1.getMessage();
                        String[] messageArray = msg.split(",");
                        if (messageArray.length >= 1 && messageArray[0].toLowerCase().trim().replaceAll(" ", "").contains("cannotrunprogram")) { // If yt-dlp program is not marked as executable
                            message.send(DRIFTY_COMPONENT_NOT_EXECUTABLE, Type.ERROR, Category.DOWNLOAD);
                        }
                        else if (messageArray.length >= 1 && messageArray[1].toLowerCase().trim().replaceAll(" ", "").equals("permissiondenied")) { // If a private YouTube video is asked to be downloaded
                            message.send(PERMISSION_DENIED_YOUTUBE_VIDEO, Type.ERROR, Category.DOWNLOAD);
                        }
                        else if (messageArray[0].toLowerCase().trim().replaceAll(" ", "").equals("videounavailable")) { // If YouTube Video is unavailable
                            message.send(YOUTUBE_VIDEO_UNAVAILABLE, Type.ERROR, Category.DOWNLOAD);
                        }
                        else {
                            message.send(e.getMessage(), Type.ERROR, Category.DOWNLOAD);
                        }
                    }
                } catch (InterruptedException e) {
                    message.send(USER_INTERRUPTION, Type.ERROR, Category.DOWNLOAD);
                }
            }
            else {
                if (link.endsWith("/") || link.endsWith("\\")) {
                    link += "media";
                }
                else {
                    link += "/media";
                }
                url = new URI(link).toURL();
                URLConnection openConnection = url.openConnection();
                openConnection.connect();
                totalSize = openConnection.getHeaderFieldLong("Content-Length", -1);

                String acceptRange = openConnection.getHeaderField("Accept-Ranges");
                FileDownloader.supportsMultithreading = (totalSize > threadingThreshold) && (acceptRange != null) && (acceptRange.equalsIgnoreCase("bytes"));

                if (fileName.length() == 0) {
                    String[] webPaths = url.getFile().trim().split("/");
                    fileName = webPaths[webPaths.length - 1];
                }
                message.send(TRYING_TO_DOWNLOAD_FILE, Type.INFORMATION, Category.DOWNLOAD);
                if (Mode.GUI()) {
                    try {
                        downloadGUI();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                downloadFile();
            }
        } catch (MalformedURLException | URISyntaxException e) {
            message.send(INVALID_LINK, Type.ERROR, Category.LINK);
        } catch (IOException e) {
            message.send(FAILED_TO_CONNECT_TO_URL + url + " !", Type.ERROR, Category.DOWNLOAD);
        }
    }

    /**
     * This method downloads a video from Instagram.
     *
     * @param dirOfYt_dlp The directory to save the file to.
     * @throws InterruptedException If the download is interrupted.
     * @throws IOException          If there is an error reading or writing the file.
     */
    public static void downloadFromInstagram(String dirOfYt_dlp) throws InterruptedException, IOException {
        String outputFileName = StringIsNull.replace(fileName, DEFAULT_FILENAME);
        String fileDownloadMessage;
        if (outputFileName.equals(DEFAULT_FILENAME)) {
            fileDownloadMessage = "the Instagram Video";
        }
        else {
            fileDownloadMessage = outputFileName;
        }
        ProcessBuilder processBuilder; // Creates a new ProcessBuilder object
        message.send("Trying to download " + fileDownloadMessage + " ...", Type.INFORMATION, Category.DOWNLOAD);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // E.g. java.awt.Dimension[width=1366,height=768]
        int height = (int) screenSize.getHeight();  // E.g.: 768
        int width = (int) screenSize.getWidth();    // E.g.: 1366
        if (dir.length() == 0 || (dir.equalsIgnoreCase("."))) {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", link, "-o",
                    outputFileName, "-f", "[height<=" + height + "][width<=" + width + "]"); // The command line arguments tell `yt-dlp` to download the video and to save it to the specified directory
        }
        else {
            processBuilder = new ProcessBuilder(dirOfYt_dlp + yt_dlpProgramName, "--quiet", "--progress", "-P", dir, link,
                    "-o", outputFileName, "-f", "[height<=" + height + "][width<=" + width + "]"); // The command line arguments tell `yt-dlp` to download the video and to save it to the specified directory.
        }
        processBuilder.inheritIO();
        message.send(DOWNLOADING + fileDownloadMessage + " ...", Type.INFORMATION, Category.DOWNLOAD);
        Process yt_dlp = processBuilder.start(); // Starts the download process
        yt_dlp.waitFor();
        int exitValueOfYt_Dlp = yt_dlp.exitValue();
        if (exitValueOfYt_Dlp == 0) {
            message.send(SUCCESSFULLY_DOWNLOADED + fileDownloadMessage + " !", Type.INFORMATION, Category.DOWNLOAD);
        }
        else if (exitValueOfYt_Dlp == 1) {
            message.send(FAILED_TO_DOWNLOAD + fileDownloadMessage + " !", Type.ERROR, Category.DOWNLOAD);
        }
    }
}
