package CLI;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class copyYt_dlp {
    public static final String tempDir = System.getProperty("java.io.tmpdir");
    public void copyToTemp() throws IOException{
        File file = new File(tempDir + "yt-dlp.exe");
        if (file.exists()){
            Drifty_CLI.logger.log("INFO", "Skipping copying yt-dlp to " + tempDir + " folder as it is already present!");
            return;
        }
        InputStream is = copyYt_dlp.class.getResourceAsStream("yt-dlp.exe");
        // sets the output stream to a system folder
        OutputStream os = new FileOutputStream(System.getProperty("java.io.tmpdir") + "yt-dlp.exe");
        byte[] b = new byte[2048]; // length of the byte array doesn't matter in copying the file to the temp folder!
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }
}
