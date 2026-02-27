package data;

import init.Environment;
import support.Job;
import support.JobHistory;
import support.Jobs;
import utils.DbConnection;

import java.sql.SQLException;
import java.util.Collection;

public class JobService {

    private JobService() {
    }

    public static JobHistory getJobHistory() {
        JobHistory jobHistory = new JobHistory();
        try {
            DbConnection dbConnection = DbConnection.getInstance();
            Collection<Job> completedJobs = dbConnection.getCompletedJobs();

            for (Job job : completedJobs) {
                jobHistory.addJob(job, true);
            }
        }catch (SQLException e) {
            Environment.getMessageBroker().msgInitError("Could not load Job history! SQLException! " + e.getMessage());
        }
        return jobHistory;
    }

    public static Jobs getJobs() {
        Jobs jobs = new Jobs();
        try {
            DbConnection dbConnection = DbConnection.getInstance();
            Collection<Job> queuedJobs = dbConnection.getQueuedJobs();

            for (Job job : queuedJobs) {
                jobs.add(job);
            }
        }catch (SQLException e) {
            Environment.getMessageBroker().msgInitError("Could not load Jobs from database! SQLException! " + e.getMessage());
        }
        return jobs;
    }
}