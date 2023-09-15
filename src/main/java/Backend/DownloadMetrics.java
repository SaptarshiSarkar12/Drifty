package Backend;

public class DownloadMetrics {
    private boolean active;
    private float progressPercent;
    private long totalSize;
    private boolean multithreaded;
    private final int threadCount = 3;
    private final long threadMaxDataSize = 52428800; // 50 MB

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

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

    public double getProgress() {
        return progressPercent / 100.0;
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
        return threadCount;
    }

    public long getThreadMaxDataSize() {
        return threadMaxDataSize;
    }
}
