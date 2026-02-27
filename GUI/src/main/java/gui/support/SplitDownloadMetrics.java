package gui.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class SplitDownloadMetrics {
    private final int id;
    private final long start;
    private final long end;
    private final String filename;
    private File file;
    private final URL url;
    private boolean failed;
    private boolean success;
    private boolean stop;


    public SplitDownloadMetrics(int id, long start, long end, String filename, URL url) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.filename = filename;
        this.url = url;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public FileOutputStream getFileOutputStream() {
        FileOutputStream fos;
        try {
            file = Files.createTempFile(filename.hashCode() + "_" + id, ".tmp").toFile();
            file.deleteOnExit();
            fos = new FileOutputStream(file);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fos;
    }

    public File getFile() {
        return file;
    }

    public URL getUrl() {
        return url;
    }

    public boolean failed() {
        return failed;
    }

    public void setFailed() {
        failed = true;
    }

    public void setSuccess() {
        success = !failed;
    }

    public boolean running() {
        return !success && !failed;
    }

    public boolean stop() {
        return stop;
    }

    public void setStop() {
        stop = true;
    }
}
