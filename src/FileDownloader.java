package src;

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
                System.out.println(Drifty_CLI.COLOR_RED + "Invalid Directory !" + Drifty_CLI.COLOR_RESET);
            }
            try {
                new CheckDirectory(dir);
            } catch (IOException e){
                System.out.println(Drifty_CLI.COLOR_RED + "Failed to create the directory : " + Drifty_CLI.COLOR_BLUE_UNDERLINED + dir + Drifty_CLI.COLOR_RESET + Drifty_CLI.COLOR_RED + " !" + Drifty_CLI.COLOR_RESET);
            }
            downloadFile();
        } catch (MalformedURLException e) {
            System.out.println(Drifty_CLI.COLOR_RED + "Invalid Link!" + Drifty_CLI.COLOR_RESET);
        } catch (IOException e) {
            System.out.println(Drifty_CLI.COLOR_RED + "Failed to connect to " + url + " !" + Drifty_CLI.COLOR_RESET);
        }
    }

    private static void downloadFile(){
        long size;
        String sizeWithUnit = "";
        ReadableByteChannel readableByteChannel = null;
        try {
            readableByteChannel = Channels.newChannel(url.openStream());
        } catch (IOException e) {
            System.out.println(Drifty_CLI.COLOR_RED + "Failed to get a data stream !" + Drifty_CLI.COLOR_RESET);
        }
        try (FileOutputStream fos = new FileOutputStream(dir + fileName)) {
            System.out.println(Drifty_CLI.COLOR_CYAN + "Downloading " + fileName + " ...");
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
        System.out.println("Successfully downloaded " + fileName + " of size " + Drifty_CLI.COLOR_BRIGHT_BLUE_BOLD + sizeWithUnit + Drifty_CLI.COLOR_CYAN + " at " + Drifty_CLI.COLOR_BLUE_UNDERLINED + dir + fileName + Drifty_CLI.COLOR_RESET);
    }
}
