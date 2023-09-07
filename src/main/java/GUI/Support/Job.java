package GUI.Support;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Job(String link, String dir) {
        this.link = link;
        this.dir = dir;
        this.filename = getName();
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

    public boolean fileExists() {
        Path path = Paths.get(dir,filename);
        return path.toFile().exists();
    }

    private String getName() {
        String name = link;
        String regex = "(/)([^/]+)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(link);
        if(m.find()) {
            name = m.group(2);
        }
        return name;
    }

    @Override
    public String toString() {
        return filename;
    }
}
