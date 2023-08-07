package GUI.Support;
/**
 * This is a data structure class for batch jobs. It holds the relevant information for a batch job
 */
public class Job {
    public Job(String link, String dir, String filename) {
        this.link = link;
        this.dir = dir;
        this.filename = filename;
    }

     private String link;
    private String dir;
    private String filename;
    private String error;
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

     public String getError() {
        return error;
    }

     public void setError(String error) {
        this.error = error;
    }

     @Override
    public String toString() {
        return filename;
    }
}
