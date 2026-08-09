package support;

public record DownloadResult(
        Job job,
        boolean success,
        long downloadedBytes,
        long totalBytes,
        String message
) {
}
