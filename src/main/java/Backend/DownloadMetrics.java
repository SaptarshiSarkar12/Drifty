package Backend;

public class DownloadMetrics {
    private boolean isDownloadActive;
    private float downloadPercentage;
    private long totalSize;
    private boolean supportsMultiThreading;
    private final int numberOfThreads = 3;
    private final long threadingThreshold = 52428800; // 50 MB

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

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
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

    public long getThreadingThreshold() {
        return threadingThreshold;
    }
}
