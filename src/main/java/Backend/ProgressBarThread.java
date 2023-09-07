package Backend;

import Enums.DownloaderProps;
import Enums.MessageCategory;
import Enums.MessageType;
import Enums.Mode;
import Utils.Environment;
import Utils.MessageBroker;
import javafx.beans.property.DoubleProperty;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Utils.DriftyConstants.*;

/**
 * This is the class responsible for showing the progress bar in the CLI (Command Line Interface) and enables progress bar values to be updated in the GUI (Graphical User Interface).
 */
public class ProgressBarThread extends Thread {
    private final static MessageBroker messageBroker = Environment.getMessageBroker();
    private final float charPercent;
    private final List<Long> partSizes;
    private final String fileName;
    private final FileOutputStream fos;
    private final int charAmt;
    private final List<Integer> charPercents;
    private final List<FileOutputStream> fileOutputStreams;
    private final boolean isMultiThreadedDownloading;
    private long downloadedBytes;
    private List<Long> downloadedBytesPerPart;
    private long totalSizeOfTheFile;
    private long downloadSpeed;
    private List<Long> downloadSpeeds;
    private long totalDownloadedBytes;
    private DoubleProperty progress;

    public ProgressBarThread(List<FileOutputStream> fileOutputStreams, List<Long> partSizes, String fileName, Long totalSize) {
        this.partSizes = partSizes;
        this.fileName = fileName;
        DownloaderProps.setValue(DownloaderProps.TOTAL_SIZE, totalSize);
        this.fileOutputStreams = fileOutputStreams;
        charPercent = 0;
        fos = null;
        totalDownloadedBytes = 0;
        charAmt = 80 / fileOutputStreams.size(); // value to determine length of terminal progressbar
        isMultiThreadedDownloading = (boolean) DownloaderProps.getValue(DownloaderProps.SUPPORTS_MULTI_THREADING);
        DownloaderProps.setValue(DownloaderProps.IS_DOWNLOAD_ACTIVE, true);
        charPercents = new ArrayList<>(fileOutputStreams.size());
        downloadedBytesPerPart = new ArrayList<>(fileOutputStreams.size());
        downloadSpeeds = new ArrayList<>(fileOutputStreams.size());
        for (int i = 0; i < fileOutputStreams.size(); i++) {
            charPercents.add((int) (partSizes.get(i) / charAmt));
        }
    }

    public ProgressBarThread(FileOutputStream fos, long totalDownloadedBytes, String fileName) {
        this.charAmt = 20; // value to determine length of terminal progressbar
        DownloaderProps.setValue(DownloaderProps.IS_DOWNLOAD_ACTIVE, true);
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

    private String generateProgressBar(String spinner) {
        if (!isMultiThreadedDownloading) {
            float filled = downloadedBytes / charPercent;
            String a = new String(new char[(int) filled]).replace("\0", "=");
            String b = new String(new char[charAmt - (int) filled]).replace("\0", ".");
            String bar = a + b;
            DownloaderProps.setValue(DownloaderProps.DOWNLOAD_PERCENTAGE, (100f * downloadedBytes / totalDownloadedBytes));
            float downloadSpeedWithoutUnit;
            String downloadSpeedUnit;
            float totalDownloadPercent = (float) DownloaderProps.getValue(DownloaderProps.DOWNLOAD_PERCENTAGE);
            if ((int) totalDownloadPercent != 100) {
                String downloadSpeedWithUnit = convertBytes(downloadSpeed);
                int indexOfDownloadSpeedUnit = downloadSpeedWithUnit.indexOf(" ") + 1;
                downloadSpeedWithoutUnit = Float.parseFloat(downloadSpeedWithUnit.substring(0, indexOfDownloadSpeedUnit - 1));
                downloadSpeedUnit = downloadSpeedWithUnit.substring(indexOfDownloadSpeedUnit);
            } else {
                downloadSpeedWithoutUnit = 0;
                downloadSpeedUnit = "bytes";
            }
            bar = bar.substring(0, charAmt / 2 - 2) + (totalDownloadPercent) + "%" + bar.substring(charAmt / 2 + 1);
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
            DownloaderProps.setValue(DownloaderProps.DOWNLOAD_PERCENTAGE, (100f * totalDownloadedBytes / totalSizeOfTheFile));
            float totalDownloadPercent = (float) DownloaderProps.getValue(DownloaderProps.DOWNLOAD_PERCENTAGE);
            bar = bar.substring(0, (charAmt / 2) - 2) + String.format("%02d", (int) (totalDownloadPercent)) + "%" + bar.substring((charAmt / 2) + 1);
            float downloadSpeedWithoutUnit;
            String downloadSpeedUnit;
            if ((int) totalDownloadPercent != 100) {
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

    private void cleanup() {
        DownloaderProps.setValue(DownloaderProps.DOWNLOAD_PERCENTAGE, 0f);
        if (isMultiThreadedDownloading) {
            String sizeWithUnit = convertBytes(totalDownloadedBytes);
            System.out.println();
            messageBroker.sendMessage(DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + SUCCESSFULLY, MessageType.INFO, MessageCategory.DOWNLOAD);
        } else if (downloadedBytes == totalDownloadedBytes) {
            String sizeWithUnit = convertBytes(downloadedBytes);
            System.out.println();
            messageBroker.sendMessage(DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + SUCCESSFULLY, MessageType.INFO, MessageCategory.DOWNLOAD);
        } else {
            System.out.println();
            messageBroker.sendMessage(DOWNLOAD_FAILED, MessageType.ERROR, MessageCategory.DOWNLOAD);
        }
    }

    @Override
    public void run() {
        long initialMeasurement;
        String[] spinner = new String[]{"/", "-", "\\", "|"};
        List<Long> initialMeasurements = isMultiThreadedDownloading ? new ArrayList<>(fileOutputStreams.size()) : null;
        this.totalSizeOfTheFile = (long) DownloaderProps.getValue(DownloaderProps.TOTAL_SIZE);
        boolean downloading = (boolean) DownloaderProps.getValue(DownloaderProps.IS_DOWNLOAD_ACTIVE);
        while (downloading) {
            try {
                if (!isMultiThreadedDownloading) {
                    for (int i = 0; i <= 3; i++) {
                        initialMeasurement = fos.getChannel().size();
                        Thread.sleep(250);
                        downloadedBytes = fos.getChannel().size();
                        downloadSpeed = (downloadedBytes - initialMeasurement) * 4;
                        if (Mode.isCLI()) {
                            System.out.print("\r" + generateProgressBar(spinner[i]));
                        } else {
                            generateProgressBar(spinner[i]);
                        }
                    }
                }
                else {
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
                        if (Mode.isCLI()) {
                            System.out.print("\r" + generateProgressBar(spinner[i]));
                        } else {
                            generateProgressBar(spinner[i]);
                        }
                    }
                }
            } catch (InterruptedException | IOException ignored) {}
            finally {
                downloading = (boolean) DownloaderProps.getValue(DownloaderProps.IS_DOWNLOAD_ACTIVE);
            }
        }
        cleanup();
    }
}
