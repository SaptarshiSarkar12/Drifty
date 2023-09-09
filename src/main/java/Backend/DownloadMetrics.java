package Backend;

public class DownloadMetrics {
    private boolean isDownloadActive;
    private float downloadPercentage;
    private double totalSize;
    private boolean supportsMultiThreading;
    private int numberOfThreads;
    private long threadingThreshold;

    public boolean isDownloadActive() {
        return isDownloadActive;
    }

    public void setDownloadActive(boolean downloadActive) {
        isDownloadActive = downloadActive;
    }

    public float getDownloadPercentage() {
        return downloadPercentage;
    }

    public void setDownloadPercentage(float downloadPercentage) {
        this.downloadPercentage = downloadPercentage;
    }

    public double getProgress() {
        return downloadPercentage / 100.0;
    }

    public double getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(double totalSize) {
        this.totalSize = totalSize;
    }

    public boolean isSupportsMultiThreading() {
        return supportsMultiThreading;
    }

    public void setSupportsMultiThreading(boolean supportsMultiThreading) {
        this.supportsMultiThreading = supportsMultiThreading;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public long getThreadingThreshold() {
        return threadingThreshold;
    }

    public void setThreadingThreshold(long threadingThreshold) {
        this.threadingThreshold = threadingThreshold;
    }
}
