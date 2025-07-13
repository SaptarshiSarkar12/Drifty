package backend;

import cli.init.Environment;
import cli.init.TestEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("File Downloader Tests")
public class FileDownloaderTest extends TestEnvironment {
    @Test
    @DisplayName("Test File Downloader Functionality")
    public void testFileDownloader() {
        assert utility != null : "Utility is not initialized!";
        assert Environment.getMessageBroker() != null : "Message Broker is not initialized!";
    }
}
