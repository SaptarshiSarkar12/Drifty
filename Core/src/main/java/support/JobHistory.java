package support;

import java.util.concurrent.ConcurrentLinkedDeque;

public class JobHistory {
    private ConcurrentLinkedDeque<Job> jobHistoryList;

    public JobHistory() {
        this.jobHistoryList = new ConcurrentLinkedDeque<>();
    }

    public void addJob(Job newJob, boolean isCLI) {
        if (isCLI) {
            jobHistoryList.addLast(newJob);
        }
else {
            for (Job job : jobHistoryList) {
                if (job.matchesLink(newJob)) {
                    return;
                }
            }
            jobHistoryList.addLast(newJob);
        }
    }

    public void clear() {
        jobHistoryList = new ConcurrentLinkedDeque<>();
    }

    public boolean exists(String link) {
        for (Job job : jobHistoryList) {
            if (job.getSourceLink().equals(link)) {
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
}
