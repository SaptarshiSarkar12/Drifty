package Enums;

import Utils.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Domain {
    YOUTUBE, INSTAGRAM, OTHER;

    public static Domain getDomain(String link) {
        if (Utility.isYoutubeLink(link)) {
            return YOUTUBE;
        } else if (Utility.isInstagramLink(link)) {
            return INSTAGRAM;
        } else {
            return OTHER;
        }
    }
}
