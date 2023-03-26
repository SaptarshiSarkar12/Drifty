package Backend;

import GUI.Drifty_GUI;
import Utils.MessageBroker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Utils.DriftyConstants.*;

/**
 * This is the class responsible for showing the progress bar in the CLI (Command Line Interface) and enables progress bar values to be updated in the GUI (Graphical User Interface).
 */
public class ProgressBarThread extends Thread {
    private final static MessageBroker messageBroker = Drifty.messageBroker;
    private final float charPercent; // This stores the total size of the file to be downloaded in bytes.
    private final List<Long> partSizes; // This is a list containing the size of each part of the total file to be downloaded, using multiple threads.
    private final String fileName; // Name of the file to be downloaded
    private final FileOutputStream fos; // This is the output stream of the file (In this case, to the file that will be saved locally after downloading it.)
    private final int charAmt; // This is the value to determine length of terminal progressbar
    private final List<Integer> charPercents; // This has the size of data to be downloaded by each thread. This is used in multithreaded downloading process. [NOTE: (No. of threads) * (this variable) = Total size of the file in bytes]
    private final List<FileOutputStream> fileOutputStreams; // This is the output stream of the temporary files which are a part of the total file and will be merged later.
    private final boolean isMultiThreadedDownloading; // This is a boolean which states whether multithreaded downloading is used or not (depends on file size) for the specific file to be downloaded.
    private long downloadedBytes; // This stores the total bytes of data downloaded
    private List<Long> downloadedBytesPerPart; // This is a List which contains the bytes of data downloaded by each thread.
    private long totalSizeOfTheFile; // This is the total size of the file to be downloaded.
    private boolean downloading; // This is a boolean which states whether the file is currently being downloaded or not.
    private long downloadSpeed; // This is a variable which contains the download speed (This variable is used if multithreaded downloading is not used)
    private List<Long> downloadSpeeds; // This is a variable which contains the download speeds for all the threads running (This variable is used if multithreaded downloading is used)
    private static float totalDownloadPercent; // This is the total download percent
    private long totalDownloadedBytes; // This is the total size of the file. This is also used in multiple threads to store the size of file downloaded by each threads

    /**
     * This is a constructor to create Progress Bars for each file downloading threads.
     * @param fileOutputStreams This contains the list of streams to each parts of the file to be downloaded.
     * @param partSizes This is a list of size of each part of the file to be downloaded, for each file downloading threads.
     * @param fileName This is the name of the file to be downloaded.
     * @param totalSize This is the total size of the file to be downloaded.
     */
    public ProgressBarThread(List<FileOutputStream> fileOutputStreams, List<Long> partSizes, String fileName, Long totalSize) {
        this.partSizes = partSizes;
        this.fileName = fileName;
        this.totalSizeOfTheFile = totalSize;
        this.fileOutputStreams = fileOutputStreams;
        charPercent = 0;
        fos = null;
        totalDownloadedBytes = 0;
        charAmt = 80 / fileOutputStreams.size(); // value to determine length of terminal progressbar
        isMultiThreadedDownloading = true;
        downloading = true;
        charPercents = new ArrayList<>(fileOutputStreams.size());
        downloadedBytesPerPart = new ArrayList<>(fileOutputStreams.size());
        downloadSpeeds = new ArrayList<>(fileOutputStreams.size());
        for (int i = 0; i < fileOutputStreams.size(); i++) {
            charPercents.add((int) (partSizes.get(i) / charAmt));
        }
    }

    /**
     * Progress Bar Constructor to initialise the data members. This is used if multithreaded downloading is not used (depends on the file size).
     * @param fos                Output stream for writing contents from the web to the local file.
     * @param totalDownloadedBytes Total size of the file in bytes.
     * @param fileName           Filename of the downloaded file.
     */
    public ProgressBarThread(FileOutputStream fos, long totalDownloadedBytes, String fileName) {
        this.charAmt = 20; // value to determine length of terminal progressbar
        this.downloading = true;
        this.downloadSpeed = 0;
        this.downloadedBytes = 0;
        this.totalDownloadedBytes = totalDownloadedBytes;
        this.fileName = fileName;
        this.fos = fos;
        this.charPercent = (int) (this.totalDownloadedBytes / charAmt);

        fileOutputStreams = null;
        partSizes = null;
        isMultiThreadedDownloading = false;
        charPercents = null;
    }

