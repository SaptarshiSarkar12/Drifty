package Enums;

public enum DownloaderProps {
    IS_DOWNLOAD_ACTIVE, DOWNLOAD_PERCENTAGE, TOTAL_SIZE, SUPPORTS_MULTI_THREADING, NUMBER_OF_THREADS, THREADING_THRESHOLD;

    private static boolean isDownloadActive = false;
    private static float downloadPercentage = 0.0f;
    private static long totalSize = 0;
    private static boolean supportsMultiThreading = false;
    private static int numberOfThreads = 3;
    private static final long threadingThreshold = 52428800; // 52428800 bytes = 50 MB

    public static synchronized Object getValue(DownloaderProps prop) {
        switch (prop) {
            case IS_DOWNLOAD_ACTIVE -> {
                return isDownloadActive;
            }
            case DOWNLOAD_PERCENTAGE -> {
                return downloadPercentage;
            }
            case TOTAL_SIZE -> {
                return totalSize;
            }
            case SUPPORTS_MULTI_THREADING -> {
                return supportsMultiThreading;
            }
            case NUMBER_OF_THREADS -> {
                return numberOfThreads;
            }
            case THREADING_THRESHOLD -> {
                return threadingThreshold;
            }
            default -> {
                return null;
            }
        }
    }

    public static synchronized void setValue(DownloaderProps prop, Object value) {
        switch (prop) {
            case IS_DOWNLOAD_ACTIVE -> isDownloadActive = (boolean) value;
            case DOWNLOAD_PERCENTAGE -> downloadPercentage = (float) value;
            case TOTAL_SIZE -> totalSize = (long) value;
            case SUPPORTS_MULTI_THREADING -> supportsMultiThreading = (boolean) value;
            case NUMBER_OF_THREADS -> numberOfThreads = (int) value;
            default -> {}
        }
    }
}
