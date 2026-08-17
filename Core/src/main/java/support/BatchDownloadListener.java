package support;

public interface BatchDownloadListener {
    default void onBatchStart(BatchProgressSnapshot snapshot) {
    }

    default void onBatchProgress(BatchProgressSnapshot snapshot) {
    }

    default void onJobStarted(Job job, BatchProgressSnapshot snapshot) {
    }

    default void onJobCompleted(DownloadResult result, BatchProgressSnapshot snapshot) {
    }

    default void onBatchCompleted(BatchDownloadResult result) {
    }
}
