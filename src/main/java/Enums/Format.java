package Enums;
import java.util.List;
/**
 * This class holds all of the valid video packages known by yt-dlp
 */
public enum Format {
    GP3,
    AAC,
    FLV,
    M4A,
    MP3,
    MP4,
    OGG,
    WAV,
    WEBM;
    private static final List<String> formats = List.of(
            "3gp",
            "aac",
            "flv",
            "m4a",
            "mp3",
            "mp4",
            "ogg",
            "wav",
            "webm");
    public static boolean isValid(String ext) {
        return formats.contains(ext.toLowerCase());
    }
}
