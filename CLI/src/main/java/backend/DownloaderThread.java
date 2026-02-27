package backend;

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

    public DownloaderThread(final URL url, FileOutputStream file, long start, long end) {
        this.url = url;
        this.file = file;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        ReadableByteChannel readableByteChannel;
        try {
            URLConnection con = url.openConnection();
            con.setRequestProperty("Range", "bytes=" + start + "-" + end); // stating how many bytes of data to be sent by the server.
            con.connect();
            readableByteChannel = Channels.newChannel(con.getInputStream());
            file.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
