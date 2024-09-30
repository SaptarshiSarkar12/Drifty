package utils;

import com.google.gson.*;
import init.Environment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.hildan.fxgson.FxGson;
import preferences.AppSettings;
import properties.*;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static properties.Program.YT_DLP;
import static support.Constants.*;
import static utils.Utility.extractQueryParams;

public class Utility {
    private static final Random RANDOM_GENERATOR = new Random(System.currentTimeMillis());
    protected static MessageBroker msgBroker;
    private static boolean interrupted;

    public static void initializeUtility() {
        // Lazy initialization of the MessageBroker as it might be null when the Environment MessageBroker is not set
        msgBroker = Environment.getMessageBroker();
    }

    public static boolean isYoutube(String url) {
        String pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+";
        return url.matches(pattern);
    }

    public static boolean isInstagram(String url) {
        String pattern = "(https?://(?:www\\.)?instagr(am|.am)?(\\.com)?(/|.*)/(p|reel)/([^/?#&]+)).*";
        return url.matches(pattern);
    }

    public static boolean isSpotify(String url) {
        String pattern = "(https?://(open.spotify\\.com|play\\.spotify\\.com)/(track|album|playlist)/[a-zA-Z0-9]+).*";
        return url.matches(pattern);
    }

    public static boolean isExtractableLink(String link) {
        return isYoutube(link) || isInstagram(link) || isSpotify(link);
    }

    public static boolean isOffline() {
        try {
            URL projectWebsite = URI.create(DRIFTY_WEBSITE_URL).toURL();
            HttpURLConnection connectProjectWebsite = (HttpURLConnection) projectWebsite.openConnection();
            connectProjectWebsite.connect();
            return false;
        } catch (UnknownHostException e) {
            msgBroker.msgLogError("You are not connected to the Internet!");
        } catch (MalformedURLException e) {
            msgBroker.msgLogError("The link is not correctly formatted! " + e.getMessage());
        } catch (IOException e) {
            msgBroker.msgLogError("Failed to connect to the project website! " + e.getMessage());
        }
        return true;
    }

    public static boolean isLinkValid(String link) {
        try {
            URL url = URI.create(link).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Faster validation and hence improves performance
            connection.connect();
            msgBroker.msgLinkInfo("Link is valid!");
            return true;
        } catch (ConnectException e) {
            msgBroker.msgLinkError("Connection to the link timed out! Please check your internet connection. " + e.getMessage());
        } catch (UnknownHostException unknownHost) {
            if (isOffline()) {
                msgBroker.msgLinkError("You are not connected to the Internet!");
            } else {
                msgBroker.msgLinkError(INVALID_LINK);
            }
        } catch (ProtocolException e) {
            msgBroker.msgLinkError("An error occurred with the protocol! " + e.getMessage());
        } catch (MalformedURLException e) {
            msgBroker.msgLinkError("The link is not correctly formatted! " + e.getMessage());
        } catch (IOException e) {
            msgBroker.msgLinkError("Failed to connect to " + link + " ! " + e.getMessage());
        } catch (IllegalArgumentException e) {
            msgBroker.msgLinkError(link + " is not a URL; Error: " + e.getMessage());
        }
        return false;
    }

    public static URL getUpdateURL() throws MalformedURLException, URISyntaxException {
        URL updateURL;
        String[] executableNames;
        String arch = System.getProperty("os.arch");
        if ("amd64".equals(arch) || "x86_64".equals(arch)) {
            arch = "x86_64";
        } else if ("aarch64".equals(arch)) {
            arch = "aarch64";
        }
        if (Mode.isGUI()) {
            executableNames = new String[]{"Drifty-GUI_" + arch + ".pkg", "Drifty-GUI.exe", "Drifty-GUI_linux"};
        } else {
            executableNames = new String[]{"Drifty-CLI_macos_" + arch, "Drifty-CLI.exe", "Drifty-CLI_linux"};
        }
        String updateURLMiddle;
        if (AppSettings.GET.earlyAccess()) {
            updateURLMiddle = "download/" + AppSettings.GET.latestDriftyVersionTag() + "/";
        } else {
            updateURLMiddle = "latest/download/";
        }
        if (OS.isMac()) {
            updateURL = new URI("https://github.com/SaptarshiSarkar12/Drifty/releases/" + updateURLMiddle + executableNames[0]).toURL();
        } else if (OS.isWindows()) {
            updateURL = new URI("https://github.com/SaptarshiSarkar12/Drifty/releases/" + updateURLMiddle + executableNames[1]).toURL();
        } else {
            updateURL = new URI("https://github.com/SaptarshiSarkar12/Drifty/releases/" + updateURLMiddle + executableNames[2]).toURL();
        }
        return updateURL;
    }

