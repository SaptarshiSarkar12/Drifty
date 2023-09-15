package GUI.Support;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is a data structure class for batch jobs. It holds the relevant information for a batch job
 */
public class Job {
    private String link;
    private String dir;
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

    public Job(Job job) {
        this.link = job.getLink();
        this.dir = job.getDir();
        this.filename = job.getFilename();
        this.repeatDownload = job.repeatOK();
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

    public void setLink(String link) {
        this.link = link;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean fileExists() {
        Path path = Paths.get(dir,filename);
        return path.toFile().exists();
    }

    private String getName() {
        String[] nameParts = link.split("/");
        return nameParts[nameParts.length - 1];
    }

    public void repeatApproved() {
        repeatDownload = true;
    }

    public boolean repeatOK() {
        return repeatDownload;
    }

    public boolean noRepeat() {
        return !repeatDownload;
    }

    @Override
    public String toString() {
        return filename;
    }
}
