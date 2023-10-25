package Backend;

import Enums.Program;
import Utils.Environment;
import Utils.MessageBroker;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class CopyExecutables {
    private static final MessageBroker M = Environment.getMessageBroker();

    public final void copyExecutables(final String[] executableNames) throws IOException {
        for (String executableName : executableNames) {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(executableName);
            Path executablePath = Program.getExecutablesPath(executableName);
            if (!Files.exists(executablePath)) {
                if (!executablePath.toFile().getParentFile().exists()) {
                    FileUtils.createParentDirectories(executablePath.toFile());
                }
                Files.copy(inputStream, executablePath);
                if (!Files.isExecutable(executablePath)) {
                    Files.setPosixFilePermissions(executablePath, Set.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
                }
            }
        }
    }
}