package GUI.experiment;

import GUI.Support.Job;
import Utils.Utility;
import javafx.concurrent.Task;

import java.util.LinkedList;

public class GetFilename extends Task<LinkedList<Job>> {

    private final String link;
    private final String dir;

    public GetFilename(String link, String dir) {
        this.link = link;
        this.dir = dir;
    }

    private final LinkedList<Job> filenameList = new LinkedList<>();

    @Override
    protected LinkedList<Job> call() {
        this.updateMessage("Retrieving Filename");
        LinkedList<String> jsonList = Utility.getLinkMetadata(link);
        for(String json : jsonList) {
            String filename = Utility.getFilenameFromJson(json);
            String fileLink = Utility.getURLFromJson(json);
            filenameList.addLast(new Job(fileLink, dir, filename));
            updateValue(filenameList);
        }
        return filenameList;
    }
}
