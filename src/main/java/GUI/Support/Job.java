package GUI.Support;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static Utils.Utility.cleanFilename;
import static Utils.Utility.randomString;

/**
 * This is a data structure class for batch jobs. It holds the relevant information for a batch job
 */
public class Job {
    private final String link;
    private final String dir;
    private String filename;
    private boolean repeatDownload = false;

    public Job(String link, String dir, String filename, boolean repeatDownload) {
        this.link = link;
        this.dir = dir;
        this.filename = filename;
        this.repeatDownload = repeatDownload;
    }

    public Job(String link, String dir) {
        this.link = link;
        this.dir = dir;
        this.filename = getName();
    }

    public boolean matches(Job otherJob) {
        return otherJob.getLink().equals(link) && otherJob.getDir().equals(dir) && otherJob.getFilename().equals(filename);
    }

    public boolean matchesLink(Job job) {
        return job.getLink().equals(link);
    }

    public boolean matchesLink(String link) {
        return this.link.equals(link);
    }

    public String getLink() {
        return link;
    }

    public String getDir() {
        return dir;
    }

    public String getFilename() {
        return this.filename;
    }

    public File getFile() {
        return Paths.get(dir, filename).toFile();
    }

    public boolean fileExists() {
        Path path = Paths.get(dir, filename);
        return path.toFile().exists();
    }

    private String getName() {
        String file = link.substring(link.lastIndexOf("/") + 1);
        if (file.isEmpty()) {
            return cleanFilename("Unknown_Filename_") + randomString(15);
        }
        // file.png?width=200 -> file.png
        filename = file.split("([?])")[0];
        return filename;
    }

    public boolean repeatOK() {
        return repeatDownload;
    }

    @Override
    public String toString() {
        return filename;
    }
}
