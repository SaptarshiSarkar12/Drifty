package backend;

import cli.init.TestEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

@DisplayName("File Downloader Tests")
public class FileDownloaderTest extends TestEnvironment {
    @TempDir
    Path tempDirectory;

//    @ParameterizedTest
//    @DisplayName("Test File Downloader Functionality")
//    @MethodSource("fileDetailsProvider")
//    @Execution(ExecutionMode.CONCURRENT)
//    public void testFileDownloader(String link, String fileName, String expectedFileName) {
//
//    }
//
//    private static Stream<Arguments> fileDetailsProvider() {
//
//    }
}
