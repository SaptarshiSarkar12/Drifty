package support;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface DownloadWorkerFactory {
    Callable<DownloadResult> create(Job job, DownloadProgressListener listener);
}
