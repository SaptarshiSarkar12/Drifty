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

    public static void main(String[] args) {
        FileDownloader o = new FileDownloader("https://github.com/mattermost/.github/blob/master/CODE_OF_CONDUCT.md", "he.md", ".//");
        o.run();
    }

    @Override
    public void run() {
        try {
            if (!(link.startsWith("http://") || link.startsWith("https://"))){
                link = "http://" + link;
            }
            url = new URL(link);
            urlConn = url.openConnection();
            if (dir.length() != 0) {
                if (dir.equals(".\\\\") || dir.equals(".//") || dir.equals(".\\") || dir.equals("./")) {
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(fileName));
                } else {
                    dir = DefaultDownloadFolderLocationFinder.findPath();
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(dir + System.getProperty("file.separator") + fileName));
                }
            } else {
                System.out.println("Invalid Directory !");
            }
            System.out.println(dir+fileName);
//            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
//            try (FileOutputStream fos = new FileOutputStream(dir + fileName)) {
//                fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//            }

        } catch (MalformedURLException e) {
            System.out.println("Invalid Link!");
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url + "");
        }
    }
}
