package support;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@NoArgsConstructor
public class Jobs {
    private ConcurrentLinkedDeque<Job> jobList = new ConcurrentLinkedDeque<>();
    public ConcurrentLinkedDeque<Job> jobList() {
        return new ConcurrentLinkedDeque<>(jobList);
    }
    public void add(Job newJob) {
        for (Job job : jobList) {
            if (job.equals(newJob)) {
                log.debug("Duplicate job ignored: {}", newJob);
                return;
            }
        }
        jobList.addLast(newJob);
    }

    public void remove(Job oldJob) {
        for (Job job : jobList) {
            if (job.matchesLink(oldJob)) {
                jobList.remove(job);
                log.debug("Removed job: {}", job);
                return;
            }
        }
        log.debug("No matching job found to remove: {}", oldJob);
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
