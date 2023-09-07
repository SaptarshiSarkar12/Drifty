package GUI.Support;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileExtensions {

    private final List<String> extensions = new ArrayList<>();

    public boolean match(String ext) {
        extensions.sort(Comparator.comparing(String::toString));
        return extensions.contains(ext);
    }
}
