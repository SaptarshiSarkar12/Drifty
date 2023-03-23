package Backend;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloaderThread extends Thread {
    private final URL url;
    private final long start;
    private final long end;
    private final FileOutputStream file;

    /**
     * This is the constructor of the file downloading threads, which sets initiates the required variables from the parameters passed.
     * @param url Link to the file to be downloaded.
     * @param file The output stream of the file to be saved locally.
     * @param start This is the start of the range of bytes of the file, to be downloaded by each thread.
     * @param end This is the end of the range of bytes of the file, to be downloaded by each thread.
     */
    public DownloaderThread(URL url, FileOutputStream file, long start, long end) {
        this.url = url;
        this.file = file;
        this.start = start;
        this.end = end;
    }

    /**
     * This is the method which downloads each part of the file. It transfers the range of bytes of data of the original file to the local file.
     */
    @Override
    public void run() {
        ReadableByteChannel readableByteChannel;
        try {
            URLConnection con = url.openConnection();
            con.setRequestProperty("Range", "bytes=" + start + "-" + end); // stating how many bytes of data to be sent by the server.
            con.connect();
            readableByteChannel = Channels.newChannel(con.getInputStream());
            file.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
