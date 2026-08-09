package support;

public record BatchProgressSnapshot(
        int totalJobs,
        int queuedJobs,
        int runningJobs,
        int completedJobs,
        int failedJobs,
        long downloadedBytes,
        long totalBytes,
        long bytesPerSecond,
        long elapsedMillis
) {
    public double progress() {
        if (totalBytes > 0) {
            return Math.min(1.0d, (double) downloadedBytes / totalBytes);
        }
        if (totalJobs <= 0) {
            return 0.0d;
        }
        return Math.min(1.0d, (double) (completedJobs + failedJobs) / totalJobs);
    }
}
