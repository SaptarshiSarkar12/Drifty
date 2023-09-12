package GUI.Support;

import Enums.MessageCategory;
import Enums.MessageType;
import Preferences.AppSettings;
import Utils.Environment;

import java.util.concurrent.ConcurrentLinkedDeque;

public class JobHistory {

    public JobHistory() {
        this.jobHistoryList = new ConcurrentLinkedDeque<>();
    }

    private ConcurrentLinkedDeque<Job> jobHistoryList;

    public ConcurrentLinkedDeque<Job> getList() {
        Environment.getMessageBroker().sendMessage("Job History Fetched", MessageType.INFO, MessageCategory.INITIALIZATION);
        if (jobHistoryList == null) {
            this.jobHistoryList = new ConcurrentLinkedDeque<>();
        }
        return new ConcurrentLinkedDeque<>(jobHistoryList);
    }

    public void addJob(Job newJob) {
        for (Job job : jobHistoryList) {
            if (job.matchesLink(newJob))
                return;
        }
        jobHistoryList.addLast(newJob);
        save();
    }

    public void clear() {
        jobHistoryList = new ConcurrentLinkedDeque<>();
        save();
    }

    public boolean isEmpty() {
        return jobHistoryList.isEmpty();
    }

    public boolean exists(Job job) {
        for (Job j : jobHistoryList) {
            if (j.getLink().equals(job.getLink())) {
                return true;
            }
        }
        return false;
    }

    public boolean exists(String link) {
        for (Job job : jobHistoryList) {
            if (job.getLink().equals(link)) {
                return true;
            }
        }
        return false;
    }

    public Job getJob(String link) {
        for(Job job : jobHistoryList)  {
            if(job.matchesLink(link))
                return job;
        }
        return null;
    }

    private void save() {
        AppSettings.set.jobHistory(this);
    }
}
