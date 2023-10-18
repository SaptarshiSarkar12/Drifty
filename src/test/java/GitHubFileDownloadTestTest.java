import Backend.FileDownloader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

public class GitHubFileDownloadTestTest extends DriftyEnvironmentTest {
    private final FileDownloader fileDownloader = new FileDownloader("https://github.com/SaptarshiSarkar12/Drifty/blob/master/README.md", "README.md", "./target/test/data/github/");

    @Test
    @DisplayName("Check parameters of FileDownloader for GitHub file")
    void checkParameters() {
        // Check if URL is not null and has "?raw=true" appended
        Assertions.assertNotNull(fileDownloader.getLink());
        Assertions.assertTrue(fileDownloader.getLink().endsWith("?raw=true"));
        // Check if the filename is not null
        Assertions.assertNotNull(fileDownloader.getFileName());
        Assertions.assertEquals("README.md", fileDownloader.getFileName());
        // Check if the directory is not null
        Assertions.assertNotNull(fileDownloader.getDir());
        Assertions.assertEquals("./target/test/data/github/", fileDownloader.getDir());
    }

    @Test
    @DisplayName("Test for downloading the GitHub file")
    void checkDownload() {
        // Run the file downloader
        fileDownloader.run();
        // Check if "README.md" file has been downloaded and exists
        File file = new File(Paths.get("./target/test/data/github/", "README.md").toAbsolutePath().toString());
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        // Check if "README.md" file is not empty
        Assertions.assertTrue(file.length() > 0);
    }
}
