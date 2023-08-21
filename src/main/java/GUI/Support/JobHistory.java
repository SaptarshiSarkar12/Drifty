package GUI.Support;

import Preferences.AppSettings;
import Utils.CheckFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JobHistory {

    private final List<Job> jobHistory = new ArrayList<>();

    public void addJob(Job job) {
        boolean hasJob = false;
        for (Job jobHistory : jobHistory) {
            if (jobHistory.getLink().equals(job.getLink())) {
                return;
            }
        }
        jobHistory.add(job);
        AppSettings.set.jobHistory(this);
    }

    public boolean jobFileExists(Job newJob) {
        for (Job job : jobHistory) {
            if (job.getLink().equals(newJob.getLink()) && job.getFilename().equals(newJob.getFilename())) {
                for (String folder : AppSettings.get.folders().getFolders()) {
                    Path downloadPath = Paths.get(folder);
                    CheckFile checkFile = new CheckFile(downloadPath, job.getFilename());
                    Thread thread = new Thread(checkFile);
                    thread.start();
                    while (thread.getState().equals(Thread.State.RUNNABLE)) {
                        sleep(100);
                    }
                    if (checkFile.fileFound())
                        return true;
                }
            }
        }
        return false;
    }

    public boolean jobMatch(Job newJob) {
        for (Job job : jobHistory) {
            if (job.getLink().equals(newJob.getLink()) && job.getFilename().equals(newJob.getFilename())) {
                return true;
            }
        }
        return false;
    }

    public List<Job> getHistory() {
        return jobHistory;
    }

    private void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
