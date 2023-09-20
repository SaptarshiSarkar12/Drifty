package Backend;

public class DownloadMetrics {
    private boolean active;
    private float progressPercent;
    private long totalSize;
    private boolean multithreaded;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(float progressPercent) {
        this.progressPercent = progressPercent;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public boolean isMultithreaded() {
        return multithreaded;
    }

    public void setMultithreaded(boolean multithreaded) {
        this.multithreaded = multithreaded;
    }

    public int getThreadCount() {
        return 6;
    }

    public long getMultiThreadingThreshold() {
        // 50 MB
        return 52428800;
    }
}
