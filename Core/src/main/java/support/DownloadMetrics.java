package support;

import lombok.Data;

@Data
public class DownloadMetrics {
    private boolean active;
    private float progressPercent;
    private long totalSize;
    private boolean multithreadingEnabled;

    public int getThreadCount() {
        return 6;
    }

    public long getMultiThreadingThreshold() {
        // 50 MB
        return 52428800;
    }
}
