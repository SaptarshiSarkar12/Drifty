package support;

import java.io.File;
import java.nio.file.Paths;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
/*
 * last ModifiedBy : @kuntal1461
 */
public class Job {

    @EqualsAndHashCode.Include
    private final String link;

    @EqualsAndHashCode.Include
    private final String dir;

    @EqualsAndHashCode.Include
    private final String filename;

    private final String downloadLink;

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
        if (downloadLink != null) return downloadLink;
        if (link != null) return link;
        throw new IllegalStateException("Both link and downloadLink are null");
    }

    public File getFile() {
        return Paths.get(dir).resolve(filename).toFile();
    }

    public boolean fileExists() {
        return getFile().exists();
    }

    @Override
    public String toString() {
        // Preserve legacy behaviour so UI lists display only the filename
        return filename;
    }
}
