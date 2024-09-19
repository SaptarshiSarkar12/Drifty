package support;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import init.Environment;
import preferences.AppSettings;
import properties.LinkType;
import properties.Mode;
import utils.MessageBroker;
import utils.Utility;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static utils.Utility.cleanFilename;
import static utils.Utility.randomString;

public class DownloadConfiguration {
    private final String directory;
    private final ArrayList<HashMap<String, Object>> fileData;
    private final LinkType linkType;
    private final String filename;
    private String link;
    private int fileCount;
    private int filesProcessed;
    private int statusCode;
    protected MessageBroker msgBroker = Environment.getMessageBroker();

    public DownloadConfiguration(String link, String directory, String filename) {
        this.link = link;
        this.directory = directory;
        this.filename = filename;
        this.linkType = LinkType.getLinkType(link);
        this.fileData = new ArrayList<>();
    }

    public void sanitizeLink() {
        link = link.trim();
        link = link.replace('\\', '/');
        if (!(link.startsWith("http://") || link.startsWith("https://"))) {
            link = "https://" + link;
        }
        if (link.startsWith("https://github.com/") || (link.startsWith("http://github.com/"))) {
            if (!link.endsWith("?raw=true")) {
                link = link + "?raw=true";
            }
        }
        if (this.linkType.equals(LinkType.INSTAGRAM)) {
            this.link = Utility.formatInstagramLink(link);
        }
    }

