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

    public Jobs() {
        this.jobList = new ConcurrentLinkedDeque<>();
    }
    private ConcurrentLinkedDeque<Job> jobList;

    public ConcurrentLinkedDeque<Job> jobList() {
        if (jobList == null) {
            return new ConcurrentLinkedDeque<>();
        }
        return new ConcurrentLinkedDeque<>(jobList);
    }

    public void add(Job newJob) {
        for(Job job : jobList) {
            if(job.matches(newJob))
                return;
        }
        jobList.addLast(newJob);
        save();
    }

    public void remove(Job oldJob) {
        Job removeJob = null;
        for(Job job : jobList) {
            if (job.matches(oldJob)) {
                removeJob = oldJob;
            }
        }
        if(removeJob != null) {
            jobList.remove(removeJob);
        }
        save();
    }

    public void setList(ConcurrentLinkedDeque<Job> jobList) {
        this.jobList = jobList;
        save();
    }

    private void save() {
        AppSettings.set.Jobs(this);
    }

    public boolean isNull() {
        return jobList == null;
    }

    public boolean notNull() {
        return jobList != null;
    }

    public boolean isEmpty() {
        return jobList.isEmpty();
    }

    public boolean isNotEmpty() {
        return !jobList.isEmpty();
    }

    public void clear() {
        jobList.clear();
    }
}
