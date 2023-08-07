package GUI.Support;

import Preferences.AppSettings;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class stores a list of jobs and is technically "the batch" itself. Anytime an update
 * is posted to this class, it saves itself locally in JSON format using the Preferences
 * class via the 'AppSettings.set' class. The Job list is thread safe to avoid conflicts with
 * loops that run the jobs.
 */

public class Jobs {

    private ConcurrentLinkedDeque<Job> jobList;

    public ConcurrentLinkedDeque<Job> jobList() {
        if (jobList == null) {
            return new ConcurrentLinkedDeque<>();
        }
        return new ConcurrentLinkedDeque<>(jobList);
    }

    public void setJobList(ConcurrentLinkedDeque<Job> jobList) {
        this.jobList = new ConcurrentLinkedDeque<>(jobList);
        AppSettings.set.batchDownloadJobs(this);
    }
}
