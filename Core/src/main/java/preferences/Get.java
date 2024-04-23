package preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import init.Environment;
import org.apache.commons.io.FileUtils;
import org.hildan.fxgson.FxGson;
import properties.Program;
import support.JobHistory;
import utils.Utility;

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
import static properties.Program.JOB_HISTORY_FILE;

public class Get {
    private static final Get INSTANCE = new Get();
    private final Preferences preferences = Labels.PREFERENCES;

    static Get getInstance() {
        return INSTANCE;
    }

    public long lastDriftyUpdateTime() {
        return preferences.getLong(LAST_DRIFTY_UPDATE_TIME, 1000L);
    }

    public long lastYtDlpUpdateTime() {
        return preferences.getLong(LAST_YT_DLP_UPDATE_TIME, 1000L);
    }

    public boolean driftyUpdateAvailable() {
        return preferences.getBoolean(DRIFTY_UPDATE_AVAILABLE, false);
    }

    public String lastDownloadFolder() {
        String defaultPath = Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString();
        return preferences.get(LAST_FOLDER, defaultPath);
    }

    public JobHistory jobHistory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = FxGson.addFxSupport(gsonBuilder).setPrettyPrinting().create();
        JobHistory jobHistory;
        Path jobHistoryFile = Paths.get(Program.get(JOB_HISTORY_FILE));
        try {
            if (!jobHistoryFile.toFile().exists()) {
                jobHistory = new JobHistory();
                String json = gson.toJson(jobHistory);
                FileUtils.write(jobHistoryFile.toFile(), json, Charset.defaultCharset());
            }
            String json = FileUtils.readFileToString(jobHistoryFile.toFile(), Charset.defaultCharset());
            if (json != null && !json.isEmpty()) {
                jobHistory = gson.fromJson(json, JobHistory.class);
                return jobHistory;
            }
        } catch (IOException ignored) {
        }
        return new JobHistory();
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

    public String updateChannel() {
        return preferences.get(UPDATE_CHANNEL, "stable");
    }
}
