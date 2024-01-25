package gui.support;

import gui.preferences.AppSettings;
import support.Job;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Jobs {
    private ConcurrentLinkedDeque<Job> jobList;

    public Jobs() {
        this.jobList = new ConcurrentLinkedDeque<>();
    }

    public ConcurrentLinkedDeque<Job> jobList() {
        if (jobList == null) {
            return new ConcurrentLinkedDeque<>();
        }
        return new ConcurrentLinkedDeque<>(jobList);
    }

    public void add(Job newJob) {
        for (Job job : jobList) {
            if (job.matches(newJob)) {
                return;
            }
        }
        jobList.addLast(newJob);
        save();
    }

    public void remove(Job oldJob) {
        Job removeJob = oldJob;
        for (Job job : jobList) {
            if (job.matchesLink(oldJob)) {
                removeJob = job;
                break;
            }
        }
        jobList.remove(removeJob);
        save();
    }

    public void setList(ConcurrentLinkedDeque<Job> jobList) {
        this.jobList = jobList;
        save();
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
        save();
    }

    private void save() {
        AppSettings.SET.jobs(this);
    }
}
