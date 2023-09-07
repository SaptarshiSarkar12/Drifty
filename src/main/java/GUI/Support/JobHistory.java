package GUI.Support;

import Enums.MessageCategory;
import Enums.MessageType;
import Preferences.AppSettings;
import Utils.Environment;

import java.util.concurrent.ConcurrentLinkedDeque;

public class JobHistory {

    public JobHistory() {
        this.jobList = new ConcurrentLinkedDeque<>();
        this.jobList.add(new Job("Template","JobHistory","Pattern", false));
    }

    private ConcurrentLinkedDeque<Job> jobList;

    public ConcurrentLinkedDeque<Job> getJobList() {
        if (jobList == null) {
            this.jobList = new ConcurrentLinkedDeque<>();
        }
        if(jobList.isEmpty())
            return jobList;
        ConcurrentLinkedDeque<Job> list = new ConcurrentLinkedDeque<>();
        Environment.getMessageBroker().sendMessage("Job History Fetched", MessageType.INFO, MessageCategory.INITIALIZATION);
        for(Job job : jobList) {
            String link = job.getLink();
            String dir = job.getDir();
            String filename = job.getFilename();
            list.addLast(new Job(link, dir, filename, false));
            Environment.getMessageBroker().sendMessage("Link: " + link + "; Dir: " + dir + "; Filename: " + filename, MessageType.INFO, MessageCategory.INITIALIZATION);
        }
        return list;
    }

    public void setJobList(ConcurrentLinkedDeque<Job> jobList) {
        if (jobList != null) {
            this.jobList = new ConcurrentLinkedDeque<>(jobList);
            AppSettings.set.jobHistory(this);
        }
    }
}
