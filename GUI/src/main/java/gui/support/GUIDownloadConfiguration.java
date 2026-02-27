package gui.support;

import gui.init.Environment;
import gui.utils.MessageBroker;
import support.DownloadConfiguration;

public class GUIDownloadConfiguration extends DownloadConfiguration {
    private final MessageBroker messageBroker = Environment.getMessageBroker();

    public GUIDownloadConfiguration(String link, String directory, String filename) {
        super(link, directory, filename);
    }

    public void prepareFileData() {
        messageBroker.msgLinkInfo("Fetching file data...");
        int statusCode = fetchFileData();
        if (statusCode == 0) {
            messageBroker.msgLinkInfo("File data fetched successfully");
            messageBroker.msgLinkInfo("Adding file(s) to batch...");
            updateJobList();
            messageBroker.msgLinkInfo("File(s) added to batch successfully");
        }
else {
            messageBroker.msgLogError("Failed to fetch file data");
        }
    }
}
