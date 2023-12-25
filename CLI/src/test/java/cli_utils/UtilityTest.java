package cli_utils;

import cli_init.Environment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Utility class of CLI")
class UtilityTest {
    @BeforeAll
    @DisplayName("Initialize Environment")
    static void initAll() {
        MessageBroker msgBroker = new MessageBroker(System.out);
        Environment.setMessageBroker(msgBroker);
        Environment.initializeEnvironment();
    }

    @Test
    @DisplayName("Test GitHub Filename Detection")
    void testGitHubFilenameDetection() {
        String filename = Utility.findFilenameInLink("https://github.com/SaptarshiSarkar12/Drifty/blob/master/.github/workflows/build.yml");
        Assertions.assertEquals("build.yml", filename);
    }

    @Test
    @DisplayName("Test YouTube Filename detection")
    void testYouTubeFilenameDetection() {
        String filename = Utility.findFilenameInLink("https://www.youtube.com/watch?v=pBy1zgt0XPc&pp=ygUGZ2l0aHVi");
        Assertions.assertEquals("What is GitHub.mp4", filename);
    }

    @Test
    @DisplayName("Test Instagram Filename detection")
    void testInstagramFilenameDetection() {
        // Instagram Video Post
        String filename = Utility.findFilenameInLink("https://www.instagram.com/p/BDin77DxtAH/");
        Assertions.assertEquals("Video by oreo.mp4", filename);
        // Instagram Reel
        filename = Utility.findFilenameInLink("https://www.instagram.com/reel/C0y8FOLMjGu/");
        Assertions.assertEquals("Video by github.mp4", filename);
    }

    @Test
    @DisplayName("Test Spotify Filename detection")
    void testSpotifyFilenameDetection() {
        String filename = Utility.findFilenameInLink("https://open.spotify.com/track/0IGXY47K2ha3AHfX57wY1O");
        Assertions.assertEquals("Deva Deva (From Brahmastra).mp3", filename);
    }

    @Test
    @DisplayName("Test Other Filename detection")
    void testOtherFilenameDetection() {
        String filename = Utility.findFilenameInLink("https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb");
        Assertions.assertEquals("jdk-21_linux-x64_bin.deb", filename);
    }
}