    public int fetchFileData() {
        if (this.linkType.equals(LinkType.YOUTUBE)) {
            String jsonString = Utility.getYtDlpMetadata(link);
            if (jsonString == null || jsonString.isEmpty()) {
                msgBroker.msgLogError("Failed to process Youtube Link");
                statusCode = -1;
                return -1;
            }
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            if (link.contains("playlist")) {
                JsonArray entries = jsonObject.get("entries").getAsJsonArray();
                fileCount = entries.size();
                for (JsonElement entry : entries) {
                    if (Mode.isCLI()) {
                        System.out.print("\r[" + (filesProcessed + 1) + "/" + fileCount + "] Processing Youtube Playlist...");
                    } else {
                        msgBroker.msgLinkInfo("[" + (filesProcessed + 1) + "/" + fileCount + "] Processing Youtube Playlist...");
                    }
                    JsonObject entryObject = entry.getAsJsonObject();
                    String videoLink = entryObject.get("url").getAsString();
                    String videoTitle = cleanFilename(entryObject.get("title").getAsString());
                    if (videoTitle.isEmpty()) {
                        videoTitle = "Unknown_YouTube_Video_".concat(randomString(5));
                    }
                    String filename = videoTitle.concat(".mp4");
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("link", videoLink);
                    data.put("filename", filename);
                    data.put("directory", this.directory);
                    fileData.add(data);
                    filesProcessed++;
                }
                if (Mode.isCLI()) {
                    System.out.println("\rYoutube Playlist processed successfully");
                } else {
                    msgBroker.msgLinkInfo("Youtube Playlist processed successfully");
                }
            } else {
                msgBroker.msgLinkInfo("Processing Youtube Video...");
                fileCount = 1;
                HashMap<String, Object> data = new HashMap<>();
                String videoTitle = cleanFilename(jsonObject.get("title").getAsString());
                if (videoTitle.isEmpty()) {
                    videoTitle = "Unknown_YouTube_Video_".concat(randomString(5));
                }
                String filename = videoTitle.concat(".mp4");
                data.put("link", link);
                data.put("filename", this.filename == null ? filename : this.filename);
                data.put("directory", this.directory);
                fileData.add(data);
                filesProcessed++;
                msgBroker.msgLinkInfo("Youtube Video processed successfully");
            }
        } else if (this.linkType.equals(LinkType.SPOTIFY)) {
            if (link.contains("playlist")) {
                ArrayList<HashMap<String, Object>> playlistMetadata = Utility.getSpotifyPlaylistMetadata(link);
                if (playlistMetadata != null && !playlistMetadata.isEmpty()) {
                    fileCount = playlistMetadata.size();
                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                    if (Mode.isGUI()) {
                        executor.scheduleAtFixedRate(this::updateJobList, 1500, 600, TimeUnit.MILLISECONDS);
                    }
                    for (HashMap<String, Object> songMetadata : playlistMetadata) {
                        if (Mode.isCLI()) {
                            System.out.print("\r[" + filesProcessed + "/" + fileCount + "] Processing Spotify Playlist...");
                        } else {
                            msgBroker.msgLinkInfo("[" + filesProcessed + "/" + fileCount + "] Processing Spotify Playlist...");
                        }
                        HashMap<String, Object> data = Utility.getSpotifySongDownloadData(songMetadata, this.directory);
                        if (data == null) {
                            msgBroker.msgLogError("Failed to process Spotify Playlist");
                            filesProcessed++;
                            statusCode = -1;
                            return -1;
                        }
                        fileData.add(data);
                        filesProcessed++;
                    }
                    msgBroker.msgLinkInfo("Spotify Playlist processed successfully");
                    if (Mode.isGUI()) {
                        executor.shutdown();
                    }
                }
            } else {
                HashMap<String, Object> songMetadata = Utility.getSpotifySongMetadata(link);
                fileCount = 1;
                if (songMetadata != null && !songMetadata.isEmpty()) {
                    msgBroker.msgLinkInfo("Processing Spotify Song...");
                    HashMap<String, Object> data = Utility.getSpotifySongDownloadData(songMetadata, this.directory);
                    if (data == null) {
                        msgBroker.msgLogError("Failed to process Spotify Song");
                        filesProcessed++;
                        statusCode = -1;
                        return -1;
                    }
                    if (this.filename != null) {
                        data.put("filename", this.filename);
                    }
                    fileData.add(data);
                    filesProcessed++;
                    msgBroker.msgLinkInfo("Spotify Song processed successfully");
                }
            }
        } else if (this.linkType.equals(LinkType.INSTAGRAM)) {
            String jsonString = Utility.getYtDlpMetadata(link);
            fileCount = 1;
            if (jsonString != null && !jsonString.isEmpty()) {
                msgBroker.msgLinkInfo("Processing Instagram Post...");
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                HashMap<String, Object> data = new HashMap<>();
                String instagramVideoName = cleanFilename(jsonObject.get("title").getAsString());
                if (instagramVideoName.isEmpty()) {
                    instagramVideoName = "Unknown_Instagram_Video_".concat(randomString(5));
                }
                String filename = instagramVideoName.concat(".").concat(jsonObject.get("ext").getAsString());
                data.put("link", link);
                data.put("filename", this.filename == null ? filename : this.filename);
                data.put("directory", this.directory);
                fileData.add(data);
                filesProcessed++;
                msgBroker.msgLinkInfo("Instagram Post processed successfully");
            }
        } else {
            msgBroker.msgLinkInfo("Processing File Link...");
            fileCount = 1;
            HashMap<String, Object> data = new HashMap<>();
            String filename = cleanFilename(Utility.extractFilenameFromURL(link));
            if (filename.isEmpty()) {
                filename = "Unknown_File_".concat(randomString(5));
            }
            data.put("link", link);
            data.put("filename", this.filename == null ? filename : this.filename);
            data.put("directory", this.directory);
            fileData.add(data);
            filesProcessed++;
            msgBroker.msgLinkInfo("File Link processed successfully");
        }
        if (fileData.isEmpty()) {
            statusCode = -1;
            return -1;
        } else {
            statusCode = 0;
            return 0;
        }
    }

    public void updateJobList() {
        Set<Job> distinctJobList = new TreeSet<>(Comparator.comparing(Job::hashCode)); // TreeSet to remove duplicates based on Job.hashCode()
        distinctJobList.addAll(AppSettings.GET.jobs().jobList());
        if (fileData.isEmpty()) {
            return;
        }
        for (HashMap<String, Object> data : fileData) {
            String link = data.get("link").toString();
            String filename = data.get("filename").toString();
            String directory = data.get("directory").toString();
            Job job;
            if (linkType.equals(LinkType.SPOTIFY)) {
                String downloadLink = data.get("downloadLink").toString();
                job = new Job(link, directory, filename, downloadLink);
            } else {
                job = new Job(link, directory, filename, null);
            }
            distinctJobList.add(job);
        }
        AppSettings.GET.jobs().setList(new ConcurrentLinkedDeque<>(distinctJobList));
    }

    public String getLink() {
        return link;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getFilesProcessed() {
        return filesProcessed;
    }

    public ArrayList<HashMap<String, Object>> getFileData() {
        return fileData;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
