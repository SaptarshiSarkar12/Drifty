import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This is the class responsible for showing the progress bar in the console.
 */
public class ProgressBarThread extends Thread {
    private final float charPercent;
    private long downloadedBytes;
    private boolean downloading;
    private long downloadSpeed;
    private final String fileName;
    private final FileOutputStream fos;
    private final long totalDownloadBytes;
    private final int charAmt;
    
   
    public ProgressBarThread(FileOutputStream fos, long totalDownloadBytes, String fileName) {
        this.charAmt = 30; // value to determine length of terminal progressbar
        this.downloading = true;
        this.downloadSpeed = 0;
        this.downloadedBytes = 0;
        this.totalDownloadBytes = totalDownloadBytes;
        this.fileName = fileName;
        this.fos = fos;
        this.charPercent = (int)(this.totalDownloadBytes/charAmt);

    }
    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
        if (downloading) {
            System.out.println("Downloading " + fileName + " ...");
        }
    }
    private String generateProgressBar(String spinner) {
        float filled = downloadedBytes/charPercent;
        String a = new String(new char[(int)filled]).replace("\0", "=");
        String b = new String(new char[charAmt-(int)filled]).replace("\0", ".");
        String bar = a + b;
        bar = bar.substring(0, charAmt/2-2) + String.format("%02d", (int)(downloadedBytes*100/totalDownloadBytes)) +"%" + bar.substring(charAmt/2+1);
        return "["+spinner+"]  "+fileName+"  (0KB)["+bar+"]("+convertBytes(totalDownloadBytes)+")  " + (float)downloadSpeed/1000000 + " MB/s      ";
    }
    private String convertBytes(long bytes) {
        String sizeWithUnit;
        if (bytes> 1000) {
            bytes = bytes/1024;
            sizeWithUnit = bytes + " kilobytes";
            if (bytes> 1000) {
                bytes = bytes/1024;
                sizeWithUnit = bytes + " megabytes";
                if (bytes > 1000){
                    sizeWithUnit = totalDownloadBytes + "gigabytes";
                }
            }
            return sizeWithUnit;
        } else {
            return totalDownloadBytes+ " bytes";
        }
    }
    private void cleanup(){
        System.out.println("\r"+generateProgressBar("/"));
        if (downloadedBytes == totalDownloadBytes) {
            String sizeWithUnit = convertBytes(downloadedBytes);
            System.out.println("Downloaded " + fileName + " of size " + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + " successfully !");
            Drifty_CLI.logger.log("INFO", "Downloaded " + fileName + " of size " + sizeWithUnit + " at " + FileDownloader.getDir() + fileName + " successfully !");
        } else {
            System.out.println("Download failed!");
            Drifty_CLI.logger.log("ERROR", "Download failed!");
        }
    }
    @Override
    public void run() {
        long initialMeasurement;
        String[] spinner = new String[]{"/", "-", "\\", "|"};
        while (downloading) {
            try {
                for (int i=0; i <= 3; i++)  {
                    initialMeasurement = fos.getChannel().size();
                    Thread.sleep(250);
                    downloadedBytes = fos.getChannel().size();
                    downloadSpeed = (downloadedBytes - initialMeasurement) * 4;
                    System.out.print("\r" + generateProgressBar(spinner[i]));
                }
            } catch (InterruptedException | IOException ignored) {
            }
        }
        cleanup();
    }
}
