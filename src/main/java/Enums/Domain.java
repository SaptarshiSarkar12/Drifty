package Enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Domain {
    YOUTUBE, INSTAGRAM, OTHER;

    public static Domain getDomain(String link) {
        String regex = "(https|http)(://)(\\w+\\.|)(\\w+\\.\\w+)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(link);
        if (m.find()) {
            return switch(m.group(4).toLowerCase()) {
                case "youtube.com" -> YOUTUBE;
                case "instagram.com" -> INSTAGRAM;
                default -> OTHER;
            };
        }
        return OTHER;
    }
}
