package Enums;

import Utils.Utility;

public enum LinkType {
    YOU_TUBE, INSTAGRAM, OTHER;

    public static LinkType fromLink(String link) {
        if (Utility.isYoutube(link)) {
            return YOU_TUBE;
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
            case YOU_TUBE -> "YouTube";
            case INSTAGRAM -> "Instagram";
            case OTHER -> "Other";
        };
    }
}
