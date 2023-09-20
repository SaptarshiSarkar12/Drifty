package Enums;

import Utils.Utility;

public enum LinkType {
    YOUTUBE, INSTAGRAM, OTHER;

    public static LinkType getLinkType(String link) {
        if (Utility.isYoutube(link)) {
            return YOUTUBE;
        }
        else if (Utility.isInstagram(link)) {
            return INSTAGRAM;
        }
        else {
            return OTHER;
        }
    }

    public String get() {
        return switch(this) {
            case YOUTUBE -> "YouTube";
            case INSTAGRAM -> "Instagram";
            case OTHER -> "Other";
        };
    }
}
