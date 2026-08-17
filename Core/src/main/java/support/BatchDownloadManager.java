package support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BatchDownloadManager {
    private final int maxConcurrency;
    private final BatchDownloadListener listener;

    public BatchDownloadManager(int maxConcurrency, BatchDownloadListener listener) {
        this.maxConcurrency = Math.max(1, maxConcurrency);
        this.listener = listener == null ? new BatchDownloadListener() {
        } : listener;
    }

    public BatchDownloadResult execute(Collection<Job> jobs, DownloadWorkerFactory workerFactory) {
        List<Job> jobList = new ArrayList<>(jobs);
        if (jobList.isEmpty()) {
            BatchDownloadResult empty = new BatchDownloadResult(0, 0, 0, 0, 0, 0, List.of());
            listener.onBatchCompleted(empty);
            return empty;
        }

        AtomicInteger startedJobs = new AtomicInteger();
        AtomicInteger runningJobs = new AtomicInteger();
        AtomicInteger completedJobs = new AtomicInteger();
        AtomicInteger failedJobs = new AtomicInteger();
        AtomicLong downloadedBytes = new AtomicLong();
        AtomicLong knownTotalBytes = new AtomicLong();
        Map<Job, Long> knownJobSizes = new ConcurrentHashMap<>();
        long startedAt = System.currentTimeMillis();

        DownloadProgressListener progressListener = new DownloadProgressListener() {
            @Override
            public void onStart(Job job, long totalBytes) {
                startedJobs.incrementAndGet();
                runningJobs.incrementAndGet();
                if (totalBytes > 0 && knownJobSizes.putIfAbsent(job, totalBytes) == null) {
                    knownTotalBytes.addAndGet(totalBytes);
                }
                listener.onJobStarted(job, snapshot(jobList.size(), startedJobs, runningJobs, completedJobs, failedJobs, downloadedBytes, knownTotalBytes, startedAt));
            }

            @Override
            public void onProgress(Job job, long bytesDelta, long jobDownloadedBytes, long totalBytes) {
                if (totalBytes > 0 && knownJobSizes.putIfAbsent(job, totalBytes) == null) {
                    knownTotalBytes.addAndGet(totalBytes);
                }
                if (bytesDelta > 0) {
                    downloadedBytes.addAndGet(bytesDelta);
                }
            }

            @Override
            public void onComplete(DownloadResult result) {
                runningJobs.decrementAndGet();
                if (result.success()) {
                    completedJobs.incrementAndGet();
                } else {
                    failedJobs.incrementAndGet();
                }
                listener.onJobCompleted(result, snapshot(jobList.size(), startedJobs, runningJobs, completedJobs, failedJobs, downloadedBytes, knownTotalBytes, startedAt));
            }
        };

        List<DownloadResult> results = new CopyOnWriteArrayList<>();
        listener.onBatchStart(snapshot(jobList.size(), startedJobs, runningJobs, completedJobs, failedJobs, downloadedBytes, knownTotalBytes, startedAt));

        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable, "batch-download-worker");
            thread.setDaemon(true);
            return thread;
        };

        ExecutorService workerPool = Executors.newFixedThreadPool(Math.min(maxConcurrency, jobList.size()), threadFactory);
        ScheduledExecutorService progressReporter = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "batch-download-progress");
            thread.setDaemon(true);
            return thread;
        });
        CompletionService<DownloadResult> completionService = new ExecutorCompletionService<>(workerPool);

        try {
            progressReporter.scheduleAtFixedRate(
                    () -> listener.onBatchProgress(snapshot(jobList.size(), startedJobs, runningJobs, completedJobs, failedJobs, downloadedBytes, knownTotalBytes, startedAt)),
                    0,
                    1,
                    TimeUnit.SECONDS
            );

            for (Job job : jobList) {
                completionService.submit(workerFactory.create(job, progressListener));
            }

            for (int i = 0; i < jobList.size(); i++) {
                Future<DownloadResult> future = completionService.take();
                DownloadResult result = future.get();
                results.add(result);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch download interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Batch download failed", e.getCause());
        } finally {
            progressReporter.shutdownNow();
            workerPool.shutdownNow();
        }

        BatchDownloadResult batchResult = new BatchDownloadResult(
                jobList.size(),
                completedJobs.get(),
                failedJobs.get(),
                downloadedBytes.get(),
                knownTotalBytes.get(),
                System.currentTimeMillis() - startedAt,
                List.copyOf(results)
        );
        listener.onBatchCompleted(batchResult);
        return batchResult;
    }

    private BatchProgressSnapshot snapshot(
            int totalJobs,
            AtomicInteger startedJobs,
            AtomicInteger runningJobs,
            AtomicInteger completedJobs,
            AtomicInteger failedJobs,
            AtomicLong downloadedBytes,
            AtomicLong knownTotalBytes,
            long startedAt
    )
    {
        int queuedJobs = Math.max(0, totalJobs - startedJobs.get());
        long elapsedMillis = Math.max(1, System.currentTimeMillis() - startedAt);
        long speed = downloadedBytes.get() * 1000 / elapsedMillis;
        return new BatchProgressSnapshot(
                totalJobs,
                queuedJobs,
                runningJobs.get(),
                completedJobs.get(),
                failedJobs.get(),
                downloadedBytes.get(),
                knownTotalBytes.get(),
                speed,
                elapsedMillis
        );
    }
}
