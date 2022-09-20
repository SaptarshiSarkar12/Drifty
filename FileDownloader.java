import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

class FileDownloader implements Runnable {
    String link;
    String fileName;
    String dir;
    URL url;
    URLConnection urlConn;

    public FileDownloader(String link, String fileName, String dir){
        this.link = link;
        this.fileName = fileName;
        this.dir = dir;
    }

    public static void main(String[] args) {
        FileDownloader o = new FileDownloader("https://www.youtube.com/watch?v=BQLOfws52Rs", "he", "://");
        System.out.println("\\");

        try {
            o.url = new URL(o.link);
            o.urlConn = o.url.openConnection();
            if (o.dir.length() != 0) {
                if (o.dir.equals(".\\") || o.dir.equals(".//")) {}
            }
//            File direc = new File();
//            boolean v = direc.setWritable(true, false);
//            System.out.println(v);
            OutputStream os = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.home") + System.getProperty("file.separator") + "Downloads"));
        } catch (MalformedURLException e) {
            System.out.println("Invalid Link!");
        } catch (IOException e) {
            System.out.println("Failed to connect to " + o.url);
        }
    }

    @Override
    public void run() {
        try {
            url = new URL(link);
            urlConn = url.openConnection();
        } catch (MalformedURLException e) {
            System.out.println("Invalid Link!");
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url);
        } finally {
            System.out.println(url);
        }
    }
}
