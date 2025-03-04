package support;

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
            if (job.equals(newJob)) {
                return;
            }
        }
        jobList.addLast(newJob);
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
    }

    public void setList(ConcurrentLinkedDeque<Job> jobList) {
        this.jobList = jobList;
    }

    public boolean notNull() {
        return jobList != null;
    }

    public boolean isEmpty() {
        return jobList.isEmpty();
    }

    public void clear() {
        jobList.clear();
    }
}
