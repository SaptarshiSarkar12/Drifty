package GUI.Support;

import Preferences.AppSettings;

import java.util.concurrent.ConcurrentLinkedDeque;

public class JobHistory {

    public JobHistory() {
        this.jobList = new ConcurrentLinkedDeque<>();
    }

    private ConcurrentLinkedDeque<Job> jobList;

    public ConcurrentLinkedDeque<Job> getJobList() {
        if (jobList == null) {
            this.jobList = new ConcurrentLinkedDeque<>();
        }
        if(jobList.isEmpty())
            return jobList;
        ConcurrentLinkedDeque<Job> list = new ConcurrentLinkedDeque<>();
        for(Job job : jobList) {
            String link = job.getLink();
            String dir = job.getDir();
            String filename = job.getFilename();
            list.addLast(new Job(link, dir, filename));
            System.out.println(link + ": " + dir + ": " + filename);
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
