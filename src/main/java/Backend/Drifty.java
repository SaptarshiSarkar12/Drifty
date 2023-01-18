package Backend;

public class Drifty {
    private static String downloadsFolder;
    private static String link;
    private static String fileName;

    public Drifty(String url, String downloadsDirectory, String fileNameOfTheDownloadedFile) {
        link = url;
        downloadsFolder = downloadsDirectory;
        fileName = fileNameOfTheDownloadedFile;
    }

    public void start() {
        new FileDownloader(link, fileName, downloadsFolder).run();
    }
}
