package backend;

import cli_init.Environment;
import cli_utils.MessageBroker;
import cli_utils.Utility;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@DisplayName("Test File Downloader of CLI")
public class FileDownloaderTest {
    @BeforeAll
    @DisplayName("Initialize Test Environment")
    static void initAll() {
        MessageBroker msgBroker = new MessageBroker(System.out);
        Environment.setMessageBroker(msgBroker);
        Environment.initializeEnvironment();
        try {
            String[] testDirectories = {"github", "youtube", "instagram", "spotify", "other"};
            for (String testDirectory : testDirectories) {
                FileUtils.forceMkdir(Paths.get(System.getProperty("user.dir"), "target", "test", "data", testDirectory).toFile());
            }
        } catch (IOException e) {
            Assertions.fail("Failed to create test directories! " + e.getMessage());
        } catch (SecurityException e) {
            Assertions.fail("Permission to create test directories denied! " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test GitHub File Download")
    void testGitHubFileDownload() {
        String link = "https://github.com/SaptarshiSarkar12/Drifty/blob/master/.github/workflows/build.yml";
        String filename = Utility.findFilenameInLink(link);
        FileDownloader fileDownloader = new FileDownloader(link, filename, Paths.get(System.getProperty("user.dir"), "target", "test", "data", "github").toAbsolutePath().toString());
        fileDownloader.run();
        File file = Paths.get(System.getProperty("user.dir"), "target", "test", "data", "github", filename).toAbsolutePath().toFile();
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertTrue(file.length() > 0);
        Assertions.assertTrue(file.getName().endsWith(".yml"));
    }

    @Test
    @DisplayName("Test YouTube File Download")
    void testYouTubeFileDownload() {
        String link = "https://www.youtube.com/watch?v=pBy1zgt0XPc&pp=ygUGZ2l0aHVi";
        String filename = Utility.findFilenameInLink(link);
        FileDownloader fileDownloader = new FileDownloader(link, filename, Paths.get(System.getProperty("user.dir"), "target", "test", "data", "youtube").toAbsolutePath().toString());
        fileDownloader.run();
        File file = Paths.get(System.getProperty("user.dir"), "target", "test", "data", "youtube", filename).toAbsolutePath().toFile();
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertTrue(file.length() > 0);
        Assertions.assertTrue(file.getName().endsWith(".mp4"));
    }

    @Test
    @DisplayName("Test Instagram File Download")
    void testInstagramFileDownload() {
        String link = "https://www.instagram.com/p/BDin77DxtAH/";
        String filename = Utility.findFilenameInLink(link);
        FileDownloader fileDownloader = new FileDownloader(link, filename, Paths.get(System.getProperty("user.dir"), "target", "test", "data", "instagram").toAbsolutePath().toString());
        fileDownloader.run();
        File file = Paths.get(System.getProperty("user.dir"), "target", "test", "data", "instagram", filename).toAbsolutePath().toFile();
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertTrue(file.length() > 0);
        Assertions.assertTrue(file.getName().endsWith(".mp4"));
    }

    @Test
    @DisplayName("Test Spotify File Download")
    void testSpotifyFileDownload() {
        String link = "https://open.spotify.com/track/0IGXY47K2ha3AHfX57wY1O";
        String filename = Utility.findFilenameInLink(link);
        FileDownloader fileDownloader = new FileDownloader(link, filename, Paths.get(System.getProperty("user.dir"), "target", "test", "data", "spotify").toAbsolutePath().toString());
        fileDownloader.run();
        File file = Paths.get(System.getProperty("user.dir"), "target", "test", "data", "spotify", filename).toAbsolutePath().toFile();
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertTrue(file.length() > 0);
        Assertions.assertTrue(file.getName().endsWith(".mp3"));
    }

    @Test
    @DisplayName("Test Other File Download")
    void testOtherFileDownload() {
        String link = "https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb";
        String filename = Utility.findFilenameInLink(link);
        FileDownloader fileDownloader = new FileDownloader(link, filename, Paths.get(System.getProperty("user.dir"), "target", "test", "data", "other").toAbsolutePath().toString());
        fileDownloader.run();
        File file = Paths.get(System.getProperty("user.dir"), "target", "test", "data", "other", filename).toAbsolutePath().toFile();
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertTrue(file.length() > 0);
        Assertions.assertTrue(file.getName().endsWith(".deb"));
    }

    @AfterAll
    @DisplayName("Clean up Test Environment")
    static void cleanUp() {
        try {
            FileUtils.deleteDirectory(Paths.get(System.getProperty("user.dir"), "target", "test").toFile());
        } catch (IOException e) {
            Assertions.fail("Failed to delete test directories! " + e.getMessage());
        }
    }
}
