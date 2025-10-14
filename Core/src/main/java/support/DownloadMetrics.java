package support;

import lombok.Data;

@Data
/*
 * Last Modified : @kuntal1461
 */
public class DownloadMetrics {
    private boolean active;
    private float progressPercent;
    private long totalSize;
    private boolean multithreading;

    public int getThreadCount() {
        return 6;
    }

    public boolean isMultithreadingEnabled() {
        return multithreading;
    }

    public long getMultiThreadingThreshold() {
        // 50 MB
        return 52428800;
    }
}
