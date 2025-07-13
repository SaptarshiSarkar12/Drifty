package cli.support;

import cli.init.TestEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import properties.LinkType;
import support.DownloadConfiguration;

import java.nio.file.Path;
import java.util.stream.Stream;

public class DownloadConfigurationTest extends TestEnvironment {
    @TempDir
    static Path tempDir;

    @ParameterizedTest
    @DisplayName("Test Link Type detection functionality")
    @MethodSource("linkAndExpectedLinkTypeProvider")
    public void testLinkTypeDetection(String link, LinkType expectedLinkType) {
        DownloadConfiguration config = new DownloadConfiguration(link, tempDir.toString(), null);
        LinkType detectedLinkType = config.getLinkType();
        assert detectedLinkType == expectedLinkType : "Detected link type does not match expected type! Expected: " + expectedLinkType + ", but got: " + detectedLinkType;
    }

    @ParameterizedTest
    @DisplayName("Test sanitize link functionality")
    @MethodSource("linkAndExpectedSanitizedLinkProvider")
    public void testSanitizeLink(String link, String expectedSanitizedLink) {
        DownloadConfiguration config = new DownloadConfiguration(link, tempDir.toString(), null);
        config.sanitizeLink();
        String sanitizedLink = config.getLink();
        assert sanitizedLink.equals(expectedSanitizedLink) : "Sanitized link does not match expected link! Expected: " + expectedSanitizedLink + ", but got: " + sanitizedLink;
    }

    private static Stream<Arguments> linkAndExpectedLinkTypeProvider() {
        return Stream.of(
            Arguments.of("https://youtu.be/pBy1zgt0XPc?feature=shared", LinkType.YOUTUBE),
            Arguments.of("https://www.youtube.com/playlist?list=PL0lo9MOBetEFGPccyxyfex8BYF_PQUQWn", LinkType.YOUTUBE),
            Arguments.of("https://www.youtube.com/watch?v=pBy1zgt0XPc", LinkType.YOUTUBE),
            Arguments.of("https://www.instagram.com/reel/DLJLsjaJrXI/", LinkType.INSTAGRAM),
            Arguments.of("https://www.instagram.com/p/C2cnVLjr1vZ/", LinkType.INSTAGRAM),
            Arguments.of("https://open.spotify.com/track/4cOdK2wGLETKBW3PvgPWqT", LinkType.SPOTIFY),
            Arguments.of("https://open.spotify.com/playlist/0qyhqmsAfxVzlQiFmILPdG", LinkType.SPOTIFY),
            Arguments.of("https://github.com/SaptarshiSarkar12/Drifty/blob/master/.github/workflows/static.yml", LinkType.OTHER)
        );
    }

    private static Stream<Arguments> linkAndExpectedSanitizedLinkProvider() {
        return Stream.of(
            Arguments.of("  https://github.com\\SaptarshiSarkar12\\Drifty\\blob\\master\\.github/workflows/static.yml?raw=true  ",
                          "https://github.com/SaptarshiSarkar12/Drifty/blob/master/.github/workflows/static.yml?raw=true"),
            Arguments.of("  https://www.instagram.com/reel/DLJLsjaJrXI/  ",
                          "https://www.instagram.com/reel/DLJLsjaJrXI/?utm_source=ig_embed"),
            Arguments.of("http://www.youtube.com\\watch?v=pBy1zgt0XPc",
                          "https://www.youtube.com/watch?v=pBy1zgt0XPc")
        );
    }
}
