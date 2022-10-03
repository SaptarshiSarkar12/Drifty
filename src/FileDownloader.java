import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

class FileDownloader implements Runnable {
    private static String dir;
    private static String fileName;
    private static String link;
    private static long totalSize;
    private static URL url;

    public FileDownloader(String link, String fileName, String dir){
        FileDownloader.link = link;
        FileDownloader.fileName = fileName;
        FileDownloader.dir = dir;
    }

    public static String getDir(){
        return dir;
    }

    @Override
    public void run() {
        link = link.replace('\\', '/');
        if (!(link.startsWith("http://") || link.startsWith("https://"))){
            link = "http://" + link;
        }
        if (link.startsWith("https://github.com/") || (link.startsWith("http://github.com/"))){
            if (!(link.endsWith("?raw=true"))){
                link = link + "?raw=true";
            }
        }
        try {
            url = new URL(link);
            URLConnection openConnection = url.openConnection();
            openConnection.connect();
            totalSize = openConnection.getContentLength();
            if (fileName.length() == 0) {
                 String[] webPaths = url.getFile().trim().split("/");
                 fileName = webPaths[webPaths.length-1];
            }
            dir = dir.replace('/', '\\');
            if (dir.length() != 0) {
                if (dir.equals(".\\\\") || dir.equals(".\\")) {
                    dir = "";
                }
            } else {
                System.out.println("Invalid Directory Entered !");
                Drifty_CLI.logger.log("ERROR", "Invalid Directory Entered !");
            }
            try {
                new CheckDirectory(dir);
            } catch (IOException e){
                System.out.println("Failed to create the directory : " + dir + " ! " + e.getMessage());
                Drifty_CLI.logger.log("ERROR", "Failed to create the directory : " + dir + " ! " + e.getMessage());
            }
            downloadFile();
        } catch (MalformedURLException e) {
            System.out.println("Invalid Link!");
            Drifty_CLI.logger.log("ERROR", "Invalid Link! " + e.getMessage());
        } catch (SocketTimeoutException e){
            System.out.println("Timed out while connecting to " + url + " !");
            Drifty_CLI.logger.log("ERROR", "Timed out while connecting to " + url + " ! " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url + " !");
            Drifty_CLI.logger.log("ERROR", "Failed to connect to " + url + " ! " + e.getMessage());
        }
    }
    private static void downloadFile(){
        ReadableByteChannel readableByteChannel;
        try {
            InputStream urlStream = url.openStream();
            System.out.println();
            readableByteChannel = Channels.newChannel(urlStream);
            try {
                FileOutputStream fos = new FileOutputStream(dir + fileName);
                ProgressBarThread progressBarThread = new ProgressBarThread(fos, totalSize, fileName);
                progressBarThread.start();
                Drifty_CLI.logger.log("INFO", "Downloading " + fileName + " ...");
                fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                progressBarThread.setDownloading(false);
                // keep main thread from closing the IO for short amt. of time so UI thread can finish and output
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }

            } catch (SecurityException e) {
                System.out.println("Write access to " + dir + fileName + " denied !");
                Drifty_CLI.logger.log("ERROR", "Write access to " + dir + fileName + " denied ! " + e.getMessage());
            }catch (IOException e) {
                System.out.println("Failed to download the contents ! ");
                Drifty_CLI.logger.log("ERROR", "Failed to download the contents ! " + e.getMessage());
            }
        } catch (NullPointerException e){
            System.out.println("Failed to get I/O operations channel to read from the data stream !");
            Drifty_CLI.logger.log("ERROR", "Failed to get I/O operations channel to read from the data stream !" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to get a data stream !");
            Drifty_CLI.logger.log("ERROR", "Failed to get a data stream ! " + e.getMessage());
        }
        if (dir.length() == 0){
            dir = System.getProperty("user.dir");
        }
        if (!(dir.endsWith("\\"))) {
            dir = dir + System.getProperty("file.separator");
        }
    }
}
