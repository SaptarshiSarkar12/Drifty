package Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class test {
    static String json = """
            [
                {
                    "name": "The Other (Shallou Remix)",
                    "artists": [
                        "Lauv",
                        "Shallou"
                    ],
                    "artist": "Lauv",
                    "genres": [
                        "pop"
                    ],
                    "disc_number": 1,
                    "disc_count": 1,
                    "album_name": "The Other (Remixes)",
                    "album_artist": "Lauv",
                    "duration": 210000,
                    "year": "2017",
                    "date": "2017-03-17",
                    "track_number": 2,
                    "tracks_count": 5,
                    "song_id": "3X4AcIcgHFEqVhZjxiqJtl",
                    "explicit": false,
                    "publisher": "Lauv",
                    "url": "https://open.spotify.com/track/3X4AcIcgHFEqVhZjxiqJtl",
                    "isrc": "TCACZ1708146",
                    "cover_url": "https://i.scdn.co/image/ab67616d0000b273043cc4551a8294ebcb93917e",
                    "copyright_text": "2017 Lauv",
                    "download_url": null,
                    "lyrics": null,
                    "popularity": 0,
                    "album_id": "5x7wsSiudoWtTAaU2ReB7Q",
                    "list_name": "Chill",
                    "list_url": "https://open.spotify.com/playlist/4PhriPzBV7HrnuwA7Z9YYh",
                    "list_position": 1,
                    "list_length": 130
                },
                {
                    "name": "Different Skies",
                    "artists": [
                        "Shoffy"
                    ],
                    "artist": "Shoffy",
                    "genres": [
                        "chill pop",
                        "la pop"
                    ],
                    "disc_number": 1,
                    "disc_count": 1,
                    "album_name": "Different Skies",
                    "album_artist": "Shoffy",
                    "duration": 153939,
                    "year": "2017",
                    "date": "2017-01-21",
                    "track_number": 1,
                    "tracks_count": 1,
                    "song_id": "6Gu1BaGI5ijqEqK3g6gvMi",
                    "explicit": true,
                    "publisher": "641809 Records DK",
                    "url": "https://open.spotify.com/track/6Gu1BaGI5ijqEqK3g6gvMi",
                    "isrc": "QZ4JJ1743715",
                    "cover_url": "https://i.scdn.co/image/ab67616d0000b2732a8d57e046fdeba6ab6b049f",
                    "copyright_text": "2017 641809 Records DK",
                    "download_url": null,
                    "lyrics": null,
                    "popularity": 36,
                    "album_id": "0CgqiKbBr0cTc30GO0NAYh",
                    "list_name": "Chill",
                    "list_url": "https://open.spotify.com/playlist/4PhriPzBV7HrnuwA7Z9YYh",
                    "list_position": 2,
                    "list_length": 130
                }
            ]""";

    public static void main(String[] args) {
        String linkRegex = "(\"url\": \")(.+)(\",)";
        Pattern linkPattern = Pattern.compile(linkRegex);
        Matcher linkMatcher = linkPattern.matcher(json);
        ArrayList<String> songLinks = new ArrayList<>();
        linkMatcher.results().forEach(matchResult -> {
            String songLink = matchResult.group(2);
            songLinks.add(songLink);
        });
        System.out.println(songLinks);
        String[] songs = songLinks.toArray(String[]::new);
    }
}
