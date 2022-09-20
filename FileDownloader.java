import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

class FileDownloader implements Runnable {
    private String link;
    private final String fileName;
    private String dir;
    private URL url;
    private URLConnection urlConn;

    public FileDownloader(String link, String fileName, String dir){
        this.link = link;
        this.fileName = fileName;
        this.dir = dir;

    }

    @Override
    public void run() {
        link = link.replace('\\', '/');
        try {
            if (!(link.startsWith("http://") || link.startsWith("https://"))){
                link = "http://" + link;
            }
            if (link.startsWith("https://github.com/") || (link.startsWith("http://github.com/"))){
                if (!(link.endsWith("?raw=true"))){
                    link = link + "?raw=true";
                }
            }
            url = new URL(link);
            urlConn = url.openConnection();
            dir = dir.replace('/', '\\');
            if (dir.length() != 0) {
                if (dir.equals(".\\\\") || dir.equals(".\\")) {
                    dir = "";
                }
            } else {
                System.out.println(Drifty_CLI.COLOR_RED + "Invalid Directory !" + Drifty_CLI.COLOR_RESET);
            }
            System.out.println(Drifty_CLI.COLOR_CYAN + "Downloading " + fileName + " ...");
            long size = 0;
            String sizeWithUnit = "";
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            try (FileOutputStream fos = new FileOutputStream(dir + fileName)) {
                fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                size = fos.getChannel().size();
                if (size > 1000) {
                    size = size/1024;
                    sizeWithUnit = size + " kilobytes";
                    if (size > 1000) {
                        size = size/1024;
                        sizeWithUnit = size + " megabytes";
                        if (size > 1000){
                            size = size/1024;
                            sizeWithUnit = size + "gigabytes";
                        }
                    }
                } else {
                    sizeWithUnit = size + " bytes";
                }
            }
            if (dir.length() == 0){
                dir = System.getProperty("user.dir");
            }
            if (!(dir.endsWith("\\"))) {
                dir = dir + System.getProperty("file.separator");
            }
            System.out.println("Downloaded " + fileName + " of size " + Drifty_CLI.COLOR_BRIGHT_BLUE_BOLD + sizeWithUnit + Drifty_CLI.COLOR_CYAN + " at " + dir + fileName + Drifty_CLI.COLOR_RESET);
        } catch (MalformedURLException e) {
            System.out.println(Drifty_CLI.COLOR_RED + "Invalid Link!" + Drifty_CLI.COLOR_RESET);
        } catch (IOException e) {
            System.out.println(Drifty_CLI.COLOR_RED + "Failed to connect to " + url + " !" + Drifty_CLI.COLOR_RESET);
        }
    }
}
