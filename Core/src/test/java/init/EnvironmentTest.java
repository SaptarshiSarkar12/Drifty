package init;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import properties.OS;
import properties.Program;
import utils.MessageBroker;

import java.io.File;

@DisplayName("Environment Tests")
public class EnvironmentTest {
    @BeforeAll
    public static void setUp() {
        Environment.setMessageBroker(new MessageBroker());
        Environment.initializeEnvironment();
    }

    @Test
    @DisplayName("Test if Drifty directory Exists and is a directory")
    public void testEnvironmentInitialization() {
        File driftyDir = new File(Program.get(Program.DRIFTY_PATH));
        Assertions.assertTrue(driftyDir.exists(), "Drifty directory does not exist!");
        Assertions.assertTrue(driftyDir.isDirectory(), "Drifty path is not a directory!");
        Assertions.assertTrue(driftyDir.canWrite(), "Drifty directory is not writable!");
    }

    @Test
    @DisplayName("Test if executable names are set correctly")
    public void testExecutableNames() {
        String ytDlpExecName = Program.get(Program.YT_DLP_EXECUTABLE_NAME);
        String ffmpegExecName = Program.get(Program.FFMPEG_EXECUTABLE_NAME);
        Assertions.assertTrue(ytDlpExecName != null && !ytDlpExecName.isEmpty(), "yt-dlp executable name is not set!");
        Assertions.assertTrue(ffmpegExecName != null && !ffmpegExecName.isEmpty(), "ffmpeg executable name is not set!");

        switch (OS.getOSType()) {
            case WIN -> {
                Assertions.assertEquals("yt-dlp.exe", ytDlpExecName, "yt-dlp executable name is not correct for Windows!");
                Assertions.assertEquals("ffmpeg.exe", ffmpegExecName, "ffmpeg executable name is not correct for Windows!");
            }
            case MAC -> {
                Assertions.assertEquals("yt-dlp_macos", ytDlpExecName, "yt-dlp executable name is not correct for macOS!");
                String osArch = System.getProperty("os.arch");
                if (osArch.contains("arm") || osArch.contains("aarch64")) {
                    Assertions.assertEquals("ffmpeg_macos-arm64", ffmpegExecName, "ffmpeg executable name is not correct for macOS ARM architecture!");
                } else {
                    Assertions.assertEquals("ffmpeg_macos-x64", ffmpegExecName, "ffmpeg executable name is not correct for macOS x64 architecture!");
                }
            }
            case LINUX -> {
                Assertions.assertEquals("yt-dlp", ytDlpExecName, "yt-dlp executable name is not correct for Linux!");
                Assertions.assertEquals("ffmpeg", ffmpegExecName, "ffmpeg executable name is not correct for Linux!");
            }
            default -> Assertions.fail("Unknown OS type!");
        }
    }

    @Test
    @DisplayName("Test if yt-dlp executable exists")
    public void testYtDlpExecutableExists() {
        File ytDlpExecutable = new File(Program.get(Program.YT_DLP));
        Assertions.assertTrue(ytDlpExecutable.exists(), "yt-dlp executable does not exist!");
        Assertions.assertTrue(ytDlpExecutable.canExecute(), "yt-dlp executable is not executable!");
    }

    @Test
    @DisplayName("Test if ffmpeg executable exists")
    public void testFfmpegExecutableExists() {
        File ffmpegExecutable = new File(Program.get(Program.FFMPEG));
        Assertions.assertTrue(ffmpegExecutable.exists(), "ffmpeg executable does not exist!");
        Assertions.assertTrue(ffmpegExecutable.canExecute(), "ffmpeg executable is not executable!");
    }
}
