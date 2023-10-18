import Enums.OS;
import Enums.Program;
import Utils.Environment;
import Utils.MessageBroker;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

@DisplayName("Initializing of test environment")
public class DriftyEnvironmentTest {
    private static MessageBroker messageBroker;

    @BeforeAll
    @DisplayName("Initialize environment for Drifty")
    static void initAll() {
        messageBroker = new MessageBroker(System.out);
        Environment.setMessageBroker(messageBroker);
        messageBroker.msgInitInfo("Initializing environment...");
        Environment.initializeEnvironment();
        messageBroker.msgInitInfo("Environment initialized successfully!");
        messageBroker.msgInitInfo("Initializing Test environment...");
        try {
            FileUtils.forceMkdir(Paths.get("./target/test/data/github/").toFile());
            FileUtils.forceMkdir(Paths.get("./target/test/data/youtube/").toFile());
            FileUtils.forceMkdir(Paths.get("./target/test/data/instagram/").toFile());
        } catch (FileAlreadyExistsException e) {
            messageBroker.msgInitInfo("Test directories already exist");
        } catch (IOException e) {
            messageBroker.msgInitInfo("Failed to create test directories");
        }
        messageBroker.msgInitInfo("Test environment initialized successfully!");
    }

    @Test
    @DisplayName("Check environment initialization of Drifty")
    void checkInitialization() {
        // Check if MessageBroker is not null
        Assertions.assertNotNull(messageBroker);
        Assertions.assertNotNull(Environment.getMessageBroker());
        // Check if config directory for Drifty exists
        File driftyConfigDir;
        if (OS.isWindows()) {
            driftyConfigDir = new File(Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath().toString());
        } else {
            driftyConfigDir = new File(Paths.get(System.getProperty("user.home"), ".config", "Drifty").toAbsolutePath().toString());
        }
        Assertions.assertTrue(driftyConfigDir.exists());
        Assertions.assertTrue(driftyConfigDir.isDirectory());
        // Check if yt-dlp exists
        File ytDLP = new File(Program.get(Program.YT_DLP));
        Assertions.assertTrue(ytDLP.exists());
        Assertions.assertTrue(ytDLP.isFile());
        // Check if yt-dlp is executable
        Assertions.assertTrue(ytDLP.canExecute());
        // Check if yt-dlp is updated
        Assertions.assertTrue(Environment.isYtDLPUpdated());
    }

    @AfterAll
    @DisplayName("Clean up test environment")
    static void cleanUp() {
        try {
            FileUtils.deleteDirectory(new File("./target/test/"));
        } catch (IOException e) {
            messageBroker.msgLogError("Failed to clean up test environment");
        }
    }
}