    public static LinkedList<String> getYtDlpMetadata(String link) {
        try {
            LinkedList<String> list = new LinkedList<>();
            File driftyJsonFolder = Program.getJsonDataPath().toFile();
            if (driftyJsonFolder.exists() && driftyJsonFolder.isDirectory()) {
                FileUtils.forceDelete(driftyJsonFolder); // Deletes the previously generated temporary directory for Drifty
            }
            if (!driftyJsonFolder.mkdir()) {
                msgBroker.msgLinkError("Failed to create temporary directory for Drifty to get link metadata!");
                return null;
            }
            Thread linkThread = new Thread(ytDLPJsonData(driftyJsonFolder.getAbsolutePath(), link));
            try {
                linkThread.start();
            } catch (Exception e) {
                msgBroker.msgLinkError("Failed to start thread to get link metadata! " + e.getMessage());
                return null;
            }
            while (!linkThread.getState().equals(Thread.State.TERMINATED) && !linkThread.isInterrupted()) {
                sleep(100);
                interrupted = linkThread.isInterrupted();
            }
            if (interrupted) {
                FileUtils.forceDelete(driftyJsonFolder);
                return null;
            }
            File[] files = driftyJsonFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if ("yt-metadata.info.json".equals(file.getName())) {
                        String linkMetadata = FileUtils.readFileToString(file, Charset.defaultCharset());
                        list.addLast(linkMetadata);
                    }
                }
                FileUtils.forceDelete(driftyJsonFolder); // delete the metadata files of Drifty from the config directory
            }
            return list;
        } catch (IOException e) {
            msgBroker.msgLinkError("Failed to perform I/O operations on link metadata! " + e.getMessage());
            return null;
        }
    }

    public static String getHomeDownloadFolder() {
        String downloadsFolder;
        msgBroker.msgDirInfo(TRYING_TO_AUTO_DETECT_DOWNLOADS_FOLDER);
        if (!OS.isWindows()) {
            String home = System.getProperty("user.home");
            downloadsFolder = home + FileSystems.getDefault().getSeparator() + "Downloads" + FileSystems.getDefault().getSeparator();
        } else {
            downloadsFolder = DownloadFolderLocator.findPath() + FileSystems.getDefault().getSeparator();
        }
        if (downloadsFolder.equals(FileSystems.getDefault().getSeparator())) {
            downloadsFolder = System.getProperty("user.home");
            msgBroker.msgDirError(FAILED_TO_RETRIEVE_DEFAULT_DOWNLOAD_FOLDER);
        } else {
            msgBroker.msgDirInfo(FOLDER_DETECTED + downloadsFolder);
        }
        return downloadsFolder;
    }

    public static String makePretty(String json) {
        // The regex strings won't match unless the json string is converted to pretty format
        GsonBuilder g = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(g).setPrettyPrinting().create();
        JsonElement element = JsonParser.parseString(json);
        return gson.toJson(element);
    }

    public static String renameFile(String filename, String dir) {
        Path path = Paths.get(dir, filename);
        String newFilename = filename;
        int fileNum = -1;
        String baseName = FilenameUtils.getBaseName(filename.replaceAll(" \\(\\d+\\)\\.", "."));
        String ext = FilenameUtils.getExtension(filename);
        if (!ext.isEmpty()) {
            ext = "." + ext;
        }
        while (path.toFile().exists()) {
            fileNum += 1;
            newFilename = baseName + " (" + fileNum + ")" + ext;
            path = Paths.get(dir, newFilename);
        }
        return newFilename;
    }

    public static String getFilenameFromJson(String jsonString) {
        String json = makePretty(jsonString);
        String filename;
        String regexFilename = "(\"title\": \")(.+)(\",)";
        Pattern p = Pattern.compile(regexFilename);
        Matcher m = p.matcher(json);
        if (m.find()) {
            filename = cleanFilename(m.group(2)) + ".mp4";
            msgBroker.msgFilenameInfo(FILENAME_DETECTED + "\"" + filename + "\"");
        } else {
            filename = cleanFilename("Unknown_Filename_") + randomString(15) + ".mp4";
            msgBroker.msgFilenameError(FILENAME_DETECTION_ERROR);
        }
        return filename;
    }

    public static HashMap<String, Object> getSpotifySongMetadata(String songUrl) {
        Pattern trackPattern = Pattern.compile("/track/(\\w+)");
        Matcher trackMatcher = trackPattern.matcher(songUrl);
        String trackId;
        if (trackMatcher.find()) {
            trackId = trackMatcher.group(1);
        } else {
            msgBroker.msgLinkError("Failed to extract track ID from Spotify link!");
            return null;
        }
        while (true) {
            try {
                HttpRequest getSongMetadata = HttpRequest.newBuilder()
                        .uri(new URI("https://api.spotify.com/v1/tracks/" + trackId))
                        .GET()
                        .header("Authorization", "Bearer " + AppSettings.GET.spotifyAccessToken())
                        .header("accept-encoding", "gzip, deflate")
                        .header("content-encoding", "gzip")
                        .build();
                HttpResponse<byte[]> songMetadataResponse;
                try (HttpClient client = HttpClient.newHttpClient()) {
                    songMetadataResponse = client.send(getSongMetadata, HttpResponse.BodyHandlers.ofByteArray());
                }
                String songMetadataResponseBody = extractContent(songMetadataResponse);
                // extract the JSON part of the list in the response body;
                JsonObject songMetadata = JsonParser.parseString(songMetadataResponseBody.replace("[\n{", "{").replace("}\n]", "}")).getAsJsonObject();
                if (songMetadata.has("error")) {
                    msgBroker.msgDownloadError("Failed to get song metadata! " + songMetadata.get("error").getAsJsonObject().get("message").getAsString());
                    if (Mode.isCLI()) {
                        // Get the time (from `retry-after` header) to wait before sending another request
                        String retryAfter = songMetadataResponse.headers().firstValue("retry-after").orElse("5");
                        long timeToWait = (long) parseStringToInt(retryAfter, "Failed to parse time to wait before retrying!", MessageCategory.DOWNLOAD) * 1000;
                        for (long i = timeToWait; i >= 0; i -= 1000) {
                            System.out.print("\r" + "Retrying in " + i / 1000 + " seconds...");
                            sleep(1000);
                        }
                        System.out.println("\r" + "Retrying now...");
                        continue;
                    } else {
                        return null;
                    }
                }
                String songName = songMetadata.get("name").getAsString().replace("\"", "");
                int duration = songMetadata.get("duration_ms").getAsInt();
                JsonArray artists = songMetadata.get("artists").getAsJsonArray();
                ArrayList<String> artistNames = new ArrayList<>();
                artists.forEach(artist -> artistNames.add(artist.getAsJsonObject().get("name").getAsString()));
                // Dump the JSON from the hashmap to a file for debugging
                HashMap<String, Object> songMetadataMap = new HashMap<>();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement artistJsonTree = gson.toJsonTree(artistNames);
                songMetadataMap.put("songName", songName);
                songMetadataMap.put("duration", duration);
                songMetadataMap.put("artists", artistJsonTree);
                String json = gson.toJson(songMetadataMap);
                File jsonFile = Program.getJsonDataPath().resolve("spotify-metadata.json").toFile();
                FileUtils.writeStringToFile(jsonFile, json, Charset.defaultCharset());
                msgBroker.msgLogInfo("Spotify metadata retrieved successfully!");
                return songMetadataMap;
            } catch (URISyntaxException e) {
                msgBroker.msgLinkError("Spotify API URI is incorrect! " + e.getMessage());
                break;
            } catch (IOException e) {
                msgBroker.msgLinkError("Failed to send request to Spotify API! " + e.getMessage());
                break;
            } catch (InterruptedException e) {
                msgBroker.msgLinkError("The request to Spotify API was interrupted! " + e.getMessage());
                break;
            }
        }
        return null;
    }

    private static String fetchSpotifyPlaylistData(String spotifyPlaylistAPIUrl) {
        try {
            HttpRequest getPlaylistMetadata = HttpRequest.newBuilder()
                    .uri(new URI(spotifyPlaylistAPIUrl))
                    .GET()
                    .header("Authorization", "Bearer " + AppSettings.GET.spotifyAccessToken())
                    .header("accept-encoding", "gzip, deflate")
                    .header("content-encoding", "gzip")
                    .build();
            HttpResponse<byte[]> songMetadataResponse;
            try (HttpClient client = HttpClient.newHttpClient()) {
                songMetadataResponse = client.send(getPlaylistMetadata, HttpResponse.BodyHandlers.ofByteArray());
                return extractContent(songMetadataResponse);
            } catch (UnknownHostException e) {
                msgBroker.msgLinkError("You are not connected to the Internet!");
                return null;
            } catch (IOException e) {
                msgBroker.msgLinkError("Failed to send request to Spotify API! " + e.getMessage());
                return null;
            } catch (InterruptedException e) {
                msgBroker.msgLinkError("The request to Spotify API was interrupted! " + e.getMessage());
                return null;
            }
        } catch (URISyntaxException e) {
            msgBroker.msgLinkError("Spotify API URI is incorrect! " + e.getMessage());
            return null;
        }
    }

    public static ArrayList<HashMap<String, Object>> getSpotifyPlaylistMetadata(String playlistUrl) {
        Pattern playlistPattern = Pattern.compile("/playlist/(\\w+)");
        Matcher playlistMatcher = playlistPattern.matcher(playlistUrl);
        String playlistId;
        if (playlistMatcher.find()) {
            playlistId = playlistMatcher.group(1);
        } else {
            msgBroker.msgLinkError("Failed to extract playlist ID from Spotify link!");
            return null;
        }
        int offset = 0;
        int totalNumberOfTracks = 0;
        String requestUrl = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks?offset=" + offset + "&limit=100";
        ArrayList<JsonArray> listOfItems = new ArrayList<>();
        while (offset == 0 || offset != totalNumberOfTracks) {
            String playlistMetadataResponseBody = fetchSpotifyPlaylistData(requestUrl);
            if (playlistMetadataResponseBody == null) {
                return null;
            }
            // extract the JSON part of the list in the response body;
            JsonObject playlistMetadata = JsonParser.parseString(playlistMetadataResponseBody.replace("[\n{", "{").replace("}\n]", "}")).getAsJsonObject();
            if (offset == 0) {
                totalNumberOfTracks = playlistMetadata.get("total").getAsInt();
                if (totalNumberOfTracks == 0) {
                    msgBroker.msgLinkError("The playlist is empty!");
                    return null;
                }
                msgBroker.msgLinkInfo("Total number of tracks in the playlist: " + totalNumberOfTracks);
            }
            JsonElement nextUrl = playlistMetadata.get("next");
            if (!nextUrl.isJsonNull()) {
                requestUrl = nextUrl.getAsString();
            }
            JsonArray items = playlistMetadata.get("items").getAsJsonArray();
            offset += items.size();
            listOfItems.add(items);
        }
        ArrayList<HashMap<String, Object>> playlistData = new ArrayList<>();
        for (JsonArray items : listOfItems) {
            for (int i = 0; i < items.size(); i++) {
                JsonObject track = items.get(i).getAsJsonObject().get("track").getAsJsonObject();
                String link = track.get("external_urls").getAsJsonObject().get("spotify").getAsString();
                String songName = track.get("name").getAsString().replace("\"", "");
                int duration = track.get("duration_ms").getAsInt();
                JsonArray artists = track.get("artists").getAsJsonArray();
                ArrayList<String> artistNames = new ArrayList<>();
                for (int j = 0; j < artists.size(); j++) {
                    artistNames.add(artists.get(j).getAsJsonObject().get("name").getAsString());
                }
                HashMap<String, Object> songMetadataMap = new HashMap<>();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement artistJsonTree = gson.toJsonTree(artistNames);
                songMetadataMap.put("link", link);
                songMetadataMap.put("songName", songName);
                songMetadataMap.put("duration", duration);
                songMetadataMap.put("artists", artistJsonTree);
                playlistData.add(songMetadataMap);
            }
        }
        msgBroker.msgLogInfo("Spotify playlist metadata retrieved successfully!");
        return playlistData;
    }

    private static String extractContent(HttpResponse<byte[]> response) {
        StringBuilder content = new StringBuilder();
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(response.body()));
             BufferedReader br = new BufferedReader(new InputStreamReader(gis)))
        {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            msgBroker.msgLinkError("Failed to extract content from Spotify API response! " + e.getMessage());
        }
        return content.toString();
    }

    public static String cleanFilename(String filename) {
        String fn = StringEscapeUtils.unescapeJava(filename);
        return fn.replaceAll("[^a-zA-Z0-9-.%?*:|_)<(> ]+", "").strip();
    }

    private static Runnable ytDLPJsonData(String folderPath, String link) {
        return () -> {
            String[] command = new String[]{Program.get(YT_DLP), "--write-info-json", "--skip-download", "--restrict-filenames", "-P", folderPath, link, "-o", "yt-metadata"}; // -o flag is used to specify the output filename which is "yt-metadata.info.json" in this case
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                Process p = pb.start();
                try (
                        InputStreamReader in = new InputStreamReader(p.getInputStream());
                        BufferedReader reader = new BufferedReader(in)
                        )
                {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("ERROR") || line.contains("WARNING")) {
                            if (line.contains("unable to extract username")) {
                                msgBroker.msgLinkError("The Instagram post/reel is private!");
                                break;
                            } else if (line.contains("The playlist does not exist")) {
                                msgBroker.msgLinkError("The YouTube playlist does not exist or is private!");
                                break;
                            } else if (line.contains("Video unavailable")) {
                                msgBroker.msgLinkError("The YouTube video is unavailable!");
                                break;
                            } else if (line.contains("Skipping player responses from android clients")) {
                                msgBroker.msgLogWarning(line);
                            } else if (line.contains("Unable to download webpage") && line.contains("Temporary failure in name resolution")) {
                                msgBroker.msgLinkError("You are not connected to the Internet!");
                                break;
                            } else {
                                if (line.contains("ERROR")) {
                                    msgBroker.msgLogError(line);
                                } else {
                                    msgBroker.msgLogWarning(line);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                msgBroker.msgLinkError("Failed to get link metadata! " + e.getMessage());
            }
        };
    }

    public static boolean isURL(String text) {
        String regex = "^(http(s)?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            msgBroker.msgLinkError("The calling method failed to sleep for " + time + " milliseconds. It got interrupted. " + e.getMessage());
        }
    }

    public static String randomString(int characterCount) {
        String source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int count = source.length();
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < characterCount; x++) {
            int index = RANDOM_GENERATOR.nextInt(count);
            sb.append(source.charAt(index));
        }
        return sb.toString();
    }

    public static String formatInstagramLink(String link) {
        if (!link.contains("?utm_source=ig_embed")) {
            if (link.contains("?")) {
                link = link.substring(0, link.indexOf("?")) + "?utm_source=ig_embed";
            } else {
                link = link + "?utm_source=ig_embed";
            }
        }
        return link;
    }

    public static String getMatchingVideoID(ArrayList<HashMap<String, Object>> searchResults, int spotifySongDuration, ArrayList<String> artistNames) {
        String matchedVideoId = "";
        for (HashMap<String, Object> searchResult : searchResults) {
            int noOfMatches = 0;
            if (searchResult.get("videoId") == null) {
                continue;
            }
            String[] durationInMinutes = searchResult.get("duration").toString().split(":");
            int videoDurationInMs = parseStringToInt(durationInMinutes[0], "Failed to parse video duration for Spotify song!", MessageCategory.DOWNLOAD) * 60000 + parseStringToInt(durationInMinutes[1], "Failed to parse video duration for Spotify song!", MessageCategory.DOWNLOAD) * 1000;
            if ("Top result".equalsIgnoreCase(searchResult.get("category").toString())) {
                if (isDurationMatched(videoDurationInMs, spotifySongDuration, false)) {
                    matchedVideoId = (String) searchResult.get("videoId");
                }
            }
            @SuppressWarnings("unchecked")
            ArrayList<String> artistsFromSearchResult = (ArrayList<String>) searchResult.get("artists");
            for (String artist : artistNames) {
                if (artistsFromSearchResult.contains(artist)) {
                    noOfMatches++;
                }
            }
            if ((double) noOfMatches / artistNames.size() >= 0.5) { // 50% match
                if (isDurationMatched(videoDurationInMs, spotifySongDuration, false)) {
                    matchedVideoId = (String) searchResult.get("videoId");
                    break;
                }
            }
            if (isDurationMatched(videoDurationInMs, spotifySongDuration, true)) {
                matchedVideoId = (String) searchResult.get("videoId");
                break;
            }
        }
        return matchedVideoId;
    }

    private static boolean isDurationMatched(int actualDuration, int expectedDuration, boolean strictMatch) {
        if (strictMatch) {
            return (double) expectedDuration / actualDuration >= 0.98 && (double) expectedDuration / actualDuration <= 1.01; // 98% - 101% match
        } else {
            return (double) expectedDuration / actualDuration >= 0.9 && (double) expectedDuration / actualDuration <= 1.1; // 90% - 110% match
        }
    }

    public static ArrayList<HashMap<String, Object>> getYoutubeSearchResults(String query, boolean searchWithFilters) {
        query = query.replace(" ", "-"); // make the query URL-friendly
        String googleVisitorId = getGoogleVisitorId();
        if (googleVisitorId == null) {
            msgBroker.msgDownloadError("Failed to get Google Visitor ID!");
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String params = "EgWKAQIIAUICCAFqDBAOEAoQAxAEEAkQBQ%3D%3D"; // Parameters for filtering search results to music only
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI("https://music.youtube.com/youtubei/v1/search?alt=json"))
                    .POST(HttpRequest.BodyPublishers.ofString("{\"context\":{\"client\":{\"clientName\":\"WEB_REMIX\",\"clientVersion\":\"1." + dateFormat.format(new Date()) + "\"},\"user\":{}},\"query\":\"" + query + "\"" + (searchWithFilters ? ",\"params\":\"" + params + "\"" : "") + "}"))
                    .header("user-agent", USER_AGENT)
                    .header("accept", "*/*")
                    .header("content-type", "application/json")
                    .header("accept-encoding", "gzip, deflate")
                    .header("content-encoding", "gzip")
                    .header("cookie", "{'SOCS': 'CAI', 'X-Goog-Visitor-Id': '" + googleVisitorId + "'}")
                    .header("origin", "https://music.youtube.com")
                    .build();
            HttpResponse<byte[]> response;
            try (HttpClient client = HttpClient.newHttpClient()) {
                response = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
            } catch (UnknownHostException e) {
                msgBroker.msgDownloadError("You are not connected to the Internet!");
                return null;
            } catch (IOException e) {
                msgBroker.msgDownloadError("Failed to get search results! " + e.getMessage());
                return null;
            }
            if (response.statusCode() != 200) {
                msgBroker.msgDownloadError("Failed to get search results! " + response.statusCode() + " " + response.uri());
            } else {
                String responseContent = extractContent(response);
                JsonObject jsonObject = JsonParser.parseString(responseContent).getAsJsonObject();
                JsonArray collectionOfMusicShelfRenderers = jsonObject.get("contents").getAsJsonObject().get("tabbedSearchResultsRenderer").getAsJsonObject().get("tabs").getAsJsonArray().get(0).getAsJsonObject().get("tabRenderer").getAsJsonObject().get("content").getAsJsonObject().get("sectionListRenderer").getAsJsonObject().get("contents").getAsJsonArray();
                ArrayList<HashMap<String, Object>> searchResults = new ArrayList<>();
                for (int i = 0; i < collectionOfMusicShelfRenderers.size(); i++) {
                    if (collectionOfMusicShelfRenderers.get(i).getAsJsonObject().has("musicCardShelfRenderer")) {
                        JsonObject musicCardShelfRenderer = collectionOfMusicShelfRenderers.get(i).getAsJsonObject().get("musicCardShelfRenderer").getAsJsonObject();
                        String resultType = musicCardShelfRenderer.get("subtitle").getAsJsonObject().get("runs").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().toLowerCase();
                        if (!"song".equalsIgnoreCase(resultType) && !"video".equalsIgnoreCase(resultType)) {
                            continue;
                        }
                        String category = musicCardShelfRenderer.get("header").getAsJsonObject().get("musicCardShelfHeaderBasicRenderer").getAsJsonObject().get("title").getAsJsonObject().get("runs").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                        HashMap<String, Object> searchResult = new HashMap<>();
                        searchResult.put("category", category);
                        searchResult.put("resultType", resultType);
                        if ("video".equalsIgnoreCase(resultType)) {
                            JsonElement videoId = musicCardShelfRenderer.get("title").getAsJsonObject().get("runs").getAsJsonArray().get(0).getAsJsonObject().get("navigationEndpoint").getAsJsonObject().get("watchEndpoint").getAsJsonObject().get("videoId");
                            if (videoId.isJsonNull()) {
                                continue;
                            }
                            searchResult.put("videoId", videoId.getAsString());
                            ProcessBuilder getVideoDuration = new ProcessBuilder(Program.get(YT_DLP), "--print", "duration_string", "https://www.youtube.com/watch?v=" + videoId.getAsString());
                            try {
                                Process getVideoDurationProcess = getVideoDuration.start();
                                try (BufferedReader in = new BufferedReader(new InputStreamReader(getVideoDurationProcess.getInputStream()))) {
                                    String duration = in.readLine();
                                    searchResult.put("duration", duration);
                                } catch (IOException e) {
                                    msgBroker.msgDownloadError("Failed to get video duration! " + e.getMessage());
                                }
                            } catch (IOException e) {
                                msgBroker.msgDownloadError("Failed to get video duration! " + e.getMessage());
                            }
                        }
                        searchResults.add(searchResult);
                    } else if (collectionOfMusicShelfRenderers.get(i).getAsJsonObject().has("musicShelfRenderer")) {
                        JsonObject musicShelfRenderer = collectionOfMusicShelfRenderers.get(i).getAsJsonObject().get("musicShelfRenderer").getAsJsonObject();
                        String category = musicShelfRenderer.get("title").getAsJsonObject().get("runs").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                        if (!"Songs".equalsIgnoreCase(category)) {
                            continue;
                        }
                        JsonArray contents = musicShelfRenderer.get("contents").getAsJsonArray();
                        for (int j = 0; j < contents.size(); j++) {
                            JsonObject musicResponsiveListItemRenderer = contents.get(j).getAsJsonObject().get("musicResponsiveListItemRenderer").getAsJsonObject();
                            JsonArray runs = musicResponsiveListItemRenderer.get("flexColumns").getAsJsonArray().get(1).getAsJsonObject().get("musicResponsiveListItemFlexColumnRenderer").getAsJsonObject().get("text").getAsJsonObject().get("runs").getAsJsonArray();
                            ArrayList<HashMap<String, Object>> artistsAndDuration = parseRuns(runs);
                            HashMap<String, Object> searchResult = new HashMap<>();
                            for (HashMap<String, Object> hashMap : artistsAndDuration) {
                                searchResult.putAll(hashMap);
                            }
                            searchResult.put("category", category);
                            JsonObject watchEndpoint = musicResponsiveListItemRenderer.get("overlay").getAsJsonObject().get("musicItemThumbnailOverlayRenderer").getAsJsonObject().get("content").getAsJsonObject().get("musicPlayButtonRenderer").getAsJsonObject().get("playNavigationEndpoint").getAsJsonObject().get("watchEndpoint").getAsJsonObject();
                            String videoType = watchEndpoint.get("watchEndpointMusicSupportedConfigs").getAsJsonObject().get("watchEndpointMusicConfig").getAsJsonObject().get("musicVideoType").getAsString();
                            if ("MUSIC_VIDEO_TYPE_ATV".equalsIgnoreCase(videoType)) {
                                searchResult.put("resultType", "song");
                                JsonElement videoId = watchEndpoint.get("videoId");
                                if (videoId.isJsonNull()) {
                                    continue;
                                }
                                searchResult.put("videoId", videoId.getAsString());
                            } else {
                                searchResult.put("resultType", "video");
                            }
                            searchResults.add(searchResult);
                        }
                    }
                }
                return searchResults;
            }
        } catch (URISyntaxException | InterruptedException e) {
            msgBroker.msgDownloadError("Failed to get search results! " + e.getMessage());
        }
        return null;
    }

    private static ArrayList<HashMap<String, Object>> parseRuns(JsonArray runs) {
        HashMap<String, Object> artist = new HashMap<>();
        HashMap<String, Object> duration = new HashMap<>();
        ArrayList<String> artists = new ArrayList<>();
        for (int i = 0; i < runs.size(); i += 2) { // uneven indices are always the separators
            String text = runs.get(i).getAsJsonObject().get("text").getAsString();
            if (runs.get(i).getAsJsonObject().has("navigationEndpoint")) {
                String pageType = runs.get(i).getAsJsonObject().get("navigationEndpoint").getAsJsonObject().get("browseEndpoint").getAsJsonObject().get("browseEndpointContextSupportedConfigs").getAsJsonObject().get("browseEndpointContextMusicConfig").getAsJsonObject().get("pageType").getAsString();
                if ("MUSIC_PAGE_TYPE_ARTIST".equalsIgnoreCase(pageType)) {
                    artists.add(text);
                }
            } else {
                duration.put("duration", text);
            }
        }
        artist.put("artists", artists);
        return new ArrayList<>() {
            {
                add(artist);
                add(duration);
            }
        };
    }

    private static String getGoogleVisitorId() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://music.youtube.com"))
                    .GET()
                    .header("user-agent", USER_AGENT)
                    .header("accept", "*/*")
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("accept-encoding", "gzip, deflate")
                    .header("content-encoding", "gzip")
                    .header("cookie", "{'SOCS': 'CAI'}")
                    .build();
            HttpResponse<byte[]> response;
            try (HttpClient client = HttpClient.newHttpClient()) {
                response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            }
            String responseContent = extractContent(response);
            // Pattern is "ytcfg\.set\s*\(\s*(\{.+?})\s*\)\s*;"
            Pattern pattern2 = Pattern.compile("ytcfg\\.set\\s*\\(\\s*(\\{.+?})\\s*\\)\\s*;");
            Matcher matcher2 = pattern2.matcher(responseContent);
            String json = "";
            if (matcher2.find()) {
                json = matcher2.group(1);
            }
            // extract the "VISITOR_DATA" from the JSON
            pattern2 = Pattern.compile("\"VISITOR_DATA\"\\s*:\\s*\"(.*?)\"");
            matcher2 = pattern2.matcher(json);
            if (matcher2.find()) {
                return matcher2.group(1); // return the visitor ID (aka "X-Goog-Visitor-Id")
            }
        } catch (UnknownHostException e) {
            msgBroker.msgDownloadError("You are not connected to the Internet!");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            msgBroker.msgDownloadError("Failed to get Google Visitor ID! " + e.getMessage());
        }
        return null;
    }

    public static void setFfmpegVersion() {
        Path ffmpegPath = Paths.get(Program.get(Program.FFMPEG));
        if (!Files.exists(ffmpegPath)) {
            msgBroker.msgLogError("FFMPEG not found at " + ffmpegPath);
            AppSettings.SET.isFfmpegWorking(false);
        } else {
            msgBroker.msgLogInfo("FFMPEG found at " + ffmpegPath);
            ProcessBuilder getFfmpegVersion = new ProcessBuilder(ffmpegPath.toString(), "-version");
            try {
                Process process = getFfmpegVersion.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("ffmpeg version")) {
                            String version = line.split(" ")[2];
                            msgBroker.msgLogInfo("FFMPEG version: " + version);
                            msgBroker.msgLogInfo(line);
                            AppSettings.SET.isFfmpegWorking(true);
                            AppSettings.SET.ffmpegVersion(version);
                        }
                    }
                }
            } catch (IOException e) {
                msgBroker.msgLogError("Failed to get FFMPEG version : " + e.getMessage());
                AppSettings.SET.isFfmpegWorking(false);
            }
        }
    }

    public static Runnable setYtDlpVersion() {
        return () -> {
            String command = Program.get(YT_DLP);
            ProcessBuilder getYtDlpVersion = new ProcessBuilder(command, "--version");
            Process ytDlpVersionTask;
            try {
                ytDlpVersionTask = getYtDlpVersion.start();
            } catch (IOException e) {
                msgBroker.msgInitError("Failed to get yt-dlp version! " + e.getMessage());
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ytDlpVersionTask).getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    AppSettings.SET.ytDlpVersion(line);
                }
            } catch (IOException e) {
                msgBroker.msgInitError("Failed to get yt-dlp version! " + e.getMessage());
            }
        };
    }

    public static Runnable setSpotifyAccessToken() {
        return () -> {
            try {
                URL url = new URI("https://accounts.spotify.com/api/token").toURL();
                String encodedCredentials = Base64.getEncoder().encodeToString(new byte[]{53, 102, 53, 55, 51, 99, 57, 54, 50, 48, 52, 57, 52, 98, 97, 101, 56, 55, 56, 57, 48, 99, 48, 102, 48, 56, 97, 54, 48, 50, 57, 51, 58, 50, 49, 50, 52, 55, 54, 100, 57, 98, 48, 102, 51, 52, 55, 50, 101, 97, 97, 55, 54, 50, 100, 57, 48, 98, 49, 57, 98, 48, 98, 97, 56});
                String data = "grant_type=client_credentials";
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Authorization", "Basic " + encodedCredentials);
                con.setRequestProperty("Payload", data);
                con.setDoOutput(true);
                con.getOutputStream().write(data.getBytes());
                con.connect();
                StringBuilder responseContent = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        responseContent.append(line);
                    }
                }
                JsonObject jsonObject = JsonParser.parseString(responseContent.toString()).getAsJsonObject();
                AppSettings.SET.spotifyAccessToken(jsonObject.get("access_token").getAsString());
            } catch (UnknownHostException e) {
                msgBroker.msgLogError("You are not connected to the Internet!");
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.schedule(setSpotifyAccessToken(), 5, TimeUnit.SECONDS); // retry after 5 seconds
            } catch (IOException e) {
                msgBroker.msgInitError("Failed to get Spotify access token! Failed to read response from Spotify API! " + e.getMessage());
            } catch (URISyntaxException e) {
                msgBroker.msgInitError("Failed to get Spotify access token! Spotify API URI is incorrect! " + e.getMessage());
            }
        };
    }

    public static String getSpotifyFilename(String jsonString) {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        String songName = jsonObject.get("songName").getAsString();
        return cleanFilename(songName) + ".webm";
    }

    public static String convertToMp3(Path inputFilePath) {
        String command = Program.get(Program.FFMPEG);
        Path outputFilePath = inputFilePath.getParent().resolve(FilenameUtils.getBaseName(inputFilePath.toString()) + " - converted.mp3").toAbsolutePath();
        String newFilename;
        if (outputFilePath.toFile().exists()) {
            newFilename = renameFile(outputFilePath.getFileName().toString(), outputFilePath.getParent().toString()); // rename the file if it already exists else ffmpeg conversion hangs indefinitely and tries to overwrite the file
            outputFilePath = outputFilePath.getParent().resolve(newFilename);
        }
        ProcessBuilder convertToMp3 = new ProcessBuilder(command, "-i", inputFilePath.toString(), outputFilePath.toString());
        try {
            Process process = convertToMp3.start();
            process.waitFor();
            if (process.exitValue() == 0 && Files.exists(outputFilePath)) {
                Files.delete(inputFilePath);
                File renamedFile = inputFilePath.getParent().resolve(FilenameUtils.getBaseName(inputFilePath.toString()) + ".mp3").toFile();
                if (renamedFile.exists()) {
                    newFilename = renameFile(renamedFile.getName(), renamedFile.getParent());
                    renamedFile = renamedFile.getParentFile().toPath().resolve(newFilename).toFile();
                }
                if (outputFilePath.toFile().renameTo(renamedFile)) {
                    msgBroker.msgLogInfo("Converted to mp3 successfully!");
                    return "Converted to mp3 successfully!";
                } else {
                    msgBroker.msgLogError("Failed to rename the converted file!");
                    return "Failed to rename the converted file!";
                }
            } else {
                msgBroker.msgLogError("Failed to convert to mp3!");
                return "Failed to convert to mp3!";
            }
        } catch (IOException e) {
            msgBroker.msgLogError("Failed to convert to mp3! IOException: " + e.getMessage());
            return "Failed to convert to mp3! IOException: " + e.getMessage();
        } catch (InterruptedException e) {
            msgBroker.msgLogError("Failed to convert to mp3! User interrupted the process. " + e.getMessage());
            return "Failed to convert to mp3! User interrupted the process. " + e.getMessage();
        }
    }

    public static int parseStringToInt(String string, String errorMessage, MessageCategory messageCategory) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            msgBroker.msgError(errorMessage + " " + e.getMessage(), messageCategory);
            return 0;
        }
    }

    public static String formatYoutubeLink(String link) throws MalformedURLException, URISyntaxException {
        String videoId = null;
        URI uri = URI.create(link);
        String domain = uri.getHost();

        if ("youtu.be".equals(domain)) {
            String path = uri.getPath();
            if (path != null && path.length() > 1) {
                videoId = path.substring(1); // removing the leading "/"
            }
        }

        else if ("www.youtube.com".equals(domain) || "youtube.com".equals(domain)) {
            Map<String, String> queryParams = extractQueryParams(link, "v");
            videoId = queryParams.get("v");
        }

        if (videoId != null) {
            uri = new URI("https", "www.youtube.com", "/watch", "v=" + videoId, null);
            link = uri.toString();
        } else {
            throw new MalformedURLException(link + " is not a valid youtube link!");
        }

        return link;
    }

    /**
     * Extracts the specified query parameters from the given URL. If no parameter names are provided (null or empty), all parameters are returned.
     *
     * @param urlLink   The URL string from which to extract parameters.
     * @param paramNames The names of the query parameters to extract. If null or empty, all parameters will be returned.
     * @return A map containing the query parameter names and their corresponding values.
     */
    public static Map<String, String> extractQueryParams(String urlLink, String... paramNames) {
        Map<String, String> paramMap = new HashMap<>();

        URL url = null;
        try {
            url = URI.create(urlLink).toURL();
        } catch (MalformedURLException e) {
            msgBroker.msgLinkError("Connection to the link timed out! Please check your internet connection. " + e.getMessage());
        }
        String query = url != null ? url.getQuery() : null;

        // query is null or empty, return an empty map (no query parameters)
        if (query == null || query.isEmpty()) {
            return paramMap;
        }

        // splitting query string into individual parameters
        String[] params = query.split("&");

        // check if specific parameters are requested or if all should be returned
        boolean returnAllParams = (paramNames == null || paramNames.length == 0);

        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                String paramName = pair[0];
                String paramValue = pair[1];

                // add parameter to the map if it's requested or if all parameters should be returned
                if (returnAllParams || contains(paramNames, paramName)) {
                    paramMap.put(paramName, paramValue);
                }
            }
        }

        return paramMap;
    }

    /**
     * Helper method to check if an array contains a specific value.
     *
     * @param array The array to check.
     * @param value The value to search for.
     * @return True if the array contains the value, false otherwise.
     */
    private static boolean contains(String[] array, String value) {
        if (array == null) return false;
        for (String item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
