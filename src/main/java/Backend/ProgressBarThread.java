package Backend;

import Enums.Mode;
import Utils.Environment;
import Utils.MessageBroker;
import com.simtechdata.unitconverter.Converter;
import javafx.beans.property.DoubleProperty;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static Utils.DriftyConstants.*;
import static com.simtechdata.unitconverter.Converter.Category.DATA;
import static com.simtechdata.unitconverter.Converter.UnitDefinition.*;

/**
 * This is the class responsible for showing the progress bar in the CLI (Command Line Interface) and enables progress bar values to be updated in the GUI (Graphical User Interface).
 */
public class ProgressBarThread extends Thread {
    private final static MessageBroker M = Environment.getMessageBroker();
    private final float charPercent;
    private final List<Long> partSizes;
    private final String fileName;
    private final FileOutputStream fos;
    private final int charAmt;
    private final List<Integer> charPercents;
    private final List<FileOutputStream> fileOutputStreams;
    private final boolean isMultiThreadedDownloading;
    private final DownloadMetrics downloadMetrics;
    private long downloadedBytes;
    private List<Long> downloadedBytesPerPart;
    private long totalSizeOfTheFile;
    private long downloadSpeed;
    private List<Long> downloadSpeeds;
    private long totalDownloadedBytes;
    private DoubleProperty progress;
    private String dir;
    private final String[] spinBars = new String[]{"/", "-", "\\", "|"};
    private int spinBarIndex = -1;

    public ProgressBarThread(List<FileOutputStream> fileOutputStreams, List<Long> partSizes, String fileName, String dir, Long totalSize, DownloadMetrics downloadMetrics) {
        this.partSizes = partSizes;
        this.fileName = fileName;
        this.dir = dir;
        this.fileOutputStreams = fileOutputStreams;
        this.downloadMetrics = downloadMetrics;
        downloadMetrics.setTotalSize(totalSize);
        downloadMetrics.setActive(true);
        charPercent = 0;
        fos = null;
        totalDownloadedBytes = 0;
        charAmt = 80 / fileOutputStreams.size(); // value to determine length of terminal progressbar
        isMultiThreadedDownloading = downloadMetrics.isMultithreaded();
        charPercents = new ArrayList<>(fileOutputStreams.size());
        downloadedBytesPerPart = new ArrayList<>(fileOutputStreams.size());
        downloadSpeeds = new ArrayList<>(fileOutputStreams.size());
        for (int i = 0; i < fileOutputStreams.size(); i++) {
            charPercents.add((int) (partSizes.get(i) / charAmt));
        }
    }

    public ProgressBarThread(FileOutputStream fos, long totalDownloadedBytes, String fileName, DownloadMetrics downloadMetrics) {
        this.downloadMetrics = downloadMetrics;
        downloadMetrics.setActive(true);
        this.charAmt = 20; // value to determine length of terminal progressbar
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

    private String generateProgressBar() {
        spinBarIndex ++;
        if(spinBarIndex == 3)
            spinBarIndex = 0;
        String spinner = spinBars[spinBarIndex];
        if (!isMultiThreadedDownloading) {
            float filled = downloadedBytes / charPercent;
            String a = new String(new char[(int) filled]).replace("\0", "=");
            String b = new String(new char[charAmt - (int) filled]).replace("\0", ".");
            String bar = a + b;
            downloadMetrics.setProgressPercent(100f * downloadedBytes / totalDownloadedBytes);
            float downloadSpeedWithoutUnit;
            String downloadSpeedUnit;
            float totalDownloadPercent = downloadMetrics.getProgressPercent();
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
            downloadMetrics.setProgressPercent(100f * totalDownloadedBytes / totalSizeOfTheFile);
            float totalDownloadPercent = downloadMetrics.getProgressPercent();
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

    private final Converter c = new Converter.Builder(DATA).units(BYTE).decimals(2).locale(Locale.getDefault()).build();
    private String convertBytes(long bytes) {
        double base = (double) bytes / 10.0;
        String sizeWithUnit = base + " bytes";
        double trackBytes = 0.0;
        if (base > 1000) {
            trackBytes = c.convert(base, KILOBYTE);
            sizeWithUnit = c.convertToString(base, KILOBYTE);
        }
        if (trackBytes > 1000) {
            trackBytes = c.convert(base, MEGABYTE);
            sizeWithUnit = c.convertToString(base, MEGABYTE);
        }
        if (trackBytes > 1000) {
            sizeWithUnit = c.convertToString(base, GIGABYTE);
        }
        return sizeWithUnit;
    }

    private void cleanup() {
        downloadMetrics.setProgressPercent(0f);
        if (isMultiThreadedDownloading) {
            String sizeWithUnit = convertBytes(totalDownloadedBytes);
            System.out.println();
            M.msgDownloadInfo(DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + dir + fileName + SUCCESSFULLY);
        } else if (downloadedBytes == totalDownloadedBytes) {
            String sizeWithUnit = convertBytes(downloadedBytes);
            System.out.println();
            M.msgDownloadInfo(DOWNLOADED + fileName + OF_SIZE + sizeWithUnit + " at " + dir + fileName + SUCCESSFULLY);
        } else {
            System.out.println();
            M.msgDownloadError(DOWNLOAD_FAILED);
        }
    }

    @Override
    public void run() {
        long initialMeasurement;
        List<Long> initialMeasurements = isMultiThreadedDownloading ? new ArrayList<>(fileOutputStreams.size()) : null;
        this.totalSizeOfTheFile = downloadMetrics.getTotalSize();
        boolean downloading = downloadMetrics.isActive();
        while (downloading) {
            try {
                if (!isMultiThreadedDownloading) {
                    for (int i = 0; i <= 3; i++) {
                        initialMeasurement = fos.getChannel().size();
                        Thread.sleep(250);
                        downloadedBytes = fos.getChannel().size();
                        downloadSpeed = (downloadedBytes - initialMeasurement) * 4;
                        if (Mode.isCLI()) {
                            System.out.print("\r" + generateProgressBar());
                        } else {
                            generateProgressBar();
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
                            System.out.print("\r" + generateProgressBar());
                        } else {
                            generateProgressBar();
                        }
                    }
                }
            } catch (InterruptedException | IOException ignored) {}
            finally {
                downloading = downloadMetrics.isActive();
            }
        }
        cleanup();
    }
}
