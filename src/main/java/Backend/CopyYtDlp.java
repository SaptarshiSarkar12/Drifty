package Backend;
import Enums.Program;
import Enums.OS;
import Utils.MessageBroker;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
public class CopyYtDlp {
    static MessageBroker messageBroker = Drifty.getMessageBrokerInstance();

    public boolean copyToTemp() throws IOException {
        Path filePath = Paths.get(Program.get(Program.COMMAND));
        if (!filePath.toFile().exists()) {
            try (InputStream inputStream = CopyYtDlp.class.getClassLoader().getResourceAsStream(Program.get(Program.NAME));
                 OutputStream outputStream = Files.newOutputStream(filePath)) {
                System.out.println("Copying file to: " + filePath);
                if (inputStream != null) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                else {
                    System.out.println("Resource not found: " + Program.get(Program.NAME));
                }
                if (!OS.isWindows()) {
                    if (Files.exists(filePath)) {
                        Set<PosixFilePermission> permissions = new HashSet<>();
                        permissions.add(PosixFilePermission.OWNER_READ);
                        permissions.add(PosixFilePermission.OWNER_WRITE);
                        permissions.add(PosixFilePermission.OWNER_EXECUTE);
                        permissions.add(PosixFilePermission.GROUP_READ);
                        permissions.add(PosixFilePermission.GROUP_WRITE);
                        permissions.add(PosixFilePermission.GROUP_EXECUTE);
                        permissions.add(PosixFilePermission.OTHERS_READ);
                        permissions.add(PosixFilePermission.OTHERS_WRITE);
                        permissions.add(PosixFilePermission.OTHERS_EXECUTE);
                        Files.setPosixFilePermissions(filePath,permissions);
                    }
                }
            }
        }
        return !filePath.toFile().exists();
    }

}
