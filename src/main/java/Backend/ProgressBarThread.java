package Backend;

import CLI.Drifty_CLI;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Utils.DriftyConstants.*;

/**
 * This is the class responsible for showing the progress bar in the CLI (Command Line Interface) and enables progress bar values to be updated in the GUI (Graphical User Interface).
 */
public class ProgressBarThread extends Thread {
    private final float charPercent; // This stores the total size of the file to be downloaded in bytes.
    private final List<Long> partSizes; // This is a list containing the size of each part of the total file to be downloaded, using multiple threads.
    private final String fileName; // Name of the file to be downloaded
    private final FileOutputStream fos; // This is the output stream of the file (In this case, to the file that will be saved locally after downloading it.)
    private final int charAmt; // This is the value to determine length of terminal progressbar
    private final List<Integer> charPercents; // This has the size of data to be downloaded by each thread. This is used in multithreaded downloading process. [NOTE: (No. of threads) * (this variable) = Total size of the file in bytes]
    private final List<FileOutputStream> fileOutputStreams; // This is the output stream of the temporary files which are a part of the total file and will be merged later.
    private final boolean isMultiThreadedDownloading; // This is a boolean which states whether multithreaded downloading is used or not (depends on file size) for the specific file to be downloaded.
    private long downloadedBytes; // This stores the total bytes of data downloaded
    private List<Long> downloadedBytesPerPart;
    private boolean downloading; // This is a boolean which states whether the file is currently being downloaded or not.
    private long downloadSpeed; // This is a variable which contains the download speed (This variable is used if multithreaded downloading is not used)
    private List<Long> downloadSpeeds; // This is a variable which contains the download speeds for all the threads running (This variable is used if multithreaded downloading is used)
    private static int totalDownloadPercent; // This is the total download percent
    private long totalDownloadedBytes; // This is the total size of the file. This is also used in multiple threads to store the size of file downloaded by each threads

    public ProgressBarThread(List<FileOutputStream> fileOutputStreams, List<Long> partSizes, String fileName) {
        this.partSizes = partSizes;
        this.fileName = fileName;
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
        if (downloading) {
            System.out.println(DOWNLOADING + fileName + " ...");
        }
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
            totalDownloadPercent = (int) ((downloadedBytes * 100) / totalDownloadedBytes);
            bar = bar.substring(0, charAmt / 2 - 2) + String.format("%02d", totalDownloadPercent) + "%" + bar.substring(charAmt / 2 + 1);
            return "[" + spinner + "]  " + fileName + "  (0KB)[" + bar + "](" + convertBytes(totalDownloadedBytes) + ")  " + (float) downloadSpeed / 1000000 + " MB/s      ";
        } else {
            StringBuilder result = new StringBuilder("[" + spinner + "]  " + convertBytes(totalDownloadedBytes));
            float filled;
            totalDownloadedBytes = 0;
            for (int i = 0; i < fileOutputStreams.size(); i++) {
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
                filled = downloadedBytesPerPart.get(i) / ((float) charPercents.get(i));
                totalDownloadedBytes += downloadedBytesPerPart.get(i);
                String a = new String(new char[(int) filled]).replace("\0", "=");
                String b = new String(new char[charAmt - (int) filled]).replace("\0", ".");
                String bar = a + b;
                long downloadPercentForSpecificThreads = downloadedBytesPerPart.get(i) * 100 / partSizes.get(i);
                bar = bar.substring(0, charAmt / 2 - 2) + String.format("%02d", (int) (downloadPercentForSpecificThreads)) + "%" + bar.substring(charAmt / 2 + 1);
                result.append(" [").append(bar).append("] ").append(String.format("%.2f", (float) downloadSpeeds.get(i) / 1000000)).append(" MB/s");
            }
            totalDownloadPercent = (int) ((totalDownloadedBytes) / charPercents.get(0));
            System.out.println("\n" + (totalDownloadedBytes / (3L *charPercents.get(0))));
            return result.toString();
        }
    }

    public static int getTotalDownloadPercent() {
        return totalDownloadPercent;
    }

    /**
     * This method converts <b>bytes</b> to <b>bigger units</b> like <i>KB, MB, GB</i> , etc.
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
        System.out.println("\r" + generateProgressBar("/"));
        if (isMultiThreadedDownloading) {
            String sizeWithUnit = convertBytes(totalDownloadedBytes);
            System.out.println(DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + SUCCESSFULLY);
            Drifty_CLI.logger.log(LOGGER_INFO, DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + SUCCESSFULLY);
        } else if (downloadedBytes == totalDownloadedBytes) {
            String sizeWithUnit = convertBytes(downloadedBytes);
            System.out.println(DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + SUCCESSFULLY);
            Drifty_CLI.logger.log(LOGGER_INFO, DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + SUCCESSFULLY);
        } else {
            System.out.println(DOWNLOAD_FAILED);
            Drifty_CLI.logger.log(LOGGER_ERROR, DOWNLOAD_FAILED);
        }
    }

    /**
     * run method for progress bar.
     */
    @Override
    public void run() {
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
                        System.out.print("\r" + generateProgressBar(spinner[i]));
                    }
                } else {
                    for (int i = 0; i <= 3; i++) {
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
                        System.out.print("\r" + generateProgressBar(spinner[i]));
                    }
                }
            } catch (InterruptedException | IOException ignored) {

            }
        }
        cleanup();
    }
}
