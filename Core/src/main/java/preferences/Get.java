package preferences;

import init.Environment;
import support.Job;
import support.JobHistory;
import support.Jobs;
import utils.DbConnection;
import utils.Utility;

import javax.crypto.*;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collection;
import java.util.prefs.Preferences;

import static preferences.Labels.*;

public class Get {
    private static final Get INSTANCE = new Get();
    private final Preferences preferences = Labels.PREFERENCES;

    static Get getInstance() {
        return INSTANCE;
    }

    public long lastYtDlpUpdateTime() {
        return preferences.getLong(LAST_YT_DLP_UPDATE_TIME, 1000L);
    }

    public String lastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString();
        return preferences.get(LAST_FOLDER, defaultPath);
    }

    public JobHistory jobHistory() {
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

    public String ytDlpVersion() {
        return preferences.get(YT_DLP_VERSION, "");
    }

    public String ffmpegVersion() {
        return preferences.get(FFMPEG_VERSION, "");
    }

    public String spotifyAccessToken() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey secretKey = Set.getInstance().secretKey;
            while (secretKey == null) { // Sometimes, the encryption of token takes time and the key doesn't get generated in time
                Utility.sleep(10);
                secretKey = Set.getInstance().secretKey;
            }
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(preferences.get(SPOTIFY_ACCESS_TOKEN, ""))));
        } catch (NoSuchAlgorithmException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! No such algorithm exists! " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! Block size of the data is incorrect! " + e.getMessage());
        } catch (BadPaddingException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! Data is not padded correctly! " + e.getMessage());
        } catch (InvalidKeyException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! Failed to generate secret key! " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            Environment.getMessageBroker().msgInitError("Failed to decrypt Spotify access token! No such padding exists! " + e.getMessage());
        }
        return preferences.get(SPOTIFY_ACCESS_TOKEN, "");
    }

    public boolean ytDlpUpdating() {
        return preferences.getBoolean(YT_DLP_UPDATING, false);
    }

    public boolean isFfmpegWorking() {
        return preferences.getBoolean(IS_FFMPEG_WORKING, false);
    }

    public boolean earlyAccess() {
        return preferences.getBoolean(EARLY_ACCESS, false);
    }

    public String newDriftyVersionName() {
        return preferences.get(NEW_DRIFTY_VERSION_NAME, "");
    }

    public long lastDriftyUpdateTime() {
        return preferences.getLong(LAST_DRIFTY_UPDATE_TIME, 1000L);
    }

    public String latestDriftyVersionTag() {
        return preferences.get(LATEST_DRIFTY_VERSION_TAG, "");
    }

    public boolean driftyUpdateAvailable() {
        return preferences.getBoolean(DRIFTY_UPDATE_AVAILABLE, false);
    }

    public Jobs jobs() {
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
