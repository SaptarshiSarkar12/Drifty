import Backend.FileDownloader;
import Utils.Utility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Objects;

public class InstagramFileDownloadTest extends DriftyEnvironmentTest {
    private static FileDownloader fileDownloader;
    private static String filename;

    @BeforeAll
    @DisplayName("Initialize FileDownloader for Instagram video")
    static void initAll() {
        DriftyEnvironmentTest.initAll();
        LinkedList<String> linkMetadataList = Utility.getLinkMetadata("https://www.instagram.com/p/BDin77DxtAH/");
        for (String json : Objects.requireNonNull(linkMetadataList)) {
            filename = Utility.getFilenameFromJson(json);
        }
        fileDownloader = new FileDownloader("https://www.instagram.com/p/BDin77DxtAH/", Utility.cleanFilename(filename), "./target/test/data/instagram/");
    }

    @Test
    @DisplayName("Test for downloading Instagram video")
    void checkDownloadOfInstagramVideo() {
        fileDownloader.run();
        // Check if the Instagram video has been downloaded and exists
        File file = new File(Paths.get("./target/test/data/instagram/", Utility.cleanFilename(filename)).toAbsolutePath().toString());
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        // Check if the Instagram video is not empty
        Assertions.assertTrue(file.length() > 0);
    }
}
