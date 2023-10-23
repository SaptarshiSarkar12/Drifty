package Enums;

import Utils.Utility;

public enum LinkType {
    YOUTUBE, INSTAGRAM, SPOTIFY, OTHER;

    public static LinkType getLinkType(String link) {
        if (Utility.isYoutube(link)) {
            return YOUTUBE;
        } else if (Utility.isInstagram(link)) {
            return INSTAGRAM;
        } else if (Utility.isSpotify(link)) {
            return SPOTIFY;
        } else {
            return OTHER;
        }
    }

    public String get() {
        return switch (this) {
            case YOUTUBE -> "YouTube";
            case INSTAGRAM -> "Instagram";
            case SPOTIFY -> "Spotify";
            case OTHER -> "Other";
        };
    }
}
