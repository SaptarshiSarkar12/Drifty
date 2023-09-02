package GUI.Support;

/**
 * This is a data structure class for batch jobs. It holds the relevant information for a batch job
 */
public class Job {
    private String link;
    private String dir;
    private String filename;

    public Job(String link, String dir, String filename) {
        this.link = link;
        this.dir = dir;
        this.filename = filename;
    }

    public Job(Job job) {
        this.link = job.getLink();
        this.dir = job.getDir();
        this.filename = job.getFilename();
    }

    public boolean matches(Job otherJob) {
        return otherJob.getLink().equals(link) && otherJob.getDir().equals(dir) && otherJob.getFilename().equals(filename);
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

    @Override
    public String toString() {
        return filename;
    }
}
