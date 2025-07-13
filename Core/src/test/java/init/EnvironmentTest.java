package init;

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
        assert driftyDir.exists() : "Drifty directory does not exist!";
        assert driftyDir.isDirectory() : "Drifty path is not a directory!";
        assert driftyDir.canWrite() : "Drifty directory is not writable!";
    }

    @Test
    @DisplayName("Test if executable names are set correctly")
    public void testExecutableNames() {
        String ytDlpExecName = Program.get(Program.YT_DLP_EXECUTABLE_NAME);
        String ffmpegExecName = Program.get(Program.FFMPEG_EXECUTABLE_NAME);
        assert ytDlpExecName != null && !ytDlpExecName.isEmpty() : "yt-dlp executable name is not set!";
        assert ffmpegExecName != null && !ffmpegExecName.isEmpty() : "ffmpeg executable name is not set!";

        switch (OS.getOSType()) {
            case WIN -> {
                assert "yt-dlp.exe".equals(ytDlpExecName) : "yt-dlp executable name is not correct for Windows!";
                assert "ffmpeg.exe".equals(ffmpegExecName) : "ffmpeg executable name is not correct for Windows!";
            }
            case MAC -> {
                assert "yt-dlp_macos".equals(ytDlpExecName) : "yt-dlp executable name is not correct for macOS!";
                String osArch = System.getProperty("os.arch");
                if (osArch.contains("arm") || osArch.contains("aarch64")) {
                    assert "ffmpeg_macos-arm64".equals(ffmpegExecName) : "ffmpeg executable name is not correct for macOS ARM architecture!";
                } else {
                    assert "ffmpeg_macos-x64".equals(ffmpegExecName) : "ffmpeg executable name is not correct for macOS x64 architecture!";
                }
            }
            case LINUX -> {
                assert "yt-dlp".equals(ytDlpExecName) : "yt-dlp executable name is not correct for Linux!";
                assert "ffmpeg".equals(ffmpegExecName) : "ffmpeg executable name is not correct for Linux!";
            }
            default -> {
                assert false : "OS type is not supported!";
            }
        }
    }

    @Test
    @DisplayName("Test if yt-dlp executable exists")
    public void testYtDlpExecutableExists() {
        File ytDlpExecutable = new File(Program.get(Program.YT_DLP));
        assert ytDlpExecutable.exists() : "yt-dlp executable does not exist!";
        assert ytDlpExecutable.canExecute() : "yt-dlp executable is not executable!";
    }

    @Test
    @DisplayName("Test if ffmpeg executable exists")
    public void testFfmpegExecutableExists() {
        File ffmpegExecutable = new File(Program.get(Program.FFMPEG));
        assert ffmpegExecutable.exists() : "ffmpeg executable does not exist!";
        assert ffmpegExecutable.canExecute() : "ffmpeg executable is not executable!";
    }
}
