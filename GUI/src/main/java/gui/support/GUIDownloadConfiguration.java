package gui.support;

import gui.init.Environment;
import gui.utils.MessageBroker;
import support.DownloadConfiguration;

public class GUIDownloadConfiguration extends DownloadConfiguration {
    protected MessageBroker msgBroker = Environment.getMessageBroker();

    public GUIDownloadConfiguration(String link, String directory) {
        super(link, directory, null);
    }

    public GUIDownloadConfiguration(String link, String directory, String filename) {
        super(link, directory, filename);
    }

    public void prepareFileData() {
        msgBroker.msgLinkInfo("Fetching file data...");
        int statusCode = fetchFileData();
        if (statusCode == 0) {
            msgBroker.msgLinkInfo("File data fetched successfully");
            msgBroker.msgLinkInfo("Adding file(s) to batch...");
            updateJobList();
            msgBroker.msgLinkInfo("File(s) added to batch successfully");
        } else {
            msgBroker.msgLogError("Failed to fetch file data");
        }
    }
}
