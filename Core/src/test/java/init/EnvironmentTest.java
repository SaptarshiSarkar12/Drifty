package init;

import org.junit.jupiter.api.*;
import preferences.AppSettings;
import properties.OS;
import properties.Program;
import utils.MessageBroker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static properties.Program.SPOTDL;
import static properties.Program.YT_DLP;

@DisplayName("Test Environment")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnvironmentTest {
    private static final MessageBroker msgBroker = new MessageBroker();

    @BeforeAll
    @DisplayName("Initialize Test Environment")
    static void initAll() {
        Environment.setMessageBroker(msgBroker);
        Environment.initializeEnvironment();
    }

    @Test
    @Order(1)
    @DisplayName("Test MessageBroker instance sent to Environment")
    void testMessageBrokerInstance() {
        Assertions.assertEquals(msgBroker, Environment.getMessageBroker());
    }

    @Test
    @Order(2)
    @DisplayName("Test OS Name")
    void testOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            Assertions.assertTrue(OS.isWindows());
        } else if (osName.contains("mac")) {
            Assertions.assertTrue(OS.isMac());
        } else {
            Assertions.assertFalse(OS.isWindows());
            Assertions.assertFalse(OS.isMac());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test Executable Names")
    void testExecutableNames() {
        switch (OS.getOSType()) {
            case WIN -> {
                Assertions.assertEquals("yt-dlp.exe", Program.get(Program.YT_DLP_EXECUTABLE_NAME));
                Assertions.assertEquals("spotdl_win.exe", Program.get(Program.SPOTDL_EXECUTABLE_NAME));
            }
            case MAC -> {
                Assertions.assertEquals("yt-dlp_macos", Program.get(Program.YT_DLP_EXECUTABLE_NAME));
                Assertions.assertEquals("spotdl_macos", Program.get(Program.SPOTDL_EXECUTABLE_NAME));
            }
            default -> {
                Assertions.assertEquals("yt-dlp", Program.get(Program.YT_DLP_EXECUTABLE_NAME));
                Assertions.assertEquals("spotdl_linux", Program.get(Program.SPOTDL_EXECUTABLE_NAME));
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test Drifty Folder path")
    void testDriftyFolderPath() {
        Path expectedDriftyFolderPath;
        if (Objects.requireNonNull(OS.getOSType()) == OS.WIN) {
            expectedDriftyFolderPath = Paths.get(System.getenv("LOCALAPPDATA"), "Drifty").toAbsolutePath();
            Assertions.assertEquals(expectedDriftyFolderPath.toString(), Program.get(Program.DRIFTY_PATH));
        } else {
            expectedDriftyFolderPath = Paths.get(System.getProperty("user.home"), ".drifty").toAbsolutePath();
            Assertions.assertEquals(expectedDriftyFolderPath.toString(), Program.get(Program.DRIFTY_PATH));
        }
        File driftyFolder = expectedDriftyFolderPath.toFile();
        Assertions.assertTrue(driftyFolder.exists());
        Assertions.assertTrue(driftyFolder.isDirectory());
    }

    @Test
    @Order(5)
    @DisplayName("Test Executables")
    void testExecutables() {
        File ytDlp = new File(Program.get(YT_DLP));
        Assertions.assertTrue(ytDlp.exists());
        Assertions.assertTrue(Files.isExecutable(ytDlp.toPath()));
        File spotDL = new File(Program.get(Program.SPOTDL));
        Assertions.assertTrue(spotDL.exists());
        Assertions.assertTrue(Files.isExecutable(spotDL.toPath()));
    }

    @Test
    @Order(6)
    @DisplayName("Test version of Executables in App Settings")
    void testExecutableVersions() {
        String[] executablePaths = {Program.get(YT_DLP), Program.get(SPOTDL)};
        String[] versions = {AppSettings.GET.ytDlpVersion(), AppSettings.GET.spotDLVersion()};
        String[] executableNames = {"yt-dlp", "spotDL"};
        for (int i = 0; i < executablePaths.length; i++) {
            ProcessBuilder getExecutableVersion = new ProcessBuilder(executablePaths[i], "--version");
            try {
                Process getExecutableVersionProcess = getExecutableVersion.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(getExecutableVersionProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.isEmpty()) {
                            Assertions.assertEquals(line, versions[i]);
                        }
                    }
                } catch (IOException e) {
                    Assertions.fail("Failed to match " + executableNames[i] + " version! " + e.getMessage());
                }
            } catch (IOException e) {
                Assertions.fail("Failed to start process to check " + executableNames[i] + " version! " + e.getMessage());
            }
        }
    }
}