    /**
     * This method sets whether the file is being downloaded or the downloading process has been over.
     * @param downloading true if the file is being downloaded and false if it's not.
     */
    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    /**
     * This method generates a progress bar for the CLI (Command Line Interface) version of Drifty.
     * @param spinner icon of the spin in the progress bar.
     * @return String object containing the progress bar.
     */
    private String generateProgressBar(String spinner) {
        if (!isMultiThreadedDownloading) {
            float filled = downloadedBytes / charPercent;
            String a = new String(new char[(int) filled]).replace("\0", "=");
            String b = new String(new char[charAmt - (int) filled]).replace("\0", ".");
            String bar = a + b;
            totalDownloadPercent = (int) (100 * downloadedBytes / totalDownloadedBytes);
            float downloadSpeedWithoutUnit;
            String downloadSpeedUnit;
            if ((int)totalDownloadPercent != 100) {
                String downloadSpeedWithUnit = convertBytes(downloadSpeed);
                int indexOfDownloadSpeedUnit = downloadSpeedWithUnit.indexOf(" ") + 1;
                downloadSpeedWithoutUnit = Float.parseFloat(downloadSpeedWithUnit.substring(0, indexOfDownloadSpeedUnit - 1));
                downloadSpeedUnit = downloadSpeedWithUnit.substring(indexOfDownloadSpeedUnit);
            } else {
                downloadSpeedWithoutUnit = 0;
                downloadSpeedUnit = "bytes";
            }
            bar = bar.substring(0, charAmt / 2 - 2) + ( totalDownloadPercent) + "%" + bar.substring(charAmt / 2 + 1);
            return "[" + spinner + "]  " + fileName + "  [" + bar + "](" + convertBytes(totalDownloadedBytes) + ")  " + downloadSpeedWithoutUnit + " " + downloadSpeedUnit + "/s";
        } else {
            int numberOfThreads = fileOutputStreams.size();
            StringBuilder result = new StringBuilder("[" + spinner + "]  " + convertBytes(totalDownloadedBytes));
            float filled;
            totalDownloadedBytes = 0;
            long downloadSpeed = 0;
            for (int i = 0; i < numberOfThreads; i++) {
                /*
                Suppose, charPercents.get(0) = 1357301076 ,and
                         downloadedBytesPerPart.get(0) = 20000000000
                Then, filled = 14.73512425
                      a = new String(new char[(int) filled]).replace("\0", "=");
                        = new String(new char[(int) 14.73512425]).replace("\0", "=");
                        = new String(new char[14]).replace("\0", "=");
                        = "00000000000000".replace("\0", "=");
                        = "=============="

                      b = new String(new char[charAmt - (int) filled]).replace("\0", ".");
                        = new String(new char[26 - (int) 14.73512425]).replace("\0", ".");
                        = new String(new char[26 - 14]).replace("\0", ".");
                        = new String(new char[12]).replace("\0", ".");
                        = "000000000000".replace("\0", ".");
                        = "............"

                     a + b = "==============" + "............"
                           = "==============............"
                */
                totalDownloadedBytes += downloadedBytesPerPart.get(i);
                downloadSpeed += downloadSpeeds.get(i);
            }
            filled = totalDownloadedBytes / ((float) (numberOfThreads * charPercents.get(0)));
            String a = new String(new char[(int) filled]).replace("\0", "=");
            String b = new String(new char[charAmt - (int) filled]).replace("\0", ".");
            String bar = a + b;
            totalDownloadPercent = (100f * totalDownloadedBytes / totalSizeOfTheFile);
            bar = bar.substring(0, (charAmt / 2) - 2) + String.format("%02d", (int) (totalDownloadPercent)) + "%" + bar.substring((charAmt / 2) + 1);
            float downloadSpeedWithoutUnit;
            String downloadSpeedUnit;
            if ((int)totalDownloadPercent != 100) {
                String downloadSpeedWithUnit = convertBytes(downloadSpeed);
                int indexOfDownloadSpeedUnit = downloadSpeedWithUnit.indexOf(" ") + 1;
                downloadSpeedWithoutUnit = Float.parseFloat(downloadSpeedWithUnit.substring(0, indexOfDownloadSpeedUnit - 1));
                downloadSpeedUnit = downloadSpeedWithUnit.substring(indexOfDownloadSpeedUnit);
            } else {
                downloadSpeedWithoutUnit = 0;
                downloadSpeedUnit = "bytes";
            }
            result.append(" [").append(bar).append("] ").append(String.format("%.2f", downloadSpeedWithoutUnit)).append(" ").append(downloadSpeedUnit).append("/s");
            return result.toString();
        }
    }

