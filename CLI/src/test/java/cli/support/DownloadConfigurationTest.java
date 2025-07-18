package cli.support;

import cli.init.TestEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import properties.LinkType;
import support.DownloadConfiguration;
import utils.Utility;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class DownloadConfigurationTest extends TestEnvironment {
    @TempDir
    static Path tempDir;

    @ParameterizedTest
    @DisplayName("Test Link Type detection functionality")
    @MethodSource("linkAndExpectedLinkTypeProvider")
    @Execution(ExecutionMode.CONCURRENT)
    public void testLinkTypeDetection(String link, LinkType expectedLinkType) {
        DownloadConfiguration config = new DownloadConfiguration(link, tempDir.toString(), null);
        LinkType detectedLinkType = config.getLinkType();
        assert detectedLinkType == expectedLinkType : "Detected link type does not match expected type! Expected: " + expectedLinkType + ", but got: " + detectedLinkType;
    }

    @ParameterizedTest
    @DisplayName("Test sanitize link functionality")
    @MethodSource("linkAndExpectedSanitizedLinkProvider")
    @Execution(ExecutionMode.CONCURRENT)
    public void testSanitizeLink(String link, String expectedSanitizedLink) {
        DownloadConfiguration config = new DownloadConfiguration(link, tempDir.toString(), null);
        config.sanitizeLink();
        String sanitizedLink = config.getLink();
        assert sanitizedLink.equals(expectedSanitizedLink) : "Sanitized link does not match expected link! Expected: " + expectedSanitizedLink + ", but got: " + sanitizedLink;
    }

    @ParameterizedTest
    @DisplayName("Test file data retrieval functionality")
    @MethodSource("linkAndExpectedFileDataProvider")
    @Execution(ExecutionMode.CONCURRENT)
    public void testFileDataRetrieval(String link, ArrayList<HashMap<String, Object>> expectedFileData) {
        DownloadConfiguration config = new DownloadConfiguration(link, tempDir.toString(), null);
        config.sanitizeLink();
        config.fetchFileData();
        ArrayList<HashMap<String, Object>> fileData = config.getFileData();
        if (LinkType.getLinkType(link).equals(LinkType.SPOTIFY)) {
            for (HashMap<String, Object> trackData : fileData) {
                int index = fileData.indexOf(trackData);
                HashMap<String, Object> expectedTrackData = expectedFileData.get(index);
                if (trackData.get("filename").toString().startsWith("Unknown_Spotify_Song_")) {
                    assert expectedTrackData.get("filename").toString().startsWith("Unknown_Spotify_Song_") : "Filename for Spotify song did not match! Expected: " + expectedTrackData + ", but got: " + trackData;
                } else {
                    assert trackData.equals(expectedTrackData) : "Track Data for Spotify song did not match! \nExpected: " + expectedTrackData + ", \nbut got: " + trackData;
                }
            }
        } else {
            assert fileData.equals(expectedFileData) : "Retrieved file data does not match expected data! \nExpected: " + expectedFileData + ", \nbut got: " + fileData;
        }
    }

    private static Stream<Arguments> linkAndExpectedFileDataProvider() {
        return Stream.of(
                Arguments.of("https://youtu.be/pBy1zgt0XPc?feature=shared",
                        new ArrayList<>(List.of(new HashMap<String, Object>() {{
                            put("link", "https://youtu.be/pBy1zgt0XPc?feature=shared");
                            put("filename", "What is GitHub?.mp4");
                            put("directory", tempDir.toString());
                        }}))
                ),
                Arguments.of("https://www.youtube.com/playlist?list=PL0lo9MOBetEFGPccyxyfex8BYF_PQUQWn", getYTPlaylistFileData()),
                Arguments.of("https://open.spotify.com/playlist/2Vc2dyNFvVTbjCMmb4SbMA", getSpotifyPlaylistFileData()),
                Arguments.of("https://github.com/SaptarshiSarkar12/Drifty/blob/master/.github/workflows/static.yml",
                        new ArrayList<>(List.of(new HashMap<String, Object>() {{
                            put("link", "https://github.com/SaptarshiSarkar12/Drifty/blob/master/.github/workflows/static.yml?raw=true");
                            put("filename", "static.yml");
                            put("directory", tempDir.toString());
                        }}))
                ),
                Arguments.of("https://www.instagram.com/reel/DLJLsjaJrXI/",
                        new ArrayList<>(List.of(new HashMap<String, Object>() {{
                            put("link", "https://www.instagram.com/reel/DLJLsjaJrXI/?utm_source=ig_embed");
                            put("filename", "Video by github.mp4");
                            put("directory", tempDir.toString());
                        }}))
                )
        );
    }

    private static ArrayList<HashMap<String, Object>> getYTPlaylistFileData() {
        ArrayList<HashMap<String, Object>> fileData = new ArrayList<>();
        String[][] playlistData = {
                {"https://www.youtube.com/watch?v=uy_PEGgUF4U", "What is the GitHub CLI?.mp4"},
                {"https://www.youtube.com/watch?v=Grel10blARo", "What is code scanning?.mp4"},
                {"https://www.youtube.com/watch?v=sYJ3CHtT6WM", "What is Codespaces?.mp4"},
                {"https://www.youtube.com/watch?v=IqXNhakuwVc", "What is GitHub Copilot?.mp4"},
                {"https://www.youtube.com/watch?v=Mh8yZu01DI8", "What is Dependabot?.mp4"},
                {"https://www.youtube.com/watch?v=l7uo1d3R0Wo", "What is GitHub Desktop?.mp4"},
                {"https://www.youtube.com/watch?v=bErGYN3Ljz8", "What is GitHub Discussions?.mp4"},
                {"https://www.youtube.com/watch?v=DA_WbNRFPT0", "What is GitHub Advanced Security?.mp4"},
                {"https://www.youtube.com/watch?v=URmeTqglS58", "What is GitHub Actions?.mp4"},
                {"https://www.youtube.com/watch?v=6HWw7rhwvtY", "What is GitHub Issues?.mp4"},
                {"https://www.youtube.com/watch?v=ObPdcm6jWoQ", "What is GitHub Mobile?.mp4"},
                {"https://www.youtube.com/watch?v=bW_cMOhjhxA", "What is GitHub Packages?.mp4"},
                {"https://www.youtube.com/watch?v=bMqz_UkYg8w", "What is secret scanning?.mp4"},
                {"https://www.youtube.com/watch?v=EG45lEhSURY", "What is GitHub Sponsors?.mp4"}
        };
        for (String[] playlistDatum : playlistData) {
            HashMap<String, Object> fileEntry = new HashMap<>();
            fileEntry.put("link", playlistDatum[0]);
            fileEntry.put("filename", playlistDatum[1]);
            fileEntry.put("directory", tempDir.toString());
            fileData.add(fileEntry);
        }
        return fileData;
    }

    private static ArrayList<HashMap<String, Object>> getSpotifyPlaylistFileData() {
        ArrayList<HashMap<String, Object>> fileData = new ArrayList<>();
        String playlistLink = "https://open.spotify.com/playlist/2Vc2dyNFvVTbjCMmb4SbMA";
        ArrayList<HashMap<String, Object>> playlistMetadata = Utility.getSpotifyPlaylistMetadata(playlistLink);
        if (playlistMetadata == null || playlistMetadata.isEmpty()) {
            return new ArrayList<>();
        }
        for (HashMap<String, Object> trackMetadata : playlistMetadata) {
            String songName = Utility.cleanFilename(trackMetadata.get("songName").toString());
            if (songName.isEmpty()) {
                songName = "Unknown_Spotify_Song_".concat(Utility.randomString(5));
            }
            String downloadLink = Utility.getSpotifyDownloadLink(trackMetadata);
            if (downloadLink != null) {
                HashMap<String, Object> fileEntry = new HashMap<>();
                fileEntry.put("link", trackMetadata.get("link").toString());
                fileEntry.put("downloadLink", downloadLink);
                fileEntry.put("filename", songName.concat(".mp3"));
                fileEntry.put("directory", tempDir.toString());
                fileData.add(fileEntry);
            }
        }
        return fileData;
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
