import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

class FileDownloader implements Runnable {
    private static String link;
    private static String fileName;
    private static String dir;
    private static URL url;

    public static final String ANSI_YELLOW = "\u001B[33m";

    public FileDownloader(String link, String fileName, String dir){
        FileDownloader.link = link;
        FileDownloader.fileName = fileName;
        FileDownloader.dir = dir;
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
            url.openConnection();
            dir = dir.replace('/', '\\');
            if (dir.length() != 0) {
                if (dir.equals(".\\\\") || dir.equals(".\\")) {
                    dir = "";
                }
            } else {
                System.out.println("Invalid Directory Entered !");
                Drifty_CLI.cl.log("ERROR", "Invalid Directory Entered !");
            }
            try {
                new CheckDirectory(dir);
            } catch (IOException e){
                System.out.println("Failed to create the directory : " + dir + " !");
                Drifty_CLI.cl.log("ERROR", "Failed to create the directory : " + dir + " !");
            }
            downloadFile();
        } catch (MalformedURLException e) {
            System.out.println("Invalid Link!");
            Drifty_CLI.cl.log("ERROR", "Invalid Link!");
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url + " !");
            Drifty_CLI.cl.log("ERROR", "Failed to connect to " + url + " !");
        }
    }

    private static void downloadFile(){
        long size;
        String sizeWithUnit = "";
        ReadableByteChannel readableByteChannel = null;
        try {
            readableByteChannel = Channels.newChannel(url.openStream());
        } catch (IOException e) {
            System.out.println("Failed to get a data stream !");
            Drifty_CLI.cl.log("ERROR", "Failed to get a data stream !" + e.getMessage());
        }
        try (FileOutputStream fos = new FileOutputStream(dir + fileName)) {
            System.out.println("Downloading " + fileName + " ...");
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
        } catch (IOException e) {
            Drifty_CLI.cl.log("ERROR", "Failed to download the contents !");
        }
        if (dir.length() == 0){
            dir = System.getProperty("user.dir");
        }
        if (!(dir.endsWith("\\"))) {
            dir = dir + System.getProperty("file.separator");
        }
        System.out.println(ANSI_YELLOW+ "Successfully downloaded " + fileName );
        Drifty_CLI.cl.log("INFO", "Downloaded " + fileName + " of size " + sizeWithUnit + " at " + dir + fileName);
    }
}
