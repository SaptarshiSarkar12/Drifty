package support;

public interface DownloadProgressListener {
    default void onStart(Job job, long totalBytes) {
    }

    default void onProgress(Job job, long bytesDelta, long downloadedBytes, long totalBytes) {
    }

    default void onComplete(DownloadResult result) {
    }
}
