package GUI.Support;

import Preferences.AppSettings;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class JobHistory {
    private ConcurrentLinkedDeque<Job> jobHistoryList;

    public JobHistory() {
        this.jobHistoryList = new ConcurrentLinkedDeque<>();
    }

    public void addJob(Job newJob, boolean isCLI) {
        if (isCLI) {
            jobHistoryList.addLast(newJob);
            AppSettings.SET.jobHistory(this);
        } else {
            for (Job job : jobHistoryList) {
                if (job.matchesLink(newJob)) {
                    return;
                }
            }
            jobHistoryList.addLast(newJob);
            save();
        }
    }

    public void clear() {
        jobHistoryList = new ConcurrentLinkedDeque<>();
        save();
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
        for (Job job : jobHistoryList) {
            if (job.matchesLink(link)) {
                return job;
            }
        }
        return null;
    }

    private void save() {
        removeDuplicates();
        AppSettings.SET.jobHistory(this);
    }

    private void removeDuplicates() {
        LinkedList<Job> removeList = new LinkedList<>();
        for (Job jobSource : jobHistoryList) {
            int dupeCount = 0;
            for (Job job : jobHistoryList) {
                boolean duplicateFound = jobSource.matchesLink(job);
                if (duplicateFound) {
                    dupeCount++;
                }
                if (duplicateFound && dupeCount > 1) {
                    removeList.addLast(job);
                }
            }
        }
        for (Job job : removeList) {
            jobHistoryList.remove(job);
        }
    }

}
