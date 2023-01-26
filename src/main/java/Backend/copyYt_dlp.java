package Backend;

import Utils.CreateLogs;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static Utils.DriftyConstants.*;

public class copyYt_dlp {
    static CreateLogs logger = CreateLogs.getInstance();
    private static String tempDir = System.getProperty("java.io.tmpdir");
    public void copyToTemp() throws IOException{
        // TODO - Try using File.createTempFile() method to create temp version of yt-dlp (might work)
        File yt_dlp_file;
        if (System.getProperty(OS_NAME).contains(WINDOWS_OS_NAME)) {
            yt_dlp_file = File.createTempFile("yt-dlp", ".exe");
        } else {
            yt_dlp_file = File.createTempFile("yt-dlp", "");
        }
        if (yt_dlp_file.exists()){
            logger.log(LOGGER_INFO, "Skipping copying yt-dlp to " + getTempDir() + " folder as it is already present!");
            return;
        }
        InputStream is = copyYt_dlp.class.getResourceAsStream("yt-dlp");
        // sets the output stream to a system folder
        OutputStream os = new FileOutputStream(yt_dlp_file.getPath());
        byte[] b = new byte[1024]; // length of the byte array doesn't matter in copying the yt_dlp_file to the temp folder!
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }

    public static String getTempDir(){
        if (!tempDir.endsWith("/")){
            tempDir += "/";
        }
        return tempDir;
    }
}
