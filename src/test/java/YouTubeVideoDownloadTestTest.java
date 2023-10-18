import Backend.FileDownloader;
import Utils.Utility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

public class YouTubeVideoDownloadTestTest extends DriftyEnvironmentTest {
    private final FileDownloader fileDownloader = new FileDownloader("https://www.youtube.com/watch?v=pBy1zgt0XPc", Utility.cleanFilename("What is GitHub?.mp4"), "./target/test/data/youtube/");

    @Test
    @DisplayName("Check parameters of FileDownloader for YouTube video")
    void checkParameters() {
        // Check if URL is not null
        Assertions.assertNotNull(fileDownloader.getLink());
        Assertions.assertEquals("https://www.youtube.com/watch?v=pBy1zgt0XPc", fileDownloader.getLink());
        // Check if the filename is not null
        Assertions.assertNotNull(fileDownloader.getFileName());
        Assertions.assertEquals("What is GitHub.mp4", fileDownloader.getFileName());
        // Check if the directory is not null
        Assertions.assertNotNull(fileDownloader.getDir());
        Assertions.assertEquals("./target/test/data/youtube/", fileDownloader.getDir());
    }

    @Test
    @DisplayName("Test for downloading YouTube video")
    void checkDownloadOfYouTubeVideo() {
        fileDownloader.run();
        // Check if the YouTube video has been downloaded and exists
        File file = new File(Paths.get("./target/test/data/youtube/", Utility.cleanFilename("What is GitHub?.mp4")).toAbsolutePath().toString());
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        // Check if the YouTube video is not empty
        Assertions.assertTrue(file.length() > 0);
    }
}
