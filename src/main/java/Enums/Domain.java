package Enums;

import Utils.Utility;

public enum Domain {
    YOUTUBE, INSTAGRAM, BINARY_FILE, OTHER;

    public static Domain getDomain(String link) {
        if (Utility.isYoutubeLink(link)) {
            return YOUTUBE;
        }
        else if (Utility.isInstagramLink(link)) {
            return INSTAGRAM;
        }
        else {
            return OTHER;
        }
    }
}
