package support;

import java.util.List;

public record BatchDownloadResult(
        int totalJobs,
        int completedJobs,
        int failedJobs,
        long downloadedBytes,
        long totalBytes,
        long elapsedMillis,
        List<DownloadResult> results
)
{
}
