package preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import init.Environment;
import org.apache.commons.io.FileUtils;
import org.hildan.fxgson.FxGson;
import properties.Program;
import support.JobHistory;
import support.Jobs;

import javax.crypto.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.prefs.Preferences;

import static preferences.Labels.*;
import static properties.Program.JOB_FILE;
import static properties.Program.JOB_HISTORY_FILE;

public class Set {
    private static final Set INSTANCE = new Set();
    private final Preferences preferences = Labels.PREFERENCES;
    SecretKey secretKey;

    protected Set() {
    }

    protected static Set getInstance() {
        return INSTANCE;
    }

    public void lastYtDlpUpdateTime(long value) {
        AppSettings.CLEAR.lastYtDlpUpdateTime();
        preferences.putLong(LAST_YT_DLP_UPDATE_TIME, value);
    }

    public void lastFolder(String lastFolderPath) {
        AppSettings.CLEAR.lastFolder();
        preferences.put(LAST_FOLDER, lastFolderPath);
    }

    public void jobHistory(JobHistory jobHistory) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        String value = gson.toJson(jobHistory);
        Path jobHistoryFile = Paths.get(Program.get(JOB_HISTORY_FILE));
        try {
            FileUtils.writeStringToFile(jobHistoryFile.toFile(), value, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ytDlpVersion(String version) {
        AppSettings.CLEAR.ytDlpVersion();
        preferences.put(YT_DLP_VERSION, version);
    }

    public void ffmpegVersion(String version) {
        AppSettings.CLEAR.ffmpegVersion();
        preferences.put(FFMPEG_VERSION, version);
    }

    public void spotifyAccessToken(String token) {
        try {
            // Generate a secret key for encryption and decryption of the access token
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            INSTANCE.secretKey = keyGenerator.generateKey();
            // Encrypt the access token
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, INSTANCE.secretKey);
            token = Base64.getEncoder().encodeToString(cipher.doFinal(token.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! No such algorithm exists! " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! Block size of the data is incorrect! " + e.getMessage());
        } catch (BadPaddingException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! Data is not padded correctly! " + e.getMessage());
        } catch (InvalidKeyException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! Failed to generate secret key! " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            Environment.getMessageBroker().msgInitError("Failed to encrypt Spotify access token! No such padding exists! " + e.getMessage());
        }
        AppSettings.CLEAR.spotifyAccessToken();
        preferences.put(SPOTIFY_ACCESS_TOKEN, token);
    }

    public void ytDlpUpdating(boolean isInitializing) {
        AppSettings.CLEAR.ytDlpUpdating();
        preferences.putBoolean(YT_DLP_UPDATING, isInitializing);
    }

    public void isFfmpegWorking(boolean isWorking) {
        AppSettings.CLEAR.isFfmpegWorking();
        preferences.putBoolean(IS_FFMPEG_WORKING, isWorking);
    }

    public void earlyAccess(boolean isEarlyAccess) {
        AppSettings.CLEAR.earlyAccess();
        preferences.putBoolean(EARLY_ACCESS, isEarlyAccess);
    }

    public void newDriftyVersionName(String versionName) {
        AppSettings.CLEAR.newDriftyVersionName();
        preferences.put(NEW_DRIFTY_VERSION_NAME, versionName);
    }

    public void lastDriftyUpdateTime(long value) {
        AppSettings.CLEAR.lastDriftyUpdateTime();
        preferences.putLong(LAST_DRIFTY_UPDATE_TIME, value);
    }

    public void latestDriftyVersionTag(String tag) {
        AppSettings.CLEAR.latestDriftyVersionTag();
        preferences.put(LATEST_DRIFTY_VERSION_TAG, tag);
    }

    public void driftyUpdateAvailable(boolean isUpdateAvailable) {
        AppSettings.CLEAR.driftyUpdateAvailable();
        preferences.putBoolean(DRIFTY_UPDATE_AVAILABLE, isUpdateAvailable);
    }

    public void jobs(Jobs jobs) {
        String serializedJobs = serializeJobs(jobs);
        writeJobsToFile(serializedJobs);
    }

    private String serializeJobs(Jobs jobs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        return gson.toJson(jobs);
    }

    private void writeJobsToFile(String serializedJobs) {
        AppSettings.CLEAR.jobs();
        Path jobBatchFile = Paths.get(Program.get(JOB_FILE));
        try {
            FileUtils.writeStringToFile(jobBatchFile.toFile(), serializedJobs, Charset.defaultCharset());
        } catch (IOException e) {
            String errorMessage = "Failed to write jobs to file: " + e.getMessage();
            Environment.getMessageBroker().msgInitError(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }
}