    /**
     * This is an accessor method to get the total download percentage.
     * @return The total download percentage.
     */
    public static synchronized float getTotalDownloadPercent() {
        return totalDownloadPercent;
    }

    /**
     * This method converts the data size from <b>bytes</b> to <b>bigger units</b> like <i>KB, MB, GB</i> , etc. if possible, else <b>the amount of data with bytes as the unit</b> is returned.
     * @param bytes file size in bytes.
     * @return returns a String object containing the file size with proper units.
     */
    private String convertBytes(long bytes) {
        String sizeWithUnit;
        double bytesWithDecimals;
        if (bytes > 1024) {
            bytesWithDecimals = bytes / 1024.0;
            sizeWithUnit = String.format("%.2f", bytesWithDecimals) + " KB";
            if (bytesWithDecimals > 1024) {
                bytesWithDecimals = bytesWithDecimals / 1024;
                sizeWithUnit = String.format("%.2f", bytesWithDecimals) + " MB";
                if (bytesWithDecimals > 1024) {
                    bytesWithDecimals = bytesWithDecimals / 1024;
                    sizeWithUnit = String.format("%.2f", bytesWithDecimals) + "GB";
                }
            }
            return sizeWithUnit;
        } else {
            return totalDownloadedBytes + " bytes";
        }
    }

    /**
     * Cleans up the resources.
     */
    private void cleanup() {
        if (isMultiThreadedDownloading) {
            String sizeWithUnit = convertBytes(totalDownloadedBytes);
            messageBroker.sendMessage(DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + SUCCESSFULLY, LOGGER_INFO, "download");
        } else if (downloadedBytes == totalDownloadedBytes) {
            String sizeWithUnit = convertBytes(downloadedBytes);
            messageBroker.sendMessage(DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + SUCCESSFULLY, LOGGER_INFO, "download");
        } else {
            messageBroker.sendMessage(DOWNLOAD_FAILED, LOGGER_ERROR, "download");
        }
        Drifty_GUI.setIsFileBeingDownloaded(false);
    }

    /**
     * run method for progress bar.
     */
    @Override
    public void run() {
        String appType = Drifty.getAppType();
        long initialMeasurement;
        String[] spinner = new String[]{"/", "-", "\\", "|"};
        List<Long> initialMeasurements = isMultiThreadedDownloading ? new ArrayList<>(fileOutputStreams.size()) : null;
        while (downloading) {
            try {
                if (!isMultiThreadedDownloading) {
                    for (int i = 0; i <= 3; i++) {
                        initialMeasurement = fos.getChannel().size();
                        Thread.sleep(250);
                        downloadedBytes = fos.getChannel().size();
                        downloadSpeed = (downloadedBytes - initialMeasurement) * 4;
                        if (appType.equals("CLI")) {
                            System.out.print("\r" + generateProgressBar(spinner[i]));
                        } else {
                            generateProgressBar(spinner[i]);
                        }
                    }
                } else {
                    for (int i = 0; i <= fileOutputStreams.size(); i++) {
                        for (int j = 0; j < fileOutputStreams.size(); j++) {
                            initialMeasurements.add(j, fileOutputStreams.get(j).getChannel().size());
                        }
                        Thread.sleep(300);
                        long downloadedPartBytes;
                        for (int j = 0; j < fileOutputStreams.size(); j++) {
                            downloadedPartBytes = fileOutputStreams.get(j).getChannel().size();
                            downloadedBytesPerPart.add(j, downloadedPartBytes);
                            downloadSpeeds.add(j, (downloadedPartBytes - initialMeasurements.get(j)) * 4);
                        }
                        if (appType.equals("CLI")) {
                            System.out.print("\r" + generateProgressBar(spinner[i]));
                        } else {
                            generateProgressBar(spinner[i]);
                        }
                    }
                }
            } catch (InterruptedException | IOException ignored) {}
        }
        cleanup();
    }
}
