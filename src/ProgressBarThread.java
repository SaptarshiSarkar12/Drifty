import java.io.FileOutputStream;
import java.io.IOException;

public class ProgressBarThread extends Thread {
    private final float charPercent;
    private long downloadedBytes;
    private boolean downloading;
    private long downloadSpeed;
    private final String fileName;
    private final FileOutputStream fos;
    public static long totalDownloadBytes;
    private final int charAmt;
    
   
    public ProgressBarThread(FileOutputStream fos, long totalDownloadBytes, String fileName) {
        this.charAmt = 30; // value to determine length of terminal progressbar
        this.downloading = true;
        this.downloadSpeed = 0;
        this.downloadedBytes = 0;
        this.totalDownloadBytes = totalDownloadBytes;
        this.fileName = fileName;
        this.fos = fos;
        this.charPercent = (this.totalDownloadBytes/charAmt);

    }
    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
        if (downloading) {
            System.out.println("Downloading " + fileName + " ...");
        }
        
    }
    private String generateProgressBar(String spinner) {
        float filled = downloadedBytes/charPercent;
        String bar = "=".repeat((int)filled)+".".repeat(charAmt-(int)filled);
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
            System.out.println("Download "+fileName+" ("+convertBytes(downloadedBytes)+") successful!");
            Drifty_CLI.logger.log("INFO", "Downloaded " + fileName + " of size " + ProgressBarThread.totalDownloadBytes + " at " + FileDownloader.getDir() + fileName);
        } else {
            System.out.println("Download failed...");
        }
    }
    public void run() {
        long initialMeasurement;
        String[] spinner = new String[]{"/", "-", "\\", "|"};
        while (downloading) {
            try {
                for (int i=0; i <= 3; i++)  {
                    initialMeasurement = fos.getChannel().size();
                    Thread.sleep(250);
                    downloadedBytes = fos.getChannel().size();
                    downloadSpeed = (downloadedBytes - initialMeasurement)*4;
                    System.out.print("\r" + generateProgressBar(spinner[i]));
                }
            } catch (InterruptedException | IOException ignored) {
            }
        }
        cleanup();
    }
}
