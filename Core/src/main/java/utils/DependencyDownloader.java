package utils;

import properties.Program;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DependencyDownloader {
    public static void downloadDependency(Dependency dependency) {
        String downloadUrl = dependency.getDownloadUrl(dependency);
        if (downloadUrl == null) {
            System.err.println("Unsupported OS or architecture for " + dependency.name());
            return;
        }
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        Path targetPath = Path.of(Program.get(Program.DRIFTY_PATH)).resolve(fileName);
        if (Files.exists(targetPath)) {
            System.out.println(dependency.name() + " already exists at " + targetPath);
            return;
        }
        try {
            URL url = URI.create(downloadUrl).toURL();
            Path tempPath = targetPath.resolveSibling(fileName + ".part");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(10_000);
            urlConnection.setReadTimeout(30_000);
            Files.createDirectories(targetPath.getParent());
            try (ReadableByteChannel readableByteChannel = Channels.newChannel(urlConnection.getInputStream());
                 FileOutputStream fos = new FileOutputStream(tempPath.toFile())) {
                fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            }
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Downloaded " + dependency.name() + " to " + targetPath);
            try {
                // Set executable permission for the downloaded file
                boolean isExecutable = targetPath.toFile().setExecutable(true, true);
                if (!isExecutable) {
                    System.err.println("Failed to set executable permission for " + targetPath);
                }
            } catch (SecurityException e) {
                System.err.println("Failed to set executable permission for " + targetPath + ": " + e.getMessage());
            }
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + downloadUrl);
        } catch (IOException e) {
            System.err.println("Failed to download " + dependency.name() + ": " + e.getMessage());
        } finally {
            // Clean up the temporary file if it exists
            Path tempPath = targetPath.resolveSibling(fileName + ".part");
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException e) {
                System.err.println("Failed to delete temporary file " + tempPath + ": " + e.getMessage());
            }
        }
    }
}
