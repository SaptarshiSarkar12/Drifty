package properties;

import org.junit.jupiter.api.*;

@DisplayName("Test Link Type detection")
class LinkTypeTest {
    @Test
    @DisplayName("Test YouTube link detection")
    void testYouTubeLink() {
        // Test YouTube Video Link
        Assertions.assertEquals(LinkType.YOUTUBE, LinkType.getLinkType("https://www.youtube.com/watch?v=pBy1zgt0XPc&pp=ygUGZ2l0aHVi"));
        // Test YouTube Playlist Link
        Assertions.assertEquals(LinkType.YOUTUBE, LinkType.getLinkType("https://www.youtube.com/playlist?list=PLX8CzqL3ArzW90jKUCf4H6xCKpStxsOzp"));
    }

    @Test
    @DisplayName("Test Instagram link detection")
    void testInstagramLink() {
        // Test Instagram post link
        Assertions.assertEquals(LinkType.INSTAGRAM, LinkType.getLinkType("https://www.instagram.com/p/BDin77DxtAH/"));
        // Test Instagram reels' link
        Assertions.assertEquals(LinkType.INSTAGRAM, LinkType.getLinkType("https://www.instagram.com/github/reel/C0y8FOLMjGu/"));
    }

    @Test
    @DisplayName("Test Spotify link detection")
    void testSpotifyLink() {
        // Test Spotify song link
        Assertions.assertEquals(LinkType.SPOTIFY, LinkType.getLinkType("https://open.spotify.com/track/4ymPEyiXabTe1NO8q8EFxG"));
        // Test Spotify playlist link
        Assertions.assertEquals(LinkType.SPOTIFY, LinkType.getLinkType("https://open.spotify.com/playlist/37i9dQZF1DX5trt9i14X7j"));
    }

    @Test
    @DisplayName("Test Other link detection")
    void testOtherLink() {
        // Test GitHub Link
        Assertions.assertEquals(LinkType.OTHER, LinkType.getLinkType("https://github.com/SaptarshiSarkar12/Drifty/blob/master/.github/workflows/build.yml"));
        // Test any other file Link
        Assertions.assertEquals(LinkType.OTHER, LinkType.getLinkType("https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb"));
    }
}