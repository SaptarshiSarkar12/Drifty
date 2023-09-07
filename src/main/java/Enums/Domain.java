package Enums;

import GUI.Support.FileExtensions;
import Utils.Utility;
import org.apache.commons.io.FilenameUtils;

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

    public static Domain getDomain(String link, FileExtensions fileExtensions) {
        String ext = FilenameUtils.getExtension(link);
        if (fileExtensions.match(ext)) {
            return BINARY_FILE;
        }
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
