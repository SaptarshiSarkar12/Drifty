package properties;

import utils.Utility;

public enum LinkType {
    YOUTUBE, INSTAGRAM, SPOTIFY, OTHER;

    public static LinkType getLinkType(String link) {
        if (Utility.isYoutube(link)) {
            return YOUTUBE;
        }else if (Utility.isInstagram(link)) {
            return INSTAGRAM;
        }else if (Utility.isSpotify(link)) {
            return SPOTIFY;
        }else {
            return OTHER;
        }
    }
}
