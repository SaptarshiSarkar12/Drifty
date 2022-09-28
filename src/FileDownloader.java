package src;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

class FileDownloader implements Runnable {
    private static String link;
    private static String fileName;
    private static String dir;
    private static URL url;
    private static long downloadedBytes;

    public FileDownloader(String link, String fileName, String dir){
        FileDownloader.link = link;
        FileDownloader.fileName = fileName;
        FileDownloader.dir = dir;
        FileDownloader.downloadedBytes = 0;
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
            URLConnection openConnection = url.openConnection();
            openConnection.connect();
            long totalDownloadBytes = Long.valueOf(openConnection.getHeaderField("content-length"));
            System.out.println(totalDownloadBytes);
            dir = dir.replace('/', '\\');
            if (dir.length() != 0) {
                if (dir.equals(".\\\\") || dir.equals(".\\")) {
                    dir = "";
                }
            } else {
                System.out.println("Invalid Directory !");
            }
            try {
                new CheckDirectory(dir);
            } catch (IOException e){
                System.out.println("Failed to create the directory : " + dir + " !");
            }
            downloadFile();
        } catch (MalformedURLException e) {
            System.out.println("Invalid Link!");
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url + " !");
        }
    }

    private static void downloadFile(){
        long size;
        String sizeWithUnit = "";
        ReadableByteChannel readableByteChannel = null;
        try {
            System.out.print(url.getFile());
            InputStream urlStream = url.openStream();
            System.out.println();
            readableByteChannel = Channels.newChannel(urlStream);
        } catch (IOException e) {
            System.out.println("Failed to get a data stream !");
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
            System.out.println("Failed to ");
        }
        if (dir.length() == 0){
            dir = System.getProperty("user.dir");
        }
        if (!(dir.endsWith("\\"))) {
            dir = dir + System.getProperty("file.separator");
        }
        System.out.println("Successfully downloaded " + fileName + " of size " + sizeWithUnit + " at " + dir + fileName);
    }
    private static void setCurrentDownloadedBytes(long bytes) {
        downloadedBytes = bytes;
    }
    private static long getCurrentDownloadedBytes() {
        return downloadedBytes;
    }
    private static void progressBar() {
        //Runnable runnable = () -> {}
    }
}
