package data;

import support.Job;
import support.JobHistory;
import support.Jobs;
import utils.DbConnection;

import java.sql.SQLException;
import java.util.Collection;

public class JobService {

    public JobService() {
    }

    public static JobHistory getJobHistory() {
        JobHistory jobHistory = new JobHistory();
        try {
            DbConnection dbConnection = DbConnection.getInstance();
            Collection<Job> completedJobs = dbConnection.getCompletedJobs();

            for (Job job : completedJobs) {
                jobHistory.addJob(job, true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching queued jobs from the database: " + e.getMessage(), e);
        }
        return jobs;
    }
}