package support;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

public class Job {
    private final String link;
    private final String dir;
    private final String filename;
    private final String downloadLink;

    public Job(String link, String dir, String filename, String downloadLink) {
        this.link = link;
        this.downloadLink = downloadLink;
        this.dir = dir;
        this.filename = filename;
    }

    public boolean matchesLink(Job job) {
        return job.getSourceLink().equals(link);
    }

    public boolean matchesLink(String link) {
        return this.link.equals(link);
    }

    public String getSourceLink() {
        return link;
    }

    public String getDownloadLink() {
        if (downloadLink == null) {
            return link;
        }
        return downloadLink;
    }

    public String getDir() {
        return dir;
    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return Paths.get(dir).resolve(filename).toFile();
    }

    public boolean fileExists() {
        return getFile().exists();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Job job) {
            return job.getSourceLink().equals(link) && job.getDir().equals(dir) && job.getFilename().equals(filename);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, dir, filename);
    }

    @Override
    public String toString() {
        // This method returns only the filename, else the hashCodes will appear in the ListView
        return filename;
    }
}
